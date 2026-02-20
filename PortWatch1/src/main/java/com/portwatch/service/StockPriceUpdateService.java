package com.portwatch.service;

import java.util.List;
import java.util.Map;

import com.portwatch.domain.StockPriceVO;

/**
 * StockPriceUpdateService Interface - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ 모든 Controller 호출 메서드 100% 커버
 * ✅ 한국 주식 크롤링 + 실시간 업데이트
 * 
 * 총 15개 메서드
 * ══════════════════════════════════════════════════════════════
 */
public interface StockPriceUpdateService {

    // ─────────────────────────────────────────
    // 기본 조회 (3개)
    // ─────────────────────────────────────────

    /**
     * ✅ 최신 주가 조회
     * PortfolioApiController 호출
     */
    StockPriceVO getLatestStockPrice(String stockCode);

    /**
     * ✅ 주가 이력 조회
     * StockPriceUpdateApiController 라인 155 호출
     */
    List<StockPriceVO> getStockPriceHistory(String stockCode, int days);

    /**
     * 최신 가격 조회 (별칭)
     */
    StockPriceVO getLatestPrice(String stockCode);

    // ─────────────────────────────────────────
    // 크롤링 (3개)
    // ─────────────────────────────────────────

    /**
     * ✅ 네이버 금융 크롤링
     * StockPriceUpdateApiController 라인 184 호출
     */
    Map<String, Object> crawlStockPriceFromNaver(String stockCode);

    /**
     * 한국 주식 크롤링
     */
    StockPriceVO crawlKoreanStock(String stockCode);

    /**
     * 여러 한국 주식 크롤링
     */
    List<StockPriceVO> crawlMultipleKoreanStocks(List<String> stockCodes);

    // ─────────────────────────────────────────
    // 업데이트 (6개)
    // ─────────────────────────────────────────

    /**
     * ✅ 단일 종목 업데이트
     * StockPriceUpdateApiController 라인 43 호출
     */
    StockPriceVO updateSingleStock(String stockCode);

    /**
     * ✅ 여러 종목 업데이트
     * StockPriceUpdateApiController 라인 74 호출
     */
    Map<String, StockPriceVO> updateMultipleStocks(List<String> stockCodes);

    /**
     * ✅ 전체 종목 업데이트
     * StockPriceUpdateApiController 라인 107 호출
     */
    int updateAllStocks();

    /**
     * ✅ 전체 주가 업데이트 (void)
     * AdminStockUpdateController 라인 39 호출
     */
    void updateAllStockPrices();

    /**
     * ✅ 특정 종목 업데이트 (void)
     * AdminStockUpdateController 라인 99 호출
     */
    void updateStockPrice(String stockCode);

    /**
     * ✅ 시장별 업데이트
     * AdminStockUpdateController 라인 132 호출
     */
    void updateByMarketType(String marketType);

    // ─────────────────────────────────────────
    // 저장/삭제 (3개)
    // ─────────────────────────────────────────

    /**
     * 주가 저장
     */
    int saveStockPrice(StockPriceVO stockPrice);

    /**
     * 주가 업데이트 (기존)
     */
    int updateStockPrice(StockPriceVO stockPrice);

    /**
     * 오래된 주가 삭제
     */
    int deleteOldPrices(int days);
}
