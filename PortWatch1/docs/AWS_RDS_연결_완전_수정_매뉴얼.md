# AWS RDS MySQL 연결 완전 수정 매뉴얼
> Spring 5.0.7 + HikariCP 3.4.5 + MySQL 8.4.7 + EC2 Ubuntu 24.04 + Tomcat 9

---

## 핵심 원인 진단 (스크린샷 분석 결과)

```
[스크린샷 확인된 오류]
cat: /opt/tomcat/bin/setenv.sh: Permission denied
→ HikariPool-1 - Connection is not available, request timed out after 30000ms
```

```
[오류 연결고리]
sudo bash -c 'echo ... > /opt/tomcat/bin/setenv.sh'
  └→ 파일 권한: 600 (root만 읽기 가능)
  └→ chmod +x 는 실행 권한만 추가, 읽기는 여전히 root 전용
  └→ Tomcat 서비스 (tomcat 유저)가 setenv.sh 읽지 못함
  └→ JAVA_OPTS=-Djava.net.preferIPv4Stack=true 미적용
  └→ JVM이 IPv6 DNS 우선 → RDS AAAA 레코드 없음 → 5-10초 지연
  └→ connectTimeout=10000ms 중 DNS에 5-10초 소진 → TCP 연결 실패
  └→ HikariCP 30초 대기 후 포기 → "timed out after 30000ms"
```

---

## 단계별 수정 (EC2 PuTTy에서 순서대로 실행)

### STEP 1: setenv.sh 올바르게 재생성

```bash
# ❌ 잘못된 방법 (권한 600 생성됨)
sudo bash -c 'echo "..." > /opt/tomcat/bin/setenv.sh'

# ✅ 올바른 방법 (tee 사용 — 644 권한으로 생성)
sudo tee /opt/tomcat/bin/setenv.sh << 'EOF'
#!/bin/bash
export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"
EOF

# 실행+읽기 권한 설정 (rwxr-xr-x)
sudo chmod 755 /opt/tomcat/bin/setenv.sh

# ✅ 이번엔 Permission denied 없이 출력돼야 함
cat /opt/tomcat/bin/setenv.sh
```

**예상 출력:**
```
#!/bin/bash
export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"
```

---

### STEP 2: tomcat.service 백업 확인 (setenv.sh 대안)

setenv.sh 방법이 불안정하면 systemd service 파일에 직접 설정:

```bash
sudo nano /etc/systemd/system/tomcat.service
```

`[Service]` 섹션에 아래 줄이 있는지 확인하고, 없으면 추가:

```ini
[Service]
Type=forking
User=tomcat
Group=tomcat

# ✅ 이 줄이 있어야 함 (없으면 추가)
Environment="JAVA_OPTS=-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"

ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh
```

저장 후:
```bash
sudo systemctl daemon-reload
```

---

### STEP 3: WAR 배포 확인

```bash
# ROOT.war 존재 확인
ls -la /opt/tomcat/webapps/ROOT.war

# 없으면 배포 (SCP로 전송된 war 파일 사용)
sudo cp /home/ubuntu/portwatch.war /opt/tomcat/webapps/ROOT.war
```

---

### STEP 4: Tomcat 재시작

```bash
sudo systemctl stop tomcat
# 잠깐 대기 (기존 프로세스 종료)
sleep 3
sudo systemctl start tomcat

# 시작 상태 확인
sudo systemctl status tomcat
```

**정상 출력:**
```
● tomcat.service - Apache Tomcat Web Application Container
   Active: active (running)
```

---

### STEP 5: IPv4 적용 확인

```bash
# Tomcat 프로세스에서 IPv4 옵션 확인
ps aux | grep java | grep -o 'preferIPv4Stack=true'
```

**정상 출력:** `preferIPv4Stack=true`

**출력 없으면:** setenv.sh 또는 tomcat.service 설정이 미적용 → STEP 1, 2 반복

---

### STEP 6: DB 연결 확인 (40초 후)

```bash
# Tomcat이 완전히 뜰 때까지 40초 대기 후 확인
sleep 40 && curl -s http://localhost:8080/debug/db-check | python3 -m json.tool
```

**성공 응답:**
```json
{
  "success": true,
  "dbConnected": true,
  "message": "DB 정상 ✅ ..."
}
```

**여전히 실패하면** STEP 7 진행.

---

### STEP 7: 로그에서 원인 확인

```bash
# HikariCP 관련 로그 확인
sudo tail -100 /opt/tomcat/logs/catalina.out | grep -i "hikari\|pool\|connection\|timeout"

# 최근 오류 확인
sudo tail -200 /opt/tomcat/logs/catalina.out | grep -i "error\|exception" | tail -30
```

---

## 보안 그룹 확인 (RDS 접근 불가 시)

### AWS 콘솔 확인 경로

```
AWS 콘솔 → RDS → portwatch-db-seoul → 연결 및 보안
→ VPC 보안 그룹 클릭
→ 인바운드 규칙 탭
→ 포트 3306, 소스: EC2 보안 그룹 ID 또는 172.31.0.0/16
```

### 보안 그룹 규칙 확인 (스크린샷 분석 결과)

현재 확인된 보안 그룹 구성 (**이미 정상**):

| 보안 그룹 | 방향 | 포트 | 소스/대상 |
|----------|------|------|----------|
| `default (sg-0f98b5a0de175b527)` | 인바운드 | 3306 | EC2 SG |
| `rds-ec2-2 (sg-0c239fc56a4698e4)` | 인바운드 | 3306 | EC2 SG |
| `ec2-rds-2 (sg-0c2e2fb5d74b409e4)` | 아웃바운드 | 3306 | RDS SG |

