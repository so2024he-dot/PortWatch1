package com.portwatch.service;

import com.portwatch.domain.StockVO;
import com.portwatch.persistence.StockDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * ✅ 주식 종목 Service 완전 구현
 * 
 * StockFilterController 완벽 호환
 * 
 * @author PortWatch
 * @version 3.0 - Spring 5.0.7 + MySQL 8.0.33 호환
 */
@Service
public class StockServiceImpl implements StockService {
    
    @Autowired
    private StockDAO stockDAO;
    
    // ========================================
    // 기본 조회
    // ========================================
    
    @Override
    public StockVO getStockById(Integer stockId) throws Exception {
        if (stockId == null || stockId <= 0) {
            throw new IllegalArgumentException("종목 ID가 유효하지 않습니다.");
        }
        
        StockVO stock = stockDAO.selectById(stockId);
        
        if (stock == null) {
            System.out.println("⚠️ 종목을 찾을 수 없습니다: ID=" + stockId);
        }
        
        return stock;
    }
    
    @Override
    public StockVO getStockByCode(String stockCode) throws Exception {
        if (stockCode == null || stockCode.trim().isEmpty()) {
            throw new IllegalArgumentException("종목 코드가 유효하지 않습니다.");
        }
        
        StockVO stock = stockDAO.selectStockByCode(stockCode);
        
        if (stock == null) {
            System.out.println("⚠️ 종목을 찾을 수 없습니다: CODE=" + stockCode);
        }
        
        return stock;
    }
    
    @Override
    public List<StockVO> getAllStocks() throws Exception {
        System.out.println("📊 전체 종목 조회");
        
        List<StockVO> stocks = stockDAO.selectAllStocks();
        
        System.out.println("✅ " + stocks.size() + "개 종목 조회 완료");
        
        return stocks;
    }
    
    // ========================================
    // 필터링 (StockFilterController 전용)
    // ========================================
    
    /**
     * ✅ 국가별 종목 조회
     */
    @Override
    public List<StockVO> getStocksByCountry(String country) throws Exception {
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("국가 코드가 유효하지 않습니다.");
        }
        
        System.out.println("📊 국가별 종목 조회: " + country);
        
        List<StockVO> stocks = stockDAO.selectStocksByCountry(country);
        
        System.out.println("✅ " + stocks.size() + "개 종목 조회 완료");
        
