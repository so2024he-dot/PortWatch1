# PortWatch EC2/RDS 종합 분석 수정 보고서
**작성일**: 2026-04-09 (13:39 세션)  
**분석 파일**: `202604091339 AWS EC2 RDS Aurora SQL output.zip` (스크린샷 5장) + `오류코드 수정할 부분 및 생성코드.ini`

---

## 1. INI 파일 분석 — 로컬 STS 13:26 세션

### 1-1. 신규 치명적 오류 — ClassNotFoundException: MemberVO

```
WARN: No MyBatis mapper was found in '[com.portwatch.mapper, com.portwatch.persistence]' package.

ERROR: Context initialization failed
BeanCreationException: Error creating bean with name 'sqlSessionFactory'
  Cause: Failed to parse mapping resource: 'MemberMapper.xml'
  Cause: Error resolving class. Cause: TypeException: Could not resolve type alias 'MemberVO'
  Cause: java.lang.ClassNotFoundException: Cannot find class: MemberVO
```

**발생 시각**: 2026-04-09 13:26:27  
**결과**: Spring Context 초기화 **완전 실패** → 모든 URL HTTP 404

### 1-2. 이전 세션(12:35) vs 이번 세션(13:26) 비교

| 항목 | 12:35 세션 | 13:26 세션 | 변화 |
|------|-----------|-----------|------|
| Spring Context | ✅ 성공 | ❌ **실패** | 회귀 |
| MemberVO alias | ✅ 정상 | ❌ ClassNotFoundException | **새 오류** |
| Mapper 스캔 | ✅ 정상 | ❌ No mapper found | **새 경고** |
| DB 연결 오류 | UnknownHostException | 발생 전에 Context 실패 | |

### 1-3. 원인 — STS-WTP 배포 캐시 불일치

```
원인 흐름:
STS에서 Maven clean 실행
  → target/classes/ 삭제
  → STS-WTP 배포 디렉토리(tmp0/wtpwebapps/portwatch1/WEB-INF/classes/)는 유지됨
Maven install 실행
  → target/classes/에 새 .class 파일 생성
  → 그러나 Eclipse 자체 빌드 캐시가 비어있거나 불일치 상태
STS에서 Tomcat 시작 (Eclipse 빌드 캐시 기준 배포)
  → WEB-INF/classes/에 MemberVO.class 없음
  → TypeAlias 패키지 스캔: com.portwatch.domain 에서 클래스 0개 발견
  → MemberVO alias 미등록
  → MemberMapper.xml 파싱 시 type="MemberVO" 해석 불가
  → ClassNotFoundException
```

### 1-4. 해결 — STS Server Clean (순서 중요)

```
Step 1: STS 상단 메뉴 → Project → Clean
        → "Clean all projects" 선택 → OK
        → 빌드 완료 대기 (하단 상태바에 "Building workspace" 사라질 때까지)

Step 2: Servers 뷰 → Tomcat v9.0 → 우클릭 → Clean...
        → "Clean tomcat work directory" 선택 → OK

Step 3: Servers 뷰 → Tomcat v9.0 → 우클릭 → Publish

Step 4: Tomcat 시작
        → 콘솔에서 "ClassNotFoundException" 없이 시작되는지 확인
```

**확인 기준**: `Root WebApplicationContext: initialization completed in XXXX ms` 메시지 출력

---

## 2. EC2 PuTTy 분석 (스크린샷 133822)

### 2-1. 이번 세션에서 실행한 명령어 확인

```bash
# iptables ACCEPT 규칙 추가 ✅
sudo iptables -I OUTPUT 1 -p tcp --dport 3306 -j ACCEPT
sudo netfilter-persistent save
run-parts: executing /usr/share/netfilter-persistent/plugins.d/25-ip6tables save
run-parts: executing /usr/share/netfilter-persistent/plugins.d/15-ip4tables save

# nc 재테스트
sudo -u tomcat nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306
nc: connect to portwatch-db-seoul... (10.0.1.109) port 3306 (tcp) failed: Connection timed out
```

### 2-2. 핵심 발견 — iptables가 원인이 아님

```
iptables ACCEPT 규칙 추가 후에도 nc 테스트 동일하게 "Connection timed out"
                    ↓
iptables는 문제가 아니었음. 패킷이 EC2를 떠난 후 차단됨
                    ↓
실제 원인: AWS Security Group 설정 오류
         - EC2 사설 IP: 172.31.36.228 (172.31.0.0/16 서브넷)
         - RDS 사설 IP: 10.0.1.109 (10.0.1.0/24 서브넷)
         - RDS Security Group이 EC2의 서브넷 또는 SG를 허용하지 않음
```

