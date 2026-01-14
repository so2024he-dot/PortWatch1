package com.portwatch.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.portwatch.service.StockPurchaseValidationService;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * StockPurchaseApiController - quickValidate 추가 완료
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 추가:
 * Line 158: quickValidate 메서드 구현
 * 
 * @author PortWatch
 * @version FINAL - quickValidate 완성
 */
@RestController
@RequestMapping("/api/stock/purchase")
public class StockPurchaseApiController {
    
    @Autowired
    private StockPurchaseValidationService validationService;
    
    /**
     * ✅ 주식 매입 전체 검증 (상세)
     * 
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @param quantity 수량
     * @param price 가격
     * @return 검증 결과
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePurchase(
            @RequestParam String memberId,
            @RequestParam String stockCode,
            @RequestParam BigDecimal quantity,
            @RequestParam BigDecimal price) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📡 API 요청: /api/stock/purchase/validate");
        System.out.println("  회원 ID: " + memberId);
        System.out.println("  종목 코드: " + stockCode);
        System.out.println("  수량: " + quantity);
        System.out.println("  가격: " + price);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            Map<String, Object> result = validationService.validatePurchase(
                memberId, stockCode, quantity, price
            );
            
            boolean isValid = (Boolean) result.getOrDefault("valid", false);
            
            if (isValid) {
                System.out.println("✅ 검증 성공");
                return ResponseEntity.ok(result);
            } else {
                System.out.println("❌ 검증 실패: " + result.get("message"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            
        } catch (Exception e) {
            System.err.println("❌ 검증 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("valid", false);
            errorResult.put("message", "검증 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    /**
     * ✅ 주식 매입 빠른 검증 (간단)
     * 
     * Line 158 추가 메서드!
     * 
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @param quantity 수량
     * @param price 가격
     * @return 검증 결과 (성공/실패만)
     */
    @GetMapping("/quick-validate")
    public ResponseEntity<Map<String, Object>> quickValidate(
            @RequestParam String memberId,
            @RequestParam String stockCode,
            @RequestParam(required = false) BigDecimal quantity,
            @RequestParam(required = false) BigDecimal price) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("⚡ API 요청: /api/stock/purchase/quick-validate");
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
    
    /**
     * ✅ POST 방식 검증 (JSON 요청)
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePurchasePost(
            @RequestBody Map<String, Object> request) {
        
        try {
            String memberId = (String) request.get("memberId");
            String stockCode = (String) request.get("stockCode");
            BigDecimal quantity = new BigDecimal(request.get("quantity").toString());
            BigDecimal price = new BigDecimal(request.get("price").toString());
            
            return validatePurchase(memberId, stockCode, quantity, price);
            
        } catch (Exception e) {
            System.err.println("❌ POST 검증 중 오류: " + e.getMessage());
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("valid", false);
            errorResult.put("message", "요청 파라미터 오류: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
        }
    }
    
    /**
     * ✅ 회원만 검증
     */
    @GetMapping("/validate-member")
    public ResponseEntity<Map<String, Object>> validateMember(
            @RequestParam String memberId) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("👤 API 요청: /api/stock/purchase/validate-member");
        System.out.println("  회원 ID: " + memberId);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 임시 검증 (실제로는 MemberService 사용)
            Map<String, Object> validationResult = validationService.validatePurchase(
                memberId, "005930", BigDecimal.ONE, new BigDecimal("60000")
            );
            
            boolean memberValid = validationResult.get("member") != null;
            
            result.put("valid", memberValid);
            result.put("memberId", memberId);
            
            if (memberValid) {
                result.put("message", "유효한 회원입니다");
                return ResponseEntity.ok(result);
            } else {
                result.put("message", "존재하지 않는 회원입니다");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            
        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "회원 검증 중 오류가 발생했습니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * ✅ 종목만 검증
     */
    @GetMapping("/validate-stock")
    public ResponseEntity<Map<String, Object>> validateStock(
            @RequestParam String stockCode) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 API 요청: /api/stock/purchase/validate-stock");
        System.out.println("  종목 코드: " + stockCode);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 임시 검증 (실제로는 StockService 사용)
            Map<String, Object> validationResult = validationService.validatePurchase(
                "test001", stockCode, BigDecimal.ONE, new BigDecimal("10000")
            );
            
            boolean stockValid = validationResult.get("stock") != null;
            
            result.put("valid", stockValid);
            result.put("stockCode", stockCode);
            
            if (stockValid) {
                result.put("message", "유효한 종목입니다");
                return ResponseEntity.ok(result);
            } else {
                result.put("message", "존재하지 않는 종목입니다");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            
        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "종목 검증 중 오류가 발생했습니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
