package com.portwatch.persistence;

import java.util.List;
import com.portwatch.domain.MemberVO;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * MemberDAO - 회원 데이터 접근 인터페이스 (Spring 5.0.7 + MySQL 8.0.33)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * 수정 내역:
 * - selectById, selectByEmail 메서드 추가 (MemberServiceImpl 호환)
 * - 기존 selectMemberById, selectMemberByEmail 유지
 * 
 * 실제 MEMBER 테이블 구조:
 * - member_id VARCHAR(50) PK
 * - member_email VARCHAR(100)
 * - member_pass VARCHAR(200)
 * - member_name VARCHAR(100)
 * - member_phone VARCHAR(20)
 * - member_address VARCHAR(255)
 * - member_gender VARCHAR(10)
 * - member_birth TIMESTAMP
 * - member_role VARCHAR(20) DEFAULT 'USER'
 * - member_status VARCHAR(20) DEFAULT 'ACTIVE'
 * - created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
 * - updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 * 
 * @author PortWatch
 * @version FINAL FIXED - 2026.01.14
 */
public interface MemberDAO {
    
    /**
     * 로그인
     * @param memberEmail 이메일
     * @param memberPass 해시된 비밀번호
     * @return 회원 정보
     */
    MemberVO login(String memberEmail, String memberPass) throws Exception;
    
    /**
     * 회원가입
     * @param member 회원 정보
     */
    void insertMember(MemberVO member) throws Exception;
    
    /**
     * 이메일 중복 체크
     * @param memberEmail 이메일
     * @return 중복 개수
     */
    int checkEmailDuplicate(String memberEmail) throws Exception;
    
    /**
     * ID 중복 체크
     * @param memberId 회원 ID
     * @return 중복 개수
     */
    int checkIdDuplicate(String memberId) throws Exception;
    
    /**
     * 회원 조회 (ID로) - 기존 메서드
     * @param memberId 회원 ID
     * @return 회원 정보
     */
    MemberVO selectMemberById(String memberId) throws Exception;
    
    /**
     * 회원 조회 (이메일로) - 기존 메서드
     * @param memberEmail 이메일
     * @return 회원 정보
     */
    MemberVO selectMemberByEmail(String memberEmail) throws Exception;
    
    /**
     * ✅ 회원 조회 (ID로) - MemberServiceImpl 호환 메서드
     * @param memberId 회원 ID
     * @return 회원 정보
     */
    MemberVO selectById(String memberId) throws Exception;
    
    /**
     * ✅ 회원 조회 (이메일로) - MemberServiceImpl 호환 메서드
     * @param memberEmail 이메일
     * @return 회원 정보
     */
    MemberVO selectByEmail(String memberEmail) throws Exception;
    
    /**
     * 전체 회원 조회
     * @return 회원 목록
     */
    List<MemberVO> selectAllMembers() throws Exception;
    
    /**
     * 회원 정보 수정
     * @param member 회원 정보
     */
    void updateMember(MemberVO member) throws Exception;
    
    /**
     * 비밀번호 변경
     * @param memberId 회원 ID
     * @param newPassword 새 비밀번호 (해시된)
     */
    void updatePassword(String memberId, String newPassword) throws Exception;
    
    /**
     * 회원 삭제
     * @param memberId 회원 ID
     */
    void deleteMember(String memberId) throws Exception;
}