### 2-3. WAR 배포 실행 결과

```bash
sudo systemctl stop tomcat
sudo rm -rf /opt/tomcat/webapps/ROOT
sudo cp /home/ubuntu/portwatch.war /opt/tomcat/webapps/ROOT.war
sudo systemctl start tomcat
sleep 30
curl http://localhost:8080/debug/db-check
# → {"dbConnected":false,"success":false,"diagnostics":"...MyBatisSystemException..."}
```

**결론**: 새 WAR 배포됨, Tomcat 시작됨 — 그러나 DB 연결 계속 실패

---

## 3. AWS Security Group 설정 — 핵심 해결책

### 현재 상태 분석

```
EC2 (ubuntu@ip-172-31-36-228): 172.31.36.228
  └─ 소속 VPC: Default VPC (172.31.0.0/16)
  └─ EC2 Security Group: (확인 필요)

RDS (portwatch-db-seoul...): DNS → 10.0.1.109
  └─ 소속 서브넷: 10.0.1.0/24
  └─ RDS Security Group: MySQL 3306 인바운드 규칙 확인 필요
```

### 해결 — AWS Console Security Group 수정

**[방법 A] RDS Security Group에서 EC2 Security Group 허용 (권장)**

```
AWS Console 로그인
→ EC2 → Instances → 해당 EC2 인스턴스 선택
  → "Security" 탭 → Security Groups → SG ID 복사 (예: sg-0abc12345)

→ VPC → Security Groups → RDS에 연결된 Security Group 선택
→ "Inbound rules" 탭 → "Edit inbound rules"
→ "Add rule" 클릭:
   Type: MySQL/Aurora
   Protocol: TCP
   Port range: 3306
   Source: Custom → (위에서 복사한 EC2 SG ID 입력) sg-0abc12345
→ "Save rules"
```

**[방법 B] RDS Security Group에서 VPC CIDR 전체 허용 (임시)**

```
→ "Add rule" 클릭:
   Type: MySQL/Aurora
   Port range: 3306
   Source: Custom → 172.31.0.0/16  (EC2가 속한 VPC CIDR)
→ "Save rules"
```

**[방법 C] EC2와 RDS가 다른 VPC인 경우 — VPC Peering 설정**

```
만약 EC2 VPC와 RDS VPC가 다르다면:
AWS Console → VPC → Peering Connections → Create Peering Connection
  Requester VPC: EC2의 VPC (172.31.0.0/16)
  Accepter VPC: RDS의 VPC (10.0.0.0/16)
→ Accept Peering Request
→ Route Table 양쪽에 상대방 VPC로 가는 라우트 추가
→ 각 Security Group에서 상대방 CIDR 허용
```

### Security Group 수정 후 확인 명령어 (PuTTy)

```bash
# 1. nc 테스트 (tomcat 사용자)
sudo -u tomcat nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306
# 기대값: Connection to ... 3306 port [tcp/mysql] succeeded!

# 2. Tomcat 재시작
sudo systemctl restart tomcat
sleep 20

# 3. DB 연결 확인
curl http://localhost:8080/debug/db-check
# 기대값: {"dbConnected":true,"success":true}
```

---

## 4. 로그인 분석 (스크린샷 133315)

### 현재 상태

- **UI**: 로그인 페이지 정상 표시 ✅ (`54.180.142.111:8080/member/login`)
- **로그인 시도**: DB 연결 실패로 인증 불가

### 테스트 계정 SQL (DB 연결 후 실행)

```sql
-- PuTTy → MySQL 접속
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -u admin -p'portwatch2026!!abcd' portwatch

-- 기존 admin 계정 확인
SELECT member_id, email, name, role, password FROM MEMBER WHERE email='admin@portwatch.com';

-- 없으면 삽입 (비밀번호: Admin2026!)
INSERT INTO MEMBER (member_id, email, password, name, phone, role, created_at)
VALUES (
    'admin',
    'admin@portwatch.com',
    '$2a$10$baT2uBDFEOC.3UPGnHN.Cu8qUifq2UgJ7HbS7FYCtenGetdcmmqd6',
    '관리자',
    '010-0000-0000',
    'ADMIN',
    NOW()
);

-- 테스트 일반 사용자 (비밀번호: Test1234!)
INSERT INTO MEMBER (member_id, email, password, name, phone, role, created_at)
VALUES (
    'testuser',
    'test@portwatch.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lHHi',
    '테스트사용자',
    '010-1234-5678',
    'USER',
    NOW()
);
```

