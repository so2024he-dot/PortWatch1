package com.portwatch.controller;

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
 * PORTFOLIO CONTROLLER - 완벽 수정 버전
 * Spring 5.0.7 + MySQL 8.0.33
 * 
 * 수정 내역:
 * 1. @RequestMapping("/portfolio") 설정 (404 해결)
 * 2. 메인 페이지 매핑: "", "/", "/list" 모두 처리
 * 3. portfolioVO Model 추가
 * 4. 세션 체크 개선
 * 5. ✅ getPortfolio 메서드 구현 완료
 * 6. ✅ modify, remove 메서드 수정 (portfolioService 사용)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
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
     * ✅ 포트폴리오 메인 페이지 (/, "", /list 모두 처리)
     * URL: /portfolio, /portfolio/, /portfolio/list
     */
    @GetMapping(value = {"", "/", "/list"})
    public String portfolioMain(HttpSession session, Model model) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📊 포트폴리오 메인 페이지");
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.info("❌ 로그인 필요");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        String memberId = member.getMemberId();
        log.info("  - 회원 ID: " + memberId);
        
        try {
            List<PortfolioVO> portfolioList = portfolioService.getPortfolioList(memberId);
            model.addAttribute("portfolioList", portfolioList);
            log.info("✅ 포트폴리오 목록 조회 완료: " + portfolioList.size() + "개");
        } catch (Exception e) {
            log.error("❌ 포트폴리오 조회 실패: " + e.getMessage(), e);
            model.addAttribute("portfolioList", List.of());
            model.addAttribute("errorMessage", "포트폴리오 조회에 실패했습니다.");
        }
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return "portfolio/list";
    }
    
    /**
     * ✅ 포트폴리오 등록 페이지
     * URL: /portfolio/create (GET)
     */
    @GetMapping("/create")
    public String createForm(HttpSession session, Model model) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📝 포트폴리오 등록 페이지");
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.info("❌ 로그인 필요");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        try {
            // ✅ portfolioVO를 Model에 추가 (BindingResult 에러 해결)
            model.addAttribute("portfolioVO", new PortfolioVO());
            
            List<StockVO> stockList = stockService.getAllStocks();
            model.addAttribute("stockList", stockList);
            log.info("✅ 주식 목록 조회 완료: " + stockList.size() + "개");
            log.info("✅ portfolioVO 추가 완료");
        } catch (Exception e) {
            log.error("❌ 주식 목록 조회 실패: " + e.getMessage(), e);
            model.addAttribute("stockList", List.of());
            model.addAttribute("errorMessage", "주식 목록 조회에 실패했습니다.");
        }
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return "portfolio/create";
    }
    
    /**
     * ✅ 포트폴리오 등록 처리
     * URL: /portfolio/create (POST)
     */
    @PostMapping("/create")
    public String create(@ModelAttribute PortfolioVO portfolio, 
                        HttpSession session, 
                        RedirectAttributes rttr) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📝 포트폴리오 등록 처리");
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.info("❌ 로그인 필요");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        try {
            portfolio.setMemberId(member.getMemberId());
            
            // purchasePrice가 null이면 0으로 설정
            if (portfolio.getPurchasePrice() == null) {
                portfolio.setPurchasePrice(0.0);
            }
            
            portfolioService.register(portfolio);
            
            log.info("✅ 포트폴리오 등록 완료");
            log.info("  - 주식 ID: " + portfolio.getStockId());
            log.info("  - 수량: " + portfolio.getQuantity());
            log.info("  - 매입 단가: " + portfolio.getPurchasePrice());
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("message", "포트폴리오가 등록되었습니다.");
            return "redirect:/portfolio/list";
        } catch (Exception e) {
            log.error("❌ 포트폴리오 등록 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("errorMessage", "포트폴리오 등록에 실패했습니다: " + e.getMessage());
            return "redirect:/portfolio/create";
        }
    }
    
    /**
     * ✅ 포트폴리오 상세 조회
     * URL: /portfolio/{portfolioId}
     */
    @GetMapping("/{portfolioId}")
    public String detail(@PathVariable Long portfolioId, 
                        HttpSession session, 
                        Model model) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📊 포트폴리오 상세 조회");
        log.info("  - 포트폴리오 ID: " + portfolioId);
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.info("❌ 로그인 필요");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        try {
            // ✅ Service를 통한 조회
            PortfolioVO portfolio = portfolioService.getPortfolio(portfolioId);
            
            if (portfolio == null) {
                log.warn("⚠️ 포트폴리오를 찾을 수 없음: ID = " + portfolioId);
                model.addAttribute("errorMessage", "포트폴리오를 찾을 수 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            model.addAttribute("portfolio", portfolio);
            
            log.info("✅ 포트폴리오 상세 조회 완료");
            log.info("  - 종목명: " + portfolio.getStockName());
            log.info("  - 보유 수량: " + portfolio.getQuantity());
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        } catch (Exception e) {
            log.error("❌ 포트폴리오 조회 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            model.addAttribute("errorMessage", "포트폴리오 조회에 실패했습니다.");
            return "redirect:/portfolio/list";
        }
        
        return "portfolio/detail";
    }
    
    /**
     * ✅ 포트폴리오 수정 페이지
     * URL: /portfolio/update/{portfolioId}
     */
    @GetMapping("/update/{portfolioId}")
    public String updateForm(@PathVariable Long portfolioId, 
                            HttpSession session, 
                            Model model) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("✏️ 포트폴리오 수정 페이지");
        log.info("  - 포트폴리오 ID: " + portfolioId);
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.info("❌ 로그인 필요");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        try {
            PortfolioVO portfolio = portfolioService.getPortfolio(portfolioId);
            
            if (portfolio == null) {
                log.warn("⚠️ 포트폴리오를 찾을 수 없음: ID = " + portfolioId);
                model.addAttribute("errorMessage", "포트폴리오를 찾을 수 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            model.addAttribute("portfolio", portfolio);
            
            List<StockVO> stockList = stockService.getAllStocks();
            model.addAttribute("stockList", stockList);
            
            log.info("✅ 포트폴리오 조회 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        } catch (Exception e) {
            log.error("❌ 포트폴리오 조회 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            model.addAttribute("errorMessage", "포트폴리오 조회에 실패했습니다.");
            return "redirect:/portfolio/list";
        }
        
        return "portfolio/update";
    }
    
    /**
     * ✅ 포트폴리오 수정 처리
     * URL: /portfolio/update (POST)
     */
    @PostMapping("/update")
    @Transactional
    public String update(@ModelAttribute PortfolioVO portfolio,
                        HttpSession session,
                        RedirectAttributes rttr) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("✏️ 포트폴리오 수정 처리");
        log.info("  - 포트폴리오 ID: " + portfolio.getPortfolioId());
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.info("❌ 로그인 필요");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        try {
            // ✅ portfolioService를 사용한 수정
            portfolioService.updatePortfolio(portfolio);
            
            log.info("✅ 포트폴리오 수정 완료");
            log.info("  - 수량: " + portfolio.getQuantity());
            log.info("  - 매입가: " + portfolio.getPurchasePrice());
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("message", "포트폴리오가 수정되었습니다.");
            return "redirect:/portfolio/" + portfolio.getPortfolioId();
            
        } catch (Exception e) {
            log.error("❌ 포트폴리오 수정 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("errorMessage", "포트폴리오 수정에 실패했습니다: " + e.getMessage());
            return "redirect:/portfolio/update/" + portfolio.getPortfolioId();
        }
    }
    
    /**
     * ✅ 포트폴리오 삭제 처리
     * URL: /portfolio/delete/{portfolioId} (POST)
     */
    @PostMapping("/delete/{portfolioId}")
    @Transactional
    public String delete(@PathVariable Long portfolioId,
                        HttpSession session,
                        RedirectAttributes rttr) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🗑️ 포트폴리오 삭제");
        log.info("  - 포트폴리오 ID: " + portfolioId);
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            log.info("❌ 로그인 필요");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        try {
            // ✅ portfolioService를 사용한 삭제
            portfolioService.deletePortfolio(portfolioId);
            
            log.info("✅ 포트폴리오 삭제 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("message", "포트폴리오가 삭제되었습니다.");
            return "redirect:/portfolio/list";
            
        } catch (Exception e) {
            log.error("❌ 포트폴리오 삭제 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("errorMessage", "포트폴리오 삭제에 실패했습니다: " + e.getMessage());
            return "redirect:/portfolio/" + portfolioId;
        }
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 유틸리티 메서드
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * ✅ 포트폴리오 ID로 조회 (Exception 버전)
     * 다른 메서드에서 사용할 수 있는 유틸리티 메서드
     */
    public PortfolioVO getPortfolioById(Long portfolioId) throws Exception {
        log.debug("🔍 포트폴리오 ID로 조회: " + portfolioId);
        
        if (portfolioId == null || portfolioId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 포트폴리오 ID: " + portfolioId);
        }
        
        // ✅ Service를 통한 조회
        PortfolioVO portfolio = portfolioService.getPortfolio(portfolioId);
        
        if (portfolio == null) {
            throw new Exception("포트폴리오를 찾을 수 없습니다: ID = " + portfolioId);
        }
        
        return portfolio;
    }
    
    /**
     * ✅ 포트폴리오 조회 (내부 사용)
     * Service를 통해 포트폴리오를 조회합니다.
     */
    private PortfolioVO getPortfolio(Long portfolioId) {
        log.debug("🔍 포트폴리오 조회 (내부): " + portfolioId);
        
        if (portfolioId == null || portfolioId <= 0) {
            log.warn("⚠️ 유효하지 않은 포트폴리오 ID: " + portfolioId);
            return null;
        }
        
        try {
            // ✅ portfolioService를 사용한 조회
            return portfolioService.getPortfolio(portfolioId);
        } catch (Exception e) {
            log.error("❌ 포트폴리오 조회 실패: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * ✅ 포트폴리오 수정 (내부 사용)
     * modify → update로 이름 변경 권장
     */
    @Transactional
    public void modify(PortfolioVO portfolio) throws Exception {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("✏️ 포트폴리오 수정 (내부 메서드)");
        log.info("  - 포트폴리오 ID: " + portfolio.getPortfolioId());
        log.info("  - 수량: " + portfolio.getQuantity());
        log.info("  - 매입가: " + portfolio.getPurchasePrice());
        
        if (portfolio == null || portfolio.getPortfolioId() == null) {
            throw new IllegalArgumentException("포트폴리오 정보가 유효하지 않습니다.");
        }
        
        try {
            // ✅ portfolioService를 사용한 수정
            portfolioService.updatePortfolio(portfolio);
            
            log.info("✅ 포트폴리오 수정 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            log.error("❌ 포트폴리오 수정 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 수정 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ 포트폴리오 삭제 (내부 사용)
     */
    @Transactional
    public void remove(Long portfolioId) throws Exception {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🗑️ 포트폴리오 삭제 (내부 메서드)");
        log.info("  - 포트폴리오 ID: " + portfolioId);
        
        if (portfolioId == null || portfolioId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 포트폴리오 ID: " + portfolioId);
        }
        
        try {
            // ✅ portfolioService를 사용한 삭제
            portfolioService.deletePortfolio(portfolioId);
            
            log.info("✅ 포트폴리오 삭제 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            log.error("❌ 포트폴리오 삭제 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 삭제 실패: " + e.getMessage(), e);
        }
    }
}
