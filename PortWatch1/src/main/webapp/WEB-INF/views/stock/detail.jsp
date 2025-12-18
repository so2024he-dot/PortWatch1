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
    .badge-kospi { background: #dbeafe; color: #1e40af; }
    .badge-kosdaq { background: #f3e8ff; color: #6b21a8; }
    
    .price-section {
        display: flex;
        align-items: center;
        gap: 2rem;
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
    
    /* 탭 네비게이션 */
    .tab-nav {
        display: flex;
        gap: 1rem;
        margin-bottom: 2rem;
        background: white;
        padding: 1rem;
        border-radius: 15px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    }
    
    .tab-btn {
        padding: 0.75rem 1.5rem;
        border: none;
        background: transparent;
        color: #6b7280;
        font-weight: 600;
        font-size: 1rem;
        border-radius: 10px;
        cursor: pointer;
        transition: all 0.3s;
    }
    
    .tab-btn:hover {
        background: #f3f4f6;
        color: #1f2937;
    }
    
    .tab-btn.active {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
    }
    
    .tab-content {
        display: none;
    }
    
    .tab-content.active {
        display: block;
    }
    
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
    
    /* 뉴스 카드 */
    .news-section {
        background: white;
        border-radius: 20px;
        padding: 2rem;
        margin-bottom: 2rem;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    }
    
    .news-title {
        font-size: 1.5rem;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 1.5rem;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .news-list {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }
    
    .news-item {
        padding: 1.5rem;
        border: 2px solid #f3f4f6;
        border-radius: 15px;
        transition: all 0.3s;
        cursor: pointer;
    }
    
    .news-item:hover {
        border-color: #667eea;
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(102, 126, 234, 0.15);
    }
    
    .news-item-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 0.75rem;
    }
    
    .news-item-title {
        font-size: 1.1rem;
        font-weight: 600;
        color: #1f2937;
        margin: 0;
    }
    
    .news-item-time {
        font-size: 0.85rem;
        color: #6b7280;
    }
    
    .news-item-summary {
        color: #4b5563;
        line-height: 1.6;
        margin-bottom: 0.5rem;
    }
    
    .news-source {
        font-size: 0.85rem;
        color: #667eea;
        font-weight: 600;
    }
    
    .empty-news {
        text-align: center;
        padding: 3rem;
        color: #6b7280;
    }
    
    .empty-news i {
        font-size: 3rem;
        opacity: 0.3;
        margin-bottom: 1rem;
    }
    
    /* 정보 그리드 */
    .info-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1.5rem;
        margin-top: 2rem;
    }
    
    .info-card {
        background: #f9fafb;
        border-radius: 12px;
        padding: 1.25rem;
    }
    
    .info-label {
        font-size: 0.85rem;
        color: #6b7280;
        margin-bottom: 0.5rem;
    }
    
    .info-value {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1f2937;
    }
    
    @media (max-width: 768px) {
        .current-price {
            font-size: 2rem;
        }
        
        .price-change {
            font-size: 1.2rem;
        }
        
        .tab-nav {
            overflow-x: auto;
        }
    }
</style>

<div class="stock-detail-container">
    <!-- 주식 헤더 -->
    <div class="stock-header">
        <div class="stock-title-section">
            <div>
                <h1 class="stock-title">
                    ${stock.stockName}
                    <span class="stock-code-badge">${stock.stockCode}</span>
                </h1>
                <div class="mt-2">
                    <span class="market-badge badge-${fn:toLowerCase(stock.marketType)}">
                        ${stock.marketType}
                    </span>
                    <span class="ms-2 text-muted">${stock.industry}</span>
                </div>
            </div>
        </div>
        
        <div class="price-section">
            <div class="current-price">
                <fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0.00" />
                <c:if test="${stock.country == 'US'}">$</c:if>
                <c:if test="${stock.country == 'KR'}">원</c:if>
            </div>
            <div class="price-change ${stock.changeRate >= 0 ? 'price-up' : 'price-down'}">
                <i class="bi bi-arrow-${stock.changeRate >= 0 ? 'up' : 'down'}"></i>
                <fmt:formatNumber value="${stock.changeAmount}" pattern="+#,##0.00;-#,##0.00" />
                (<fmt:formatNumber value="${stock.changeRate}" pattern="+#,##0.00;-#,##0.00" />%)
            </div>
        </div>
        
        <div class="info-grid">
            <div class="info-card">
                <div class="info-label">시가</div>
                <div class="info-value">
                    <fmt:formatNumber value="${stock.openPrice}" pattern="#,##0.00" />
                </div>
            </div>
            <div class="info-card">
                <div class="info-label">고가</div>
                <div class="info-value">
                    <fmt:formatNumber value="${stock.highPrice}" pattern="#,##0.00" />
                </div>
            </div>
            <div class="info-card">
                <div class="info-label">저가</div>
                <div class="info-value">
                    <fmt:formatNumber value="${stock.lowPrice}" pattern="#,##0.00" />
                </div>
            </div>
            <div class="info-card">
                <div class="info-label">거래량</div>
                <div class="info-value">
                    <fmt:formatNumber value="${stock.volume}" pattern="#,##0" />
                </div>
            </div>
        </div>
    </div>
    
    <!-- 탭 네비게이션 -->
    <div class="tab-nav">
        <button class="tab-btn active" onclick="switchTab('chart')">
            <i class="bi bi-graph-up me-2"></i>차트
        </button>
        <button class="tab-btn" onclick="switchTab('news')">
            <i class="bi bi-newspaper me-2"></i>뉴스
        </button>
        <button class="tab-btn" onclick="switchTab('info')">
            <i class="bi bi-info-circle me-2"></i>기업정보
        </button>
    </div>
    
    <!-- 차트 탭 -->
    <div id="tab-chart" class="tab-content active">
        <div class="chart-card">
            <h3 class="chart-title">
                <i class="bi bi-graph-up-arrow"></i> 가격 추이
            </h3>
            <div class="chart-container">
                <canvas id="priceChart"></canvas>
            </div>
        </div>
    </div>
    
    <!-- 뉴스 탭 -->
    <div id="tab-news" class="tab-content">
        <div class="news-section">
            <h3 class="news-title">
                <i class="bi bi-newspaper"></i> 관련 뉴스
            </h3>
            
            <c:choose>
                <c:when test="${not empty newsList}">
                    <div class="news-list">
                        <c:forEach items="${newsList}" var="news" varStatus="status">
                            <c:if test="${status.index < 10}">
                                <div class="news-item" onclick="window.open('${news.link}', '_blank')">
                                    <div class="news-item-header">
                                        <h4 class="news-item-title">${news.title}</h4>
                                        <span class="news-item-time">
                                            <fmt:formatDate value="${news.publishedDate}" pattern="MM-dd HH:mm" />
                                        </span>
                                    </div>
                                    <p class="news-item-summary">${news.summary}</p>
                                    <div class="news-source">
                                        <i class="bi bi-link-45deg me-1"></i>원문보기
                                    </div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-news">
                        <i class="bi bi-inbox"></i>
                        <h4>관련 뉴스가 없습니다</h4>
                        <p>해당 종목의 뉴스가 아직 수집되지 않았습니다</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <!-- 기업정보 탭 -->
    <div id="tab-info" class="tab-content">
        <div class="chart-card">
            <h3 class="chart-title">
                <i class="bi bi-building"></i> 기업 정보
            </h3>
            
            <div class="info-grid">
                <div class="info-card">
                    <div class="info-label">종목코드</div>
                    <div class="info-value">${stock.stockCode}</div>
                </div>
                <div class="info-card">
                    <div class="info-label">시장</div>
                    <div class="info-value">${stock.marketType}</div>
                </div>
                <div class="info-card">
                    <div class="info-label">산업</div>
                    <div class="info-value">${stock.industry}</div>
                </div>
                <div class="info-card">
                    <div class="info-label">국가</div>
                    <div class="info-value">${stock.country == 'KR' ? '한국' : '미국'}</div>
                </div>
                <div class="info-card">
                    <div class="info-label">현재가</div>
                    <div class="info-value">
                        <fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0.00" />
                    </div>
                </div>
                <div class="info-card">
                    <div class="info-label">등락률</div>
                    <div class="info-value ${stock.changeRate >= 0 ? 'price-up' : 'price-down'}">
                        <fmt:formatNumber value="${stock.changeRate}" pattern="+#,##0.00;-#,##0.00" />%
                    </div>
                </div>
                <div class="info-card">
                    <div class="info-label">거래량</div>
                    <div class="info-value">
                        <fmt:formatNumber value="${stock.volume}" pattern="#,##0" />
                    </div>
                </div>
                <div class="info-card">
                    <div class="info-label">최종 업데이트</div>
                    <div class="info-value">
                        <fmt:formatDate value="${stock.updatedAt}" pattern="MM-dd HH:mm" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Chart.js 스크립트 -->
<script>
    console.log('=== 주식 차트 초기화 ===');
    
    // 가격 데이터 (샘플 - 실제로는 DB에서 가져와야 함)
    const priceData = {
        labels: ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00'],
        prices: [
            ${stock.currentPrice * 0.98},
            ${stock.currentPrice * 0.99},
            ${stock.currentPrice * 1.00},
            ${stock.currentPrice * 0.97},
            ${stock.currentPrice * 0.99},
            ${stock.currentPrice * 1.01},
            ${stock.currentPrice * 1.00},
            ${stock.currentPrice}
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
                                return '가격: ' + context.parsed.y.toLocaleString() + '${stock.country == "US" ? "$" : "원"}';
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: false,
                        ticks: {
                            callback: function(value) {
                                return value.toLocaleString() + '${stock.country == "US" ? "$" : "원"}';
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
    
    // 탭 전환
    function switchTab(tabName) {
        // 모든 탭 버튼 비활성화
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        
        // 모든 탭 콘텐츠 숨기기
        document.querySelectorAll('.tab-content').forEach(content => {
            content.classList.remove('active');
        });
        
        // 선택된 탭 활성화
        event.target.classList.add('active');
        document.getElementById('tab-' + tabName).classList.add('active');
    }
</script>

<jsp:include page="../common/footer.jsp" />
