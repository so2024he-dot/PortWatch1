# PortWatch 프로젝트 종합 분석 리포트
> 분석 기준일: 2026-04-01
> 분석 대상: 스크린샷 131306 · 134818 · 134854 · 134924 · 134954 + STS 콘솔 INI

---

## 1. 현재 전체 상태 요약

| 항목 | 상태 | 비고 |
|------|------|------|
| 로컬 STS Maven 빌드 | ✅ 정상 | BUILD SUCCESS, 84개 파일 컴파일 |
| 로컬 STS Tomcat 기동 | ✅ 정상 | 7613ms 기동, 에러 없음 |
| @Scheduled 스케줄러 | ✅ 정상 | taskScheduler Bean 초기화 확인됨 |
| EC2 HTTP 접근 (54.180.142.111:8080) | ❌ 404 | ROOT.war 삭제로 재발 |
| EC2 AWS RDS DB 연결 | ❌ 실패 | HikariPool 30000ms 타임아웃 |
| 로그인 / 회원가입 | ❌ 불가 | DB 연결 실패 cascade |
| 주식 목록 | ❌ 불가 | DB 연결 실패 cascade |
| 뉴스 목록 | ❌ 불가 | DB 연결 실패 cascade |
| 뉴스 크롤링 스케줄 | ✅ 코드 완료 | 한국 30분, 미국 1시간 주기 |
| 주식 크롤링 스케줄 | ✅ 코드 완료 | 한국 30분, 미국 1시간 주기 |

---

## 2. HTTP 404 원인 분석

### 2-1. 현상 (스크린샷 134954)
`http://54.180.142.111:8080` → **HTTP Status 404 – Not Found**

### 2-2. 근본 원인: ROOT.war 삭제 후 Tomcat autoDeploy 반응

```
배포 후 ROOT.war 삭제
      ↓
Tomcat autoDeploy 감지: "WAR가 사라짐 = 앱 제거 요청"
      ↓
ROOT/ 폴더 자동 삭제
      ↓
HTTP 404 전체 페이지
```

스크린샷 분석에서 여러 번의 배포 사이클이 보이며, 매번 같은 패턴으로 실패합니다.

### 2-3. 올바른 배포 순서

```bash
# ✅ 올바른 방법 (이 순서 정확히 준수)
sudo systemctl stop tomcat
sudo rm -rf /opt/tomcat/webapps/ROOT      # ROOT 폴더 삭제
sudo rm -f  /opt/tomcat/webapps/ROOT.war  # OLD WAR 삭제
sudo cp /home/ubuntu/portwatch.war /opt/tomcat/webapps/ROOT.war  # 새 WAR 복사
sudo systemctl start tomcat               # 기동

# ← 여기서 절대 ROOT.war를 삭제하지 마세요!
# Tomcat이 자동으로 ROOT.war → ROOT/ 를 관리합니다.
```

---

## 3. AWS RDS DB 연결 실패 원인 분석

### 3-1. 현상 (스크린샷 131306, 134924)
```
curl http://localhost:8080/debug/db-check 결과:
{
  "dbConnected": false,
  "success": false,
  "diagnosis": "HikariCP 풀 불러오기 실패",
  "error": "HikariPool-1 - Connection is not available,
            request timed out after 30000ms.
            com.portwatch.mapper.MemberMapper.findByEmail"
}
```

### 3-2. 확인된 사실

| 항목 | 내용 |
|------|------|
| MySQL 직접 접속 (PuTTy) | ✅ 성공 (스크린샷 134854 확인) |
| Java HikariCP 연결 | ❌ 30초 후 타임아웃 |
| root-context.xml connectTimeout | 10000ms (정상) |
| root-context.xml connectionTimeout | 30000ms (정상) |
| initializationFailTimeout | -1 (Tomcat 기동 허용) |

### 3-3. 근본 원인: Java IPv6 우선 DNS 해석

```
EC2 Ubuntu + Java → DNS 해석 시 IPv6 먼저 시도
                 ↓
AAAA 레코드 없음 → IPv6 실패 (5-10초 소요)
                 ↓
IPv4 재시도 → 성공 but connectTimeout 이미 소진
                 ↓
HikariCP 30초 대기 중 반복 실패 → "timed out after 30000ms"
```

