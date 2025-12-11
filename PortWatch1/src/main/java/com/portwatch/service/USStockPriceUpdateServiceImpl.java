package com.portwatch.service;

import com.portwatch.domain.StockPriceVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.StockDAO;
import com.portwatch.persistence.StockPriceDAO;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 미국 주식 업데이트 서비스 구현체 (개선 버전)
 * 
 * ✅ Alpha Vantage API 사용
 * ✅ 강화된 에러 처리
 * ✅ API 제한 대응
 * ✅ 안정적인 데이터 파싱
 * 
 * API 키 발급: https://www.alphavantage.co/support/#api-key
 * 무료 플랜: 하루 25회, 분당 5회 제한
 * 
 * @author PortWatch
 * @version 2.0 (개선)
 * @since 2024-12-11
 */
@Service("usStockPriceUpdateService")
public class USStockPriceUpdateServiceImpl implements USStockPriceUpdateService {
    
    private static final Logger logger = LoggerFactory.getLogger(USStockPriceUpdateServiceImpl.class);
    
    // Alpha Vantage API 설정
    private String apiKey = "demo"; // 기본값 (application.properties에서 주입)
    private String apiUrl = "https://www.alphavantage.co/query";
    
    private static final int REQUEST_TIMEOUT = 15000; // 15초
    private static final int API_CALL_DELAY = 13000; // 13초 간격 (분당 5회 제한 대응)
    private static final int MAX_RETRY = 3; // 최대 재시도 횟수
    
    @Autowired
    private StockDAO stockDAO;
    
    @Autowired
    private StockPriceDAO stockPriceDAO;
    
