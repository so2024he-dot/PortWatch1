<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>500 - 서버 오류</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', 'Malgun Gothic', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .container {
            background: white;
            padding: 60px;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            text-align: center;
            max-width: 600px;
        }
        h1 { font-size: 4em; color: #e74c3c; margin-bottom: 20px; }
        h2 { font-size: 1.5em; color: #333; margin-bottom: 20px; }
        p { color: #666; margin-bottom: 30px; }
        .btn {
            padding: 15px 30px;
            background: #667eea;
            color: white;
            border: none;
            border-radius: 50px;
            text-decoration: none;
            display: inline-block;
            margin: 0 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>500</h1>
        <h2>서버 내부 오류</h2>
        <p>죄송합니다. 서버에서 오류가 발생했습니다.</p>
        <a href="${pageContext.request.contextPath}/" class="btn">홈으로</a>
    </div>
</body>
</html>
