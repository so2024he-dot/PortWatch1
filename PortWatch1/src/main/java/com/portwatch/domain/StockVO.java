package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 종목 VO
 * 현재 MySQL DDL에 완벽히 맞춤
 */
public class StockVO {
    
    // 기본 정보
    private Integer stockId;
    private String stockCode;
    private String stockName;
    private String marketType;    // KOSPI, KOSDAQ
    private String industry;      // 업종
    private Timestamp updatedAt;
    
    // 주가 정보 (STOCK_PRICE 테이블 JOIN)
    private BigDecimal currentPrice;      // 현재가
    private BigDecimal openPrice;         // 시가
    private BigDecimal highPrice;         // 고가
    private BigDecimal lowPrice;          // 저가
    private BigDecimal closePrice;        // 종가
    private BigDecimal priceChange;       // 가격 변동
    private BigDecimal priceChangeRate;   // 변동률 (%)
    private Long volume;                  // 거래량
    private Long tradingValue;            // 거래대금
    
    // 기본 생성자
    public StockVO() {}
    
    // Getters and Setters
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
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
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
    
    public BigDecimal getPriceChange() {
        return priceChange;
    }
    
    public void setPriceChange(BigDecimal priceChange) {
        this.priceChange = priceChange;
    }
    
    public BigDecimal getPriceChangeRate() {
        return priceChangeRate;
    }
    
    public void setPriceChangeRate(BigDecimal priceChangeRate) {
        this.priceChangeRate = priceChangeRate;
    }
    
    public Long getVolume() {
        return volume;
    }
    
    public void setVolume(Long volume) {
        this.volume = volume;
    }
    
    public Long getTradingValue() {
        return tradingValue;
    }
    
    public void setTradingValue(Long tradingValue) {
        this.tradingValue = tradingValue;
    }
    
    @Override
    public String toString() {
        return "StockVO{" +
                "stockId=" + stockId +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", marketType='" + marketType + '\'' +
                ", industry='" + industry + '\'' +
                ", currentPrice=" + currentPrice +
                ", priceChange=" + priceChange +
                ", priceChangeRate=" + priceChangeRate +
                ", volume=" + volume +
                '}';
    }
}