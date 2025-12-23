package com.portwatch.domain;

import java.sql.Timestamp;

/**
 * 회원 VO (Value Object)
 * 
 * ✅ String memberId 사용 (이메일 기반 로그인)
 * @author PortWatch
 * @version 2.0 - Spring 5.0.7 + MySQL 8.0.33 호환
 */
public class MemberVO {
    
    private String memberId;        // 회원 ID (이메일)
    private String password;        // 비밀번호
    private String name;           // 회원 이름
    private String phone;          // 전화번호
    private String email;          // 이메일
    private Timestamp createdAt;   // 가입일시
    private Timestamp updatedAt;   // 수정일시
    private String status;         // 회원 상태 (ACTIVE, INACTIVE, SUSPENDED)
    private String role;           // 회원 권한 (USER, ADMIN)
    
    // 기본 생성자
    public MemberVO() {
    }
    
    // 전체 생성자
    public MemberVO(String memberId, String password, String name, String phone, String email, 
                   Timestamp createdAt, Timestamp updatedAt, String status, String role) {
        this.memberId = memberId;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.role = role;
    }
    
    // Getters and Setters
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }    
   
	public String getMemberEmail() {
		
		return getMemberEmail();
	}

	public String getMemberName() {
		
		return getMemberName();
	}

	public String getMemberPhone() {
		
		return getMemberPhone();
	}

	public Object getMemberPass() {
		
		return getMemberPass();
	}

	@Override
	public String toString() {
		return "MemberVO [memberId=" + memberId + ", password=" + password + ", name=" + name + ", phone=" + phone
				+ ", email=" + email + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", status=" + status
				+ ", role=" + role + "]";
	}
	
	
}
