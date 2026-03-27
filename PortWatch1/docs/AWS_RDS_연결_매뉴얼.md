# AWS RDS 연결 매뉴얼 & 종목 데이터 초기화 가이드

## 1. 현재 오류 원인 분석

### /news/list 500 에러
| 원인 | 설명 |
|------|------|
| **DB 연결 실패** | EC2 Tomcat의 WAR에 `sslMode=DISABLED` 미반영 |
| **MySQL 8.4 SSL** | `useSSL=false`만으론 부족, `sslMode=DISABLED` 필수 |
| **IPv6 문제** | Ubuntu JVM 기본값 IPv6 → RDS는 IPv4만 지원 |

### /stock/list 데이터 없음
| 원인 | 설명 |
|------|------|
| **StockMapper.xml 누락** | `StockMapper.java` 인터페이스에 대응하는 XML이 없었음 |
| **STOCK 테이블 빈 상태** | 종목 초기 데이터 미삽입 |

---

## 2. EC2 서버 - Tomcat JAVA_OPTS 설정 확인

### 2-1. systemd 서비스 파일 확인
```bash
cat /etc/systemd/system/tomcat.service
```
아래 줄이 있는지 확인:
```
Environment="JAVA_OPTS=-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true"
```

### 2-2. 없으면 추가 (nano 에디터)
```bash
sudo nano /etc/systemd/system/tomcat.service
```
`[Service]` 섹션에 추가:
```
Environment="JAVA_OPTS=-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true"
```

### 2-3. 서비스 재시작
```bash
sudo systemctl daemon-reload
sudo systemctl restart tomcat
sudo systemctl status tomcat
```

---

## 3. JDBC URL 확인 (root-context.xml)

현재 올바른 설정 (로컬 STS 프로젝트):
```xml
<property name="jdbcUrl"
    value="jdbc:mysql://portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306/portwatch
           ?useSSL=false
           &amp;sslMode=DISABLED
           &amp;serverTimezone=Asia/Seoul
           &amp;characterEncoding=UTF-8
           &amp;allowPublicKeyRetrieval=true
           &amp;connectTimeout=30000
           &amp;socketTimeout=60000"/>
```
**핵심**: `sslMode=DISABLED` 포함 여부 확인

---

## 4. WAR 재빌드 및 배포 절차

### 4-1. STS에서 WAR 빌드
```
Project 우클릭 → Run As → Maven Build
Goals: clean package -DskipTests
```
또는 cmd/터미널에서:
```bash
cd C:\Users\소현석\git\PortWatch1\PortWatch1
mvn clean package -DskipTests
```
빌드 완료 후 파일: `target/PortWatch1.war`

### 4-2. EC2 서버에 WAR 업로드 (WinSCP 또는 scp)
```bash
# WinSCP로 접속 후 아래 경로에 업로드:
/opt/tomcat/webapps/PortWatch1.war

# 또는 scp 명령어:
scp -i [키파일.pem] target/PortWatch1.war ubuntu@54.180.142.111:/opt/tomcat/webapps/
```

### 4-3. Tomcat 재시작
```bash
sudo systemctl stop tomcat
# 기존 배포 폴더 삭제 (필수!)
sudo rm -rf /opt/tomcat/webapps/PortWatch1
sudo systemctl start tomcat
sudo systemctl status tomcat
```

### 4-4. 배포 확인
```bash
# 로그 확인
sudo tail -f /opt/tomcat/logs/catalina.out

# 정상 연결 시 출력:
# HikariPool-1 - Start completed.
# Root WebApplicationContext: initialization completed in ...ms
```

---

## 5. DB 연결 테스트 (PuTTy/SSH)

```bash
# EC2에서 RDS MySQL 직접 연결 테스트
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -u admin -p portwatch

# 연결 후:
USE portwatch;
SHOW TABLES;
SELECT COUNT(*) FROM STOCK;
SELECT COUNT(*) FROM NEWS;
SELECT COUNT(*) FROM MEMBER;
```

---

## 6. STOCK 테이블 초기 데이터 삽입

> PuTTy SSH로 EC2 접속 후 MySQL에서 실행하거나, MySQL Workbench에서 RDS에 직접 실행

### 6-1. 테이블 구조 확인
```sql
USE portwatch;
DESCRIBE STOCK;
```

