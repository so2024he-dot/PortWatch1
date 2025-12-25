<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../common/header.jsp" />

<style>
    .signup-container {
        min-height: calc(100vh - 200px);
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 2rem 0;
    }
    
    .signup-card {
        background: white;
        border-radius: 20px;
        padding: 3rem;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
        max-width: 500px;
        width: 100%;
    }
    
    .signup-header {
        text-align: center;
        margin-bottom: 2rem;
    }
    
    .signup-icon {
        font-size: 4rem;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 1rem;
    }
    
    .signup-title {
        font-size: 2rem;
        font-weight: 700;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 0.5rem;
    }
    
    .signup-subtitle {
        color: #6b7280;
        font-size: 1rem;
    }
    
    .form-floating {
        margin-bottom: 1rem;
    }
    
    .form-control {
        border-radius: 10px;
        border: 2px solid #e5e7eb;
        padding: 1rem;
        transition: all 0.3s;
    }
    
    .form-control:focus {
        border-color: var(--primary-color);
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }
    
    .password-strength {
        height: 4px;
        background: #e5e7eb;
        border-radius: 4px;
        margin-top: 0.5rem;
        overflow: hidden;
    }
    
    .password-strength-bar {
        height: 100%;
        transition: all 0.3s;
    }
    
    .strength-weak { background: #ef4444; width: 33%; }
    .strength-medium { background: #f59e0b; width: 66%; }
    .strength-strong { background: #10b981; width: 100%; }
    
    .btn-signup {
        width: 100%;
        padding: 1rem;
        font-size: 1.1rem;
        font-weight: 600;
        border-radius: 10px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        color: white;
        transition: all 0.3s;
        margin-top: 1rem;
    }
    
    .btn-signup:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }
    
    .terms {
        margin: 1rem 0;
    }
    
    .terms label {
        color: #6b7280;
        font-size: 0.9rem;
    }
    
    .login-link {
        text-align: center;
        margin-top: 1.5rem;
        color: #6b7280;
    }
    
    .login-link a {
        color: var(--primary-color);
        font-weight: 600;
        text-decoration: none;
    }
    
    .login-link a:hover {
        text-decoration: underline;
    }
    
    @media (max-width: 576px) {
        .signup-card {
            padding: 2rem 1.5rem;
            margin: 1rem;
        }
        
        .signup-title {
            font-size: 1.5rem;
        }
        
        .signup-icon {
            font-size: 3rem;
        }
    }
</style>

<div class="signup-container">
    <div class="signup-card animate-fade-in">
        <div class="signup-header">
            <div class="signup-icon">
                <i class="bi bi-person-plus"></i>
            </div>
            <h2 class="signup-title">회원가입</h2>
            <p class="signup-subtitle">무료로 시작하세요</p>
        </div>
        
        <!-- Error Message -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">
                <i class="bi bi-exclamation-circle me-2"></i>${error}
            </div>
        </c:if>
        
        <!-- Signup Form -->
        <form action="${pageContext.request.contextPath}/member/signup" method="post" onsubmit="return validateForm()">
            <div class="form-floating mb-3">
                <input type="text" class="form-control" id="memberName" 
                       name="memberName" placeholder="홍길동" required>
                <label for="memberName">
                    <i class="bi bi-person me-2"></i>이름
                </label>
            </div>
            
            <div class="form-floating mb-3">
                <input type="email" class="form-control" id="memberEmail" 
                       name="memberEmail" placeholder="example@email.com" required>
                <label for="memberEmail">
                    <i class="bi bi-envelope me-2"></i>이메일
                </label>
                <small class="text-muted">이메일은 아이디로 사용됩니다</small>
            </div>
            
            <div class="form-floating mb-3">
                <input type="password" class="form-control" id="memberPass" 
                       name="memberPass" placeholder="Password" required
                       onkeyup="checkPasswordStrength()">
                <label for="memberPass">
                    <i class="bi bi-lock me-2"></i>비밀번호
                </label>
                <div class="password-strength">
                    <div class="password-strength-bar" id="strengthBar"></div>
                </div>
                <small class="text-muted" id="strengthText">4자 이상 입력하세요</small>
            </div>
            
            <div class="form-floating mb-3">
                <input type="password" class="form-control" id="memberPassConfirm" 
                       placeholder="Password" required>
                <label for="memberPassConfirm">
                    <i class="bi bi-lock-fill me-2"></i>비밀번호 확인
                </label>
            </div>
            
            <div class="form-floating mb-3">
                <input type="tel" class="form-control" id="memberPhone" 
                       name="memberPhone" placeholder="010-1234-5678">
                <label for="memberPhone">
                    <i class="bi bi-phone me-2"></i>전화번호 (선택)
                </label>
            </div>
            
            <div class="terms">
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" id="agreeTerms" required>
                    <label class="form-check-label" for="agreeTerms">
                        <a href="#" class="text-decoration-none">이용약관</a> 및 
                        <a href="#" class="text-decoration-none">개인정보처리방침</a>에 동의합니다
                    </label>
                </div>
            </div>
            
            <button type="submit" class="btn btn-signup">
                <i class="bi bi-check-circle me-2"></i>회원가입
            </button>
        </form>
        
        <div class="login-link">
            이미 계정이 있으신가요?
            <a href="${pageContext.request.contextPath}/member/login">
                로그인하기 <i class="bi bi-arrow-right"></i>
            </a>
        </div>
    </div>
</div>

<script>
function checkPasswordStrength() {
    const password = $('#memberPass').val();
    const strengthBar = $('#strengthBar');
    const strengthText = $('#strengthText');
    
    if (password.length === 0) {
        strengthBar.removeClass().addClass('password-strength-bar');
        strengthText.text('4자 이상 입력하세요');
    } else if (password.length < 6) {
        strengthBar.removeClass().addClass('password-strength-bar strength-weak');
        strengthText.text('약함 - 6자 이상 권장').css('color', '#ef4444');
    } else if (password.length < 10) {
        strengthBar.removeClass().addClass('password-strength-bar strength-medium');
        strengthText.text('보통 - 10자 이상 권장').css('color', '#f59e0b');
    } else {
        strengthBar.removeClass().addClass('password-strength-bar strength-strong');
        strengthText.text('강함 - 안전한 비밀번호입니다').css('color', '#10b981');
    }
}

function validateForm() {
    const password = $('#memberPass').val();
    const confirm = $('#memberPassConfirm').val();
    
    if (password !== confirm) {
        alert('비밀번호가 일치하지 않습니다.');
        return false;
    }
    
    if (password.length < 4) {
        alert('비밀번호는 4자 이상이어야 합니다.');
        return false;
    }
    
    return true;
}
</script>

<jsp:include page="../common/footer.jsp" />
