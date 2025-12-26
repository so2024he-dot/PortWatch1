package com.portwatch.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

/**
 * ✅ 환율 Service 구현 V3
 * 
 * @author PortWatch
 * @version 3.0 FINAL
 */
@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
    
    // 기본 환율 설정 (실제로는 API에서 조회)
    private static final BigDecimal USD_TO_KRW = new BigDecimal("1300.00");
    
    /**
     * ✅ USD → KRW 환율 조회
     * 
     * @return USD → KRW 환율
     */
    @Override
    public BigDecimal getUSDToKRW() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💱 USD → KRW 환율 조회");
        
        try {
            System.out.println("  - 환율: 1 USD = " + USD_TO_KRW + " KRW");
            System.out.println("✅ 환율 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return USD_TO_KRW;
            
        } catch (Exception e) {
            System.err.println("❌ 환율 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            // 기본값 반환
            return USD_TO_KRW;
        }
    }
    
    /**
     * ✅ 환율 조회 (from → to)
     * 
     * @param from 변환할 통화
     * @param to 목표 통화
     * @return 환율
     * @throws Exception
     */
    public BigDecimal getExchangeRate(String from, String to) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💱 환율 조회");
        System.out.println("  - 변환: " + from + " → " + to);
        
        try {
            // 동일 통화
            if (from.equals(to)) {
                return BigDecimal.ONE;
            }
            
            BigDecimal rate;
            
            // USD → KRW
            if ("USD".equals(from) && "KRW".equals(to)) {
                rate = USD_TO_KRW;
            }
            // KRW → USD
            else if ("KRW".equals(from) && "USD".equals(to)) {
                rate = BigDecimal.ONE.divide(USD_TO_KRW, 6, RoundingMode.HALF_UP);
            }
            else {
                throw new IllegalArgumentException("지원하지 않는 통화 쌍: " + from + " → " + to);
            }
            
            System.out.println("  - 환율: 1 " + from + " = " + rate + " " + to);
            System.out.println("✅ 환율 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return rate;
            
        } catch (Exception e) {
            System.err.println("❌ 환율 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("환율 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 금액 환전
     * 
     * @param amount 금액
     * @param from 변환할 통화
     * @param to 목표 통화
     * @return 환전 금액
     * @throws Exception
     */
    public BigDecimal convert(BigDecimal amount, String from, String to) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💱 금액 환전");
        System.out.println("  - 금액: " + amount + " " + from);
        
        try {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("유효하지 않은 금액: " + amount);
            }
            
            BigDecimal rate = getExchangeRate(from, to);
            BigDecimal convertedAmount = amount
                .multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);
            
            System.out.println("  - 환전 금액: " + convertedAmount + " " + to);
            System.out.println("✅ 환전 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return convertedAmount;
            
        } catch (Exception e) {
            System.err.println("❌ 환전 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("환전 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ USD → KRW 환전
     * 
     * @param usdAmount USD 금액
     * @return KRW 금액
     */
    public BigDecimal convertUSDToKRW(BigDecimal usdAmount) throws Exception {
        return convert(usdAmount, "USD", "KRW");
    }
    
    /**
     * ✅ KRW → USD 환전
     * 
     * @param krwAmount KRW 금액
     * @return USD 금액
     */
    public BigDecimal convertKRWToUSD(BigDecimal krwAmount) throws Exception {
        return convert(krwAmount, "KRW", "USD");
    }
}
