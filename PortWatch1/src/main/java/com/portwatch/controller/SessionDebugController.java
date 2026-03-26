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
            result.put("success",   false);
            result.put("dbConnected", false);
            result.put("message",   "DB 연결 실패: " + e.getMessage());
            result.put("diagnosis", "HikariCP 연결 풀 문제. root-context.xml의 initializationFailTimeout 확인 필요.");
            result.put("error",     e.getClass().getSimpleName());
        }

        return ResponseEntity.ok(result);
    }
}
