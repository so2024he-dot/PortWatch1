package com.portwatch.mapper;

import java.util.List;
import java.util.Map;

import com.portwatch.domain.StockVO;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * Stock Mapper Interface
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * MyBatis와 연동되는 Stock 데이터 접근 인터페이스
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
public interface StockMapper {
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 삽입
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * 종목 등록
     */
    public int insert(StockVO stock);
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 조회
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * 종목 코드로 조회
     */
    public StockVO findByCode(String stockCode);
    
    /**
     * 종목 ID로 조회
     */
    public StockVO findById(String stockId);
    
    /**
     * 모든 종목 조회
     */
    public List<StockVO> findAll();
    
    /**
     * 국가별 종목 조회
     */
    public List<StockVO> findByCountry(String country);
    
    /**
     * 시장별 종목 조회
     */
    public List<StockVO> findByMarket(String market);
    
    /**
     * 종목 검색
     */
    public List<StockVO> searchStocks(String keyword);
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 업데이트
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * 종목 정보 수정
     */
    public int update(StockVO stock);
    
    /**
     * 주가 정보 업데이트
     */
    public int updatePrice(StockVO stock);
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 삭제
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * 종목 삭제
     */
    public int delete(String stockId);
    
    /**
     * 국가별 주식 삭제 (크롤링 업데이트용)
     */
    public int deleteByCountry(String country);
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 통계
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * 총 종목 수
     */
    public int count();
    
    /**
     * 국가별 종목 수
     */
    public List<Map<String, Object>> countByCountry();
}
