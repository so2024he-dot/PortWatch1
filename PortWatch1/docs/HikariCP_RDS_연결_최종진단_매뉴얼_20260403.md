# HikariCP + AWS RDS MySQL 연결 최종 진단 매뉴얼
**날짜**: 2026-04-03
**환경**: HikariCP 3.4.5 / MySQL Connector/J 8.0.33 / AWS RDS MySQL 8.4.7 / Spring 5.0.7.RELEASE

---

## 1. 핵심 원칙 요약

| 설정 | 값 | 이유 |
|------|-----|------|
| `keepaliveTime` | **사용 금지** | HikariCP 3.4.5 Spring bean 미지원 → BeanCreationException |
| `autoReconnect` | **사용 금지** | MySQL 8.x deprecated → 재연결 폭풍 유발 |
| `allowPublicKeyRetrieval` | `true` 필수 | MySQL 8.x `caching_sha2_password` 인증 |
| `tcpKeepAlive` | `true` 권장 | AWS NAT Gateway 4분 유휴 끊김 방지 |
| `useUnicode` | `true` 필수 | 한글 처리 |
| jdbcUrl | **반드시 한 줄** | XML 개행 → URL 파싱 오류 → 연결 실패 |
| `connectTimeout` | `< connectionTimeout` | connectTimeout(10s) < connectionTimeout(30s) 규칙 |
| `initializationFailTimeout` | `-1` | EC2 재시작 시 Tomcat 기동 허용 |

---

## 2. root-context.xml 최종 정답 (현재 적용됨)

```xml
<bean id="dataSource" class="com.zaxxer.hikari.HikariDataSource" destroy-method="close">
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>

    <!-- jdbcUrl: 반드시 한 줄! 개행 있으면 URL 파싱 실패 -->
    <property name="jdbcUrl" value="jdbc:mysql://portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306/portwatch?useSSL=false&amp;sslMode=DISABLED&amp;serverTimezone=Asia/Seoul&amp;characterEncoding=UTF-8&amp;useUnicode=true&amp;allowPublicKeyRetrieval=true&amp;connectTimeout=10000&amp;socketTimeout=60000&amp;tcpKeepAlive=true"/>

    <property name="username" value="admin"/>
    <property name="password" value="portwatch2026!!abcd"/>

    <!-- db.t3.micro: max_connections≈150, 스케줄러 4개+수동 3개+여유=15 적합 -->
    <property name="maximumPoolSize" value="15"/>
    <property name="minimumIdle" value="3"/>

    <!-- connectTimeout(10s) < connectionTimeout(30s) 규칙 준수 -->
    <property name="connectionTimeout" value="30000"/>

    <!-- AWS NAT Gateway 4분 유휴 타임아웃 대비: maxLifetime=3분으로 먼저 교체 -->
    <property name="idleTimeout" value="120000"/>
    <property name="maxLifetime" value="180000"/>
    <property name="connectionTestQuery" value="SELECT 1"/>

    <!-- EC2 재시작 시 DB 연결 실패해도 Tomcat 기동 허용 -->
    <property name="initializationFailTimeout" value="-1"/>
</bean>
```

---

## 3. 연결 실패 유형별 진단표

### 3-1. Spring Context 기동 실패
```
오류: BeanCreationException: Error creating bean with name 'dataSource'
      NotWritablePropertyException: Invalid property 'keepaliveTime'
```
**원인**: `keepaliveTime` 속성은 HikariCP 3.4.5에서 Spring XML 주입 불가
**조치**: `<property name="keepaliveTime" .../>` 줄 완전 제거

---

### 3-2. TCP 연결 실패
```
오류: Communications link failure
      The last packet sent successfully to the server was 0 milliseconds ago.
```
**원인 체크리스트**:
```bash
# 1. RDS 엔드포인트 DNS 확인
nslookup portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com

# 2. TCP 포트 연결 확인
nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306

# 3. MySQL CLI 직접 연결
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com -P 3306 -u admin -p
```

**가능한 원인**:
| 원인 | 확인 방법 | 조치 |
|------|---------|------|
| RDS 보안그룹 3306 미허용 | AWS Console → RDS SG → Inbound | EC2 SG에서 3306 허용 추가 |
| jdbcUrl 개행 포함 | root-context.xml 확인 | 한 줄로 수정 |
| 호스트명 오타 | URL 문자 하나씩 확인 | `cpggwqm1goo3` (숫자1) |
| IPv6 연결 시도 | ps aux | grep preferIPv4 | setenv.sh에 `-Djava.net.preferIPv4Stack=true` |

