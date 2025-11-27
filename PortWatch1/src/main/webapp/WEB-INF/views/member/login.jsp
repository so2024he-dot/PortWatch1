<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- header.jsp include (올바른 방법) -->
<jsp:include page="../common/header.jsp" />

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h3>로그인</h3>
            </div>
            <div class="card-body">
                <!-- 에러 메시지 -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        ${error}
                    </div>
                </c:if>
                
                <!-- 로그인 폼 -->
                <form action="${pageContext.request.contextPath}/member/login" method="post">
                    <div class="mb-3">
                        <label for="memberEmail" class="form-label">이메일</label>
                        <input type="email" class="form-control" id="memberEmail" 
                               name="memberEmail" required 
                               placeholder="example@email.com">
                    </div>
                    <div class="mb-3">
                        <label for="memberPass" class="form-label">비밀번호</label>
                        <input type="password" class="form-control" id="memberPass" 
                               name="memberPass" required 
                               placeholder="비밀번호를 입력하세요">
                    </div>
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary">로그인</button>
                    </div>
                </form>
                
                <hr>
                
                <div class="text-center">
                    <p>계정이 없으신가요? 
                        <a href="${pageContext.request.contextPath}/member/register">회원가입</a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- ✅ footer.jsp include -->
<jsp:include page="../common/footer.jsp" />

    
