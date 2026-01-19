package com.portwatch.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.portwatch.domain.MemberVO;

import lombok.extern.log4j.Log4j;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * HomeController - 메인 페이지
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * @author PortWatch
 * @version 1.0 - 2026.01.16
 */
@Controller
@Log4j
public class HomeController {
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 메인 페이지
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /
     */
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🏠 메인 페이지 접속");
        
        // 로그인 체크
        MemberVO member = (MemberVO) session.getAttribute("member");
        
        if (member != null) {
            log.info("  - 로그인 회원: " + member.getMemberId());
            
            // 로그인 상태면 Dashboard로 리다이렉트
            log.info("  → Dashboard로 리다이렉트");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "redirect:/dashboard";
        }
        
        log.info("  - 비로그인 상태");
        log.info("  → 홈 페이지 표시");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return "home";
    }
}
