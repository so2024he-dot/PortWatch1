package com.portwatch.service;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.MemberVO;
import com.portwatch.persistence.MemberDAO;

/**
 * ✅ MemberServiceImpl - 회원가입 오류 완전 해결!
 * 
 * 수정 내역:
 * - signup 메서드에 member_id 자동 생성 로직 추가
 * - 이메일 기반 member_id 생성 (중복 방지)
 * 
 * @author PortWatch
 * @version FIXED - 2026.01.14
 */
@Service
public class MemberServiceImpl implements MemberService {
    
    @Autowired
    private MemberDAO memberDAO;
    
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
     * 
     * 생성 규칙:
     * - 이메일의 @ 앞부분 추출
     * - 특수문자 제거
     * - 타임스탬프 추가 (중복 방지)
     * - 최대 50자
     * 
     * 예시:
     * - test@portwatch.com → test_1705217613
     * - john.doe@example.com → johndoe_1705217613
     */
    private String generateMemberId(String email) {
        if (email == null || email.isEmpty()) {
            // 이메일이 없으면 UUID 사용
            return "user_" + UUID.randomUUID().toString().substring(0, 8);
        }
        
        // 1. 이메일에서 @ 앞부분 추출
        String localPart = email.split("@")[0];
        
        // 2. 특수문자 제거 (영문자, 숫자만 남김)
        String cleanedPart = localPart.replaceAll("[^a-zA-Z0-9]", "");
        
        // 3. 타임스탬프 추가 (밀리초의 마지막 10자리)
        long timestamp = System.currentTimeMillis();
        String timestampStr = String.valueOf(timestamp).substring(3); // 마지막 10자리
        
        // 4. member_id 생성
        String memberId = cleanedPart + "_" + timestampStr;
        
        // 5. 50자로 제한
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
            return false;  // member_id가 없으면 중복 아님 (자동 생성될 예정)
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
     * ✅ 비밀번호 변경
     */
    @Override
    @Transactional
    public void changePassword(String memberId, String oldPassword, String newPassword) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔑 비밀번호 변경");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            MemberVO member = memberDAO.selectById(memberId);
            
            if (member == null) {
                throw new Exception("회원 정보를 찾을 수 없습니다.");
            }
            
            String hashedOldPassword = hashPassword(oldPassword);
            
            if (!hashedOldPassword.equals(member.getMemberPass())) {
                System.out.println("❌ 현재 비밀번호가 일치하지 않습니다.");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                throw new Exception("현재 비밀번호가 일치하지 않습니다.");
            }
            
            String hashedNewPassword = hashPassword(newPassword);
            memberDAO.updatePassword(memberId, hashedNewPassword);
            
            System.out.println("✅ 비밀번호 변경 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 비밀번호 변경 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw e;
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

	@Override
	public void deleteMember(String memberId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<MemberVO> getAllMembers() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
