# AWS RDS + STS 로컬 개발 연결 매뉴얼
**날짜**: 2026-04-06
**목적**: 로컬 STS(Windows 11)에서 AWS RDS MySQL 8.4.7 연결하는 완전한 방법

---

## 핵심: 왜 로컬에서 안 되나?

```
로컬 STS JVM이 DNS 조회 시 Inet6AddressImpl(IPv6)를 사용
→ Windows 11 IPv6 환경에서 AWS RDS 엔드포인트 해석 불가
→ java.net.UnknownHostException

해결: STS JVM args에 -Djava.net.preferIPv4Stack=true 추가
```

추가로, RDS가 VPC 프라이빗 서브넷에만 있는 경우 퍼블릭 접근도 활성화 필요.

---

## STEP 1: STS Tomcat JVM 아규먼트 설정

### 1-1. STS에서 설정 열기
```
① STS 하단 [Servers] 탭 클릭
② "Tomcat v9.0 Server at localhost" 더블클릭
③ 서버 설정 탭이 열리면 → "Open launch configuration" 링크 클릭
④ [Run Configurations] 창이 열림
⑤ [Arguments] 탭 선택
⑥ "VM arguments:" 텍스트 박스 → 현재 내용 맨 끝에 추가:
   -Djava.net.preferIPv4Stack=true
⑦ [Apply] → [Close]
```

### 1-2. 추가 전/후 비교
```
[추가 전 VM arguments 마지막 줄]:
  -XX:+ShowCodeDetailsInExceptionMessages

[추가 후]:
  -XX:+ShowCodeDetailsInExceptionMessages
  -Djava.net.preferIPv4Stack=true
```

### 1-3. 적용 확인
```
Tomcat 재시작 후 콘솔에서 확인:
정보: 명령 행 아규먼트: -Djava.net.preferIPv4Stack=true  ← 이 줄 나와야 함
```

---

## STEP 2: AWS RDS 퍼블릭 접근 활성화

> ⚠️ 이 설정은 **로컬 개발 전용**. 운영 환경에서는 비활성화 권장.

### 2-1. RDS 퍼블릭 접근 설정
```
① AWS Console (https://console.aws.amazon.com/) 로그인
② 상단 리전: 아시아 태평양(서울) ap-northeast-2 확인
③ 서비스 → RDS
④ 왼쪽 메뉴 → [데이터베이스]
⑤ portwatch-db-seoul 클릭
⑥ [수정(Modify)] 버튼 클릭
⑦ "연결(Connectivity)" 섹션 찾기
⑧ "추가 구성(Additional configuration)" 펼치기
⑨ "공개적으로 액세스 가능(Publicly accessible)" = ○ 예(Yes) 선택
⑩ 페이지 하단 [계속(Continue)] 클릭
⑪ "수정 일정": ● 즉시 적용(Apply immediately) 선택
⑫ [클러스터 수정(Modify cluster)] 클릭
⑬ 상태가 "modifying" → "available" 될 때까지 대기 (약 2~5분)
```

### 2-2. RDS 보안 그룹 인바운드 규칙 추가
```
① RDS → portwatch-db-seoul → [연결 및 보안] 탭
② "VPC 보안 그룹" 링크 클릭 → EC2 보안 그룹 페이지로 이동
③ [인바운드 규칙 편집] 클릭
④ [규칙 추가] 클릭:
   유형: MySQL/Aurora
   프로토콜: TCP
   포트 범위: 3306
   소스: 내 IP (자동 감지됨) ← 개발 PC IP
⑤ [규칙 저장] 클릭
```

> **내 IP 확인**: https://whatismyip.com 에서 확인 후 수동 입력 가능
> **개발용 임시 허용**: 소스를 `0.0.0.0/0`으로 하면 모든 IP 허용 (보안 주의)

### 2-3. RDS 퍼블릭 엔드포인트 확인
```
RDS → portwatch-db-seoul → [연결 및 보안] 탭
→ "엔드포인트 및 포트" 섹션
→ 엔드포인트: portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com
→ 포트: 3306
→ "공개적으로 액세스 가능": 예  ← 확인
```

---

## STEP 3: 연결 테스트

### 3-1. Windows CMD에서 DNS/TCP 연결 확인
```cmd
:: DNS 해석 확인
nslookup portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com

:: 성공 시 출력 예:
:: 이름:    portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com
:: Address:  3.36.xx.xx  ← IPv4 주소가 나와야 함

:: IPv4 응답이 안 나오면 preferIPv4Stack 설정 필요
```

### 3-2. MySQL CLI로 직접 연결 (선택사항)
```cmd
:: MySQL Workbench 또는 로컬 MySQL CLI (설치된 경우)
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com ^
      -P 3306 -u admin -p

:: 비밀번호: portwatch2026!!abcd
:: 연결 성공 시:
:: Server version: 8.4.7 Source distribution
```

### 3-3. STS 재시작 후 브라우저 확인
```
① STS에서 Tomcat 재시작 (Stop → Start)
② 콘솔에서 확인:
   -Djava.net.preferIPv4Stack=true ← JVM 아규먼트에 있는지 확인
   Root WebApplicationContext: initialization completed in XXXXms  ← Spring 기동
③ 브라우저: http://localhost:8080/debug/db-check
   → {"dbConnected": true}  ← 성공
```

---

