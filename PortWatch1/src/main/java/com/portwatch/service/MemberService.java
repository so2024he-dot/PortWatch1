package com.portwatch.service;

import com.portwatch.domain.MemberVO;

public interface MemberService {
    
    /**
     * 로그인
     */
    MemberVO login(String memberEmail, String memberPass);
    
    /**
     * 회원가입
     */
    void signup(MemberVO member) throws Exception;
    
    /**
     * 회원 ID로 조회
     */
    MemberVO getMemberById(String memberId);
    
    /**
     * 회원 정보 수정
     */
    void updateMember(MemberVO member) throws Exception;
    
    /**
     * 비밀번호 확인
     */
    boolean checkPassword(String memberId, String password);
    
    /**
     * 비밀번호 변경
     */
    void updatePassword(String memberId, String newPassword) throws Exception;
    
    /**
     * 회원 탈퇴
     */
    void deleteMember(String memberId) throws Exception;
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ⭐ 이메일 인증 관련 메서드 (MemberApiController용)
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * 이메일 사용 가능 여부 확인
     * @param email 확인할 이메일
     * @return true: 사용 가능, false: 이미 존재
     */
    boolean checkEmailAvailable(String email);
    
    /**
     * 이메일 인증 코드 생성
     * @return 6자리 인증 코드
     */
    String generateVerificationCode();
    
    /**
     * 인증 코드 검증
     * @param email 이메일
     * @param code 인증 코드
     * @return true: 인증 성공, false: 인증 실패
     */
    boolean verifyCode(String email, String code);
}
