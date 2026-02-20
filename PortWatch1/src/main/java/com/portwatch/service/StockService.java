package com.portwatch.service;

import java.util.List;

import com.portwatch.domain.StockVO;

/**
 * StockService Interface - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ 모든 Controller 호출 메서드 100% 커버
 * ✅ 크롤링 데이터 (한국 100개 + 미국 100개) 기반
 * 
 * 총 20개 메서드
 * ══════════════════════════════════════════════════════════════
 */
public interface StockService {

    // ─────────────────────────────────────────
    // 기본 조회 (8개)
    // ─────────────────────────────────────────

    /**
     * ✅ 종목 코드로 조회
     * StockPurchaseApiController, PortfolioController 호출
     */
    StockVO getStockByCode(String stockCode);

    /**
     * ✅ stockId로 조회 (Integer)
     * PaymentController 호출
     */
    StockVO getStockById(Integer stockId);

    /**
     * ✅ 전체 주식 목록
     * PortfolioController, StockController 호출
     */
    List<StockVO> getAllStocks();

    /**
     * ✅ 국가별 조회
     * StockController, StockFilterController 호출
     */
    List<StockVO> getStocksByCountry(String country);

    /**
     * ✅ 시장별 조회 (market 컬럼)
     * StockController 호출
     */
    List<StockVO> getStocksByMarket(String market);

    /**
     * ✅ 시장 타입별 조회 (marketType 컬럼)
     * StockFilterController 호출
     */
    List<StockVO> getStocksByMarketType(String marketType);

    /**
     * ✅ 국가 + 시장 복합 조회
     * StockController 호출
     */
    List<StockVO> getStocksByCountryAndMarket(String country, String market);

    /**
     * ✅ 주식 검색 (종목 코드 또는 종목명)
     * StockController 호출
     */
    List<StockVO> searchStocks(String keyword);

    // ─────────────────────────────────────────
    // 업종 관련 (2개)
    // ─────────────────────────────────────────

    /**
     * ✅ 업종별 조회
     * StockFilterController 호출
     */
    List<StockVO> getStocksByIndustry(String industry);

    /**
     * ✅ 전체 업종 목록
     * StockFilterController 호출
     */
    List<String> getAllIndustries();

    // ─────────────────────────────────────────
    // 정렬 조회 (4개)
    // ─────────────────────────────────────────

    /**
     * ✅ 거래량 상위 종목
     * MarketApiController, StockFilterController 호출
     */
    List<StockVO> getStocksOrderByVolume(int limit);

    /**
     * ✅ 상승률 상위 종목
     * MarketApiController, StockFilterController 호출
     */
    List<StockVO> getStocksOrderByChangeRate(int limit);

    /**
     * 하락률 상위 종목
     */
    List<StockVO> getStocksOrderByChangeRateDesc(int limit);

    /**
     * 최근 업데이트 종목
     */
    List<StockVO> findRecentlyUpdated(int limit);

    // ─────────────────────────────────────────
    // 통계 (2개)
    // ─────────────────────────────────────────

    /**
     * 국가별 종목 수
     */
    int countByCountry(String country);

    /**
     * 시장별 종목 수
     */
    int countByMarket(String market);

    // ─────────────────────────────────────────
    // CRUD (4개)
    // ─────────────────────────────────────────

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

    /**
     * 시장 + 국가 복합 조회
     */
    List<StockVO> findByMarketAndCountry(String market, String country);
}
