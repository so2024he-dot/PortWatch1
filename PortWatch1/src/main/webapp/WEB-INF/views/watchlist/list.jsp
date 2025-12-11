<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>⭐ 관심종목 - PortWatch</title>
<style>
* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
}

body {
	font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	min-height: 100vh;
	padding: 20px;
}

.container {
	max-width: 1400px;
	margin: 0 auto;
}

.header {
	background: white;
	border-radius: 15px;
	padding: 30px;
	margin-bottom: 20px;
	box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.header h1 {
	color: #667eea;
	margin-bottom: 10px;
	font-size: 2em;
}

.header .stats {
	display: flex;
	gap: 20px;
	margin-top: 15px;
}

.stat-box {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
	padding: 15px 25px;
	border-radius: 10px;
	text-align: center;
}

.stat-box .label {
	font-size: 0.9em;
	opacity: 0.9;
	margin-bottom: 5px;
}

.stat-box .value {
	font-size: 1.8em;
	font-weight: bold;
}

.controls {
	background: white;
	border-radius: 15px;
	padding: 20px;
	margin-bottom: 20px;
	box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.filter-buttons button {
	background: #667eea;
	color: white;
	border: none;
	padding: 10px 20px;
	border-radius: 8px;
	margin-right: 10px;
	cursor: pointer;
	transition: all 0.3s;
}

.filter-buttons button:hover {
	background: #764ba2;
	transform: translateY(-2px);
}

.filter-buttons button.active {
	background: #764ba2;
	box-shadow: 0 5px 15px rgba(118, 75, 162, 0.4);
}

.refresh-btn {
	background: #28a745;
	color: white;
	border: none;
	padding: 10px 20px;
	border-radius: 8px;
	cursor: pointer;
	display: flex;
	align-items: center;
	gap: 8px;
	transition: all 0.3s;
}

.refresh-btn:hover {
	background: #218838;
	transform: translateY(-2px);
}

.watchlist-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
	gap: 20px;
}

.stock-card {
	background: white;
	border-radius: 15px;
	padding: 20px;
	box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
	transition: all 0.3s;
	position: relative;
}

.stock-card:hover {
	transform: translateY(-5px);
	box-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
}

.stock-card.korean {
	border-top: 4px solid #764ba2;
}

.stock-card.us {
	border-top: 4px solid #4facfe;
}

.stock-header {
	display: flex;
	justify-content: space-between;
	align-items: start;
	margin-bottom: 15px;
}

.stock-info {
	flex: 1;
}

.stock-name {
	font-size: 1.3em;
	font-weight: bold;
	color: #333;
	margin-bottom: 5px;
}

.stock-code {
	color: #666;
	font-size: 0.9em;
}

.market-badge {
	padding: 5px 10px;
	border-radius: 5px;
	font-size: 0.8em;
	font-weight: bold;
}

.market-badge.korean {
	background: #e8d5f5;
	color: #764ba2;
}

.market-badge.us {
	background: #d5f0fe;
	color: #4facfe;
}

.price-section {
	margin: 20px 0;
}

.current-price {
	font-size: 2em;
	font-weight: bold;
	margin-bottom: 10px;
}

.current-price.up {
	color: #dc3545;
}

.current-price.down {
	color: #007bff;
}

.current-price.flat {
	color: #6c757d;
}

.price-change {
	display: flex;
	gap: 10px;
	align-items: center;
	margin-bottom: 15px;
}

.change-amount {
	padding: 5px 12px;
	border-radius: 5px;
	font-weight: bold;
	font-size: 0.9em;
}

.change-amount.up {
	background: #ffe0e0;
	color: #dc3545;
}

.change-amount.down {
	background: #d5e8ff;
	color: #007bff;
}

.change-amount.flat {
	background: #f0f0f0;
	color: #6c757d;
}

.price-details {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 10px;
	padding: 15px;
	background: #f8f9fa;
	border-radius: 8px;
}

.price-item {
	display: flex;
	justify-content: space-between;
}

.price-item .label {
	color: #666;
	font-size: 0.9em;
}

.price-item .value {
	font-weight: bold;
	color: #333;
}

.card-actions {
	display: flex;
	gap: 10px;
	margin-top: 15px;
}

.btn {
	flex: 1;
	padding: 10px;
	border: none;
	border-radius: 8px;
	cursor: pointer;
	font-weight: bold;
	transition: all 0.3s;
}

.btn-detail {
	background: #667eea;
	color: white;
}

.btn-detail:hover {
	background: #764ba2;
}

.btn-delete {
	background: #dc3545;
	color: white;
}

.btn-delete:hover {
	background: #c82333;
}

.empty-message {
	background: white;
	border-radius: 15px;
	padding: 60px;
	text-align: center;
	box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.empty-message h2 {
	color: #667eea;
	margin-bottom: 15px;
}

.empty-message p {
	color: #666;
	font-size: 1.1em;
}

.loading {
	text-align: center;
	padding: 40px;
	background: white;
	border-radius: 15px;
	box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.spinner {
	border: 4px solid #f3f3f3;
	border-top: 4px solid #667eea;
	border-radius: 50%;
	width: 40px;
	height: 40px;
	animation: spin 1s linear infinite;
	margin: 0 auto 20px;
}

@
keyframes spin { 0% {
	transform: rotate(0deg);
}

100
%
{
transform
:
rotate(
360deg
);
}
}
.trade-date {
	color: #999;
	font-size: 0.85em;
	margin-top: 5px;
}
</style>
</head>
<body>
	<div class="container">
		<!-- 헤더 -->
		<div class="header">
			<h1>⭐ 나의 관심종목</h1>
			<div class="stats">
				<div class="stat-box">
					<div class="label">전체</div>
					<div class="value">${totalCount}</div>
				</div>
				<div class="stat-box">
					<div class="label">🇰🇷 한국</div>
					<div class="value">${koreanStockCount}</div>
				</div>
				<div class="stat-box">
					<div class="label">🇺🇸 미국</div>
					<div class="value">${usStockCount}</div>
				</div>
			</div>
		</div>

		<!-- 컨트롤 -->
		<div class="controls">
			<div class="filter-buttons">
				<button class="active" onclick="filterStocks('all')">전체 보기</button>
				<button onclick="filterStocks('korean')">🇰🇷 한국 주식</button>
				<button onclick="filterStocks('us')">🇺🇸 미국 주식</button>
			</div>
			<button class="refresh-btn" onclick="refreshPrices()">
				<span>🔄</span> <span>새로고침</span>
			</button>
		</div>

		<!-- 관심종목 그리드 -->
		<c:choose>
			<c:when test="${empty watchlist}">
				<div class="empty-message">
					<h2>📭 관심종목이 비어있습니다</h2>
					<p>주식 목록에서 관심종목을 추가해보세요!</p>
				</div>
			</c:when>
			<c:otherwise>
				<div class="watchlist-grid" id="watchlistGrid">
					<c:forEach var="item" items="${watchlist}">
						<div class="stock-card ${item.koreanStock ? 'korean' : 'us'}"
							data-market="${item.koreanStock ? 'korean' : 'us'}">

							<!-- 헤더 -->
							<div class="stock-header">
								<div class="stock-info">
									<div class="stock-name">${item.stockName}</div>
									<div class="stock-code">${item.stockCode}</div>
								</div>
								<span class="market-badge ${item.koreanStock ? 'korean' : 'us'}">
									${item.marketType} </span>
							</div>

							<!-- 가격 섹션 -->
							<div class="price-section">
								<c:choose>
									<c:when test="${item.currentPrice != null}">
										<!-- 현재가 -->
										<div
											class="current-price ${item.changeDirection == 'UP' ? 'up' : item.changeDirection == 'DOWN' ? 'down' : 'flat'}">
											<c:choose>
												<c:when test="${item.koreanStock}">
                                                    ₩<fmt:formatNumber
														value="${item.currentPrice}" pattern="#,##0" />
												</c:when>
												<c:otherwise>
                                                    $<fmt:formatNumber
														value="${item.currentPrice}" pattern="#,##0.00" />
												</c:otherwise>
											</c:choose>
										</div>

										<!-- 변동 정보 -->
										<c:if test="${item.priceChange != null}">
											<div class="price-change">
												<span
													class="change-amount ${item.changeDirection == 'UP' ? 'up' : item.changeDirection == 'DOWN' ? 'down' : 'flat'}">
													<c:choose>
														<c:when test="${item.changeDirection == 'UP'}">▲</c:when>
														<c:when test="${item.changeDirection == 'DOWN'}">▼</c:when>
														<c:otherwise>-</c:otherwise>
													</c:choose> <c:choose>
														<c:when test="${item.koreanStock}">
															<fmt:formatNumber value="${item.priceChange}"
																pattern="#,##0" />
														</c:when>
														<c:otherwise>
															<fmt:formatNumber value="${item.priceChange}"
																pattern="#,##0.00" />
														</c:otherwise>
													</c:choose>
												</span> <span
													class="change-amount ${item.changeDirection == 'UP' ? 'up' : item.changeDirection == 'DOWN' ? 'down' : 'flat'}">
													<fmt:formatNumber value="${item.changePercent}"
														pattern="#,##0.00" />%
												</span>
											</div>
										</c:if>

										<!-- 상세 가격 정보 -->
										<div class="price-details">
											<div class="price-item">
												<span class="label">시가</span> <span class="value"> <c:choose>
														<c:when test="${item.koreanStock}">
                                                            ₩<fmt:formatNumber
																value="${item.openPrice}" pattern="#,##0" />
														</c:when>
														<c:otherwise>
                                                            $<fmt:formatNumber
																value="${item.openPrice}" pattern="#,##0.00" />
														</c:otherwise>
													</c:choose>
												</span>
											</div>
											<div class="price-item">
												<span class="label">고가</span> <span class="value"> <c:choose>
														<c:when test="${item.koreanStock}">
                                                            ₩<fmt:formatNumber
																value="${item.highPrice}" pattern="#,##0" />
														</c:when>
														<c:otherwise>
                                                            $<fmt:formatNumber
																value="${item.highPrice}" pattern="#,##0.00" />
														</c:otherwise>
													</c:choose>
												</span>
											</div>
											<div class="price-item">
												<span class="label">저가</span> <span class="value"> <c:choose>
														<c:when test="${item.koreanStock}">
                                                            ₩<fmt:formatNumber
																value="${item.lowPrice}" pattern="#,##0" />
														</c:when>
														<c:otherwise>
                                                            $<fmt:formatNumber
																value="${item.lowPrice}" pattern="#,##0.00" />
														</c:otherwise>
													</c:choose>
												</span>
											</div>
											<div class="price-item">
												<span class="label">거래량</span> <span class="value"> <fmt:formatNumber
														value="${item.volume}" pattern="#,##0" />
												</span>
											</div>
										</div>

										<!-- 거래일 -->
										<div class="trade-date">
											<fmt:formatDate value="${item.tradeDate}"
												pattern="yyyy-MM-dd" />
											기준
										</div>
									</c:when>
									<c:otherwise>
										<div class="current-price flat">가격 정보 없음</div>
										<p style="color: #999; font-size: 0.9em; margin-top: 10px;">
											주가 데이터를 업데이트해주세요</p>
									</c:otherwise>
								</c:choose>
							</div>

							<!-- 액션 버튼 -->
							<div class="card-actions">
								<button class="btn btn-detail"
									onclick="goToDetail('${item.stockCode}')">상세보기</button>
								<button class="btn btn-delete"
									onclick="removeFromWatchlist(${item.watchlistId})">삭제
								</button>
							</div>
						</div>
					</c:forEach>
				</div>
			</c:otherwise>
		</c:choose>
	</div>

	<script>
        // 필터 버튼 활성화
        function filterStocks(market) {
            const cards = document.querySelectorAll('.stock-card');
            const buttons = document.querySelectorAll('.filter-buttons button');
            
            // 버튼 활성화
            buttons.forEach(btn => btn.classList.remove('active'));
            event.target.classList.add('active');
            
            // 카드 필터링
            cards.forEach(card => {
                if (market === 'all') {
                    card.style.display = 'block';
                } else {
                    if (card.getAttribute('data-market') === market) {
                        card.style.display = 'block';
                    } else {
                        card.style.display = 'none';
                    }
                }
            });
        }
        
        // 상세보기
        function goToDetail(stockCode) {
            window.location.href = '${pageContext.request.contextPath}/stock/detail/' + stockCode;
        }
        
        // 관심종목 삭제
        function removeFromWatchlist(watchlistId) {
            if (!confirm('관심종목에서 삭제하시겠습니까?')) {
                return;
            }
            
            fetch('${pageContext.request.contextPath}/watchlist/delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: 'watchlistId=' + watchlistId
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    location.reload();
                } else {
                    alert(data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('삭제 중 오류가 발생했습니다');
            });
        }
        
        // 페이지 새로고침
        function refreshPrices() {
            location.reload();
        }
        
        // 3분마다 자동 새로고침 (선택사항)
        // setInterval(refreshPrices, 180000);
    </script>
</body>
</html>

    
