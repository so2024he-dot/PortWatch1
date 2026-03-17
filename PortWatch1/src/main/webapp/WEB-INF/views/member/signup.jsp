<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>PortWatch - 회원가입</title>
<style>
*{margin:0;padding:0;box-sizing:border-box}
body{font-family:'Segoe UI',sans-serif;background:linear-gradient(135deg,#1a237e,#0d47a1);min-height:100vh;display:flex;align-items:center;justify-content:center;padding:40px 0}
.box{background:#fff;border-radius:14px;padding:48px 42px;width:460px;box-shadow:0 24px 64px rgba(0,0,0,.3)}
.logo{text-align:center;margin-bottom:28px}
.logo h1{font-size:24px;color:#1a237e;font-weight:700}
.logo p{color:#888;font-size:13px;margin-top:4px}
label{display:block;font-size:13px;font-weight:600;color:#333;margin-top:14px;margin-bottom:5px}
.row{display:flex;gap:8px}
.row input{flex:1}
input{width:100%;padding:12px 14px;border:1.5px solid #e0e0e0;border-radius:9px;font-size:14px;outline:none;transition:border-color .2s}
input:focus{border-color:#1a237e}
.btn-chk{padding:12px 14px;background:#e8f0fe;color:#1a237e;border:1.5px solid #1a237e;border-radius:9px;font-size:13px;font-weight:600;cursor:pointer;white-space:nowrap}
.btn-chk:hover{background:#d0e4ff}
.hint{font-size:11px;margin-top:4px;min-height:16px}
.ok{color:#2e7d32}.err-h{color:#c62828}
.btn{width:100%;padding:14px;background:#1a237e;color:#fff;border:none;border-radius:9px;font-size:15px;font-weight:600;cursor:pointer;margin-top:22px}
.btn:hover{background:#283593}
.err{background:#fdecea;color:#c62828;border-radius:8px;padding:11px 15px;font-size:13px;margin-bottom:14px;display:none}
.links{text-align:center;margin-top:18px;font-size:13px;color:#888}
.links a{color:#1a237e;font-weight:600;text-decoration:none}
</style>
</head>
<body>
<div class="box">
  <div class="logo">
    <h1>📈 PortWatch 회원가입</h1>
    <p>주식 포트폴리오 관리 시스템</p>
  </div>
  <div class="err" id="err"></div>

  <label>이메일 *</label>
  <div class="row">
    <input type="email" id="memberEmail" placeholder="이메일 주소"/>
    <button class="btn-chk" onclick="checkEmail()">중복확인</button>
  </div>
  <div class="hint" id="emailHint"></div>

  <label>비밀번호 *</label>
  <input type="password" id="memberPass" placeholder="비밀번호 (8자 이상)"/>

  <label>비밀번호 확인 *</label>
  <input type="password" id="memberPassCfm" placeholder="비밀번호 재입력"
         oninput="checkPw()"/>
  <div class="hint" id="pwHint"></div>

  <label>이름 *</label>
  <input type="text" id="memberName" placeholder="이름을 입력하세요"/>

  <label>전화번호</label>
  <input type="tel" id="memberPhone" placeholder="010-0000-0000"/>

  <button class="btn" onclick="doSignup()">회원가입</button>
  <div class="links">이미 계정이 있으신가요? <a href="/member/login">로그인</a></div>
</div>

<script>
var emailOk = false;

function checkEmail(){
  var email = document.getElementById('memberEmail').value.trim();
  if(!email){setHint('emailHint','이메일을 입력하세요.',false);return;}
  fetch('/member/check-email?memberEmail='+encodeURIComponent(email))
  .then(r=>r.json()).then(d=>{
    emailOk = d.available;
    setHint('emailHint', d.message, d.available);
  });
}

function checkPw(){
  var p1 = document.getElementById('memberPass').value;
  var p2 = document.getElementById('memberPassCfm').value;
  if(!p2) return;
  setHint('pwHint', p1===p2?'비밀번호가 일치합니다.':'비밀번호가 일치하지 않습니다.', p1===p2);
}

function doSignup(){
  var email = document.getElementById('memberEmail').value.trim();
  var pass  = document.getElementById('memberPass').value;
  var pass2 = document.getElementById('memberPassCfm').value;
  var name  = document.getElementById('memberName').value.trim();
  var phone = document.getElementById('memberPhone').value.trim();

  if(!email||!pass||!name){showErr('필수 항목을 모두 입력하세요.');return;}
  if(pass!==pass2){showErr('비밀번호가 일치하지 않습니다.');return;}
  if(pass.length<8){showErr('비밀번호는 8자 이상이어야 합니다.');return;}

  fetch('/member/signup',{
    method:'POST',
    headers:{'Content-Type':'application/x-www-form-urlencoded'},
    body:'memberEmail='+encodeURIComponent(email)+
         '&memberPass=' +encodeURIComponent(pass) +
         '&memberName=' +encodeURIComponent(name) +
         '&memberPhone='+encodeURIComponent(phone)
  })
  .then(r=>r.json()).then(d=>{
    if(d.success){alert('회원가입이 완료되었습니다!');location.href='/member/login';}
    else showErr(d.message);
  })
  .catch(()=>showErr('서버 연결 오류가 발생했습니다.'));
}

function setHint(id,msg,ok){
  var el=document.getElementById(id);
  el.textContent=msg;
  el.className='hint '+(ok?'ok':'err-h');
}
function showErr(m){var e=document.getElementById('err');e.textContent=m;e.style.display='block';}
</script>
</body>
</html>
