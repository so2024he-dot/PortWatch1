package com.portwatch.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

import lombok.Data;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * PortfolioStockVO - 수정 버전
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 수정:
 * - stockQuantity를 Integer -> BigDecimal로 변경
 * - 미국 주식 소수점 매수 지원 (예: 0.5주, 0.123주)
 * - DB: DECIMAL(15,4) 매핑
 * 
 * @author PortWatch
 * @version FIXED - 2026.01.16
 */
@Data
public class PortfolioStockVO {
    
    // 기본 정보
    private Integer portfolioStockId;
    private Integer portfolioId;
    private String stockCode;
    
    // ✅ 수량: Integer → BigDecimal 변경!
    private BigDecimal stockQuantity;
    
    private BigDecimal stockAvgPrice;
    private Timestamp stockAddDate;
    
    // JOIN용 추가 필드 (StockVO에서 가져온 정보)
    private String stockName;
    private String stockMarket;
    private String stockSector;
    private String country;  // 추가: 한국/미국 구분용
    private BigDecimal currentPrice; // 현재가 (STOCK_PRICE에서 조회)
    
    // 계산 필드
    private BigDecimal totalPurchaseAmount; // 매입 총액 = quantity * avgPrice
    private BigDecimal currentValue; // 현재 평가액 = quantity * currentPrice
    private BigDecimal profitLoss; // 손익 = currentValue - totalPurchaseAmount
    private Double profitLossRate; // 수익률 = (profitLoss / totalPurchaseAmount) * 100
    
    public PortfolioStockVO() {}
    
    // ===== Getters and Setters =====
    
    public Integer getPortfolioStockId() {
        return portfolioStockId;
    }
    
    public void setPortfolioStockId(Integer portfolioStockId) {
        this.portfolioStockId = portfolioStockId;
    }
    
    public Integer getPortfolioId() {
        return portfolioId;
    }
    
    public void setPortfolioId(Integer portfolioId) {
        this.portfolioId = portfolioId;
    }
    
    public String getStockCode() {
        return stockCode;
    }
    
    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }
    
    /**
     * ✅ 수량 Getter (BigDecimal)
     */
    public BigDecimal getStockQuantity() {
        return stockQuantity;
    }
    
    /**
     * ✅ 수량 Setter (BigDecimal)
     * 한국 주식: 정수 (1, 10, 100)
     * 미국 주식: 소수점 가능 (0.5, 0.123, 1.234)
     */
    public void setStockQuantity(BigDecimal stockQuantity) {
        this.stockQuantity = stockQuantity;
        calculateValues(); // 수량 변경 시 자동 계산
    }
    
    public BigDecimal getStockAvgPrice() {
        return stockAvgPrice;
    }
    
    public void setStockAvgPrice(BigDecimal stockAvgPrice) {
        this.stockAvgPrice = stockAvgPrice;
        calculateValues(); // 평균 가격 변경 시 자동 계산
    }
    
    public Timestamp getStockAddDate() {
        return stockAddDate;
    }
    
    public void setStockAddDate(Timestamp stockAddDate) {
        this.stockAddDate = stockAddDate;
    }
    
    public String getStockName() {
        return stockName;
    }
    
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
    
    public String getStockMarket() {
        return stockMarket;
    }
    
    public void setStockMarket(String stockMarket) {
        this.stockMarket = stockMarket;
    }
    
    public String getStockSector() {
        return stockSector;
    }
    
    public void setStockSector(String stockSector) {
        this.stockSector = stockSector;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
        calculateValues(); // 현재가 변경 시 자동 계산
    }
    
    public BigDecimal getTotalPurchaseAmount() {
        return totalPurchaseAmount;
    }
    
    public void setTotalPurchaseAmount(BigDecimal totalPurchaseAmount) {
        this.totalPurchaseAmount = totalPurchaseAmount;
    }
    
    public BigDecimal getCurrentValue() {
        return currentValue;
    }
    
    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }
    
    public BigDecimal getProfitLoss() {
        return profitLoss;
    }
    
    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }
    
    public Double getProfitLossRate() {
        return profitLossRate;
    }
    
    public void setProfitLossRate(Double profitLossRate) {
        this.profitLossRate = profitLossRate;
    }
    
    /**
     * ✅ 수익률 및 손익 자동 계산 (BigDecimal 버전)
     * 
     * 한국 주식: 정수 수량
     * 미국 주식: 소수점 수량
     */
    public void calculateValues() {
        if (stockQuantity != null && stockAvgPrice != null) {
            // 매입 총액 = 수량 × 평균 가격
            totalPurchaseAmount = stockAvgPrice.multiply(stockQuantity);
        }
        
        if (stockQuantity != null && currentPrice != null) {
            // 현재 평가액 = 수량 × 현재가
            currentValue = currentPrice.multiply(stockQuantity);
        }
        
        if (currentValue != null && totalPurchaseAmount != null && 
            totalPurchaseAmount.compareTo(BigDecimal.ZERO) != 0) {
            // 손익 = 현재 평가액 - 매입 총액
            profitLoss = currentValue.subtract(totalPurchaseAmount);
            
            // 수익률 = (손익 / 매입 총액) × 100
            profitLossRate = profitLoss.divide(totalPurchaseAmount, 2, RoundingMode.HALF_UP)
                                      .multiply(new BigDecimal(100))
                                      .doubleValue();
        }
    }
    
    /**
     * ✅ 한국 주식 여부 확인
     */
    public boolean isKoreanStock() {
        return "KR".equals(country);
    }
    
    /**
     * ✅ 미국 주식 여부 확인
     */
    public boolean isUSStock() {
        return "US".equals(country);
    }
    
    /**
     * ✅ 수량을 국가별 형식으로 포맷팅
     * 한국: 정수 (예: "10")
     * 미국: 소수점 3자리 (예: "10.500")
     */
    public String getFormattedQuantity() {
        if (stockQuantity == null) {
            return "0";
        }
        
        if (isKoreanStock()) {
            // 한국 주식: 정수만 표시
            return stockQuantity.setScale(0, RoundingMode.DOWN).toString();
        } else {
            // 미국 주식: 소수점 3자리까지 표시
            return stockQuantity.setScale(2, RoundingMode.HALF_UP).toString();
        }
    }
    
    @Override
    public String toString() {
        return "PortfolioStockVO{" +
                "portfolioStockId=" + portfolioStockId +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", country='" + country + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", stockAvgPrice=" + stockAvgPrice +
                ", currentPrice=" + currentPrice +
                ", totalPurchaseAmount=" + totalPurchaseAmount +
                ", currentValue=" + currentValue +
                ", profitLoss=" + profitLoss +
                ", profitLossRate=" + profitLossRate +
                '}';
    }
}
