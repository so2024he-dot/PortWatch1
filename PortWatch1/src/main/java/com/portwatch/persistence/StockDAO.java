    package com.portwatch.persistence;

import com.portwatch.domain.StockPriceVO;
import com.portwatch.domain.StockVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 종목 DAO 인터페이스
 * API Controller 완전 지원
 */
public interface StockDAO {
    
	
	/**
	 * MySQL 자동 주식 종목 등록
	 */
	 List<StockPriceVO> getRecentPrices(int stockId);
	 
    /**
     * 모든 종목 조회 (Map 형태)
     */
    List<Map<String, Object>> selectAllStocks() throws Exception;
    
    /**
     * 모든 종목 조회 (VO 형태)
     */
    List<StockVO> selectAll() throws Exception;
    
    /**
     * 종목 코드로 조회
     */
    StockVO selectByCode(String stockCode) throws Exception;
    
    /**
     * 종목 ID로 조회
     */
    StockVO selectById(Integer stockId) throws Exception;
    
    /**
     * 종목 검색 (이름 또는 코드)
     */
    List<StockVO> searchStocks(String keyword) throws Exception;
    
    /**
     * 종목 검색 (키워드 + 시장 타입)
     */
    List<StockVO> searchStocksWithMarket(@Param("keyword") String keyword, 
                                         @Param("marketType") String marketType) throws Exception;
    
    /**
     * 자동완성 (제한된 개수)
     */
    List<StockVO> autocomplete(@Param("keyword") String keyword, 
                               @Param("limit") int limit) throws Exception;
    
    /**
     * 시장별 종목 목록
     */
    List<StockVO> selectByMarket(String marketType) throws Exception;
    
    /**
     * 거래량 상위 종목
     */
    List<StockVO> selectTopVolume(int limit) throws Exception;
    
    /**
     * 상승률 상위 종목
     */
    List<StockVO> selectTopGainers(int limit) throws Exception;
}

    
