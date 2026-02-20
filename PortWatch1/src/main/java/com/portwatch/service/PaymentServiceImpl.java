package com.portwatch.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.PaymentVO;
import com.portwatch.mapper.PaymentMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * PaymentServiceImpl - 신규
 * ══════════════════════════════════════════════════════════════
 * ✅ 결제 처리 완전 구현
 * ✅ Toss Payments (한국) + Stripe (미국) 연동 준비
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    /**
     * ✅ 결제 처리
     */
    @Override
    @Transactional
    public Long processPayment(PaymentVO payment) {
        try {
            log.info("결제 처리 시작: memberId={}, stockCode={}, amount={}", 
                     payment.getMemberId(), payment.getStockCode(), payment.getTotalAmount());

            // 결제 상태 초기화
            payment.setStatus("PENDING");
            payment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            
            // 결제 등록
            int result = paymentMapper.insert(payment);
            
            if (result > 0) {
                log.info("결제 등록 성공: paymentId={}", payment.getPaymentId());
                
                // TODO: PG사 결제 요청 (Toss Payments / Stripe)
                // 실제 운영 시 PG사 API 호출
                
                return payment.getPaymentId();
            } else {
                log.error("결제 등록 실패");
                throw new RuntimeException("결제 등록 실패");
            }
            
        } catch (Exception e) {
            log.error("결제 처리 실패", e);
            throw new RuntimeException("결제 처리 실패: " + e.getMessage());
        }
    }

    /**
     * ✅ 결제 내역 조회
     */
    @Override
    public List<PaymentVO> getPaymentHistory(String memberId) {
        try {
            return paymentMapper.findByMemberId(memberId);
        } catch (Exception e) {
            log.error("결제 내역 조회 실패 (memberId={}): {}", memberId, e.getMessage());
            return null;
        }
    }

    /**
     * ✅ 결제 요약 정보
     */
    @Override
    public Map<String, Object> getPaymentSummary(String memberId) {
        try {
            Map<String, Object> summary = new HashMap<>();
            
            // 총 결제 금액
            BigDecimal totalAmount = paymentMapper.getTotalAmount(memberId);
            if (totalAmount == null) totalAmount = BigDecimal.ZERO;
            
            // 결제 건수
            int count = paymentMapper.countByMemberId(memberId);
            
            // 최근 결제
            List<PaymentVO> recentPayments = paymentMapper.findRecent(memberId, 5);
            
            summary.put("totalAmount", totalAmount);
            summary.put("count", count);
            summary.put("recentPayments", recentPayments);
            
            return summary;
            
        } catch (Exception e) {
            log.error("결제 요약 조회 실패 (memberId={}): {}", memberId, e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * ✅ 결제 취소
     */
    @Override
    @Transactional
    public void cancelPayment(Long paymentId) {
        try {
            log.info("결제 취소 시작: paymentId={}", paymentId);
            
            // 결제 정보 조회
            PaymentVO payment = paymentMapper.findById(paymentId);
            if (payment == null) {
                throw new RuntimeException("결제 정보를 찾을 수 없습니다.");
            }
            
            // 취소 가능 상태 확인
            if (!"COMPLETED".equals(payment.getStatus()) && !"PENDING".equals(payment.getStatus())) {
                throw new RuntimeException("취소할 수 없는 상태입니다: " + payment.getStatus());
            }
            
            // 결제 상태 업데이트
            int result = paymentMapper.updateStatus(paymentId, "CANCELLED");
            
            if (result > 0) {
                log.info("결제 취소 완료: paymentId={}", paymentId);
                
                // TODO: PG사 취소 요청
                
            } else {
                throw new RuntimeException("결제 취소 실패");
            }
            
        } catch (Exception e) {
            log.error("결제 취소 실패 (paymentId={}): {}", paymentId, e.getMessage());
            throw new RuntimeException("결제 취소 실패: " + e.getMessage());
        }
    }

    /**
     * ✅ 결제 승인
     */
    @Override
    @Transactional
    public void approvePayment(Long paymentId, String transactionId) {
        try {
            log.info("결제 승인 시작: paymentId={}, transactionId={}", paymentId, transactionId);
            
            // 거래 ID 업데이트
            int result1 = paymentMapper.updateTransactionId(paymentId, transactionId);
            
            // 결제 상태 업데이트
            int result2 = paymentMapper.updateStatus(paymentId, "COMPLETED");
            
            if (result1 > 0 && result2 > 0) {
                log.info("결제 승인 완료: paymentId={}", paymentId);
            } else {
                throw new RuntimeException("결제 승인 실패");
            }
            
        } catch (Exception e) {
            log.error("결제 승인 실패 (paymentId={}): {}", paymentId, e.getMessage());
            throw new RuntimeException("결제 승인 실패: " + e.getMessage());
        }
    }

    /**
     * 결제 단건 조회
     */
    @Override
    public PaymentVO getPaymentById(Long paymentId) {
        try {
            return paymentMapper.findById(paymentId);
        } catch (Exception e) {
            log.error("결제 조회 실패 (paymentId={}): {}", paymentId, e.getMessage());
            return null;
        }
    }

    /**
     * 결제 상태 업데이트
     */
    @Override
    @Transactional
    public int updatePaymentStatus(Long paymentId, String status) {
        try {
            return paymentMapper.updateStatus(paymentId, status);
        } catch (Exception e) {
            log.error("결제 상태 업데이트 실패 (paymentId={}): {}", paymentId, e.getMessage());
            return 0;
        }
    }

    /**
     * 기간별 결제 내역
     */
    @Override
    public List<PaymentVO> getPaymentsByDateRange(String memberId, String startDate, String endDate) {
        try {
            return paymentMapper.findByDateRange(memberId, startDate, endDate);
        } catch (Exception e) {
            log.error("기간별 결제 조회 실패 (memberId={}): {}", memberId, e.getMessage());
            return null;
        }
    }

    /**
     * 총 결제 금액
     */
    @Override
    public BigDecimal getTotalPaymentAmount(String memberId) {
        try {
            BigDecimal total = paymentMapper.getTotalAmount(memberId);
            return total != null ? total : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("총 결제액 조회 실패 (memberId={}): {}", memberId, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 결제 건수
     */
    @Override
    public int countPayments(String memberId) {
        try {
            return paymentMapper.countByMemberId(memberId);
        } catch (Exception e) {
            log.error("결제 건수 조회 실패 (memberId={}): {}", memberId, e.getMessage());
            return 0;
        }
    }
}