**왜 mysql 명령은 되는가?**
- `mysql` CLI는 기본적으로 IPv4를 사용 (또는 OS가 IPv4 선택)
- Java JVM은 명시적 지정이 없으면 IPv6를 먼저 시도

### 3-4. 해결: Tomcat JVM에 IPv4 강제 설정

#### 방법 1: setenv.sh (권장)
```bash
# setenv.sh 파일 확인/생성
sudo nano /opt/tomcat/bin/setenv.sh
```
내용 입력:
```bash
#!/bin/bash
export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"
```
저장 후:
```bash
sudo chmod +x /opt/tomcat/bin/setenv.sh
sudo systemctl restart tomcat
```

#### 방법 2: tomcat.service Environment (이미 시도함)
```bash
sudo nano /etc/systemd/system/tomcat.service
```
`[Service]` 섹션에:
```ini
Environment="JAVA_OPTS=-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"
```
저장 후:
```bash
sudo systemctl daemon-reload
sudo systemctl restart tomcat
```

#### 적용 확인
```bash
# Java 프로세스의 JVM 옵션 확인
ps aux | grep tomcat | grep IPv4
# 또는
sudo -u tomcat /opt/tomcat/bin/catalina.sh version 2>/dev/null
```

---

## 4. STS 콘솔 로그 분석 (INI 파일)

### 4-1. Maven 빌드 결과

```
[INFO] BUILD SUCCESS
[INFO] Total time: 6.208 s
[INFO] Finished at: 2026-04-01T12:07:25+09:00
target/portwatch-1.0.0-BUILD-SNAPSHOT.war 생성 완료
```
✅ 84개 소스 파일 컴파일 성공

### 4-2. STS Tomcat 기동 결과

```
INFO: Root WebApplicationContext: initialization completed in 3145 ms
INFO: FrameworkServlet 'appServlet': initialization completed in 1434 ms
INFO: 서버가 [7613] 밀리초 내에 시작되었습니다.
```
✅ **오류 없음**, 정상 기동

### 4-3. taskScheduler 초기화 확인
```
INFO: Bean 'taskScheduler' of type [ThreadPoolTaskScheduler]
      is not eligible for getting processed by all BeanPostProcessors
```
✅ **@Scheduled 스케줄러 활성화됨** (이 메시지는 정상 경고)

### 4-4. 전체 매핑된 컨트롤러 (주요 엔드포인트)

| 엔드포인트 | 컨트롤러 |
|-----------|---------|
| GET `/` | HomeController |
| GET/POST `/member/login` | MemberController |
| GET/POST `/member/signup` | MemberController |
| GET `/member/guest-login` | MemberController |
| GET `/stock/list` | StockController |
| GET `/news/list` | NewsController |
| GET `/debug/db-check` | SessionDebugController |
| GET `/debug/bcrypt` | SessionDebugController |
| POST `/crawler/korea` | StockCrawlerController |
| POST `/crawler/us` | StockCrawlerController |
| POST `/api/news/crawl` | NewsApiController |
| GET `/api/admin/update-all` | AdminStockUpdateController |

---

## 5. 로그인 / 회원가입 문제 분석

### 5-1. 현재 실패 원인
DB 연결 실패가 cascade로 전파됩니다:
```
로그인 POST /member/login
  → MemberServiceImpl.login()
    → memberMapper.findByEmail()
      → HikariCP 연결 시도
        → 타임아웃 → 500 Internal Server Error
```

### 5-2. DB 연결 복구 후 테스트 계정 확인

EC2 PuTTy에서:
```bash
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -P 3306 -u admin -p
```
비밀번호: `portwatch2026!!abcd`

```sql
USE portwatch;

-- 현재 계정 확인
SELECT member_id, member_email, member_status,
       LEFT(member_pass, 30) as pass_prefix FROM MEMBER;

-- 테스트 계정 없으면 추가
INSERT IGNORE INTO MEMBER (
    member_id, member_email, member_pass, member_name,
    member_role, member_status, balance, created_at
) VALUES
('testuser01', 'test@test.com',
 '$2a$10$0GBSb04xfIUriIM/5WwqhSuKgZHeltHn.4qFqnLIlojkISte/pSgSC',
 '테스트유저', 'USER', 'ACTIVE', 1000000, NOW());

-- 기존 계정 비밀번호 업데이트 (portwatch123)
UPDATE MEMBER
SET member_pass = '$2a$10$0GBSb04xfIUriIM/5WwqhSuKgZHeltHn.4qFqnLIlojkISte/pSgSC'
WHERE member_email = 'test@test.com';
```

