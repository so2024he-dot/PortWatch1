package com.portwatch.service;

import com.portwatch.domain.MemberVO;
import com.portwatch.persistence.MemberDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * ✅ 회원 Service 구현
 * 
 * @author PortWatch
 * @version 3.0 - Spring 5.0.7 호환
 */
@Service
public class MemberServiceImpl implements MemberService {
    
    @Autowired
    private MemberDAO memberDAO;
    
    /**
     * ✅ 회원가입
     */
    @Override
    @Transactional
    public void signup(MemberVO member) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📝 회원가입 처리 시작");
        
        // 1. 이메일 중복 체크
        if (memberDAO.checkDuplicateEmail(member.getMemberEmail()) > 0) {
            throw new Exception("이미 사용 중인 이메일입니다.");
        }
        
        // 2. ID 생성 (이메일 앞부분 + UUID)
        if (member.getMemberId() == null || member.getMemberId().isEmpty()) {
            String emailPrefix = member.getMemberEmail().split("@")[0];
            String uniqueId = emailPrefix + "_" + UUID.randomUUID().toString().substring(0, 8);
            member.setMemberId(uniqueId);
        }
        
        // 3. ID 중복 체크
        if (memberDAO.checkDuplicateId(member.getMemberId()) > 0) {
            // 중복이면 완전 랜덤 ID 생성
            member.setMemberId("user_" + UUID.randomUUID().toString().substring(0, 12));
        }
        
        // 4. 비밀번호 암호화 (간단한 해싱 - 실제로는 BCrypt 사용 권장)
        String hashedPassword = hashPassword(member.getMemberPass());
        member.setMemberPass(hashedPassword);
        
        // 5. 기본값 설정
        if (member.getMemberRole() == null || member.getMemberRole().isEmpty()) {
            member.setMemberRole("USER");
        }
        if (member.getMemberStatus() == null || member.getMemberStatus().isEmpty()) {
            member.setMemberStatus("ACTIVE");
        }
        
        // 6. DB 저장
        memberDAO.insertMember(member);
        
        System.out.println("✅ 회원가입 완료: " + member.getMemberId());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * ✅ 로그인
     */
    @Override
    public MemberVO login(String memberEmail, String memberPass) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔐 로그인 시도: " + memberEmail);
        
        // 1. 이메일로 회원 조회
        MemberVO member = memberDAO.selectMemberByEmail(memberEmail);
        
        if (member == null) {
            System.out.println("❌ 존재하지 않는 이메일");
            throw new Exception("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
        
        // 2. 비밀번호 검증
        String hashedPassword = hashPassword(memberPass);
        if (!member.getMemberPass().equals(hashedPassword)) {
            System.out.println("❌ 비밀번호 불일치");
            throw new Exception("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
        
        // 3. 계정 상태 확인
        if (!"ACTIVE".equals(member.getMemberStatus())) {
            System.out.println("❌ 비활성 계정");
            throw new Exception("비활성화된 계정입니다. 관리자에게 문의하세요.");
        }
        
        System.out.println("✅ 로그인 성공: " + member.getMemberId());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return member;
    }
    
    /**
     * ✅ 이메일로 회원 조회
     */
    @Override
    public MemberVO getMemberByEmail(String memberEmail) throws Exception {
        return memberDAO.selectMemberByEmail(memberEmail);
    }
    
    /**
     * ✅ ID로 회원 조회
     */
    @Override
    public MemberVO getMemberById(String memberId) throws Exception {
        return memberDAO.selectMemberById(memberId);
    }
    
    /**
     * ✅ 회원 정보 업데이트
     */
    @Override
    @Transactional
    public void updateMember(MemberVO member) throws Exception {
        memberDAO.updateMember(member);
    }
    
    /**
     * ✅ 비밀번호 변경
     */
    @Override
    @Transactional
    public void changePassword(String memberId, String oldPassword, String newPassword) throws Exception {
        MemberVO member = memberDAO.selectMemberById(memberId);
        
        if (member == null) {
            throw new Exception("존재하지 않는 회원입니다.");
        }
        
        String hashedOldPassword = hashPassword(oldPassword);
        if (!member.getMemberPass().equals(hashedOldPassword)) {
            throw new Exception("현재 비밀번호가 일치하지 않습니다.");
        }
        
        String hashedNewPassword = hashPassword(newPassword);
        member.setMemberPass(hashedNewPassword);
        memberDAO.updateMember(member);
    }
    
    /**
     * ✅ 회원 탈퇴 (논리 삭제)
     */
    @Override
    @Transactional
    public void withdrawMember(String memberId) throws Exception {
        memberDAO.updateMemberStatus(memberId, "DELETED");
    }
    
    /**
     * ✅ 이메일 중복 체크
     */
    @Override
    public boolean isEmailDuplicate(String memberEmail) throws Exception {
        return memberDAO.checkDuplicateEmail(memberEmail) > 0;
    }
    
    /**
     * ✅ ID 중복 체크
     */
    @Override
    public boolean isIdDuplicate(String memberId) throws Exception {
        return memberDAO.checkDuplicateId(memberId) > 0;
    }
    
    /**
     * 간단한 비밀번호 해싱 (실제로는 BCrypt 사용 권장)
     */
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("비밀번호 암호화 실패", e);
        }
    }

	@Override
	public boolean checkEmailAvailable(String email) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String generateVerificationCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyCode(String email, String code) {
		// TODO Auto-generated method stub
		return false;
	}
}
