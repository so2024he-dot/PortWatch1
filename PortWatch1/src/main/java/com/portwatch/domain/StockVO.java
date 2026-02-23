package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * StockVO - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ 오류 해결: stockId 필드 추가 (라인 174)
 * - setStockId() 메서드 자동 생성
 * ══════════════════════════════════════════════════════════════
 */
@Data
public class StockVO {
    // ✅ stockId 추가 (컴파일 오류 해결)
    private Integer stockId;
    
    // 기본 정보
    private String crawlStockId;
    private String stockCode;
    private String stockName;
    private String country;
    private String market;
    private String marketType;
    private String industry;
    
    // 가격 정보
    private BigDecimal currentPrice;
    private BigDecimal previousClose;
    private BigDecimal changeAmount;
    private BigDecimal changeRate;
    
    // 거래 정보
    private Long volume;
    private Long tradingVolume;
    private BigDecimal marketCap;
    
    // 타임스탬프
    private Timestamp updatedAt;
    private Timestamp createdAt;
}
