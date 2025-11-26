#!/bin/bash

BASE="/home/claude/portwatch-spring507/src/main/java/com/portwatch"

# ======================
# Domain (VO with Validation)
# ======================

cat > "$BASE/domain/MemberVO.java" << 'EOF'
package com.portwatch.domain;

import javax.validation.constraints.*;
import java.sql.Timestamp;

public class MemberVO {
    
    private int memberId;
    
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String memberEmail;
    
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String memberPass;
    
    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
    private String memberName;
    
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (010-0000-0000)")
    private String memberPhone;
    
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public MemberVO() {}
    
    // Getters and Setters
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    
    public String getMemberEmail() { return memberEmail; }
    public void setMemberEmail(String memberEmail) { this.memberEmail = memberEmail; }
    
    public String getMemberPass() { return memberPass; }
    public void setMemberPass(String memberPass) { this.memberPass = memberPass; }
    
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    
    public String getMemberPhone() { return memberPhone; }
    public void setMemberPhone(String memberPhone) { this.memberPhone = memberPhone; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "MemberVO{memberId=" + memberId + ", memberEmail='" + memberEmail + "', memberName='" + memberName + "'}";
    }
}
EOF

cat > "$BASE/domain/StockVO.java" << 'EOF'
package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class StockVO {
    private int stockId;
    private String stockCode;
    private String stockName;
    private String marketType;
    private String industry;
    private Timestamp updatedAt;
    
    // 추가 필드 (조회용)
    private BigDecimal currentPrice;
    private BigDecimal changeRate;
    private Long volume;
    
    public StockVO() {}
    
    // Getters and Setters
    public int getStockId() { return stockId; }
    public void setStockId(int stockId) { this.stockId = stockId; }
    
    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }
    
    public String getStockName() { return stockName; }
    public void setStockName(String stockName) { this.stockName = stockName; }
    
    public String getMarketType() { return marketType; }
    public void setMarketType(String marketType) { this.marketType = marketType; }
    
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    
    public BigDecimal getChangeRate() { return changeRate; }
    public void setChangeRate(BigDecimal changeRate) { this.changeRate = changeRate; }
    
    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }
}
EOF

cat > "$BASE/domain/PortfolioVO.java" << 'EOF'
package com.portwatch.domain;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class PortfolioVO {
    private long portfolioId;
    private int memberId;
    
    @NotNull(message = "종목을 선택해주세요.")
    private Integer stockId;
    
    @NotNull(message = "보유수량을 입력해주세요.")
    @Min(value = 1, message = "보유수량은 1 이상이어야 합니다.")
    private Integer quantity;
    
    @NotNull(message = "평균매수가를 입력해주세요.")
    @DecimalMin(value = "0.01", message = "평균매수가는 0보다 커야 합니다.")
    private BigDecimal avgPurchasePrice;
    
    private Date purchaseDate;
    private Timestamp updatedAt;
    
    // JOIN 필드
    private String stockCode;
    private String stockName;
    private BigDecimal currentPrice;
    
    public PortfolioVO() {}
    
    // Getters and Setters
    public long getPortfolioId() { return portfolioId; }
    public void setPortfolioId(long portfolioId) { this.portfolioId = portfolioId; }
    
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    
    public Integer getStockId() { return stockId; }
    public void setStockId(Integer stockId) { this.stockId = stockId; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getAvgPurchasePrice() { return avgPurchasePrice; }
    public void setAvgPurchasePrice(BigDecimal avgPurchasePrice) { this.avgPurchasePrice = avgPurchasePrice; }
    
    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }
    
    public String getStockName() { return stockName; }
    public void setStockName(String stockName) { this.stockName = stockName; }
    
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    
    // 계산 메서드
    public BigDecimal getPurchaseAmount() {
        if (avgPurchasePrice == null || quantity == null) return BigDecimal.ZERO;
        return avgPurchasePrice.multiply(new BigDecimal(quantity));
    }
    
    public BigDecimal getCurrentAmount() {
        if (currentPrice == null || quantity == null) return BigDecimal.ZERO;
        return currentPrice.multiply(new BigDecimal(quantity));
    }
    
    public BigDecimal getProfitLoss() {
        if (currentPrice == null || avgPurchasePrice == null || quantity == null) return BigDecimal.ZERO;
        return currentPrice.subtract(avgPurchasePrice).multiply(new BigDecimal(quantity));
    }
    
    public BigDecimal getProfitRate() {
        if (avgPurchasePrice == null || avgPurchasePrice.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        if (currentPrice == null) return BigDecimal.ZERO;
        return currentPrice.subtract(avgPurchasePrice)
                .divide(avgPurchasePrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(100));
    }
}
EOF

echo "Domain classes created"
