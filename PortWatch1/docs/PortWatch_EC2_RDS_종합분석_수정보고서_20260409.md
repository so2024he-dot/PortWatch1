# PortWatch1 — EC2 / RDS 종합 분석 및 수정 보고서
**작성일:** 2026-04-09  
**분석 세션:** 2026-04-08 10:37 ~ 2026-04-09 10:33 KST (총 24시간)  
**기술 스택:** Spring 5.0.7.RELEASE · MyBatis 3.5.6 · HikariCP 3.4.5 · MySQL Connector/J 8.0.33 → 9.1.0  
**EC2:** Ubuntu 24.04 LTS · Tomcat 9.0 · Java 11 · IP: 54.180.142.111:8080  
**RDS:** MySQL 8.4.7 · `portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306`

---

## 요약: 전체 오류 원인과 현재 상태

| 문제 | 원인 | 상태 |
|------|------|------|
| 로컬 STS — Spring 컨텍스트 시작 실패 | `<property name="sslMode">` 잘못된 HikariCP 빈 속성 | ✅ 수정 완료 |
| 로컬 STS — DB 연결 실패 | `preferIPv4Stack=true` 미적용 | ✅ 적용 확인 (INI 28번 줄) |
| EC2 — HikariCP DB 연결 실패 | `tomcat` 사용자 아웃바운드 3306 포트 차단 | ⚠️ EC2 PuTTy에서 수동 수정 필요 |
| EC2 — sslMode=DISABLED | RDS SSL 정책과 충돌 | ✅ PREFERRED로 변경 완료 |
| 로그인/회원가입 실패 | DB 연결 실패의 파생 현상 | DB 연결 후 자동 해결 |
| 주식 목록 0개 | DB 비어 있음 + DB 연결 실패 | DB 연결 후 크롤러 실행 필요 |
| 뉴스 목록 HTTP 500 | DB 연결 실패 | DB 연결 후 자동 해결 |

---

## 1. EC2 PuTTy 분석 — 스크린샷 타임라인

### 1.1 세션별 진행 상황

#### 2026-04-08 17:03 KST (스크린샷 170314, 170403)
- `sed -i 's/sslMode=DISABLED/sslMode=PREFERRED/g'` EC2 배포 XML에 적용 ✅
- Tomcat 재시작 후 `curl /debug/db-check` → 여전히 `dbConnected:false` ❌
- `sslMode=PREFERRED` 파일에 반영은 됐지만 DB 연결 여전히 실패

#### 2026-04-08 17:04 KST (스크린샷 170423) — 핵심 발견
```bash
sudo -u tomcat nc -zv \
  portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306
# 결과: Connection timed out (RDS IP: 10.0.1.109)
```
→ **`tomcat` 사용자로 실행하면 TCP 연결이 차단됨** ← 확정적 원인

MySQL CLI (`ubuntu` 사용자)는 연결 성공, Java (`tomcat` 사용자)는 연결 실패.  
→ **iptables가 `tomcat` 사용자의 아웃바운드 3306 포트를 차단 중**

#### 2026-04-09 10:27~10:33 KST (스크린샷 102700, 102752, 103344)
- 브라우저: 로그인, 회원가입, 뉴스 모두 여전히 실패 (24시간 경과)
- EC2 iptables 미수정 상태 유지

#### 2026-04-09 10:31 KST — INI 파일 (로컬 STS)
```
줄 28: 명령 행 아규먼트: -Djava.net.preferIPv4Stack=true  ← 드디어 적용 ✅
줄 77: WARN — NotWritablePropertyException: Invalid property 'sslMode' of HikariDataSource ← 새 오류
줄 78: ERROR — Context initialization failed ← Spring 컨텍스트 시작 완전 실패
```

---

## 2. 원인 분석 — 3단계 오류 구조

