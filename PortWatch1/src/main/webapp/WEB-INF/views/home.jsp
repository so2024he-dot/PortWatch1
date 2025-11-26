<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:include page="common/header.jsp" />

<!-- Hero Section -->
<div class="container-fluid bg-primary text-white py-5 mb-4">
    <div class="container text-center">
        <h1 class="display-4"><i class="bi bi-graph-up-arrow"></i> <spring:message code="app.name" /></h1>
        <p class="lead"><spring:message code="app.description" /></p>
        
        <!-- 빠른 검색 -->
        <div class="row justify-content-center mt-4">
            <div class="col-md-8 col-lg-6">
                <div class="input-group input-group-lg">
                    <input type="text" class="form-control" id="quickSearch" 
                           placeholder="종목명 또는 종목코드 검색">
                    <button class="btn btn-light" type="button" onclick="searchStock()">
                        <i class="bi bi-search"></i> 검색
                    </button>
                </div>
                <div id="autocompleteResults" class="list-group mt-2" style="display:none;"></div>
            </div>
        </div>
    </div>
</div>

<div class="container">
    <!-- 시장 현황 -->
    <div class="row mb-4">
        <div class="col-md-6 mb-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title"><i class="bi bi-graph-up"></i> KOSPI</h5>
                    <h2 id="kospiPrice" class="mb-0">-</h2>
                    <p id="kospiChange" class="mb-0">-</p>
                </div>
            </div>
        </div>
        <div class="col-md-6 mb-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title"><i class="bi bi-graph-up"></i> KOSDAQ</h5>
                    <h2 id="kosdaqPrice" class="mb-0">-</h2>
                    <p id="kosdaqChange" class="mb-0">-</p>
                </div>
            </div>
        </div>
    </div>
    
    <!-- 인기 종목 -->
    <div class="row mb-4">
        <div class="col-md-6 mb-3">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-fire"></i> 거래량 상위 TOP 5</h5>
                </div>
                <div class="card-body">
                    <ul id="topVolumeList" class="list-group list-group-flush">
                        <li class="list-group-item">로딩 중...</li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="col-md-6 mb-3">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-arrow-up-circle"></i> 급등 종목 TOP 5</h5>
                </div>
                <div class="card-body">
                    <ul id="topGainersList" class="list-group list-group-flush">
                        <li class="list-group-item">로딩 중...</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    
    <!-- 빠른 바로가기 -->
    <div class="row">
        <div class="col-md-3 col-sm-6 mb-3">
            <a href="${pageContext.request.contextPath}/stock/search" class="text-decoration-none">
                <div class="card text-center hover-shadow">
                    <div class="card-body">
                        <i class="bi bi-search display-4 text-primary"></i>
                        <h5 class="card-title mt-3">종목 검색</h5>
                    </div>
                </div>
            </a>
        </div>
        <c:if test="${not empty sessionScope.member}">
            <div class="col-md-3 col-sm-6 mb-3">
                <a href="${pageContext.request.contextPath}/portfolio/list" class="text-decoration-none">
                    <div class="card text-center hover-shadow">
                        <div class="card-body">
                            <i class="bi bi-briefcase display-4 text-success"></i>
                            <h5 class="card-title mt-3">포트폴리오</h5>
                        </div>
                    </div>
                </a>
            </div>
            <div class="col-md-3 col-sm-6 mb-3">
                <a href="${pageContext.request.contextPath}/watchlist/list" class="text-decoration-none">
                    <div class="card text-center hover-shadow">
                        <div class="card-body">
                            <i class="bi bi-star display-4 text-warning"></i>
                            <h5 class="card-title mt-3">관심 종목</h5>
                        </div>
                    </div>
                </a>
            </div>
        </c:if>
        <div class="col-md-3 col-sm-6 mb-3">
            <a href="${pageContext.request.contextPath}/market/index" class="text-decoration-none">
                <div class="card text-center hover-shadow">
                    <div class="card-body">
                        <i class="bi bi-graph-up-arrow display-4 text-info"></i>
                        <h5 class="card-title mt-3">시장 정보</h5>
                    </div>
                </div>
            </a>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
    loadMarketData();
    loadTopStocks();
    
    // 10초마다 자동 갱신
    setInterval(function() {
        loadMarketData();
        loadTopStocks();
    }, 10000);
});

function loadMarketData() {
    $.get('${pageContext.request.contextPath}/api/market/indices', function(response) {
        if (response.success) {
            $('#kospiPrice').text(response.kospi.value);
            $('#kospiChange').text(response.kospi.change + ' (' + response.kospi.changeRate + ')');
            
            $('#kosdaqPrice').text(response.kosdaq.value);
            $('#kosdaqChange').text(response.kosdaq.change + ' (' + response.kosdaq.changeRate + ')');
        }
    });
}

function loadTopStocks() {
    $.get('${pageContext.request.contextPath}/api/market/top-volume?limit=5', function(response) {
        if (response.success && response.data) {
            let html = '';
            response.data.forEach(function(stock) {
                html += '<li class="list-group-item d-flex justify-content-between align-items-center">';
                html += '<span>' + stock.stockName + '</span>';
                html += '<span class="badge bg-primary">' + (stock.volume || 0).toLocaleString() + '</span>';
                html += '</li>';
            });
            $('#topVolumeList').html(html);
        }
    });
    
    $.get('${pageContext.request.contextPath}/api/market/top-gainers?limit=5', function(response) {
        if (response.success && response.data) {
            let html = '';
            response.data.forEach(function(stock) {
                html += '<li class="list-group-item d-flex justify-content-between align-items-center">';
                html += '<span>' + stock.stockName + '</span>';
                html += '<span class="badge bg-danger">+' + (stock.changeRate || 0) + '%</span>';
                html += '</li>';
            });
            $('#topGainersList').html(html);
        }
    });
}

function searchStock() {
    const keyword = $('#quickSearch').val();
    if (keyword.trim()) {
        location.href = '${pageContext.request.contextPath}/stock/search?keyword=' + encodeURIComponent(keyword);
    }
}

// Enter 키 검색
$('#quickSearch').keypress(function(e) {
    if (e.which == 13) {
        searchStock();
    }
});
</script>

<jsp:include page="common/footer.jsp" />
