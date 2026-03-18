package com.portwatch.domain;

import java.sql.Timestamp;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * MemberVO - 최종 병합본 (FINAL MERGED VERSION)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 * ✅ 병합 기준:
 *   - Version B (실제 MEMBER 테이블 완전 매핑) 기반 유지
 *   - balance 필드 추가 (MemberServiceImpl, StockPurchaseValidationService 에서 필요)
 *
 * ✅ 해결된 컴파일 오류 (4개):
 *   [ERROR] MemberServiceImpl.java:[99]  cannot find symbol: method getBalance()
 *   [ERROR] MemberServiceImpl.java:[100] cannot find symbol: method setBalance(double)
 *   [ERROR] MemberServiceImpl.java:[202] cannot find symbol: method getBalance()
 *   → balance 필드 + getter/setter 추가로 해결
 *
 * ✅ 실제 MEMBER 테이블 구조 (AWS MySQL 8.0):
 *   member_id      VARCHAR(50)  PK
 *   member_email   VARCHAR(100) UNIQUE NOT NULL
 *   member_pass    VARCHAR(200) NOT NULL
 *   member_name    VARCHAR(20)  NOT NULL
 *   member_phone   VARCHAR(20)  NULL
 *   member_address VARCHAR(255) NULL
 *   member_gender  VARCHAR(10)  NULL
 *   member_birth   TIMESTAMP    NULL
 *   member_role    VARCHAR(20)  DEFAULT 'USER'
 *   member_status  VARCHAR(20)  DEFAULT 'ACTIVE'
 *   balance        DOUBLE       DEFAULT 1000000  ← 포트폴리오 보유 현금
 *   created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
 *   updated_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 *
 * ✅ MyBatis mapUnderscoreToCamelCase=true 설정으로 자동 매핑:
 *   member_id → memberId
 *   member_role → memberRole
 *   created_at → createdAt  등 자동 처리
 */
public class MemberVO {

    // ════════════════════════════════════════
    // 필수 필드 (Required Fields)
    // ════════════════════════════════════════

    /** 회원 ID - PK, VARCHAR(50) */
    private String memberId;

    /** 이메일 - VARCHAR(100), UNIQUE */
    private String memberEmail;

    /** 비밀번호 - VARCHAR(200), BCrypt 암호화 */
    private String memberPass;

    /** 회원 이름 - VARCHAR(20) */
    private String memberName;

    // ════════════════════════════════════════
    // 선택 필드 (Optional Fields)
    // ════════════════════════════════════════

    /** 전화번호 - VARCHAR(20), NULL 허용 */
    private String memberPhone;

    /** 주소 - VARCHAR(255), NULL 허용 */
    private String memberAddress;

    /** 성별 - VARCHAR(10): MALE / FEMALE / OTHER */
    private String memberGender;

    /** 생년월일 - TIMESTAMP, NULL 허용 */
    private Timestamp memberBirth;

    // ════════════════════════════════════════
    // 시스템 필드 (System Fields)
    // ════════════════════════════════════════

    /** 권한 - VARCHAR(20): USER / ADMIN, DEFAULT 'USER' */
    private String memberRole;

    /** 상태 - VARCHAR(20): ACTIVE / INACTIVE / BANNED, DEFAULT 'ACTIVE' */
    private String memberStatus;

    /**
     * 보유 현금 잔액 - DOUBLE, DEFAULT 1000000
     *
     * ✅ 병합 포인트:
     *   Version A 에 있던 balance 필드.
     *   MemberServiceImpl.java 및 StockPurchaseValidationService.java에서 사용.
     *   포트폴리오 주식 매수/매도 시 잔액 증감 처리에 필요.
     */
    private double balance;

    /** 계정 생성 일시 - TIMESTAMP, AUTO */
    private Timestamp createdAt;

    /** 계정 수정 일시 - TIMESTAMP, AUTO (ON UPDATE) */
    private Timestamp updatedAt;

    // ════════════════════════════════════════
    // 생성자 (Constructors)
    // ════════════════════════════════════════

    /** 기본 생성자 - 기본값 자동 설정 */
    public MemberVO() {
        this.memberRole   = "USER";
        this.memberStatus = "ACTIVE";
        this.balance      = 1_000_000.0;  // 초기 잔액 100만원
    }

    /** 필수 필드 생성자 */
    public MemberVO(String memberId, String memberEmail,
                    String memberPass, String memberName) {
        this();
        this.memberId    = memberId;
        this.memberEmail = memberEmail;
        this.memberPass  = memberPass;
        this.memberName  = memberName;
    }

    // ════════════════════════════════════════
    // Getter / Setter
    // ════════════════════════════════════════

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getMemberEmail() { return memberEmail; }
    public void setMemberEmail(String memberEmail) { this.memberEmail = memberEmail; }

    public String getMemberPass() { return memberPass; }
    public void setMemberPass(String memberPass) { this.memberPass = memberPass; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getMemberPhone() { return memberPhone; }
    public void setMemberPhone(String memberPhone) { this.memberPhone = memberPhone; }

    public String getMemberAddress() { return memberAddress; }
    public void setMemberAddress(String memberAddress) { this.memberAddress = memberAddress; }

    public String getMemberGender() { return memberGender; }
    public void setMemberGender(String memberGender) { this.memberGender = memberGender; }

    public Timestamp getMemberBirth() { return memberBirth; }
    public void setMemberBirth(Timestamp memberBirth) { this.memberBirth = memberBirth; }

    public String getMemberRole() { return memberRole; }
    public void setMemberRole(String memberRole) { this.memberRole = memberRole; }

    public String getMemberStatus() { return memberStatus; }
    public void setMemberStatus(String memberStatus) { this.memberStatus = memberStatus; }

    /** ✅ 추가된 메서드: MemberServiceImpl, StockPurchaseValidationService 에서 필요 */
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // ════════════════════════════════════════
    // toString
    // ════════════════════════════════════════

    @Override
    public String toString() {
        return "MemberVO{" +
                "memberId='"    + memberId    + '\'' +
                ", memberEmail='" + memberEmail + '\'' +
                ", memberName='"  + memberName  + '\'' +
                ", memberPhone='" + memberPhone + '\'' +
                ", memberRole='"  + memberRole  + '\'' +
                ", memberStatus='"+ memberStatus+ '\'' +
                ", balance="      + balance     +
                ", createdAt="    + createdAt   +
                '}';
    }
}