### 6-2. 한국 대표 종목 50개 삽입
```sql
USE portwatch;

INSERT INTO STOCK (stock_code, stock_name, country, market, market_type, industry, current_price, price_change, change_rate, volume, market_cap, created_at, updated_at) VALUES
-- KOSPI 대형주
('005930', '삼성전자',    'KR', 'KOSPI', 'KOSPI', '반도체',   73400, -200, -0.27, 12500000, 438000, NOW(), NOW()),
('000660', 'SK하이닉스',  'KR', 'KOSPI', 'KOSPI', '반도체',   180000, 1500, 0.84, 3200000, 131000, NOW(), NOW()),
('005380', '현대차',      'KR', 'KOSPI', 'KOSPI', '자동차',   210000, -1000, -0.47, 980000, 44700, NOW(), NOW()),
('035420', 'NAVER',       'KR', 'KOSPI', 'KOSPI', 'IT서비스', 190000, 2000, 1.06, 720000, 31200, NOW(), NOW()),
('005490', 'POSCO홀딩스', 'KR', 'KOSPI', 'KOSPI', '철강',     380000, -5000, -1.30, 280000, 33100, NOW(), NOW()),
('051910', 'LG화학',      'KR', 'KOSPI', 'KOSPI', '화학',     330000, 3000, 0.92, 350000, 23300, NOW(), NOW()),
('006400', '삼성SDI',     'KR', 'KOSPI', 'KOSPI', '배터리',   280000, -2000, -0.71, 420000, 19300, NOW(), NOW()),
('035720', '카카오',      'KR', 'KOSPI', 'KOSPI', 'IT서비스', 44000, 500, 1.15, 2100000, 19500, NOW(), NOW()),
('003550', 'LG',          'KR', 'KOSPI', 'KOSPI', '지주회사', 75000, -200, -0.27, 380000, 13100, NOW(), NOW()),
('096770', 'SK이노베이션', 'KR', 'KOSPI', 'KOSPI', '에너지',  120000, -800, -0.66, 520000, 11000, NOW(), NOW()),
('028260', '삼성물산',    'KR', 'KOSPI', 'KOSPI', '건설',     140000, 1000, 0.72, 290000, 27400, NOW(), NOW()),
('012330', '현대모비스',  'KR', 'KOSPI', 'KOSPI', '자동차부품',270000, -1500, -0.55, 310000, 25700, NOW(), NOW()),
('066570', 'LG전자',      'KR', 'KOSPI', 'KOSPI', '전자',     88000, 1200, 1.38, 650000, 14300, NOW(), NOW()),
('055550', '신한지주',    'KR', 'KOSPI', 'KOSPI', '금융',     48500, 300, 0.62, 1100000, 22900, NOW(), NOW()),
('105560', 'KB금융',      'KR', 'KOSPI', 'KOSPI', '금융',     82000, 500, 0.61, 780000, 32200, NOW(), NOW()),
('086790', '하나금융지주','KR', 'KOSPI', 'KOSPI', '금융',     64000, 400, 0.63, 690000, 18900, NOW(), NOW()),
('032830', '삼성생명',    'KR', 'KOSPI', 'KOSPI', '보험',     98000, -500, -0.51, 230000, 19600, NOW(), NOW()),
('011170', '롯데케미칼',  'KR', 'KOSPI', 'KOSPI', '화학',     85000, -1000, -1.16, 390000, 5900, NOW(), NOW()),
('207940', '삼성바이오로직스','KR','KOSPI','KOSPI','바이오',   800000, 10000, 1.27, 180000, 113500, NOW(), NOW()),
('068270', '셀트리온',    'KR', 'KOSPI', 'KOSPI', '바이오',   160000, 2000, 1.27, 580000, 21500, NOW(), NOW()),
-- KOSDAQ 대표 종목
('247540', '에코프로비엠', 'KR', 'KOSDAQ', 'KOSDAQ', '배터리',  200000, -3000, -1.48, 650000, 18200, NOW(), NOW()),
('086520', '에코프로',    'KR', 'KOSDAQ', 'KOSDAQ', '배터리',   70000, 1000, 1.45, 1200000, 9400, NOW(), NOW()),
('091990', '셀트리온헬스케어','KR','KOSDAQ','KOSDAQ','바이오', 75000, 800, 1.08, 520000, 10800, NOW(), NOW()),
('041510', 'SM엔터테인먼트','KR','KOSDAQ','KOSDAQ','엔터',    95000, 1500, 1.60, 280000, 8300, NOW(), NOW()),
('122870', '와이지엔터테인먼트','KR','KOSDAQ','KOSDAQ','엔터', 55000, 700, 1.29, 310000, 2100, NOW(), NOW()),
-- 미국 대표 종목 (나스닥)
('AAPL', 'Apple',         'US', 'NASDAQ', 'NASDAQ', '기술',    213.49, 1.25, 0.59, 52000000, 3250000, NOW(), NOW()),
('MSFT', 'Microsoft',     'US', 'NASDAQ', 'NASDAQ', '기술',    415.32, -2.11, -0.51, 18000000, 3090000, NOW(), NOW()),
('NVDA', 'NVIDIA',        'US', 'NASDAQ', 'NASDAQ', '반도체',  875.40, 15.30, 1.78, 42000000, 2160000, NOW(), NOW()),
('GOOGL', 'Alphabet',     'US', 'NASDAQ', 'NASDAQ', '기술',    175.84, 0.92, 0.52, 21000000, 2180000, NOW(), NOW()),
('AMZN', 'Amazon',        'US', 'NASDAQ', 'NASDAQ', '전자상거래',185.70, 2.30, 1.25, 35000000, 1930000, NOW(), NOW()),
('META', 'Meta',          'US', 'NASDAQ', 'NASDAQ', '소셜미디어',503.10, -1.80, -0.36, 14000000, 1280000, NOW(), NOW()),
('TSLA', 'Tesla',         'US', 'NASDAQ', 'NASDAQ', '전기차',   172.40, 3.20, 1.89, 98000000, 551000, NOW(), NOW()),
('AVGO', 'Broadcom',      'US', 'NASDAQ', 'NASDAQ', '반도체',  1580.00, 22.50, 1.44, 4200000, 735000, NOW(), NOW()),
('ASML', 'ASML',          'US', 'NASDAQ', 'NASDAQ', '반도체장비',958.00, -5.40, -0.56, 1100000, 377000, NOW(), NOW()),
('COST', 'Costco',        'US', 'NASDAQ', 'NASDAQ', '유통',    885.50, 4.20, 0.48, 2800000, 393000, NOW(), NOW()),
-- NYSE 대표 종목
('BRK.B', 'Berkshire Hathaway','US','NYSE','NYSE','금융',  431.20, 1.80, 0.42, 3500000, 623000, NOW(), NOW()),
('JPM', 'JPMorgan Chase', 'US', 'NYSE', 'NYSE', '금융',   226.50, 0.75, 0.33, 9800000, 650000, NOW(), NOW()),
('V', 'Visa',             'US', 'NYSE', 'NYSE', '결제',    287.40, 1.10, 0.38, 5200000, 594000, NOW(), NOW()),
('XOM', 'ExxonMobil',     'US', 'NYSE', 'NYSE', '에너지',  113.80, -0.50, -0.44, 14000000, 454000, NOW(), NOW()),
('WMT', 'Walmart',        'US', 'NYSE', 'NYSE', '유통',    87.90, 0.30, 0.34, 22000000, 710000, NOW(), NOW()),
('MA', 'Mastercard',      'US', 'NYSE', 'NYSE', '결제',    487.60, 2.10, 0.43, 3100000, 453000, NOW(), NOW()),
('LLY', 'Eli Lilly',      'US', 'NYSE', 'NYSE', '제약',    892.40, -8.20, -0.91, 2800000, 847000, NOW(), NOW()),
('UNH', 'UnitedHealth',   'US', 'NYSE', 'NYSE', '헬스케어', 530.60, -3.50, -0.66, 3200000, 488000, NOW(), NOW()),
('JNJ', 'Johnson & Johnson','US','NYSE','NYSE','제약',    158.90, 0.40, 0.25, 6800000, 381000, NOW(), NOW()),
('HD', 'Home Depot',      'US', 'NYSE', 'NYSE', '유통',    378.20, 1.90, 0.50, 3900000, 375000, NOW(), NOW()),
('PG', 'Procter & Gamble','US', 'NYSE', 'NYSE', '소비재',   168.50, 0.60, 0.36, 5100000, 396000, NOW(), NOW()),
('BAC', 'Bank of America', 'US', 'NYSE', 'NYSE', '금융',    40.80, 0.20, 0.49, 35000000, 320000, NOW(), NOW()),
('ABBV', 'AbbVie',        'US', 'NYSE', 'NYSE', '제약',    183.70, 1.20, 0.66, 4500000, 325000, NOW(), NOW()),
('CVX', 'Chevron',        'US', 'NYSE', 'NYSE', '에너지',   153.40, -0.80, -0.52, 8200000, 287000, NOW(), NOW()),
('MRK', 'Merck',          'US', 'NYSE', 'NYSE', '제약',    128.60, 0.70, 0.55, 7800000, 328000, NOW(), NOW()),
('KO', 'Coca-Cola',       'US', 'NYSE', 'NYSE', '음료',     63.20, 0.15, 0.24, 12000000, 272000, NOW(), NOW());
```

