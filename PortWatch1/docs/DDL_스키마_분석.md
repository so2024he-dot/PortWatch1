# PortWatch DB 스키마 (DDL) 완전 분석
> 분석 기준: PuTTy MySQL SHOW CREATE TABLE + 소스코드 매핑 확인
> DB: AWS RDS MySQL 8.4.7 / portwatch

---

## 1. MEMBER 테이블

### 1-1. 실제 DDL (PuTTy SHOW CREATE TABLE 기반)

```sql
CREATE TABLE `MEMBER` (
  `member_id`      VARCHAR(50)   NOT NULL,
  `member_email`   VARCHAR(100)  NOT NULL,
  `member_pass`    VARCHAR(200)  NOT NULL,
  `member_name`    VARCHAR(20)   NOT NULL,
  `member_phone`   VARCHAR(20)   DEFAULT NULL,
  `member_address` VARCHAR(255)  DEFAULT NULL,
  `member_gender`  VARCHAR(10)   DEFAULT NULL,
  `member_birth`   TIMESTAMP     DEFAULT NULL,
  `member_role`    VARCHAR(20)   DEFAULT 'USER',
  `member_status`  VARCHAR(20)   DEFAULT 'ACTIVE',
  `balance`        DOUBLE        DEFAULT 1000000,
  `created_at`     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
  `updated_at`     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `uk_member_email` (`member_email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 1-2. Java MemberVO 매핑 확인

| DB 컬럼 | Java 필드 | 타입 | 상태 |
|---------|-----------|------|------|
| `member_id` | `memberId` | String | ✅ |
| `member_email` | `memberEmail` | String | ✅ |
| `member_pass` | `memberPass` | String | ✅ |
| `member_name` | `memberName` | String | ✅ |
| `member_phone` | `memberPhone` | String | ✅ |
| `member_address` | `memberAddress` | String | ✅ |
| `member_gender` | `memberGender` | String | ✅ |
| `member_birth` | `memberBirth` | Timestamp | ✅ |
| `member_role` | `memberRole` | String | ✅ |
| `member_status` | `memberStatus` | String | ✅ |
| `balance` | `balance` | double | ✅ |
| `created_at` | `createdAt` | Timestamp | ✅ |
| `updated_at` | `updatedAt` | Timestamp | ✅ |

✅ **모든 컬럼 매핑 정상**

### 1-3. 테스트 계정 INSERT (즉시 실행 가능)

```sql
USE portwatch;

-- 테스트 계정 삽입 (이미 있으면 무시)
INSERT IGNORE INTO MEMBER (
    member_id, member_email, member_pass, member_name,
    member_role, member_status, balance
) VALUES
-- 일반 사용자 (비밀번호: portwatch123)
('testuser01', 'test@portwatch.com',
 '$2a$10$0GBSb04xfIUriIM/5WwqhSuKgZHeltHn.4qFqnLIlojkISte/pSgSC',
 '테스트유저', 'USER', 'ACTIVE', 1000000),

-- 관리자 (비밀번호: portwatch123)
('adminuser01', 'admin@portwatch.com',
 '$2a$10$0GBSb04xfIUriIM/5WwqhSuKgZHeltHn.4qFqnLIlojkISte/pSgSC',
 '관리자', 'ADMIN', 'ACTIVE', 5000000);

-- 기존 계정 비밀번호 강제 업데이트 (portwatch123)
UPDATE MEMBER
SET member_pass = '$2a$10$0GBSb04xfIUriIM/5WwqhSuKgZHeltHn.4qFqnLIlojkISte/pSgSC',
    member_status = 'ACTIVE'
WHERE member_email IN ('test@portwatch.com', 'admin@portwatch.com');

-- 확인
SELECT member_id, member_email, member_name, member_role,
       member_status, balance FROM MEMBER;
```

**로그인 정보**:
- 이메일: `test@portwatch.com` / 비밀번호: `portwatch123`
- 이메일: `admin@portwatch.com` / 비밀번호: `portwatch123`

> ⚠️ 비밀번호 해시가 바뀌었을 경우 EC2에서 직접 확인:
> ```
> http://54.180.142.111:8080/debug/bcrypt?pass=portwatch123
> ```
> 결과의 `hash` 값을 UPDATE에 사용

---

## 2. STOCK 테이블

### 2-1. DDL

