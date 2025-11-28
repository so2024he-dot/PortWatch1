    package com.portwatch.domain;

import java.sql.Timestamp;

/**
 * 관심종목 VO
 * WATCHLIST 테이블 매핑 (stock_id 기반)
 */
public class WatchlistVO {
    // 테이블 기본 필드
    private Integer watchlistId;
    private Integer memberId;
    private Integer stockId;          // DB 테이블의 실제 컬럼
    private Timestamp addedAt;        // DB의 added_at 컬럼
    
    // JOIN용 필드 (STOCK 테이블)
    private String stockCode;         // JOIN으로 가져오는 필드
    private String stockName;
    private String marketType;        // DB는 market_type
    private String industry;
    
    // JOIN용 필드 (최신 가격 정보)
    private Long currentPrice;
    private Long priceChange;
    private Double priceChangeRate;
    
    // 기본 생성자
    public WatchlistVO() {}
    
    // Getters and Setters
    public Integer getWatchlistId() {
        return watchlistId;
    }
    
    public void setWatchlistId(Integer watchlistId) {
        this.watchlistId = watchlistId;
    }
    
    public Integer getMemberId() {
        return memberId;
    }
    
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
    
    public Integer getStockId() {
        return stockId;
    }
    
    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }
    
    public Timestamp getAddedAt() {
        return addedAt;
    }
    
    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
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
    
    public Long getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(Long currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public Long getPriceChange() {
        return priceChange;
    }
    
    public void setPriceChange(Long priceChange) {
        this.priceChange = priceChange;
    }
    
    public Double getPriceChangeRate() {
        return priceChangeRate;
    }
    
    public void setPriceChangeRate(Double priceChangeRate) {
        this.priceChangeRate = priceChangeRate;
    }
    
    @Override
    public String toString() {
        return "WatchlistVO{" +
                "watchlistId=" + watchlistId +
                ", memberId=" + memberId +
                ", stockId=" + stockId +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", currentPrice=" + currentPrice +
                '}';
    }
}

    
