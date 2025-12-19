package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 종목 정보 VO (Value Object)
 * 
 * @author PortWatch
 * @version 2.0 - 국가별 구분 및 현재가 추가
 */
public class StockVO {
    
    private Integer stockId;           // 종목 ID
    private String stockCode;          // 종목 코드
    private String stockName;          // 종목명
    private String marketType;         // 시장 구분 (KOSPI, KOSDAQ, NASDAQ, NYSE)
    private String industry;           // 업종
    
    // 추가 필드 (v2.0)
    private String country;            // 국가 코드 (KR, US, JP)
    private BigDecimal currentPrice;   // 현재가 (크롤링된 가격)
    private BigDecimal priceChange;    // 가격 변동폭
    private BigDecimal priceChangeRate; // 변동률 (%)
    
    private Timestamp updatedAt;       // 수정일시
    
    // ================================================
    // Constructors
    // ================================================
    
    public StockVO() {
    }
    
    public StockVO(String stockCode, String stockName, String marketType) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.marketType = marketType;
    }
    
    // ================================================
    // Getters and Setters
    // ================================================
    
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
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    // double 타입으로도 받을 수 있도록 편의 메서드 추가
    public double getCurrentPriceAsDouble() {
        return currentPrice != null ? currentPrice.doubleValue() : 0.0;
    }
    
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = BigDecimal.valueOf(currentPrice);
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
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // ================================================
    // Utility Methods
    // ================================================
    
    /**
     * 한국 주식 여부 확인
     */
    public boolean isKoreanStock() {
        return "KR".equals(country) || 
               "KOSPI".equals(marketType) || 
               "KOSDAQ".equals(marketType);
    }
    
    /**
     * 미국 주식 여부 확인
     */
    public boolean isUSStock() {
        return "US".equals(country) || 
               "NASDAQ".equals(marketType) || 
               "NYSE".equals(marketType) ||
               "AMEX".equals(marketType);
    }
    
    /**
     * 분할 매수 가능 여부 (미국 주식만 가능)
     */
    public boolean isFractionalShareAllowed() {
        return isUSStock();
    }
    
    // ================================================
    // toString
    // ================================================
    
    @Override
    public String toString() {
        return "StockVO{" +
                "stockId=" + stockId +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", marketType='" + marketType + '\'' +
                ", industry='" + industry + '\'' +
                ", country='" + country + '\'' +
                ", currentPrice=" + currentPrice +
                ", priceChange=" + priceChange +
                ", priceChangeRate=" + priceChangeRate +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