```sql
CREATE TABLE `STOCK` (
  `stock_code`    VARCHAR(20)    NOT NULL,
  `stock_name`    VARCHAR(100)   NOT NULL,
  `country`       VARCHAR(10)    DEFAULT 'KR',
  `market`        VARCHAR(20)    DEFAULT NULL,
  `market_type`   VARCHAR(20)    DEFAULT NULL,
  `industry`      VARCHAR(50)    DEFAULT NULL,
  `current_price` DECIMAL(18,4)  DEFAULT 0.0000,
  `price_change`  DECIMAL(18,4)  DEFAULT 0.0000,
  `change_rate`   DECIMAL(8,4)   DEFAULT 0.0000,
  `volume`        BIGINT         DEFAULT 0,
  `market_cap`    BIGINT         DEFAULT 0,
  `updated_at`    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_at`    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`stock_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 2-2. 데이터 채우기 (크롤링 실행)

DB 연결 성공 후:
```bash
# 한국 주식 TOP100 크롤링 (수동)
curl -X POST http://54.180.142.111:8080/crawler/korea

# 미국 주식 크롤링 (수동)
curl -X POST http://54.180.142.111:8080/crawler/us

# 전체 업데이트
curl -X GET "http://54.180.142.111:8080/api/admin/update-all"
```

---

## 3. NEWS 테이블

### 3-1. DDL

```sql
CREATE TABLE `NEWS` (
  `news_id`      BIGINT        NOT NULL AUTO_INCREMENT,
  `title`        VARCHAR(500)  NOT NULL,
  `content`      TEXT          DEFAULT NULL,
  `url`          VARCHAR(1000) DEFAULT NULL,
  `source`       VARCHAR(100)  DEFAULT NULL,
  `category`     VARCHAR(50)   DEFAULT NULL,
  `stock_code`   VARCHAR(20)   DEFAULT NULL,
  `published_at` TIMESTAMP     DEFAULT NULL,
  `created_at`   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`news_id`),
  INDEX `idx_stock_code` (`stock_code`),
  INDEX `idx_published_at` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 3-2. 뉴스 크롤링 확인

```bash
# 뉴스 크롤링 수동 트리거
curl -X GET "http://54.180.142.111:8080/api/admin/update-all-news"

# 뉴스 개수 확인
curl "http://54.180.142.111:8080/api/news/stats"
```

---

## 4. 전체 스키마 검증 쿼리

```sql
USE portwatch;

-- 테이블 목록
SHOW TABLES;

-- 각 테이블 행 수 확인
SELECT 'MEMBER' as tbl, COUNT(*) as cnt FROM MEMBER
UNION ALL
SELECT 'STOCK',  COUNT(*) FROM STOCK
UNION ALL
SELECT 'NEWS',   COUNT(*) FROM NEWS;

-- MEMBER 컬럼 확인
DESCRIBE MEMBER;

-- STOCK 컬럼 확인
DESCRIBE STOCK;
```

---

## 5. 발견된 스키마 이슈

| 이슈 | 내용 | 해결 |
|------|------|------|
| `MEMBER.member_status` 없는 경우 | 구버전 테이블에 해당 컬럼 없음 | `ALTER TABLE MEMBER ADD COLUMN member_status VARCHAR(20) DEFAULT 'ACTIVE'` |
| `MEMBER.balance` 없는 경우 | 구버전 테이블에 balance 컬럼 없음 | `ALTER TABLE MEMBER ADD COLUMN balance DOUBLE DEFAULT 1000000` |
| `MemberMapper.xml` findByEmail에 `AND member_status = 'ACTIVE'` 조건 | status가 ACTIVE가 아니면 로그인 불가 | INSERT 시 `member_status='ACTIVE'` 필수 |

### 긴급 스키마 수정 (컬럼 없는 경우)

```sql
USE portwatch;

-- member_status 컬럼 추가 (없으면)
ALTER TABLE MEMBER
  ADD COLUMN IF NOT EXISTS member_status VARCHAR(20) DEFAULT 'ACTIVE';

-- balance 컬럼 추가 (없으면)
ALTER TABLE MEMBER
  ADD COLUMN IF NOT EXISTS balance DOUBLE DEFAULT 1000000;

-- member_role 컬럼 추가 (없으면)
ALTER TABLE MEMBER
  ADD COLUMN IF NOT EXISTS member_role VARCHAR(20) DEFAULT 'USER';

-- 기존 회원 status 일괄 ACTIVE 설정
UPDATE MEMBER SET member_status = 'ACTIVE' WHERE member_status IS NULL OR member_status = '';
UPDATE MEMBER SET member_role = 'USER' WHERE member_role IS NULL OR member_role = '';
```
