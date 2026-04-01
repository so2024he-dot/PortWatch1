# PortWatch AWS RDS MySQL 연결 완전 분석

> 작성일: 2026-03-31 | 대상: AWS EC2 Ubuntu 24.04 + Tomcat 9 + HikariCP 3.4.5 + MySQL 8.4.7

---

## 1. 현재 증상 요약

| 증상 | URL | 오류 메시지 |
|---|---|---|
| 로그인 실패 | `/member/login` | "로그인 처리 중 오류가 발생했습니다." |
| 회원가입 실패 | `/member/signup` | "회원가입 처리 중 오류가 발생했습니다." |
| 주식목록 비어있음 | `/stock/list` | "주식 목록 (0개 기업)" |
| 뉴스 로드 실패 | `/news/list` | HTTP 500 / DB 오류 |
| DB 연결 확인 | `/debug/db-check` | `{"dbConnected":false}` |

**공통 원인**: HikariCP `Connection is not available, request timed out after 5000ms`

---

## 2. 핵심 원인 분석

### 2-1. 타임아웃 충돌 (Default 값 혼용 문제)

이것이 현재 오류의 **진짜 원인**입니다.

```
┌──────────────────────────────────────────────────────────────┐
│  연결 요청 흐름                                                │
│                                                              │
│  Java 코드 → HikariCP Pool → JDBC Driver → MySQL RDS        │
│              ↑                ↑                              │
│    connectionTimeout    connectTimeout                        │
│        (HikariCP)         (JDBC URL)                         │
└──────────────────────────────────────────────────────────────┘
```

| 파라미터 | 위치 | 의미 | 이전 값 | 수정 후 |
|---|---|---|---|---|
| `connectTimeout` | JDBC URL | TCP 소켓 연결 최대 대기 시간 | 30000ms | **10000ms** |
| `connectionTimeout` | HikariCP 빈 | 풀에서 연결 꺼낼 때 최대 대기 시간 | **5000ms** ← 문제 | **30000ms** |

**충돌 발생 시나리오:**

```
1. 코드가 DB 연결 요청
2. HikariCP: "풀에서 꺼낼게, 최대 5초(connectionTimeout) 기다릴게"
3. TCP 연결 시도 중 (connectTimeout=30초 = 30번 시도 가능)
4. 그런데 IPv6 DNS 해석 실패로 TCP 연결 시도에 10초 소요
5. HikariCP의 5초가 먼저 만료 → "request timed out after 5000ms"
6. TCP 연결은 아직 시도 중이지만 HikariCP가 이미 포기함
```

**규칙: `connectionTimeout` >= `connectTimeout` + 여유시간**
- 수정 전: `connectionTimeout(5000)` < `connectTimeout(30000)` → **충돌**
- 수정 후: `connectionTimeout(30000)` >= `connectTimeout(10000)` → **정상**

---

### 2-2. Java IPv6 우선순위 문제

**증상**: `mysql` CLI는 연결 성공, Java JDBC는 타임아웃

**원인**:
```
mysql CLI (C 기반)  → 시스템 DNS → IPv4 주소 반환 → 즉시 연결 성공
Java JDBC           → Java DNS  → IPv6 주소 시도 → 실패(5-10초) → IPv4 재시도
```

AWS EC2 Ubuntu에서 Java는 기본적으로 IPv6를 먼저 시도합니다.
RDS 엔드포인트는 IPv4 전용이므로, IPv6 시도가 실패한 후 IPv4로 폴백하는 시간 = 5~10초
이 5~10초가 `connectionTimeout=5000ms` 를 초과 → 타임아웃

**해결**: Tomcat JVM에 `-Djava.net.preferIPv4Stack=true` 추가

---

### 2-3. ROOT.war 재추출로 수동 수정이 무효화

**발생 상황**:
```
1. 사용자: sudo python3 -c "...URL 수정..." → 수정 완료
2. 사용자: sudo systemctl restart tomcat
3. Tomcat: ROOT.war 타임스탬프 확인 → ROOT/보다 새로움 → ROOT/ 재추출!
4. 수동으로 수정한 파일이 WAR 원본으로 덮어씌워짐
```

**Tomcat 재추출 조건**: `autoDeploy=true`(기본값) 상태에서 ROOT.war가 ROOT/보다 신규이면 항상 재추출

**해결**: WAR 파일 자체를 수정 후 배포하거나, ROOT.war 삭제 후 서비스 재시작

---

### 2-4. EC2 배포 파일 경로 구조

```
/opt/tomcat/webapps/
├── ROOT/                          ← Tomcat이 ROOT.war에서 추출한 디렉토리
│   └── WEB-INF/spring/root-context.xml  ← 이 파일을 수동 수정해도 의미 없음!
└── ROOT.war                       ← 이 파일이 있으면 재시작 시 ROOT/ 덮어씌움
```

