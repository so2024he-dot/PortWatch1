package com.portwatch.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portwatch.domain.StockPriceVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.StockDAO;

/**
 * ✅ 미국 주식 현재가 업데이트 서비스
 * 
 * Yahoo Finance에서 미국 주식 가격 크롤링
 * 
 * @author PortWatch
 * @version 3.0 - Spring 5.0.7 + MySQL 8.0
 */
@Service
public class USStockPriceUpdateServiceImpl implements USStockPriceUpdateService {
    
    private static final Logger logger = LoggerFactory.getLogger(USStockPriceUpdateServiceImpl.class);
    
    @Autowired
    private StockDAO stockDAO;
    
    /**
     * 전체 미국 주식 현재가 업데이트
     */
    @Override
    public void updateAllUSStockPrices() {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🇺🇸 미국 주식 전체 현재가 업데이트 시작");
        
        try {
            // NASDAQ 종목
            List<StockVO> nasdaqStocks = stockDAO.getStocksByMarketType("NASDAQ");
            logger.info("📊 NASDAQ 종목 수: {}", nasdaqStocks.size());
            updateStockList(nasdaqStocks);
            
            // NYSE 종목
            List<StockVO> nyseStocks = stockDAO.getStocksByMarketType("NYSE");
            logger.info("📊 NYSE 종목 수: {}", nyseStocks.size());
            updateStockList(nyseStocks);
            
            // AMEX 종목
            List<StockVO> amexStocks = stockDAO.getStocksByMarketType("AMEX");
            logger.info("📊 AMEX 종목 수: {}", amexStocks.size());
            updateStockList(amexStocks);
            
            logger.info("✅ 미국 주식 전체 현재가 업데이트 완료");
            
        } catch (Exception e) {
            logger.error("❌ 미국 주식 업데이트 실패", e);
            throw new RuntimeException("미국 주식 업데이트 실패", e);
        } finally {
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }
    
    /**
     * 특정 종목의 현재가 업데이트
     */
    @Override
    public void updateUSStockPrice(String symbol) {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🔄 미국 종목 현재가 업데이트: {}", symbol);
        
        try {
            StockVO stock = stockDAO.getStockByCode(symbol);
            
            if (stock == null) {
                logger.error("❌ 종목을 찾을 수 없음: {}", symbol);
                throw new RuntimeException("종목을 찾을 수 없습니다: " + symbol);
            }
            
            updateSingleStockInternal(stock);
            
            logger.info("✅ 종목 업데이트 완료: {}", symbol);
            
        } catch (Exception e) {
            logger.error("❌ 종목 업데이트 실패: {}", symbol, e);
            throw new RuntimeException("종목 업데이트 실패: " + symbol, e);
        } finally {
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }
    
    /**
     * 종목 리스트 일괄 업데이트
     */
    private void updateStockList(List<StockVO> stocks) {
        int successCount = 0;
        int failCount = 0;
        
        for (StockVO stock : stocks) {
            try {
                updateSingleStockInternal(stock);
                successCount++;
                
                // 크롤링 간격 (Yahoo Finance 서버 부담 방지)
                Thread.sleep(500); // 0.5초
                
            } catch (Exception e) {
                failCount++;
                logger.warn("⚠️ 종목 업데이트 실패 ({} - {}): {}", 
                    stock.getStockCode(), stock.getStockName(), e.getMessage());
            }
        }
        
        logger.info("📊 업데이트 결과 - 성공: {}, 실패: {}", successCount, failCount);
    }
    
    /**
     * 단일 종목 현재가 크롤링 및 업데이트
     */
    private void updateSingleStockInternal(StockVO stock) throws IOException {
        String symbol = stock.getStockCode();
        String stockName = stock.getStockName();
        
        logger.debug("🔍 크롤링 시작: {} ({})", stockName, symbol);
        
        // Yahoo Finance URL
        String url = "https://finance.yahoo.com/quote/" + symbol;
        
        try {
            // 페이지 크롤링
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(15000)
                .get();
            
            // 현재가 추출 (Yahoo Finance 구조)
            Element priceElement = doc.selectFirst("fin-streamer[data-symbol='" + symbol + "'][data-field='regularMarketPrice']");
            
            if (priceElement == null) {
                // 대체 방법
                priceElement = doc.selectFirst("span[data-reactid*='regularMarketPrice']");
            }
            
            if (priceElement == null) {
                logger.warn("⚠️ 현재가를 찾을 수 없음: {}", symbol);
                return;
            }
            
            String priceText = priceElement.attr("value");
            if (priceText == null || priceText.isEmpty()) {
                priceText = priceElement.text();
            }
            
            priceText = priceText.replaceAll("[^0-9.]", "");
            BigDecimal currentPrice = new BigDecimal(priceText);
            
            // 전일 대비 추출
            Element changeElement = doc.selectFirst("fin-streamer[data-symbol='" + symbol + "'][data-field='regularMarketChange']");
            BigDecimal priceChange = BigDecimal.ZERO;
            
            if (changeElement != null) {
                String changeText = changeElement.attr("value");
                if (changeText == null || changeText.isEmpty()) {
                    changeText = changeElement.text();
                }
                changeText = changeText.replaceAll("[^0-9.-]", "");
                if (!changeText.isEmpty()) {
                    priceChange = new BigDecimal(changeText);
                }
            }
            
            // 등락률 추출
            Element rateElement = doc.selectFirst("fin-streamer[data-symbol='" + symbol + "'][data-field='regularMarketChangePercent']");
            BigDecimal priceChangeRate = BigDecimal.ZERO;
            
            if (rateElement != null) {
                String rateText = rateElement.attr("value");
                if (rateText == null || rateText.isEmpty()) {
                    rateText = rateElement.text();
                }
                rateText = rateText.replaceAll("[^0-9.-]", "");
                if (!rateText.isEmpty()) {
                    priceChangeRate = new BigDecimal(rateText);
                }
            }
            
            // DB 업데이트
            stockDAO.updateCurrentPrice(symbol, currentPrice, priceChange, priceChangeRate);
            
            logger.debug("✅ 업데이트 완료: {} - 현재가: {} USD, 변동: {} ({}%)", 
                stockName, currentPrice, priceChange, priceChangeRate);
            
        } catch (NumberFormatException e) {
            logger.error("❌ 숫자 파싱 오류: {}", symbol, e);
            throw new IOException("가격 정보 파싱 실패: " + symbol, e);
        } catch (IOException e) {
            logger.error("❌ 네트워크 오류: {}", symbol, e);
            throw e;
        } catch (Exception e) {
            logger.error("❌ 크롤링 오류: {}", symbol, e);
            throw new IOException("크롤링 실패: " + symbol, e);
        }
    }
    
    /**
     * 단일 종목 업데이트 (StockPriceVO 반환)
     */
    @Override
    public StockPriceVO updateSingleUSStock(String symbol) throws Exception {
        logger.info("🔄 단일 미국 종목 업데이트: {}", symbol);
        
        try {
            StockVO stock = stockDAO.getStockByCode(symbol);
            
            if (stock == null) {
                throw new RuntimeException("종목을 찾을 수 없습니다: " + symbol);
            }
            
            updateSingleStockInternal(stock);
            
            // 업데이트 후 현재가 정보 조회
            StockVO updatedStock = stockDAO.getStockByCode(symbol);
            
            StockPriceVO result = new StockPriceVO();
            result.setStockCode(updatedStock.getStockCode());
            result.setCurrentPrice(updatedStock.getCurrentPrice());
            result.setPriceChange(updatedStock.getPriceChange());
            result.setPriceChangeRate(updatedStock.getPriceChangeRate());
            
            logger.info("✅ 단일 미국 종목 업데이트 완료: {}", symbol);
            return result;
            
        } catch (Exception e) {
            logger.error("❌ 단일 미국 종목 업데이트 실패: {}", symbol, e);
            throw e;
        }
    }
    
    /**
     * 복수 종목 업데이트
     */
    @Override
    public Map<String, StockPriceVO> updateMultipleUSStocks(List<String> symbols) throws Exception {
        logger.info("🔄 복수 미국 종목 업데이트: {} 개", symbols.size());
        
        Map<String, StockPriceVO> results = new HashMap<>();
        
        for (String symbol : symbols) {
            try {
                StockPriceVO result = updateSingleUSStock(symbol);
                results.put(symbol, result);
                
                // 크롤링 간격
                Thread.sleep(500);
                
            } catch (Exception e) {
                logger.warn("⚠️ 종목 업데이트 실패: {}", symbol, e);
                // 실패한 종목은 null로 표시
                results.put(symbol, null);
            }
        }
        
        logger.info("✅ 복수 미국 종목 업데이트 완료");
        return results;
    }
    
    /**
     * 전체 미국 주식 업데이트 (int 반환)
     */
    @Override
    public int updateAllUSStocks() throws Exception {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🇺🇸 미국 주식 전체 업데이트 (int 반환 버전)");
        
        try {
            int totalCount = 0;
            
            // NASDAQ 종목
            List<StockVO> nasdaqStocks = stockDAO.getStocksByMarketType("NASDAQ");
            totalCount += nasdaqStocks.size();
            logger.info("📊 NASDAQ 종목 수: {}", nasdaqStocks.size());
            updateStockList(nasdaqStocks);
            
            // NYSE 종목
            List<StockVO> nyseStocks = stockDAO.getStocksByMarketType("NYSE");
            totalCount += nyseStocks.size();
            logger.info("📊 NYSE 종목 수: {}", nyseStocks.size());
            updateStockList(nyseStocks);
            
            // AMEX 종목
            List<StockVO> amexStocks = stockDAO.getStocksByMarketType("AMEX");
            totalCount += amexStocks.size();
            logger.info("📊 AMEX 종목 수: {}", amexStocks.size());
            updateStockList(amexStocks);
            
            logger.info("✅ 미국 주식 전체 업데이트 완료 - 총 {}개", totalCount);
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return totalCount;
            
        } catch (Exception e) {
            logger.error("❌ 미국 주식 업데이트 실패", e);
            throw e;
        }
    }
    
    /**
     * Yahoo Finance 직접 크롤링 결과를 Map으로 반환
     */
    @Override
    public Map<String, Object> crawlUSStockPriceFromYahoo(String symbol) throws Exception {
        logger.debug("🔍 Yahoo Finance 크롤링: {}", symbol);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            StockVO stock = stockDAO.getStockByCode(symbol);
            if (stock == null) {
                throw new RuntimeException("종목을 찾을 수 없습니다: " + symbol);
            }
            
            updateSingleStockInternal(stock);
            
            StockVO updatedStock = stockDAO.getStockByCode(symbol);
            result.put("stockCode", updatedStock.getStockCode());
            result.put("stockName", updatedStock.getStockName());
            result.put("currentPrice", updatedStock.getCurrentPrice());
            result.put("priceChange", updatedStock.getPriceChange());
            result.put("priceChangeRate", updatedStock.getPriceChangeRate());
            result.put("success", true);
            
            return result;
            
        } catch (Exception e) {
            logger.error("❌ Yahoo Finance 크롤링 오류: {}", symbol, e);
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }
    
    /**
     * 최신 주가 조회
     */
    @Override
    public StockPriceVO getLatestUSStockPrice(String symbol) throws Exception {
        logger.debug("📊 최신 미국 주가 조회: {}", symbol);
        
        StockVO stock = stockDAO.getStockByCode(symbol);
        
        if (stock == null) {
            throw new RuntimeException("종목을 찾을 수 없습니다: " + symbol);
        }
        
        StockPriceVO result = new StockPriceVO();
        result.setStockCode(stock.getStockCode());
        result.setCurrentPrice(stock.getCurrentPrice());
        result.setPriceChange(stock.getPriceChange());
        result.setPriceChangeRate(stock.getPriceChangeRate());
        
        return result;
    }
    
    /**
     * 주가 히스토리 조회 (현재 미지원)
     */
    @Override
    public List<StockPriceVO> getUSStockPriceHistory(String symbol, int days) throws Exception {
        throw new UnsupportedOperationException(
            "미국 주가 히스토리 조회 기능은 현재 미지원입니다. " +
            "STOCK 테이블에는 최신 현재가만 저장됩니다. " +
            "히스토리가 필요한 경우 STOCK_PRICE_HISTORY 테이블을 생성하세요."
        );
    }

	@Override
	public void updateByMarketType(String marketType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Object> crawlStockPriceFromYahoo(String symbol) {
		// TODO Auto-generated method stub
		return null;
	}
}
