<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>📈 주식 목록 - PortWatch</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <style>
        body {
            background-color: #f8f9fa;
        }
        
        .stock-header {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        
        .filter-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        
        .stock-table {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .stock-table table {
            margin-bottom: 0;
        }
        
        .price-up {
            color: #dc3545;
            font-weight: bold;
        }
        
        .price-down {
            color: #0d6efd;
            font-weight: bold;
        }
        
        .btn-action {
            padding: 5px 15px;
            border-radius: 15px;
            font-size: 0.9em;
            border: none;
            transition: all 0.3s;
        }
        
        .btn-buy {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-buy:hover {
            transform: scale(1.05);
            color: white;
        }
        
        .btn-watchlist {
            background: #ffc107;
            color: white;
        }
        
        .btn-watchlist:hover {
            background: #ffb300;
            color: white;
        }
        
        .search-bar {
            border-radius: 25px;
            padding: 10px 20px;
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
                <li class="nav-item"><a class="nav-link active" href="/stock/list">주식 목록</a></li>
                <li class="nav-item"><a class="nav-link" href="/portfolio">포트폴리오</a></li>
                <li class="nav-item"><a class="nav-link" href="/watchlist">관심종목</a></li>
                <li class="nav-item"><a class="nav-link" href="/payment/history">결제 내역</a></li>
                <li class="nav-item"><a class="nav-link" href="/member/logout">로그아웃</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container mt-4">
    
    <!-- 주식 헤더 -->
    <div class="stock-header">
        <h2><i class="fas fa-chart-line"></i> 주식 목록</h2>
        <p class="mb-0">실시간 주가 정보 | 한국/미국 주식 투자</p>
    </div>
    
    <!-- 필터 & 검색 -->
    <div class="filter-card">
        <div class="row g-3">
            <div class="col-md-3">
                <select class="form-select" id="countryFilter" onchange="filterStocks()">
                    <option value="ALL">전체 국가</option>
                    <option value="KR" selected>🇰🇷 한국</option>
                    <option value="US">🇺🇸 미국</option>
                </select>
            </div>
            <div class="col-md-3">
                <select class="form-select" id="marketFilter" onchange="filterStocks()">
                    <option value="ALL">전체 시장</option>
                    <option value="KOSPI">KOSPI</option>
                    <option value="KOSDAQ">KOSDAQ</option>
                    <option value="NASDAQ">NASDAQ</option>
                    <option value="NYSE">NYSE</option>
                </select>
            </div>
            <div class="col-md-6">
                <input type="text" class="form-control search-bar" id="searchInput" 
                       placeholder="🔍 종목명 또는 종목코드 검색..." onkeyup="searchStocks()">
            </div>
        </div>
    </div>
    
    <!-- 주식 테이블 -->
    <div class="stock-table">
        <table class="table table-hover mb-0">
            <thead class="table-light">
                <tr>
                    <th>종목명</th>
                    <th>종목코드</th>
                    <th>시장</th>
                    <th>현재가</th>
                    <th>전일대비</th>
                    <th>등락률</th>
                    <th>거래량</th>
                    <th>관리</th>
                </tr>
            </thead>
            <tbody id="stockTableBody">
                <c:forEach items="${stockList}" var="stock">
                    <tr>
                        <td><strong>${stock.stockName}</strong></td>
                        <td><code>${stock.stockCode}</code></td>
                        <td><span class="badge bg-primary">${stock.marketType}</span></td>
                        <td><strong><fmt:formatNumber value="${stock.currentPrice}" pattern="#,###"/></strong></td>
                        <td class="${stock.priceChange >= 0 ? 'price-up' : 'price-down'}">
                            <c:if test="${stock.priceChange >= 0}">+</c:if>
                            <fmt:formatNumber value="${stock.priceChange}" pattern="#,###"/>
                        </td>
                        <td class="${stock.priceChangeRate >= 0 ? 'price-up' : 'price-down'}">
                            <c:if test="${stock.priceChangeRate >= 0}">+</c:if>
                            <fmt:formatNumber value="${stock.priceChangeRate}" pattern="#,##0.00"/>%
                        </td>
                        <td><fmt:formatNumber value="${stock.volume}" pattern="#,###"/></td>
                        <td>
                            <button class="btn btn-sm btn-action btn-buy me-1" 
                                    onclick="buyStock('${stock.stockCode}')">
                                <i class="fas fa-shopping-cart"></i> 매수
                            </button>
                            <button class="btn btn-sm btn-action btn-watchlist" 
                                    onclick="addToWatchlist('${stock.stockCode}')">
                                <i class="fas fa-star"></i>
                            </button>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    
    <!-- 페이징 -->
    <nav class="mt-4">
        <ul class="pagination justify-content-center">
            <li class="page-item"><a class="page-link" href="#">이전</a></li>
            <li class="page-item active"><a class="page-link" href="#">1</a></li>
            <li class="page-item"><a class="page-link" href="#">2</a></li>
            <li class="page-item"><a class="page-link" href="#">3</a></li>
            <li class="page-item"><a class="page-link" href="#">다음</a></li>
        </ul>
    </nav>
    
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // 필터링
    function filterStocks() {
        const country = document.getElementById('countryFilter').value;
        const market = document.getElementById('marketFilter').value;
        
        let url = '/stock/list?';
        if (country !== 'ALL') url += 'country=' + country + '&';
        if (market !== 'ALL') url += 'market=' + market;
        
        location.href = url;
    }
    
    // 검색
    function searchStocks() {
        const keyword = document.getElementById('searchInput').value.toLowerCase();
        const rows = document.querySelectorAll('#stockTableBody tr');
        
        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            if (text.includes(keyword)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    }
    
    // 매수하기
    function buyStock(stockCode) {
        location.href = '/stock/buy?stockCode=' + stockCode;
    }
    
    // 관심종목 추가
    function addToWatchlist(stockCode) {
        fetch('/watchlist/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'stockCode=' + stockCode
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('관심종목에 추가되었습니다!');
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('추가 중 오류가 발생했습니다.');
        });
    }
</script>

</body>
</html>
