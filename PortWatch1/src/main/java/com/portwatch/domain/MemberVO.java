package com.portwatch.domain;

import java.sql.Date;
import java.sql.Timestamp;
import javax.validation.constraints.*;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class MemberVO {
    private Integer memberId;
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String memberEmail;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, max = 100, message = "비밀번호는 6자 이상이어야 합니다")
    private String memberPass;
    
    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다")
    private String memberName;
    
    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다")
    private String memberPhone;
    
    private String memberGender; // 'M', 'F', 'O'
    private Date memberBirth;
    private Timestamp memberRegDate;
    private String memberStatus; // 'ACTIVE', 'INACTIVE', 'BANNED'
    
    // 기본 생성자
    public MemberVO() {
        this.memberStatus = "ACTIVE"; // 기본값
    }
    
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
    
    public String getMemberGender() {
        return memberGender;
    }
    
    public void setMemberGender(String memberGender) {
        this.memberGender = memberGender;
    }
    
    public Date getMemberBirth() {
        return memberBirth;
    }
    
    public void setMemberBirth(Date memberBirth) {
        this.memberBirth = memberBirth;
    }
    
    public Timestamp getMemberRegDate() {
        return memberRegDate;
    }
    
    public void setMemberRegDate(Timestamp memberRegDate) {
        this.memberRegDate = memberRegDate;
    }
    
    public String getMemberStatus() {
        return memberStatus;
    }
    
    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }
    
    @Override
    public String toString() {
        return "MemberVO{" +
                "memberId=" + memberId +
                ", memberEmail='" + memberEmail + '\'' +
                ", memberName='" + memberName + '\'' +
                ", memberPhone='" + memberPhone + '\'' +
                ", memberGender='" + memberGender + '\'' +
                ", memberStatus='" + memberStatus + '\'' +
                '}';
    }
}