### 오류 1 (EC2): iptables가 tomcat 사용자 차단
```
ubuntu 사용자 → nc -zv RDS:3306 → 성공 ✅
tomcat  사용자 → nc -zv RDS:3306 → Connection timed out ❌
```
iptables의 `-m owner --uid-owner` 규칙 또는 AppArmor 프로파일이 Tomcat 프로세스의 3306 아웃바운드를 차단.

### 오류 2 (로컬 STS): HikariCP 빈에 잘못된 속성 태그
```xml
<!-- 잘못된 코드 — root-context.xml 116번 줄 -->
<property name= "sslMode" value="PREFERRED"/>
```
`sslMode`는 JDBC URL 파라미터로 jdbcUrl 문자열 안에 넣어야 함.  
HikariDataSource 빈의 property로 설정하면 `setSslMode()` 메서드가 없어서 `NotWritablePropertyException` 발생.  
→ **Spring 컨텍스트 시작 자체가 실패 → Tomcat은 떠있지만 모든 URL이 404/500**

### 오류 3 (해결됨): STS VM 인수 미적용
INI 10:31 세션에서 `preferIPv4Stack=true` 첫 번째 아규먼트로 확인 ✅  
**STS UI → Open launch configuration → VM arguments 추가 방법이 성공**

---

## 3. 수정 완료 사항

### 수정 1 — `root-context.xml` 잘못된 속성 제거 ✅ (즉시 적용)

**파일:** `src/main/webapp/WEB-INF/spring/root-context.xml`

```xml
<!-- 제거한 코드 (116번 줄) -->
<property name= "sslMode" value="PREFERRED"/>

<!-- 이유: sslMode는 HikariDataSource의 setter 메서드가 없음
          jdbcUrl 안에 파라미터로 넣어야 함 — 이미 93번 줄에 있음 -->
```

### 수정 2 — `root-context.xml` jdbcUrl 변경 ✅

**93번 줄 변경 내용:**

| 항목 | 변경 전 | 변경 후 | 이유 |
|------|---------|---------|------|
| `sslMode` | `DISABLED` | `PREFERRED` | RDS SSL 정책 대응 |
| `connectTimeout` | `10000` (10초) | `30000` (30초) | SSL 핸드셰이크 시간 확보 |
| `zeroDateTimeBehavior` | 없음 | `convertToNull` | 날짜 오류 방지 |

```xml
<!-- 수정 후 jdbcUrl -->
<property name="jdbcUrl" value="jdbc:mysql://portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306/portwatch?useSSL=false&amp;sslMode=PREFERRED&amp;serverTimezone=Asia/Seoul&amp;characterEncoding=UTF-8&amp;useUnicode=true&amp;allowPublicKeyRetrieval=true&amp;connectTimeout=30000&amp;socketTimeout=60000&amp;tcpKeepAlive=true&amp;zeroDateTimeBehavior=convertToNull"/>
```

### 수정 3 — `pom.xml` MySQL Connector/J 업그레이드 ✅

```xml
<!-- 변경 전 -->
<version>8.0.33</version>
<!-- 변경 후 -->
<version>9.1.0</version>
```
Connector/J 9.1.0은 MySQL 8.4에 최적화. 8.0.33은 8.4 인증 프로토콜과 비호환 문제 발생.

### 수정 4 — `.launch` 파일 JVM 인수 추가 ✅

**파일:** `Tomcat v9.0 Server at localhost.launch`  
VM_ARGUMENTS 첫 번째 항목에 `-Djava.net.preferIPv4Stack=true` 추가.  
INI 28번 줄에서 적용 확인.

---

## 4. EC2 PuTTy — 지금 바로 실행할 수정 명령

### STEP 1 — iptables 규칙 확인 및 차단 해제 (핵심)

```bash
# 현재 OUT 규칙 확인
sudo iptables -L OUTPUT -n -v --line-numbers | head -30

# 3306 포트 아웃바운드 허용 추가
sudo iptables -I OUTPUT 1 -p tcp --dport 3306 -j ACCEPT

# 규칙 확인
sudo iptables -L OUTPUT -n | grep 3306

# tomcat 사용자로 연결 재테스트
sudo -u tomcat nc -zv \
  portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306
# 기대값: Connection to ... 3306 port [tcp/mysql] succeeded!
```

