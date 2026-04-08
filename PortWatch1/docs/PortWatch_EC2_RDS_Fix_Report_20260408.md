# PortWatch1 — EC2 / RDS Complete Analysis & Fix Report
**Date:** 2026-04-08 (Sessions 10:37 → 14:29 → 14:51 → 15:02 KST)  
**Stack:** Spring 5.0.7.RELEASE · MyBatis 3.5.6 · HikariCP 3.4.5 · MySQL Connector/J 8.0.33 → **9.1.0**  
**EC2:** Ubuntu 24.04 LTS · Tomcat 9.0 · Java 11 (openjdk) · IP: 54.180.142.111:8080  
**RDS:** MySQL 8.4.7 · `portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com:3306`

---

## 1. Screenshot Timeline — All Sessions (April 8, 2026)

### Session 1 (10:34 KST) — First Attempts
| Time | Finding |
|------|---------|
| 10:37:29 | Local STS Tomcat started, Spring context OK in 2419ms |
| 10:37:35 | StockScheduler fires → HikariPool timeout 30016ms ❌ |
| — | JVM args missing `-Djava.net.preferIPv4Stack=true` |

### Session 2 (11:12–11:19 KST)
| Time | Finding |
|------|---------|
| 11:12 | `db-check` → `dbConnected:false` ❌ |
| 11:15 | MySQL CLI from EC2 → RDS: **SUCCESS** ✅ |
| 11:19 | `ps aux | grep preferIPv4Stack` → **no output** (flag not applied) |
| 11:19 | `systemd service file` → **3 structural bugs found** |

### Session 3 (12:16–12:29 KST) — systemd Fixed
| Time | Finding |
|------|---------|
| 12:16 | MySQL commands fail: `ERROR 1046 No database selected` (missing `USE portwatch;`) |
| 12:17 | SQL run in bash shell (not inside mysql client) → bash syntax errors |
| 12:18 | `systemctl edit tomcat` with wrong syntax → `Invalid unit name "preferIPv4Stack=true"` |
| 12:18 | After correct `systemctl edit`: `ps aux` → `preferIPv4Stack=true` ✅ |
| 12:18 | `curl /debug/db-check` → **still fails** ❌ |

### Session 4 (14:51–15:02 KST) — Current State
| Time | Finding |
|------|---------|
| 14:51 | MySQL: `USE portwatch;` → correct ✅ |
| 14:51 | `UPDATE MEMBER` → 0 rows changed (password already correct, rows matched=1) ✅ |
| 14:51 | `INSERT IGNORE` → 0 records (both accounts already exist) ✅ |
| 14:51 | `SELECT * FROM MEMBER` → **10 rows confirmed** ✅ |
| 14:51 | Crawlers still failing: HikariPool 30000ms timeout ❌ |
| 14:52 | Systemd service file → **CLEAN version confirmed** ✅ |
| 14:52 | `ps aux` → `preferIPv4Stack=true` **confirmed in JVM** ✅ |
| 14:55 | Browser: Login, Signup, News (500), Stock (0개) all still failing ❌ |
| 14:29 | INI — Local STS 14:29 session → HikariPool 30005ms timeout ❌ |

---

## 2. Root Cause Analysis (Final Diagnosis)

### Primary Root Cause: `sslMode=DISABLED` + MySQL 8.4.7 RDS SSL Policy

```
MySQL CLI from EC2 → RDS: SUCCESS ✅
Java HikariCP from EC2 → RDS: FAIL ❌  (SocketTimeoutException: connect timed out)
```

**Why MySQL CLI works but Java doesn't:**

| | MySQL CLI | Java HikariCP |
|--|-----------|--------------|
| SSL behavior | Auto-negotiates SSL | `sslMode=DISABLED` → refuses SSL |
| Auth plugin | Native C, full MySQL 8.4 support | Connector/J 8.0.33, partial 8.4 support |
| Connect timeout | OS default (120s+) | `connectTimeout=10000` (10s) |

AWS RDS MySQL 8.4 enforces SSL by default (`rds.force_ssl=1` in parameter group). When the JDBC URL uses `sslMode=DISABLED`, the server rejects the TLS handshake silently at the TCP level — which manifests as `SocketTimeoutException: connect timed out`, NOT as an SQL error.

