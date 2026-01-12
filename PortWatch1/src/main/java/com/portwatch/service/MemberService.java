package com.portwatch.service;

import com.portwatch.domain.MemberVO;

/**
 * ✅ 회원 Service 인터페이스
 * 
 * @author PortWatch
 * @version 3.0
 */
public interface MemberService {
    
    /**
     * 회원가입
     */
    void signup(MemberVO member) throws Exception;
    
    /**
     * 로그인
     */
    MemberVO login(String memberEmail, String memberPass) throws Exception;
    
    /**
     * 이메일로 회원 조회
     */
    MemberVO getMemberByEmail(String memberEmail) throws Exception;
    
    /**
     * ID로 회원 조회
     */
    MemberVO getMemberById(String memberId) throws Exception;
    
    /**
     * 회원 정보 업데이트
     */
    void updateMember(MemberVO member) throws Exception;
    
    /**
     * 비밀번호 변경
     * @return 
     */
    boolean changePassword(String memberId, String oldPassword, String newPassword) throws Exception;
    
    /**
     * 회원 탈퇴 (논리 삭제)
     */
    void withdrawMember(String memberId) throws Exception;
    
    /**
     * 이메일 중복 체크
     */
    boolean isEmailDuplicate(String memberEmail) throws Exception;
    
    /**
     * ID 중복 체크
     */
    boolean isIdDuplicate(String memberId) throws Exception;

	boolean checkEmailAvailable(String email) throws Exception;

	String generateVerificationCode() throws Exception;

	boolean verifyCode(String email, String code) throws Exception;

	/**
	 * ✅ 인증 코드 저장
	 * 
	 * @param email 이메일
	 * @param code 인증 코드
	 */
	void saveVerificationCode(String email, String code) throws Exception;

	/**
	 * ✅ 비밀번호 변경
	 * 
	 * @param memberId 회원 ID
	 * @param newPassword 새 비밀번호
	 */
	void changePassword(String memberId, String newPassword) throws Exception;

	/**
	 * ✅ 회원 탈퇴
	 * 
	 * @param memberId 회원 ID
	 */
	void deleteMember(String memberId) throws Exception;
}
