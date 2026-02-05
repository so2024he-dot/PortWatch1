package com.portwatch.persistence;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.portwatch.domain.StockVO;

public interface StockMapper {
    
    /**
     * 종목 등록
     */
    void insert(StockVO stock);
    
    /**
     * 종목 코드로 조회
     */
    StockVO findByCode(String stockCode);
    
    /**
     * 종목 ID로 조회
     */
    StockVO findById(Integer stockId);
    
    /**
     * ⭐ 모든 종목 조회 (백업용)
     */
    List<StockVO> findAll();
    
    /**
     * 국가별 종목 조회
     */
    List<StockVO> findByCountry(String country);
    
    /**
     * 시장별 종목 조회
     */
    List<StockVO> findByMarketType(String marketType);
    
    /**
     * 종목 검색
     */
    List<StockVO> searchStocks(@Param("keyword") String keyword);
    
    /**
     * 종목 정보 수정
     */
    void update(StockVO stock);
    
    /**
     * 주가 정보 업데이트
     */
    void updatePrice(@Param("stockId") Integer stockId, 
                     @Param("currentPrice") Double currentPrice,
                     @Param("changeRate") Double changeRate);
    
    /**
     * 종목 삭제
     */
    void delete(Integer stockId);
    
    /**
     * 총 종목 수
     */
    int count();
    
    /**
     * 국가별 종목 수
     */
    List<Map<String, Object>> countByCountry();
}
