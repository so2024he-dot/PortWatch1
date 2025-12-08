    package com.portwatch.service;

import com.portwatch.domain.StockPriceVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.StockDAO;
import com.portwatch.persistence.StockPriceDAO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 주가 업데이트 서비스 구현체
 * 네이버 금융에서 실시간 주가 크롤링
 */
@Service
public class StockPriceUpdateServiceImpl implements StockPriceUpdateService {
    
    private static final Logger logger = LoggerFactory.getLogger(StockPriceUpdateServiceImpl.class);
    
    private static final String NAVER_FINANCE_URL = "https://finance.naver.com/item/main.naver?code=";
    private static final int REQUEST_TIMEOUT = 10000; // 10초
    
    @Autowired
    private StockDAO stockDAO;
    
    @Autowired
    private StockPriceDAO stockPriceDAO;
    
    /**
     * 단일 종목 주가 업데이트
     */
    @Override
    @Transactional
    public StockPriceVO updateSingleStock(String stockCode) throws Exception {
        logger.info("📊 종목 {} 주가 업데이트 시작", stockCode);
        
        // 1. 종목 정보 조회
        StockVO stock = stockDAO.selectByCode(stockCode);
        if (stock == null) {
            throw new Exception("종목 코드 " + stockCode + "를 찾을 수 없습니다");
        }
        
        // 2. 네이버에서 크롤링
        Map<String, Object> crawledData = crawlStockPriceFromNaver(stockCode);
        
        // 3. StockPriceVO 생성
        StockPriceVO stockPrice = new StockPriceVO();
        stockPrice.setStockId(stock.getStockId());
        stockPrice.setTradeDate(Date.valueOf(LocalDate.now()));
        stockPrice.setOpenPrice((BigDecimal) crawledData.get("openPrice"));
        stockPrice.setHighPrice((BigDecimal) crawledData.get("highPrice"));
        stockPrice.setLowPrice((BigDecimal) crawledData.get("lowPrice"));
        stockPrice.setClosePrice((BigDecimal) crawledData.get("closePrice"));
        stockPrice.setVolume((Long) crawledData.get("volume"));
        
        // 4. DB에 저장 (UPSERT)
        stockPriceDAO.upsertStockPrice(stockPrice);
        
        logger.info("✅ 종목 {} 주가 업데이트 완료: {}원", stockCode, stockPrice.getClosePrice());
        
        return stockPrice;
    }
    
    /**
     * 여러 종목 주가 업데이트
     */
    @Override
    @Transactional
    public Map<String, StockPriceVO> updateMultipleStocks(List<String> stockCodes) throws Exception {
        logger.info("📊 {}개 종목 주가 업데이트 시작", stockCodes.size());
        
        Map<String, StockPriceVO> results = new HashMap<>();
        
        for (String stockCode : stockCodes) {
            try {
                StockPriceVO stockPrice = updateSingleStock(stockCode);
                results.put(stockCode, stockPrice);
                
                // 크롤링 간격 (네이버 차단 방지)
                Thread.sleep(500);
                
            } catch (Exception e) {
                logger.error("❌ 종목 {} 업데이트 실패: {}", stockCode, e.getMessage());
            }
        }
        
        logger.info("✅ 여러 종목 업데이트 완료: {}/{}", results.size(), stockCodes.size());
        
        return results;
    }
    
    /**
     * 전체 종목 주가 업데이트
     */
    @Override
    @Transactional
    public int updateAllStocks() throws Exception {
        logger.info("📊 전체 종목 주가 업데이트 시작");
        
        List<StockVO> allStocks = stockDAO.selectAll();
        int successCount = 0;
        
        for (StockVO stock : allStocks) {
            try {
                updateSingleStock(stock.getStockCode());
                successCount++;
                
                // 크롤링 간격
                Thread.sleep(500);
                
            } catch (Exception e) {
                logger.error("❌ 종목 {} 업데이트 실패: {}", stock.getStockCode(), e.getMessage());
            }
        }
        
        logger.info("✅ 전체 종목 업데이트 완료: {}/{}", successCount, allStocks.size());
        
        return successCount;
    }
    
