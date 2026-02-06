package com.portwatch.service;

import com.portwatch.domain.MemberVO;

/**
 * 회원 서비스 인터페이스
 */
public interface MemberService {
    
    /**
     * 로그인
     */
    MemberVO login(String memberEmail, String memberPass);
    
    /**
     * 회원가입
     */
    void register(MemberVO member);
    
    /**
     * 회원 조회 (ID)
     */
    MemberVO getMemberById(String memberId);
    
    /**
     * 회원 조회 (이메일)
     */
    MemberVO getMemberByEmail(String memberEmail);
    
    /**
     * 회원 정보 수정
     */
    void updateMember(MemberVO member);
    
    /**
     * 회원 삭제
     */
    void deleteMember(String memberId);
    
    /**
     * 이메일 중복 체크
     */
    boolean checkEmailAvailable(String email);
    
    /**
     * 인증 코드 생성
     */
    String generateVerificationCode();
    
    /**
     * 인증 코드 검증
     */
    boolean verifyCode(String email, String code);
}
