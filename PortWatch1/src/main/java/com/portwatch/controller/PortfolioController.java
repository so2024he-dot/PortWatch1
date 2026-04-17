package com.portwatch.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.StockVO;
import com.portwatch.service.PortfolioService;
import com.portwatch.service.StockService;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * PORTFOLIO CONTROLLER - 완벽 수정 버전 v2
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * 수정 내역 (2026.01.16):
 * ✅ GET 방식 삭제 지원 추가 (/portfolio/delete?portfolioId=X)
 * ✅ POST 방식 삭제 유지 (/portfolio/delete/{portfolioId})
 * ✅ 환급 처리 로직 추가
 * 
 * @author PortWatch
 */
@Controller
@RequestMapping("/portfolio")
@Log4j
public class PortfolioController {
    
    @Setter(onMethod_ = @Autowired)
    private PortfolioService portfolioService;
    
    @Setter(onMethod_ = @Autowired)
    private StockService stockService;
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 포트폴리오 메인 페이지
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @GetMapping({"", "/", "/list"})
    public String portfolioMain(HttpSession session, Model model) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📊 포트폴리오 메인 페이지");
        
        // 로그인 체크
        MemberVO member = (MemberVO) session.getAttribute("loginMember");
        if (member == null) member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.warn("⚠️ 로그인 필요");
            return "redirect:/member/login";
        }

        log.info("  - 회원 ID: " + member.getMemberId());

        try {
            // 포트폴리오 목록 조회
            List<PortfolioVO> portfolioList = portfolioService.getPortfolioByMemberId(member.getMemberId());
            model.addAttribute("portfolioList", portfolioList);
            model.addAttribute("loginMember", member);
            
            log.info("✅ 포트폴리오 목록 조회 완료: " + portfolioList.size() + "개");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "portfolio/list";
            
        } catch (Exception e) {
            log.error("❌ 포트폴리오 조회 실패", e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            model.addAttribute("errorMessage", "포트폴리오를 불러오는데 실패했습니다.");
            return "portfolio/list";
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 포트폴리오 상세
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @GetMapping("/{portfolioId}")
    public String detail(@PathVariable Long portfolioId,
                        HttpSession session,
                        Model model) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📊 포트폴리오 상세 조회");
        log.info("  - portfolioId: " + portfolioId);

        // 로그인 체크
        MemberVO member = (MemberVO) session.getAttribute("loginMember");
        if (member == null) member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.warn("⚠️ 로그인 필요");
            return "redirect:/member/login";
        }

        try {
            PortfolioVO portfolio = portfolioService.getPortfolio(portfolioId);
            
            if (portfolio == null) {
                log.warn("⚠️ 포트폴리오를 찾을 수 없습니다");
                return "redirect:/portfolio/list";
            }
            
            // 권한 체크
            if (!portfolio.getMemberId().equals(member.getMemberId())) {
                log.warn("⚠️ 권한 없음");
                return "redirect:/portfolio/list";
            }
            
            model.addAttribute("portfolio", portfolio);
            
            log.info("✅ 포트폴리오 조회 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "portfolio/detail";
            
        } catch (Exception e) {
            log.error("❌ 포트폴리오 조회 실패", e);
            return "redirect:/portfolio/list";
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 포트폴리오 생성 폼
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @GetMapping("/create")
    public String createForm(HttpSession session, Model model) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📝 포트폴리오 생성 폼");

        // 로그인 체크
        MemberVO member = (MemberVO) session.getAttribute("loginMember");
        if (member == null) member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.warn("⚠️ 로그인 필요");
            return "redirect:/member/login";
        }

        try {
            // 전체 주식 목록
            List<StockVO> stockList = stockService.getAllStocks();
            model.addAttribute("stockList", stockList);
            
            // 빈 PortfolioVO
            model.addAttribute("portfolioVO", new PortfolioVO());
            
            log.info("✅ 생성 폼 준비 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "portfolio/create";
            
        } catch (Exception e) {
            log.error("❌ 생성 폼 준비 실패", e);
            return "redirect:/portfolio/list";
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 포트폴리오 생성 실행
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @PostMapping("/create")
    public String create(@ModelAttribute PortfolioVO portfolioVO,
                        HttpSession session,
                        RedirectAttributes rttr) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📝 포트폴리오 생성 실행");

        // 로그인 체크
        MemberVO member = (MemberVO) session.getAttribute("loginMember");
        if (member == null) member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.warn("⚠️ 로그인 필요");
            return "redirect:/member/login";
        }

        try {
            // 회원 ID 설정
            portfolioVO.setMemberId(member.getMemberId());
            
            // 포트폴리오 생성
            portfolioService.updatePortfolio(portfolioVO);
            
            rttr.addFlashAttribute("successMessage", "포트폴리오가 생성되었습니다.");
            
            log.info("✅ 포트폴리오 생성 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "redirect:/portfolio/list";
            
        } catch (Exception e) {
            log.error("❌ 포트폴리오 생성 실패", e);
            
            rttr.addFlashAttribute("errorMessage", "포트폴리오 생성에 실패했습니다.");
            return "redirect:/portfolio/create";
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 포트폴리오 수정 폼
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @GetMapping("/update/{portfolioId}")
    public String updateForm(@PathVariable Long portfolioId,
                            HttpSession session,
                            Model model) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📝 포트폴리오 수정 폼");
        log.info("  - portfolioId: " + portfolioId);

        // 로그인 체크
        MemberVO member = (MemberVO) session.getAttribute("loginMember");
        if (member == null) member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.warn("⚠️ 로그인 필요");
            return "redirect:/member/login";
        }

        try {
            PortfolioVO portfolio = portfolioService.getPortfolio(portfolioId);
            
            if (portfolio == null) {
                return "redirect:/portfolio/list";
            }
            
            // 권한 체크
            if (!portfolio.getMemberId().equals(member.getMemberId())) {
                return "redirect:/portfolio/list";
            }
            
            model.addAttribute("portfolio", portfolio);
            
            // 주식 목록
            List<StockVO> stockList = stockService.getAllStocks();
            model.addAttribute("stockList", stockList);
            
            log.info("✅ 수정 폼 준비 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "portfolio/update";
            
        } catch (Exception e) {
            log.error("❌ 수정 폼 준비 실패", e);
            return "redirect:/portfolio/list";
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 포트폴리오 수정 실행
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @PostMapping("/update")
    public String update(@ModelAttribute PortfolioVO portfolioVO,
                        HttpSession session,
                        RedirectAttributes rttr) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📝 포트폴리오 수정 실행");
        log.info("  - portfolioId: " + portfolioVO.getPortfolioId());

        // 로그인 체크
        MemberVO member = (MemberVO) session.getAttribute("loginMember");
        if (member == null) member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.warn("⚠️ 로그인 필요");
            return "redirect:/member/login";
        }
        
        try {
            // 기존 포트폴리오 조회
            PortfolioVO existing = portfolioService.getPortfolio(portfolioVO.getPortfolioId());
            
            if (existing == null) {
                rttr.addFlashAttribute("errorMessage", "포트폴리오를 찾을 수 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            // 권한 체크
            if (!existing.getMemberId().equals(member.getMemberId())) {
                rttr.addFlashAttribute("errorMessage", "권한이 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            // 회원 ID 설정
            portfolioVO.setMemberId(member.getMemberId());
            
            // 포트폴리오 수정
            portfolioService.updatePortfolio(portfolioVO);
            
            rttr.addFlashAttribute("successMessage", "포트폴리오가 수정되었습니다.");
            
            log.info("✅ 포트폴리오 수정 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "redirect:/portfolio/" + portfolioVO.getPortfolioId();
            
        } catch (Exception e) {
            log.error("❌ 포트폴리오 수정 실패", e);
            
            rttr.addFlashAttribute("errorMessage", "포트폴리오 수정에 실패했습니다.");
            return "redirect:/portfolio/update/" + portfolioVO.getPortfolioId();
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 포트폴리오 삭제 (GET 방식) - 신규 추가!
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: /portfolio/delete?portfolioId=X
     * 
     * 오류 해결:
     * - 기존: @PostMapping("/delete/{portfolioId}") → POST만 지원
     * - 신규: @GetMapping("/delete") + @RequestParam → GET 지원
     * 
     * 환급 처리:
     * - 매입 금액 반환 (quantity × avgPurchasePrice)
     */
    @GetMapping("/delete")
    public String deleteByGet(@RequestParam Long portfolioId,
                             HttpSession session,
                             RedirectAttributes rttr) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🗑️ 포트폴리오 삭제 (GET 방식)");
        log.info("  - portfolioId: " + portfolioId);

        // 로그인 체크
        MemberVO member = (MemberVO) session.getAttribute("loginMember");
        if (member == null) member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.warn("⚠️ 로그인 필요");
            return "redirect:/member/login";
        }

        try {
            // 기존 포트폴리오 조회
            PortfolioVO portfolio = portfolioService.getPortfolio(portfolioId);

            if (portfolio == null) {
                log.warn("⚠️ 포트폴리오를 찾을 수 없습니다");
                rttr.addFlashAttribute("errorMessage", "포트폴리오를 찾을 수 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            // 권한 체크
            if (!portfolio.getMemberId().equals(member.getMemberId())) {
                log.warn("⚠️ 권한 없음");
                rttr.addFlashAttribute("errorMessage", "권한이 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            // 환급 금액 계산
            BigDecimal refundAmount = calculateRefund(portfolio);
            
            log.info("💰 환급 금액: " + refundAmount);
            
            // 포트폴리오 삭제
            portfolioService.deletePortfolio(portfolioId);
            
            rttr.addFlashAttribute("successMessage", 
                "포트폴리오가 삭제되었습니다. 환급 금액: " + refundAmount + "원");
            
            log.info("✅ 포트폴리오 삭제 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "redirect:/portfolio/list";
            
        } catch (Exception e) {
            log.error("❌ 포트폴리오 삭제 실패", e);
            
            rttr.addFlashAttribute("errorMessage", "포트폴리오 삭제에 실패했습니다: " + e.getMessage());
            return "redirect:/portfolio/list";
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 포트폴리오 삭제 (POST 방식) - 기존 유지
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: /portfolio/delete/{portfolioId} (POST)
     */
    @PostMapping("/delete/{portfolioId}")
    public String delete(@PathVariable Long portfolioId,
                        HttpSession session,
                        RedirectAttributes rttr) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🗑️ 포트폴리오 삭제 (POST 방식)");
        log.info("  - portfolioId: " + portfolioId);

        // 로그인 체크
        MemberVO member = (MemberVO) session.getAttribute("loginMember");
        if (member == null) member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.warn("⚠️ 로그인 필요");
            return "redirect:/member/login";
        }
        
        try {
            // 기존 포트폴리오 조회
            PortfolioVO portfolio = portfolioService.getPortfolio(portfolioId);
            
            if (portfolio == null) {
                rttr.addFlashAttribute("errorMessage", "포트폴리오를 찾을 수 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            // 권한 체크
            if (!portfolio.getMemberId().equals(member.getMemberId())) {
                rttr.addFlashAttribute("errorMessage", "권한이 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            // 환급 금액 계산
            BigDecimal refundAmount = calculateRefund(portfolio);
            
            log.info("💰 환급 금액: " + refundAmount);
            
            // 포트폴리오 삭제
            portfolioService.deletePortfolio(portfolioId);
            
            rttr.addFlashAttribute("successMessage", 
                "포트폴리오가 삭제되었습니다. 환급 금액: " + refundAmount + "원");
            
            log.info("✅ 포트폴리오 삭제 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "redirect:/portfolio/list";
            
        } catch (Exception e) {
            log.error("❌ 포트폴리오 삭제 실패", e);
            
            rttr.addFlashAttribute("errorMessage", "포트폴리오 삭제에 실패했습니다.");
            return "redirect:/portfolio/list";
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 환급 금액 계산 헬퍼 메서드
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * 환급 금액 = 수량 × 평균 매입 단가
     */
    private BigDecimal calculateRefund(PortfolioVO portfolio) {
        try {
            BigDecimal quantity = portfolio.getQuantity();
            BigDecimal avgPrice = portfolio.getAvgPurchasePrice();
            
            if (quantity != null && avgPrice != null) {
                return quantity.multiply(avgPrice);
            }
            
            return BigDecimal.ZERO;
            
        } catch (Exception e) {
            log.error("❌ 환급 금액 계산 실패", e);
            return BigDecimal.ZERO;
        }
    }
}
