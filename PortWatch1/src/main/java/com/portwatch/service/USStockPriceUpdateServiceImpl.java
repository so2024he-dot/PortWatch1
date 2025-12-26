    package com.portwatch.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.StockPriceVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.StockDAO;


/**
 * ✅ 미국 주식 가격 업데이트 Service 구현 V3 (수정)
 * 
 * @author PortWatch
 * @version 3.0 FINAL - FIXED
 */
@Service
public class USStockPriceUpdateServiceImpl implements USStockPriceUpdateService {
    
    @Autowired
    private StockDAO stockDAO;  // ✅ 수정: StockVO → StockDAO
    
    /**
     * ✅ 전체 미국 주식 업데이트
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
     * ✅ Yahoo Finance 주식 가격 크롤링
     * 
     * 실제 구현 시:
     * - Yahoo Finance API 사용
     * - Jsoup HTML 파싱
     * - Alpha Vantage API
     * 
     * @param symbol 종목코드
     * @return 주식 가격 데이터 Map
     */
    @Override
    public Map<String, Object> crawlStockPriceFromYahoo(String symbol) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🌐 Yahoo Finance 크롤링");
        System.out.println("  - 종목코드: " + symbol);
        
        try {
            // TODO: 실제 Yahoo Finance API 또는 크롤링 구현
            // 현재는 더미 데이터 생성
            
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
            System.out.println("  - 고가: $" + highPrice);
            System.out.println("  - 저가: $" + lowPrice);
            System.out.println("  - 현재가: $" + currentPrice);
            System.out.println("  - 거래량: " + volume);
            System.out.println("  - 변동률: " + changeRate + "%");
            System.out.println("✅ 크롤링 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return priceData;
            
        } catch (Exception e) {
            System.err.println("❌ 크롤링 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return null;
        }
    }
    
    /**
     * ✅ 더미 주식 가격 생성 (개발/테스트용)
     * 
     * @param symbol 종목코드
     * @return 더미 가격
     */
    private BigDecimal generateDummyPrice(String symbol) {
        // 종목코드별 기본 가격 설정
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
        
        // 기본 가격 조회 (없으면 100.00)
        BigDecimal basePrice = basePrices.getOrDefault(symbol, new BigDecimal("100.00"));
        
        // ±2% 랜덤 변동
        double variation = (Math.random() - 0.5) * 0.04; // -0.02 ~ +0.02
        BigDecimal price = basePrice.multiply(BigDecimal.ONE.add(new BigDecimal(variation)));
        
        return price.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * ✅ 더미 변동률 생성
     * 
     * @return -5% ~ +5% 랜덤 변동률
     */
    private BigDecimal generateDummyChangeRate() {
        double changeRate = (Math.random() - 0.5) * 10.0; // -5.0 ~ +5.0
        return new BigDecimal(changeRate).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void updateAllUSStockPrices() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateUSStockPrice(String symbol) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public StockPriceVO updateSingleUSStock(String symbol) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, StockPriceVO> updateMultipleUSStocks(List<String> symbols) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> crawlUSStockPriceFromYahoo(String symbol) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StockPriceVO getLatestUSStockPrice(String symbol) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<StockPriceVO> getUSStockPriceHistory(String symbol, int days) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}

    
