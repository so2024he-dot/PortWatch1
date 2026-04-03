# AWS Aurora MySQL 전환 완전 매뉴얼
> Spring 5.0.7 + HikariCP 3.4.5 + EC2 Ubuntu 24.04 + Tomcat 9
> 기준: 2026-04-02 스크린샷 18장 분석 결과

---

## 1. 오류 2단계 진행 분석 (스크린샷 타임라인)

```
[12:27 KST] 1단계 오류: HikariPool-1 timed out after 30000ms
  원인: setenv.sh 권한 600 (root 전용) → Tomcat이 읽지 못함
        → -Djava.net.preferIPv4Stack=true 미적용
        → IPv6 DNS 5-10초 지연 → HikariCP 30초 소진

[12:41 KST] setenv.sh tee로 재생성 시도

[13:04 KST] Spring Context 초기화 성공! (수백 줄의 INFO 로그 정상)
  → RequestMappingHandlerMapping 정상
  → 모든 Controller Bean 로드 성공
  → 이 말은: setenv.sh 적용되어 IPv4 정상

[13:09 KST] 2단계 오류 등장 (새로운 오류):
  CommunicationsException: Communications link failure
  ConnectException: Connection timed out
  → HikariPool 풀 타임아웃이 아닌 TCP 자체 연결 거부/불가
  → 구 MySQL 엔드포인트가 삭제되거나 새 Aurora와 보안그룹 불일치
```

### 오류 유형 비교

| 항목 | 1단계 오류 | 2단계 오류 |
|------|-----------|-----------|
| 예외 클래스 | `SQLTransientConnectionException` | `CommunicationsException` |
| 메시지 | `request timed out after 30000ms` | `Communications link failure` |
| 원인 | HikariCP 풀 레벨 타임아웃 | TCP 소켓 연결 자체 실패 |
| 계층 | 풀(Pool) 레벨 | 네트워크(TCP) 레벨 |
| 근본 원인 | IPv6 DNS 지연 | 엔드포인트 불일치 / 보안그룹 |
| setenv.sh | 적용 안 됨 | **적용됨** (Spring 로드 성공) |

---

## 2. 현재 상태 (2026-04-02 13:11 기준)

```
✅ setenv.sh: tee로 재생성 완료 (IPv4 적용됨)
✅ Spring Context: 초기화 성공 (모든 Controller, Mapper Bean 로드)
✅ Tomcat 9.0.98: 정상 기동
❌ DB 연결: Communications link failure (엔드포인트 불일치)
❌ STS 로컬: wtpwebapps 오류 (12:52 INI 로그에도 동일 오류)
```

---

## 3. Aurora 엔드포인트 확인 (AWS 콘솔)

### 3-1. Writer 엔드포인트 찾기

```
AWS 콘솔 로그인
→ 서비스 → RDS
→ 왼쪽 메뉴: 데이터베이스
→ 클러스터 목록에서 portwatch 관련 클러스터 클릭
→ [연결 및 보안] 탭 클릭
→ 엔드포인트 섹션 확인
```

| 엔드포인트 유형 | 용도 | root-context.xml 사용 여부 |
|----------------|------|--------------------------|
| **쓰기 (Writer)** | INSERT/UPDATE/DELETE | **이걸 사용** |
| 읽기 (Reader) | SELECT 전용 | 고성능 시 별도 설정 |
| 사용자 지정 | 특정 인스턴스 | 불필요 |

**Writer 엔드포인트 형식:**
```
[클러스터명].cluster-[랜덤ID].ap-northeast-2.rds.amazonaws.com
예: portwatch-cluster.cluster-abc123def.ap-northeast-2.rds.amazonaws.com
```

### 3-2. PuTTy에서 직접 연결 테스트

```bash
# Aurora 엔드포인트로 직접 MySQL 연결 테스트
# (AWS 콘솔에서 확인한 Writer 엔드포인트로 교체)
mysql -h [Writer-엔드포인트] -P 3306 -u admin -p

# 비밀번호 입력: portwatch2026!!abcd

# 연결 성공 시:
mysql> SHOW DATABASES;
mysql> USE portwatch;
mysql> SHOW TABLES;
```

---

## 4. root-context.xml 수정 (엔드포인트 교체)

AWS 콘솔에서 Writer 엔드포인트 확인 후 아래와 같이 수정:

```xml
<!-- ⚠️ [여기를 수정] Aurora Writer 엔드포인트로 교체 -->
<property name="jdbcUrl"
    value="jdbc:mysql://portwatch-db-seoul.cpggwqmigoo3.ap-northeast-2.rds.amazonaws.com:3306/portwatch
           ?useSSL=false
           &amp;sslMode=DISABLED
           &amp;serverTimezone=Asia/Seoul
           &amp;characterEncoding=UTF-8
           &amp;allowPublicKeyRetrieval=true
           &amp;connectTimeout=5000
           &amp;socketTimeout=60000
           &amp;autoReconnect=true
           &amp;failOverReadOnly=false"/>
```

