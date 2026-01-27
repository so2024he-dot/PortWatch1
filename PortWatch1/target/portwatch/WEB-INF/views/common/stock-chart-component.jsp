<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 
    통합 주식 차트 컴포넌트
    사용법: <jsp:include page="../common/stock-chart.jsp">
               <jsp:param name="stockCode" value="${stock.stockCode}" />
               <jsp:param name="stockName" value="${stock.stockName}" />
               <jsp:param name="country" value="${stock.country}" />
               <jsp:param name="marketType" value="${stock.marketType}" />
           </jsp:include>
-->

<div class="stock-chart-container" style="background: white; border-radius: 16px; padding: 1.5rem; box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1); margin-bottom: 2rem;">
    
    <!-- 차트 헤더 -->
    <div class="chart-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
        <h5 style="margin: 0; font-weight: 600; color: #1f2937;">
            <i class="bi bi-graph-up" style="margin-right: 0.5rem;"></i>
            주가 차트
        </h5>
        <div class="chart-controls" style="display: flex; gap: 0.5rem;">
            <button class="period-btn active" data-period="1D" onclick="changePeriod('1D', this)">1일</button>
            <button class="period-btn" data-period="1W" onclick="changePeriod('1W', this)">1주</button>
            <button class="period-btn" data-period="1M" onclick="changePeriod('1M', this)">1개월</button>
            <button class="period-btn" data-period="3M" onclick="changePeriod('3M', this)">3개월</button>
            <button class="period-btn" data-period="1Y" onclick="changePeriod('1Y', this)">1년</button>
        </div>
    </div>
    
    <!-- TradingView 차트 (한국/미국 통합) -->
    <div id="tradingview-chart" style="height: 500px; position: relative;">
        <c:choose>
            <c:when test="${param.country == 'US'}">
                <!-- 미국 주식 차트 -->
                <div class="tradingview-widget-container" style="height: 100%; width: 100%;">
                    <div id="tradingview_widget" style="height: 100%; width: 100%;"></div>
                </div>
            </c:when>
            <c:otherwise>
                <!-- 한국 주식 차트 -->
                <div class="tradingview-widget-container" style="height: 100%; width: 100%;">
                    <div id="tradingview_widget" style="height: 100%; width: 100%;"></div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    
    <!-- Chart.js 대체 차트 (TradingView 실패 시) -->
    <div id="fallback-chart" style="display: none; height: 500px;">
        <canvas id="stockPriceChart"></canvas>
    </div>
    
</div>

<style>
    .period-btn {
        padding: 0.5rem 1rem;
        border: 2px solid #e5e7eb;
        background: white;
        border-radius: 8px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.2s;
        font-size: 0.875rem;
    }
    
    .period-btn:hover {
        border-color: #667eea;
        color: #667eea;
    }
    
    .period-btn.active {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-color: transparent;
    }
</style>

<!-- TradingView Widget Script -->
<script type="text/javascript" src="https://s3.tradingview.com/tv.js"></script>

<script>
    // 주식 정보
    const stockInfo = {
        code: '${param.stockCode}',
        name: '${param.stockName}',
        country: '${param.country}',
        marketType: '${param.marketType}'
    };
    
    let currentPeriod = '1D';
    
    /**
     * TradingView 차트 초기화
     */
    function initTradingViewChart(interval = 'D') {
        try {
            let symbol = stockInfo.code;
            
            // 국가/시장별 심볼 포맷 조정
            if (stockInfo.country === 'US') {
                // 미국 주식: NASDAQ:AAPL, NYSE:BA 형식
                symbol = stockInfo.marketType + ':' + stockInfo.code;
            } else if (stockInfo.country === 'KR') {
                // 한국 주식: KRX:005930 형식
                symbol = 'KRX:' + stockInfo.code;
            }
            
            console.log('TradingView 차트 로딩:', symbol);
            
            new TradingView.widget({
                "width": "100%",
                "height": "100%",
                "symbol": symbol,
                "interval": interval,
                "timezone": "Asia/Seoul",
                "theme": "light",
                "style": "1",
                "locale": "kr",
                "toolbar_bg": "#f1f3f6",
                "enable_publishing": false,
                "hide_side_toolbar": false,
                "allow_symbol_change": false,
                "details": true,
                "hotlist": false,
                "calendar": false,
                "container_id": "tradingview_widget",
                "studies": [
                    "MASimple@tv-basicstudies",
                    "Volume@tv-basicstudies"
                ]
            });
            
        } catch (error) {
            console.error('TradingView 차트 로딩 실패:', error);
            // 대체 차트 표시
            showFallbackChart();
        }
    }
    
    /**
     * 대체 Chart.js 차트 표시
     */
    function showFallbackChart() {
        document.getElementById('tradingview-chart').style.display = 'none';
        document.getElementById('fallback-chart').style.display = 'block';
        
        // Chart.js로 간단한 차트 그리기
        const ctx = document.getElementById('stockPriceChart').getContext('2d');
        
        // 더미 데이터 (실제로는 API에서 가져와야 함)
        const dummyData = generateDummyData(30);
        
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: dummyData.labels,
                datasets: [{
                    label: stockInfo.name + ' 주가',
                    data: dummyData.prices,
                    borderColor: '#667eea',
                    backgroundColor: 'rgba(102, 126, 234, 0.1)',
                    borderWidth: 2,
                    tension: 0.1,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    },
                    tooltip: {
                        mode: 'index',
                        intersect: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: false,
                        ticks: {
                            callback: function(value) {
                                return value.toLocaleString() + '원';
                            }
                        }
                    }
                }
            }
        });
    }
    
    /**
     * 더미 데이터 생성 (테스트용)
     */
    function generateDummyData(days) {
        const labels = [];
        const prices = [];
        const basePrice = 50000;
        
        for (let i = days; i >= 0; i--) {
            const date = new Date();
            date.setDate(date.getDate() - i);
            labels.push(date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' }));
            
            const randomChange = (Math.random() - 0.5) * 5000;
            prices.push(basePrice + randomChange);
        }
        
        return { labels, prices };
    }
    
    /**
     * 기간 변경
     */
    function changePeriod(period, btn) {
        currentPeriod = period;
        
        // 버튼 활성화 상태 변경
        document.querySelectorAll('.period-btn').forEach(b => {
            b.classList.remove('active');
        });
        btn.classList.add('active');
        
        // TradingView 인터벌 매핑
        const intervalMap = {
            '1D': '1',      // 1분
            '1W': '15',     // 15분
            '1M': '60',     // 1시간
            '3M': 'D',      // 일봉
            '1Y': 'W'       // 주봉
        };
        
        const interval = intervalMap[period] || 'D';
        
        // 차트 새로고침
        initTradingViewChart(interval);
    }
    
    /**
     * 페이지 로드 시 차트 초기화
     */
    document.addEventListener('DOMContentLoaded', function() {
        // TradingView 스크립트 로딩 대기
        setTimeout(function() {
            if (typeof TradingView !== 'undefined') {
                initTradingViewChart();
            } else {
                console.warn('TradingView 스크립트 로딩 실패, 대체 차트 사용');
                showFallbackChart();
            }
        }, 500);
    });
</script>
