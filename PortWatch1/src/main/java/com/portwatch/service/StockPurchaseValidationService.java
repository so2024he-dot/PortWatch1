package com.portwatch.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.MemberDAO;
import com.portwatch.persistence.StockDAO;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * StockPurchaseValidationService - Member 메서드 수정 완료
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 수정:
 * 1. Line 213: selectMemberByEmail → selectMemberById
 * 2. Line 222: getStatus() → getMemberStatus()
 * 
 * @author PortWatch
 * @version 4.0 FINAL - Member 수정 완료
 */
@Service
public class StockPurchaseValidationService {
    
    @Autowired
    private MemberDAO memberDAO;
    
    @Autowired
    private StockDAO stockDAO;
    
    // 검증 상수
    private static final BigDecimal MIN_PURCHASE_AMOUNT = new BigDecimal("1000");      // 최소 매입 금액: 1,000원
    private static final BigDecimal MAX_PURCHASE_AMOUNT = new BigDecimal("100000000"); // 최대 매입 금액: 1억원
    private static final BigDecimal PRICE_TOLERANCE = new BigDecimal("0.10");          // 가격 허용 오차: ±10%
    
    /**
     * ✅ 주식 매입 전체 검증 (BigDecimal 버전)
     */
    public Map<String, Object> validatePurchase(String memberId, String stockCode, 
                                                 BigDecimal quantity, BigDecimal price) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 주식 매입 검증 시작");
        System.out.println("  회원 ID: " + memberId);
        System.out.println("  종목 코드: " + stockCode);
        System.out.println("  수량: " + quantity);
        System.out.println("  가격: " + price);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 회원 검증
            Map<String, Object> memberResult = validateMember(memberId);
            if (!(Boolean) memberResult.get("valid")) {
                System.out.println("❌ 회원 검증 실패: " + memberResult.get("message"));
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return memberResult;
            }
            
            // 2. 종목 검증
            Map<String, Object> stockResult = validateStock(stockCode);
            if (!(Boolean) stockResult.get("valid")) {
                System.out.println("❌ 종목 검증 실패: " + stockResult.get("message"));
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return stockResult;
            }
            
            // 3. 수량 검증
            Map<String, Object> quantityResult = validateQuantity(quantity, stockCode);
            if (!(Boolean) quantityResult.get("valid")) {
                System.out.println("❌ 수량 검증 실패: " + quantityResult.get("message"));
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return quantityResult;
            }
            
            // 4. 가격 검증
            StockVO stock = (StockVO) stockResult.get("stock");
            Map<String, Object> priceResult = validatePrice(price, stock);
            if (!(Boolean) priceResult.get("valid")) {
                System.out.println("❌ 가격 검증 실패: " + priceResult.get("message"));
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return priceResult;
            }
            
            // 5. 총 금액 검증
            Map<String, Object> amountResult = validateTotalAmount(quantity, price);
            if (!(Boolean) amountResult.get("valid")) {
                System.out.println("❌ 총 금액 검증 실패: " + amountResult.get("message"));
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return amountResult;
            }
            
            // 모든 검증 통과
            result.put("valid", true);
            result.put("message", "모든 검증을 통과했습니다.");
            result.put("member", memberResult.get("member"));
            result.put("stock", stockResult.get("stock"));
            result.put("totalAmount", quantity.multiply(price));
            
