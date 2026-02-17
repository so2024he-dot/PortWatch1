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
        body { background: #0f1117; color: #e0e0e0; font-family: 'Segoe UI', sans-serif; }
        .card { background: #1a1d27; border: 1px solid #2d3250; border-radius: 12px; }
        .card-header { background: #1e2235; border-bottom: 1px solid #2d3250; border-radius: 12px 12px 0 0 !important; }
        .btn-kr  { background: #e8343f; border: none; color: #fff; }
        .btn-kr:hover  { background: #c82030; color: #fff; }
        .btn-us  { background: #1a56db; border: none; color: #fff; }
        .btn-us:hover  { background: #1343b0; color: #fff; }
        .btn-all { background: linear-gradient(135deg, #e8343f 0%, #1a56db 100%); border: none; color: #fff; }
        .btn-all:hover { opacity: 0.9; color: #fff; }
        .stat-box { background: #12151f; border: 1px solid #2d3250; border-radius: 10px; padding: 18px; text-align: center; }
        .stat-number { font-size: 2rem; font-weight: 700; }
        .stat-label  { font-size: 0.85rem; color: #6b7280; margin-top: 4px; }
        .log-area { background: #0a0c12; border: 1px solid #1f2337; border-radius: 8px;
                    height: 280px; overflow-y: auto; font-family: 'Courier New', monospace;
                    font-size: 0.82rem; padding: 12px; color: #a8b4c8; }
        .log-ok   { color: #22c55e; }
        .log-err  { color: #ef4444; }
        .log-info { color: #60a5fa; }
        .log-warn { color: #f59e0b; }
        .progress { height: 8px; border-radius: 4px; background: #1f2337; }
        .progress-bar-kr { background: #e8343f; }
        .progress-bar-us { background: #1a56db; }
        .badge-kr { background: #e8343f; }
        .badge-us { background: #1a56db; }
        .spinner-pulse { animation: pulse 1.5s ease-in-out infinite; }
        @keyframes pulse { 0%,100%{opacity:1} 50%{opacity:0.4} }
        .flag { font-size: 1.3rem; }
    </style>
</head>
<body>

<nav class="navbar navbar-dark px-4 py-3" style="background:#13162080; border-bottom:1px solid #2d3250;">
    <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">
        <i class="fas fa-chart-line text-primary me-2"></i>PortWatch
    </a>
    <div>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-secondary btn-sm me-2">
            <i class="fas fa-tachometer-alt me-1"></i>대시보드
        </a>
        <a href="${pageContext.request.contextPath}/stock" class="btn btn-outline-secondary btn-sm">
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
            <small class="text-muted">한국 · 미국 주식 크롤링 → AWS MySQL 저장</small>
        </div>
        <div class="ms-auto">
            <span id="statusBadge" class="badge bg-secondary fs-6 px-3 py-2">
                <i class="fas fa-circle me-1"></i>대기중
            </span>
        </div>
    </div>

    <div class="row g-4">

        <!-- 좌측: 컨트롤 패널 -->
        <div class="col-lg-4">

            <!-- 크롤링 버튼 -->
            <div class="card mb-4">
                <div class="card-header py-3">
                    <i class="fas fa-play-circle me-2 text-primary"></i>크롤링 실행
                </div>
                <div class="card-body p-4">

                    <!-- 한국 주식 -->
                    <div class="mb-3">
                        <div class="d-flex align-items-center mb-2">
                            <span class="flag me-2">🇰🇷</span>
                            <strong>한국 주식 (KOSPI / KOSDAQ)</strong>
                        </div>
                        <div class="text-muted small mb-3">
                            네이버 금융 시가총액 TOP 100 수집
                        </div>
                        <button id="btnKorea" class="btn btn-kr w-100 py-2" onclick="crawl('korea')">
                            <i class="fas fa-database me-2"></i>한국 주식 업데이트
                        </button>
                    </div>

                    <hr class="border-secondary">

                    <!-- 미국 주식 -->
                    <div class="mb-3">
                        <div class="d-flex align-items-center mb-2">
                            <span class="flag me-2">🇺🇸</span>
                            <strong>미국 주식 (NYSE / NASDAQ)</strong>
                        </div>
                        <div class="text-muted small mb-3">
                            Yahoo Finance S&P 500 TOP 100 수집
                        </div>
                        <button id="btnUs" class="btn btn-us w-100 py-2" onclick="crawl('us')">
                            <i class="fas fa-database me-2"></i>미국 주식 업데이트
                        </button>
                    </div>

                    <hr class="border-secondary">

                    <!-- 전체 크롤링 -->
                    <button id="btnAll" class="btn btn-all w-100 py-2" onclick="crawl('all')">
                        <i class="fas fa-globe me-2"></i>전체 업데이트 (한국 + 미국)
                    </button>
                </div>
            </div>

            <!-- 자동 업데이트 정보 -->
            <div class="card">
                <div class="card-header py-3">
                    <i class="fas fa-clock me-2 text-warning"></i>자동 업데이트 스케줄
                </div>
                <div class="card-body">
                    <table class="table table-dark table-sm mb-0">
                        <tbody>
                            <tr>
                                <td><i class="fas fa-sun text-warning me-1"></i>오전 09:00</td>
                                <td><span class="badge badge-kr">한국</span> 시장 시작</td>
                            </tr>
                            <tr>
                                <td><i class="fas fa-moon text-info me-1"></i>오후 06:00</td>
                                <td><span class="badge badge-kr">한국</span> 시장 종료</td>
                            </tr>
                            <tr>
                                <td><i class="fas fa-star text-primary me-1"></i>오후 11:30</td>
                                <td><span class="badge badge-us">미국</span> 시장 시작</td>
                            </tr>
                            <tr>
                                <td><i class="fas fa-star-half text-secondary me-1"></i>오전 06:00</td>
                                <td><span class="badge badge-us">미국</span> 시장 종료</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>

        <!-- 우측: 상태 & 결과 -->
        <div class="col-lg-8">

            <!-- 통계 카드 -->
            <div class="row g-3 mb-4">
                <div class="col-4">
                    <div class="stat-box">
                        <div class="stat-number text-danger" id="koreaCount">0</div>
                        <div class="stat-label">🇰🇷 한국 주식</div>
                        <div class="progress mt-2">
                            <div id="koreaBar" class="progress-bar progress-bar-kr" style="width:0%"></div>
                        </div>
                    </div>
                </div>
                <div class="col-4">
                    <div class="stat-box">
                        <div class="stat-number text-primary" id="usCount">0</div>
                        <div class="stat-label">🇺🇸 미국 주식</div>
                        <div class="progress mt-2">
                            <div id="usBar" class="progress-bar progress-bar-us" style="width:0%"></div>
                        </div>
                    </div>
                </div>
                <div class="col-4">
                    <div class="stat-box">
                        <div class="stat-number text-success" id="totalCount">0</div>
                        <div class="stat-label">📊 전체 종목</div>
                        <div class="progress mt-2">
                            <div id="totalBar" class="progress-bar bg-success" style="width:0%"></div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 실행 로그 -->
            <div class="card mb-4">
                <div class="card-header py-3 d-flex justify-content-between align-items-center">
                    <span><i class="fas fa-terminal me-2 text-success"></i>실행 로그</span>
                    <button class="btn btn-outline-secondary btn-sm" onclick="clearLog()">
                        <i class="fas fa-trash me-1"></i>지우기
                    </button>
                </div>
                <div class="card-body p-0">
                    <div class="log-area" id="logArea">
                        <div class="log-info">[시스템] 크롤링 준비 완료. 버튼을 눌러 업데이트하세요.</div>
                    </div>
                </div>
            </div>

            <!-- DB 저장 안내 -->
            <div class="card">
                <div class="card-header py-3">
                    <i class="fas fa-database me-2 text-info"></i>AWS MySQL 저장 정보
                </div>
                <div class="card-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <div class="p-3" style="background:#12151f; border-radius:8px; border:1px solid #2d3250;">
                                <div class="text-muted small mb-1">📋 STOCK 테이블 구조</div>
                                <code style="color:#a8d8ff; font-size:0.8rem;">
                                    stock_id (PK)<br>
                                    stock_code, stock_name<br>
                                    market, country<br>
                                    current_price (DECIMAL)<br>
                                    previous_close (DECIMAL)<br>
                                    change_amount, change_rate<br>
                                    volume, created_at, updated_at
                                </code>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="p-3" style="background:#12151f; border-radius:8px; border:1px solid #2d3250;">
                                <div class="text-muted small mb-1">🔑 데이터 형식</div>
                                <code style="color:#a8d8ff; font-size:0.8rem;">
                                    🇰🇷 stock_id = "KR_005930"<br>
                                    🇺🇸 stock_id = "US_AAPL"<br><br>
                                    🇰🇷 market = "KOSPI"/"KOSDAQ"<br>
                                    🇺🇸 market = "NYSE"/"NASDAQ"<br><br>
                                    가격 = BigDecimal (정밀도 15,2)
                                </code>
                            </div>
                        </div>
                    </div>

                    <!-- MySQL 확인 쿼리 -->
                    <div class="mt-3 p-3" style="background:#0a0c12; border-radius:8px; border:1px solid #1f2337;">
                        <div class="text-muted small mb-2">🔍 Putty MySQL 확인 쿼리</div>
                        <code style="color:#22c55e; font-size:0.82rem; white-space:pre-line;">
USE portwatch_db;

-- 국가별 개수 확인
SELECT country, market, COUNT(*) AS cnt
FROM STOCK GROUP BY country, market;

-- 한국 TOP 5 (현재가 기준)
SELECT stock_code, stock_name, current_price, change_rate
FROM STOCK WHERE country='KR'
ORDER BY current_price DESC LIMIT 5;

-- 미국 TOP 5 (현재가 기준)
SELECT stock_code, stock_name,
       CONCAT('$', FORMAT(current_price,2)) AS price
FROM STOCK WHERE country='US'
ORDER BY current_price DESC LIMIT 5;
                        </code>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>

<!-- jQuery + Bootstrap JS -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>

<script>
const CTX = '${pageContext.request.contextPath}';

/* ─────────────────────────────────────────────
 * 크롤링 실행 (AJAX)
 * ───────────────────────────────────────────── */
function crawl(type) {
    const urlMap = {
        korea: CTX + '/crawler/korea',
        us:    CTX + '/crawler/us',
        all:   CTX + '/crawler/all'
    };
    const labelMap = {
        korea: '🇰🇷 한국 주식',
        us:    '🇺🇸 미국 주식',
        all:   '🌐 전체'
    };

    setRunning(true);
    addLog('info', `[시작] ${labelMap[type]} 크롤링 요청...`);

    $.ajax({
        url:    urlMap[type],
        method: 'POST',
        success: function(res) {
            if (res.success) {
                addLog('ok', `[완료] ${res.message}`);
                if (res.koreaCount !== undefined) updateStat('korea', res.koreaCount);
                if (res.usCount    !== undefined) updateStat('us',    res.usCount);
                if (res.count      !== undefined) {
                    if (type === 'korea') updateStat('korea', res.count);
                    if (type === 'us')    updateStat('us',    res.count);
                }
                updateTotal();
                showStatus('완료', 'success');
            } else {
                addLog('err', `[오류] ${res.message}`);
                showStatus('오류', 'danger');
            }
        },
        error: function(xhr, status, err) {
            addLog('err', `[네트워크 오류] ${err}`);
            showStatus('오류', 'danger');
        },
        complete: function() {
            setRunning(false);
        }
    });
}

/* ─────────────────────────────────────────────
 * UI 헬퍼 함수
 * ───────────────────────────────────────────── */
function setRunning(running) {
    ['btnKorea','btnUs','btnAll'].forEach(id => {
        const btn = document.getElementById(id);
        btn.disabled = running;
    });
    if (running) {
        showStatus('진행중', 'warning');
        addLog('warn', '[진행] 크롤링 실행 중... (수십 초 소요됩니다)');
    }
}

function addLog(type, msg) {
    const log = document.getElementById('logArea');
    const now = new Date().toLocaleTimeString('ko-KR');
    const cls = { ok:'log-ok', err:'log-err', info:'log-info', warn:'log-warn' }[type] || '';
    log.innerHTML += `<div class="${cls}">[${now}] ${msg}</div>`;
    log.scrollTop = log.scrollHeight;
}

function clearLog() {
    document.getElementById('logArea').innerHTML =
        '<div class="log-info">[시스템] 로그 초기화됨.</div>';
}

function showStatus(label, color) {
    const badge = document.getElementById('statusBadge');
    badge.className = `badge bg-${color} fs-6 px-3 py-2`;
    badge.innerHTML = `<i class="fas fa-circle me-1"></i>${label}`;
}

function updateStat(type, count) {
    const max = 100;
    const pct = Math.min((count / max) * 100, 100);
    if (type === 'korea') {
        document.getElementById('koreaCount').textContent = count;
        document.getElementById('koreaBar').style.width = pct + '%';
    } else {
        document.getElementById('usCount').textContent = count;
        document.getElementById('usBar').style.width = pct + '%';
    }
}

function updateTotal() {
    const kr  = parseInt(document.getElementById('koreaCount').textContent) || 0;
    const us  = parseInt(document.getElementById('usCount').textContent)    || 0;
    const tot = kr + us;
    document.getElementById('totalCount').textContent = tot;
    document.getElementById('totalBar').style.width = Math.min((tot / 200) * 100, 100) + '%';
}

/* ─────────────────────────────────────────────
 * 페이지 로드 시 상태 조회
 * ───────────────────────────────────────────── */
$(function() {
    $.getJSON(CTX + '/crawler/status', function(res) {
        updateStat('korea', res.koreaCount || 0);
        updateStat('us',    res.usCount    || 0);
        updateTotal();
    });
});
</script>

</body>
</html>
