# AWS RDS 네트워크 스크린샷 분석 리포트
> 분석 기준: 2026-04-02 스크린샷 13장 (11:29:12 ~ 11:53:07)

---

## 1. 전체 결론 (가장 중요)

```
✅ DB 연결 정상 작동 확인!
   http://54.180.142.111:8080/api/admin/update-all
   → {"duration":"60초","success":true,"message":"전체 주식 현재가 업데이트 완료"}

✅ AWS 보안 그룹 설정 정상!
   EC2→RDS 포트 3306 인바운드/아웃바운드 모두 올바르게 구성됨

❌ 크롤러 405 오류 → 코드 수정 완료 (이 문서 하단 참고)
```

---

## 2. 스크린샷별 분석

| 시간 | 화면 | 발견 내용 |
|------|------|----------|
| 11:29:12 | PortWatch 로그인 | 로그인 오류 표시 (DB 연결 문제였던 것) |
| 11:40:55 | RDS 대시보드 | portwatch-db-seoul - 상태: **사용 가능** ✅ |
| 11:41:17 | RDS 연결 및 보안 | 엔드포인트/포트 3306 정상, VPC 보안그룹 연결됨 |
| 11:41:33 | 보안 그룹 규칙 | EC2 연결된 컴퓨팅 리소스(1개) + 보안그룹 2개 ✅ |
| 11:42:36 | VPC 대시보드 | vpc-09bdfc35952dfe978, CIDR 172.31.0.0/16 ✅ |
| 11:42:55 | VPC 서브넷 | ap-northeast-2a/b/c/d 4개 서브넷 ✅ |
| 11:43:34 | DHCP 옵션 세트 | AmazonProvidedDNS 사용 (정상) |
| 11:45:09 | EC2 보안그룹 | ec2-rds-2: 아웃바운드 TCP 3306 → RDS SG ✅ |
| 11:45:47 | 네트워크 인터페이스 | EC2 내부 IP: 172.31.45.x, VPC 연결 정상 |
| 11:46:05 | 네트워크 인터페이스 상세 | Private DNS: ip-172-31-45-2.ap-northeast-2.compute.internal |
| 11:48:25 | 브라우저 | `GET /crawler/korea` → **405 오류** ❌ |
| 11:48:47 | 브라우저 | `GET /crawler/us` → **405 오류** ❌ |
| 11:53:07 | 브라우저 | `GET /api/admin/update-all` → **success: true** ✅ |

---

## 3. AWS 네트워크 구성 분석 (정상 판정)

### 3-1. VPC 구성

```
VPC: vpc-09bdfc35952dfe978
CIDR: 172.31.0.0/16
리전: ap-northeast-2 (서울)
서브넷: ap-northeast-2a, 2b, 2c, 2d (4개)
```

### 3-2. 보안 그룹 구성 (AWS 자동 설정 — 정상)

```
[RDS 보안 그룹]
┌─────────────────────────────────────────────────────────┐
│ sg-0f98b5a0de175b527 (default)                          │
│   인바운드: EC2 Security Group (sg-*) TCP 3306 허용      │
│                                                         │
│ sg-0c239fc56a4698e4* (rds-ec2-2)                        │
│   인바운드: EC2 Security Group TCP 3306 허용             │
└─────────────────────────────────────────────────────────┘

[EC2 보안 그룹]
┌─────────────────────────────────────────────────────────┐
│ sg-0c2e2fb5d74b409e4 (ec2-rds-2)                        │
│ 설명: "Security group attached to instances to securely  │
│        connect to portwatch-db-seoul"                   │
│   아웃바운드: TCP 3306 → sg-0143be8a5aa7110 (MySQLAurora) │
└─────────────────────────────────────────────────────────┘
```

AWS의 **"EC2 인스턴스 연결"** 자동 설정 기능이 정상 적용되어 있음 ✅

### 3-3. 판정: 보안 그룹 추가 작업 불필요

AWS RDS 콘솔에서 EC2를 연결할 때 자동 생성된 `ec2-rds-2` 보안 그룹이 이미
양방향 포트 3306 통신을 허용하고 있습니다. **별도 수정 필요 없음.**

---

## 4. 발견된 코드 오류: 크롤러 405

### 원인

```
GET http://54.180.142.111:8080/crawler/korea
→ HTTP 405 "Request method 'GET' not supported"
```

