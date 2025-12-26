package com.portwatch.persistence;

import java.math.BigDecimal;
import java.util.List;

import com.portwatch.domain.StockVO;

/**
 * ✅ Stock DAO 인터페이스 (완전 구현)
 * 
 * StockMapper.xml과 연동되는 메서드들
 * 
 * @author PortWatch
 * @version FINAL
 */
public interface StockDAO {
    
    /**
     * ✅ 종목 코드로 조회 (selectStockByCode)
     * 
     * @param stockCode 종목 코드
     * @return StockVO
     * @throws Exception
     */
    StockVO selectStockByCode(String stockCode) throws Exception;
    
    /**
     * ✅ 종목 코드로 조회 (selectByCode - 별칭)
     * 
     * @param stockCode 종목 코드
     * @return StockVO
     * @throws Exception
     */
    StockVO selectByCode(String stockCode) throws Exception;
    
    /**
     * ✅ 종목 ID로 조회
     * 
     * @param stockId 종목 ID
     * @return StockVO
     * @throws Exception
     */
    StockVO selectStockById(Integer stockId) throws Exception;
    
    /**
     * ✅ ID로 조회 (별칭)
     * 
     * @param stockId 종목 ID
     * @return StockVO
     * @throws Exception
     */
    StockVO selectById(Integer stockId) throws Exception;
    
    /**
     * ✅ 전체 종목 조회
     * 
     * @return List<StockVO>
     * @throws Exception
     */
    List<StockVO> selectAllStocks() throws Exception;
    
    /**
     * ✅ 전체 종목 조회 (별칭)
     * 
     * @return List<StockVO>
     * @throws Exception
     */
    List<StockVO> selectAll() throws Exception;
    
    /**
     * ✅ 국가별 종목 조회
     * 
     * @param country 국가 (KR, US)
     * @return List<StockVO>
     * @throws Exception
     */
    List<StockVO> selectStocksByCountry(String country) throws Exception;
    
    /**
     * ✅ 국가별 종목 조회 (별칭)
     * 
     * @param country 국가 (KR, US)
     * @return List<StockVO>
     * @throws Exception
     */
    List<StockVO> selectByCountry(String country) throws Exception;
    
    /**
     * ✅ 시장별 종목 조회
     * 
     * @param marketType 시장 타입 (KOSPI, KOSDAQ, NASDAQ, NYSE)
     * @return List<StockVO>
     * @throws Exception
     */
    List<StockVO> selectStocksByMarketType(String marketType) throws Exception;
    
    /**
     * ✅ 시장별 종목 조회 (별칭)
     * 
     * @param marketType 시장 타입
     * @return List<StockVO>
     * @throws Exception
     */
    List<StockVO> selectByMarket(String marketType) throws Exception;
    
    /**
     * ✅ 업종별 종목 조회
     * 
     * @param industry 업종
     * @return List<StockVO>
     * @throws Exception
     */
    List<StockVO> selectStocksByIndustry(String industry) throws Exception;
    
    /**
     * ✅ 업종별 종목 조회 (별칭)
     * 
     * @param industry 업종
     * @return List<StockVO>
     * @throws Exception
     */
    List<StockVO> selectByIndustry(String industry) throws Exception;
    
    /**
     * ✅ 전체 업종 목록 조회
     * 
     * @return List<String>
     * @throws Exception
     */
    List<String> selectAllIndustries() throws Exception;
    
    /**
     * ✅ 거래량 순 조회
     * 
     * @param limit 조회 개수
     * @return List<StockVO>
     * @throws Exception
     */
    List<StockVO> selectStocksOrderByVolume(int limit) throws Exception;
    
    /**
     * ✅ 거래량 순 조회 (별칭)
     * 
     * @param limit 조회 개수
     * @return List<StockVO>
     * @throws Exception
     */
    List<StockVO> selectOrderByVolume(int limit) throws Exception;
    
    /**
     * ✅ 등락률 순 조회
     * 
     * @param limit 조회 개수
     * @return List<StockVO>
     * @throws Exception
     */
    List<StockVO> selectStocksOrderByChangeRate(int limit) throws Exception;
    
    /**
     * ✅ 등락률 순 조회 (별칭)
     * 
     * @param limit 조회 개수
     * @return List<StockVO>
     * @throws Exception
     */
    List<StockVO> selectOrderByChangeRate(int limit) throws Exception;
    
    /**
     * ✅ 종목 검색
     * 
     * @param keyword 검색 키워드
     * @return List<StockVO>
     * @throws Exception
     */
    List<StockVO> searchStocks(String keyword) throws Exception;
    
    /**
     * ✅ 종목 검색 (별칭)
     * 
     * @param keyword 검색 키워드
     * @return List<StockVO>
     * @throws Exception
     */
    List<StockVO> search(String keyword) throws Exception;
    
    /**
     * ✅ 종목 추가
     * 
     * @param stock 종목 정보
     * @throws Exception
     */
    void insertStock(StockVO stock) throws Exception;
    
    /**
     * ✅ 종목 추가 (별칭)
     * 
     * @param stock 종목 정보
     * @throws Exception
     */
    void insert(StockVO stock) throws Exception;
    
    /**
     * ✅ 종목 정보 수정
     * 
     * @param stock 종목 정보
     * @throws Exception
     */
    void updateStock(StockVO stock) throws Exception;
    
    /**
     * ✅ 종목 정보 수정 (별칭)
     * 
     * @param stock 종목 정보
     * @throws Exception
     */
    void update(StockVO stock) throws Exception;
    
    /**
     * ✅ 현재가 업데이트
     * 
     * @param stockCode 종목 코드
     * @param currentPrice 현재가
     * @throws Exception
     */
    void updateStockPrice(String stockCode, BigDecimal currentPrice) throws Exception;
    
    /**
     * ✅ 전체 종목 수 조회
     * 
     * @return int
     * @throws Exception
     */
    int countAllStocks() throws Exception;
    
    /**
     * ✅ 국가별 종목 수 조회
     * 
     * @param country 국가
     * @return int
     * @throws Exception
     */
    int countStocksByCountry(String country) throws Exception;
    
    /**
     * ✅ 종목 삭제
     * 
     * @param stockId 종목 ID
     * @throws Exception
     */
    void delete(Integer stockId) throws Exception;

	List<StockVO> getStocksByMarketType(String string);

	StockVO getStockByCode(String stockCode);

	void updateCurrentPrice(String stockCode, BigDecimal currentPrice, BigDecimal priceChange,
			BigDecimal priceChangeRate);
}