### STS에서 수정하는 방법

```
1. STS 프로젝트 → src/main/webapp/WEB-INF/spring/root-context.xml 열기
2. jdbcUrl 속성의 호스트명 부분만 교체:
   [기존] portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com
   [신규] [AWS콘솔에서 복사한 Aurora Writer 엔드포인트]
3. 저장 (Ctrl+S)
```

---

## 5. Aurora 보안 그룹 설정 (필수 확인)

### 5-1. Aurora 클러스터에 EC2 보안 그룹 연결 확인

```
AWS 콘솔 → RDS → [Aurora 클러스터] → 연결 및 보안 탭
→ VPC 보안 그룹 섹션 확인
→ ec2-rds-2 또는 EC2 인스턴스의 보안 그룹이 포함되어 있어야 함
```

**포함되지 않았다면:**
```
① RDS → [Aurora 클러스터] → 수정 버튼
② 연결 섹션 → VPC 보안 그룹 → ec2-rds-2 추가
③ 즉시 적용 선택 → 수정 저장
```

### 5-2. Aurora 보안 그룹 인바운드 규칙 추가

```
EC2 콘솔 → 보안 그룹 → Aurora가 사용하는 보안 그룹 선택
→ 인바운드 규칙 탭 → 인바운드 규칙 편집
→ 규칙 추가:
  유형: MySQL/Aurora
  프로토콜: TCP
  포트 범위: 3306
  소스: EC2 인스턴스의 보안 그룹 ID (또는 172.31.0.0/16)
→ 저장
```

### 5-3. PuTTy에서 보안 그룹 테스트

```bash
# nc (netcat)으로 Aurora 포트 3306 연결 가능한지 테스트
nc -zv portwatch-db-seoul.cpggwqmigoo3.ap-northeast-2.rds.amazonaws.com 3306

# 성공 시 출력:
# Connection to [호스트] 3306 port [tcp/mysql] succeeded!

# 실패 시 출력:
# nc: connect to [호스트] port 3306 (tcp) failed: Connection timed out
# → 보안 그룹 문제
```

---

## 6. Aurora 데이터베이스 및 테이블 초기화

Aurora에 연결 성공 후 portwatch DB와 테이블이 없으면 생성:

```sql
-- MySQL/PuTTy에서 실행
CREATE DATABASE IF NOT EXISTS portwatch
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE portwatch;

-- MEMBER 테이블
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

-- STOCK 테이블
CREATE TABLE IF NOT EXISTS STOCK (
  stock_id       INT           NOT NULL AUTO_INCREMENT,
  stock_code     VARCHAR(20)   NOT NULL,
  stock_name     VARCHAR(100)  NOT NULL,
  stock_price    DOUBLE        DEFAULT 0,
  stock_change   DOUBLE        DEFAULT 0,
  stock_volume   BIGINT        DEFAULT 0,
  stock_market   VARCHAR(20)   DEFAULT 'KR',
  updated_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (stock_id),
  UNIQUE KEY uk_stock_code_market (stock_code, stock_market)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- NEWS 테이블
CREATE TABLE IF NOT EXISTS NEWS (
  news_id        INT           NOT NULL AUTO_INCREMENT,
  news_title     VARCHAR(500)  NOT NULL,
  news_url       VARCHAR(1000) DEFAULT NULL,
  news_source    VARCHAR(100)  DEFAULT NULL,
  news_category  VARCHAR(50)   DEFAULT 'KR',
  published_at   TIMESTAMP     NULL DEFAULT NULL,
  created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (news_id)
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

-- 확인
SELECT member_id, member_email, member_role, member_status FROM MEMBER;
SHOW TABLES;
```

---

## 7. 전체 수정 후 EC2 재배포 절차

### 7-1. STS에서 빌드 (wtpwebapps 오류 해결 포함)

```
[STS 실행 순서]
① root-context.xml의 jdbcUrl → Aurora 엔드포인트로 교체
② STS 상단 메뉴: Project → Clean... → OK
③ Maven: Run As → Maven install
   → target/portwatch-1.0.0-BUILD-SNAPSHOT.war 생성됨
④ Servers 탭 → Tomcat → 우클릭 → Clean
⑤ Tomcat → 우클릭 → Publish
⑥ Tomcat → Start (로컬 테스트용)
```

### 7-2. WAR를 EC2로 전송 (WinSCP 또는 SCP)

**WinSCP 방법:**
```
호스트: 54.180.142.111
사용자: ubuntu
개인키: .pem 파일 선택
로컬: C:\Users\소현석\git\PortWatch1\PortWatch1\target\portwatch-1.0.0-BUILD-SNAPSHOT.war
원격: /home/ubuntu/portwatch.war
```

