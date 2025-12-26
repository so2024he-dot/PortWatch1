package com.portwatch.persistence;

import com.portwatch.domain.StockVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * ✅ 주식 종목 DAO 인터페이스 (완전 수정)
 * 
 * 수정 사항:
 * - @Mapper 어노테이션 추가 (필수!)
 * - Spring 5.0.7 + MySQL 8.0 완벽 호환
 * 
 * @author PortWatch
 * @version 4.0
 */
@Mapper  // ✅ MyBatis Mapper 어노테이션 (필수!)
public interface StockDAO {
    
    // ========================================
    // 기본 CRUD
    // ========================================
    
    /**
     * 종목 추가
     */
    void insertStock(StockVO stock);
    
    /**
     * 종목 정보 수정
     */
    void updateStock(StockVO stock);
    
    /**
     * 가격 정보만 업데이트
     */
    void updateStockPrice(
        @Param("stockCode") String stockCode,
        @Param("currentPrice") BigDecimal currentPrice,
        @Param("priceChange") BigDecimal priceChange,
        @Param("priceChangeRate") BigDecimal priceChangeRate,
        @Param("volume") Long volume
    );
    
    /**
     * 종목 코드로 조회 (StockVO 전체 반환)
     */
    StockVO selectStockByCode(@Param("stockCode") String stockCode);
    
    /**
     * 종목 코드로 조회 (별칭)
     */
    StockVO selectByCode(@Param("stockCode") String stockCode);
    
    /**
     * 종목 ID로 조회
     */
    StockVO selectStockById(@Param("stockId") Integer stockId);
    
    /**
     * 전체 종목 조회
     */
    List<StockVO> selectAllStocks();
    
    // ========================================
    // 필터링 쿼리
    // ========================================
    
    /**
     * 나라별 조회
     */
    List<StockVO> selectStocksByCountry(@Param("country") String country);
    
    /**
     * 시장별 조회
     */
    List<StockVO> selectStocksByMarketType(@Param("marketType") String marketType);
    
    /**
     * 업종별 조회
     */
    List<StockVO> selectStocksByIndustry(@Param("industry") String industry);
    
    /**
     * 검색 (종목명 또는 종목코드)
     */
    List<StockVO> searchStocks(@Param("keyword") String keyword);
    
    // ========================================
    // 정렬 쿼리
    // ========================================
    
    /**
     * 거래량 상위 종목
     */
    List<StockVO> selectStocksOrderByVolume(@Param("limit") int limit);
    
    /**
     * 등락률 상위 종목
     */
    List<StockVO> selectStocksOrderByChangeRate(@Param("limit") int limit);
    
    // ========================================
    // 업종 목록
    // ========================================
    
    /**
     * 전체 업종 목록 조회
     */
    List<String> selectAllIndustries();
    
    // ========================================
    // 통계 쿼리
    // ========================================
    
    /**
     * 전체 종목 수
     */
    int countAllStocks();
    
    /**
     * 나라별 종목 수
     */
    int countStocksByCountry(@Param("country") String country);

	StockVO selectById(Integer stockId);

	List<StockVO> getStocksByMarketType(String string);

	StockVO getStockByCode(String stockCode);

	void updateCurrentPrice(String stockCode, BigDecimal currentPrice, BigDecimal priceChange,
			BigDecimal priceChangeRate);

	List<StockVO> selectStocksByMarket(String marketType);

	void updateCurrentPrice(Integer stockId, BigDecimal currentPrice);

	void deleteStock(Integer stockId);
}
