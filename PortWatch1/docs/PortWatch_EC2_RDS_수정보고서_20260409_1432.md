# PortWatch EC2/RDS 종합 분석 수정 보고서
**작성일**: 2026-04-09 (14:32 세션)  
**분석 파일**: `202604090227 AWS EC2 RDS Aurora SQL output.zip` (스크린샷 9장) + `오류코드 수정할 부분 및 생성코드.ini`

---

## 1. INI 파일 분석 — 로컬 STS 콘솔 로그 (2개 세션)

### 1-1. 세션 1 (14:04) — Maven clean + install 후 첫 기동

#### 성공 항목

| 항목 | 결과 | 상세 |
|------|------|------|
| Maven clean | ✅ 성공 | `BUILD SUCCESS` |
| Maven install | ✅ 성공 | `portwatch-1.0.0-BUILD-SNAPSHOT.war` 생성 |
| Spring Context 초기화 | ✅ 성공 | `initialization completed in 2893 ms` |
| 모든 컨트롤러 매핑 | ✅ 성공 | `/member/**`, `/stock/**`, `/news/**`, `/debug/**` 등 전 엔드포인트 매핑 |
| `preferIPv4Stack=true` | ✅ 적용됨 | JVM 명령줄 아규먼트 확인 |

#### 신규 오류 — HikariPool Connection Timeout (스케줄러)

```
ERROR: HikariPool-1 - Connection is not available, request timed out after 30005ms
  발생 위치: StockPriceScheduler (14:04 ~ 14:07)
```

**원인**: EC2 Security Group이 아직 반영 전 → RDS TCP 연결 불가  
**상태**: 이 세션에서 SG 업데이트 후 EC2 Tomcat 재시작 필요

---

### 1-2. 세션 2 (14:16) — "배포 후 콘솔 재시작" 레이블

#### ❌ 치명적 오류 재발 — ClassNotFoundException: MemberVO

```
WARN: No MyBatis mapper was found in '[com.portwatch.mapper, com.portwatch.persistence]' package
ERROR: Context initialization failed
  Caused by: org.apache.ibatis.builder.BuilderException: 
    Error parsing Mapper XML. Failed to parse mapping resource: 'MemberMapper.xml'
    Caused by: java.lang.ClassNotFoundException: Cannot find class: MemberVO
```

**Tomcat 기동 시각**: 14:16:38  
**오류 발생**: Context 초기화 단계 (`sqlSessionFactory` 빈 생성 시)  
**결과**: Spring ApplicationContext 완전 실패 → 모든 URL HTTP 500 또는 404

---

## 2. ClassNotFoundException: MemberVO 반복 오류 — 원인 및 영구 해결

### 2-1. 근본 원인 분석

```
Maven clean 실행
  └─ target/classes/ 디렉토리 전체 삭제
       └─ STS-WTP 배포 경로 (tmp0/wtpwebapps/portwatch1/WEB-INF/classes/) 는 그대로
            └─ MemberVO.class 등 domain 클래스 모두 사라짐
                 └─ mybatis-config.xml <package name="com.portwatch.domain"/> 스캔
                      └─ 스캔 결과: 0개 클래스 (디렉토리 비어 있음)
                           └─ MemberVO alias 미등록
                                └─ MemberMapper.xml 파싱 실패 → ClassNotFoundException
                                     └─ sqlSessionFactory 빈 생성 실패
                                          └─ Spring Context 초기화 완전 실패
```

**핵심**: Maven clean은 `target/` 만 삭제하지만, STS는 별도의 WTP 배포 폴더를 사용함.  
Maven rebuild 후 STS Project Clean을 하지 않으면 WTP 폴더에 클래스가 재생성되지 않음.

### 2-2. 즉시 해결 — STS Project Clean (필수 절차)

**Maven clean / Maven install 후 반드시 이 절차를 따를 것:**

```
① STS 상단 메뉴 → Project → Clean...
② "Clean all projects" 선택 → [OK]
③ Console에서 빌드 완료 메시지 확인 (오류 없을 시)
④ Servers 탭 → Tomcat → 우클릭 → Clean
⑤ Servers 탭 → Tomcat → 우클릭 → Clean Tomcat Work Directory
⑥ Servers 탭 → Tomcat → 우클릭 → Publish
⑦ Servers 탭 → Tomcat → 우클릭 → Start (또는 Restart)
```

