package com.portwatch.service;

import com.portwatch.domain.MemberVO;

public interface MemberService {
    public void signup(MemberVO member) throws Exception;
    public MemberVO login(String email, String password) throws Exception;
    public boolean checkEmailAvailable(String email) throws Exception;
    public MemberVO getMemberById(int memberId) throws Exception;
    public void updateMember(MemberVO member) throws Exception;
    public void changePassword(int memberId, String currentPassword, String newPassword) throws Exception;
    public void deleteMember(int memberId) throws Exception;
    public String generateVerificationCode() throws Exception;
    public boolean verifyCode(String email, String code) throws Exception;
}
