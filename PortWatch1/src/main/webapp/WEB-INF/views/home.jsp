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
            transition: all 0.3s ease;
            cursor: pointer;
        }
        
        .feature-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 15px 40px rgba(102, 126, 234, 0.4);
            background: linear-gradient(135deg, #f8f9ff 0%, #ffffff 100%);
        }
        
        .feature-card:active {
            transform: translateY(-5px);
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
            <!-- ✅ 한국 + 미국 주식 카드 - 클릭 가능 -->
            <div class="col-md-4">
                <a href="${pageContext.request.contextPath}/stock/list" style="text-decoration: none; color: inherit;">
                    <div class="feature-card" style="cursor: pointer;">
                        <div class="feature-icon">
                            <i class="fas fa-globe-americas"></i>
                        </div>
                        <h3>한국 + 미국 주식</h3>
                        <p>KOSPI, KOSDAQ, NASDAQ, NYSE의 주식을 한 곳에서 관리하세요.</p>
                        <small class="text-muted">클릭하여 종목 보기 →</small>
                    </div>
                </a>
            </div>
            
            <!-- ✅ 실시간 차트 카드 - 클릭 가능 -->
            <div class="col-md-4">
                <a href="${pageContext.request.contextPath}/stock/list?country=KR" style="text-decoration: none; color: inherit;">
                    <div class="feature-card" style="cursor: pointer;">
                        <div class="feature-icon">
                            <i class="fas fa-chart-bar"></i>
                        </div>
                        <h3>실시간 차트</h3>
                        <p>일봉, 주봉, 월봉 차트로 종목 흐름을 한눈에 파악하세요.</p>
                        <small class="text-muted">클릭하여 차트 보기 →</small>
                    </div>
                </a>
            </div>
            
            <!-- ✅ 자동 뉴스 수집 카드 - 클릭 가능 -->
            <div class="col-md-4">
                <a href="${pageContext.request.contextPath}/news/list" style="text-decoration: none; color: inherit;">
                    <div class="feature-card" style="cursor: pointer;">
                        <div class="feature-icon">
                            <i class="fas fa-newspaper"></i>
                        </div>
                        <h3>자동 뉴스 수집</h3>
                        <p>관련 뉴스를 자동으로 수집하여 투자 결정을 도와드립니다.</p>
                        <small class="text-muted">클릭하여 뉴스 보기 →</small>
                    </div>
                </a>
            </div>
        </div>
        
        <div class="row mt-4">
            <!-- ✅ 스마트 필터링 카드 - 클릭 가능 -->
            <div class="col-md-4">
                <a href="${pageContext.request.contextPath}/stock/list?country=US" style="text-decoration: none; color: inherit;">
                    <div class="feature-card" style="cursor: pointer;">
                        <div class="feature-icon">
                            <i class="fas fa-filter"></i>
                        </div>
                        <h3>스마트 필터링</h3>
                        <p>나라, 시장, 업종별로 원하는 종목을 빠르게 찾으세요.</p>
                        <small class="text-muted">클릭하여 필터 사용 →</small>
                    </div>
                </a>
            </div>
            
            <!-- ✅ 간편한 매매 카드 - 클릭 가능 -->
            <div class="col-md-4">
                <c:choose>
                    <c:when test="${not empty sessionScope.memberId}">
                        <a href="${pageContext.request.contextPath}/portfolio/list" style="text-decoration: none; color: inherit;">
                            <div class="feature-card" style="cursor: pointer;">
                                <div class="feature-icon">
                                    <i class="fas fa-shopping-cart"></i>
                                </div>
                                <h3>간편한 매매</h3>
                                <p>실시간 검증과 함께 안전하게 주식을 매매하세요.</p>
                                <small class="text-muted">클릭하여 매매하기 →</small>
                            </div>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/member/login" style="text-decoration: none; color: inherit;">
                            <div class="feature-card" style="cursor: pointer;">
                                <div class="feature-icon">
                                    <i class="fas fa-shopping-cart"></i>
                                </div>
                                <h3>간편한 매매</h3>
                                <p>실시간 검증과 함께 안전하게 주식을 매매하세요.</p>
                                <small class="text-info">로그인이 필요합니다 →</small>
                            </div>
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <!-- ✅ 포트폴리오 관리 카드 - 클릭 가능 -->
            <div class="col-md-4">
                <c:choose>
                    <c:when test="${not empty sessionScope.memberId}">
                        <a href="${pageContext.request.contextPath}/dashboard" style="text-decoration: none; color: inherit;">
                            <div class="feature-card" style="cursor: pointer;">
                                <div class="feature-icon">
                                    <i class="fas fa-wallet"></i>
                                </div>
                                <h3>포트폴리오 관리</h3>
                                <p>수익률, 보유 종목, 거래 내역을 한눈에 확인하세요.</p>
                                <small class="text-muted">클릭하여 대시보드 보기 →</small>
                            </div>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/member/login" style="text-decoration: none; color: inherit;">
                            <div class="feature-card" style="cursor: pointer;">
                                <div class="feature-icon">
                                    <i class="fas fa-wallet"></i>
                                </div>
                                <h3>포트폴리오 관리</h3>
                                <p>수익률, 보유 종목, 거래 내역을 한눈에 확인하세요.</p>
                                <small class="text-info">로그인이 필요합니다 →</small>
                            </div>
                        </a>
                    </c:otherwise>
                </c:choose>
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
