package com.portwatch.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.portwatch.domain.MemberVO;
import com.portwatch.service.MemberService;

/**
 * MemberController - 최종 병합본
 *
 * ✅ 원본 기준 유지:
 *   - 로그인: 이메일(memberEmail) + 비밀번호(memberPass)
 *   - GET /member/login  → login.jsp
 *   - GET /member/signup → signup.jsp  ← 404 오류 원인, GET 매핑 추가
 *
 * ✅ 신규 추가:
 *   - 이메일 중복체크 AJAX: /member/check-email
 *   - 아이디 중복체크 AJAX: /member/check-id
 */
@Controller
@RequestMapping("/member")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;

    // ─────────────────────────────────────────────────
    // GET /member/login → login.jsp
    // ─────────────────────────────────────────────────
    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        // "loginMember" 또는 "member" 중 하나라도 있으면 이미 로그인됨
        if (session.getAttribute("loginMember") != null
                || session.getAttribute("member") != null) {
            return "redirect:/dashboard";
        }
        return "member/login";   // /WEB-INF/views/member/login.jsp
    }

    // ─────────────────────────────────────────────────
    // POST /member/login → JSON
    // ─────────────────────────────────────────────────
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam("memberEmail") String memberEmail,
            @RequestParam("memberPass")  String memberPass,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();
        try {
            logger.info("로그인 요청 - email: {}", memberEmail);

            MemberVO member = memberService.login(memberEmail, memberPass);

            if (member != null) {
                // 세션 키 통일: "loginMember"와 "member" 모두 저장
                // (DashboardController, PortfolioController 등이 "member" 사용)
                session.setAttribute("loginMember", member);
                session.setAttribute("member",      member);
                session.setAttribute("memberId",    member.getMemberId());
                session.setAttribute("memberEmail", member.getMemberEmail());
                result.put("success",     true);
                result.put("message",     "로그인 성공");
                result.put("redirectUrl", "/dashboard");
            } else {
                result.put("success", false);
                result.put("message", "이메일 또는 비밀번호가 올바르지 않습니다.");
            }

        } catch (Exception e) {
            logger.error("로그인 예외 - email: {}, 오류: {}", memberEmail, e.getMessage(), e);
            result.put("success", false);
            result.put("message", "로그인 처리 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok(result);
    }

    // ─────────────────────────────────────────────────
    // GET /member/signup → signup.jsp  ← 404 원인 해결
    // ─────────────────────────────────────────────────
    @GetMapping("/signup")
    public String signupForm() {
        return "member/signup";  // /WEB-INF/views/member/signup.jsp
    }

    // ─────────────────────────────────────────────────
    // POST /member/signup → JSON
    // ─────────────────────────────────────────────────
    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> signup(MemberVO member) {

        Map<String, Object> result = new HashMap<>();
        try {
            // 이메일 중복 체크
            if (!memberService.checkEmailAvailable(member.getMemberEmail())) {
                result.put("success", false);
                result.put("message", "이미 사용 중인 이메일입니다.");
                return ResponseEntity.ok(result);
            }

            memberService.register(member);
            result.put("success",     true);
            result.put("message",     "회원가입이 완료되었습니다.");
            result.put("redirectUrl", "/member/login");

        } catch (Exception e) {
            logger.error("회원가입 예외: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "회원가입 처리 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok(result);
    }

    // ─────────────────────────────────────────────────
    // GET /member/logout
    // ─────────────────────────────────────────────────
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/member/login";
    }

    // ─────────────────────────────────────────────────
    // GET /member/guest-login → DB 없이 게스트 세션 생성
    // ─────────────────────────────────────────────────
    @GetMapping("/guest-login")
    public String guestLogin(HttpSession session) {
        MemberVO guest = new MemberVO("guest_user", "guest@portwatch.com", "", "게스트");
        guest.setMemberRole("GUEST");
        guest.setMemberStatus("ACTIVE");
        guest.setBalance(0.0);
        session.setAttribute("loginMember", guest);
        session.setAttribute("member",      guest);
        session.setAttribute("memberId",    "guest_user");
        session.setAttribute("memberEmail", "guest@portwatch.com");
        logger.info("게스트 로그인 - 임시 체험 세션 생성");
        return "redirect:/dashboard";
    }

    // ─────────────────────────────────────────────────
    // AJAX 이메일 중복 체크
    // ─────────────────────────────────────────────────
    @GetMapping("/check-email")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkEmail(
            @RequestParam("memberEmail") String memberEmail) {

        Map<String, Object> result = new HashMap<>();
        try {
            boolean available = memberService.checkEmailAvailable(memberEmail);
            result.put("available", available);
            result.put("message",   available ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.");
        } catch (Exception e) {
            logger.error("이메일 중복체크 오류: {}", e.getMessage());
            result.put("available", false);
            result.put("message",   "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
        }
        return ResponseEntity.ok(result);
    }

    // ─────────────────────────────────────────────────
    // AJAX 아이디 중복 체크
    // ─────────────────────────────────────────────────
    @GetMapping("/check-id")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkId(
            @RequestParam("memberId") String memberId) {

        Map<String, Object> result = new HashMap<>();
        try {
            boolean available = memberService.checkIdAvailable(memberId);
            result.put("available", available);
            result.put("message",   available ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.");
        } catch (Exception e) {
            logger.error("아이디 중복체크 오류: {}", e.getMessage());
            result.put("available", false);
            result.put("message",   "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
        }
        return ResponseEntity.ok(result);
    }

    // ─────────────────────────────────────────────────
    // 마이페이지
    // ─────────────────────────────────────────────────
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/member/login";
        model.addAttribute("member", loginMember);
        return "member/mypage";
    }
}
