package com.portwatch.service;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.MemberVO;
import com.portwatch.persistence.MemberDAO;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * MemberServiceImpl - 완전 구현 버전 (Spring 5.0.7 + MySQL 8.0.33)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 수정 내역:
 * - Line 243: changePassword(3개 파라미터) 반환 타입을 void -> boolean 변경
 * - 현재 비밀번호 불일치 시 Exception 대신 false 반환
 * 
 * @author PortWatch
 * @version FIXED - 2026.01.16
 */
@Service
public class MemberServiceImpl implements MemberService {
    
    @Autowired
    private MemberDAO memberDAO;
    
    // ✅ 인증 코드 임시 저장소 (실제 프로덕션에서는 Redis 사용 권장)
    private final java.util.Map<String, VerificationCode> verificationCodes = 
        new java.util.concurrent.ConcurrentHashMap<>();
    
    /**
     * ✅ 인증 코드 클래스 (내부 클래스)
     */
    private static class VerificationCode {
        String code;
        long expiryTime;
        
        VerificationCode(String code, long expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
    
    /**
     * ✅ 회원가입 - member_id 자동 생성 추가!
     */
    @Override
    @Transactional
    public void signup(MemberVO member) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📝 회원가입");
        System.out.println("  - 이메일: " + member.getMemberEmail());
        
        try {
            // ✅ 1. member_id가 없으면 자동 생성
            if (member.getMemberId() == null || member.getMemberId().trim().isEmpty()) {
                String generatedId = generateMemberId(member.getMemberEmail());
                member.setMemberId(generatedId);
                System.out.println("  - 자동 생성된 회원 ID: " + generatedId);
            } else {
                System.out.println("  - 회원 ID: " + member.getMemberId());
            }
            
            // ✅ 2. 비밀번호 해시
            String hashedPassword = hashPassword(member.getMemberPass());
            member.setMemberPass(hashedPassword);
            
            // ✅ 3. 기본값 설정
            if (member.getMemberRole() == null || member.getMemberRole().trim().isEmpty()) {
                member.setMemberRole("USER");
            }
            if (member.getMemberStatus() == null || member.getMemberStatus().trim().isEmpty()) {
                member.setMemberStatus("ACTIVE");
            }
            
            // ✅ 4. 회원 등록
            memberDAO.insertMember(member);
            
            System.out.println("✅ 회원가입 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 회원가입 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("회원가입 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ member_id 자동 생성 (이메일 기반 + 타임스탬프)
     */
    private String generateMemberId(String email) {
        if (email == null || email.isEmpty()) {
            return "user_" + UUID.randomUUID().toString().substring(0, 8);
        }
        
        String localPart = email.split("@")[0];
        String cleanedPart = localPart.replaceAll("[^a-zA-Z0-9]", "");
        long timestamp = System.currentTimeMillis();
        String timestampStr = String.valueOf(timestamp).substring(3);
        String memberId = cleanedPart + "_" + timestampStr;
        
        if (memberId.length() > 50) {
            memberId = memberId.substring(0, 50);
        }
        
        return memberId;
    }
    
    /**
     * ✅ 이메일 중복 체크
     */
    @Override
    public boolean isEmailDuplicate(String email) throws Exception {
        MemberVO member = memberDAO.selectByEmail(email);
        boolean isDuplicate = (member != null);
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📧 이메일 중복 체크");
        System.out.println("  - 이메일: " + email);
        System.out.println("  - 중복 여부: " + (isDuplicate ? "중복됨" : "사용 가능"));
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return isDuplicate;
    }
    
    /**
     * ✅ 아이디 중복 체크
     */
    @Override
    public boolean isIdDuplicate(String memberId) throws Exception {
        if (memberId == null || memberId.trim().isEmpty()) {
            return false;
        }
        
        MemberVO member = memberDAO.selectById(memberId);
        boolean isDuplicate = (member != null);
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🆔 아이디 중복 체크");
        System.out.println("  - 아이디: " + memberId);
        System.out.println("  - 중복 여부: " + (isDuplicate ? "중복됨" : "사용 가능"));
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return isDuplicate;
    }
    
    /**
     * ✅ 로그인
     */
    @Override
    public MemberVO login(String email, String password) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔐 로그인");
        System.out.println("  - 이메일: " + email);
        
        try {
            MemberVO member = memberDAO.selectByEmail(email);
            
            if (member == null) {
                System.out.println("❌ 존재하지 않는 이메일");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return null;
            }
            
            String hashedPassword = hashPassword(password);
            
            if (hashedPassword.equals(member.getMemberPass())) {
                System.out.println("✅ 로그인 성공");
                System.out.println("  - 회원 ID: " + member.getMemberId());
                System.out.println("  - 이름: " + member.getMemberName());
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return member;
            } else {
                System.out.println("❌ 비밀번호 불일치");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("❌ 로그인 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("로그인 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 회원 정보 조회 (ID)
     */
    @Override
    public MemberVO getMemberById(String memberId) throws Exception {
        return memberDAO.selectById(memberId);
    }
    
    /**
     * ✅ 회원 정보 조회 (Email)
     */
    @Override
    public MemberVO getMemberByEmail(String email) throws Exception {
        return memberDAO.selectByEmail(email);
    }
    
    /**
     * ✅ 회원 정보 수정
     */
    @Override
    @Transactional
    public void updateMember(MemberVO member) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✏️ 회원 정보 수정");
        System.out.println("  - 회원 ID: " + member.getMemberId());
        
        try {
            memberDAO.updateMember(member);
            
            System.out.println("✅ 회원 정보 수정 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 회원 정보 수정 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("회원 정보 수정 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 비밀번호 변경 (현재 비밀번호 확인 포함) - 반환 타입 boolean으로 수정!
     * 
     * @param memberId 회원 ID
     * @param oldPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     * @return true: 성공, false: 현재 비밀번호 불일치
     */
    @Override
    @Transactional
    public boolean changePassword(String memberId, String oldPassword, String newPassword) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔑 비밀번호 변경");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            MemberVO member = memberDAO.selectById(memberId);
            
            if (member == null) {
                System.out.println("❌ 회원 정보를 찾을 수 없습니다.");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                throw new Exception("회원 정보를 찾을 수 없습니다.");
            }
            
            String hashedOldPassword = hashPassword(oldPassword);
            
            // ✅ 현재 비밀번호 불일치 시 false 반환 (Exception X)
            if (!hashedOldPassword.equals(member.getMemberPass())) {
                System.out.println("❌ 현재 비밀번호가 일치하지 않습니다.");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return false;
            }
            
            String hashedNewPassword = hashPassword(newPassword);
            memberDAO.updatePassword(memberId, hashedNewPassword);
            
            System.out.println("✅ 비밀번호 변경 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ 비밀번호 변경 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw e;
        }
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ✅ 추가 메서드들
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * ✅ 회원 탈퇴 (소프트 삭제)
     */
    @Override
    @Transactional
    public void withdrawMember(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🚪 회원 탈퇴");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            MemberVO member = memberDAO.selectById(memberId);
            
            if (member == null) {
                throw new Exception("회원 정보를 찾을 수 없습니다.");
            }
            
            // 회원 상태를 INACTIVE로 변경 (소프트 삭제)
            member.setMemberStatus("INACTIVE");
            memberDAO.updateMember(member);
            
            System.out.println("✅ 회원 탈퇴 완료 (상태: INACTIVE)");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 회원 탈퇴 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("회원 탈퇴 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 이메일 사용 가능 여부 확인
     */
    @Override
    public boolean checkEmailAvailable(String email) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📧 이메일 사용 가능 여부 확인");
        System.out.println("  - 이메일: " + email);
        
        try {
            boolean isAvailable = !isEmailDuplicate(email);
            
            System.out.println("  - 사용 가능: " + (isAvailable ? "YES" : "NO"));
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return isAvailable;
            
        } catch (Exception e) {
            System.err.println("❌ 확인 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw e;
        }
    }
    
    /**
     * ✅ 인증 코드 생성
     */
    @Override
    public String generateVerificationCode() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔢 인증 코드 생성");
        
        try {
            Random random = new Random();
            int code = 100000 + random.nextInt(900000); // 6자리 숫자
            String verificationCode = String.valueOf(code);
            
            System.out.println("  - 생성된 코드: " + verificationCode);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return verificationCode;
            
        } catch (Exception e) {
            System.err.println("❌ 코드 생성 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("인증 코드 생성 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 인증 코드 검증
     */
    @Override
    public boolean verifyCode(String email, String code) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 인증 코드 검증");
        System.out.println("  - 이메일: " + email);
        System.out.println("  - 입력 코드: " + code);
        
        try {
            VerificationCode stored = verificationCodes.get(email);
            
            if (stored == null) {
                System.out.println("❌ 인증 코드 없음");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return false;
            }
            
            if (stored.isExpired()) {
                verificationCodes.remove(email);
                System.out.println("❌ 인증 코드 만료");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return false;
            }
            
            boolean isValid = stored.code.equals(code);
            
            if (isValid) {
                verificationCodes.remove(email); // 인증 성공 시 삭제
                System.out.println("✅ 인증 성공");
            } else {
                System.out.println("❌ 인증 실패 (코드 불일치)");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return isValid;
            
        } catch (Exception e) {
            System.err.println("❌ 검증 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("인증 코드 검증 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 인증 코드 저장
     */
    @Override
    public void saveVerificationCode(String email, String code) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💾 인증 코드 저장");
        System.out.println("  - 이메일: " + email);
        System.out.println("  - 코드: " + code);
        
        try {
            long expiryTime = System.currentTimeMillis() + (5 * 60 * 1000); // 5분
            verificationCodes.put(email, new VerificationCode(code, expiryTime));
            
            System.out.println("✅ 인증 코드 저장 완료 (유효 시간: 5분)");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 저장 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("인증 코드 저장 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 비밀번호 변경 (현재 비밀번호 확인 없이)
     */
    @Override
    @Transactional
    public void changePassword(String memberId, String newPassword) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔑 비밀번호 변경 (직접)");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            MemberVO member = memberDAO.selectById(memberId);
            
            if (member == null) {
                throw new Exception("회원 정보를 찾을 수 없습니다.");
            }
            
            String hashedPassword = hashPassword(newPassword);
            memberDAO.updatePassword(memberId, hashedPassword);
            
            System.out.println("✅ 비밀번호 변경 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 비밀번호 변경 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("비밀번호 변경 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 회원 삭제 (하드 삭제)
     */
    @Override
    @Transactional
    public void deleteMember(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 회원 삭제 (하드 삭제)");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            MemberVO member = memberDAO.selectById(memberId);
            
            if (member == null) {
                throw new Exception("회원 정보를 찾을 수 없습니다.");
            }
            
            memberDAO.deleteMember(memberId);
            
            System.out.println("✅ 회원 삭제 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 회원 삭제 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("회원 삭제 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 전체 회원 조회
     */
    @Override
    public List<MemberVO> getAllMembers() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📋 전체 회원 조회");
        
        try {
            List<MemberVO> members = memberDAO.selectAllMembers();
            
            System.out.println("  - 회원 수: " + (members != null ? members.size() : 0));
            System.out.println("✅ 전체 회원 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return members;
            
        } catch (Exception e) {
            System.err.println("❌ 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("전체 회원 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 비밀번호 해시 (MD5)
     */
    private String hashPassword(String password) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
            
        } catch (Exception e) {
            throw new Exception("비밀번호 해시 실패: " + e.getMessage(), e);
        }
    }
}
