<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>종목 검색 - PortWatch</title>

    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

    <style>
        body { background: #f5f7fa; font-family: 'Segoe UI', Arial, sans-serif; }

        /* ── 검색 히어로 배너 ── */
        .search-hero {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: #fff;
            padding: 48px 0 36px;
            text-align: center;
        }
        .search-hero h1 { font-size: 2rem; font-weight: 700; margin-bottom: 6px; }
        .search-hero p  { opacity: .85; margin-bottom: 0; }

        /* ── 검색 입력 영역 ── */
        .search-wrap {
            background: #fff;
            border-radius: 50px;
            box-shadow: 0 4px 20px rgba(0,0,0,.15);
            display: flex;
            align-items: center;
            max-width: 680px;
            margin: 28px auto 0;
            padding: 6px 8px 6px 20px;
        }
        .search-wrap input {
            border: none;
            outline: none;
            flex: 1;
            font-size: 1.1rem;
            background: transparent;
            padding: 6px 0;
        }
        .search-wrap .btn-search {
            background: #667eea;
            color: #fff;
            border: none;
            border-radius: 40px;
            padding: 8px 24px;
            font-size: 1rem;
            cursor: pointer;
            white-space: nowrap;
        }
        .search-wrap .btn-search:hover { background: #5a6fd6; }

        /* ── 자동완성 드롭다운 ── */
        #autocomplete-list {
            position: absolute;
            z-index: 9999;
            background: #fff;
            border: 1px solid #e0e0e0;
            border-radius: 12px;
            box-shadow: 0 8px 24px rgba(0,0,0,.12);
            max-height: 340px;
            overflow-y: auto;
            width: 100%;
            top: calc(100% + 4px);
            left: 0;
        }
        .autocomplete-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 10px 18px;
            cursor: pointer;
            border-bottom: 1px solid #f5f5f5;
            transition: background .12s;
        }
        .autocomplete-item:last-child { border-bottom: none; }
        .autocomplete-item:hover, .autocomplete-item.active { background: #f0f4ff; }
        .autocomplete-item .ac-name { font-weight: 600; color: #222; font-size: .97rem; }
        .autocomplete-item .ac-meta { font-size: .82rem; color: #888; }
        .autocomplete-item .ac-badge {
            font-size: .72rem;
            padding: 2px 8px;
            border-radius: 20px;
            font-weight: 600;
        }
        .ac-badge-kr { background: #ffe5e5; color: #dc3545; }
        .ac-badge-us { background: #e5eeff; color: #0d6efd; }
        .ac-badge-kospi  { background: #fff3cd; color: #856404; }
        .ac-badge-kosdaq { background: #d1ecf1; color: #0c5460; }
        .ac-badge-nasdaq { background: #d4edda; color: #155724; }
        .ac-badge-nyse   { background: #f8d7da; color: #721c24; }

        .autocomplete-loading {
            padding: 12px 18px;
            color: #888;
            font-size: .9rem;
            text-align: center;
        }
        .autocomplete-empty {
            padding: 12px 18px;
            color: #aaa;
            font-size: .9rem;
            text-align: center;
        }

        /* ── 필터 탭 ── */
        .filter-tabs { margin: 28px 0 16px; }
        .filter-tabs .nav-link {
            border-radius: 20px;
            margin-right: 6px;
            padding: 5px 18px;
            color: #555;
            border: 1px solid #ddd;
        }
        .filter-tabs .nav-link.active {
            background: #667eea;
            border-color: #667eea;
            color: #fff;
        }

        /* ── 결과 테이블 ── */
        .result-table th { background: #667eea; color: #fff; white-space: nowrap; }
        .result-table tr:hover td { background: #f0f4ff; cursor: pointer; }
        .change-up   { color: #dc3545; font-weight: 600; }
        .change-down { color: #0d6efd; font-weight: 600; }
        .change-zero { color: #6c757d; }
        .market-badge {
            font-size: .72rem;
            padding: 2px 8px;
            border-radius: 10px;
            font-weight: 600;
        }

        /* ── 빈 결과 안내 ── */
        .no-result-box {
            text-align: center;
            padding: 60px 20px;
            color: #aaa;
        }
        .no-result-box i { font-size: 3rem; margin-bottom: 16px; }

        /* ── 검색어 하이라이트 ── */
        mark { background: #fff176; padding: 0; border-radius: 2px; }
    </style>
</head>
<body>

<!-- 네비게이션 -->
<nav class="navbar navbar-dark" style="background:#667eea;">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">
            <i class="fas fa-chart-line me-2"></i>PortWatch
        </a>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/stock/list" class="btn btn-outline-light btn-sm">
                <i class="fas fa-list me-1"></i>종목 목록
            </a>
            <a href="${pageContext.request.contextPath}/watchlist/list" class="btn btn-outline-light btn-sm">
                <i class="fas fa-heart me-1"></i>관심종목
            </a>
            <a href="${pageContext.request.contextPath}/portfolio/list" class="btn btn-outline-light btn-sm">
                <i class="fas fa-briefcase me-1"></i>포트폴리오
            </a>
        </div>
    </div>
</nav>

<!-- 검색 히어로 -->
<div class="search-hero">
    <div class="container">
        <h1><i class="fas fa-search me-2"></i>종목 검색</h1>
        <p>한글 종목명, 영문 종목명, 종목코드(KR/US) 모두 검색 가능합니다</p>

        <!-- 검색 입력 폼 -->
        <div style="position:relative; max-width:680px; margin:0 auto;">
            <form id="searchForm" action="${pageContext.request.contextPath}/stock/search" method="get" autocomplete="off">
                <div class="search-wrap">
                    <i class="fas fa-search" style="color:#aaa; margin-right:10px;"></i>
                    <input type="text" id="searchInput" name="keyword"
                           value="${fn:escapeXml(keyword)}"
                           placeholder="삼성전자, Apple, AAPL, 005930 ..."
                           aria-label="종목 검색"
                    />
                    <select name="country" class="form-select form-select-sm me-2" style="width:90px; border-radius:20px; border:1px solid #ddd;">
                        <option value="">전체</option>
                        <option value="KR" ${selectedCountry == 'KR' ? 'selected' : ''}>한국</option>
                        <option value="US" ${selectedCountry == 'US' ? 'selected' : ''}>미국</option>
                    </select>
                    <button type="submit" class="btn-search">
                        <i class="fas fa-search me-1"></i>검색
                    </button>
                </div>
                <!-- 자동완성 드롭다운 -->
                <div id="autocomplete-list" style="display:none;"></div>
            </form>
        </div>
    </div>
</div>

<!-- 메인 컨텐츠 -->
<div class="container my-4">

    <!-- 검색 결과 헤더 -->
    <c:if test="${not empty keyword}">
        <div class="d-flex align-items-center mb-3 gap-2">
            <h5 class="mb-0">
                "<strong>${fn:escapeXml(keyword)}</strong>" 검색 결과
            </h5>
            <span class="badge bg-secondary">${totalCount}건</span>
            <c:if test="${not empty selectedCountry}">
                <span class="badge ${selectedCountry == 'KR' ? 'bg-danger' : 'bg-primary'}">${selectedCountry}</span>
            </c:if>
        </div>
    </c:if>

    <!-- 국가 필터 탭 -->
    <c:if test="${not empty keyword}">
        <ul class="nav filter-tabs">
            <li class="nav-item">
                <a class="nav-link ${empty selectedCountry ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/stock/search?keyword=${fn:escapeXml(keyword)}">
                    전체
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${selectedCountry == 'KR' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/stock/search?keyword=${fn:escapeXml(keyword)}&country=KR">
                    <i class="fas fa-flag me-1"></i>한국 (KR)
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${selectedCountry == 'US' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/stock/search?keyword=${fn:escapeXml(keyword)}&country=US">
                    <i class="fas fa-flag me-1"></i>미국 (US)
                </a>
            </li>
        </ul>
    </c:if>

    <!-- 검색 결과 테이블 -->
    <c:choose>
        <c:when test="${empty keyword}">
            <!-- 키워드 없음: 안내 -->
            <div class="no-result-box">
                <i class="fas fa-search d-block"></i>
                <p class="fs-5 fw-semibold mb-1">종목명 또는 코드를 입력하세요</p>
                <p class="text-muted">한글/영문/코드 모두 검색 가능합니다 (예: 삼성전자, AAPL, 005930)</p>
            </div>
        </c:when>
        <c:when test="${empty stocks}">
            <!-- 검색 결과 없음 -->
            <div class="no-result-box">
                <i class="fas fa-search-minus d-block"></i>
                <p class="fs-5 fw-semibold mb-1">"${fn:escapeXml(keyword)}"에 대한 검색 결과가 없습니다</p>
                <p class="text-muted">종목명, 영문명, 종목코드로 다시 검색해 보세요</p>
                <a href="${pageContext.request.contextPath}/stock/list" class="btn btn-outline-primary mt-2">
                    <i class="fas fa-list me-1"></i>전체 종목 보기
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <!-- 검색 결과 목록 -->
            <div class="table-responsive">
                <table class="table table-hover result-table">
                    <thead>
                        <tr>
                            <th>종목코드</th>
                            <th>종목명</th>
                            <th>시장</th>
                            <th>국가</th>
                            <th class="text-end">현재가</th>
                            <th class="text-end">전일대비</th>
                            <th class="text-end">등락률</th>
                            <th class="text-center">액션</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="stock" items="${stocks}">
                            <tr onclick="location.href='${pageContext.request.contextPath}/stock/detail?stockCode=${stock.stockCode}'" style="cursor:pointer;">
                                <td><code>${stock.stockCode}</code></td>
                                <td><strong>${stock.stockName}</strong></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${stock.marketType == 'KOSPI'}">
                                            <span class="market-badge" style="background:#fff3cd;color:#856404;">KOSPI</span>
                                        </c:when>
                                        <c:when test="${stock.marketType == 'KOSDAQ'}">
                                            <span class="market-badge" style="background:#d1ecf1;color:#0c5460;">KOSDAQ</span>
                                        </c:when>
                                        <c:when test="${stock.marketType == 'NASDAQ'}">
                                            <span class="market-badge" style="background:#d4edda;color:#155724;">NASDAQ</span>
                                        </c:when>
                                        <c:when test="${stock.marketType == 'NYSE'}">
                                            <span class="market-badge" style="background:#f8d7da;color:#721c24;">NYSE</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="market-badge" style="background:#e2e3e5;color:#383d41;">${stock.marketType}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <span class="badge ${stock.country == 'KR' ? 'bg-danger' : 'bg-primary'}">${stock.country}</span>
                                </td>
                                <td class="text-end fw-bold">
                                    <c:choose>
                                        <c:when test="${stock.country == 'KR'}">
                                            <fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0"/>원
                                        </c:when>
                                        <c:otherwise>
                                            $<fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0.00"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end">
                                    <c:choose>
                                        <c:when test="${stock.priceChange > 0}">
                                            <span class="change-up">+<fmt:formatNumber value="${stock.priceChange}" pattern="#,##0.##"/></span>
                                        </c:when>
                                        <c:when test="${stock.priceChange < 0}">
                                            <span class="change-down"><fmt:formatNumber value="${stock.priceChange}" pattern="#,##0.##"/></span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="change-zero">0</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end">
                                    <c:choose>
                                        <c:when test="${stock.changeRate > 0}">
                                            <span class="change-up">+<fmt:formatNumber value="${stock.changeRate}" pattern="#,##0.00"/>%</span>
                                        </c:when>
                                        <c:when test="${stock.changeRate < 0}">
                                            <span class="change-down"><fmt:formatNumber value="${stock.changeRate}" pattern="#,##0.00"/>%</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="change-zero">0.00%</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center" onclick="event.stopPropagation();">
                                    <a href="${pageContext.request.contextPath}/stock/detail?stockCode=${stock.stockCode}"
                                       class="btn btn-sm btn-outline-primary me-1">
                                        <i class="fas fa-info-circle"></i>
                                    </a>
                                    <button class="btn btn-sm btn-outline-danger"
                                            onclick="addWatchlist('${stock.stockCode}', this)">
                                        <i class="fas fa-heart"></i>
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>

</div><!-- /.container -->

<!-- Bootstrap 5 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script>
    var ctx = '${pageContext.request.contextPath}';

    /* ════════════════════════════════════════════════════════════
       자동완성 (Autocomplete) 구현
       ▸ API: GET /api/stocks/search?keyword={q}
       ▸ 한글 종목명 / 영문 종목명 / 종목코드 모두 지원
       ▸ 디바운스 250ms → 타이핑 중 과다 요청 방지
       ▸ 키보드 방향키(↑↓) + Enter + ESC 지원
    ════════════════════════════════════════════════════════════ */
    var debounceTimer = null;
    var acItems = [];
    var acIndex = -1;

    var $input  = $('#searchInput');
    var $acList = $('#autocomplete-list');

    $input.on('input', function() {
        clearTimeout(debounceTimer);
        var q = $(this).val().trim();
        if (q.length < 1) {
            hideAC();
            return;
        }
        debounceTimer = setTimeout(function() { fetchAC(q); }, 250);
    });

    $input.on('keydown', function(e) {
        if (!$acList.is(':visible')) return;
        if (e.key === 'ArrowDown') {
            e.preventDefault();
            acIndex = Math.min(acIndex + 1, acItems.length - 1);
            renderAC(acItems, acIndex);
        } else if (e.key === 'ArrowUp') {
            e.preventDefault();
            acIndex = Math.max(acIndex - 1, -1);
            renderAC(acItems, acIndex);
        } else if (e.key === 'Enter') {
            if (acIndex >= 0 && acItems[acIndex]) {
                e.preventDefault();
                selectItem(acItems[acIndex]);
            }
        } else if (e.key === 'Escape') {
            hideAC();
        }
    });

    // 바깥 클릭 시 닫기
    $(document).on('click', function(e) {
        if (!$(e.target).closest('#searchForm').length) hideAC();
    });

    function fetchAC(q) {
        $acList.html('<div class="autocomplete-loading"><i class="fas fa-spinner fa-spin me-1"></i>검색 중...</div>').show();
        $.ajax({
            url: ctx + '/api/stocks/search',
            method: 'GET',
            data: { keyword: q },
            success: function(res) {
                if (res.success && res.stocks && res.stocks.length > 0) {
                    acItems = res.stocks;
                    acIndex = -1;
                    renderAC(acItems, -1);
                } else {
                    acItems = [];
                    $acList.html('<div class="autocomplete-empty"><i class="fas fa-search me-1"></i>"' + escHtml(q) + '" 검색 결과 없음</div>').show();
                }
            },
            error: function() {
                hideAC();
            }
        });
    }

    function renderAC(items, activeIdx) {
        if (!items || items.length === 0) { hideAC(); return; }

        var html = '';
        $.each(items, function(i, s) {
            var activeClass = (i === activeIdx) ? ' active' : '';
            var badgeClass  = s.country === 'KR' ? 'ac-badge-kr' : 'ac-badge-us';
            var marketClass = getMarketBadgeClass(s.marketType);

            html += '<div class="autocomplete-item' + activeClass + '" data-idx="' + i + '">';
            html +=   '<div>';
            html +=     '<span class="ac-name">' + highlightKeyword(s.stockName, $input.val()) + '</span>';
            if (s.englishName) {
                html += '<br><span class="ac-meta">' + escHtml(s.englishName) + '</span>';
            }
            html +=   '</div>';
            html +=   '<div class="d-flex gap-1 align-items-center">';
            html +=     '<span class="ac-meta">' + escHtml(s.stockCode) + '</span>';
            html +=     '<span class="ac-badge ' + marketClass + '">' + escHtml(s.marketType || '') + '</span>';
            html +=     '<span class="ac-badge ' + badgeClass + '">' + escHtml(s.country) + '</span>';
            html +=   '</div>';
            html += '</div>';
        });

        $acList.html(html).show();

        // 클릭 이벤트 등록
        $acList.find('.autocomplete-item').on('mousedown', function(e) {
            e.preventDefault();
            var idx = parseInt($(this).data('idx'), 10);
            selectItem(items[idx]);
        }).on('mouseenter', function() {
            acIndex = parseInt($(this).data('idx'), 10);
            $acList.find('.autocomplete-item').removeClass('active');
            $(this).addClass('active');
        });
    }

    function selectItem(stock) {
        // 자동완성 선택 → 상세 페이지로 바로 이동
        hideAC();
        window.location.href = ctx + '/stock/detail?stockCode=' + encodeURIComponent(stock.stockCode);
    }

    function hideAC() { $acList.hide(); acIndex = -1; }

    function highlightKeyword(text, keyword) {
        if (!keyword || !text) return escHtml(text || '');
        var escaped = escHtml(text);
        var escapedKw = escHtml(keyword).replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
        return escaped.replace(new RegExp('(' + escapedKw + ')', 'gi'), '<mark>$1</mark>');
    }

    function getMarketBadgeClass(market) {
        switch ((market || '').toUpperCase()) {
            case 'KOSPI':  return 'ac-badge-kospi';
            case 'KOSDAQ': return 'ac-badge-kosdaq';
            case 'NASDAQ': return 'ac-badge-nasdaq';
            case 'NYSE':   return 'ac-badge-nyse';
            default:       return '';
        }
    }

    function escHtml(s) {
        if (!s) return '';
        return String(s)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;');
    }

    /* ════════════════════════════════════════════════════════════
       관심종목 추가
    ════════════════════════════════════════════════════════════ */
    function addWatchlist(stockCode, btn) {
        $.ajax({
            url: ctx + '/api/watchlist/add',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ stockCode: stockCode }),
            success: function(res) {
                if (res.success) {
                    $(btn).removeClass('btn-outline-danger').addClass('btn-danger');
                    $(btn).html('<i class="fas fa-heart"></i>');
                    alert('관심종목에 추가되었습니다.');
                } else {
                    alert(res.message || '관심종목 추가 실패');
                }
            },
            error: function(xhr) {
                if (xhr.status === 401) {
                    alert('로그인이 필요합니다.');
                    location.href = ctx + '/member/login';
                } else {
                    alert('관심종목 추가 중 오류가 발생했습니다.');
                }
            }
        });
    }
</script>
</body>
</html>
