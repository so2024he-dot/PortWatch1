package com.portwatch.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portwatch.domain.StockPriceVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.StockDAO;

/**
 * 미국 주식 현재가 업데이트 서비스 (개선 버전)
 * 
 * ✅ 주요 개선 사항:
 * - Alpha Vantage API 우선 사용
 * - Yahoo Finance 크롤링 대체 방법
 * - STOCK 테이블의 current_price 직접 업데이트
 * - 에러 처리 강화
 * 
 * @author PortWatch
 * @version 3.0 (Spring 5.0.7 + MySQL 8.0)
 */
@Service
public class USStockPriceUpdateServiceImpl implements USStockPriceUpdateService {
    
    private static final Logger logger = LoggerFactory.getLogger(USStockPriceUpdateServiceImpl.class);
    
    @Autowired
    private StockDAO stockDAO;
    
    @Value("${alphavantage.api.key:demo}")
    private String alphaVantageApiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
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
    public void updateUSStockPrice(String stockCode) {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🔄 미국 종목 현재가 업데이트: {}", stockCode);
        
        try {
            StockVO stock = stockDAO.getStockByCode(stockCode);
            
            if (stock == null) {
                logger.error("❌ 종목을 찾을 수 없음: {}", stockCode);
                throw new RuntimeException("종목을 찾을 수 없습니다: " + stockCode);
            }
            
            updateSingleStockInternal(stock);
            
            logger.info("✅ 미국 종목 업데이트 완료: {}", stockCode);
            
        } catch (Exception e) {
            logger.error("❌ 미국 종목 업데이트 실패: {}", stockCode, e);
            throw new RuntimeException("미국 종목 업데이트 실패: " + stockCode, e);
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
                
                // API 요청 간격 (Alpha Vantage 제한 방지)
                Thread.sleep(12000); // 12초 (5 requests/minute 제한 준수)
                
            } catch (Exception e) {
                failCount++;
                logger.warn("⚠️ 미국 종목 업데이트 실패 ({} - {}): {}", 
                    stock.getStockCode(), stock.getStockName(), e.getMessage());
                
                // 실패 시 대기 시간 증가
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        logger.info("📊 업데이트 결과 - 성공: {}, 실패: {}", successCount, failCount);
    }
    
    /**
     * 단일 종목 현재가 크롤링 및 업데이트
     * 
     * 우선순위:
     * 1. Alpha Vantage API
     * 2. Yahoo Finance 크롤링
     */
    private void updateSingleStockInternal(StockVO stock) throws Exception {
        String stockCode = stock.getStockCode();
        String stockName = stock.getStockName();
        
        logger.debug("🔍 미국 주식 크롤링 시작: {} ({})", stockName, stockCode);
        
        boolean success = false;
        
        // 방법 1: Alpha Vantage API 사용
        if (!success && !"demo".equals(alphaVantageApiKey)) {
            try {
                success = updateViaAlphaVantage(stock);
            } catch (Exception e) {
                logger.warn("⚠️ Alpha Vantage 실패: {}", e.getMessage());
            }
        }
        
        // 방법 2: Yahoo Finance 크롤링
        if (!success) {
            try {
                success = updateViaYahooFinance(stock);
            } catch (Exception e) {
                logger.warn("⚠️ Yahoo Finance 실패: {}", e.getMessage());
            }
        }
        
        if (!success) {
            throw new IOException("모든 데이터 소스에서 현재가를 가져올 수 없습니다: " + stockCode);
        }
    }
    
    /**
     * Alpha Vantage API로 현재가 조회
     */
    private boolean updateViaAlphaVantage(StockVO stock) throws Exception {
        String stockCode = stock.getStockCode();
        
        // API URL
        String url = String.format(
            "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
            stockCode, alphaVantageApiKey
        );
        
        logger.debug("🌐 Alpha Vantage API 호출: {}", stockCode);
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode globalQuote = root.path("Global Quote");
                
                if (!globalQuote.isMissingNode()) {
                    // 현재가
                    String priceStr = globalQuote.path("05. price").asText();
                    BigDecimal currentPrice = new BigDecimal(priceStr).setScale(2, RoundingMode.HALF_UP);
                    
                    // 가격 변동
                    String changeStr = globalQuote.path("09. change").asText();
                    BigDecimal priceChange = new BigDecimal(changeStr).setScale(2, RoundingMode.HALF_UP);
                    
                    // 변동률
                    String changePercentStr = globalQuote.path("10. change percent").asText().replace("%", "");
                    BigDecimal priceChangeRate = new BigDecimal(changePercentStr).setScale(2, RoundingMode.HALF_UP);
                    
                    // DB 업데이트
                    stockDAO.updateCurrentPrice(stockCode, currentPrice, priceChange, priceChangeRate);
                    
                    logger.debug("✅ Alpha Vantage 업데이트 완료: {} - ${}", stock.getStockName(), currentPrice);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("❌ Alpha Vantage API 오류: {}", stockCode, e);
        }
        
        return false;
    }
    
    /**
     * Yahoo Finance 크롤링으로 현재가 조회
     */
    private boolean updateViaYahooFinance(StockVO stock) throws Exception {
        String stockCode = stock.getStockCode();
        
        // Yahoo Finance URL
        String url = "https://finance.yahoo.com/quote/" + stockCode;
        
        logger.debug("🌐 Yahoo Finance 크롤링: {}", stockCode);
        
        try {
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(15000)
                .get();
            
            // 현재가 추출 (다양한 선택자 시도)
            Element priceElement = doc.selectFirst("fin-streamer[data-symbol=\"" + stockCode + "\"][data-field=\"regularMarketPrice\"]");
            
            if (priceElement == null) {
                priceElement = doc.selectFirst("fin-streamer[data-field=\"regularMarketPrice\"]");
            }
            
            if (priceElement == null) {
                priceElement = doc.selectFirst("span[data-reactid*=\"price\"]");
            }
            
            if (priceElement == null) {
                logger.warn("⚠️ Yahoo Finance에서 현재가를 찾을 수 없음: {}", stockCode);
                return false;
            }
            
            String priceText = priceElement.attr("value");
            if (priceText == null || priceText.isEmpty()) {
                priceText = priceElement.text();
            }
            
            priceText = priceText.replaceAll("[^0-9.]", "");
            BigDecimal currentPrice = new BigDecimal(priceText).setScale(2, RoundingMode.HALF_UP);
            
            // 가격 변동 추출
            Element changeElement = doc.selectFirst("fin-streamer[data-field=\"regularMarketChange\"]");
            BigDecimal priceChange = BigDecimal.ZERO;
            if (changeElement != null) {
                String changeText = changeElement.attr("value");
                if (changeText == null || changeText.isEmpty()) {
                    changeText = changeElement.text();
                }
                changeText = changeText.replaceAll("[^0-9.-]", "");
                if (!changeText.isEmpty()) {
                    priceChange = new BigDecimal(changeText).setScale(2, RoundingMode.HALF_UP);
                }
            }
            
            // 변동률 추출
            Element rateElement = doc.selectFirst("fin-streamer[data-field=\"regularMarketChangePercent\"]");
            BigDecimal priceChangeRate = BigDecimal.ZERO;
            if (rateElement != null) {
                String rateText = rateElement.attr("value");
                if (rateText == null || rateText.isEmpty()) {
                    rateText = rateElement.text();
                }
                rateText = rateText.replaceAll("[^0-9.-]", "");
                if (!rateText.isEmpty()) {
                    priceChangeRate = new BigDecimal(rateText).setScale(2, RoundingMode.HALF_UP);
                }
            }
            
            // DB 업데이트
            stockDAO.updateCurrentPrice(stockCode, currentPrice, priceChange, priceChangeRate);
            
            logger.debug("✅ Yahoo Finance 업데이트 완료: {} - ${}", stock.getStockName(), currentPrice);
            return true;
            
        } catch (NumberFormatException e) {
            logger.error("❌ Yahoo Finance 파싱 오류: {}", stockCode, e);
        } catch (IOException e) {
            logger.error("❌ Yahoo Finance 네트워크 오류: {}", stockCode, e);
        }
        
        return false;
    }
    
    /**
     * 시장 타입별 업데이트
     */
    @Override
    public void updateByMarketType(String marketType) {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📊 {} 종목 현재가 업데이트 시작", marketType);
        
        try {
            List<StockVO> stocks = stockDAO.getStocksByMarketType(marketType);
            logger.info("종목 수: {}", stocks.size());
            updateStockList(stocks);
            logger.info("✅ {} 업데이트 완료", marketType);
        } catch (Exception e) {
            logger.error("❌ {} 업데이트 실패", marketType, e);
            throw new RuntimeException(marketType + " 업데이트 실패", e);
        } finally {
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }

	@Override
	public StockPriceVO updateSingleUSStock(String stockSymbol) throws Exception {
		logger.info("🔄 단일 미국 종목 업데이트: {}", stockSymbol);
		
		try {
			StockVO stock = stockDAO.getStockByCode(stockSymbol);
			
			if (stock == null) {
				throw new RuntimeException("종목을 찾을 수 없습니다: " + stockSymbol);
			}
			
			updateSingleStockInternal(stock);
			
			// 업데이트 후 현재가 정보 조회
			StockVO updatedStock = stockDAO.getStockByCode(stockSymbol);
			
			StockPriceVO result = new StockPriceVO();
			result.setStockCode(updatedStock.getStockCode());
			result.setCurrentPrice(updatedStock.getCurrentPrice());
			result.setPriceChange(updatedStock.getPriceChange());
			result.setPriceChangeRate(updatedStock.getPriceChangeRate());
			
			logger.info("✅ 단일 미국 종목 업데이트 완료: {}", stockSymbol);
			return result;
			
		} catch (Exception e) {
			logger.error("❌ 단일 미국 종목 업데이트 실패: {}", stockSymbol, e);
			throw e;
		}
	}

	@Override
	public Map<String, StockPriceVO> updateMultipleUSStocks(List<String> stockSymbols) throws Exception {
		logger.info("🔄 복수 미국 종목 업데이트: {} 개", stockSymbols.size());
		
		Map<String, StockPriceVO> results = new java.util.HashMap<>();
		
		for (String symbol : stockSymbols) {
			try {
				StockPriceVO result = updateSingleUSStock(symbol);
				results.put(symbol, result);
				
				// API 제한 방지
				Thread.sleep(12000);
				
			} catch (Exception e) {
				logger.warn("⚠️ 종목 업데이트 실패: {}", symbol, e);
				// 실패한 종목은 null로 표시
				results.put(symbol, null);
			}
		}
		
		logger.info("✅ 복수 미국 종목 업데이트 완료");
		return results;
	}

	@Override
	public int updateAllUSStocks() throws Exception {
		logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		logger.info("🇺🇸 미국 주식 전체 업데이트 (int 반환 버전)");
		
		try {
			// NASDAQ 종목
			List<StockVO> nasdaqStocks = stockDAO.getStocksByMarketType("NASDAQ");
			int nasdaqCount = nasdaqStocks.size();
			logger.info("📊 NASDAQ 종목 수: {}", nasdaqCount);
			updateStockList(nasdaqStocks);
			
			// NYSE 종목
			List<StockVO> nyseStocks = stockDAO.getStocksByMarketType("NYSE");
			int nyseCount = nyseStocks.size();
			logger.info("📊 NYSE 종목 수: {}", nyseCount);
			updateStockList(nyseStocks);
			
			// AMEX 종목
			List<StockVO> amexStocks = stockDAO.getStocksByMarketType("AMEX");
			int amexCount = amexStocks.size();
			logger.info("📊 AMEX 종목 수: {}", amexCount);
			updateStockList(amexStocks);
			
			int totalCount = nasdaqCount + nyseCount + amexCount;
			logger.info("✅ 미국 주식 전체 업데이트 완료 - 총 {}개", totalCount);
			logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			
			return totalCount;
			
		} catch (Exception e) {
			logger.error("❌ 미국 주식 업데이트 실패", e);
			throw e;
		}
	}

	@Override
	public Map<String, Object> crawlStockPriceFromYahoo(String stockSymbol) throws Exception {
		// Yahoo Finance 직접 크롤링 결과를 Map으로 반환
		logger.debug("🔍 Yahoo Finance 크롤링: {}", stockSymbol);
		
		Map<String, Object> result = new java.util.HashMap<>();
		
		try {
			StockVO stock = stockDAO.getStockByCode(stockSymbol);
			if (stock == null) {
				throw new RuntimeException("종목을 찾을 수 없습니다: " + stockSymbol);
			}
			
			boolean success = updateViaYahooFinance(stock);
			
			if (success) {
				StockVO updatedStock = stockDAO.getStockByCode(stockSymbol);
				result.put("stockCode", updatedStock.getStockCode());
				result.put("stockName", updatedStock.getStockName());
				result.put("currentPrice", updatedStock.getCurrentPrice());
				result.put("priceChange", updatedStock.getPriceChange());
				result.put("priceChangeRate", updatedStock.getPriceChangeRate());
				result.put("success", true);
			} else {
				result.put("success", false);
				result.put("error", "Yahoo Finance 크롤링 실패");
			}
			
			return result;
			
		} catch (Exception e) {
			logger.error("❌ Yahoo Finance 크롤링 오류: {}", stockSymbol, e);
			result.put("success", false);
			result.put("error", e.getMessage());
			return result;
		}
	}

	@Override
	public StockPriceVO getLatestUSStockPrice(String stockSymbol) throws Exception {
		logger.debug("📊 최신 주가 조회: {}", stockSymbol);
		
		StockVO stock = stockDAO.getStockByCode(stockSymbol);
		
		if (stock == null) {
			throw new RuntimeException("종목을 찾을 수 없습니다: " + stockSymbol);
		}
		
		StockPriceVO result = new StockPriceVO();
		result.setStockCode(stock.getStockCode());
		result.setCurrentPrice(stock.getCurrentPrice());
		result.setPriceChange(stock.getPriceChange());
		result.setPriceChangeRate(stock.getPriceChangeRate());
		
		return result;
	}

	@Override
	public List<StockPriceVO> getUSStockPriceHistory(String stockSymbol, int days) throws Exception {
		// 주가 히스토리 조회 기능은 현재 미지원
		// 필요시 별도의 테이블 (STOCK_PRICE_HISTORY) 생성하여 구현 가능
		throw new UnsupportedOperationException(
			"주가 히스토리 조회 기능은 현재 미지원합니다. " +
			"STOCK 테이블에는 최신 현재가만 저장됩니다. " +
			"히스토리가 필요한 경우 STOCK_PRICE_HISTORY 테이블을 생성하세요."
		);
	}
}
