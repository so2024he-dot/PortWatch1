<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>포트폴리오 생성 - PortWatch</title>
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
        
        nav {
            background: rgba(255, 255, 255, 0.95);
            padding: 1rem 2rem;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
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
        }
        
        .container {
            max-width: 600px;
            margin: 3rem auto;
            padding: 0 2rem;
        }
        
        .form-card {
            background: white;
            padding: 3rem;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
        }
        
        .form-header {
            text-align: center;
            margin-bottom: 2rem;
        }
        
        .form-header h1 {
            color: #667eea;
            font-size: 2rem;
            margin-bottom: 0.5rem;
        }
        
        .form-header p {
            color: #666;
        }
        
        .form-group {
            margin-bottom: 1.5rem;
        }
        
        label {
            display: block;
            margin-bottom: 0.5rem;
            color: #333;
            font-weight: 600;
        }
        
        .required {
            color: #f44336;
        }
        
        input[type="text"],
        textarea {
            width: 100%;
            padding: 1rem;
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            font-size: 1rem;
            font-family: inherit;
            transition: border-color 0.3s;
        }
        
        input:focus,
        textarea:focus {
            outline: none;
            border-color: #667eea;
        }
        
        textarea {
            resize: vertical;
            min-height: 120px;
        }
        
        .error {
            color: #f44336;
            font-size: 0.9rem;
            margin-top: 0.25rem;
        }
        
        .form-actions {
            display: flex;
            gap: 1rem;
            margin-top: 2rem;
        }
        
        .btn {
            flex: 1;
            padding: 1rem 2rem;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            font-weight: 600;
            font-size: 1rem;
            transition: all 0.3s;
            text-align: center;
            text-decoration: none;
            display: inline-block;
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
        
        .help-text {
            color: #999;
            font-size: 0.9rem;
            margin-top: 0.25rem;
        }
    </style>
</head>
<body>
    <nav>
        <a href="${pageContext.request.contextPath}/" class="nav-brand">📈 PortWatch</a>
        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/">홈</a></li>
            <li><a href="${pageContext.request.contextPath}/portfolio/list">포트폴리오</a></li>
            <li><a href="${pageContext.request.contextPath}/stock/list">종목</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="form-card">
            <div class="form-header">
                <h1>📊 새 포트폴리오 생성</h1>
                <p>당신의 투자 포트폴리오를 만들어보세요</p>
            </div>
            
            <form:form action="${pageContext.request.contextPath}/portfolio/create" 
                       method="post" 
                       modelAttribute="portfolioVO">
                
                <!-- 포트폴리오 이름 -->
                <div class="form-group">
                    <label for="portfolioName">
                        포트폴리오 이름 <span class="required">*</span>
                    </label>
                    <form:input path="portfolioName" 
                               id="portfolioName" 
                               placeholder="예: 2025 장기투자 포트폴리오" 
                               required="true" 
                               maxlength="100" />
                    <form:errors path="portfolioName" cssClass="error" />
                    <div class="help-text">최대 100자까지 입력 가능합니다</div>
                </div>
                
                <!-- 포트폴리오 설명 -->
                <div class="form-group">
                    <label for="portfolioDesc">
                        포트폴리오 설명
                    </label>
                    <form:textarea path="portfolioDesc" 
                                  id="portfolioDesc" 
                                  placeholder="이 포트폴리오의 목적과 투자 전략을 설명해주세요" />
                    <form:errors path="portfolioDesc" cssClass="error" />
                    <div class="help-text">포트폴리오의 투자 목표나 전략을 기록해두세요 (선택사항)</div>
                </div>
                
                <!-- 버튼 -->
                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/portfolio/list" class="btn btn-secondary">
                        취소
                    </a>
                    <button type="submit" class="btn btn-primary">
                        포트폴리오 생성
                    </button>
                </div>
            </form:form>
        </div>
    </div>
    
    <script>
        // 폼 제출 시 확인
        document.querySelector('form').addEventListener('submit', function(e) {
            const name = document.getElementById('portfolioName').value.trim();
            if (!name) {
                e.preventDefault();
                alert('포트폴리오 이름을 입력해주세요.');
                document.getElementById('portfolioName').focus();
            }
        });
    </script>
</body>
</html>
