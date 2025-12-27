package com.portwatch.domain;

import java.sql.Timestamp;

/**
 * ✅ 회원 VO - 완벽 수정
 * 
 * MEMBER 테이블과 1:1 매핑
 * - 중복 메서드 모두 제거
 * - Alias 메서드 추가 (호환성)
 * 
 * @author PortWatch
 * @version FINAL - Spring 5.0.7 + MySQL 8.0.33 완전 대응
 */
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
    
    // ===== Getters and Setters =====
    
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
    
    // ===== Alias Methods (호환성) =====
    
    /**
     * ✅ JSP 호환성: memberRegDate alias (createdAt를 반환)
     */
    public Timestamp getMemberRegDate() {
        return this.createdAt;
    }
    
    public void setMemberRegDate(Timestamp memberRegDate) {
        this.createdAt = memberRegDate;
    }
    
    /**
     * ✅ email alias (memberEmail를 반환)
     * MemberServiceImpl, Controller에서 사용
     */
    public String getEmail() {
        return this.memberEmail;
    }
    
    public void setEmail(String email) {
        this.memberEmail = email;
    }
    
    /**
     * ✅ name alias (memberName를 반환)
     * Controller, JSP에서 사용
     */
    public String getName() {
        return this.memberName;
    }
    
    public void setName(String name) {
        this.memberName = name;
    }
    
    /**
     * ✅ password alias (memberPass를 반환)
     * MemberServiceImpl.login()에서 사용
     */
    public String getPassword() {
        return this.memberPass;
    }
    
    public void setPassword(String password) {
        this.memberPass = password;
    }
    
    /**
     * ✅ status alias (memberStatus를 반환)
     */
    public String getStatus() {
        return this.memberStatus;
    }
    
    public void setStatus(String status) {
        this.memberStatus = status;
    }
    
    @Override
    public String toString() {
        return "MemberVO [memberId=" + memberId + ", memberEmail=" + memberEmail + ", memberPass=" + memberPass
                + ", memberName=" + memberName + ", memberPhone=" + memberPhone + ", memberAddress=" + memberAddress
                + ", memberGender=" + memberGender + ", memberBirth=" + memberBirth + ", memberRole=" + memberRole
                + ", memberStatus=" + memberStatus + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }
}
