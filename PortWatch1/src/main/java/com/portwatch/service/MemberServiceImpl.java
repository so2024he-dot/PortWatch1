package com.portwatch.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.MemberVO;
import com.portwatch.persistence.MemberMapper;

/**
 * 회원 서비스 구현
 */
@Service
public class MemberServiceImpl implements MemberService {
    
    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);
    
    @Autowired
    private MemberMapper memberMapper;
    
    // 인증 코드 임시 저장소 (실제 운영에서는 Redis 사용 권장)
    private static final Map<String, String> verificationCodes = new HashMap<>();
    
    /**
     * 로그인
     */
    @Override
    public MemberVO login(String memberEmail, String memberPass) {
        MemberVO member = memberMapper.findByEmail(memberEmail);
        
        if (member != null && member.getMemberPass().equals(memberPass)) {
            if ("ACTIVE".equals(member.getMemberStatus())) {
                return member;
            }
        }
        return null;
    }
    
    /**
     * 회원가입
     */
    @Override
    @Transactional
    public void signup(MemberVO member) throws Exception {
        // 회원 ID 생성
        String email = member.getMemberEmail();
        String emailId = email.substring(0, email.indexOf('@'));
        String memberId = emailId + "_" + System.currentTimeMillis();
        member.setMemberId(memberId);
        
        // 기본값 설정
        if (member.getMemberRole() == null) {
            member.setMemberRole("USER");
        }
        if (member.getMemberStatus() == null) {
            member.setMemberStatus("ACTIVE");
        }
        
        memberMapper.insert(member);
    }
    
    /**
     * 회원 ID로 조회
     */
    @Override
    public MemberVO getMemberById(String memberId) {
        return memberMapper.findById(memberId);
    }
    
    /**
     * 회원 정보 수정
     */
    @Override
    @Transactional
    public void updateMember(MemberVO member) throws Exception {
        memberMapper.update(member);
    }
    
    /**
     * 비밀번호 확인
     */
    @Override
    public boolean checkPassword(String memberId, String password) {
        MemberVO member = memberMapper.findById(memberId);
        if (member != null) {
            return member.getMemberPass().equals(password);
        }
        return false;
    }
    
    /**
     * 비밀번호 변경
     */
    @Override
    @Transactional
    public void updatePassword(String memberId, String newPassword) throws Exception {
        memberMapper.updatePassword(memberId, newPassword);
    }
    
    /**
     * 회원 탈퇴
     */
    @Override
    @Transactional
    public void deleteMember(String memberId) throws Exception {
        memberMapper.delete(memberId);
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ⭐ 이메일 인증 관련 메서드 구현
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * 이메일 사용 가능 여부 확인
     */
    @Override
    public boolean checkEmailAvailable(String email) {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📧 이메일 중복 확인: {}", email);
        
        try {
            MemberVO existingMember = memberMapper.findByEmail(email);
            boolean available = (existingMember == null);
            
            if (available) {
                logger.info("  ✅ 사용 가능한 이메일");
            } else {
                logger.info("  ❌ 이미 사용 중인 이메일");
            }
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return available;
        } catch (Exception e) {
            logger.error("이메일 확인 중 오류: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 인증 코드 생성 (6자리 숫자)
     */
    @Override
    public String generateVerificationCode() {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🔑 인증 코드 생성");
        
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자
        String verificationCode = String.valueOf(code);
        
        logger.info("  생성된 코드: {}", verificationCode);
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return verificationCode;
    }
    
    /**
     * 인증 코드 저장 (이메일과 연결)
     */
    public void saveVerificationCode(String email, String code) {
        logger.info("📝 인증 코드 저장: {} -> {}", email, code);
        verificationCodes.put(email, code);
        
        // 실제 운영에서는 유효시간 설정 (예: 5분)
        // Redis 사용 시: redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);
    }
    
    /**
     * 인증 코드 검증
     */
    @Override
    public boolean verifyCode(String email, String code) {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("✅ 인증 코드 검증");
        logger.info("  이메일: {}", email);
        logger.info("  입력 코드: {}", code);
        
        try {
            String savedCode = verificationCodes.get(email);
            
            if (savedCode == null) {
                logger.warn("  ❌ 저장된 인증 코드 없음");
                logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return false;
            }
            
            boolean verified = savedCode.equals(code);
            
            if (verified) {
                logger.info("  ✅ 인증 성공!");
                // 인증 성공 후 코드 삭제
                verificationCodes.remove(email);
            } else {
                logger.warn("  ❌ 인증 실패 (코드 불일치)");
                logger.warn("  저장된 코드: {}", savedCode);
            }
            
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return verified;
            
        } catch (Exception e) {
            logger.error("인증 코드 검증 중 오류: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 테스트용: 저장된 모든 인증 코드 출력
     */
    public void printVerificationCodes() {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📋 저장된 인증 코드 목록:");
        if (verificationCodes.isEmpty()) {
            logger.info("  (없음)");
        } else {
            verificationCodes.forEach((email, code) -> 
                logger.info("  {} -> {}", email, code)
            );
        }
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