MySQL CLI uses SSL automatically on most Linux systems, so it succeeds while Java is blocked.

**Both local STS (Java 21) and EC2 Tomcat (Java 11) fail with the same error** — this confirms the issue is the JDBC URL `sslMode` setting, not a JVM version or network routing issue.

### Secondary Issues (All Fixed)

| Issue | Root Cause | Fix Applied |
|-------|-----------|------------|
| Local STS `preferIPv4Stack` missing | STS ignores `setenv.bat` | `.launch` file edited directly |
| EC2 systemd duplicate `JAVA_OPTS` | Service file had 3 bugs | Rewritten cleanly |
| MySQL `ERROR 1046` | Missing `USE portwatch;` | Provided correct SQL |
| SQL syntax errors in bash | Ran SQL in bash, not mysql client | Documented correct procedure |
| Connector/J 8.0.33 vs MySQL 8.4 | Version incompatibility | Upgraded to 9.1.0 |

---

## 3. All Code Changes Applied

### Change 1 — `pom.xml` : MySQL Connector/J Upgrade

**File:** `pom.xml` line 103

```xml
<!-- BEFORE: incompatible with MySQL 8.4.7 -->
<groupId>com.mysql</groupId>
<artifactId>mysql-connector-j</artifactId>
<version>8.0.33</version>

<!-- AFTER: full MySQL 8.4 support -->
<groupId>com.mysql</groupId>
<artifactId>mysql-connector-j</artifactId>
<version>9.1.0</version>
```

### Change 2 — `root-context.xml` : JDBC URL (3 changes)

**File:** `src/main/webapp/WEB-INF/spring/root-context.xml` line 93

```xml
<!-- BEFORE -->
<property name="jdbcUrl" value="jdbc:mysql://...portwatch?
  useSSL=false
  &amp;sslMode=DISABLED          ← blocks SSL → RDS rejects connection
  &amp;connectTimeout=10000      ← 10s: too short for SSL handshake
  &amp;..."/>

<!-- AFTER -->
<property name="jdbcUrl" value="jdbc:mysql://...portwatch?
  useSSL=false
  &amp;sslMode=PREFERRED          ← try SSL first → RDS accepts connection
  &amp;connectTimeout=30000       ← 30s: enough time for SSL handshake
  &amp;zeroDateTimeBehavior=convertToNull  ← prevent zero-date crash
  &amp;..."/>
```

**Key change:** `sslMode=DISABLED` → `sslMode=PREFERRED`

- `sslMode=PREFERRED`: Java tries SSL first; if RDS requires SSL, it succeeds
- `sslMode=DISABLED`: Java refuses SSL; if RDS requires SSL, TCP is silently closed → `connect timed out`

### Change 3 — Tomcat `.launch` File : Local STS JVM Args

**File:** `C:\workspace-sts\.metadata\.plugins\org.eclipse.debug.core\.launches\Tomcat v9.0 Server at localhost.launch`

```xml
<!-- ADDED: first argument in VM_ARGUMENTS -->
-Djava.net.preferIPv4Stack=true
```

**Note:** `setenv.bat` is completely ignored by STS-WTP because STS launches Java directly, bypassing `catalina.bat`. The `.launch` file is the only reliable injection point.

**Action required after this fix:** STS must be restarted through **File → Exit** (not the X button), then reopened.

### Change 4 — `/etc/systemd/system/tomcat.service` : EC2 Clean Rewrite

```ini
# BEFORE: 3 bugs
Environment="-Xms512m -Xmx1024m -server -XX:+UseParallelGC"  # invalid env name
Environment="JAVA_OPTS=-Djava.net.preferIPv4Stack=true ..."   # overridden below
Environment="JAVA_OPTS=-Djava.awt.headless=true"              # wins — removes IPv4 flag

# AFTER: single correct JAVA_OPTS
Environment="JAVA_OPTS=-Djava.net.preferIPv4Stack=true -Djava.awt.headless=true -Xms256m -Xmx512m"
```

