<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>⭐ 관심종목 - PortWatch</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <style>
        body {
            background-color: #f8f9fa;
        }
        
        .watchlist-header {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        
        .stock-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 15px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            transition: all 0.3s;
        }
        
        .stock-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        
        .price-up {
            color: #dc3545;
            font-weight: bold;
        }
        
        .price-down {
            color: #0d6efd;
            font-weight: bold;
        }
        
        .btn-buy {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 20px;
            padding: 8px 20px;
            transition: all 0.3s;
        }
        
        .btn-buy:hover {
            transform: scale(1.05);
            color: white;
        }
        
        .btn-remove {
            background: #6c757d;
            color: white;
            border: none;
            border-radius: 20px;
            padding: 8px 20px;
        }
        
        .empty-watchlist {
            text-align: center;
            padding: 60px 20px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .empty-watchlist i {
            font-size: 80px;
            color: #ccc;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="/">📊 PortWatch</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="/stock/list">주식 목록</a></li>
                <li class="nav-item"><a class="nav-link" href="/portfolio">포트폴리오</a></li>
                <li class="nav-item"><a class="nav-link active" href="/watchlist">관심종목</a></li>
                <li class="nav-item"><a class="nav-link" href="/payment/history">결제 내역</a></li>
                <li class="nav-item"><a class="nav-link" href="/member/logout">로그아웃</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container mt-4">
    
    <!-- 관심종목 헤더 -->
    <div class="watchlist-header">
        <h2><i class="fas fa-star"></i> 관심종목</h2>
        <p class="mb-0">실시간으로 업데이트되는 관심종목 가격</p>
    </div>
    
    <!-- 관심종목 목록 -->
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4><i class="fas fa-list"></i> 내 관심종목 (${watchlist.size()}개)</h4>
        <a href="/stock/list" class="btn btn-buy">
            <i class="fas fa-plus"></i> 관심종목 추가
        </a>
    </div>
    
    <c:choose>
        <c:when test="${empty watchlist}">
            <!-- 빈 관심종목 -->
            <div class="empty-watchlist">
                <i class="fas fa-star-half-alt"></i>
                <h4 class="text-muted">등록된 관심종목이 없습니다</h4>
                <p class="text-muted mb-4">관심있는 주식을 추가하여 가격을 모니터링하세요</p>
                <a href="/stock/list" class="btn btn-buy">
                    <i class="fas fa-search"></i> 주식 찾아보기
                </a>
            </div>
        </c:when>
        
        <c:otherwise>
            <!-- 관심종목 카드 목록 -->
            <c:forEach items="${watchlist}" var="stock">
                <div class="stock-card">
                    <div class="row align-items-center">
                        <div class="col-md-3">
                            <h5 class="mb-1">${stock.stockName}</h5>
                            <small class="text-muted">${stock.stockCode} | ${stock.marketType}</small>
                        </div>
                        <div class="col-md-2">
                            <small class="text-muted">현재가</small>
                            <h5 class="mb-0">
                                <fmt:formatNumber value="${stock.currentPrice}" pattern="#,###"/>
                                <c:if test="${stock.country == 'KR'}">원</c:if>
                                <c:if test="${stock.country == 'US'}">$</c:if>
                            </h5>
                        </div>
                        <div class="col-md-2">
                            <small class="text-muted">전일 대비</small>
                            <h6 class="${stock.priceChange >= 0 ? 'price-up' : 'price-down'}">
                                <c:if test="${stock.priceChange >= 0}">+</c:if>
                                <fmt:formatNumber value="${stock.priceChange}" pattern="#,###"/>
                            </h6>
                        </div>
                        <div class="col-md-2">
                            <small class="text-muted">등락률</small>
                            <h6 class="${stock.priceChangeRate >= 0 ? 'price-up' : 'price-down'}">
                                <c:if test="${stock.priceChangeRate >= 0}">+</c:if>
                                <fmt:formatNumber value="${stock.priceChangeRate}" pattern="#,##0.00"/>%
                            </h6>
                        </div>
                        <div class="col-md-3 text-end">
                            <button class="btn btn-sm btn-buy me-2" onclick="buyStock('${stock.stockCode}')">
                                <i class="fas fa-shopping-cart"></i> 매수
                            </button>
                            <button class="btn btn-sm btn-remove" onclick="removeFromWatchlist('${stock.watchlistId}')">
                                <i class="fas fa-times"></i> 제거
                            </button>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
    
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // 관심종목 제거
    function removeFromWatchlist(watchlistId) {
        if (confirm('관심종목에서 제거하시겠습니까?')) {
            fetch('/watchlist/remove/' + watchlistId, {
                method: 'DELETE'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('제거되었습니다.');
                    location.reload();
                } else {
                    alert('제거 실패: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('제거 중 오류가 발생했습니다.');
            });
        }
    }
    
    // 매수하기
    function buyStock(stockCode) {
        location.href = '/stock/buy?stockCode=' + stockCode;
    }
    
    // 실시간 가격 업데이트 (30초마다)
    setInterval(function() {
        location.reload();
    }, 30000);
</script>

</body>
</html>
