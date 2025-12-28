package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * ✅ 주식 정보 VO - 완벽 최종 수정
 * 
 * volume setter 추가로 MyBatis 매핑 에러 해결!
 * 
 * @author PortWatch
 * @version FINAL - Spring 5.0.7 + MySQL 8.0.33 완벽 호환
 */
public class StockVO {
    
    // 기본 정보
    private Integer stockId;           // 종목 ID (PK)
    private String stockCode;          // 종목 코드
    private String stockName;          // 종목명
    private String country;            // 국가 (KR/US)
    private String marketType;         // 시장 (KOSPI/KOSDAQ/NASDAQ/NYSE)
    private String industry;           // 업종
    
    // 가격 정보
    private BigDecimal currentPrice;   // 현재가
    private BigDecimal openPrice;      // 시가
    private BigDecimal highPrice;      // 고가
    private BigDecimal lowPrice;       // 저가
    private BigDecimal closePrice;     // 종가
    
    // 변동 정보
    private BigDecimal changeAmount;   // 전일 대비 가격 변동
    private BigDecimal changeRate;     // 전일 대비 변동률(%)
    private BigDecimal priceChange;    // priceChange 별칭
    private BigDecimal priceChangeRate;// priceChangeRate 별칭
    
    // 거래 정보
    private Long volume;               // 거래량 ✅✅✅
    private Long tradingVolume;        // 거래량 별칭
    private BigDecimal tradingValue;   // 거래대금
    private BigDecimal marketCap;      // 시가총액
    
    // 시간 정보
    private Timestamp createdAt;       // 생성일시
    private Timestamp updatedAt;       // 수정일시
    private Timestamp lastUpdated;     // 최종 업데이트
    
    // ===== Getters and Setters =====
    
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
     * ✅ priceChange getter (DB 컬럼명 매핑)
     */
    public BigDecimal getPriceChange() {
        return changeAmount;
    }
    
    /**
     * ✅ priceChange setter (DB 컬럼명 매핑)
     */
    public void setPriceChange(BigDecimal priceChange) {
        this.changeAmount = priceChange;
    }
    
    /**
     * ✅ priceChangeRate getter (DB 컬럼명 매핑)
     */
    public BigDecimal getPriceChangeRate() {
        return changeRate;
    }
    
    /**
     * ✅ priceChangeRate setter (DB 컬럼명 매핑)
     */
    public void setPriceChangeRate(BigDecimal priceChangeRate) {
        this.changeRate = priceChangeRate;
    }
    
    /**
     * ✅✅✅ volume getter
     */
    public Long getVolume() {
        return volume;
    }
    
    /**
     * ✅✅✅ volume setter - 핵심 수정!
     * MyBatis 매핑 에러 해결!
     */
    public void setVolume(Long volume) {
        this.volume = volume;
        this.tradingVolume = volume;  // 별칭도 동시 설정
    }
    
    /**
     * ✅ tradingVolume getter (별칭)
     */
    public Long getTradingVolume() {
        return tradingVolume != null ? tradingVolume : volume;
    }
    
    /**
     * ✅ tradingVolume setter (별칭)
     */
    public void setTradingVolume(Long tradingVolume) {
        this.tradingVolume = tradingVolume;
        this.volume = tradingVolume;  // volume도 동시 설정
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
    
    @Override
    public String toString() {
        return "StockVO [stockId=" + stockId + ", stockCode=" + stockCode + ", stockName=" + stockName
                + ", country=" + country + ", marketType=" + marketType + ", industry=" + industry
                + ", currentPrice=" + currentPrice + ", openPrice=" + openPrice + ", highPrice=" + highPrice
                + ", lowPrice=" + lowPrice + ", closePrice=" + closePrice + ", changeAmount=" + changeAmount
                + ", changeRate=" + changeRate + ", volume=" + volume + ", tradingVolume=" + tradingVolume
                + ", tradingValue=" + tradingValue + ", marketCap=" + marketCap + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + ", lastUpdated=" + lastUpdated + "]";
    }
}