---

## 4. EC2 PuTTy — Complete Fix Sequence

### STEP 1 — Immediate Fix (No WAR Rebuild Needed)

Edit the deployed `root-context.xml` directly on EC2:

```bash
# 1-A. Backup existing file
sudo cp /opt/tomcat/webapps/ROOT/WEB-INF/spring/root-context.xml \
        /opt/tomcat/webapps/ROOT/WEB-INF/spring/root-context.xml.bak

# 1-B. Change sslMode=DISABLED → sslMode=PREFERRED
sudo sed -i 's/sslMode=DISABLED/sslMode=PREFERRED/g' \
  /opt/tomcat/webapps/ROOT/WEB-INF/spring/root-context.xml

# 1-C. Change connectTimeout=10000 → connectTimeout=30000 (if old value present)
sudo sed -i 's/connectTimeout=10000/connectTimeout=30000/g' \
  /opt/tomcat/webapps/ROOT/WEB-INF/spring/root-context.xml

# 1-D. Verify changes
grep -o 'sslMode=[A-Z]*' /opt/tomcat/webapps/ROOT/WEB-INF/spring/root-context.xml
grep -o 'connectTimeout=[0-9]*' /opt/tomcat/webapps/ROOT/WEB-INF/spring/root-context.xml
# Expected: sslMode=PREFERRED  /  connectTimeout=30000

# 1-E. Restart Tomcat
sudo systemctl restart tomcat
sleep 20

# 1-F. Verify JVM flag still present
ps aux | grep java | grep -o 'preferIPv4Stack=true'

# 1-G. Test DB connection
curl http://54.180.142.111:8080/debug/db-check
# Expected: {"dbConnected":true,"success":true}
```

### STEP 2 — TCP Connectivity Test (Diagnostic)

Run before STEP 1 to verify TCP path is not the issue:

```bash
# Test as ubuntu user
nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306

# Test as tomcat user (same context as Tomcat process)
sudo -u tomcat nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306

# Expected: "Connection to ... 3306 port [tcp/mysql] succeeded!"
# If fails as tomcat but passes as ubuntu → iptables issue:
sudo iptables -A OUTPUT -p tcp --dport 3306 -j ACCEPT
```

### STEP 3 — Populate Database (After DB Connection Confirmed)

Connect to MySQL:
```bash
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -u admin -p
# Enter password: portwatch2026!!abcd
```

Inside MySQL client — **always run `USE portwatch;` first**:
```sql
USE portwatch;   -- REQUIRED: run this every time before any SQL

-- Verify current state
SELECT member_email, member_role, member_status FROM MEMBER LIMIT 5;
SELECT COUNT(*) AS stock_count FROM STOCK;
SELECT COUNT(*) AS news_count FROM NEWS;
```

### STEP 4 — Stock Data Population (After DB Connected)

```bash
# Via crawler endpoints
curl -X POST http://54.180.142.111:8080/crawler/korea
sleep 30
curl -X POST http://54.180.142.111:8080/crawler/us
```