**올바른 배포 방법**: ROOT.war를 수정된 것으로 교체 → ROOT/ 삭제 → Tomcat 시작

---

## 3. 수정된 코드 (root-context.xml)

### 변경 전

```xml
<property name="jdbcUrl"
    value="...connectTimeout=30000&amp;..."/>
<property name="connectionTimeout" value="5000"/>  <!-- ❌ 문제 -->
<property name="initializationFailTimeout" value="30000"/>  <!-- ❌ 문제 -->
```

### 변경 후

```xml
<property name="jdbcUrl"
    value="...connectTimeout=10000&amp;..."/>       <!-- ✅ 10초로 단축 -->
<property name="connectionTimeout" value="30000"/> <!-- ✅ 30초 (connectTimeout보다 큼) -->
<property name="initializationFailTimeout" value="-1"/> <!-- ✅ 기동 시 실패해도 허용 -->
```

---

## 4. EC2 완전 해결 절차 (단계별)

### Step 1: Tomcat 서비스 파일에 IPv4 강제 설정

```bash
# 서비스 파일 확인
sudo cat /etc/systemd/system/tomcat.service | grep JAVA_OPTS

# 편집
sudo nano /etc/systemd/system/tomcat.service
```

다음 줄을 찾거나 추가:
```ini
Environment="JAVA_OPTS=-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"
```

저장 후:
```bash
sudo systemctl daemon-reload
```

---

### Step 2: 새 WAR 빌드 (로컬 Windows에서)

STS에서:
```
메뉴 → Run As → Maven clean
메뉴 → Run As → Maven install
```

또는 Windows CMD에서:
```cmd
cd C:\Users\소현석\git\PortWatch1\PortWatch1
mvn clean install -DskipTests
```

---

### Step 3: EC2로 WAR 전송

Windows CMD:
```cmd
scp "C:\Users\소현석\git\PortWatch1\PortWatch1\target\portwatch-1.0.0-BUILD-SNAPSHOT.war" ubuntu@54.180.142.111:/home/ubuntu/portwatch.war
```

SSH 키 파일이 있는 경우:
```cmd
scp -i "C:\Users\소현석\.ssh\포치워치키.pem" "C:\Users\소현석\git\PortWatch1\PortWatch1\target\portwatch-1.0.0-BUILD-SNAPSHOT.war" ubuntu@54.180.142.111:/home/ubuntu/portwatch.war
```

---

### Step 4: EC2에서 배포 (PuTTy)

```bash
# Tomcat 중지
sudo systemctl stop tomcat

# 기존 배포 완전 삭제 (ROOT.war와 ROOT/ 모두)
sudo rm -rf /opt/tomcat/webapps/ROOT
sudo rm -f  /opt/tomcat/webapps/ROOT.war

# 새 WAR 배포
sudo cp /home/ubuntu/portwatch.war /opt/tomcat/webapps/ROOT.war

# Tomcat 시작 (ROOT.war에서 ROOT/ 추출)
sudo systemctl start tomcat
echo "30초 대기 중..."
sleep 30

# ROOT.war 삭제 ← 핵심! 재시작 시 재추출 방지
sudo rm -f /opt/tomcat/webapps/ROOT.war

# DB 연결 확인
curl -s http://localhost:8080/debug/db-check
```

예상 결과:
```json
{"success":true,"dbConnected":true,"message":"DB 정상 ✅ ..."}
```

---

### Step 5: DB 연결 성공 후 - 테스트 계정 비밀번호 확인

```bash
# BCrypt 해시 확인 (브라우저)
http://54.180.142.111:8080/debug/bcrypt?pass=portwatch123
```

MySQL에서 UPDATE (RDS 직접 연결):
```bash
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -P 3306 -u admin -p
```
비밀번호: `portwatch2026!!abcd`

```sql
USE portwatch;

-- 비밀번호 업데이트
UPDATE MEMBER
SET member_pass = '$2a$10$0GBSb04xfIUriIM/5WwqhSuKgZHeltHn.4qFqnLIlojkISte/pSgSC'
WHERE member_email IN ('test@test.com', 'user1@test.com', 'admin@test.com');

-- 확인 (3 rows matched 이어야 함)
SELECT member_id, member_email, member_status,
       LEFT(member_pass,15) AS pass_prefix
FROM MEMBER
WHERE member_email IN ('test@test.com','user1@test.com','admin@test.com');
```

---

## 5. 테스트 계정 INSERT (DB가 비어있는 경우)

