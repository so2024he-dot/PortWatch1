package com.portwatch.domain;

import java.sql.Timestamp;

/**
 * MemberVO - 최종 확정본
 *
 * =====================================================================
 * [오류 분석 - 영어 / English Analysis]
 *
 *  Maven install: BUILD SUCCESS
 *    → Java compile succeeded, WAR file created at:
 *      target/portwatch-1.0.0-BUILD-SNAPSHOT.war
 *
 *  Tomcat Console: ClassNotFoundException: com.portwatch.domain.MemberVO
 *    → Root cause: STS WTP (Web Tools Platform) deploys to:
 *      C:\workspace-sts\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\
 *      This folder has a CACHED old version of MemberVO.class
 *      (or NO MemberVO.class if it was previously in a different package)
 *    → Solution: Delete wtpwebapps folder, restart STS
 *
 * [오류 분석 - 한글]
 *
 *  Maven install: BUILD SUCCESS
 *    → 컴파일 성공, WAR 파일 생성됨
 *      target/portwatch-1.0.0-BUILD-SNAPSHOT.war
 *
 *  Tomcat 오류: ClassNotFoundException: MemberVO
 *    → STS "Run on Server"는 WAR를 배포하는 게 아니라
 *      WTP 경로에 직접 배포함:
 *      C:\workspace-sts\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\
 *    → 이 폴더에 이전 버전 MemberVO.class 또는 다른 패키지의 파일이 캐시됨
 *    → wtpwebapps 폴더 삭제 후 STS 재시작 필요
 *
 * =====================================================================
 *
 * ✅ 실제 MEMBER 테이블 완전 매핑 + balance 필드 포함
 */
public class MemberVO {

    // ── 필수 필드 ────────────────────────────────────────
    private String    memberId;       // member_id      VARCHAR(50)  PK
    private String    memberEmail;    // member_email   VARCHAR(100)
    private String    memberPass;     // member_pass    VARCHAR(200) BCrypt
    private String    memberName;     // member_name    VARCHAR(20)

    // ── 선택 필드 ────────────────────────────────────────
    private String    memberPhone;    // member_phone   VARCHAR(20)
    private String    memberAddress;  // member_address VARCHAR(255)
    private String    memberGender;   // member_gender  VARCHAR(10)
    private Timestamp memberBirth;    // member_birth   TIMESTAMP

    // ── 시스템 필드 ──────────────────────────────────────
    private String    memberRole;     // member_role    VARCHAR(20)  DEFAULT 'USER'
    private String    memberStatus;   // member_status  VARCHAR(20)  DEFAULT 'ACTIVE'
    private double    balance;        // balance        DOUBLE       DEFAULT 1000000
    private Timestamp createdAt;      // created_at     TIMESTAMP
    private Timestamp updatedAt;      // updated_at     TIMESTAMP

    // ── 생성자 ───────────────────────────────────────────
    public MemberVO() {
        this.memberRole   = "USER";
        this.memberStatus = "ACTIVE";
        this.balance      = 1_000_000.0;
    }

    public MemberVO(String memberId, String memberEmail,
                    String memberPass, String memberName) {
        this();
        this.memberId    = memberId;
        this.memberEmail = memberEmail;
        this.memberPass  = memberPass;
        this.memberName  = memberName;
    }

    // ── Getter / Setter ──────────────────────────────────

    public String getMemberId()                    { return memberId; }
    public void   setMemberId(String memberId)     { this.memberId = memberId; }

    public String getMemberEmail()                         { return memberEmail; }
    public void   setMemberEmail(String memberEmail)       { this.memberEmail = memberEmail; }

    public String getMemberPass()                          { return memberPass; }
    public void   setMemberPass(String memberPass)         { this.memberPass = memberPass; }

    public String getMemberName()                          { return memberName; }
    public void   setMemberName(String memberName)         { this.memberName = memberName; }

    public String getMemberPhone()                         { return memberPhone; }
    public void   setMemberPhone(String memberPhone)       { this.memberPhone = memberPhone; }

    public String getMemberAddress()                       { return memberAddress; }
    public void   setMemberAddress(String memberAddress)   { this.memberAddress = memberAddress; }

    public String getMemberGender()                        { return memberGender; }
    public void   setMemberGender(String memberGender)     { this.memberGender = memberGender; }

    public Timestamp getMemberBirth()                      { return memberBirth; }
    public void      setMemberBirth(Timestamp memberBirth) { this.memberBirth = memberBirth; }

    public String getMemberRole()                          { return memberRole; }
    public void   setMemberRole(String memberRole)         { this.memberRole = memberRole; }

    public String getMemberStatus()                        { return memberStatus; }
    public void   setMemberStatus(String memberStatus)     { this.memberStatus = memberStatus; }

    /** ✅ balance - MemberServiceImpl, StockPurchaseValidationService 에서 필수 */
    public double getBalance()               { return balance; }
    public void   setBalance(double balance) { this.balance = balance; }

    public Timestamp getCreatedAt()                    { return createdAt; }
    public void      setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt()                    { return updatedAt; }
    public void      setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "MemberVO{memberId='" + memberId + "', email='" + memberEmail +
               "', name='" + memberName + "', role='" + memberRole +
               "', balance=" + balance + "}";
    }
}
