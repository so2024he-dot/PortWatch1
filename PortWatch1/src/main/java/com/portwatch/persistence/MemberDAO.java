package com.portwatch.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.portwatch.domain.MemberVO;

/**
 * MemberDAO - persistence 레이어 (최종 수정본)
 *
 * ✅ 해결된 컴파일 오류:
 *   [ERROR] StockPurchaseValidationService.java:[178]
 *           cannot find symbol: method selectMemberById(java.lang.String)
 *   → selectMemberById() 메서드 추가로 해결
 *
 * ✅ 이전 오류 해결 유지:
 *   @Mapper  (value() 속성 없으므로 @Mapper("memberDAO") 사용 불가)
 */
@Mapper
public interface MemberDAO {

    /** 이메일로 회원 조회 (로그인) */
    MemberVO findByEmail(@Param("memberEmail") String memberEmail);

    /** 아이디로 회원 조회 */
    MemberVO findById(@Param("memberId") String memberId);

    /**
     * 아이디로 회원 조회 (selectMemberById 명칭)
     * ✅ StockPurchaseValidationService.java:178 에서 호출하므로 필수 추가
     */
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
