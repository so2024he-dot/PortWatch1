package com.portwatch.service;

import com.portwatch.domain.StockVO;
import java.util.List;

/**
 * 종목 서비스 인터페이스
 * 
 * @author PortWatch
 * @version 7.0 - 나라별/시장별 구분 기능
 */
public interface StockService {
    
    /**
     * 전체 종목 조회
     */
    List<StockVO> getAllStocks() throws Exception;
    
    /**
     * 종목 코드로 조회
     */
    StockVO getStockByCode(String stockCode) throws Exception;
    
    /**
     * 종목 ID로 조회
     */
    StockVO getStockById(int stockId) throws Exception;
    
    /**
     * 종목 검색
     */
    List<StockVO> searchStocks(String keyword) throws Exception;
    
    /**
     * 시장별 종목 조회 (KOSPI, KOSDAQ, NASDAQ, NYSE, AMEX)
     */
    List<StockVO> getStocksByMarketType(String marketType) throws Exception;
    
    /**
     * 나라별 종목 조회 (KR, US)
     */
    List<StockVO> getStocksByCountry(String country) throws Exception;
    
    /**
     * 업종별 종목 조회
     */
    List<StockVO> getStocksByIndustry(String industry) throws Exception;
    
    /**
     * 전체 업종 목록
     */
    List<String> getAllIndustries() throws Exception;
    
    /**
     * 종목 추가
     */
    void addStock(StockVO stock) throws Exception;
    
    /**
     * 종목 수정
     */
    void updateStock(StockVO stock) throws Exception;
    
    /**
     * 종목 삭제
     */
    void deleteStock(int stockId) throws Exception;
    /**
     * 수정코드 202512191230
     */
    List<StockVO> getStocksOrderByVolume(int limit) throws Exception;
    List<StockVO> getStocksOrderByChangeRate(int limit) throws Exception;

}
