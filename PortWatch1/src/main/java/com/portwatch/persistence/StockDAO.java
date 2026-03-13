package com.portwatch.persistence;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.portwatch.domain.StockVO;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * ✅ StockDAO 인터페이스 (최종 병합본)
 *
 * 수정 내역:
 *   v1 FINAL    : 원본 메서드 전체 (주석 포함)
 *   v2 MERGE    : @Mapper 어노테이션 추가
 *                 → root-context.xml annotationClass 스캔 대상 포함
 *                 → StockPurchaseValidationService @Autowired StockDAO 정상 동작
 *
 * XML 매핑: src/main/resources/mappers/StockDAOMapper.xml
 *           namespace = com.portwatch.persistence.StockDAO
 *
 * @author PortWatch
 * @version FINAL MERGED
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Mapper
public interface StockDAO {

    // ════════════════════════════════════════════════════════════
    // 단건 조회 - 종목코드로
    // ════════════════════════════════════════════════════════════

    /**
     * ✅ 종목 코드로 조회 (StockPurchaseValidationService 사용)
     */
    StockVO selectStockByCode(String stockCode) throws Exception;

    /**
     * ✅ 종목 코드로 조회 (selectByCode - 별칭)
     */
    StockVO selectByCode(String stockCode) throws Exception;

    /**
     * ✅ 종목 코드로 조회 (getStockByCode - 별칭)
     */
    StockVO getStockByCode(String stockCode);

    // ════════════════════════════════════════════════════════════
    // 단건 조회 - 종목ID로
    // ════════════════════════════════════════════════════════════

    /**
     * ✅ 종목 ID로 조회
     */
    StockVO selectStockById(Integer stockId) throws Exception;

    /**
     * ✅ 종목 ID로 조회 (selectById - 별칭)
     */
    StockVO selectById(Integer stockId) throws Exception;

    // ════════════════════════════════════════════════════════════
    // 전체 조회
    // ════════════════════════════════════════════════════════════

    /**
     * ✅ 전체 종목 조회
     */
    List<StockVO> selectAllStocks() throws Exception;

    /**
     * ✅ 전체 종목 조회 (selectAll - 별칭)
     */
    List<StockVO> selectAll() throws Exception;

    // ════════════════════════════════════════════════════════════
    // 국가별 조회
    // ════════════════════════════════════════════════════════════

    /**
     * ✅ 국가별 종목 조회
     *
     * @param country 국가 코드 (KR, US)
     */
    List<StockVO> selectStocksByCountry(String country) throws Exception;

    /**
     * ✅ 국가별 종목 조회 (별칭)
     */
    List<StockVO> selectByCountry(String country) throws Exception;

    // ════════════════════════════════════════════════════════════
    // 시장별 조회
    // ════════════════════════════════════════════════════════════

    /**
     * ✅ 시장별 종목 조회
     *
     * @param marketType 시장 타입 (KOSPI, KOSDAQ, NASDAQ, NYSE)
     */
    List<StockVO> selectStocksByMarketType(String marketType) throws Exception;

    /**
     * ✅ 시장별 종목 조회 (selectByMarket - 별칭)
     */
    List<StockVO> selectByMarket(String marketType) throws Exception;

    /**
     * ✅ 시장별 종목 조회 (getStocksByMarketType - 별칭)
     */
    List<StockVO> getStocksByMarketType(String marketType);

    // ════════════════════════════════════════════════════════════
    // 국가 + 시장 복합 조회
    // ════════════════════════════════════════════════════════════

    /**
     * ✅ 국가 + 시장 복합 조회
     *
     * @param country    국가 코드 (KR, US)
     * @param market     시장 타입 (KOSPI, KOSDAQ, NASDAQ, NYSE)
     */
    List<StockVO> selectByCountryAndMarket(String country, String market) throws Exception;

    // ════════════════════════════════════════════════════════════
    // 업종별 조회
    // ════════════════════════════════════════════════════════════

    /**
     * ✅ 업종별 종목 조회
     */
    List<StockVO> selectStocksByIndustry(String industry) throws Exception;

    /**
     * ✅ 업종별 종목 조회 (별칭)
     */
    List<StockVO> selectByIndustry(String industry) throws Exception;

    /**
     * ✅ 전체 업종 목록 조회 (중복 제거)
     */
    List<String> selectAllIndustries() throws Exception;

    // ════════════════════════════════════════════════════════════
    // 정렬 조회
    // ════════════════════════════════════════════════════════════

    /**
     * ✅ 거래량 순 내림차순 조회
     *
     * @param limit 조회 개수
     */
    List<StockVO> selectStocksOrderByVolume(int limit) throws Exception;

    /**
     * ✅ 거래량 순 조회 (별칭)
     */
    List<StockVO> selectOrderByVolume(int limit) throws Exception;

    /**
     * ✅ 등락률 순 내림차순 조회 (상승률 상위)
     *
     * @param limit 조회 개수
     */
    List<StockVO> selectStocksOrderByChangeRate(int limit) throws Exception;

    /**
     * ✅ 등락률 순 조회 (별칭)
     */
    List<StockVO> selectOrderByChangeRate(int limit) throws Exception;

    // ════════════════════════════════════════════════════════════
    // 검색
    // ════════════════════════════════════════════════════════════

    /**
     * ✅ 종목 검색 (종목코드 OR 종목명 LIKE)
     *
     * @param keyword 검색 키워드
     */
    List<StockVO> searchStocks(String keyword) throws Exception;

    /**
     * ✅ 종목 검색 (search - 별칭)
     */
    List<StockVO> search(String keyword) throws Exception;

    // ════════════════════════════════════════════════════════════
    // INSERT
    // ════════════════════════════════════════════════════════════

    /**
     * ✅ 종목 추가
     */
    void insertStock(StockVO stock) throws Exception;

    /**
     * ✅ 종목 추가 (insert - 별칭)
     */
    void insert(StockVO stock) throws Exception;

    // ════════════════════════════════════════════════════════════
    // UPDATE
    // ════════════════════════════════════════════════════════════

    /**
     * ✅ 종목 정보 전체 수정
     */
    void updateStock(StockVO stock) throws Exception;

    /**
     * ✅ 종목 정보 수정 (update - 별칭)
     */
    void update(StockVO stock) throws Exception;

    /**
     * ✅ 현재가만 업데이트
     *
     * @param stockCode    종목 코드
     * @param currentPrice 현재가
     */
    void updateStockPrice(String stockCode, BigDecimal currentPrice) throws Exception;

    /**
     * ✅ 현재가 + 등락 정보 업데이트 (크롤러 배치 사용)
     *
     * @param stockCode      종목 코드
     * @param currentPrice   현재가
     * @param priceChange    등락 금액
     * @param priceChangeRate 등락률
     */
    void updateCurrentPrice(String stockCode, BigDecimal currentPrice,
                            BigDecimal priceChange, BigDecimal priceChangeRate);

    // ════════════════════════════════════════════════════════════
    // DELETE
    // ════════════════════════════════════════════════════════════

    /**
     * ✅ 종목 삭제
     *
     * @param stockId 종목 ID
     */
    void delete(Integer stockId) throws Exception;

    // ════════════════════════════════════════════════════════════
    // 통계
    // ════════════════════════════════════════════════════════════

    /**
     * ✅ 전체 종목 수 조회
     */
    int countAllStocks() throws Exception;

    /**
     * ✅ 국가별 종목 수 조회
     *
     * @param country 국가 코드 (KR, US)
     */
    int countStocksByCountry(String country) throws Exception;
}
