package com.portwatch.persistence;

import org.apache.ibatis.annotations.Param;
import com.portwatch.domain.MemberVO;

public interface MemberMapper {
    
    /**
     * 회원 등록
     */
    void insert(MemberVO member);
    
    /**
     * 이메일로 회원 조회
     */
    MemberVO findByEmail(String memberEmail);
    
    /**
     * 회원 ID로 조회
     */
    MemberVO findById(String memberId);
    
    /**
     * 회원 정보 수정
     */
    void update(MemberVO member);
    
    /**
     * 비밀번호 변경
     */
    void updatePassword(@Param("memberId") String memberId, 
                       @Param("memberPass") String memberPass);
    
    /**
     * 회원 삭제
     */
    void delete(String memberId);
}
