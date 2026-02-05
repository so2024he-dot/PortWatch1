package com.portwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.MemberVO;
import com.portwatch.persistence.MemberMapper;

/**
 * 회원 서비스 구현
 */
@Service
public class MemberServiceImpl implements MemberService {
    
    @Autowired
    private MemberMapper memberMapper;
    
    /**
     * 로그인
     */
    @Override
    public MemberVO login(String memberEmail, String memberPass) {
        MemberVO member = memberMapper.findByEmail(memberEmail);
        
        if (member != null && member.getMemberPass().equals(memberPass)) {
            // 비밀번호 일치
            if ("ACTIVE".equals(member.getMemberStatus())) {
                return member;
            }
        }
        
        return null;
    }
    
    /**
     * 회원가입
     */
    @Override
    @Transactional
    public void signup(MemberVO member) throws Exception {
        // 회원 ID 생성 (이메일 기반)
        String email = member.getMemberEmail();
        String emailId = email.substring(0, email.indexOf('@'));
        String memberId = emailId + "_" + System.currentTimeMillis();
        member.setMemberId(memberId);
        
        // 기본값 설정
        if (member.getMemberRole() == null) {
            member.setMemberRole("USER");
        }
        if (member.getMemberStatus() == null) {
            member.setMemberStatus("ACTIVE");
        }
        
        memberMapper.insert(member);
    }
    
    /**
     * 회원 ID로 조회
     */
    @Override
    public MemberVO getMemberById(String memberId) {
        return memberMapper.findById(memberId);
    }
    
    /**
     * 회원 정보 수정
     */
    @Override
    @Transactional
    public void updateMember(MemberVO member) throws Exception {
        memberMapper.update(member);
    }
    
    /**
     * 비밀번호 확인
     */
    @Override
    public boolean checkPassword(String memberId, String password) {
        MemberVO member = memberMapper.findById(memberId);
        if (member != null) {
            return member.getMemberPass().equals(password);
        }
        return false;
    }
    
    /**
     * 비밀번호 변경
     */
    @Override
    @Transactional
    public void updatePassword(String memberId, String newPassword) throws Exception {
        memberMapper.updatePassword(memberId, newPassword);
    }
    
    /**
     * 회원 탈퇴
     */
    @Override
    @Transactional
    public void deleteMember(String memberId) throws Exception {
        memberMapper.delete(memberId);
    }
}