            System.out.println("✅ 모든 검증 통과");
            System.out.println("  총 금액: " + quantity.multiply(price) + "원");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "검증 중 오류가 발생했습니다: " + e.getMessage());
            System.err.println("❌ 검증 중 오류: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
        
        return result;
    }
    
    /**
     * ✅ 주식 매입 전체 검증 (Double 버전 - 오버로드)
     */
    public Map<String, Object> validatePurchase(String memberId, String stockCode, 
                                                 Double quantity, Double price) {
        return validatePurchase(memberId, stockCode, 
                                BigDecimal.valueOf(quantity), 
                                BigDecimal.valueOf(price));
    }
    
    /**
     * ✅ 주식 매입 전체 검증 (Integer 수량 버전 - 오버로드)
     */
    public Map<String, Object> validatePurchase(String memberId, String stockCode, 
                                                 Integer quantity, BigDecimal price) {
        return validatePurchase(memberId, stockCode, 
                                new BigDecimal(quantity), 
                                price);
    }
    
    /**
     * ✅ 주식 매입 전체 검증 (모두 Double 버전 - 오버로드)
     */
    public Map<String, Object> validatePurchase(String memberId, String stockCode, 
                                                 Integer quantity, Double price) {
        return validatePurchase(memberId, stockCode, 
                                new BigDecimal(quantity), 
                                BigDecimal.valueOf(price));
    }
    
    /**
     * ✅ 주식 매입 간편 검증 (빠른 검증용)
     */
    public boolean isValidPurchase(String memberId, String stockCode, 
                                   BigDecimal quantity, BigDecimal price) {
        Map<String, Object> result = validatePurchase(memberId, stockCode, quantity, price);
        return (Boolean) result.getOrDefault("valid", false);
    }
    
    // ========================================
    // Private 검증 메서드들
    // ========================================
    
    /**
     * ✅ 1. 회원 유효성 검증 (수정 완료!)
     */
    private Map<String, Object> validateMember(String memberId) {
        Map<String, Object> result = new HashMap<>();
        
        if (memberId == null || memberId.trim().isEmpty()) {
            result.put("valid", false);
            result.put("message", "회원 ID가 유효하지 않습니다.");
            return result;
        }
        
        try {
            // ✅ 수정 1: selectMemberByEmail → selectMemberById
            MemberVO member = memberDAO.selectMemberById(memberId);
            
            if (member == null) {
                result.put("valid", false);
                result.put("message", "존재하지 않는 회원입니다.");
                return result;
            }
            
            // ✅ 수정 2: getStatus() → getMemberStatus()
            if (member.getMemberStatus() != null && !"ACTIVE".equals(member.getMemberStatus())) {
                result.put("valid", false);
                result.put("message", "활성화되지 않은 회원입니다.");
                return result;
            }
            
            result.put("valid", true);
            result.put("member", member);
            
        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "회원 조회 중 오류가 발생했습니다: " + e.getMessage());
            System.err.println("회원 조회 오류: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 2. 종목 유효성 검증
     */
    private Map<String, Object> validateStock(String stockCode) {
        Map<String, Object> result = new HashMap<>();
        
        if (stockCode == null || stockCode.trim().isEmpty()) {
            result.put("valid", false);
            result.put("message", "종목 코드가 유효하지 않습니다.");
            return result;
        }
        
        try {
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            
            if (stock == null) {
                result.put("valid", false);
                result.put("message", "존재하지 않는 종목입니다.");
                return result;
            }
            
            result.put("valid", true);
            result.put("stock", stock);
            
        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "종목 조회 중 오류가 발생했습니다: " + e.getMessage());
            System.err.println("종목 조회 오류: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 3. 수량 유효성 검증
     */
    private Map<String, Object> validateQuantity(BigDecimal quantity, String stockCode) {
        Map<String, Object> result = new HashMap<>();
        
        // null 체크
        if (quantity == null) {
            result.put("valid", false);
            result.put("message", "수량이 입력되지 않았습니다.");
            return result;
        }
        
        // 0 이하 체크
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            result.put("valid", false);
            result.put("message", "수량은 0보다 커야 합니다.");
            return result;
        }
        
        // 미국 주식인 경우 소수점 허용 (분할 매수)
        boolean isUSStock = stockCode != null && stockCode.matches("[A-Z]+");
        
        if (!isUSStock) {
            // 한국 주식은 정수만 허용
            if (quantity.stripTrailingZeros().scale() > 0) {
                result.put("valid", false);
                result.put("message", "한국 주식은 소수점 수량을 지원하지 않습니다.");
                return result;
            }
        }
        
        result.put("valid", true);
        return result;
    }
    
    /**
     * 4. 가격 유효성 검증
     */
    private Map<String, Object> validatePrice(BigDecimal price, StockVO stock) {
        Map<String, Object> result = new HashMap<>();
        
        // null 체크
        if (price == null) {
            result.put("valid", false);
            result.put("message", "가격이 입력되지 않았습니다.");
            return result;
        }
        
        // 0 이하 체크
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            result.put("valid", false);
            result.put("message", "가격은 0보다 커야 합니다.");
            return result;
        }
        
        // 현재 가격과 비교 (±10% 허용)
        BigDecimal currentPrice = stock.getCurrentPrice();
        if (currentPrice != null && currentPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal minPrice = currentPrice.multiply(BigDecimal.ONE.subtract(PRICE_TOLERANCE));
            BigDecimal maxPrice = currentPrice.multiply(BigDecimal.ONE.add(PRICE_TOLERANCE));
            
            if (price.compareTo(minPrice) < 0 || price.compareTo(maxPrice) > 0) {
                result.put("valid", false);
                result.put("message", String.format(
                    "입력한 가격(%s원)이 현재 시세(%.0f원)와 너무 차이가 납니다. (허용 범위: %.0f ~ %.0f원)",
                    price, currentPrice, minPrice, maxPrice
                ));
                return result;
            }
        }
        
        result.put("valid", true);
        return result;
    }
    
    /**
     * 5. 총 금액 유효성 검증
     */
    private Map<String, Object> validateTotalAmount(BigDecimal quantity, BigDecimal price) {
        Map<String, Object> result = new HashMap<>();
        
        BigDecimal totalAmount = quantity.multiply(price);
        
        // 최소 금액 체크
        if (totalAmount.compareTo(MIN_PURCHASE_AMOUNT) < 0) {
            result.put("valid", false);
            result.put("message", String.format(
                "최소 매입 금액은 %,d원입니다. (현재: %s원)",
                MIN_PURCHASE_AMOUNT.intValue(), totalAmount
            ));
            return result;
        }
        
        // 최대 금액 체크
        if (totalAmount.compareTo(MAX_PURCHASE_AMOUNT) > 0) {
            result.put("valid", false);
            result.put("message", String.format(
                "최대 매입 금액은 %,d원입니다. (현재: %s원)",
                MAX_PURCHASE_AMOUNT.intValue(), totalAmount
            ));
            return result;
        }
        
        result.put("valid", true);
        return result;
    }
}
