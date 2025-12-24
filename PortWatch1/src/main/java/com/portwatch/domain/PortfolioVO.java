package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 포트폴리오 VO
 * 
 * ✅ String memberId, BigDecimal 수량 (분할 매수 지원)
 * @author PortWatch
 * @version 2.0 - Spring 5.0.7 + MySQL 8.0.33 호환
 */
@Data
public class PortfolioVO {
    
    private Long portfolioId;              // 포트폴리오 ID
    private String memberId;               // 회원 ID (String)
    private Integer stockId;               // 종목 ID
    private String stockCode;              // 종목 코드
    private String stockName;              // 종목명
    private BigDecimal quantity;           // 보유 수량 (소수점 지원)
    private BigDecimal avgPurchasePrice;   // 평균 매입가 (avgPrice 별칭)
    private Timestamp purchaseDate;             // 매입일
    private Timestamp createdAt;           // 생성일시
    private Timestamp updatedAt;           // 수정일시
    
    // 추가 필드 (현재가 계산용)
    private BigDecimal currentPrice;       // 현재가
    private BigDecimal totalValue;         // 총 평가액
    private BigDecimal profitLoss;         // 손익
    private BigDecimal profitLossRate;     // 수익률(%)
    
    // 기본 생성자
    public PortfolioVO() {
    }
    
    // Getters and Setters
    public Long getPortfolioId() {
        return portfolioId;
    }
    
    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
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
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getAvgPurchasePrice() {
        return avgPurchasePrice;
    }
    
    public void setPurchaseDate() {
    	this.setPurchaseDate();
    }
    public void setAvgPurchasePrice(BigDecimal avgPurchasePrice) {
        this.avgPurchasePrice = avgPurchasePrice;
    }
    
    // avgPrice 별칭 (PortfolioServiceImpl 호환)
    public BigDecimal getAvgPrice() {
        return avgPurchasePrice;
    }
    
    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPurchasePrice = avgPrice;
    }
    
    public Timestamp getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(Timestamp purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
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
    
    public BigDecimal getTotalValue() {
        return totalValue;
    }
    
    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
    
    public BigDecimal getProfitLoss() {
        return profitLoss;
    }
    
    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }
    
    public BigDecimal getProfitLossRate() {
        return profitLossRate;
    }
    
    public void setProfitLossRate(BigDecimal profitLossRate) {
        this.profitLossRate = profitLossRate;
    }
    
    @Override
    public String toString() {
        return "PortfolioVO{" +
                "portfolioId=" + portfolioId +
                ", memberId='" + memberId + '\'' +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", quantity=" + quantity +
                ", avgPurchasePrice=" + avgPurchasePrice +
                ", currentPrice=" + currentPrice +
                '}';
    }
}
