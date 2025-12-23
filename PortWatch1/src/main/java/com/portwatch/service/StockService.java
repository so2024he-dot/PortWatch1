package com.portwatch.service;

import com.portwatch.domain.StockVO;
import java.util.List;

/**
 * 주식 종목 Service 인터페이스
 * 
 * ✅ StockFilterController 완벽 호환
 * @author PortWatch
 * @version 3.0 - Spring 5.0.7 + MySQL 8.0.33 호환
 */
public interface StockService {
    
    // ========================================
    // 기본 조회
    // ========================================
    
    /**
     * 종목 ID로 조회
     */
    StockVO getStockById(Integer stockId) throws Exception;
    
    /**
     * 종목 코드로 조회
     */
    StockVO getStockByCode(String stockCode) throws Exception;
    
    /**
     * 전체 종목 목록 조회
     */
    List<StockVO> getAllStocks() throws Exception;
    
    // ========================================
    // 필터링 (StockFilterController 전용)
    // ========================================
    
    /**
     * ✅ 국가별 종목 목록 조회
     * 
     * @param country 국가 코드 (KR, US)
     * @return 종목 목록
     */
    List<StockVO> getStocksByCountry(String country) throws Exception;
    
    /**
     * ✅ 시장별 종목 목록 조회 (StockFilterController Line 106)
     * 
     * @param marketType 시장 타입 (KOSPI, KOSDAQ, NASDAQ, NYSE)
     * @return 종목 목록
     */
    List<StockVO> getStocksByMarketType(String marketType) throws Exception;
    
    /**
     * ✅ 시장별 종목 목록 조회 (별칭)
     */
    List<StockVO> getStocksByMarket(String marketType) throws Exception;
    
    /**
     * ✅ 업종별 종목 목록 조회 (StockFilterController Line 135)
     * 
     * @param industry 업종 (반도체, 자동차, 소프트웨어 등)
     * @return 종목 목록
     */
    List<StockVO> getStocksByIndustry(String industry) throws Exception;
    
    /**
     * ✅ 전체 업종 목록 조회 (StockFilterController Line 189)
     * 
     * @return 업종 목록 (중복 제거)
     */
    List<String> getAllIndustries() throws Exception;
    
    /**
     * ✅ 거래량 상위 종목 조회 (StockFilterController Line 215)
     * 
     * @param limit 조회 개수
     * @return 거래량 순 정렬 종목 목록
     */
    List<StockVO> getStocksOrderByVolume(int limit) throws Exception;
    
    /**
     * ✅ 등락률 상위 종목 조회 (StockFilterController Line 241)
     * 
     * @param limit 조회 개수
     * @return 등락률 순 정렬 종목 목록
     */
    List<StockVO> getStocksOrderByChangeRate(int limit) throws Exception;
    
    // ========================================
    // 검색
    // ========================================
    
    /**
     * 종목명으로 검색
     * 
     * @param keyword 검색 키워드
     * @return 종목 목록
     */
    List<StockVO> searchStocks(String keyword) throws Exception;
    
    // ========================================
    // 업데이트
    // ========================================
    
    /**
     * 현재가 업데이트
     * 
     * @param stockCode 종목 코드
     * @param currentPrice 현재가
     */
    void updateCurrentPrice(String stockCode, java.math.BigDecimal currentPrice) throws Exception;
    
    /**
     * 종목 정보 전체 업데이트
     * 
     * @param stock 종목 정보
     */
    void updateStock(StockVO stock) throws Exception;
    
    /**
     * 종목 추가
     * 
     * @param stock 종목 정보
     */
    void insertStock(StockVO stock) throws Exception;
    
    /**
     * 종목 삭제
     * 
     * @param stockId 종목 ID
     */
    void deleteStock(Integer stockId) throws Exception;
}
