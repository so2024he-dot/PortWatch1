package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;
@Data
/**
 * ✅ 관심종목 + 현재가 VO
 * 
 * WatchlistVO + 종목 가격 정보
 * 
 * @author PortWatch
 * @version 1.0
 */
public class WatchlistWithPriceVO {
    
    // ========================================
    // 기본 필드 (watchlist 테이블)
    // ========================================
    
    private Integer watchlistId;      // 관심종목 ID
    private String memberId;          // 회원 ID (String!)
    private Integer stockId;          // 종목 ID
    private Timestamp createdAt;      // 생성일시
    
    // ========================================
    // 종목 정보 필드 (stock 테이블 조인)
    // ========================================
    
    private String stockCode;         // 종목 코드
    private String stockName;         // 종목명
    private String marketType;        // 시장 구분 (KOSPI/KOSDAQ/NASDAQ/NYSE)
    private String country;           // 국가 (KR/US)
    private String industry;          // 업종
    
    // ========================================
    // 가격 정보 필드
    // ========================================
    
    private BigDecimal currentPrice;  // 현재가
    private BigDecimal priceChange;   // 전일 대비
    private BigDecimal priceChangeRate; // 등락률(%)
    private Long volume;              // 거래량
    
    // ========================================
    // 계산 메서드
    // ========================================
    
    /**
     * 한국 주식 여부
     */
    public boolean isKoreanStock() {
        return "KR".equals(country);
    }
    
    /**
     * 미국 주식 여부
     */
    public boolean isUSStock() {
        return "US".equals(country);
    }
    
    /**
     * KOSPI 여부
     */
    public boolean isKOSPI() {
        return "KOSPI".equals(marketType);
    }
    
    /**
     * KOSDAQ 여부
     */
    public boolean isKOSDAQ() {
        return "KOSDAQ".equals(marketType);
    }
    
    /**
     * NASDAQ 여부
     */
    public boolean isNASDAQ() {
        return "NASDAQ".equals(marketType);
    }
    
    /**
     * NYSE 여부
     */
    public boolean isNYSE() {
        return "NYSE".equals(marketType);
    }
    
    /**
     * 상승 여부
     */
    public boolean isRising() {
        return priceChange != null && priceChange.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 하락 여부
     */
    public boolean isFalling() {
        return priceChange != null && priceChange.compareTo(BigDecimal.ZERO) < 0;
    }
    
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
    
    public BigDecimal getPriceChange() {
        return priceChange;
    }
    
    public void setPriceChange(BigDecimal priceChange) {
        this.priceChange = priceChange;
    }
    
    public BigDecimal getPriceChangeRate() {
        return priceChangeRate;
    }
    
    public void setPriceChangeRate(BigDecimal priceChangeRate) {
        this.priceChangeRate = priceChangeRate;
    }
    
    public Long getVolume() {
        return volume;
    }
    
    public void setVolume(Long volume) {
        this.volume = volume;
    }
    
    // ========================================
    // toString
    // ========================================
    
    @Override
    public String toString() {
        return "WatchlistWithPriceVO [watchlistId=" + watchlistId + ", memberId=" + memberId + ", stockCode="
                + stockCode + ", stockName=" + stockName + ", marketType=" + marketType + ", currentPrice="
                + currentPrice + ", priceChange=" + priceChange + ", priceChangeRate=" + priceChangeRate + "]";
    }
}
