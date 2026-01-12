<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- ✅ currentMenu 설정 -->
<c:set var="currentMenu" value="dashboard" scope="request"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>대시보드 - PortWatch</title>
    
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    
    <style>
        :root {
            --primary-color: #667eea;
            --secondary-color: #764ba2;
            --success-color: #28a745;
            --danger-color: #dc3545;
            --shadow: 0 2px 10px rgba(0,0,0,0.08);
            --shadow-hover: 0 5px 20px rgba(0,0,0,0.15);
        }
        
        body {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            margin: 0;
            padding: 0;
        }
        
        .dashboard-content {
            padding-top: 30px;
        }
        
        .dashboard-header {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            color: white;
            padding: 40px 0;
            margin-bottom: 30px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        
        .stat-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            margin-bottom: 30px;
            box-shadow: var(--shadow);
            transition: all 0.3s ease;
            border: none;
        }
        
        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: var(--shadow-hover);
        }
        
        .stat-icon {
            font-size: 2rem;
            margin-bottom: 10px;
            opacity: 0.8;
        }
        
        .stat-value {
            font-size: 2rem;
            font-weight: bold;
            margin: 10px 0;
            font-family: 'Courier New', monospace;
        }
        
        .stat-label {
            color: #6c757d;
            font-size: 0.85rem;
            text-transform: uppercase;
            letter-spacing: 1px;
            font-weight: 600;
        }
        
        .profit {
            color: var(--success-color);
        }
        
        .loss {
            color: var(--danger-color);
        }
        
        .chart-container {
            background: white;
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: var(--shadow);
            height: 450px;
        }
        
        .chart-title {
            font-size: 1.3rem;
            font-weight: 600;
            margin-bottom: 20px;
            color: #333;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .portfolio-table {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow: var(--shadow);
        }
        
        .table-title {
            font-size: 1.3rem;
            font-weight: 600;
            margin-bottom: 20px;
            color: #333;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .btn-action {
            padding: 12px 30px;
            border-radius: 50px;
            font-weight: 600;
            transition: all 0.3s;
            border: none;
        }
        
        .btn-primary-custom {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            color: white;
        }
        
        .btn-primary-custom:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            color: white;
        }
        
        .loading-spinner {
            text-align: center;
            padding: 50px;
        }
        
        .empty-state {
            text-align: center;
            padding: 80px 20px;
            color: #6c757d;
        }
        
        .empty-state i {
            font-size: 5rem;
            margin-bottom: 30px;
            opacity: 0.3;
        }
        
        canvas {
            max-height: 350px !important;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        .fade-in {
            animation: fadeIn 0.5s ease-out;
        }
    </style>
</head>
<body>
    <!-- ✅ Header 포함 -->
    <%@ include file="./common/header.jsp" %>
    
    <!-- Dashboard Content -->
    <div class="dashboard-content">
        <!-- Header -->
        <div class="dashboard-header">
            <div class="container">
                <h1 class="mb-2"><i class="fas fa-chart-line"></i> 포트폴리오 대시보드</h1>
                <p class="mb-0 opacity-75">실시간 자산 현황 및 수익률 분석</p>
            </div>
        </div>

        <div class="container">
            <!-- Summary Cards -->
            <div class="row" id="summaryCards">
                <div class="col-lg-3 col-md-6">
                    <div class="stat-card fade-in">
                        <div class="stat-icon text-primary">
                            <i class="fas fa-wallet"></i>
                        </div>
                        <div class="stat-label">총 평가금액</div>
                        <div class="stat-value text-primary" id="totalValue">₩0</div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="stat-card fade-in" style="animation-delay: 0.1s;">
                        <div class="stat-icon text-info">
                            <i class="fas fa-dollar-sign"></i>
                        </div>
                        <div class="stat-label">총 투자원금</div>
                        <div class="stat-value text-info" id="totalCost">₩0</div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="stat-card fade-in" style="animation-delay: 0.2s;">
                        <div class="stat-icon">
                            <i class="fas fa-chart-line"></i>
                        </div>
                        <div class="stat-label">총 평가손익</div>
                        <div class="stat-value" id="totalProfit">₩0</div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="stat-card fade-in" style="animation-delay: 0.3s;">
                        <div class="stat-icon">
                            <i class="fas fa-percent"></i>
                        </div>
                        <div class="stat-label">총 수익률</div>
                        <div class="stat-value" id="returnRate">0.00%</div>
                    </div>
                </div>
            </div>

            <!-- Charts -->
            <div class="row">
                <!-- Pie Chart -->
                <div class="col-lg-6">
                    <div class="chart-container fade-in" style="animation-delay: 0.4s;">
                        <div class="chart-title">
                            <i class="fas fa-chart-pie text-primary"></i>
                            <span>자산 구성 비율</span>
                        </div>
                        <canvas id="assetPieChart"></canvas>
                    </div>
                </div>
                
                <!-- Bar Chart -->
                <div class="col-lg-6">
                    <div class="chart-container fade-in" style="animation-delay: 0.5s;">
                        <div class="chart-title">
                            <i class="fas fa-chart-bar text-success"></i>
                            <span>종목별 수익률</span>
                        </div>
                        <canvas id="profitBarChart"></canvas>
                    </div>
                </div>
            </div>

            <!-- Portfolio Table -->
            <div class="portfolio-table fade-in" style="animation-delay: 0.6s;">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div class="table-title">
                        <i class="fas fa-list-ul text-secondary"></i>
                        <span>보유 종목 상세</span>
                    </div>
                    <a href="${pageContext.request.contextPath}/portfolio/create" 
                       class="btn btn-primary-custom btn-action">
                        <i class="fas fa-plus me-2"></i>종목 추가
                    </a>
                </div>
                
                <div id="portfolioTableContainer">
                    <div class="loading-spinner">
                        <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                        <p class="mt-3 text-muted">데이터를 불러오는 중...</p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- ✅ Footer 포함 -->
    <%@ include file="./common/footer.jsp" %>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // Dashboard Manager
        const Dashboard = {
            contextPath: '${pageContext.request.contextPath}',
            assetPieChart: null,
            profitBarChart: null,
            
            init: function() {
                console.log('📊 Dashboard 초기화');
                Dashboard.loadData();
            },
            
            loadData: function() {
                fetch(Dashboard.contextPath + '/api/portfolio/list', {
                    method: 'GET',
                    credentials: 'same-origin'
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success && data.portfolios && data.portfolios.length > 0) {
                        Dashboard.updateSummary(data.summary);
                        Dashboard.renderCharts(data.portfolios);
                        Dashboard.renderTable(data.portfolios);
                    } else {
                        Dashboard.showEmpty();
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    Dashboard.showEmpty();
                });
            },
            
            updateSummary: function(summary) {
                if (!summary) return;
                
                document.getElementById('totalValue').textContent = 
                    '₩' + Math.round(summary.totalValue || 0).toLocaleString('ko-KR');
                document.getElementById('totalCost').textContent = 
                    '₩' + Math.round(summary.totalCost || 0).toLocaleString('ko-KR');
                
                const profitElement = document.getElementById('totalProfit');
                const totalProfit = summary.totalProfit || 0;
                profitElement.textContent = 
                    (totalProfit >= 0 ? '+' : '') + '₩' + Math.round(totalProfit).toLocaleString('ko-KR');
                profitElement.className = 'stat-value ' + (totalProfit >= 0 ? 'profit' : 'loss');
                
                const rateElement = document.getElementById('returnRate');
                const returnRate = summary.returnRate || 0;
                rateElement.textContent = 
                    (returnRate >= 0 ? '+' : '') + returnRate.toFixed(2) + '%';
                rateElement.className = 'stat-value ' + (returnRate >= 0 ? 'profit' : 'loss');
            },
            
            renderCharts: function(portfolios) {
                Dashboard.renderPieChart(portfolios);
                Dashboard.renderBarChart(portfolios);
            },
            
            renderPieChart: function(portfolios) {
                const labels = portfolios.map(p => p.stockName || p.stockCode);
                const data = portfolios.map(p => p.totalValue || 0);
                const colors = ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF',
                               '#FF9F40', '#FF6384', '#C9CBCF', '#4BC0C0', '#E7E9ED'];
                
                const ctx = document.getElementById('assetPieChart');
                
                if (Dashboard.assetPieChart) {
                    Dashboard.assetPieChart.destroy();
                }
                
                Dashboard.assetPieChart = new Chart(ctx, {
                    type: 'pie',
                    data: {
                        labels: labels,
                        datasets: [{
                            data: data,
                            backgroundColor: colors,
                            borderWidth: 2,
                            borderColor: '#fff'
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: true,
                        plugins: {
                            legend: {
                                position: 'bottom',
                                labels: {
                                    padding: 20,
                                    font: { size: 12 },
                                    usePointStyle: true
                                }
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        const value = context.parsed || 0;
                                        const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                        const percentage = ((value / total) * 100).toFixed(1);
                                        return context.label + ': ₩' + 
                                               Math.round(value).toLocaleString('ko-KR') + 
                                               ' (' + percentage + '%)';
                                    }
                                }
                            }
                        }
                    }
                });
            },
            
            renderBarChart: function(portfolios) {
                const labels = portfolios.map(p => p.stockName || p.stockCode);
                const data = portfolios.map(p => p.profitRate || 0);
                const colors = data.map(rate => rate >= 0 ? '#28a745' : '#dc3545');
                
                const ctx = document.getElementById('profitBarChart');
                
                if (Dashboard.profitBarChart) {
                    Dashboard.profitBarChart.destroy();
                }
                
                Dashboard.profitBarChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: '수익률 (%)',
                            data: data,
                            backgroundColor: colors,
                            borderColor: colors,
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: true,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    callback: value => value.toFixed(1) + '%'
                                }
                            }
                        },
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        const rate = context.parsed.y;
                                        return '수익률: ' + (rate >= 0 ? '+' : '') + rate.toFixed(2) + '%';
                                    }
                                }
                            }
                        }
                    }
                });
            },
            
            renderTable: function(portfolios) {
                let html = '<div class="table-responsive">';
                html += '<table class="table table-hover align-middle">';
                html += '<thead><tr>';
                html += '<th>종목명</th>';
                html += '<th class="text-end">보유수량</th>';
                html += '<th class="text-end">평균단가</th>';
                html += '<th class="text-end">현재가</th>';
                html += '<th class="text-end">평가금액</th>';
                html += '<th class="text-end">평가손익</th>';
                html += '<th class="text-end">수익률</th>';
                html += '</tr></thead><tbody>';
                
                portfolios.forEach(item => {
                    const profit = item.profit || 0;
                    const profitRate = item.profitRate || 0;
                    const profitClass = profit >= 0 ? 'profit' : 'loss';
                    
                    html += '<tr>';
                    html += '<td><strong>' + (item.stockName || item.stockCode) + '</strong><br>';
                    html += '<small class="text-muted">' + item.stockCode + '</small></td>';
                    html += '<td class="text-end">' + (item.quantity || 0).toLocaleString() + '</td>';
                    html += '<td class="text-end">₩' + Math.round(item.purchasePrice || 0).toLocaleString() + '</td>';
                    html += '<td class="text-end">₩' + Math.round(item.currentPrice || 0).toLocaleString() + '</td>';
                    html += '<td class="text-end">₩' + Math.round(item.totalValue || 0).toLocaleString() + '</td>';
                    html += '<td class="text-end ' + profitClass + '">';
                    html += (profit >= 0 ? '+' : '') + '₩' + Math.round(profit).toLocaleString() + '</td>';
                    html += '<td class="text-end"><span class="badge ' + 
                            (profitRate >= 0 ? 'bg-success' : 'bg-danger') + '">';
                    html += (profitRate >= 0 ? '+' : '') + profitRate.toFixed(2) + '%</span></td>';
                    html += '</tr>';
                });
                
                html += '</tbody></table></div>';
                document.getElementById('portfolioTableContainer').innerHTML = html;
            },
            
            showEmpty: function() {
                const html = '<div class="empty-state">' +
                    '<i class="fas fa-folder-open"></i>' +
                    '<h3>보유 종목이 없습니다</h3>' +
                    '<p class="text-muted mb-4">첫 번째 종목을 추가해보세요!</p>' +
                    '<a href="' + Dashboard.contextPath + '/portfolio/create" ' +
                    'class="btn btn-primary-custom btn-action btn-lg">' +
                    '<i class="fas fa-plus me-2"></i>종목 추가하기</a></div>';
                
                document.getElementById('portfolioTableContainer').innerHTML = html;
                document.getElementById('totalValue').textContent = '₩0';
                document.getElementById('totalCost').textContent = '₩0';
                document.getElementById('totalProfit').textContent = '₩0';
                document.getElementById('returnRate').textContent = '0.00%';
            }
        };
        
        document.addEventListener('DOMContentLoaded', () => {
            Dashboard.init();
        });
    </script>
</body>
</html>
