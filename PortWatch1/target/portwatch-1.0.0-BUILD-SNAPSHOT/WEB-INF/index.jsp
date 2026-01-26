<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PortWatch - 주식 포트폴리오 관리 시스템</title>
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
            max-width: 600px;
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
            padding: 10px 20px;
            border-radius: 20px;
            display: inline-block;
            margin-bottom: 20px;
            font-size: 0.9em;
            font-weight: 600;
        }
        
        h1 {
            font-size: 3em;
            color: #667eea;
            margin-bottom: 10px;
            font-weight: 700;
        }
        
        .subtitle {
            font-size: 1.1em;
            color: #999;
            margin-bottom: 30px;
        }
        
        p {
            font-size: 1.1em;
            color: #666;
            margin-bottom: 40px;
            line-height: 1.8;
        }
        
        .buttons {
            display: flex;
            gap: 20px;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .btn {
            padding: 15px 40px;
            font-size: 1.1em;
            border: none;
            border-radius: 50px;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.3s;
            font-weight: 600;
            display: inline-block;
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
            background: #f0f0f0;
            color: #333;
        }
        
        .btn-secondary:hover {
            background: #e0e0e0;
            transform: translateY(-3px);
        }
        
        .features {
            margin: 40px 0;
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 20px;
            text-align: left;
        }
        
        .feature {
            padding: 15px;
            background: #f8f9fa;
            border-radius: 10px;
        }
        
        .feature-icon {
            font-size: 2em;
            margin-bottom: 10px;
        }
        
        .feature-title {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
        }
        
        .feature-desc {
            font-size: 0.9em;
            color: #666;
        }
        
        .version {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #eee;
            font-size: 0.9em;
            color: #999;
        }
        
        .version strong {
            color: #667eea;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="status">✓ 시스템 정상 작동 중</div>
        
        <h1>PortWatch</h1>
        <div class="subtitle">Stock Portfolio Management System</div>
        
        <p>
            한국 & 미국 주식 포트폴리오를 한눈에 관리하는<br>
            스마트한 투자 관리 시스템입니다.
        </p>
        
        <div class="features">
            <div class="feature">
                <div class="feature-icon">📊</div>
                <div class="feature-title">실시간 시세</div>
                <div class="feature-desc">실시간 주가 확인</div>
            </div>
            <div class="feature">
                <div class="feature-icon">💼</div>
                <div class="feature-title">포트폴리오</div>
                <div class="feature-desc">자산 관리</div>
            </div>
            <div class="feature">
                <div class="feature-icon">📈</div>
                <div class="feature-title">수익률 분석</div>
                <div class="feature-desc">투자 성과 추적</div>
            </div>
            <div class="feature">
                <div class="feature-icon">🌏</div>
                <div class="feature-title">글로벌 투자</div>
                <div class="feature-desc">한국+미국 주식</div>
            </div>
        </div>
        
        <div class="buttons">
            <a href="${pageContext.request.contextPath}/member/login" class="btn btn-primary">로그인</a>
            <a href="${pageContext.request.contextPath}/member/join" class="btn btn-secondary">회원가입</a>
        </div>
        
        <div class="version">
            <strong>PortWatch v1.0</strong><br>
            Spring Framework 5.0.7.RELEASE | MySQL 8.0.33 | HikariCP 3.4.5
        </div>
    </div>
</body>
</html>
