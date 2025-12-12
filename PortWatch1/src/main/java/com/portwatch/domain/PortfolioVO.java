package com.portwatch.domain;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * 포트폴리오 VO (분할 매수 지원)
 * 
 * ✅ 수정 사항:
 * - createdAt 필드 제거 (DB 컬럼 없음)
 * - quantity: BigDecimal (0.01주 단위 지원)
 * - industry 필드 추가
 * 
 * @version 3.2 (환원 버전 수정)
 */
public class PortfolioVO {
    
    // 기본 정보
    private Long portfolioId;
    private Integer memberId;
    private Integer stockId;
    
    // ✅ 수량: BigDecimal (분할 매수 지원)
    @NotNull(message = "보유 수량은 필수 입력 항목입니다.")
    @DecimalMin(value = "0.01", message = "수량은 0.01 이상이어야 합니다.")
    private BigDecimal quantity;
    
    @NotNull(message = "평균 매입가는 필수 입력 항목입니다.")
    @DecimalMin(value = "0.01", message = "평균 매입가는 0보다 커야 합니다.")
    private BigDecimal avgPurchasePrice;
    
    private Date purchaseDate;
    private Timestamp updatedAt;  // ✅ created_at 제거, updated_at만 사용
    
    // 조인 정보 (STOCK 테이블)
    private String stockCode;
    private String stockName;
    private String marketType;
    private String industry;  // ✅ 추가
    
    // 주가 정보 (런타임에 설정)
    private BigDecimal currentPrice;
    
    // 계산 필드
    private BigDecimal totalPurchaseAmount;
    private BigDecimal totalCurrentValue;
    private BigDecimal profit;
    private BigDecimal profitRate;
    
    // 기본 생성자
    public PortfolioVO() {}
    
    // Getters and Setters
    public Long getPortfolioId() {
        return portfolioId;
    }
    
    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }
    
    public Integer getMemberId() {
        return memberId;
    }
    
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
    
    public Integer getStockId() {
        return stockId;
    }
    
    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getAvgPurchasePrice() {
        return avgPurchasePrice;
    }
    
    public void setAvgPurchasePrice(BigDecimal avgPurchasePrice) {
        this.avgPurchasePrice = avgPurchasePrice;
    }
    
    public Date getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
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
    
    // ✅ 계산 필드 Getters
    public BigDecimal getTotalPurchaseAmount() {
        if (quantity != null && avgPurchasePrice != null) {
            return avgPurchasePrice.multiply(quantity)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalCurrentValue() {
        if (quantity != null && currentPrice != null) {
            return currentPrice.multiply(quantity)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getProfit() {
        return getTotalCurrentValue().subtract(getTotalPurchaseAmount());
    }
    
    public BigDecimal getProfitRate() {
        BigDecimal purchaseAmount = getTotalPurchaseAmount();
        if (purchaseAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (currentPrice == null) {
            return BigDecimal.ZERO;
        }
        
        return currentPrice.subtract(avgPurchasePrice)
                .divide(avgPurchasePrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
    }
    
    // ✅ 시장 아이콘 (편의 메서드)
    public String getMarketIcon() {
        if (marketType == null) return "❓";
        switch (marketType.toUpperCase()) {
            case "KOSPI":
            case "KOSDAQ":
                return "🇰🇷";
            case "NASDAQ":
            case "NYSE":
            case "AMEX":
                return "🇺🇸";
            default:
                return "🌐";
        }
    }
    
    // ✅ 업종 아이콘 (편의 메서드)
    public String getIndustryIcon() {
        if (industry == null) return "📊";
        String industryLower = industry.toLowerCase();
        
        if (industryLower.contains("반도체") || industryLower.contains("semiconductor")) {
            return "💾";
        } else if (industryLower.contains("바이오") || industryLower.contains("bio") || 
                   industryLower.contains("healthcare") || industryLower.contains("의약")) {
            return "💊";
        } else if (industryLower.contains("전지") || industryLower.contains("battery")) {
            return "🔋";
        } else if (industryLower.contains("자동차") || industryLower.contains("automotive")) {
            return "🚗";
        } else if (industryLower.contains("금융") || industryLower.contains("financial")) {
            return "💰";
        } else if (industryLower.contains("tech") || industryLower.contains("소프트웨어")) {
            return "💻";
        } else {
            return "📊";
        }
    }
    
    @Override
    public String toString() {
        return "PortfolioVO{" +
                "portfolioId=" + portfolioId +
                ", memberId=" + memberId +
                ", stockId=" + stockId +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", quantity=" + quantity +
                ", avgPurchasePrice=" + avgPurchasePrice +
                ", currentPrice=" + currentPrice +
                ", profit=" + getProfit() +
                ", profitRate=" + getProfitRate() +
                '}';
    }
}
