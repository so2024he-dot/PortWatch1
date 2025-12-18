package com.portwatch.service;

import java.io.IOException;
import java.math.BigDecimal;
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
 * 한국 주식 현재가 업데이트 서비스 (개선 버전)
 * 
 * ✅ 주요 개선 사항:
 * - 네이버 금융 크롤링 안정화
 * - STOCK 테이블의 current_price 직접 업데이트
 * - 에러 처리 강화
 * - 로깅 개선
 * 
 * @author PortWatch
 * @version 3.0 (Spring 5.0.7 + MySQL 8.0)
 */
@Service
public class StockPriceUpdateServiceImpl implements StockPriceUpdateService {
    
    private static final Logger logger = LoggerFactory.getLogger(StockPriceUpdateServiceImpl.class);
    
    @Autowired
    private StockDAO stockDAO;
    
    /**
     * 전체 한국 주식 현재가 업데이트
     */
    @Override
    public void updateAllStockPrices() {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🇰🇷 한국 주식 전체 현재가 업데이트 시작");
        
        try {
            // KOSPI 종목
            List<StockVO> kospiStocks = stockDAO.getStocksByMarketType("KOSPI");
            logger.info("📊 KOSPI 종목 수: {}", kospiStocks.size());
            updateStockList(kospiStocks);
            
            // KOSDAQ 종목
            List<StockVO> kosdaqStocks = stockDAO.getStocksByMarketType("KOSDAQ");
            logger.info("📊 KOSDAQ 종목 수: {}", kosdaqStocks.size());
            updateStockList(kosdaqStocks);
            
            logger.info("✅ 한국 주식 전체 현재가 업데이트 완료");
            
        } catch (Exception e) {
            logger.error("❌ 한국 주식 업데이트 실패", e);
            throw new RuntimeException("한국 주식 업데이트 실패", e);
        } finally {
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }
    
    /**
     * 특정 종목의 현재가 업데이트
     */
    @Override
    public void updateStockPrice(String stockCode) {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🔄 종목 현재가 업데이트: {}", stockCode);
        
        try {
            StockVO stock = stockDAO.getStockByCode(stockCode);
            
            if (stock == null) {
                logger.error("❌ 종목을 찾을 수 없음: {}", stockCode);
                throw new RuntimeException("종목을 찾을 수 없습니다: " + stockCode);
            }
            
            // ✅ 수정: updateSingleStock → updateSingleStockInternal
            updateSingleStockInternal(stock);
            
            logger.info("✅ 종목 업데이트 완료: {}", stockCode);
            
        } catch (Exception e) {
            logger.error("❌ 종목 업데이트 실패: {}", stockCode, e);
            throw new RuntimeException("종목 업데이트 실패: " + stockCode, e);
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
                
                // 크롤링 간격 (네이버 서버 부담 방지)
                Thread.sleep(200); // 0.2초
                
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
        String stockCode = stock.getStockCode();
        String stockName = stock.getStockName();
        
        logger.debug("🔍 크롤링 시작: {} ({})", stockName, stockCode);
        
        // 네이버 금융 URL
        String url = "https://finance.naver.com/item/main.nhn?code=" + stockCode;
        
        try {
            // 페이지 크롤링
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .get();
            
            // 현재가 추출
            Element priceElement = doc.selectFirst("p.no_today span.blind");
            if (priceElement == null) {
                logger.warn("⚠️ 현재가를 찾을 수 없음: {}", stockCode);
                return;
            }
            
            String priceText = priceElement.text().replaceAll("[^0-9.]", "");
            BigDecimal currentPrice = new BigDecimal(priceText);
            
            // 전일 대비 (가격 변동폭)
            Element changeElement = doc.selectFirst("p.no_today span.no_up span.blind");
            if (changeElement == null) {
                changeElement = doc.selectFirst("p.no_today span.no_down span.blind");
            }
            
            BigDecimal priceChange = BigDecimal.ZERO;
            if (changeElement != null) {
                String changeText = changeElement.text().replaceAll("[^0-9.-]", "");
                if (!changeText.isEmpty()) {
                    priceChange = new BigDecimal(changeText);
                }
            }
            
            // 등락률 (%)
            Element rateElement = doc.selectFirst("p.no_today em span.blind");
            BigDecimal priceChangeRate = BigDecimal.ZERO;
            if (rateElement != null) {
                String rateText = rateElement.text().replaceAll("[^0-9.-]", "");
                if (!rateText.isEmpty()) {
                    priceChangeRate = new BigDecimal(rateText);
                }
            }
            
            // DB 업데이트
            stockDAO.updateCurrentPrice(stockCode, currentPrice, priceChange, priceChangeRate);
            
            logger.debug("✅ 업데이트 완료: {} - 현재가: {}, 변동: {} ({}%)", 
                stockName, currentPrice, priceChange, priceChangeRate);
            
        } catch (NumberFormatException e) {
            logger.error("❌ 숫자 파싱 오류: {}", stockCode, e);
            throw new IOException("가격 정보 파싱 실패: " + stockCode, e);
        } catch (IOException e) {
            logger.error("❌ 네트워크 오류: {}", stockCode, e);
            throw e;
        } catch (Exception e) {
            logger.error("❌ 크롤링 오류: {}", stockCode, e);
            throw new IOException("크롤링 실패: " + stockCode, e);
        }
    }
    
    /**
     * 시장 타입별 업데이트
     */
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
	public StockPriceVO updateSingleStock(String stockCode) throws Exception {
		logger.info("🔄 단일 한국 종목 업데이트: {}", stockCode);
		
		try {
			StockVO stock = stockDAO.getStockByCode(stockCode);
			
			if (stock == null) {
				throw new RuntimeException("종목을 찾을 수 없습니다: " + stockCode);
			}
			
			updateSingleStockInternal(stock);
			
			// 업데이트 후 현재가 정보 조회
			StockVO updatedStock = stockDAO.getStockByCode(stockCode);
			
			StockPriceVO result = new StockPriceVO();
			result.setStockCode(updatedStock.getStockCode());
			result.setCurrentPrice(updatedStock.getCurrentPrice());
			result.setPriceChange(updatedStock.getPriceChange());
			result.setPriceChangeRate(updatedStock.getPriceChangeRate());
			
			logger.info("✅ 단일 한국 종목 업데이트 완료: {}", stockCode);
			return result;
			
		} catch (Exception e) {
			logger.error("❌ 단일 한국 종목 업데이트 실패: {}", stockCode, e);
			throw e;
		}
	}

	@Override
	public Map<String, StockPriceVO> updateMultipleStocks(List<String> stockCodes) throws Exception {
		logger.info("🔄 복수 한국 종목 업데이트: {} 개", stockCodes.size());
		
		Map<String, StockPriceVO> results = new java.util.HashMap<>();
		
		for (String code : stockCodes) {
			try {
				StockPriceVO result = updateSingleStock(code);
				results.put(code, result);
				
				// 크롤링 간격
				Thread.sleep(200);
				
			} catch (Exception e) {
				logger.warn("⚠️ 종목 업데이트 실패: {}", code, e);
				// 실패한 종목은 null로 표시
				results.put(code, null);
			}
		}
		
		logger.info("✅ 복수 한국 종목 업데이트 완료");
		return results;
	}

	@Override
	public int updateAllStocks() throws Exception {
		logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		logger.info("🇰🇷 한국 주식 전체 업데이트 (int 반환 버전)");
		
		try {
			// KOSPI 종목
			List<StockVO> kospiStocks = stockDAO.getStocksByMarketType("KOSPI");
			int kospiCount = kospiStocks.size();
			logger.info("📊 KOSPI 종목 수: {}", kospiCount);
			updateStockList(kospiStocks);
			
			// KOSDAQ 종목
			List<StockVO> kosdaqStocks = stockDAO.getStocksByMarketType("KOSDAQ");
			int kosdaqCount = kosdaqStocks.size();
			logger.info("📊 KOSDAQ 종목 수: {}", kosdaqCount);
			updateStockList(kosdaqStocks);
			
			int totalCount = kospiCount + kosdaqCount;
			logger.info("✅ 한국 주식 전체 업데이트 완료 - 총 {}개", totalCount);
			logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			
			return totalCount;
			
		} catch (Exception e) {
			logger.error("❌ 한국 주식 업데이트 실패", e);
			throw e;
		}
	}

	@Override
	public Map<String, Object> crawlStockPriceFromNaver(String stockCode) throws Exception {
		// 네이버 금융 직접 크롤링 결과를 Map으로 반환
		logger.debug("🔍 네이버 금융 크롤링: {}", stockCode);
		
		Map<String, Object> result = new java.util.HashMap<>();
		
		try {
			StockVO stock = stockDAO.getStockByCode(stockCode);
			if (stock == null) {
				throw new RuntimeException("종목을 찾을 수 없습니다: " + stockCode);
			}
			
			updateSingleStockInternal(stock);
			
			StockVO updatedStock = stockDAO.getStockByCode(stockCode);
			result.put("stockCode", updatedStock.getStockCode());
			result.put("stockName", updatedStock.getStockName());
			result.put("currentPrice", updatedStock.getCurrentPrice());
			result.put("priceChange", updatedStock.getPriceChange());
			result.put("priceChangeRate", updatedStock.getPriceChangeRate());
			result.put("success", true);
			
			return result;
			
		} catch (Exception e) {
			logger.error("❌ 네이버 금융 크롤링 오류: {}", stockCode, e);
			result.put("success", false);
			result.put("error", e.getMessage());
			return result;
		}
	}

	@Override
	public StockPriceVO getLatestStockPrice(String stockCode) throws Exception {
		logger.debug("📊 최신 주가 조회: {}", stockCode);
		
		StockVO stock = stockDAO.getStockByCode(stockCode);
		
		if (stock == null) {
			throw new RuntimeException("종목을 찾을 수 없습니다: " + stockCode);
		}
		
		StockPriceVO result = new StockPriceVO();
		result.setStockCode(stock.getStockCode());
		result.setCurrentPrice(stock.getCurrentPrice());
		result.setPriceChange(stock.getPriceChange());
		result.setPriceChangeRate(stock.getPriceChangeRate());
		
		return result;
	}

	@Override
	public List<StockPriceVO> getStockPriceHistory(String stockCode, int days) throws Exception {
		// 주가 히스토리 조회 기능은 현재 미지원
		// 필요시 별도의 테이블 (STOCK_PRICE_HISTORY) 생성하여 구현 가능
		throw new UnsupportedOperationException(
			"주가 히스토리 조회 기능은 현재 미지원합니다. " +
			"STOCK 테이블에는 최신 현재가만 저장됩니다. " +
			"히스토리가 필요한 경우 STOCK_PRICE_HISTORY 테이블을 생성하세요."
		);
	}
}
