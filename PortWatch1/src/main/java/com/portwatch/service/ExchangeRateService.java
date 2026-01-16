package com.portwatch.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * ExchangeRateService - 환율 서비스
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * 기능:
 * - USD → KRW 실시간 환율 조회
 * - 달러 → 원화 변환
 * - 원화 → 달러 변환
 * 
 * @author PortWatch
 * @version 1.0 - 2026.01.16
 */
@Service
public class ExchangeRateService {
    
    // 기본 환율 (API 실패 시 사용)
    private static final BigDecimal DEFAULT_EXCHANGE_RATE = new BigDecimal("1350.00");
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 현재 USD → KRW 환율 조회
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * @return 환율 (예: 1350.50)
     */
    public BigDecimal getCurrentExchangeRate() {
        try {
            // 실제로는 외부 API 호출
            // 예: 한국은행 API, exchangerate-api.com 등
            
            // 임시: 고정 환율 반환 (1 USD = 1350 KRW)
            // 실제 구현 시 API 호출 코드로 교체
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("💱 현재 환율 조회");
            System.out.println("  - 환율: " + DEFAULT_EXCHANGE_RATE + " KRW/USD");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return DEFAULT_EXCHANGE_RATE;
            
        } catch (Exception e) {
            System.err.println("❌ 환율 조회 실패: " + e.getMessage());
            return DEFAULT_EXCHANGE_RATE;
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ USD → KRW 변환
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * @param usdAmount 달러 금액
     * @return 원화 금액
     * 
     * 예시:
     * - USD $100 × 1350 = KRW 135,000원
     */
    public BigDecimal convertUsdToKrw(BigDecimal usdAmount) {
        if (usdAmount == null) {
            return BigDecimal.ZERO;
        }
        
        try {
            BigDecimal exchangeRate = getCurrentExchangeRate();
            BigDecimal krwAmount = usdAmount.multiply(exchangeRate);
            
            // 소수점 2자리까지 (원화)
            krwAmount = krwAmount.setScale(2, RoundingMode.HALF_UP);
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("💱 USD → KRW 변환");
            System.out.println("  - USD: $" + usdAmount);
            System.out.println("  - 환율: " + exchangeRate);
            System.out.println("  - KRW: " + krwAmount + "원");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return krwAmount;
            
        } catch (Exception e) {
            System.err.println("❌ 환율 변환 실패: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ KRW → USD 변환
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * @param krwAmount 원화 금액
     * @return 달러 금액
     * 
     * 예시:
     * - KRW 135,000원 ÷ 1350 = USD $100
     */
    public BigDecimal convertKrwToUsd(BigDecimal krwAmount) {
        if (krwAmount == null) {
            return BigDecimal.ZERO;
        }
        
        try {
            BigDecimal exchangeRate = getCurrentExchangeRate();
            BigDecimal usdAmount = krwAmount.divide(exchangeRate, 2, RoundingMode.HALF_UP);
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("💱 KRW → USD 변환");
            System.out.println("  - KRW: " + krwAmount + "원");
            System.out.println("  - 환율: " + exchangeRate);
            System.out.println("  - USD: $" + usdAmount);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return usdAmount;
            
        } catch (Exception e) {
            System.err.println("❌ 환율 변환 실패: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 환율 정보 조회 (상세)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * @return 환율 정보 문자열
     */
    public String getExchangeRateInfo() {
        BigDecimal rate = getCurrentExchangeRate();
        
        return String.format(
            "현재 환율: 1 USD = %.2f KRW",
            rate
        );
    }
}