    /**
     * 네이버 금융에서 실시간 주가 크롤링
     */
    @Override
    public Map<String, Object> crawlStockPriceFromNaver(String stockCode) throws Exception {
        logger.info("🌐 네이버 금융 크롤링 시작: {}", stockCode);
        
        String url = NAVER_FINANCE_URL + stockCode;
        
        try {
            // Jsoup으로 HTML 파싱
            Document doc = Jsoup.connect(url)
                    .timeout(REQUEST_TIMEOUT)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();
            
            Map<String, Object> result = new HashMap<>();
            
            // 현재가
            Element todayElement = doc.selectFirst(".no_today .blind");
            BigDecimal closePrice = todayElement != null ? 
                    parsePriceToBigDecimal(todayElement.text()) : BigDecimal.ZERO;
            result.put("closePrice", closePrice);
            
            // 시가, 고가, 저가
            Element tableElement = doc.selectFirst("table.no_info");
            if (tableElement != null) {
                // 시가
                Element openElement = tableElement.selectFirst("tr:nth-child(1) td:nth-child(1) span.blind");
                result.put("openPrice", openElement != null ? 
                        parsePriceToBigDecimal(openElement.text()) : closePrice);
                
                // 고가
                Element highElement = tableElement.selectFirst("tr:nth-child(1) td:nth-child(2) span.blind");
                result.put("highPrice", highElement != null ? 
                        parsePriceToBigDecimal(highElement.text()) : closePrice);
                
                // 저가
                Element lowElement = tableElement.selectFirst("tr:nth-child(2) td:nth-child(1) span.blind");
                result.put("lowPrice", lowElement != null ? 
                        parsePriceToBigDecimal(lowElement.text()) : closePrice);
            } else {
                result.put("openPrice", closePrice);
                result.put("highPrice", closePrice);
                result.put("lowPrice", closePrice);
            }
            
            // 거래량
            Element volumeElement = doc.selectFirst("table.no_info tr:nth-child(2) td:nth-child(2) span.blind");
            Long volume = volumeElement != null ? 
                    parseVolume(volumeElement.text()) : 0L;
            result.put("volume", volume);
            
            result.put("stockCode", stockCode);
            result.put("crawledAt", LocalDate.now().toString());
            
            logger.info("✅ 크롤링 성공: {} = {}원", stockCode, closePrice);
            
            return result;
            
        } catch (Exception e) {
            logger.error("❌ 크롤링 실패: {}", e.getMessage());
            throw new Exception("네이버 금융 크롤링 실패: " + e.getMessage());
        }
    }
    
    /**
     * 최신 주가 조회
     */
    @Override
    public StockPriceVO getLatestStockPrice(String stockCode) throws Exception {
        StockVO stock = stockDAO.selectByCode(stockCode);
        if (stock == null) {
            return null;
        }
        
        return stockPriceDAO.selectLatestByStockId(stock.getStockId());
    }
    
    /**
     * 주가 히스토리 조회
     */
    @Override
    public List<StockPriceVO> getStockPriceHistory(String stockCode, int days) throws Exception {
        StockVO stock = stockDAO.selectByCode(stockCode);
        if (stock == null) {
            throw new Exception("종목을 찾을 수 없습니다");
        }
        
        return stockPriceDAO.selectPriceHistory(stock.getStockId(), days);
    }
    
    /**
     * 가격 문자열을 BigDecimal로 변환
     */
    private BigDecimal parsePriceToBigDecimal(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        try {
            // 쉼표 제거
            String cleaned = priceStr.replaceAll(",", "").trim();
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            logger.warn("가격 파싱 실패: {}", priceStr);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 거래량 문자열을 Long으로 변환
     */
    private Long parseVolume(String volumeStr) {
        if (volumeStr == null || volumeStr.trim().isEmpty()) {
            return 0L;
        }
        
        try {
            String cleaned = volumeStr.replaceAll(",", "").trim();
            return Long.parseLong(cleaned);
        } catch (Exception e) {
            logger.warn("거래량 파싱 실패: {}", volumeStr);
            return 0L;
        }
    }
}

    
