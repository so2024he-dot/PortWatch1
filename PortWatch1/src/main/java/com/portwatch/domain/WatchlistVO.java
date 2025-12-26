package com.portwatch.domain;

import java.sql.Timestamp;

import lombok.Data;

/**
 * ✅ 관심종목 VO (완전 구현)
 * 
 * WATCHLIST 테이블과 1:1 매핑
 * 
 * @author PortWatch Team
 * @version 1.0
 */
@Data
public class WatchlistVO {
    
    // 기본 필드 (WATCHLIST 테이블)
    private Long watchlistId;         // watchlist_id BIGINT AUTO_INCREMENT PK
    private String memberId;          // member_id VARCHAR(50) FK
    private String stockCode;         // stock_code VARCHAR(20) FK
    private Timestamp createdAt;      // created_at TIMESTAMP
    
    // 조인 시 사용할 추가 필드 (STOCK 테이블)
    private String stockName;         // stock_name (조인)
    private Double currentPrice;      // current_price (조인)
    private String marketType;        // market_type (조인)
    private String country;           // country (조인)
    
    // 기본 생성자
    public WatchlistVO() {
    }
    
    // 생성자 (필수 필드)
    public WatchlistVO(String memberId, String stockCode) {
        this.memberId = memberId;
        this.stockCode = stockCode;
    }
    
    // ===== Getter & Setter =====
    
    public Long getWatchlistId() {
        return watchlistId;
    }
    
    public void setWatchlistId(Long watchlistId) {
        this.watchlistId = watchlistId;
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
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    // 조인 필드 Getter & Setter
    
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
    
    public String getMarketType() {
        return marketType;
    }
    
    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    @Override
	public String toString() {
		return "WatchlistVO [watchlistId=" + watchlistId + ", memberId=" + memberId + ", stockCode=" + stockCode
				+ ", createdAt=" + createdAt + ", stockName=" + stockName + ", currentPrice=" + currentPrice
				+ ", marketType=" + marketType + ", country=" + country + "]";
	}
}
