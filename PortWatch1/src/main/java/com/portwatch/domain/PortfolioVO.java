package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.springframework.http.HttpStatus;

/**
 * ✅ 포트폴리오 VO
 * 
 * @author PortWatch
 * @version 1.0
 */
public class PortfolioVO {
    
    // ========================================
    // 기본 필드 (DB 컬럼)
    // ========================================
    
    private Integer portfolioId;      // 포트폴리오 ID
    private String memberId;          // 회원 ID
    private String stockCode;         // 종목 코드
    private Double quantity;          // 보유 수량
    private Double avgPrice;          // 평균 매입가
    private Timestamp createdAt;      // 생성일시
    private Timestamp updatedAt;      // 수정일시
    
    // ========================================
    // 추가 필드 (조인 시 사용)
    // ========================================
    
    private String stockName;         // 종목명 (stock 테이블에서)
    private Double currentPrice;      // 현재가 (stock 테이블에서)
    
    // ========================================
    // 계산 필드 (getter에서 계산)
    // ========================================
    
    /**
     * 평가 금액 = 보유 수량 × 현재가
     */
    public Double getEvaluationAmount() {
        if (quantity != null && currentPrice != null) {
            return quantity * currentPrice;
        }
        return 0.0;
    }
    
    /**
     * 매입 금액 = 보유 수량 × 평균 매입가
     */
    public Double getBuyAmount() {
        if (quantity != null && avgPrice != null) {
            return quantity * avgPrice;
        }
        return 0.0;
    }
    
    /**
     * 손익 = 평가 금액 - 매입 금액
     */
    public Double getProfit() {
        if (quantity != null && currentPrice != null && avgPrice != null) {
            return (currentPrice - avgPrice) * quantity;
        }
        return 0.0;
    }
    
    /**
     * 수익률(%) = (손익 / 매입 금액) × 100
     */
    public Double getProfitRate() {
        Double buyAmount = getBuyAmount();
        if (buyAmount != null && buyAmount > 0) {
            return (getProfit() / buyAmount) * 100;
        }
        return 0.0;
    }
    
    // ========================================
    // Getter & Setter
    // ========================================
    
   
    
    public void setPortfolioId(Integer portfolioId) {
        this.portfolioId = portfolioId;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public String getStockCode() {
        return stockCode;
    }
    
    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }
    
    public Double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    
    public Double getAvgPrice() {
        return avgPrice;
    }
    
    public void setAvgPrice(Double avgPrice) {
        this.avgPrice = avgPrice;
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
    
    public String getStockName() {
        return stockName;
    }
    
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
    
    public Double getCurrentPrice() {
        return currentPrice;
    }
    
   
    
    // ========================================
    // toString
    // ========================================
    
    @Override
    public String toString() {
        return "PortfolioVO [portfolioId=" + portfolioId + ", memberId=" + memberId + ", stockCode=" + stockCode
                + ", quantity=" + quantity + ", avgPrice=" + avgPrice + ", stockName=" + stockName
                + ", currentPrice=" + currentPrice + ", profit=" + getProfit() + ", profitRate=" + getProfitRate()
                + "%]";
    }

	public String getPortfolioId() {
		
		return memberId;
	}

	
	
}
