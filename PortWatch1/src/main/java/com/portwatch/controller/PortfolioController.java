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
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * PORTFOLIO CONTROLLER - 완벽 수정
 * Spring 5.0.7 + MySQL 8.0.33
 * 
 * 수정 내역:
 * 1. @RequestMapping("/portfolio") 변경 (404 해결)
 * 2. 메인 페이지 매핑: "", "/", "/list" 모두 처리
 * 3. portfolioVO Model 추가
 * 4. 세션 체크 개선
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
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
            PortfolioVO portfolio = portfolioService.getPortfolio(portfolioId);
            model.addAttribute("portfolio", portfolio);
            
            log.info("✅ 포트폴리오 상세 조회 완료");
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
     * ✅ 포트폴리오 수정 (PortfolioController용)
     */
    @Transactional
    public void modify(PortfolioVO portfolio) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✏️ 포트폴리오 수정");
        System.out.println("  - 포트폴리오 ID: " + portfolio.getPortfolioId());
        System.out.println("  - 수량: " + portfolio.getQuantity());
        System.out.println("  - 매입가: " + portfolio.getPurchasePrice());
        
        try {
            PortfolioService portfolioDAO = null;
			portfolioDAO.updatePortfolio(portfolio);
            
            System.out.println("✅ 포트폴리오 수정 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 수정 실패: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new RuntimeException("포트폴리오 수정 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ 포트폴리오 삭제 (PortfolioController용)
     */
    @Transactional
    public void remove(Long portfolioId) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 포트폴리오 삭제");
        System.out.println("  - 포트폴리오 ID: " + portfolioId);
        
        try {
            PortfolioService portfolioDAO = null;
			portfolioDAO.deletePortfolio(portfolioId);
            
            System.out.println("✅ 포트폴리오 삭제 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 삭제 실패: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new RuntimeException("포트폴리오 삭제 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ 포트폴리오 ID로 조회 (별칭 - Exception 버전)
     */
    public PortfolioVO getPortfolioById(Long portfolioId) throws Exception {
        return getPortfolio(portfolioId);
    }

	private PortfolioVO getPortfolio(Long portfolioId) {
		// TODO Auto-generated method stub
		return null;
	}

}
