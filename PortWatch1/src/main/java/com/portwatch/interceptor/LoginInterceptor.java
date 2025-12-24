package com.portwatch.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * ✅ 로그인 체크 인터셉터
 * 
 * @author PortWatch
 * @version 1.0
 */
public class LoginInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔒 LoginInterceptor - 로그인 체크");
        System.out.println("📍 요청 URI: " + request.getRequestURI());
        
        HttpSession session = request.getSession(false);
        
        // 세션이 없거나 로그인 정보가 없으면 로그인 페이지로 리다이렉트
        if (session == null || session.getAttribute("loginMember") == null) {
            System.out.println("❌ 세션 없음 - 로그인 페이지로 리다이렉트");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            // AJAX 요청인 경우
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"로그인이 필요합니다.\"}");
                return false;
            }
            
            // 일반 요청인 경우
            response.sendRedirect(request.getContextPath() + "/member/login");
            return false;
        }
        
        System.out.println("✅ 로그인 확인 - 요청 진행");
        System.out.println("👤 회원 ID: " + session.getAttribute("memberId"));
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return true;
    }
}
