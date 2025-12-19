package com.portwatch.domain;

import java.math.BigDecimal;

/**
 * 포트폴리오 아이템 VO (주식 매입 정보)
 * 
 * ✅ Builder 패턴 지원
 * ✅ StockPurchaseApiController와 호환
 * 
 * @author PortWatch
 * @version 8.0
 */
public class PortfolioItemVO {
    
    private Long portfolioId;      // 포트폴리오 ID (매입 시 회원 ID로 임시 사용)
    private String stockCode;      // 종목 코드
    private double quantity;       // 수량
    private double purchasePrice;  // 매입 가격
    
    // 기본 생성자
    public PortfolioItemVO() {
    }
    
    // 전체 생성자
    public PortfolioItemVO(Long portfolioId, String stockCode, double quantity, double purchasePrice) {
        this.portfolioId = portfolioId;
        this.stockCode = stockCode;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }
    
    // Builder 패턴
    public static PortfolioItemVOBuilder builder() {
        return new PortfolioItemVOBuilder();
    }
    
    public static class PortfolioItemVOBuilder {
        private Long portfolioId;
        private String stockCode;
        private double quantity;
        private double purchasePrice;
        
        public PortfolioItemVOBuilder portfolioId(Long portfolioId) {
            this.portfolioId = portfolioId;
            return this;
        }
        
        public PortfolioItemVOBuilder stockCode(String stockCode) {
            this.stockCode = stockCode;
            return this;
        }
        
        public PortfolioItemVOBuilder quantity(double quantity) {
            this.quantity = quantity;
            return this;
        }
        
        public PortfolioItemVOBuilder purchasePrice(double purchasePrice) {
            this.purchasePrice = purchasePrice;
            return this;
        }
        
        public PortfolioItemVO build() {
            return new PortfolioItemVO(portfolioId, stockCode, quantity, purchasePrice);
        }
    }
    
    // Getters and Setters
    public Long getPortfolioId() {
        return portfolioId;
    }
    
    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }
    
    public String getStockCode() {
        return stockCode;
    }
    
    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }
    
    public double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    
    public double getPurchasePrice() {
        return purchasePrice;
    }
    
    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    
    @Override
    public String toString() {
        return "PortfolioItemVO{" +
                "portfolioId=" + portfolioId +
                ", stockCode='" + stockCode + '\'' +
                ", quantity=" + quantity +
                ", purchasePrice=" + purchasePrice +
                '}';
    }
}