## STEP 4: EC2 DB 연결 확인

### 4-1. EC2 PuTTy에서 확인
```bash
# DB 연결 상태 확인
curl -s http://localhost:8080/debug/db-check | python3 -m json.tool

# 성공 응답:
{
    "dbConnected": true,
    "memberFound": true
}

# 실패 시 원인 파악
sudo grep -n "UnknownHost\|Communications\|HikariPool" \
    /opt/tomcat/logs/catalina.out | tail -20
```

### 4-2. EC2에서 RDS 직접 연결
```bash
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -P 3306 -u admin -p'portwatch2026!!abcd'

# 연결 후:
USE portwatch;
SELECT COUNT(*) FROM MEMBER;
```

### 4-3. EC2 preferIPv4Stack 확인
```bash
# 적용 확인
ps aux | grep tomcat | grep -o '\-Djava.net.preferIPv4Stack=true'
# 출력: -Djava.net.preferIPv4Stack=true ← 있어야 함

# 없으면 setenv.sh 확인
sudo cat /opt/tomcat/bin/setenv.sh
sudo chmod 644 /opt/tomcat/bin/setenv.sh  # Permission denied 시
```

---

## STEP 5: Tomcat 새 WAR 배포 (이미 완료된 경우 건너뜀)

```bash
# [로컬 STS] Maven install → BUILD SUCCESS
# [WinSCP] 전송:
#   로컬: target/portwatch-1.0.0-BUILD-SNAPSHOT.war
#   원격: /home/ubuntu/portwatch.war

# [EC2 PuTTy]
sudo systemctl stop tomcat
sudo cp /home/ubuntu/portwatch.war /opt/tomcat/webapps/ROOT.war
sudo rm -rf /opt/tomcat/webapps/ROOT/
sudo systemctl start tomcat
sleep 45 && curl -s http://localhost:8080/debug/db-check | python3 -m json.tool
```

---

## STEP 6: DB 연결 성공 후 초기화

### 6-1. 테스트 계정 확인
```bash
# EC2 PuTTy에서
curl -s "http://localhost:8080/debug/bcrypt?pass=test1234"
# 응답: {"hash": "$2a$10$..."}  → 이 값을 MySQL UPDATE에 사용
```

```sql
USE portwatch;

-- 현재 테스트 계정 상태 확인
SELECT member_id, member_email, LEFT(member_pass,20),
       member_status, balance
  FROM MEMBER
 WHERE member_email = 'test@test.com';

-- 비밀번호 재설정 (필요시)
UPDATE MEMBER
   SET member_pass   = '$2a$10$CUgz7dDXNJNhW3RBK6K3vOz1bV/7/HBWp8taJpKskregpzZw.2duy',
       member_status = 'ACTIVE',
       updated_at    = NOW()
 WHERE member_email  = 'test@test.com';
-- 1 row affected ← 확인
```

### 6-2. 뉴스/주식 크롤링 수동 실행
```bash
# 뉴스 크롤링
curl -X POST http://54.180.142.111:8080/news/refresh
# → {"success": true}

# 주식 크롤링
curl -X POST http://54.180.142.111:8080/crawler/korea
curl -X POST http://54.180.142.111:8080/crawler/us
```

---

## 트러블슈팅 가이드

### 케이스 A: preferIPv4Stack 추가 후에도 UnknownHostException
```
원인: RDS가 퍼블릭 IP 없음 (Publicly accessible = No)
확인: AWS Console → RDS → 공개적으로 액세스 가능 = 예인지 확인
조치: STEP 2 진행
```

### 케이스 B: TCP 연결되나 인증 실패
```
오류: Access denied / Public Key Retrieval is not allowed
확인: jdbcUrl에 allowPublicKeyRetrieval=true 있는지 확인
조치: root-context.xml jdbcUrl 확인 (현재 포함됨)
```

### 케이스 C: 보안 그룹 TCP timeout
```
오류: Communications link failure (timeout, 0ms 경과 후)
확인: nc -zv portwatch-db-seoul... 3306 → 연결 안 됨
조치: RDS 보안그룹 → Inbound → 3306 내 IP 허용 규칙 추가 (STEP 2-2)
```

### 케이스 D: STS에서 설정이 적용 안 됨
```
확인: 콘솔에서 -Djava.net.preferIPv4Stack=true 라인 없음
조치:
  1. Servers 탭 → Tomcat 우클릭 → [Clean...]
  2. STS 완전 종료 후 재시작
  3. STEP 1 다시 진행
```

---

## 최종 검증 체크리스트

```
로컬 STS:
  □ STS VM args에 -Djava.net.preferIPv4Stack=true 추가
  □ Tomcat 재시작
  □ 콘솔에 -Djava.net.preferIPv4Stack=true 아규먼트 확인
  □ localhost:8080/debug/db-check → {"dbConnected": true}
  □ localhost:8080/member/login → test@test.com/test1234 로그인 성공
  □ localhost:8080/news/list → 뉴스 목록 표시

EC2:
  □ curl -s http://localhost:8080/debug/db-check → {"dbConnected": true}
  □ curl -X POST http://54.180.142.111:8080/news/refresh → {"success": true}
  □ http://54.180.142.111:8080/member/login → 로그인 성공
```

---

*생성일: 2026-04-06 | PortWatch AWS RDS + STS 연결 매뉴얼*
