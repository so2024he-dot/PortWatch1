package com.portwatch.domain;

import java.sql.Timestamp;

public class WatchlistVO {
    private long watchlistId;
    private int memberId;
    private int stockId;
    private Timestamp addedAt;
    
    // JOIN 필드
    private String stockCode;
    private String stockName;
    private String marketType;
    
    public WatchlistVO() {}
    
    public long getWatchlistId() {
        return watchlistId;
    }
    
    public void setWatchlistId(long watchlistId) {
        this.watchlistId = watchlistId;
    }
    
    public int getMemberId() {
        return memberId;
    }
    
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
    
    public int getStockId() {
        return stockId;
    }
    
    public void setStockId(int stockId) {
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
}
