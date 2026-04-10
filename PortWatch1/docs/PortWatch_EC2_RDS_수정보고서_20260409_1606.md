# PortWatch EC2/RDS 종합 분석 수정 보고서
**작성일**: 2026-04-09 (15:11 ~ 16:06 세션)  
**분석 파일**: `202604091511 AWS EC2 RDS Aurora SQL output` (스크린샷 11장) + `오류코드 수정할 부분 및 생성코드.ini`

---

## 1. 이번 세션 핵심 발견 요약

| 항목 | 이전 세션 | 이번 세션 | 변화 |
|------|---------|---------|------|
| ClassNotFoundException: MemberVO | ❌ 발생 | ✅ 없음 | **해결됨** |
| Spring Context 초기화 | ❌ 실패 | ✅ 성공 | **해결됨** |
| EC2 → RDS TCP 연결 (mysql CLI) | ❌ 실패 | ✅ **성공** | **핵심 발견** |
| EC2 → RDS (Tomcat HikariCP) | ❌ 실패 | ❌ 여전히 실패 | ⚠️ 원인 규명 필요 |
| 로컬 STS → RDS | ❌ UnknownHost | ❌ 동일 | 로컬 환경 한계 |
| /news/refresh | ❌ 500 | ❌ **HTTP 405** | 새 오류 확인 |
| SQL INSERT (PuTTy) | 미실행 | ❌ **1046 오류** | DB 선택 누락 |

---

## 2. 스크린샷 전체 분석 (155352 ~ 160633)

### 📸 155352 — EC2 `/debug/db-check` 결과

```json
{
  "dbConnected": false,
  "success": false,
  "error": "HikariPool-1 - Connection is not available, request timed out after 30000ms"
}
```

**판단**: EC2의 Tomcat(Java)에서 RDS로 연결 실패.  
원인 분석은 3절에서 상세 서술.

---

### 📸 155507 — EC2 `/crawler/korea` 실패

```
"success": false
"error": "HikariPool-1 - Connection is not available, request timed out after 30011ms"
```

DB 연결 실패로 한국 주식 크롤러 동작 불가.

---

### 📸 155603 — EC2 `/crawler/us` 실패

```
"success": false
"error": "HikariPool-1 - Connection is not available, request timed out after 30008ms"
```

DB 연결 실패로 미국 주식 크롤러 동작 불가.

---

### 📸 155631 — EC2 `/news/refresh` **HTTP 405 오류**

```
HTTP Status 405 – Method Not Allowed
Request method 'GET' not supported
Server: Apache-Coyote/1.1 (Tomcat 9.0.106)
```

**원인**: `/news/refresh`는 `@PostMapping`으로 선언되어 있어 **POST 요청만 허용**.  
브라우저로 직접 URL 입력 시 GET 요청이 전송되어 405 발생.  

**수정 방법**:
```bash
# 브라우저 X → PuTTy 또는 curl 에서 POST로 호출
curl -X POST http://54.180.142.111:8080/news/refresh
```

---

### 📸 155754 — EC2 `/stock/list` 0개 기업

```
총 0개 기업
오류한 주식이 없습니다
```

DB 연결 미해결 상태이므로 STOCK 테이블 데이터 없음. DB 연결 복구 후 크롤러 실행 필요.

---

### 📸 155923 — EC2 `/news/list` DB 오류

```
DB 오류 발생합니다: HikariPool-1 - Connection is not available...
뉴스를 불러오는데 실패했습니다: HTTP 500
```

DB 연결 실패가 원인. 연결 복구 시 자동 해소.

---

### 📸 160106 / 160212 — EC2 회원가입 / 로그인 페이지

페이지 렌더링 정상 ✅  
프론트엔드(JSP, CSS, JS)는 완전히 정상 작동.  
DB 연결만 복구되면 회원가입·로그인도 정상 동작 예상.

---

### 📸 160552 — **[핵심 발견]** PuTTy: mysql CLI → RDS 직접 연결 성공

