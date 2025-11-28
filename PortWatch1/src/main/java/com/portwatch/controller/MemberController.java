    package com.portwatch.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.portwatch.domain.MemberVO;
import com.portwatch.service.MemberService;

@Controller
@RequestMapping("/member")
public class MemberController {
    
    @Autowired
    private MemberService memberService;
    
    // 로그인 폼을 위한 LoginForm 클래스
    public static class LoginForm {
        private String memberEmail;
        private String memberPass;
        private boolean rememberMe;
        
        public String getMemberEmail() { return memberEmail; }
        public void setMemberEmail(String memberEmail) { this.memberEmail = memberEmail; }
        
        public String getMemberPass() { return memberPass; }
        public void setMemberPass(String memberPass) { this.memberPass = memberPass; }
        
        public boolean isRememberMe() { return rememberMe; }
        public void setRememberMe(boolean rememberMe) { this.rememberMe = rememberMe; }
    }
    
    // 로그인 페이지
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "member/login";
    }
    
    // 로그인 처리
    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm loginForm,
                       HttpSession session,
                       RedirectAttributes rttr) {
        try {
            MemberVO member = memberService.login(loginForm.getMemberEmail(), loginForm.getMemberPass());
            
            if (member != null) {
                session.setAttribute("member", member);
                rttr.addFlashAttribute("message", "로그인에 성공하였습니다.");
                rttr.addFlashAttribute("messageType", "success");
                return "redirect:/";
            } else {
                rttr.addFlashAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
                return "redirect:/member/login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            rttr.addFlashAttribute("error", "로그인 중 오류가 발생했습니다.");
            return "redirect:/member/login";
        }
    }
    
    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes rttr) {
        session.invalidate();
        rttr.addFlashAttribute("message", "로그아웃되었습니다.");
        rttr.addFlashAttribute("messageType", "info");
        return "redirect:/";
    }
    
    // 회원가입 페이지
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("memberVO", new MemberVO());
        return "member/signup";
    }
    
    // 회원가입 처리 (디버깅 로그 추가)
    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute MemberVO memberVO,
                        BindingResult bindingResult,
                        RedirectAttributes rttr) {
        
        System.out.println("========================================");
        System.out.println(" 회원가입 시도");
        System.out.println("이메일: " + memberVO.getMemberEmail());
        System.out.println("이름: " + memberVO.getMemberName());
        System.out.println("전화번호: " + memberVO.getMemberPhone());
        System.out.println("비밀번호 있음: " + (memberVO.getMemberPass() != null && !memberVO.getMemberPass().isEmpty()));
        System.out.println("========================================");
        
        // Validation 오류 확인
        if (bindingResult.hasErrors()) {
            System.out.println(" Validation 에러 발생!");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println("에러: " + error.getDefaultMessage());
            });
            rttr.addFlashAttribute("error", "입력 정보를 확인해주세요.");
            return "redirect:/member/signup";
        }
        
        try {
            System.out.println(" MemberService.signup() 호출 전");
            memberService.signup(memberVO);
            System.out.println(" 회원가입 성공!");
            
            rttr.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            rttr.addFlashAttribute("messageType", "success");
            return "redirect:/member/login";
            
        } catch (Exception e) {
            System.out.println(" 회원가입 에러 발생!");
            System.out.println("에러 메시지: " + e.getMessage());
            e.printStackTrace();
            
            rttr.addFlashAttribute("error", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/member/signup";
        }
    }
    
    // 마이페이지
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";
        }
        model.addAttribute("member", member);
        return "member/mypage";
    }
}

    
