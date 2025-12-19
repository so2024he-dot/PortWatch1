<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="../common/header.jsp" />

<!-- Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

<style>
    .portfolio-container {
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
        margin: 0;
    }
    
    /* ✅ 필터 탭 */
    .filter-section {
        background: white;
        border-radius: 15px;
        padding: 1.5rem;
        margin-bottom: 2rem;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
    }
    
    .filter-tabs {
        display: flex;
        gap: 10px;
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
    
    /* 요약 카드 */
    .summary-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 1.5rem;
        margin-bottom: 2rem;
    }
    
    .summary-card {
        background: white;
        border-radius: 15px;
        padding: 1.5rem;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
    }
    
    .summary-label {
        font-size: 0.9rem;
        color: #6b7280;
        margin-bottom: 0.5rem;
    }
    
    .summary-value {
        font-size: 2rem;
        font-weight: 700;
        color: #1f2937;
    }
    
    .summary-value.positive {
        color: #dc2626;
    }
    
    .summary-value.negative {
        color: #2563eb;
    }
    
    /* 포트폴리오 테이블 */
    .portfolio-table {
        background: white;
        border-radius: 15px;
        overflow: hidden;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
    }
    
    .portfolio-table table {
        width: 100%;
        border-collapse: collapse;
    }
    
    .portfolio-table th {
        background: #f9fafb;
        padding: 1rem;
        text-align: left;
        font-weight: 600;
        color: #374151;
        border-bottom: 2px solid #e5e7eb;
    }
    
    .portfolio-table td {
        padding: 1rem;
        border-bottom: 1px solid #f3f4f6;
        color: #1f2937;
    }
    
    .portfolio-table tr:hover {
        background: #f9fafb;
    }
    
    .stock-info {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .country-flag {
        font-size: 1.3rem;
    }
    
    .stock-name {
        font-weight: 600;
    }
    
    .stock-code {
        color: #6b7280;
        font-size: 0.9rem;
    }
    
    .quantity {
        font-weight: 600;
    }
    
    .price {
        font-weight: 600;
    }
    
    .profit-positive {
        color: #dc2626;
        font-weight: 600;
    }
    
    .profit-negative {
        color: #2563eb;
        font-weight: 600;
    }
    
    .action-btns {
        display: flex;
        gap: 0.5rem;
    }
    
    .action-btn {
        padding: 6px 12px;
        border: none;
        border-radius: 8px;
        font-weight: 600;
        font-size: 0.85rem;
        cursor: pointer;
        transition: all 0.3s;
    }
    
    .btn-edit {
        background: #667eea;
        color: white;
    }
    
    .btn-delete {
        background: #ef4444;
        color: white;
    }
    
    .action-btn:hover {
        transform: translateY(-2px);
    }
    
    /* 추가 버튼 */
    .add-btn {
        position: fixed;
        bottom: 30px;
        right: 30px;
        width: 60px;
        height: 60px;
        border-radius: 50%;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border: none;
        font-size: 2rem;
        cursor: pointer;
        box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
        transition: all 0.3s;
    }
    
    .add-btn:hover {
        transform: translateY(-3px);
        box-shadow: 0 12px 30px rgba(102, 126, 234, 0.6);
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

<div class="portfolio-container">
    
    <!-- 헤더 -->
    <div class="page-header">
        <h1 class="page-title">💼 내 포트폴리오</h1>
        <p style="color: #6b7280; margin: 0.5rem 0 0 0;">
            보유 종목 관리 및 수익률 분석
        </p>
    </div>
    
    <!-- ✅ 필터 섹션 -->
    <div class="filter-section">
        <div class="filter-tabs">
            <button class="filter-btn active" onclick="filterPortfolio('all')">
                🌐 전체
            </button>
            <button class="filter-btn" onclick="filterPortfolio('KR')">
                🇰🇷 한국
            </button>
            <button class="filter-btn" onclick="filterPortfolio('US')">
                🇺🇸 미국
            </button>
            <button class="filter-btn" onclick="filterPortfolio('KOSPI')">
                📊 KOSPI
            </button>
            <button class="filter-btn" onclick="filterPortfolio('KOSDAQ')">
                📈 KOSDAQ
            </button>
            <button class="filter-btn" onclick="filterPortfolio('NASDAQ')">
                🚀 NASDAQ
            </button>
            <button class="filter-btn" onclick="filterPortfolio('NYSE')">
                🏛️ NYSE
            </button>
        </div>
    </div>
    
    <!-- 요약 통계 -->
    <div class="summary-grid">
        <div class="summary-card">
            <div class="summary-label">📊 보유 종목 수</div>
            <div class="summary-value" id="stockCount">-</div>
        </div>
        <div class="summary-card">
            <div class="summary-label">💰 총 투자금액</div>
            <div class="summary-value" id="totalInvestment">-</div>
        </div>
        <div class="summary-card">
            <div class="summary-label">📈 총 평가금액</div>
            <div class="summary-value" id="totalValue">-</div>
        </div>
        <div class="summary-card">
            <div class="summary-label">💵 총 손익</div>
            <div class="summary-value" id="totalProfit">-</div>
        </div>
    </div>
    
    <!-- 포트폴리오 테이블 -->
    <div class="portfolio-table">
        <table>
            <thead>
                <tr>
                    <th>종목</th>
                    <th>수량</th>
                    <th>평균 매입가</th>
                    <th>현재가</th>
                    <th>평가금액</th>
                    <th>손익</th>
                    <th>수익률</th>
                    <th>액션</th>
                </tr>
            </thead>
            <tbody id="portfolioTableBody">
                <tr>
                    <td colspan="8" style="text-align: center; padding: 3rem;">
                        포트폴리오를 불러오는 중...
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    
</div>

<!-- ✅ 추가 버튼 -->
<button class="add-btn" onclick="location.href='/portwatch/portfolio/create'">
    +
</button>

<script>
    // ✅ 전역 변수
    let allPortfolio = [];
    let currentFilter = 'all';
    
    // ✅ 포트폴리오 로드
    async function loadPortfolio() {
        try {
            console.log('포트폴리오 로드 시작...');
            
            const response = await fetch('/portwatch/portfolio/list');
            const data = await response.json();
            
            if (data.success && data.portfolioList) {
                allPortfolio = data.portfolioList;
                console.log('포트폴리오 로드 완료:', allPortfolio.length + '개');
                
                displayPortfolio(allPortfolio);
                updateSummary(data.summary);
            } else {
                showEmptyState();
            }
            
        } catch (error) {
            console.error('포트폴리오 로드 실패:', error);
            showErrorState();
        }
    }
    
    // ✅ 포트폴리오 표시
    function displayPortfolio(portfolio) {
        const tbody = document.getElementById('portfolioTableBody');
        
        if (!portfolio || portfolio.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8">
                        <div class="empty-state">
                            <i class="bi bi-inbox"></i>
                            <h3>포트폴리오가 비어있습니다</h3>
                            <p>첫 번째 종목을 추가해보세요!</p>
                        </div>
                    </td>
                </tr>
            `;
            return;
        }
        
        tbody.innerHTML = '';
        
        portfolio.forEach(item => {
            const row = createPortfolioRow(item);
            tbody.appendChild(row);
        });
    }
    
    // ✅ 포트폴리오 행 생성
    function createPortfolioRow(item) {
        const tr = document.createElement('tr');
        
        // 국가 판단
        const isKorean = item.marketType === 'KOSPI' || item.marketType === 'KOSDAQ';
        const countryFlag = isKorean ? '🇰🇷' : '🇺🇸';
        const currencySymbol = isKorean ? '원' : '$';
        
        // 가격 계산
        const avgPrice = item.avgPurchasePrice || 0;
        const currentPrice = item.currentPrice || 0;
        const quantity = item.quantity || 0;
        const totalValue = currentPrice * quantity;
        const totalInvestment = avgPrice * quantity;
        const profit = totalValue - totalInvestment;
        const profitRate = totalInvestment > 0 ? ((profit / totalInvestment) * 100) : 0;
        
        // 가격 포맷
        const formatPrice = (price) => {
            return isKorean 
                ? price.toLocaleString() + currencySymbol
                : currencySymbol + price.toFixed(2);
        };
        
        // 손익 클래스
        const profitClass = profit >= 0 ? 'profit-positive' : 'profit-negative';
        const profitSign = profit >= 0 ? '+' : '';
        
        tr.innerHTML = `
            <td>
                <div class="stock-info">
                    <span class="country-flag">${countryFlag}</span>
                    <div>
                        <div class="stock-name">${item.stockName}</div>
                        <div class="stock-code">${item.stockCode}</div>
                    </div>
                </div>
            </td>
            <td>
                <span class="quantity">${quantity.toLocaleString()}</span>
            </td>
            <td>
                <span class="price">${formatPrice(avgPrice)}</span>
            </td>
            <td>
                <span class="price">${formatPrice(currentPrice)}</span>
            </td>
            <td>
                <span class="price">${formatPrice(totalValue)}</span>
            </td>
            <td>
                <span class="${profitClass}">${profitSign}${formatPrice(profit)}</span>
            </td>
            <td>
                <span class="${profitClass}">${profitSign}${profitRate.toFixed(2)}%</span>
            </td>
            <td>
                <div class="action-btns">
                    <button class="action-btn btn-edit" onclick="editPortfolio(${item.portfolioId})">
                        ✏️ 수정
                    </button>
                    <button class="action-btn btn-delete" onclick="deletePortfolio(${item.portfolioId})">
                        🗑️ 삭제
                    </button>
                </div>
            </td>
        `;
        
        return tr;
    }
    
    // ✅ 요약 통계 업데이트
    function updateSummary(summary) {
        if (!summary) {
            summary = {
                stockCount: 0,
                totalInvestment: 0,
                totalValue: 0,
                totalProfit: 0
            };
        }
        
        document.getElementById('stockCount').textContent = (summary.stockCount || 0) + '개';
        document.getElementById('totalInvestment').textContent = (summary.totalInvestment || 0).toLocaleString() + '원';
        document.getElementById('totalValue').textContent = (summary.totalValue || 0).toLocaleString() + '원';
        
        const profit = summary.totalProfit || 0;
        const profitElement = document.getElementById('totalProfit');
        profitElement.textContent = (profit >= 0 ? '+' : '') + profit.toLocaleString() + '원';
        profitElement.className = 'summary-value ' + (profit >= 0 ? 'positive' : 'negative');
    }
    
    // ✅ 필터링
    function filterPortfolio(filter) {
        currentFilter = filter;
        
        // 버튼 상태 변경
        document.querySelectorAll('.filter-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        event.target.classList.add('active');
        
        // 필터링
        let filtered = allPortfolio;
        
        if (filter !== 'all') {
            filtered = allPortfolio.filter(item => {
                if (filter === 'KR') {
                    return item.marketType === 'KOSPI' || item.marketType === 'KOSDAQ';
                } else if (filter === 'US') {
                    return item.marketType === 'NASDAQ' || item.marketType === 'NYSE' || item.marketType === 'AMEX';
                } else {
                    return item.marketType === filter;
                }
            });
        }
        
        console.log('필터링 결과:', filter, filtered.length + '개');
        displayPortfolio(filtered);
    }
    
    // ✅ 포트폴리오 수정
    async function editPortfolio(portfolioId) {
        // TODO: 수정 모달 또는 페이지로 이동
        location.href = '/portwatch/portfolio/edit/' + portfolioId;
    }
    
    // ✅ 포트폴리오 삭제
    async function deletePortfolio(portfolioId) {
        if (!confirm('정말 삭제하시겠습니까?')) {
            return;
        }
        
        try {
            const response = await fetch('/portwatch/portfolio/delete/' + portfolioId, {
                method: 'DELETE'
            });
            
            const data = await response.json();
            
            if (data.success) {
                alert('삭제되었습니다.');
                loadPortfolio(); // 새로고침
            } else {
                alert(data.message || '삭제 중 오류가 발생했습니다.');
            }
            
        } catch (error) {
            console.error('삭제 실패:', error);
            alert('삭제 중 오류가 발생했습니다.');
        }
    }
    
    // ✅ 빈 상태
    function showEmptyState() {
        const tbody = document.getElementById('portfolioTableBody');
        tbody.innerHTML = `
            <tr>
                <td colspan="8">
                    <div class="empty-state">
                        <i class="bi bi-inbox"></i>
                        <h3>포트폴리오가 비어있습니다</h3>
                        <p>첫 번째 종목을 추가해보세요!</p>
                    </div>
                </td>
            </tr>
        `;
        updateSummary(null);
    }
    
    // ✅ 에러 상태
    function showErrorState() {
        const tbody = document.getElementById('portfolioTableBody');
        tbody.innerHTML = `
            <tr>
                <td colspan="8">
                    <div class="empty-state">
                        <i class="bi bi-exclamation-triangle"></i>
                        <h3>포트폴리오를 불러올 수 없습니다</h3>
                        <button class="filter-btn" onclick="loadPortfolio()" style="margin-top: 20px;">
                            다시 시도
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }
    
    // ✅ 페이지 로드 시 포트폴리오 로드
    document.addEventListener('DOMContentLoaded', () => {
        console.log('페이지 로드 완료');
        loadPortfolio();
    });
</script>

<jsp:include page="../common/footer.jsp" />
