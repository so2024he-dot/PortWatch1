package com.portwatch.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.portwatch.domain.MemberVO;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * HomeController - 루트 URL 및 기본 페이지 처리
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 해결:
 * http://localhost:8088/ → 404 에러 해결!
 * 
 * 로직:
 * - 로그인 상태: Dashboard로 이동
 * - 비로그인 상태: 로그인 페이지로 이동
 * 
 * @author PortWatch
 * @version FINAL
 */
@Controller
public class HomeController {
    
    /**
     * ✅ 루트 URL 처리
     * 
     * http://localhost:8088/
     * 
     * @param session HTTP 세션
     * @return 로그인 상태에 따라 Dashboard 또는 로그인 페이지
     */
    @GetMapping("/")
    public String home(HttpSession session) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🏠 루트 URL 접근: /");
        
        // 세션에서 로그인 정보 확인
        MemberVO loginMember = (MemberVO) session.getAttribute("member");
        
        if (loginMember != null) {
            // 로그인 상태 → Dashboard로 이동
            System.out.println("  ✅ 로그인 상태 확인");
            System.out.println("  회원 ID: " + loginMember.getMemberId());
            System.out.println("  → Dashboard로 리다이렉트");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/dashboard";
        } else {
            // 비로그인 상태 → 로그인 페이지로 이동
            System.out.println("  ❌ 비로그인 상태");
            System.out.println("  → 로그인 페이지로 리다이렉트");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
    }
    
    /**
     * ✅ index 페이지 처리 (대체 경로)
     */
    @GetMapping("/index")
    public String index(HttpSession session) {
        return home(session);
    }
    
    /**
     * ✅ main 페이지 처리 (대체 경로)
     */
    @GetMapping("/main")
    public String main(HttpSession session) {
        return home(session);
    }
}
