package com.portwatch.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.portwatch.domain.PaymentVO;

import java.util.List;
import java.util.Map;

/**
 * 결제 DAO 인터페이스
 * MyBatis가 자동으로 구현체를 생성
 * 
 * @author PortWatch
 * @version 1.0
 */
@Mapper
public interface PaymentDAO {
    
    /**
     * 결제 정보 등록 (결제 요청)
     */
    void insertPayment(PaymentVO payment) throws Exception;
    
    /**
     * 결제 ID로 조회
     */
    PaymentVO selectPaymentById(Long paymentId) throws Exception;
    
    /**
     * 회원의 결제 내역 조회
     */
    List<PaymentVO> selectPaymentsByMember(Integer memberId) throws Exception;
    
    /**
     * 결제 상태 업데이트
     */
    void updatePaymentStatus(@Param("paymentId") Long paymentId, 
                            @Param("status") String status,
                            @Param("transactionId") String transactionId) throws Exception;
    
    /**
     * 결제 완료 처리 (포트폴리오 ID 업데이트)
     */
    void completePayment(@Param("paymentId") Long paymentId,
                        @Param("portfolioId") Long portfolioId,
                        @Param("transactionId") String transactionId) throws Exception;
    
    /**
     * 결제 취소
     */
    void cancelPayment(Long paymentId) throws Exception;
    
    /**
     * 회원의 총 결제 금액 조회
     */
    Map<String, Object> getPaymentSummary(Integer memberId) throws Exception;
    
    /**
     * 국가별 결제 통계
     */
    List<Map<String, Object>> getPaymentStatsByCountry() throws Exception;
    
    /**
     * 최근 결제 내역 조회
     */
    List<PaymentVO> selectRecentPayments(@Param("limit") int limit) throws Exception;

	List<PaymentVO> selectPaymentsByMember(String memberId);

	Map<String, Object> getPaymentSummary(String memberId);
}
