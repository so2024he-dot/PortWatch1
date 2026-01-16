package com.portwatch.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.StockVO;
import com.portwatch.service.PortfolioService;
import com.portwatch.service.StockService;
import com.portwatch.service.StockPurchaseValidationService;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * StockPurchaseApiController - 완전판
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 추가:
 * - executePurchase: 매수 실행 API 추가
 * - MySQL 현재가 자동 매핑
 * - 한국 주식: 1주 단위 (정수)
 * - 미국 주식: 0.001주 단위 (소수점 3자리)
 * 
 * @author PortWatch
 * @version COMPLETE - 2026.01.16
 */
@RestController
@RequestMapping("/api/purchase")
public class StockPurchaseApiController {
    
    @Autowired
    private StockPurchaseValidationService validationService;
    
    @Autowired
    private PortfolioService portfolioService;
    
    @Autowired
    private StockService stockService;
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 매수 실행 API (신규 추가!)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * 매수 실행 시:
     * 1. MySQL에서 종목 정보 조회
     * 2. 현재가 자동 매핑 (사용자 입력 가격은 참고용)
     * 3. 수량 검증 (한국: 정수, 미국: 소수점)
     * 4. 포트폴리오에 추가
     * 
     * @param request {portfolioId, stockCode, quantity, price}
     * @param session HttpSession
     * @return 성공/실패 응답
     */
    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executePurchase(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💰 API 요청: /api/purchase/execute");
        System.out.println("  요청 데이터: " + request);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 1. 세션에서 회원 정보 확인
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String memberId = member.getMemberId();
            
            // 2. 요청 파라미터 파싱
            String stockCode = (String) request.get("stockCode");
            BigDecimal quantity = new BigDecimal(request.get("quantity").toString());
            BigDecimal userInputPrice = new BigDecimal(request.get("price").toString());
            
            System.out.println("  회원 ID: " + memberId);
            System.out.println("  종목 코드: " + stockCode);
            System.out.println("  수량: " + quantity);
            System.out.println("  입력 가격: " + userInputPrice);
            
            // 3. MySQL에서 종목 정보 조회 (현재가 포함)
            StockVO stock = stockService.getStockByCode(stockCode);
            if (stock == null) {
                response.put("success", false);
                response.put("message", "존재하지 않는 종목입니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // 4. ✅ MySQL 현재가 사용 (사용자 입력 가격 무시!)
            BigDecimal currentPrice = stock.getCurrentPrice();
            if (currentPrice == null || currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
                // 현재가가 없으면 사용자 입력 가격 사용
                currentPrice = userInputPrice;
                System.out.println("⚠️  현재가 정보 없음, 입력 가격 사용: " + currentPrice);
            } else {
                System.out.println("✅ MySQL 현재가 사용: " + currentPrice);
            }
            
            // 5. 국가별 수량 검증
            String country = stock.getCountry();
            if ("KR".equals(country)) {
                // 한국 주식: 정수만 허용
                if (quantity.stripTrailingZeros().scale() > 0) {
                    response.put("success", false);
                    response.put("message", "한국 주식은 정수 수량만 매수할 수 있습니다.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            } else if ("US".equals(country)) {
                // 미국 주식: 소수점 3자리까지 허용
                if (quantity.scale() > 3) {
                    response.put("success", false);
                    response.put("message", "미국 주식은 소수점 3자리까지만 매수할 수 있습니다.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
            
            // 6. 포트폴리오에 추가
            boolean success = portfolioService.addStockToPortfolio(
                memberId, 
                stockCode, 
                quantity.doubleValue(), 
                currentPrice.doubleValue()  // ✅ MySQL 현재가 사용!
            );
            
            if (success) {
                System.out.println("✅ 매수 완료");
                System.out.println("  종목: " + stock.getStockName());
                System.out.println("  수량: " + quantity);
                System.out.println("  가격: " + currentPrice);
                System.out.println("  총액: " + quantity.multiply(currentPrice));
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                response.put("success", true);
                response.put("message", "매수가 완료되었습니다.");
                response.put("stockName", stock.getStockName());
                response.put("quantity", quantity);
                response.put("price", currentPrice);
                response.put("totalAmount", quantity.multiply(currentPrice));
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "매수 처리 중 오류가 발생했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (NumberFormatException e) {
            System.err.println("❌ 숫자 형식 오류: " + e.getMessage());
            response.put("success", false);
            response.put("message", "수량 또는 가격 형식이 올바르지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (Exception e) {
            System.err.println("❌ 매수 실행 중 오류: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            response.put("success", false);
            response.put("message", "매수 실행 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * ✅ 주식 매입 검증 (상세)
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePurchase(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📡 API 요청: /api/purchase/validate");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 세션에서 회원 정보 확인
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member == null) {
                response.put("valid", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String memberId = member.getMemberId();
            String stockCode = (String) request.get("stockCode");
            BigDecimal quantity = new BigDecimal(request.get("quantity").toString());
            BigDecimal price = new BigDecimal(request.get("price").toString());
            
            System.out.println("  회원 ID: " + memberId);
            System.out.println("  종목 코드: " + stockCode);
            System.out.println("  수량: " + quantity);
            System.out.println("  가격: " + price);
            
            // MySQL에서 현재가 조회
            StockVO stock = stockService.getStockByCode(stockCode);
            if (stock != null && stock.getCurrentPrice() != null) {
                price = stock.getCurrentPrice();
                System.out.println("  ✅ MySQL 현재가 적용: " + price);
            }
            
            // 검증 실행
            Map<String, Object> result = validationService.validatePurchase(
                memberId, stockCode, quantity, price
            );
            
            boolean isValid = (Boolean) result.getOrDefault("valid", false);
            
            if (isValid) {
                // 총액 정보 추가
                BigDecimal totalAmount = quantity.multiply(price);
                BigDecimal commission = totalAmount.multiply(new BigDecimal("0.001")); // 0.1% 수수료
                
                result.put("totalAmount", totalAmount);
                result.put("commission", commission);
                result.put("requiredAmount", totalAmount.add(commission));
                result.put("currentPrice", price);
                
                System.out.println("✅ 검증 성공");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return ResponseEntity.ok(result);
            } else {
                System.out.println("❌ 검증 실패: " + result.get("message"));
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            
        } catch (Exception e) {
            System.err.println("❌ 검증 중 오류: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            response.put("valid", false);
            response.put("message", "검증 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * ✅ 주식 매입 빠른 검증 (간단)
     */
    @GetMapping("/quick-validate")
    public ResponseEntity<Map<String, Object>> quickValidate(
            @RequestParam String memberId,
            @RequestParam String stockCode,
            @RequestParam(required = false) BigDecimal quantity,
            @RequestParam(required = false) BigDecimal price) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("⚡ API 요청: /api/purchase/quick-validate");
        System.out.println("  회원 ID: " + memberId);
        System.out.println("  종목 코드: " + stockCode);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 기본값 설정
            if (quantity == null) {
                quantity = BigDecimal.ONE;
            }
            if (price == null) {
                price = new BigDecimal("10000");
            }
            
            // 검증 실행
            boolean isValid = validationService.isValidPurchase(
                memberId, stockCode, quantity, price
            );
            
            result.put("valid", isValid);
            result.put("memberId", memberId);
            result.put("stockCode", stockCode);
            
            if (isValid) {
                result.put("message", "검증 통과");
                System.out.println("✅ 빠른 검증 성공");
                return ResponseEntity.ok(result);
            } else {
                result.put("message", "검증 실패");
                System.out.println("❌ 빠른 검증 실패");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            
        } catch (Exception e) {
            System.err.println("❌ 빠른 검증 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            result.put("valid", false);
            result.put("message", "검증 중 오류가 발생했습니다");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