> ⚠️ Maven clean 후 STS Tomcat을 바로 Start하면 항상 ClassNotFoundException 발생.  
> STS Project Clean → Publish → Start 순서를 반드시 지킬 것.

### 2-3. 영구 방지 — root-context.xml HikariCP 설정 강화

`initializationFailTimeout=-1` 설정으로 DB 연결 실패 시에도 Context 기동 허용:

```xml
<!-- root-context.xml HikariCP 설정 (initializationFailTimeout 확인) -->
<bean id="dataSource" class="com.zaxxer.hikari.HikariDataSource" destroy-method="close">
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
    <property name="jdbcUrl" value="jdbc:mysql://portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306/portwatch?sslMode=PREFERRED&amp;serverTimezone=Asia/Seoul&amp;characterEncoding=UTF-8&amp;useUnicode=true&amp;allowPublicKeyRetrieval=true&amp;connectTimeout=30000&amp;socketTimeout=60000&amp;tcpKeepAlive=true&amp;zeroDateTimeBehavior=convertToNull"/>
    <property name="username" value="admin"/>
    <property name="password" value="[YOUR_PASSWORD]"/>
    <!-- DB 연결 실패해도 Context 기동 허용 (스케줄러 오류 방지) -->
    <property name="initializationFailTimeout" value="-1"/>
    <property name="connectionTimeout" value="30000"/>
    <property name="maximumPoolSize" value="10"/>
    <property name="minimumIdle" value="2"/>
</bean>
```

---

## 3. 스크린샷 분석 (140615 ~ 143227)

### 스크린샷 140615 — AWS RDS Security Group 인바운드 규칙 추가

**사용자가 추가한 규칙:**

| 유형 | 프로토콜 | 포트 | 소스 | 비고 |
|------|---------|------|------|------|
| MySQL/Aurora | TCP | 3306 | `172.31.0.0/16` | ✅ VPC CIDR 전체 허용 (권장) |
| MySQL/Aurora | TCP | 3306 | `172.31.75.91/32` | ✅ 특정 IP 허용 |

**분석**: `172.31.0.0/16`은 EC2가 속한 VPC CIDR이므로 EC2(172.31.36.228)에서 RDS(10.0.1.109)로의 연결이 이 규칙으로 허용됨. ✅

> ⚠️ **주의**: RDS의 사설 IP가 `10.0.1.x`임. EC2 VPC CIDR(`172.31.0.0/16`)에서 온 트래픽을 RDS SG가 수락하려면 RDS가 EC2와 같은 VPC에 있어야 함. 스크린샷 141526에서 동일 VPC(`vpc-09bafc55952dfe978`) 확인됨 ✅.

### 스크린샷 140654 — EC2 보안 그룹 상세

```
보안 그룹: sg-0e220fb5d74b409e4 (ec2-rds-2)
```

**이 SG는 EC2에 연결된 SG임.** RDS SG의 소스를 이 SG ID로 지정하는 방법도 가능:
```
RDS SG 인바운드 → MySQL/Aurora 3306 → sg-0e220fb5d74b409e4 (EC2 SG ID)
```
이 방법이 IP 범위보다 더 정확한 EC2 단위 허용.

### 스크린샷 141526 / 141546 — VPC 정보

```
VPC ID: vpc-09bafc55952dfe978
IPv4 CIDR: 172.31.0.0/16
서브넷: ap-northeast-2a, 2b, 2c
```

EC2와 RDS 모두 이 VPC 내에 있음 → SG 규칙만 올바르면 연결 가능.

### 스크린샷 142123 / 142147 — RDS Security Group 목록 및 상세

RDS에 연결된 Security Group 상세 확인됨.  
추가된 인바운드 규칙 (`172.31.0.0/16`) 반영 확인 ✅

### 스크린샷 142730 — EC2 `/news/list` DB 오류

```
DB 오류 발생합니다: HikariPool-1 - Connection is not available, request timed out after 30000ms
+ 뉴스를 불러오는데 실패했습니다: HTTP 500
```

**원인**: Security Group은 업데이트되었으나 **EC2 Tomcat이 재시작되지 않아** 이전 연결 실패 상태 유지.  
**해결**: Tomcat 재시작 필요 (아래 4절 참조).