```bash
ubuntu@ip-172-31-36-228:~$ mysql \
  -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
  -P 3306 -u admin -p
Enter password:
Welcome to the MySQL monitor. Commands end with ; or \g.
Server version: 8.4.7 Source distribution
mysql>
```

**✅ EC2에서 RDS(MySQL 8.4.7)로 TCP 연결 성공!**  
RDS 서버 자체는 살아 있고, EC2에서 네트워크 경로도 존재함.  
이 사실이 매우 중요함 → Tomcat이 연결 못하는 이유가 별도로 존재.

#### ❌ SQL 오류: `ERROR 1046 (3D000): No database selected`

```sql
mysql> INSERT IGNORE INTO MEMBER (email, password, ...) VALUES (...);
ERROR 1046 (3D000): No database selected

mysql> SELECT member_id, email, name, role FROM MEMBER;
ERROR 1046 (3D000): No database selected
```

**원인**: mysql CLI 접속 후 `USE portwatch;` 명령을 실행하지 않아 DB가 선택되지 않은 상태.

**수정 SQL** (PuTTy에서 재실행):
```sql
-- 반드시 이 명령을 먼저 실행
USE portwatch;

-- 관리자 계정 삽입
INSERT IGNORE INTO MEMBER (email, password, name, phone, role, created_at, is_active)
VALUES (
    'admin@portwatch.com',
    '$2a$10$baT2uBDFEOC.3UPGnHN.Cu8qUifq2UgJ7HbS7FYCtenGetdcmmqd6',
    '관리자', '010-0000-0000', 'ADMIN', NOW(), 1
);

-- 테스트 계정 삽입
INSERT IGNORE INTO MEMBER (email, password, name, phone, role, created_at, is_active)
VALUES (
    'test@portwatch.com',
    '$2a$10$baT2uBDFEOC.3UPGnHN.Cu8qUifq2UgJ7HbS7FYCtenGetdcmmqd6',
    '테스트유저', '010-1234-5678', 'USER', NOW(), 1
);

-- 확인
SELECT member_id, email, name, role, is_active FROM MEMBER;
```

비밀번호 (공통): `Admin2026!`

---

### 📸 160612 — PuTTy: curl 및 nc 테스트

```bash
curl -I http://54.180.142.111:8080/debug/db-check
# → DB 연결 실패 응답

nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306
# → connection timed out
```

**중요**: mysql CLI는 성공했는데 nc는 타임아웃 → **IPv6 문제**

| 도구 | 결과 | 이유 |
|------|------|------|
| `mysql -h RDS_HOST -P 3306` | ✅ 성공 | MySQL 클라이언트는 기본 IPv4 우선 사용 |
| `nc -zv RDS_HOST 3306` | ❌ 타임아웃 | nc는 IPv6을 먼저 시도 → RDS는 IPv4만 지원 |
| Java/Tomcat (HikariCP) | ❌ 타임아웃 | `preferIPv4Stack=true` 누락/손실 가능성 |

**nc IPv4 강제 테스트**:
```bash
nc -4 -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306
# -4 플래그로 IPv4 강제 → 이번에는 succeeded 예상
```

---

### 📸 160633 — PuTTy: WAR 재배포 절차

```bash
sudo rm -rf /opt/tomcat/webapps/ROOT
sudo cp /home/ubuntu/portwatch.war /opt/tomcat/webapps/ROOT.war
sudo systemctl start tomcat
sleep 20
```

WAR 재배포 완료. 그러나 **setenv.sh 확인 없이 재시작** → preferIPv4Stack 미검증.

---

## 3. 핵심 원인 분석 — EC2 Tomcat DB 연결 실패

### 3-1. 오류 연쇄 구조

```
Tomcat (Java/HikariCP) → RDS 연결 시도
  ↓
com.mysql.cj.protocol.a.NativeSocketConnection.connect()
  ↓
java.net.InetAddress.getAllByName("portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com")
  ↓
preferIPv4Stack=true가 없으면 → IPv6 주소 조회 시도
  ↓
RDS는 IPv4만 지원 → IPv6 연결 실패 → 타임아웃 30초 대기
  ↓
HikariPool-1: Connection is not available, request timed out after 30000ms
```