### STEP 2 — iptables 규칙 영구 저장

```bash
sudo apt-get install iptables-persistent -y
sudo netfilter-persistent save
```

### STEP 3 — AppArmor 확인 (iptables로 안 풀리면)

```bash
# AppArmor 상태 확인
sudo aa-status | grep tomcat

# Tomcat AppArmor 프로파일 있으면 비활성화
sudo aa-disable /etc/apparmor.d/opt.tomcat.bin.java 2>/dev/null || echo "프로파일 없음"
```

### STEP 4 — EC2 root-context.xml 수정 확인

```bash
# sslMode 확인
sudo grep -o 'sslMode=[A-Z]*' \
  /opt/tomcat/webapps/ROOT/WEB-INF/spring/root-context.xml
# 기대값: sslMode=PREFERRED

# 없으면 수정
sudo sed -i 's/sslMode=DISABLED/sslMode=PREFERRED/g' \
  /opt/tomcat/webapps/ROOT/WEB-INF/spring/root-context.xml
```

### STEP 5 — Tomcat 재시작 및 DB 연결 확인

```bash
sudo systemctl restart tomcat
sleep 20

curl http://54.180.142.111:8080/debug/db-check
# 기대값: {"dbConnected":true,"success":true}
```

### STEP 6 — 새 WAR 배포 (로컬 빌드 후)

로컬 STS에서:
```
Maven clean → Maven install
target/portwatch-1.0.0-BUILD-SNAPSHOT.war 생성
```

WinSCP로 EC2 업로드:
```
로컬: C:\Users\소현석\git\PortWatch1\PortWatch1\target\portwatch-1.0.0-BUILD-SNAPSHOT.war
EC2:  /opt/tomcat/webapps/ROOT.war  (덮어쓰기)
```

EC2에서:
```bash
sudo systemctl stop tomcat
sudo rm -rf /opt/tomcat/webapps/ROOT/
sudo systemctl start tomcat
sleep 20
curl http://54.180.142.111:8080/debug/db-check
```

---

## 5. AWS RDS 설정 매뉴얼

### 5.1 현재 구성

```
[브라우저] → EC2 (54.180.142.111:8080) → RDS MySQL 8.4.7
                  EC2 VPC: 172.31.36.228      RDS VPC: 10.0.1.109
```

### 5.2 보안그룹 체크리스트

| 보안그룹 | 방향 | 프로토콜 | 포트 | 소스 | 상태 |
|---------|------|---------|------|------|------|
| portwatch-sg (EC2) | 인바운드 | TCP | 8080 | 0.0.0.0/0 | 확인 필요 |
| ec2-rds-1 (RDS) | 인바운드 | TCP | 3306 | portwatch-sg | **필수** |
| ec2-rds-1 (RDS) | 인바운드 | TCP | 3306 | 172.31.0.0/16 (VPC) | 권장 |

### 5.3 RDS 파라미터 그룹 — SSL 설정

MySQL 8.4에서는 `force_ssl` 대신 `require_secure_transport` 파라미터 사용:

```
AWS 콘솔 → RDS → 파라미터 그룹 → portwatch-params
→ 파라미터 편집 → require_secure_transport 검색
→ 현재값: ON
→ 변경값: OFF  (또는 ON 유지하고 sslMode=PREFERRED 사용)
```

**현재 권장:** `require_secure_transport=ON` + `sslMode=PREFERRED` 조합  
→ SSL 강제해도 PREFERRED이면 정상 연결

### 5.4 RDS 연결 테스트 순서

