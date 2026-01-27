<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!-- 
  미국 주식 차트 컴포넌트
  
  ✅ Chart.js 사용 (한국 주식과 동일)
  ✅ 실시간 가격 업데이트
  ✅ 일봉/주봉/월봉 지원
  
  @author PortWatch
  @version 3.0
-->

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${stock.stockName} (${stock.stockCode}) - US Stock Chart</title>
    
    <!-- Chart.js CDN -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <style>
        .chart-container {
            position: relative;
            height: 500px;
            margin: 20px 0;
        }
        
        .stock-header {
            background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
            color: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        
        .stock-price {
            font-size: 3rem;
            font-weight: bold;
            margin: 10px 0;
        }
        
        .stock-change {
            font-size: 1.5rem;
            font-weight: 600;
        }
        
        .change-positive {
            color: #28a745;
        }
        
        .change-negative {
            color: #dc3545;
        }
        
        .period-buttons {
            margin: 20px 0;
            text-align: center;
        }
        
        .period-btn {
            margin: 0 5px;
            min-width: 80px;
        }
        
        .period-btn.active {
            background-color: #1e3c72;
            border-color: #1e3c72;
        }
        
        .stock-info-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        
        .info-label {
            color: #6c757d;
            font-size: 0.9rem;
            margin-bottom: 5px;
        }
        
        .info-value {
            font-size: 1.2rem;
            font-weight: 600;
            color: #212529;
        }
    </style>
</head>
<body>

<div class="container mt-4">
    <!-- 종목 헤더 -->
    <div class="stock-header">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h2>${stock.stockName}</h2>
                <div class="text-white-50">
                    ${stock.stockCode} | ${stock.marketType} | ${stock.country}
                </div>
                <div class="stock-price" id="currentPrice">
                    $<fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0.00"/>
                </div>
                <div class="stock-change" id="priceChange">
                    <c:choose>
                        <c:when test="${stock.changeRate >= 0}">
                            <span class="change-positive">
                                ▲ $<fmt:formatNumber value="${stock.changeAmount}" pattern="#,##0.00"/>
                                (<fmt:formatNumber value="${stock.changeRate}" pattern="#,##0.00"/>%)
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span class="change-negative">
                                ▼ $<fmt:formatNumber value="${stock.changeAmount * -1}" pattern="#,##0.00"/>
                                (<fmt:formatNumber value="${stock.changeRate * -1}" pattern="#,##0.00"/>%)
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="col-md-4 text-end">
                <button class="btn btn-light btn-lg" onclick="addToWatchlist()">
                    <i class="bi bi-star"></i> 관심종목 추가
                </button>
                <button class="btn btn-success btn-lg mt-2" onclick="buyStock()">
                    <i class="bi bi-cart-plus"></i> 매수하기
                </button>
            </div>
        </div>
    </div>
    
    <!-- 기간 선택 버튼 -->
    <div class="period-buttons">
        <button class="btn btn-outline-primary period-btn active" data-period="1D" onclick="changePeriod('1D', this)">일봉</button>
        <button class="btn btn-outline-primary period-btn" data-period="1W" onclick="changePeriod('1W', this)">주봉</button>
        <button class="btn btn-outline-primary period-btn" data-period="1M" onclick="changePeriod('1M', this)">월봉</button>
        <button class="btn btn-outline-primary period-btn" data-period="3M" onclick="changePeriod('3M', this)">3개월</button>
        <button class="btn btn-outline-primary period-btn" data-period="1Y" onclick="changePeriod('1Y', this)">1년</button>
    </div>
    
    <!-- 차트 -->
    <div class="stock-info-card">
        <h4 class="mb-3">가격 차트</h4>
        <div class="chart-container">
            <canvas id="stockChart"></canvas>
        </div>
    </div>
    
    <!-- 종목 정보 -->
    <div class="row">
        <div class="col-md-3">
            <div class="stock-info-card">
                <div class="info-label">시가총액</div>
                <div class="info-value">
                    $<fmt:formatNumber value="${stock.marketCap}" pattern="#,##0"/>M
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stock-info-card">
                <div class="info-label">거래량</div>
                <div class="info-value">
                    <fmt:formatNumber value="${stock.tradingVolume}" pattern="#,##0"/>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stock-info-card">
                <div class="info-label">업종</div>
                <div class="info-value">${stock.industry}</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stock-info-card">
                <div class="info-label">섹터</div>
                <div class="info-value">${stock.sector}</div>
            </div>
        </div>
    </div>
</div>

<script>
let stockChart = null;
const stockCode = '${stock.stockCode}';
let currentPeriod = '1D';

// ========================================
// 차트 초기화
// ========================================

function initChart() {
    const ctx = document.getElementById('stockChart').getContext('2d');
    
    // 샘플 데이터 (실제로는 API에서 가져와야 함)
    const chartData = generateSampleData(currentPeriod);
    
    stockChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: chartData.labels,
            datasets: [{
                label: '${stock.stockName} 가격',
                data: chartData.prices,
                borderColor: 'rgb(30, 60, 114)',
                backgroundColor: 'rgba(30, 60, 114, 0.1)',
                borderWidth: 2,
                fill: true,
                tension: 0.4,
                pointRadius: 3,
                pointHoverRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                intersect: false,
                mode: 'index'
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return '$' + context.parsed.y.toFixed(2);
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: false,
                    ticks: {
                        callback: function(value) {
                            return '$' + value.toFixed(2);
                        }
                    }
                }
            }
        }
    });
}

