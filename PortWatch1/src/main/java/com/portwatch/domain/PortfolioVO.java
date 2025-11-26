package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class PortfolioVO {
    private long portfolioId;
    private int memberId;
    private int stockId;
    private int quantity;
    private BigDecimal avgPurchasePrice;
    private Date purchaseDate;
    private Timestamp updatedAt;
    
    // JOIN 필드
    private String stockCode;
    private String stockName;
    private BigDecimal currentPrice;
    
    public PortfolioVO() {}
    
    public long getPortfolioId() {
        return portfolioId;
    }
    
    public void setPortfolioId(long portfolioId) {
        this.portfolioId = portfolioId;
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
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getAvgPurchasePrice() {
        return avgPurchasePrice;
    }
    
    public void setAvgPurchasePrice(BigDecimal avgPurchasePrice) {
        this.avgPurchasePrice = avgPurchasePrice;
    }
    
    public Date getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
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
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    // 계산 메서드
    public BigDecimal getPurchaseAmount() {
        if (avgPurchasePrice == null) return BigDecimal.ZERO;
        return avgPurchasePrice.multiply(new BigDecimal(quantity));
    }
    
    public BigDecimal getCurrentAmount() {
        if (currentPrice == null) return BigDecimal.ZERO;
        return currentPrice.multiply(new BigDecimal(quantity));
    }
    
    public BigDecimal getProfitLoss() {
        if (currentPrice == null || avgPurchasePrice == null) return BigDecimal.ZERO;
        return currentPrice.subtract(avgPurchasePrice).multiply(new BigDecimal(quantity));
    }
    
    public BigDecimal getProfitRate() {
        if (avgPurchasePrice == null || avgPurchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (currentPrice == null) return BigDecimal.ZERO;
        
        return currentPrice.subtract(avgPurchasePrice)
                .divide(avgPurchasePrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(100));
    }
}
