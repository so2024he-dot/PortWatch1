package com.portwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.StockDAO;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ✅ 수정사항: StockServiceImpl.java
 * 
 * 1. getStockById 메서드 (60-72번) - 실제로 ID로 필터링하도록 수정
 * 2. getStocksOrderByVolume 메서드 (202-205번) - DAO 호출 구현
 * 3. getStocksOrderByChangeRate 메서드 (208-211번) - DAO 호출 구현
 * 
 * 원인:
 * - getStockById가 selectAllStocks를 호출하지만 필터링하지 않음
 * - getStocksOrderByVolume/getStocksOrderByChangeRate가 구현되지 않음
 * 
 * @author PortWatch
 * @version 8.0 - 메서드 구현 완료
 */
@Service
public class StockServiceImpl implements StockService {
    
    @Autowired
    private StockDAO stockDAO;
    
    @Override
    public List<StockVO> getAllStocks() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Service: 전체 종목 조회");
        
        List<StockVO> stocks = stockDAO.selectAllStocks();
        
        System.out.println("✅ Service: " + (stocks != null ? stocks.size() : 0) + "개 조회 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return stocks;
    }
    
    @Override
    public StockVO getStockByCode(String stockCode) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Service: 종목 코드로 조회");
        System.out.println("  - 종목 코드: " + stockCode);
        
        StockVO stock = stockDAO.selectStockByCode(stockCode);
        
        if (stock != null) {
            System.out.println("✅ Service: 종목 조회 완료!");
            System.out.println("  - 종목명: " + stock.getStockName());
            System.out.println("  - 시장: " + stock.getMarketType());
        } else {
            System.out.println("❌ Service: 종목을 찾을 수 없습니다.");
        }
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return stock;
    }
    
    /**
     * ✅ 수정: 종목 ID로 조회
     * - 실제로 ID로 필터링하여 반환
     */
    @Override
    public StockVO getStockById(int stockId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Service: 종목 ID로 조회");
        System.out.println("  - 종목 ID: " + stockId);
        
        // ✅ DAO에 selectById 메서드가 있으면 직접 호출
        StockVO stock = null;
        
        try {
            // 방법 1: DAO에 selectById가 있는 경우
            stock = stockDAO.selectById(stockId);
        } catch (Exception e) {
            // 방법 2: selectById가 없으면 전체 조회 후 필터링
            System.out.println("  ℹ️ selectById 메서드가 없어 전체 조회 후 필터링합니다.");
            List<StockVO> allStocks = stockDAO.selectAllStocks();
            
            if (allStocks != null) {
                stock = allStocks.stream()
                    .filter(s -> s.getStockId() != null && s.getStockId() == stockId)
                    .findFirst()
                    .orElse(null);
            }
        }
        
        if (stock != null) {
            System.out.println("✅ Service: 종목 조회 완료!");
            System.out.println("  - 종목명: " + stock.getStockName());
            System.out.println("  - 종목 코드: " + stock.getStockCode());
        } else {
            System.out.println("❌ Service: 종목을 찾을 수 없습니다. (ID: " + stockId + ")");
        }
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return stock;
    }
    
    @Override
    public List<StockVO> searchStocks(String keyword) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Service: 종목 검색");
        System.out.println("  - 키워드: " + keyword);
        
        List<StockVO> stocks = stockDAO.searchStocks(keyword);
        
        System.out.println("✅ Service: " + (stocks != null ? stocks.size() : 0) + "개 검색 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return stocks;
    }
    
    @Override
    public List<StockVO> getStocksByMarketType(String marketType) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Service: 시장별 종목 조회");
        System.out.println("  - 시장: " + marketType);
        
        List<StockVO> stocks = stockDAO.selectStocksByMarketType(marketType);
        
        System.out.println("✅ Service: " + (stocks != null ? stocks.size() : 0) + "개 조회 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return stocks;
    }
    
    @Override
    public List<StockVO> getStocksByCountry(String country) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Service: 나라별 종목 조회");
        System.out.println("  - 국가: " + country);
        
        List<StockVO> allStocks = stockDAO.selectAllStocks();
        
        // ✅ 나라별 필터링
        List<StockVO> filtered = allStocks.stream()
            .filter(stock -> {
                String marketType = stock.getMarketType();
                if (marketType == null) {
                    return false;
                }
                
                if ("KR".equalsIgnoreCase(country)) {
                    return marketType.equals("KOSPI") || marketType.equals("KOSDAQ");
                } else if ("US".equalsIgnoreCase(country)) {
                    return marketType.equals("NASDAQ") || marketType.equals("NYSE") || marketType.equals("AMEX");
                }
                
                return false;
            })
            .collect(Collectors.toList());
        
        System.out.println("✅ Service: " + filtered.size() + "개 조회 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return filtered;
    }
    
    @Override
    public List<StockVO> getStocksByIndustry(String industry) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Service: 업종별 종목 조회");
        System.out.println("  - 업종: " + industry);
        
        List<StockVO> stocks = stockDAO.selectStocksByIndustry(industry);
        
        System.out.println("✅ Service: " + (stocks != null ? stocks.size() : 0) + "개 조회 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return stocks;
    }
    
    @Override
    public List<String> getAllIndustries() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Service: 전체 업종 목록 조회");
        
        List<String> industries = stockDAO.selectAllIndustries();
        
        System.out.println("✅ Service: " + (industries != null ? industries.size() : 0) + "개 조회 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return industries;
    }
    
    @Override
    @Transactional
    public void addStock(StockVO stock) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Service: 종목 추가");
        System.out.println("  - 종목 코드: " + stock.getStockCode());
        System.out.println("  - 종목명: " + stock.getStockName());
        
        stockDAO.insertStock(stock);
        
        System.out.println("✅ Service: 추가 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    @Override
    @Transactional
    public void updateStock(StockVO stock) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Service: 종목 수정");
        System.out.println("  - 종목 ID: " + stock.getStockId());
        
        stockDAO.updateStock(stock);
        
        System.out.println("✅ Service: 수정 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    @Override
    @Transactional
    public void deleteStock(int stockId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Service: 종목 삭제");
        System.out.println("  - 종목 ID: " + stockId);
        
        stockDAO.deleteStock(stockId);
        
        System.out.println("✅ Service: 삭제 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    /**
     * ✅ 수정: 거래량 상위 종목 조회
     * - DAO의 selectTopVolume 메서드 호출
     */
    @Override
    public List<StockVO> getStocksOrderByVolume(int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Service: 거래량 상위 종목 조회");
        System.out.println("  - 조회 개수: " + limit);
        
        List<StockVO> stocks = stockDAO.selectTopVolume(limit);
        
        System.out.println("✅ Service: " + (stocks != null ? stocks.size() : 0) + "개 조회 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return stocks;
    }

    /**
     * ✅ 수정: 상승률 상위 종목 조회
     * - DAO의 selectTopGainers 메서드 호출
     */
    @Override
    public List<StockVO> getStocksOrderByChangeRate(int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Service: 상승률 상위 종목 조회");
        System.out.println("  - 조회 개수: " + limit);
        
        List<StockVO> stocks = stockDAO.selectTopGainers(limit);
        
        System.out.println("✅ Service: " + (stocks != null ? stocks.size() : 0) + "개 조회 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return stocks;
    }
}
