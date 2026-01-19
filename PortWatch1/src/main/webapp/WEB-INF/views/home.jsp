<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PortWatch - 스마트 포트폴리오 관리</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .home-container {
            max-width: 900px;
            margin: 0 auto;
            padding: 2rem;
        }
        
        .logo-section {
            text-align: center;
            margin-bottom: 3rem;
            color: white;
        }
        
        .logo-section h1 {
            font-size: 4rem;
            font-weight: 700;
            margin-bottom: 1rem;
        }
        
        .logo-section p {
            font-size: 1.5rem;
            opacity: 0.9;
        }
        
        .card {
            background: white;
            border-radius: 20px;
            padding: 3rem;
            box-shadow: 0 10px 40px rgba(0,0,0,0.3);
        }
        
        .feature-box {
            text-align: center;
            padding: 2rem;
            margin-bottom: 2rem;
        }
        
        .feature-box i {
            font-size: 3rem;
            color: #667eea;
            margin-bottom: 1rem;
        }
        
        .feature-box h3 {
            font-weight: 700;
            margin-bottom: 1rem;
        }
        
        .btn-action {
            padding: 1rem 3rem;
            font-size: 1.2rem;
            font-weight: 700;
            border-radius: 50px;
            margin: 0.5rem;
            transition: all 0.3s;
        }
        
        .btn-primary-custom {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
        }
        
        .btn-primary-custom:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary-custom {
            background: white;
            border: 2px solid #667eea;
            color: #667eea;
        }
        
        .btn-secondary-custom:hover {
            background: #f3f4f6;
            transform: translateY(-3px);
        }
        
        .action-section {
            text-align: center;
            margin-top: 3rem;
        }
    </style>
</head>
<body>
    <div class="home-container">
        <!-- 로고 섹션 -->
        <div class="logo-section">
            <h1>
                <i class="fas fa-chart-line"></i> PortWatch
            </h1>
            <p>스마트 포트폴리오 관리 시스템</p>
        </div>
        
        <!-- 메인 카드 -->
        <div class="card">
            <div class="row">
                <!-- 기능 1 -->
                <div class="col-md-4">
                    <div class="feature-box">
                        <i class="fas fa-wallet"></i>
                        <h3>포트폴리오 관리</h3>
                        <p class="text-muted">한국·미국 주식을 한 곳에서 관리하세요</p>
                    </div>
                </div>
                
                <!-- 기능 2 -->
                <div class="col-md-4">
                    <div class="feature-box">
                        <i class="fas fa-chart-pie"></i>
                        <h3>실시간 분석</h3>
                        <p class="text-muted">총 평가액과 손익을 실시간으로 확인</p>
                    </div>
                </div>
                
                <!-- 기능 3 -->
                <div class="col-md-4">
                    <div class="feature-box">
                        <i class="fas fa-newspaper"></i>
                        <h3>뉴스 모니터링</h3>
                        <p class="text-muted">주요 뉴스를 자동으로 수집</p>
                    </div>
                </div>
            </div>
            
            <!-- 액션 버튼 -->
            <div class="action-section">
                <a href="${pageContext.request.contextPath}/member/login" 
                   class="btn btn-primary-custom btn-action">
                    <i class="fas fa-sign-in-alt"></i> 로그인
                </a>
                
                <a href="${pageContext.request.contextPath}/member/register" 
                   class="btn btn-secondary-custom btn-action">
                    <i class="fas fa-user-plus"></i> 회원가입
                </a>
            </div>
            
            <!-- 추가 정보 -->
            <div class="text-center mt-4">
                <p class="text-muted">
                    <i class="fas fa-info-circle"></i> 
                    한국 주식과 미국 주식을 모두 지원합니다
                </p>
            </div>
        </div>
        
        <!-- 푸터 -->
        <div class="text-center mt-4">
            <p class="text-white">
                © 2026 PortWatch. All rights reserved.
            </p>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
