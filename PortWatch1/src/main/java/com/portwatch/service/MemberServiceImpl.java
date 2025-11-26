package com.portwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.portwatch.domain.MemberVO;
import com.portwatch.persistence.MemberDAO;

@Service
public class MemberServiceImpl implements MemberService {
    
    @Autowired
    private MemberDAO memberDAO;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public void signup(MemberVO member) throws Exception {
        String encodedPassword = passwordEncoder.encode(member.getMemberPass());
        member.setMemberPass(encodedPassword);
        memberDAO.insertMember(member);
    }
    
    @Override
    public MemberVO login(String email, String password) throws Exception {
        MemberVO member = memberDAO.selectMemberByEmail(email);
        
        if (member != null && passwordEncoder.matches(password, member.getMemberPass())) {
            return member;
        }
        
        return null;
    }
    
    @Override
    public boolean checkEmailAvailable(String email) throws Exception {
        int count = memberDAO.checkEmailDuplicate(email);
        return count == 0;
    }
    
    @Override
    public MemberVO getMemberById(int memberId) throws Exception {
        return memberDAO.selectMemberById(memberId);
    }
    
    @Override
    public void updateMember(MemberVO member) throws Exception {
        memberDAO.updateMember(member);
    }
    
    @Override
    public void changePassword(int memberId, String currentPassword, String newPassword) throws Exception {
        MemberVO member = memberDAO.selectMemberById(memberId);
        
        if (member != null && passwordEncoder.matches(currentPassword, member.getMemberPass())) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            memberDAO.updatePassword(memberId, encodedPassword);
        } else {
            throw new Exception("현재 비밀번호가 일치하지 않습니다.");
        }
    }
    
    @Override
    public void deleteMember(int memberId) throws Exception {
        memberDAO.deleteMember(memberId);
    }
    
    @Override
    public String generateVerificationCode() throws Exception {
        return String.format("%06d", (int)(Math.random() * 1000000));
    }
    
    @Override
    public boolean verifyCode(String email, String code) throws Exception {
        // TODO: 실제 인증 로직 (세션 또는 Redis 사용)
        return true;
    }
}
