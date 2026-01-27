<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../common/header.jsp" />

<style>
    .container {
        max-width: 1400px;
        margin: 0 auto;
        padding: 2rem;
    }
    
    .back-button {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        color: #6b7280;
        text-decoration: none;
        margin-bottom: 1.5rem;
        font-weight: 500;
        transition: all 0.3s;
    }
    
    .back-button:hover {
        color: #667eea;
        transform: translateX(-5px);
    }
    
    .stock-header-card {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 20px;
        padding: 2.5rem;
        color: white;
        margin-bottom: 2rem;
        box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
    }
    
    .stock-title-section {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 2rem;
    }
    
    .stock-title {
        font-size: 2.5rem;
        font-weight: 700;
        margin-bottom: 0.5rem;
    }
    
    .stock-code-badge {
        display: inline-block;
        background: rgba(255, 255, 255, 0.2);
        padding: 0.5rem 1rem;
        border-radius: 8px;
        font-size: 1rem;
        font-weight: 600;
        margin-right: 0.5rem;
    }
    
    .market-badge {
        display: inline-block;
        background: rgba(255, 255, 255, 0.2);
        padding: 0.5rem 1rem;
        border-radius: 8px;
        font-size: 0.875rem;
        font-weight: 600;
    }
    
    .action-buttons {
        display: flex;
        gap: 1rem;
    }
    
    .btn-action {
        padding: 0.75rem 1.5rem;
        border-radius: 10px;
        border: 2px solid white;
        background: rgba(255, 255, 255, 0.1);
        color: white;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .btn-action:hover {
        background: white;
        color: #667eea;
    }
    
    .price-section {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1.5rem;
    }
    
    .price-item {
        background: rgba(255, 255, 255, 0.1);
        padding: 1.5rem;
        border-radius: 12px;
        backdrop-filter: blur(10px);
    }
    
    .price-label {
        font-size: 0.875rem;
        opacity: 0.9;
        margin-bottom: 0.5rem;
    }
    
    .price-value {
        font-size: 1.5rem;
        font-weight: 700;
    }
    
    .current-price-large {
        font-size: 3rem;
        font-weight: 700;
    }
    
    .price-change {
        font-size: 1.25rem;
        font-weight: 600;
        margin-top: 0.5rem;
    }
    
    .info-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 1.5rem;
        margin-bottom: 2rem;
    }
    
    .info-card {
        background: white;
        border-radius: 16px;
        padding: 2rem;
        box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
    }
    
    .info-card-title {
        font-size: 1.25rem;
        font-weight: 700;
        color: #1f2937;
        margin-bottom: 1.5rem;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .info-row {
        display: flex;
        justify-content: space-between;
        padding: 0.75rem 0;
        border-bottom: 1px solid #f3f4f6;
    }
    
    .info-row:last-child {
        border-bottom: none;
    }
    
    .info-label {
        color: #6b7280;
        font-weight: 500;
    }
    
    .info-value {
        color: #1f2937;
        font-weight: 600;
    }
    
    .chart-section {
        background: white;
        border-radius: 16px;
        padding: 2rem;
        box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
        margin-bottom: 2rem;
    }
    
    .chart-container {
        width: 100%;
        height: 500px;
        position: relative;
    }
    
    .news-section {
        background: white;
        border-radius: 16px;
        padding: 2rem;
        box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
    }
    
    .section-title {
        font-size: 1.5rem;
        font-weight: 700;
        color: #1f2937;
        margin-bottom: 1.5rem;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .news-item {
        padding: 1.5rem;
        border-bottom: 1px solid #f3f4f6;
        transition: all 0.3s;
        cursor: pointer;
    }
    
    .news-item:last-child {
        border-bottom: none;
    }
    
    .news-item:hover {
        background: #f9fafb;
        transform: translateX(5px);
    }
    
    .news-title {
        font-size: 1.1rem;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 0.5rem;
        line-height: 1.5;
    }
    
    .news-meta {
        display: flex;
        gap: 1rem;
        font-size: 0.875rem;
        color: #6b7280;
    }
    
    .news-source {
        font-weight: 500;
    }
    
    .alert {
        padding: 1rem 1.5rem;
        border-radius: 12px;
        margin-bottom: 1.5rem;
    }
    
    .alert-info {
        background: #dbeafe;
        color: #1e40af;
        border-left: 4px solid #3b82f6;
    }
    
    .alert-danger {
        background: #fee2e2;
        color: #991b1b;
        border-left: 4px solid #ef4444;
    }
    
    @media (max-width: 768px) {
        .stock-title-section {
            flex-direction: column;
            gap: 1rem;
        }
        
        .stock-title {
            font-size: 1.75rem;
        }
        
        .current-price-large {
            font-size: 2rem;
        }
        
        .action-buttons {
            width: 100%;
        }
        
        .btn-action {
            flex: 1;
        }
        
        .chart-container {
            height: 400px;
        }
    }
</style>

<!-- TradingView Widget Script -->
<script type="text/javascript" src="https://s3.tradingview.com/tv.js"></script>

<div class="container">
    <!-- Back Button -->
    <a href="${pageContext.request.contextPath}/stock/list" class="back-button">
        <i class="bi bi-arrow-left"></i>
        종목 목록으로 돌아가기
    </a>
    
    <c:choose>
        <c:when test="${not empty stock}">
            <!-- Stock Header Card -->
            <div class="stock-header-card">
                <div class="stock-title-section">
                    <div>
                        <div class="stock-title">${stock.stockName}</div>
                        <div>
                            <span class="stock-code-badge">${stock.stockCode}</span>
                            <span class="market-badge">${stock.marketType}</span>
                        </div>
                    </div>
                    <div class="action-buttons">
                        <button class="btn-action" onclick="addToWatchlist('${stock.stockCode}')">
                            <i class="bi bi-star"></i>
                            관심종목
                        </button>
                        <button class="btn-action" onclick="addToPortfolio('${stock.stockCode}')">
                            <i class="bi bi-plus-circle"></i>
                            포트폴리오
                        </button>
                    </div>
                </div>
                
                <!-- Price Section -->
                <div class="price-section">
                    <div class="price-item" style="grid-column: span 2;">
                        <div class="price-label">현재가</div>
                        <div class="current-price-large">
                            <c:choose>
                                <c:when test="${not empty stock.currentPrice}">
                                    <fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0"/>원
                                </c:when>
                                <c:otherwise>
                                    -
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="price-change">
                            <c:if test="${not empty stock.priceChange}">
                                <c:choose>
                                    <c:when test="${stock.priceChange > 0}">
                                        ▲ <fmt:formatNumber value="${stock.priceChange}" pattern="#,##0"/>
                                        (<fmt:formatNumber value="${stock.priceChangeRate}" pattern="#,##0.00"/>%)
                                    </c:when>
                                    <c:when test="${stock.priceChange < 0}">
                                        ▼ <fmt:formatNumber value="${stock.priceChange * -1}" pattern="#,##0"/>
                                        (<fmt:formatNumber value="${stock.priceChangeRate * -1}" pattern="#,##0.00"/>%)
                                    </c:when>
                                    <c:otherwise>
                                        - (0.00%)
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </div>
                    </div>
                    
                    <div class="price-item">
                        <div class="price-label">거래량</div>
                        <div class="price-value">
                            <c:choose>
                                <c:when test="${not empty stock.volume}">
                                    <fmt:formatNumber value="${stock.volume}" pattern="#,##0"/>
                                </c:when>
                                <c:otherwise>
                                    -
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    
                    <div class="price-item">
                        <div class="price-label">거래대금</div>
                        <div class="price-value">
                            <c:choose>
                                <c:when test="${not empty stock.tradingValue}">
                                    <fmt:formatNumber value="${stock.tradingValue / 100000000}" pattern="#,##0"/>억
                                </c:when>
                                <c:otherwise>
                                    -
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Info Grid -->
            <div class="info-grid">
                <!-- 가격 정보 -->
                <div class="info-card">
                    <div class="info-card-title">
                        <i class="bi bi-graph-up"></i>
                        가격 정보
                    </div>
                    <div class="info-row">
                        <span class="info-label">시가</span>
                        <span class="info-value">
                            <c:choose>
                                <c:when test="${not empty stock.openPrice}">
                                    <fmt:formatNumber value="${stock.openPrice}" pattern="#,##0"/>원
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">고가</span>
                        <span class="info-value">
                            <c:choose>
                                <c:when test="${not empty stock.highPrice}">
                                    <fmt:formatNumber value="${stock.highPrice}" pattern="#,##0"/>원
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">저가</span>
                        <span class="info-value">
                            <c:choose>
                                <c:when test="${not empty stock.lowPrice}">
                                    <fmt:formatNumber value="${stock.lowPrice}" pattern="#,##0"/>원
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">종가</span>
                        <span class="info-value">
                            <c:choose>
                                <c:when test="${not empty stock.closePrice}">
                                    <fmt:formatNumber value="${stock.closePrice}" pattern="#,##0"/>원
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </div>
                
                <!-- 종목 정보 -->
                <div class="info-card">
                    <div class="info-card-title">
                        <i class="bi bi-info-circle"></i>
                        종목 정보
                    </div>
                    <div class="info-row">
                        <span class="info-label">종목코드</span>
                        <span class="info-value">${stock.stockCode}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">시장</span>
                        <span class="info-value">${stock.marketType}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">업종</span>
                        <span class="info-value">
                            <c:choose>
                                <c:when test="${not empty stock.industry}">
                                    ${stock.industry}
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">업데이트</span>
                        <span class="info-value">
                            <c:choose>
                                <c:when test="${not empty stock.updatedAt}">
                                    <fmt:formatDate value="${stock.updatedAt}" pattern="yyyy-MM-dd HH:mm"/>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </div>
            </div>
            
            <!-- Chart Section -->
            <div class="chart-section">
                <div class="info-card-title">
                    <i class="bi bi-bar-chart-line"></i>
                    가격 차트
                </div>
                <div class="chart-container" id="tradingview_chart"></div>
            </div>
            
            <!-- News Section -->
            <div class="news-section">
                <div class="section-title">
                    <i class="bi bi-newspaper"></i>
                    관련 뉴스
                </div>
                
                <c:choose>
                    <c:when test="${not empty newsList}">
                        <c:forEach var="news" items="${newsList}">
                            <div class="news-item" onclick="window.open('${news.newsUrl}', '_blank')">
                                <div class="news-title">${news.newsTitle}</div>
                                <div class="news-meta">
                                    <span class="news-source">
                                        <i class="bi bi-building"></i> ${news.newsSource}
                                    </span>
                                    <span class="news-date">
                                        <i class="bi bi-clock"></i>
                                        <fmt:formatDate value="${news.newsPubDate}" pattern="yyyy-MM-dd HH:mm"/>
                                    </span>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-info">
                            <i class="bi bi-info-circle me-2"></i>
                            현재 표시할 뉴스가 없습니다.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            
        </c:when>
        <c:otherwise>
            <!-- Error State -->
            <div class="alert alert-danger">
                <i class="bi bi-exclamation-triangle me-2"></i>
                종목 정보를 불러올 수 없습니다.
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- TradingView 위젯 스크립트 -->
<script type="text/javascript" src="https://s3.tradingview.com/tv.js"></script>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
// 차트 초기화 - $(document).ready 내부에서 실행
$(document).ready(function() {
    console.log('=== 종목 상세 페이지 로드 ===');
    console.log('종목코드: ${stock.stockCode}');
    console.log('종목명: ${stock.stockName}');
    console.log('뉴스 개수: ${not empty newsList ? newsList.size() : 0}개');
    
    // 종목 코드
    var stockCode = '${stock.stockCode}';
    
    // TradingView 차트 초기화
    try {
        console.log('차트 초기화 시작 - 종목:', stockCode);
        
        new TradingView.widget({
            "width": "100%",
            "height": 500,
            "symbol": "KRX:" + stockCode,  // 한국거래소 심볼 형식
            "interval": "D",
            "timezone": "Asia/Seoul",
            "theme": "light",
            "style": "1",
            "locale": "kr",
            "toolbar_bg": "#f1f3f6",
            "enable_publishing": false,
            "allow_symbol_change": true,
            "container_id": "tradingview_chart",
            "hide_side_toolbar": false,
            "studies": [
                "MASimple@tv-basicstudies"  // 이동평균선 추가
            ],
            "autosize": true
        });
        
        console.log('✅ 차트 위젯 생성 완료');
    } catch (error) {
        console.error('❌ 차트 로드 실패:', error);
        // 차트 로드 실패 시 메시지 표시
        $('#tradingview_chart').html(
            '<div style="display: flex; align-items: center; justify-content: center; height: 100%; color: #999;">' +
            '<div style="text-align: center;">' +
            '<i class="bi bi-graph-up" style="font-size: 3rem;"></i><br>' +
            '<p style="margin-top: 1rem;">차트를 불러올 수 없습니다.</p>' +
            '<p style="font-size: 0.875rem;">종목코드: ' + stockCode + '</p>' +
            '</div></div>'
        );
    }
});

function addToWatchlist(stockCode) {
    // 로그인 체크
    <c:choose>
        <c:when test="${empty sessionScope.member}">
            if (confirm('로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?')) {
                location.href = '${pageContext.request.contextPath}/member/login';
            }
            return;
        </c:when>
        <c:otherwise>
            $.ajax({
                url: '${pageContext.request.contextPath}/watchlist/add',
                type: 'POST',
                data: { stockCode: stockCode },
                success: function(response) {
                    if (response.success) {
                        if (confirm('관심종목에 추가되었습니다. 관심종목 페이지로 이동하시겠습니까?')) {
                            location.href = '${pageContext.request.contextPath}/watchlist/list';
                        }
                    } else {
                        alert(response.message || '이미 관심종목에 등록된 종목입니다.');
                    }
                },
                error: function(xhr) {
                    console.error('Error:', xhr);
                    alert('관심종목 추가 중 오류가 발생했습니다.');
                }
            });
        </c:otherwise>
    </c:choose>
}

function addToPortfolio(stockCode) {
    // 로그인 체크
    <c:choose>
        <c:when test="${empty sessionScope.member}">
            if (confirm('로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?')) {
                location.href = '${pageContext.request.contextPath}/member/login';
            }
            return;
        </c:when>
        <c:otherwise>
            // 포트폴리오 목록 페이지로 이동하여 선택하게 함
            if (confirm('포트폴리오에 종목을 추가하시겠습니까?')) {
                location.href = '${pageContext.request.contextPath}/portfolio/list?addStock=' + stockCode;
            }
        </c:otherwise>
    </c:choose>
}
</script>

<jsp:include page="../common/footer.jsp" />
