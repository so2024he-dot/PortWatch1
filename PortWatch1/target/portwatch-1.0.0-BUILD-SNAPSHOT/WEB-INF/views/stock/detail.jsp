<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${stock.stockName} (${stock.stockCode}) - PortWatch</title>
    
    <!-- CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">
    
    <style>
        /* 주식 상세 페이지 스타일 */
        .stock-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px 0;
            margin-bottom: 30px;
        }
        
        .stock-title {
            font-size: 32px;
            font-weight: bold;
            margin-bottom: 10px;
        }
        
        .stock-code {
            font-size: 18px;
            opacity: 0.9;
        }
        
        .price-info {
            background: white;
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        
        .current-price {
            font-size: 48px;
            font-weight: bold;
            color: #212529;
        }
        
        .price-change {
            font-size: 24px;
            font-weight: 600;
            margin-top: 10px;
        }
        
        .price-change.positive {
            color: #dc3545;
        }
        
        .price-change.negative {
            color: #007bff;
        }
        
        .stock-info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        
        .info-item {
            padding: 15px;
            background: #f8f9fa;
            border-radius: 8px;
        }
        
        .info-label {
            font-size: 14px;
            color: #6c757d;
            margin-bottom: 5px;
        }
        
        .info-value {
            font-size: 18px;
            font-weight: 600;
            color: #212529;
        }
        
        /* 차트 영역 */
        .chart-container {
            background: white;
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        
        .chart-controls {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
        }
        
        .chart-period-btn {
            padding: 8px 20px;
            border: 2px solid #dee2e6;
            background: white;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .chart-period-btn:hover {
            border-color: #007bff;
            background: #f8f9fa;
        }
        
        .chart-period-btn.active {
            background: #007bff;
            color: white;
            border-color: #007bff;
        }
        
        #stockChart {
            width: 100%;
            height: 400px;
        }
        
        /* 뉴스 영역 */
        .news-section {
            background: white;
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.1);
        }
        
        .section-title {
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #dee2e6;
        }
        
        /* 매수 버튼 */
        .buy-button-container {
            position: fixed;
            bottom: 30px;
            right: 30px;
            z-index: 1000;
        }
        
        .btn-buy {
            padding: 15px 40px;
            font-size: 18px;
            font-weight: bold;
            border-radius: 50px;
            background: #28a745;
            color: white;
            border: none;
            box-shadow: 0 4px 16px rgba(40, 167, 69, 0.4);
            transition: all 0.3s;
        }
        
        .btn-buy:hover {
            background: #218838;
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(40, 167, 69, 0.5);
        }
    </style>
</head>
<body>
    <!-- Header -->
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    
    <!-- 주식 헤더 -->
    <div class="stock-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h1 class="stock-title">
                        ${stock.stockName}
                        <span class="badge badge-${stock.country == 'KR' ? 'primary' : 'success'}">
                            ${stock.marketType}
                        </span>
                    </h1>
                    <p class="stock-code">${stock.stockCode} | ${stock.industry}</p>
                </div>
                <div class="col-md-4 text-right">
                    <button class="btn btn-outline-light btn-lg" onclick="addToWatchlist('${stock.stockCode}')">
                        <i class="fas fa-star"></i> 관심종목 추가
                    </button>
                </div>
            </div>
        </div>
    </div>
    
    <!-- 메인 컨텐츠 -->
    <div class="container">
        <div class="row">
            <!-- 왼쪽: 가격 정보 & 차트 -->
            <div class="col-lg-8">
                <!-- 가격 정보 -->
                <div class="price-info">
                    <div class="current-price">
                        <c:choose>
                            <c:when test="${stock.country == 'US'}">
                                $<fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0.00"/>
                            </c:when>
                            <c:otherwise>
                                <fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0"/>원
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <div class="price-change ${stock.priceChangeRate >= 0 ? 'positive' : 'negative'}">
                        <c:choose>
                            <c:when test="${stock.country == 'US'}">
                                ${stock.priceChangeRate >= 0 ? '▲' : '▼'}
                                $<fmt:formatNumber value="${stock.priceChange}" pattern="#,##0.00"/>
                                (<fmt:formatNumber value="${stock.priceChangeRate}" pattern="+#,##0.00;-#,##0.00"/>%)
                            </c:when>
                            <c:otherwise>
                                ${stock.priceChangeRate >= 0 ? '▲' : '▼'}
                                <fmt:formatNumber value="${stock.priceChange}" pattern="#,##0"/>원
                                (<fmt:formatNumber value="${stock.priceChangeRate}" pattern="+#,##0.00;-#,##0.00"/>%)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <!-- 주요 지표 -->
                    <div class="stock-info-grid">
                        <div class="info-item">
                            <div class="info-label">시가</div>
                            <div class="info-value">
                                <c:choose>
                                    <c:when test="${stock.country == 'US'}">
                                        $<fmt:formatNumber value="${stock.openPrice}" pattern="#,##0.00"/>
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatNumber value="${stock.openPrice}" pattern="#,##0"/>원
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        
                        <div class="info-item">
                            <div class="info-label">고가</div>
                            <div class="info-value">
                                <c:choose>
                                    <c:when test="${stock.country == 'US'}">
                                        $<fmt:formatNumber value="${stock.highPrice}" pattern="#,##0.00"/>
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatNumber value="${stock.highPrice}" pattern="#,##0"/>원
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        
                        <div class="info-item">
                            <div class="info-label">저가</div>
                            <div class="info-value">
                                <c:choose>
                                    <c:when test="${stock.country == 'US'}">
                                        $<fmt:formatNumber value="${stock.lowPrice}" pattern="#,##0.00"/>
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatNumber value="${stock.lowPrice}" pattern="#,##0"/>원
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        
                        <div class="info-item">
                            <div class="info-label">거래량</div>
                            <div class="info-value">
                                <fmt:formatNumber value="${stock.volume}" pattern="#,##0"/>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- 차트 -->
                <div class="chart-container">
                    <h3 class="section-title">
                        <i class="fas fa-chart-line"></i> 주가 차트
                    </h3>
                    
                    <!-- 차트 기간 선택 -->
                    <div class="chart-controls">
                        <button class="chart-period-btn active" id="btn-daily" 
                                onclick="changeChartPeriod('daily', '${stock.stockCode}', '${stock.country}')">
                            일봉
                        </button>
                        <button class="chart-period-btn" id="btn-weekly"
                                onclick="changeChartPeriod('weekly', '${stock.stockCode}', '${stock.country}')">
                            주봉
                        </button>
                        <button class="chart-period-btn" id="btn-monthly"
                                onclick="changeChartPeriod('monthly', '${stock.stockCode}', '${stock.country}')">
                            월봉
                        </button>
                        <button class="btn btn-sm btn-outline-secondary ml-auto" 
                                onclick="refreshChart('${stock.stockCode}', '${stock.country}')">
                            <i class="fas fa-sync-alt"></i> 새로고침
                        </button>
                    </div>
                    
                    <!-- 캔버스 -->
                    <canvas id="stockChart"></canvas>
                </div>
                
                <!-- 관련 뉴스 -->
                <div class="news-section">
                    <h3 class="section-title">
                        <i class="fas fa-newspaper"></i> 관련 뉴스
                    </h3>
                    <div id="stock-news-container">
                        <!-- 뉴스가 여기에 동적으로 로드됩니다 -->
                        <p class="text-center text-muted">뉴스를 불러오는 중...</p>
                    </div>
                </div>
            </div>
            
            <!-- 오른쪽: 매수 패널 -->
            <div class="col-lg-4">
                <div class="card">
                    <div class="card-header bg-success text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-shopping-cart"></i> 주식 매수
                        </h5>
                    </div>
                    <div class="card-body">
                        <form id="buyForm">
                            <input type="hidden" id="stockCode" value="${stock.stockCode}">
                            <input type="hidden" id="country" value="${stock.country}">
                            
                            <div class="form-group">
                                <label>종목명</label>
                                <input type="text" class="form-control" value="${stock.stockName}" readonly>
                            </div>
                            
                            <div class="form-group">
                                <label>현재가</label>
                                <input type="text" class="form-control" id="displayPrice" readonly>
                            </div>
                            
                            <div class="form-group">
                                <label>매수 수량</label>
                                <input type="number" class="form-control" id="quantity" 
                                       min="1" value="1" required>
                            </div>
                            
                            <div class="form-group">
                                <label>매수 가격</label>
                                <input type="number" class="form-control" id="buyPrice" 
                                       value="${stock.currentPrice}" step="0.01" required>
                            </div>
                            
                            <div class="alert alert-info">
                                <strong>예상 금액:</strong>
                                <div id="estimatedAmount" class="mt-2 h4">-</div>
                            </div>
                            
                            <button type="button" class="btn btn-success btn-block btn-lg" 
                                    onclick="executeBuy()">
                                <i class="fas fa-check"></i> 매수 주문
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Footer -->
    <jsp:include page="/WEB-INF/views/common/footer.jsp"/>
    
    <!-- JavaScript -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-zoom@1.2.1/dist/chartjs-plugin-zoom.min.js"></script>
    
    <!-- 커스텀 JavaScript -->
    <script src="${pageContext.request.contextPath}/resources/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/unified-stock-chart.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/stock-purchase.js"></script>
    
    <script>
        // ========================================
        // 페이지 로드 시 초기화
        // ========================================
        $(document).ready(function() {
            console.log('📊 주식 상세 페이지 초기화...');
            
            const stockCode = '${stock.stockCode}';
            const country = '${stock.country}';
            const currentPrice = ${stock.currentPrice};
            
            // 가격 포맷팅
            updateDisplayPrice(currentPrice, country);
            
            // 차트 초기화
            initStockChart(stockCode, country);
            
            // 뉴스 로드
            loadStockNews(stockCode);
            
            // 매수 금액 계산 이벤트
            $('#quantity, #buyPrice').on('input', calculateEstimatedAmount);
            
            // 초기 금액 계산
            calculateEstimatedAmount();
        });
        
        // ========================================
        // 가격 표시 업데이트
        // ========================================
        function updateDisplayPrice(price, country) {
            let formatted;
            if (country === 'US') {
                formatted = '$' + Number(price).toFixed(2);
            } else {
                formatted = Number(price).toLocaleString() + '원';
            }
            $('#displayPrice').val(formatted);
        }
        
        // ========================================
        // 예상 금액 계산
        // ========================================
        function calculateEstimatedAmount() {
            const quantity = parseFloat($('#quantity').val()) || 0;
            const price = parseFloat($('#buyPrice').val()) || 0;
            const country = $('#country').val();
            
            const total = quantity * price;
            const commission = total * 0.001; // 0.1% 수수료
            const finalAmount = total + commission;
            
            let formatted;
            if (country === 'US') {
                formatted = '$' + finalAmount.toFixed(2);
            } else {
                formatted = finalAmount.toLocaleString() + '원';
            }
            
            $('#estimatedAmount').text(formatted);
        }
        
        // ========================================
        // 종목별 뉴스 로드
        // ========================================
        function loadStockNews(stockCode) {
            $.ajax({
                url: '/api/news/stock/' + stockCode,
                method: 'GET',
                success: function(response) {
                    if (response.success && response.news.length > 0) {
                        renderStockNews(response.news);
                    } else {
                        $('#stock-news-container').html('<p class="text-muted">관련 뉴스가 없습니다.</p>');
                    }
                },
                error: function() {
                    $('#stock-news-container').html('<p class="text-danger">뉴스를 불러올 수 없습니다.</p>');
                }
            });
        }
        
        function renderStockNews(newsList) {
            const container = $('#stock-news-container');
            container.empty();
            
            newsList.forEach(function(news) {
                const newsHtml = `
                    <div class="news-item mb-3 pb-3 border-bottom">
                        <h6><a href="${news.link}" target="_blank">${news.title}</a></h6>
                        <small class="text-muted">${news.source} | ${formatDate(news.publishedAt)}</small>
                    </div>
                `;
                container.append(newsHtml);
            });
        }
        
        // ========================================
        // 매수 실행
        // ========================================
        function executeBuy() {
            const stockCode = $('#stockCode').val();
            const quantity = $('#quantity').val();
            const price = $('#buyPrice').val();
            
            if (!quantity || quantity <= 0) {
                alert('올바른 수량을 입력해주세요.');
                return;
            }
            
            if (!price || price <= 0) {
                alert('올바른 가격을 입력해주세요.');
                return;
            }
            
            if (confirm('주식을 매수하시겠습니까?')) {
                $.ajax({
                    url: '/api/purchase/execute',
                    method: 'POST',
                    data: {
                        stockCode: stockCode,
                        quantity: quantity,
                        price: price
                    },
                    success: function(response) {
                        if (response.success) {
                            alert('✅ 매수가 완료되었습니다!');
                            // 포트폴리오 페이지로 이동
                            window.location.href = '/portfolio/';
                        } else {
                            alert('❌ ' + response.message);
                        }
                    },
                    error: function() {
                        alert('매수 처리 중 오류가 발생했습니다.');
                    }
                });
            }
        }
        
        // ========================================
        // 관심종목 추가
        // ========================================
        function addToWatchlist(stockCode) {
            $.ajax({
                url: '/api/watchlist/add',
                method: 'POST',
                data: { stockCode: stockCode },
                success: function(response) {
                    if (response.success) {
                        alert('✅ 관심종목에 추가되었습니다!');
                    } else {
                        alert('❌ ' + response.message);
                    }
                },
                error: function() {
                    alert('관심종목 추가에 실패했습니다.');
                }
            });
        }
        
        // ========================================
        // 날짜 포맷팅
        // ========================================
        function formatDate(dateString) {
            if (!dateString) return '';
            const date = new Date(dateString);
            return date.toLocaleDateString('ko-KR');
        }
    </script>
</body>
</html>
