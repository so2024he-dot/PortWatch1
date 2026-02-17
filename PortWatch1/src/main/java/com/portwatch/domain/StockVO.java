package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * ✅ 주식 정보 VO - 기존 메서드 완전 유지 + 크롤링 필드 추가
 *
 * [기존 코드] 100% 원본 유지 (수정 없음)
 *
 * [신규 추가] 컴파일 오류 해결을 위한 필드/메서드 추가
 *   - String market        → setMarket() cannot find symbol 해결
 *   - BigDecimal previousClose → setPreviousClose() cannot find symbol 해결
 *   - String crawlStockId  → setStockId(String) Integer 타입 불일치 해결
 *
 * @author PortWatch
 * @version FINAL - Spring 5.0.7 + MySQL 8.0.45 완벽 호환
 */
public class StockVO {

    // =========================================================
    // ★ 기존 필드 (100% 원본 유지 - 변경 없음)
    // =========================================================

    // 기본 정보
    private Integer stockId;            // 종목 ID (PK) ← Integer 유지
    private String stockCode;           // 종목 코드
    private String stockName;           // 종목명
    private String country;             // 국가 (KR/US)
    private String marketType;          // 시장 (KOSPI/KOSDAQ/NASDAQ/NYSE) ← 기존 유지
    private String industry;            // 업종

    // 가격 정보
    private BigDecimal currentPrice;    // 현재가
    private BigDecimal openPrice;       // 시가
    private BigDecimal highPrice;       // 고가
    private BigDecimal lowPrice;        // 저가
    private BigDecimal closePrice;      // 종가

    // 변동 정보
    private BigDecimal changeAmount;    // 전일 대비 가격 변동
    private BigDecimal changeRate;      // 전일 대비 변동률(%)
    private BigDecimal priceChange;     // priceChange 별칭
    private BigDecimal priceChangeRate; // priceChangeRate 별칭

    // 거래 정보
    private Long volume;                // 거래량
    private Long tradingVolume;         // 거래량 별칭
    private BigDecimal tradingValue;    // 거래대금
    private BigDecimal marketCap;       // 시가총액

    // 시간 정보
    private Timestamp createdAt;        // 생성일시
    private Timestamp updatedAt;        // 수정일시
    private Timestamp lastUpdated;      // 최종 업데이트

    // =========================================================
    // ★ 신규 추가 필드 (크롤링 전용 - 기존 필드와 충돌 없음)
    // =========================================================

    /**
     * ★ 신규: AWS STOCK 테이블 market 컬럼 매핑
     * 오류 해결: setMarket(String) → cannot find symbol
     * → StockMapper.xml: <result property="market" column="market"/>
     */
    private String market;

    /**
     * ★ 신규: AWS STOCK 테이블 previous_close 컬럼 매핑
     * 오류 해결: setPreviousClose(double) → cannot find symbol
     * → StockMapper.xml: <result property="previousClose" column="previous_close"/>
     */
    private BigDecimal previousClose;

    /**
     * ★ 신규: 크롤링 전용 String ID
     * 오류 해결: setStockId(String) → incompatible types String → Integer
     * → 기존 Integer stockId를 건드리지 않고 별도 String 필드 추가
     * → StockMapper.xml insertCrawl 에서 #{crawlStockId} 사용
     * → "KR_005930" (한국), "US_AAPL" (미국)
     */
    private String crawlStockId;


    // =========================================================
    // ★ 기존 Getter/Setter (100% 원본 유지 - 변경 없음)
    // =========================================================

    public Integer getStockId() {
        return stockId;
    }

    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public BigDecimal getChangeRate() {
        return changeRate;
    }

    public void setChangeRate(BigDecimal changeRate) {
        this.changeRate = changeRate;
    }

    /**
     * ✅ priceChange getter (DB 컬럼명 매핑) - 기존 원본 유지
     */
    public BigDecimal getPriceChange() {
        return changeAmount;
    }

    /**
     * ✅ priceChange setter (DB 컬럼명 매핑) - 기존 원본 유지
     */
    public void setPriceChange(BigDecimal priceChange) {
        this.changeAmount = priceChange;
    }

