package com.portwatch.service;

import java.math.BigDecimal;

/**
 * 환율 정보 서비스
 * 
 * USD/KRW 환율 제공
 */
public interface ExchangeRateService {
    
    /**
     * USD → KRW 환율 조회
     * 
     * @return 환율 (예: 1300.50)
     */
    BigDecimal getUSDToKRW() throws Exception;
    
    /**
     * 달러를 원화로 변환
     * 
     * @param usdAmount 달러 금액
     * @return 원화 금액
     */
    BigDecimal convertUSDToKRW(BigDecimal usdAmount) throws Exception;
    
    /**
     * 원화를 달러로 변환
     * 
     * @param krwAmount 원화 금액
     * @return 달러 금액
     */
    BigDecimal convertKRWToUSD(BigDecimal krwAmount) throws Exception;

	BigDecimal getExchangeRate(String currency, String string);
}
