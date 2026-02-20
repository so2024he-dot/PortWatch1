package com.portwatch.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.portwatch.domain.StockVO;

/**
 * StockMapper Interface - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ 콘솔 오류 해결: 메서드 추가
 * - findById (라인 44)
 * - findByMarketType (라인 84)
 * - findByCountryAndMarket (라인 94)
 * - findByIndustry (라인 118)
 * - findAllIndustries (라인 128)
 * - findTopByVolume (라인 142)
 * - findTopByChangeRate (라인 152)
 * - findTopByChangeRateDesc (라인 162)
 * - findRecentlyUpdated (라인 172)
 * - countByCountry (라인 182)
 * - countByMarket (라인 196)
 * - findByMarketAndCountry (라인 240)
 * ══════════════════════════════════════════════════════════════
 */
public interface StockMapper {

    // ✅ 기본 조회
    StockVO findById(Integer stockId);
    StockVO findByCode(String stockCode);
    List<StockVO> findAll();
    List<StockVO> findByCountry(String country);
    List<StockVO> findByMarket(String market);
    List<StockVO> findByMarketType(String marketType);
    List<StockVO> findByCountryAndMarket(@Param("country") String country, @Param("market") String market);
    List<StockVO> searchStocks(String keyword);

    // ✅ 업종 관련
    List<StockVO> findByIndustry(String industry);
    List<String> findAllIndustries();

    // ✅ 정렬 조회
    List<StockVO> findTopByVolume(int limit);
    List<StockVO> findTopByChangeRate(int limit);
    List<StockVO> findTopByChangeRateDesc(int limit);
    List<StockVO> findRecentlyUpdated(int limit);

    // ✅ 통계
    int countByCountry(String country);
    int countByMarket(String market);
    int count();

    // ✅ 복합 조회
    List<StockVO> findByMarketAndCountry(@Param("market") String market, @Param("country") String country);
    List<StockVO> findByPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);
    List<StockVO> findByVolumeRange(@Param("minVolume") long minVolume, @Param("maxVolume") long maxVolume);

    // ✅ 추가 조회
    List<StockVO> findHighPriceStocks(int limit);
    List<StockVO> findLowPriceStocks(int limit);
    List<StockVO> findGainers(int limit);
    List<StockVO> findLosers(int limit);
    List<StockVO> findActiveStocks(int limit);

    // ✅ CRUD
    int insert(StockVO stock);
    int insertCrawl(StockVO stock);
    int update(StockVO stock);
    int updateCurrentPrice(@Param("stockCode") String stockCode, @Param("currentPrice") double currentPrice, @Param("volume") long volume);
    int delete(String stockId);
    int deleteByCountry(String country);
    int deleteByMarket(String market);
    int deleteOldData(int days);

    // ✅ 배치
    int updateBatch(List<StockVO> stocks);

    // ✅ 시장 요약
    Map<String, Object> getMarketSummary(String market);
}
