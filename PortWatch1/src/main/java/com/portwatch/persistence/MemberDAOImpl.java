package com.portwatch.persistence;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.portwatch.domain.MemberVO;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * MemberDAOImpl - 회원 데이터 접근 구현체
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * @author PortWatch
 * @version FINAL COMPLETE
 */
@Repository
public class MemberDAOImpl implements MemberDAO {
    
    @Autowired
    private SqlSession sqlSession;
    
    private static final String NAMESPACE = "com.portwatch.persistence.MemberDAO";
    
    /**
     * ✅ 로그인
     */
    @Override
    public MemberVO login(String memberEmail, String memberPass) throws Exception {
        return sqlSession.selectOne(NAMESPACE + ".login", 
            new Object[] { memberEmail, memberPass });
    }
    
    /**
     * ✅ 회원가입
     */
    @Override
    public void insertMember(MemberVO member) throws Exception {
        sqlSession.insert(NAMESPACE + ".insertMember", member);
    }
    
    /**
     * ✅ 이메일 중복 체크
     */
    @Override
    public int checkEmailDuplicate(String memberEmail) throws Exception {
        return sqlSession.selectOne(NAMESPACE + ".checkEmailDuplicate", memberEmail);
    }
    
    /**
     * ✅ ID 중복 체크
     */
    @Override
    public int checkIdDuplicate(String memberId) throws Exception {
        return sqlSession.selectOne(NAMESPACE + ".checkIdDuplicate", memberId);
    }
    
    /**
     * ✅ 회원 조회 (ID로)
     */
    @Override
    public MemberVO selectMemberById(String memberId) throws Exception {
        return sqlSession.selectOne(NAMESPACE + ".selectMemberById", memberId);
    }
    
    /**
     * ✅ 회원 조회 (이메일로)
     */
    @Override
    public MemberVO selectMemberByEmail(String memberEmail) throws Exception {
        return sqlSession.selectOne(NAMESPACE + ".selectMemberByEmail", memberEmail);
    }
    
    /**
     * ✅ 전체 회원 조회
     */
    @Override
    public List<MemberVO> selectAllMembers() throws Exception {
        return sqlSession.selectList(NAMESPACE + ".selectAllMembers");
    }
    
    /**
     * ✅ 회원 정보 수정
     */
    @Override
    public void updateMember(MemberVO member) throws Exception {
        sqlSession.update(NAMESPACE + ".updateMember", member);
    }
    
    /**
     * ✅ 비밀번호 변경
     */
    @Override
    public void updatePassword(String memberId, String newPassword) throws Exception {
        sqlSession.update(NAMESPACE + ".updatePassword", 
            new Object[] { memberId, newPassword });
    }
    
    /**
     * ✅ 회원 삭제
     */
    @Override
    public void deleteMember(String memberId) throws Exception {
        sqlSession.delete(NAMESPACE + ".deleteMember", memberId);
    }
}
