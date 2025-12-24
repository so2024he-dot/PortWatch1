package com.portwatch.service;

import com.portwatch.domain.StockPriceVO;

import java.util.List;
import java.util.Map;

/**
 * ✅ 미국 주식 현재가 업데이트 서비스 인터페이스
 * 
 * Yahoo Finance를 통한 미국 주식 가격 크롤링 및 업데이트
 * 
 * @author PortWatch
 * @version 1.0 - Spring 5.0.7 호환
 */
public interface USStockPriceUpdateService {
    
    /**
     * 전체 미국 주식 현재가 업데이트
     * 
     * NASDAQ, NYSE, AMEX 전체 종목 업데이트
     */
    void updateAllUSStockPrices();
    
    /**
     * 특정 미국 주식 현재가 업데이트
     * 
     * @param symbol 종목 심볼 (예: AAPL, MSFT, TSLA)
     */
    void updateUSStockPrice(String symbol);
    
    /**
     * 단일 미국 종목 업데이트 (StockPriceVO 반환)
     * 
     * @param symbol 종목 심볼
     * @return 업데이트된 주가 정보
     * @throws Exception 업데이트 실패 시
     */
    StockPriceVO updateSingleUSStock(String symbol) throws Exception;
    
    /**
     * 복수 미국 종목 일괄 업데이트
     * 
     * @param symbols 종목 심볼 리스트
     * @return 종목별 주가 정보 맵 (symbol → StockPriceVO)
     * @throws Exception 업데이트 실패 시
     */
    Map<String, StockPriceVO> updateMultipleUSStocks(List<String> symbols) throws Exception;
    
    /**
     * 전체 미국 주식 업데이트 (업데이트된 종목 수 반환)
     * 
     * @return 업데이트된 총 종목 수
     * @throws Exception 업데이트 실패 시
     */
    int updateAllUSStocks() throws Exception;
    
    /**
     * Yahoo Finance에서 주가 정보 직접 크롤링
     * 
     * @param symbol 종목 심볼
     * @return 크롤링 결과 (currentPrice, priceChange, priceChangeRate 등)
     * @throws Exception 크롤링 실패 시
     */
    Map<String, Object> crawlUSStockPriceFromYahoo(String symbol) throws Exception;
    
    /**
     * 최신 미국 주가 조회 (DB에서)
     * 
     * @param symbol 종목 심볼
     * @return 최신 주가 정보
     * @throws Exception 조회 실패 시
     */
    StockPriceVO getLatestUSStockPrice(String symbol) throws Exception;
    
    /**
     * 미국 주가 히스토리 조회
     * 
     * @param symbol 종목 심볼
     * @param days 조회할 일수
     * @return 주가 히스토리 리스트
     * @throws Exception 조회 실패 시
     * @throws UnsupportedOperationException 현재 미지원
     */
    List<StockPriceVO> getUSStockPriceHistory(String symbol, int days) throws Exception;
}
