package com.portwatch.service;

import java.util.List;
import java.util.Map;

import com.portwatch.domain.StockPriceVO;

/**
 * USStockPriceUpdateService Interface - 신규
 * ══════════════════════════════════════════════════════════════
 * ✅ AdminStockUpdateController 호출 메서드 100% 커버
 * ✅ 미국 주식 실시간 업데이트 (Alpha Vantage / Yahoo Finance)
 * 
 * 총 8개 메서드
 * ══════════════════════════════════════════════════════════════
 */
public interface USStockPriceUpdateService {

    /**
     * ✅ 전체 미국 주식 업데이트
     * AdminStockUpdateController 라인 43 호출
     */
    void updateAllUSStockPrices();

    /**
     * ✅ 특정 미국 주식 업데이트
     * AdminStockUpdateController 라인 96 호출
     */
    void updateUSStockPrice(String stockCode);

    /**
     * ✅ 시장별 업데이트
     * AdminStockUpdateController 라인 129 호출
     */
    void updateByMarketType(String marketType);

    /**
     * 단일 미국 주식 업데이트
     */
    StockPriceVO updateSingleUSStock(String stockCode);

    /**
     * 여러 미국 주식 업데이트
     */
    Map<String, StockPriceVO> updateMultipleUSStocks(List<String> stockCodes);

    /**
     * 미국 주식 크롤링 (Yahoo Finance)
     */
    StockPriceVO crawlUSStock(String stockCode);

    /**
     * 미국 주가 저장
     */
    int saveUSStockPrice(StockPriceVO stockPrice);

    /**
     * 미국 최신 가격 조회
     */
    StockPriceVO getLatestUSPrice(String stockCode);
}