---

### 3-3. 인증 실패
```
오류: Access denied for user 'admin'@'...' (using password: YES)
     Public Key Retrieval is not allowed
```
**조치**:
```
jdbcUrl에 추가: allowPublicKeyRetrieval=true
```

```sql
-- RDS에서 권한 확인
SHOW GRANTS FOR 'admin'@'%';
```

---

### 3-4. HikariPool 타임아웃
```
오류: HikariPool-1 - Connection is not available, request timed out after 30000ms
```
**원인 체크리스트**:
```bash
# Tomcat 로그에서 상세 원인 확인
sudo grep -A5 "HikariPool.*Exception\|JDBC.*failed\|Cannot.*connection" \
    /opt/tomcat/logs/catalina.out | tail -50
```

**가능한 원인**:
1. Spring Context 기동 실패 (BeanCreationException) → HikariCP 초기화 안 됨
2. RDS 연결 불가 (보안그룹, 호스트명)
3. MySQL 인증 실패 (allowPublicKeyRetrieval)
4. jdbcUrl 개행 포함 → URL 파싱 오류

---

### 3-5. MyBatis Mapper 오류
```
오류: org.apache.ibatis.binding.BindingException:
      Invalid bound statement (not found): com.portwatch.mapper.MemberMapper.findEmail
```
**원인**: 구버전 WAR의 `findEmail` 메서드 → 현재 코드는 `findByEmail`
**조치**: 새 WAR 빌드 후 배포

---

## 4. AWS 환경 특수 설정

### 4-1. NAT Gateway 4분 유휴 타임아웃 대응
AWS NAT Gateway는 4분 유휴 TCP 연결을 끊음 → HikariCP 풀의 유휴 연결이 강제 종료됨

**tcpKeepAlive=true** (JDBC URL 파라미터):
- TCP 레벨 keepalive 패킷 전송
- OS 기본 설정 (약 2시간)과 결합하여 동작

**maxLifetime=180000** (3분):
- AWS가 4분에 끊기 전에 HikariCP가 먼저 연결 교체
- 반드시 `maxLifetime < 4분(240000ms)` 이어야 함

**idleTimeout=120000** (2분):
- 유휴 연결을 2분 후 풀에서 제거
- minimumIdle=3 유지하면서 나머지 교체

```
타임아웃 계층 (반드시 준수):
connectTimeout(10s) < connectionTimeout(30s) < idleTimeout(120s) < maxLifetime(180s) < AWS NAT(240s)
```

### 4-2. setenv.sh (EC2 Tomcat JVM 설정)
```bash
# 파일 내용 확인
sudo cat /opt/tomcat/bin/setenv.sh

# 읽기 권한 없을 경우
sudo chmod 644 /opt/tomcat/bin/setenv.sh

# 적용 여부 확인 (Tomcat 실행 중)
ps aux | grep tomcat | grep -o '\-D[^ ]*' | tr ' ' '\n'
```

**필수 JVM 옵션**:
```bash
export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"
```
- `preferIPv4Stack=true`: IPv6 대신 IPv4 강제 → AWS RDS 연결 안정성

---

## 5. 연결 성공 확인 절차

### 5-1. /debug/db-check 엔드포인트
```bash
curl -s http://localhost:8080/debug/db-check | python3 -m json.tool
```

**성공 응답**:
```json
{
    "dbConnected": true,
    "memberFound": true
}
```

**실패 응답 해석**:
```json
{"dbConnected": false, "error": "BeanCreationException", ...}
→ Spring Context 기동 실패 → Tomcat 로그 확인

{"dbConnected": false, "error": "MyBatisSystemException", "message": "...timed out after 30000ms"}
→ HikariCP 연결 풀 고갈 → JDBC 연결 실패 → 3-4항목 진단

{"dbConnected": false, "error": "DataAccessException", ...}
→ SQL 실행 오류 → Mapper XML 확인
```

