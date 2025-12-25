package com.portwatch.domain;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;


/**
 * 대시보드 통계 VO
 * 여러 테이블의 통계 정보를 담는 집계용 VO
 */
@Data
public class DashboardVO {
    
    // 전체 통계
    private Integer totalPortfolios;        // 총 포트폴리오 수
    private Integer totalStocks;            // 총 보유 종목 수
    private Integer totalWatchlist;         // 총 관심종목 수
    
    // 투자 금액 통계
    private BigDecimal totalInvestment;     // 총 투자금액
    private BigDecimal totalCurrentValue;   // 총 평가금액
    private BigDecimal totalProfitLoss;     // 총 손익
    private Double totalProfitLossRate;     // 총 수익률
    
    // 포트폴리오별 통계 리스트
    private List<PortfolioSummaryVO> portfolioSummaries;
    
    // 보유 종목 Top 5
    private List<PortfolioStockVO> topStocks;
    
    // 최근 뉴스
    private List<NewsVO> recentNews;
    
    // 기본 생성자
    public DashboardVO() {
        this.totalPortfolios = 0;
        this.totalStocks = 0;
        this.totalWatchlist = 0;
        this.totalInvestment = BigDecimal.ZERO;
        this.totalCurrentValue = BigDecimal.ZERO;
        this.totalProfitLoss = BigDecimal.ZERO;
        this.totalProfitLossRate = 0.0;
    }
    
    // 수익률 계산 메서드
    public void calculateProfitLossRate() {
        if (totalInvestment != null && totalInvestment.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = totalProfitLoss.divide(totalInvestment, 4, BigDecimal.ROUND_HALF_UP)
                                            .multiply(BigDecimal.valueOf(100));
            this.totalProfitLossRate = rate.doubleValue();
        } else {
            this.totalProfitLossRate = 0.0;
        }
    }
    
    // 내부 클래스: 포트폴리오 요약
    public static class PortfolioSummaryVO {
        private Integer portfolioId;
        private String portfolioName;
        private BigDecimal investment;
        private BigDecimal currentValue;
        private BigDecimal profitLoss;
        private Double profitLossRate;
        private Integer stockCount;
        
        // Getters and Setters
        public Integer getPortfolioId() { return portfolioId; }
        public void setPortfolioId(Integer portfolioId) { this.portfolioId = portfolioId; }
        
        public String getPortfolioName() { return portfolioName; }
        public void setPortfolioName(String portfolioName) { this.portfolioName = portfolioName; }
        
        public BigDecimal getInvestment() { return investment; }
        public void setInvestment(BigDecimal investment) { this.investment = investment; }
        
        public BigDecimal getCurrentValue() { return currentValue; }
        public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }
        
        public BigDecimal getProfitLoss() { return profitLoss; }
        public void setProfitLoss(BigDecimal profitLoss) { this.profitLoss = profitLoss; }
        
        public Double getProfitLossRate() { return profitLossRate; }
        public void setProfitLossRate(Double profitLossRate) { this.profitLossRate = profitLossRate; }
        
        public Integer getStockCount() { return stockCount; }
        public void setStockCount(Integer stockCount) { this.stockCount = stockCount; }
    }
    
    // Getters and Setters
    public Integer getTotalPortfolios() {
        return totalPortfolios;
    }
    
    public void setTotalPortfolios(Integer totalPortfolios) {
        this.totalPortfolios = totalPortfolios;
    }
    
    public Integer getTotalStocks() {
        return totalStocks;
    }
    
    public void setTotalStocks(Integer totalStocks) {
        this.totalStocks = totalStocks;
    }
    
    public Integer getTotalWatchlist() {
        return totalWatchlist;
    }
    
    public void setTotalWatchlist(Integer totalWatchlist) {
        this.totalWatchlist = totalWatchlist;
    }
    
    public BigDecimal getTotalInvestment() {
        return totalInvestment;
    }
    
    public void setTotalInvestment(BigDecimal totalInvestment) {
        this.totalInvestment = totalInvestment;
    }
    
    public BigDecimal getTotalCurrentValue() {
        return totalCurrentValue;
    }
    
    public void setTotalCurrentValue(BigDecimal totalCurrentValue) {
        this.totalCurrentValue = totalCurrentValue;
    }
    
    public BigDecimal getTotalProfitLoss() {
        return totalProfitLoss;
    }
    
    public void setTotalProfitLoss(BigDecimal totalProfitLoss) {
        this.totalProfitLoss = totalProfitLoss;
    }
    
    public Double getTotalProfitLossRate() {
        return totalProfitLossRate;
    }
    
    public void setTotalProfitLossRate(Double totalProfitLossRate) {
        this.totalProfitLossRate = totalProfitLossRate;
    }
    
    public List<PortfolioSummaryVO> getPortfolioSummaries() {
        return portfolioSummaries;
    }
    
    public void setPortfolioSummaries(List<PortfolioSummaryVO> portfolioSummaries) {
        this.portfolioSummaries = portfolioSummaries;
    }
    
    public List<PortfolioStockVO> getTopStocks() {
        return topStocks;
    }
    
    public void setTopStocks(List<PortfolioStockVO> topStocks) {
        this.topStocks = topStocks;
    }
    
    public List<NewsVO> getRecentNews() {
        return recentNews;
    }
    
    public void setRecentNews(List<NewsVO> recentNews) {
        this.recentNews = recentNews;
    }
}
