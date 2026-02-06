package com.portwatch.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.portwatch.domain.MemberVO;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * MemberMapper - 회원 데이터 매퍼
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * @author PortWatch
 * @version 1.0
 */
@Mapper
public interface MemberMapper {
    
    /**
     * 회원 등록
     * @param member 회원 정보
     */
    void insert(MemberVO member);
    
    /**
     * 회원 조회 (ID)
     * @param memberId 회원 ID
     * @return 회원 정보
     */
    MemberVO findById(@Param("memberId") String memberId);
    
    /**
     * 회원 조회 (이메일)
     * @param memberEmail 이메일
     * @return 회원 정보
     */
    MemberVO findByEmail(@Param("memberEmail") String memberEmail);
    
    /**
     * 회원 정보 수정
     * @param member 회원 정보
     */
    void update(MemberVO member);
    
    /**
     * 회원 삭제
     * @param memberId 회원 ID
     */
    void delete(@Param("memberId") String memberId);
    
    /**
     * 전체 회원 수 조회
     * @return 회원 수
     */
    int count();
}
