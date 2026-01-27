<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>오류 - PortWatch</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        
        .error-container {
            background: white;
            border-radius: 20px;
            padding: 50px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
            max-width: 600px;
            text-align: center;
        }
        
        .error-icon {
            font-size: 6rem;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin-bottom: 20px;
        }
        
        .error-title {
            font-size: 2rem;
            font-weight: 700;
            color: #1f2937;
            margin-bottom: 15px;
        }
        
        .error-message {
            font-size: 1.1rem;
            color: #6b7280;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        
        .error-details {
            background: #f3f4f6;
            padding: 20px;
            border-radius: 10px;
            text-align: left;
            margin-bottom: 30px;
            font-family: monospace;
            font-size: 0.9rem;
            color: #374151;
            max-height: 200px;
            overflow-y: auto;
        }
        
        .btn-home {
            padding: 12px 30px;
            font-size: 1.1rem;
            font-weight: 600;
            border-radius: 10px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s;
        }
        
        .btn-home:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
            color: white;
        }
        
        .btn-back {
            padding: 12px 30px;
            font-size: 1.1rem;
            font-weight: 600;
            border-radius: 10px;
            background: #f3f4f6;
            border: none;
            color: #6b7280;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s;
            margin-left: 10px;
        }
        
        .btn-back:hover {
            background: #e5e7eb;
            color: #374151;
        }
    </style>
</head>
<body>
    <div class="error-container animate-fade-in">
        <div class="error-icon">
            <i class="bi bi-exclamation-triangle"></i>
        </div>
        
        <h1 class="error-title">오류가 발생했습니다</h1>
        
        <p class="error-message">
            <c:choose>
                <c:when test="${not empty errorMessage}">
                    ${errorMessage}
                </c:when>
                <c:otherwise>
                    요청하신 페이지를 처리하는 중 문제가 발생했습니다.
                </c:otherwise>
            </c:choose>
        </p>
        
        <c:if test="${not empty exception}">
            <div class="error-details">
                <strong>상세 정보:</strong><br>
                ${exception.message}
            </div>
        </c:if>
        
        <div>
            <a href="${pageContext.request.contextPath}/" class="btn-home">
                <i class="bi bi-house-door me-2"></i>홈으로 가기
            </a>
            <a href="javascript:history.back()" class="btn-back">
                <i class="bi bi-arrow-left me-2"></i>이전 페이지
            </a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
