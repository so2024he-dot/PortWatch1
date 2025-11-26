package com.portwatch.domain;

import java.sql.Timestamp;

public class StockVO {
    private int stockId;
    private String stockCode;
    private String stockName;
    private String marketType;
    private String industry;
    private Timestamp updatedAt;
    
    public StockVO() {}
    
    public int getStockId() {
        return stockId;
    }
    
    public void setStockId(int stockId) {
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
    
    @Override
    public String toString() {
        return "StockVO{stockId=" + stockId + ", stockCode='" + stockCode + "', stockName='" + stockName + "', marketType='" + marketType + "'}";
    }
}
