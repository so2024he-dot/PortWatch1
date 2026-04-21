<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>주식 목록 - PortWatch</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <style>
        .stock-card {
            transition: transform 0.2s, box-shadow 0.2s;
            cursor: pointer;
            height: 100%;
        }
        
        .stock-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }
        
        .price-up {
            color: #dc3545;
        }
        
        .price-down {
            color: #0d6efd;
        }
        
        .filter-section {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 30px;
        }
        
        .btn-filter {
            margin: 5px;
        }
        
        .stock-code {
            font-size: 0.9em;
            color: #6c757d;
        }
        
        .stock-price {
            font-size: 1.5em;
            font-weight: bold;
        }
        
        .change-rate {
            font-size: 1.1em;
            font-weight: bold;
        }
        
        .market-badge {
            font-size: 0.8em;
        }
    </style>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f5f5f5; padding: 20px; }
        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; }
        h1 { color: #667eea; margin-bottom: 30px; }
        .search-box { margin-bottom: 20px; }
        .search-box input { padding: 10px; width: 300px; border: 2px solid #ddd; border-radius: 5px; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background: #667eea; color: white; }
        tr:hover { background: #f0f0f0; }
        .kr { color: #e74c3c; font-weight: bold; }
        .us { color: #3498db; font-weight: bold; }
    </style>
</head>
<body>
    <!-- 네비게이션 바 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                <i class="fas fa-chart-line"></i> PortWatch
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                            <i class="fas fa-th-large"></i> 대시보드
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/stock/list">
                            <i class="fas fa-chart-bar"></i> 주식
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/watchlist/list">
                            <i class="fas fa-star"></i> 관심종목
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/news/list">
                            <i class="fas fa-newspaper"></i> 뉴스
                        </a>
                    </li>
                    <c:choose>
                        <c:when test="${not empty loginMember}">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="userDropdown" 
                                   role="button" data-bs-toggle="dropdown">
                                    <i class="fas fa-user"></i> ${loginMember.memberName}
                                </a>
                                <ul class="dropdown-menu" aria-labelledby="userDropdown">
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/member/mypage">
                                            <i class="fas fa-user-circle"></i> 마이페이지
                                        </a>
                                    </li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/member/logout">
                                            <i class="fas fa-sign-out-alt"></i> 로그아웃
                                        </a>
                                    </li>
                                </ul>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/member/login">
                                    <i class="fas fa-sign-in-alt"></i> 로그인
                                </a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>
    
    <!-- 메인 컨텐츠 -->
    <div class="container mt-4">
        <!-- 페이지 헤더 -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>
                <i class="fas fa-chart-bar"></i> 주식 목록
                <small class="text-muted fs-6 ms-2">
                    (${not empty stocks ? stocks.size() : 0}개 기업 표시 중
                    <c:if test="${not empty totalCount}"> / 전체 ${totalCount}개</c:if>)
                </small>
            </h2>
            <div class="d-flex gap-2">
                <!-- 페이지 이동 버튼 -->
                <c:if test="${not empty currentPage}">
                    <div class="btn-group">
                        <c:if test="${currentPage > 0}">
                            <a href="${pageContext.request.contextPath}/stock/list?page=${currentPage-1}<c:if test="${not empty selectedCountry}">&country=${selectedCountry}</c:if>"
                               class="btn btn-outline-secondary btn-sm">
                                <i class="fas fa-chevron-left"></i> 이전 50개
                            </a>
                        </c:if>
                        <span class="btn btn-secondary btn-sm disabled">
                            ${currentPage+1}/${totalPages} 페이지
                        </span>
                        <c:if test="${currentPage + 1 < totalPages}">
                            <a href="${pageContext.request.contextPath}/stock/list?page=${currentPage+1}<c:if test="${not empty selectedCountry}">&country=${selectedCountry}</c:if>"
                               class="btn btn-outline-secondary btn-sm">
                                다음 50개 <i class="fas fa-chevron-right"></i>
                            </a>
                        </c:if>
                    </div>
                </c:if>
                <a href="${pageContext.request.contextPath}/stock/search" class="btn btn-outline-primary btn-sm">
                    <i class="fas fa-search"></i> 검색
                </a>
            </div>
        </div>
        
        <!-- 필터 섹션 -->
        <div class="filter-section">
            <div class="row">
                <div class="col-md-6 mb-3">
                    <h5>
                        <i class="fas fa-globe"></i> 국가 선택
                    </h5>
                    <div class="btn-group" role="group">
                        <button type="button" class="btn btn-outline-primary btn-filter" data-country="ALL">
                            전체
                        </button>
                        <button type="button" class="btn btn-outline-primary btn-filter" data-country="KR">
                            🇰🇷 한국
                        </button>
                        <button type="button" class="btn btn-outline-primary btn-filter" data-country="US">
                            🇺🇸 미국
                        </button>
                    </div>
                </div>
                
                <div class="col-md-6 mb-3">
                    <h5>
                        <i class="fas fa-chart-line"></i> 시장 선택
                    </h5>
                    <div class="btn-group" role="group">
                        <button type="button" class="btn btn-outline-primary btn-filter" data-market="ALL">
                            전체
                        </button>
                        <button type="button" class="btn btn-outline-primary btn-filter" data-market="KOSPI">
                            KOSPI
                        </button>
                        <button type="button" class="btn btn-outline-primary btn-filter" data-market="KOSDAQ">
                            KOSDAQ
                        </button>
                        <button type="button" class="btn btn-outline-primary btn-filter" data-market="NASDAQ">
                            NASDAQ
                        </button>
                        <button type="button" class="btn btn-outline-primary btn-filter" data-market="NYSE">
                            NYSE
                        </button>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- 에러 메시지 -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <!-- 주식 목록 -->
        <div id="stockListContainer">
            <c:choose>
                <c:when test="${empty stocks}">
                    <div class="alert alert-info text-center">
                        <i class="fas fa-info-circle"></i>
                        조회된 주식이 없습니다.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row">
                        <c:forEach items="${stocks}" var="stock">
                            <div class="col-md-4 col-lg-3 mb-4">
                                <div class="card stock-card" onclick="location.href='${pageContext.request.contextPath}/stock/detail?stockCode=${stock.stockCode}'">
                                    <div class="card-body">
                                        <!-- 종목명 -->
                                        <h5 class="card-title">
                                            ${stock.stockName}
                                            <c:if test="${not empty loginMember}">
                                                <button class="btn btn-sm btn-outline-danger float-end" 
                                                        onclick="event.stopPropagation(); addToWatchlist('${stock.stockCode}')">
                                                    <i class="fas fa-heart"></i>
                                                </button>
                                            </c:if>
                                        </h5>
                                        
                                        <!-- 종목 코드 -->
                                        <p class="stock-code mb-2">
                                            ${stock.stockCode}
                                            <span class="badge bg-secondary market-badge ms-2">
                                                ${stock.marketType}
                                            </span>
                                        </p>
                                        
                                        <!-- 현재가 -->
                                        <div class="stock-price mb-2">
                                            <fmt:formatNumber value="${stock.currentPrice}" type="number" groupingUsed="true"/>
                                            <c:choose>
                                                <c:when test="${stock.country eq 'KR'}">원</c:when>
                                                <c:otherwise>$</c:otherwise>
                                            </c:choose>
                                        </div>
                                        
                                        <!-- 등락률 -->
                                        <div class="change-rate ${stock.changeRate >= 0 ? 'price-up' : 'price-down'}">
                                            <c:choose>
                                                <c:when test="${stock.changeRate >= 0}">
                                                    <i class="fas fa-arrow-up"></i> +<fmt:formatNumber value="${stock.changeRate}" type="number" minFractionDigits="2" maxFractionDigits="2"/>%
                                                </c:when>
                                                <c:otherwise>
                                                    <i class="fas fa-arrow-down"></i> <fmt:formatNumber value="${stock.changeRate}" type="number" minFractionDigits="2" maxFractionDigits="2"/>%
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        
                                        <!-- 거래량 -->
                                        <small class="text-muted">
                                            거래량: <fmt:formatNumber value="${stock.volume}" type="number" groupingUsed="true"/>
                                        </small>
                                        
                                        <!-- 매수 버튼 -->
                                        <c:if test="${not empty loginMember}">
                                            <div class="mt-3">
                                                <button class="btn btn-primary btn-sm w-100" 
                                                        onclick="event.stopPropagation(); location.href='${pageContext.request.contextPath}/stock/buy?stockCode=${stock.stockCode}'">
                                                    <i class="fas fa-shopping-cart"></i> 매수
                                                </button>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         ✅ 수정된 JavaScript - JSP EL 에러 해결
         ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ -->
    <script>
    /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * StockFilter 객체 - 주식 필터링 관리
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * 핵심 수정:
     * ❌ 잘못된 방법: currentCountry: '${StockFilter.currentCountry}',
     * ✅ 올바른 방법: currentCountry: '${selectedCountry}' || 'ALL',
     * 
     * 설명:
     * - JSP EL 표현식에서 'this' 키워드 사용 불가
     * - Controller에서 Model로 전달한 값을 직접 사용
     * - JavaScript 메서드 내에서는 'this' 사용 가능
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
    
    const StockFilter = {
        // ✅ 올바른 방법: Controller에서 전달받은 값 직접 사용
        contextPath: '${pageContext.request.contextPath}',
	    currentCountry: '${selectedCountry}' || 'ALL',
	    currentMarket: '${selectedMarket}' || 'ALL',
        
        /**
         * 초기화
         */
        init: function() {
            console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
            console.log('📊 주식 필터 초기화');
            console.log('  - contextPath:', StockFilter.contextPath);
            console.log('  - 현재 국가:', StockFilter.currentCountry);
            console.log('  - 현재 시장:', StockFilter.currentMarket);
            console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
            
            StockFilter.bindEvents();
            StockFilter.updateButtonStates();
        },
        
        /**
         * 이벤트 리스너 바인딩
         */
        bindEvents: function() {
            console.log('🔗 이벤트 리스너 바인딩');
            
            // 국가 필터 버튼
            document.querySelectorAll('[data-country]').forEach(btn => {
                btn.addEventListener('click', (e) => {
                    e.preventDefault();
                    const country = btn.getAttribute('data-country');
                    console.log('🌍 국가 필터 변경:', country);
                    StockFilter.filterByCountry(country);
                });
            });
            
            // 시장 필터 버튼
            document.querySelectorAll('[data-market]').forEach(btn => {
                btn.addEventListener('click', (e) => {
                    e.preventDefault();
                    const market = btn.getAttribute('data-market');
                    console.log('📈 시장 필터 변경:', market);
                    StockFilter.filterByMarket(market);
                });
            });
            
            console.log('✅ 이벤트 리스너 바인딩 완료');
        },
        
        /**
         * 버튼 상태 업데이트
         */
        updateButtonStates: function() {
            console.log('🔄 버튼 상태 업데이트');
            
            // 모든 필터 버튼 초기화
            document.querySelectorAll('[data-country], [data-market]').forEach(btn => {
                btn.classList.remove('active', 'btn-primary');
                btn.classList.add('btn-outline-primary');
            });
            
            // 현재 선택된 국가 버튼 활성화
            if (StockFilter.currentCountry && StockFilter.currentCountry !== 'ALL') {
                const countryBtn = document.querySelector('[data-country="' + StockFilter.currentCountry + '"]');
                if (countryBtn) {
                    countryBtn.classList.remove('btn-outline-primary');
                    countryBtn.classList.add('active', 'btn-primary');
                    console.log('✅ 국가 버튼 활성화:', StockFilter.currentCountry);
                }
            }
            
            // 현재 선택된 시장 버튼 활성화
            if (StockFilter.currentMarket && StockFilter.currentMarket !== 'ALL') {
                const marketBtn = document.querySelector('[data-market="' + StockFilter.currentMarket + '"]');
                if (marketBtn) {
                    marketBtn.classList.remove('btn-outline-primary');
                    marketBtn.classList.add('active', 'btn-primary');
                    console.log('✅ 시장 버튼 활성화:', StockFilter.currentMarket);
                }
            }
        },
        
        /**
         * 국가별 필터링
         */
        filterByCountry: function(country) {
            StockFilter.currentCountry = country;
            StockFilter.currentMarket = 'ALL';  // 시장 필터 초기화
            
            // URL 생성
            const url = country === 'ALL' 
                ? StockFilter.contextPath + '/stock/list'
                : StockFilter.contextPath + '/stock/list?country=' + country;
            
            console.log('🔗 이동:', url);
            window.location.href = url;
        },
        
        /**
         * 시장별 필터링
         * ✅ 시장 선택 시 국가 자동 연계
         *    KOSPI/KOSDAQ → 한국(KR)
         *    NASDAQ/NYSE  → 미국(US)
         *    ALL          → 기존 국가 유지
         */
        filterByMarket: function(market) {
            StockFilter.currentMarket = market;

            // ✅ 시장에 따라 국가 자동 연계
            if (market === 'KOSPI' || market === 'KOSDAQ') {
                StockFilter.currentCountry = 'KR';
                console.log('🇰🇷 한국 시장 선택 → 국가 KR 자동 설정');
            } else if (market === 'NASDAQ' || market === 'NYSE') {
                StockFilter.currentCountry = 'US';
                console.log('🇺🇸 미국 시장 선택 → 국가 US 자동 설정');
            } else {
                // 전체(ALL) 시장: 국가가 없으면 전체로 초기화
                if (!StockFilter.currentCountry) {
                    StockFilter.currentCountry = 'ALL';
                }
            }

            // URL 생성
            var url;
            if (market === 'ALL') {
                url = StockFilter.currentCountry && StockFilter.currentCountry !== 'ALL'
                    ? StockFilter.contextPath + '/stock/list?country=' + StockFilter.currentCountry
                    : StockFilter.contextPath + '/stock/list';
            } else {
                url = StockFilter.contextPath + '/stock/list?country=' +
                      StockFilter.currentCountry + '&market=' + market;
            }

            console.log('🔗 이동:', url);
            window.location.href = url;
        }
    };
    
    /**
     * 관심종목 추가
     * ✅ credentials: 'same-origin' 추가 — 세션 쿠키 전송 필수
     * ✅ 401 처리 — 미로그인 시 로그인 페이지로 이동
     */
    function addToWatchlist(stockCode) {
        console.log('💖 관심종목 추가:', stockCode);

        fetch(StockFilter.contextPath + '/watchlist/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            credentials: 'same-origin',   // ✅ 세션 쿠키 반드시 포함
            body: 'stockCode=' + encodeURIComponent(stockCode)
        })
        .then(response => {
            // ✅ 401: 로그인 세션 없음 → 로그인 페이지로 이동
            if (response.status === 401) {
                alert('로그인이 필요합니다.');
                window.location.href = StockFilter.contextPath + '/member/login';
                return null;
            }
            return response.json();
        })
        .then(data => {
            if (!data) return;
            if (data.success) {
                alert('💖 관심종목에 추가되었습니다!');
            } else {
                // 이미 등록된 종목 등 서버 메시지 표시
                alert(data.message || '관심종목 추가에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('❌ 관심종목 추가 오류:', error);
            alert('관심종목 추가 중 오류가 발생했습니다.');
        });
    }
    
    // ✅ 페이지 로드 시 초기화
    document.addEventListener('DOMContentLoaded', function() {
        console.log('📄 Stock List 페이지 로드 완료');
        StockFilter.init();
    });
    </script>
    
       <div class="container">
        <h1>📊 주식 목록 (<c:out value="${not empty stocks ? stocks.size() : 0}"/>개 기업)</h1>
        <div class="search-box" style="display:flex;gap:6px;align-items:center;">
            <input type="text" id="searchInput" placeholder="종목명/코드 검색 (Enter → 검색 페이지)..."
                   style="flex:1;max-width:360px;"
                   onkeydown="if(event.key==='Enter'&&this.value.trim()){location.href='${pageContext.request.contextPath}/stock/search?keyword='+encodeURIComponent(this.value.trim());}"/>
            <button onclick="var v=document.getElementById('searchInput').value.trim();if(v){location.href='${pageContext.request.contextPath}/stock/search?keyword='+encodeURIComponent(v);}"
                    style="padding:8px 16px;background:#667eea;color:#fff;border:none;border-radius:5px;cursor:pointer;">
                🔍 검색
            </button>
        </div>
        <table id="stockTable">
            <thead>
                <tr>
                    <th>종목코드</th>
                    <th>종목명</th>
                    <th>시장</th>
                    <th>국가</th>
                    <th>업종</th>
                    <th>현재가</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${stocks}" var="stock">
                    <tr>
                        <td>${stock.stockCode}</td>
                        <td>${stock.stockName}</td>
                        <td>${stock.marketType}</td>
                        <td class="${stock.country == 'KR' ? 'kr' : 'us'}">${stock.country}</td>
                        <td>${stock.industry}</td>
                        <td>${stock.currentPrice}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty stocks}">
                    <tr><td colspan="6" style="text-align:center; padding:20px; color:#888;">
                        DB에 주식 데이터가 없습니다. 아래 PuTTy 명령으로 데이터를 삽입해 주세요.
                    </td></tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <script>
    // 테이블 검색 기능
    document.getElementById('searchInput').addEventListener('input', function() {
        const keyword = this.value.toLowerCase();
        document.querySelectorAll('#stockTable tbody tr').forEach(function(row) {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(keyword) ? '' : 'none';
        });
    });
    </script>
</body>
</html>
