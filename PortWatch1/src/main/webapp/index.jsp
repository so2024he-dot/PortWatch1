<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // 루트 접속 시 자동으로 로그인 페이지로 리다이렉트
    response.sendRedirect(request.getContextPath() + "/member/login");
%>
