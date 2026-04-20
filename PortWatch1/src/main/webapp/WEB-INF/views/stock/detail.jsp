<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${not empty stock.stockName ? stock.stockName : stockCode}"/> - PortWatch</title>

    <!-- Bootstrap 5.3 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        body { background-color: #f0f2f5; font-family: 'Segoe UI', sans-serif; }

        /* ── 헤더 배너 ─────────────────────────────── */
        .stock-banner {
            background: linear-gradient(135deg, #1e3a5f 0%, #2d6a9f 100%);
            color: white;
            padding: 32px 0 24px;
            margin-bottom: 28px;
        }
        .banner-name  { font-size: 2rem; font-weight: 700; }
        .banner-code  { font-size: 1rem; opacity: .85; margin-top: 4px; }
        .banner-badge { font-size: .8rem; padding: 4px 12px; border-radius: 20px; }

        /* ── 가격 카드 ──────────────────────────────── */
        .price-card {
            background: white;
            border-radius: 14px;
            padding: 28px;
            box-shadow: 0 2px 12px rgba(0,0,0,.08);
            margin-bottom: 22px;
        }
        .current-price { font-size: 2.8rem; font-weight: 800; color: #212529; }
        .price-change  { font-size: 1.3rem; font-weight: 600; margin-top: 6px; }
        .price-up      { color: #dc3545; }
        .price-down    { color: #0d6efd; }

        /* ── 정보 그리드 ─────────────────────────────── */
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
            gap: 14px;
            margin-top: 20px;
        }
        .info-box {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 14px 16px;
        }
        .info-label { font-size: .78rem; color: #6c757d; margin-bottom: 4px; }
        .info-value { font-size: 1.05rem; font-weight: 600; color: #212529; }

        /* ── 회사 정보 카드 ─────────────────────────── */
        .company-card {
            background: white;
            border-radius: 14px;
            padding: 24px 28px;
            box-shadow: 0 2px 12px rgba(0,0,0,.08);
            margin-bottom: 22px;
        }
        .company-card h5 {
            font-weight: 700; color: #1e3a5f;
            border-bottom: 2px solid #e9ecef;
            padding-bottom: 10px; margin-bottom: 16px;
        }
        .company-row { display: flex; flex-wrap: wrap; gap: 20px 40px; }
        .company-item label { font-size:.78rem; color:#6c757d; display:block; margin-bottom:2px; }
        .company-item span  { font-size:.98rem; font-weight:600; color:#212529; }

        /* ── 차트 카드 ──────────────────────────────── */
        .chart-card {
            background: white;
            border-radius: 14px;
            padding: 24px 28px;
            box-shadow: 0 2px 12px rgba(0,0,0,.08);
            margin-bottom: 22px;
        }
        .chart-card h5 {
            font-weight:700; color:#1e3a5f;
            border-bottom: 2px solid #e9ecef;
            padding-bottom: 10px; margin-bottom: 16px;
        }
        .period-btns { display:flex; gap:8px; margin-bottom:16px; flex-wrap:wrap; }
        .period-btn  {
            padding: 6px 18px; border: 2px solid #dee2e6;
            background: white; border-radius: 20px; cursor:pointer;
            font-size:.85rem; transition: all .2s;
        }
        .period-btn:hover  { border-color:#1e3a5f; background:#f0f4ff; }
        .period-btn.active { background:#1e3a5f; color:white; border-color:#1e3a5f; }
        /* ── 이중 차트: 거시적(좌, 전체추이) + 미시적(우, 기간별) ────
           ★ 높이 수정: .macro-wrap / .micro-wrap height 값만 변경
              거시 기본 190px | 미시 기본 210px
           ──────────────────────────────────────────────────────── */
        .chart-dual-wrap {
            display: grid;
            grid-template-columns: 38fr 62fr;  /* 거시 38% : 미시 62% */
            gap: 14px;
            align-items: start;
        }
        .chart-sub-label {
            font-size: .72rem; font-weight: 700; color: #6c757d;
            text-transform: uppercase; letter-spacing: .05em;
            margin-bottom: 6px;
        }
        .macro-wrap { position: relative; height: 190px; } /* ★ 거시 높이 */
        .micro-wrap  { position: relative; height: 210px; } /* ★ 미시 높이 */

        @media (max-width: 767.98px) {
            .chart-dual-wrap { grid-template-columns: 1fr; }
            .macro-wrap { height: 155px; }
            .micro-wrap  { height: 175px; }
        }
        @media (max-width: 575.98px) {
            .macro-wrap { height: 130px; }
            .micro-wrap  { height: 155px; }
        }

        /* 모바일: 카드 여백 축소 → 한 화면에 차트+정보 모두 표시 */
        @media (max-width: 575.98px) {
            .chart-card, .price-card, .company-card, .news-card {
                padding: 14px 12px;
                margin-bottom: 12px;
            }
            .current-price { font-size: 2rem; }
            .price-change  { font-size: 1.05rem; }
            .period-btn    { padding: 5px 11px; font-size: .78rem; }
            .period-btns   { gap: 5px; margin-bottom: 10px; }
            .info-grid     { grid-template-columns: repeat(auto-fill, minmax(130px, 1fr)); gap: 8px; margin-top: 14px; }
            .buy-panel     { position: static; margin-top: 14px; }
            .stock-banner  { padding: 20px 0 16px; margin-bottom: 16px; }
            .banner-name   { font-size: 1.4rem; }
        }

        /* ── 뉴스 카드 ──────────────────────────────── */
        .news-card {
            background: white;
            border-radius: 14px;
            padding: 24px 28px;
            box-shadow: 0 2px 12px rgba(0,0,0,.08);
            margin-bottom: 22px;
        }
        .news-card h5 {
            font-weight:700; color:#1e3a5f;
            border-bottom: 2px solid #e9ecef;
            padding-bottom:10px; margin-bottom:16px;
        }
        .news-item { padding: 12px 0; border-bottom: 1px solid #f0f2f5; }
        .news-item:last-child { border-bottom: none; }
        .news-item a { font-weight:600; color:#212529; text-decoration:none; }
        .news-item a:hover { color:#1e3a5f; }

        /* ── 매수 사이드 패널 ────────────────────────── */
        .buy-panel {
            background: white;
            border-radius: 14px;
            box-shadow: 0 2px 12px rgba(0,0,0,.08);
            overflow: hidden;
            position: sticky; top: 20px;
        }
        .buy-panel-header {
            background: linear-gradient(135deg, #28a745, #1e7e34);
            color: white; padding: 18px 22px;
        }
        .buy-panel-body { padding: 22px; }
        .btn-buy-action {
            background: linear-gradient(135deg, #28a745, #1e7e34);
            color: white; border: none; padding: 14px;
            border-radius: 10px; font-size: 1.05rem; font-weight: 700;
            width: 100%; transition: all .2s;
        }
        .btn-buy-action:hover { opacity:.9; transform: translateY(-1px); }
        .btn-watchlist {
            border: 2px solid #ffc107; background: transparent;
            color: #e67e00; border-radius: 10px; padding: 10px;
            font-weight: 600; width: 100%; transition: all .2s;
        }
        .btn-watchlist:hover { background:#fff3cd; }
        .amount-preview {
            background: #f8f9fa; border-radius: 10px;
            padding: 14px; text-align: center; margin: 14px 0;
        }
        .amount-preview .label { font-size:.8rem; color:#6c757d; }
        .amount-preview .value { font-size:1.4rem; font-weight:800; color:#212529; }
    </style>
</head>
<body>

<%-- ══ 공통 헤더 ══════════════════════════════════════════════ --%>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<%-- ══ 주식 데이터 변수 (null 방어) ══════════════════════════ --%>
<c:set var="sc"   value="${not empty stock.stockCode   ? stock.stockCode   : param.stockCode}"/>
<c:set var="sn"   value="${not empty stock.stockName   ? stock.stockName   : sc}"/>
<c:set var="ctry" value="${not empty stock.country     ? stock.country     : 'KR'}"/>
<c:set var="mkt"  value="${not empty stock.marketType  ? stock.marketType  : (ctry=='US' ? 'US' : 'KOSPI')}"/>
<c:set var="ind"  value="${not empty stock.industry    ? stock.industry    : '-'}"/>
<c:set var="cp"   value="${not empty stock.currentPrice ? stock.currentPrice : 0}"/>
<c:set var="pc"   value="${not empty stock.priceChange  ? stock.priceChange  : 0}"/>
<c:set var="cr"   value="${not empty stock.changeRate   ? stock.changeRate   : 0}"/>
<c:set var="vol"  value="${not empty stock.volume       ? stock.volume       : 0}"/>
<c:set var="mcap" value="${not empty stock.marketCap    ? stock.marketCap    : 0}"/>

<%-- ══ 배너 헤더 ══════════════════════════════════════════════ --%>
<div class="stock-banner">
    <div class="container">
        <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
            <div>
                <div class="banner-name">
                    <c:out value="${sn}"/>
                    <span class="badge banner-badge ms-2
                        ${ctry == 'US' ? 'bg-success' : (mkt == 'KOSPI' ? 'bg-primary' : 'bg-warning text-dark')}">
                        <c:out value="${mkt}"/>
                    </span>
                </div>
                <div class="banner-code">
                    <c:out value="${sc}"/> &nbsp;|&nbsp; <c:out value="${ind}"/>
                    &nbsp;|&nbsp; <c:out value="${ctry == 'US' ? '🇺🇸 미국' : '🇰🇷 한국'}"/>
                </div>
            </div>
            <div class="d-flex gap-2 align-items-center">
                <button class="btn btn-outline-light btn-sm"
                        onclick="addToWatchlist('<c:out value="${sc}"/>')">
                    <i class="fas fa-star"></i> 관심종목 추가
                </button>
                <a href="${pageContext.request.contextPath}/stock/list"
                   class="btn btn-outline-light btn-sm">
                    <i class="fas fa-list"></i> 목록
                </a>
            </div>
        </div>
    </div>
</div>

<%-- ══ 메인 컨텐츠 ════════════════════════════════════════════ --%>
<div class="container pb-5">
    <div class="row gx-4">

        <%-- ── 왼쪽: 정보 + 차트 + 뉴스 ─────────────────── --%>
        <div class="col-lg-8">

            <%-- 현재가 카드 --%>
            <div class="price-card">
                <div class="current-price">
                    <c:choose>
                        <c:when test="${ctry == 'US'}">
                            $<fmt:formatNumber value="${cp}" pattern="#,##0.00"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:formatNumber value="${cp}" pattern="#,##0"/>원
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="price-change ${cr >= 0 ? 'price-up' : 'price-down'}">
                    ${cr >= 0 ? '▲' : '▼'}
                    <c:choose>
                        <c:when test="${ctry == 'US'}">
                            $<fmt:formatNumber value="${pc}" pattern="#,##0.00"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:formatNumber value="${pc}" pattern="#,##0"/>원
                        </c:otherwise>
                    </c:choose>
                    &nbsp;(<fmt:formatNumber value="${cr}" pattern="+#,##0.00;-#,##0.00"/>%)
                </div>

                <div class="info-grid">
                    <div class="info-box">
                        <div class="info-label">전일대비</div>
                        <div class="info-value ${cr >= 0 ? 'price-up' : 'price-down'}">
                            <c:choose>
                                <c:when test="${ctry == 'US'}">$<fmt:formatNumber value="${pc}" pattern="#,##0.00"/></c:when>
                                <c:otherwise><fmt:formatNumber value="${pc}" pattern="#,##0"/>원</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="info-box">
                        <div class="info-label">등락률</div>
                        <div class="info-value ${cr >= 0 ? 'price-up' : 'price-down'}">
                            <fmt:formatNumber value="${cr}" pattern="+#,##0.00;-#,##0.00"/>%
                        </div>
                    </div>
                    <div class="info-box">
                        <div class="info-label">거래량</div>
                        <div class="info-value"><fmt:formatNumber value="${vol}" pattern="#,##0"/></div>
                    </div>
                    <div class="info-box">
                        <div class="info-label">시가총액</div>
                        <div class="info-value">
                            <c:choose>
                                <c:when test="${mcap > 0}">
                                    <c:choose>
                                        <c:when test="${ctry == 'US'}">$<fmt:formatNumber value="${mcap}" pattern="#,##0"/></c:when>
                                        <c:otherwise><fmt:formatNumber value="${mcap}" pattern="#,##0"/>억</c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="info-box">
                        <div class="info-label">업종</div>
                        <div class="info-value"><c:out value="${ind}"/></div>
                    </div>
                    <div class="info-box">
                        <div class="info-label">시장</div>
                        <div class="info-value"><c:out value="${mkt}"/></div>
                    </div>
                </div>
            </div>

            <%-- ── 회사 상세 정보 카드 ─────────────────────── --%>
            <div class="company-card">
                <h5><i class="fas fa-building me-2"></i>회사 정보</h5>
                <div class="company-row">
                    <div class="company-item">
                        <label>종목코드</label>
                        <span><c:out value="${sc}"/></span>
                    </div>
                    <div class="company-item">
                        <label>회사명</label>
                        <span><c:out value="${sn}"/></span>
                    </div>
                    <div class="company-item">
                        <label>국가</label>
                        <span><c:out value="${ctry == 'US' ? '미국 (United States)' : '대한민국'}"/></span>
                    </div>
                    <div class="company-item">
                        <label>상장 시장</label>
                        <span><c:out value="${mkt}"/></span>
                    </div>
                    <div class="company-item">
                        <label>업종 / 섹터</label>
                        <span><c:out value="${ind}"/></span>
                    </div>
                    <div class="company-item">
                        <label>현재가</label>
                        <span class="${cr >= 0 ? 'price-up' : 'price-down'}">
                            <c:choose>
                                <c:when test="${ctry == 'US'}">
                                    $<fmt:formatNumber value="${cp}" pattern="#,##0.00"/>
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatNumber value="${cp}" pattern="#,##0"/>원
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="company-item">
                        <label>등락률</label>
                        <span class="${cr >= 0 ? 'price-up' : 'price-down'}">
                            <fmt:formatNumber value="${cr}" pattern="+#,##0.00;-#,##0.00"/>%
                        </span>
                    </div>
                    <c:if test="${mcap > 0}">
                    <div class="company-item">
                        <label>시가총액</label>
                        <span>
                            <c:choose>
                                <c:when test="${ctry == 'US'}">$<fmt:formatNumber value="${mcap}" pattern="#,##0"/></c:when>
                                <c:otherwise><fmt:formatNumber value="${mcap}" pattern="#,##0"/>억원</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    </c:if>
                    <c:if test="${vol > 0}">
                    <div class="company-item">
                        <label>거래량</label>
                        <span><fmt:formatNumber value="${vol}" pattern="#,##0"/></span>
                    </div>
                    </c:if>
                </div>
            </div>

            <%-- ── 주가 차트: 거시(전체) + 미시(기간별) ─────── --%>
            <div class="chart-card">
                <h5><i class="fas fa-chart-line me-2"></i>주가 차트 (2025년 ~ 현재)</h5>
                <div class="chart-dual-wrap">

                    <%-- ─── 거시적 차트: 전체 기간, 최고/최저 범위 표시 ─── --%>
                    <div>
                        <div class="chart-sub-label">
                            <i class="fas fa-globe-asia me-1"></i>거시 추이 (전체)
                        </div>
                        <div class="macro-wrap">
                            <canvas id="macroChart"></canvas>
                        </div>
                        <div class="text-muted text-center mt-1" style="font-size:.67rem;">
                            전체 기간 최고 · 최저 범위 포함
                        </div>
                    </div>

                    <%-- ─── 미시적 차트: 기간 선택, 상세 분석 ───────────── --%>
                    <div>
                        <div class="chart-sub-label">
                            <i class="fas fa-search-plus me-1"></i>미시 분석 (기간 선택)
                        </div>
                        <div class="period-btns" id="periodBtns">
                            <button class="period-btn active" onclick="switchPeriod(this,'3M')">3개월</button>
                            <button class="period-btn"        onclick="switchPeriod(this,'6M')">6개월</button>
                            <button class="period-btn"        onclick="switchPeriod(this,'1Y')">1년</button>
                            <button class="period-btn"        onclick="switchPeriod(this,'ALL')">전체</button>
                        </div>
                        <div class="micro-wrap">
                            <canvas id="priceChart"></canvas>
                        </div>
                    </div>

                </div>
            </div>

            <%-- ── 관련 뉴스 ──────────────────────────────── --%>
            <div class="news-card">
                <h5><i class="fas fa-newspaper me-2"></i>관련 뉴스</h5>
                <div id="newsContainer">
                    <div class="text-center text-muted py-3">
                        <i class="fas fa-spinner fa-spin me-1"></i> 뉴스 로딩 중...
                    </div>
                </div>
            </div>
        </div>

        <%-- ── 오른쪽: 매수 패널 ─────────────────────────── --%>
        <div class="col-lg-4">
            <div class="buy-panel">
                <div class="buy-panel-header">
                    <h6 class="mb-0"><i class="fas fa-shopping-cart me-2"></i>주식 매수</h6>
                </div>
                <div class="buy-panel-body">
                    <div class="mb-3">
                        <label class="form-label text-muted small">종목명</label>
                        <input type="text" class="form-control bg-light" value="<c:out value="${sn}"/>" readonly>
                    </div>
                    <div class="mb-3">
                        <label class="form-label text-muted small">현재가</label>
                        <input type="text" class="form-control bg-light" id="displayPrice" readonly>
                    </div>
                    <div class="mb-3">
                        <label class="form-label text-muted small">수량 (주)</label>
                        <div class="input-group">
                            <button class="btn btn-outline-secondary" onclick="adjustQty(-1)">−</button>
                            <input type="number" class="form-control text-center" id="buyQty"
                                   min="1" value="1">
                            <button class="btn btn-outline-secondary" onclick="adjustQty(1)">+</button>
                        </div>
                    </div>
                    <div class="mb-2">
                        <label class="form-label text-muted small">매수 단가</label>
                        <input type="number" class="form-control" id="buyPrice" step="0.01"
                               value="<c:out value="${cp}"/>">
                    </div>
                    <div class="amount-preview">
                        <div class="label">예상 투자금액</div>
                        <div class="value" id="estimatedAmt">-</div>
                        <div class="label mt-1">수수료 0.1% 포함</div>
                    </div>
                    <button class="btn-buy-action mb-3" onclick="executeBuy()">
                        <i class="fas fa-check me-1"></i> 매수 주문
                    </button>
                    <button class="btn-watchlist" onclick="addToWatchlist('<c:out value="${sc}"/>')">
                        <i class="fas fa-star me-1"></i> 관심종목 추가
                    </button>
                </div>
            </div>
        </div>

    </div><!-- /row -->
</div><!-- /container -->

<%-- ══ 공통 Footer ══════════════════════════════════════════════ --%>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

<%-- ══ JS ══════════════════════════════════════════════════════ --%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>

<script>
/* ────────────────────────────────────────────────
   1. 기본 변수 설정
   ─────────────────────────────────────────────── */
const stockCode    = '<c:out value="${sc}"/>';
const country      = '<c:out value="${ctry}"/>';
const currentPrice = parseFloat('${not empty cp ? cp : 0}') || 0;
const ctx          = '${pageContext.request.contextPath}';

/* ────────────────────────────────────────────────
   2. 현재가 표시
   ─────────────────────────────────────────────── */
function formatPrice(p) {
    if (country === 'US') return '$' + Number(p).toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2});
    return Number(p).toLocaleString('ko-KR') + '원';
}
document.getElementById('displayPrice').value = formatPrice(currentPrice);

/* ────────────────────────────────────────────────
   3. 예상금액 계산
   ─────────────────────────────────────────────── */
function calcAmount() {
    const qty   = parseInt(document.getElementById('buyQty').value)   || 0;
    const price = parseFloat(document.getElementById('buyPrice').value) || 0;
    const total = qty * price * 1.001;
    document.getElementById('estimatedAmt').textContent = formatPrice(total);
}
document.getElementById('buyQty').addEventListener('input', calcAmount);
document.getElementById('buyPrice').addEventListener('input', calcAmount);
calcAmount();

function adjustQty(delta) {
    const el = document.getElementById('buyQty');
    el.value = Math.max(1, (parseInt(el.value) || 1) + delta);
    calcAmount();
}

/* ────────────────────────────────────────────────
   4. 매수 실행
   ─────────────────────────────────────────────── */
function executeBuy() {
    const qty   = parseInt(document.getElementById('buyQty').value)   || 0;
    const price = parseFloat(document.getElementById('buyPrice').value) || 0;
    if (qty <= 0)   { alert('수량을 1 이상 입력하세요.'); return; }
    if (price <= 0) { alert('유효한 매수 가격을 입력하세요.'); return; }

    if (!confirm(stockCode + ' ' + qty + '주를 ' + formatPrice(price) + '에 매수하시겠습니까?')) return;

    $.ajax({
        url: ctx + '/api/purchase/execute',
        type: 'POST',
        contentType: 'application/json',
        xhrFields: { withCredentials: true },
        data: JSON.stringify({ stockCode: stockCode, quantity: qty, price: price }),
        success: function(res) {
            if (res.success) {
                alert('✅ 매수 완료! 포트폴리오를 확인하세요.');
                window.location.href = ctx + '/portfolio';
            } else {
                alert('❌ ' + (res.message || '매수 처리 중 오류가 발생했습니다.'));
            }
        },
        error: function(xhr) {
            if (xhr.status === 401) {
                alert('로그인이 필요합니다.');
                window.location.href = ctx + '/member/login';
            } else {
                const msg = (xhr.responseJSON && xhr.responseJSON.message) || '매수 처리 중 오류가 발생했습니다.';
                alert('❌ ' + msg);
            }
        }
    });
}

/* ────────────────────────────────────────────────
   5. 관심종목 추가
   ─────────────────────────────────────────────── */
function addToWatchlist(code) {
    fetch(ctx + '/watchlist/add', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        credentials: 'same-origin',
        body: 'stockCode=' + encodeURIComponent(code)
    })
    .then(r => {
        if (r.status === 401) {
            alert('로그인이 필요합니다.');
            window.location.href = ctx + '/member/login';
            return null;
        }
        return r.json();
    })
    .then(d => {
        if (!d) return;
        alert(d.success ? '💖 관심종목에 추가되었습니다!' : (d.message || '추가 실패'));
    })
    .catch(() => alert('관심종목 추가 실패'));
}

/* ────────────────────────────────────────────────
   6. 뉴스 로드
   ─────────────────────────────────────────────── */
function loadNews() {
    $.ajax({
        url: ctx + '/news/api/stock/' + stockCode,
        method: 'GET',
        success: function(res) {
            const container = document.getElementById('newsContainer');
            if (res.success && res.news && res.news.length > 0) {
                // JSP EL 충돌 방지: backtick 템플릿 리터럴 대신 문자열 연결(+) 사용
                var html = '';
                res.news.slice(0,8).forEach(function(n) {
                    var link   = n.link  || '#';
                    var title  = n.title || '';
                    var src    = n.source || '';
                    var dt     = n.publishedAt ? new Date(n.publishedAt).toLocaleDateString('ko-KR') : '';
                    html += '<div class="news-item">'
                          + '<a href="' + link + '" target="_blank">' + title + '</a><br>'
                          + '<small class="text-muted">' + src + ' | ' + dt + '</small>'
                          + '</div>';
                });
                container.innerHTML = html;
            } else {
                container.innerHTML = '<p class="text-muted text-center py-2">관련 뉴스가 없습니다.</p>';
            }
        },
        error: function() {
            document.getElementById('newsContainer').innerHTML =
                '<p class="text-muted text-center py-2">뉴스를 불러올 수 없습니다.</p>';
        }
    });
}
loadNews();

/* ────────────────────────────────────────────────
   7. 주가 차트 (2025년 1월 ~ 현재)
      STOCK_PRICE 테이블 데이터 없음 → 현재가 기반 시뮬레이션
   ─────────────────────────────────────────────── */
let chartInstance = null;
let macroInstance = null;  // 거시 차트 인스턴스
let allData       = [];    // 전체 데이터 (2025-01-01 ~ today)

function generateHistory(basePrice, startDate, endDate) {
    const data   = [];
    const cur    = new Date(startDate);
    const end    = new Date(endDate);
    let   price  = basePrice;

    // 역방향: 현재가에서 과거로 역산 (현재→과거 랜덤 워크)
    // ✅ 일일 변동폭 ±1.2% (이전 -1.5%~+2% 대비 스파이크 방지)
    // ✅ 최저 가격 70% (이전 30% 대비 Y축 범위 축소)
    const totalDays = Math.ceil((end - cur) / 86400000);
    const prices    = new Array(totalDays + 1);
    prices[totalDays] = basePrice;

    for (let i = totalDays - 1; i >= 0; i--) {
        const change = prices[i+1] * (Math.random() * 0.024 - 0.012);
        prices[i] = Math.max(prices[i+1] + change, basePrice * 0.70);
    }

    let idx = 0;
    const d = new Date(startDate);
    while (d <= end) {
        const dow = d.getDay();
        if (dow !== 0 && dow !== 6) {  // 주말 제외
            data.push({
                date:   new Date(d).toISOString().split('T')[0],
                close:  Math.round(prices[idx] * 100) / 100,
                volume: Math.floor(Math.random() * 5000000 + 500000)
            });
        }
        idx++;
        d.setDate(d.getDate() + 1);
    }
    return data;
}

function filterByPeriod(data, period) {
    if (!data || data.length === 0) return data;
    const now  = new Date();
    let cutoff = new Date('2025-01-01');
    if (period === '3M') { cutoff = new Date(now); cutoff.setMonth(now.getMonth() - 3); }
    else if (period === '6M') { cutoff = new Date(now); cutoff.setMonth(now.getMonth() - 6); }
    else if (period === '1Y') { cutoff = new Date('2025-01-01'); }
    // 'ALL' → 전체 (2025-01-01부터)
    return data.filter(d => new Date(d.date) >= cutoff);
}

function renderChart(data) {
    const canvas = document.getElementById('priceChart');
    if (!canvas) return;

    const labels = data.map(d => d.date);
    const prices = data.map(d => d.close);

    const isUp = prices.length >= 2 && prices[prices.length-1] >= prices[0];
    const lineColor = isUp ? '#dc3545' : '#0d6efd';
    const fillColor = isUp ? 'rgba(220,53,69,.08)' : 'rgba(13,110,253,.08)';

    // ✅ Y축 범위 자동 계산: 실제 데이터 min/max ± CHART_Y_PADDING 여백
    //    크롤링 실제 데이터 대비 넉넉히 여백 확보 (모바일/태블릿 최적화)
    //    ★ 수치 조정 가이드: CHART_Y_PADDING 값만 바꾸면 즉시 반영
    //      - 0.10 (10% 여백, 좁게)
    //      - 0.20 (20% 여백, 넉넉 ← 현재 설정)
    //      - 0.30 (30% 여백, 매우 넉넉)
    const CHART_Y_PADDING = 0.20;   // ← 이 값만 바꾸면 됩니다
    const minPrice = Math.min.apply(null, prices);
    const maxPrice = Math.max.apply(null, prices);
    const rangePad = (maxPrice - minPrice) * CHART_Y_PADDING || maxPrice * 0.08;
    const yMin = Math.max(0, Math.floor(minPrice - rangePad));
    const yMax = Math.ceil(maxPrice + rangePad);

    if (chartInstance) chartInstance.destroy();

    chartInstance = new Chart(canvas.getContext('2d'), {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: country === 'US' ? 'Price (USD)' : '종가 (원)',
                data: prices,
                borderColor: lineColor,
                backgroundColor: fillColor,
                borderWidth: 2,
                fill: true,
                tension: 0.3,
                pointRadius: data.length > 100 ? 0 : 3,
                pointHoverRadius: 5
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { mode: 'index', intersect: false },
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: function(ctx) {
                            return country === 'US'
                                ? '$' + ctx.parsed.y.toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})
                                : ctx.parsed.y.toLocaleString('ko-KR') + '원';
                        }
                    }
                }
            },
            scales: {
                x: {
                    ticks: {
                        // ★ X축 날짜 눈금 수: 모바일에서 겹치지 않도록 8개로 제한
                        maxTicksLimit: 8,
                        callback: function(val, idx, ticks) {
                            const label = this.getLabelForValue(val);
                            return label ? label.substring(0,7) : '';
                        }
                    }
                },
                y: {
                    // ✅ 실제 가격 범위에 맞게 min/max 자동 설정 (모든 종목 공통 적용)
                    min: yMin,
                    max: yMax,
                    ticks: {
                        // ✅ Y축 눈금 수 제한 → 모바일 화면에 깔끔하게 표시
                        // ★ 조정 가이드: 4(최소) ~ 8(최대) 권장, 현재 6
                        maxTicksLimit: 6,
                        callback: function(val) {
                            if (country === 'US') {
                                return '$' + Number(val).toLocaleString('en-US', {minimumFractionDigits:0, maximumFractionDigits:2});
                            }
                            return Number(val).toLocaleString('ko-KR') + '원';
                        }
                    }
                }
            }
        }
    });
}

/* ────────────────────────────────────────────────
   거시적 차트 (macroChart): 전체 기간, 실제 최고/최저 표시
   - Y축: 실제 데이터 min/max ± 5% (꽉 찬 뷰 → 역사적 가격 범위 한눈에)
   - X축: 연·월 4개 눈금만 (공간 절약)
   - 툴팁: 가격만 표시, 포인트 없음 (상세는 미시 차트에서)
   ─────────────────────────────────────────────── */
function renderMacroChart(data) {
    const canvas = document.getElementById('macroChart');
    if (!canvas || !data || data.length === 0) return;

    const labels = data.map(d => d.date);
    const prices = data.map(d => d.close);
    const isUp   = prices.length >= 2 && prices[prices.length - 1] >= prices[0];
    const lineColor = isUp ? 'rgba(220,53,69,.75)' : 'rgba(13,110,253,.75)';
    const fillColor = isUp ? 'rgba(220,53,69,.05)' : 'rgba(13,110,253,.05)';

    // 거시 Y축: 전체 실제 min/max ± 5% (최고·최저 한 화면에 표시)
    // ★ 여백 비율 조정: 0.05 = 5%, 0.10 = 10%
    const allMin  = Math.min.apply(null, prices);
    const allMax  = Math.max.apply(null, prices);
    const macroPad = (allMax - allMin) * 0.05 || allMax * 0.03;
    const macroMin = Math.max(0, allMin - macroPad);
    const macroMax = allMax + macroPad;

    if (macroInstance) macroInstance.destroy();

    macroInstance = new Chart(canvas.getContext('2d'), {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                data: prices,
                borderColor: lineColor,
                backgroundColor: fillColor,
                borderWidth: 1.5,
                fill: true,
                tension: 0.3,
                pointRadius: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { mode: 'index', intersect: false },
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        title: function(t) { return t[0].label ? t[0].label.substring(0,7) : ''; },
                        label: function(t) {
                            return country === 'US'
                                ? '$' + t.parsed.y.toLocaleString('en-US',{minimumFractionDigits:0,maximumFractionDigits:2})
                                : t.parsed.y.toLocaleString('ko-KR') + '원';
                        }
                    }
                }
            },
            scales: {
                x: {
                    ticks: {
                        maxTicksLimit: 4,
                        callback: function(val) {
                            const label = this.getLabelForValue(val);
                            return label ? label.substring(0,7) : '';
                        }
                    },
                    grid: { display: false }
                },
                y: {
                    min: macroMin,
                    max: macroMax,
                    ticks: {
                        maxTicksLimit: 4,  // ★ 거시 Y축 눈금 수 (4개 권장)
                        callback: function(val) {
                            return country === 'US'
                                ? '$' + Math.round(val).toLocaleString()
                                : Math.round(val).toLocaleString('ko-KR') + '원';
                        }
                    }
                }
            }
        }
    });
}

function switchPeriod(btn, period) {
    document.querySelectorAll('.period-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    // 미시 차트만 업데이트 (거시 차트는 항상 전체 데이터 유지)
    renderChart(filterByPeriod(allData, period));
}

// 차트 초기화: 먼저 API 시도, 실패하면 시뮬레이션 데이터 사용
(function initChart() {
    $.ajax({
        url: ctx + '/api/chart/' + stockCode + '?period=daily',
        method: 'GET',
        timeout: 5000,
        success: function(res) {
            if (res.success && res.data && res.data.length > 0) {
                allData = res.data;
            } else {
                allData = generateHistory(currentPrice > 0 ? currentPrice : 100,
                                          '2025-01-01', new Date().toISOString().split('T')[0]);
            }
            renderMacroChart(allData);                        // 거시 차트: 전체 기간
            renderChart(filterByPeriod(allData, '3M'));       // 미시 차트: 3개월
        },
        error: function() {
            allData = generateHistory(currentPrice > 0 ? currentPrice : 100,
                                      '2025-01-01', new Date().toISOString().split('T')[0]);
            renderMacroChart(allData);                        // 거시 차트: 전체 기간
            renderChart(filterByPeriod(allData, '3M'));       // 미시 차트: 3개월
        }
    });
})();
</script>
</body>
</html>
