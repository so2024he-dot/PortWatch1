<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>포트폴리오 대시보드 - PortWatch</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    
    <style>
        body {
            background: #f8f9fa;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .dashboard-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
            transition: transform 0.3s, box-shadow 0.3s;
        }
        
        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 20px rgba(0,0,0,0.15);
        }
        
        .stat-value {
            font-size: 2.5rem;
            font-weight: bold;
            margin: 10px 0;
        }
        
        .stat-label {
            color: #6c757d;
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        
        .profit {
            color: #28a745;
        }
        
        .loss {
            color: #dc3545;
        }
        
        .chart-container {
            background: white;
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
        }
        
        .chart-title {
            font-size: 1.4rem;
            font-weight: 600;
            margin-bottom: 20px;
            color: #333;
        }
        
        .portfolio-table {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
        }
        
        .table-title {
            font-size: 1.4rem;
            font-weight: 600;
            margin-bottom: 20px;
            color: #333;
        }
        
        .btn-action {
            padding: 10px 25px;
            border-radius: 50px;
            font-weight: 600;
            transition: all 0.3s;
        }
        
        .btn-primary-custom {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
        }
        
        .btn-primary-custom:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
        }
        
        .loading-spinner {
            text-align: center;
            padding: 50px;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }
        
        .empty-state i {
            font-size: 4rem;
            margin-bottom: 20px;
            opacity: 0.5;
        }
        
        canvas {
            max-height: 400px;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <div class="dashboard-header">
        <div class="container">
            <h1><i class="fas fa-chart-line"></i> 포트폴리오 대시보드</h1>
            <p class="mb-0">실시간 자산 현황 및 수익률 분석</p>
        </div>
    </div>

    <div class="container">
        <!-- Summary Cards -->
        <div class="row" id="summaryCards">
            <div class="col-md-3">
                <div class="stat-card">
                    <div class="stat-label">
                        <i class="fas fa-wallet"></i> 총 자산
                    </div>
                    <div class="stat-value" id="totalAsset">-</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card">
                    <div class="stat-label">
                        <i class="fas fa-dollar-sign"></i> 투자원금
                    </div>
                    <div class="stat-value" id="totalCost">-</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card">
                    <div class="stat-label">
                        <i class="fas fa-chart-line"></i> 평가손익
                    </div>
                    <div class="stat-value" id="totalProfit">-</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card">
                    <div class="stat-label">
                        <i class="fas fa-percent"></i> 수익률
                    </div>
                    <div class="stat-value" id="returnRate">-</div>
                </div>
            </div>
        </div>

        <!-- Charts -->
        <div class="row">
            <!-- Pie Chart - 자산 구성 -->
            <div class="col-md-6">
                <div class="chart-container">
                    <div class="chart-title">
                        <i class="fas fa-chart-pie"></i> 자산 구성 비율
                    </div>
                    <canvas id="assetPieChart"></canvas>
                </div>
            </div>
            
            <!-- Bar Chart - 종목별 수익률 -->
            <div class="col-md-6">
                <div class="chart-container">
                    <div class="chart-title">
                        <i class="fas fa-chart-bar"></i> 종목별 수익률
                    </div>
                    <canvas id="profitBarChart"></canvas>
                </div>
            </div>
        </div>

        <!-- Portfolio Table -->
        <div class="portfolio-table">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div class="table-title">
                    <i class="fas fa-list"></i> 보유 종목 상세
                </div>
                <a href="${pageContext.request.contextPath}/portfolio/create" 
                   class="btn btn-primary-custom btn-action">
                    <i class="fas fa-plus"></i> 종목 추가
                </a>
            </div>
            
            <div id="portfolioTableContainer">
                <div class="loading-spinner">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <p class="mt-2">데이터를 불러오는 중...</p>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // Portfolio Dashboard Manager
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        
        const PortfolioDashboard = {
            contextPath: '${pageContext.request.contextPath}',
            assetPieChart: null,
            profitBarChart: null,
            
            /**
             * 초기화
             */
            init: function() {
                console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
                console.log('📊 Portfolio Dashboard 초기화');
                console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
                
                PortfolioDashboard.loadPortfolioData();
            },
            
            /**
             * 포트폴리오 데이터 로드
             */
            loadPortfolioData: function() {
                console.log('📊 포트폴리오 데이터 로딩...');
                
                fetch(PortfolioDashboard.contextPath + '/api/portfolio/list')
                    .then(function(response) {
                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(function(data) {
                        console.log('✅ 데이터 로드 완료:', data);
                        
                        if (data.success && data.portfolios && data.portfolios.length > 0) {
                            PortfolioDashboard.updateSummary(data.summary);
                            PortfolioDashboard.renderCharts(data.portfolios);
                            PortfolioDashboard.renderTable(data.portfolios);
                        } else {
                            PortfolioDashboard.showEmptyState();
                        }
                    })
                    .catch(function(error) {
                        console.error('❌ 데이터 로드 실패:', error);
                        PortfolioDashboard.showEmptyState();
                    });
            },
            
            /**
             * 요약 정보 업데이트
             */
            updateSummary: function(summary) {
                if (!summary) return;
                
                const formatNumber = function(num) {
                    return new Intl.NumberFormat('ko-KR').format(Math.round(num));
                };
                
                const formatPercent = function(num) {
                    return (num >= 0 ? '+' : '') + num.toFixed(2) + '%';
                };
                
                document.getElementById('totalAsset').textContent = '₩' + formatNumber(summary.totalValue || 0);
                document.getElementById('totalCost').textContent = '₩' + formatNumber(summary.totalCost || 0);
                
                const profitElement = document.getElementById('totalProfit');
                const profitValue = summary.totalProfit || 0;
                profitElement.textContent = '₩' + formatNumber(profitValue);
                profitElement.className = 'stat-value ' + (profitValue >= 0 ? 'profit' : 'loss');
                
                const rateElement = document.getElementById('returnRate');
                const rateValue = summary.returnRate || 0;
                rateElement.textContent = formatPercent(rateValue);
                rateElement.className = 'stat-value ' + (rateValue >= 0 ? 'profit' : 'loss');
            },
            
            /**
             * 차트 렌더링
             */
            renderCharts: function(portfolios) {
                PortfolioDashboard.renderAssetPieChart(portfolios);
                PortfolioDashboard.renderProfitBarChart(portfolios);
            },
            
            /**
             * 자산 구성 원형 차트
             */
            renderAssetPieChart: function(portfolios) {
                const labels = [];
                const data = [];
                const colors = [
                    '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF',
                    '#FF9F40', '#FF6384', '#C9CBCF', '#4BC0C0', '#FF6384'
                ];
                
                portfolios.forEach(function(item) {
                    labels.push(item.stockName || item.stockCode);
                    data.push(item.totalValue || 0);
                });
                
                const ctx = document.getElementById('assetPieChart').getContext('2d');
                
                if (PortfolioDashboard.assetPieChart) {
                    PortfolioDashboard.assetPieChart.destroy();
                }
                
                PortfolioDashboard.assetPieChart = new Chart(ctx, {
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
                                    padding: 15,
                                    font: {
                                        size: 12
                                    }
                                }
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        const label = context.label || '';
                                        const value = context.parsed || 0;
                                        const formatted = new Intl.NumberFormat('ko-KR').format(value);
                                        return label + ': ₩' + formatted;
                                    }
                                }
                            }
                        }
                    }
                });
            },
            
            /**
             * 종목별 수익률 막대 차트
             */
            renderProfitBarChart: function(portfolios) {
                const labels = [];
                const data = [];
                const colors = [];
                
                portfolios.forEach(function(item) {
                    labels.push(item.stockName || item.stockCode);
                    const profitRate = item.profitRate || 0;
                    data.push(profitRate);
                    colors.push(profitRate >= 0 ? '#28a745' : '#dc3545');
                });
                
                const ctx = document.getElementById('profitBarChart').getContext('2d');
                
                if (PortfolioDashboard.profitBarChart) {
                    PortfolioDashboard.profitBarChart.destroy();
                }
                
                PortfolioDashboard.profitBarChart = new Chart(ctx, {
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
                                    callback: function(value) {
                                        return value + '%';
                                    }
                                }
                            }
                        },
                        plugins: {
                            legend: {
                                display: false
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        return '수익률: ' + context.parsed.y.toFixed(2) + '%';
                                    }
                                }
                            }
                        }
                    }
                });
            },
            
            /**
             * 테이블 렌더링
             */
            renderTable: function(portfolios) {
                let html = '<div class="table-responsive"><table class="table table-hover align-middle">';
                html += '<thead class="table-light"><tr>';
                html += '<th>종목명</th>';
                html += '<th class="text-end">보유수량</th>';
                html += '<th class="text-end">평균단가</th>';
                html += '<th class="text-end">현재가</th>';
                html += '<th class="text-end">평가금액</th>';
                html += '<th class="text-end">손익</th>';
                html += '<th class="text-end">수익률</th>';
                html += '</tr></thead><tbody>';
                
                portfolios.forEach(function(item) {
                    const profitClass = (item.profit || 0) >= 0 ? 'text-success' : 'text-danger';
                    const profitRateClass = (item.profitRate || 0) >= 0 ? 'text-success' : 'text-danger';
                    
                    html += '<tr>';
                    html += '<td><strong>' + (item.stockName || item.stockCode) + '</strong><br>';
                    html += '<small class="text-muted">' + item.stockCode + '</small></td>';
                    html += '<td class="text-end">' + (item.quantity || 0).toLocaleString() + '</td>';
                    html += '<td class="text-end">₩' + (item.purchasePrice || 0).toLocaleString() + '</td>';
                    html += '<td class="text-end">₩' + (item.currentPrice || 0).toLocaleString() + '</td>';
                    html += '<td class="text-end">₩' + (item.totalValue || 0).toLocaleString() + '</td>';
                    html += '<td class="text-end ' + profitClass + '">₩' + (item.profit || 0).toLocaleString() + '</td>';
                    html += '<td class="text-end ' + profitRateClass + '">' + 
                            ((item.profitRate || 0) >= 0 ? '+' : '') + 
                            (item.profitRate || 0).toFixed(2) + '%</td>';
                    html += '</tr>';
                });
                
                html += '</tbody></table></div>';
                
                document.getElementById('portfolioTableContainer').innerHTML = html;
            },
            
            /**
             * 빈 상태 표시
             */
            showEmptyState: function() {
                const emptyHtml = '<div class="empty-state">' +
                    '<i class="fas fa-inbox"></i>' +
                    '<h3>보유 종목이 없습니다</h3>' +
                    '<p class="text-muted">첫 번째 종목을 추가해보세요!</p>' +
                    '<a href="' + PortfolioDashboard.contextPath + '/portfolio/create" ' +
                    'class="btn btn-primary-custom btn-action mt-3">' +
                    '<i class="fas fa-plus"></i> 종목 추가하기</a>' +
                    '</div>';
                
                document.getElementById('portfolioTableContainer').innerHTML = emptyHtml;
                document.getElementById('totalAsset').textContent = '₩0';
                document.getElementById('totalCost').textContent = '₩0';
                document.getElementById('totalProfit').textContent = '₩0';
                document.getElementById('returnRate').textContent = '0.00%';
            }
        };
        
        // 페이지 로드 시 초기화
        document.addEventListener('DOMContentLoaded', function() {
            PortfolioDashboard.init();
        });
    </script>
</body>
</html>
