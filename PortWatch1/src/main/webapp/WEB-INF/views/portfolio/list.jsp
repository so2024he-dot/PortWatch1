<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>📊 내 포트폴리오 - PortWatch</title>

    <!-- Bootstrap 5.3 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Chart.js 4.4 -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

    <style>
        body { background-color: #f0f2f5; font-family: 'Segoe UI', sans-serif; }

        /* ── 헤더 ────────────────────────────────────── */
        .portfolio-banner {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 32px 0 24px;
            margin-bottom: 28px;
        }

        /* ── 요약 카드 ──────────────────────────────── */
        .summary-card {
            background: white;
            border-radius: 14px;
            padding: 20px 24px;
            box-shadow: 0 2px 12px rgba(0,0,0,.08);
            height: 100%;
        }
        .summary-card .label  { font-size: .78rem; color: #6c757d; margin-bottom: 6px; }
        .summary-card .value  { font-size: 1.6rem; font-weight: 800; color: #212529; }
        .summary-card .value.up   { color: #dc3545; }
        .summary-card .value.down { color: #0d6efd; }

        /* ── 차트 섹션 ──────────────────────────────── */
        .chart-section {
            background: white;
            border-radius: 14px;
            padding: 24px 28px;
            box-shadow: 0 2px 12px rgba(0,0,0,.08);
            margin-bottom: 24px;
        }
        .chart-section h5 {
            font-weight: 700; color: #1e3a5f;
            border-bottom: 2px solid #e9ecef;
            padding-bottom: 10px; margin-bottom: 20px;
        }
        .chart-wrap { position: relative; height: 340px; }

        /* ── 종목 카드 ──────────────────────────────── */
        .stock-item {
            background: white;
            border-radius: 14px;
            padding: 18px 22px;
            box-shadow: 0 2px 8px rgba(0,0,0,.07);
            margin-bottom: 14px;
            transition: box-shadow .2s;
        }
        .stock-item:hover { box-shadow: 0 4px 16px rgba(0,0,0,.12); }
        .stock-item .sname { font-weight: 700; font-size: 1.05rem; }
        .stock-item .scode { font-size: .8rem; color: #6c757d; }
        .tbl-label { font-size: .75rem; color: #6c757d; }
        .tbl-val   { font-size: .95rem; font-weight: 600; }
        .profit-up   { color: #dc3545; font-weight: 700; }
        .profit-down { color: #0d6efd; font-weight: 700; }

        /* ── 빈 포트폴리오 ──────────────────────────── */
        .empty-state {
            background: white; border-radius: 14px;
            padding: 60px 24px; text-align: center;
            box-shadow: 0 2px 12px rgba(0,0,0,.08);
        }
        .empty-state i { font-size: 64px; color: #dee2e6; }
    </style>
</head>
<body>

<%-- 공통 헤더 --%>
<jsp:include page="../common/header.jsp"/>

<%-- ══ 배너 ══════════════════════════════════════════════════ --%>
<div class="portfolio-banner">
    <div class="container">
        <h2 class="mb-1"><i class="fas fa-briefcase me-2"></i>내 포트폴리오</h2>
        <p class="mb-0 opacity-75">
            <c:if test="${not empty loginMember}">
                <c:out value="${loginMember.memberName}"/>님의 투자 현황
            </c:if>
        </p>
    </div>
</div>

<div class="container pb-5">

<%-- ══ 에러 메시지 ══════════════════════════════════════════ --%>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-warning"><i class="fas fa-exclamation-triangle me-2"></i><c:out value="${errorMessage}"/></div>
</c:if>
<c:if test="${not empty successMessage}">
    <div class="alert alert-success"><i class="fas fa-check me-2"></i><c:out value="${successMessage}"/></div>
</c:if>

<c:choose>
<%-- ══════════════════════════════════════════════
     [A] 포트폴리오가 비어 있을 때
     ══════════════════════════════════════════════ --%>
<c:when test="${empty portfolioList}">
    <div class="empty-state">
        <i class="fas fa-folder-open d-block mb-4"></i>
        <h4 class="text-muted mb-2">보유 종목이 없습니다</h4>
        <p class="text-muted mb-4">주식을 매수하면 포트폴리오에 자동으로 추가됩니다.</p>
        <a href="${pageContext.request.contextPath}/stock/list" class="btn btn-primary btn-lg rounded-pill px-4">
            <i class="fas fa-shopping-cart me-1"></i> 주식 매수하기
        </a>
    </div>
</c:when>

<%-- ══════════════════════════════════════════════
     [B] 포트폴리오 데이터가 있을 때
     ══════════════════════════════════════════════ --%>
<c:otherwise>

    <%-- ── 요약 카드 4개 ───────────────────────────────── --%>
    <%-- JSTL 변수로 합계 계산 (US 종목은 환율 1380 적용) --%>
    <c:set var="KRW_RATE" value="1380"/>
    <c:set var="totalInvest" value="0"/>
    <c:set var="totalCurrent" value="0"/>
    <c:forEach items="${portfolioList}" var="p">
        <c:set var="fx" value="${p.country eq 'US' ? KRW_RATE : 1}"/>
        <c:set var="pInvest"  value="${(not empty p.avgPrice ? p.avgPrice : 0) * (not empty p.quantity ? p.quantity : 0) * fx}"/>
        <c:set var="pCurrent" value="${(not empty p.currentPrice ? p.currentPrice : 0) * (not empty p.quantity ? p.quantity : 0) * fx}"/>
        <c:set var="totalInvest"  value="${totalInvest  + pInvest}"/>
        <c:set var="totalCurrent" value="${totalCurrent + pCurrent}"/>
    </c:forEach>
    <c:set var="totalProfit"     value="${totalCurrent - totalInvest}"/>
    <c:set var="totalProfitRate" value="${totalInvest > 0 ? (totalProfit / totalInvest * 100) : 0}"/>

    <div class="row g-3 mb-4">
        <div class="col-6 col-md-3">
            <div class="summary-card">
                <div class="label">총 투자금</div>
                <div class="value"><fmt:formatNumber value="${totalInvest}" pattern="#,###"/>원</div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="summary-card">
                <div class="label">평가금액</div>
                <div class="value"><fmt:formatNumber value="${totalCurrent}" pattern="#,###"/>원</div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="summary-card">
                <div class="label">평가손익</div>
                <div class="value ${totalProfit >= 0 ? 'up' : 'down'}">
                    <c:if test="${totalProfit >= 0}">+</c:if><fmt:formatNumber value="${totalProfit}" pattern="#,###"/>원
                </div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="summary-card">
                <div class="label">수익률</div>
                <div class="value ${totalProfitRate >= 0 ? 'up' : 'down'}">
                    <c:if test="${totalProfitRate >= 0}">+</c:if><fmt:formatNumber value="${totalProfitRate}" pattern="#,##0.00"/>%
                </div>
            </div>
        </div>
    </div>

    <%-- ── 차트 섹션 ─────────────────────────────────────── --%>
    <div class="row g-4 mb-4">

        <%-- 원형 차트: 종목별 보유 비중 --%>
        <div class="col-md-5">
            <div class="chart-section">
                <h5><i class="fas fa-chart-pie me-2"></i>종목별 보유 비중</h5>
                <div class="chart-wrap">
                    <canvas id="pieChart"></canvas>
                </div>
            </div>
        </div>

        <%-- 막대 차트: 종목별 손익 현황 --%>
        <div class="col-md-7">
            <div class="chart-section">
                <h5><i class="fas fa-chart-bar me-2"></i>종목별 손익 현황</h5>
                <div class="chart-wrap">
                    <canvas id="barChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <%-- ── 종목 목록 헤더 ──────────────────────────────── --%>
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="mb-0"><i class="fas fa-list me-2"></i>보유 종목 (${portfolioList.size()}개)</h5>
        <a href="${pageContext.request.contextPath}/stock/list"
           class="btn btn-primary rounded-pill px-3">
            <i class="fas fa-plus me-1"></i> 주식 추가
        </a>
    </div>

    <%-- ── 종목 카드 리스트 ───────────────────────────── --%>
    <c:forEach items="${portfolioList}" var="p">
        <c:set var="pAvg"     value="${not empty p.avgPrice ? p.avgPrice : 0}"/>
        <c:set var="pQty"     value="${not empty p.quantity ? p.quantity : 0}"/>
        <c:set var="pCur"     value="${not empty p.currentPrice ? p.currentPrice : 0}"/>
        <c:set var="pIsUS"    value="${p.country eq 'US'}"/>
        <c:set var="pUnit"    value="${pIsUS ? '$' : '원'}"/>
        <c:set var="pPnl"     value="${(pCur - pAvg) * pQty}"/>
        <c:set var="pRate"    value="${pAvg > 0 ? ((pCur - pAvg) / pAvg * 100) : 0}"/>
        <%-- 손익 표시: US는 USD 단위 그대로 (환율 표시는 차트에서) --%>

        <div class="stock-item">
            <div class="row align-items-center">
                <div class="col-md-3 mb-2 mb-md-0">
                    <div class="sname"><c:out value="${not empty p.stockName ? p.stockName : p.stockCode}"/></div>
                    <div class="scode">
                        <c:out value="${p.stockCode}"/>
                        <c:if test="${pIsUS}">
                            <span class="badge bg-info ms-1" style="font-size:.7rem">USD</span>
                        </c:if>
                    </div>
                </div>
                <div class="col-4 col-md-2 text-center">
                    <div class="tbl-label">수량</div>
                    <div class="tbl-val"><fmt:formatNumber value="${pQty}" pattern="#,##0.####"/>주</div>
                </div>
                <div class="col-4 col-md-2 text-center">
                    <div class="tbl-label">평균매수가</div>
                    <div class="tbl-val">
                        <c:choose>
                            <c:when test="${pIsUS}">$<fmt:formatNumber value="${pAvg}" pattern="#,##0.00"/></c:when>
                            <c:otherwise><fmt:formatNumber value="${pAvg}" pattern="#,###"/>원</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="col-4 col-md-2 text-center">
                    <div class="tbl-label">현재가</div>
                    <div class="tbl-val">
                        <c:choose>
                            <c:when test="${pIsUS}">$<fmt:formatNumber value="${pCur}" pattern="#,##0.00"/></c:when>
                            <c:otherwise><fmt:formatNumber value="${pCur}" pattern="#,###"/>원</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="col-6 col-md-2 text-center">
                    <div class="tbl-label">평가손익</div>
                    <div class="tbl-val ${pPnl >= 0 ? 'profit-up' : 'profit-down'}">
                        <c:if test="${pPnl >= 0}">+</c:if>
                        <c:choose>
                            <c:when test="${pIsUS}">$<fmt:formatNumber value="${pPnl}" pattern="#,##0.00"/></c:when>
                            <c:otherwise><fmt:formatNumber value="${pPnl}" pattern="#,###"/>원</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="col-6 col-md-1 text-center">
                    <div class="tbl-label">수익률</div>
                    <div class="tbl-val ${pRate >= 0 ? 'profit-up' : 'profit-down'}">
                        <c:if test="${pRate >= 0}">+</c:if><fmt:formatNumber value="${pRate}" pattern="#,##0.0"/>%
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>

</c:otherwise>
</c:choose>

</div><!-- /container -->

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<c:if test="${not empty portfolioList}">
<script>
/* ════════════════════════════════════════════════
   Chart.js 포트폴리오 차트 초기화
   ════════════════════════════════════════════════
   ✅ [수정 2026-04-22] 미국 주식 환율 변환 추가
      - US 종목은 USD → KRW 변환 후 차트에 표시
      - 기준 환율: 1 USD = USD_TO_KRW 원 (고정값)
      - 툴팁에 원래 USD 금액도 함께 표시
   ════════════════════════════════════════════════ */

// ━━━ 환율 설정 (USD → KRW) ━━━━━━━━━━━━━━━━━━━━
const USD_TO_KRW = 1380;   // 참고 환율: 1달러 = 1,380원 (필요 시 조정)

// ── 데이터 준비 ────────────────────────────────
const labels       = [];
const valuesKRW    = [];   // 평가금액 (모두 KRW로 환산)
const valuesOrig   = [];   // 평가금액 (원래 통화)
const profits      = [];   // 손익 금액 (KRW 환산)
const profitsOrig  = [];   // 손익 금액 (원래 통화)
const pRates       = [];   // 수익률 %
const hasPriceFeed = [];   // true=실시간 현재가 / false=매수가 기준
const currencies   = [];   // 'KRW' 또는 'USD'
const COLORS  = ['#4e79a7','#f28e2b','#e15759','#76b7b2',
                 '#59a14f','#edc948','#b07aa1','#ff9da7',
                 '#9c755f','#bab0ac'];

<c:forEach items="${portfolioList}" var="p" varStatus="st">
<c:if test="${not empty p.stockCode}"><%-- 종목코드 없는 빈 행 제외 --%>
    <c:set var="pAvg2" value="${not empty p.avgPrice ? p.avgPrice : 0}"/>
    <c:set var="pQty2" value="${not empty p.quantity ? p.quantity : 0}"/>
    <c:set var="pCountry" value="${not empty p.country ? p.country : 'KR'}"/>
    <%-- 현재가 우선순위: STOCK.currentPrice > PORTFOLIO_ITEM.purchasePrice > avgPrice --%>
    <c:choose>
        <c:when test="${not empty p.currentPrice and p.currentPrice gt 0}">
            <c:set var="pCur2"    value="${p.currentPrice}"/>
            <c:set var="pHasFeed" value="true"/>
        </c:when>
        <c:when test="${not empty p.purchasePrice and p.purchasePrice gt 0}">
            <c:set var="pCur2"    value="${p.purchasePrice}"/>
            <c:set var="pHasFeed" value="false"/>
        </c:when>
        <c:otherwise>
            <c:set var="pCur2"    value="${pAvg2}"/>
            <c:set var="pHasFeed" value="false"/>
        </c:otherwise>
    </c:choose>
    {
        const rawCur  = ${pCur2};
        const rawAvg  = ${pAvg2};
        const qty     = ${pQty2};
        const isUS    = ('${pCountry}' === 'US');
        const fx      = isUS ? USD_TO_KRW : 1;   // KR은 1:1, US는 환율 적용
        const curKRW  = rawCur * fx;
        const avgKRW  = rawAvg * fx;

        labels.push('<c:out value="${not empty p.stockName ? p.stockName : p.stockCode}"/>');
        valuesKRW.push(Math.round(curKRW  * qty));
        valuesOrig.push(Math.round(rawCur * qty));
        profits.push(Math.round((curKRW  - avgKRW)  * qty));
        profitsOrig.push(Math.round((rawCur - rawAvg) * qty));
        pRates.push(rawAvg > 0 ? Math.round(((rawCur - rawAvg) / rawAvg) * 10000) / 100 : 0);
        hasPriceFeed.push(${pHasFeed});
        currencies.push(isUS ? 'USD' : 'KRW');
    }
</c:if>
</c:forEach>

// ── 1. 도넛 차트 (종목별 보유 비중 — KRW 환산) ───────────
(function() {
    const ctx = document.getElementById('pieChart');
    if (!ctx) return;

    if (valuesKRW.length === 0) return;

    const total = valuesKRW.reduce((a,b) => a+b, 0);

    new Chart(ctx.getContext('2d'), {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: valuesKRW,
                backgroundColor: COLORS.slice(0, labels.length),
                borderWidth: 3,
                borderColor: '#fff',
                hoverBorderWidth: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '55%',
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: { padding: 12, font: { size: 12 } }
                },
                tooltip: {
                    callbacks: {
                        label: function(c) {
                            const idx  = c.dataIndex;
                            const krw  = valuesKRW[idx];
                            const orig = valuesOrig[idx];
                            const cur  = currencies[idx];
                            const pct  = total > 0 ? (krw / total * 100).toFixed(1) : 0;
                            // 미국 주식은 USD 원금도 함께 표시
                            if (cur === 'USD') {
                                return [
                                    ` ${c.label}: ${krw.toLocaleString('ko-KR')}원 환산 (${pct}%)`,
                                    ` 원화 환산 = $${orig.toLocaleString('en-US')} × ${USD_TO_KRW.toLocaleString('ko-KR')}원/$ `
                                ];
                            }
                            return ` ${c.label}: ${krw.toLocaleString('ko-KR')}원 (${pct}%)`;
                        }
                    }
                }
            }
        }
    });
})();

// ── 2. 막대 차트 (종목별 손익 — KRW 환산) ─────────────────
(function() {
    const ctx = document.getElementById('barChart');
    if (!ctx) return;

    if (labels.length === 0) {
        ctx.parentElement.innerHTML =
            '<p class="text-center text-muted py-4">' +
            '<i class="fas fa-info-circle me-1"></i>보유 종목이 없습니다</p>';
        return;
    }

    const bgColors = profits.map(v => v >= 0 ? 'rgba(220,53,69,.75)' : 'rgba(13,110,253,.75)');
    const bdColors = profits.map(v => v >= 0 ? '#dc3545' : '#0d6efd');

    const profMax = profits.length ? Math.max(...profits, 1)  : 1;
    const profMin = profits.length ? Math.min(...profits, -1) : -1;
    const yPad    = Math.max(Math.abs(profMax), Math.abs(profMin)) * 0.30 + 100;

    new Chart(ctx.getContext('2d'), {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: '평가손익 (KRW 환산)',
                data: profits,
                backgroundColor: bgColors,
                borderColor: bdColors,
                borderWidth: 2,
                borderRadius: 6,
                borderSkipped: false,
                minBarLength: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: function(c) {
                            const idx  = c.dataIndex;
                            const v    = c.parsed.y;
                            const sign = v >= 0 ? '+' : '';
                            const cur  = currencies[idx];
                            const note = !hasPriceFeed[idx] ? ' ⚠️현재가 미확인' : '';
                            if (cur === 'USD') {
                                const usdPnl = profitsOrig[idx];
                                return [
                                    ` 손익(환산): ${sign}${v.toLocaleString('ko-KR')}원${note}`,
                                    ` 손익(USD): ${usdPnl >= 0 ? '+' : ''}$${usdPnl.toLocaleString('en-US')}`
                                ];
                            }
                            return ` 손익: ${sign}${v.toLocaleString('ko-KR')}원${note}`;
                        },
                        afterLabel: function(c) {
                            const r = pRates[c.dataIndex];
                            return ` 수익률: ${r >= 0 ? '+' : ''}${r}%`;
                        }
                    }
                }
            },
            scales: {
                x: { grid: { display: false } },
                y: {
                    suggestedMin: profMin - yPad,
                    suggestedMax: profMax + yPad,
                    grid: { color: 'rgba(0,0,0,.06)' },
                    ticks: {
                        maxTicksLimit: 6,
                        callback: v => (v >= 0 ? '+' : '') + v.toLocaleString('ko-KR') + '원'
                    }
                }
            }
        }
    });
})();
</script>
</c:if>

</body>
</html>
