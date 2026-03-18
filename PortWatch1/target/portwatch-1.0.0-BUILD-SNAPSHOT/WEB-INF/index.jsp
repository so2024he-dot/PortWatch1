<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- 루트 URL(/) 접근 시 로그인 페이지로 이동 --%>
<% response.sendRedirect(request.getContextPath() + "/member/login"); %>
