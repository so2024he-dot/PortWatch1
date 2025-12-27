package com.portwatch.persistence;

import java.lang.System.Logger;

import com.portwatch.domain.MemberVO;

/**
 * ✅ 회원 DAO 인터페이스
 * 
 * @author PortWatch
 * @version 3.0 - MySQL 8.0 호환
 */
public interface MemberDAO {
    
    /**
     * 회원가입
     */
    void insertMember(MemberVO member) throws Exception;
    
    /**
     * 이메일로 회원 조회
     */
    MemberVO selectMemberByEmail(String memberEmail) throws Exception;
    
    /**
     * ID로 회원 조회
     */
    MemberVO selectMemberById(String memberId) throws Exception;
    
    /**
     * 회원 정보 업데이트
     */
    void updateMember(MemberVO member) throws Exception;
    
    /**
     * 회원 삭제 (물리 삭제)
     */
    void deleteMember(String memberId) throws Exception;
    
    /**
     * 회원 상태 변경 (논리 삭제)
     */
    void updateMemberStatus(String memberId, String status) throws Exception;
    
    /**
     * 이메일 중복 체크
     */
    int checkDuplicateEmail(String memberEmail) throws Exception;
    
    /**
     * ID 중복 체크
     */
    int checkDuplicateId(String memberId) throws Exception;
    
    /**
     * 전체 회원 수
     */
    int getTotalMemberCount() throws Exception;
    
    /**
     * 활성 회원 수
     */
    int getActiveMemberCount() throws Exception;
    
	static MemberVO selectByEmail(String email) throws Exception{
		
	    return null;
	}

	void updatePassword(String memberId, String newPassword);


}
