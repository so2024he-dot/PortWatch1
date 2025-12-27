package com.portwatch.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.MemberVO;
import com.portwatch.persistence.MemberDAO;

/**
 * ✅ 회원 Service 구현 클래스 - 완전 수정
 * 
 * 핵심 수정:
 * 1. member_id 자동 생성 (UUID)
 * 2. 비밀번호 SHA-256 해시 처리
 * 
 * @author PortWatch
 * @version FINAL FIX - Spring 5.0.7 + MySQL 8.0.33
 */
@Service
public class MemberServiceImpl implements MemberService {
    
    @Autowired
    private MemberDAO memberDAO;
    
    // 이메일 인증 코드 저장 (메모리)
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    
    /**
     * ✅ SHA-256 해시 생성
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // 바이트를 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (Exception e) {
            System.err.println("❌ 비밀번호 해시 생성 실패: " + e.getMessage());
            return password; // 실패 시 원본 반환 (fallback)
        }
    }
    
    /**
     * ✅ 고유 member_id 생성
     */
    private String generateMemberId() {
        // UUID 기반 고유 ID 생성
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 앞 12자리만 사용 (VARCHAR(50) 범위 내)
        return "M" + uuid.substring(0, 11).toUpperCase();
    }
    
    /**
     * ✅ 이메일 사용 가능 확인
     */
    @Override
    public boolean checkEmailAvailable(String email) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📧 이메일 중복 체크");
        System.out.println("  - 이메일: " + email);
        
