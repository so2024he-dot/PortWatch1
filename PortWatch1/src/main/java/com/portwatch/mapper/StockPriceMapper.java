package com.portwatch.mapper;

import java.util.List;

import com.portwatch.domain.StockPriceVO;

/**
 * StockPriceMapper Interface
 * ══════════════════════════════════════════════════════════════
 * 문제: StockPriceMapper.xml은 로드됐지만 StockPriceMapper.class 미등록
 * 원인: StockPriceMapper.java 인터페이스 파일이 없었음
 * 해결: 이 파일 생성 → 자동 Bean 등록
 *
 * 용도: 주가 이력 데이터 저장 (차트 데이터용)
 *       StockPriceVO.class는 domain에서 정상 로드됨 (이미 있음)
 * ══════════════════════════════════════════════════════════════
 */
public interface StockPriceMapper {

    /** 주가 이력 등록 */
    int insert(StockPriceVO stockPrice);

    /** 종목 코드로 주가 이력 조회 (최신순) */
    List<StockPriceVO> findByStockCode(String stockCode);

    /** 종목 코드 + 기간 조회 */
    List<StockPriceVO> findByStockCodeAndPeriod(String stockCode, String startDate, String endDate);

    /** 최근 N개 이력 조회 */
    List<StockPriceVO> findRecentByStockCode(String stockCode, int limit);

    /** 주가 이력 삭제 */
    int deleteByStockCode(String stockCode);

    /** 오래된 이력 삭제 (N일 이전) */
    int deleteOlderThan(int days);

    /** 전체 이력 수 */
    int count();
}