    /**
     * API 키 설정 (Setter Injection)
     */
    public void setApiKey(String apiKey) {
        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equals("YOUR_API_KEY_HERE")) {
            this.apiKey = apiKey.trim();
            logger.info("✅ Alpha Vantage API 키 설정 완료: {}***", apiKey.substring(0, Math.min(4, apiKey.length())));
        } else {
            logger.warn("⚠️ Alpha Vantage API 키가 설정되지 않았습니다. 'demo' 키를 사용합니다 (제한적)");
        }
    }
    
    /**
     * API URL 설정 (Setter Injection)
     */
    public void setApiUrl(String apiUrl) {
        if (apiUrl != null && !apiUrl.trim().isEmpty()) {
            this.apiUrl = apiUrl.trim();
            logger.info("✅ Alpha Vantage API URL 설정: {}", apiUrl);
        }
    }
    
    /**
     * 단일 미국 종목 주가 업데이트
     */
    @Override
    @Transactional
    public StockPriceVO updateSingleUSStock(String stockSymbol) throws Exception {
        logger.info("📊 [US] 종목 {} 주가 업데이트 시작", stockSymbol);
        
        // 1. 종목 정보 조회
        StockVO stock = stockDAO.selectByCode(stockSymbol);
        if (stock == null) {
            String errorMsg = "종목 심볼 " + stockSymbol + "를 데이터베이스에서 찾을 수 없습니다";
            logger.error("❌ [US] {}", errorMsg);
            throw new Exception(errorMsg);
        }
        
        // 2. Alpha Vantage에서 데이터 가져오기 (재시도 로직 포함)
        Map<String, Object> stockData = null;
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                stockData = crawlStockPriceFromAlphaVantage(stockSymbol);
                break; // 성공하면 반복 종료
            } catch (Exception e) {
                lastException = e;
                logger.warn("⚠️ [US] 종목 {} 데이터 가져오기 실패 (시도 {}/{}): {}", 
                        stockSymbol, attempt, MAX_RETRY, e.getMessage());
                
                if (attempt < MAX_RETRY) {
                    logger.info("⏰ [US] {}초 후 재시도...", API_CALL_DELAY / 1000);
                    Thread.sleep(API_CALL_DELAY);
                }
            }
        }
        
        if (stockData == null) {
            throw new Exception("Alpha Vantage API 호출 실패 (" + stockSymbol + "): " + 
                    (lastException != null ? lastException.getMessage() : "알 수 없는 오류"));
        }
        
        // 3. StockPriceVO 생성
        StockPriceVO stockPrice = new StockPriceVO();
        stockPrice.setStockId(stock.getStockId());
        stockPrice.setTradeDate(Date.valueOf(LocalDate.now()));
        
        // 안전한 데이터 설정
        stockPrice.setOpenPrice(getOrDefault(stockData, "openPrice", BigDecimal.ZERO));
        stockPrice.setHighPrice(getOrDefault(stockData, "highPrice", BigDecimal.ZERO));
        stockPrice.setLowPrice(getOrDefault(stockData, "lowPrice", BigDecimal.ZERO));
        stockPrice.setClosePrice(getOrDefault(stockData, "closePrice", BigDecimal.ZERO));
        stockPrice.setVolume(getLongOrDefault(stockData, "volume", 0L));
        
        // 4. DB에 저장 (UPSERT)
        try {
            stockPriceDAO.upsertStockPrice(stockPrice);
            logger.info("✅ [US] 종목 {} 주가 업데이트 완료: ${} (거래량: {})", 
                    stockSymbol, stockPrice.getClosePrice(), stockPrice.getVolume());
        } catch (Exception e) {
            logger.error("❌ [US] 종목 {} DB 저장 실패: {}", stockSymbol, e.getMessage());
            throw new Exception("주가 데이터 저장 실패", e);
        }
        
        return stockPrice;
    }
    
    /**
     * 여러 미국 종목 주가 업데이트
     */
    @Override
    @Transactional
    public Map<String, StockPriceVO> updateMultipleUSStocks(List<String> stockSymbols) throws Exception {
        logger.info("📊 [US] {}개 종목 주가 업데이트 시작", stockSymbols.size());
        
        if (stockSymbols == null || stockSymbols.isEmpty()) {
            logger.warn("⚠️ [US] 업데이트할 종목이 없습니다");
            return new HashMap<String, StockPriceVO>();
        }
        
        // 무료 API 제한 경고
        if (stockSymbols.size() > 5) {
            logger.warn("⚠️ [US] 무료 API는 분당 5회 제한이 있습니다!");
            logger.warn("⚠️ [US] {}개 종목 업데이트에 약 {}분 소요 예상", 
                    stockSymbols.size(), (stockSymbols.size() * API_CALL_DELAY / 60000) + 1);
        }
        
        Map<String, StockPriceVO> results = new HashMap<String, StockPriceVO>();
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < stockSymbols.size(); i++) {
            String symbol = stockSymbols.get(i);
            try {
                StockPriceVO stockPrice = updateSingleUSStock(symbol);
                results.put(symbol, stockPrice);
                successCount++;
                
                // API 요청 간격 (마지막 종목이 아닐 때만)
                if (i < stockSymbols.size() - 1) {
                    logger.info("⏰ [US] API 제한 대응 - {}초 대기 중... ({}/{})", 
                            API_CALL_DELAY / 1000, i + 1, stockSymbols.size());
                    Thread.sleep(API_CALL_DELAY);
                }
                
            } catch (Exception e) {
                failCount++;
                logger.error("❌ [US] 종목 {} 업데이트 실패: {}", symbol, e.getMessage());
                // 계속 진행 (다른 종목 업데이트)
            }
        }
        
        logger.info("✅ [US] 여러 종목 업데이트 완료: 성공 {}, 실패 {}, 전체 {}", 
                successCount, failCount, stockSymbols.size());
        
        return results;
    }
    
    /**
     * 전체 미국 종목 주가 업데이트
     */
    @Override
    @Transactional
    public int updateAllUSStocks() throws Exception {
        logger.info("📊 [US] 전체 미국 종목 주가 업데이트 시작");
        
        // 미국 시장 종목만 조회
        List<StockVO> allStocks = stockDAO.selectAll();
        List<StockVO> usStocks = new java.util.ArrayList<StockVO>();
        
        for (StockVO stock : allStocks) {
            String marketType = stock.getMarketType();
            if (marketType != null && 
                (marketType.equals("NASDAQ") || 
                 marketType.equals("NYSE") || 
                 marketType.equals("AMEX"))) {
                usStocks.add(stock);
            }
        }
        
        logger.info("📊 [US] 미국 종목 수: {}", usStocks.size());
        
        // 무료 API 제한 경고
        if (usStocks.size() > 25) {
            logger.warn("⚠️ [US] 무료 API 키로는 하루 25개 종목만 업데이트 가능합니다!");
            logger.warn("⚠️ [US] 전체 {}개 중 25개만 업데이트됩니다", usStocks.size());
            logger.warn("💡 [US] 프리미엄 API 키 구매 권장: https://www.alphavantage.co/premium/");
        }
        
        int maxCount = Math.min(usStocks.size(), 25); // 최대 25개만 처리
        int successCount = 0;
        
        for (int i = 0; i < maxCount; i++) {
            StockVO stock = usStocks.get(i);
            try {
                updateSingleUSStock(stock.getStockCode());
                successCount++;
                
                // API 요청 간격
                if (i < maxCount - 1) {
                    Thread.sleep(API_CALL_DELAY);
                }
                
            } catch (Exception e) {
                logger.error("❌ [US] 종목 {} 업데이트 실패: {}", stock.getStockCode(), e.getMessage());
            }
        }
        
        logger.info("✅ [US] 전체 미국 종목 업데이트 완료: {}/{}", successCount, usStocks.size());
        
        return successCount;
    }
    
    /**
     * Alpha Vantage API에서 실시간 주가 가져오기 (개선 버전)
     */
    @Override
    public Map<String, Object> crawlStockPriceFromYahoo(String stockSymbol) throws Exception {
        return crawlStockPriceFromAlphaVantage(stockSymbol);
    }
    
    /**
     * Alpha Vantage API 호출 (에러 처리 강화)
     */
    private Map<String, Object> crawlStockPriceFromAlphaVantage(String stockSymbol) throws Exception {
        logger.info("🌐 [US] Alpha Vantage API 호출: {}", stockSymbol);
        
        // API 키 검증
        if ("demo".equals(apiKey)) {
            logger.warn("⚠️ [US] 데모 API 키를 사용 중입니다. application.properties에서 실제 API 키를 설정하세요!");
        }
        
        // API URL 구성
        String urlString = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", 
                apiUrl, stockSymbol, apiKey);
        
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(REQUEST_TIMEOUT);
            conn.setReadTimeout(REQUEST_TIMEOUT);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) PortWatch/2.0");
            conn.setRequestProperty("Accept", "application/json");
            
            int responseCode = conn.getResponseCode();
            logger.debug("[US] HTTP 응답 코드: {}", responseCode);
            
            if (responseCode != 200) {
                throw new Exception("API 호출 실패: HTTP " + responseCode);
            }
            
            // 응답 읽기
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            String responseText = response.toString();
            logger.debug("[US] API 응답: {}", responseText.substring(0, Math.min(200, responseText.length())));
            
            // JSON 파싱
            JSONObject jsonResponse = new JSONObject(responseText);
            
            // 에러 체크 1: Error Message
            if (jsonResponse.has("Error Message")) {
                String errorMsg = jsonResponse.getString("Error Message");
                logger.error("❌ [US] API 에러: {}", errorMsg);
                throw new Exception("API 에러: " + errorMsg);
            }
            
            // 에러 체크 2: API 요청 제한 (Note)
            if (jsonResponse.has("Note")) {
                String note = jsonResponse.getString("Note");
                logger.error("❌ [US] API 요청 제한: {}", note);
                throw new Exception("API 요청 제한 초과. 잠시 후 다시 시도하세요. (하루 25회 또는 분당 5회 제한)");
            }
            
            // 에러 체크 3: Information (일일 제한)
            if (jsonResponse.has("Information")) {
                String info = jsonResponse.getString("Information");
                logger.error("❌ [US] API 정보: {}", info);
                throw new Exception("API 호출 제한 초과. 내일 다시 시도하세요.");
            }
            
            // Global Quote 데이터 추출
            if (!jsonResponse.has("Global Quote")) {
                logger.error("❌ [US] 응답에 'Global Quote' 없음. API 키를 확인하세요!");
                throw new Exception("응답에 주가 데이터가 없습니다. API 키가 올바른지 확인하세요.");
            }
            
            JSONObject quote = jsonResponse.getJSONObject("Global Quote");
            
            // 빈 응답 체크
            if (quote.length() == 0) {
                logger.error("❌ [US] 빈 응답. 종목 심볼 확인: {}", stockSymbol);
                throw new Exception("유효하지 않은 종목 심볼입니다: " + stockSymbol);
            }
            
            // 결과 맵 생성
            Map<String, Object> result = new HashMap<String, Object>();
            
            // 데이터 파싱 (안전하게)
            BigDecimal openPrice = parseBigDecimal(quote.optString("02. open", "0"));
            BigDecimal highPrice = parseBigDecimal(quote.optString("03. high", "0"));
            BigDecimal lowPrice = parseBigDecimal(quote.optString("04. low", "0"));
            BigDecimal closePrice = parseBigDecimal(quote.optString("05. price", "0"));
            Long volume = parseLong(quote.optString("06. volume", "0"));
            
            // 유효성 검사
            if (closePrice.compareTo(BigDecimal.ZERO) == 0) {
                logger.warn("⚠️ [US] 종목 {} 주가가 0입니다. 데이터 확인 필요", stockSymbol);
            }
            
            result.put("openPrice", openPrice);
            result.put("highPrice", highPrice);
            result.put("lowPrice", lowPrice);
            result.put("closePrice", closePrice);
            result.put("volume", volume);
            result.put("stockSymbol", stockSymbol);
            result.put("crawledAt", LocalDate.now().toString());
            result.put("latestTradingDay", quote.optString("07. latest trading day", ""));
            
            logger.info("✅ [US] Alpha Vantage 호출 성공: {} = ${} (시가: ${}, 고가: ${}, 저가: ${}, 거래량: {})", 
                    stockSymbol, closePrice, openPrice, highPrice, lowPrice, volume);
            
            return result;
            
        } catch (Exception e) {
            logger.error("❌ [US] Alpha Vantage 호출 실패: {} - {}", stockSymbol, e.getMessage());
            throw new Exception("Alpha Vantage API 호출 실패 (" + stockSymbol + "): " + e.getMessage());
        } finally {
            // 리소스 정리
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    // ignore
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    /**
     * 최신 주가 조회
     */
    @Override
    public StockPriceVO getLatestUSStockPrice(String stockSymbol) throws Exception {
        StockVO stock = stockDAO.selectByCode(stockSymbol);
        if (stock == null) {
            return null;
        }
        
        return stockPriceDAO.selectLatestByStockId(stock.getStockId());
    }
    
    /**
     * 주가 히스토리 조회
     */
    @Override
    public List<StockPriceVO> getUSStockPriceHistory(String stockSymbol, int days) throws Exception {
        StockVO stock = stockDAO.selectByCode(stockSymbol);
        if (stock == null) {
            throw new Exception("종목을 찾을 수 없습니다: " + stockSymbol);
        }
        
        return stockPriceDAO.selectPriceHistory(stock.getStockId(), days);
    }
    
    /**
     * BigDecimal 파싱 (안전)
     */
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty() || "null".equals(value)) {
            return BigDecimal.ZERO;
        }
        
        try {
            return new BigDecimal(value.trim());
        } catch (Exception e) {
            logger.warn("[US] BigDecimal 파싱 실패: '{}'", value);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Long 파싱 (안전)
     */
    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty() || "null".equals(value)) {
            return 0L;
        }
        
        try {
            return Long.parseLong(value.trim());
        } catch (Exception e) {
            logger.warn("[US] Long 파싱 실패: '{}'", value);
            return 0L;
        }
    }
    
    /**
     * Map에서 안전하게 BigDecimal 가져오기
     */
    private BigDecimal getOrDefault(Map<String, Object> map, String key, BigDecimal defaultValue) {
        Object value = map.get(key);
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return defaultValue;
    }
    
    /**
     * Map에서 안전하게 Long 가져오기
     */
    private Long getLongOrDefault(Map<String, Object> map, String key, Long defaultValue) {
        Object value = map.get(key);
        if (value instanceof Long) {
            return (Long) value;
        }
        return defaultValue;
    }
}
