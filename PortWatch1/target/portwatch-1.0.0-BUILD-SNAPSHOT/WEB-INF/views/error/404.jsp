<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>404 - 페이지 없음</title>
<style>
*{margin:0;padding:0;box-sizing:border-box}
body{font-family:'Malgun Gothic',sans-serif;background:linear-gradient(135deg,#667eea,#764ba2);min-height:100vh;display:flex;align-items:center;justify-content:center}
.box{background:#fff;border-radius:16px;padding:60px 50px;text-align:center;box-shadow:0 20px 60px rgba(0,0,0,.3);max-width:480px}
h1{font-size:80px;color:#667eea;font-weight:800;line-height:1}
h2{font-size:22px;color:#333;margin:16px 0 10px}
p{color:#888;font-size:15px}
.btn{display:inline-block;margin-top:28px;padding:13px 32px;background:#667eea;color:#fff;border-radius:8px;font-size:15px;font-weight:600;text-decoration:none}
.btn:hover{background:#764ba2}
</style>
</head>
<body>
<div class="box">
  <h1>404</h1>
  <h2>페이지를 찾을 수 없습니다</h2>
  <p>요청하신 페이지가 존재하지 않습니다.</p>
  <a href="/member/login" class="btn">홈으로</a>
</div>
</body>
</html>
