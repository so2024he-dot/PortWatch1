<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PortWatch - 나만의 주식 포트폴리오</title>
    
    <!-- CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        
        .hero-section {
            color: white;
            padding: 100px 0;
            text-align: center;
        }
        
        .hero-section h1 {
            font-size: 3.5rem;
            font-weight: bold;
            margin-bottom: 20px;
        }
        
        .hero-section p {
            font-size: 1.5rem;
            margin-bottom: 40px;
        }
        
        .feature-card {
            background: white;
            border-radius: 15px;
            padding: 30px;
            margin: 20px 0;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            transition: transform 0.3s ease;
        }
        
        .feature-card:hover {
            transform: translateY(-10px);
        }
        
        .feature-icon {
            font-size: 3rem;
            color: #667eea;
            margin-bottom: 20px;
        }
        
        .btn-start {
            background: white;
            color: #667eea;
            font-size: 1.2rem;
            padding: 15px 40px;
            border-radius: 50px;
            border: none;
            font-weight: bold;
            transition: all 0.3s ease;
        }
        
        .btn-start:hover {
            background: #764ba2;
            color: white;
            transform: scale(1.05);
        }
    </style>
</head>
<body>
    <!-- Hero Section -->
    <div class="hero-section">
        <div class="container">
            <h1><i class="fas fa-chart-line"></i> PortWatch</h1>
            <p>한국과 미국 주식을 한눈에 관리하세요</p>
            
            <c:choose>
                <c:when test="${not empty sessionScope.memberId}">
                    <a href="${pageContext.request.contextPath}/stocks" class="btn btn-start">
                        <i class="fas fa-briefcase"></i> 내 포트폴리오 보기
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/member/login" class="btn btn-start">
                        <i class="fas fa-sign-in-alt"></i> 시작하기
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <!-- Features Section -->
    <div class="container mt-5">
        <div class="row">
            <div class="col-md-4">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-globe-americas"></i>
                    </div>
                    <h3>한국 + 미국 주식</h3>
                    <p>KOSPI, KOSDAQ, NASDAQ, NYSE의 주식을 한 곳에서 관리하세요.</p>
                </div>
            </div>
            
            <div class="col-md-4">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-chart-bar"></i>
                    </div>
                    <h3>실시간 차트</h3>
                    <p>일봉, 주봉, 월봉 차트로 종목 흐름을 한눈에 파악하세요.</p>
                </div>
            </div>
            
            <div class="col-md-4">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-newspaper"></i>
                    </div>
                    <h3>자동 뉴스 수집</h3>
                    <p>관련 뉴스를 자동으로 수집하여 투자 결정을 도와드립니다.</p>
                </div>
            </div>
        </div>
        
        <div class="row mt-4">
            <div class="col-md-4">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-filter"></i>
                    </div>
                    <h3>스마트 필터링</h3>
                    <p>나라, 시장, 업종별로 원하는 종목을 빠르게 찾으세요.</p>
                </div>
            </div>
            
            <div class="col-md-4">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-shopping-cart"></i>
                    </div>
                    <h3>간편한 매매</h3>
                    <p>실시간 검증과 함께 안전하게 주식을 매매하세요.</p>
                </div>
            </div>
            
            <div class="col-md-4">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-wallet"></i>
                    </div>
                    <h3>포트폴리오 관리</h3>
                    <p>수익률, 보유 종목, 거래 내역을 한눈에 확인하세요.</p>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Footer -->
    <div class="container mt-5 pb-5 text-center text-white">
        <p>&copy; 2025 PortWatch. All rights reserved.</p>
        <p>
            <a href="#" class="text-white mr-3">이용약관</a>
            <a href="#" class="text-white mr-3">개인정보처리방침</a>
            <a href="#" class="text-white">고객센터</a>
        </p>
    </div>
    
    <!-- Scripts -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>
</body>
</html>
