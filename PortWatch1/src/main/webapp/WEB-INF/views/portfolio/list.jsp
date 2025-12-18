<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="../common/header.jsp" />

<!-- Chart.js 라이브러리 -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

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
    
    .summary-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 1.5rem;
        margin-bottom: 2rem;
    }
    
    .summary-card {
        position: relative;
        border-radius: 16px;
        padding: 1.5rem;
        color: white;
        overflow: hidden;
        transition: all 0.3s;
    }
    
    .summary-card-primary {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    
    .summary-card-success {
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    }
    
    .summary-card-info {
        background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
    }
    
    .summary-card-warning {
        background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
    }
    
    .summary-card::before {
        content: '';
        position: absolute;
        top: -50%;
        right: -20%;
        width: 200px;
        height: 200px;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 50%;
    }
    
    .summary-label {
        font-size: 0.875rem;
        opacity: 0.9;
        margin-bottom: 0.5rem;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .summary-value {
        font-size: 2rem;
        font-weight: 700;
        margin: 0;
        position: relative;
        z-index: 1;
    }
    
    .summary-icon {
        position: absolute;
        right: 1.5rem;
        bottom: 1.5rem;
        font-size: 3rem;
        opacity: 0.2;
    }
    
    .chart-card {
        background: white;
        border-radius: 16px;
        padding: 1.5rem;
        margin-bottom: 2rem;
        box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
    }
    
    .chart-title {
        font-size: 1.25rem;
        font-weight: 600;
        margin-bottom: 1.5rem;
        color: #1f2937;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .chart-container {
        position: relative;
        height: 350px;
    }
    
    .portfolio-table {
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
        flex-wrap: wrap;
        gap: 1rem;
    }
    
    .table-title {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1f2937;
    }
    
    .btn-add {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border: none;
        padding: 0.75rem 1.5rem;
        border-radius: 0.5rem;
        font-weight: 500;
        transition: all 0.3s;
    }
    
    .btn-add:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    }
    
    .table-responsive {
        overflow-x: auto;
    }
    
    .portfolio-list-table {
        width: 100%;
        border-collapse: separate;
        border-spacing: 0;
    }
    
    .portfolio-list-table thead {
        background: #f9fafb;
    }
    
    .portfolio-list-table th {
        padding: 1rem;
        text-align: left;
        font-weight: 600;
        color: #6b7280;
        font-size: 0.875rem;
        text-transform: uppercase;
        letter-spacing: 0.05em;
        border-bottom: 2px solid #e5e7eb;
    }
    
    .portfolio-list-table td {
        padding: 1rem;
        border-bottom: 1px solid #f3f4f6;
    }
    
    .portfolio-list-table tbody tr {
        transition: all 0.2s;
    }
    
    .portfolio-list-table tbody tr:hover {
        background: #f9fafb;
    }
    
    .stock-info {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }
    
    .stock-name {
        font-weight: 600;
        color: #1f2937;
    }
    
    .stock-code {
        font-size: 0.875rem;
        color: #6b7280;
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
    
    .badge-nasdaq,
    .badge-nyse {
        background: #d1fae5;
        color: #065f46;
    }
    
    .profit-up {
        color: #dc2626;
        font-weight: 600;
    }
    
    .profit-down {
        color: #2563eb;
        font-weight: 600;
    }
    
    .btn-group {
        display: flex;
        gap: 0.5rem;
    }
    
    .btn-sm {
        padding: 0.5rem 1rem;
        border: none;
        border-radius: 0.375rem;
        font-size: 0.875rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s;
    }
    
    .btn-edit {
        background: #fbbf24;
        color: #78350f;
    }
    
    .btn-delete {
        background: #ef4444;
        color: white;
    }
    
    .btn-sm:hover {
        opacity: 0.8;
        transform: translateY(-2px);
    }
    
    .empty-state {
        text-align: center;
        padding: 4rem 2rem;
        color: #6b7280;
    }
    
    .empty-state i {
        font-size: 4rem;
        margin-bottom: 1rem;
        opacity: 0.3;
    }
    
    .animate-fade-in {
        animation: fadeIn 0.5s ease-in;
    }
    
    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
    
    @media (max-width: 768px) {
        .summary-grid {
            grid-template-columns: 1fr 1fr;
        }
        
        .summary-value {
            font-size: 1.5rem;
        }
        
        .table-responsive {
            font-size: 0.875rem;
        }
    }
</style>

<!-- Page Header -->
<div class="page-header animate-fade-in">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
        <h1 class="page-title">
            <i class="bi bi-briefcase me-2"></i>내 포트폴리오
        </h1>
        <a href="${pageContext.request.contextPath}/portfolio/create" class="btn btn-add">
            <i class="bi bi-plus-circle me-2"></i>종목 추가
        </a>
    </div>
</div>

<!-- Summary Cards -->
<div class="summary-grid animate-fade-in" style="animation-delay: 0.1s;">
    <div class="summary-card summary-card-primary">
        <div class="summary-label">
            <i class="bi bi-wallet2"></i> 총 투자금액
        </div>
        <h3 class="summary-value">
            <fmt:formatNumber value="${summary.totalInvestment}" type="number" pattern="#,##0" />원
        </h3>
        <i class="bi bi-cash-stack summary-icon"></i>
    </div>
    
    <div class="summary-card summary-card-success">
        <div class="summary-label">
            <i class="bi bi-graph-up"></i> 총 평가금액
        </div>
        <h3 class="summary-value">
            <fmt:formatNumber value="${summary.totalValue}" type="number" pattern="#,##0" />원
        </h3>
        <i class="bi bi-currency-exchange summary-icon"></i>
    </div>
    
    <div class="summary-card summary-card-info">
        <div class="summary-label">
            <i class="bi bi-calculator"></i> 총 손익
        </div>
        <h3 class="summary-value">
            <c:choose>
                <c:when test="${summary.totalProfit >= 0}">+</c:when>
            </c:choose>
            <fmt:formatNumber value="${summary.totalProfit}" type="number" pattern="#,##0" />원
        </h3>
        <i class="bi bi-graph-up-arrow summary-icon"></i>
    </div>
    
    <div class="summary-card summary-card-warning">
        <div class="summary-label">
            <i class="bi bi-percent"></i> 총 수익률
        </div>
        <h3 class="summary-value">
            <c:choose>
                <c:when test="${summary.totalProfitRate >= 0}">+</c:when>
            </c:choose>
            <fmt:formatNumber value="${summary.totalProfitRate}" type="number" pattern="#,##0.00" />%
        </h3>
        <i class="bi bi-bar-chart-fill summary-icon"></i>
    </div>
</div>

<!-- Charts -->
<c:if test="${not empty portfolioList}">
<div class="row g-3 mb-4 animate-fade-in" style="animation-delay: 0.2s;">
    <div class="col-lg-6">
        <div class="chart-card">
            <h5 class="chart-title">
                <i class="bi bi-pie-chart"></i> 포트폴리오 구성
            </h5>
            <div class="chart-container">
                <canvas id="portfolioChart"></canvas>
            </div>
        </div>
    </div>
    
    <div class="col-lg-6">
        <div class="chart-card">
            <h5 class="chart-title">
                <i class="bi bi-bar-chart"></i> 수익률 분석
            </h5>
            <div class="chart-container">
                <canvas id="profitChart"></canvas>
            </div>
        </div>
    </div>
</div>
</c:if>

<!-- Portfolio Table -->
<div class="portfolio-table animate-fade-in" style="animation-delay: 0.3s;">
    <div class="table-header">
        <h5 class="table-title">
            <i class="bi bi-table me-2"></i>보유 종목
        </h5>
    </div>
    
    <c:choose>
        <c:when test="${empty portfolioList}">
            <div class="empty-state">
                <i class="bi bi-inbox"></i>
                <h4>보유 중인 종목이 없습니다</h4>
                <p>종목을 추가하여 포트폴리오를 시작해보세요</p>
                <a href="${pageContext.request.contextPath}/portfolio/create" class="btn btn-add mt-3">
                    <i class="bi bi-plus-circle me-2"></i>첫 종목 추가하기
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="portfolio-list-table">
                    <thead>
                        <tr>
                            <th>종목</th>
                            <th>시장</th>
                            <th>보유수량</th>
                            <th>평균매입가</th>
                            <th>현재가</th>
                            <th>평가금액</th>
                            <th>손익</th>
                            <th>수익률</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${portfolioList}" var="p">
                            <tr>
                                <td>
                                    <div class="stock-info">
                                        <span class="stock-name">${p.stockName}</span>
                                        <span class="stock-code">${p.stockCode}</span>
                                    </div>
                                </td>
                                <td>
                                    <span class="badge badge-${fn:toLowerCase(p.marketType)}">
                                        ${p.marketType}
                                    </span>
                                </td>
                                <td>
                                    <fmt:formatNumber value="${p.quantity}" pattern="#,##0.####" />주
                                </td>
                                <td>
                                    <fmt:formatNumber value="${p.avgPurchasePrice}" pattern="#,##0" />원
                                </td>
                                <td>
                                    <fmt:formatNumber value="${p.currentPrice}" pattern="#,##0" />원
                                </td>
                                <td>
                                    <fmt:formatNumber value="${p.totalCurrentValue}" pattern="#,##0" />원
                                </td>
                                <td class="${p.profit >= 0 ? 'profit-up' : 'profit-down'}">
                                    <fmt:formatNumber value="${p.profit}" pattern="+#,##0;-#,##0" />원
                                </td>
                                <td class="${p.profitRate >= 0 ? 'profit-up' : 'profit-down'}">
                                    <fmt:formatNumber value="${p.profitRate}" pattern="+#,##0.00;-#,##0.00" />%
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button class="btn-sm btn-edit" onclick="editPortfolio(${p.portfolioId})">
                                            <i class="bi bi-pencil"></i> 수정
                                        </button>
                                        <button class="btn-sm btn-delete" onclick="deletePortfolio(${p.portfolioId}, '${p.stockName}')">
                                            <i class="bi bi-trash"></i> 삭제
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Chart.js Script -->
<c:if test="${not empty portfolioList}">
<script>
    console.log('=== 차트 초기화 시작 ===');
    
    // 데이터 준비
    const portfolioData = [
        <c:forEach items="${portfolioList}" var="p" varStatus="status">
        {
            name: '${p.stockName}',
            value: ${p.totalCurrentValue},
            rate: ${p.profitRate}
        }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];
    
    console.log('포트폴리오 데이터:', portfolioData);
    
    // 색상 팔레트
    const colors = [
        '#667eea', '#764ba2', '#f093fb', '#4facfe',
        '#43e97b', '#fa709a', '#fee140', '#30cfd0',
        '#a8edea', '#fed6e3', '#c471f5', '#f64f59'
    ];
    
    // 1. 포트폴리오 구성 도넛 차트
    const ctx1 = document.getElementById('portfolioChart');
    if (ctx1) {
        new Chart(ctx1, {
            type: 'doughnut',
            data: {
                labels: portfolioData.map(d => d.name),
                datasets: [{
                    data: portfolioData.map(d => d.value),
                    backgroundColor: colors.slice(0, portfolioData.length),
                    borderWidth: 3,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                        labels: {
                            padding: 15,
                            font: { size: 12 },
                            generateLabels: function(chart) {
                                const data = chart.data;
                                if (data.labels.length && data.datasets.length) {
                                    return data.labels.map((label, i) => {
                                        const value = data.datasets[0].data[i];
                                        const total = data.datasets[0].data.reduce((a, b) => a + b, 0);
                                        const percentage = ((value / total) * 100).toFixed(1);
                                        return {
                                            text: label + ' (' + percentage + '%)',
                                            fillStyle: data.datasets[0].backgroundColor[i],
                                            hidden: false,
                                            index: i
                                        };
                                    });
                                }
                                return [];
                            }
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const label = context.label || '';
                                const value = context.parsed || 0;
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = ((value / total) * 100).toFixed(1);
                                return label + ': ' + value.toLocaleString() + '원 (' + percentage + '%)';
                            }
                        }
                    }
                }
            }
        });
        console.log('✅ 도넛 차트 생성 완료');
    }
    
    // 2. 수익률 막대 그래프
    const ctx2 = document.getElementById('profitChart');
    if (ctx2) {
        new Chart(ctx2, {
            type: 'bar',
            data: {
                labels: portfolioData.map(d => d.name),
                datasets: [{
                    label: '수익률 (%)',
                    data: portfolioData.map(d => d.rate),
                    backgroundColor: portfolioData.map(d => 
                        d.rate >= 0 ? 'rgba(220, 38, 38, 0.7)' : 'rgba(37, 99, 235, 0.7)'
                    ),
                    borderColor: portfolioData.map(d => 
                        d.rate >= 0 ? 'rgb(220, 38, 38)' : 'rgb(37, 99, 235)'
                    ),
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return '수익률: ' + context.parsed.y.toFixed(2) + '%';
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return value + '%';
                            }
                        }
                    }
                }
            }
        });
        console.log('✅ 막대 그래프 생성 완료');
    }
    
    console.log('=== 차트 초기화 완료 ===');
</script>
</c:if>

<script>
function editPortfolio(id) {
    location.href = '${pageContext.request.contextPath}/portfolio/edit?id=' + id;
}

function deletePortfolio(id, name) {
    if (!confirm(name + ' 종목을 삭제하시겠습니까?')) {
        return;
    }
    
    fetch('${pageContext.request.contextPath}/portfolio/delete/' + id, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(result => {
        if (result.success) {
            alert('✅ 삭제되었습니다.');
            location.reload();
        } else {
            alert('❌ 삭제 실패: ' + result.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('❌ 삭제 중 오류가 발생했습니다.');
    });
}
</script>

<jsp:include page="../common/footer.jsp" />
