# EC2 + RDS MySQL 연결 최종 해결 매뉴얼
> 2026-04-02 스크린샷 8장 + INI 파일 분석 결과 기준

---

## 1. 전체 현황 (현재 상태)

| 항목 | 상태 | 비고 |
|------|------|------|
| RDS MySQL 8.4.3 | ✅ 정상 실행 중 | 사용 가능, MySQL Community |
| RDS 엔드포인트 | ✅ 정상 | portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2 |
| EC2→RDS MySQL 클라이언트 연결 | ✅ 성공 | 스크린샷 134437 확인 |
| portwatch 데이터베이스 | ✅ 존재 | SHOW DATABASES 확인 |
| 보안그룹 포트 3306 | ✅ 정상 | rds-ec2-2 그룹 설정 완료 |
| setenv.sh (IPv4) | ⚠️ 재확인 필요 | tee로 재생성했으나 검증 필요 |
| root-context.xml jdbcUrl | ✅ **방금 수정 완료** | 버그 2종 수정 |
| EC2 WAR 배포 | ❌ 구버전 | 새 WAR 재배포 필요 |
| STS 로컬 Tomcat | ❌ 오류 4회 반복 | wtpwebapps 문제 미해결 |
| Java/Tomcat DB 연결 | ❌ 실패 | 새 WAR 배포 후 해결 예상 |

---

## 2. root-context.xml 버그 수정 내용 (이번에 수정 완료)

### 버그 1: jdbcUrl 다중 줄 작성 오류 (치명적)

```xml
<!-- ❌ 잘못된 방식 — XML 속성값에 개행이 포함됨 -->
<property name="jdbcUrl"
    value="jdbc:mysql://...amazonaws.com:3306/portwatch
           ?useSSL=false
           &amp;sslMode=DISABLED"/>

<!-- XML 파서 해석 결과: -->
<!-- "jdbc:mysql://...amazonaws.com:3306/portwatch\n           ?useSSL=false\n           ..." -->
<!-- 개행과 공백이 URL에 포함 → MySQL 드라이버 파싱 오류 → Communications link failure! -->

<!-- ✅ 올바른 방식 — 한 줄로 작성 -->
<property name="jdbcUrl" value="jdbc:mysql://...amazonaws.com:3306/portwatch?useSSL=false&amp;sslMode=DISABLED&amp;..."/>
```

### 버그 2: 호스트명 오타 (치명적)

```
❌ 잘못됨: cpggwqmigoo3  (알파벳 소문자 i)
✅ 올바름: cpggwqm1goo3  (숫자 1)

→ 존재하지 않는 호스트로 연결 시도
→ DNS 조회 실패 또는 Connection timed out
→ Communications link failure: Connection timed out
```

### 수정된 jdbcUrl (최종)

```xml
<property name="jdbcUrl" value="jdbc:mysql://portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306/portwatch?useSSL=false&amp;sslMode=DISABLED&amp;serverTimezone=Asia/Seoul&amp;characterEncoding=UTF-8&amp;allowPublicKeyRetrieval=true&amp;connectTimeout=5000&amp;socketTimeout=60000&amp;autoReconnect=true"/>
```

---

## 3. 지금 바로 실행할 순서

### Step 1: STS에서 Maven 강제 재컴파일 + WAR 빌드

```
[INI 확인] 13:42:17 Maven install → "Nothing to compile - all classes are up to date"
→ Java 파일은 재컴파일 안 됨, but resources (root-context.xml) 는 복사됨
→ 방금 root-context.xml을 수정했으므로 Maven install 다시 실행 필요

① STS 상단: Project → Clean... → OK (강제 재컴파일 준비)
② Run As → Maven install
   → "Compiling X source files" 또는 "Copying 15 resources" 확인
   → BUILD SUCCESS 확인
   → target/portwatch-1.0.0-BUILD-SNAPSHOT.war 생성됨
```

### Step 2: STS 로컬 Tomcat 오류 해결 (wtpwebapps)

```
[INI 1:48:51 PM — 4번째 동일 오류]
wtpwebapps\portwatch1 is not a directory or war file

해결 순서 (반드시 이 순서로!):
① Servers 탭 → Tomcat → 우클릭 → Clean
② Tomcat → 우클릭 → Publish
③ Tomcat → Start

또는 Start 버튼 클릭 전에:
① Project → Clean
② Servers → Tomcat → Clean
③ Tomcat → Publish → Start
```

### Step 3: EC2에 새 WAR 전송 (WinSCP)

```
[WinSCP 설정]
호스트: 54.180.142.111
포트: 22
사용자: ubuntu
인증: .pem 개인키 파일

[전송]
로컬: C:\Users\소현석\git\PortWatch1\PortWatch1\target\portwatch-1.0.0-BUILD-SNAPSHOT.war
원격: /home/ubuntu/portwatch.war
```

### Step 4: EC2 PuTTy에서 setenv.sh 확인 및 재생성

