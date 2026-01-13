package com.portwatch.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.MemberVO;
import com.portwatch.persistence.MemberDAO;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * MemberServiceImpl - TODO 메서드 완전 구현
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 추가 구현:
 * 1. withdrawMember - 회원 탈퇴
 * 2. checkEmailAvailable - 이메일 사용 가능 확인
 * 3. generateVerificationCode - 인증 코드 생성
 * 4. verifyCode - 인증 코드 확인
 * 5. saveVerificationCode - 인증 코드 저장
 * 6. changePassword (String, String) - 비밀번호 변경 (오버로드)
 * 
 * @version ULTIMATE COMPLETE
 */
@Service
public class MemberServiceImpl implements MemberService {
    
    @Autowired
    private MemberDAO memberDAO;
    
    // 인증 코드 저장소 (실제로는 Redis 또는 DB 사용 권장)
    private Map<String, String> verificationCodes = new HashMap<>();
    
    /**
     * ✅ 로그인
     */
    @Override
    public MemberVO login(String memberEmail, String memberPass) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔐 로그인 시도");
        System.out.println("  - 이메일: " + memberEmail);
        
        try {
            String hashedPassword = hashPassword(memberPass);
            MemberVO member = memberDAO.login(memberEmail, hashedPassword);
            
            if (member != null) {
                System.out.println("✅ 로그인 성공 - Member ID: " + member.getMemberId());
            } else {
                System.out.println("❌ 로그인 실패 - 이메일 또는 비밀번호 불일치");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return member;
            
        } catch (Exception e) {
            System.err.println("❌ 로그인 처리 중 오류: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("로그인 처리 중 오류 발생: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 회원가입
     */
    @Override
    @Transactional
    public void signup(MemberVO member) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📝 회원가입");
        System.out.println("  - 이메일: " + member.getMemberEmail());
        System.out.println("  - 회원 ID: " + member.getMemberId());
        
        try {
            String hashedPassword = hashPassword(member.getMemberPass());
            member.setMemberPass(hashedPassword);
            
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
     * ✅ 이메일 중복 체크
     */
    @Override
    public boolean isEmailDuplicate(String memberEmail) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 이메일 중복 체크");
        System.out.println("  - 이메일: " + memberEmail);
        
        try {
            int count = memberDAO.checkEmailDuplicate(memberEmail);
            boolean isDuplicate = (count > 0);
            
            if (isDuplicate) {
                System.out.println("❌ 중복된 이메일");
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
        System.out.println("🔍 ID 중복 체크");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            int count = memberDAO.checkIdDuplicate(memberId);
            boolean isDuplicate = (count > 0);
            
            if (isDuplicate) {
                System.out.println("❌ 중복된 ID");
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
     * ✅ 회원 조회 (ID로)
     */
    @Override
    public MemberVO getMemberById(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 회원 조회");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            MemberVO member = memberDAO.selectMemberById(memberId);
            
            if (member != null) {
                System.out.println("✅ 회원 조회 완료");
                System.out.println("  - 이름: " + member.getMemberName());
                System.out.println("  - 이메일: " + member.getMemberEmail());
            } else {
                System.out.println("⚠️ 회원을 찾을 수 없습니다");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return member;
            
        } catch (Exception e) {
            System.err.println("❌ 회원 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("회원 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 회원 조회 (이메일로)
     */
    @Override
    public MemberVO getMemberByEmail(String memberEmail) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 회원 조회 (이메일)");
        System.out.println("  - 이메일: " + memberEmail);
        
        try {
            MemberVO member = memberDAO.selectMemberByEmail(memberEmail);
            
            if (member != null) {
                System.out.println("✅ 회원 조회 완료");
                System.out.println("  - 회원 ID: " + member.getMemberId());
                System.out.println("  - 이름: " + member.getMemberName());
            } else {
                System.out.println("⚠️ 회원을 찾을 수 없습니다");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return member;
            
        } catch (Exception e) {
            System.err.println("❌ 회원 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("회원 조회 실패: " + e.getMessage(), e);
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
            List<MemberVO> memberList = memberDAO.selectAllMembers();
            
            System.out.println("✅ 전체 회원 조회 완료");
            System.out.println("  - 회원 수: " + memberList.size());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return memberList;
            
        } catch (Exception e) {
            System.err.println("❌ 전체 회원 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("전체 회원 조회 실패: " + e.getMessage(), e);
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
     * ✅ 비밀번호 변경 (3개 파라미터) - boolean 반환
     */
    @Override
    @Transactional
    public boolean changePassword(String memberId, String oldPassword, String newPassword) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔐 비밀번호 변경 (3개 파라미터)");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            MemberVO member = memberDAO.selectMemberById(memberId);
            
            if (member == null) {
                System.out.println("❌ 회원을 찾을 수 없습니다");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return false;
            }
            
            String hashedOldPassword = hashPassword(oldPassword);
            
            if (!hashedOldPassword.equals(member.getMemberPass())) {
                System.out.println("❌ 기존 비밀번호 불일치");
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
            throw new Exception("비밀번호 변경 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ⭐ 비밀번호 변경 (2개 파라미터) - void 반환 (새로 구현!)
     */
    @Override
    @Transactional
    public void changePassword(String memberId, String newPassword) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔐 비밀번호 변경 (2개 파라미터)");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            MemberVO member = memberDAO.selectMemberById(memberId);
            
            if (member == null) {
                throw new Exception("회원을 찾을 수 없습니다");
            }
            
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
     * ✅ 회원 삭제
     */
    @Override
    @Transactional
    public void deleteMember(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 회원 삭제");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
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
     * ⭐ 회원 탈퇴 (새로 구현!)
     */
    @Override
    @Transactional
    public void withdrawMember(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("👋 회원 탈퇴");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            // 탈퇴 전 관련 데이터 삭제 (포트폴리오, 관심종목 등)
            // TODO: 필요시 추가 삭제 로직 구현
            
            // 회원 삭제
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
     * ⭐ 이메일 사용 가능 확인 (새로 구현!)
     */
    @Override
    public boolean checkEmailAvailable(String email) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✉️ 이메일 사용 가능 확인");
        System.out.println("  - 이메일: " + email);
        
        try {
            // 중복이면 사용 불가(false), 중복 아니면 사용 가능(true)
            boolean isDuplicate = isEmailDuplicate(email);
            boolean isAvailable = !isDuplicate;
            
            if (isAvailable) {
                System.out.println("✅ 사용 가능한 이메일");
            } else {
                System.out.println("❌ 이미 사용 중인 이메일");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return isAvailable;
            
        } catch (Exception e) {
            System.err.println("❌ 이메일 확인 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("이메일 확인 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ⭐ 인증 코드 생성 (새로 구현!)
     */
    @Override
    public String generateVerificationCode() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔢 인증 코드 생성");
        
        try {
            // 6자리 랜덤 숫자 생성
            SecureRandom random = new SecureRandom();
            int code = 100000 + random.nextInt(900000);
            String verificationCode = String.valueOf(code);
            
            System.out.println("✅ 인증 코드 생성 완료: " + verificationCode);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return verificationCode;
            
        } catch (Exception e) {
            System.err.println("❌ 인증 코드 생성 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("인증 코드 생성 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ⭐ 인증 코드 저장 (새로 구현!)
     */
    @Override
    public void saveVerificationCode(String email, String code) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💾 인증 코드 저장");
        System.out.println("  - 이메일: " + email);
        System.out.println("  - 코드: " + code);
        
        try {
            // 메모리에 저장 (실제로는 Redis 또는 DB 사용 권장)
            verificationCodes.put(email, code);
            
            System.out.println("✅ 인증 코드 저장 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 인증 코드 저장 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("인증 코드 저장 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ⭐ 인증 코드 확인 (새로 구현!)
     */
    @Override
    public boolean verifyCode(String email, String code) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✔️ 인증 코드 확인");
        System.out.println("  - 이메일: " + email);
        System.out.println("  - 입력 코드: " + code);
        
        try {
            // 저장된 코드 가져오기
            String savedCode = verificationCodes.get(email);
            
            if (savedCode == null) {
                System.out.println("❌ 해당 이메일의 인증 코드가 없습니다");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return false;
            }
            
            boolean isValid = savedCode.equals(code);
            
            if (isValid) {
                System.out.println("✅ 인증 코드 일치");
                // 인증 성공 후 코드 삭제
                verificationCodes.remove(email);
            } else {
                System.out.println("❌ 인증 코드 불일치");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return isValid;
            
        } catch (Exception e) {
            System.err.println("❌ 인증 코드 확인 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("인증 코드 확인 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 🔐 비밀번호 해시 처리 (SHA-256)
     */
    private String hashPassword(String password) throws Exception {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
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
            throw new Exception("비밀번호 해시 처리 실패: " + e.getMessage(), e);
        }
    }
}