### 스크린샷 143015 — EC2 `/stock/list` 0개 기업

```
총 0개 기업
```

**원인**: DB 연결 성공 후 크롤러를 한 번도 실행하지 않아 STOCK 테이블 데이터 없음.  
**해결**: DB 연결 복구 후 크롤러 실행.

### 스크린샷 143227 — EC2 `/member/signup` 테스트

회원가입 폼이 정상 렌더링됨 ✅  
테스트 데이터 입력 확인 — DB 연결이 없으면 회원가입 INSERT 실패.

---

## 4. EC2 즉시 처리 목록 (PuTTy 명령어)

### Step 1 — Security Group 반영 확인 및 Tomcat 재시작

```bash
# 1. nc 테스트 (SG 반영 확인)
nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306
# 기대값: Connection to ... 3306 port [tcp/mysql] succeeded!

# 2. Tomcat 재시작
sudo systemctl restart tomcat
sleep 20

# 3. DB 연결 확인
curl http://localhost:8080/debug/db-check
# 기대값: {"dbConnected":true,"success":true}
```

### Step 2 — 데이터 채우기 (DB 연결 성공 후)

```bash
# 주식 데이터 크롤링 (한국 + 미국)
curl -X POST http://localhost:8080/crawler/korea
sleep 10
curl -X POST http://localhost:8080/crawler/us

# 뉴스 데이터 크롤링
curl -X POST http://localhost:8080/news/refresh

# 주식 가격 업데이트
curl -X POST http://localhost:8080/crawler/price
```

### Step 3 — 기능 확인

```bash
# 외부 접속 테스트
curl -I http://54.180.142.111:8080/
curl -I http://54.180.142.111:8080/member/login
curl -I http://54.180.142.111:8080/stock/list
curl -I http://54.180.142.111:8080/news/list
```

---

## 5. 로컬 STS 처리 목록

### ClassNotFoundException 완전 해결 절차

```
[매번 Maven clean/install 후 반드시 수행]

1. STS 메뉴 → Project → Clean... → Clean all projects → [OK]
2. Console 탭에서 빌드 완료 대기
3. Servers 탭 → Tomcat → 우클릭 → Clean
4. Servers 탭 → Tomcat → 우클릭 → Publish
5. Servers 탭 → Tomcat → 우클릭 → Start

확인: Console에
  "Root WebApplicationContext: initialization completed in ????ms" 출력
  + "ClassNotFoundException" 없음
```

### 로컬 RDS 연결 (UnknownHostException 해소)

**방법 A — AWS Console에서 RDS 퍼블릭 액세스 허용**

```
AWS Console → RDS → 데이터베이스 → portwatch-db-seoul
→ [수정] → "연결" 섹션 → "퍼블릭 액세스 가능" → 체크
→ [계속] → "즉시 적용" → [수정 저장]

RDS SG 인바운드 추가:
→ MySQL/Aurora, 3306, 소스: 내 IP (현재 공인 IP)
```

**방법 B — SSH 터널 (PuTTy)**

```
PuTTy → Connection → SSH → Tunnels
  Source port: 3307
  Destination: portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306
  → [Add] → Open (EC2에 SSH 연결)

root-context.xml jdbcUrl 임시 변경:
  jdbc:mysql://localhost:3307/portwatch?sslMode=PREFERRED&amp;...
```

---

## 6. 테스트 계정 SQL (DB 연결 후 실행)

```sql
-- 관리자 계정 삽입 (이미 있으면 IGNORE)
INSERT IGNORE INTO MEMBER (
    email, password, name, phone, role, created_at, is_active
) VALUES (
    'admin@portwatch.com',
    '$2a$10$baT2uBDFEOC.3UPGnHN.Cu8qUifq2UgJ7HbS7FYCtenGetdcmmqd6',
    '관리자',
    '010-0000-0000',
    'ADMIN',
    NOW(),
    1
);

-- 테스트 일반 계정
INSERT IGNORE INTO MEMBER (
    email, password, name, phone, role, created_at, is_active
) VALUES (
    'test@portwatch.com',
    '$2a$10$baT2uBDFEOC.3UPGnHN.Cu8qUifq2UgJ7HbS7FYCtenGetdcmmqd6',
    '테스트유저',
    '010-1234-5678',
    'USER',
    NOW(),
    1
);

-- 확인
SELECT member_id, email, name, role, is_active FROM MEMBER;
```