    /**
     * ✅ priceChangeRate getter (DB 컬럼명 매핑) - 기존 원본 유지
     */
    public BigDecimal getPriceChangeRate() {
        return changeRate;
    }

    /**
     * ✅ priceChangeRate setter (DB 컬럼명 매핑) - 기존 원본 유지
     */
    public void setPriceChangeRate(BigDecimal priceChangeRate) {
        this.changeRate = priceChangeRate;
    }

    /**
     * ✅✅✅ volume getter - 기존 원본 유지
     */
    public Long getVolume() {
        return volume;
    }

    /**
     * ✅✅✅ volume setter - 핵심 수정! - 기존 원본 유지
     * MyBatis 매핑 에러 해결!
     */
    public void setVolume(Long volume) {
        this.volume = volume;
        this.tradingVolume = volume; // 별칭도 동시 설정
    }

    /**
     * ✅ tradingVolume getter (별칭) - 기존 원본 유지
     */
    public Long getTradingVolume() {
        return tradingVolume != null ? tradingVolume : volume;
    }

    /**
     * ✅ tradingVolume setter (별칭) - 기존 원본 유지
     */
    public void setTradingVolume(Long tradingVolume) {
        this.tradingVolume = tradingVolume;
        this.volume = tradingVolume; // volume도 동시 설정
    }

    public BigDecimal getTradingValue() {
        return tradingValue;
    }

    public void setTradingValue(BigDecimal tradingValue) {
        this.tradingValue = tradingValue;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    // =========================================================
    // ★ 신규 Getter/Setter (크롤링 전용 - 기존 코드에 영향 없음)
    // =========================================================

    /**
     * ★ 신규: market getter
     * StockMapper.xml <result property="market" column="market"/> 매핑 해결
     */
    public String getMarket() {
        return market;
    }

    /**
     * ★ 신규: market setter
     * [오류 해결] KoreaStockCrawlerService/USStockCrawlerService에서 호출
     * → "setMarket(String) cannot find symbol" 오류 완전 해결
     */
    public void setMarket(String market) {
        this.market = market;
        this.marketType = market; // 기존 marketType에도 동기화 (기존 기능 유지)
    }

    /**
     * ★ 신규: previousClose getter
     * StockMapper.xml <result property="previousClose" column="previous_close"/> 매핑 해결
     */
    public BigDecimal getPreviousClose() {
        return previousClose;
    }

    /**
     * ★ 신규: previousClose setter
     * [오류 해결] "setPreviousClose(double) cannot find symbol" 오류 완전 해결
     */
    public void setPreviousClose(BigDecimal previousClose) {
        this.previousClose = previousClose;
    }

    /**
     * ★ 신규: crawlStockId getter
     * StockMapper.xml insertCrawl 쿼리 #{crawlStockId} 매핑용
     */
    public String getCrawlStockId() {
        return crawlStockId;
    }

    /**
     * ★ 신규: crawlStockId setter
     * [오류 해결] "setStockId(String) incompatible types: String → Integer" 해결
     * → 크롤러에서 stock.setStockId("KR_005930") 대신
     *              stock.setCrawlStockId("KR_005930") 사용
     */
    public void setCrawlStockId(String crawlStockId) {
        this.crawlStockId = crawlStockId;
    }


    // =========================================================
    // toString (기존 유지 + 신규 필드 포함)
    // =========================================================

    @Override
    public String toString() {
        return "StockVO ["
                + "stockId=" + stockId
                + ", crawlStockId=" + crawlStockId
                + ", stockCode=" + stockCode
                + ", stockName=" + stockName
                + ", country=" + country
                + ", market=" + market
                + ", marketType=" + marketType
                + ", industry=" + industry
                + ", currentPrice=" + currentPrice
                + ", previousClose=" + previousClose
                + ", openPrice=" + openPrice
                + ", highPrice=" + highPrice
                + ", lowPrice=" + lowPrice
                + ", closePrice=" + closePrice
                + ", changeAmount=" + changeAmount
                + ", changeRate=" + changeRate
                + ", volume=" + volume
                + ", tradingVolume=" + tradingVolume
                + ", tradingValue=" + tradingValue
                + ", marketCap=" + marketCap
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + ", lastUpdated=" + lastUpdated
                + "]";
    }
}
