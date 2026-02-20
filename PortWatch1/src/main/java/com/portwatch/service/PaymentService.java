package com.portwatch.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.portwatch.domain.PaymentVO;

/**
 * PaymentService Interface - 신규
 * ══════════════════════════════════════════════════════════════
 * ✅ PaymentController 호출 메서드 100% 커버
 * 
 * 총 10개 메서드
 * ══════════════════════════════════════════════════════════════
 */
public interface PaymentService {

    /**
     * ✅ 결제 처리
     * PaymentController 라인 95 호출
     */
    Long processPayment(PaymentVO payment);

    /**
     * ✅ 결제 내역 조회
     * PaymentController 라인 114 호출
     */
    List<PaymentVO> getPaymentHistory(String memberId);

    /**
     * ✅ 결제 요약 정보
     * PaymentController 라인 115 호출
     */
    Map<String, Object> getPaymentSummary(String memberId);

    /**
     * ✅ 결제 취소
     * PaymentController 라인 141 호출
     */
    void cancelPayment(Long paymentId);

    /**
     * ✅ 결제 승인
     * PaymentController 라인 167 호출
     */
    void approvePayment(Long paymentId, String transactionId);

    /**
     * 결제 단건 조회
     */
    PaymentVO getPaymentById(Long paymentId);

    /**
     * 결제 상태 업데이트
     */
    int updatePaymentStatus(Long paymentId, String status);

    /**
     * 기간별 결제 내역
     */
    List<PaymentVO> getPaymentsByDateRange(String memberId, String startDate, String endDate);

    /**
     * 총 결제 금액
     */
    BigDecimal getTotalPaymentAmount(String memberId);

    /**
     * 결제 건수
     */
    int countPayments(String memberId);
}
