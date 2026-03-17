package com.portwatch.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.portwatch.domain.MemberVO;

/**
 * MemberMapper - mapper 레이어 (최종 수정본)
 * persistence.MemberDAO 와 동일한 메서드 구성 유지
 */
@Mapper
public interface MemberMapper {

    /** 이메일로 회원 조회 (로그인) */
    MemberVO findByEmail(@Param("memberEmail") String memberEmail);

    /** 아이디로 회원 조회 */
    MemberVO findById(@Param("memberId") String memberId);

    /** 아이디로 회원 조회 (selectMemberById 명칭) */
    MemberVO selectMemberById(@Param("memberId") String memberId);

    /** 회원 등록 */
    void insert(MemberVO member);

    /** 회원 정보 수정 */
    void update(MemberVO member);

    /** 회원 삭제 */
    void delete(@Param("memberId") String memberId);

    /** 마지막 로그인 시간 업데이트 */
    void updateLastLogin(@Param("memberId") String memberId);

    /** 잔액 업데이트 */
    void updateBalance(@Param("memberId") String memberId,
                       @Param("balance")  double balance);
}
