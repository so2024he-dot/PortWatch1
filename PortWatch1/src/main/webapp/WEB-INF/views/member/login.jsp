<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>PortWatch - 로그인</title>
<style>
*{margin:0;padding:0;box-sizing:border-box}
body{font-family:'Segoe UI',sans-serif;background:linear-gradient(135deg,#1a237e,#0d47a1);min-height:100vh;display:flex;align-items:center;justify-content:center}
.box{background:#fff;border-radius:14px;padding:50px 42px;width:420px;box-shadow:0 24px 64px rgba(0,0,0,.3)}
.logo{text-align:center;margin-bottom:34px}
.logo h1{font-size:28px;color:#1a237e;font-weight:700}
.logo p{color:#888;font-size:13px;margin-top:5px}
label{display:block;font-size:13px;font-weight:600;color:#333;margin-bottom:6px}
input{width:100%;padding:13px 15px;border:1.5px solid #e0e0e0;border-radius:9px;font-size:14px;outline:none;margin-bottom:18px;transition:border-color .2s}
input:focus{border-color:#1a237e}
.btn{width:100%;padding:14px;background:#1a237e;color:#fff;border:none;border-radius:9px;font-size:15px;font-weight:600;cursor:pointer;transition:background .2s}
.btn:hover{background:#283593}
.err{background:#fdecea;color:#c62828;border-radius:8px;padding:11px 15px;font-size:13px;margin-bottom:18px;display:none}
.links{text-align:center;margin-top:20px;font-size:13px;color:#888}
.links a{color:#1a237e;font-weight:600;text-decoration:none}
.links a:hover{text-decoration:underline}
</style>
</head>
<body>
<div class="box">
  <div class="logo">
    <h1>📈 PortWatch</h1>
    <p>주식 포트폴리오 관리 시스템</p>
  </div>
  <div class="err" id="err"></div>
  <label>이메일</label>
  <input type="email" id="memberEmail" placeholder="이메일 주소 입력"/>
  <label>비밀번호</label>
  <input type="password" id="memberPass" placeholder="비밀번호 입력"
         onkeypress="if(event.key==='Enter')doLogin()"/>
  <button class="btn" onclick="doLogin()">로그인</button>
  <div class="links">계정이 없으신가요? <a href="/member/signup">회원가입</a></div>
</div>
<script>
function doLogin(){
  var email = document.getElementById('memberEmail').value.trim();
  var pass  = document.getElementById('memberPass').value;
  if(!email||!pass){showErr('이메일과 비밀번호를 입력하세요.');return;}
  fetch('/member/login',{
    method:'POST',
    headers:{'Content-Type':'application/x-www-form-urlencoded'},
    body:'memberEmail='+encodeURIComponent(email)+'&memberPass='+encodeURIComponent(pass)
  })
  .then(r=>r.json())
  .then(d=>{
    if(d.success) location.href=d.redirectUrl||'/dashboard';
    else showErr(d.message);
  })
  .catch(()=>showErr('서버 연결 오류가 발생했습니다.'));
}
function showErr(m){var e=document.getElementById('err');e.textContent=m;e.style.display='block';}
</script>
</body>
</html>
