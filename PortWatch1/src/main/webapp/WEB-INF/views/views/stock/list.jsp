<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../common/header.jsp" />

<style>
    .container {
        max-width: 1400px;
        margin: 0 auto;
        padding: 2rem;
    }
    
    .page-header {
        background: white;
        border-radius: 16px;
        padding: 2rem;
        margin-bottom: 2rem;
        box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
    }
    
    .page-title {
        font-size: 2rem;
        font-weight: 700;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin: 0;
    }
    
    .market-tabs {
        margin-bottom: 2rem;
        display: flex;
        gap: 1rem;
    }
    
    .market-tabs .nav-link {
        border-radius: 10px;
        padding: 0.75rem 1.5rem;
        font-weight: 600;
        color: #6b7280;
        transition: all 0.3s;
        border: none;
        background: white;
        cursor: pointer;
        box-shadow: 0 2px 4px rgb(0 0 0 / 0.1);
    }
    
    .market-tabs .nav-link.active {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
    }
    
    .market-tabs .nav-link:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 6px rgb(0 0 0 / 0.1);
    }
    
    .stock-card {
        background: white;
        border-radius: 16px;
        padding: 1.5rem;
        box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
        transition: all 0.3s;
        cursor: pointer;
        margin-bottom: 1.5rem;
        height: 100%;
        min-height: 200px;
    }
    
    .stock-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1);
    }
    
    .stock-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 1rem;
        padding-bottom: 1rem;
        border-bottom: 2px solid #f3f4f6;
    }
    
    .stock-name {
        font-size: 1.25rem;
        font-weight: 700;
        color: #1f2937;
        margin-bottom: 0.25rem;
    }
    
    .stock-code {
        font-size: 0.875rem;
        color: #6b7280;
    }
    
    .stock-price {
        text-align: right;
    }
    
    .current-price {
        font-size: 1.5rem;
        font-weight: 700;
        color: #6b7280;
    }
    
    .stock-info {
        display: flex;
        gap: 1.5rem;
        margin-top: 1rem;
    }
    
    .info-item {
        flex: 1;
    }
    
    .info-label {
        font-size: 0.75rem;
        color: #6b7280;
        margin-bottom: 0.25rem;
    }
    
    .info-value {
        font-weight: 600;
        color: #1f2937;
        font-size: 0.875rem;
    }
    
    .market-badge {
        display: inline-block;
        padding: 0.25rem 0.75rem;
        border-radius: 6px;
        font-size: 0.75rem;
        font-weight: 600;
        margin-top: 0.5rem;
    }
    
    .badge-kospi {
        background: #dbeafe;
        color: #1e40af;
    }
    
    .badge-kosdaq {
        background: #fce7f3;
        color: #9f1239;
    }
    
    .empty-state {
        text-align: center;
        padding: 4rem 2rem;
        background: white;
        border-radius: 16px;
        color: #6b7280;
    }
    
    .row {
        display: flex;
        flex-wrap: wrap;
        margin: 0 -0.75rem;
    }
    
    .col-lg-4 {
        width: 33.333%;
        padding: 0 0.75rem;
        margin-bottom: 1.5rem;
    }
    
    .col-md-6 {
        width: 50%;
    }
    
    @media (max-width: 992px) {
        .col-lg-4 {
            width: 50%;
        }
    }
    
    @media (max-width: 768px) {
        .col-lg-4, .col-md-6 {
            width: 100%;
        }
        
        .stock-name {
            font-size: 1.1rem;
        }
        
        .current-price {
            font-size: 1.25rem;
        }
    }
</style>

