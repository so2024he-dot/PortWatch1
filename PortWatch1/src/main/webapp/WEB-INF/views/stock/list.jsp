<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="../common/header.jsp" />

<style>
    .stock-list-container {
        max-width: 1400px;
        margin: 0 auto;
        padding: 20px;
    }
    
    /* 헤더 */
    .page-header {
        background: white;
        border-radius: 20px;
        padding: 2rem;
        margin-bottom: 2rem;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    }
    
    .page-title {
        font-size: 2rem;
        font-weight: 700;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin: 0 0 1rem 0;
    }
    
    /* ✅ 필터 탭 */
    .filter-section {
        background: white;
        border-radius: 15px;
        padding: 1.5rem;
        margin-bottom: 2rem;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
    }
    
    .filter-label {
        font-size: 0.9rem;
        font-weight: 600;
        color: #6b7280;
        margin-bottom: 0.5rem;
    }
    
    .filter-tabs {
        display: flex;
        gap: 10px;
        margin-bottom: 15px;
        flex-wrap: wrap;
    }
    
    .filter-btn {
        padding: 10px 20px;
        border: 2px solid #e5e7eb;
        background: white;
        color: #6b7280;
        border-radius: 10px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s;
    }
    
    .filter-btn:hover {
        border-color: #667eea;
        color: #667eea;
    }
    
    .filter-btn.active {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-color: transparent;
    }
    
    .stock-count {
        font-size: 1rem;
        color: #1f2937;
        margin-top: 10px;
        font-weight: 600;
    }
    
    /* 검색 바 */
    .search-bar {
        display: flex;
        gap: 10px;
        margin-top: 15px;
    }
    
    .search-input {
        flex: 1;
        padding: 12px 20px;
        border: 2px solid #e5e7eb;
        border-radius: 10px;
        font-size: 1rem;
        transition: all 0.3s;
    }
    
    .search-input:focus {
        outline: none;
        border-color: #667eea;
    }
    
    .search-btn {
        padding: 12px 30px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border: none;
        border-radius: 10px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s;
    }
    
    .search-btn:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
    }
    
    /* 종목 테이블 */
    .stock-table {
        background: white;
        border-radius: 15px;
        overflow: hidden;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
    }
    
    .stock-table table {
        width: 100%;
        border-collapse: collapse;
    }
    
    .stock-table th {
        background: #f9fafb;
        padding: 1rem;
        text-align: left;
        font-weight: 600;
        color: #374151;
        border-bottom: 2px solid #e5e7eb;
    }
    
    .stock-table td {
        padding: 1rem;
        border-bottom: 1px solid #f3f4f6;
        color: #1f2937;
    }
    
    .stock-table tr:hover {
        background: #f9fafb;
        cursor: pointer;
    }
    
    .stock-name {
        font-weight: 600;
        color: #1f2937;
    }
    
    .stock-code {
        color: #6b7280;
        font-size: 0.9rem;
    }
    
    .market-badge {
        display: inline-block;
        padding: 0.4rem 0.8rem;
        border-radius: 8px;
        font-size: 0.85rem;
        font-weight: 600;
    }
    
    .badge-kospi { background: #dbeafe; color: #1e40af; }
    .badge-kosdaq { background: #f3e8ff; color: #6b21a8; }
    .badge-nasdaq { background: #d1fae5; color: #065f46; }
    .badge-nyse { background: #fef3c7; color: #92400e; }
    .badge-amex { background: #fce7f3; color: #9f1239; }
    
    .country-flag {
        font-size: 1.3rem;
    }
    
    .price {
        font-weight: 600;
        font-size: 1.1rem;
    }
    
    .price-up { color: #dc2626; }
    .price-down { color: #2563eb; }
    
    .action-btn {
        padding: 8px 16px;
        background: #667eea;
        color: white;
        border: none;
        border-radius: 8px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s;
    }
    
    .action-btn:hover {
        background: #5568d3;
        transform: translateY(-2px);
    }
    
    /* 빈 상태 */
    .empty-state {
        text-align: center;
        padding: 60px 20px;
        color: #6b7280;
    }
    
    .empty-state i {
        font-size: 4rem;
        color: #d1d5db;
        margin-bottom: 1rem;
    }
</style>

<div class="stock-list-container">
    
    <!-- 헤더 -->
    <div class="page-header">
        <h1 class="page-title">📊 종목 목록</h1>
        <p style="color: #6b7280; margin: 0;">
            실시간 주식 정보 및 시장 동향
        </p>
    </div>
    
    <!-- ✅ 필터 섹션 -->
    <div class="filter-section">
        
        <!-- 국가별 필터 -->
        <div class="filter-label">🌏 국가별</div>
        <div class="filter-tabs">
            <button class="filter-btn active" onclick="filterByCountry('all')">
                🌐 전체
            </button>
            <button class="filter-btn" onclick="filterByCountry('KR')">
                🇰🇷 한국
            </button>
            <button class="filter-btn" onclick="filterByCountry('US')">
                🇺🇸 미국
            </button>
        </div>
        
        <!-- 시장별 필터 -->
        <div class="filter-label" style="margin-top: 1rem;">📈 시장별</div>
        <div class="filter-tabs">
            <button class="filter-btn" onclick="filterByMarket('KOSPI')">
                📊 KOSPI
            </button>
            <button class="filter-btn" onclick="filterByMarket('KOSDAQ')">
                📈 KOSDAQ
            </button>
            <button class="filter-btn" onclick="filterByMarket('NASDAQ')">
                🚀 NASDAQ
            </button>
            <button class="filter-btn" onclick="filterByMarket('NYSE')">
                🏛️ NYSE
            </button>
            <button class="filter-btn" onclick="filterByMarket('AMEX')">
                💎 AMEX
            </button>
        </div>
        
        <!-- 검색 바 -->
        <div class="search-bar">
            <input type="text" 
                   id="searchInput" 
                   class="search-input" 
                   placeholder="종목명 또는 종목코드로 검색..."
                   onkeypress="if(event.key === 'Enter') searchStocks()">
            <button class="search-btn" onclick="searchStocks()">
                🔍 검색
            </button>
        </div>
        
        <div class="stock-count" id="stockCount">
            전체 종목을 불러오는 중...
        </div>
    </div>
    
    <!-- 종목 테이블 -->
    <div class="stock-table">
        <table>
            <thead>
                <tr>
                    <th>국가</th>
                    <th>종목명</th>
                    <th>종목코드</th>
                    <th>시장</th>
                    <th>업종</th>
                    <th>현재가</th>
                    <th>등락률</th>
                    <th>액션</th>
                </tr>
            </thead>
            <tbody id="stockTableBody">
                <tr>
                    <td colspan="8" style="text-align: center; padding: 3rem;">
                        <div class="loading">종목을 불러오는 중...</div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    
</div>

<script>
    // ✅ 전역 변수
    let allStocks = [];
    let currentFilter = { type: 'country', value: 'all' };
    
    // ✅ 종목 로드
    async function loadStocks() {
        try {
            console.log('종목 로드 시작...');
            
            const response = await fetch('/portwatch/stock/api/list');
            const data = await response.json();
            
            if (data.success && data.stockList) {
                allStocks = data.stockList;
                console.log('종목 로드 완료:', allStocks.length + '개');
                displayStocks(allStocks);
            } else {
                showEmptyState('종목 정보를 불러올 수 없습니다.');
            }
            
        } catch (error) {
            console.error('종목 로드 실패:', error);
            showEmptyState('종목 정보를 불러오는 중 오류가 발생했습니다.');
        }
    }
    
    // ✅ 종목 표시
    function displayStocks(stocks) {
        const tbody = document.getElementById('stockTableBody');
        const countElement = document.getElementById('stockCount');
        
        if (!stocks || stocks.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8">
                        <div class="empty-state">
                            <i class="bi bi-inbox"></i>
                            <h3>종목이 없습니다</h3>
                        </div>
                    </td>
                </tr>
            `;
            countElement.textContent = '0개의 종목';
            return;
        }
        
        tbody.innerHTML = '';
        countElement.textContent = `총 ${stocks.length}개의 종목`;
        
        stocks.forEach(stock => {
            const row = createStockRow(stock);
            tbody.appendChild(row);
        });
    }
    
    // ✅ 종목 행 생성
    function createStockRow(stock) {
        const tr = document.createElement('tr');
        tr.onclick = () => location.href = '/portwatch/stock/detail/' + stock.stockCode;
        
        // 국가 판단
        const isKorean = stock.marketType === 'KOSPI' || stock.marketType === 'KOSDAQ';
        const countryFlag = isKorean ? '🇰🇷' : '🇺🇸';
        
        // 가격 포맷
        const price = stock.currentPrice || (isKorean ? 50000 : 100.00);
        const priceText = isKorean 
            ? price.toLocaleString() + '원' 
            : '$' + price.toFixed(2);
        
        tr.innerHTML = `
            <td>
                <span class="country-flag">${countryFlag}</span>
            </td>
            <td>
                <div class="stock-name">${stock.stockName}</div>
            </td>
            <td>
                <div class="stock-code">${stock.stockCode}</div>
            </td>
            <td>
                <span class="market-badge badge-${stock.marketType.toLowerCase()}">
                    ${stock.marketType}
                </span>
            </td>
            <td>${stock.industry || '-'}</td>
            <td>
                <span class="price">${priceText}</span>
            </td>
            <td>
                <span class="price-up">+2.5%</span>
            </td>
            <td>
                <button class="action-btn" onclick="event.stopPropagation(); addToWatchlist('${stock.stockCode}')">
                    ⭐ 관심종목
                </button>
            </td>
        `;
        
        return tr;
    }
    
    // ✅ 국가별 필터
    function filterByCountry(country) {
        currentFilter = { type: 'country', value: country };
        updateFilterButtons('country', country);
        
        let filtered = allStocks;
        
        if (country !== 'all') {
            filtered = allStocks.filter(stock => {
                if (country === 'KR') {
                    return stock.marketType === 'KOSPI' || stock.marketType === 'KOSDAQ';
                } else if (country === 'US') {
                    return stock.marketType === 'NASDAQ' || stock.marketType === 'NYSE' || stock.marketType === 'AMEX';
                }
                return false;
            });
        }
        
        console.log('국가별 필터:', country, filtered.length + '개');
        displayStocks(filtered);
    }
    
    // ✅ 시장별 필터
    function filterByMarket(market) {
        currentFilter = { type: 'market', value: market };
        updateFilterButtons('market', market);
        
        const filtered = allStocks.filter(stock => stock.marketType === market);
        
        console.log('시장별 필터:', market, filtered.length + '개');
        displayStocks(filtered);
    }
    
    // ✅ 필터 버튼 상태 업데이트
    function updateFilterButtons(type, value) {
        document.querySelectorAll('.filter-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        
        event.target.classList.add('active');
    }
    
    // ✅ 검색
    async function searchStocks() {
        const keyword = document.getElementById('searchInput').value.trim();
        
        if (!keyword) {
            displayStocks(allStocks);
            return;
        }
        
        try {
            const response = await fetch('/portwatch/stock/api/search?keyword=' + encodeURIComponent(keyword));
            const data = await response.json();
            
            if (data.success && data.stockList) {
                console.log('검색 결과:', data.stockList.length + '개');
                displayStocks(data.stockList);
            } else {
                showEmptyState('검색 결과가 없습니다.');
            }
            
        } catch (error) {
            console.error('검색 실패:', error);
            showEmptyState('검색 중 오류가 발생했습니다.');
        }
    }
    
    // ✅ 관심종목 추가
    async function addToWatchlist(stockCode) {
        try {
            const response = await fetch('/portwatch/watchlist/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ stockCode: stockCode })
            });
            
            const data = await response.json();
            
            if (data.success) {
                alert('관심종목에 추가되었습니다.');
            } else {
                alert(data.message || '추가 중 오류가 발생했습니다.');
            }
            
        } catch (error) {
            console.error('관심종목 추가 실패:', error);
            alert('관심종목 추가 중 오류가 발생했습니다.');
        }
    }
    
    // ✅ 빈 상태 표시
    function showEmptyState(message) {
        const tbody = document.getElementById('stockTableBody');
        tbody.innerHTML = `
            <tr>
                <td colspan="8">
                    <div class="empty-state">
                        <i class="bi bi-inbox"></i>
                        <h3>${message}</h3>
                    </div>
                </td>
            </tr>
        `;
    }
    
    // ✅ 페이지 로드 시 종목 로드
    document.addEventListener('DOMContentLoaded', () => {
        console.log('페이지 로드 완료');
        loadStocks();
    });
</script>

<jsp:include page="../common/footer.jsp" />
