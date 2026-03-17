-- =====================================================================
--  PortWatch AWS RDS MySQL 8.4.7 - MEMBER 테이블 컬럼명 수정 SQL
--
--  [오류 분석 - English]
--  Screenshot shows actual DB columns:
--    password, name, email, phone, role  (short names)
--  But MemberVO and MemberMapper.xml expect:
--    member_pass, member_name, member_email, member_phone, member_role
--  → MISMATCH → login always fails with JSON error
--
--  [오류 분석 - 한글]
--  실제 DB 컬럼명: password, name, email, phone, role (짧은 이름)
--  코드에서 기대하는 컬럼명: member_pass, member_name, member_email ...
--  → 불일치 → 로그인 시 항상 JSON 오류 발생
--
--  [ALTER TABLE IF NOT EXISTS 오류 원인]
--  ERROR 1064: 'ADD COLUMN IF NOT EXISTS' 는 MariaDB 전용 문법!
--  MySQL 8.0 에서는 지원 안 됨 → 그냥 ADD COLUMN 사용
--
--  실행 방법 (PuTTY):
--  mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com
--        -u admin -p portwatch
--  mysql> SOURCE /tmp/alter_member.sql;
-- =====================================================================

USE portwatch;

-- 현재 상태 확인
SELECT 'Before ALTER:' AS status;
DESC MEMBER;

-- =====================================================================
--  Step 1: 컬럼명 변경 (RENAME COLUMN - MySQL 8.0 지원)
--  password   → member_pass
--  name       → member_name
--  email      → member_email
--  phone      → member_phone
--  role       → member_role
-- =====================================================================

ALTER TABLE MEMBER RENAME COLUMN password TO member_pass;
SELECT 'password → member_pass 완료' AS progress;

ALTER TABLE MEMBER RENAME COLUMN name TO member_name;
SELECT 'name → member_name 완료' AS progress;

ALTER TABLE MEMBER RENAME COLUMN email TO member_email;
SELECT 'email → member_email 완료' AS progress;

ALTER TABLE MEMBER RENAME COLUMN phone TO member_phone;
SELECT 'phone → member_phone 완료' AS progress;

ALTER TABLE MEMBER RENAME COLUMN role TO member_role;
SELECT 'role → member_role 완료' AS progress;

-- =====================================================================
--  Step 2: 누락된 컬럼 추가
--  ※ MySQL 8.0: ADD COLUMN IF NOT EXISTS 미지원 → 그냥 ADD COLUMN
--     이미 있으면 오류 발생 → 그 줄만 건너뛰면 됨
-- =====================================================================

ALTER TABLE MEMBER
    ADD COLUMN member_status  VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'   AFTER member_role,
    ADD COLUMN balance        DOUBLE       NOT NULL DEFAULT 1000000     AFTER member_status,
    ADD COLUMN member_address VARCHAR(255) NULL                         AFTER balance,
    ADD COLUMN member_gender  VARCHAR(10)  NULL                         AFTER member_address,
    ADD COLUMN member_birth   TIMESTAMP    NULL                         AFTER member_gender;

SELECT '누락 컬럼 추가 완료' AS progress;

-- =====================================================================
--  Step 3: 기존 데이터 업데이트 (이미 가입된 2명)
-- =====================================================================

UPDATE MEMBER
   SET member_status = 'ACTIVE',
       balance       = 1000000
 WHERE member_status IS NULL OR member_status = '';

SELECT '기존 데이터 업데이트 완료' AS progress;

-- =====================================================================
--  Step 4: 최종 결과 확인
-- =====================================================================

SELECT 'After ALTER - Final Structure:' AS status;
DESC MEMBER;

SELECT 'Member count:' AS info, COUNT(*) AS cnt FROM MEMBER;
SELECT member_id, member_email, member_name, member_role, member_status, balance FROM MEMBER;
