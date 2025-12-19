package com.portwatch.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portwatch.domain.StockVO;
import com.portwatch.domain.MemberVO;
import com.portwatch.persistence.MemberDAO;
import com.portwatch.persistence.StockDAO;


/**
 * ✅ 수정사항: StockPurchaseValidationService.java
 * 
 * 46번 라인 수정:
 * - 변경 전: memberDAO.selectMemberByEmail(memberId)
 * - 변경 후: memberDAO.selectMemberById(memberId)
 * 
 * 원인: 
 * - memberId가 이메일이 아닌 회원 ID일 경우 올바른 메서드 호출 필요
 * - 메서드명을 실제 사용 목적에 맞게 수정
 * 
 * @author PortWatch
 * @version 1.2 - 메서드명 수정
 */
@Service
public class StockPurchaseValidationService {
    
    @Autowired
    private StockDAO stockDAO;
    
    @Autowired
    private MemberDAO memberDAO;
    
    /**
     * ✅ 주식 매입 전체 검증
     * 
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @param quantity 수량
     * @param price 매입 가격
     * @return 검증 결과 맵
     */
    public Map<String, Object> validatePurchase(String memberId, String stockCode, double quantity, double price) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // ✅ 1. 회원 정보 검증 - selectMemberById 사용
            MemberVO member = memberDAO.selectMemberById(memberId);
            
            if (member == null) {
                result.put("valid", false);
                result.put("message", "회원 정보를 찾을 수 없습니다.");
                return result;
            }
            
            // 2. 종목 정보 검증
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            if (stock == null) {
                result.put("valid", false);
                result.put("message", "종목 정보를 찾을 수 없습니다.");
                return result;
            }
            
            // 3. 수량 검증
            Map<String, Object> quantityValidation = validateQuantity(stock, quantity);
            if (!(boolean) quantityValidation.get("valid")) {
                return quantityValidation;
            }
            
            // 4. 가격 검증
            Map<String, Object> priceValidation = validatePrice(stock, price);
            if (!(boolean) priceValidation.get("valid")) {
                return priceValidation;
            }
            
            // 5. 구매 가능 금액 검증
            Map<String, Object> budgetValidation = validateBudget(member, quantity, price, stock);
            if (!(boolean) budgetValidation.get("valid")) {
                return budgetValidation;
            }
            
            // 6. 시장 시간 검증 (선택사항)
            Map<String, Object> marketTimeValidation = validateMarketTime(stock);
            
            // 모든 검증 통과
            result.put("valid", true);
            result.put("message", "구매 가능합니다.");
            result.put("stock", stock);
            result.put("totalAmount", quantity * price);
            
            // ✅ 안전한 country 접근
            String country = stock.getCountry() != null ? stock.getCountry() : "KR";
            result.put("commission", calculateCommission(quantity * price, country));
            result.put("marketTimeWarning", marketTimeValidation.get("message"));
            
        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "검증 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 수량 검증
     */
    private Map<String, Object> validateQuantity(StockVO stock, double quantity) {
        Map<String, Object> result = new HashMap<>();
        
        // 수량이 양수인지 확인
        if (quantity <= 0) {
            result.put("valid", false);
            result.put("message", "수량은 0보다 커야 합니다.");
            return result;
        }
        
        // ✅ 안전한 country 접근 (null이면 한국으로 간주)
        String country = stock.getCountry();
        boolean isKoreanStock = (country == null || "KR".equals(country));
        
        // 미국 주식은 소수점 가능, 한국 주식은 정수만 가능
        if (isKoreanStock) {
            if (quantity != Math.floor(quantity)) {
                result.put("valid", false);
                result.put("message", "한국 주식은 정수 수량만 구매 가능합니다.");
                return result;
            }
        } else if ("US".equals(country)) {
            // 미국 주식은 소수점 3자리까지 허용
            BigDecimal bd = new BigDecimal(quantity).setScale(3, RoundingMode.HALF_UP);
            if (bd.doubleValue() != quantity) {
                result.put("valid", false);
                result.put("message", "미국 주식은 소수점 3자리까지 구매 가능합니다.");
                return result;
            }
        }
        
        // 최소 수량 검증
        double minQuantity = isKoreanStock ? 1.0 : 0.001;
        if (quantity < minQuantity) {
            result.put("valid", false);
            result.put("message", "최소 구매 수량은 " + minQuantity + "입니다.");
            return result;
        }
        
        result.put("valid", true);
        result.put("message", "수량 검증 통과");
        return result;
    }
    
    /**
     * 가격 검증
     */
    private Map<String, Object> validatePrice(StockVO stock, double price) {
        Map<String, Object> result = new HashMap<>();
        
        // 가격이 양수인지 확인
        if (price <= 0) {
            result.put("valid", false);
            result.put("message", "가격은 0보다 커야 합니다.");
            return result;
        }
        
        // ✅ currentPrice가 null이면 가격 검증 스킵
        BigDecimal currentPriceBD = stock.getCurrentPrice();
        
        if (currentPriceBD == null || currentPriceBD.doubleValue() == 0) {
            // 현재가 정보가 없으면 가격 검증 통과 (경고만 표시)
            result.put("valid", true);
            result.put("message", "가격 검증 통과 (현재가 정보 없음)");
            result.put("warning", "현재가 정보가 없어 가격 비교를 할 수 없습니다.");
            return result;
        }
        
        // 현재가와 비교 (±10% 범위 내)
        double currentPrice = currentPriceBD.doubleValue();
        double lowerBound = currentPrice * 0.9;
        double upperBound = currentPrice * 1.1;
        
        if (price < lowerBound || price > upperBound) {
            result.put("valid", false);
            result.put("message", String.format("매입 가격이 현재가(%.2f원) 대비 10%% 이상 차이가 납니다.", currentPrice));
            result.put("currentPrice", currentPrice);
            result.put("suggestedPrice", currentPrice);
            return result;
        }
        
        result.put("valid", true);
        result.put("message", "가격 검증 통과");
        return result;
    }
    
