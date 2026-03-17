package com.portwatch.service;

import com.portwatch.domain.MemberVO;

/**
 * MemberService 인터페이스 - 최종본
 * MemberVO Version B (실제 DB) + balance 병합 기준
 */
public interface MemberService {

    /** 로그인 - 이메일 기반, 성공 시 MemberVO 반환, 실패 시 null */
    MemberVO login(String memberEmail, String memberPass);

    /** 회원가입 */
    void register(MemberVO member);

    /** 회원 조회 (아이디) */
    MemberVO getMemberById(String memberId);

    /** 회원 조회 (이메일) */
    MemberVO getMemberByEmail(String memberEmail);

    /** 회원 정보 수정 */
    void updateMember(MemberVO member);

    /** 회원 삭제 (soft delete → INACTIVE) */
    void deleteMember(String memberId);

    /** 이메일 사용 가능 여부 - true: 사용 가능 */
    boolean checkEmailAvailable(String email);

    /** 아이디 사용 가능 여부 - true: 사용 가능 */
    boolean checkIdAvailable(String memberId);

    /** 6자리 이메일 인증 코드 생성 */
    String generateVerificationCode();

    /** 인증 코드 검증 */
    boolean verifyCode(String email, String code);

    /** 잔액 조회 */
    double getBalance(String memberId);

    /** 잔액 업데이트 */
    void updateBalance(String memberId, double balance);
}