### 6-3. 삽입 결과 확인
```sql
SELECT COUNT(*) AS total_stocks FROM STOCK;
SELECT country, market_type, COUNT(*) AS cnt FROM STOCK GROUP BY country, market_type;
SELECT stock_code, stock_name, current_price FROM STOCK ORDER BY country, stock_code LIMIT 20;
```

---

## 7. NEWS 테이블 초기 데이터 삽입 (선택)

```sql
USE portwatch;

INSERT INTO news (title, stock_code, news_url, published_date, name) VALUES
('삼성전자, AI 반도체 시장 공략 본격화', '005930', 'https://finance.naver.com/news/1', NOW(), '네이버금융'),
('SK하이닉스, HBM3E 생산량 확대 발표', '000660', 'https://finance.naver.com/news/2', NOW(), '네이버금융'),
('NVIDIA, 2분기 실적 예상치 상회', 'NVDA', 'https://finance.naver.com/news/3', NOW(), '네이버금융'),
('Tesla, 사이버트럭 판매 호조', 'TSLA', 'https://finance.naver.com/news/4', NOW(), '네이버금융'),
('Apple, WWDC 2026 혁신 발표 예정', 'AAPL', 'https://finance.naver.com/news/5', NOW(), '네이버금융'),
('카카오, 생성형 AI 서비스 출시', '035720', 'https://finance.naver.com/news/6', NOW(), '네이버금융'),
('현대차, 전기차 글로벌 판매 증가', '005380', 'https://finance.naver.com/news/7', NOW(), '네이버금융'),
('에코프로비엠, 배터리 소재 신공장 가동', '247540', 'https://finance.naver.com/news/8', NOW(), '네이버금융'),
('NAVER, 하이퍼클로바X 성능 개선', '035420', 'https://finance.naver.com/news/9', NOW(), '네이버금융'),
('삼성SDI, 전고체 배터리 2027년 양산', '006400', 'https://finance.naver.com/news/10', NOW(), '네이버금융');

SELECT COUNT(*) AS news_count FROM news;
```