### 3-2. 근거

| 증거 | 내용 |
|------|------|
| mysql CLI 성공 (160552) | EC2에서 RDS 포트 3306 접근 가능 |
| nc -zv 실패 (160612) | nc의 기본 IPv6 시도 시 타임아웃 |
| Java UnknownHostException (INI 로컬) | Java의 DNS 해석 실패 패턴 동일 |
| 과거 `preferIPv4Stack=true` 설정 | 이전 세션에서 이 설정으로 개선됨 |

### 3-3. 검증 방법

```bash
# 1. setenv.sh 현재 내용 확인
cat /opt/tomcat/bin/setenv.sh

# 기대값:
# export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"

# 2. nc IPv4 강제 테스트
nc -4 -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306
# 기대값: Connection to ... 3306 port [tcp/mysql] succeeded!
```

---

## 4. EC2 수정 절차 (PuTTy 실행 순서)

### Step 0 — setenv.sh 확인 및 재설정

```bash
# 현재 내용 확인
cat /opt/tomcat/bin/setenv.sh

# 내용이 비어 있거나 preferIPv4Stack이 없으면 재작성
sudo tee /opt/tomcat/bin/setenv.sh << 'EOF'
#!/bin/bash
export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"
EOF
sudo chmod 755 /opt/tomcat/bin/setenv.sh
cat /opt/tomcat/bin/setenv.sh   # 확인
```

### Step 1 — nc IPv4 테스트 (RDS 연결 확인)

```bash
nc -4 -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306
# → succeeded! 나와야 함
```

### Step 2 — Tomcat 재시작

```bash
sudo systemctl restart tomcat
sleep 25

# Tomcat 기동 확인
sudo systemctl status tomcat
curl http://localhost:8080/debug/db-check
# 기대값: {"dbConnected":true,"success":true}
```

### Step 3 — SQL INSERT (계정 생성)

```bash
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -P 3306 -u admin -p
```

mysql> 프롬프트에서:
```sql
USE portwatch;

INSERT IGNORE INTO MEMBER (email, password, name, phone, role, created_at, is_active)
VALUES ('admin@portwatch.com',
        '$2a$10$baT2uBDFEOC.3UPGnHN.Cu8qUifq2UgJ7HbS7FYCtenGetdcmmqd6',
        '관리자', '010-0000-0000', 'ADMIN', NOW(), 1);

INSERT IGNORE INTO MEMBER (email, password, name, phone, role, created_at, is_active)
VALUES ('test@portwatch.com',
        '$2a$10$baT2uBDFEOC.3UPGnHN.Cu8qUifq2UgJ7HbS7FYCtenGetdcmmqd6',
        '테스트유저', '010-1234-5678', 'USER', NOW(), 1);

SELECT member_id, email, name, role, is_active FROM MEMBER;
```

### Step 4 — 데이터 크롤링 (DB 연결 성공 확인 후)

```bash
# 주식 데이터 (POST 방식 필수)
curl -X POST http://localhost:8080/crawler/korea
sleep 15
curl -X POST http://localhost:8080/crawler/us

# 뉴스 데이터 (POST 방식 필수)
curl -X POST http://localhost:8080/news/refresh

# 주가 업데이트
curl -X POST http://localhost:8080/crawler/price
```

### Step 5 — 외부 접속 테스트

```bash
curl http://54.180.142.111:8080/stock/list    | grep "기업"
curl http://54.180.142.111:8080/news/list     | grep "뉴스"
```

---

## 5. INI 파일 분석 (로컬 STS — 15:15 ~ 15:56)

### 5-1. 이번 세션 긍정적 변화

```
✅ ClassNotFoundException: MemberVO → 이번 세션에서 발생 없음!
✅ Spring Context 초기화 성공
✅ 모든 컨트롤러 매핑 정상
✅ 스케줄러(NewsScheduler, StockPriceScheduler) 정상 기동
```

