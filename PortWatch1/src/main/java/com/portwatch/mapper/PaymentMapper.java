package com.portwatch.mapper;

import java.util.List;

import com.portwatch.domain.PaymentVO;

/**
 * PaymentMapper Interface
 * ══════════════════════════════════════════════════════════════
 * 문제: PaymentMapper.xml은 로드됐지만 PaymentMapper.class 미등록
 * 원인: PaymentMapper.java 인터페이스 파일이 없었음
 * 해결: 이 파일 생성 → 자동 Bean 등록
 * ══════════════════════════════════════════════════════════════
 */
public interface PaymentMapper {

    /** 결제 등록 */
    int insert(PaymentVO payment);

    /** 결제 단건 조회 */
    PaymentVO findById(Long paymentId);

    /** 회원별 결제 내역 조회 */
    List<PaymentVO> findByMemberId(Long memberId);

    /** 결제 상태 업데이트 */
    int updateStatus(PaymentVO payment);

    /** 결제 삭제 */
    int delete(Long paymentId);

    /** 전체 결제 수 */
    int count();
}
