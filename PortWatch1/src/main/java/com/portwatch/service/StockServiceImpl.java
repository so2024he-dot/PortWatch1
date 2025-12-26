package com.portwatch.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.StockVO;
import com.portwatch.persistence.StockDAO;

/**
 * ✅ 주식 Service 구현 V3 (완전 구현)
 * 
 * @author PortWatch
 * @version 3.0 FINAL
 */
@Service
public class StockServiceImpl implements StockService {
    
    @Autowired
    private StockDAO stockDAO;
    
    /**
     * ✅ 주식 ID로 조회
     */
    @Override
    public StockVO getStockById(Integer stockId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 주식 조회 (ID)");
        System.out.println("  - 주식 ID: " + stockId);
        
        try {
            StockVO stock = stockDAO.selectById(stockId);
            
            if (stock != null) {
                System.out.println("✅ 주식 조회 성공");
                System.out.println("  - 종목코드: " + stock.getStockCode());
                System.out.println("  - 종목명: " + stock.getStockName());
            } else {
                System.out.println("❌ 주식 없음");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return stock;
            
        } catch (Exception e) {
            System.err.println("❌ 주식 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("주식 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 종목코드로 조회
     */
    @Override
    public StockVO getStockByCode(String stockCode) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 주식 조회 (종목코드)");
        System.out.println("  - 종목코드: " + stockCode);
        
        try {
            StockVO stock = stockDAO.selectByCode(stockCode);
            
            if (stock != null) {
                System.out.println("✅ 주식 조회 성공");
                System.out.println("  - 종목명: " + stock.getStockName());
                System.out.println("  - 현재가: " + stock.getCurrentPrice());
            } else {
                System.out.println("❌ 주식 없음");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return stock;
            
        } catch (Exception e) {
            System.err.println("❌ 주식 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("주식 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 전체 주식 조회
     */
    @Override
    public List<StockVO> getAllStocks() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 전체 주식 조회");
        
        try {
            List<StockVO> stockList = stockDAO.selectAll();
            
            System.out.println("  - 전체 종목: " + stockList.size() + "개");
            System.out.println("✅ 전체 주식 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stockList;
            
        } catch (Exception e) {
            System.err.println("❌ 전체 주식 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("전체 주식 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 국가별 주식 조회
     */
    @Override
    public List<StockVO> getStocksByCountry(String country) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🌏 국가별 주식 조회");
        System.out.println("  - 국가: " + country);
        
        try {
            List<StockVO> stockList = stockDAO.selectByCountry(country);
            
            System.out.println("  - " + country + " 종목: " + stockList.size() + "개");
            System.out.println("✅ 국가별 주식 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stockList;
            
        } catch (Exception e) {
            System.err.println("❌ 국가별 주식 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("국가별 주식 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 한국 주식 조회
     */
    @Override
    public List<StockVO> getKoreanStocks() throws Exception {
        return getStocksByCountry("KR");
    }
    
    /**
     * ✅ 미국 주식 조회
     */
    @Override
    public List<StockVO> getUSStocks() throws Exception {
        return getStocksByCountry("US");
    }
    
    /**
     * ✅ 시장별 주식 조회 (getStocksByMarketType 별칭)
     */
    @Override
    public List<StockVO> getStocksByMarketType(String marketType) throws Exception {
        return getStocksByMarket(marketType);
    }
    
    /**
     * ✅ 시장별 주식 조회
     */
    @Override
    public List<StockVO> getStocksByMarket(String marketType) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 시장별 주식 조회");
        System.out.println("  - 시장: " + marketType);
        
        try {
            List<StockVO> stockList = stockDAO.selectByMarket(marketType);
            
            System.out.println("  - " + marketType + " 종목: " + stockList.size() + "개");
            System.out.println("✅ 시장별 주식 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stockList;
            
        } catch (Exception e) {
            System.err.println("❌ 시장별 주식 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("시장별 주식 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 업종별 주식 조회
     */
    @Override
    public List<StockVO> getStocksByIndustry(String industry) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🏭 업종별 주식 조회");
        System.out.println("  - 업종: " + industry);
        
        try {
            List<StockVO> stockList = stockDAO.selectByIndustry(industry);
            
            System.out.println("  - " + industry + " 종목: " + stockList.size() + "개");
            System.out.println("✅ 업종별 주식 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stockList;
            
        } catch (Exception e) {
            System.err.println("❌ 업종별 주식 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("업종별 주식 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 전체 업종 목록 조회
     */
    @Override
    public List<String> getAllIndustries() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🏭 전체 업종 목록 조회");
        
        try {
            // 전체 주식 조회 후 업종 추출
            List<StockVO> allStocks = stockDAO.selectAll();
            
            List<String> industries = allStocks.stream()
                .map(StockVO::getIndustry)
                .filter(industry -> industry != null && !industry.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
            
            System.out.println("  - 전체 업종: " + industries.size() + "개");
            System.out.println("✅ 전체 업종 목록 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return industries;
            
        } catch (Exception e) {
            System.err.println("❌ 전체 업종 목록 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("전체 업종 목록 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 거래량 순 조회
     */
    @Override
    public List<StockVO> getStocksOrderByVolume(int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 거래량 순 조회");
        System.out.println("  - 조회 개수: " + limit);
        
        try {
            List<StockVO> stockList = stockDAO.selectOrderByVolume(limit);
            
            System.out.println("  - 조회 결과: " + stockList.size() + "개");
            System.out.println("✅ 거래량 순 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stockList;
            
        } catch (Exception e) {
            System.err.println("❌ 거래량 순 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("거래량 순 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 등락률 순 조회
     */
    @Override
    public List<StockVO> getStocksOrderByChangeRate(int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📈 등락률 순 조회");
        System.out.println("  - 조회 개수: " + limit);
        
        try {
            List<StockVO> stockList = stockDAO.selectOrderByChangeRate(limit);
            
            System.out.println("  - 조회 결과: " + stockList.size() + "개");
            System.out.println("✅ 등락률 순 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stockList;
            
        } catch (Exception e) {
            System.err.println("❌ 등락률 순 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("등락률 순 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 주식 검색 (키워드)
     */
    @Override
    public List<StockVO> searchStocks(String keyword) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 주식 검색");
        System.out.println("  - 키워드: " + keyword);
        
        try {
            List<StockVO> stockList = stockDAO.search(keyword);
            
            System.out.println("  - 검색 결과: " + stockList.size() + "개");
            System.out.println("✅ 주식 검색 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stockList;
            
        } catch (Exception e) {
            System.err.println("❌ 주식 검색 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("주식 검색 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 현재가 업데이트
     */
    @Override
    @Transactional
    public void updateCurrentPrice(String stockCode, BigDecimal currentPrice) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💰 현재가 업데이트");
        System.out.println("  - 종목코드: " + stockCode);
        System.out.println("  - 현재가: " + currentPrice);
        
        try {
            StockVO stock = stockDAO.selectByCode(stockCode);
            
            if (stock == null) {
                throw new Exception("존재하지 않는 종목코드: " + stockCode);
            }
            
            stock.setCurrentPrice(currentPrice);
            stockDAO.update(stock);
            
            System.out.println("✅ 현재가 업데이트 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 현재가 업데이트 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("현재가 업데이트 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 주식 정보 업데이트
     */
    @Override
    @Transactional
    public void updateStock(StockVO stock) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✏️ 주식 정보 업데이트");
        System.out.println("  - 종목코드: " + stock.getStockCode());
        
        try {
            stockDAO.update(stock);
            
            System.out.println("✅ 주식 정보 업데이트 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 주식 정보 업데이트 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("주식 정보 업데이트 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 주식 추가
     */
    @Override
    @Transactional
    public void insertStock(StockVO stock) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("➕ 주식 추가");
        System.out.println("  - 종목코드: " + stock.getStockCode());
        System.out.println("  - 종목명: " + stock.getStockName());
        
        try {
            stockDAO.insert(stock);
            
            System.out.println("✅ 주식 추가 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 주식 추가 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("주식 추가 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 주식 삭제
     */
    @Override
    @Transactional
    public void deleteStock(Integer stockId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 주식 삭제");
        System.out.println("  - 주식 ID: " + stockId);
        
        try {
            stockDAO.delete(stockId);
            
            System.out.println("✅ 주식 삭제 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 주식 삭제 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("주식 삭제 실패: " + e.getMessage(), e);
        }
    }

	@Override
	public StockVO getStockById(Long stockId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
