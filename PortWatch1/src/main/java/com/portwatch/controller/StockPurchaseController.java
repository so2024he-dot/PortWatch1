package com.portwatch.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.portwatch.domain.StockVO;
import com.portwatch.service.StockPurchaseValidationService;
import com.portwatch.service.PortfolioService;
import com.portwatch.service.StockService;

/**
 * ✅ 주식 구매 API 컨트롤러
 * 
 * 기능:
 * 1. 구매 전 검증 (가격, 수량, 잔액)
 * 2. 실제 구매 처리
 * 3. 포트폴리오에 추가
 * 4. 거래 내역 기록
 * 
 * @author PortWatch
 * @version 1.0
 */
@RestController
@RequestMapping("/api/purchase")
@CrossOrigin(origins = "*")
public class StockPurchaseController {
    
    @Autowired
    private StockPurchaseValidationService validationService;
    
    @Autowired
    private PortfolioService portfolioService;
    
    @Autowired
    private StockService stockService;
    
    /**
     * 1. 구매 가능 여부 검증
     * POST /api/purchase/validate
     * 
     * @param request {
     *     stockCode: "005930",
     *     quantity: 10,
     *     price: 75000
     * }
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePurchase(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 로그인 확인
            String memberId = (String) session.getAttribute("memberId");
            if (memberId == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // 파라미터 추출
            String stockCode = (String) request.get("stockCode");
            double quantity = Double.parseDouble(request.get("quantity").toString());
            double price = Double.parseDouble(request.get("price").toString());
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("💰 구매 검증 요청");
            System.out.println("  - 회원 ID: " + memberId);
            System.out.println("  - 종목 코드: " + stockCode);
            System.out.println("  - 수량: " + quantity);
            System.out.println("  - 가격: " + price);
            
            // 검증 수행
            Map<String, Object> validationResult = validationService.validatePurchase(
                memberId, stockCode, quantity, price
            );
            
            if ((boolean) validationResult.get("valid")) {
                System.out.println("✅ 검증 통과!");
                response.put("success", true);
                response.put("message", "구매 가능합니다.");
                response.put("validation", validationResult);
            } else {
                System.out.println("❌ 검증 실패: " + validationResult.get("message"));
                response.put("success", false);
                response.put("message", validationResult.get("message"));
                response.put("validation", validationResult);
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ 검증 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "검증 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 2. 실제 구매 처리
     * POST /api/purchase/execute
     * 
     * @param request {
     *     stockCode: "005930",
     *     quantity: 10,
     *     price: 75000
     * }
     */
    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executePurchase(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 로그인 확인
            String memberId = (String) session.getAttribute("memberId");
            if (memberId == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // 파라미터 추출
            String stockCode = (String) request.get("stockCode");
            double quantity = Double.parseDouble(request.get("quantity").toString());
            double price = Double.parseDouble(request.get("price").toString());
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("💳 구매 실행 요청");
            System.out.println("  - 회원 ID: " + memberId);
            System.out.println("  - 종목 코드: " + stockCode);
            System.out.println("  - 수량: " + quantity);
            System.out.println("  - 가격: " + price);
            
            // 1. 최종 검증
            Map<String, Object> validationResult = validationService.validatePurchase(
                memberId, stockCode, quantity, price
            );
            
            if (!(boolean) validationResult.get("valid")) {
                System.out.println("❌ 최종 검증 실패: " + validationResult.get("message"));
                response.put("success", false);
                response.put("message", validationResult.get("message"));
                return ResponseEntity.ok(response);
            }
            
            // 2. 종목 정보 조회
            StockVO stock = stockService.getStockByCode(stockCode);
            if (stock == null) {
                response.put("success", false);
                response.put("message", "종목 정보를 찾을 수 없습니다.");
                return ResponseEntity.ok(response);
            }
            
            // 3. 포트폴리오에 추가
            boolean addSuccess = portfolioService.addStockToPortfolio(
                memberId,
                stockCode,
                quantity,
                price
            );
            
            if (addSuccess) {
                System.out.println("✅ 구매 완료!");
                
                // 구매 정보
                double totalAmount = quantity * price;
                double commission = (double) validationResult.get("commission");
                
                response.put("success", true);
                response.put("message", "구매가 완료되었습니다!");
                response.put("purchase", Map.of(
                    "stockCode", stockCode,
                    "stockName", stock.getStockName(),
                    "quantity", quantity,
                    "price", price,
                    "totalAmount", totalAmount,
                    "commission", commission,
                    "finalAmount", totalAmount + commission
                ));
            } else {
                System.out.println("❌ 포트폴리오 추가 실패");
                response.put("success", false);
                response.put("message", "구매 처리 중 오류가 발생했습니다.");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ 구매 실행 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "구매 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 3. 빠른 검증 (간단한 체크만)
     * GET /api/purchase/quick-check?stockCode=005930&quantity=10&price=75000
     */
    @GetMapping("/quick-check")
    public ResponseEntity<Map<String, Object>> quickCheck(
            @RequestParam String stockCode,
            @RequestParam double quantity,
            @RequestParam double price) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("⚡ 빠른 검증: " + stockCode);
            
            Map<String, Object> result = validationService.quickValidate(
                stockCode, quantity, price
            );
            
            response.put("success", true);
            response.put("validation", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "빠른 검증 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 4. 구매 가능 금액 조회
     * GET /api/purchase/available-budget
     */
    @GetMapping("/available-budget")
    public ResponseEntity<Map<String, Object>> getAvailableBudget(
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String memberId = (String) session.getAttribute("memberId");
            if (memberId == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // TODO: 실제 회원 예산 조회 (MemberService에서)
            double availableBudget = 10000000.0; // 임시값
            
            response.put("success", true);
            response.put("availableBudget", availableBudget);
            response.put("formattedBudget", String.format("%,.0f원", availableBudget));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "예산 조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
