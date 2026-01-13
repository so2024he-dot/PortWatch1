package com.portwatch.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.portwatch.domain.MemberVO;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * HomeController - 홈페이지 404 에러 해결
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 기능:
 * 1. 루트 URL (/) 처리
 * 2. 로그인 여부에 따라 리다이렉트
 * 3. 로그인: Dashboard로 이동
 * 4. 미로그인: 로그인 페이지로 이동
 * 
 * @version 1.0
 */
@Controller
public class HomeController {
    
    /**
     * ✅ 홈페이지 (루트 URL)
     * URL: GET /
     * 
     * 로그인 상태에 따라 다른 페이지로 리다이렉트:
     * - 로그인 O → /dashboard
     * - 로그인 X → /member/login
     */
    @GetMapping("/")
    public String home(HttpSession session) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🏠 홈페이지 접근");
        
        // 로그인 여부 확인
        MemberVO member = (MemberVO) session.getAttribute("member");
        
        if (member != null) {
            // 로그인 O → Dashboard로 이동
            System.out.println("  - 로그인 상태: " + member.getMemberId());
            System.out.println("  - 이동: Dashboard");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/dashboard";
        } else {
            // 로그인 X → 로그인 페이지로 이동
            System.out.println("  - 로그인 상태: 미로그인");
            System.out.println("  - 이동: 로그인 페이지");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
    }
    
    /**
     * ✅ index 페이지
     * URL: GET /index
     * 
     * 루트 URL과 동일한 동작
     */
    @GetMapping("/index")
    public String index(HttpSession session) {
        return home(session);
    }
    
    /**
     * ✅ 에러 페이지 테스트
     * URL: GET /error-test
     */
    @GetMapping("/error-test")
    public String errorTest() {
        throw new RuntimeException("테스트 에러입니다.");
    }
}
