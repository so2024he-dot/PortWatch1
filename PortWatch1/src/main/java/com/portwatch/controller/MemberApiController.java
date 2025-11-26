package com.portwatch.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.portwatch.service.MemberService;

@RestController
@RequestMapping("/api/member")
public class MemberApiController {
    
    @Autowired
    private MemberService memberService;
    
    @GetMapping("/check-email")
    public Map<String, Object> checkEmail(@RequestParam String email) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean available = memberService.checkEmailAvailable(email);
            result.put("available", available);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/send-verification")
    public Map<String, Object> sendVerification(@RequestParam String email) {
        Map<String, Object> result = new HashMap<>();
        try {
            String code = memberService.generateVerificationCode();
            // TODO: 실제 이메일 발송
            result.put("success", true);
            result.put("message", "인증번호가 발송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "인증번호 발송에 실패했습니다.");
        }
        return result;
    }
    
    @PostMapping("/verify-code")
    public Map<String, Object> verifyCode(@RequestParam String email, 
                                          @RequestParam String code) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean verified = memberService.verifyCode(email, code);
            result.put("verified", verified);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
