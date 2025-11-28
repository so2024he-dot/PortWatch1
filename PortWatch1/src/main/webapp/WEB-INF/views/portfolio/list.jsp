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
        color: #1f2937;
        margin-bottom: 1rem;
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
        margin: 0;
    }
    
    .stock-name {
        font-weight: 600;
        color: #1f2937;
    }
    
    .stock-code {
        font-size: 0.875rem;
        color: #6b7280;
    }
    
    .price-positive {
        color: #dc2626;
        font-weight: 600;
    }
    
    .price-negative {
        color: #2563eb;
        font-weight: 600;
    }
    
    .action-buttons {
        display: flex;
        gap: 0.5rem;
    }
    
    .btn-sm {
        padding: 0.375rem 0.75rem;
        font-size: 0.875rem;
    }
    
    .empty-state {
        text-align: center;
        padding: 3rem 1rem;
        color: #6b7280;
    }
    
    .empty-icon {
        font-size: 4rem;
        margin-bottom: 1rem;
        opacity: 0.5;
    }
    
    @media (max-width: 768px) {
        .page-header {
            padding: 1.5rem;
        }
        
        .page-title {
            font-size: 1.5rem;
        }
        
        .summary-grid {
            grid-template-columns: repeat(2, 1fr);
            gap: 1rem;
        }
        
        .summary-card {
            padding: 1rem;
        }
        
        .summary-value {
            font-size: 1.5rem;
        }
        
        .summary-icon {
            font-size: 2rem;
        }
        
        .portfolio-table {
            padding: 1rem;
        }
        
        .table-responsive {
            font-size: 0.875rem;
        }
    }
    
    @media (max-width: 576px) {
        .summary-grid {
            grid-template-columns: 1fr;
        }
        
        .summary-value {
            font-size: 1.25rem;
        }
    }
</style>

<!-- Alert Messages -->
<c:if test="${not empty message}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="bi bi-check-circle me-2"></i>${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="bi bi-exclamation-circle me-2"></i>${error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<!-- Page Header -->
<div class="page-header animate-fade-in">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
        <h1 class="page-title">
            <i class="bi bi-briefcase me-2"></i>내 포트폴리오
        </h1>
        <a href="${pageContext.request.contextPath}/portfolio/create" class="btn btn-primary">
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
<div class="row g-3 mb-4">
    <div class="col-lg-6">
        <div class="chart-card animate-fade-in" style="animation-delay: 0.2s;">
            <h5 class="chart-title">
                <i class="bi bi-pie-chart me-2"></i>포트폴리오 구성
            </h5>
            <canvas id="portfolioChart" height="250"></canvas>
        </div>
    </div>
    
    <div class="col-lg-6">
        <div class="chart-card animate-fade-in" style="animation-delay: 0.3s;">
            <h5 class="chart-title">
                <i class="bi bi-bar-chart me-2"></i>수익률 분석
            </h5>
            <canvas id="profitChart" height="250"></canvas>
        </div>
    </div>
</div>

