package com.portwatch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.service.PortfolioService;
import com.portwatch.service.StockService;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ✅ 포트폴리오 컨트롤러 (느슨한 결합 개선)
 * 
 * 개선 사항:
 * - 생성자 주입 사용 (필드 주입 → 생성자 주입)
 * - final 키워드로 불변성 보장
 * - 인터페이스 의존
 * 
 * @author PortWatch
 * @version 9.0 - Loose Coupling
 */
@Controller
@RequestMapping("/portfolio")
public class PortfolioController {
    
    private final PortfolioService portfolioService;
    private final StockService stockService;
    
    /**
     * 생성자 주입 (권장)
     * - 테스트 용이성 증가
     * - 순환 참조 방지
     * - 불변성 보장
     */
    @Autowired
    public PortfolioController(
            PortfolioService portfolioService,
            StockService stockService) {
        this.portfolioService = portfolioService;
        this.stockService = stockService;
    }
    
    /**
     * ✅ 포트폴리오 메인 페이지
     * GET /portfolio 또는 /portfolio/
     */
    @GetMapping({"", "/"})
    public String portfolioMain(HttpSession session, Model model) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 포트폴리오 메인 페이지");
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            System.out.println("❌ 로그인 필요");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        try {
            String memberId = loginMember.getMemberId();
            System.out.println("  - 회원 ID: " + memberId);
            
            List<PortfolioVO> portfolioList = portfolioService.getPortfolioList(memberId);
            Map<String, Object> summary = portfolioService.getPortfolioSummary(memberId);
            
            System.out.println("  - 포트폴리오 개수: " + portfolioList.size());
            System.out.println("✅ 포트폴리오 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            model.addAttribute("portfolioList", portfolioList);
            model.addAttribute("summary", summary);
            model.addAttribute("loginMember", loginMember);
            
            return "portfolio/portfolio";
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "포트폴리오 조회 중 오류가 발생했습니다: " + e.getMessage());
            return "portfolio/portfolio";
        }
    }
    
    /**
     * ✅ 포트폴리오 생성 페이지
     * GET /portfolio/create
     */
    @GetMapping("/create")
    public String createForm(HttpSession session, Model model) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📝 포트폴리오 생성 페이지");
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            System.out.println("❌ 로그인 필요");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        try {
            // 주식 목록 조회
            if (stockService != null) {
                List<?> stockList = stockService.getAllStocks();
                System.out.println("  - 주식 목록: " + stockList.size() + "개");
                model.addAttribute("stockList", stockList);
            }
            
            model.addAttribute("loginMember", loginMember);
            
            System.out.println("✅ 생성 페이지 로딩 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 생성 페이지 로딩 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
        }
        
        return "portfolio/create";
    }
    
    /**
     * ✅ 포트폴리오 목록 페이지  
     * GET /portfolio/list
     */
    @GetMapping("/list")
    public String listPortfolio(HttpSession session, Model model) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 포트폴리오 목록 조회");
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            System.out.println("❌ 로그인 필요");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        try {
            String memberId = loginMember.getMemberId();
            System.out.println("  - 회원 ID: " + memberId);
            
            // 포트폴리오 목록 조회
            List<PortfolioVO> portfolioList = portfolioService.getPortfolioList(memberId);
            
            // 포트폴리오 요약 정보
            Map<String, Object> summary = portfolioService.getPortfolioSummary(memberId);
            
            System.out.println("  - 포트폴리오 개수: " + portfolioList.size());
            System.out.println("✅ 포트폴리오 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            model.addAttribute("portfolioList", portfolioList);
            model.addAttribute("summary", summary);
            model.addAttribute("loginMember", loginMember);
            
            return "portfolio/list";
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "포트폴리오 조회 중 오류가 발생했습니다: " + e.getMessage());
            return "portfolio/list";
        }
    }
    
    /**
     * ✅ 포트폴리오 추가 (AJAX)
     * POST /portfolio/add
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addPortfolio(
            @RequestBody PortfolioVO portfolio,
            HttpSession session) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("➕ 포트폴리오 추가 요청");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 세션에서 회원 정보 가져오기
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            
            if (loginMember == null) {
                System.out.println("❌ 로그인 필요");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String memberId = loginMember.getMemberId();
            portfolio.setMemberId(memberId);
            
            System.out.println("  - 회원 ID: " + memberId);
            System.out.println("  - 종목 ID: " + portfolio.getStockId());
            System.out.println("  - 수량: " + portfolio.getQuantity());
            
            // 검증
            if (portfolio.getStockId() == null) {
                System.out.println("❌ 종목 미선택");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                response.put("success", false);
                response.put("message", "종목을 선택해주세요.");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (portfolio.getQuantity() == null || portfolio.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("❌ 수량 오류");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                response.put("success", false);
                response.put("message", "수량은 0보다 커야 합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 포트폴리오에 추가
            boolean added = portfolioService.addStockToPortfolio(portfolio);
            
            if (added) {
                System.out.println("✅ 포트폴리오 추가 성공");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                response.put("success", true);
                response.put("message", "포트폴리오에 추가되었습니다.");
            } else {
                System.out.println("❌ 포트폴리오 추가 실패");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                response.put("success", false);
                response.put("message", "포트폴리오 추가에 실패했습니다.");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ 추가 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * ✅ 포트폴리오 삭제 (AJAX)
     * DELETE /portfolio/delete
     */
    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletePortfolio(
            @RequestParam("stockCode") String stockCode,
            @RequestParam("quantity") double quantity,
            HttpSession session) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 포트폴리오 삭제 요청");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            
            if (loginMember == null) {
                System.out.println("❌ 로그인 필요");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String memberId = loginMember.getMemberId();
            
            System.out.println("  - 회원 ID: " + memberId);
            System.out.println("  - 종목 코드: " + stockCode);
            System.out.println("  - 수량: " + quantity);
            
            boolean deleted = portfolioService.removeStockFromPortfolio(memberId, stockCode, quantity);
            
            if (deleted) {
                System.out.println("✅ 포트폴리오 삭제 성공");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                response.put("success", true);
                response.put("message", "포트폴리오에서 삭제되었습니다.");
            } else {
                System.out.println("❌ 포트폴리오 삭제 실패");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                response.put("success", false);
                response.put("message", "삭제에 실패했습니다.");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ 삭제 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * ✅ 포트폴리오 상세 조회 (AJAX)
     * GET /portfolio/detail
     */
    @GetMapping("/detail")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPortfolioDetail(HttpSession session) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 포트폴리오 상세 조회 (AJAX)");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            
            if (loginMember == null) {
                System.out.println("❌ 로그인 필요");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String memberId = loginMember.getMemberId();
            System.out.println("  - 회원 ID: " + memberId);
            
            List<PortfolioVO> portfolioList = portfolioService.getPortfolioList(memberId);
            Map<String, Object> summary = portfolioService.getPortfolioSummary(memberId);
            
            System.out.println("  - 포트폴리오 개수: " + portfolioList.size());
            System.out.println("✅ 상세 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            response.put("success", true);
            response.put("portfolioList", portfolioList);
            response.put("summary", summary);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ 상세 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
