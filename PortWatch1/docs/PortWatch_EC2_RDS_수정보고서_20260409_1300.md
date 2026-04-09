# PortWatch EC2/RDS 종합 분석 수정 보고서
**작성일**: 2026-04-09 (13:00 세션)  
**분석 파일**: `202604091300 AWS EC2 RDS Aurora SQL output.zip` (스크린샷 5장) + `오류코드 수정할 부분 및 생성코드.ini`

---

## 1. INI 파일 분석 — 로컬 STS 콘솔 로그

### 1-1. 성공 항목 (이전 세션 수정 결과 확인)

| 항목 | 결과 | 상세 |
|------|------|------|
| Maven clean | ✅ 성공 | 0.424s 완료 |
| Maven install | ✅ 성공 | `portwatch-1.0.0-BUILD-SNAPSHOT.war` 생성 |
| `-Djava.net.preferIPv4Stack=true` | ✅ 적용됨 | 콘솔 줄 83: 명령 행 아규먼트로 확인 |
| Spring Context 초기화 | ✅ 성공 | "initialization completed in 3283 ms" |
| `NotWritablePropertyException` | ✅ 사라짐 | 이전 세션에서 `<property name="sslMode">` 제거 효과 |
| 모든 컨트롤러 매핑 | ✅ 성공 | 총 84개 소스파일 컴파일, 전 엔드포인트 정상 매핑 |

### 1-2. 신규 오류 — UnknownHostException (핵심 문제)

```
Caused by: java.net.UnknownHostException: 알려진 호스트가 없습니다
  (portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com)
  at java.base/java.net.Inet4AddressImpl.lookupAllHostAddr(Native Method)
```

**오류 발생 위치**: StockPriceScheduler, NewsScheduler, StockScheduler  
**오류 타이밍**: Tomcat 기동 후 스케줄러 첫 실행 시 (12:43 ~ 12:50)

#### 원인 분석

```
preferIPv4Stack=true 적용됨 → Inet4AddressImpl.lookupAllHostAddr 사용 (IPv4 DNS 조회) ✅
그러나 → RDS 엔드포인트 DNS 조회 자체 실패 (알려진 호스트 없음)
```

**핵심 원인**: AWS RDS가 **Private VPC Endpoint**로 설정되어 있음  
- AWS VPC 내부(EC2)에서만 DNS 조회 가능  
- 로컬 Windows PC (외부 인터넷)에서는 해당 도메인을 DNS로 해석 불가  
- 이는 정상적인 보안 동작 (Private RDS = 외부 접근 차단)

#### 발생 스케줄러별 오류

| 스케줄러 | 오류 시각 | 상세 |
|---------|---------|------|
| StockPriceScheduler | 12:43 | HikariPool-1 Connection timed out 30008ms |
| NewsScheduler (KR) | 12:44 | CannotCreateTransactionException |
| StockScheduler (KR) | 12:45 | CannotCreateTransactionException |
| StockScheduler (US) | 12:47 | UnknownHostException |
| NewsScheduler (US) | 12:50 | existsByUrl 조회 실패 |

---

## 2. EC2 스크린샷 분석

### 스크린샷 125018 — EC2 PuTTy 상태

```bash
sudo systemctl restart tomcat
# → Tomcat active (running) since Thu 2026-04-09 03:24:22 UTC ✅

curl http://localhost:8080/debug/db-check
# → {"dbConnected":false,"success":false,...}
#    원인: PersistenceException / HikariPool Connection timed out
```

**결론**: Tomcat 실행 중이나 DB 연결 계속 실패

### 스크린샷 125040 — iptables-persistent 설치

```bash
apt-get install iptables-persistent  # 설치 진행 중
```

**분석**: iptables 규칙 영구 저장용 패키지 설치 → **ACCEPT 규칙 추가가 선행**되어야 효과 있음

### 스크린샷 125104 — AWS RDS 현황

RDS MySQL 접속 확인 (ubuntu 사용자 기준):
```
ACTIVITY, DIVIDEND, MEMBER, NEWS, PORTFOLIO, STOCK, WATCHLIST 등 테이블 존재
```
**RDS 자체는 정상 작동 중** — ubuntu 사용자로는 MySQL CLI 접속 가능

### 스크린샷 125137 — EC2 404 오류

```
http://54.180.142.111:8080
HTTP 상태 404 — 찾을 수 없음
Apache Tomcat/9.0.98
```

**원인**: 새 WAR 파일이 EC2에 아직 배포되지 않음  
- 로컬에서 Maven 빌드는 성공했으나 EC2 전송이 안 됨
- EC2에 배포된 구 버전 WAR는 Spring Context 초기화 실패 → 모든 요청 404 응답

---

## 3. EC2 404 오류 원인 및 해결

### 원인 체계도

