package com.portwatch.domain;

import java.sql.Timestamp;

/**
 * MemberVO - 최종 확정본
 *
 * ✅ 파일 위치 (반드시 이 경로!):
 *    src/main/java/com/portwatch/domain/MemberVO.java
 *
 * ✅ 해결하는 오류:
 *    ClassNotFoundException: com.portwatch.domain.MemberVO
 *    → mybatis-config.xml typeAlias 등록 실패
 *    → sqlSessionFactory 생성 실패
 *    → HTTP 500 서버 내부 오류
 *
 * ✅ DB 컬럼 매핑 (MEMBER 테이블 수정 후):
 *    member_id, member_email, member_pass, member_name,
 *    member_phone, member_address, member_gender, member_birth,
 *    member_role, member_status, balance, created_at, updated_at
 */
public class MemberVO {

    private String    memberId;       // member_id      VARCHAR(50) PK
    private String    memberEmail;    // member_email   VARCHAR(100) UNIQUE
    private String    memberPass;     // member_pass    VARCHAR(200) BCrypt
    private String    memberName;     // member_name    VARCHAR(20)
    private String    memberPhone;    // member_phone   VARCHAR(20) NULL
    private String    memberAddress;  // member_address VARCHAR(255) NULL
    private String    memberGender;   // member_gender  VARCHAR(10) NULL
    private Timestamp memberBirth;    // member_birth   TIMESTAMP NULL
    private String    memberRole;     // member_role    VARCHAR(20) DEFAULT 'USER'
    private String    memberStatus;   // member_status  VARCHAR(20) DEFAULT 'ACTIVE'
    private double    balance;        // balance        DOUBLE DEFAULT 1000000
    private Timestamp createdAt;      // created_at     TIMESTAMP AUTO
    private Timestamp updatedAt;      // updated_at     TIMESTAMP AUTO

    // ── 기본 생성자 ──────────────────────────────────
    public MemberVO() {
        this.memberRole   = "USER";
        this.memberStatus = "ACTIVE";
        this.balance      = 1_000_000.0;
    }

    // ── 생성자 ───────────────────────────────────────
    public MemberVO(String memberId, String memberEmail,
                    String memberPass, String memberName) {
        this();
        this.memberId    = memberId;
        this.memberEmail = memberEmail;
        this.memberPass  = memberPass;
        this.memberName  = memberName;
    }

    // ── Getter / Setter ──────────────────────────────

    public String getMemberId()                    { return memberId; }
    public void   setMemberId(String v)            { this.memberId = v; }

    public String getMemberEmail()                 { return memberEmail; }
    public void   setMemberEmail(String v)         { this.memberEmail = v; }

    public String getMemberPass()                  { return memberPass; }
    public void   setMemberPass(String v)          { this.memberPass = v; }

    public String getMemberName()                  { return memberName; }
    public void   setMemberName(String v)          { this.memberName = v; }

    public String getMemberPhone()                 { return memberPhone; }
    public void   setMemberPhone(String v)         { this.memberPhone = v; }

    public String getMemberAddress()               { return memberAddress; }
    public void   setMemberAddress(String v)       { this.memberAddress = v; }

    public String getMemberGender()                { return memberGender; }
    public void   setMemberGender(String v)        { this.memberGender = v; }

    public Timestamp getMemberBirth()              { return memberBirth; }
    public void      setMemberBirth(Timestamp v)   { this.memberBirth = v; }

    public String getMemberRole()                  { return memberRole; }
    public void   setMemberRole(String v)          { this.memberRole = v; }

    public String getMemberStatus()                { return memberStatus; }
    public void   setMemberStatus(String v)        { this.memberStatus = v; }

    public double getBalance()                     { return balance; }
    public void   setBalance(double v)             { this.balance = v; }

    public Timestamp getCreatedAt()                { return createdAt; }
    public void      setCreatedAt(Timestamp v)     { this.createdAt = v; }

    public Timestamp getUpdatedAt()                { return updatedAt; }
    public void      setUpdatedAt(Timestamp v)     { this.updatedAt = v; }

    @Override
    public String toString() {
        return "MemberVO{memberId='" + memberId + "', email='" + memberEmail
             + "', name='" + memberName + "', role='" + memberRole
             + "', balance=" + balance + "}";
    }
}
