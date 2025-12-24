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

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 포트폴리오 컨트롤러
 * 
 * @author PortWatch
 * @version 7.0 - 세션 필드명 통일 (loginMember) + 한글 인코딩 수정
 */
@Controller
@RequestMapping("/portfolio")
public class PortfolioController {
    
    @Autowired
    private PortfolioService portfolioService;
    
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
            
            // ✅ 회원 ID 설정
            portfolio.setMemberId(memberId);
            
            // ✅ 검증
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
            
            if (portfolio.getAvgPurchasePrice() == null || portfolio.getAvgPurchasePrice().compareTo(BigDecimal.ZERO) <= 0) {
                response.put("success", false);
                response.put("message", "평균 매입가는 0보다 커야 합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // ✅ 포트폴리오 추가
            portfolioService.updatePortfolio(portfolio);
            
            response.put("success", true);
            response.put("message", "포트폴리오에 추가되었습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 포트폴리오 수정 (AJAX)
     */
    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePortfolio(
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
            portfolio.setMemberId(memberId);
            
            portfolioService.updatePortfolio(portfolio);
            
            response.put("success", true);
            response.put("message", "포트폴리오가 수정되었습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 포트폴리오 삭제 (AJAX)
     */
    @DeleteMapping("/delete/{portfolioId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletePortfolio(
            @PathVariable Long portfolioId,
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
            
            // 주의: portfolioId로 삭제하는 메서드가 UnsupportedOperationException을 던질 수 있음
            try {
                portfolioService.deletePortfolio(portfolioId);
                response.put("success", true);
                response.put("message", "포트폴리오에서 삭제되었습니다.");
            } catch (UnsupportedOperationException e) {
                response.put("success", false);
                response.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 포트폴리오 목록 조회 (AJAX)
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPortfolioList(HttpSession session) {
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
            
            List<PortfolioVO> portfolioList = portfolioService.getPortfolioList(memberId);
            Map<String, Object> summary = portfolioService.getPortfolioSummary(memberId);
            
            response.put("success", true);
            response.put("portfolioList", portfolioList);
            response.put("summary", summary);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 중복 체크 (AJAX)
     */
    @GetMapping("/check-duplicate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkDuplicate(
            @RequestParam Integer stockId,
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
            boolean exists = portfolioService.checkDuplicate(memberId, stockId);
            
            response.put("success", true);
            response.put("exists", exists);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