<!-- Portfolio Table -->
<div class="portfolio-table animate-fade-in" style="animation-delay: 0.4s;">
    <div class="table-header">
        <h5 class="table-title">
            <i class="bi bi-list-ul me-2"></i>보유 종목
        </h5>
    </div>
    
    <c:choose>
        <c:when test="${empty portfolioList}">
            <div class="empty-state">
                <div class="empty-icon">
                    <i class="bi bi-inbox"></i>
                </div>
                <h5>등록된 종목이 없습니다</h5>
                <p>종목 추가 버튼을 눌러 첫 번째 종목을 추가해보세요!</p>
                <a href="${pageContext.request.contextPath}/portfolio/create" class="btn btn-primary mt-3">
                    <i class="bi bi-plus-circle me-2"></i>종목 추가
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th>종목명</th>
                            <th class="text-end">보유수량</th>
                            <th class="text-end d-none d-md-table-cell">평균단가</th>
                            <th class="text-end">현재가</th>
                            <th class="text-end d-none d-lg-table-cell">평가금액</th>
                            <th class="text-end">손익</th>
                            <th class="text-end">수익률</th>
                            <th class="text-center">관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${portfolioList}">
                            <tr>
                                <td>
                                    <div class="stock-name">${item.stockName}</div>
                                    <div class="stock-code">${item.stockCode}</div>
                                </td>
                                <td class="text-end">
                                    <fmt:formatNumber value="${item.quantity}" type="number" pattern="#,##0" />
                                </td>
                                <td class="text-end d-none d-md-table-cell">
                                    <fmt:formatNumber value="${item.avgPurchasePrice}" type="number" pattern="#,##0" />원
                                </td>
                                <td class="text-end">
                                    <fmt:formatNumber value="${item.currentPrice}" type="number" pattern="#,##0" />원
                                </td>
                                <td class="text-end d-none d-lg-table-cell">
                                    <fmt:formatNumber value="${item.totalCurrentValue}" type="number" pattern="#,##0" />원
                                </td>
                                <td class="text-end ${item.profit >= 0 ? 'price-positive' : 'price-negative'}">
                                    <i class="bi bi-${item.profit >= 0 ? 'arrow-up' : 'arrow-down'}"></i>
                                    ${item.profit >= 0 ? '+' : ''}
                                    <fmt:formatNumber value="${item.profit}" type="number" pattern="#,##0" />원
                                </td>
                                <td class="text-end ${item.profitRate >= 0 ? 'price-positive' : 'price-negative'}">
                                    <i class="bi bi-${item.profitRate >= 0 ? 'arrow-up' : 'arrow-down'}"></i>
                                    ${item.profitRate >= 0 ? '+' : ''}
                                    <fmt:formatNumber value="${item.profitRate}" type="number" pattern="#,##0.00" />%
                                </td>
                                <td class="text-center">
                                    <div class="action-buttons justify-content-center">
                                        <form action="${pageContext.request.contextPath}/portfolio/delete/${item.portfolioId}" 
                                              method="post" 
                                              onsubmit="return confirm('정말 삭제하시겠습니까?');" 
                                              style="display: inline;">
                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </form>
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

<script>
// Auto-hide alerts
setTimeout(function() {
    $('.alert').fadeOut('slow');
}, 3000);

// Portfolio Composition Chart
<c:if test="${not empty portfolioList}">
const portfolioData = {
    labels: [
        <c:forEach var="item" items="${portfolioList}" varStatus="status">
        '${item.stockName}'${not status.last ? ',' : ''}
        </c:forEach>
    ],
    datasets: [{
        data: [
            <c:forEach var="item" items="${portfolioList}" varStatus="status">
            ${item.totalCurrentValue}${not status.last ? ',' : ''}
            </c:forEach>
        ],
        backgroundColor: [
            'rgba(102, 126, 234, 0.8)',
            'rgba(118, 75, 162, 0.8)',
            'rgba(16, 185, 129, 0.8)',
            'rgba(59, 130, 246, 0.8)',
            'rgba(245, 158, 11, 0.8)',
            'rgba(239, 68, 68, 0.8)',
            'rgba(168, 85, 247, 0.8)',
            'rgba(236, 72, 153, 0.8)'
        ]
    }]
};

const portfolioChart = new Chart(document.getElementById('portfolioChart'), {
    type: 'doughnut',
    data: portfolioData,
    options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
            legend: {
                position: 'bottom',
                labels: {
                    padding: 15,
                    font: {
                        size: 12
                    }
                }
            }
        }
    }
});

// Profit Chart
const profitData = {
    labels: [
        <c:forEach var="item" items="${portfolioList}" varStatus="status">
        '${item.stockName}'${not status.last ? ',' : ''}
        </c:forEach>
    ],
    datasets: [{
        label: '수익률 (%)',
        data: [
            <c:forEach var="item" items="${portfolioList}" varStatus="status">
            ${item.profitRate}${not status.last ? ',' : ''}
            </c:forEach>
        ],
        backgroundColor: [
            <c:forEach var="item" items="${portfolioList}" varStatus="status">
            '${item.profitRate >= 0 ? "rgba(16, 185, 129, 0.8)" : "rgba(239, 68, 68, 0.8)"}'${not status.last ? ',' : ''}
            </c:forEach>
        ]
    }]
};

const profitChart = new Chart(document.getElementById('profitChart'), {
    type: 'bar',
    data: profitData,
    options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
            legend: {
                display: false
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
</c:if>
</script>

<jsp:include page="../common/footer.jsp" />
