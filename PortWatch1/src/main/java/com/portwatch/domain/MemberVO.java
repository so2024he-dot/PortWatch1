package com.portwatch.domain;

import java.sql.Timestamp;

public class MemberVO {
    private int memberId;
    private String memberEmail;
    private String memberPass;
    private String memberName;
    private String memberPhone;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public MemberVO() {}
    
    public int getMemberId() {
        return memberId;
    }
    
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
    
    public String getMemberEmail() {
        return memberEmail;
    }
    
    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }
    
    public String getMemberPass() {
        return memberPass;
    }
    
    public void setMemberPass(String memberPass) {
        this.memberPass = memberPass;
    }
    
    public String getMemberName() {
        return memberName;
    }
    
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    
    public String getMemberPhone() {
        return memberPhone;
    }
    
    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "MemberVO{memberId=" + memberId + ", memberEmail='" + memberEmail + "', memberName='" + memberName + "'}";
    }
}