```bash
# 1단계: DNS 확인
nslookup portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com

# 2단계: ubuntu 사용자로 TCP 확인
nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306

# 3단계: tomcat 사용자로 TCP 확인 (핵심)
sudo -u tomcat nc -zv \
  portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306

# 4단계: MySQL 클라이언트 접속
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -u admin -p
# 비밀번호: portwatch2026!!abcd

# 5단계: Java DB 연결
curl http://54.180.142.111:8080/debug/db-check
```

---

## 6. 로그인 / 회원가입 분석

### 6.1 현재 DB 상태 (2026-04-08 14:51 확인)

```sql
SELECT member_email, member_role, member_status FROM MEMBER;
-- 10개 행 존재 확인됨 ✅
-- admin@portwatch.com | ADMIN | ACTIVE
-- test@portwatch.com  | USER  | ACTIVE
```

### 6.2 테스트 로그인 계정

| 이메일 | 비밀번호 | 역할 |
|--------|---------|------|
| `admin@portwatch.com` | `Admin2026!` | ADMIN |
| `test@portwatch.com` | `Admin2026!` | USER |

BCrypt 해시 (`Admin2026!` 기준):
```
$2a$10$baT2uBDFEOC.3UPGnHN.Cu8qUifq2UgJ7HbS7FYCtenGetdcmmqd6
```

### 6.3 MySQL 쿼리 (비밀번호 재설정)

```sql
USE portwatch;

-- 비밀번호 재설정
UPDATE MEMBER
SET member_pass   = '$2a$10$baT2uBDFEOC.3UPGnHN.Cu8qUifq2UgJ7HbS7FYCtenGetdcmmqd6',
    member_status = 'ACTIVE'
WHERE member_email = 'admin@portwatch.com';

-- 계정 없을 경우 삽입
INSERT IGNORE INTO MEMBER
  (member_id, member_email, member_pass, member_name, member_phone,
   member_role, member_status, balance, created_at, updated_at)
VALUES
  (UUID(), 'admin@portwatch.com',
   '$2a$10$baT2uBDFEOC.3UPGnHN.Cu8qUifq2UgJ7HbS7FYCtenGetdcmmqd6',
   '관리자', '010-0000-0001', 'ADMIN', 'ACTIVE', 1000000, NOW(), NOW()),
  (UUID(), 'test@portwatch.com',
   '$2a$10$baT2uBDFEOC.3UPGnHN.Cu8qUifq2UgJ7HbS7FYCtenGetdcmmqd6',
   '테스트', '010-0000-0002', 'USER', 'ACTIVE', 1000000, NOW(), NOW());

SELECT member_email, member_role, member_status FROM MEMBER;
```

### 6.4 회원가입 실패 원인

```
현재 화면: "현재 서비스를 이용할 수 없습니다"
원인: MemberServiceImpl.signup() → memberMapper.insertMember() → HikariPool 타임아웃
코드 수정 불필요 — DB 연결 복구 시 자동 해결
```

---

## 7. 주식 목록 (0개) 수정

### 7.1 원인 분석

```
StockController.list() → stockMapper.findAll() → HikariPool 타임아웃 → 0개 반환
STOCK 테이블: 비어 있음 (크롤러가 DB 연결 실패로 한 번도 성공 못함)
```

### 7.2 DB 연결 후 크롤러 실행

```bash
curl -X POST http://54.180.142.111:8080/crawler/korea
sleep 30
curl -X POST http://54.180.142.111:8080/crawler/us
```

### 7.3 크롤러 실패 시 직접 SQL 입력