**이전 세션에서 "STS Project Clean → Publish → Start" 절차를 올바르게 수행한 결과**.

### 5-2. 반복 오류 — HikariPool Connection Timeout

```
HikariPool-1 - Connection is not available, request timed out after 30007ms
  → Caused by: Communications link failure
  → Caused by: java.net.UnknownHostException:
      알려진 호스트가 없습니다
      (portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com)
```

**원인**: 로컬 Windows PC는 RDS Private VPC 호스트명을 DNS 해석 불가.  
RDS가 VPC 내부 전용(Public Accessible = No)이므로 인터넷에서 접근 불가.

**영향 범위**:
- StockPriceScheduler: 5분마다 실행되지만 전 DB 작업 실패
- NewsScheduler [US-RSS]: 20건 크롤링했으나 저장 실패 (0건 저장)
- NewsScheduler [KR]: 크롤링 실패
- `/crawler/korea`, `/crawler/us`: CannotCreateTransactionException
- `/news/refresh` GET 호출: `Request method 'GET' not supported` (HTTP 405)

### 5-3. 오류 흐름 타임라인

```
15:15:06 - Maven clean 완료
15:16:06 - Maven install 완료 (84개 소스 파일 컴파일)
15:xx    - STS Tomcat 기동 → Spring Context 초기화 성공 ✅
15:30    - StockPriceScheduler: 한국 100대 기업 크롤링 시작 → DB 실패
15:32    - NewsScheduler [US]: 미국 뉴스 크롤링 시작 → 20건 발견, 0건 저장
15:55    - NewsScheduler [KR]: 한국 뉴스 크롤링 시작 → DB 실패
15:56    - /news/refresh GET 호출 → HTTP 405 (POST 필요)
```

### 5-4. 로컬 RDS 연결 방법

**방법 A — RDS 퍼블릭 액세스 허용 (권장)**:

```
AWS Console → RDS → portwatch-db-seoul → [수정]
→ "연결" 섹션 → "퍼블릭 액세스 가능" → [예] 선택
→ [계속] → "즉시 적용" → [DB 인스턴스 수정]

RDS Security Group 인바운드 추가:
→ MySQL/Aurora, 3306, 소스: "내 IP" (현재 공인 IP 자동 입력)
```

**방법 B — SSH 터널 (PuTTy 설정)**:

```
PuTTy → Connection → SSH → Tunnels
  Source port: 3307
  Destination: portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306
  → [Add] → Open (EC2 SSH 연결 유지)

root-context.xml jdbcUrl 임시 변경 (로컬 전용):
  jdbc:mysql://localhost:3307/portwatch?sslMode=PREFERRED&...
```

---

## 6. Spring 5.0.7 ↔ AWS RDS MySQL 연결 구조 분석

### 6-1. 현재 연결 아키텍처

```
[로컬 Windows STS]                    [AWS Seoul Region]
  STS Tomcat                              ┌─────────────────────────┐
  └─ HikariCP 3.4.5                       │  VPC: vpc-09bafc55952dfe978   │
       └─ MySQL Connector/J 9.1.0         │  CIDR: 172.31.0.0/16    │
            └─ DNS 조회 시도               │                         │
                 ↓                        │  [EC2] ip-172-31-36-228 │
          ❌ UnknownHostException         │    └─ Tomcat 9.0.98     │
             (Private VPC DNS)           │         └─ HikariCP     │
                                         │              │           │
                                         │              ↓ TCP:3306  │
[EC2 PuTTy SSH]                         │   [RDS] 10.0.1.109       │
  ubuntu user                           │   MySQL 8.4.7            │
  └─ mysql CLI ─────────────────────────→   ✅ 연결 성공           │
  └─ nc (IPv6 기본) ──────────────────────→  ❌ 타임아웃           │
                                         └─────────────────────────┘
```

### 6-2. Spring–MyBatis–HikariCP 연결 설정 (정상 상태)

