package com.portwatch.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portwatch.service.MemberService;
import com.portwatch.service.MemberServiceImpl;

/**
 * 회원 관련 API 컨트롤러
 * (이메일 인증, 중복 확인 등)
 */
@RestController
@RequestMapping("/api/member")
public class MemberApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(MemberApiController.class);
    
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private MemberServiceImpl memberServiceImpl;
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 이메일 중복 확인
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @GetMapping("/check-email")
    public Map<String, Object> checkEmail(@RequestParam(name = "email") String email) {
        Map<String, Object> result = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📧 이메일 중복 확인 API 호출");
        logger.info("  - 이메일: {}", email);
        
        try {
            boolean available = memberService.checkEmailAvailable(email);
            result.put("available", available);
            result.put("success", true);
            
            if (available) {
                result.put("message", "사용 가능한 이메일입니다.");
                logger.info("  ✅ 사용 가능");
            } else {
                result.put("message", "이미 사용 중인 이메일입니다.");
                logger.info("  ❌ 사용 불가");
            }
            
        } catch (Exception e) {
            logger.error("❌ 이메일 확인 오류: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "이메일 확인 중 오류가 발생했습니다.");
        }
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return result;
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 인증 번호 발송
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @PostMapping("/send-verification")
    public Map<String, Object> sendVerification(@RequestParam(name = "email") String email) {
        Map<String, Object> result = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📨 인증번호 발송 API 호출");
        logger.info("  - 이메일: {}", email);
        
        try {
            // 1. 인증 코드 생성
            String code = memberService.generateVerificationCode();
            
            // 2. 인증 코드 저장 (이메일과 연결)
            memberServiceImpl.saveVerificationCode(email, code);
            
            // 3. 실제 이메일 발송 (TODO)
            // emailService.sendVerificationEmail(email, code);
            
            logger.info("  ✅ 인증번호 생성 완료: {}", code);
            logger.info("  📧 이메일 발송 (실제 발송은 TODO)");
            
            result.put("success", true);
            result.put("message", "인증번호가 발송되었습니다.");
            result.put("code", code); // ⚠️ 개발용 - 운영에서는 제거!
            
        } catch (Exception e) {
            logger.error("❌ 인증번호 발송 오류: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "인증번호 발송에 실패했습니다.");
        }
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return result;
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 인증 번호 확인
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @PostMapping("/verify-code")
    public Map<String, Object> verifyCode(
            @RequestParam String email, 
            @RequestParam(name = "code") String code) {
        
        Map<String, Object> result = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("✅ 인증번호 확인 API 호출");
        logger.info("  - 이메일: {}", email);
        logger.info("  - 입력 코드: {}", code);
        
        try {
            boolean verified = memberService.verifyCode(email, code);
            
            result.put("verified", verified);
            result.put("success", true);
            
            if (verified) {
                result.put("message", "인증이 완료되었습니다.");
                logger.info("  ✅ 인증 성공!");
            } else {
                result.put("message", "인증번호가 일치하지 않습니다.");
                logger.info("  ❌ 인증 실패");
            }
            
        } catch (Exception e) {
            logger.error("❌ 인증 확인 오류: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "인증 확인 중 오류가 발생했습니다.");
        }
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return result;
    }
}
