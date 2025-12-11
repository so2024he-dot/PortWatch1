package com.portwatch.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 환율 정보 서비스 구현체
 * 
 * 환율 정보 제공 방식:
 * 1. 고정 환율 사용 (빠르고 안정적)
 * 2. API 호출 (실시간, 하루 한 번 캐싱)
 * 
 * @author PortWatch
 * @version 1.0
 */
@Service("exchangeRateService")
public class ExchangeRateServiceImpl implements ExchangeRateService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateServiceImpl.class);
    
    // ========================================
    // 설정
    // ========================================
    
    // 🔧 고정 환율 사용 여부 (true: 고정 환율, false: API 호출)
    private static final boolean USE_FIXED_RATE = true;
    
    // 🔧 고정 환율 값 (2024년 12월 기준: 약 1,300원)
    private static final BigDecimal FIXED_EXCHANGE_RATE = new BigDecimal("1310.00");
    
    // API 캐시
    private BigDecimal cachedRate = null;
    private long cacheTimestamp = 0;
    private static final long CACHE_DURATION = 24 * 60 * 60 * 1000; // 24시간
    
    // ========================================
    // 환율 조회
    // ========================================
    
    @Override
    public BigDecimal getUSDToKRW() throws Exception {
        if (USE_FIXED_RATE) {
            // 고정 환율 사용
            logger.debug("💱 고정 환율 사용: 1 USD = {} KRW", FIXED_EXCHANGE_RATE);
            return FIXED_EXCHANGE_RATE;
        } else {
            // API 호출 (캐시 사용)
            return getExchangeRateFromAPI();
        }
    }
    
    @Override
    public BigDecimal convertUSDToKRW(BigDecimal usdAmount) throws Exception {
        if (usdAmount == null || usdAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal rate = getUSDToKRW();
        BigDecimal krwAmount = usdAmount.multiply(rate);
        
        logger.debug("💱 환전: ${} × {} = ₩{}", 
                usdAmount, rate, krwAmount.setScale(0, RoundingMode.HALF_UP));
        
        return krwAmount.setScale(0, RoundingMode.HALF_UP);
    }
    
    @Override
    public BigDecimal convertKRWToUSD(BigDecimal krwAmount) throws Exception {
        if (krwAmount == null || krwAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal rate = getUSDToKRW();
        BigDecimal usdAmount = krwAmount.divide(rate, 2, RoundingMode.HALF_UP);
        
        logger.debug("💱 환전: ₩{} ÷ {} = ${}", krwAmount, rate, usdAmount);
        
        return usdAmount;
    }
    
    // ========================================
    // API 호출 (선택사항)
    // ========================================
    
    /**
     * 환율 API에서 실시간 환율 조회 (캐시 사용)
     * 
     * 무료 API: https://exchangerate-api.com
     * 또는: https://api.exchangerate.host
     */
    private BigDecimal getExchangeRateFromAPI() throws Exception {
        long currentTime = System.currentTimeMillis();
        
        // 캐시가 유효하면 캐시된 값 사용
        if (cachedRate != null && (currentTime - cacheTimestamp) < CACHE_DURATION) {
            logger.debug("💱 캐시된 환율 사용: 1 USD = {} KRW", cachedRate);
            return cachedRate;
        }
        
        // API 호출
        logger.info("💱 환율 API 호출 중...");
        
        try {
            // exchangerate-api.com (무료, 1,500 requests/month)
            String apiUrl = "https://api.exchangerate-api.com/v4/latest/USD";
            
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            if (conn.getResponseCode() != 200) {
                throw new Exception("환율 API 호출 실패: HTTP " + conn.getResponseCode());
            }
            
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            conn.disconnect();
            
            // JSON 파싱
            JSONObject json = new JSONObject(response.toString());
            JSONObject rates = json.getJSONObject("rates");
            double krwRate = rates.getDouble("KRW");
            
            cachedRate = new BigDecimal(String.valueOf(krwRate));
            cacheTimestamp = currentTime;
            
            logger.info("✅ 환율 API 조회 성공: 1 USD = {} KRW", cachedRate);
            
            return cachedRate;
            
        } catch (Exception e) {
            logger.error("❌ 환율 API 호출 실패: {}", e.getMessage());
            
            // API 실패 시 고정 환율 사용
            logger.warn("⚠️ 고정 환율로 대체: 1 USD = {} KRW", FIXED_EXCHANGE_RATE);
            return FIXED_EXCHANGE_RATE;
        }
    }
    
    // ========================================
    // 수동 환율 설정 (관리자용)
    // ========================================
    
    /**
     * 환율 수동 설정
     * 
     * @param rate 환율 (예: 1310.00)
     */
    public void setExchangeRate(BigDecimal rate) {
        this.cachedRate = rate;
        this.cacheTimestamp = System.currentTimeMillis();
        logger.info("✅ 환율 수동 설정: 1 USD = {} KRW", rate);
    }
    
    /**
     * 캐시 초기화
     */
    public void clearCache() {
        this.cachedRate = null;
        this.cacheTimestamp = 0;
        logger.info("🗑️ 환율 캐시 초기화");
    }
}
