package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * ✅ 관심종목 VO (완전 구현) - v2.0
 * 
 * WATCHLIST 테이블과 1:1 매핑
 * changeRate, priceChange 필드 추가
 * 
 * @author PortWatch Team
 * @version 2.0
 */
@Data
public class WatchlistVO {
    
    // 기본 필드 (WATCHLIST 테이블)
    private Long watchlistId;         // watchlist_id BIGINT AUTO_INCREMENT PK
    private String memberId;          // member_id VARCHAR(50) FK
    private String stockCode;         // stock_code VARCHAR(20) FK
    private Timestamp createdAt;      // created_at TIMESTAMP
    
    // 조인 시 사용할 추가 필드 (STOCK 테이블)
    private String stockName;         // stock_name (조인)
    private Double currentPrice;      // current_price (조인)
    private String marketType;        // market_type (조인)
    private String country;           // country (조인)
    
    // ✅ 추가 필드 (주가 변동 정보)
    private Double priceChange;       // 가격 변동액 (현재가 - 전일종가)
    private Double changeRate;        // 등락률 (%)
    private Double previousClose;     // 전일 종가
    private Long volume;              // 거래량
    
    // BigDecimal 버전 (계산용)
    private BigDecimal currentPriceBD;
    private BigDecimal priceChangeBD;
    private BigDecimal changeRateBD;
    
    // 기본 생성자
    public WatchlistVO() {
    }
    
    // 생성자 (필수 필드)
    public WatchlistVO(String memberId, String stockCode) {
        this.memberId = memberId;
        this.stockCode = stockCode;
    }
    
    // ===== Getter & Setter =====
    
    public Long getWatchlistId() {
        return watchlistId;
    }
    
    public void setWatchlistId(Long watchlistId) {
        this.watchlistId = watchlistId;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public String getStockCode() {
        return stockCode;
    }
    
    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    // 조인 필드 Getter & Setter
    
    public String getStockName() {
        return stockName;
    }
    
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
    
    public Double getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public String getMarketType() {
        return marketType;
    }
    
    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    // ✅ 추가 필드 Getter & Setter
    
    public Double getPriceChange() {
        return priceChange;
    }
    
    public void setPriceChange(Double priceChange) {
        this.priceChange = priceChange;
    }
    
    /**
     * ✅ changeRate getter - JSP에서 사용
     */
    public Double getChangeRate() {
        return changeRate;
    }
    
    /**
     * ✅ changeRate setter
     */
    public void setChangeRate(Double changeRate) {
        this.changeRate = changeRate;
    }
    
    public Double getPreviousClose() {
        return previousClose;
    }
    
    public void setPreviousClose(Double previousClose) {
        this.previousClose = previousClose;
    }
    
    public Long getVolume() {
        return volume;
    }
    
    public void setVolume(Long volume) {
        this.volume = volume;
    }
    
    // BigDecimal 버전
    
    public BigDecimal getCurrentPriceBD() {
        return currentPriceBD;
    }
    
    public void setCurrentPriceBD(BigDecimal currentPriceBD) {
        this.currentPriceBD = currentPriceBD;
    }
    
    public BigDecimal getPriceChangeBD() {
        return priceChangeBD;
    }
    
    public void setPriceChangeBD(BigDecimal priceChangeBD) {
        this.priceChangeBD = priceChangeBD;
    }
    
    public BigDecimal getChangeRateBD() {
        return changeRateBD;
    }
    
    public void setChangeRateBD(BigDecimal changeRateBD) {
        this.changeRateBD = changeRateBD;
    }
    
    @Override
    public String toString() {
        return "WatchlistVO [watchlistId=" + watchlistId + ", memberId=" + memberId + ", stockCode=" + stockCode
                + ", createdAt=" + createdAt + ", stockName=" + stockName + ", currentPrice=" + currentPrice
                + ", marketType=" + marketType + ", country=" + country + ", priceChange=" + priceChange
                + ", changeRate=" + changeRate + ", previousClose=" + previousClose + ", volume=" + volume + "]";
    }
}
