package com.portwatch.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * 환율 Service 구현
 * 
 * @author PortWatch
 * @version 2.0 - Spring 5.0.7 + MySQL 8.0.33 호환
 */
@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
    
    // 임시 환율 저장소 (실제로는 외부 API 또는 DB에서 가져와야 함)
    private final Map<String, BigDecimal> exchangeRates;
    
    public ExchangeRateServiceImpl() {
        // 기본 환율 설정 (2024년 12월 기준)
        exchangeRates = new HashMap<>();
        
        // USD → KRW
        exchangeRates.put("USD_KRW", new BigDecimal("1300.00"));
        exchangeRates.put("KRW_USD", new BigDecimal("0.00077"));
        
        // EUR → KRW
        exchangeRates.put("EUR_KRW", new BigDecimal("1420.00"));
        exchangeRates.put("KRW_EUR", new BigDecimal("0.00070"));
        
        // JPY → KRW
        exchangeRates.put("JPY_KRW", new BigDecimal("8.80"));
        exchangeRates.put("KRW_JPY", new BigDecimal("0.11364"));
        
        // CNY → KRW
        exchangeRates.put("CNY_KRW", new BigDecimal("180.00"));
        exchangeRates.put("KRW_CNY", new BigDecimal("0.00556"));
    }
    
    /**
     * 환율 조회
     * 
     * @param from 변환 전 통화 (USD, EUR, JPY 등)
     * @param to 변환 후 통화 (KRW 등)
     * @return 환율
     * @throws Exception
     */
    @Override
    public BigDecimal getExchangeRate(String from, String to) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💱 환율 조회");
        System.out.println("  - From: " + from);
        System.out.println("  - To: " + to);
        
        // 같은 통화면 1.0 반환
        if (from.equals(to)) {
            System.out.println("  - 환율: 1.0 (같은 통화)");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return BigDecimal.ONE;
        }
        
        String key = from + "_" + to;
        BigDecimal rate = exchangeRates.get(key);
        
        if (rate == null) {
            // 역방향 환율이 있으면 계산
            String reverseKey = to + "_" + from;
            BigDecimal reverseRate = exchangeRates.get(reverseKey);
            
            if (reverseRate != null) {
                rate = BigDecimal.ONE.divide(reverseRate, 6, RoundingMode.HALF_UP);
                System.out.println("  - 환율: " + rate + " (역방향 계산)");
            } else {
                System.err.println("  ⚠️ 환율 정보 없음, 기본값 사용");
                
                // 기본값: USD 기준
                if ("USD".equals(from) && "KRW".equals(to)) {
                    rate = new BigDecimal("1300.00");
                } else if ("KRW".equals(from) && "USD".equals(to)) {
                    rate = new BigDecimal("0.00077");
                } else {
                    // 그 외에는 1.0
                    rate = BigDecimal.ONE;
                }
            }
        } else {
            System.out.println("  - 환율: " + rate);
        }
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return rate;
    }
    
    /**
     * 금액 환산
     * 
     * @param amount 금액
     * @param from 변환 전 통화
     * @param to 변환 후 통화
     * @return 환산 금액
     * @throws Exception
     */
    @Override
    public BigDecimal convert(BigDecimal amount, String from, String to) throws Exception {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 합니다.");
        }
        
        BigDecimal rate = getExchangeRate(from, to);
        BigDecimal result = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        
        System.out.println("💱 환산 결과: " + amount + " " + from + " → " + result + " " + to);
        
        return result;
    }
    
    /**
     * 환율 업데이트 (관리자용)
     * 
     * @param from 변환 전 통화
     * @param to 변환 후 통화
     * @param rate 환율
     */
    public void updateExchangeRate(String from, String to, BigDecimal rate) {
        String key = from + "_" + to;
        exchangeRates.put(key, rate);
        System.out.println("✅ 환율 업데이트: " + key + " = " + rate);
    }

	@Override
	public BigDecimal getUSDToKRW() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal convertUSDToKRW(BigDecimal currentPrice) {
		// TODO Auto-generated method stub
		return null;
	}
}
