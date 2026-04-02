# PortWatch DB 테이블 DDL 전체
> MySQL 8.4.3 (RDS) — portwatch 데이터베이스
> 마이바티스 Mapper XML 분석 기준으로 작성

---

## 초기 설정

```sql
-- EC2 PuTTy에서 MySQL 접속
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -P 3306 -u admin -p

-- DB 선택
USE portwatch;

-- 현재 테이블 목록 확인
SHOW TABLES;
```

---

## 1. MEMBER 테이블

```sql
CREATE TABLE IF NOT EXISTS MEMBER (
    member_id      VARCHAR(50)   NOT NULL          COMMENT '회원 아이디 (서버 자동 생성)',
    member_email   VARCHAR(100)  NOT NULL UNIQUE   COMMENT '이메일 (로그인 키)',
    member_pass    VARCHAR(200)  NOT NULL           COMMENT 'BCrypt 암호화 비밀번호',
    member_name    VARCHAR(20)   NOT NULL           COMMENT '이름',
    member_phone   VARCHAR(20)   DEFAULT NULL       COMMENT '전화번호',
    member_address VARCHAR(255)  DEFAULT NULL       COMMENT '주소',
    member_gender  VARCHAR(10)   DEFAULT NULL       COMMENT '성별 (MALE/FEMALE)',
    member_birth   TIMESTAMP     NULL DEFAULT NULL  COMMENT '생년월일',
    member_role    VARCHAR(20)   NOT NULL DEFAULT 'USER'   COMMENT '역할 (USER/ADMIN)',
    member_status  VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE' COMMENT '상태 (ACTIVE/INACTIVE)',
    balance        DOUBLE        NOT NULL DEFAULT 1000000  COMMENT '보유 잔액 (원)',
    created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 테스트 계정 삽입

```sql
-- 비밀번호: portwatch123
INSERT IGNORE INTO MEMBER (
    member_id, member_email, member_pass, member_name,
    member_role, member_status, balance
) VALUES
('testuser01', 'test@portwatch.com',
 '$2a$10$0GBSb04xfIUriIM/5WwqhSuKgZHeltHn.4qFqnLIlojkISte/pSgSC',
 '테스트유저', 'USER', 'ACTIVE', 1000000),
('adminuser01', 'admin@portwatch.com',
 '$2a$10$0GBSb04xfIUriIM/5WwqhSuKgZHeltHn.4qFqnLIlojkISte/pSgSC',
 '관리자', 'ADMIN', 'ACTIVE', 5000000);
