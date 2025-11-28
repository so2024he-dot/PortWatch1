<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../common/header.jsp" />

<style>
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
    }
    
    .market-tabs .nav-link {
        border-radius: 10px;
        padding: 0.75rem 1.5rem;
        font-weight: 600;
        color: #6b7280;
        transition: all 0.3s;
    }
    
    .market-tabs .nav-link.active {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
    }
    
    .stock-card {
        background: white;
        border-radius: 16px;
        padding: 1.5rem;
        box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
        transition: all 0.3s;
        cursor: pointer;
        margin-bottom: 1.5rem;
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
    }
    
    .stock-name {
        font-size: 1.25rem;
        font-weight: 700;
        color: #1f2937;
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
    }
    
    .price-change {
        font-size: 0.875rem;
        font-weight: 600;
    }
    
    .price-positive {
        color: #dc2626;
    }
    
    .price-negative {
        color: #2563eb;
    }
    
    .stock-info {
        display: flex;
        gap: 1.5rem;
        margin-top: 1rem;
        padding-top: 1rem;
        border-top: 1px solid #e5e7eb;
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
    }
    
    .market-badge {
        display: inline-block;
        padding: 0.25rem 0.75rem;
        border-radius: 6px;
        font-size: 0.75rem;
        font-weight: 600;
    }
    
    .badge-kospi {
        background: #dbeafe;
        color: #1e40af;
    }
    
    .badge-kosdaq {
        background: #fce7f3;
        color: #9f1239;
    }
    
    @media (max-width: 768px) {
        .page-header {
            padding: 1.5rem;
        }
        
        .page-title {
            font-size: 1.5rem;
        }
        
        .stock-card {
            padding: 1rem;
        }
        
        .stock-name {
            font-size: 1.1rem;
        }
        
        .current-price {
            font-size: 1.25rem;
        }
        
        .stock-info {
            flex-wrap: wrap;
            gap: 1rem;
        }
        
        .info-item {
            flex: 0 0 calc(50% - 0.5rem);
        }
    }
</style>

<!-- Page Header -->
<div class="page-header animate-fade-in">
    <h1 class="page-title">
        <i class="bi bi-graph-up me-2"></i>종목 목록
    </h1>
    <p class="text-muted mb-0 mt-2">
        총 <strong>${stockList.size()}</strong>개의 종목
    </p>
</div>

<!-- Market Tabs -->
<ul class="nav nav-pills market-tabs animate-fade-in" style="animation-delay: 0.1s;">
    <li class="nav-item">
        <a class="nav-link active" data-market="all" href="#" onclick="filterMarket('all'); return false;">
            <i class="bi bi-grid me-2"></i>전체
        </a>
    </li>
    <li class="nav-item">
        <a class="nav-link" data-market="KOSPI" href="#" onclick="filterMarket('KOSPI'); return false;">
            <i class="bi bi-bar-chart me-2"></i>KOSPI
        </a>
    </li>
    <li class="nav-item">
        <a class="nav-link" data-market="KOSDAQ" href="#" onclick="filterMarket('KOSDAQ'); return false;">
            <i class="bi bi-graph-up-arrow me-2"></i>KOSDAQ
        </a>
    </li>
</ul>

<!-- Stock List -->
<div class="row" id="stockList">
    <c:forEach var="stock" items="${stockList}" varStatus="status">
        <div class="col-lg-4 col-md-6 stock-item animate-fade-in" 
             data-market="${stock.market_type}" 
             style="animation-delay: ${status.index * 0.05}s;">
            <div class="stock-card">
                <div class="stock-header">
                    <div>
                        <div class="stock-name">${stock.stock_name}</div>
                        <div class="stock-code">${stock.stock_code}</div>
                        <span class="market-badge badge-${stock.market_type == 'KOSPI' ? 'kospi' : 'kosdaq'} mt-2">
                            ${stock.market_type}
                        </span>
                    </div>
                    <div class="stock-price">
                        <div class="current-price">
                            <fmt:formatNumber value="${stock.current_price}" type="number" pattern="#,##0" />
                        </div>
                        <div class="price-change price-${stock.price_change >= 0 ? 'positive' : 'negative'}">
                            <i class="bi bi-${stock.price_change >= 0 ? 'arrow-up' : 'arrow-down'}"></i>
                            ${stock.price_change >= 0 ? '+' : ''}
                            <fmt:formatNumber value="${stock.price_change_rate}" type="number" pattern="#,##0.00" />%
                        </div>
                    </div>
                </div>
                
                <div class="stock-info">
                    <div class="info-item">
                        <div class="info-label">업종</div>
                        <div class="info-value">${stock.industry}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">거래량</div>
                        <div class="info-value">
                            <fmt:formatNumber value="${stock.volume}" type="number" pattern="#,##0" />
                        </div>
                    </div>
                </div>
                
                <div class="mt-3">
                    <a href="${pageContext.request.contextPath}/portfolio/create?stockId=${stock.stock_id}" 
                       class="btn btn-sm btn-outline-primary w-100">
                        <i class="bi bi-plus-circle me-2"></i>포트폴리오에 추가
                    </a>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

<c:if test="${empty stockList}">
    <div class="empty-state text-center py-5">
        <i class="bi bi-inbox" style="font-size: 4rem; opacity: 0.3;"></i>
        <h5 class="mt-3">등록된 종목이 없습니다</h5>
    </div>
</c:if>

<script>
function filterMarket(market) {
    // Update active tab
    $('.market-tabs .nav-link').removeClass('active');
    $(`.market-tabs .nav-link[data-market="${market}"]`).addClass('active');
    
    // Filter stocks
    if (market === 'all') {
        $('.stock-item').show();
    } else {
        $('.stock-item').hide();
        $(`.stock-item[data-market="${market}"]`).show();
    }
}
</script>

<jsp:include page="../common/footer.jsp" />