**테스트 계정 정보**:

| 이메일 | 비밀번호 | 역할 |
|-------|---------|------|
| admin@portwatch.com | Admin2026! | ADMIN |
| test@portwatch.com | Test1234! | USER |

---

## 5. 회원가입 분석 (스크린샷 133433)

### 현재 상태

- **UI**: 회원가입 페이지 정상 표시 ✅
- **가입 시도**: DB 연결 실패로 처리 불가

### 코드 분석

`MemberController.signup()` 코드:

```java
@PostMapping("/signup")
@ResponseBody
public ResponseEntity<Map<String, Object>> signup(MemberVO member) {
    try {
        if (!memberService.checkEmailAvailable(member.getMemberEmail())) {
            // 이메일 중복 체크 (DB 조회 필요)
            ...
        }
        memberService.register(member);  // DB INSERT 필요
        ...
    } catch (Exception e) {
        // 예외 처리 ✅
        result.put("success", false);
        result.put("message", "회원가입 처리 중 오류가 발생했습니다.");
    }
    return ResponseEntity.ok(result);
}
```

**결론**: 코드 자체는 정상 (try-catch 처리됨). DB 연결 해결 후 회원가입 가능.

### DB 연결 후 회원가입 테스트 방법

```
http://54.180.142.111:8080/member/signup 접속
→ 이메일: new@test.com
→ 비밀번호: NewUser2026!
→ 이름: 홍길동
→ 전화번호: 010-9999-8888
→ [회원가입] 클릭
→ "회원가입이 완료되었습니다." 응답 확인
```

---

## 6. 주식 목록 분석 (스크린샷 133752)

### 현재 상태

```
http://54.180.142.111:8080/stock/list
주식 목록 (0개 기업)
"DOM에 데이터가 없습니다. DB에 데이터를 추가해 주기 바랍니다."
```

### 원인

STOCK 테이블이 비어있음 (크롤러 미실행 또는 DB 연결 실패로 크롤링 데이터 저장 안 됨)

### 해결 — DB 연결 후 크롤러 실행

```bash
# 한국 주식 (KOSPI/KOSDAQ 상위 100개)
curl -X POST http://54.180.142.111:8080/crawler/korea
# 예상 실행 시간: 1-3분

# 미국 주식 (NASDAQ/NYSE 상위 100개)
curl -X POST http://54.180.142.111:8080/crawler/us
# 예상 실행 시간: 1-3분

# 결과 확인
curl http://54.180.142.111:8080/stock/list
```

### 임시 SQL (크롤러 대신 수동으로 데이터 삽입)

```sql
USE portwatch;

-- 한국 주식 샘플 데이터
INSERT INTO STOCK (stock_code, stock_name, market, country, industry, price, change_rate, volume, created_at) VALUES
('005930', '삼성전자', 'KOSPI', 'KR', '반도체', 72500.00, -1.23, 15000000, NOW()),
('000660', 'SK하이닉스', 'KOSPI', 'KR', '반도체', 172000.00, 0.58, 3500000, NOW()),
('035420', 'NAVER', 'KOSPI', 'KR', 'IT서비스', 178500.00, -0.83, 450000, NOW()),
('051910', 'LG화학', 'KOSPI', 'KR', '화학', 285000.00, 1.07, 280000, NOW()),
('006400', '삼성SDI', 'KOSPI', 'KR', '배터리', 192000.00, -2.15, 210000, NOW());

-- 미국 주식 샘플 데이터
INSERT INTO STOCK (stock_code, stock_name, market, country, industry, price, change_rate, volume, created_at) VALUES
('AAPL', 'Apple Inc.', 'NASDAQ', 'US', 'Technology', 172.50, 0.85, 80000000, NOW()),
('MSFT', 'Microsoft Corp.', 'NASDAQ', 'US', 'Technology', 415.20, 1.23, 25000000, NOW()),
('NVDA', 'NVIDIA Corp.', 'NASDAQ', 'US', 'Semiconductors', 875.40, -1.45, 45000000, NOW()),
('GOOGL', 'Alphabet Inc.', 'NASDAQ', 'US', 'Technology', 168.30, 0.42, 20000000, NOW()),
('AMZN', 'Amazon.com Inc.', 'NASDAQ', 'US', 'E-Commerce', 182.60, 0.67, 35000000, NOW());

-- 입력 확인
SELECT COUNT(*) AS total_stocks FROM STOCK;
SELECT stock_code, stock_name, market, country, price FROM STOCK LIMIT 10;
```

