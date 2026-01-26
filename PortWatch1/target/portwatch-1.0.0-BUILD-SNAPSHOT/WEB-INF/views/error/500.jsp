<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - 서버 오류 | PortWatch</title>
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
        
        .error-container {
            background: white;
            padding: 60px;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            text-align: center;
            max-width: 600px;
            width: 100%;
        }
        
        .error-icon {
            font-size: 80px;
            margin-bottom: 20px;
            animation: shake 0.5s;
        }
        
        @keyframes shake {
            0%, 100% { transform: rotate(0deg); }
            25% { transform: rotate(-5deg); }
            75% { transform: rotate(5deg); }
        }
        
        h1 {
            font-size: 4em;
            color: #e74c3c;
            margin-bottom: 10px;
        }
        
        h2 {
            font-size: 1.5em;
            color: #333;
            margin-bottom: 20px;
        }
        
        p {
            color: #666;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        
        .error-details {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 30px;
            text-align: left;
        }
        
        .error-details strong {
            color: #e74c3c;
        }
        
        .error-details pre {
            margin-top: 10px;
            padding: 10px;
            background: #fff;
            border-left: 3px solid #e74c3c;
            overflow-x: auto;
            font-size: 0.9em;
        }
        
        .buttons {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .btn {
            padding: 15px 30px;
            border: none;
            border-radius: 50px;
            cursor: pointer;
            text-decoration: none;
            font-size: 1em;
            font-weight: 600;
            transition: all 0.3s;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: #95a5a6;
            color: white;
        }
        
        .btn-secondary:hover {
            background: #7f8c8d;
            transform: translateY(-2px);
        }
        
        .help-text {
            margin-top: 30px;
            font-size: 0.9em;
            color: #999;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">⚠️</div>
        <h1>500</h1>
        <h2>서버 내부 오류</h2>
        <p>
            죄송합니다. 서버에서 오류가 발생했습니다.<br>
            잠시 후 다시 시도해 주세요.
        </p>
        
        <% if (exception != null) { %>
        <div class="error-details">
            <strong>오류 정보:</strong>
            <pre><%= exception.getMessage() != null ? exception.getMessage() : "알 수 없는 오류" %></pre>
        </div>
        <% } %>
        
        <div class="buttons">
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">홈으로 돌아가기</a>
            <a href="javascript:history.back()" class="btn btn-secondary">이전 페이지</a>
        </div>
        
        <div class="help-text">
            문제가 계속되면 관리자에게 문의하세요.
        </div>
    </div>
</body>
</html>