### 5-2. Tomcat 로그 실시간 모니터링
```bash
# 기동 시 오류 실시간 확인
sudo tail -f /opt/tomcat/logs/catalina.out

# Spring Context 기동 성공 확인
sudo grep "Root WebApplicationContext initialized" /opt/tomcat/logs/catalina.out

# 컨트롤러 매핑 확인
sudo grep "Mapped.*MemberController\|Mapped.*NewsController" /opt/tomcat/logs/catalina.out
```

---

## 6. EC2 배포 표준 절차

```bash
# [로컬 STS]
# 1. Project → Clean → OK
# 2. Run As → Maven install → BUILD SUCCESS 확인
# 3. WinSCP: target/portwatch-1.0.0-BUILD-SNAPSHOT.war → /home/ubuntu/portwatch.war

# [EC2 PuTTy]
sudo systemctl stop tomcat
sudo cp /home/ubuntu/portwatch.war /opt/tomcat/webapps/ROOT.war
sudo rm -rf /opt/tomcat/webapps/ROOT/
sudo systemctl start tomcat

# 45초 대기 후 DB 연결 확인
sleep 45 && curl -s http://localhost:8080/debug/db-check | python3 -m json.tool
```

---

## 7. 테스트 계정 SQL (배포 후 로그인 테스트)

```sql
-- EC2 → MySQL 연결
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com -P 3306 -u admin -p

-- 반드시 먼저 실행
USE portwatch;

-- test@test.com 계정 상태 확인
SELECT member_id, member_email, LEFT(member_pass,20) AS pass_prefix,
       member_status, balance, updated_at
  FROM MEMBER
 WHERE member_email = 'test@test.com';
```

**현재 설정된 테스트 계정**:
```
이메일: test@test.com
비밀번호: test1234
BCrypt hash: $2a$10$CUgz7dDXNJNhW3RBK6K3vOz1bV/7/HBWp8taJpKskregpzZw.2duy
member_status: ACTIVE
```

**bcrypt 해시 신규 생성** (EC2 배포 후):
```
http://54.180.142.111:8080/debug/bcrypt?pass=test1234
→ 응답의 hash 값 복사 후 UPDATE에 사용
```

---

## 8. 자주 발생하는 EC2 작업 오류

### 8-1. USE portwatch 누락
```sql
-- 잘못된 예
UPDATE MEMBER SET ...   → ERROR 1046 (No database selected)

-- 올바른 예
USE portwatch;
UPDATE MEMBER SET ...   → 1 row affected
```

### 8-2. Shell 파싱 오류
```bash
# 잘못된 예 (특수문자 붙여넣기)
$ UPDATE MEMBER ...
bash: syntax error near unexpected token '{'

# 올바른 예: .sh 파일로 저장 후 실행
cat > /tmp/update.sh << 'EOF'
mysql -h ... -u admin -p'portwatch2026!!abcd' portwatch << 'SQL'
UPDATE MEMBER SET member_status='ACTIVE' WHERE member_email='test@test.com';
SQL
EOF
bash /tmp/update.sh
```

### 8-3. INSERT IGNORE 0 rows affected
```sql
-- 원인: member_id 충돌 (다른 email로 이미 존재)
-- 확인
SELECT * FROM MEMBER WHERE member_id = '...';

-- 해결: UPDATE 사용
UPDATE MEMBER SET member_pass='...', member_status='ACTIVE'
 WHERE member_email='test@test.com';
```

---

## 9. HikariCP 3.4.5 사용 가능/불가 속성 목록

| 속성 | Spring XML 사용 | 비고 |
|------|--------------|------|
| `driverClassName` | ✅ | |
| `jdbcUrl` | ✅ | 한 줄 필수 |
| `username` | ✅ | |
| `password` | ✅ | |
| `maximumPoolSize` | ✅ | |
| `minimumIdle` | ✅ | |
| `connectionTimeout` | ✅ | |
| `idleTimeout` | ✅ | |
| `maxLifetime` | ✅ | |
| `connectionTestQuery` | ✅ | |
| `initializationFailTimeout` | ✅ | |
| `keepaliveTime` | ❌ **사용 불가** | 3.4.5 미지원, BeanCreationException |
| `autoReconnect` | N/A | jdbcUrl 파라미터 (MySQL 8.x deprecated) |
| `tcpKeepAlive` | N/A | jdbcUrl 파라미터 (사용 권장) |

---

*생성일: 2026-04-03 | PortWatch HikariCP RDS 연결 진단 매뉴얼*
