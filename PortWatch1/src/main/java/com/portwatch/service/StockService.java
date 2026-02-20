package com.portwatch.service;

import java.util.List;

import com.portwatch.domain.StockVO;

/**
 * StockService Interface
 * ══════════════════════════════════════════════════════════════
 * ✅ PortfolioController, StockPurchaseApiController 호출 메서드
 * ══════════════════════════════════════════════════════════════
 */
public interface StockService {

    /**
     * ✅ 종목 코드로 주식 조회
     * StockPurchaseApiController에서 호출
     */
    StockVO getStockByCode(String stockCode);

    /**
     * ✅ 전체 주식 목록 조회
     * PortfolioController에서 호출 (생성/수정 폼용)
     */
    List<StockVO> getAllStocks();

    /**
     * 국가별 주식 목록
     */
    List<StockVO> getStocksByCountry(String country);

    /**
     * 시장별 주식 목록
     */
    List<StockVO> getStocksByMarket(String market);

    /**
     * 주식 검색
     */
    List<StockVO> searchStocks(String keyword);

    /**
     * 주식 등록
     */
    int insertStock(StockVO stock);

    /**
     * 주식 수정
     */
    int updateStock(StockVO stock);

    /**
     * 주식 삭제
     */
    int deleteStock(String stockId);
}
