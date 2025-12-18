package com.portwatch.service;

import com.portwatch.domain.StockPriceVO;
import java.util.List;
import java.util.Map;

/**
 * 주가 업데이트 서비스 인터페이스
 */
public interface StockPriceUpdateService {
    
    /**
     * 단일 종목 주가 업데이트
     */
    StockPriceVO updateSingleStock(String stockCode) throws Exception;
    
    /**
     * 여러 종목 주가 업데이트
     */
    Map<String, StockPriceVO> updateMultipleStocks(List<String> stockCodes) throws Exception;
    
    /**
     * 전체 종목 주가 업데이트
     */
    int updateAllStocks() throws Exception;
    
    /**
     * 네이버 금융에서 실시간 주가 크롤링
     */
    Map<String, Object> crawlStockPriceFromNaver(String stockCode) throws Exception;
    
    /**
     * 최신 주가 조회
     */
    StockPriceVO getLatestStockPrice(String stockCode) throws Exception;
    
    /**
     * 주가 히스토리 조회
     */
    List<StockPriceVO> getStockPriceHistory(String stockCode, int days) throws Exception;

	/**
	 * 전체 한국 주식 현재가 업데이트
	 */
	void updateAllStockPrices();

	/**
	 * 특정 종목의 현재가 업데이트
	 */
	void updateStockPrice(String stockCode);

	void updateByMarketType(String marketType);
}

    
