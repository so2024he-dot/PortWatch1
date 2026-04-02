package com.portwatch.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.portwatch.mapper.MemberMapper;
import com.portwatch.domain.MemberVO;

/**
 * 세션 디버그 컨트롤러
 *
 * ✅ 추가된 엔드포인트:
 *   GET /debug/bcrypt?pass=xxx  → BCrypt 해시 생성 (MySQL 비밀번호 업데이트용)
 *   GET /debug/db-check         → DB 연결 상태 확인
 *
 * ⚠️ 운영 환경에서는 /debug/** 경로를 SecurityConfig에서 제거하거나
 *    IP 제한을 추가하는 것을 권장합니다.
 */
@Controller
@RequestMapping("/debug")
public class SessionDebugController {

    @Autowired(required = false)
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private MemberMapper memberMapper;

    /**
     * 세션 진단 페이지
     * GET /debug/session
     */
    @GetMapping("/session")
    public String sessionDebug() {
        return "session-debug";
    }

    /**
     * BCrypt 해시 생성 (테스트 계정 비밀번호 설정용)
     * GET /debug/bcrypt?pass=test1234
     *
     * 사용법:
     *   1. 브라우저에서 http://54.180.142.111:8080/debug/bcrypt?pass=원하는비밀번호 접속
     *   2. 반환된 hash 값을 복사
     *   3. MySQL에서 UPDATE MEMBER SET member_pass='복사한hash' WHERE member_email='이메일';
     */
    @GetMapping("/bcrypt")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateBcrypt(
            @RequestParam("pass") String pass) {

        Map<String, Object> result = new HashMap<>();

        if (passwordEncoder == null) {
            result.put("success", false);
            result.put("message", "BCryptPasswordEncoder Bean을 찾을 수 없습니다. DB 연결을 먼저 확인하세요.");
            return ResponseEntity.ok(result);
        }

        try {
            String hash = passwordEncoder.encode(pass);
            result.put("success",   true);
            result.put("plain",     pass);
            result.put("hash",      hash);
            result.put("usage",
                "MySQL: UPDATE MEMBER SET member_pass='" + hash + "' WHERE member_email='이메일주소';");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "BCrypt 생성 실패: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * DB 연결 상태 확인
     * GET /debug/db-check
     *
     * 로그인/회원가입 오류 진단:
     *   - success=true  → DB 연결 정상, 비밀번호 불일치 문제
     *   - success=false → DB 연결 실패, HikariCP 설정 문제
     */
    @GetMapping("/db-check")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> dbCheck() {

        Map<String, Object> result = new HashMap<>();

        if (memberMapper == null) {
            result.put("success",  false);
            result.put("message",  "MemberMapper Bean을 찾을 수 없습니다.");
            result.put("diagnosis", "Spring Bean 등록 오류 - root-context.xml 확인 필요");
            return ResponseEntity.ok(result);
        }

        try {
            // admin@portwatch.com 조회로 DB 연결 테스트
            MemberVO member = memberMapper.findByEmail("admin@portwatch.com");

            result.put("success",      true);
            result.put("dbConnected",  true);
            result.put("memberFound",  member != null);
            result.put("memberStatus", member != null ? member.getMemberStatus() : "없음");
            result.put("message",
                member != null
                    ? "DB 정상 ✅ admin@portwatch.com 회원 발견 (상태: " + member.getMemberStatus() + ")"
                    : "DB 정상 ✅ (admin@portwatch.com 없음 - 회원가입 필요)");
            result.put("diagnosis",
                "DB 연결은 정상입니다. 로그인이 안 되면 비밀번호 형식(BCrypt) 확인 필요. " +
                "/debug/bcrypt?pass=원하는비밀번호 로 BCrypt 해시를 생성하세요.");

        } catch (Exception e) {
            String errMsg = e.getMessage() != null ? e.getMessage() : "";
            String diagnosis;
            String action;

            if (errMsg.contains("timed out") || errMsg.contains("Connection is not available")) {
                // HikariCP 풀 타임아웃 — 3단계 진단 체크리스트
                diagnosis = "[HikariCP 30초 타임아웃] 다음 순서로 EC2에서 진단하세요:\n"
                          + "① IPv4 확인: ps aux | grep java | grep -o 'preferIPv4Stack=true'\n"
                          + "   → 출력없으면 setenv.sh 미적용 → setenv.sh 재생성 필요\n"
                          + "② RDS TCP 연결: nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306\n"
                          + "   → 실패하면 보안그룹 문제 → AWS 콘솔에서 SG 확인\n"
                          + "③ jdbcUrl 확인: unzip -p /opt/tomcat/webapps/ROOT.war WEB-INF/spring/root-context.xml | grep jdbcUrl\n"
                          + "   → cpggwqm1goo3(숫자1) 이고 한 줄인지 확인";
                action    = "# Step 1: IPv4 확인\n"
                          + "ps aux | grep java | grep -o 'preferIPv4Stack=true'\n\n"
                          + "# Step 2: RDS TCP 직접 테스트\n"
                          + "nc -zv portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com 3306\n\n"
                          + "# Step 3: MySQL 직접 연결 테스트\n"
                          + "mysql -h portwatch-db-seoul.cpggwqm1goo3.ap-northeast-2.rds.amazonaws.com -P 3306 -u admin -p\n\n"
                          + "# Step 4: Tomcat 로그 확인\n"
                          + "sudo grep -i 'hikari\\|ERROR\\|jdbc' /opt/tomcat/logs/catalina.out | tail -30";
            } else if (errMsg.contains("Communications link failure") || errMsg.contains("connect timed out")) {
                // TCP 연결 자체 실패 — 보안 그룹 또는 RDS 엔드포인트 문제
                diagnosis = "[원인] TCP 소켓 연결 실패. RDS 보안 그룹 포트 3306 인바운드 규칙 확인 필요.";
                action    = "AWS 콘솔 → RDS → 보안 그룹 → 인바운드 규칙 → 포트 3306 → EC2 IP 허용 확인";
            } else if (errMsg.contains("Access denied")) {
                // 인증 실패 — DB 사용자/비밀번호 오류
                diagnosis = "[원인] DB 인증 실패. root-context.xml의 username/password 확인 필요.";
                action    = "root-context.xml: username=admin, password 확인";
            } else if (errMsg.contains("Unknown database") || errMsg.contains("portwatch")) {
                // DB 없음
                diagnosis = "[원인] 'portwatch' 데이터베이스 없음. RDS에서 CREATE DATABASE 필요.";
                action    = "mysql -h [RDS엔드포인트] -u admin -p → CREATE DATABASE portwatch;";
            } else {
                diagnosis = "[원인 미상] 예외: " + e.getClass().getSimpleName();
                action    = "EC2 로그 확인: sudo tail -100 /opt/tomcat/logs/catalina.out | grep -i hikari";
            }

            result.put("success",    false);
            result.put("dbConnected", false);
            result.put("message",    "DB 연결 실패: " + errMsg);
            result.put("diagnosis",  diagnosis);
            result.put("action",     action);
            result.put("error",      e.getClass().getSimpleName());
        }

        return ResponseEntity.ok(result);
    }
}
