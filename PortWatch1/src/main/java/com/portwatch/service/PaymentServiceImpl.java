package com.portwatch.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.PaymentVO;
import com.portwatch.persistence.PaymentDAO;

/**
 * ✅ 결제 Service 구현 (완전 구현)
 * 
 * 기능:
 * - 결제 처리
 * - 결제 승인
 * - 결제 취소
 * - 결제 조회
 * - 결제 내역 조회
 * - 결제 통계
 * 
 * @author PortWatch
 * @version 2.0 FINAL
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private PaymentDAO paymentDAO;
    
    /**
     * ✅ 결제 처리 (결제 생성)
     * 
     * 처리 순서:
     * 1. 필수 값 검증
     * 2. 결제 상태 PENDING 설정
     * 3. DB 저장
     * 4. 결제 ID 반환
     * 
     * @param payment 결제 정보
     * @return 결제 ID
     * @throws Exception
     */
    @Override
    @Transactional
    public Long processPayment(PaymentVO payment) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💳 결제 처리");
        System.out.println("  - 회원 ID: " + payment.getMemberId());
        System.out.println("  - 주식 ID: " + payment.getStockId());
        System.out.println("  - 수량: " + payment.getQuantity());
        System.out.println("  - 단가: " + payment.getPurchasePrice());
        System.out.println("  - 총 금액: " + payment.getTotalAmount());
        
        try {
            // 1. 필수 값 검증
            if (payment.getMemberId() == null) {
                throw new IllegalArgumentException("회원 ID는 필수입니다.");
            }
            
            if (payment.getStockId() == null) {
                throw new IllegalArgumentException("주식 ID는 필수입니다.");
            }
            
            if (payment.getQuantity() == null || payment.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
            }
            
            if (payment.getPurchasePrice() == null || payment.getPurchasePrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("구매 단가는 0보다 커야 합니다.");
            }
            
            // 2. 총 금액 계산 (수량 * 단가)
            BigDecimal totalAmount = payment.getQuantity()
                .multiply(payment.getPurchasePrice())
                .setScale(2, RoundingMode.HALF_UP);
            payment.setTotalAmount(totalAmount);
            
            // 3. 결제 상태 설정
            payment.setPaymentStatus("PENDING");
            
            // 4. 생성일시 설정
            payment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            
            // 5. DB 저장
            paymentDAO.insert(payment);
            
            System.out.println("  - 결제 ID: " + payment.getPaymentId());
            System.out.println("  - 결제 상태: PENDING");
            System.out.println("✅ 결제 처리 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return payment.getPaymentId();
            
        } catch (Exception e) {
            System.err.println("❌ 결제 처리 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("결제 처리 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 결제 승인 (PENDING → COMPLETED)
     * 
     * @param paymentId 결제 ID
     * @param transactionId 거래 번호
     * @throws Exception
     */
    @Override
    @Transactional
    public void approvePayment(Long paymentId, String transactionId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✅ 결제 승인");
        System.out.println("  - 결제 ID: " + paymentId);
        System.out.println("  - 거래번호: " + transactionId);
        
        try {
            // 1. 결제 조회
            PaymentVO payment = paymentDAO.selectById(paymentId);
            
            if (payment == null) {
                throw new Exception("존재하지 않는 결제입니다.");
            }
            
            // 2. 상태 확인
            if (!"PENDING".equals(payment.getPaymentStatus())) {
                throw new Exception("승인할 수 없는 상태입니다: " + payment.getPaymentStatus());
            }
            
            // 3. 결제 승인 처리
            payment.setPaymentStatus("COMPLETED");
            payment.setTransactionId(transactionId);
            payment.setCompletedAt(new Timestamp(System.currentTimeMillis()));
            
            // 4. DB 업데이트
            paymentDAO.update(payment);
            
            System.out.println("✅ 결제 승인 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 결제 승인 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("결제 승인 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 결제 취소 (PENDING/COMPLETED → CANCELLED)
     * 
     * @param paymentId 결제 ID
     * @throws Exception
     */
    @Override
    @Transactional
    public void cancelPayment(Long paymentId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("❌ 결제 취소");
        System.out.println("  - 결제 ID: " + paymentId);
        
        try {
            // 1. 결제 조회
            PaymentVO payment = paymentDAO.selectById(paymentId);
            
            if (payment == null) {
                throw new Exception("존재하지 않는 결제입니다.");
            }
            
            // 2. 상태 확인
            if ("CANCELLED".equals(payment.getPaymentStatus())) {
                throw new Exception("이미 취소된 결제입니다.");
            }
            
            // 3. 결제 취소 처리
            payment.setPaymentStatus("CANCELLED");
            payment.setCancelledAt(new Timestamp(System.currentTimeMillis()));
            
            // 4. DB 업데이트
            paymentDAO.update(payment);
            
            System.out.println("✅ 결제 취소 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 결제 취소 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("결제 취소 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 결제 조회 (ID로)
     * 
     * @param paymentId 결제 ID
     * @return 결제 정보
     * @throws Exception
     */
    @Override
    public PaymentVO getPayment(Long paymentId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 결제 조회");
        System.out.println("  - 결제 ID: " + paymentId);
        
        try {
            PaymentVO payment = paymentDAO.selectById(paymentId);
            
            if (payment != null) {
                System.out.println("✅ 결제 조회 성공");
                System.out.println("  - 상태: " + payment.getPaymentStatus());
                System.out.println("  - 금액: " + payment.getTotalAmount());
            } else {
                System.out.println("❌ 결제 없음");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return payment;
            
        } catch (Exception e) {
            System.err.println("❌ 결제 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("결제 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 결제 내역 조회
     * 
     * @param memberId 회원 ID (Integer)
     * @return 결제 내역 리스트
     * @throws Exception
     */
    @Override
    public List<PaymentVO> getPaymentHistory(Integer memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💳 결제 내역 조회");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            // DB에서 결제 내역 조회
            List<PaymentVO> paymentList = paymentDAO.selectByMemberId(String.valueOf(memberId));
            
            System.out.println("  - 결제 건수: " + paymentList.size());
            System.out.println("✅ 결제 내역 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return paymentList;
            
        } catch (Exception e) {
            System.err.println("❌ 결제 내역 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("결제 내역 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 결제 통계 조회
     * 
     * 통계 항목:
     * - totalPayments: 총 결제 건수
     * - totalAmount: 총 결제 금액
     * - completedPayments: 완료된 결제 건수
     * - pendingPayments: 대기 중 결제 건수
     * - cancelledPayments: 취소된 결제 건수
     * - avgPaymentAmount: 평균 결제 금액
     * - completionRate: 완료율 (%)
     * 
     * @param memberId 회원 ID (Integer)
     * @return 결제 통계 Map
     * @throws Exception
     */
    @Override
    public Map<String, Object> getPaymentSummary(Integer memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 결제 통계 조회");
        System.out.println("  - 회원 ID: " + memberId);
        
        Map<String, Object> summary = new HashMap<>();
        
        try {
            // 결제 내역 조회
            List<PaymentVO> paymentList = getPaymentHistory(memberId);
            
            // 통계 초기화
            int totalPayments = paymentList.size();
            BigDecimal totalAmount = BigDecimal.ZERO;
            int completedPayments = 0;
            int pendingPayments = 0;
            int cancelledPayments = 0;
            
            // 통계 계산
            for (PaymentVO payment : paymentList) {
                // 결제 금액 합계
                if (payment.getTotalAmount() != null) {
                    totalAmount = totalAmount.add(payment.getTotalAmount());
                }
                
                // 상태별 카운트
                String status = payment.getPaymentStatus();
                if ("COMPLETED".equals(status)) {
                    completedPayments++;
                } else if ("PENDING".equals(status)) {
                    pendingPayments++;
                } else if ("CANCELLED".equals(status)) {
                    cancelledPayments++;
                }
            }
            
            // 평균 결제 금액
            BigDecimal avgPaymentAmount = BigDecimal.ZERO;
            if (totalPayments > 0) {
                avgPaymentAmount = totalAmount.divide(
                    new BigDecimal(totalPayments), 
                    2, 
                    RoundingMode.HALF_UP
                );
            }
            
            // 완료율 계산
            BigDecimal completionRate = BigDecimal.ZERO;
            if (totalPayments > 0) {
                completionRate = new BigDecimal(completedPayments)
                    .divide(new BigDecimal(totalPayments), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
            }
            
            // 결과 Map 구성
            summary.put("totalPayments", totalPayments);
            summary.put("totalAmount", totalAmount);
            summary.put("completedPayments", completedPayments);
            summary.put("pendingPayments", pendingPayments);
            summary.put("cancelledPayments", cancelledPayments);
            summary.put("avgPaymentAmount", avgPaymentAmount);
            summary.put("completionRate", completionRate);
            
            // 로그 출력
            System.out.println("  - 총 결제 건수: " + totalPayments);
            System.out.println("  - 총 결제 금액: " + totalAmount);
            System.out.println("  - 완료 건수: " + completedPayments);
            System.out.println("  - 대기 건수: " + pendingPayments);
            System.out.println("  - 취소 건수: " + cancelledPayments);
            System.out.println("  - 평균 결제 금액: " + avgPaymentAmount);
            System.out.println("  - 완료율: " + completionRate + "%");
            System.out.println("✅ 결제 통계 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return summary;
            
        } catch (Exception e) {
            System.err.println("❌ 결제 통계 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("결제 통계 조회 실패: " + e.getMessage(), e);
        }
    }

	@Override
	public List<PaymentVO> getPaymentHistory(String memberId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getPaymentSummary(String memberId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
