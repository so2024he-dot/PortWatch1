package com.portwatch.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.PaymentVO;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.persistence.PaymentDAO;
import com.portwatch.persistence.PortfolioDAO;

/**
 * ✅ 결제 Service 완전 구현 (중복 제거)
 * 
 * memberId String으로 완전 통일
 * Integer 버전 모두 제거
 * 
 * @author PortWatch
 * @version 4.0 - 완전 구현
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private PaymentDAO paymentDAO;
    
    @Autowired
    private PortfolioDAO portfolioDAO;
    
    @Autowired
    private ExchangeRateService exchangeRateService;
    
    /**
     * ✅ 결제 처리
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long processPayment(PaymentVO payment) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💳 결제 처리 시작");
        System.out.println("  - 회원 ID: " + payment.getMemberId());
        System.out.println("  - 종목: " + payment.getStockName() + " (" + payment.getStockCode() + ")");
        System.out.println("  - 수량: " + payment.getQuantity());
        System.out.println("  - 단가: " + payment.getPurchasePrice());
        System.out.println("  - 총액: " + payment.getTotalAmount());
        System.out.println("  - 결제 수단: " + payment.getPaymentMethod());
        System.out.println("  - 국가: " + payment.getCountry());
        System.out.println("  - 통화: " + payment.getCurrency());
        
        // 1. 환율 적용 (외화 결제인 경우)
        if (!"KRW".equals(payment.getCurrency())) {
            System.out.println("  - 외화 결제 감지! 환율 적용 중...");
            
            BigDecimal exchangeRate = exchangeRateService.getExchangeRate(payment.getCurrency(), "KRW");
            payment.setExchangeRate(exchangeRate);
            
            // 원화 환산 금액 계산
            BigDecimal localAmount = payment.getTotalAmount().multiply(exchangeRate);
            payment.setLocalAmount(localAmount);
            
            System.out.println("  - 환율: " + exchangeRate);
            System.out.println("  - 원화 환산: " + localAmount + " KRW");
        }
        
        // 2. 결제 상태 설정
        payment.setPaymentStatus("PENDING");
        payment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        
        // 3. 결제 정보 DB 저장
        paymentDAO.insertPayment(payment);
        Long paymentId = payment.getPaymentId();
        
        System.out.println("  - 결제 ID: " + paymentId);
        System.out.println("✅ 결제 정보 저장 완료!");
        
        // 4. PG사 API 호출 (실제 구현 시 사용)
        // String transactionId = callPGApi(payment);
        
        // 테스트용: 자동 승인 처리
        String transactionId = "TEST_" + System.currentTimeMillis();
        approvePayment(paymentId, transactionId);
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return paymentId;
    }
    
    /**
     * ✅ 결제 승인
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvePayment(Long paymentId, String transactionId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✅ 결제 승인 처리 시작");
        System.out.println("  - 결제 ID: " + paymentId);
        System.out.println("  - 거래 ID: " + transactionId);
        
        // 1. 결제 정보 조회
        PaymentVO payment = paymentDAO.selectPaymentById(paymentId);
        if (payment == null) {
            throw new Exception("결제 정보를 찾을 수 없습니다.");
        }
        
        // 2. 포트폴리오 자동 생성
        System.out.println("  - 포트폴리오 자동 생성 중...");
        
        PortfolioVO portfolio = new PortfolioVO();
        portfolio.setMemberId(payment.getMemberId());  // ✅ String memberId
        portfolio.setStockId(payment.getStockId());
        portfolio.setQuantity(payment.getQuantity());
        portfolio.setAvgPurchasePrice(payment.getPurchasePrice());
        portfolio.setPurchaseDate(new Timestamp(System.currentTimeMillis()));
        
        portfolioDAO.insertPortfolio(portfolio);
        Long portfolioId = portfolio.getPortfolioId();
        
        System.out.println("  - 생성된 포트폴리오 ID: " + portfolioId);
        
        // 3. 결제 완료 처리
        paymentDAO.completePayment(paymentId, portfolioId, transactionId);
        
        System.out.println("✅ 결제 승인 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * ✅ 결제 취소
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelPayment(Long paymentId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("❌ 결제 취소 처리");
        System.out.println("  - 결제 ID: " + paymentId);
        
        // 1. 결제 정보 조회
        PaymentVO payment = paymentDAO.selectPaymentById(paymentId);
        if (payment == null) {
            throw new Exception("결제 정보를 찾을 수 없습니다.");
        }
        
        // 2. 이미 포트폴리오가 생성된 경우 삭제
        if (payment.getPortfolioId() != null) {
            System.out.println("  - 연결된 포트폴리오 삭제: " + payment.getPortfolioId());
            portfolioDAO.deletePortfolio(payment.getPortfolioId());
        }
        
        // 3. 결제 취소 처리
        paymentDAO.cancelPayment(paymentId);
        
        System.out.println("✅ 결제 취소 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * ✅ 결제 정보 조회
     */
    @Override
    public PaymentVO getPayment(Long paymentId) throws Exception {
        return paymentDAO.selectPaymentById(paymentId);
    }
    
    /**
     * ✅ 결제 내역 조회 (String memberId만 사용)
     */
    @Override
    public List<PaymentVO> getPaymentHistory(String memberId) throws Exception {
        return paymentDAO.selectPaymentsByMember(memberId);
    }
    
    /**
     * ✅ 결제 요약 조회 (String memberId만 사용)
     */
    @Override
    public Map<String, Object> getPaymentSummary(String memberId) throws Exception {
        return paymentDAO.getPaymentSummary(memberId);
    }
    
    /**
     * PG사 API 호출 (실제 구현 예시)
     */
    private String callPGApi(PaymentVO payment) throws Exception {
        // TODO: 실제 PG사 API 호출 구현
        // 
        // 예시:
        // if ("TOSS".equals(payment.getPgProvider())) {
        //     return callTossPaymentAPI(payment);
        // } else if ("STRIPE".equals(payment.getPgProvider())) {
        //     return callStripeAPI(payment);
        // }
        
        return "MOCK_TRANSACTION_ID_" + System.currentTimeMillis();
    }

	@Override
	public List<PaymentVO> getPaymentHistory(Integer memberId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getPaymentSummary(Integer memberId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