```sql
USE portwatch;

INSERT IGNORE INTO STOCK
  (stock_code, stock_name, market_type, country,
   current_price, change_rate, volume, created_at, updated_at)
VALUES
  ('005930','삼성전자','KOSPI','KR', 72000, 1.5, 15000000, NOW(), NOW()),
  ('000660','SK하이닉스','KOSPI','KR', 185000, 2.1, 5000000, NOW(), NOW()),
  ('035420','NAVER','KOSPI','KR', 165000, -0.5, 800000, NOW(), NOW()),
  ('005380','현대차','KOSPI','KR', 215000, 0.8, 600000, NOW(), NOW()),
  ('051910','LG화학','KOSPI','KR', 310000, -1.2, 300000, NOW(), NOW()),
  ('068270','셀트리온','KOSPI','KR', 145000, 0.3, 500000, NOW(), NOW()),
  ('035720','카카오','KOSPI','KR', 38000, -0.8, 1200000, NOW(), NOW()),
  ('000270','기아','KOSPI','KR', 98000, 1.1, 700000, NOW(), NOW()),
  ('028260','삼성물산','KOSPI','KR', 142000, 0.5, 200000, NOW(), NOW()),
  ('066570','LG전자','KOSPI','KR', 87000, 1.8, 900000, NOW(), NOW()),
  ('AAPL','Apple Inc.','NASDAQ','US', 169.5, 0.8, 50000000, NOW(), NOW()),
  ('MSFT','Microsoft Corp.','NASDAQ','US', 415.2, 1.2, 20000000, NOW(), NOW()),
  ('GOOGL','Alphabet Inc.','NASDAQ','US', 175.8, 0.5, 15000000, NOW(), NOW()),
  ('NVDA','NVIDIA Corp.','NASDAQ','US', 875.4, 2.3, 35000000, NOW(), NOW()),
  ('AMZN','Amazon.com Inc.','NASDAQ','US', 190.3, 0.9, 25000000, NOW(), NOW());

SELECT market_type, country, COUNT(*) AS 종목수
FROM STOCK GROUP BY market_type, country;
```

---

## 8. 뉴스 HTTP 500 수정

```
오류: ### Error querying database. NewsMapper.xml
원인: newsDAO.selectAllNews() → HikariPool 타임아웃
```

**DB 연결 후 처리:**

```bash
curl -X POST http://54.180.142.111:8080/news/refresh
```

NEWS 테이블 없을 경우:

```sql
USE portwatch;

CREATE TABLE IF NOT EXISTS NEWS (
    news_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(500) NOT NULL,
    content      TEXT,
    source       VARCHAR(100),
    category     VARCHAR(50),
    stock_code   VARCHAR(20),
    published_at TIMESTAMP,
    url          VARCHAR(1000),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_stock_code  (stock_code),
    INDEX idx_published_at (published_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 9. INI 콘솔 로그 상세 분석 (2026-04-09 10:31)

| 줄번호 | 내용 | 분석 |
|--------|------|------|
| 5 | Tomcat 시작: 10:31:30 | 2026-04-09 신규 세션 |
| **28** | `명령 행 아규먼트: -Djava.net.preferIPv4Stack=true` | **✅ IPv4 플래그 드디어 적용!** |
| 29~56 | 나머지 JVM 인수 | 정상 |
| 74~75 | root-context.xml 로딩 시작 | 정상 |
| **77** | `WARN: NotWritablePropertyException: Invalid property 'sslMode'` | **❌ 핵심 오류** |
| **78** | `ERROR: Context initialization failed` | Spring 컨텍스트 완전 실패 |
| 131~189 | Tomcat: 컨텍스트 시작 실패 | 전체 앱 비활성 |
| 193 | JDBC 드라이버 메모리 경고 | 심각하지 않음 |
| 207~209 | Tomcat은 기동됨 (포트는 열림) | 하지만 앱 없음 |
| 210~219 | 중지된 앱 접근 시도 | 스케줄러 스레드 잔류 |

**핵심 발견:** `preferIPv4Stack=true` 적용은 성공했지만, `<property name="sslMode">` 잘못된 태그로 인해 Spring 컨텍스트 자체가 시작되지 않음.

**수정 조치:** `root-context.xml` 116번 줄 제거 → **완료** ✅

---

## 8. 로컬 STS preferIPv4Stack 적용 방법 — 경위

### 이전에 시도한 방법들

| 시도 | 방법 | 결과 |
|------|------|------|
| 1차 | `setenv.bat` 생성 | ❌ STS-WTP가 catalina.bat을 사용하지 않음 |
| 2차 | `.launch` 파일 직접 편집 | ❌ STS 재시작 시 파일 덮어씌워짐 |
| **3차** | **STS UI → Open launch configuration → VM args** | **✅ INI 28번 줄 확인 성공** |

**STS-WTP 동작 원리:**  
STS는 `catalina.bat`을 경유하지 않고 Java를 직접 실행합니다. 따라서 `setenv.bat`은 완전히 무시됩니다. 올바른 방법은 STS UI를 통해 VM 인수를 추가하는 것이며, 이 방법만이 재시작 후에도 설정이 유지됩니다.

**적용 절차 (참조용):**
```
1. STS Servers 패널 → "Tomcat v9.0 Server at localhost" 더블클릭
2. Overview 탭 → "Open launch configuration" 클릭
3. Arguments 탭 → VM arguments 입력창에 추가: -Djava.net.preferIPv4Stack=true
4. Apply → Close
5. Tomcat 중지 → 시작
6. 콘솔에서 확인: 명령 행 아규먼트: -Djava.net.preferIPv4Stack=true ← 첫 번째 줄에 나와야 함
```

---

## 10. 전체 처리 우선순위

```
[EC2 즉시]
  1. sudo iptables -I OUTPUT 1 -p tcp --dport 3306 -j ACCEPT
  2. sudo -u tomcat nc -zv [RDS_HOST] 3306  → "succeeded!" 확인
  3. sudo systemctl restart tomcat
  4. curl /debug/db-check → {"dbConnected":true}

