package com.portwatch.service;

import java.math.BigDecimal;

/**
 * 환율 Service 인터페이스
 * 
 * @author PortWatch
 * @version 2.0 - Spring 5.0.7 + MySQL 8.0.33 호환
 */
public interface ExchangeRateService {
    
    /**
     * 환율 조회
     * 
     * @param from 변환 전 통화 (USD, EUR, JPY 등)
     * @param to 변환 후 통화 (KRW 등)
     * @return 환율
     * @throws Exception
     */
    BigDecimal getExchangeRate(String from, String to) throws Exception;
    
    /**
     * 금액 환산
     * 
     * @param amount 금액
     * @param from 변환 전 통화
     * @param to 변환 후 통화
     * @return 환산 금액
     * @throws Exception
     */
    BigDecimal convert(BigDecimal amount, String from, String to) throws Exception;

	BigDecimal getUSDToKRW();

	BigDecimal convertUSDToKRW(BigDecimal currentPrice);
}
