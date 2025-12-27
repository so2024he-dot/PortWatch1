package com.portwatch.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.portwatch.domain.StockPriceVO;

import java.sql.Date;
import java.util.List;

/**
 * 주가 정보 DAO 인터페이스
 * MyBatis가 자동으로 구현체를 생성
 * 
 * 실시간 주가 업데이트용
 */
@Mapper  // ⭐ MyBatis Mapper 인터페이스임을 명시
public interface StockPriceDAO {
    
    /**
     * 주가 정보 삽입 (INSERT)
     */
    void insertStockPrice(StockPriceVO stockPrice) throws Exception;
    
    /**
     * 주가 정보 업데이트 (UPDATE - 같은 날짜면 덮어쓰기)
     */
    void updateStockPrice(StockPriceVO stockPrice) throws Exception;
    
    /**
     * 주가 정보 UPSERT (INSERT ON DUPLICATE KEY UPDATE)
     */
    void upsertStockPrice(StockPriceVO stockPrice) throws Exception;
    
    /**
     * 특정 종목의 최신 주가 조회
     */
    StockPriceVO selectLatestByStockId(Integer stockId) throws Exception;
    
    /**
     * 특정 종목의 특정 날짜 주가 조회
     */
    StockPriceVO selectByStockIdAndDate(@Param("stockId") Integer stockId, 
                                         @Param("tradeDate") Date tradeDate) throws Exception;
    
    /**
     * 특정 종목의 주가 히스토리 조회 (최근 N일)
     */
    List<StockPriceVO> selectPriceHistory(@Param("stockId") Integer stockId, 
                                          @Param("days") int days) throws Exception;
    
    /**
     * 오늘 날짜에 업데이트된 종목 수 조회
     */
    int countTodayUpdates() throws Exception;
    
    /**
     * 주가 정보 삭제 (특정 날짜 이전)
     */
    void deletePricesBeforeDate(Date beforeDate) throws Exception;

	void insert(StockPriceVO stockPrice);

	List<StockPriceVO> selectByStockIdAndDays(Integer stockId, int days);

	List<StockPriceVO> selectRecentByStockId(Integer stockId, int days);
}