```xml
<!-- root-context.xml (현재 설정 — 정상) -->
<bean id="dataSource" class="com.zaxxer.hikari.HikariDataSource" destroy-method="close">
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
    <property name="jdbcUrl" value="jdbc:mysql://portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306/portwatch?sslMode=PREFERRED&amp;serverTimezone=Asia/Seoul&amp;characterEncoding=UTF-8&amp;useUnicode=true&amp;allowPublicKeyRetrieval=true&amp;connectTimeout=30000&amp;socketTimeout=60000&amp;tcpKeepAlive=true&amp;zeroDateTimeBehavior=convertToNull"/>
    <property name="username" value="admin"/>
    <property name="password" value="portwatch2026!!abcd"/>
    <property name="maximumPoolSize" value="15"/>
    <property name="minimumIdle" value="3"/>
    <property name="connectionTimeout" value="30000"/>
    <property name="idleTimeout" value="120000"/>
    <property name="maxLifetime" value="180000"/>
    <property name="connectionTestQuery" value="SELECT 1"/>
    <property name="initializationFailTimeout" value="-1"/>
</bean>
```

**각 파라미터 설명**:

| 파라미터 | 값 | 이유 |
|---------|---|------|
| `sslMode=PREFERRED` | SSL 우선 사용 | MySQL 8.4+ 기본값, RDS `require_secure_transport=ON` 대응 |
| `serverTimezone=Asia/Seoul` | KST 타임존 | 한국 시간 기준 데이터 처리 |
| `allowPublicKeyRetrieval=true` | 공개키 자동 수신 | MySQL 8.x `caching_sha2_password` 인증 지원 |
| `tcpKeepAlive=true` | TCP 킵얼라이브 | AWS NAT Gateway 4분 유휴 끊김 방지 |
| `initializationFailTimeout=-1` | 기동 시 DB 실패 무시 | EC2 재시작 안전 |
| `maxLifetime=180000` | 3분 | AWS NAT 4분 타임아웃 이전에 연결 교체 |

### 6-3. EC2 Tomcat setenv.sh (필수 JVM 설정)

```bash
# /opt/tomcat/bin/setenv.sh
#!/bin/bash
export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"
```

| 파라미터 | 효과 |
|---------|------|
| `-Djava.net.preferIPv4Stack=true` | Java DNS 해석 시 IPv4 우선 (IPv6 시도 방지) → RDS 연결 성공의 핵심 |
| `-Xms256m -Xmx512m` | JVM 힙 메모리 설정 (t3.micro 기준) |

---

## 7. 오류 목록 및 수정 내용

### 7-1. ❌ SQL ERROR 1046: No database selected

**발생 위치**: PuTTy mysql CLI (스크린샷 160552)  
**원인**: `mysql -h RDS_HOST -u admin -p` 접속 후 `USE portwatch;` 미실행  
**수정**:
```sql
-- mysql> 프롬프트에서 첫 줄로 반드시 실행
USE portwatch;
```

---

### 7-2. ❌ /news/refresh HTTP 405 Method Not Allowed

**발생 위치**: 스크린샷 155631 (브라우저 직접 접근) + INI 라인 652  
**원인**: `NewsController.refresh()`는 `@PostMapping("/refresh")` → GET 불가  
**수정**:
```bash
# 브라우저 주소창 입력 X
# curl로 POST 요청
curl -X POST http://54.180.142.111:8080/news/refresh
curl -X POST http://localhost:8080/news/refresh  # EC2 로컬에서
```

---

### 7-3. ❌ EC2 Tomcat HikariPool Connection Timeout

**발생 위치**: 모든 EC2 API 응답 (155352, 155507, 155603)  
**원인 추정**: `setenv.sh`의 `preferIPv4Stack=true` 누락 또는 손실  
**수정**: 4절 Step 0 참조 (setenv.sh 재작성 후 Tomcat 재시작)

---

### 7-4. ⚠️ nc -zv 타임아웃 (IPv6 사용)

