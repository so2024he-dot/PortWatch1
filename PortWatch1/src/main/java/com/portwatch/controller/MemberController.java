package com.portwatch.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.portwatch.domain.MemberVO;
import com.portwatch.service.MemberService;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * MemberController - 세션 키 통일 버전
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 수정:
 * 1. loginMember → member로 세션 키 통일
 * 2. 모든 컨트롤러와 일관성 유지
 * 3. 로그인, 회원가입, 로그아웃 완벽 작동
 * 
 * @version 4.0 FINAL
 */
@Controller
@RequestMapping("/member")
public class MemberController {
    
    @Autowired
    private MemberService memberService;
    
    /**
     * ✅ 로그인 페이지
     * URL: GET /member/login
     */
    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        
        // ✅ 세션 키 통일: member
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member != null) {
            return "redirect:/dashboard";
        }
        
        return "member/login";
    }
    
    /**
     * ✅ 로그인 처리 (완전 수정!)
     * URL: POST /member/login
     * 
     * 핵심 수정:
     * - session.setAttribute("member", member);
     * - loginMember 제거, member로 통일
     */
    @PostMapping("/login")
    public String login(
            @RequestParam("memberEmail") String memberEmail,
            @RequestParam("memberPass") String memberPass,
            HttpSession session,
            RedirectAttributes rttr) {
        
        try {
            // 로그인 서비스 호출
            MemberVO member = memberService.login(memberEmail, memberPass);
            
            if (member != null) {
                // ✅ 세션에 저장 - member로 통일!
                session.setAttribute("member", member);
                session.setAttribute("memberId", member.getMemberId());
                
                // ✅ JSP 호환성을 위해 loginMember도 추가 (선택)
                session.setAttribute("loginMember", member);
                
                // 대시보드로 이동
                return "redirect:/dashboard";
                
            } else {
                // 로그인 실패
                rttr.addFlashAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
                return "redirect:/member/login";
            }
            
        } catch (Exception e) {
            System.err.println("로그인 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            rttr.addFlashAttribute("error", "로그인 처리 중 오류가 발생했습니다.");
            return "redirect:/member/login";
        }
    }
    
    /**
     * ✅ 로그아웃
     * URL: GET /member/logout
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes rttr) {
        
        try {
            // 세션 무효화
            session.invalidate();
            
            rttr.addFlashAttribute("message", "로그아웃되었습니다.");
            
        } catch (Exception e) {
            System.err.println("로그아웃 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "redirect:/";
    }
    
    /**
     * ✅ 회원가입 페이지
     * URL: GET /member/signup
     */
    @GetMapping("/signup")
    public String signupForm(HttpSession session) {
        
        // ✅ 세션 키 통일: member
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member != null) {
            return "redirect:/dashboard";
        }
        
        return "member/signup";
    }
    
    /**
     * ✅ 회원가입 처리
     * URL: POST /member/signup
     */
    @PostMapping("/signup")
    public String signup(
            MemberVO member,
            RedirectAttributes rttr) {
        
        try {
            // 이메일 중복 체크
            if (memberService.isEmailDuplicate(member.getMemberEmail())) {
                rttr.addFlashAttribute("error", "이미 사용 중인 이메일입니다.");
                return "redirect:/member/signup";
            }
            
            // ID 중복 체크
            if (memberService.isIdDuplicate(member.getMemberId())) {
                rttr.addFlashAttribute("error", "이미 사용 중인 아이디입니다.");
                return "redirect:/member/signup";
            }
            
            // 회원가입 처리
            memberService.signup(member);
            
            rttr.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/member/login";
            
        } catch (Exception e) {
            System.err.println("회원가입 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            rttr.addFlashAttribute("error", "회원가입 처리 중 오류가 발생했습니다.");
            return "redirect:/member/signup";
        }
    }
    
    /**
     * ✅ 마이페이지
     * URL: GET /member/mypage
     */
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        
        // ✅ 세션 키 통일: member
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";
        }
        
        try {
            // 최신 회원 정보 조회
            MemberVO updatedMember = memberService.getMemberById(member.getMemberId());
            model.addAttribute("member", updatedMember);
            
            return "member/mypage";
            
        } catch (Exception e) {
            System.err.println("마이페이지 로드 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            return "redirect:/dashboard";
        }
    }
    
    /**
     * ✅ 회원정보 수정 처리
     * URL: POST /member/update
     */
    @PostMapping("/update")
    public String update(
            MemberVO member,
            HttpSession session,
            RedirectAttributes rttr) {
        
        // ✅ 세션 키 통일: member
        MemberVO loginMember = (MemberVO) session.getAttribute("member");
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        try {
            // 회원정보 수정
            member.setMemberId(loginMember.getMemberId());
            memberService.updateMember(member);
            
            // ✅ 세션 정보 갱신 - member로 통일!
            MemberVO updatedMember = memberService.getMemberById(member.getMemberId());
            session.setAttribute("member", updatedMember);
            session.setAttribute("loginMember", updatedMember);  // JSP 호환성
            
            rttr.addFlashAttribute("message", "회원정보가 수정되었습니다.");
            
        } catch (Exception e) {
            System.err.println("회원정보 수정 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            rttr.addFlashAttribute("error", "회원정보 수정 중 오류가 발생했습니다.");
        }
        
        return "redirect:/member/mypage";
    }
    
    /**
     * ✅ 비밀번호 변경 페이지
     * URL: GET /member/change-password
     */
    @GetMapping("/change-password")
    public String changePasswordForm(HttpSession session) {
        
        // ✅ 세션 키 통일: member
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";
        }
        
        return "member/change-password";
    }
    
    /**
     * ✅ 비밀번호 변경 처리
     * URL: POST /member/change-password
     */
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            RedirectAttributes rttr) {
        
        // ✅ 세션 키 통일: member
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";
        }
        
        try {
            // 새 비밀번호 확인
            if (!newPassword.equals(confirmPassword)) {
                rttr.addFlashAttribute("error", "새 비밀번호가 일치하지 않습니다.");
                return "redirect:/member/change-password";
            }
            
            // 비밀번호 변경
            boolean success = (boolean) memberService.changePassword(
                member.getMemberId(),
                currentPassword,
                newPassword
            );
            
            if (success) {
                rttr.addFlashAttribute("message", "비밀번호가 변경되었습니다.");
                return "redirect:/member/mypage";
            } else {
                rttr.addFlashAttribute("error", "현재 비밀번호가 올바르지 않습니다.");
                return "redirect:/member/change-password";
            }
            
        } catch (Exception e) {
            System.err.println("비밀번호 변경 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            rttr.addFlashAttribute("error", "비밀번호 변경 중 오류가 발생했습니다.");
            return "redirect:/member/change-password";
        }
    }
}
