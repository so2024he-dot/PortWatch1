package com.portwatch.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.portwatch.domain.MemberVO;
import com.portwatch.service.StockService;

/**
 * 홈 컨트롤러
 * 
 * @author PortWatch Team
 * @version 2.0
 */
@Controller
public class HomeController {
    
    @Autowired(required = false)
    private StockService stockService;
    
    /**
     * 메인 페이지
     */
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        
        try {
            // 로그인 여부 확인
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            
            if (loginMember != null) {
                model.addAttribute("loginMember", loginMember);
            }
            
        } catch (Exception e) {
            System.err.println("홈 페이지 로딩 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "home";
    }
    
    /**
     * 대시보드 페이지 (로그인 필요)
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        model.addAttribute("loginMember", loginMember);
        
        return "dashboard/index";
    }
}