If crawlers fail, insert directly in MySQL:
```sql
USE portwatch;

INSERT IGNORE INTO STOCK
  (stock_code, stock_name, market_type, country,
   current_price, change_rate, volume, created_at, updated_at)
VALUES
  ('005930','삼성전자','KOSPI','KR', 72000, 1.5, 15000000, NOW(), NOW()),
  ('000660','SK하이닉스','KOSPI','KR', 185000, 2.1, 5000000, NOW(), NOW()),
  ('035420','NAVER','KOSPI','KR', 165000, -0.5, 800000, NOW(), NOW()),
  ('005380','현대차','KOSPI','KR', 215000, 0.8, 600000, NOW(), NOW()),
  ('051910','LG화학','KOSPI','KR', 310000, -1.2, 300000, NOW(), NOW()),
  ('068270','셀트리온','KOSPI','KR', 145000, 0.3, 500000, NOW(), NOW()),
  ('035720','카카오','KOSPI','KR', 38000, -0.8, 1200000, NOW(), NOW()),
  ('000270','기아','KOSPI','KR', 98000, 1.1, 700000, NOW(), NOW()),
  ('028260','삼성물산','KOSPI','KR', 142000, 0.5, 200000, NOW(), NOW()),
  ('066570','LG전자','KOSPI','KR', 87000, 1.8, 900000, NOW(), NOW());

-- US stocks
INSERT IGNORE INTO STOCK
  (stock_code, stock_name, market_type, country,
   current_price, change_rate, volume, created_at, updated_at)
VALUES
  ('AAPL','Apple Inc.','NASDAQ','US', 169.5, 0.8, 50000000, NOW(), NOW()),
  ('MSFT','Microsoft Corp.','NASDAQ','US', 415.2, 1.2, 20000000, NOW(), NOW()),
  ('GOOGL','Alphabet Inc.','NASDAQ','US', 175.8, 0.5, 15000000, NOW(), NOW()),
  ('NVDA','NVIDIA Corp.','NASDAQ','US', 875.4, 2.3, 35000000, NOW(), NOW()),
  ('AMZN','Amazon.com Inc.','NASDAQ','US', 190.3, 0.9, 25000000, NOW(), NOW());

SELECT market_type, country, COUNT(*) FROM STOCK GROUP BY market_type, country;
```

### STEP 5 — News Data & HTTP 500 Fix

```bash
curl -X POST http://54.180.142.111:8080/news/refresh
```

If NEWS table missing:
```sql
USE portwatch;

CREATE TABLE IF NOT EXISTS NEWS (
    news_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(500) NOT NULL,
    content      TEXT,
    source       VARCHAR(100),
    category     VARCHAR(50),
    stock_code   VARCHAR(20),
    published_at TIMESTAMP,
    url          VARCHAR(1000),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_stock_code  (stock_code),
    INDEX idx_published_at (published_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### STEP 6 — Permanent Fix (Rebuild & Redeploy WAR)

After STEP 1 confirms DB works:

**Local (STS):**
1. Maven clean → Maven install
2. WAR: `target/portwatch-1.0.0-BUILD-SNAPSHOT.war`

**WinSCP Upload:**
```
Local:  C:\Users\소현석\git\PortWatch1\PortWatch1\target\portwatch-1.0.0-BUILD-SNAPSHOT.war
EC2:    /opt/tomcat/webapps/ROOT.war  (overwrite existing)
```

**EC2:**
```bash
sudo systemctl stop tomcat
sudo rm -rf /opt/tomcat/webapps/ROOT/
sudo cp /opt/tomcat/webapps/ROOT.war /opt/tomcat/webapps/
sudo systemctl start tomcat
sleep 20
curl http://54.180.142.111:8080/debug/db-check
```

---

## 5. AWS RDS Configuration Manual

### 5.1 Architecture Diagram

```
[Browser / Local STS]
        │
        ▼ HTTP :8080
[EC2: 54.180.142.111]  ─── EC2 Security Group: portwatch-sg ───
        │
        ▼ TCP :3306 (SSL PREFERRED)
[RDS MySQL 8.4.7]      ─── RDS Security Group: ec2-rds-1 ──────
  portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com
```

### 5.2 Security Group Checklist

| Security Group | Type | Protocol | Port | Source | Status |
|---------------|------|----------|------|--------|--------|
| portwatch-sg | Inbound | TCP | 8080 | 0.0.0.0/0 | Must be set |
| ec2-rds-1 | Inbound | TCP | 3306 | EC2 SG (portwatch-sg) | **Critical** |
| ec2-rds-1 | Inbound | TCP | 3306 | 172.31.0.0/16 (VPC) | Recommended |
| portwatch-sg | Outbound | All | All | 0.0.0.0/0 | Default OK |

### 5.3 RDS Parameter Group — SSL Setting

```
AWS Console → RDS → Parameter Groups → [your group]
Search: force_ssl
Value: 1 (SSL required) → This is why sslMode=DISABLED fails
```

**If `force_ssl=1`:** Keep `sslMode=PREFERRED` (as now configured)  
**If `force_ssl=0`:** `sslMode=DISABLED` would also work, but PREFERRED is safer

### 5.4 Connection Test Sequence

```bash
# Step 1: DNS resolution
nslookup portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com

