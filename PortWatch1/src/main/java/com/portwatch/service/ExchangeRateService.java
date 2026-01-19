package com.portwatch.service;

import java.math.BigDecimal;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * ExchangeRateService - 환율 서비스 인터페이스
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * @author PortWatch
 * @version 2.0 - 2026.01.16 (인터페이스로 변경!)
 */
public interface ExchangeRateService {
    
    /**
     * 현재 USD → KRW 환율 조회
     * @return 환율 (예: 1350.00)
     */
    BigDecimal getUSDToKRW();
    
    /**
     * USD → KRW 변환
     * @param usdAmount 달러 금액
     * @return 원화 금액
     */
    BigDecimal convertUSDToKRW(BigDecimal usdAmount);
    
    /**
     * KRW → USD 변환
     * @param krwAmount 원화 금액
     * @return 달러 금액
     */
    BigDecimal convertKRWToUSD(BigDecimal krwAmount);
}
