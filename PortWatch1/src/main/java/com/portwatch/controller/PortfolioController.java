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
 * ✅ 포트폴리오 컨트롤러 (완성)
 * 
 * @author PortWatch
 * @version 8.0 - create, list 메서드 추가
 */
@Controller
@RequestMapping("/portfolio")
public class PortfolioController {
    
    @Autowired
    private PortfolioService portfolioService;
    
    @Autowired(required = false)
    private StockService stockService;
    
    /**
     * ✅ 포트폴리오 생성 페이지
     * GET /portfolio/create
     */
    @GetMapping("/create")
    public String createForm(HttpSession session, Model model) {
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        try {
            // 주식 목록 조회 (선택사항)
            if (stockService != null) {
                model.addAttribute("stockList", stockService.getAllStocks());
            }
            
            model.addAttribute("loginMember", loginMember);
            
        } catch (Exception e) {
            System.err.println("포트폴리오 생성 페이지 로딩 실패: " + e.getMessage());
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
     * 포트폴리오 메인 페이지
     */
    @GetMapping({"", "/"})
    public String portfolioMain(HttpSession session, Model model) {
        // 세션에서 회원 정보 가져오기
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        try {
            String memberId = loginMember.getMemberId();
            
            List<PortfolioVO> portfolioList = portfolioService.getPortfolioList(memberId);
            Map<String, Object> summary = portfolioService.getPortfolioSummary(memberId);
            
            model.addAttribute("portfolioList", portfolioList);
            model.addAttribute("summary", summary);
            model.addAttribute("loginMember", loginMember);
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "포트폴리오 조회 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "portfolio/portfolio";
    }
    
    /**
     * 포트폴리오 추가 (AJAX)
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addPortfolio(
            @RequestBody PortfolioVO portfolio,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 세션에서 회원 정보 가져오기
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            
            if (loginMember == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String memberId = loginMember.getMemberId();
            
            // 회원 ID 설정
            portfolio.setMemberId(memberId);
            
            // 검증
            if (portfolio.getStockId() == null) {
                response.put("success", false);
                response.put("message", "종목을 선택해주세요.");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (portfolio.getQuantity() == null || portfolio.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                response.put("success", false);
                response.put("message", "수량은 0보다 커야 합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 포트폴리오에 추가
            boolean added = portfolioService.addStockToPortfolio(portfolio);
            
            if (added) {
                response.put("success", true);
                response.put("message", "포트폴리오에 추가되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "포트폴리오 추가에 실패했습니다.");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 포트폴리오 삭제 (AJAX)
     */
    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletePortfolio(
            @RequestParam("stockCode") String stockCode,
            @RequestParam("quantity") double quantity,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            
            if (loginMember == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String memberId = loginMember.getMemberId();
            
            boolean deleted = portfolioService.removeStockFromPortfolio(memberId, stockCode, quantity);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "포트폴리오에서 삭제되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "삭제에 실패했습니다.");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 포트폴리오 상세 조회 (AJAX)
     */
    @GetMapping("/detail")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPortfolioDetail(HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            
            if (loginMember == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String memberId = loginMember.getMemberId();
            
            List<PortfolioVO> portfolioList = portfolioService.getPortfolioList(memberId);
            Map<String, Object> summary = portfolioService.getPortfolioSummary(memberId);
            
            response.put("success", true);
            response.put("portfolioList", portfolioList);
            response.put("summary", summary);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
