package com.portwatch.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.portwatch.domain.MemberVO;
import com.portwatch.service.MemberService;

import java.util.HashMap;
import java.util.Map;

/**
 * 회원 관련 컨트롤러
 */
@Controller
@RequestMapping("/member")
public class MemberController {
    
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
    
    @Autowired
    private MemberService memberService;
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 로그인 페이지
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🔐 로그인 페이지 접근");
        logger.info("  → 로그인 페이지 표시");
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // 이미 로그인되어 있으면 대시보드로 리다이렉트
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember != null) {
            return "redirect:/dashboard";
        }
        
        return "member/login";
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 로그인 처리
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String memberEmail,
            @RequestParam String memberPass,
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🔐 로그인");
        logger.info("  - 이메일: {}", memberEmail);
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            MemberVO member = memberService.login(memberEmail, memberPass);
            
            if (member != null) {
                // 세션에 저장
                session.setAttribute("loginMember", member);
                session.setAttribute("memberId", member.getMemberId());
                session.setAttribute("memberName", member.getMemberName());
                
                logger.info("✅ 로그인 성공");
                logger.info("  - 회원 ID: {}", member.getMemberId());
                logger.info("  - 이름: {}", member.getMemberName());
                logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                result.put("success", true);
                result.put("message", "로그인 성공");
                result.put("memberId", member.getMemberId());
                result.put("memberName", member.getMemberName());
                return ResponseEntity.ok(result);
            } else {
                logger.warn("❌ 로그인 실패: 이메일 또는 비밀번호 불일치");
                logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                result.put("success", false);
                result.put("message", "이메일 또는 비밀번호가 일치하지 않습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }
        } catch (Exception e) {
            logger.error("❌ 로그인 오류: {}", e.getMessage());
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "로그인 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 로그아웃
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🔓 로그아웃");
        
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember != null) {
            logger.info("  - 회원 ID: {}", loginMember.getMemberId());
        }
        
        session.invalidate();
        
        logger.info("  ✅ 로그아웃 완료");
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return "redirect:/member/login";
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 회원가입 페이지
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @GetMapping("/signup")
    public String signupPage() {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📝 회원가입 페이지 접근");
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return "member/signup";
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 회원가입 처리
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> signup(@RequestBody MemberVO member) {
        Map<String, Object> result = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📝 회원가입 요청");
        logger.info("  - 이메일: {}", member.getMemberEmail());
        logger.info("  - 이름: {}", member.getMemberName());
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            memberService.signup(member);
            
            logger.info("✅ 회원가입 성공");
            logger.info("  - 회원 ID: {}", member.getMemberId());
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", true);
            result.put("message", "회원가입이 완료되었습니다.");
            result.put("memberId", member.getMemberId());
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("❌ 회원가입 오류: {}", e.getMessage());
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "회원가입 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ⭐ 프로필 페이지 (NEW!)
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("👤 프로필 페이지 접근");
        
        // 로그인 확인
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            logger.warn("❌ 로그인 필요 → /member/login으로 리다이렉트");
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        logger.info("  ✅ 로그인 상태 확인");
        logger.info("  회원 ID: {}", loginMember.getMemberId());
        logger.info("  회원 이름: {}", loginMember.getMemberName());
        logger.info("  이메일: {}", loginMember.getMemberEmail());
        
        try {
            // 최신 회원 정보 조회
            MemberVO member = memberService.getMemberById(loginMember.getMemberId());
            
            if (member != null) {
                model.addAttribute("member", member);
                
                logger.info("  ✅ 회원 정보 조회 완료");
                logger.info("  → 프로필 페이지 표시");
                logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                return "member/profile";
            } else {
                logger.warn("❌ 회원 정보 없음 → 로그인 페이지로 리다이렉트");
                logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                session.invalidate();
                return "redirect:/member/login";
            }
        } catch (Exception e) {
            logger.error("❌ 프로필 조회 오류: {}", e.getMessage());
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ⭐ 프로필 수정 (NEW!)
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @PostMapping("/profile/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestBody MemberVO member,
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("✏️ 프로필 수정 요청");
        logger.info("  - 회원 ID: {}", member.getMemberId());
        logger.info("  - 이름: {}", member.getMemberName());
        logger.info("  - 전화번호: {}", member.getMemberPhone());
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // 로그인 확인
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
        
        // 본인 확인
        if (!loginMember.getMemberId().equals(member.getMemberId())) {
            result.put("success", false);
            result.put("message", "본인의 정보만 수정할 수 있습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        
        try {
            memberService.updateMember(member);
            
            // 세션 정보 업데이트
            MemberVO updatedMember = memberService.getMemberById(member.getMemberId());
            session.setAttribute("loginMember", updatedMember);
            session.setAttribute("memberName", updatedMember.getMemberName());
            
            logger.info("✅ 프로필 수정 완료");
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", true);
            result.put("message", "프로필이 수정되었습니다.");
            result.put("member", updatedMember);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("❌ 프로필 수정 오류: {}", e.getMessage());
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "프로필 수정 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ⭐ 비밀번호 변경 (NEW!)
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @PostMapping("/profile/password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🔐 비밀번호 변경 요청");
        
        // 로그인 확인
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
        
        logger.info("  - 회원 ID: {}", loginMember.getMemberId());
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            // 현재 비밀번호 확인
            boolean isValid = memberService.checkPassword(
                loginMember.getMemberId(), 
                currentPassword
            );
            
            if (!isValid) {
                logger.warn("❌ 현재 비밀번호 불일치");
                logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                result.put("success", false);
                result.put("message", "현재 비밀번호가 일치하지 않습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            
            // 비밀번호 변경
            memberService.updatePassword(loginMember.getMemberId(), newPassword);
            
            logger.info("✅ 비밀번호 변경 완료");
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", true);
            result.put("message", "비밀번호가 변경되었습니다.");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("❌ 비밀번호 변경 오류: {}", e.getMessage());
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "비밀번호 변경 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