**명령어 방법:**
```bash
scp -i "portwatch-key.pem" \
    target/portwatch-1.0.0-BUILD-SNAPSHOT.war \
    ubuntu@54.180.142.111:/home/ubuntu/portwatch.war
```

### 7-3. EC2 PuTTy에서 배포

```bash
# 1. setenv.sh 최종 확인 (Permission denied 없어야 함)
cat /opt/tomcat/bin/setenv.sh
# 예상 출력: #!/bin/bash \n export JAVA_OPTS="-Djava.net.preferIPv4Stack=true..."

# 2. Tomcat 정지
sudo systemctl stop tomcat

# 3. WAR 배포 (ROOT.war로 복사)
sudo cp /home/ubuntu/portwatch.war /opt/tomcat/webapps/ROOT.war
# ⚠️ 절대 ROOT.war 삭제 금지! 삭제하면 Tomcat이 ROOT/ 폴더도 삭제 → 전체 404

# 4. Tomcat 시작
sudo systemctl start tomcat

# 5. IPv4 적용 확인
ps aux | grep java | grep -o 'preferIPv4Stack=true'
# 예상 출력: preferIPv4Stack=true

# 6. 40초 후 DB 연결 확인
sleep 40 && curl -s http://localhost:8080/debug/db-check | python3 -m json.tool
```

**DB 연결 성공 시:**
```json
{
  "success": true,
  "dbConnected": true,
  "message": "DB 정상 ✅ ..."
}
```

### 7-4. 주식/뉴스 데이터 크롤링

```bash
# 한국 주식 크롤링
curl http://localhost:8080/crawler/korea

# 미국 주식 크롤링
curl http://localhost:8080/crawler/us

# 또는 브라우저에서:
# http://54.180.142.111:8080/crawler/korea
# http://54.180.142.111:8080/crawler/us
```

---

## 8. setenv.sh 올바른 생성 방법 (재확인)

```bash
# ❌ 잘못된 방법 (권한 600, Permission denied 발생)
sudo bash -c 'echo "#!/bin/bash
export JAVA_OPTS=..." > /opt/tomcat/bin/setenv.sh'

# ✅ 올바른 방법 1: tee 사용 (heredoc)
sudo tee /opt/tomcat/bin/setenv.sh << 'EOF'
#!/bin/bash
export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"
EOF
sudo chmod 755 /opt/tomcat/bin/setenv.sh

# ✅ 올바른 방법 2: tomcat.service 직접 편집
sudo nano /etc/systemd/system/tomcat.service
# [Service] 섹션에 추가:
# Environment="JAVA_OPTS=-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"
sudo systemctl daemon-reload

# 확인 (Permission denied 없어야 함)
cat /opt/tomcat/bin/setenv.sh
```

---

## 9. STS 로컬 wtpwebapps 오류 해결

**INI 로그 확인된 오류 (12:52:56 KST):**
```
wtpwebapps\portwatch1 is not a directory or war file, or is not readable
```

**원인:** Maven Clean 실행 → target/ 삭제 → wtpwebapps 폴더도 사라짐

**해결 (반드시 이 순서):**
```
① Project → Clean... → OK
② Servers 탭 → Tomcat → 우클릭 → Clean
③ Tomcat → 우클릭 → Publish
④ Tomcat → Start
```

> Maven Clean 후 바로 Start하면 반드시 실패.

---

## 10. 최종 확인 체크리스트

```bash
# EC2에서 순서대로 실행
echo "=== 1. setenv.sh 확인 ===" && cat /opt/tomcat/bin/setenv.sh
echo "=== 2. IPv4 적용 확인 ===" && ps aux | grep java | grep -o 'preferIPv4Stack=true'
echo "=== 3. Tomcat 상태 ===" && sudo systemctl status tomcat | grep Active
echo "=== 4. Aurora 포트 연결 테스트 ===" && nc -zv [Aurora-엔드포인트] 3306
echo "=== 5. DB 연결 확인 ===" && curl -s http://localhost:8080/debug/db-check | python3 -m json.tool
echo "=== 6. 앱 응답 확인 ===" && curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/
```

---

## 11. 전체 문제-원인-해결 요약

| 증상 | 원인 | 해결책 |
|------|------|--------|
| `HikariPool timed out 30000ms` | setenv.sh 권한 600 → IPv4 미적용 | `tee` + `chmod 755` |
| `Communications link failure` + `Connection timed out` | Aurora 전환 후 구 MySQL 엔드포인트 사용 | root-context.xml jdbcUrl 교체 |
| `wtpwebapps\portwatch1 not found` | Maven Clean 후 재배포 없이 Start | Project Clean → Server Clean → Publish → Start |
| `GET /crawler/korea → 405` | `@PostMapping`만 존재 | `@RequestMapping(GET+POST)` 수정 완료 ✅ |
| `Permission denied` (setenv.sh) | `bash -c 'echo >'` 방식이 root 전용 파일 생성 | `sudo tee` 방식으로 교체 |
