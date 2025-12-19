package com.portwatch.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.portwatch.domain.MemberVO;

/**
 * 회원 DAO 인터페이스
 * MyBatis가 자동으로 구현체를 생성
 */
@Mapper  // ⭐ MyBatis Mapper 인터페이스임을 명시
public interface MemberDAO {
    
    /**
     * 회원 등록
     */
    public void insertMember(MemberVO member) throws Exception;
    
    /**
     * 이메일로 회원 조회
     */
    public MemberVO selectMemberByEmail(String email) throws Exception;
    
    /**
     * 회원 ID로 조회
     */
    public MemberVO selectMemberById(int memberId) throws Exception;
    
    /**
     * 이메일 중복 확인
     */
    public int checkEmailDuplicate(String email) throws Exception;
    
    /**
     * 회원 정보 수정
     */
    public void updateMember(MemberVO member) throws Exception;
    
    /**
     * 비밀번호 변경
     * @Param 어노테이션으로 파라미터 명시
     */
    public void updatePassword(@Param("memberId") int memberId, 
                              @Param("newPassword") String newPassword) throws Exception;
    
    /**
     * 회원 탈퇴
     */
    public void deleteMember(int memberId) throws Exception;

	public MemberVO selectMemberById(String memberId);
    
    
}
