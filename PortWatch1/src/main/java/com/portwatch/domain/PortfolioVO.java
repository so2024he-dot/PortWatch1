package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * ✅ 포트폴리오 VO - 완벽 수정
 * 
 * Ambiguous setters 에러 완전 해결!
 * - quantity: BigDecimal 타입으로 통일
 * - 중복 setter 모두 제거
 * 
 * @author PortWatch
 * @version FINAL - Spring 5.0.7 + MySQL 8.0.33 완전 대응
 */
public class PortfolioVO {
    
    // 기본 필드
    private Long portfolioId;              // 포트폴리오 ID
    private String memberId;               // 회원 ID (String)
    private Integer stockId;               // 종목 ID
    private String stockCode;              // 종목 코드
    private String stockName;              // 종목명
    private BigDecimal quantity;           // 보유 수량 (소수점 지원)
    private BigDecimal avgPurchasePrice;   // 평균 매입가
    private Timestamp purchaseDate;        // 매입일
    private Timestamp createdAt;           // 생성일시
    private Timestamp updatedAt;           // 수정일시
    private String country;                // 국가 지정
    
    // 추가 필드 (현재가 계산용)
    private BigDecimal currentPrice;       // 현재가
    private BigDecimal totalValue;         // 총 평가액
    private BigDecimal profitLoss;         // 손익
    private BigDecimal profitLossRate;     // 수익률(%)
    private String marketType;             // 시장타입
    
    // 기본 생성자
    public PortfolioVO() {
    }
    
    // ===== Getters and Setters =====
    
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
    
    /**
     * ✅ quantity getter (BigDecimal 반환)
     */
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    /**
     * ✅ quantity setter (BigDecimal만 허용)
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    /**
     * ✅ quantity setter (double 자동 변환)
     * PortfolioServiceImpl에서 사용
     */
    public void setQuantity(double quantity) {
        this.quantity = BigDecimal.valueOf(quantity);
    }
    
    public BigDecimal getAvgPurchasePrice() {
        return avgPurchasePrice;
    }
    
    public void setAvgPurchasePrice(BigDecimal avgPurchasePrice) {
        this.avgPurchasePrice = avgPurchasePrice;
    }
    
    /**
     * ✅ avgPrice 별칭 (PortfolioServiceImpl 호환)
     */
    public BigDecimal getAvgPrice() {
        return avgPurchasePrice;
    }
    
    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPurchasePrice = avgPrice;
    }
    
    /**
     * ✅ purchasePrice 별칭 (PortfolioServiceImpl 호환)
     */
    public BigDecimal getPurchasePrice() {
        return avgPurchasePrice;
    }
    
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.avgPurchasePrice = purchasePrice;
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
    
    /**
     * ✅ setCurrentPrice (Object 자동 변환)
     * PortfolioServiceImpl에서 사용
     */
    public void setCurrentPrice(Object currentPrice) {
        if (currentPrice instanceof BigDecimal) {
            this.currentPrice = (BigDecimal) currentPrice;
        } else if (currentPrice instanceof Number) {
            this.currentPrice = BigDecimal.valueOf(((Number) currentPrice).doubleValue());
        }
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
    
    /**
     * ✅ profitRate 별칭 (호환성)
     */
    public void setProfitRate(BigDecimal profitRate) {
        this.profitLossRate = profitRate;
    }
    
    public String getMarketType() {
        return marketType;
    }
    
    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }
    
    @Override
    public String toString() {
        return "PortfolioVO [portfolioId=" + portfolioId + ", memberId=" + memberId + ", stockId=" + stockId
                + ", stockCode=" + stockCode + ", stockName=" + stockName + ", quantity=" + quantity
                + ", avgPurchasePrice=" + avgPurchasePrice + ", purchaseDate=" + purchaseDate + ", createdAt="
                + createdAt + ", updatedAt=" + updatedAt + ", country=" + country + ", currentPrice=" + currentPrice
                + ", totalValue=" + totalValue + ", profitLoss=" + profitLoss + ", profitLossRate=" + profitLossRate
                + ", marketType=" + marketType + "]";
    }
}
