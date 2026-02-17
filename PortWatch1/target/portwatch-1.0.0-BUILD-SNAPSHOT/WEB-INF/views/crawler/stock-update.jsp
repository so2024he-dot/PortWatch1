<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>PortWatch - 주식 데이터 업데이트</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<style>
body { background:#0f1117; color:#e0e0e0; font-family:'Segoe UI',sans-serif; }
.navbar-c { background:#13162080; border-bottom:1px solid #2d3250; backdrop-filter:blur(10px); }
.card { background:#1a1d27; border:1px solid #2d3250; border-radius:14px; }
.card-header { background:#1e2235; border-bottom:1px solid #2d3250; border-radius:14px 14px 0 0 !important; font-weight:600; }
.btn-kr { background:#e8343f; border:none; color:#fff; }
.btn-kr:hover { background:#c82030; color:#fff; transform:translateY(-1px); }
.btn-us { background:#1a56db; border:none; color:#fff; }
.btn-us:hover { background:#1343b0; color:#fff; transform:translateY(-1px); }
.btn-all { background:linear-gradient(135deg,#e8343f,#1a56db); border:none; color:#fff; }
.btn-all:hover { opacity:.9; color:#fff; }
.stat-box { background:#12151f; border:1px solid #2d3250; border-radius:12px; padding:20px; text-align:center; }
.stat-num { font-size:2.4rem; font-weight:800; line-height:1; }
.stat-lbl { font-size:.82rem; color:#6b7280; margin-top:5px; }
.log-box  { background:#0a0c12; border:1px solid #1f2337; border-radius:10px;
            height:300px; overflow-y:auto; font-family:'Courier New',monospace;
            font-size:.82rem; padding:14px; color:#a8b4c8; }
.log-ok { color:#22c55e; }
.log-err { color:#ef4444; }
.log-inf { color:#60a5fa; }
.log-wrn { color:#f59e0b; }
.progress { height:6px; border-radius:3px; background:#1f2337; }
.code-block { background:#0a0c12; border:1px solid #1f2337; border-radius:8px;
              padding:14px; font-family:'Courier New',monospace; font-size:.8rem; }
</style>
</head>
<body>

<!-- 네비게이션 -->
<nav class="navbar navbar-dark navbar-c px-4 py-3">
    <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">
        <i class="fas fa-chart-line text-primary me-2"></i>PortWatch
    </a>
    <div class="d-flex gap-2">
        <a href="${pageContext.request.contextPath}/dashboard"
           class="btn btn-outline-secondary btn-sm">
            <i class="fas fa-tachometer-alt me-1"></i>대시보드
        </a>
        <a href="${pageContext.request.contextPath}/stock"
           class="btn btn-outline-secondary btn-sm">
            <i class="fas fa-search me-1"></i>주식검색
        </a>
    </div>
</nav>

<div class="container-fluid py-4 px-4">

    <!-- 헤더 -->
    <div class="d-flex align-items-center mb-4">
        <i class="fas fa-sync-alt fa-2x text-primary me-3"></i>
        <div>
            <h4 class="mb-0 fw-bold">주식 데이터 업데이트</h4>
            <small class="text-muted">크롤링 → AWS MySQL (stock_id: KR_005930 / US_AAPL 형식)</small>
        </div>
        <div class="ms-auto">
            <span id="statusBadge" class="badge bg-secondary fs-6 px-3 py-2">
                <i class="fas fa-circle me-1"></i><span id="statusTxt">대기중</span>
            </span>
        </div>
    </div>

    <div class="row g-4">

        <!-- 좌측: 컨트롤 -->
        <div class="col-lg-4">

            <div class="card mb-4">
                <div class="card-header py-3">
                    <i class="fas fa-play-circle me-2 text-primary"></i>크롤링 실행
                </div>
                <div class="card-body p-4">

                    <!-- 한국 -->
                    <div class="mb-4">
                        <div class="d-flex align-items-center mb-1">
                            <span class="me-2" style="font-size:1.2rem">🇰🇷</span>
                            <strong>한국 주식 (KOSPI / KOSDAQ)</strong>
                        </div>
                        <p class="text-muted small mb-3">
                            네이버 금융 시가총액 TOP 100 수집<br>
                            <code style="font-size:.75rem">stock_id = "KR_005930"</code>
                        </p>
                        <button id="btnKr" class="btn btn-kr w-100 py-2 fw-semibold"
                                onclick="crawl('korea')">
                            <i class="fas fa-database me-2"></i>한국 주식 업데이트
                        </button>
                    </div>

                    <hr class="border-secondary">

                    <!-- 미국 -->
                    <div class="mb-4">
                        <div class="d-flex align-items-center mb-1">
                            <span class="me-2" style="font-size:1.2rem">🇺🇸</span>
                            <strong>미국 주식 (NYSE / NASDAQ)</strong>
                        </div>
                        <p class="text-muted small mb-3">
                            Yahoo Finance S&amp;P 500 TOP 100 수집<br>
                            <code style="font-size:.75rem">stock_id = "US_AAPL"</code>
                        </p>
                        <button id="btnUs" class="btn btn-us w-100 py-2 fw-semibold"
                                onclick="crawl('us')">
                            <i class="fas fa-database me-2"></i>미국 주식 업데이트
                        </button>
                    </div>

                    <hr class="border-secondary">

                    <button id="btnAll" class="btn btn-all w-100 py-2 fw-semibold"
                            onclick="crawl('all')">
                        <i class="fas fa-globe me-2"></i>전체 업데이트 (한국 + 미국)
                    </button>
                </div>
            </div>

            <!-- 스케줄 -->
            <div class="card">
                <div class="card-header py-3">
                    <i class="fas fa-clock me-2 text-warning"></i>자동 업데이트 스케줄
                </div>
                <div class="card-body p-3">
                    <table class="table table-dark table-sm mb-0" style="font-size:.85rem">
                        <tbody>
                        <tr><td>☀️ 09:00</td><td>🇰🇷 한국 시장 시작</td></tr>
                        <tr><td>🌆 18:00</td><td>🇰🇷 한국 시장 종료</td></tr>
                        <tr><td>🌙 23:30</td><td>🇺🇸 미국 시장 시작</td></tr>
                        <tr><td>🌅 06:00</td><td>🇺🇸 미국 시장 종료</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>

        <!-- 우측: 상태 & 결과 -->
        <div class="col-lg-8">

            <!-- 통계 -->
            <div class="row g-3 mb-4">
                <div class="col-4">
                    <div class="stat-box">
                        <div class="stat-num text-danger" id="krCnt">0</div>
                        <div class="stat-lbl">🇰🇷 한국 종목</div>
                        <div class="progress mt-2">
                            <div id="krBar" class="progress-bar bg-danger" style="width:0%"></div>
                        </div>
                    </div>
                </div>
                <div class="col-4">
                    <div class="stat-box">
                        <div class="stat-num text-primary" id="usCnt">0</div>
                        <div class="stat-lbl">🇺🇸 미국 종목</div>
                        <div class="progress mt-2">
                            <div id="usBar" class="progress-bar bg-primary" style="width:0%"></div>
                        </div>
                    </div>
                </div>
                <div class="col-4">
                    <div class="stat-box">
                        <div class="stat-num text-success" id="totCnt">0</div>
                        <div class="stat-lbl">📊 전체 종목</div>
                        <div class="progress mt-2">
                            <div id="totBar" class="progress-bar bg-success" style="width:0%"></div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 로그 -->
            <div class="card mb-4">
                <div class="card-header py-3 d-flex justify-content-between align-items-center">
                    <span><i class="fas fa-terminal me-2 text-success"></i>실행 로그</span>
                    <button class="btn btn-outline-secondary btn-sm" onclick="clearLog()">
                        <i class="fas fa-trash me-1"></i>지우기
                    </button>
                </div>
                <div class="card-body p-0">
                    <div class="log-box" id="logArea">
                        <div class="log-inf">[시스템] 크롤링 대기 중. 버튼을 클릭하여 시작하세요.</div>
                    </div>
                </div>
            </div>

            <!-- AWS MySQL 확인 -->
            <div class="card">
                <div class="card-header py-3">
                    <i class="fas fa-database me-2 text-info"></i>AWS MySQL (Putty) 확인 쿼리
                </div>
                <div class="card-body">
                    <div class="code-block" style="color:#22c55e">
<span style="color:#60a5fa">USE</span> portwatch_db;<br><br>
<span style="color:#6b7280">-- 국가/시장별 종목 수</span><br>
<span style="color:#60a5fa">SELECT</span> country, market, <span style="color:#60a5fa">COUNT</span>(*) AS cnt<br>
<span style="color:#60a5fa">FROM</span> STOCK <span style="color:#60a5fa">GROUP BY</span> country, market;<br><br>
<span style="color:#6b7280">-- 한국 주식 TOP 5</span><br>
<span style="color:#60a5fa">SELECT</span> stock_id, stock_name, current_price, change_rate<br>
<span style="color:#60a5fa">FROM</span> STOCK <span style="color:#60a5fa">WHERE</span> country=<span style="color:#ffd700">'KR'</span><br>
<span style="color:#60a5fa">ORDER BY</span> current_price <span style="color:#60a5fa">DESC LIMIT</span> 5;<br><br>
<span style="color:#6b7280">-- 미국 주식 TOP 5</span><br>
<span style="color:#60a5fa">SELECT</span> stock_id, stock_name,<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <span style="color:#60a5fa">CONCAT</span>(<span style="color:#ffd700">'$'</span>,FORMAT(current_price,2)) price<br>
<span style="color:#60a5fa">FROM</span> STOCK <span style="color:#60a5fa">WHERE</span> country=<span style="color:#ffd700">'US'</span><br>
<span style="color:#60a5fa">ORDER BY</span> current_price <span style="color:#60a5fa">DESC LIMIT</span> 5;
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
<script>
const CTX = '${pageContext.request.contextPath}';

function crawl(type) {
    const urls   = { korea:CTX+'/crawler/korea', us:CTX+'/crawler/us', all:CTX+'/crawler/all' };
    const labels = { korea:'🇰🇷 한국 주식', us:'🇺🇸 미국 주식', all:'🌐 전체' };

    setRunning(true);
    addLog('inf', `[시작] ${labels[type]} 크롤링 요청 (수 분 소요될 수 있습니다)...`);

    $.ajax({
        url: urls[type], method: 'POST',
        success(r) {
            if (r.success) {
                addLog('ok', `[완료] ${r.message}`);
                if (r.koreaCount !== undefined) setStat('kr', r.koreaCount);
                if (r.usCount    !== undefined) setStat('us', r.usCount);
                if (r.count !== undefined) {
                    if (type === 'korea') setStat('kr', r.count);
                    if (type === 'us')    setStat('us', r.count);
                }
                updateTotal(); setBadge('완료','success');
            } else {
                addLog('err', `[오류] ${r.message}`); setBadge('오류','danger');
            }
        },
        error(x,s,e) { addLog('err',`[네트워크 오류] ${e}`); setBadge('오류','danger'); },
        complete()   { setRunning(false); }
    });
}

function setRunning(on) {
    ['btnKr','btnUs','btnAll'].forEach(id => document.getElementById(id).disabled = on);
    if (on) setBadge('진행중','warning');
}
function addLog(type, msg) {
    const a = document.getElementById('logArea');
    const now = new Date().toLocaleTimeString('ko-KR');
    const cls = {ok:'log-ok',err:'log-err',inf:'log-inf',wrn:'log-wrn'}[type]||'';
    a.innerHTML += `<div class="${cls}">[${now}] ${msg}</div>`;
    a.scrollTop = a.scrollHeight;
}
function clearLog() {
    document.getElementById('logArea').innerHTML = '<div class="log-inf">[시스템] 초기화됨.</div>';
}
function setBadge(label, color) {
    document.getElementById('statusBadge').className = `badge bg-${color} fs-6 px-3 py-2`;
    document.getElementById('statusTxt').textContent = label;
}
function setStat(type, n) {
    const id = type === 'kr' ? 'krCnt' : 'usCnt';
    const bar = type === 'kr' ? 'krBar' : 'usBar';
    document.getElementById(id).textContent = n;
    document.getElementById(bar).style.width = Math.min(n/100*100,100)+'%';
}
function updateTotal() {
    const kr = parseInt(document.getElementById('krCnt').textContent)||0;
    const us = parseInt(document.getElementById('usCnt').textContent)||0;
    const t  = kr + us;
    document.getElementById('totCnt').textContent = t;
    document.getElementById('totBar').style.width = Math.min(t/200*100,100)+'%';
}

$(function() {
    $.getJSON(CTX+'/crawler/status', function(r) {
        setStat('kr', r.koreaCount||0);
        setStat('us', r.usCount||0);
        updateTotal();
    });
});
</script>
</body>
</html>
