package com.portwatch.service;

import java.util.List;

import com.portwatch.domain.MemberVO;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * ✅ 회원 Service 인터페이스 (수정 버전)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * 핵심 수정:
 * - Line 45: changePassword(3개 파라미터) 반환 타입을 void -> boolean 변경
 * 
 * @author PortWatch
 * @version FIXED - 2026.01.16
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
     * ✅ 비밀번호 변경 (현재 비밀번호 확인 포함)
     * 
     * @param memberId 회원 ID
     * @param oldPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     * @return 성공 여부 (true: 성공, false: 현재 비밀번호 불일치)
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

    /**
     * 이메일 사용 가능 여부 확인
     */
    boolean checkEmailAvailable(String email) throws Exception;

    /**
     * 인증 코드 생성
     */
    String generateVerificationCode() throws Exception;

    /**
     * 인증 코드 검증
     */
    boolean verifyCode(String email, String code) throws Exception;

    /**
     * ✅ 인증 코드 저장
     * 
     * @param email 이메일
     * @param code 인증 코드
     */
    void saveVerificationCode(String email, String code) throws Exception;

    /**
     * ✅ 비밀번호 변경 (현재 비밀번호 확인 없이)
     * 
     * @param memberId 회원 ID
     * @param newPassword 새 비밀번호
     */
    void changePassword(String memberId, String newPassword) throws Exception;

    /**
     * ✅ 회원 탈퇴 (하드 삭제)
     * 
     * @param memberId 회원 ID
     */
    void deleteMember(String memberId) throws Exception;

    /**
     * ✅ 전체 회원 조회
     */
    List<MemberVO> getAllMembers() throws Exception;
}