```
EC2 구 버전 WAR 배포 상태
  └─ root-context.xml: <property name="sslMode"> 태그 포함 (구 버전)
       └─ NotWritablePropertyException 발생
            └─ Spring Context 초기화 완전 실패
                 └─ 모든 URL → HTTP 404
                      ↑ 이것이 http://54.180.142.111:8080 404의 원인
```

### 해결 — 새 WAR EC2 배포 (WinSCP + PuTTy)

**Step 1: 로컬 STS Maven 리빌드**
```
1. STS → Run As → Maven clean
2. STS → Run As → Maven install
3. 빌드 성공 확인: target/portwatch-1.0.0-BUILD-SNAPSHOT.war
```

**Step 2: WinSCP로 EC2에 WAR 업로드**
```
로컬 파일: C:\Users\소현석\git\PortWatch1\PortWatch1\target\portwatch-1.0.0-BUILD-SNAPSHOT.war
EC2 업로드 경로: /opt/tomcat/webapps/ROOT.war  ← 파일명 반드시 ROOT.war
```

**Step 3: PuTTy에서 배포 적용**
```bash
# 기존 앱 삭제
sudo systemctl stop tomcat
sudo rm -rf /opt/tomcat/webapps/ROOT
sudo rm -f /opt/tomcat/webapps/ROOT.war

# WinSCP로 새 ROOT.war 업로드 후 ↓ 실행
sudo chown tomcat:tomcat /opt/tomcat/webapps/ROOT.war
sudo systemctl start tomcat
sleep 30

# 확인
curl http://localhost:8080/
# 기대값: PortWatch 홈페이지 HTML (HTTP 200)

curl http://localhost:8080/debug/db-check
# 기대값: {"dbConnected":true,"success":true}
```

---

## 4. EC2 DB 연결 오류 — iptables 차단 해제

### 원인 (이전 세션에서 확인)
```bash
sudo -u tomcat nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306
# 결과: Connection timed out  ← tomcat 사용자 아웃바운드 포트 3306 차단됨
# ubuntu 사용자 MySQL CLI는 정상 작동 → iptables owner 규칙이 tomcat 사용자만 차단
```

### 해결 — PuTTy 명령어 (순서 중요)

```bash
# 1. 현재 OUTPUT 체인 확인
sudo iptables -L OUTPUT -n --line-numbers

# 2. ACCEPT 규칙 추가 (최우선 순위로 1번에 삽입)
sudo iptables -I OUTPUT 1 -p tcp --dport 3306 -j ACCEPT

# 3. 규칙 추가 확인
sudo iptables -L OUTPUT -n --line-numbers | grep 3306

# 4. 영구 저장 (이미 iptables-persistent 설치됨)
sudo netfilter-persistent save

# 5. tomcat 사용자로 연결 재테스트
sudo -u tomcat nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306
# 기대값: Connection to ... 3306 port [tcp/mysql] succeeded!

# 6. Tomcat 재시작 후 DB 확인
sudo systemctl restart tomcat
sleep 20
curl http://localhost:8080/debug/db-check
# 기대값: {"dbConnected":true}
```

---

## 5. AWS RDS 설정 — 로컬 PC 접속 방법

### 현재 상태
- RDS: **Private Endpoint** (VPC 내부 전용)
- 로컬 PC: DNS 조회 자체 불가 → `UnknownHostException`
- EC2 (같은 VPC): DNS 조회 가능, 단 iptables 차단으로 TCP 연결 실패

### 방법 A — RDS Publicly Accessible 설정 (권장: 개발 편의)

**AWS Console 경로**:
```
RDS → 데이터베이스 → portwatch-db-seoul 선택
→ [수정] 버튼 클릭
→ "연결" 섹션 → "퍼블릭 액세스 가능성"
→ "퍼블릭 액세스 가능" 선택
→ [계속] → "즉시 적용" 선택 → [수정 저장]
```

**Security Group 추가 (RDS)**:
```
EC2 → 보안 그룹 → RDS에 연결된 SG 선택
→ 인바운드 규칙 편집 → 규칙 추가
→ 유형: MySQL/Aurora  포트: 3306
→ 소스: 내 IP (자동 감지) 또는 0.0.0.0/0 (개발용만)
→ 저장
```

**로컬 PC 연결 테스트**:
```bash
# Windows CMD에서
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com -P 3306 -u admin -p
```

### 방법 B — SSH 터널 (보안 우선)

PuTTy Connection Tunneling:
```
PuTTy → Connection → SSH → Tunnels
Source port: 3307
Destination: portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306
→ Add → Open
```

로컬 STS root-context.xml jdbcUrl 임시 변경:
```xml
<property name="jdbcUrl" value="jdbc:mysql://localhost:3307/portwatch?sslMode=PREFERRED&amp;..."/>
```

---

## 6. 코드 수정 내역

### 수정 1: root-context.xml — useSSL=false 제거 (이번 세션)

**파일**: `src/main/webapp/WEB-INF/spring/root-context.xml`

