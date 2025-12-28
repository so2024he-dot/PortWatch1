package com.portwatch.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.StockVO;
import com.portwatch.persistence.StockDAO;

/**
 * ✅ 주식 Service 구현 클래스
 * 
 * @author PortWatch
 * @version FINAL COMPLETE - Spring 5.0.7 + MySQL 8.0.33
 */
@Service
public class StockServiceImpl implements StockService {
    
    @Autowired
    private StockDAO stockDAO;
    
    // ========================================
    // 기본 조회
    // ========================================
    
    /**
     * ✅ 주식 ID로 조회 (Integer 버전)
     * 
     * @param stockId 주식 ID (Integer)
     * @return StockVO 주식 정보
     */
    @Override
    public StockVO getStockById(Integer stockId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 주식 ID로 조회 (Integer)");
        System.out.println("  - 주식 ID: " + stockId);
        
        try {
            StockVO stock = stockDAO.selectById(stockId);
            
            if (stock != null) {
                System.out.println("  - 종목 코드: " + stock.getStockCode());
                System.out.println("  - 종목명: " + stock.getStockName());
                System.out.println("  - 현재가: " + stock.getCurrentPrice());
                System.out.println("✅ 주식 조회 완료");
            } else {
                System.out.println("⚠️ 주식을 찾을 수 없습니다");
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
     * ✅ 주식 ID로 조회 (Long 버전 - 오버로딩)
     * 
     * @param stockId 주식 ID (Long)
     * @return StockVO 주식 정보
     */
    @Override
    public StockVO getStockById(Long stockId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 주식 ID로 조회 (Long → Integer 변환)");
        System.out.println("  - 주식 ID (Long): " + stockId);
        
        try {
            // Long을 Integer로 변환
            Integer stockIdInt = stockId.intValue();
            
            System.out.println("  - 주식 ID (Integer): " + stockIdInt);
            
            // Integer 버전 메서드 호출
            StockVO stock = getStockById(stockIdInt);
            
            System.out.println("✅ 주식 조회 완료 (Long → Integer 변환)");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stock;
            
        } catch (Exception e) {
            System.err.println("❌ 주식 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("주식 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 종목 코드로 조회
     * 
     * @param stockCode 종목 코드
     * @return StockVO 주식 정보
     */
    @Override
    public StockVO getStockByCode(String stockCode) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 종목 코드로 조회");
        System.out.println("  - 종목 코드: " + stockCode);
        
        try {
            StockVO stock = stockDAO.selectByCode(stockCode);
            
            if (stock != null) {
                System.out.println("  - 종목명: " + stock.getStockName());
                System.out.println("  - 현재가: " + stock.getCurrentPrice());
                System.out.println("✅ 주식 조회 완료");
            } else {
                System.out.println("⚠️ 주식을 찾을 수 없습니다");
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
     * ✅ 전체 종목 목록 조회
     * 
     * @return List<StockVO> 전체 종목 목록
     */
    @Override
    public List<StockVO> getAllStocks() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 전체 종목 목록 조회");
        
        try {
            List<StockVO> stocks = stockDAO.selectAll();
            
            System.out.println("  - 조회 결과: " + stocks.size() + "개");
            System.out.println("✅ 전체 종목 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stocks;
            
        } catch (Exception e) {
            System.err.println("❌ 전체 종목 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("전체 종목 조회 실패: " + e.getMessage(), e);
        }
    }
    
    // ========================================
    // 필터링 (StockFilterController 전용)
    // ========================================
    
    /**
     * ✅ 국가별 종목 목록 조회
     * 
     * @param country 국가 코드 (KR, US)
     * @return List<StockVO> 종목 목록
     */
    @Override
    public List<StockVO> getStocksByCountry(String country) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🌏 국가별 종목 조회");
        System.out.println("  - 국가: " + country);
        
        try {
            List<StockVO> stocks = stockDAO.selectByCountry(country);
            
            System.out.println("  - 조회 결과: " + stocks.size() + "개");
            System.out.println("✅ 국가별 종목 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stocks;
            
        } catch (Exception e) {
            System.err.println("❌ 국가별 종목 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("국가별 종목 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 한국 주식 전체 조회
     * 
     * @return List<StockVO> 한국 주식 목록
     */
    @Override
    public List<StockVO> getKoreanStocks() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🇰🇷 한국 주식 조회");
        
        try {
            List<StockVO> stocks = getStocksByCountry("KR");
            
            System.out.println("  - 조회 결과: " + stocks.size() + "개");
            System.out.println("✅ 한국 주식 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stocks;
            
        } catch (Exception e) {
            System.err.println("❌ 한국 주식 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("한국 주식 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 미국 주식 전체 조회
     * 
     * @return List<StockVO> 미국 주식 목록
     */
    @Override
    public List<StockVO> getUSStocks() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🇺🇸 미국 주식 조회");
        
        try {
            List<StockVO> stocks = getStocksByCountry("US");
            
            System.out.println("  - 조회 결과: " + stocks.size() + "개");
            System.out.println("✅ 미국 주식 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stocks;
            
        } catch (Exception e) {
            System.err.println("❌ 미국 주식 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("미국 주식 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 시장별 종목 목록 조회
     * 
     * @param marketType 시장 타입 (KOSPI, KOSDAQ, NASDAQ, NYSE)
     * @return List<StockVO> 종목 목록
     */
    @Override
    public List<StockVO> getStocksByMarketType(String marketType) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📈 시장별 종목 조회");
        System.out.println("  - 시장: " + marketType);
        
        try {
            List<StockVO> stocks = stockDAO.selectByMarket(marketType);
            
            System.out.println("  - 조회 결과: " + stocks.size() + "개");
            System.out.println("✅ 시장별 종목 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stocks;
            
        } catch (Exception e) {
            System.err.println("❌ 시장별 종목 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("시장별 종목 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 시장별 종목 목록 조회 (별칭)
     * 
     * @param marketType 시장 타입
     * @return List<StockVO> 종목 목록
     */
    @Override
    public List<StockVO> getStocksByMarket(String marketType) throws Exception {
        return getStocksByMarketType(marketType);
    }
    
    /**
     * ✅ 업종별 종목 목록 조회
     * 
     * @param industry 업종 (반도체, 자동차, 소프트웨어 등)
     * @return List<StockVO> 종목 목록
     */
    @Override
    public List<StockVO> getStocksByIndustry(String industry) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🏭 업종별 종목 조회");
        System.out.println("  - 업종: " + industry);
        
        try {
            List<StockVO> stocks = stockDAO.selectByIndustry(industry);
            
            System.out.println("  - 조회 결과: " + stocks.size() + "개");
            System.out.println("✅ 업종별 종목 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stocks;
            
        } catch (Exception e) {
            System.err.println("❌ 업종별 종목 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("업종별 종목 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 전체 업종 목록 조회
     * 
     * @return List<String> 업종 목록 (중복 제거)
     */
    @Override
    public List<String> getAllIndustries() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🏢 전체 업종 목록 조회");
        
        try {
            List<String> industries = stockDAO.selectAllIndustries();
            
            System.out.println("  - 조회 결과: " + industries.size() + "개");
            System.out.println("✅ 업종 목록 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return industries;
            
        } catch (Exception e) {
            System.err.println("❌ 업종 목록 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("업종 목록 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 거래량 상위 종목 조회
     * 
     * @param limit 조회 개수
     * @return List<StockVO> 거래량 순 정렬 종목 목록
     */
    @Override
    public List<StockVO> getStocksOrderByVolume(int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 거래량 상위 종목 조회");
        System.out.println("  - 조회 개수: " + limit);
        
        try {
            List<StockVO> stocks = stockDAO.selectOrderByVolume(limit);
            
            System.out.println("  - 조회 결과: " + stocks.size() + "개");
            System.out.println("✅ 거래량 상위 종목 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stocks;
            
        } catch (Exception e) {
            System.err.println("❌ 거래량 상위 종목 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("거래량 상위 종목 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 등락률 상위 종목 조회
     * 
     * @param limit 조회 개수
     * @return List<StockVO> 등락률 순 정렬 종목 목록
     */
    @Override
    public List<StockVO> getStocksOrderByChangeRate(int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📈 등락률 상위 종목 조회");
        System.out.println("  - 조회 개수: " + limit);
        
        try {
            List<StockVO> stocks = stockDAO.selectOrderByChangeRate(limit);
            
            System.out.println("  - 조회 결과: " + stocks.size() + "개");
            System.out.println("✅ 등락률 상위 종목 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stocks;
            
        } catch (Exception e) {
            System.err.println("❌ 등락률 상위 종목 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("등락률 상위 종목 조회 실패: " + e.getMessage(), e);
        }
    }
    
    // ========================================
    // 검색
    // ========================================
    
    /**
     * ✅ 종목명으로 검색
     * 
     * @param keyword 검색 키워드
     * @return List<StockVO> 종목 목록
     */
    @Override
    public List<StockVO> searchStocks(String keyword) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 종목 검색");
        System.out.println("  - 키워드: " + keyword);
        
        try {
            List<StockVO> stocks = stockDAO.search(keyword);
            
            System.out.println("  - 검색 결과: " + stocks.size() + "개");
            System.out.println("✅ 종목 검색 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stocks;
            
        } catch (Exception e) {
            System.err.println("❌ 종목 검색 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("종목 검색 실패: " + e.getMessage(), e);
        }
    }
    
    // ========================================
    // 업데이트
    // ========================================
    
    /**
     * ✅ 현재가 업데이트
     * 
     * @param stockCode 종목 코드
     * @param currentPrice 현재가
     */
    @Override
    @Transactional
    public void updateCurrentPrice(String stockCode, BigDecimal currentPrice) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💰 현재가 업데이트");
        System.out.println("  - 종목 코드: " + stockCode);
        System.out.println("  - 현재가: " + currentPrice);
        
        try {
            stockDAO.updateStockPrice(stockCode, currentPrice);
            
            System.out.println("✅ 현재가 업데이트 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 현재가 업데이트 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("현재가 업데이트 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 종목 정보 전체 업데이트
     * 
     * @param stock 종목 정보
     */
    @Override
    @Transactional
    public void updateStock(StockVO stock) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📝 종목 정보 업데이트");
        System.out.println("  - 종목 코드: " + stock.getStockCode());
        System.out.println("  - 종목명: " + stock.getStockName());
        
        try {
            stockDAO.update(stock);
            
            System.out.println("✅ 종목 정보 업데이트 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 종목 정보 업데이트 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("종목 정보 업데이트 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 종목 추가
     * 
     * @param stock 종목 정보
     */
    @Override
    @Transactional
    public void insertStock(StockVO stock) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("➕ 종목 추가");
        System.out.println("  - 종목 코드: " + stock.getStockCode());
        System.out.println("  - 종목명: " + stock.getStockName());
        
        try {
            stockDAO.insert(stock);
            
            System.out.println("✅ 종목 추가 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 종목 추가 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("종목 추가 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 종목 삭제
     * 
     * @param stockId 종목 ID
     */
    @Override
    @Transactional
    public void deleteStock(Integer stockId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 종목 삭제");
        System.out.println("  - 종목 ID: " + stockId);
        
        try {
            stockDAO.delete(stockId);
            
            System.out.println("✅ 종목 삭제 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 종목 삭제 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("종목 삭제 실패: " + e.getMessage(), e);
        }
    }

	/**
	 * ✅ 국가+시장별 종목 조회 (완전 구현)
	 * 
	 * @param country 국가 코드 (KR, US)
	 * @param market 시장 타입 (KOSPI, KOSDAQ, NASDAQ, NYSE)
	 * @return List<StockVO> 종목 목록
	 * @throws Exception
	 */
	@Override
	public List<StockVO> getStocksByCountryAndMarket(String country, String market) throws Exception {
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.println("🌏🏛️ 국가+시장별 종목 조회");
		System.out.println("  - 국가: " + country);
		System.out.println("  - 시장: " + market);
		
		try {
			List<StockVO> stocks = stockDAO.selectByCountryAndMarket(country, market);
			
			System.out.println("  - 조회 결과: " + stocks.size() + "개");
			System.out.println("✅ 국가+시장별 종목 조회 완료");
			System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			
			return stocks;
			
		} catch (Exception e) {
			System.err.println("❌ 국가+시장별 종목 조회 실패: " + e.getMessage());
			System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			throw new Exception("국가+시장별 종목 조회 실패: " + e.getMessage(), e);
		}
	}
}
