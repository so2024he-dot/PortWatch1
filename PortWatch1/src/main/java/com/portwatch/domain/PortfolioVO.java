package com.portwatch.domain;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 포트폴리오 VO
 * 
 * ✅ BigDecimal quantity 지원 (소수점 수량 가능)
 * ✅ Timestamp purchaseDate 사용
 * ✅ 현재 MySQL DDL에 완벽히 맞춤
 * 
 * @author PortWatch
 * @version 6.0 (결제 시스템 연동)
 */
public class PortfolioVO {
    
    // 기본 정보
    private Long portfolioId;
    private Integer memberId;
    private Integer stockId;
    
    // 보유 정보
    @NotNull(message = "보유 수량은 필수 입력 항목입니다.")
    @DecimalMin(value = "0.0001", message = "수량은 0보다 커야 합니다.")
    private BigDecimal quantity;  // ✅ BigDecimal로 변경 (소수점 수량 지원)
    
    @NotNull(message = "평균 매입가는 필수 입력 항목입니다.")
    @DecimalMin(value = "0.01", message = "평균 매입가는 0보다 커야 합니다.")
    private BigDecimal avgPurchasePrice;
    
    private Timestamp purchaseDate;  // ✅ Timestamp로 변경
    private Timestamp updatedAt;
    
    // 조인 정보 (STOCK 테이블)
    private String stockCode;
    private String stockName;
    private String marketType;
    private String industry;
    
    // 주가 정보 (STOCK_PRICE 테이블)
    private BigDecimal currentPrice;
    
    // 기본 생성자
    public PortfolioVO() {}
    
    // ================================================
    // Getters and Setters
    // ================================================
    
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
    
    public Timestamp getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(Timestamp purchaseDate) {
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
    
    // ================================================
    // 계산 메서드 (자동 계산)
    // ================================================
    
    /**
     * 총 매입금액 = 수량 × 평균 매입가
     */
    public BigDecimal getTotalPurchaseAmount() {
        if (quantity != null && avgPurchasePrice != null) {
            return avgPurchasePrice.multiply(quantity);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 총 평가금액 = 수량 × 현재가
     */
    public BigDecimal getTotalCurrentValue() {
        if (quantity != null && currentPrice != null) {
            return currentPrice.multiply(quantity);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 평가손익 = 총 평가금액 - 총 매입금액
     */
    public BigDecimal getProfit() {
        return getTotalCurrentValue().subtract(getTotalPurchaseAmount());
    }
    
    /**
     * 수익률(%) = ((현재가 - 평균 매입가) / 평균 매입가) × 100
     */
    public BigDecimal getProfitRate() {
        BigDecimal purchaseAmount = getTotalPurchaseAmount();
        if (purchaseAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (currentPrice == null) {
            return BigDecimal.ZERO;
        }
        
        return currentPrice.subtract(avgPurchasePrice)
                .divide(avgPurchasePrice, 4, BigDecimal.ROUND_HALF_UP)
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
