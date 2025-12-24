package com.portwatch.domain;

import java.sql.Timestamp;

import lombok.Data;

/**
 * ✅ 관심종목 VO
 * 
 * @author PortWatch
 * @version 1.0
 */
@Data
public class WatchlistVO {
    
    // ========================================
    // 기본 필드 (DB 컬럼)
    // ========================================
    
    private Integer watchlistId;      // 관심종목 ID
    private String memberId;          // 회원 ID (String 타입!)
    private Integer stockId;          // 종목 ID
    private Timestamp createdAt;      // 생성일시
    
    // ========================================
    // 추가 필드 (조인 시 사용)
    // ========================================
    
    private String stockCode;         // 종목 코드 (stock 테이블에서)
    private String stockName;         // 종목명 (stock 테이블에서)
    private Double currentPrice;      // 현재가 (stock 테이블에서)
    
    // ========================================
    // Getter & Setter
    // ========================================
    
    public Integer getWatchlistId() {
        return watchlistId;
    }
    
    public void setWatchlistId(Integer watchlistId) {
        this.watchlistId = watchlistId;
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
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
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
    
    public Double getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    // ========================================
    // toString
    // ========================================
    
    @Override
    public String toString() {
        return "WatchlistVO [watchlistId=" + watchlistId + ", memberId=" + memberId + ", stockId=" + stockId
                + ", stockCode=" + stockCode + ", stockName=" + stockName + ", currentPrice=" + currentPrice
                + ", createdAt=" + createdAt + "]";
    }
}
