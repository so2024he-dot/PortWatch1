<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - 페이지를 찾을 수 없음 | PortWatch</title>
    
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
            color: #ffc107;
            margin-bottom: 30px;
            animation: shake 2s infinite;
        }
        
        @keyframes shake {
            0%, 100% { transform: rotate(0deg); }
            25% { transform: rotate(-10deg); }
            75% { transform: rotate(10deg); }
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
            margin: 5px;
        }
        
        .btn-home:hover {
            color: white;
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
        }
        
        .btn-back {
            background: #6c757d;
            color: white;
            padding: 15px 40px;
            border-radius: 50px;
            text-decoration: none;
            font-size: 16px;
            font-weight: 600;
            display: inline-block;
            transition: transform 0.3s, box-shadow 0.3s;
            margin: 5px;
        }
        
        .btn-back:hover {
            color: white;
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">
            <i class="fas fa-search"></i>
        </div>
        
        <div class="error-code">404</div>
        
        <h1 class="error-title">페이지를 찾을 수 없습니다</h1>
        
        <p class="error-message">
            요청하신 페이지가 존재하지 않거나 이동되었습니다.<br>
            URL을 확인하시거나 홈으로 돌아가세요.
        </p>
        
        <div>
            <a href="${pageContext.request.contextPath}/" class="btn-home">
                <i class="fas fa-home"></i> 홈으로 돌아가기
            </a>
            <a href="javascript:history.back()" class="btn-back">
                <i class="fas fa-arrow-left"></i> 이전 페이지
            </a>
        </div>
    </div>
    
    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
