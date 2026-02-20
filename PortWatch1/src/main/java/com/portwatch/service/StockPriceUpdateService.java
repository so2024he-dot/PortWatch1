package com.portwatch.service;

import com.portwatch.domain.StockPriceVO;

/**
 * StockPriceUpdateService Interface
 * ══════════════════════════════════════════════════════════════
 * ✅ PortfolioApiController 호출 메서드
 * 
 * 라인 252: stockPriceUpdateService.getLatestStockPrice(stockCode)
 * ══════════════════════════════════════════════════════════════
 */
public interface StockPriceUpdateService {

    /**
     * ✅ 최신 주가 조회
     * PortfolioApiController에서 호출
     * 
     * @param stockCode 종목 코드
     * @return 최신 주가 정보
     */
    StockPriceVO getLatestStockPrice(String stockCode);

    /**
     * 주가 업데이트 (크롤링 후 저장)
     */
    int updateStockPrice(StockPriceVO stockPrice);

    /**
     * 주가 이력 삭제 (오래된 데이터)
     */
    int deleteOldPrices(int days);
}