```

---

## 2. STOCK 테이블

```sql
CREATE TABLE IF NOT EXISTS STOCK (
    stock_id      INT           NOT NULL AUTO_INCREMENT COMMENT '주식 고유 ID',
    stock_code    VARCHAR(20)   NOT NULL UNIQUE         COMMENT '종목 코드 (005930, AAPL 등)',
    stock_name    VARCHAR(100)  NOT NULL                COMMENT '종목명',
    country       VARCHAR(5)    NOT NULL DEFAULT 'KR'   COMMENT '국가 코드 (KR/US)',
    market        VARCHAR(20)   DEFAULT NULL            COMMENT '시장명 (KOSPI/KOSDAQ/NYSE/NASDAQ)',
    market_type   VARCHAR(20)   DEFAULT NULL            COMMENT '시장 유형 (상세 분류)',
    industry      VARCHAR(50)   DEFAULT NULL            COMMENT '업종/산업 분류',
    current_price DOUBLE        DEFAULT 0.0             COMMENT '현재가',
    price_change  DOUBLE        DEFAULT 0.0             COMMENT '전일 대비 가격 변동',
    change_rate   DOUBLE        DEFAULT 0.0             COMMENT '등락률 (%)',
    volume        BIGINT        DEFAULT 0               COMMENT '거래량',
    market_cap    BIGINT        DEFAULT NULL            COMMENT '시가총액',
    created_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (stock_id),
    UNIQUE KEY uk_stock_code (stock_code),
    INDEX idx_country (country),
    INDEX idx_market  (market),
    INDEX idx_industry (industry)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 최소 테스트 데이터 삽입

```sql
INSERT IGNORE INTO STOCK (stock_code, stock_name, country, market, industry, current_price)
VALUES
('005930', '삼성전자',   'KR', 'KOSPI', '반도체/전자',  74000.0),
('000660', 'SK하이닉스', 'KR', 'KOSPI', '반도체',       195000.0),
('035420', 'NAVER',     'KR', 'KOSPI', 'IT/인터넷',   180000.0),
('005380', '현대차',    'KR', 'KOSPI', '자동차',       250000.0),
('068270', '셀트리온',  'KR', 'KOSPI', '바이오',       175000.0),
('AAPL',  'Apple',     'US', 'NASDAQ', 'Technology',   170.0),
('MSFT',  'Microsoft', 'US', 'NASDAQ', 'Technology',   380.0),
('GOOGL', 'Alphabet',  'US', 'NASDAQ', 'Technology',   140.0),
('AMZN',  'Amazon',    'US', 'NASDAQ', 'E-Commerce',   180.0),
('TSLA',  'Tesla',     'US', 'NASDAQ', 'Automotive',   175.0);

SELECT stock_code, stock_name, country, current_price FROM STOCK;
```

---

## 3. NEWS 테이블

```sql
CREATE TABLE IF NOT EXISTS NEWS (
    news_id        BIGINT        NOT NULL AUTO_INCREMENT COMMENT '뉴스 고유 ID',
    title          VARCHAR(500)  DEFAULT NULL            COMMENT '뉴스 제목',
    stock_id       INT           DEFAULT NULL            COMMENT 'STOCK 테이블 FK (선택적)',
    stock_code     VARCHAR(20)   DEFAULT NULL            COMMENT '관련 종목 코드',
    news_code      VARCHAR(50)   DEFAULT NULL            COMMENT '뉴스 고유 코드',
    news_title     VARCHAR(500)  DEFAULT NULL            COMMENT '뉴스 원제목',
    news_url       VARCHAR(1000) DEFAULT NULL            COMMENT '뉴스 원문 URL (중복 방지)',
    published_date TIMESTAMP     DEFAULT NULL            COMMENT '기사 발행 날짜',
    created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    name           VARCHAR(100)  DEFAULT NULL            COMMENT '출처/카테고리 (예: 네이버, 연합뉴스)',
    PRIMARY KEY (news_id),
    UNIQUE KEY uk_news_url (news_url(500)),
    INDEX idx_stock_code    (stock_code),
    INDEX idx_published_date (published_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 최소 테스트 뉴스 삽입

```sql
INSERT IGNORE INTO NEWS (title, stock_code, news_url, published_date, name)
VALUES
('삼성전자, 3분기 실적 발표... 반도체 흑자 전환',
 '005930', 'https://example.com/news1', NOW(), '연합뉴스'),
('APPLE, 신형 iPhone 출시 예정',
 'AAPL', 'https://example.com/news2', NOW(), 'Bloomberg'),
('테슬라 자율주행 소프트웨어 업데이트',
 'TSLA', 'https://example.com/news3', NOW(), 'Reuters');

SELECT news_id, title, stock_code, published_date FROM NEWS LIMIT 10;
```

---

## 4. PORTFOLIO 테이블

```sql
CREATE TABLE IF NOT EXISTS PORTFOLIO (
    portfolio_id   INT           NOT NULL AUTO_INCREMENT,
    member_id      VARCHAR(50)   NOT NULL                COMMENT '회원 ID (MEMBER FK)',
    stock_code     VARCHAR(20)   NOT NULL                COMMENT '종목 코드',
    stock_name     VARCHAR(100)  DEFAULT NULL,
    quantity       INT           NOT NULL DEFAULT 0       COMMENT '보유 수량',
    avg_price      DOUBLE        DEFAULT 0.0              COMMENT '평균 매입가',
    total_invested DOUBLE        DEFAULT 0.0              COMMENT '총 투자금액',
    created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (portfolio_id),
    INDEX idx_member_id (member_id),
    INDEX idx_stock_code (stock_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 5. WATCHLIST 테이블

```sql
CREATE TABLE IF NOT EXISTS WATCHLIST (
    watchlist_id   INT           NOT NULL AUTO_INCREMENT,
    member_id      VARCHAR(50)   NOT NULL                COMMENT '회원 ID (MEMBER FK)',
    stock_code     VARCHAR(20)   NOT NULL                COMMENT '종목 코드',
    stock_name     VARCHAR(100)  DEFAULT NULL,
    created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (watchlist_id),
    UNIQUE KEY uk_member_stock (member_id, stock_code),
    INDEX idx_member_id (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 6. PAYMENT 테이블

```sql
CREATE TABLE IF NOT EXISTS PAYMENT (
    payment_id     INT           NOT NULL AUTO_INCREMENT,
    member_id      VARCHAR(50)   NOT NULL,
    stock_code     VARCHAR(20)   NOT NULL,
    payment_type   VARCHAR(20)   NOT NULL DEFAULT 'BUY'  COMMENT 'BUY/SELL',
    quantity       INT           NOT NULL DEFAULT 0,
    price          DOUBLE        NOT NULL DEFAULT 0.0,
    total_amount   DOUBLE        NOT NULL DEFAULT 0.0,
    payment_date   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (payment_id),
    INDEX idx_member_id  (member_id),
    INDEX idx_stock_code (stock_code),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 전체 테이블 한 번에 생성 (최초 설치 시)

```sql
-- portwatch 데이터베이스 없으면 생성
CREATE DATABASE IF NOT EXISTS portwatch
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE portwatch;

-- 테이블 생성 (이미 있으면 무시)
CREATE TABLE IF NOT EXISTS MEMBER ( ... );    -- 위의 DDL 사용
CREATE TABLE IF NOT EXISTS STOCK  ( ... );    -- 위의 DDL 사용
CREATE TABLE IF NOT EXISTS NEWS   ( ... );    -- 위의 DDL 사용
CREATE TABLE IF NOT EXISTS PORTFOLIO ( ... ); -- 위의 DDL 사용
CREATE TABLE IF NOT EXISTS WATCHLIST ( ... ); -- 위의 DDL 사용
CREATE TABLE IF NOT EXISTS PAYMENT ( ... );   -- 위의 DDL 사용

-- 확인
SHOW TABLES;
```

---

## 테이블 현황 빠른 확인 쿼리

```sql
-- 전체 테이블 데이터 개수 한 번에 확인
SELECT 'MEMBER'    AS tbl, COUNT(*) AS cnt FROM MEMBER
UNION ALL
SELECT 'STOCK',    COUNT(*) FROM STOCK
UNION ALL
SELECT 'NEWS',     COUNT(*) FROM NEWS
UNION ALL
SELECT 'PORTFOLIO', COUNT(*) FROM PORTFOLIO
UNION ALL
SELECT 'WATCHLIST', COUNT(*) FROM WATCHLIST
UNION ALL
SELECT 'PAYMENT',  COUNT(*) FROM PAYMENT;
```

---

## 주요 쿼리 테스트

```sql
-- 1. 로그인 테스트
SELECT member_id, member_email, member_status
  FROM MEMBER WHERE member_email = 'test@portwatch.com';

-- 2. 주식 목록 테스트
SELECT stock_code, stock_name, country, market, current_price
  FROM STOCK ORDER BY country, stock_code LIMIT 20;

-- 3. 뉴스 목록 테스트
SELECT news_id, title, stock_code, published_date
  FROM NEWS ORDER BY published_date DESC LIMIT 10;

-- 4. 비밀번호 업데이트 (test@test.com 비번 초기화)
-- 비밀번호: portwatch123
UPDATE MEMBER
   SET member_pass   = '$2a$10$0GBSb04xfIUriIM/5WwqhSuKgZHeltHn.4qFqnLIlojkISte/pSgSC',
       member_status = 'ACTIVE'
 WHERE member_email  = 'test@test.com';
```