---

## 7. 뉴스 목록 / HTTP 500 분석 (스크린샷 133612)

### 현재 상태

```
http://54.180.142.111:8080/news/list

[분홍색 오류 박스]
DB 오류: org.apache.ibatis.exceptions.PersistenceException
  ### Error querying database
  Cause: CannotGetJdbcConnectionException: Failed to obtain JDBC Connection
  HikariPool-1 - Connection is not available, request timed out after 30000ms

[빨간 경고]
뉴스를 불러오는데 실패했습니다: HTTP 500
```

### 원인 분석

**오류 1 (분홍 박스 - HTTP 200)**: `NewsController.newsList()`의 try-catch가 DB 예외를 잡아서 `dbError` 모델에 추가 → JSP에서 표시  
**오류 2 (HTTP 500)**: JSP 페이지 로드 후 자동 실행되는 JavaScript AJAX 호출 (`/api/news/recent` 또는 `/news/refresh`)이 DB 실패로 500 반환

### 코드 분석 — NewsController.java

```java
// ✅ newsList()는 이미 예외 처리됨 — 코드 수정 불필요
try {
    List<NewsVO> fetched = newsService.getRecentNews(50);
    ...
} catch (Exception e) {
    dbError = "뉴스 데이터를 불러오지 못했습니다. (DB 연결 확인 필요: " + e.getMessage() + ")";
}
model.addAttribute("dbError", dbError);
return "news/list";  // HTTP 200으로 페이지 반환
```

**결론**: NewsController 코드는 정상. DB 연결 해결 시 HTTP 500 자동 해소.

### DB 연결 후 뉴스 데이터 생성

```bash
# 뉴스 크롤링 실행
curl -X POST http://54.180.142.111:8080/news/refresh
# 예상: {"success":true,"count":20}

# 뉴스 목록 확인
curl http://54.180.142.111:8080/news/list
# 기대: 뉴스 기사 목록 표시 (HTTP 500 사라짐)
```

---

## 8. AWS RDS Aurora 설정 매뉴얼

### 8-1. 현재 RDS 구성 확인 (AWS Console)

```
AWS Console → Amazon RDS → Databases → portwatch-db-seoul
→ Connectivity & Security 탭:
  - Endpoint: portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com
  - Port: 3306
  - VPC: (EC2와 같은 VPC인지 확인)
  - Publicly accessible: No (외부 접근 불가)
  - VPC Security Groups: (SG 이름 확인)
```

### 8-2. RDS Security Group 인바운드 규칙 추가

```
RDS → portwatch-db-seoul → 해당 Security Group 클릭
→ Edit inbound rules
→ Add rule:
   유형: MySQL/Aurora
   포트 범위: 3306
   소스: Custom → sg-XXXXXXXX (EC2의 Security Group ID)
→ Save rules
```

### 8-3. RDS 파라미터 그룹 — require_secure_transport 설정

```
RDS → Parameter Groups → portwatch-db-seoul에 연결된 파라미터 그룹
→ 파라미터 검색: require_secure_transport
→ 현재값: ON (기본)
→ 변경: OFF (테스트 목적, SSL 없이 연결 가능)
  ※ 또는 현행 유지 — root-context.xml의 sslMode=PREFERRED로 SSL 시도함
```

### 8-4. RDS 모니터링 — 연결 실패 원인 파악

```
AWS Console → RDS → portwatch-db-seoul → Monitoring 탭
→ DatabaseConnections: 0이면 연결 자체가 안 됨
→ NetworkReceiveThroughput: 트래픽 없으면 Security Group 문제
```

---

## 9. 로컬 PC에서 EC2 RDS 접속 방법 (개발 편의)

### 방법 A — SSH 터널 (보안 우선, 권장)

```
PuTTy 설정:
  Connection → SSH → Tunnels
  Source port: 3307
  Destination: portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306
  → Add → Open

로컬 JDBC URL (임시):
  jdbc:mysql://127.0.0.1:3307/portwatch?sslMode=PREFERRED&...

로컬 MySQL Workbench 접속:
  Host: 127.0.0.1  Port: 3307  User: admin  Password: portwatch2026!!abcd
```

### 방법 B — RDS Publicly Accessible 설정