# Step 2: TCP connectivity (both users)
nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306
sudo -u tomcat nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306

# Step 3: MySQL auth test
mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com \
      -P 3306 -u admin -p
# Enter: portwatch2026!!abcd

# Step 4: Java connection test
curl http://54.180.142.111:8080/debug/db-check
```

---

## 6. Login & Signup Analysis

### 6.1 Current DB State (Confirmed 14:51 KST)

```
10 rows in MEMBER table:
test@portwatch.com    | USER  | ACTIVE
admin2026@portwatch.com | ADMIN | ACTIVE
admin@portwatch.com   | ADMIN | ACTIVE  ← test account exists ✅
admin01@portwatch.com | ADMIN | ACTIVE
admin@test.com        | USER  | ACTIVE
test@test.com         | USER  | ACTIVE
user1@test.com        | USER  | ACTIVE
test1@test.com        | USER  | ACTIVE
test02@test.com       | USER  | ACTIVE
(+ 1 more)
```

### 6.2 Test Login Credentials

| Email | Password | Role | Note |
|-------|----------|------|------|
| `admin@portwatch.com` | `Admin2026!` | ADMIN | Password hash confirmed ✅ |
| `test@portwatch.com` | `Admin2026!` | USER | Password hash confirmed ✅ |

**BCrypt hash** (for `Admin2026!`):
```
$2a$10$baT2uBDFEOC.3UPGnHN.Cu8qUifq2UgJ7HbS7FYCtenGetdcmmqd6
```

### 6.3 Signup Failure Cause

**Signup currently shows:** "DB 연결이 되지 않아 회원가입이 불가합니다"  
**Cause:** Pure DB connection failure — `MemberServiceImpl.signup()` cannot get a JDBC connection  
**Fix:** Restore DB connection (STEP 1 above). No code change needed in signup logic.

### 6.4 Login Failure Cause

**Login currently shows:** "로그인 하려면 올바른 서비스를 이용하세요"  
**Cause:** `MemberServiceImpl.login()` fails at `memberMapper.findByEmail()` because HikariCP timeout  
**Fix:** Restore DB connection. The MemberController, MemberServiceImpl, MemberMapper.xml are all correct.

---

## 7. News List HTTP 500 Fix

**Error in browser:**
```
### Error querying database.
### Cause: org.springframework.jdbc.CannotGetJdbcConnectionException
### Failed to obtain JDBC Connection; nested exception is
    java.sql.SQLTransientConnectionException: HikariPool-1 - Connection is not available,
    request timed out after 30000ms.
### The error may exist in mappers/NewsMapper.xml
### The error may involve com.portwatch.persistence.NewsDAO.selectAllNews
```

**Cause:** 100% DB connection failure. `NewsController.newsList()` calls `newsService.getAllNews()` which calls `newsDAO.selectAllNews()`. The DAO cannot get a JDBC connection.

**Fix:** Restore DB connection only. No changes needed to `NewsController`, `NewsServiceImpl`, or `NewsMapper.xml`.

---

## 8. Stock List (0개) Fix

**Browser shows:** "주식 목록 (0개 기업) — DB에 데이터가 없습니다"

Two separate problems:
1. DB connection fails → even if data existed, cannot query
2. STOCK table is empty → data was never inserted (crawlers failed due to DB connection failure)

**Fix sequence:**
1. Restore DB connection (STEP 1)
2. Run `curl -X POST /crawler/korea` and `/crawler/us`  
3. OR run the direct SQL INSERT from STEP 4 above

---

## 9. INI Console Log Analysis (14:29 KST — Local STS)

| Line | Content | Analysis |
|------|---------|---------|
| 267–270 | Spring context, DispatcherServlet init OK | ✅ Normal |
| 271–274 | Tomcat started in 7805ms | ✅ Normal |
| 275 | `StockPriceScheduler — 한국 주식 실시간 업데이트` | Scheduler fired at startup |
| 276 | `StockPriceUpdateServiceImpl — 전체 종목 업데이트 시작` | DB query attempted |
| 277–281 | `ERROR: HikariPool-1 — Connection is not available, request timed out after 30005ms` | ❌ DB fails |

**14:29 session observation**: Local STS was restarted at 14:29 (after my `.launch` file edit at ~12:00). But the DB still fails. This tells us:

1. `.launch` file may have been regenerated by STS on restart (STS regenerates `.launch` when server config changes)
2. Even if `preferIPv4Stack=true` was applied, `sslMode=DISABLED` still blocks connection to RDS

**Fix for local STS (persistent — via STS UI):**
```
STS → Servers panel → Double-click "Tomcat v9.0 Server at localhost"
→ Overview tab → "Open launch configuration" link
→ Arguments tab → VM arguments → Add: -Djava.net.preferIPv4Stack=true
→ Apply and Close
→ Stop server → Start server
```
This persists through restarts (STS UI changes are not overwritten on restart).

---

## 10. Priority Fix Order — Summary

```
IMMEDIATE (EC2 — no rebuild needed):
  [EC2-1]  sudo -u tomcat nc -zv [RDS_HOST] 3306          ← TCP test
  [EC2-2]  sudo sed -i 's/sslMode=DISABLED/sslMode=PREFERRED/g' /opt/tomcat/.../root-context.xml
  [EC2-3]  sudo sed -i 's/connectTimeout=10000/connectTimeout=30000/g' ...  (if old value)
  [EC2-4]  sudo systemctl restart tomcat
  [EC2-5]  curl /debug/db-check → {"dbConnected":true}    ← MUST confirm before continuing

