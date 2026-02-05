package com.portwatch.service;

import com.portwatch.domain.MemberVO;

public interface MemberService {
    
    /**
     * 로그인
     * @param memberEmail 이메일
     * @param memberPass 비밀번호
     * @return 회원 정보 (로그인 실패 시 null)
     */
    MemberVO login(String memberEmail, String memberPass);
    
    /**
     * 회원가입
     * @param member 회원 정보
     * @throws Exception 회원가입 실패 시
     */
    void signup(MemberVO member) throws Exception;
    
    /**
     * 회원 ID로 조회
     * @param memberId 회원 ID
     * @return 회원 정보
     */
    MemberVO getMemberById(String memberId);
    
    /**
     * 회원 정보 수정
     * @param member 수정할 회원 정보
     * @throws Exception 수정 실패 시
     */
    void updateMember(MemberVO member) throws Exception;
    
    /**
     * 비밀번호 확인
     * @param memberId 회원 ID
     * @param password 확인할 비밀번호
     * @return 일치 여부
     */
    boolean checkPassword(String memberId, String password);
    
    /**
     * 비밀번호 변경
     * @param memberId 회원 ID
     * @param newPassword 새 비밀번호
     * @throws Exception 변경 실패 시
     */
    void updatePassword(String memberId, String newPassword) throws Exception;
    
    /**
     * 회원 탈퇴
     * @param memberId 회원 ID
     * @throws Exception 탈퇴 실패 시
     */
    void deleteMember(String memberId) throws Exception;
}
