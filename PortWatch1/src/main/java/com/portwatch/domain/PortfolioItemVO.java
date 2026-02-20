package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * PortfolioItemVO - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ 콘솔 오류 해결: setter 메서드 추가
 * - setCreatedAt (라인 127)
 * - setPortfolioId (라인 180)
 * - setAvgPrice (라인 183)
 * ══════════════════════════════════════════════════════════════
 */
@Data
public class PortfolioItemVO {
    private Long itemId;
    private Long portfolioId;
    private String stockCode;
    private BigDecimal quantity;
    private BigDecimal avgPrice;
    private BigDecimal purchasePrice;
    private Timestamp createdAt;
}
