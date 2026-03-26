package com.portwatch.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.portwatch.domain.MemberVO;
import com.portwatch.mapper.MemberMapper;

/**
 * MemberServiceImpl - 최종 병합본
 *
 * ✅ 해결된 컴파일 오류 (3개):
 *   [ERROR] MemberServiceImpl.java:[99]  cannot find symbol: method getBalance()
 *   [ERROR] MemberServiceImpl.java:[100] cannot find symbol: method setBalance(double)
 *   [ERROR] MemberServiceImpl.java:[202] cannot find symbol: method getBalance()
 *   → MemberVO에 balance 필드 + getter/setter 추가로 해결
 *
 * ✅ 필드명 통일:
 *   memberRole   (구버전의 role → memberRole)
 *   memberStatus (신규 추가)
 */
@Service
public class MemberServiceImpl implements MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /** 이메일 인증 코드 임시 저장소 */
    private final Map<String, String> verificationCodes = new HashMap<>();

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 로그인 (이메일 기반)
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public MemberVO login(String memberEmail, String memberPass) {
        logger.info("로그인 시도 - email: {}", memberEmail);
        try {
            MemberVO member = memberMapper.findByEmail(memberEmail);

            if (member == null) {
                logger.warn("회원 없음 - email: {}", memberEmail);
                return null;
            }

            if (!passwordEncoder.matches(memberPass, member.getMemberPass())) {
                logger.warn("비밀번호 불일치 - email: {}", memberEmail);
                return null;
            }

            // 로그인 시간 업데이트 (실패해도 로그인 성공 유지)
            try {
                memberMapper.updateLastLogin(member.getMemberId());
            } catch (Exception e) {
                logger.warn("updateLastLogin 실패 (무시됨): {}", e.getMessage());
            }

            logger.info("로그인 성공 - email: {}, id: {}", memberEmail, member.getMemberId());
            return member;

        } catch (Exception e) {
            logger.error("login() 예외 - email: {}, 원인: {}", memberEmail, e.getMessage(), e);
            throw e;
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 회원가입
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public void register(MemberVO member) {
        logger.info("회원가입 시작 - email: {}", member.getMemberEmail());
        try {
            // memberId 자동 생성 (폼에서 전송하지 않으므로 서버에서 생성)
            if (member.getMemberId() == null || member.getMemberId().isEmpty()) {
                String base = member.getMemberEmail().split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
                if (base.length() > 10) base = base.substring(0, 10);
                String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
                member.setMemberId(base + "_" + suffix);
                logger.info("memberId 자동 생성: {}", member.getMemberId());
            }

            // 비밀번호 암호화
            member.setMemberPass(passwordEncoder.encode(member.getMemberPass()));

            // ✅ 초기 잔액 설정 (MemberVO.balance 필드 사용)
            if (member.getBalance() <= 0) {   // ← 이 부분이 오류였던 getBalance()
                member.setBalance(1_000_000.0); // ← 이 부분이 오류였던 setBalance()
            }

            // 기본 역할/상태 설정
            if (member.getMemberRole() == null || member.getMemberRole().isEmpty()) {
                member.setMemberRole("USER");
            }
            if (member.getMemberStatus() == null || member.getMemberStatus().isEmpty()) {
                member.setMemberStatus("ACTIVE");
            }

            memberMapper.insert(member);
            logger.info("회원가입 완료 - email: {}", member.getMemberEmail());

        } catch (Exception e) {
            logger.error("register() 예외 - email: {}, 원인: {}", member.getMemberEmail(), e.getMessage(), e);
            throw e;
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 회원 조회
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public MemberVO getMemberById(String memberId) {
        return memberMapper.findById(memberId);
    }

    @Override
    public MemberVO getMemberByEmail(String memberEmail) {
        return memberMapper.findByEmail(memberEmail);
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 회원 수정
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public void updateMember(MemberVO member) {
        logger.info("회원 정보 수정 - id: {}", member.getMemberId());
        if (member.getMemberPass() != null && !member.getMemberPass().isEmpty()) {
            member.setMemberPass(passwordEncoder.encode(member.getMemberPass()));
        }
        memberMapper.update(member);
        logger.info("회원 정보 수정 완료 - id: {}", member.getMemberId());
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 회원 삭제 (soft delete)
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public void deleteMember(String memberId) {
        logger.info("회원 삭제(비활성화) - id: {}", memberId);
        memberMapper.delete(memberId);
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 중복 체크
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public boolean checkEmailAvailable(String email) {
        return memberMapper.findByEmail(email) == null;
    }

    @Override
    public boolean checkIdAvailable(String memberId) {
        return memberMapper.findById(memberId) == null;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 이메일 인증 코드
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public String generateVerificationCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String saved = verificationCodes.get(email);
        if (saved != null && saved.equals(code)) {
            verificationCodes.remove(email);
            return true;
        }
        return false;
    }

    public void saveVerificationCode(String email, String code) {
        verificationCodes.put(email, code);
        logger.info("인증 코드 저장 - email: {}", email);
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 잔액 관리
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @Override
    public double getBalance(String memberId) {
        MemberVO member = memberMapper.findById(memberId);
        // ✅ member.getBalance() - MemberVO에 balance 필드 있어야 정상 동작
        return (member != null) ? member.getBalance() : 0.0;  // ← 오류였던 getBalance()
    }

    @Override
    public void updateBalance(String memberId, double balance) {
        logger.info("잔액 업데이트 - id: {}, balance: {}", memberId, balance);
        memberMapper.updateBalance(memberId, balance);
    }
}
