<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    /**
     * index.jsp - HTTP 404 오류 해결
     * 
     * 위치: src/main/webapp/index.jsp
     * 
     * 문제: http://localhost:8088 접속 시 HTTP 404 발생
     * 원인: webapp 루트에 index.jsp 없음
     * 해결: 로그인 페이지로 자동 리다이렉트
     */
    response.sendRedirect(request.getContextPath() + "/member/login");
%>
