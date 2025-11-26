<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PortWatch - 포트폴리오 관리 시스템</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        
        /* 네비게이션 바 */
        nav {
            background: rgba(255, 255, 255, 0.95);
            padding: 1rem 2rem;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            position: sticky;
            top: 0;
            z-index: 1000;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .nav-brand {
            font-size: 1.5rem;
            font-weight: bold;
            color: #667eea;
            text-decoration: none;
        }
        
        .nav-links {
            display: flex;
            gap: 2rem;
            list-style: none;
        }
        
        .nav-links a {
            text-decoration: none;
            color: #333;
            font-weight: 500;
            transition: color 0.3s;
        }
        
        .nav-links a:hover {
            color: #667eea;
        }
        
        .nav-user {
            display: flex;
            gap: 1rem;
            align-items: center;
        }
        
        .btn {
            padding: 0.5rem 1.5rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 500;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s;
        }
        
        .btn-primary {
            background: #667eea;
            color: white;
        }
        
        .btn-primary:hover {
            background: #5568d3;
        }
        
        .btn-secondary {
            background: #e0e0e0;
            color: #333;
        }
        
        .btn-secondary:hover {
            background: #d0d0d0;
        }
        
        /* 메인 컨테이너 */
        .container {
            max-width: 1200px;
            margin: 3rem auto;
            padding: 0 2rem;
        }
        
        .hero {
            background: white;
            padding: 4rem;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            text-align: center;
        }
        
        .hero-icon {
            font-size: 5rem;
            margin-bottom: 1rem;
        }
        
        .hero h1 {
            color: #667eea;
            font-size: 3rem;
            margin-bottom: 1rem;
        }
        
        .hero p {
            color: #666;
            font-size: 1.3rem;
            margin-bottom: 2rem;
        }
        
        /* 기능 카드 */
        .features {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 2rem;
            margin-top: 3rem;
        }
        
        .feature-card {
            background: white;
            padding: 2rem;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            text-align: center;
            transition: transform 0.3s;
        }
        
        .feature-card:hover {
            transform: translateY(-10px);
        }
        
        .feature-icon {
            font-size: 3rem;
            margin-bottom: 1rem;
        }
        
        .feature-card h3 {
            color: #667eea;
            margin-bottom: 0.5rem;
        }
        
        .feature-card p {
            color: #666;
            font-size: 0.9rem;
        }
        
        /* 알림 메시지 */
        .alert {
            padding: 1rem;
            border-radius: 10px;
            margin-bottom: 2rem;
            display: none;
        }
        
        .alert.show {
            display: block;
        }
        
        .alert-success {
            background: #e8f5e9;
            border-left: 4px solid #4caf50;
            color: #2e7d32;
        }
        
        .alert-info {
            background: #e3f2fd;
            border-left: 4px solid #2196f3;
            color: #1565c0;
        }
        
        .alert-error {
            background: #ffebee;
            border-left: 4px solid #f44336;
            color: #c62828;
        }
    </style>
</head>
<body>
    <!-- 네비게이션 바 -->
    <nav>
        <a href="${pageContext.request.contextPath}/" class="nav-brand">📈 PortWatch</a>
        
        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/">홈</a></li>
            <li><a href="${pageContext.request.contextPath}/portfolio/list">포트폴리오</a></li>
            <li><a href="${pageContext.request.contextPath}/stock/list">종목</a></li>
            <c:if test="${not empty member}">
                <li><a href="${pageContext.request.contextPath}/member/mypage">마이페이지</a></li>
            </c:if>
        </ul>
        
        <div class="nav-user">
            <c:choose>
                <c:when test="${not empty member}">
                    <span>환영합니다, ${member.memberName}님!</span>
                    <a href="${pageContext.request.contextPath}/member/logout" class="btn btn-secondary">로그아웃</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/member/login" class="btn btn-secondary">로그인</a>
                    <a href="${pageContext.request.contextPath}/member/signup" class="btn btn-primary">회원가입</a>
                </c:otherwise>
            </c:choose>
        </div>
    </nav>
    
    <!-- 메인 컨텐츠 -->
    <div class="container">
        <!-- 알림 메시지 -->
        <c:if test="${not empty message}">
            <div class="alert alert-${messageType} show">
                ${message}
            </div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error show">
                ${error}
            </div>
        </c:if>
        
        <!-- 히어로 섹션 -->
        <div class="hero">
            <div class="hero-icon">🎯</div>
            <h1>PortWatch</h1>
            <p>당신의 주식 포트폴리오를 스마트하게 관리하세요</p>
            
            <c:choose>
                <c:when test="${not empty member}">
                    <a href="${pageContext.request.contextPath}/portfolio/list" class="btn btn-primary" style="padding: 1rem 3rem; font-size: 1.1rem;">
                        포트폴리오 시작하기
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/member/signup" class="btn btn-primary" style="padding: 1rem 3rem; font-size: 1.1rem;">
                        무료로 시작하기
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- 기능 카드 -->
        <div class="features">
            <div class="feature-card">
                <div class="feature-icon">📊</div>
                <h3>포트폴리오 관리</h3>
                <p>여러 개의 포트폴리오를 만들고 실시간으로 수익률을 확인하세요</p>
            </div>
            
            <div class="feature-card">
                <div class="feature-icon">📈</div>
                <h3>종목 분석</h3>
                <p>KOSPI, KOSDAQ 종목 정보와 가격 변동을 한눈에 확인하세요</p>
            </div>
            
            <div class="feature-card">
                <div class="feature-icon">⭐</div>
                <h3>관심종목</h3>
                <p>관심있는 종목을 저장하고 빠르게 접근하세요</p>
            </div>
            
            <div class="feature-card">
                <div class="feature-icon">📰</div>
                <h3>뉴스</h3>
                <p>최신 주식 시장 뉴스를 실시간으로 받아보세요</p>
            </div>
        </div>
    </div>
    
    <script>
        // 알림 메시지 자동 숨김 (3초 후)
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert.show');
            alerts.forEach(alert => {
                alert.style.opacity = '0';
                setTimeout(() => alert.style.display = 'none', 500);
            });
        }, 3000);
    </script>
</body>
</html>
