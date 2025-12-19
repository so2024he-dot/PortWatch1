package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * 주가 정보 VO (STOCK_PRICE 테이블)
 * 실시간 주가 업데이트용
 */
public class StockPriceVO {
    
    private Long priceId;
    private Integer stockId;
    private Date tradeDate;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private Long volume;
    private Long tradingValue;
    private Timestamp createdAt;
    
    // 추가 정보 (JOIN용)
    private String stockCode;
    private String stockName;
    
    public StockPriceVO() {}
    
    // Getters and Setters
    public Long getPriceId() {
        return priceId;
    }
    
    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }
    
    public Integer getStockId() {
        return stockId;
    }
    
    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }
    
    public Date getTradeDate() {
        return tradeDate;
    }
    
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
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
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
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
    
    @Override
    public String toString() {
        return "StockPriceVO{" +
                "priceId=" + priceId +
                ", stockId=" + stockId +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", tradeDate=" + tradeDate +
                ", openPrice=" + openPrice +
                ", highPrice=" + highPrice +
                ", lowPrice=" + lowPrice +
                ", closePrice=" + closePrice +
                ", volume=" + volume +
                ", tradingValue=" + tradingValue +
                '}';
    }

	public void setCurrentPrice(BigDecimal currentPrice) {
		this.setCurrentPrice(currentPrice);		
	}

	public void setPriceChange(BigDecimal priceChange) {
		this.setPriceChange(priceChange);			
	}

	public void setPriceChangeRate(BigDecimal priceChangeRate) {
		this.setPriceChangeRate(priceChangeRate);
		
	}

	

	
	
}
