<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:include page="../common/header.jsp" />

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="card shadow-sm mt-5">
                <div class="card-body p-5">
                    <h2 class="card-title text-center mb-4">
                        <i class="bi bi-box-arrow-in-right text-primary"></i>
                        <spring:message code="member.login.title" />
                    </h2>
                    
                    <!-- Spring Form -->
                    <form:form action="${pageContext.request.contextPath}/member/login" 
                               method="post" 
                               modelAttribute="loginForm" 
                               id="loginForm">
                        
                        <!-- 이메일 -->
                        <div class="mb-3">
                            <label for="memberEmail" class="form-label">
                                <spring:message code="member.email" />
                            </label>
                            <form:input path="memberEmail" 
                                       class="form-control" 
                                       id="memberEmail" 
                                       placeholder="example@email.com" 
                                       autofocus="true" />
                            <form:errors path="memberEmail" cssClass="text-danger small" />
                        </div>
                        
                        <!-- 비밀번호 -->
                        <div class="mb-3">
                            <label for="memberPass" class="form-label">
                                <spring:message code="member.password" />
                            </label>
                            <form:password path="memberPass" 
                                          class="form-control" 
                                          id="memberPass" 
                                          placeholder="••••••••" />
                            <form:errors path="memberPass" cssClass="text-danger small" />
                        </div>
                        
                        <!-- 자동 로그인 -->
                        <div class="mb-3 form-check">
                            <form:checkbox path="rememberMe" 
                                         class="form-check-input" 
                                         id="rememberMe" />
                            <label class="form-check-label" for="rememberMe">
                                자동 로그인
                            </label>
                        </div>
                        
                        <!-- 로그인 버튼 -->
                        <button type="submit" class="btn btn-primary w-100 mb-3">
                            <i class="bi bi-box-arrow-in-right"></i> <spring:message code="menu.login" />
                        </button>
                    </form:form>
                    
                    <!-- 추가 링크 -->
                    <div class="text-center">
                        <a href="<c:url value='/member/findPassword' />" class="text-decoration-none small">
                            비밀번호를 잊으셨나요?
                        </a>
                    </div>
                    
                    <hr class="my-4">
                    
                    <div class="text-center">
                        <p class="text-muted mb-2">아직 회원이 아니신가요?</p>
                        <a href="<c:url value='/member/signup' />" class="btn btn-outline-primary">
                            <i class="bi bi-person-plus"></i> <spring:message code="menu.signup" />
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
