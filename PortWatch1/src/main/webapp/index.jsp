<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // HTTP 404 오류 해결 - 루트 접속 시 로그인으로 리다이렉트
    response.sendRedirect(request.getContextPath() + "/member/login");
%>
