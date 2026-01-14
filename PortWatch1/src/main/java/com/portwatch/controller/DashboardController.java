package com.portwatch.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.service.PortfolioService;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * DashboardController - Dashboard 페이지 처리
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 해결:
 * http://localhost:8088/dashboard → 404 에러 해결!
 * 
 * @author PortWatch
 * @version FINAL
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired(required = false)
    private PortfolioService portfolioService;
    
    /**
     * ✅ Dashboard 메인 페이지
     * 
     * http://localhost:8088/dashboard
     * 
     * @param session HTTP 세션
     * @param model 모델
     * @return dashboard 뷰
     */
    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Dashboard 접근: /dashboard");
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("member");
        
        if (loginMember == null) {
            System.out.println("  ❌ 비로그인 상태");
            System.out.println("  → 로그인 페이지로 리다이렉트");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        try {
            System.out.println("  ✅ 로그인 상태 확인");
            System.out.println("  회원 ID: " + loginMember.getMemberId());
            System.out.println("  회원 이름: " + loginMember.getMemberName());
            
            // 모델에 회원 정보 추가
            model.addAttribute("member", loginMember);
            
            // 포트폴리오 서비스가 있으면 포트폴리오 정보 조회
            if (portfolioService != null) {
                try {
                    List<PortfolioVO> portfolioList = portfolioService.getPortfolioByMemberId(
                        loginMember.getMemberId()
                    );
                    model.addAttribute("portfolioList", portfolioList);
                    System.out.println("  포트폴리오 개수: " + portfolioList.size());
                } catch (Exception e) {
                    System.err.println("  ⚠️ 포트폴리오 조회 실패: " + e.getMessage());
                    model.addAttribute("portfolioList", null);
                }
            }
            
            System.out.println("  → Dashboard 페이지 표시");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "dashboard/dashboard";
            
        } catch (Exception e) {
            System.err.println("❌ Dashboard 로딩 중 오류: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            model.addAttribute("errorMessage", "Dashboard를 로드하는 중 오류가 발생했습니다.");
            return "error/error";
        }
    }
    
    /**
     * ✅ Dashboard 통계 페이지
     */
    @GetMapping("/stats")
    public String dashboardStats(HttpSession session, Model model) {
        System.out.println("📊 Dashboard 통계 접근: /dashboard/stats");
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("member");
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        model.addAttribute("member", loginMember);
        return "dashboard/stats";
    }
    
    /**
     * ✅ Dashboard 포트폴리오 페이지
     */
    @GetMapping("/portfolio")
    public String dashboardPortfolio(HttpSession session, Model model) {
        System.out.println("📊 Dashboard 포트폴리오 접근: /dashboard/portfolio");
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("member");
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        model.addAttribute("member", loginMember);
        return "dashboard/portfolio";
    }
    
    /**
     * ✅ Dashboard 설정 페이지
     */
    @GetMapping("/settings")
    public String dashboardSettings(HttpSession session, Model model) {
        System.out.println("📊 Dashboard 설정 접근: /dashboard/settings");
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("member");
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        model.addAttribute("member", loginMember);
        return "dashboard/settings";
    }
}
