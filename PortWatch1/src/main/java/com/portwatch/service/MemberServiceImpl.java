package com.portwatch.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.MemberVO;
import com.portwatch.persistence.MemberDAO;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * MemberServiceImpl - changePassword boolean 반환 버전
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 수정:
 * 1. changePassword 반환 타입: void → boolean
 * 2. 성공 시 true, 실패 시 false 반환
 * 
 * @version FINAL with boolean return
 */
@Service
public class MemberServiceImpl implements MemberService {
    
    @Autowired
    private MemberDAO memberDAO;
    
    /**
     * ✅ 로그인
     */
    @Override
    public MemberVO login(String memberEmail, String memberPass) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔐 로그인 시도");
        System.out.println("  - 이메일: " + memberEmail);
        
        try {
            // 비밀번호 해시
            String hashedPassword = hashPassword(memberPass);
            
            // 로그인 처리
            MemberVO member = ((MemberService) memberDAO).login(memberEmail, hashedPassword);
            
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
            // 비밀번호 해시 처리
            String hashedPassword = hashPassword(member.getMemberPass());
            member.setMemberPass(hashedPassword);
            
            // 회원 등록
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
     * ⭐ 비밀번호 변경 (boolean 반환!)
     * 
     * ✅ 핵심 수정:
     * - 반환 타입: void → boolean
     * - 성공 시 true, 실패 시 false 반환
     */
    @Override
    @Transactional
    public boolean changePassword(String memberId, String oldPassword, String newPassword) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔐 비밀번호 변경");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            // 1. 회원 정보 조회
            MemberVO member = memberDAO.selectMemberById(memberId);
            
            if (member == null) {
                System.out.println("❌ 회원을 찾을 수 없습니다");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return false;
            }
            
            // 2. 기존 비밀번호 확인 (해시 비교)
            String hashedOldPassword = hashPassword(oldPassword);
            
            if (!hashedOldPassword.equals(member.getMemberPass())) {
                System.out.println("❌ 기존 비밀번호 불일치");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return false;
            }
            
            // 3. 새 비밀번호로 업데이트 (해시 처리)
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

	@Override
	public void withdrawMember(String memberId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean checkEmailAvailable(String email) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String generateVerificationCode() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyCode(String email, String code) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void saveVerificationCode(String email, String code) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changePassword(String memberId, String newPassword) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
