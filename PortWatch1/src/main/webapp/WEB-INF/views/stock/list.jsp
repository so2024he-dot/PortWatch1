<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
    
    /* 필터 섹션 */
    .filter-section {
        background: white;
        border-radius: 16px;
        padding: 1.5rem;
        margin-bottom: 2rem;
        box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
    }
    
    .filter-title {
        font-size: 1.1rem;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 1rem;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .filter-buttons {
        display: flex;
        flex-wrap: wrap;
        gap: 0.75rem;
    }
    
    .filter-btn {
        padding: 0.6rem 1.2rem;
        border: 2px solid #e5e7eb;
        background: white;
        border-radius: 10px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s;
        font-size: 0.95rem;
    }
    
    .filter-btn:hover {
        border-color: #667eea;
        color: #667eea;
        transform: translateY(-2px);
    }
    
    .filter-btn.active {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-color: transparent;
    }
    
    .filter-group {
        margin-bottom: 1.5rem;
    }
    
    .filter-group:last-child {
        margin-bottom: 0;
    }
    
    .filter-subtitle {
        font-size: 0.9rem;
        color: #6b7280;
        margin-bottom: 0.75rem;
        font-weight: 500;
    }
    
    /* 통계 카드 */
    .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1rem;
        margin-bottom: 2rem;
    }
    
    .stat-card {
        background: white;
        border-radius: 12px;
        padding: 1.25rem;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        transition: all 0.3s;
    }
    
    .stat-card:hover {
        transform: translateY(-3px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
    
    .stat-label {
        font-size: 0.85rem;
        color: #6b7280;
        margin-bottom: 0.5rem;
    }
    
    .stat-value {
        font-size: 1.5rem;
        font-weight: 700;
        color: #1f2937;
    }
    
    .stat-icon {
        float: right;
        font-size: 2rem;
        opacity: 0.2;
        color: #667eea;
    }
    
    /* 종목 테이블 */
    .stock-table-container {
        background: white;
        border-radius: 16px;
        padding: 1.5rem;
        box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
    }
    
    .table-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1.5rem;
    }
    
    .table-title {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1f2937;
    }
    
    .search-box {
        display: flex;
        gap: 0.5rem;
    }
    
    .search-input {
        padding: 0.6rem 1rem;
        border: 2px solid #e5e7eb;
        border-radius: 10px;
        width: 300px;
        font-size: 0.95rem;
    }
    
    .search-input:focus {
        outline: none;
        border-color: #667eea;
    }
    
    .btn-search {
        padding: 0.6rem 1.5rem;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border: none;
        border-radius: 10px;
        font-weight: 600;
        cursor: pointer;
    }
    
    .stock-table {
        width: 100%;
        border-collapse: separate;
        border-spacing: 0;
    }
    
    .stock-table thead {
        background: #f9fafb;
    }
    
    .stock-table th {
        padding: 1rem;
        text-align: left;
        font-weight: 600;
        color: #6b7280;
        font-size: 0.875rem;
        text-transform: uppercase;
        letter-spacing: 0.05em;
        border-bottom: 2px solid #e5e7eb;
    }
    
    .stock-table td {
        padding: 1rem;
        border-bottom: 1px solid #f3f4f6;
    }
    
    .stock-table tbody tr {
        transition: all 0.2s;
        cursor: pointer;
    }
    
    .stock-table tbody tr:hover {
        background: #f9fafb;
    }
    
    .stock-name {
        font-weight: 600;
        color: #1f2937;
    }
    
    .stock-code {
        font-size: 0.875rem;
        color: #6b7280;
        display: block;
        margin-top: 0.25rem;
    }
    
    .badge {
        display: inline-block;
        padding: 0.25rem 0.75rem;
        border-radius: 0.375rem;
        font-size: 0.75rem;
        font-weight: 600;
    }
    
    .badge-kospi {
        background: #dbeafe;
        color: #1e40af;
    }
    
    .badge-kosdaq {
        background: #f3e8ff;
        color: #6b21a8;
    }
    
    .badge-nasdaq {
        background: #d1fae5;
        color: #065f46;
    }
    
    .badge-nyse {
        background: #fef3c7;
        color: #92400e;
    }
    
    .badge-kr {
        background: #dbeafe;
        color: #1e40af;
    }
    
    .badge-us {
        background: #fecaca;
        color: #991b1b;
    }
    
    .price-up {
        color: #dc2626;
        font-weight: 600;
    }
    
    .price-down {
        color: #2563eb;
        font-weight: 600;
    }
    
    .empty-state {
        text-align: center;
        padding: 4rem 2rem;
        color: #6b7280;
    }
    
    .empty-state i {
        font-size: 4rem;
        opacity: 0.3;
        margin-bottom: 1rem;
    }
</style>

<!-- Page Header -->
<div class="page-header">
    <h1 class="page-title">
        <i class="bi bi-graph-up-arrow me-2"></i>종목 검색
    </h1>
    <p class="mt-2 text-muted">한국/미국 주식 시장의 종목을 검색하고 분석하세요</p>
</div>

<!-- 필터 섹션 -->
<div class="filter-section">
    <div class="filter-group">
        <div class="filter-title">
            <i class="bi bi-funnel"></i> 국가별 필터
        </div>
        <div class="filter-buttons">
            <button class="filter-btn active" data-filter="country" data-value="ALL" onclick="filterByCountry('ALL', this)">
                <i class="bi bi-globe me-1"></i> 전체
            </button>
            <button class="filter-btn" data-filter="country" data-value="KR" onclick="filterByCountry('KR', this)">
                <i class="bi bi-flag me-1"></i> 한국
            </button>
            <button class="filter-btn" data-filter="country" data-value="US" onclick="filterByCountry('US', this)">
                <i class="bi bi-flag-fill me-1"></i> 미국
            </button>
        </div>
    </div>
    
    <div class="filter-group">
        <div class="filter-title">
            <i class="bi bi-building"></i> 시장별 필터
        </div>
        <div class="filter-buttons">
            <button class="filter-btn active" data-filter="market" data-value="ALL" onclick="filterByMarket('ALL', this)">
                전체
            </button>
            <button class="filter-btn" data-filter="market" data-value="KOSPI" onclick="filterByMarket('KOSPI', this)">
                KOSPI
            </button>
            <button class="filter-btn" data-filter="market" data-value="KOSDAQ" onclick="filterByMarket('KOSDAQ', this)">
                KOSDAQ
            </button>
            <button class="filter-btn" data-filter="market" data-value="NASDAQ" onclick="filterByMarket('NASDAQ', this)">
                NASDAQ
            </button>
            <button class="filter-btn" data-filter="market" data-value="NYSE" onclick="filterByMarket('NYSE', this)">
                NYSE
            </button>
        </div>
    </div>
</div>

<!-- 통계 카드 -->
<div class="stats-grid">
    <div class="stat-card">
        <i class="bi bi-graph-up stat-icon"></i>
        <div class="stat-label">총 종목 수</div>
        <div class="stat-value" id="totalCount">${fn:length(stockList)}</div>
    </div>
    <div class="stat-card">
        <i class="bi bi-flag stat-icon"></i>
        <div class="stat-label">한국 종목</div>
        <div class="stat-value" id="krCount">-</div>
    </div>
    <div class="stat-card">
        <i class="bi bi-flag-fill stat-icon"></i>
        <div class="stat-label">미국 종목</div>
        <div class="stat-value" id="usCount">-</div>
    </div>
    <div class="stat-card">
        <i class="bi bi-eye stat-icon"></i>
        <div class="stat-label">현재 표시</div>
        <div class="stat-value" id="visibleCount">${fn:length(stockList)}</div>
    </div>
</div>

<!-- 종목 테이블 -->
<div class="stock-table-container">
    <div class="table-header">
        <h5 class="table-title">
            <i class="bi bi-table me-2"></i>종목 목록
        </h5>
        <div class="search-box">
            <input type="text" class="search-input" id="searchInput" 
                   placeholder="종목명 또는 종목코드로 검색..." 
                   onkeyup="searchStock(event)">
            <button class="btn-search" onclick="searchStock()">
                <i class="bi bi-search me-1"></i>검색
            </button>
        </div>
    </div>
    
    <div class="table-responsive">
        <table class="stock-table">
            <thead>
                <tr>
                    <th>종목명</th>
                    <th>시장</th>
                    <th>국가</th>
                    <th>산업</th>
                    <th>현재가</th>
                    <th>등락률</th>
                </tr>
            </thead>
            <tbody id="stockTableBody">
                <c:forEach items="${stockList}" var="stock">
                    <tr class="stock-row" 
                        data-country="${stock.country}" 
                        data-market="${stock.marketType}"
                        data-name="${fn:toLowerCase(stock.stockName)}"
                        data-code="${fn:toLowerCase(stock.stockCode)}"
                        onclick="location.href='${pageContext.request.contextPath}/stock/detail/${stock.stockCode}'">
                        <td>
                            <span class="stock-name">${stock.stockName}</span>
                            <span class="stock-code">${stock.stockCode}</span>
                        </td>
                        <td>
                            <span class="badge badge-${fn:toLowerCase(stock.marketType)}">
                                ${stock.marketType}
                            </span>
                        </td>
                        <td>
                            <span class="badge badge-${fn:toLowerCase(stock.country)}">
                                ${stock.country == 'KR' ? '한국' : '미국'}
                            </span>
                        </td>
                        <td>${stock.industry}</td>
                        <td>
                            <fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0" />원
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${stock.changeRate >= 0}">
                                    <span class="price-up">
                                        <i class="bi bi-arrow-up"></i>
                                        <fmt:formatNumber value="${stock.changeRate}" pattern="+#,##0.00" />%
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="price-down">
                                        <i class="bi bi-arrow-down"></i>
                                        <fmt:formatNumber value="${stock.changeRate}" pattern="#,##0.00" />%
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    
    <div id="emptyState" class="empty-state" style="display: none;">
        <i class="bi bi-inbox"></i>
        <h4>검색 결과가 없습니다</h4>
        <p>다른 검색어나 필터를 시도해보세요</p>
    </div>
</div>

<script>
    let currentCountryFilter = 'ALL';
    let currentMarketFilter = 'ALL';
    let currentSearchText = '';
    
    // 페이지 로드 시 통계 업데이트
    window.addEventListener('load', () => {
        updateStats();
    });
    
    // 국가별 필터
    function filterByCountry(country, btn) {
        currentCountryFilter = country;
        
        // 버튼 활성화 상태 변경
        document.querySelectorAll('[data-filter="country"]').forEach(b => {
            b.classList.remove('active');
        });
        btn.classList.add('active');
        
        applyFilters();
    }
    
    // 시장별 필터
    function filterByMarket(market, btn) {
        currentMarketFilter = market;
        
        // 버튼 활성화 상태 변경
        document.querySelectorAll('[data-filter="market"]').forEach(b => {
            b.classList.remove('active');
        });
        btn.classList.add('active');
        
        applyFilters();
    }
    
    // 검색
    function searchStock(event) {
        if (event && event.key !== 'Enter') {
            return;
        }
        
        currentSearchText = document.getElementById('searchInput').value.toLowerCase().trim();
        applyFilters();
    }
    
    // 필터 적용
    function applyFilters() {
        const rows = document.querySelectorAll('.stock-row');
        let visibleCount = 0;
        
        rows.forEach(row => {
            const country = row.dataset.country;
            const market = row.dataset.market;
            const name = row.dataset.name;
            const code = row.dataset.code;
            
            let showCountry = (currentCountryFilter === 'ALL' || country === currentCountryFilter);
            let showMarket = (currentMarketFilter === 'ALL' || market === currentMarketFilter);
            let showSearch = (currentSearchText === '' || name.includes(currentSearchText) || code.includes(currentSearchText));
            
            if (showCountry && showMarket && showSearch) {
                row.style.display = '';
                visibleCount++;
            } else {
                row.style.display = 'none';
            }
        });
        
        // 결과 없음 표시
        const emptyState = document.getElementById('emptyState');
        const tableBody = document.getElementById('stockTableBody');
        
        if (visibleCount === 0) {
            emptyState.style.display = 'block';
            tableBody.style.display = 'none';
        } else {
            emptyState.style.display = 'none';
            tableBody.style.display = '';
        }
        
        // 통계 업데이트
        document.getElementById('visibleCount').textContent = visibleCount;
    }
    
    // 통계 업데이트
    function updateStats() {
        const rows = document.querySelectorAll('.stock-row');
        let krCount = 0;
        let usCount = 0;
        
        rows.forEach(row => {
            if (row.dataset.country === 'KR') {
                krCount++;
            } else if (row.dataset.country === 'US') {
                usCount++;
            }
        });
        
        document.getElementById('krCount').textContent = krCount;
        document.getElementById('usCount').textContent = usCount;
        document.getElementById('totalCount').textContent = rows.length;
        document.getElementById('visibleCount').textContent = rows.length;
    }
</script>

<jsp:include page="../common/footer.jsp" />