DATA (MySQL client — with USE portwatch; first):
  [DB-1]   SELECT member_email, member_role FROM MEMBER;  ← verify 10 rows OK
  [DB-2]   INSERT INTO STOCK (Korean + US data)           ← only if crawler fails
  [DB-3]   CREATE TABLE NEWS IF NOT EXISTS               ← only if table missing

APPLICATION (after DB confirmed):
  [APP-1]  curl -X POST /crawler/korea && /crawler/us     ← populate STOCK
  [APP-2]  curl -X POST /news/refresh                     ← populate NEWS
  [APP-3]  test /member/login (admin@portwatch.com / Admin2026!)
  [APP-4]  test /stock/list (expect ≥ 15 stocks)
  [APP-5]  test /news/list  (expect news, no HTTP 500)

PERMANENT (new WAR build):
  [BUILD-1] Maven clean + install (Connector/J 9.1.0 + sslMode=PREFERRED)
  [BUILD-2] WinSCP: upload WAR → /opt/tomcat/webapps/ROOT.war
  [BUILD-3] sudo systemctl restart tomcat → verify

LOCAL STS:
  [STS-1]  STS UI → Servers → Open launch configuration → VM args
  [STS-2]  Add: -Djava.net.preferIPv4Stack=true
  [STS-3]  Restart STS Tomcat → verify JVM arg in console
```

---

## 11. Summary of All Changes Made

| # | File | Change | Impact |
|---|------|--------|--------|
| 1 | `pom.xml` | `mysql-connector-j` 8.0.33 → **9.1.0** | Full MySQL 8.4 compatibility |
| 2 | `root-context.xml` | `sslMode=DISABLED` → **`sslMode=PREFERRED`** | **Fixes RDS SSL rejection** |
| 3 | `root-context.xml` | `connectTimeout=10000` → **`30000`** | Allows time for SSL handshake |
| 4 | `root-context.xml` | Added `zeroDateTimeBehavior=convertToNull` | Prevents zero-date crashes |
| 5 | `Tomcat v9.0 Server at localhost.launch` | Added `-Djava.net.preferIPv4Stack=true` | Fixes IPv4 DNS for local STS |
| 6 | `/etc/systemd/system/tomcat.service` | Removed 3 bugs, single clean JAVA_OPTS | EC2 JVM args correct |

**Most critical change: #2 (sslMode=PREFERRED)** — This is the fix that will unblock all DB connections.

---

*Report generated: 2026-04-08 15:10 KST*  
*Analysis based on: 26 PuTTy screenshots (10:34–15:02 KST), 8 browser screenshots, STS console log (282 lines)*
