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
 * MemberController - /member/register 매핑 추가 버전
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 수정:
 * 1. /member/register 매핑 추가 (GET, POST)
 * 2. register와 signup 모두 지원
 * 
 * @version 2.0 - 2026.01.19
 * @author PortWatch
 */
@Controller
@RequestMapping("/member")
public class MemberController {
    
    @Autowired
    private MemberService memberService;
    
    /**
     * ✅ 로그인 페이지
     */
    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔐 로그인 페이지 접근");
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member != null) {
            System.out.println("  이미 로그인된 사용자: " + member.getMemberId());
            System.out.println("  → 대시보드로 리다이렉트");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/dashboard";
        }
        
        System.out.println("  → 로그인 페이지 표시");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return "member/login";
    }
    
    /**
     * ✅ 로그인 처리
     */
    @PostMapping("/login")
    public String login(
            @RequestParam("memberEmail") String memberEmail,
            @RequestParam("memberPass") String memberPass,
            HttpSession session,
            RedirectAttributes rttr) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔐 로그인");
        System.out.println("  - 이메일: " + memberEmail);
        
        try {
            MemberVO member = memberService.login(memberEmail, memberPass);
            
            if (member != null) {
                session.setAttribute("member", member);
                session.setAttribute("memberId", member.getMemberId());
                session.setAttribute("loginMember", member);
                
                System.out.println("✅ 로그인 성공");
                System.out.println("  - 회원 ID: " + member.getMemberId());
                System.out.println("  - 이름: " + member.getMemberName());
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                return "redirect:/dashboard";
            } else {
                System.out.println("❌ 로그인 실패: 잘못된 이메일 또는 비밀번호");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                rttr.addFlashAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
                return "redirect:/member/login";
            }
            
        } catch (Exception e) {
            System.err.println("❌ 로그인 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("error", "로그인 처리 중 오류가 발생했습니다.");
            return "redirect:/member/login";
        }
    }
    
    /**
     * ✅ 로그아웃
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes rttr) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🚪 로그아웃");
        
        try {
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member != null) {
                System.out.println("  - 회원 ID: " + member.getMemberId());
            }
            
            session.invalidate();
            
            System.out.println("✅ 로그아웃 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("message", "로그아웃되었습니다.");
        } catch (Exception e) {
            System.err.println("❌ 로그아웃 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
        return "redirect:/";
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 회원가입 페이지 - /member/register (신규 추가!)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * /member/register와 /member/signup 모두 지원
     */
    @GetMapping("/register")
    public String registerForm(HttpSession session) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📝 회원가입 페이지 접근 (/register)");
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member != null) {
            System.out.println("  이미 로그인된 사용자: " + member.getMemberId());
            System.out.println("  → 대시보드로 리다이렉트");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/dashboard";
        }
        
        System.out.println("  → 회원가입 페이지 표시");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return "member/signup";
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 회원가입 처리 - /member/register (신규 추가!)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @PostMapping("/register")
    public String register(MemberVO member, RedirectAttributes rttr) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📝 회원가입 처리 (/register)");
        System.out.println("  - 이메일: " + member.getMemberEmail());
        System.out.println("  - 이름: " + member.getMemberName());
        
        try {
            // 이메일 중복 검사
            if (memberService.isEmailDuplicate(member.getMemberEmail())) {
                System.out.println("❌ 이메일 중복");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                rttr.addFlashAttribute("error", "이미 사용 중인 이메일입니다.");
                return "redirect:/member/register";
            }
            
            // 아이디 중복 검사
            if (member.getMemberId() != null && memberService.isIdDuplicate(member.getMemberId())) {
                System.out.println("❌ 아이디 중복");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                rttr.addFlashAttribute("error", "이미 사용 중인 아이디입니다.");
                return "redirect:/member/register";
            }
            
            // 회원가입 처리
            memberService.signup(member);
            
            System.out.println("✅ 회원가입 성공");
            System.out.println("  - 생성된 회원 ID: " + member.getMemberId());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/member/login";
            
        } catch (Exception e) {
            System.err.println("❌ 회원가입 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("error", "회원가입 처리 중 오류가 발생했습니다.");
            return "redirect:/member/register";
        }
    }
    
    /**
     * ✅ 회원가입 페이지 - /member/signup (기존 유지)
     */
    @GetMapping("/signup")
    public String signupForm(HttpSession session) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📝 회원가입 페이지 접근 (/signup)");
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member != null) {
            System.out.println("  이미 로그인된 사용자: " + member.getMemberId());
            System.out.println("  → 대시보드로 리다이렉트");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/dashboard";
        }
        
        System.out.println("  → 회원가입 페이지 표시");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return "member/signup";
    }
    
    /**
     * ✅ 회원가입 처리 - /member/signup (기존 유지)
     */
    @PostMapping("/signup")
    public String signup(MemberVO member, RedirectAttributes rttr) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📝 회원가입 처리 (/signup)");
        System.out.println("  - 이메일: " + member.getMemberEmail());
        System.out.println("  - 이름: " + member.getMemberName());
        
        try {
            // 이메일 중복 검사
            if (memberService.isEmailDuplicate(member.getMemberEmail())) {
                System.out.println("❌ 이메일 중복");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                rttr.addFlashAttribute("error", "이미 사용 중인 이메일입니다.");
                return "redirect:/member/signup";
            }
            
            // 아이디 중복 검사
            if (member.getMemberId() != null && memberService.isIdDuplicate(member.getMemberId())) {
                System.out.println("❌ 아이디 중복");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                rttr.addFlashAttribute("error", "이미 사용 중인 아이디입니다.");
                return "redirect:/member/signup";
            }
            
            // 회원가입 처리
            memberService.signup(member);
            
            System.out.println("✅ 회원가입 성공");
            System.out.println("  - 생성된 회원 ID: " + member.getMemberId());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/member/login";
            
        } catch (Exception e) {
            System.err.println("❌ 회원가입 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("error", "회원가입 처리 중 오류가 발생했습니다.");
            return "redirect:/member/signup";
        }
    }
    
    /**
     * ✅ 마이페이지
     */
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("👤 마이페이지 접근");
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            System.out.println("  비로그인 상태");
            System.out.println("  → 로그인 페이지로 리다이렉트");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        try {
            System.out.println("  - 회원 ID: " + member.getMemberId());
            
            MemberVO updatedMember = memberService.getMemberById(member.getMemberId());
            model.addAttribute("member", updatedMember);
            
            System.out.println("✅ 마이페이지 로드 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "member/mypage";
            
        } catch (Exception e) {
            System.err.println("❌ 마이페이지 로드 중 오류: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "redirect:/dashboard";
        }
    }
    
    /**
     * ✅ 회원정보 수정 처리
     */
    @PostMapping("/update")
    public String update(
            MemberVO member,
            HttpSession session,
            RedirectAttributes rttr) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✏️  회원정보 수정");
        
        MemberVO loginMember = (MemberVO) session.getAttribute("member");
        if (loginMember == null) {
            System.out.println("  비로그인 상태");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        try {
            System.out.println("  - 회원 ID: " + loginMember.getMemberId());
            
            member.setMemberId(loginMember.getMemberId());
            memberService.updateMember(member);
            
            MemberVO updatedMember = memberService.getMemberById(member.getMemberId());
            session.setAttribute("member", updatedMember);
            session.setAttribute("loginMember", updatedMember);
            
            System.out.println("✅ 회원정보 수정 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("message", "회원정보가 수정되었습니다.");
            
        } catch (Exception e) {
            System.err.println("❌ 회원정보 수정 중 오류: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("error", "회원정보 수정 중 오류가 발생했습니다.");
        }
        
        return "redirect:/member/mypage";
    }
    
    /**
     * ✅ 비밀번호 변경 페이지
     */
    @GetMapping("/change-password")
    public String changePasswordForm(HttpSession session) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔑 비밀번호 변경 페이지 접근");
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            System.out.println("  비로그인 상태");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        System.out.println("  - 회원 ID: " + member.getMemberId());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return "member/change-password";
    }
    
    /**
     * ✅ 비밀번호 변경 처리
     */
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            RedirectAttributes rttr) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔑 비밀번호 변경 처리");
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            System.out.println("  비로그인 상태");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        try {
            System.out.println("  - 회원 ID: " + member.getMemberId());
            
            // 새 비밀번호 확인
            if (!newPassword.equals(confirmPassword)) {
                System.out.println("❌ 새 비밀번호 불일치");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                rttr.addFlashAttribute("error", "새 비밀번호가 일치하지 않습니다.");
                return "redirect:/member/change-password";
            }
            
            // 비밀번호 변경
            boolean success = memberService.changePassword(
                member.getMemberId(),
                currentPassword,
                newPassword
            );
            
            if (success) {
                System.out.println("✅ 비밀번호 변경 성공");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                rttr.addFlashAttribute("message", "비밀번호가 변경되었습니다.");
                return "redirect:/member/mypage";
            } else {
                System.out.println("❌ 현재 비밀번호 불일치");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                rttr.addFlashAttribute("error", "현재 비밀번호가 올바르지 않습니다.");
                return "redirect:/member/change-password";
            }
            
        } catch (Exception e) {
            System.err.println("❌ 비밀번호 변경 중 오류: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            rttr.addFlashAttribute("error", "비밀번호 변경 중 오류가 발생했습니다.");
            return "redirect:/member/change-password";
        }
    }
}