    /**
     * 예산 검증
     */
    private Map<String, Object> validateBudget(MemberVO member, double quantity, double price, StockVO stock) {
        Map<String, Object> result = new HashMap<>();
        
        // 총 구매 금액 계산
        double totalAmount = quantity * price;
        
        // ✅ 안전한 country 접근
        String country = stock.getCountry() != null ? stock.getCountry() : "KR";
        
        // 수수료 계산
        double commission = calculateCommission(totalAmount, country);
        
        // 필요 금액
        double requiredAmount = totalAmount + commission;
        
        // 회원 예산 확인 (여기서는 가상의 예산, 실제로는 DB에서 가져와야 함)
        // ✅ MemberVO에 availableBudget 필드가 있으면 사용, 없으면 기본값
        double availableBudget = 10000000; // 임시 값 (1천만원)
        
        // MemberVO에 getAvailableBudget() 메서드가 있으면 사용
        try {
            // Reflection을 사용하여 메서드 존재 여부 확인
            java.lang.reflect.Method method = member.getClass().getMethod("getAvailableBudget");
            Object budget = method.invoke(member);
            if (budget != null) {
                availableBudget = ((Number) budget).doubleValue();
            }
        } catch (Exception e) {
            // getAvailableBudget 메서드가 없으면 기본값 사용
            System.out.println("ℹ️ MemberVO에 availableBudget 필드가 없습니다. 기본값 사용: " + availableBudget);
        }
        
        if (requiredAmount > availableBudget) {
            result.put("valid", false);
            result.put("message", String.format("잔액이 부족합니다. (필요: %.0f원, 보유: %.0f원)", 
                requiredAmount, availableBudget));
            result.put("requiredAmount", requiredAmount);
            result.put("availableBudget", availableBudget);
            result.put("shortage", requiredAmount - availableBudget);
            return result;
        }
        
        result.put("valid", true);
        result.put("message", "예산 검증 통과");
        result.put("availableBudget", availableBudget);
        result.put("requiredAmount", requiredAmount);
        return result;
    }
    
    /**
     * 시장 거래 시간 검증
     */
    private Map<String, Object> validateMarketTime(StockVO stock) {
        Map<String, Object> result = new HashMap<>();
        
        // 현재 시간 (KST 기준)
        int currentHour = java.time.LocalTime.now().getHour();
        
        boolean isMarketOpen = false;
        String message = "";
        
        // ✅ 안전한 country 접근
        String country = stock.getCountry();
        boolean isKoreanStock = (country == null || "KR".equals(country));
        
        if (isKoreanStock) {
            // 한국 시장: 09:00 ~ 15:30
            isMarketOpen = (currentHour >= 9 && currentHour < 15) ||
                          (currentHour == 15 && java.time.LocalTime.now().getMinute() < 30);
            
            if (!isMarketOpen) {
                message = "한국 시장 거래 시간이 아닙니다. (거래시간: 09:00~15:30)";
            }
        } else if ("US".equals(country)) {
            // 미국 시장: 23:30 ~ 06:00 (KST 기준, 서머타임 고려 필요)
            isMarketOpen = (currentHour >= 23) || (currentHour < 6);
            
            if (!isMarketOpen) {
                message = "미국 시장 거래 시간이 아닙니다. (거래시간: 23:30~06:00 KST)";
            }
        }
        
        result.put("isMarketOpen", isMarketOpen);
        result.put("message", isMarketOpen ? "시장 거래 시간입니다." : message);
        
        return result;
    }
    
    /**
     * 수수료 계산
     */
    private double calculateCommission(double amount, String country) {
        if ("KR".equals(country)) {
            // 한국 주식: 매매대금의 0.015% (최소 수수료 없음)
            return amount * 0.00015;
        } else if ("US".equals(country)) {
            // 미국 주식: 고정 수수료 (예: $0.99)
            // USD -> KRW 환율 적용 필요
            double exchangeRate = 1300; // 임시 환율
            return 0.99 * exchangeRate;
        }
        return 0;
    }
    
    /**
     * 빠른 검증 (기본 정보만)
     */
    public Map<String, Object> quickValidate(String stockCode, double quantity, double price) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            if (stock == null) {
                result.put("valid", false);
                result.put("message", "종목 정보를 찾을 수 없습니다.");
                return result;
            }
            
            // 기본 검증만 수행
            Map<String, Object> quantityValidation = validateQuantity(stock, quantity);
            if (!(boolean) quantityValidation.get("valid")) {
                return quantityValidation;
            }
            
            Map<String, Object> priceValidation = validatePrice(stock, price);
            if (!(boolean) priceValidation.get("valid")) {
                return priceValidation;
            }
            
            result.put("valid", true);
            result.put("message", "기본 검증 통과");
            result.put("totalAmount", quantity * price);
            
        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "검증 실패: " + e.getMessage());
        }
        
        return result;
    }
}
