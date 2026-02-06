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
        logger.info("🔐 로그인 시도");
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
            logger.error("❌ 로그인 오류", e);
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
        logger.info("🚪 로그아웃");
        logger.info("  → 세션 무효화");
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        session.invalidate();
        return "redirect:/member/login";
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 회원가입 페이지
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @GetMapping("/register")
    public String registerPage() {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📝 회원가입 페이지 접근");
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return "member/register";
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 회원가입 처리
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> register(@RequestBody MemberVO member) {
        
        Map<String, Object> result = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📝 회원가입 시도");
        logger.info("  - 이메일: {}", member.getMemberEmail());
        logger.info("  - 이름: {}", member.getMemberName());
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            memberService.register(member);
            
            logger.info("✅ 회원가입 성공");
            logger.info("  - 회원 ID: {}", member.getMemberId());
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", true);
            result.put("message", "회원가입이 완료되었습니다.");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("❌ 회원가입 오류", e);
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "회원가입 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 프로필 페이지
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("👤 프로필 페이지 접근");
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        try {
            MemberVO member = memberService.getMemberById(loginMember.getMemberId());
            model.addAttribute("member", member);
            
            logger.info("✅ 프로필 조회 성공");
            logger.info("  - 회원 ID: {}", member.getMemberId());
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "member/profile";
            
        } catch (Exception e) {
            logger.error("❌ 프로필 조회 오류", e);
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            model.addAttribute("error", e.getMessage());
            return "error/500";
        }
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 프로필 수정
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @PostMapping("/profile/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestBody MemberVO member,
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("✏️ 프로필 수정 시도");
        logger.info("  - 회원 ID: {}", member.getMemberId());
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            memberService.updateMember(member);
            
            // 세션 업데이트
            MemberVO updatedMember = memberService.getMemberById(member.getMemberId());
            session.setAttribute("loginMember", updatedMember);
            
            logger.info("✅ 프로필 수정 성공");
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", true);
            result.put("message", "프로필이 수정되었습니다.");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("❌ 프로필 수정 오류", e);
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "프로필 수정 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
