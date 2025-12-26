package com.portwatch.domain;

import java.math.BigDecimal;

/**
 * ✅ 시장 요약 정보 VO
 * 
 * 시장별 통계 정보를 담는 VO
 * 
 * @author PortWatch
 * @version 1.0
 */
public class MarketSummaryVO {
    
    private String market;              // 시장 (KOSPI, NASDAQ, etc.)
    private String country;             // 국가 (KR, US)
    private Integer stockCount;         // 종목 수
    private BigDecimal avgChangeRate;   // 평균 등락률
    private Long totalVolume;           // 총 거래량
    private Long totalMarketCap;        // 총 시가총액
    
    // 기본 생성자
    public MarketSummaryVO() {}
    
    // 전체 생성자
    public MarketSummaryVO(String market, String country, Integer stockCount, 
                          BigDecimal avgChangeRate, Long totalVolume, Long totalMarketCap) {
        this.market = market;
        this.country = country;
        this.stockCount = stockCount;
        this.avgChangeRate = avgChangeRate;
        this.totalVolume = totalVolume;
        this.totalMarketCap = totalMarketCap;
    }
    
    // Getters and Setters
    public String getMarket() {
        return market;
    }
    
    public void setMarket(String market) {
        this.market = market;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public Integer getStockCount() {
        return stockCount;
    }
    
    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }
    
    public BigDecimal getAvgChangeRate() {
        return avgChangeRate;
    }
    
    public void setAvgChangeRate(BigDecimal avgChangeRate) {
        this.avgChangeRate = avgChangeRate;
    }
    
    public Long getTotalVolume() {
        return totalVolume;
    }
    
    public void setTotalVolume(Long totalVolume) {
        this.totalVolume = totalVolume;
    }
    
    public Long getTotalMarketCap() {
        return totalMarketCap;
    }
    
    public void setTotalMarketCap(Long totalMarketCap) {
        this.totalMarketCap = totalMarketCap;
    }
    
    @Override
    public String toString() {
        return "MarketSummaryVO{" +
                "market='" + market + '\'' +
                ", country='" + country + '\'' +
                ", stockCount=" + stockCount +
                ", avgChangeRate=" + avgChangeRate +
                ", totalVolume=" + totalVolume +
                ", totalMarketCap=" + totalMarketCap +
                '}';
    }
}