```xml
<!-- 수정 전 (MySQL Connector/J 9.1.0에서 useSSL 파라미터 제거됨) -->
<property name="jdbcUrl" value="jdbc:mysql://...?useSSL=false&amp;sslMode=PREFERRED&amp;..."/>

<!-- 수정 후 (9.1.0 호환, sslMode만 사용) -->
<property name="jdbcUrl" value="jdbc:mysql://...?sslMode=PREFERRED&amp;..."/>
```

**이유**: MySQL Connector/J 9.0부터 `useSSL` 파라미터 완전 제거, `sslMode`로 통합

### 수정 2: root-context.xml — 현재 최종 jdbcUrl

```xml
<property name="jdbcUrl" value="jdbc:mysql://portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306/portwatch?sslMode=PREFERRED&amp;serverTimezone=Asia/Seoul&amp;characterEncoding=UTF-8&amp;useUnicode=true&amp;allowPublicKeyRetrieval=true&amp;connectTimeout=30000&amp;socketTimeout=60000&amp;tcpKeepAlive=true&amp;zeroDateTimeBehavior=convertToNull"/>
```

---

## 7. 전체 수정 이력 (누적)

| 세션 | 수정 파일/대상 | 수정 내용 | 상태 |
|------|-------------|---------|------|
| 이전 | `pom.xml` | MySQL Connector/J 8.0.33 → 9.1.0 | ✅ 완료 |
| 이전 | `root-context.xml` | sslMode=DISABLED → PREFERRED | ✅ 완료 |
| 이전 | `root-context.xml` | `<property name="sslMode">` 태그 제거 | ✅ 완료 |
| 이전 | `root-context.xml` | connectTimeout 10000 → 30000 | ✅ 완료 |
| 이전 | `.launch` | `-Djava.net.preferIPv4Stack=true` 추가 | ✅ 완료 |
| 이전 | EC2 `tomcat.service` | JAVA_OPTS 버그 3개 수정 | ✅ 완료 |
| 이번 | `root-context.xml` | `useSSL=false` 제거 (9.1.0 호환) | ✅ 완료 |

---

## 8. 남은 작업 목록 (우선순위 순)

### EC2 (PuTTy)

```
[ ] 1. iptables ACCEPT 규칙 추가
       sudo iptables -I OUTPUT 1 -p tcp --dport 3306 -j ACCEPT
       sudo netfilter-persistent save
       
[ ] 2. tomcat 사용자 nc 테스트
       sudo -u tomcat nc -zv [RDS_HOST] 3306
       → succeeded! 확인
       
[ ] 3. 새 WAR 배포
       WinSCP: target/*.war → /opt/tomcat/webapps/ROOT.war
       
[ ] 4. Tomcat 재시작 + DB 확인
       curl http://localhost:8080/debug/db-check
       → {"dbConnected":true}
       
[ ] 5. 기능 확인
       curl http://54.180.142.111:8080/
       curl http://54.180.142.111:8080/member/login
       curl http://54.180.142.111:8080/stock/list
       curl http://54.180.142.111:8080/news/list
```

### 로컬 STS

```
[ ] 1. RDS Publicly Accessible 설정 (AWS Console)
       또는 SSH 터널 설정

[ ] 2. Maven clean → Maven install (useSSL=false 제거 적용)

[ ] 3. Tomcat 시작 → UnknownHostException 사라짐 확인

[ ] 4. http://localhost:8080/member/login 접속 → 로그인 테스트
       ID: admin@portwatch.com / PW: Admin2026!
```

### DB 데이터 (EC2 DB 연결 후)

```bash
# 주식 데이터 채우기
curl -X POST http://54.180.142.111:8080/crawler/korea
curl -X POST http://54.180.142.111:8080/crawler/us

# 뉴스 데이터 채우기
curl -X POST http://54.180.142.111:8080/news/refresh
```

---

## 9. 오류별 원인-해결 요약

| # | 오류 | 원인 | 해결 |
|---|------|------|------|
| 1 | EC2 HTTP 404 | 구 버전 WAR (NotWritablePropertyException) | 새 WAR 배포 |
| 2 | EC2 DB 연결 실패 | iptables → tomcat 사용자 포트 3306 차단 | iptables ACCEPT 규칙 추가 |
| 3 | 로컬 UnknownHostException | RDS Private VPC, 외부 DNS 조회 불가 | RDS Publicly Accessible 설정 |
| 4 | 스케줄러 오류 반복 | DB 연결 실패 연쇄 반응 | DB 연결 해결 시 자동 해소 |
| 5 | useSSL=false 경고 | Connector/J 9.1.0에서 파라미터 제거됨 | jdbcUrl에서 useSSL=false 제거 ✅ |

---

*PortWatch v1.0.0 | Spring 5.0.7 | MySQL Connector/J 9.1.0 | HikariCP 3.4.5 | MyBatis 3.5.6*
