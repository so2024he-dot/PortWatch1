package com.portwatch.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.PaymentVO;
import com.portwatch.persistence.PaymentDAO;

/**
 * ✅ 결제 Service 구현 클래스 - 완전 구현
 * 
 * @author PortWatch
 * @version FINAL COMPLETE - Spring 5.0.7 + MySQL 8.0.33
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private PaymentDAO paymentDAO;
    
    /**
     * ✅ 결제 내역 조회 (String memberId)
     */
    @Override
    public List<PaymentVO> getPaymentHistory(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💳 결제 내역 조회 (String)");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            List<PaymentVO> paymentList = paymentDAO.selectByMemberId(memberId);
            
            if (paymentList == null) {
                paymentList = new ArrayList<>();
            }
            
            // 최신순 정렬
            paymentList.sort((p1, p2) -> {
                if (p2.getCreatedAt() == null) return -1;
                if (p1.getCreatedAt() == null) return 1;
                return p2.getCreatedAt().compareTo(p1.getCreatedAt());
            });
            
            System.out.println("  - 결제 내역: " + paymentList.size() + "건");
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
     * ✅ 결제 내역 조회 (Integer memberId) - 추가!
     */
    @Override
    public List<PaymentVO> getPaymentHistory(Integer memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💳 결제 내역 조회 (Integer)");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            // Integer를 String으로 변환하여 호출
            String memberIdStr = String.valueOf(memberId);
            return getPaymentHistory(memberIdStr);
            
        } catch (Exception e) {
            System.err.println("❌ 결제 내역 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("결제 내역 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 결제 요약 정보 조회 (String memberId)
     */
    @Override
    public Map<String, Object> getPaymentSummary(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 결제 요약 정보 조회");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            List<PaymentVO> allPayments = getPaymentHistory(memberId);
            
            int totalCount = allPayments.size();
            int successCount = 0;
            int failedCount = 0;
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (PaymentVO payment : allPayments) {
                if ("SUCCESS".equals(payment.getPaymentStatus())) {
                    successCount++;
                    if (payment.getAmount() != null) {
                        totalAmount = totalAmount.add(payment.getAmount());
                    }
                } else if ("FAILED".equals(payment.getPaymentStatus())) {
                    failedCount++;
                }
            }
            
            List<PaymentVO> recentPayments = allPayments.stream()
                .limit(5)
                .collect(Collectors.toList());
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalCount", totalCount);
            summary.put("successCount", successCount);
            summary.put("failedCount", failedCount);
            summary.put("totalAmount", totalAmount);
            summary.put("recentPayments", recentPayments);
            
            System.out.println("  - 전체 건수: " + totalCount);
            System.out.println("  - 성공: " + successCount);
            System.out.println("  - 실패: " + failedCount);
            System.out.println("  - 총 금액: " + totalAmount);
            System.out.println("✅ 결제 요약 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return summary;
            
        } catch (Exception e) {
            System.err.println("❌ 결제 요약 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("결제 요약 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 결제 요약 정보 조회 (Integer memberId) - 추가!
     */
    @Override
    public Map<String, Object> getPaymentSummary(Integer memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 결제 요약 정보 조회 (Integer)");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            // Integer를 String으로 변환하여 호출
            String memberIdStr = String.valueOf(memberId);
            return getPaymentSummary(memberIdStr);
            
        } catch (Exception e) {
            System.err.println("❌ 결제 요약 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("결제 요약 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 결제 처리 - 추가!
     */
    @Override
    @Transactional
    public Long processPayment(PaymentVO payment) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💳 결제 처리");
        System.out.println("  - 회원 ID: " + payment.getMemberId());
        System.out.println("  - 금액: " + payment.getAmount());
        
        try {
            // 결제 상태를 PENDING으로 설정
            payment.setPaymentStatus("PENDING");
            
            // 결제 정보 저장
            paymentDAO.insertPayment(payment);
            
            // 저장된 결제 ID 반환
            Long paymentId = payment.getPaymentId();
            
            System.out.println("✅ 결제 처리 완료");
            System.out.println("  - 결제 ID: " + paymentId);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return paymentId;
            
        } catch (Exception e) {
            System.err.println("❌ 결제 처리 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("결제 처리 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 결제 승인 - 추가!
     */
    @Override
    @Transactional
    public void approvePayment(Long paymentId, String transactionId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✅ 결제 승인");
        System.out.println("  - 결제 ID: " + paymentId);
        System.out.println("  - 거래 ID: " + transactionId);
        
        try {
            // 결제 조회
            PaymentVO payment = paymentDAO.selectPaymentById(paymentId);
            
            if (payment == null) {
                throw new Exception("결제 정보를 찾을 수 없습니다");
            }
            
            // 결제 상태를 SUCCESS로 업데이트
            paymentDAO.updatePaymentStatus(paymentId, "SUCCESS");
            
            System.out.println("✅ 결제 승인 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 결제 승인 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("결제 승인 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 결제 조회 - 추가!
     */
    @Override
    public PaymentVO getPayment(Long paymentId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 결제 조회");
        System.out.println("  - 결제 ID: " + paymentId);
        
        try {
            PaymentVO payment = paymentDAO.selectPaymentById(paymentId);
            
            if (payment != null) {
                System.out.println("✅ 결제 조회 완료");
                System.out.println("  - 금액: " + payment.getAmount());
                System.out.println("  - 상태: " + payment.getPaymentStatus());
            } else {
                System.out.println("⚠️ 결제를 찾을 수 없습니다");
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
     * ✅ 결제 생성
     */
    @Override
    @Transactional
    public void createPayment(PaymentVO payment) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💳 결제 생성");
        System.out.println("  - 회원 ID: " + payment.getMemberId());
        System.out.println("  - 금액: " + payment.getAmount());
        
        try {
            paymentDAO.insertPayment(payment);
            
            System.out.println("✅ 결제 생성 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 결제 생성 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("결제 생성 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 결제 상태 업데이트
     */
    @Override
    @Transactional
    public void updatePaymentStatus(Long paymentId, String status) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔄 결제 상태 업데이트");
        System.out.println("  - 결제 ID: " + paymentId);
        System.out.println("  - 상태: " + status);
        
        try {
            paymentDAO.updatePaymentStatus(paymentId, status);
            
            System.out.println("✅ 결제 상태 업데이트 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 결제 상태 업데이트 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("결제 상태 업데이트 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 결제 취소
     */
    @Override
    @Transactional
    public void cancelPayment(Long paymentId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🚫 결제 취소");
        System.out.println("  - 결제 ID: " + paymentId);
        
        try {
            updatePaymentStatus(paymentId, "CANCELLED");
            
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
     */
    @Override
    public PaymentVO getPaymentById(Long paymentId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 결제 조회 (ID)");
        System.out.println("  - 결제 ID: " + paymentId);
        
        try {
            PaymentVO payment = paymentDAO.selectPaymentById(paymentId);
            
            if (payment != null) {
                System.out.println("✅ 결제 조회 완료");
                System.out.println("  - 금액: " + payment.getAmount());
                System.out.println("  - 상태: " + payment.getPaymentStatus());
            } else {
                System.out.println("⚠️ 결제를 찾을 수 없습니다");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return payment;
            
        } catch (Exception e) {
            System.err.println("❌ 결제 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("결제 조회 실패: " + e.getMessage(), e);
        }
    }
}
