# AWS RDS MySQL 연결 - Default 값 충돌 완전 분석
> Spring 5.0.7 + HikariCP 3.4.5 + MySQL Connector/J 8.x + AWS RDS MySQL 8.4.7

---

## 1. 핵심 원인 요약

AWS RDS 연결 실패의 3가지 층위 원인:

```
[Layer 1] JVM 레벨: Java가 IPv6 우선 DNS 해석 → RDS는 IPv4 전용 → 5-10초 지연
[Layer 2] 풀 레벨: HikariCP connectionTimeout(30s) 내 연결 성공 못함 → 포기
[Layer 3] DB 레벨: MySQL 8.4 SSL 기본값 충돌 (useSSL/sslMode 혼용)
```

---

## 2. HikariCP 3.4.5 기본값 vs 설정값 대조표

| 속성명 | HikariCP 기본값 | 현재 설정값 | 충돌 여부 | 영향 |
|--------|----------------|------------|---------|------|
| `connectionTimeout` | **30,000ms** | 30,000ms | ✅ 일치 | 풀에서 연결 대기 최대 시간 |
| `idleTimeout` | **600,000ms** (10분) | 120,000ms | 재정의 | 유휴 연결 조기 반환 (AWS NAT 대비) |
| `maxLifetime` | **1,800,000ms** (30분) | 180,000ms | 재정의 | AWS NAT 4분 타임아웃 전에 갱신 |
| `minimumIdle` | maximumPoolSize와 동일 | 2 | 재정의 | 최소 2개 연결 유지 |
| `maximumPoolSize` | **10** | 10 | ✅ 일치 | 최대 연결 수 |
| `initializationFailTimeout` | **1ms** | -1 | ⚠️ 재정의 | -1: Tomcat 기동 허용 (EC2 필수) |
| `connectionTestQuery` | 없음 (JDBC4 isValid 사용) | SELECT 1 | 재정의 | 연결 유효성 확인 강화 |
| `keepaliveTime` | 0 (비활성) | 미설정 | - | HikariCP 3.4.5는 미지원 (4.0+에서만) |

### 위험한 기본값: `initializationFailTimeout=1ms`

```
HikariCP 기본값: initializationFailTimeout=1ms
의미: Tomcat 시작 시 1ms 안에 DB 연결 못 하면 → 기동 자체 실패

AWS EC2 환경에서:
  Tomcat 시작 → HikariCP 초기화 → DB 연결 시도 → 1ms 안에 연결 성공 불가
  → 즉시 기동 실패 (LifecycleException)

해결: initializationFailTimeout=-1
  → DB 연결 실패해도 Tomcat 기동 허용
  → 첫 요청 시 DB 연결 재시도
```

---

## 3. MySQL JDBC URL 파라미터 기본값 vs 설정값

| 파라미터 | MySQL 8.x 기본값 | 현재 설정값 | 충돌 여부 | 영향 |
|---------|----------------|------------|---------|------|
| `useSSL` | true (MySQL 5.7+) | false | ⚠️ 재정의 | SSL 비활성화 |
| `sslMode` | PREFERRED (MySQL 8.0.13+) | DISABLED | ⚠️ 재정의 | SSL 완전 비활성 |
| `serverTimezone` | 없음 (서버 TZ 사용) | Asia/Seoul | 재정의 | KST 강제 |
| `characterEncoding` | utf8 | UTF-8 | 재정의 | 한글 처리 |
| `allowPublicKeyRetrieval` | false | true | 재정의 | SHA-256 인증 허용 |
| `connectTimeout` | **0ms (무제한)** | 10,000ms | ⚠️ 재정의 | TCP 연결 타임아웃 |
| `socketTimeout` | **0ms (무제한)** | 60,000ms | ⚠️ 재정의 | 쿼리 실행 타임아웃 |

### `useSSL` vs `sslMode` 혼용 문제 (MySQL 8.4 핵심!)

```
MySQL 5.7 시대:   useSSL=true/false 로 제어
MySQL 8.0.13+:    sslMode=DISABLED/REQUIRED/VERIFY_CA/VERIFY_IDENTITY 로 제어
MySQL 8.4.7 (현재): sslMode가 우선, useSSL은 하위 호환용

현재 설정: useSSL=false & sslMode=DISABLED → 두 파라미터 모두 명시 (안전)
잘못된 설정 예시:
  useSSL=false 만 설정 → MySQL 8.4에서 경고 발생, sslMode는 PREFERRED 유지됨
  → SSL 핸드셰이크 시도 → 실패 시 500 오류

결론: 두 파라미터를 모두 명시하는 현재 설정이 올바름 ✅
```

---

## 4. HikariCP ↔ JDBC 타임아웃 2단계 충돌 분석

```
[JDBC 레벨] connectTimeout=10,000ms
  └→ MySQL JDBC 드라이버가 TCP 소켓 연결에 허용하는 최대 시간

[HikariCP 레벨] connectionTimeout=30,000ms
  └→ HikariCP가 풀에서 연결을 꺼낼 때 기다리는 최대 시간

충돌 조건: connectionTimeout < connectTimeout
  이전 설정: connectionTimeout=5,000ms, connectTimeout=30,000ms
  → 5초 안에 TCP 연결(30초 필요) 못 기다리고 포기 → 오류

현재 수정: connectionTimeout=30,000ms > connectTimeout=10,000ms ✅
  → 10초 TCP 연결 실패 → HikariCP 재시도 → 30초 내 성공 or 실패
```

---

## 5. IPv6 우선 DNS 해석 문제 (EC2 핵심 원인)

