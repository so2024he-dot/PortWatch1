package com.portwatch.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * DashboardController - 완전판 (총 평가액/손익 계산 추가!)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 추가 기능:
 * - 총 평가액 계산
 * - 총 손익 계산
 * - 총 수익률 계산
 * - 총 투자원금 계산
 * 
 * @author PortWatch
 * @version COMPLETE - 2026.01.16
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired(required = false)
    private PortfolioService portfolioService;
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ Dashboard 메인 페이지 (계산 로직 추가!)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
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
            
            // ✅ 포트폴리오 서비스가 있으면 포트폴리오 정보 조회
            if (portfolioService != null) {
                try {
                    List<PortfolioVO> portfolioList = portfolioService.getPortfolioByMemberId(
                        loginMember.getMemberId()
                    );
                    
                    // ✅ 총 평가액, 총 손익 등 계산
                    DashboardSummary summary = calculateDashboardSummary(portfolioList);
                    
                    // 모델에 추가
                    model.addAttribute("portfolioList", portfolioList);
                    model.addAttribute("totalValue", summary.totalValue);
                    model.addAttribute("totalCost", summary.totalCost);
                    model.addAttribute("totalProfit", summary.totalProfit);
                    model.addAttribute("totalProfitRate", summary.totalProfitRate);
                    model.addAttribute("portfolioCount", portfolioList.size());
                    
                    System.out.println("  포트폴리오 개수: " + portfolioList.size());
                    System.out.println("  총 평가액: " + formatNumber(summary.totalValue));
                    System.out.println("  총 투자원금: " + formatNumber(summary.totalCost));
                    System.out.println("  총 손익: " + formatNumber(summary.totalProfit));
                    System.out.println("  총 수익률: " + String.format("%.2f", summary.totalProfitRate) + "%");
                    
                } catch (Exception e) {
                    System.err.println("  ⚠️ 포트폴리오 조회 실패: " + e.getMessage());
                    e.printStackTrace();
                    
                    // 기본값 설정
                    model.addAttribute("portfolioList", null);
                    model.addAttribute("totalValue", BigDecimal.ZERO);
                    model.addAttribute("totalCost", BigDecimal.ZERO);
                    model.addAttribute("totalProfit", BigDecimal.ZERO);
                    model.addAttribute("totalProfitRate", 0.0);
                    model.addAttribute("portfolioCount", 0);
                }
            } else {
                System.out.println("  ⚠️ PortfolioService가 없습니다");
                
                // 기본값 설정
                model.addAttribute("portfolioList", null);
                model.addAttribute("totalValue", BigDecimal.ZERO);
                model.addAttribute("totalCost", BigDecimal.ZERO);
                model.addAttribute("totalProfit", BigDecimal.ZERO);
                model.addAttribute("totalProfitRate", 0.0);
                model.addAttribute("portfolioCount", 0);
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
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ Dashboard 요약 정보 계산 (신규!)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * 계산 공식:
     * - 총 평가액 = Σ(수량 × 현재가)
     * - 총 투자원금 = Σ(수량 × 평균 매입가)
     * - 총 손익 = 총 평가액 - 총 투자원금
     * - 총 수익률 = (총 손익 / 총 투자원금) × 100
     */
    private DashboardSummary calculateDashboardSummary(List<PortfolioVO> portfolioList) {
        System.out.println("  💰 Dashboard 요약 정보 계산 중...");
        
        DashboardSummary summary = new DashboardSummary();
        
        if (portfolioList == null || portfolioList.isEmpty()) {
            System.out.println("  ⚠️ 포트폴리오가 비어있습니다");
            return summary;
        }
        
        BigDecimal totalValue = BigDecimal.ZERO;      // 총 평가액
        BigDecimal totalCost = BigDecimal.ZERO;       // 총 투자원금
        
        for (PortfolioVO portfolio : portfolioList) {
            try {
                // 수량
                BigDecimal quantity = portfolio.getQuantity();
                if (quantity == null) {
                    quantity = BigDecimal.ZERO;
                }
                
                // 현재가
                BigDecimal currentPrice = portfolio.getCurrentPrice();
                if (currentPrice == null) {
                    currentPrice = BigDecimal.ZERO;
                }
                
                // 평균 매입가
                BigDecimal avgPrice = portfolio.getAvgPrice();
                if (avgPrice == null) {
                    avgPrice = portfolio.getAvgPurchasePrice();
                }
                if (avgPrice == null) {
                    avgPrice = BigDecimal.ZERO;
                }
                
                // 평가액 = 수량 × 현재가
                BigDecimal itemValue = quantity.multiply(currentPrice);
                
                // 투자원금 = 수량 × 평균 매입가
                BigDecimal itemCost = quantity.multiply(avgPrice);
                
                // 누적
                totalValue = totalValue.add(itemValue);
                totalCost = totalCost.add(itemCost);
                
                System.out.println("    - " + portfolio.getStockName() + 
                                   ": 평가액 " + formatNumber(itemValue) + 
                                   ", 원금 " + formatNumber(itemCost));
                
            } catch (Exception e) {
                System.err.println("    ⚠️ 계산 오류 (" + portfolio.getStockName() + "): " + e.getMessage());
            }
        }
        
        // 총 손익 = 총 평가액 - 총 투자원금
        BigDecimal totalProfit = totalValue.subtract(totalCost);
        
        // 총 수익률 = (총 손익 / 총 투자원금) × 100
        double totalProfitRate = 0.0;
        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
            totalProfitRate = totalProfit
                .divide(totalCost, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .doubleValue();
        }
        
        summary.totalValue = totalValue;
        summary.totalCost = totalCost;
        summary.totalProfit = totalProfit;
        summary.totalProfitRate = totalProfitRate;
        
        return summary;
    }
    
    /**
     * 숫자 포맷팅 (콤마 추가)
     */
    private String formatNumber(BigDecimal number) {
        if (number == null) return "0";
        return String.format("%,d", number.longValue());
    }
    
    /**
     * Dashboard 요약 정보 DTO
     */
    private static class DashboardSummary {
        BigDecimal totalValue = BigDecimal.ZERO;       // 총 평가액
        BigDecimal totalCost = BigDecimal.ZERO;        // 총 투자원금
        BigDecimal totalProfit = BigDecimal.ZERO;      // 총 손익
        double totalProfitRate = 0.0;                  // 총 수익률(%)
    }
    
    /**
     * ✅ Dashboard 통계 페이지
     */
    @GetMapping("/stats")
    public String dashboardStats(HttpSession session, Model model) {
        MemberVO loginMember = (MemberVO) session.getAttribute("member");
        
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        model.addAttribute("member", loginMember);
        return "dashboard/stats";
    }
    
    /**
     * ✅ Dashboard 설정 페이지
     */
    @GetMapping("/settings")
    public String dashboardSettings(HttpSession session, Model model) {
        MemberVO loginMember = (MemberVO) session.getAttribute("member");
        
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        model.addAttribute("member", loginMember);
        return "dashboard/settings";
    }
    
    /**
     * ✅ Dashboard 포트폴리오 페이지
     */
    @GetMapping("/portfolio")
    public String dashboardPortfolio(HttpSession session, Model model) {
        MemberVO loginMember = (MemberVO) session.getAttribute("member");
        
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        model.addAttribute("member", loginMember);
        return "dashboard/portfolio";
    }
}
