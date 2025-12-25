<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../common/header.jsp" />

<style>
    .login-container {
        min-height: calc(100vh - 200px);
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 2rem 0;
    }
    
    .login-card {
        background: white;
        border-radius: 20px;
        padding: 3rem;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
        max-width: 450px;
        width: 100%;
    }
    
    .login-header {
        text-align: center;
        margin-bottom: 2rem;
    }
    
    .login-icon {
        font-size: 4rem;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 1rem;
    }
    
    .login-title {
        font-size: 2rem;
        font-weight: 700;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 0.5rem;
    }
    
    .login-subtitle {
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
        font-size: 1rem;
        transition: all 0.3s;
    }
    
    .form-control:focus {
        border-color: var(--primary-color);
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }
    
    .btn-login {
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
    
    .btn-login:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }
    
    .divider {
        text-align: center;
        margin: 2rem 0;
        position: relative;
    }
    
    .divider::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        width: 100%;
        height: 1px;
        background: #e5e7eb;
    }
    
    .divider span {
        background: white;
        padding: 0 1rem;
        position: relative;
        color: #6b7280;
    }
    
    .signup-link {
        text-align: center;
        margin-top: 1.5rem;
        color: #6b7280;
    }
    
    .signup-link a {
        color: var(--primary-color);
        font-weight: 600;
        text-decoration: none;
    }
    
    .signup-link a:hover {
        text-decoration: underline;
    }
    
    .remember-forgot {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin: 1rem 0;
    }
    
    .form-check-label {
        color: #6b7280;
    }
    
    .forgot-link {
        color: var(--primary-color);
        text-decoration: none;
        font-size: 0.9rem;
    }
    
    .forgot-link:hover {
        text-decoration: underline;
    }
    
    @media (max-width: 576px) {
        .login-card {
            padding: 2rem 1.5rem;
            margin: 1rem;
        }
        
        .login-title {
            font-size: 1.5rem;
        }
        
        .login-icon {
            font-size: 3rem;
        }
    }
</style>

<div class="login-container">
    <div class="login-card animate-fade-in">
        <div class="login-header">
            <div class="login-icon">
                <i class="bi bi-shield-lock"></i>
            </div>
            <h2 class="login-title">로그인</h2>
            <p class="login-subtitle">PortWatch에 오신 것을 환영합니다</p>
        </div>
        
        <!-- Error Message -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">
                <i class="bi bi-exclamation-circle me-2"></i>${error}
            </div>
        </c:if>
        
        <!-- Success Message -->
        <c:if test="${not empty message}">
            <div class="alert alert-success" role="alert">
                <i class="bi bi-check-circle me-2"></i>${message}
            </div>
        </c:if>
        
        <!-- Login Form -->
        <form action="${pageContext.request.contextPath}/member/login" method="post">
            <div class="form-floating mb-3">
                <input type="email" class="form-control" id="memberEmail" 
                       name="memberEmail" placeholder="example@email.com" required autofocus>
                <label for="memberEmail">
                    <i class="bi bi-envelope me-2"></i>이메일
                </label>
            </div>
            
            <div class="form-floating mb-3">
                <input type="password" class="form-control" id="memberPass" 
                       name="memberPass" placeholder="Password" required>
                <label for="memberPass">
                    <i class="bi bi-lock me-2"></i>비밀번호
                </label>
            </div>
            
            <div class="remember-forgot">
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" id="rememberMe">
                    <label class="form-check-label" for="rememberMe">
                        로그인 상태 유지
                    </label>
                </div>
                <a href="#" class="forgot-link">비밀번호 찾기</a>
            </div>
            
            <button type="submit" class="btn btn-login">
                <i class="bi bi-box-arrow-in-right me-2"></i>로그인
            </button>
        </form>
        
        <div class="divider">
            <span>또는</span>
        </div>
        
        <div class="signup-link">
            아직 계정이 없으신가요?
            <a href="${pageContext.request.contextPath}/member/signup">
                회원가입하기 <i class="bi bi-arrow-right"></i>
            </a>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
