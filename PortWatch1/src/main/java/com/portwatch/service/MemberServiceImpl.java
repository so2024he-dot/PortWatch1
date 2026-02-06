package com.portwatch.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.portwatch.domain.MemberVO;
import com.portwatch.mapper.MemberMapper;

/**
 * 회원 서비스 구현체
 */
@Service
public class MemberServiceImpl implements MemberService {
    
    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);
    
    @Autowired
    private MemberMapper memberMapper;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    // 이메일 인증 코드 임시 저장소
    private Map<String, String> verificationCodes = new HashMap<>();
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 로그인
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public MemberVO login(String memberEmail, String memberPass) {
        logger.info("🔐 로그인 시도: {}", memberEmail);
        
        MemberVO member = memberMapper.findByEmail(memberEmail);
        
        if (member != null && passwordEncoder.matches(memberPass, member.getMemberPass())) {
            logger.info("✅ 로그인 성공: {}", memberEmail);
            return member;
        }
        
        logger.warn("❌ 로그인 실패: {}", memberEmail);
        return null;
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 회원가입
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public void register(MemberVO member) {
        logger.info("📝 회원가입 시작: {}", member.getMemberEmail());
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(member.getMemberPass());
        member.setMemberPass(encodedPassword);
        
        // 회원 저장
        memberMapper.insert(member);
        
        logger.info("✅ 회원가입 완료: {}", member.getMemberEmail());
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 회원 조회
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public MemberVO getMemberById(String memberId) {
        return memberMapper.findById(memberId);
    }
    
    @Override
    public MemberVO getMemberByEmail(String memberEmail) {
        return memberMapper.findByEmail(memberEmail);
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 회원 수정
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public void updateMember(MemberVO member) {
        logger.info("✏️ 회원 정보 수정: {}", member.getMemberId());
        
        // 비밀번호가 변경된 경우 암호화
        if (member.getMemberPass() != null && !member.getMemberPass().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(member.getMemberPass());
            member.setMemberPass(encodedPassword);
        }
        
        memberMapper.update(member);
        
        logger.info("✅ 회원 정보 수정 완료: {}", member.getMemberId());
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 회원 삭제
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public void deleteMember(String memberId) {
        logger.info("🗑️ 회원 삭제: {}", memberId);
        memberMapper.delete(memberId);
        logger.info("✅ 회원 삭제 완료: {}", memberId);
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 이메일 중복 체크
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public boolean checkEmailAvailable(String email) {
        MemberVO existingMember = memberMapper.findByEmail(email);
        return (existingMember == null);
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 인증 코드 생성
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 인증 코드 검증
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public boolean verifyCode(String email, String code) {
        String savedCode = verificationCodes.get(email);
        if (savedCode != null && savedCode.equals(code)) {
            verificationCodes.remove(email);
            return true;
        }
        return false;
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 인증 코드 저장 (이메일 발송 전)
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public void saveVerificationCode(String email, String code) {
        verificationCodes.put(email, code);
        logger.info("📧 인증 코드 저장: {} - {}", email, code);
    }
}