**로그인 테스트 정보**: `test@test.com` / `portwatch123`

### 5-3. 비밀번호 해시 확인 (브라우저)
```
http://54.180.142.111:8080/debug/bcrypt?pass=portwatch123
```
반환값의 hash를 MySQL UPDATE에 사용

---

## 6. 뉴스·주식 크롤링 스케줄러 현황

### 6-1. NewsScheduler.java

| 스케줄 | 실행 시점 | 주기 |
|--------|----------|------|
| 한국 뉴스 크롤링 | 서버 시작 1분 후 | 30분마다 |
| 미국 뉴스 크롤링 | 서버 시작 2분 후 | 1시간마다 |

크롤링 소스:
- 한국: 네이버 증권 뉴스 (NaverNewsCrawler)
- 미국: Yahoo Finance RSS `feeds.finance.yahoo.com/rss/2.0/headline`

### 6-2. StockScheduler.java

| 스케줄 | 실행 시점 | 주기 |
|--------|----------|------|
| 한국 주식 TOP100 | 서버 시작 2분 후 | 30분마다 |
| 미국 주식 업데이트 | 서버 시작 3분 후 | 1시간마다 |

### 6-3. 수동 즉시 실행 (DB 연결 후)

```bash
# 한국 주식 크롤링
curl -X POST http://localhost:8080/crawler/korea

# 미국 주식 크롤링
curl -X POST http://localhost:8080/crawler/us

# 뉴스 전체 크롤링
curl -X GET http://localhost:8080/api/admin/update-all-news

# 주식 전체 업데이트
curl -X GET http://localhost:8080/api/admin/update-all
```

---

## 7. 코드 수정 완료 내역

### 7-1. root-context.xml

| 항목 | 이전 | 현재 | 이유 |
|------|------|------|------|
| JDBC connectTimeout | 30000ms | 10000ms | TCP 연결 빠른 실패 |
| HikariCP connectionTimeout | 5000ms | 30000ms | connectTimeout보다 커야 함 |
| initializationFailTimeout | 30000 | -1 | Tomcat 기동 시 DB 없어도 허용 |
| maximumPoolSize | 5 | 10 | 동시 요청 처리 향상 |
| task:annotation-driven | ❌ 없음 | ✅ 추가 | @Scheduled 활성화 (핵심!) |
| task:scheduler pool-size | - | 5 | 동시 스케줄 실행 가능 |

### 7-2. SecurityConfig.java

| 항목 | 수정 내용 |
|------|----------|
| 공개 URL 허용 | `/`, `/home`, `/main`, `/member/**` |
| API 허용 | `/api/stocks/**`, `/api/news/**`, `/api/member/**`, `/debug/**` |
| 크롤러 허용 | `/crawler/**`, `/api/admin/**` |
| 미인증 처리 | 403 대신 `/member/login` 리다이렉트 |

### 7-3. 스케줄러

| 파일 | 수정 내용 |
|------|----------|
| StockScheduler.java | 신규 생성: 한국/미국 주기 크롤링 |
| NewsScheduler.java | 미국 뉴스 Yahoo Finance RSS 구현 |
| StockServiceImpl.java | getAllStocks() null → emptyList() |

### 7-4. 매퍼 성능

| 파일 | 수정 내용 |
|------|----------|
| StockMapper.xml | `SELECT * FROM STOCK LIMIT 200` 추가 |
| NewsMapper.xml | `SELECT * FROM NEWS LIMIT 50` 추가 |

---

## 8. EC2 완전 복구 절차 (지금 바로 실행)

### Step 1: setenv.sh 생성 (IPv4 강제)

```bash
# setenv.sh 생성 (없으면)
sudo bash -c 'cat > /opt/tomcat/bin/setenv.sh << EOF
#!/bin/bash
export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"
EOF'
sudo chmod +x /opt/tomcat/bin/setenv.sh
```

검증:
```bash
cat /opt/tomcat/bin/setenv.sh
```
출력 확인: `export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"`

### Step 2: 올바른 배포