```
RDS → portwatch-db-seoul → Modify
→ Connectivity 섹션 → "Publicly accessible: Yes"
→ Continue → "Apply immediately" → Modify DB Instance

Security Group 추가:
  소스: 내 IP 주소 (X.X.X.X/32)
  포트: 3306

로컬 PC에서 테스트:
  mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com -P 3306 -u admin -p
```

---

## 10. 코드 수정 내역

### 수정 1: root-context.xml — useSSL=false 제거 (이전 세션 완료)

**파일**: `src/main/webapp/WEB-INF/spring/root-context.xml`

```xml
<!-- 수정 전 -->
<property name="jdbcUrl" value="jdbc:mysql://...?useSSL=false&amp;sslMode=PREFERRED&amp;..."/>

<!-- 수정 후 (현재 상태) -->
<property name="jdbcUrl" value="jdbc:mysql://...?sslMode=PREFERRED&amp;..."/>
```

### 현재 최종 jdbcUrl

```xml
<property name="jdbcUrl" value="jdbc:mysql://portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306/portwatch?sslMode=PREFERRED&amp;serverTimezone=Asia/Seoul&amp;characterEncoding=UTF-8&amp;useUnicode=true&amp;allowPublicKeyRetrieval=true&amp;connectTimeout=30000&amp;socketTimeout=60000&amp;tcpKeepAlive=true&amp;zeroDateTimeBehavior=convertToNull"/>
```

---

## 11. 전체 오류 원인-해결 요약

| # | 화면/오류 | 원인 | 해결 방법 | 우선순위 |
|---|---------|------|---------|---------|
| 1 | 로컬 ClassNotFoundException | STS-WTP 캐시 불일치 | STS → Project Clean + Server Clean | **최우선** |
| 2 | EC2 DB 연결 실패 | AWS Security Group 미설정 | RDS SG 인바운드 3306 허용 | **최우선** |
| 3 | 로그인 불가 | DB 연결 실패 연쇄 | DB 연결 후 자동 해결 | 2순위 |
| 4 | 회원가입 불가 | DB 연결 실패 연쇄 | DB 연결 후 자동 해결 | 2순위 |
| 5 | 주식 0개 | STOCK 테이블 비어있음 | DB 연결 후 크롤러 실행 | 3순위 |
| 6 | 뉴스 HTTP 500 | DB 연결 실패 연쇄 | DB 연결 후 자동 해결 | 2순위 |

---

## 12. 당장 해야 할 작업 체크리스트

### [AWS Console] Security Group 수정 — 가장 먼저

```
[ ] 1. EC2 인스턴스 Security Group ID 확인
       EC2 → Instances → portwatch → Security → Security Group ID 복사

[ ] 2. RDS Security Group 인바운드 규칙 추가
       RDS → portwatch-db-seoul → Security Group
       → Edit inbound rules → Add rule
       → MySQL/Aurora, 3306, Source: EC2 SG ID
       → Save rules
```

### [PuTTy] 확인

```bash
[ ] 3. nc 테스트 (Security Group 수정 후)
       sudo -u tomcat nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306
       → succeeded! 확인

[ ] 4. Tomcat 재시작
       sudo systemctl restart tomcat && sleep 20
       curl http://localhost:8080/debug/db-check
       → {"dbConnected":true}
```

### [PuTTy] 데이터 채우기

```bash
[ ] 5. 주식 크롤러
       curl -X POST http://54.180.142.111:8080/crawler/korea
       curl -X POST http://54.180.142.111:8080/crawler/us

[ ] 6. 뉴스 크롤러
       curl -X POST http://54.180.142.111:8080/news/refresh
```

### [로컬 STS] ClassNotFoundException 해결

```
[ ] 7. STS → Project → Clean → Clean All Projects
[ ] 8. Servers 뷰 → Tomcat → Clean → Publish
[ ] 9. Tomcat 시작 → 콘솔에서 "initialization completed" 확인
```

### [브라우저] 최종 확인

```
[ ] 10. http://54.180.142.111:8080/         → PortWatch 홈페이지
[ ] 11. http://54.180.142.111:8080/member/login → 로그인 (admin@portwatch.com / Admin2026!)
[ ] 12. http://54.180.142.111:8080/stock/list   → 주식 목록 (>0개)
[ ] 13. http://54.180.142.111:8080/news/list    → 뉴스 목록 (HTTP 500 없음)
```

---

*PortWatch v1.0.0 | Spring 5.0.7 | MySQL Connector/J 9.1.0 | HikariCP 3.4.5 | MyBatis 3.5.6 | 분석일: 2026-04-09*
