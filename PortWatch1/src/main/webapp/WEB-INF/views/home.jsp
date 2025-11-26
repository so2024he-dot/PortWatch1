<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .container {
            background: white;
            padding: 3rem;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            text-align: center;
            max-width: 600px;
        }
        h1 {
            color: #667eea;
            font-size: 2.5rem;
            margin-bottom: 1rem;
        }
        p {
            color: #666;
            font-size: 1.2rem;
            margin-bottom: 2rem;
        }
        .status {
            background: #e8f5e9;
            border-left: 4px solid #4caf50;
            padding: 1rem;
            margin-top: 2rem;
            border-radius: 5px;
        }
        .status h3 {
            color: #2e7d32;
            margin-bottom: 0.5rem;
        }
        .status p {
            color: #1b5e20;
            font-size: 1rem;
            margin: 0;
        }
        .emoji {
            font-size: 4rem;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="emoji">🎉</div>
        <h1>PortWatch</h1>
        <p>포트폴리오 관리 시스템에 오신 것을 환영합니다!</p>
        
        <div class="status">
            <h3>✅ 서버가 정상적으로 작동 중입니다!</h3>
            <p>이제 나머지 기능을 추가할 준비가 되었습니다.</p>
        </div>
    </div>
</body>
</html>