```
EC2 Ubuntu + Java 기본 설정:
  DNS 해석 시 AAAA(IPv6) 레코드 먼저 조회
       ↓
  AWS RDS: IPv4만 지원, AAAA 레코드 없음
       ↓
  IPv6 DNS 실패 (5-10초 대기)
       ↓
  A(IPv4) 레코드로 재시도 → 성공하지만 이미 5-10초 소요
       ↓
  connectTimeout=10,000ms 중 5-10초가 DNS 해석에 소진됨
       ↓
  TCP 연결 가능 시간: 0-5초 → 짧아서 실패 → HikariPool timeout
```

**mysql CLI는 되는데 Java는 안 되는 이유:**
```
mysql 명령어: OS DNS resolver → IPv4 우선 (많은 Linux 도구 기본값)
Java JVM:    JVM DNS resolver → IPv6 우선 (Java 기본값)
```

**해결 방법 (EC2에서):**
```bash
# 방법 1: setenv.sh (권장)
sudo bash -c 'echo "#!/bin/bash
export JAVA_OPTS=\"-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m\"" \
> /opt/tomcat/bin/setenv.sh'
sudo chmod +x /opt/tomcat/bin/setenv.sh

# 방법 2: tomcat.service Environment 추가
sudo nano /etc/systemd/system/tomcat.service
# [Service] 섹션에 추가:
# Environment="JAVA_OPTS=-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"
sudo systemctl daemon-reload
```

---

## 6. 최종 권장 root-context.xml DataSource 설정

```xml
<bean id="dataSource" class="com.zaxxer.hikari.HikariDataSource" destroy-method="close">
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>

    <!--
        ✅ JDBC URL 파라미터 설명:
        - useSSL=false        : SSL 비활성 (MySQL 5.7 하위호환)
        - sslMode=DISABLED    : SSL 완전 비활 (MySQL 8.x 방식) ← 두 파라미터 필수
        - serverTimezone      : 타임존 명시 (없으면 에러)
        - characterEncoding   : 한글 인코딩
        - allowPublicKeyRetrieval=true : caching_sha2_password 허용
        - connectTimeout=10000 : TCP 연결 10초 제한 (default는 무제한)
        - socketTimeout=60000  : 쿼리 실행 60초 제한 (default는 무제한)
    -->
    <property name="jdbcUrl" value="jdbc:mysql://portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306/portwatch?useSSL=false&amp;sslMode=DISABLED&amp;serverTimezone=Asia/Seoul&amp;characterEncoding=UTF-8&amp;allowPublicKeyRetrieval=true&amp;connectTimeout=10000&amp;socketTimeout=60000"/>
    <property name="username" value="admin"/>
    <property name="password" value="portwatch2026!!abcd"/>

    <!--
        ✅ HikariCP 풀 설정:
        - maximumPoolSize=10    : 최대 연결 수 (default=10, 동일)
        - minimumIdle=2         : 최소 유지 연결 (default=maximumPoolSize)
        - connectionTimeout=30000 : 풀에서 연결 대기 최대 30초 (default=30000)
                                    반드시 connectTimeout(10000)보다 커야 함
        - idleTimeout=120000    : 유휴 연결 2분 후 반환 (AWS NAT 4분 대비)
        - maxLifetime=180000    : 연결 최대 수명 3분 (AWS NAT 전에 갱신)
        - initializationFailTimeout=-1 : Tomcat 기동 시 DB 없어도 허용 (EC2 필수!)
                                         default=1ms → 즉시 실패 → Tomcat 기동 불가
    -->
    <property name="maximumPoolSize" value="10"/>
    <property name="minimumIdle" value="2"/>
    <property name="connectionTimeout" value="30000"/>
    <property name="idleTimeout" value="120000"/>
    <property name="maxLifetime" value="180000"/>
    <property name="connectionTestQuery" value="SELECT 1"/>
    <property name="initializationFailTimeout" value="-1"/>
</bean>
```

---

## 7. 연결 문제 진단 체크리스트

```bash
# 1. EC2에서 RDS 직접 연결 테스트
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -P 3306 -u admin -p
# → 성공: RDS/보안그룹 정상, 실패: 보안그룹 확인

# 2. IPv4 설정 확인
cat /opt/tomcat/bin/setenv.sh
# → JAVA_OPTS에 -Djava.net.preferIPv4Stack=true 있어야 함

# 3. Tomcat 실행 중 JVM 옵션 확인
ps aux | grep java | grep -o 'preferIPv4[^ ]*'
# → preferIPv4Stack=true 출력되어야 함

# 4. DB 연결 API 확인
curl -s http://localhost:8080/debug/db-check | python3 -m json.tool
# → dbConnected: true 이어야 함

# 5. Tomcat 로그에서 HikariCP 메시지 확인
sudo tail -50 /opt/tomcat/logs/catalina.out | grep -i "hikari\|pool\|connection"
```

---

## 8. 보안그룹 설정 확인 (RDS + EC2)

| 보안그룹 | 인바운드 규칙 | 필요 여부 |
|---------|-------------|---------|
| EC2 보안그룹 | 포트 8080 (HTTP) - 0.0.0.0/0 | ✅ 필요 |
| RDS 보안그룹 | 포트 3306 (MySQL) - EC2 내부 IP | ✅ 필요 |
| RDS 보안그룹 | 포트 3306 (MySQL) - EC2 보안그룹 ID | ✅ 권장 |

AWS 콘솔에서:
- EC2 내부 IP: `172.31.36.228` (스크린샷 확인)
- RDS 포트 3306이 이 IP를 허용하는지 확인
