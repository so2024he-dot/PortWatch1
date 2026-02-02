<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원가입 - PortWatch</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        .container {
            background: white;
            padding: 40px;
            border-radius: 15px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            max-width: 500px;
            width: 100%;
        }
        h1 {
            color: #667eea;
            text-align: center;
            margin-bottom: 30px;
            font-size: 32px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            color: #333;
            font-weight: 600;
            margin-bottom: 8px;
        }
        input {
            width: 100%;
            padding: 12px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        input:focus {
            outline: none;
            border-color: #667eea;
        }
        .btn {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 18px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s;
        }
        .btn:hover {
            transform: translateY(-2px);
        }
        .links {
            text-align: center;
            margin-top: 20px;
        }
        .links a {
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🎯 PortWatch 회원가입</h1>
        <form action="${pageContext.request.contextPath}/member/signup" method="post">
            <div class="form-group">
                <label>이메일 *</label>
                <input type="email" name="memberEmail" required placeholder="example@portwatch.com">
            </div>
            <div class="form-group">
                <label>비밀번호 *</label>
                <input type="password" name="memberPass" required placeholder="8자 이상">
            </div>
            <div class="form-group">
                <label>이름 *</label>
                <input type="text" name="memberName" required placeholder="홍길동">
            </div>
            <div class="form-group">
                <label>전화번호</label>
                <input type="tel" name="memberPhone" placeholder="010-1234-5678">
            </div>
            <button type="submit" class="btn">가입하기</button>
        </form>
        <div class="links">
            <a href="${pageContext.request.contextPath}/member/login">이미 계정이 있으신가요? 로그인</a>
        </div>
    </div>
</body>
</html>