        try {
            MemberVO member = memberDAO.selectMemberByEmail(email);
            boolean available = (member == null);
            
            if (available) {
                System.out.println("✅ 사용 가능한 이메일");
            } else {
                System.out.println("❌ 이미 사용 중인 이메일");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return available;
            
        } catch (Exception e) {
            System.err.println("❌ 이메일 체크 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("이메일 확인 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 6자리 인증 코드 생성
     */
    @Override
    public String generateVerificationCode() {
        int code = (int) (Math.random() * 900000) + 100000; // 100000~999999
        return String.valueOf(code);
    }
    
    /**
     * ✅ 인증 코드 저장
     */
    @Override
    public void saveVerificationCode(String email, String code) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💾 인증 코드 저장");
        System.out.println("  - 이메일: " + email);
        System.out.println("  - 코드: " + code);
        
        verificationCodes.put(email, code);
        
        System.out.println("✅ 인증 코드 저장 완료");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * ✅ 인증 코드 검증
     */
    @Override
    public boolean verifyCode(String email, String code) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔐 인증 코드 검증");
        System.out.println("  - 이메일: " + email);
        System.out.println("  - 입력 코드: " + code);
        
        String savedCode = verificationCodes.get(email);
        
        if (savedCode == null) {
            System.out.println("❌ 저장된 인증 코드 없음");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return false;
        }
        
        boolean isValid = savedCode.equals(code);
        
        if (isValid) {
            System.out.println("✅ 인증 성공");
            verificationCodes.remove(email); // 인증 성공 시 코드 삭제
        } else {
            System.out.println("❌ 인증 실패");
            System.out.println("  - 저장된 코드: " + savedCode);
        }
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return isValid;
    }
    
    /**
     * ✅ 회원 가입 (member_id 자동 생성 + 비밀번호 해시)
     */
    @Override
    @Transactional
    public void signup(MemberVO member) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📝 회원 가입");
        System.out.println("  - 이메일: " + member.getMemberEmail());
        System.out.println("  - 이름: " + member.getMemberName());
        
        try {
            // 1. member_id 자동 생성
            String memberId = generateMemberId();
            member.setMemberId(memberId);
            
            System.out.println("  - 생성된 ID: " + memberId);
            
            // 2. 비밀번호 해시 처리
            String originalPassword = member.getMemberPass();
            String hashedPassword = hashPassword(originalPassword);
            member.setMemberPass(hashedPassword);
            
            System.out.println("  - 원본 비밀번호: " + originalPassword);
            System.out.println("  - 해시 비밀번호: " + hashedPassword);
            
            // 3. DB 삽입
            memberDAO.insertMember(member);
            
            System.out.println("✅ 회원 가입 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 회원 가입 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("회원 가입 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 로그인 (비밀번호 해시 비교)
     */
    @Override
    public MemberVO login(String email, String password) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔐 로그인 시도");
        System.out.println("  - 이메일: " + email);
        
        try {
            // 1. 이메일로 회원 조회
            MemberVO member = memberDAO.selectMemberByEmail(email);
            
            if (member == null) {
                System.out.println("❌ 존재하지 않는 이메일");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return null;
            }
            
            // 2. 비밀번호 해시 처리
            String hashedInputPassword = hashPassword(password);
            String dbPassword = member.getMemberPass();
            
            System.out.println("  - 입력 비밀번호 해시: " + hashedInputPassword);
            System.out.println("  - DB 비밀번호 해시: " + dbPassword);
            
            // 3. 비밀번호 비교
            if (!hashedInputPassword.equals(dbPassword)) {
                System.out.println("❌ 비밀번호 불일치");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return null;
            }
            
            System.out.println("✅ 로그인 성공");
            System.out.println("  - 회원 ID: " + member.getMemberId());
            System.out.println("  - 이름: " + member.getMemberName());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return member;
            
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
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("👤 회원 정보 조회 (ID)");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            MemberVO member = memberDAO.selectMemberById(memberId);
            
            if (member != null) {
                System.out.println("✅ 회원 정보 조회 완료");
            } else {
                System.out.println("⚠️ 회원을 찾을 수 없습니다");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return member;
            
        } catch (Exception e) {
            System.err.println("❌ 회원 정보 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("회원 정보 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 회원 정보 조회 (Email)
     */
    @Override
    public MemberVO getMemberByEmail(String memberEmail) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📧 회원 정보 조회 (Email)");
        System.out.println("  - 이메일: " + memberEmail);
        
        try {
            MemberVO member = memberDAO.selectMemberByEmail(memberEmail);
            
            if (member != null) {
                System.out.println("✅ 회원 정보 조회 완료");
            } else {
                System.out.println("⚠️ 회원을 찾을 수 없습니다");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return member;
            
        } catch (Exception e) {
            System.err.println("❌ 회원 정보 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("회원 정보 조회 실패: " + e.getMessage(), e);
        }
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
     * ✅ 비밀번호 변경 (해시 처리)
     */
    @Override
    @Transactional
    public void changePassword(String memberId, String oldPassword, String newPassword) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔐 비밀번호 변경");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            // 1. 회원 정보 조회
            MemberVO member = memberDAO.selectMemberById(memberId);
            
            if (member == null) {
                throw new Exception("회원을 찾을 수 없습니다");
            }
            
            // 2. 기존 비밀번호 확인 (해시 비교)
            String hashedOldPassword = hashPassword(oldPassword);
            
            if (!hashedOldPassword.equals(member.getMemberPass())) {
                System.out.println("❌ 기존 비밀번호 불일치");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                throw new Exception("기존 비밀번호가 일치하지 않습니다");
            }
            
            // 3. 새 비밀번호로 업데이트 (해시 처리)
            String hashedNewPassword = hashPassword(newPassword);
            memberDAO.updatePassword(memberId, hashedNewPassword);
            
            System.out.println("✅ 비밀번호 변경 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 비밀번호 변경 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("비밀번호 변경 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 회원 탈퇴
     */
    @Override
    @Transactional
    public void withdrawMember(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 회원 탈퇴");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            memberDAO.deleteMember(memberId);
            
            System.out.println("✅ 회원 탈퇴 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 회원 탈퇴 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("회원 탈퇴 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 이메일 중복 체크
     */
    @Override
    public boolean isEmailDuplicate(String memberEmail) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📧 이메일 중복 체크");
        System.out.println("  - 이메일: " + memberEmail);
        
        try {
            MemberVO member = memberDAO.selectMemberByEmail(memberEmail);
            boolean isDuplicate = (member != null);
            
            if (isDuplicate) {
                System.out.println("❌ 이미 사용 중인 이메일");
            } else {
                System.out.println("✅ 사용 가능한 이메일");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return isDuplicate;
            
        } catch (Exception e) {
            System.err.println("❌ 이메일 중복 체크 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("이메일 중복 체크 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ ID 중복 체크
     */
    @Override
    public boolean isIdDuplicate(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🆔 ID 중복 체크");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            MemberVO member = memberDAO.selectMemberById(memberId);
            boolean isDuplicate = (member != null);
            
            if (isDuplicate) {
                System.out.println("❌ 이미 사용 중인 ID");
            } else {
                System.out.println("✅ 사용 가능한 ID");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return isDuplicate;
            
        } catch (Exception e) {
            System.err.println("❌ ID 중복 체크 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("ID 중복 체크 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 회원 삭제 (별칭)
     */
    @Override
    @Transactional
    public void deleteMember(String memberId) throws Exception {
        withdrawMember(memberId);
    }

    @Override
    @Transactional
    public void changePassword(String memberId, String newPassword) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔐 비밀번호 변경 (간단 버전)");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            // 1. 회원 정보 조회
            MemberVO member = memberDAO.selectMemberById(memberId);
            
            if (member == null) {
                System.out.println("❌ 회원을 찾을 수 없습니다");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                throw new Exception("회원을 찾을 수 없습니다");
            }
            
            // 2. 새 비밀번호 해시 처리
            String hashedNewPassword = hashPassword(newPassword);
            
            System.out.println("  - 원본 비밀번호: " + newPassword);
            System.out.println("  - 해시 비밀번호: " + hashedNewPassword);
            
            // 3. 비밀번호 업데이트
            memberDAO.updatePassword(memberId, hashedNewPassword);
            
            System.out.println("✅ 비밀번호 변경 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 비밀번호 변경 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("비밀번호 변경 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ SHA-256 해시 생성 (비밀번호 해시용)
     * 
     * 이미 MemberServiceImpl.java에 있는 메서드를 사용합니다.
     * 없다면 아래 코드를 추가하세요.
     */
    private String hashPassword1(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // 바이트를 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (Exception e) {
            System.err.println("❌ 비밀번호 해시 생성 실패: " + e.getMessage());
            return password; // 실패 시 원본 반환 (fallback)
        }
    }
}