→ **보안 그룹은 이미 올바름. 추가 수정 불필요.**

### PuTTy에서 RDS 직접 연결 테스트

```bash
# MySQL 클라이언트로 직접 연결 테스트
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -P 3306 -u admin -p

# 비밀번호: portwatch2026!!abcd
```

**성공하면:** 보안 그룹 정상, Java/Tomcat 설정 문제
**실패하면:** 보안 그룹 또는 RDS 상태 확인 필요

---

## 테스트 계정 생성 (DB 연결 성공 후)

```bash
# EC2 PuTTy에서 MySQL 접속
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -P 3306 -u admin -p
```

MySQL 프롬프트에서:

```sql
USE portwatch;

-- 데이터베이스 없으면 먼저 생성
-- CREATE DATABASE IF NOT EXISTS portwatch CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 테이블 없으면 생성
CREATE TABLE IF NOT EXISTS MEMBER (
  member_id      VARCHAR(50)   NOT NULL,
  member_email   VARCHAR(100)  NOT NULL,
  member_pass    VARCHAR(200)  NOT NULL,
  member_name    VARCHAR(20)   NOT NULL,
  member_phone   VARCHAR(20)   DEFAULT NULL,
  member_address VARCHAR(255)  DEFAULT NULL,
  member_gender  VARCHAR(10)   DEFAULT NULL,
  member_birth   TIMESTAMP     DEFAULT NULL,
  member_role    VARCHAR(20)   DEFAULT 'USER',
  member_status  VARCHAR(20)   DEFAULT 'ACTIVE',
  balance        DOUBLE        DEFAULT 1000000,
  created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (member_id),
  UNIQUE KEY uk_member_email (member_email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
```

**로그인 정보:**
- 이메일: `test@portwatch.com` / 비밀번호: `portwatch123`
- 이메일: `admin@portwatch.com` / 비밀번호: `portwatch123`

---

## 크롤러 실행 (주식 데이터 채우기)

```bash
# ✅ 수정 후: 브라우저에서 GET으로도 접근 가능
# 한국 주식 크롤링
curl http://localhost:8080/crawler/korea

# 미국 주식 크롤링
curl http://localhost:8080/crawler/us

# 또는 브라우저에서:
# http://54.180.142.111:8080/crawler/korea
# http://54.180.142.111:8080/crawler/us
```

---

## 최종 확인 체크리스트

```bash
# 1. setenv.sh 확인 (Permission denied 없어야 함)
cat /opt/tomcat/bin/setenv.sh

# 2. IPv4 적용 확인
ps aux | grep java | grep -o 'preferIPv4Stack=true'

# 3. Tomcat 상태
sudo systemctl status tomcat | grep Active

# 4. DB 연결 확인
curl -s http://localhost:8080/debug/db-check | python3 -m json.tool

# 5. 로그인 페이지 접근 확인
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/member/login

# 6. 주식 목록 확인
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/stock/list
```

**모두 정상이면 예상 결과:**
```
1. #!/bin/bash\nexport JAVA_OPTS=...
2. preferIPv4Stack=true
3. Active: active (running)
4. {"success":true,"dbConnected":true,...}
5. 200
6. 200
```

---

## STS 로컬 개발환경 오류 (wtpwebapps)

```
Caused by: java.lang.IllegalArgumentException:
  [C:\workspace-sts\.metadata\...\wtpwebapps\portwatch1] is not a directory
```

**원인:** Maven Clean → target/ 삭제 → STS wtpwebapps 폴더도 삭제됨

**해결 순서 (반드시 이 순서로):**
```
① Project → Clean... → OK
② Servers 탭 → Tomcat → 우클릭 → Clean
③ Tomcat → 우클릭 → Publish
④ Tomcat → Start
```

> Maven Clean 실행 후 "Start"만 누르면 반드시 실패.
> Clean → Publish → Start 순서를 지킬 것.

---

## HikariCP 타임아웃 2단계 설계 (root-context.xml — 현재 올바름)

```xml
<!-- ✅ 현재 root-context.xml 설정 — 수정 불필요 -->
<property name="jdbcUrl"
    value="jdbc:mysql://portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306/portwatch
           ?useSSL=false&amp;sslMode=DISABLED
           &amp;serverTimezone=Asia/Seoul
           &amp;characterEncoding=UTF-8
           &amp;allowPublicKeyRetrieval=true
           &amp;connectTimeout=10000     ← TCP 연결 10초 제한 (JDBC 레벨)
           &amp;socketTimeout=60000"/>   ← 쿼리 실행 60초 제한

<property name="connectionTimeout"        value="30000"/>  ← 풀 대기 30초 (HikariCP 레벨)
<property name="initializationFailTimeout" value="-1"/>    ← 기동 시 DB 없어도 허용
<property name="maxLifetime"              value="180000"/> ← AWS NAT 4분 전에 갱신
<property name="idleTimeout"              value="120000"/> ← 유휴 연결 2분 후 반환
```

**타임아웃 계층 규칙:**
```
connectTimeout (10초)  <  connectionTimeout (30초)
DNS 해석 (0-10초)  +  TCP 연결 (0-10초)  <  connectionTimeout (30초)
→ IPv4 강제 시: DNS 0.1초 + TCP 0.3초 = 0.4초 → 30초 안에 충분히 성공
→ IPv6 시:      DNS 5-10초 + TCP 실패 → 30초 내 연결 불가 → 타임아웃
```
