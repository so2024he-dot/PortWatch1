<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:include page="../common/header.jsp" />

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <div class="card shadow-sm mt-4 mb-5">
                <div class="card-body p-5">
                    <h2 class="card-title text-center mb-4">
                        <i class="bi bi-person-plus text-primary"></i>
                        <spring:message code="member.signup.title" />
                    </h2>
                    
                    <!-- Spring Form with Validation -->
                    <form:form action="${pageContext.request.contextPath}/member/signup" 
                               method="post" 
                               modelAttribute="memberVO" 
                               id="signupForm">
                        
                        <!-- 이메일 -->
                        <div class="mb-3">
                            <label for="memberEmail" class="form-label">
                                <spring:message code="member.email" /> <span class="text-danger">*</span>
                            </label>
                            <div class="input-group">
                                <form:input path="memberEmail" 
                                           class="form-control" 
                                           id="memberEmail" 
                                           placeholder="example@email.com" />
                                <button type="button" class="btn btn-outline-secondary" id="checkEmailBtn">
                                    중복 확인
                                </button>
                            </div>
                            <form:errors path="memberEmail" cssClass="text-danger small d-block mt-1" />
                            <div id="emailCheckResult" class="small mt-1"></div>
                        </div>
                        
                        <!-- 비밀번호 -->
                        <div class="mb-3">
                            <label for="memberPass" class="form-label">
                                <spring:message code="member.password" /> <span class="text-danger">*</span>
                            </label>
                            <form:password path="memberPass" 
                                          class="form-control" 
                                          id="memberPass" 
                                          placeholder="8자 이상 입력" />
                            <form:errors path="memberPass" cssClass="text-danger small d-block mt-1" />
                            <div class="form-text">
                                영문, 숫자, 특수문자 중 2가지 이상 조합 (8~20자)
                            </div>
                        </div>
                        
                        <!-- 비밀번호 확인 -->
                        <div class="mb-3">
                            <label for="memberPassConfirm" class="form-label">
                                <spring:message code="member.password.confirm" /> <span class="text-danger">*</span>
                            </label>
                            <input type="password" 
                                   class="form-control" 
                                   id="memberPassConfirm" 
                                   name="memberPassConfirm" 
                                   placeholder="비밀번호 재입력" />
                            <div id="passwordMatchResult" class="small mt-1"></div>
                        </div>
                        
                        <!-- 이름 -->
                        <div class="mb-3">
                            <label for="memberName" class="form-label">
                                <spring:message code="member.name" /> <span class="text-danger">*</span>
                            </label>
                            <form:input path="memberName" 
                                       class="form-control" 
                                       id="memberName" 
                                       placeholder="홍길동" />
                            <form:errors path="memberName" cssClass="text-danger small d-block mt-1" />
                        </div>
                        
                        <!-- 전화번호 -->
                        <div class="mb-3">
                            <label for="memberPhone" class="form-label">
                                <spring:message code="member.phone" />
                            </label>
                            <form:input path="memberPhone" 
                                       class="form-control" 
                                       id="memberPhone" 
                                       placeholder="010-0000-0000" 
                                       maxlength="13" />
                            <form:errors path="memberPhone" cssClass="text-danger small d-block mt-1" />
                        </div>
                        
                        <!-- 약관 동의 -->
                        <div class="mb-3">
                            <div class="form-check">
                                <input class="form-check-input" 
                                       type="checkbox" 
                                       id="termsAgree" 
                                       required>
                                <label class="form-check-label" for="termsAgree">
                                    <a href="#" data-bs-toggle="modal" data-bs-target="#termsModal">이용약관</a> 및 
                                    <a href="#" data-bs-toggle="modal" data-bs-target="#privacyModal">개인정보처리방침</a>에 동의합니다. 
                                    <span class="text-danger">*</span>
                                </label>
                            </div>
                        </div>
                        
                        <!-- 가입 버튼 -->
                        <button type="submit" class="btn btn-primary w-100 btn-lg" id="submitBtn">
                            <i class="bi bi-check-circle"></i> 회원가입
                        </button>
                    </form:form>
                    
                    <hr class="my-4">
                    
                    <div class="text-center">
                        <p class="text-muted mb-2">이미 회원이신가요?</p>
                        <a href="<c:url value='/member/login' />" class="btn btn-outline-secondary">
                            <i class="bi bi-box-arrow-in-right"></i> 로그인하기
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modals -->
<div class="modal fade" id="termsModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">이용약관</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p>여기에 이용약관 내용이 들어갑니다.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="privacyModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">개인정보처리방침</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p>여기에 개인정보처리방침 내용이 들어갑니다.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
            </div>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
    let emailChecked = false;
    
    // 이메일 중복 체크
    $('#checkEmailBtn').click(function() {
        const email = $('#memberEmail').val();
        if (!email) {
            alert('이메일을 입력해주세요.');
            return;
        }
        
        $.get('${pageContext.request.contextPath}/api/member/check-email', {email: email}, function(response) {
            if (response.success) {
                if (response.available) {
                    $('#emailCheckResult').html('<span class="text-success">✓ 사용 가능한 이메일입니다.</span>');
                    emailChecked = true;
                } else {
                    $('#emailCheckResult').html('<span class="text-danger">✗ 이미 사용중인 이메일입니다.</span>');
                    emailChecked = false;
                }
            }
        });
    });
    
    // 이메일 변경 시 체크 상태 초기화
    $('#memberEmail').on('input', function() {
        emailChecked = false;
        $('#emailCheckResult').html('');
    });
    
    // 비밀번호 확인
    $('#memberPassConfirm').on('input', function() {
        const pass = $('#memberPass').val();
        const passConfirm = $(this).val();
        
        if (passConfirm.length === 0) {
            $('#passwordMatchResult').html('');
        } else if (pass === passConfirm) {
            $('#passwordMatchResult').html('<span class="text-success">✓ 비밀번호가 일치합니다.</span>');
        } else {
            $('#passwordMatchResult').html('<span class="text-danger">✗ 비밀번호가 일치하지 않습니다.</span>');
        }
    });
    
    // 전화번호 자동 하이픈
    $('#memberPhone').on('input', function() {
        let value = $(this).val().replace(/[^0-9]/g, '');
        if (value.length > 3 && value.length <= 7) {
            value = value.substring(0, 3) + '-' + value.substring(3);
        } else if (value.length > 7) {
            value = value.substring(0, 3) + '-' + value.substring(3, 7) + '-' + value.substring(7, 11);
        }
        $(this).val(value);
    });
    
    // 폼 제출 검증
    $('#signupForm').submit(function(e) {
        if (!emailChecked) {
            alert('이메일 중복 확인을 해주세요.');
            e.preventDefault();
            return false;
        }
        
        const pass = $('#memberPass').val();
        const passConfirm = $('#memberPassConfirm').val();
        if (pass !== passConfirm) {
            alert('비밀번호가 일치하지 않습니다.');
            e.preventDefault();
            return false;
        }
        
        if (!$('#termsAgree').is(':checked')) {
            alert('약관에 동의해주세요.');
            e.preventDefault();
            return false;
        }
    });
});
</script>

<jsp:include page="../common/footer.jsp" />
