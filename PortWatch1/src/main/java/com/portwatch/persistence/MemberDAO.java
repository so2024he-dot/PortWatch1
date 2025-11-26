package com.portwatch.persistence;

import com.portwatch.domain.MemberVO;

public interface MemberDAO {
    public void insertMember(MemberVO member) throws Exception;
    public MemberVO selectMemberByEmail(String email) throws Exception;
    public MemberVO selectMemberById(int memberId) throws Exception;
    public int checkEmailDuplicate(String email) throws Exception;
    public void updateMember(MemberVO member) throws Exception;
    public void updatePassword(int memberId, String newPassword) throws Exception;
    public void deleteMember(int memberId) throws Exception;
}
