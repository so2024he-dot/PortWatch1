package com.portwatch.util;

import javax.servlet.http.HttpSession;
import com.portwatch.domain.MemberVO;

/**
 * 세션 관리 유틸리티 클래스
 * 
 * 프로젝트 전체에서 세션 필드명을 통일하고,
 * 세션 관련 작업을 간편하게 처리하기 위한 유틸리티
 * 
 * @author PortWatch
 * @version 1.0
 */
public class SessionUtils {
    
    /** 세션에 저장되는 로그인 회원 정보의 키 */
    private static final String LOGIN_MEMBER = "loginMember";
    
    /**
     * 로그인 회원 정보를 세션에 저장
     * 
     * @param session HttpSession 객체
     * @param member 로그인한 회원 정보
     */
    public static void setLoginMember(HttpSession session, MemberVO member) {
        if (session != null && member != null) {
            session.setAttribute(LOGIN_MEMBER, member);
        }
    }
    
    /**
     * 세션에서 로그인 회원 정보 조회
     * 
     * @param session HttpSession 객체
     * @return 로그인한 회원 정보, 없으면 null
     */
    public static MemberVO getLoginMember(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (MemberVO) session.getAttribute(LOGIN_MEMBER);
    }
    
    /**
     * 로그인 여부 확인
     * 
     * @param session HttpSession 객체
     * @return 로그인 상태면 true, 아니면 false
     */
    public static boolean isLoggedIn(HttpSession session) {
        return getLoginMember(session) != null;
    }
    
    /**
     * 세션에서 회원 ID만 조회
     * 
     * @param session HttpSession 객체
     * @return 회원 ID, 로그인하지 않았으면 null
     */
    public static String getMemberId(HttpSession session) {
        MemberVO member = getLoginMember(session);
        return (member != null) ? member.getMemberId() : null;
    }
    
    /**
     * 세션에서 회원 이메일 조회
     * 
     * @param session HttpSession 객체
     * @return 회원 이메일, 로그인하지 않았으면 null
     */
    public static String getMemberEmail(HttpSession session) {
        MemberVO member = getLoginMember(session);
        return (member != null) ? member.getMemberEmail() : null;
    }
    
    /**
     * 세션에서 회원 이름 조회
     * 
     * @param session HttpSession 객체
     * @return 회원 이름, 로그인하지 않았으면 null
     */
    public static String getMemberName(HttpSession session) {
        MemberVO member = getLoginMember(session);
        return (member != null) ? member.getMemberName() : null;
    }
    
    /**
     * 로그아웃 처리
     * 세션에서 로그인 정보를 제거하고 세션을 무효화
     * 
     * @param session HttpSession 객체
     */
    public static void logout(HttpSession session) {
        if (session != null) {
            session.removeAttribute(LOGIN_MEMBER);
            session.invalidate();
        }
    }
    
    /**
     * 세션에서 로그인 정보만 제거 (세션은 유지)
     * 
     * @param session HttpSession 객체
     */
    public static void removeLoginMember(HttpSession session) {
        if (session != null) {
            session.removeAttribute(LOGIN_MEMBER);
        }
    }
    
    /**
     * 세션 유효성 검사 및 리다이렉트 URL 반환
     * 
     * @param session HttpSession 객체
     * @return 로그인하지 않았으면 로그인 페이지 URL, 로그인했으면 null
     */
    public static String checkLoginAndGetRedirect(HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/member/login";
        }
        return null;
    }
}