```sql
USE portwatch;

INSERT IGNORE INTO MEMBER (
    member_id, member_email, member_pass, member_name,
    member_role, member_status, balance, created_at
) VALUES
('testuser01', 'test@test.com',
 '$2a$10$0GBSb04xfIUriIM/5WwqhSuKgZHeltHn.4qFqnLIlojkISte/pSgSC',
 '테스트유저', 'USER', 'ACTIVE', 1000000, NOW()),

('adminuser01', 'admin@test.com',
 '$2a$10$0GBSb04xfIUriIM/5WwqhSuKgZHeltHn.4qFqnLIlojkISte/pSgSC',
 '관리자', 'ADMIN', 'ACTIVE', 0, NOW()),

('user0001', 'user1@test.com',
 '$2a$10$0GBSb04xfIUriIM/5WwqhSuKgZHeltHn.4qFqnLIlojkISte/pSgSC',
 '일반유저', 'USER', 'ACTIVE', 500000, NOW());
```

**로그인 정보**: 이메일: `test@test.com` / 비밀번호: `portwatch123`

---

## 6. Default 값 충돌 항목 전체 목록

| 항목 | 충돌 | 이전 값 | 수정 값 | 설명 |
|---|---|---|---|---|
| HikariCP `connectionTimeout` | ✅ 충돌 | 5000ms | **30000ms** | JDBC connectTimeout보다 작으면 안됨 |
| JDBC `connectTimeout` | ✅ 충돌 | 30000ms | **10000ms** | connectionTimeout보다 작게 조정 |
| HikariCP `initializationFailTimeout` | ✅ 충돌 | 30000ms | **-1** | EC2 재시작 시 DB 없어도 Tomcat 기동 가능 |
| JVM `-Djava.net.preferIPv4Stack` | ✅ 누락 | 미설정 | **true** | EC2에서 Java IPv6 우선 시도 방지 |
| `maximumPoolSize` | 최적화 | 5 | **10** | 동시 연결 부족 방지 |

---

## 7. 로컬 STS 오류 (INI 파일 분석)

### 오류 메시지
```
ClassNotFoundException: Cannot find class: MemberVO
WARN: No MyBatis mapper was found in '[com.portwatch.mapper, com.portwatch.persistence]' package
```

### 원인
STS 내장 Tomcat이 Maven 빌드 결과물을 제대로 반영하지 못하는 Eclipse 프로젝트 배포 문제

### 해결 (3단계)

**Step 1: STS Project Clean**
```
메뉴 → Project → Clean... → Clean all projects → OK
```

**Step 2: 서버 재설정**
```
Servers 탭 → 서버 우클릭 → Remove All
서버 더블클릭 → Modules 탭 → Add Web Module → portwatch1 선택 → OK
Ctrl+S로 저장
```

**Step 3: 서버 재시작**
```
Servers 탭 → 서버 우클릭 → Start
```

---

## 8. 회원가입 (Signup) 오류 분석

### 원인 코드 위치: `MemberMapper_mapper_pkg.xml:30`

```xml
<!-- 이메일 중복 체크 -->
<select id="findByEmail" parameterType="String" resultMap="memberResultMap">
    SELECT * FROM MEMBER
    WHERE member_email = #{memberEmail}
    AND member_status = 'ACTIVE'   <!-- ← 이 조건이 문제! -->
</select>
```

**문제**: `INACTIVE` 상태 계정의 이메일로 재가입 시도 시 "사용 가능한 이메일"로 잘못 표시
→ INSERT 시도 → UNIQUE 제약 위반 → 500 오류

**수정 방법**: DB 연결 정상화 후 이 문제가 드러나면 별도 수정 필요

---

## 9. 연결 성공 후 점검 목록

```bash
# 1. DB 연결
curl -s http://localhost:8080/debug/db-check | python3 -m json.tool

# 2. 로그인 테스트
curl -s -c /tmp/cookies.txt -b /tmp/cookies.txt \
     -d "memberEmail=test@test.com&memberPass=portwatch123" \
     http://54.180.142.111:8080/member/login -L

# 3. Tomcat 로그 확인
sudo tail -100 /opt/tomcat/logs/catalina.out | grep -E "ERROR|HikariPool|Connected"
```

---

## 10. 향후 예방 조치

1. **WAR 배포 시 ROOT.war 삭제**: 배포 스크립트에 `rm -f ROOT.war` 포함
2. **JVM 플래그 영구 설정**: `/etc/systemd/system/tomcat.service` 에 IPv4 플래그 고정
3. **connectionTimeout ≥ connectTimeout**: 항상 이 규칙 유지
4. **`/debug/db-check` 모니터링**: 배포 후 반드시 이 엔드포인트로 확인
