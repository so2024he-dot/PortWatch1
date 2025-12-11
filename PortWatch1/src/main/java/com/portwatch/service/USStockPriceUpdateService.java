package com.portwatch.service;

import com.portwatch.domain.StockPriceVO;
import java.util.List;
import java.util.Map;

/**
 * 미국 주식 가격 업데이트 서비스
 * Yahoo Finance에서 데이터 수집
 */
public interface USStockPriceUpdateService {
    
    /**
     * 단일 미국 종목 주가 업데이트
     * @param stockSymbol 종목 심볼 (예: AAPL, MSFT)
     * @return 업데이트된 주가 정보
     */
    StockPriceVO updateSingleUSStock(String stockSymbol) throws Exception;
    
    /**
     * 여러 미국 종목 주가 업데이트
     * @param stockSymbols 종목 심볼 리스트
     * @return 종목별 업데이트 결과 맵
     */
    Map<String, StockPriceVO> updateMultipleUSStocks(List<String> stockSymbols) throws Exception;
    
    /**
     * 전체 미국 종목 주가 업데이트
     * market_type이 NASDAQ, NYSE, AMEX인 모든 종목 업데이트
     * @return 성공한 종목 수
     */
    int updateAllUSStocks() throws Exception;
    
    /**
     * Yahoo Finance에서 실시간 주가 크롤링
     * @param stockSymbol 종목 심볼
     * @return 크롤링된 주가 데이터 맵
     */
    Map<String, Object> crawlStockPriceFromYahoo(String stockSymbol) throws Exception;
    
    /**
     * 최신 주가 조회
     * @param stockSymbol 종목 심볼
     * @return 최신 주가 정보
     */
    StockPriceVO getLatestUSStockPrice(String stockSymbol) throws Exception;
    
    /**
     * 주가 히스토리 조회
     * @param stockSymbol 종목 심볼
     * @param days 조회할 일수
     * @return 주가 히스토리 리스트
     */
    List<StockPriceVO> getUSStockPriceHistory(String stockSymbol, int days) throws Exception;
}
