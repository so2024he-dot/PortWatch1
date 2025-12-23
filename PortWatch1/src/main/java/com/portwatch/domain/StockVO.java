package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 주식 종목 VO
 * 
 * ✅ industry 필드 추가 (업종 정보)
 * @author PortWatch
 * @version 3.0 - Spring 5.0.7 + MySQL 8.0.33 호환
 */
public class StockVO {
    
    private Integer stockId;           // 종목 ID
    private String stockCode;          // 종목 코드 (005930, AAPL 등)
    private String stockName;          // 종목명
    private String country;            // 국가 (KR, US)
    private String marketType;         // 시장 (KOSPI, KOSDAQ, NASDAQ, NYSE)
    private BigDecimal currentPrice;   // 현재가
    private BigDecimal changeAmount;   // 전일 대비 변동액
    private BigDecimal changeRate;     // 전일 대비 변동률 (%)
    private Long tradingVolume;        // 거래량
    private BigDecimal marketCap;      // 시가총액
    private String sector;             // 섹터 (기술, 금융, 헬스케어 등)
    private String industry;           // ✅ 업종 (반도체, 자동차, 소프트웨어 등)
    private Timestamp updatedAt;       // 업데이트 시간
    
    // 기본 생성자
    public StockVO() {
    }
    
    // Getters and Setters
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
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getMarketType() {
        return marketType;
    }
    
    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public BigDecimal getChangeAmount() {
        return changeAmount;
    }
    
    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }
    
    public BigDecimal getChangeRate() {
        return changeRate;
    }
    
    public void setChangeRate(BigDecimal changeRate) {
        this.changeRate = changeRate;
    }
    
    public Long getTradingVolume() {
        return tradingVolume;
    }
    
    public void setTradingVolume(Long tradingVolume) {
        this.tradingVolume = tradingVolume;
    }
    
    public BigDecimal getMarketCap() {
        return marketCap;
    }
    
    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }
    
    public String getSector() {
        return sector;
    }
    
    public void setSector(String sector) {
        this.sector = sector;
    }
    
    /**
     * ✅ 업종 getter (StockFilterController 호환)
     */
    public String getIndustry() {
        return industry;
    }
    
    /**
     * ✅ 업종 setter
     */
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "StockVO{" +
                "stockId=" + stockId +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", country='" + country + '\'' +
                ", marketType='" + marketType + '\'' +
                ", industry='" + industry + '\'' +
                ", currentPrice=" + currentPrice +
                ", changeRate=" + changeRate +
                '}';
    }

	public BigDecimal getPriceChange() {
		
		return this.getPriceChange();
	}

	public BigDecimal getPriceChangeRate() {
		
		return this.getPriceChangeRate();
	}
}