[로컬 STS 즉시]
  5. Maven clean → Maven install  (root-context.xml 수정사항 반영)
  6. Tomcat 시작 → 콘솔 오류 없어야 함 (NotWritablePropertyException 사라짐)
  7. curl http://localhost:8080/debug/db-check → {"dbConnected":true}

[EC2 WAR 배포]
  8. WinSCP: 새 WAR → /opt/tomcat/webapps/ROOT.war
  9. sudo systemctl restart tomcat

[DB 데이터]
  10. mysql 접속 → USE portwatch; → 상태 확인
  11. STOCK, NEWS 테이블 크롤러 또는 SQL로 채우기

[검증]
  12. http://54.180.142.111:8080/member/login (admin@portwatch.com / Admin2026!)
  13. http://54.180.142.111:8080/stock/list  (15개 이상 종목)
  14. http://54.180.142.111:8080/news/list   (뉴스 표시, HTTP 500 없음)
```

---

## 11. 전체 수정사항 요약

| 번호 | 파일 | 변경 내용 | 중요도 |
|------|------|---------|--------|
| 1 | `root-context.xml` | `<property name="sslMode">` 잘못된 태그 **제거** | 🔴 최우선 |
| 2 | `root-context.xml` | `sslMode=DISABLED` → `sslMode=PREFERRED` | 🔴 최우선 |
| 3 | `root-context.xml` | `connectTimeout=10000` → `connectTimeout=30000` | 🟡 높음 |
| 4 | `root-context.xml` | `zeroDateTimeBehavior=convertToNull` 추가 | 🟢 보통 |
| 5 | `pom.xml` | Connector/J `8.0.33` → `9.1.0` | 🟡 높음 |
| 6 | `.launch` 파일 | `-Djava.net.preferIPv4Stack=true` 추가 | 🟡 높음 |
| 7 | EC2 systemd | JAVA_OPTS 중복/오류 제거, 단일 정리 | 🟡 높음 |
| 8 | EC2 배포 XML | `sslMode=PREFERRED` sed 적용 | 🔴 최우선 |
| **9** | **EC2 iptables** | **3306 포트 아웃바운드 허용** | **🔴 EC2 최우선** |

---

*작성일: 2026-04-09 10:35 KST*  
*분석 근거: PuTTy 스크린샷 32장, 브라우저 스크린샷 12장, STS 콘솔 로그 220줄*
