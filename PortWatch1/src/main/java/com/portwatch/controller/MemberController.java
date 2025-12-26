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
 * ✅ 회원 컨트롤러
 * 로그인, 회원가입, 로그아웃, 마이페이지 등 회원 관련 기능 처리
 * 
 * @author PortWatch Team
 * @version 3.0
 */
@Controller
@RequestMapping("/member")
public class MemberController {
    
    @Autowired
    private MemberService memberService;
    
    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        
        // 이미 로그인된 경우 대시보드로 이동
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember != null) {
            return "redirect:/dashboard";
        }
        
        return "member/login";
    }
    
    /**
     * 로그인 처리
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
                // 세션에 로그인 정보 저장
                session.setAttribute("loginMember", member);
                session.setAttribute("memberId", member.getMemberId());
                
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
     * 로그아웃
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
     * 회원가입 페이지
     */
    @GetMapping("/signup")
    public String signupForm(HttpSession session) {
        
        // 이미 로그인된 경우 대시보드로 이동
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember != null) {
            return "redirect:/dashboard";
        }
        
        return "member/signup";
    }
    
    /**
     * 회원가입 처리
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
     * 마이페이지
     */
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        try {
            // 최신 회원 정보 조회
            MemberVO member = memberService.getMemberById(loginMember.getMemberId());
            model.addAttribute("member", member);
            
        } catch (Exception e) {
            System.err.println("마이페이지 로딩 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "member/mypage";
    }
    
    /**
     * 회원정보 수정 처리
     */
    @PostMapping("/update")
    public String update(
            MemberVO member,
            HttpSession session,
            RedirectAttributes rttr) {
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        try {
            // 본인 확인
            if (!loginMember.getMemberId().equals(member.getMemberId())) {
                rttr.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/member/mypage";
            }
            
            // 회원정보 업데이트
            memberService.updateMember(member);
            
            // 세션 업데이트
            MemberVO updatedMember = memberService.getMemberById(member.getMemberId());
            session.setAttribute("loginMember", updatedMember);
            
            rttr.addFlashAttribute("message", "회원정보가 수정되었습니다.");
            
        } catch (Exception e) {
            System.err.println("회원정보 수정 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            rttr.addFlashAttribute("error", "회원정보 수정 중 오류가 발생했습니다.");
        }
        
        return "redirect:/member/mypage";
    }
    
    /**
     * 비밀번호 변경 처리
     */
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            HttpSession session,
            RedirectAttributes rttr) {
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        try {
            // 비밀번호 변경
            memberService.changePassword(loginMember.getMemberId(), oldPassword, newPassword);
            
            rttr.addFlashAttribute("message", "비밀번호가 변경되었습니다.");
            
        } catch (Exception e) {
            System.err.println("비밀번호 변경 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            rttr.addFlashAttribute("error", "비밀번호 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return "redirect:/member/mypage";
    }
    
    /**
     * 회원 탈퇴 처리
     */
    @PostMapping("/withdraw")
    public String withdraw(
            @RequestParam("memberPass") String memberPass,
            HttpSession session,
            RedirectAttributes rttr) {
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        try {
            // 비밀번호 확인
            MemberVO member = memberService.login(loginMember.getMemberEmail(), memberPass);
            
            if (member == null) {
                rttr.addFlashAttribute("error", "비밀번호가 올바르지 않습니다.");
                return "redirect:/member/mypage";
            }
            
            // 회원 탈퇴
            memberService.withdrawMember(loginMember.getMemberId());
            
            // 세션 무효화
            session.invalidate();
            
            rttr.addFlashAttribute("message", "회원탈퇴가 완료되었습니다.");
            return "redirect:/";
            
        } catch (Exception e) {
            System.err.println("회원탈퇴 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            rttr.addFlashAttribute("error", "회원탈퇴 처리 중 오류가 발생했습니다.");
            return "redirect:/member/mypage";
        }
    }
}
