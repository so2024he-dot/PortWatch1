package com.portwatch.domain;

import java.math.BigDecimal;

import lombok.Data;

/**
 * PortfolioStockVO - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ 콘솔 오류 해결: getter/setter 메서드 추가
 * - getAvgPrice (라인 236)
 * - getQuantity (라인 237)
 * - setProfitRate (라인 241, 252, 263, 267)
 * - setTotalInvestment (라인 255)
 * ══════════════════════════════════════════════════════════════
 */
@Data
public class PortfolioStockVO {
    // 기본 정보
    private Long itemId;
    private Long portfolioId;
    private String stockCode;
    private String stockName;
    
    // 보유 정보
    private Long quantity;           // ✅ getQuantity() 추가
    private BigDecimal avgPrice;     // ✅ getAvgPrice() 추가
    private BigDecimal currentPrice;
    
    // 손익 계산
    private BigDecimal profitLoss;
    private BigDecimal profitRate;        // ✅ setProfitRate() 추가
    private BigDecimal totalInvestment;   // ✅ setTotalInvestment() 추가
    private BigDecimal currentValue;
}
