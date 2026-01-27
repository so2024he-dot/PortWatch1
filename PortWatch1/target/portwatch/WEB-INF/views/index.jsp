<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PortWatch - 주식 포트폴리오 관리</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        
        .container {
            background: white;
            padding: 60px;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            text-align: center;
            max-width: 700px;
            width: 100%;
            animation: fadeIn 0.5s ease-in;
        }
        
        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .status {
            background: #4CAF50;
            color: white;
            padding: 12px 24px;
            border-radius: 25px;
            display: inline-block;
            margin-bottom: 25px;
            font-weight: 600;
            font-size: 1.1em;
            animation: pulse 2s infinite;
        }
        
        @keyframes pulse {
            0%, 100% {
                transform: scale(1);
            }
            50% {
                transform: scale(1.05);
            }
        }
        
        h1 {
            font-size: 3.5em;
            color: #667eea;
            margin-bottom: 20px;
            font-weight: 700;
        }
        
        .message {
            color: #666;
            margin-bottom: 30px;
            line-height: 1.8;
            font-size: 1.2em;
        }
        
        .server-time {
            background: #f5f5f5;
            padding: 15px;
            border-radius: 10px;
            color: #666;
            font-size: 0.95em;
            margin-bottom: 35px;
        }
        
        .btn {
            padding: 16px 45px;
            border: none;
            border-radius: 50px;
            font-size: 1.15em;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.3s;
            display: inline-block;
            margin: 0 12px;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: white;
            color: #667eea;
            border: 2px solid #667eea;
        }
        
        .btn-secondary:hover {
            background: #667eea;
            color: white;
            transform: translateY(-3px);
        }
        
        .features {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
            margin-top: 45px;
        }
        
        .feature {
            background: #f8f9fa;
            padding: 25px;
            border-radius: 12px;
            transition: all 0.3s;
        }
        
        .feature:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        
        .feature h3 {
            color: #667eea;
            margin-bottom: 12px;
            font-size: 1.3em;
        }
        
        .feature p {
            color: #666;
            font-size: 1em;
        }
        
        .build-info {
            margin-top: 30px;
            padding-top: 25px;
            border-top: 2px solid #eee;
            color: #999;
            font-size: 0.9em;
        }
        
        @media (max-width: 768px) {
            .container {
                padding: 40px 30px;
            }
            
            h1 {
                font-size: 2.5em;
            }
            
            .features {
                grid-template-columns: 1fr;
            }
            
            .btn {
                display: block;
                margin: 10px 0;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="status">✓ 시스템 정상 작동 중</div>
        
        <h1>PortWatch</h1>
        
        <div class="message">
            <c:choose>
                <c:when test="${not empty message}">
                    ${message}
                </c:when>
                <c:otherwise>
                    한국 & 미국 주식 포트폴리오를<br>스마트하게 관리하세요
                </c:otherwise>
            </c:choose>
        </div>
        
        <c:if test="${not empty serverTime}">
            <div class="server-time">
                🕐 서버 시간: ${serverTime}
            </div>
        </c:if>
        
        <div style="margin-top: 30px;">
            <a href="${pageContext.request.contextPath}/member/login" class="btn btn-primary">로그인</a>
            <a href="${pageContext.request.contextPath}/member/signup" class="btn btn-secondary">회원가입</a>
        </div>
        
        <div class="features">
            <div class="feature">
                <h3>📊 실시간 주가</h3>
                <p>한국 & 미국 주식<br>실시간 시세 확인</p>
            </div>
            <div class="feature">
                <h3>💼 포트폴리오</h3>
                <p>보유 종목<br>수익률 관리</p>
            </div>
            <div class="feature">
                <h3>📰 뉴스 크롤링</h3>
                <p>종목별<br>최신 뉴스</p>
            </div>
            <div class="feature">
                <h3>📈 차트 분석</h3>
                <p>주가 추이<br>시각화</p>
            </div>
        </div>
        
        <div class="build-info">
            <strong>PortWatch v1.0.0</strong><br>
            Spring Framework 5.0.7 | MySQL 8.0.33 | MyBatis 3.5.6
        </div>
    </div>
</body>
</html>
