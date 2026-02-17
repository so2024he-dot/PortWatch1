package com.portwatch.mapper;

import java.util.List;
import java.util.Map;

import com.portwatch.domain.StockVO;

/**
 * StockMapper Interface
 * ══════════════════════════════════════════════════
 * [기존 유지] 모든 기존 메서드 100% 원본 보존
 * [신규 추가] insertCrawl, deleteByCountry
 * ══════════════════════════════════════════════════
 */
public interface StockMapper {

    // ─────────────────────────────────────────
    // [기존 유지] INSERT
    // ─────────────────────────────────────────

    /** 기존 종목 등록 (Integer stockId 기반) */
    int insert(StockVO stock);

    // ─────────────────────────────────────────
    // ★ [신규 추가] 크롤링 전용 INSERT
    // ─────────────────────────────────────────

    /**
     * ★ 크롤링 전용 INSERT (ON DUPLICATE KEY UPDATE 포함)
     * crawlStockId(String "KR_005930") → stock_id VARCHAR(50) 저장
     * 기존 insert()와 별도 동작 → 충돌 없음
     */
    int insertCrawl(StockVO stock);

    // ─────────────────────────────────────────
    // [기존 유지] SELECT
    // ─────────────────────────────────────────

    StockVO findByCode(String stockCode);

    StockVO findById(String stockId);

    List<StockVO> findAll();

    List<StockVO> findByCountry(String country);

    List<StockVO> findByMarket(String market);

    List<StockVO> searchStocks(String keyword);

    // ─────────────────────────────────────────
    // [기존 유지] UPDATE
    // ─────────────────────────────────────────

    int update(StockVO stock);

    int updatePrice(StockVO stock);

    // ─────────────────────────────────────────
    // [기존 유지] DELETE
    // ─────────────────────────────────────────

    int delete(String stockId);

    // ─────────────────────────────────────────
    // ★ [신규 추가] 국가별 전체 삭제
    // ─────────────────────────────────────────

    /**
     * ★ 국가별 전체 삭제
     * 크롤링 전 기존 데이터 초기화용
     * KoreaStockCrawlerService / USStockCrawlerService 에서 호출
     */
    int deleteByCountry(String country);

    // ─────────────────────────────────────────
    // [기존 유지] 통계
    // ─────────────────────────────────────────

    int count();

    List<Map<String, Object>> countByCountry();
}
