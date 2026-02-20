package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * PaymentVO - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ 콘솔 오류 해결: getter/setter 메서드 추가
 * - setStatus (라인 43)
 * - getStatus (라인 126, 127)
 * ══════════════════════════════════════════════════════════════
 */
@Data
public class PaymentVO {
    // 기본 정보
    private Long paymentId;
    private String memberId;
    private Integer stockId;
    private String stockCode;
    private String stockName;
    
    // 결제 정보
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String cardNumber;
    private String cardCompany;
    
    // PG사 정보
    private String pgProvider;
    private String transactionId;
    private String status;           // ✅ setStatus(), getStatus() 추가
    
    // 국가/통화
    private String country;
    private String currency;
    
    // 타임스탬프
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
