package com.portwatch.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.portwatch.service.StockPurchaseValidationService;
import com.portwatch.service.PortfolioService;
import com.portwatch.domain.PortfolioItemVO;

/**
 * 주식 매입 API 컨트롤러
 * 
 * @author PortWatch
 * @version 1.1 (PortfolioItemVO 사용)
 */
@RestController
@RequestMapping("/api/purchase")
public class StockPurchaseApiController {
    
    @Autowired
    private StockPurchaseValidationService validationService;
    
    @Autowired
    private PortfolioService portfolioService;
    
    /**
     * 주식 매입 검증 API
     * 
     * @param stockCode 종목 코드
     * @param quantity 수량
     * @param price 가격
     * @param session 세션
     * @return 검증 결과
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePurchase(
            @RequestParam("stockCode") String stockCode,
            @RequestParam("quantity") double quantity,
            @RequestParam("price") double price,
            HttpSession session) {
        
        try {
            // 세션에서 회원 ID 가져오기
            String memberId = (String) session.getAttribute("memberId");
            
            if (memberId == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("valid", false);
                result.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }
            
            // 검증 수행
            Map<String, Object> validationResult = validationService.validatePurchase(
                memberId, stockCode, quantity, price
            );
            
            return ResponseEntity.ok(validationResult);
            
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("valid", false);
            errorResult.put("message", "검증 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    /**
     * 주식 매입 실행 API
     * 
     * @param portfolioId 포트폴리오 ID
     * @param stockCode 종목 코드
     * @param quantity 수량
     * @param price 매입 가격
     * @param session 세션
     * @return 매입 결과
     */
    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executePurchase(
            @RequestParam("portfolioId") Long portfolioId,
            @RequestParam("stockCode") String stockCode,
            @RequestParam("quantity") double quantity,
            @RequestParam("price") double price,
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 세션에서 회원 ID 가져오기
            String memberId = (String) session.getAttribute("memberId");
            
            if (memberId == null) {
                result.put("success", false);
                result.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }
            
            // 1. 최종 검증
            Map<String, Object> validationResult = validationService.validatePurchase(
                memberId, stockCode, quantity, price
            );
            
            if (!(boolean) validationResult.get("valid")) {
                result.put("success", false);
                result.put("message", validationResult.get("message"));
                return ResponseEntity.ok(result);
            }
            
            // 2. 포트폴리오에 주식 추가 (PortfolioItemVO 사용)
            PortfolioItemVO item = PortfolioItemVO.builder()
                    .portfolioId(portfolioId)
                    .stockCode(stockCode)
                    .quantity(quantity)
                    .purchasePrice(price)
                    .build();
            
            boolean added = portfolioService.addStockToPortfolio(item);
            
            if (added) {
                result.put("success", true);
                result.put("message", "주식 매입이 완료되었습니다.");
                result.put("item", item);
                result.put("totalAmount", validationResult.get("totalAmount"));
                result.put("commission", validationResult.get("commission"));
                
                System.out.println("✅ 주식 매입 성공: " + stockCode + " x " + quantity);
            } else {
                result.put("success", false);
                result.put("message", "주식 매입에 실패했습니다.");
                
                System.err.println("❌ 주식 매입 실패: " + stockCode);
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "매입 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 빠른 검증 API (로그인 불필요)
     * 
     * @param stockCode 종목 코드
     * @param quantity 수량
     * @param price 가격
     * @return 검증 결과
     */
    @GetMapping("/quick-validate")
    public ResponseEntity<Map<String, Object>> quickValidate(
            @RequestParam("stockCode") String stockCode,
            @RequestParam("quantity") double quantity,
            @RequestParam("price") double price) {
        
        try {
            Map<String, Object> validationResult = validationService.quickValidate(
                stockCode, quantity, price
            );
            
            return ResponseEntity.ok(validationResult);
            
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("valid", false);
            errorResult.put("message", "검증 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    /**
     * 매입 가능 금액 조회 API
     * 
     * @param session 세션
     * @return 매입 가능 금액 정보
     */
    @GetMapping("/available-budget")
    public ResponseEntity<Map<String, Object>> getAvailableBudget(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String memberId = (String) session.getAttribute("memberId");
            
            if (memberId == null) {
                result.put("success", false);
                result.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }
            
            // TODO: DB에서 실제 예산 조회 (임시로 고정 금액 반환)
            result.put("success", true);
            result.put("availableBudget", 10000000); // 1천만원
            result.put("currency", "KRW");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