---

## 8. 전체 배포 체크리스트

```
[ ] 1. STS에서 Maven clean package 실행
[ ] 2. target/PortWatch1.war 생성 확인
[ ] 3. WinSCP로 EC2에 WAR 업로드
[ ] 4. PuTTy로 EC2 접속
[ ] 5. systemd JAVA_OPTS 확인 (-Djava.net.preferIPv4Stack=true)
[ ] 6. Tomcat 중지 → 기존 배포폴더 삭제 → 시작
[ ] 7. catalina.out 로그에서 "HikariPool-1 - Start completed" 확인
[ ] 8. http://54.180.142.111:8080/debug/db-check 접속하여 DB 연결 확인
[ ] 9. http://54.180.142.111:8080/member/login 로그인 테스트
[ ] 10. MySQL STOCK 테이블 데이터 삽입
[ ] 11. http://54.180.142.111:8080/stock/list 확인
[ ] 12. http://54.180.142.111:8080/news/list 확인
```

---

## 9. 테스트 계정

| 이메일 | 비밀번호 | 역할 |
|--------|----------|------|
| admin2026@portwatch.com | portwatch123 | ADMIN |
| admin@portwatch.com | portwatch123 | ADMIN |
| test01@test.com | portwatch123 | USER |
| test02@test.com | portwatch123 | USER |

---

## 10. 디버그 엔드포인트 (배포 후 확인용)

```
GET /debug/db-check          → DB 연결 상태 확인
GET /debug/bcrypt?pass=XXXX  → BCrypt 해시 생성
GET /api/news/recent?limit=5  → 뉴스 API 테스트
GET /stock/api/stocks         → 주식 API 테스트
```
