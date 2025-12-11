package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Date;

/**
 * 관심종목 + 현재가 통합 VO
 * 
 * ✅ memberId 기반
 */
public class WatchlistWithPriceVO {
    
    // Watchlist 정보
    private Integer watchlistId;
    private Integer memberId;    // ✅ memberId 사용
    private Integer stockId;
    private Timestamp addedAt;
    
    // Stock 정보
    private String stockCode;
    private String stockName;
    private String marketType;  // KRX, NASDAQ, NYSE, AMEX
    private String industry;
    
    // Stock Price 정보 (최신 주가)
    private BigDecimal currentPrice;    // 현재가 (종가)
    private BigDecimal openPrice;       // 시가
    private BigDecimal highPrice;       // 고가
    private BigDecimal lowPrice;        // 저가
    private BigDecimal previousClose;   // 전일 종가
    private BigDecimal priceChange;     // 가격 변동
    private BigDecimal changePercent;   // 변동률 (%)
    private Long volume;                // 거래량
    private Date tradeDate;             // 거래일
    
    // 계산 필드
    private String changeDirection;     // "UP", "DOWN", "FLAT"
    private boolean isKoreanStock;      // 한국 주식 여부
    private boolean isUSStock;          // 미국 주식 여부
    
    // Constructors
    public WatchlistWithPriceVO() {
    }
    
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
        // 시장 타입에 따라 플래그 자동 설정
        this.isKoreanStock = "KRX".equals(marketType) || "KOSPI".equals(marketType) || "KOSDAQ".equals(marketType);
        this.isUSStock = "NASDAQ".equals(marketType) || "NYSE".equals(marketType) || "AMEX".equals(marketType);
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
    
    public BigDecimal getPreviousClose() {
        return previousClose;
    }
    
    public void setPreviousClose(BigDecimal previousClose) {
        this.previousClose = previousClose;
    }
    
    public BigDecimal getPriceChange() {
        return priceChange;
    }
    
    public void setPriceChange(BigDecimal priceChange) {
        this.priceChange = priceChange;
        // 가격 변동에 따라 방향 자동 설정
        if (priceChange != null) {
            int comparison = priceChange.compareTo(BigDecimal.ZERO);
            if (comparison > 0) {
                this.changeDirection = "UP";
            } else if (comparison < 0) {
                this.changeDirection = "DOWN";
            } else {
                this.changeDirection = "FLAT";
            }
        }
    }
    
    public BigDecimal getChangePercent() {
        return changePercent;
    }
    
    public void setChangePercent(BigDecimal changePercent) {
        this.changePercent = changePercent;
    }
    
    public Long getVolume() {
        return volume;
    }
    
    public void setVolume(Long volume) {
        this.volume = volume;
    }
    
    public Date getTradeDate() {
        return tradeDate;
    }
    
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }
    
    public String getChangeDirection() {
        return changeDirection;
    }
    
    public void setChangeDirection(String changeDirection) {
        this.changeDirection = changeDirection;
    }
    
    public boolean isKoreanStock() {
        return isKoreanStock;
    }
    
    public void setKoreanStock(boolean koreanStock) {
        isKoreanStock = koreanStock;
    }
    
    public boolean isUSStock() {
        return isUSStock;
    }
    
    public void setUSStock(boolean USStock) {
        isUSStock = USStock;
    }
    
    @Override
    public String toString() {
        return "WatchlistWithPriceVO{" +
                "watchlistId=" + watchlistId +
                ", memberId=" + memberId +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", currentPrice=" + currentPrice +
                ", priceChange=" + priceChange +
                ", changePercent=" + changePercent +
                '}';
    }
}
