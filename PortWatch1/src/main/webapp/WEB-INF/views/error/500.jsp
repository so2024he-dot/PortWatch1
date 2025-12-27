<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - Internal Server Error | PortWatch</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 20px;
        }
        
        .error-container {
            background: white;
            border-radius: 20px;
            padding: 60px 40px;
            text-align: center;
            max-width: 600px;
            width: 100%;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
        }
        
        .error-code {
            font-size: 120px;
            font-weight: bold;
            color: #667eea;
            margin-bottom: 20px;
            line-height: 1;
        }
        
        .error-title {
            font-size: 32px;
            font-weight: bold;
            color: #333;
            margin-bottom: 20px;
        }
        
        .error-message {
            font-size: 18px;
            color: #666;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        
        .error-details {
            background: #f5f5f5;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 30px;
            text-align: left;
            max-height: 200px;
            overflow-y: auto;
        }
        
        .error-details pre {
            font-size: 12px;
            color: #d32f2f;
            white-space: pre-wrap;
            word-break: break-word;
        }
        
        .button-group {
            display: flex;
            gap: 15px;
            justify-content: center;
        }
        
        .btn {
            padding: 15px 30px;
            font-size: 16px;
            font-weight: bold;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-primary {
            background: #667eea;
            color: white;
        }
        
        .btn-primary:hover {
            background: #5568d3;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: #f5f5f5;
            color: #333;
        }
        
        .btn-secondary:hover {
            background: #e0e0e0;
            transform: translateY(-2px);
        }
        
        .icon {
            font-size: 100px;
            margin-bottom: 20px;
        }
        
        @media (max-width: 600px) {
            .error-container {
                padding: 40px 20px;
            }
            
            .error-code {
                font-size: 80px;
            }
            
            .error-title {
                font-size: 24px;
            }
            
            .error-message {
                font-size: 16px;
            }
            
            .button-group {
                flex-direction: column;
            }
            
            .btn {
                width: 100%;
            }
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="icon">🔧</div>
        <div class="error-code">500</div>
        <div class="error-title">Internal Server Error</div>
        <div class="error-message">
            서버 내부 오류가 발생했습니다.<br>
            잠시 후 다시 시도해 주세요.
        </div>
        
        <% if (exception != null) { %>
        <div class="error-details">
            <pre><%= exception.getMessage() %></pre>
        </div>
        <% } %>
        
        <div class="button-group">
            <a href="<%= request.getContextPath() %>/" class="btn btn-primary">
                🏠 홈으로
            </a>
            <button onclick="history.back()" class="btn btn-secondary">
                ← 뒤로가기
            </button>
        </div>
    </div>
    
    <script>
        // 3초 후 자동으로 에러 상세 숨기기 (선택사항)
        setTimeout(function() {
            var errorDetails = document.querySelector('.error-details');
            if (errorDetails) {
                // errorDetails.style.display = 'none';
            }
        }, 10000);
    </script>
</body>
</html>