`StockCrawlerController.java`에 `@PostMapping("/korea")`만 있어
브라우저에서 URL 입력(GET) 시 405 발생.

### 수정 내용 (적용 완료 ✅)

```java
// Before:
@PostMapping("/korea")
public ResponseEntity<...> crawlKorea() { ... }

// After:
@RequestMapping(value = "/korea", method = {RequestMethod.GET, RequestMethod.POST})
public ResponseEntity<...> crawlKorea() { ... }
```

`/korea`, `/us`, `/all` 세 엔드포인트 모두 GET+POST 허용으로 변경.

---

## 5. 현재 EC2 상태 정리

| 항목 | 상태 | 비고 |
|------|------|------|
| Tomcat 9 실행 | ✅ 정상 | 54.180.142.111:8080 응답 |
| DB 연결 | ✅ 정상 | update-all API 성공 |
| RDS 엔드포인트 | ✅ 정상 | portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com |
| 보안 그룹 포트 3306 | ✅ 정상 | ec2-rds-2 자동 구성됨 |
| 크롤러 /korea | ✅ 수정됨 | GET+POST 허용 (코드 수정 후 재배포 필요) |
| 크롤러 /us | ✅ 수정됨 | GET+POST 허용 (코드 수정 후 재배포 필요) |

---

## 6. 다음 단계 (EC2 재배포)

### STS에서 Maven Build → WAR 생성

```
1. STS 메뉴: Run As → Maven install
   → target/portwatch-1.0.0-BUILD-SNAPSHOT.war 생성됨

2. STS Tomcat 서버 오류 해결 (wtpwebapps 오류):
   ① STS 상단 메뉴: Project → Clean... → OK
   ② Servers 탭 → Tomcat → 우클릭 → Clean
   ③ Tomcat → 우클릭 → Publish
   ④ Tomcat → Start
```

### EC2에 WAR 배포

```bash
# 1. WAR 파일 EC2로 전송 (WinSCP 또는 scp)
scp target/portwatch-1.0.0-BUILD-SNAPSHOT.war ubuntu@54.180.142.111:~/

# 2. EC2에서 배포
ssh ubuntu@54.180.142.111
sudo systemctl stop tomcat
sudo cp ~/portwatch-1.0.0-BUILD-SNAPSHOT.war /opt/tomcat/webapps/ROOT.war
sudo systemctl start tomcat

# ⚠️  주의: ROOT.war 절대 삭제하지 말 것
# 삭제하면 Tomcat이 ROOT/ 폴더도 삭제 → 전체 404

# 3. 로그 확인
sudo tail -f /opt/tomcat/logs/catalina.out
```

### IPv4 강제 설정 확인 (EC2 필수)

```bash
# 아직 설정 안 했다면:
sudo bash -c 'cat > /opt/tomcat/bin/setenv.sh << EOF
#!/bin/bash
export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Xms256m -Xmx512m"
EOF'
sudo chmod +x /opt/tomcat/bin/setenv.sh

# 확인
cat /opt/tomcat/bin/setenv.sh
```

### 크롤러 수동 실행 (재배포 후)

```bash
# 브라우저에서 직접 접근 (수정 후 GET 허용됨):
http://54.180.142.111:8080/crawler/korea
http://54.180.142.111:8080/crawler/us

# 또는 curl:
curl -X POST http://54.180.142.111:8080/crawler/korea
curl -X POST http://54.180.142.111:8080/crawler/us
```

---

## 7. 로그인 실패 해결

DB 연결은 정상이므로 로그인 문제는 계정 데이터 없음:

```sql
-- EC2에서 PuTTy → MySQL 접속:
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -P 3306 -u admin -p

-- 테스트 계정 삽입 (비밀번호: portwatch123):
USE portwatch;
INSERT IGNORE INTO MEMBER (
    member_id, member_email, member_pass, member_name,
    member_role, member_status, balance
) VALUES
('testuser01', 'test@portwatch.com',
 '$2a$10$0GBSb04xfIUriIM/5WwqhSuKgZHeltHn.4qFqnLIlojkISte/pSgSC',
 '테스트유저', 'USER', 'ACTIVE', 1000000);

-- 확인:
SELECT member_id, member_email, member_status FROM MEMBER;
```