<div class="container">
    <!-- Page Header -->
    <div class="page-header">
        <h1 class="page-title">
            <i class="bi bi-graph-up me-2"></i>종목 목록
        </h1>
        <p class="text-muted mb-0 mt-2">
            <c:choose>
                <c:when test="${not empty stockList}">
                    총 <strong>${stockList.size()}</strong>개의 종목
                </c:when>
                <c:otherwise>
                    등록된 종목이 없습니다
                </c:otherwise>
            </c:choose>
        </p>
    </div>

    <!-- Market Tabs -->
    <div class="market-tabs">
        <button class="nav-link active" data-market="all" onclick="filterMarket('all')">
            <i class="bi bi-grid me-2"></i>전체
        </button>
        <button class="nav-link" data-market="KOSPI" onclick="filterMarket('KOSPI')">
            <i class="bi bi-bar-chart me-2"></i>KOSPI
        </button>
        <button class="nav-link" data-market="KOSDAQ" onclick="filterMarket('KOSDAQ')">
            <i class="bi bi-graph-up-arrow me-2"></i>KOSDAQ
        </button>
    </div>

    <!-- Stock List -->
    <div class="row" id="stockList">
        <c:choose>
            <c:when test="${not empty stockList}">
                <c:forEach var="stock" items="${stockList}">
                    <div class="col-lg-4 col-md-6 stock-item" data-market="${stock.marketType}">
                        <div class="stock-card" onclick="location.href='${pageContext.request.contextPath}/stock/detail/${stock.stockCode}'">
                            <div class="stock-header">
                                <div>
                                    <div class="stock-name">${stock.stockName}</div>
                                    <div class="stock-code">${stock.stockCode}</div>
                                    <span class="market-badge badge-${stock.marketType == 'KOSPI' ? 'kospi' : 'kosdaq'}">
                                        ${stock.marketType}
                                    </span>
                                </div>
                                <div class="stock-price">
                                    <div class="current-price">-</div>
                                </div>
                            </div>
                            
                            <div class="stock-info">
                                <div class="info-item">
                                    <div class="info-label">업종</div>
                                    <div class="info-value">
                                        <c:choose>
                                            <c:when test="${not empty stock.industry}">
                                                ${stock.industry}
                                            </c:when>
                                            <c:otherwise>
                                                -
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">코드</div>
                                    <div class="info-value">${stock.stockCode}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="col-12">
                    <div class="empty-state">
                        <i class="bi bi-inbox" style="font-size: 4rem; opacity: 0.3;"></i>
                        <h5 class="mt-3">등록된 종목이 없습니다</h5>
                        <p class="text-muted mt-2">MySQL에서 STOCK 테이블에 데이터를 추가해주세요.</p>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-3">
                            <i class="bi bi-house me-2"></i>홈으로 돌아가기
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    
    <!--MySQL 주식 종목 추가 코드  -->
	<table>
	    <tr>
	        <th>날짜</th><th>시가</th><th>고가</th><th>저가</th><th>종가</th><th>거래량</th>
	    </tr>
	    <c:forEach var="p" items="${prices}">
	        <tr>
	            <td>${p.tradeDate}</td>
	            <td>${p.openPrice}</td>
	            <td>${p.highPrice}</td>
	            <td>${p.lowPrice}</td>
	            <td>${p.closePrice}</td>
	            <td>${p.volume}</td>
	        </tr>
	    </c:forEach>
	</table>
  <!--MySQL 주식 종목 추가 코드  -->

    <c:if test="${not empty error}">
        <div class="alert alert-danger mt-3" role="alert">
            ${error}
        </div>
    </c:if>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
function filterMarket(market) {
    console.log('필터링:', market);
    
    // 탭 활성화
    $('.market-tabs .nav-link').removeClass('active');
    $(`.market-tabs .nav-link[data-market="${market}"]`).addClass('active');
    
    // 종목 필터링
    if (market === 'all') {
        $('.stock-item').fadeIn(300);
    } else {
        $('.stock-item').fadeOut(300);
        $(`.stock-item[data-market="${market}"]`).fadeIn(300);
    }
    
    // 개수 출력
    setTimeout(function() {
        var visibleCount = $('.stock-item:visible').length;
        console.log('표시된 종목:', visibleCount + '개');
    }, 350);
}

// 페이지 로드 시
$(document).ready(function() {
    var totalStocks = $('.stock-item').length;
    var kospiCount = $('.stock-item[data-market="KOSPI"]').length;
    var kosdaqCount = $('.stock-item[data-market="KOSDAQ"]').length;
    
    console.log('=== 종목 데이터 로드 완료 ===');
    console.log('총 종목:', totalStocks + '개');
    console.log('KOSPI:', kospiCount + '개');
    console.log('KOSDAQ:', kosdaqCount + '개');
    
    if (totalStocks === 0) {
        console.warn('⚠️ 종목 데이터가 없습니다.');
        console.warn('MySQL에서 다음 SQL을 실행하세요:');
        console.warn('INSERT INTO STOCK (stock_code, stock_name, market_type, industry) VALUES ...');
    } else {
        console.log('✅ 종목 데이터 정상 로드');
    }
});
</script>

<jsp:include page="../common/footer.jsp" />

    