// ========================================
// 샘플 데이터 생성
// ========================================

function generateSampleData(period) {
    const labels = [];
    const prices = [];
    const basePrice = parseFloat('${stock.currentPrice}');
    
    let dataPoints = 30;
    if (period === '1W') dataPoints = 7;
    else if (period === '1M') dataPoints = 30;
    else if (period === '3M') dataPoints = 90;
    else if (period === '1Y') dataPoints = 365;
    
    for (let i = dataPoints; i >= 0; i--) {
        const date = new Date();
        date.setDate(date.getDate() - i);
        
        labels.push(date.toLocaleDateString('en-US', { 
            month: 'short', 
            day: 'numeric' 
        }));
        
        // 랜덤 가격 변동 (±5%)
        const randomChange = (Math.random() - 0.5) * basePrice * 0.1;
        const price = basePrice + randomChange;
        prices.push(price.toFixed(2));
    }
    
    return { labels, prices };
}

// ========================================
// 기간 변경
// ========================================

function changePeriod(period, button) {
    console.log('📊 기간 변경:', period);
    
    currentPeriod = period;
    
    // 버튼 활성화 상태 변경
    document.querySelectorAll('.period-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    button.classList.add('active');
    
    // 차트 업데이트
    updateChart();
}

// ========================================
// 차트 업데이트
// ========================================

function updateChart() {
    const chartData = generateSampleData(currentPeriod);
    
    stockChart.data.labels = chartData.labels;
    stockChart.data.datasets[0].data = chartData.prices;
    stockChart.update();
}

// ========================================
// 실시간 가격 업데이트
// ========================================

function updateRealTimePrice() {
    console.log('💰 실시간 가격 업데이트');
    
    // AJAX로 현재가 조회
    fetch('/api/stocks/' + stockCode + '/price')
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                const price = data.price;
                const change = data.change;
                const changeRate = data.changeRate;
                
                // 가격 업데이트
                document.getElementById('currentPrice').textContent = 
                    '$' + price.toFixed(2);
                
                // 변동액 업데이트
                const changeHtml = changeRate >= 0 ?
                    '<span class="change-positive">▲ $' + change.toFixed(2) + ' (' + changeRate.toFixed(2) + '%)</span>' :
                    '<span class="change-negative">▼ $' + Math.abs(change).toFixed(2) + ' (' + Math.abs(changeRate).toFixed(2) + '%)</span>';
                
                document.getElementById('priceChange').innerHTML = changeHtml;
            }
        })
        .catch(error => {
            console.error('가격 업데이트 실패:', error);
        });
}

// ========================================
// 관심종목 추가
// ========================================

function addToWatchlist() {
    console.log('⭐ 관심종목 추가:', stockCode);
    
    fetch('/api/watchlist/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            stockCode: stockCode
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('✅ 관심종목에 추가되었습니다!');
        } else {
            alert('❌ 관심종목 추가 실패: ' + data.message);
        }
    })
    .catch(error => {
        console.error('관심종목 추가 실패:', error);
        alert('❌ 관심종목 추가 중 오류가 발생했습니다.');
    });
}

// ========================================
// 매수하기
// ========================================

function buyStock() {
    console.log('💵 매수하기:', stockCode);
    
    window.location.href = '/stock/purchase?code=' + stockCode;
}

// ========================================
// 페이지 로드 시 실행
// ========================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('📈 미국 주식 차트 초기화');
    
    // 차트 초기화
    initChart();
    
    // 10초마다 실시간 가격 업데이트
    setInterval(updateRealTimePrice, 10000);
});
</script>

<!-- Bootstrap Icons -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">

</body>
</html>
