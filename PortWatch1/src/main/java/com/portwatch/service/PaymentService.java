package com.portwatch.service;

import com.portwatch.domain.PaymentVO;
import java.util.List;
import java.util.Map;

/**
 * 결제 Service 인터페이스
 * 
 * @author PortWatch
 * @version 1.0
 */
public interface PaymentService {
    
    /**
     * 결제 요청 처리
     * 1. 결제 정보 등록
     * 2. PG사 API 호출
     * 3. 결제 완료 시 포트폴리오 자동 생성
     * 
     * @param payment 결제 정보
     * @return 결제 ID
     */
    Long processPayment(PaymentVO payment) throws Exception;
    
    /**
     * 결제 승인 처리
     * PG사로부터 결제 승인 콜백 받았을 때 호출
     * 
     * @param paymentId 결제 ID
     * @param transactionId PG사 거래 ID
     */
    void approvePayment(Long paymentId, String transactionId) throws Exception;
    
    /**
     * 결제 취소
     * 
     * @param paymentId 결제 ID
     */
    void cancelPayment(Long paymentId) throws Exception;
    
    /**
     * 결제 내역 조회
     * 
     * @param paymentId 결제 ID
     * @return 결제 정보
     */
    PaymentVO getPayment(Long paymentId) throws Exception;
    
    /**
     * 회원의 결제 내역 조회
     * 
     * @param memberId 회원 ID
     * @return 결제 내역 리스트
     */
    List<PaymentVO> getPaymentHistory(Integer memberId) throws Exception;

	Map<String, Object> getPaymentSummary(Integer memberId) throws Exception;

	List<PaymentVO> getPaymentHistory(String memberId) throws Exception;

	Map<String, Object> getPaymentSummary(String memberId) throws Exception;

	/**
	 * ✅ 결제 생성
	 * 
	 * @param payment 결제 정보
	 */
	void createPayment(PaymentVO payment) throws Exception;

	/**
	 * ✅ 결제 상태 업데이트
	 * 
	 * @param paymentId 결제 ID
	 * @param status 결제 상태
	 */
	void updatePaymentStatus(Long paymentId, String status) throws Exception;

	/**
	 * ✅ 결제 조회 (ID로)
	 * 
	 * @param paymentId 결제 ID
	 * @return 결제 정보
	 */
	PaymentVO getPaymentById(Long paymentId) throws Exception;
    
    /**
     * 회원의 결제 요약 정보 조회
     * 
     * @param memberId 회원 ID
     * @return 결제 요약 정보
     */

}