        return stocks;
    }
    
    /**
     * ✅ 시장별 종목 조회 (StockFilterController Line 106)
     */
    @Override
    public List<StockVO> getStocksByMarketType(String marketType) throws Exception {
        if (marketType == null || marketType.trim().isEmpty()) {
            throw new IllegalArgumentException("시장 타입이 유효하지 않습니다.");
        }
        
        System.out.println("📊 시장별 종목 조회: " + marketType);
        
        List<StockVO> stocks = stockDAO.selectStocksByMarket(marketType);
        
        System.out.println("✅ " + stocks.size() + "개 종목 조회 완료");
        
        return stocks;
    }
    
    /**
     * ✅ 시장별 종목 조회 (별칭)
     */
    @Override
    public List<StockVO> getStocksByMarket(String marketType) throws Exception {
        return getStocksByMarketType(marketType);
    }
    
    /**
     * ✅ 업종별 종목 조회 (StockFilterController Line 135)
     */
    @Override
    public List<StockVO> getStocksByIndustry(String industry) throws Exception {
        if (industry == null || industry.trim().isEmpty()) {
            throw new IllegalArgumentException("업종이 유효하지 않습니다.");
        }
        
        System.out.println("📊 업종별 종목 조회: " + industry);
        
        List<StockVO> stocks = stockDAO.selectStocksByIndustry(industry);
        
        System.out.println("✅ " + stocks.size() + "개 종목 조회 완료");
        
        return stocks;
    }
    
    /**
     * ✅ 전체 업종 목록 조회 (StockFilterController Line 189)
     */
    @Override
    public List<String> getAllIndustries() throws Exception {
        System.out.println("📊 전체 업종 목록 조회");
        
        List<String> industries = stockDAO.selectAllIndustries();
        
        System.out.println("✅ " + industries.size() + "개 업종 조회 완료");
        
        return industries;
    }
    
    /**
     * ✅ 거래량 상위 종목 조회 (StockFilterController Line 215)
     */
    @Override
    public List<StockVO> getStocksOrderByVolume(int limit) throws Exception {
        if (limit <= 0) {
            throw new IllegalArgumentException("조회 개수는 0보다 커야 합니다.");
        }
        
        System.out.println("📊 거래량 상위 " + limit + "개 종목 조회");
        
        List<StockVO> stocks = stockDAO.selectStocksOrderByVolume(limit);
        
        System.out.println("✅ " + stocks.size() + "개 종목 조회 완료");
        
        return stocks;
    }
    
    /**
     * ✅ 등락률 상위 종목 조회 (StockFilterController Line 241)
     */
    @Override
    public List<StockVO> getStocksOrderByChangeRate(int limit) throws Exception {
        if (limit <= 0) {
            throw new IllegalArgumentException("조회 개수는 0보다 커야 합니다.");
        }
        
        System.out.println("📊 등락률 상위 " + limit + "개 종목 조회");
        
        List<StockVO> stocks = stockDAO.selectStocksOrderByChangeRate(limit);
        
        System.out.println("✅ " + stocks.size() + "개 종목 조회 완료");
        
        return stocks;
    }
    
    // ========================================
    // 검색
    // ========================================
    
    @Override
    public List<StockVO> searchStocks(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색 키워드가 유효하지 않습니다.");
        }
        
        System.out.println("🔍 종목 검색: " + keyword);
        
        List<StockVO> stocks = stockDAO.searchStocks(keyword);
        
        System.out.println("✅ " + stocks.size() + "개 종목 검색 완료");
        
        return stocks;
    }
    
    // ========================================
    // 업데이트
    // ========================================
    
    @Override
    @Transactional
    public void updateCurrentPrice(String stockCode, BigDecimal currentPrice) throws Exception {
        if (stockCode == null || stockCode.trim().isEmpty()) {
            throw new IllegalArgumentException("종목 코드가 유효하지 않습니다.");
        }
        
        if (currentPrice == null || currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("현재가가 유효하지 않습니다.");
        }
        
        StockVO stock = stockDAO.selectStockByCode(stockCode);
        if (stock == null) {
            throw new Exception("종목을 찾을 수 없습니다: " + stockCode);
        }
        
        stockDAO.updateCurrentPrice(stock.getStockId(), currentPrice);
        
        System.out.println("✅ 현재가 업데이트: " + stockCode + " → " + currentPrice);
    }
    
    @Override
    @Transactional
    public void updateStock(StockVO stock) throws Exception {
        if (stock == null) {
            throw new IllegalArgumentException("종목 정보가 null입니다.");
        }
        
        if (stock.getStockId() == null || stock.getStockId() <= 0) {
            throw new IllegalArgumentException("종목 ID가 유효하지 않습니다.");
        }
        
        stockDAO.updateStock(stock);
        
        System.out.println("✅ 종목 정보 업데이트: " + stock.getStockCode());
    }
    
    @Override
    @Transactional
    public void insertStock(StockVO stock) throws Exception {
        if (stock == null) {
            throw new IllegalArgumentException("종목 정보가 null입니다.");
        }
        
        if (stock.getStockCode() == null || stock.getStockCode().trim().isEmpty()) {
            throw new IllegalArgumentException("종목 코드가 유효하지 않습니다.");
        }
        
        stockDAO.insertStock(stock);
        
        System.out.println("✅ 종목 추가: " + stock.getStockCode());
    }
    
    @Override
    @Transactional
    public void deleteStock(Integer stockId) throws Exception {
        if (stockId == null || stockId <= 0) {
            throw new IllegalArgumentException("종목 ID가 유효하지 않습니다.");
        }
        
        stockDAO.deleteStock(stockId);
        
        System.out.println("✅ 종목 삭제: ID=" + stockId);
    }
}
