package com.portwatch.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.StockPriceVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.StockDAO;
import com.portwatch.persistence.StockPriceDAO;

/**
 * ✅ 미국 주식 가격 업데이트 Service 구현
 * 
 * @author PortWatch
 * @version 3.0 COMPLETE - Spring 5.0.7 + MySQL 8.0.33
 */
@Service
public class USStockPriceUpdateServiceImpl implements USStockPriceUpdateService {
    
    @Autowired
    private StockDAO stockDAO;
    
    @Autowired(required = false)
    private StockPriceDAO stockPriceDAO;
    
    /**
     * ✅ 전체 미국 주식 가격 업데이트 (간단 버전)
     */
    @Override
    public void updateAllUSStockPrices() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📈 전체 미국 주식 가격 업데이트 (간단 버전)");
        
        try {
            // NASDAQ과 NYSE 업데이트
            updateByMarketType("NASDAQ");
            updateByMarketType("NYSE");
            
            System.out.println("✅ 전체 미국 주식 업데이트 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 전체 미국 주식 업데이트 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
        }
    }
    
    /**
     * ✅ 특정 종목 가격 업데이트
     * 
     * @param symbol 종목 코드 (예: AAPL, MSFT)
     */
    @Override
    public void updateUSStockPrice(String symbol) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 특정 종목 업데이트");
        System.out.println("  - 종목 코드: " + symbol);
        
        try {
            // Yahoo Finance에서 가격 정보 크롤링
            Map<String, Object> priceData = crawlStockPriceFromYahoo(symbol);
            
            if (priceData != null) {
                // 주식 정보 조회
                StockVO stock = stockDAO.selectByCode(symbol);
                
                if (stock != null) {
                    // 현재가 업데이트
                    BigDecimal currentPrice = (BigDecimal) priceData.get("currentPrice");
                    BigDecimal changeRate = (BigDecimal) priceData.get("changeRate");
                    
                    stock.setCurrentPrice(currentPrice);
                    stock.setChangeRate(changeRate);
                    
                    stockDAO.update(stock);
                    
                    System.out.println("  - 현재가: $" + currentPrice);
                    System.out.println("  - 등락률: " + changeRate + "%");
                    System.out.println("✅ 종목 업데이트 완료");
                } else {
                    System.out.println("⚠️ 종목을 찾을 수 없습니다");
                }
            } else {
                System.out.println("⚠️ 가격 정보를 가져올 수 없습니다");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 종목 업데이트 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
        }
    }
    
    /**
     * ✅ 단일 미국 주식 업데이트 (StockPriceVO 반환)
     * 
     * @param symbol 종목 코드
     * @return StockPriceVO 주가 정보
     * @throws Exception
     */
    @Override
    public StockPriceVO updateSingleUSStock(String symbol) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 단일 종목 업데이트 (StockPriceVO)");
        System.out.println("  - 종목 코드: " + symbol);
        
