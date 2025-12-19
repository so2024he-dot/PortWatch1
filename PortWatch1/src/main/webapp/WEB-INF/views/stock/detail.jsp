<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="../common/header.jsp" />

<!-- Chart.js 라이브러리 -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

<style>
    .stock-detail-container {
        max-width: 1400px;
        margin: 0 auto;
        padding: 20px;
    }
    
    /* 헤더 카드 */
    .stock-header {
        background: white;
        border-radius: 20px;
        padding: 2rem;
        margin-bottom: 2rem;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    }
    
    .stock-title-section {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 1.5rem;
        flex-wrap: wrap;
        gap: 1rem;
    }
    
    .stock-title {
        font-size: 2rem;
        font-weight: 700;
        color: #1f2937;
        margin: 0;
    }
    
    .stock-code-badge {
        display: inline-block;
        background: #f3f4f6;
        color: #6b7280;
        padding: 0.5rem 1rem;
        border-radius: 10px;
        font-size: 1rem;
        font-weight: 600;
        margin-left: 1rem;
    }
    
    .market-badge {
        display: inline-block;
        padding: 0.5rem 1rem;
        border-radius: 10px;
        font-size: 0.9rem;
        font-weight: 600;
    }
    
    .badge-nasdaq { background: #d1fae5; color: #065f46; }
    .badge-nyse { background: #fef3c7; color: #92400e; }
    .badge-amex { background: #dbeafe; color: #1e40af; }
    .badge-kospi { background: #dbeafe; color: #1e40af; }
    .badge-kosdaq { background: #f3e8ff; color: #6b21a8; }
    
    .country-flag {
        font-size: 1.5rem;
        margin-right: 0.5rem;
    }
    
    .price-section {
        display: flex;
        align-items: center;
        gap: 2rem;
        margin-top: 1rem;
    }
    
    .current-price {
        font-size: 3rem;
        font-weight: 700;
        color: #1f2937;
    }
    
    .price-change {
        font-size: 1.5rem;
        font-weight: 600;
    }
    
    .price-up { color: #dc2626; }
    .price-down { color: #2563eb; }
    
    /* 차트 카드 */
    .chart-card {
        background: white;
        border-radius: 20px;
        padding: 2rem;
        margin-bottom: 2rem;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    }
    
    .chart-title {
        font-size: 1.5rem;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 1.5rem;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .chart-container {
        position: relative;
        height: 400px;
    }
    
    /* 통계 카드 */
    .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1rem;
        margin-top: 2rem;
    }
    
    .stat-card {
        background: #f9fafb;
        padding: 1.5rem;
        border-radius: 12px;
        text-align: center;
    }
    
    .stat-label {
        font-size: 0.875rem;
        color: #6b7280;
        margin-bottom: 0.5rem;
    }
    
    .stat-value {
        font-size: 1.5rem;
        font-weight: 700;
        color: #1f2937;
    }
    
    /* 뉴스 섹션 */
    .news-section {
        margin-top: 2rem;
    }
    
    .news-card {
        background: white;
        border-radius: 15px;
        padding: 1.5rem;
        margin-bottom: 1rem;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        cursor: pointer;
        transition: all 0.3s;
    }
    
    .news-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    }
    
    .news-title {
        font-size: 1.1rem;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 0.5rem;
    }
    
    .news-meta {
        display: flex;
        gap: 1rem;
        font-size: 0.875rem;
        color: #6b7280;
    }
</style>

<div class="stock-detail-container">
    
    <!-- 종목 헤더 -->
    <div class="stock-header">
        <div class="stock-title-section">
            <div>
                <h1 class="stock-title">
                    <!-- ✅ 국기 아이콘 추가 -->
                    <c:choose>
                        <c:when test="${stock.marketType == 'NASDAQ' || stock.marketType == 'NYSE' || stock.marketType == 'AMEX'}">
                            <span class="country-flag">🇺🇸</span>
                        </c:when>
                        <c:otherwise>
                            <span class="country-flag">🇰🇷</span>
                        </c:otherwise>
                    </c:choose>
                    ${stock.stockName}
                    <span class="stock-code-badge">${stock.stockCode}</span>
                </h1>
                <div style="margin-top: 1rem;">
                    <span class="market-badge badge-${fn:toLowerCase(stock.marketType)}">
                        ${stock.marketType}
                    </span>
                    <c:if test="${not empty stock.industry}">
                        <span class="market-badge" style="background: #f3f4f6; color: #6b7280;">
                            ${stock.industry}
                        </span>
                    </c:if>
                </div>
            </div>
            
            <div class="price-section">
                <div class="current-price">
                    <!-- ✅ 미국/한국 주식 통화 단위 구분 -->
                    <c:choose>
                        <c:when test="${stock.marketType == 'NASDAQ' || stock.marketType == 'NYSE' || stock.marketType == 'AMEX'}">
                            $<fmt:formatNumber value="${stock.currentPrice != null ? stock.currentPrice : 100.00}" pattern="#,##0.00"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:formatNumber value="${stock.currentPrice != null ? stock.currentPrice : 50000}" pattern="#,##0"/>원
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="price-change price-up">
                    +2.5%
                </div>
            </div>
        </div>
    </div>
    
    <!-- 가격 차트 -->
    <div class="chart-card">
        <h2 class="chart-title">
            📈 가격 차트
        </h2>
        <div class="chart-container">
            <canvas id="priceChart"></canvas>
        </div>
    </div>
    
    <!-- 주요 통계 -->
    <div class="chart-card">
        <h2 class="chart-title">
            📊 주요 통계
        </h2>
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-label">시가총액</div>
                <div class="stat-value">
                    <c:choose>
                        <c:when test="${stock.marketType == 'NASDAQ' || stock.marketType == 'NYSE' || stock.marketType == 'AMEX'}">
                            $2.5T
                        </c:when>
                        <c:otherwise>
                            500조원
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-label">거래량</div>
                <div class="stat-value">1.2M</div>
            </div>
            <div class="stat-card">
                <div class="stat-label">52주 최고</div>
                <div class="stat-value">
                    <c:choose>
                        <c:when test="${stock.marketType == 'NASDAQ' || stock.marketType == 'NYSE' || stock.marketType == 'AMEX'}">
                            $120.50
                        </c:when>
                        <c:otherwise>
                            65,000원
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-label">52주 최저</div>
                <div class="stat-value">
                    <c:choose>
                        <c:when test="${stock.marketType == 'NASDAQ' || stock.marketType == 'NYSE' || stock.marketType == 'AMEX'}">
                            $85.30
                        </c:when>
                        <c:otherwise>
                            42,000원
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
    
    <!-- 관련 뉴스 -->
    <div class="chart-card news-section">
        <h2 class="chart-title">
            📰 관련 뉴스
        </h2>
        <div id="newsContainer">
            <p style="text-align: center; color: #6b7280; padding: 2rem;">
                뉴스를 불러오는 중...
            </p>
        </div>
    </div>
    
</div>

<!-- ✅ Chart.js 스크립트 - 미국/한국 주식 모두 지원 -->
<script>
    console.log('=== 주식 차트 초기화 ===');
    
    // ✅ 시장 타입 확인
    const marketType = '${stock.marketType}';
    const isUSStock = ['NASDAQ', 'NYSE', 'AMEX'].includes(marketType);
    const currencySymbol = isUSStock ? '$' : '원';
    
    console.log('시장 타입:', marketType);
    console.log('미국 주식:', isUSStock);
    console.log('통화 기호:', currencySymbol);
    
    // ✅ 현재가 (null 체크)
    let currentPrice = ${stock.currentPrice != null ? stock.currentPrice : (isUSStock ? 100.00 : 50000)};
    
    // ✅ 가격 데이터 생성 (샘플)
    const priceData = {
        labels: ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00'],
        prices: [
            currentPrice * 0.98,
            currentPrice * 0.99,
            currentPrice * 1.00,
            currentPrice * 0.97,
            currentPrice * 0.99,
            currentPrice * 1.01,
            currentPrice * 1.00,
            currentPrice
        ]
    };
    
    const ctx = document.getElementById('priceChart');
    
    if (ctx) {
        const gradient = ctx.getContext('2d').createLinearGradient(0, 0, 0, 400);
        gradient.addColorStop(0, 'rgba(102, 126, 234, 0.3)');
        gradient.addColorStop(1, 'rgba(102, 126, 234, 0.01)');
        
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: priceData.labels,
                datasets: [{
                    label: '가격',
                    data: priceData.prices,
                    borderColor: '#667eea',
                    backgroundColor: gradient,
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 4,
                    pointBackgroundColor: '#667eea',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2,
                    pointHoverRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        padding: 12,
                        titleFont: {
                            size: 14,
                            weight: 'bold'
                        },
                        bodyFont: {
                            size: 13
                        },
                        callbacks: {
                            label: function(context) {
                                // ✅ 미국/한국 주식 포맷 구분
                                const value = context.parsed.y;
                                if (isUSStock) {
                                    return '가격: $' + value.toFixed(2);
                                } else {
                                    return '가격: ' + Math.round(value).toLocaleString() + '원';
                                }
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: false,
                        ticks: {
                            callback: function(value) {
                                // ✅ 미국/한국 주식 포맷 구분
                                if (isUSStock) {
                                    return '$' + value.toFixed(2);
                                } else {
                                    return Math.round(value).toLocaleString() + '원';
                                }
                            },
                            font: {
                                size: 12
                            }
                        },
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        }
                    },
                    x: {
                        ticks: {
                            font: {
                                size: 12
                            }
                        },
                        grid: {
                            display: false
                        }
                    }
                },
                interaction: {
                    intersect: false,
                    mode: 'index'
                }
            }
        });
        
        console.log('✅ 차트 생성 완료');
    }
    
    // ✅ 뉴스 로드 (미국/한국 구분)
    function loadNews() {
        const stockCode = '${stock.stockCode}';
        const stockName = '${stock.stockName}';
        
        fetch('/portwatch/news/api/stock?stockCode=' + stockCode)
            .then(response => response.json())
            .then(data => {
                const container = document.getElementById('newsContainer');
                
                if (data.success && data.newsList && data.newsList.length > 0) {
                    container.innerHTML = '';
                    
                    data.newsList.forEach(news => {
                        const newsCard = document.createElement('div');
                        newsCard.className = 'news-card';
                        newsCard.onclick = () => window.open(news.link, '_blank');
                        
                        newsCard.innerHTML = `
                            <div class="news-title">${news.title}</div>
                            <div class="news-meta">
                                <span>📰 ${news.source || '뉴스'}</span>
                                <span>🕒 ${news.publishedAt || '방금 전'}</span>
                            </div>
                        `;
                        
                        container.appendChild(newsCard);
                    });
                } else {
                    container.innerHTML = '<p style="text-align: center; color: #6b7280; padding: 2rem;">뉴스가 없습니다.</p>';
                }
            })
            .catch(error => {
                console.error('뉴스 로드 실패:', error);
                document.getElementById('newsContainer').innerHTML = 
                    '<p style="text-align: center; color: #dc2626; padding: 2rem;">뉴스를 불러오는 중 오류가 발생했습니다.</p>';
            });
    }
    
    // 페이지 로드 시 뉴스 로드
    document.addEventListener('DOMContentLoaded', loadNews);
</script>

<jsp:include page="../common/footer.jsp" />
