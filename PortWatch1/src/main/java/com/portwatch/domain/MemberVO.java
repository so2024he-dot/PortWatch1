package com.portwatch.domain;

import java.sql.Timestamp;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * MemberVO - 실제 MEMBER 테이블 완벽 매핑
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 실제 MEMBER 테이블 구조:
 * - member_id VARCHAR(50) PK
 * - member_email VARCHAR(100)
 * - member_pass VARCHAR(200)
 * - member_name VARCHAR(20)
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
 * @version FINAL COMPLETE
 */
public class MemberVO {
    
    // ========================================
    // 필수 필드
    // ========================================
    
    /**
     * 회원 ID (Primary Key)
     * VARCHAR(50)
     */
    private String memberId;
    
    /**
     * 이메일
     * VARCHAR(100)
     */
    private String memberEmail;
    
    /**
     * 비밀번호 (SHA-256 해시)
     * VARCHAR(200)
     */
    private String memberPass;
    
    /**
     * 회원 이름
     * VARCHAR(20)
     */
    private String memberName;
    
    // ========================================
    // 선택 필드
    // ========================================
    
    /**
     * 전화번호
     * VARCHAR(20) NULL
     */
    private String memberPhone;
    
    /**
     * 주소
     * VARCHAR(255) NULL
     */
    private String memberAddress;
    
    /**
     * 성별 (MALE, FEMALE, OTHER)
     * VARCHAR(10) NULL
     */
    private String memberGender;
    
    /**
     * 생년월일
     * TIMESTAMP NULL
     */
    private Timestamp memberBirth;
    
    // ========================================
    // 시스템 필드
    // ========================================
    
    /**
     * 권한 (USER, ADMIN)
     * VARCHAR(20) DEFAULT 'USER'
     */
    private String memberRole;
    
    /**
     * 상태 (ACTIVE, INACTIVE, BANNED)
     * VARCHAR(20) DEFAULT 'ACTIVE'
     */
    private String memberStatus;
    
    /**
     * 생성 일시
     * TIMESTAMP AUTO
     */
    private Timestamp createdAt;
    
    /**
     * 수정 일시
     * TIMESTAMP AUTO
     */
    private Timestamp updatedAt;
    
    // ========================================
    // Constructors
    // ========================================
    
    public MemberVO() {
        // 기본값 설정
        this.memberRole = "USER";
        this.memberStatus = "ACTIVE";
    }
    
    public MemberVO(String memberId, String memberEmail, String memberPass, String memberName) {
        this();
        this.memberId = memberId;
        this.memberEmail = memberEmail;
        this.memberPass = memberPass;
        this.memberName = memberName;
    }
    
    // ========================================
    // Getters and Setters
    // ========================================
    
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
    
    // ========================================
    // toString
    // ========================================
    
    @Override
    public String toString() {
        return "MemberVO{" +
                "memberId='" + memberId + '\'' +
                ", memberEmail='" + memberEmail + '\'' +
                ", memberName='" + memberName + '\'' +
                ", memberPhone='" + memberPhone + '\'' +
                ", memberAddress='" + memberAddress + '\'' +
                ", memberGender='" + memberGender + '\'' +
                ", memberBirth=" + memberBirth +
                ", memberRole='" + memberRole + '\'' +
                ", memberStatus='" + memberStatus + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
