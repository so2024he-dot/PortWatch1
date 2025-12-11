package com.portwatch.domain;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * 포트폴리오 VO (분할 매수 지원)
 * 
 * ✅ quantity: Integer → BigDecimal 변경
 * ✅ 0.5주, 0.1주 등 소수점 매수 가능
 * 
 * @version 2.0
 */
public class PortfolioVO {
    
    // 기본 정보
    private Long portfolioId;
    private Integer memberId;
    private Integer stockId;
    
    // ✅ 수정: Integer → BigDecimal
    @NotNull(message = "보유 수량은 필수 입력 항목입니다.")
    @DecimalMin(value = "0.01", message = "수량은 0.01 이상이어야 합니다.")
    private BigDecimal quantity;  // 분할 매수 지원 (0.01주 단위)
    
    @NotNull(message = "평균 매입가는 필수 입력 항목입니다.")
    @DecimalMin(value = "0.01", message = "평균 매입가는 0보다 커야 합니다.")
    private BigDecimal avgPurchasePrice;
    
    private Date purchaseDate;
    private Timestamp updatedAt;
    
    // 조인 정보 (STOCK 테이블)
    private String stockCode;
    private String stockName;
    private String marketType;
    private String industry;
    
    // 주가 정보 (STOCK_PRICE 테이블)
    private BigDecimal currentPrice;
    
    // 계산 필드
    private BigDecimal totalPurchaseAmount;  // 총 매입금액 = quantity * avgPurchasePrice
    private BigDecimal totalCurrentValue;    // 총 평가금액 = quantity * currentPrice
    private BigDecimal profit;               // 손익 = totalCurrentValue - totalPurchaseAmount
    private BigDecimal profitRate;           // 수익률 = (profit / totalPurchaseAmount) * 100
    
    // 기본 생성자
    public PortfolioVO() {}
    
    // Getters and Setters
    public Long getPortfolioId() {
        return portfolioId;
    }
    
    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
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
    
    // ✅ 수정: BigDecimal 타입
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
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
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    // ✅ 계산 필드 Getters (BigDecimal 사용)
    public BigDecimal getTotalPurchaseAmount() {
        if (quantity != null && avgPurchasePrice != null) {
            return avgPurchasePrice.multiply(quantity)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalCurrentValue() {
        if (quantity != null && currentPrice != null) {
            return currentPrice.multiply(quantity)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getProfit() {
        return getTotalCurrentValue().subtract(getTotalPurchaseAmount());
    }
    
    public BigDecimal getProfitRate() {
        BigDecimal purchaseAmount = getTotalPurchaseAmount();
        if (purchaseAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (currentPrice == null) {
            return BigDecimal.ZERO;
        }
        
        return currentPrice.subtract(avgPurchasePrice)
                .divide(avgPurchasePrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
    }
    
    @Override
    public String toString() {
        return "PortfolioVO{" +
                "portfolioId=" + portfolioId +
                ", memberId=" + memberId +
                ", stockId=" + stockId +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", quantity=" + quantity +
                ", avgPurchasePrice=" + avgPurchasePrice +
                ", currentPrice=" + currentPrice +
                ", profit=" + getProfit() +
                ", profitRate=" + getProfitRate() +
                '}';
    }
}
