package com.portwatch.persistence;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.portwatch.domain.StockVO;

/**
 * 종목 데이터 접근 객체 (DAO)
 * MyBatis가 자동으로 구현체를 생성
 * 
 * Spring 5.0.7 RELEASE + MySQL 8.0 완전 호환
 * 
 * @author PortWatch
 * @version 3.0 (current_price 업데이트 지원)
 */
@Mapper  // ⭐ MyBatis Mapper 인터페이스임을 명시
public interface StockDAO {
    
    /**
     * 종목 ID로 조회
     */
    StockVO getStockById(int stockId);
    
    /**
     * 종목 코드로 조회
     */
    StockVO getStockByCode(String stockCode);
    
    /**
     * 전체 종목 조회
     */
    List<StockVO> getAllStocks();
    
    /**
     * 시장 타입별 종목 조회
     * 
     * @param marketType KOSPI, KOSDAQ, NASDAQ, NYSE, AMEX
     */
    List<StockVO> getStocksByMarketType(String marketType);
    
    /**
     * 종목명으로 검색
     */
    List<StockVO> searchStocksByName(String stockName);
    
    /**
     * 종목 코드로 검색
     */
    List<StockVO> searchStocksByCode(String stockCode);
    
    /**
     * 종목 추가
     */
    void insertStock(StockVO stock);
    
    /**
     * 종목 수정
     */
    void updateStock(StockVO stock);
    
    /**
     * 종목 삭제
     */
    void deleteStock(int stockId);
    
    /**
     * 현재가 업데이트 (크롤링용)
     * 
     * @param stockCode 종목 코드
     * @param currentPrice 현재가
     * @param priceChange 가격 변동폭
     * @param priceChangeRate 변동률 (%)
     * 
     * ✅ 이 메서드는 크롤링 서비스에서 사용됩니다.
     *    - 한국 주식: StockPriceUpdateServiceImpl
     *    - 미국 주식: USStockPriceUpdateServiceImpl
     */
    void updateCurrentPrice(@Param("stockCode") String stockCode, 
                           @Param("currentPrice") BigDecimal currentPrice, 
                           @Param("priceChange") BigDecimal priceChange, 
                           @Param("priceChangeRate") BigDecimal priceChangeRate);
    
    /**
     * 종목 개수 조회
     */
    int getStockCount();
    
    /**
     * 자동완성용 종목 조회
     */
    List<StockVO> autocomplete(@Param("keyword") String keyword, @Param("limit") int limit);
    
    /**
     * 시장별 종목 조회
     */
    List<StockVO> selectByMarket(String market);
    
    /**
     * 거래량 상위 종목 조회
     */
    List<StockVO> selectTopVolume(int limit);
    
    /**
     * 상승률 상위 종목 조회
     */
    List<StockVO> selectTopGainers(int limit);
    
    /**
     * 전체 종목 조회
     */
    List<StockVO> selectAll();
    
    /**
     * 키워드와 시장 타입으로 검색
     */
    List<StockVO> searchStocksWithMarket(@Param("keyword") String keyword, 
                                         @Param("marketType") String marketType);
    
    /**
     * ID로 조회 (selectById)
     */
    StockVO selectById(Integer stockId);
    
    /**
     * 코드로 조회 (selectByCode)
     */
    StockVO selectByCode(String stockCode);
    
    /**
     * 전체 종목 조회 (Map 형태)
     */
    List<Map<String, Object>> selectAllStocks();
}
