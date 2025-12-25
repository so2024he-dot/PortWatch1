package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 포트폴리오에 속한 종목 정보
 * PORTFOLIO_STOCK 테이블과 매핑
 */
@Data
public class PortfolioStockVO {
    private Integer portfolioStockId;
    private Integer portfolioId;
    private String stockCode;
    private Integer stockQuantity;
    private BigDecimal stockAvgPrice;
    private Timestamp stockAddDate;
    
    // JOIN용 추가 필드 (StockVO에서 가져온 정보)
    private String stockName;
    private String stockMarket;
    private String stockSector;
    private BigDecimal currentPrice; // 현재가 (STOCK_PRICE에서 조회)
    
    // 계산 필드
    private BigDecimal totalPurchaseAmount; // 매입 총액 = quantity * avgPrice
    private BigDecimal currentValue; // 현재 평가액 = quantity * currentPrice
    private BigDecimal profitLoss; // 손익 = currentValue - totalPurchaseAmount
    private Double profitLossRate; // 수익률 = (profitLoss / totalPurchaseAmount) * 100
    
    public PortfolioStockVO() {}
    
    // Getters and Setters
    public Integer getPortfolioStockId() {
        return portfolioStockId;
    }
    
    public void setPortfolioStockId(Integer portfolioStockId) {
        this.portfolioStockId = portfolioStockId;
    }
    
    public Integer getPortfolioId() {
        return portfolioId;
    }
    
    public void setPortfolioId(Integer portfolioId) {
        this.portfolioId = portfolioId;
    }
    
    public String getStockCode() {
        return stockCode;
    }
    
    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public BigDecimal getStockAvgPrice() {
        return stockAvgPrice;
    }
    
    public void setStockAvgPrice(BigDecimal stockAvgPrice) {
        this.stockAvgPrice = stockAvgPrice;
    }
    
    public Timestamp getStockAddDate() {
        return stockAddDate;
    }
    
    public void setStockAddDate(Timestamp stockAddDate) {
        this.stockAddDate = stockAddDate;
    }
    
    public String getStockName() {
        return stockName;
    }
    
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
    
    public String getStockMarket() {
        return stockMarket;
    }
    
    public void setStockMarket(String stockMarket) {
        this.stockMarket = stockMarket;
    }
    
    public String getStockSector() {
        return stockSector;
    }
    
    public void setStockSector(String stockSector) {
        this.stockSector = stockSector;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
        calculateValues();
    }
    
    public BigDecimal getTotalPurchaseAmount() {
        return totalPurchaseAmount;
    }
    
    public void setTotalPurchaseAmount(BigDecimal totalPurchaseAmount) {
        this.totalPurchaseAmount = totalPurchaseAmount;
    }
    
    public BigDecimal getCurrentValue() {
        return currentValue;
    }
    
    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }
    
    public BigDecimal getProfitLoss() {
        return profitLoss;
    }
    
    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }
    
    public Double getProfitLossRate() {
        return profitLossRate;
    }
    
    public void setProfitLossRate(Double profitLossRate) {
        this.profitLossRate = profitLossRate;
    }
    
    /**
     * 수익률 및 손익 자동 계산
     */
    public void calculateValues() {
        if (stockQuantity != null && stockAvgPrice != null) {
            totalPurchaseAmount = stockAvgPrice.multiply(new BigDecimal(stockQuantity));
        }
        
        if (stockQuantity != null && currentPrice != null) {
            currentValue = currentPrice.multiply(new BigDecimal(stockQuantity));
        }
        
        if (currentValue != null && totalPurchaseAmount != null) {
            profitLoss = currentValue.subtract(totalPurchaseAmount);
            
            if (totalPurchaseAmount.compareTo(BigDecimal.ZERO) != 0) {
                profitLossRate = profitLoss.divide(totalPurchaseAmount, 4, BigDecimal.ROUND_HALF_UP)
                                          .multiply(new BigDecimal(100))
                                          .doubleValue();
            }
        }
    }
    
    @Override
    public String toString() {
        return "PortfolioStockVO{" +
                "portfolioStockId=" + portfolioStockId +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", stockAvgPrice=" + stockAvgPrice +
                ", currentPrice=" + currentPrice +
                ", profitLoss=" + profitLoss +
                ", profitLossRate=" + profitLossRate +
                '}';
    }
}
