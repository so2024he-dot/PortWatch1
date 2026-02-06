package com.portwatch.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portwatch.service.MemberService;  // ⭐ 인터페이스로 변경!

/**
 * 회원 API 컨트롤러
 * 
 * ⚠️ 중요: MemberServiceImpl이 아닌 MemberService 인터페이스를 주입해야 함!
 */
@RestController
@RequestMapping("/api/member")
@CrossOrigin(origins = "*")
public class MemberApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(MemberApiController.class);
    
    // ⭐ 핵심 수정: MemberServiceImpl → MemberService
    @Autowired
    private MemberService memberService;  // 인터페이스로 주입!
    
    /**
     * 이메일 중복 체크 API
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        
        Map<String, Object> result = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📧 이메일 중복 체크");
        logger.info("  - 이메일: {}", email);
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            boolean available = memberService.checkEmailAvailable(email);
            
            logger.info("✅ 이메일 체크 완료");
            logger.info("  - 사용 가능: {}", available);
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", true);
            result.put("available", available);
            result.put("message", available ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("❌ 이메일 체크 오류", e);
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "이메일 확인 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 이메일 인증 코드 발송 API
     */
    @PostMapping("/send-verification")
    public ResponseEntity<Map<String, Object>> sendVerification(@RequestParam String email) {
        
        Map<String, Object> result = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📨 인증 코드 발송");
        logger.info("  - 이메일: {}", email);
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            String verificationCode = memberService.generateVerificationCode();
            
            logger.info("✅ 인증 코드 생성 완료");
            logger.info("  - 코드: {}", verificationCode);
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", true);
            result.put("message", "인증 코드가 발송되었습니다.");
            result.put("code", verificationCode);  // 개발용 (실제로는 이메일로 발송)
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("❌ 인증 코드 발송 오류", e);
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "인증 코드 발송 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 이메일 인증 코드 확인 API
     */
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(
            @RequestBody Map<String, String> request) {
        
        Map<String, Object> result = new HashMap<>();
        
        String email = request.get("email");
        String code = request.get("code");
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🔐 인증 코드 확인");
        logger.info("  - 이메일: {}", email);
        logger.info("  - 코드: {}", code);
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            boolean verified = memberService.verifyCode(email, code);
            
            logger.info("✅ 인증 코드 확인 완료");
            logger.info("  - 결과: {}", verified);
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", true);
            result.put("verified", verified);
            result.put("message", verified ? "인증이 완료되었습니다." : "인증 코드가 일치하지 않습니다.");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("❌ 인증 코드 확인 오류", e);
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "인증 코드 확인 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 헬스 체크 API
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "OK");
        result.put("service", "MemberApiController");
        result.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(result);
    }
}
