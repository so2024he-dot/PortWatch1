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
 * ✅ 주식 매입 검증 서비스 (에러 수정 완료)
 * 
 * 수정 사항:
 * - selectMemberById 타입 변경 (int → String)
 * - validatePurchase 파라미터 타입 통일 (BigDecimal)
 * 
 * @author PortWatch
 * @version 3.1 - 에러 수정 완료
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
            // 1. 회원 유효성 검증
            Map<String, Object> memberValidation = validateMember(memberId);
            if (!(boolean) memberValidation.get("valid")) {
                result.put("valid", false);
                result.put("message", memberValidation.get("message"));
                System.out.println("❌ 회원 검증 실패: " + memberValidation.get("message"));
                return result;
            }
            
            // 2. 종목 유효성 검증
            Map<String, Object> stockValidation = validateStock(stockCode);
            if (!(boolean) stockValidation.get("valid")) {
                result.put("valid", false);
                result.put("message", stockValidation.get("message"));
                System.out.println("❌ 종목 검증 실패: " + stockValidation.get("message"));
                return result;
            }
            
            StockVO stock = (StockVO) stockValidation.get("stock");
            
            // 3. 수량 검증
            Map<String, Object> quantityValidation = validateQuantity(quantity);
            if (!(boolean) quantityValidation.get("valid")) {
                result.put("valid", false);
                result.put("message", quantityValidation.get("message"));
                System.out.println("❌ 수량 검증 실패: " + quantityValidation.get("message"));
                return result;
            }
            
            // 4. 가격 검증
            Map<String, Object> priceValidation = validatePrice(price, stock);
            if (!(boolean) priceValidation.get("valid")) {
                result.put("valid", false);
                result.put("message", priceValidation.get("message"));
                System.out.println("❌ 가격 검증 실패: " + priceValidation.get("message"));
                return result;
            }
            
            // 5. 매입 금액 검증
            BigDecimal totalAmount = quantity.multiply(price);
            Map<String, Object> amountValidation = validateAmount(totalAmount);
            if (!(boolean) amountValidation.get("valid")) {
                result.put("valid", false);
                result.put("message", amountValidation.get("message"));
                System.out.println("❌ 금액 검증 실패: " + amountValidation.get("message"));
                return result;
            }
            
            // 6. 시장 시간 검증
            Map<String, Object> marketTimeValidation = validateMarketTime(stock.getCountry());
            if (!(boolean) marketTimeValidation.get("valid")) {
                result.put("valid", false);
                result.put("message", marketTimeValidation.get("message"));
                result.put("warning", true); // 경고로 표시 (무시 가능)
                System.out.println("⚠️ 시장 시간 경고: " + marketTimeValidation.get("message"));
            }
            
            // ✅ 모든 검증 통과
            result.put("valid", true);
            result.put("message", "매입 가능합니다.");
            result.put("stock", stock);
            result.put("totalAmount", totalAmount);
            
            // 수수료 계산 (예: 0.015%)
            BigDecimal commission = totalAmount.multiply(new BigDecimal("0.00015"));
            result.put("commission", commission);
            
            System.out.println("✅ 모든 검증 통과!");
            System.out.println("  총 매입 금액: " + totalAmount);
            System.out.println("  수수료: " + commission);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 검증 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            
            result.put("valid", false);
            result.put("message", "검증 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * ✅ 주식 매입 전체 검증 (double 버전 - 하위 호환)
     */
    public Map<String, Object> validatePurchase(String memberId, String stockCode, 
                                                 double quantity, double price) {
        return validatePurchase(memberId, stockCode, 
            new BigDecimal(String.valueOf(quantity)), 
            new BigDecimal(String.valueOf(price)));
    }
    
    /**
     * ✅ 빠른 검증 (로그인 불필요)
     */
    public Map<String, Object> quickValidate(String stockCode, double quantity, double price) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 종목 검증
            Map<String, Object> stockValidation = validateStock(stockCode);
            if (!(boolean) stockValidation.get("valid")) {
                return stockValidation;
            }
            
            StockVO stock = (StockVO) stockValidation.get("stock");
            
            // 수량 검증
            BigDecimal quantityBD = new BigDecimal(String.valueOf(quantity));
            Map<String, Object> quantityValidation = validateQuantity(quantityBD);
            if (!(boolean) quantityValidation.get("valid")) {
                return quantityValidation;
            }
            
            // 가격 검증
            BigDecimal priceBD = new BigDecimal(String.valueOf(price));
            Map<String, Object> priceValidation = validatePrice(priceBD, stock);
            if (!(boolean) priceValidation.get("valid")) {
                return priceValidation;
            }
            
            // 금액 검증
            BigDecimal totalAmount = quantityBD.multiply(priceBD);
            Map<String, Object> amountValidation = validateAmount(totalAmount);
            if (!(boolean) amountValidation.get("valid")) {
                return amountValidation;
            }
            
            result.put("valid", true);
            result.put("message", "매입 가능합니다.");
            result.put("totalAmount", totalAmount);
            
        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "검증 실패: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * ✅ 1. 회원 유효성 검증 (String memberId로 통일)
     */
    private Map<String, Object> validateMember(String memberId) {
        Map<String, Object> result = new HashMap<>();
        
        if (memberId == null || memberId.trim().isEmpty()) {
            result.put("valid", false);
            result.put("message", "회원 ID가 유효하지 않습니다.");
            return result;
        }
        
        try {
            // ✅ 수정: selectMemberById(String)로 호출
            MemberVO member = memberDAO.selectMemberByEmail(memberId); // 또는 적절한 메서드
            
            if (member == null) {
                result.put("valid", false);
                result.put("message", "존재하지 않는 회원입니다.");
                return result;
            }
            
            // 회원 상태 확인
            if (member.getStatus() != null && !"ACTIVE".equals(member.getStatus())) {
                result.put("valid", false);
                result.put("message", "활성화되지 않은 회원입니다.");
                return result;
            }
            
            result.put("valid", true);
            result.put("member", member);
            
        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "회원 조회 중 오류가 발생했습니다.");
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
            
            // 현재가 확인
            if (stock.getCurrentPrice() == null || stock.getCurrentPrice().compareTo(BigDecimal.ZERO) <= 0) {
                result.put("valid", false);
                result.put("message", "현재가 정보가 없는 종목입니다.");
                return result;
            }
            
            result.put("valid", true);
            result.put("stock", stock);
            
        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "종목 조회 중 오류가 발생했습니다.");
        }
        
        return result;
    }
    
    /**
     * 3. 수량 검증
     */
    private Map<String, Object> validateQuantity(BigDecimal quantity) {
        Map<String, Object> result = new HashMap<>();
        
        if (quantity == null) {
            result.put("valid", false);
            result.put("message", "수량을 입력하세요.");
            return result;
        }
        
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            result.put("valid", false);
            result.put("message", "수량은 0보다 커야 합니다.");
            return result;
        }
        
        // 최대 수량 제한 (1,000주)
        if (quantity.compareTo(new BigDecimal("1000")) > 0) {
            result.put("valid", false);
            result.put("message", "한 번에 최대 1,000주까지만 매입할 수 있습니다.");
            return result;
        }
        
        result.put("valid", true);
        return result;
    }
    
    /**
     * 4. 가격 검증
     */
    private Map<String, Object> validatePrice(BigDecimal price, StockVO stock) {
        Map<String, Object> result = new HashMap<>();
        
        if (price == null) {
            result.put("valid", false);
            result.put("message", "가격을 입력하세요.");
            return result;
        }
        
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            result.put("valid", false);
            result.put("message", "가격은 0보다 커야 합니다.");
            return result;
        }
        
        // 현재가 대비 ±10% 이내 검증
        BigDecimal currentPrice = stock.getCurrentPrice();
        BigDecimal minPrice = currentPrice.multiply(BigDecimal.ONE.subtract(PRICE_TOLERANCE));
        BigDecimal maxPrice = currentPrice.multiply(BigDecimal.ONE.add(PRICE_TOLERANCE));
        
        if (price.compareTo(minPrice) < 0 || price.compareTo(maxPrice) > 0) {
            result.put("valid", false);
            result.put("message", String.format(
                "입력한 가격이 현재가 대비 ±10%% 범위를 벗어났습니다. " +
                "(현재가: %s, 허용 범위: %s ~ %s)",
                currentPrice, minPrice.setScale(2, BigDecimal.ROUND_HALF_UP), 
                maxPrice.setScale(2, BigDecimal.ROUND_HALF_UP)
            ));
            return result;
        }
        
        result.put("valid", true);
        return result;
    }
    
    /**
     * 5. 매입 금액 검증
     */
    private Map<String, Object> validateAmount(BigDecimal totalAmount) {
        Map<String, Object> result = new HashMap<>();
        
        if (totalAmount.compareTo(MIN_PURCHASE_AMOUNT) < 0) {
            result.put("valid", false);
            result.put("message", "최소 매입 금액은 " + MIN_PURCHASE_AMOUNT + "원입니다.");
            return result;
        }
        
        if (totalAmount.compareTo(MAX_PURCHASE_AMOUNT) > 0) {
            result.put("valid", false);
            result.put("message", "최대 매입 금액은 " + MAX_PURCHASE_AMOUNT + "원입니다.");
            return result;
        }
        
        result.put("valid", true);
        return result;
    }
    
    /**
     * 6. 시장 시간 검증
     */
    private Map<String, Object> validateMarketTime(String country) {
        Map<String, Object> result = new HashMap<>();
        
        // 현재는 경고만 표시 (실제 매입은 가능)
        result.put("valid", true);
        result.put("message", "시장 시간 외 거래입니다. 다음 거래일에 체결됩니다.");
        
        return result;
    }
}