```bash
# Tomcat 정지
sudo systemctl stop tomcat

# 기존 배포 삭제
sudo rm -rf /opt/tomcat/webapps/ROOT
sudo rm -f  /opt/tomcat/webapps/ROOT.war

# 새 WAR 배포
sudo cp /home/ubuntu/portwatch.war /opt/tomcat/webapps/ROOT.war
sudo ls -la /opt/tomcat/webapps/ROOT.war   # 파일 확인

# Tomcat 기동
sudo systemctl start tomcat

# 30초 대기 후 로그 확인
sleep 30
sudo tail -30 /opt/tomcat/logs/catalina.out
```

### Step 3: DB 연결 확인

```bash
curl -s http://localhost:8080/debug/db-check | python3 -m json.tool
```

**성공 응답:**
```json
{
    "dbConnected": true,
    "success": true,
    "message": "DB 정상..."
}
```

### Step 4: 전체 기능 확인

```bash
# 모든 주요 페이지 상태 코드 확인
for url in "/" "/member/login" "/member/signup" "/stock/list" "/news/list"; do
    code=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080${url}")
    echo "$url → $code"
done
```

기대 출력:
```
/ → 200
/member/login → 200
/member/signup → 200
/stock/list → 200  (또는 302 if 인증 필요)
/news/list → 200   (또는 302 if 인증 필요)
```

---

## 9. 내부 문제 & 해결책 요약

| # | 문제 | 원인 | 해결 | 상태 |
|---|------|------|------|------|
| 1 | HTTP 404 전체 | ROOT.war 삭제 후 autoDeploy | ROOT.war 삭제 금지 | 절차 문서화 |
| 2 | DB 연결 30s 타임아웃 | Java IPv6 우선 DNS | setenv.sh JAVA_OPTS IPv4 | **지금 적용 필요** |
| 3 | @Scheduled 미작동 | task:annotation-driven 누락 | root-context.xml 추가 | ✅ 코드 수정됨 |
| 4 | HikariCP timeout 충돌 | connectionTimeout < connectTimeout | 30000 vs 10000 교정 | ✅ 코드 수정됨 |
| 5 | 주식목록 0개 | STOCK 테이블 비어있음 + JSP 변수 오류 | ${stockList}→${stocks}, 스케줄러 | ✅ 코드 수정됨 |
| 6 | 뉴스 500 오류 | DB 연결 실패 cascade | DB 연결 해결 시 자동 해결 | DB 연결 의존 |
| 7 | 미국 뉴스 TODO 비어있음 | crawlUSNewsNow() stub | Yahoo Finance RSS 구현 | ✅ 코드 수정됨 |
| 8 | STS MemberVO 404 | Eclipse 배포 설정 문제 | Project Clean → Re-add server | IDE 설정 |
| 9 | EC2 sed URL 오염 | sed `/` 구분자 충돌 | python3으로 수정 | 과거 이슈 |
| 10 | member_id 자동생성 | register() 로직 누락 | UUID 생성 코드 추가 | ✅ 수정됨 |

---

## 10. 아키텍처 다이어그램

```
[사용자 브라우저]
       │ HTTP
       ▼
[EC2 Ubuntu 24.04]
  ┌─────────────────┐
  │ Tomcat 9        │
  │  └ ROOT.war     │
  │     ├ Spring 5  │
  │     ├ Security  │
  │     ├ MyBatis   │
  │     └ Schedulers│
  │        ├ NewsScheduler    (30분/1시간)
  │        └ StockScheduler   (30분/1시간)
  └─────────────────┘
       │ JDBC MySQL
       ▼
[AWS RDS MySQL 8.4.7]
  portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306
  Database: portwatch
  Tables: MEMBER, STOCK, NEWS, PORTFOLIO, WATCHLIST, PAYMENT, ...

[외부 데이터 소스]
  ├ 네이버 증권 → NaverNewsCrawler → NEWS 테이블
  ├ Yahoo Finance RSS → NewsScheduler → NEWS 테이블
  ├ 네이버 금융 → KoreaStockCrawler → STOCK 테이블
  └ Yahoo Finance → USStockPriceUpdateService → STOCK 테이블
```

---

## 11. 다음 우선 작업 (Priority)

1. **[즉시]** EC2에서 setenv.sh 생성 후 재배포 → DB 연결 확인
2. **[즉시]** 테스트 계정 비밀번호 확인 및 로그인 테스트
3. **[DB 연결 후]** 수동 크롤링 명령으로 STOCK/NEWS 데이터 채움
4. **[선택]** 운영 환경에서 `/debug/**` 엔드포인트 비활성화
