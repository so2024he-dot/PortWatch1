package com.portwatch.domain;

import javax.validation.constraints.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import java.sql.Timestamp;

/**
 * 회원 VO
 * 현재 MySQL DDL에 완벽히 맞춤
 */
public class MemberVO {
    
    // 기본 정보
    private Integer memberId;
    
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String memberEmail;
    
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 4, max = 20, message = "비밀번호는 4~20자 사이여야 합니다.")
    private String memberPass;
    
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String memberName;
    
    private String memberPhone;
    
    // 시간 정보
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // 기본 생성자
    public MemberVO() {}
    
    // Getters and Setters
    public Integer getMemberId() {
        return memberId;
    }
    
    public void setMemberId(Integer memberId) {
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
        return "MemberVO{" +
                "memberId=" + memberId +
                ", memberEmail='" + memberEmail + '\'' +
                ", memberName='" + memberName + '\'' +
                ", memberPhone='" + memberPhone + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
