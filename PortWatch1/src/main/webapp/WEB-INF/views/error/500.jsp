<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - 서버 오류 | PortWatch</title>
    
    <!-- Bootstrap 5 CSS -->
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
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .error-container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            padding: 60px 40px;
            text-align: center;
            max-width: 600px;
            margin: 20px;
        }
        
        .error-icon {
            font-size: 100px;
            color: #dc3545;
            margin-bottom: 30px;
            animation: bounce 2s infinite;
        }
        
        @keyframes bounce {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-20px); }
        }
        
        .error-code {
            font-size: 80px;
            font-weight: bold;
            color: #333;
            margin-bottom: 20px;
        }
        
        .error-title {
            font-size: 28px;
            color: #555;
            margin-bottom: 20px;
        }
        
        .error-message {
            font-size: 16px;
            color: #777;
            margin-bottom: 40px;
            line-height: 1.6;
        }
        
        .btn-home {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px 40px;
            border-radius: 50px;
            text-decoration: none;
            font-size: 16px;
            font-weight: 600;
            display: inline-block;
            transition: transform 0.3s, box-shadow 0.3s;
        }
        
        .btn-home:hover {
            color: white;
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
        }
        
        .error-details {
            margin-top: 30px;
            padding: 20px;
            background: #f8f9fa;
            border-radius: 10px;
            text-align: left;
            font-size: 14px;
            color: #666;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">
            <i class="fas fa-exclamation-triangle"></i>
        </div>
        
        <div class="error-code">500</div>
        
        <h1 class="error-title">서버 내부 오류</h1>
        
        <p class="error-message">
            죄송합니다. 서버에서 오류가 발생했습니다.<br>
            잠시 후 다시 시도해 주세요.
        </p>
        
        <a href="${pageContext.request.contextPath}/" class="btn-home">
            <i class="fas fa-home"></i> 홈으로 돌아가기
        </a>
        
        <% if (exception != null) { %>
        <div class="error-details">
            <strong><i class="fas fa-info-circle"></i> 오류 정보:</strong><br>
            <small><%= exception.getMessage() %></small>
        </div>
        <% } %>
    </div>
    
    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
