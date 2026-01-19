package com.portwatch.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * ExchangeRateServiceImpl - 환율 서비스 구현
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * @author PortWatch
 * @version 2.0 - 2026.01.16 (오류 수정!)
 */
@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
    
    // 기본 환율 (API 실패 시 사용)
    private static final BigDecimal DEFAULT_EXCHANGE_RATE = new BigDecimal("1350.00");
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 현재 USD → KRW 환율 조회
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @Override
    public BigDecimal getUSDToKRW() {
        try {
            // 실제로는 외부 API 호출
            // 예: 한국은행 API, exchangerate-api.com 등
            
            // 임시: 고정 환율 반환 (1 USD = 1350 KRW)
            
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
     */
    @Override
    public BigDecimal convertUSDToKRW(BigDecimal usdAmount) {
        if (usdAmount == null) {
            return BigDecimal.ZERO;
        }
        
        try {
            BigDecimal exchangeRate = getUSDToKRW();
            BigDecimal krwAmount = usdAmount.multiply(exchangeRate);
            
            // 소수점 2자리까지
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
     */
    @Override
    public BigDecimal convertKRWToUSD(BigDecimal krwAmount) {
        if (krwAmount == null) {
            return BigDecimal.ZERO;
        }
        
        try {
            BigDecimal exchangeRate = getUSDToKRW();
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
}
