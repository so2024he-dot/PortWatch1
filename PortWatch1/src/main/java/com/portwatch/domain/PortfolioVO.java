package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * PortfolioVO - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ 콘솔 오류 해결: setter 메서드 추가
 * - setPortfolioName (라인 156)
 * - setTotalInvestment (라인 305)
 * - setTotalCurrentValue (라인 306)
 * - setTotalProfitLoss (라인 307)
 * - setTotalProfitRate (라인 308)
 * ══════════════════════════════════════════════════════════════
 */
@Data
public class PortfolioVO {
    // 기본 정보
    private Long portfolioId;
    private String memberId;
    private String portfolioName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // 종목 정보 (JOIN 결과)
    private String stockId;
    private String stockCode;
    private String stockName;
    private BigDecimal quantity;
    private BigDecimal avgPrice;
    private BigDecimal avgPurchasePrice;
    private BigDecimal purchasePrice;
    private BigDecimal currentPrice;
    
    // 요약 통계
    private BigDecimal totalInvestment;
    private BigDecimal totalCurrentValue;
    private BigDecimal totalProfitLoss;
    private BigDecimal totalProfitRate;
}
