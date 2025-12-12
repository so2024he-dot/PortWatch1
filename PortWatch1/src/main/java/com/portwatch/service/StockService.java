package com.portwatch.service;

import com.portwatch.domain.StockVO;
import java.util.List;
import java.util.Map;

/**
 * 종목 서비스 인터페이스
 */
public interface StockService {
    
    /**
     * 모든 종목 목록 조회 (Map 형태)
     */
    List<Map<String, Object>> getAllStocks() throws Exception;
    
    /**
     * 모든 종목 목록 조회 (VO 형태) - 추가
     */
    List<StockVO> getAllStocksList() throws Exception;
    
    /**
     * 종목 코드로 조회
     */
    StockVO getStockByCode(String stockCode) throws Exception;
    
    /**
     * 종목 ID로 조회
     */
    StockVO getStockById(Integer stockId) throws Exception;
    
    /**
     * 종목 검색 (키워드 + 시장 타입)
     */
    List<StockVO> searchStocks(String keyword, String marketType) throws Exception;
    
    /**
     * 자동완성 (키워드만)
     */
    List<StockVO> getAutocomplete(String keyword) throws Exception;
    
    /**
     * 시장별 종목 목록 (KOSPI/KOSDAQ)
     */
    List<StockVO> getStocksByMarket(String market) throws Exception;
    
    /**
     * 거래량 상위 종목
     */
    List<StockVO> getTopVolume(int limit) throws Exception;
    
    /**
     * 상승률 상위 종목
     */
    List<StockVO> getTopGainers(int limit) throws Exception;
}

    
