package com.portwatch.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.portwatch.domain.PaymentVO;

/**
 * PaymentMapper Interface - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ 콘솔 오류 해결: 메서드 추가
 * - findByMemberId (라인 73)
 * - getTotalAmount (라인 83, 93, 221)
 * - findRecent (라인 96)
 * - updateStatus (라인 131, 161, 195)
 * - updateTransactionId (라인 158)
 * - findByDateRange (라인 208)
 * - countByMemberId (라인 235)
 * ══════════════════════════════════════════════════════════════
 */
public interface PaymentMapper {

    // ✅ 기본 CRUD
    int insert(PaymentVO payment);
    PaymentVO findById(Long paymentId);
    List<PaymentVO> findByMemberId(String memberId);
    int delete(Long paymentId);

    // ✅ 상태 업데이트
    int updateStatus(@Param("paymentId") Long paymentId, @Param("status") String status);
    int updateTransactionId(@Param("paymentId") Long paymentId, @Param("transactionId") String transactionId);

    // ✅ 조회
    List<PaymentVO> findByDateRange(@Param("memberId") String memberId, @Param("startDate") String startDate, @Param("endDate") String endDate);
    List<PaymentVO> findRecent(@Param("memberId") String memberId, @Param("limit") int limit);
    List<PaymentVO> findByStatus(@Param("memberId") String memberId, @Param("status") String status);
    List<PaymentVO> findByStockCode(String stockCode);
    List<PaymentVO> findByCountry(@Param("memberId") String memberId, @Param("country") String country);

    // ✅ 통계
    BigDecimal getTotalAmount(String memberId);
    int countByMemberId(String memberId);
    int countByStatus(@Param("memberId") String memberId, @Param("status") String status);
    Map<String, Object> getPaymentSummary(String memberId);
}
