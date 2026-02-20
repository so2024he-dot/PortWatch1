package com.portwatch.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.portwatch.domain.StockPriceVO;

/**
 * StockPriceMapper Interface - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ 콘솔 오류 해결: 메서드 추가
 * - findHistoryByDays (라인 69)
 * ══════════════════════════════════════════════════════════════
 */
public interface StockPriceMapper {

    // ✅ 기본 CRUD
    int insert(StockPriceVO stockPrice);
    List<StockPriceVO> findByStockCode(String stockCode);
    List<StockPriceVO> findByStockCodeAndPeriod(@Param("stockCode") String stockCode, @Param("startDate") String startDate, @Param("endDate") String endDate);
    List<StockPriceVO> findRecentByStockCode(@Param("stockCode") String stockCode, @Param("limit") int limit);
    int deleteByStockCode(String stockCode);
    int deleteOlderThan(int days);
    int count();

    // ✅ 신규 추가
    List<StockPriceVO> findHistoryByDays(@Param("stockCode") String stockCode, @Param("days") int days);
    StockPriceVO findLatestByCode(String stockCode);
    int insertBatch(List<StockPriceVO> stockPrices);
    int updateBatch(List<StockPriceVO> stockPrices);
    List<StockPriceVO> findByDateRange(@Param("stockCode") String stockCode, @Param("startDate") String startDate, @Param("endDate") String endDate);
    BigDecimal getAveragePrice(@Param("stockCode") String stockCode, @Param("days") int days);
    BigDecimal getHighestPrice(@Param("stockCode") String stockCode, @Param("days") int days);
    BigDecimal getLowestPrice(@Param("stockCode") String stockCode, @Param("days") int days);
    Long getTotalVolume(@Param("stockCode") String stockCode, @Param("days") int days);
    Map<String, Object> getPriceChange(String stockCode);
}
