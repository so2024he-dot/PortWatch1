package com.portwatch.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Dashboard 요약 정보 VO
 */
public class DashboardVO {
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 포트폴리오 통계
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private int portfolioCount;          // 포트폴리오 수
    private BigDecimal totalValue;       // 총 평가액
    private BigDecimal totalInvestment;  // 총 투자원금
    private BigDecimal totalProfit;      // ⭐ 총 손익 (필수!)
    private BigDecimal returnRate;       // 수익률
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 주식 통계
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private int stockCount;              // 보유 종목 수
    private int koreanStockCount;        // 한국 종목 수
    private int usStockCount;            // 미국 종목 수
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 상위 종목
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private List<PortfolioStockVO> topGainers;    // 수익 상위 종목
    private List<PortfolioStockVO> topLosers;     // 손실 상위 종목
    private List<PortfolioStockVO> topHoldings;   // 보유액 상위 종목
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 최근 뉴스
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private List<NewsVO> recentNews;     // 최근 뉴스
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Constructors
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    public DashboardVO() {
        this.totalValue = BigDecimal.ZERO;
        this.totalInvestment = BigDecimal.ZERO;
        this.totalProfit = BigDecimal.ZERO;        // ⭐ 초기화!
        this.returnRate = BigDecimal.ZERO;
        this.topGainers = new ArrayList<>();
        this.topLosers = new ArrayList<>();
        this.topHoldings = new ArrayList<>();
        this.recentNews = new ArrayList<>();
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Getters and Setters
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    public int getPortfolioCount() {
        return portfolioCount;
    }
    
    public void setPortfolioCount(int portfolioCount) {
        this.portfolioCount = portfolioCount;
    }
    
    public BigDecimal getTotalValue() {
        return totalValue;
    }
    
    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
    
    public BigDecimal getTotalInvestment() {
        return totalInvestment;
    }
    
    public void setTotalInvestment(BigDecimal totalInvestment) {
        this.totalInvestment = totalInvestment;
    }
    
    // ⭐ totalProfit Getter/Setter (필수!)
    public BigDecimal getTotalProfit() {
        return totalProfit;
    }
    
    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }
    
    public BigDecimal getReturnRate() {
        return returnRate;
    }
    
    public void setReturnRate(BigDecimal returnRate) {
        this.returnRate = returnRate;
    }
    
    public int getStockCount() {
        return stockCount;
    }
    
    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }
    
    public int getKoreanStockCount() {
        return koreanStockCount;
    }
    
    public void setKoreanStockCount(int koreanStockCount) {
        this.koreanStockCount = koreanStockCount;
    }
    
    public int getUsStockCount() {
        return usStockCount;
    }
    
    public void setUsStockCount(int usStockCount) {
        this.usStockCount = usStockCount;
    }
    
    public List<PortfolioStockVO> getTopGainers() {
        return topGainers;
    }
    
    public void setTopGainers(List<PortfolioStockVO> topGainers) {
        this.topGainers = topGainers;
    }
    
    public List<PortfolioStockVO> getTopLosers() {
        return topLosers;
    }
    
    public void setTopLosers(List<PortfolioStockVO> topLosers) {
        this.topLosers = topLosers;
    }
    
    public List<PortfolioStockVO> getTopHoldings() {
        return topHoldings;
    }
    
    public void setTopHoldings(List<PortfolioStockVO> topHoldings) {
        this.topHoldings = topHoldings;
    }
    
    public List<NewsVO> getRecentNews() {
        return recentNews;
    }
    
    public void setRecentNews(List<NewsVO> recentNews) {
        this.recentNews = recentNews;
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Utility Methods
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * 수익률 계산 (RoundingMode 사용)
     */
    public void calculateReturnRate() {
        if (totalInvestment == null || totalInvestment.compareTo(BigDecimal.ZERO) == 0) {
            this.returnRate = BigDecimal.ZERO;
            return;
        }
        
        // ⭐ RoundingMode 사용 (Deprecated 해결!)
        this.returnRate = totalProfit
            .divide(totalInvestment, 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal(100))
            .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * 총 손익 계산
     */
    public void calculateTotalProfit() {
        if (totalValue != null && totalInvestment != null) {
            this.totalProfit = totalValue.subtract(totalInvestment);
        } else {
            this.totalProfit = BigDecimal.ZERO;
        }
    }
    
    /**
     * 통계 자동 계산
     */
    public void calculateAll() {
        calculateTotalProfit();
        calculateReturnRate();
    }
    
    /**
     * 수익 여부
     */
    public boolean isProfit() {
        return totalProfit != null && totalProfit.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 손실 여부
     */
    public boolean isLoss() {
        return totalProfit != null && totalProfit.compareTo(BigDecimal.ZERO) < 0;
    }
    
    @Override
    public String toString() {
        return "DashboardVO{" +
                "portfolioCount=" + portfolioCount +
                ", totalValue=" + totalValue +
                ", totalInvestment=" + totalInvestment +
                ", totalProfit=" + totalProfit +
                ", returnRate=" + returnRate +
                ", stockCount=" + stockCount +
                '}';
    }
}