        try {
            // 가격 정보 크롤링
            Map<String, Object> priceData = crawlStockPriceFromYahoo(symbol);
            
            if (priceData == null) {
                System.out.println("⚠️ 가격 정보를 가져올 수 없습니다");
                return null;
            }
            
            // StockPriceVO 생성
            StockPriceVO stockPrice = new StockPriceVO();
            stockPrice.setOpenPrice((BigDecimal) priceData.get("openPrice"));
            stockPrice.setHighPrice((BigDecimal) priceData.get("highPrice"));
            stockPrice.setLowPrice((BigDecimal) priceData.get("lowPrice"));
            stockPrice.setClosePrice((BigDecimal) priceData.get("currentPrice"));
            stockPrice.setVolume((Long) priceData.get("volume"));
            stockPrice.setPriceDate(new java.sql.Date(System.currentTimeMillis()));
            
            // 주식 ID 조회
            StockVO stock = stockDAO.selectByCode(symbol);
            if (stock != null) {
                stockPrice.setStockId(stock.getStockId());
            }
            
            System.out.println("  - 시가: $" + stockPrice.getOpenPrice());
            System.out.println("  - 고가: $" + stockPrice.getHighPrice());
            System.out.println("  - 저가: $" + stockPrice.getLowPrice());
            System.out.println("  - 종가: $" + stockPrice.getClosePrice());
            System.out.println("  - 거래량: " + stockPrice.getVolume());
            System.out.println("✅ 단일 종목 업데이트 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stockPrice;
            
        } catch (Exception e) {
            System.err.println("❌ 단일 종목 업데이트 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("단일 종목 업데이트 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 여러 미국 주식 동시 업데이트
     * 
     * @param symbols 종목 코드 리스트
     * @return Map<종목코드, StockPriceVO>
     * @throws Exception
     */
    @Override
    public Map<String, StockPriceVO> updateMultipleUSStocks(List<String> symbols) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 여러 종목 동시 업데이트");
        System.out.println("  - 종목 수: " + symbols.size());
        
        Map<String, StockPriceVO> resultMap = new HashMap<>();
        
        try {
            int successCount = 0;
            int failCount = 0;
            
            for (String symbol : symbols) {
                try {
                    System.out.println("  - 업데이트 중: " + symbol);
                    StockPriceVO stockPrice = updateSingleUSStock(symbol);
                    
                    if (stockPrice != null) {
                        resultMap.put(symbol, stockPrice);
                        successCount++;
                    } else {
                        failCount++;
                    }
                    
                    // API Rate Limit 방지
                    Thread.sleep(100);
                    
                } catch (Exception e) {
                    System.err.println("  ❌ " + symbol + " 업데이트 실패");
                    failCount++;
                }
            }
            
            System.out.println("  - 성공: " + successCount + "개");
            System.out.println("  - 실패: " + failCount + "개");
            System.out.println("✅ 여러 종목 업데이트 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return resultMap;
            
        } catch (Exception e) {
            System.err.println("❌ 여러 종목 업데이트 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("여러 종목 업데이트 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 전체 미국 주식 업데이트 (업데이트된 종목 수 반환)
     * 
     * @return 업데이트된 주식 수
     * @throws Exception
     */
    @Override
    @Transactional
    public int updateAllUSStocks() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📈 전체 미국 주식 가격 업데이트");
        
        try {
            int updatedCount = 0;
            
            // NASDAQ 업데이트
            System.out.println("  - NASDAQ 업데이트 시작...");
            updateByMarketType("NASDAQ");
            List<StockVO> nasdaqStocks = stockDAO.selectByMarket("NASDAQ");
            updatedCount += nasdaqStocks.size();
            
            // NYSE 업데이트
            System.out.println("  - NYSE 업데이트 시작...");
            updateByMarketType("NYSE");
            List<StockVO> nyseStocks = stockDAO.selectByMarket("NYSE");
            updatedCount += nyseStocks.size();
            
            System.out.println("  - 총 업데이트: " + updatedCount + "개");
            System.out.println("✅ 전체 미국 주식 가격 업데이트 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return updatedCount;
            
        } catch (Exception e) {
            System.err.println("❌ 전체 미국 주식 가격 업데이트 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("전체 미국 주식 가격 업데이트 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ Yahoo Finance 크롤링 (API 방식)
     * 
     * @param symbol 종목 코드
     * @return 가격 정보 Map
     * @throws Exception
     */
    @Override
    public Map<String, Object> crawlUSStockPriceFromYahoo(String symbol) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🌐 Yahoo Finance 크롤링");
        System.out.println("  - 종목 코드: " + symbol);
        
        try {
            // TODO: 실제 Yahoo Finance API 구현
            // 현재는 더미 데이터 반환
            
            Map<String, Object> priceData = new HashMap<>();
            
            // 더미 데이터 생성
            BigDecimal basePrice = generateDummyPrice(symbol);
            BigDecimal openPrice = basePrice.multiply(new BigDecimal("0.98")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal highPrice = basePrice.multiply(new BigDecimal("1.03")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal lowPrice = basePrice.multiply(new BigDecimal("0.97")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal currentPrice = basePrice;
            Long volume = (long) (Math.random() * 50000000) + 10000000;
            
            // 변동률 계산
            BigDecimal changeRate = currentPrice.subtract(openPrice)
                .divide(openPrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
            
            priceData.put("symbol", symbol);
            priceData.put("openPrice", openPrice);
            priceData.put("highPrice", highPrice);
            priceData.put("lowPrice", lowPrice);
            priceData.put("currentPrice", currentPrice);
            priceData.put("volume", volume);
            priceData.put("changeRate", changeRate);
            priceData.put("timestamp", System.currentTimeMillis());
            
            System.out.println("  - 시가: $" + openPrice);
            System.out.println("  - 현재가: $" + currentPrice);
            System.out.println("  - 등락률: " + changeRate + "%");
            System.out.println("✅ 크롤링 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return priceData;
            
        } catch (Exception e) {
            System.err.println("❌ 크롤링 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("크롤링 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 최신 주가 정보 조회
     * 
     * @param symbol 종목 코드
     * @return StockPriceVO 최신 주가
     * @throws Exception
     */
    @Override
    public StockPriceVO getLatestUSStockPrice(String symbol) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 최신 주가 조회");
        System.out.println("  - 종목 코드: " + symbol);
        
        try {
            // 주식 정보 조회
            StockVO stock = stockDAO.selectByCode(symbol);
            
            if (stock == null) {
                System.out.println("⚠️ 종목을 찾을 수 없습니다");
                return null;
            }
            
            // StockPriceDAO가 없으면 StockVO에서 정보 가져오기
            if (stockPriceDAO != null) {
                StockPriceVO latestPrice = stockPriceDAO.selectLatestByStockId(stock.getStockId());
                
                if (latestPrice != null) {
                    System.out.println("  - 종가: $" + latestPrice.getClosePrice());
                    System.out.println("  - 거래일: " + latestPrice.getPriceDate());
                    System.out.println("✅ 최신 주가 조회 완료");
                } else {
                    System.out.println("⚠️ 주가 정보가 없습니다");
                }
                
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return latestPrice;
            } else {
                // StockPriceDAO가 없으면 현재 StockVO의 정보로 StockPriceVO 생성
                StockPriceVO stockPrice = new StockPriceVO();
                stockPrice.setStockId(stock.getStockId());
                stockPrice.setClosePrice(stock.getCurrentPrice());
                stockPrice.setPriceDate(new java.sql.Date(System.currentTimeMillis()));
                
                System.out.println("  - 현재가: $" + stock.getCurrentPrice());
                System.out.println("✅ 현재 정보 조회 완료 (StockPriceDAO 없음)");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                return stockPrice;
            }
            
        } catch (Exception e) {
            System.err.println("❌ 최신 주가 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("최신 주가 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 주가 이력 조회
     * 
     * @param symbol 종목 코드
     * @param days 조회 일수
     * @return 주가 이력 리스트
     * @throws Exception
     */
    @Override
    public List<StockPriceVO> getUSStockPriceHistory(String symbol, int days) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 주가 이력 조회");
        System.out.println("  - 종목 코드: " + symbol);
        System.out.println("  - 조회 일수: " + days + "일");
        
        try {
            // 주식 정보 조회
            StockVO stock = stockDAO.selectByCode(symbol);
            
            if (stock == null) {
                System.out.println("⚠️ 종목을 찾을 수 없습니다");
                return new ArrayList<>();
            }
            
            List<StockPriceVO> priceHistory = new ArrayList<>();
            
            // StockPriceDAO가 있으면 사용
            if (stockPriceDAO != null) {
                priceHistory = stockPriceDAO.selectRecentByStockId(stock.getStockId(), days);
            }
            
            if (priceHistory == null) {
                priceHistory = new ArrayList<>();
            }
            
            System.out.println("  - 조회 결과: " + priceHistory.size() + "건");
            System.out.println("✅ 주가 이력 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return priceHistory;
            
        } catch (Exception e) {
            System.err.println("❌ 주가 이력 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("주가 이력 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 시장별 주식 업데이트 (NASDAQ, NYSE)
     * 
     * @param marketType 시장 타입
     */
    @Override
    @Transactional
    public void updateByMarketType(String marketType) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 시장별 주식 업데이트");
        System.out.println("  - 시장: " + marketType);
        
        try {
            // 해당 시장의 주식 조회
            List<StockVO> stocks = stockDAO.selectByMarket(marketType);
            
            System.out.println("  - " + marketType + " 종목: " + stocks.size() + "개");
            
            int successCount = 0;
            int failCount = 0;
            
            // 각 주식 업데이트
            for (StockVO stock : stocks) {
                try {
                    // Yahoo Finance 크롤링
                    Map<String, Object> priceData = crawlStockPriceFromYahoo(stock.getStockCode());
                    
                    if (priceData != null) {
                        // 현재가 업데이트
                        BigDecimal currentPrice = (BigDecimal) priceData.get("currentPrice");
                        BigDecimal changeRate = (BigDecimal) priceData.get("changeRate");
                        
                        stock.setCurrentPrice(currentPrice);
                        stock.setChangeRate(changeRate);
                        
                        stockDAO.update(stock);
                        successCount++;
                    } else {
                        failCount++;
                    }
                    
                    // API Rate Limit 방지
                    Thread.sleep(100);
                    
                } catch (Exception e) {
                    System.err.println("  ❌ " + stock.getStockCode() + " 업데이트 실패");
                    failCount++;
                }
            }
            
            System.out.println("  - 성공: " + successCount + "개");
            System.out.println("  - 실패: " + failCount + "개");
            System.out.println("✅ 시장별 주식 업데이트 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 시장별 주식 업데이트 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
        }
    }
    
    /**
     * ✅ Yahoo Finance 주식 가격 크롤링 (인터페이스 구현용)
     * 
     * @param symbol 종목코드
     * @return 주식 가격 데이터 Map
     */
    @Override
    public Map<String, Object> crawlStockPriceFromYahoo(String symbol) {
        try {
            return crawlUSStockPriceFromYahoo(symbol);
        } catch (Exception e) {
            System.err.println("❌ 크롤링 실패: " + e.getMessage());
            return null;
        }
    }
    
    // ============================================
    // Helper 메서드 (더미 데이터 생성용)
    // ============================================
    
    /**
     * 더미 주가 생성 (개발/테스트용)
     */
    private BigDecimal generateDummyPrice(String symbol) {
        Map<String, BigDecimal> basePrices = new HashMap<>();
        basePrices.put("AAPL", new BigDecimal("195.50"));
        basePrices.put("MSFT", new BigDecimal("378.20"));
        basePrices.put("GOOGL", new BigDecimal("142.30"));
        basePrices.put("AMZN", new BigDecimal("178.90"));
        basePrices.put("TSLA", new BigDecimal("248.50"));
        basePrices.put("NVDA", new BigDecimal("495.20"));
        basePrices.put("META", new BigDecimal("362.50"));
        basePrices.put("NFLX", new BigDecimal("528.30"));
        basePrices.put("AMD", new BigDecimal("148.70"));
        basePrices.put("INTC", new BigDecimal("42.80"));
        basePrices.put("JPM", new BigDecimal("152.30"));
        basePrices.put("BAC", new BigDecimal("34.50"));
        basePrices.put("WMT", new BigDecimal("168.90"));
        basePrices.put("DIS", new BigDecimal("95.80"));
        basePrices.put("V", new BigDecimal("258.40"));
        
        BigDecimal basePrice = basePrices.getOrDefault(symbol, new BigDecimal("100.00"));
        
        // ±2% 랜덤 변동
        double variation = (Math.random() - 0.5) * 0.04;
        BigDecimal price = basePrice.multiply(BigDecimal.ONE.add(new BigDecimal(variation)));
        
        return price.setScale(2, RoundingMode.HALF_UP);
    }
}
