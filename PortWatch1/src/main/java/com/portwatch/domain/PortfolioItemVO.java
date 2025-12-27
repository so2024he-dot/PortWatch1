package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * ✅ 포트폴리오 아이템 VO - 완벽 수정
 * 
 * PortfolioServiceImpl.addStockToPortfolio(PortfolioItemVO) 전용
 * - 모든 오류 수정
 * - 정상적인 getter/setter 구현
 * 
 * @author PortWatch
 * @version FINAL - Spring 5.0.7 + MySQL 8.0.33 완전 대응
 */
public class PortfolioItemVO {
    
    private String memberId;           // 회원 ID
    private Integer stockId;           // 종목 ID
    private String stockCode;          // 종목 코드
    private BigDecimal quantity;       // 수량
    private BigDecimal price;          // 가격
    private BigDecimal purchasePrice;  // 매입가
    private BigDecimal currentPrice;   // 현재가
    private BigDecimal avgPurchasePrice;  // 평균 매입가
    private Timestamp purchaseDate;    // 매입일
    
    // 기본 생성자
    public PortfolioItemVO() {
    }
    
    // 전체 생성자
    public PortfolioItemVO(String memberId, String stockCode, BigDecimal quantity, BigDecimal price) {
        this.memberId = memberId;
        this.stockCode = stockCode;
        this.quantity = quantity;
        this.price = price;
    }
    
    // ===== Getters and Setters =====
    
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
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }
    
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    
    /**
     * ✅ currentPrice getter
     * PortfolioServiceImpl에서 사용
     */
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    /**
     * ✅ avgPurchasePrice getter
     */
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
    
    @Override
    public String toString() {
        return "PortfolioItemVO [memberId=" + memberId + ", stockId=" + stockId + ", stockCode=" + stockCode
                + ", quantity=" + quantity + ", price=" + price + ", purchasePrice=" + purchasePrice
                + ", currentPrice=" + currentPrice + ", avgPurchasePrice=" + avgPurchasePrice
                + ", purchaseDate=" + purchaseDate + "]";
    }
}