**발생 위치**: 스크린샷 160612  
**원인**: nc 기본값이 IPv6 주소 사용 → RDS IPv4만 지원  
**수정**:
```bash
# IPv4 강제 (nc -4 플래그)
nc -4 -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306
```

---

## 8. 최종 체크리스트

### EC2 (PuTTy — 즉시 처리)

```
[ ] cat /opt/tomcat/bin/setenv.sh → preferIPv4Stack=true 확인
[ ] (없으면) setenv.sh 재작성
[ ] nc -4 -zv RDS_HOST 3306 → succeeded! 확인
[ ] sudo systemctl restart tomcat && sleep 25
[ ] curl http://localhost:8080/debug/db-check → {"dbConnected":true}
[ ] mysql 접속 → USE portwatch; 후 INSERT IGNORE INTO MEMBER
[ ] curl -X POST http://localhost:8080/crawler/korea
[ ] curl -X POST http://localhost:8080/crawler/us
[ ] curl -X POST http://localhost:8080/news/refresh
[ ] http://54.180.142.111:8080/stock/list → 기업 목록 표시
[ ] http://54.180.142.111:8080/news/list → 뉴스 표시
[ ] http://54.180.142.111:8080/member/login → admin@portwatch.com 로그인
```

### 로컬 STS (다음 Maven 재빌드 시)

```
[ ] Maven clean + Maven install
[ ] STS → Project → Clean → Clean all projects
[ ] Servers 탭 → Tomcat → Clean → Publish → Start
[ ] Console: "initialization completed" 확인
[ ] ClassNotFoundException 없음 확인
```

---

## 9. 이번 세션 수정 이력

| 파일/대상 | 수정 내용 | 상태 |
|---------|---------|------|
| EC2 `/opt/tomcat/bin/setenv.sh` | preferIPv4Stack=true 재확인/재설정 필요 | ❌ 미확인 |
| EC2 mysql CLI | `USE portwatch;` 추가 후 INSERT 재실행 필요 | ❌ 미완료 |
| EC2 Tomcat | setenv.sh 확인 후 재시작 필요 | ❌ 미완료 |
| 로컬 STS | ClassNotFoundException 해소 ✅ (이번 세션) | ✅ 해결됨 |
| 로컬 → RDS | Public Access 또는 SSH 터널 설정 필요 | ❌ 미완료 |

---

## 10. 전체 누적 수정 이력 (전 세션 포함)

| 세션 | 수정 대상 | 수정 내용 | 상태 |
|------|---------|---------|------|
| 이전 | `pom.xml` | MySQL Connector/J 8.0.33 → 9.1.0 | ✅ |
| 이전 | `root-context.xml` | `<property name="sslMode">` 태그 제거 | ✅ |
| 이전 | `root-context.xml` | sslMode=DISABLED → PREFERRED | ✅ |
| 이전 | `.launch` | preferIPv4Stack=true JVM 아규먼트 추가 | ✅ |
| 13:00 | `root-context.xml` | useSSL=false 제거 (Connector/J 9.1.0 호환) | ✅ |
| 13:39 | EC2 | ROOT.war 새로 배포 | ✅ |
| 14:06 | AWS Console | RDS SG 인바운드: 172.31.0.0/16 추가 | ✅ |
| 14:16 | 로컬 STS | STS Project Clean → ClassNotFoundException 해소 | ✅ |
| 15:11 | EC2 PuTTy | mysql CLI 성공 확인 → 네트워크 경로 존재 확인 | ✅ 확인 |
| 15:11 | EC2 setenv.sh | preferIPv4Stack=true 재확인 필요 | ❌ 진행 중 |
| 15:11 | mysql CLI | USE portwatch; 추가 필요 | ❌ 미완료 |

---

*PortWatch v1.0.0 | Spring 5.0.7 | MySQL 8.4.7 | MySQL Connector/J 9.1.0 | HikariCP 3.4.5 | MyBatis 3.5.6 | Tomcat 9.0.98*
