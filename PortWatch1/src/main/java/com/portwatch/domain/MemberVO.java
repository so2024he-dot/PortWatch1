package com.portwatch.domain;

import java.sql.Timestamp;

import lombok.Data;

/**
 * ✅ 회원 VO (완전 구현)
 * 
 * MEMBER 테이블과 1:1 매핑
 * 
 * @author PortWatch
 * @version 3.0 - MySQL 8.0 + Spring 5.0.7 완벽 호환
 */
@Data
public class MemberVO {
    
    // 기본 정보
    private String memberId;          // member_id VARCHAR(50) PK
    private String memberEmail;       // member_email VARCHAR(100) UNIQUE
    private String memberPass;        // member_pass VARCHAR(200)
    private String memberName;        // member_name VARCHAR(50)
    private String memberPhone;       // member_phone VARCHAR(20)
    
    // 추가 정보
    private String memberAddress;     // member_address VARCHAR(200)
    private String memberGender;      // member_gender VARCHAR(10)
    private Timestamp memberBirth;    // member_birth TIMESTAMP
    
    // 시스템 정보
    private String memberRole;        // member_role VARCHAR(20) DEFAULT 'USER'
    private String memberStatus;      // member_status VARCHAR(20) DEFAULT 'ACTIVE'
    private Timestamp createdAt;      // created_at TIMESTAMP
    private Timestamp updatedAt;      // updated_at TIMESTAMP
    
    // 기본 생성자
    public MemberVO() {
    }
    
    // ===== Getter & Setter =====
    
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
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
    
    public String getMemberAddress() {
        return memberAddress;
    }
    
    public void setMemberAddress(String memberAddress) {
        this.memberAddress = memberAddress;
    }
    
    public String getMemberGender() {
        return memberGender;
    }
    
    public void setMemberGender(String memberGender) {
        this.memberGender = memberGender;
    }
    
    public Timestamp getMemberBirth() {
        return memberBirth;
    }
    
    public void setMemberBirth(Timestamp memberBirth) {
        this.memberBirth = memberBirth;
    }
    
    public String getMemberRole() {
        return memberRole;
    }
    
    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }
    
    public String getMemberStatus() {
        return memberStatus;
    }
    
    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
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
        return "MemberVO [memberId=" + memberId + ", memberEmail=" + memberEmail + 
               ", memberName=" + memberName + ", memberPhone=" + memberPhone + 
               ", memberRole=" + memberRole + ", memberStatus=" + memberStatus + "]";
    }

	public Object getEmail() {
			return getEmail();
	}

	public Object getStatus() {
		// TODO Auto-generated method stub
		return getStatus();
	}

	
}