**비밀번호 (공통)**: `Admin2026!`

---

## 7. 오류별 원인-해결 요약 (이번 세션)

| # | 오류 | 원인 | 해결 | 상태 |
|---|------|------|------|------|
| 1 | ClassNotFoundException: MemberVO (재발) | Maven clean 후 STS Project Clean 미수행 → WTP 클래스 없음 | STS Project Clean → Publish → Tomcat Start | ❌ 미완료 |
| 2 | EC2 DB Connection timed out | Security Group 업데이트 후 Tomcat 재시작 안 함 | `sudo systemctl restart tomcat` | ❌ 미완료 |
| 3 | /news/list HTTP 500 (AJAX) | DB 연결 실패 연쇄 반응 | DB 연결 복구 후 자동 해소 | ❌ 미완료 |
| 4 | /stock/list 0개 기업 | 크롤러 미실행 (STOCK 테이블 빔) | DB 연결 후 크롤러 실행 | ❌ 미완료 |
| 5 | 로컬 UnknownHostException | RDS Private VPC, 외부 DNS 불가 | RDS Publicly Accessible 또는 SSH 터널 | ⚠️ 진행 중 |
| 6 | RDS SG 차단 (iptables 오인) | AWS Security Group 인바운드 규칙 없음 | 172.31.0.0/16 CIDR 규칙 추가 | ✅ 규칙 추가됨 (Tomcat 재시작 필요) |

---

## 8. 전체 수정 이력 (누적 — 전 세션 포함)

| 세션 | 수정 파일/대상 | 수정 내용 | 상태 |
|------|-------------|---------|------|
| 이전 | `pom.xml` | MySQL Connector/J 8.0.33 → 9.1.0 | ✅ 완료 |
| 이전 | `root-context.xml` | `<property name="sslMode">` 태그 제거 | ✅ 완료 |
| 이전 | `root-context.xml` | sslMode=DISABLED → PREFERRED | ✅ 완료 |
| 이전 | `root-context.xml` | connectTimeout 10000 → 30000 | ✅ 완료 |
| 이전 | `.launch` | `-Djava.net.preferIPv4Stack=true` 추가 | ✅ 완료 |
| 이전 | EC2 `tomcat.service` | JAVA_OPTS 버그 3개 수정 | ✅ 완료 |
| 13:00 | `root-context.xml` | `useSSL=false` 제거 (Connector/J 9.1.0 호환) | ✅ 완료 |
| 13:39 | EC2 PuTTy | iptables ACCEPT 규칙 추가 (포트 3306) | ✅ 완료 (효과 없었음—SG가 실제 원인) |
| 13:39 | EC2 PuTTy | 새 ROOT.war 배포 | ✅ 완료 |
| 14:06 | AWS Console | RDS SG 인바운드: 172.31.0.0/16 추가 | ✅ 완료 |
| 14:32 | 로컬 STS | STS Project Clean 절차 확인 | ❌ 사용자 미수행 |

---

## 9. 다음 세션 체크리스트

### EC2 (PuTTy) — 최우선

```
[ ] sudo systemctl restart tomcat
[ ] sleep 20
[ ] curl http://localhost:8080/debug/db-check  → {"dbConnected":true} 확인
[ ] curl -X POST http://localhost:8080/crawler/korea
[ ] curl -X POST http://localhost:8080/crawler/us
[ ] curl -X POST http://localhost:8080/news/refresh
[ ] http://54.180.142.111:8080/stock/list → 기업 목록 표시 확인
[ ] http://54.180.142.111:8080/news/list  → 뉴스 목록 표시 확인
[ ] http://54.180.142.111:8080/member/login → admin@portwatch.com / Admin2026! 로그인 테스트
```

### 로컬 STS — 최우선

```
[ ] STS → Project → Clean → Clean all projects
[ ] Servers 탭 → Tomcat → Clean → Publish → Start
[ ] Console: "initialization completed" 확인
[ ] Console: "ClassNotFoundException" 없음 확인
[ ] http://localhost:8080/member/login 접속 테스트
```

---

*PortWatch v1.0.0 | Spring 5.0.7 | MySQL Connector/J 9.1.0 | HikariCP 3.4.5 | MyBatis 3.5.6 | Tomcat 9.0.98*