```bash
# 현재 setenv.sh 상태 확인
cat /opt/tomcat/bin/setenv.sh

# Permission denied가 나오면 아래 명령으로 재생성:
sudo tee /opt/tomcat/bin/setenv.sh << 'SETENV'
#!/bin/bash
export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"
SETENV

sudo chmod 755 /opt/tomcat/bin/setenv.sh

# 확인 (Permission denied 없이 내용이 보여야 함)
cat /opt/tomcat/bin/setenv.sh
```

### Step 5: EC2에 WAR 배포 및 Tomcat 재시작

```bash
# Tomcat 정지
sudo systemctl stop tomcat

# WAR 배포 (ROOT.war로 복사)
sudo cp /home/ubuntu/portwatch.war /opt/tomcat/webapps/ROOT.war

# 기존 압축 해제 폴더 삭제 (깨끗한 재배포)
sudo rm -rf /opt/tomcat/webapps/ROOT/

# Tomcat 시작
sudo systemctl start tomcat

# IPv4 적용 확인 (preferIPv4Stack=true 출력돼야 함)
ps aux | grep java | grep -o 'preferIPv4Stack=true'
```

### Step 6: 40초 후 DB 연결 확인

```bash
sleep 40 && curl -s http://localhost:8080/debug/db-check | python3 -m json.tool
```

**성공 시 출력:**
```json
{
  "success": true,
  "dbConnected": true,
  "memberFound": false,
  "message": "DB 정상 ✅ (admin@portwatch.com 없음 - 회원가입 필요)"
}
```

### Step 7: 테스트 계정 삽입 (DB 연결 성공 후)

```bash
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -P 3306 -u admin -p
```

```sql
USE portwatch;

-- 테이블 확인
SHOW TABLES;

-- 없으면 생성
CREATE TABLE IF NOT EXISTS MEMBER (
  member_id      VARCHAR(50)   NOT NULL,
  member_email   VARCHAR(100)  NOT NULL UNIQUE,
  member_pass    VARCHAR(200)  NOT NULL,
  member_name    VARCHAR(20)   NOT NULL,
  member_phone   VARCHAR(20)   DEFAULT NULL,
  member_address VARCHAR(255)  DEFAULT NULL,
  member_gender  VARCHAR(10)   DEFAULT NULL,
  member_birth   TIMESTAMP     NULL DEFAULT NULL,
  member_role    VARCHAR(20)   NOT NULL DEFAULT 'USER',
  member_status  VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
  balance        DOUBLE        NOT NULL DEFAULT 1000000,
  created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 테스트 계정 삽입 (비밀번호: portwatch123)
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

SELECT member_id, member_email, member_role, member_status FROM MEMBER;
```

### Step 8: 주식 데이터 크롤링 (데이터 채우기)

```bash
# 한국 주식 크롤링 (브라우저 또는 curl)
curl http://localhost:8080/crawler/korea

# 미국 주식 크롤링
curl http://localhost:8080/crawler/us
```

---

## 4. 오류 원인 전체 타임라인 (2026-04-02)

```
[10:29] STS Tomcat 시작 → wtpwebapps 오류 (1번째)
[10:38] Maven install 성공 (84 파일 컴파일)
[12:27] EC2 첫 배포 → setenv.sh 권한 600 → IPv4 미적용 → HikariPool 30초 타임아웃
[12:41] setenv.sh tee로 재생성 시도 (헤더 /bin/bash 오타 가능성)
[12:52] STS Tomcat 시작 → wtpwebapps 오류 (2번째)
[13:04] EC2 Tomcat 재시작 → Spring Context 초기화 성공! (IPv4 적용됨)
[13:09] DB 연결 실패 → Communications link failure (jdbcUrl 버그 2종)
[13:33] AWS 콘솔 확인 → MySQL 8.4.3, 정상 실행 중 확인
[13:34] EC2 MySQL 클라이언트 직접 연결 성공! (portwatch DB 확인)
[13:42] Maven install 성공 (WAR 빌드, resources 복사)
[13:48] STS Tomcat 시작 → wtpwebapps 오류 (3번째)  ← 여전히 미해결
[현재]  root-context.xml jdbcUrl 버그 2종 수정 완료 → 재빌드 + 재배포 필요
```

---

## 5. 현재까지 수정된 코드 목록

| 파일 | 수정 내용 | 상태 |
|------|----------|------|
| `root-context.xml` | jdbcUrl 버그 2종 수정 (개행, 오타) + connectTimeout=5000 | ✅ 완료 |
| `SessionDebugController.java` | db-check 진단 메시지 정확화 | ✅ 완료 |
| `StockCrawlerController.java` | /korea, /us, /all → GET+POST 허용 | ✅ 완료 |
| `NewsController.java` | required=false + null 체크 | ✅ 완료 |
| **EC2 setenv.sh** | tee + chmod 755 필요 | ⚠️ 재확인 |
| **EC2 ROOT.war** | 신규 WAR 재배포 필요 | ❌ 미완 |
| **STS wtpwebapps** | Clean→Publish→Start 필요 | ❌ 미완 |
| **DB MEMBER 테이블** | 테스트 계정 INSERT 필요 | ❌ 미완 |
