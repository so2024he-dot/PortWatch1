<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>📊 내 포트폴리오 - PortWatch</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .portfolio-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        
        .summary-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
            transition: transform 0.2s;
        }
        
        .summary-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.15);
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
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        
        .profit-positive {
            color: #dc3545;
            font-weight: bold;
        }
        
        .profit-negative {
            color: #0d6efd;
            font-weight: bold;
        }
        
        .btn-add-stock {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 12px 30px;
            border-radius: 25px;
            font-weight: bold;
            transition: all 0.3s;
        }
        
        .btn-add-stock:hover {
            transform: scale(1.05);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }
        
        .empty-portfolio {
            text-align: center;
            padding: 60px 20px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .empty-portfolio i {
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
                <li class="nav-item"><a class="nav-link active" href="/portfolio">포트폴리오</a></li>
                <li class="nav-item"><a class="nav-link" href="/watchlist">관심종목</a></li>
                <li class="nav-item"><a class="nav-link" href="/payment/history">결제 내역</a></li>
                <li class="nav-item"><a class="nav-link" href="/member/logout">로그아웃</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container mt-4">
    
    <!-- 포트폴리오 헤더 -->
    <div class="portfolio-header">
        <h2><i class="fas fa-briefcase"></i> 내 포트폴리오</h2>
        <p class="mb-0">
            <c:if test="${not empty loginMember}">
                ${loginMember.memberName}님의 투자 현황
            </c:if>
        </p>
    </div>
    
    <!-- 요약 정보 -->
    <div class="row mb-4">
        <div class="col-md-3">
            <div class="summary-card">
                <h6 class="text-muted mb-2">총 투자금</h6>
                <h3 class="mb-0">
                    <fmt:formatNumber value="${summary.totalInvestment}" pattern="#,###"/>원
                </h3>
            </div>
        </div>
        <div class="col-md-3">
            <div class="summary-card">
                <h6 class="text-muted mb-2">현재 가치</h6>
                <h3 class="mb-0">
                    <fmt:formatNumber value="${summary.totalCurrentValue}" pattern="#,###"/>원
                </h3>
            </div>
        </div>
        <div class="col-md-3">
            <div class="summary-card">
                <h6 class="text-muted mb-2">총 수익</h6>
                <h3 class="mb-0 ${summary.totalProfit >= 0 ? 'profit-positive' : 'profit-negative'}">
                    <c:if test="${summary.totalProfit >= 0}">+</c:if>
                    <fmt:formatNumber value="${summary.totalProfit}" pattern="#,###"/>원
                </h3>
            </div>
        </div>
        <div class="col-md-3">
            <div class="summary-card">
                <h6 class="text-muted mb-2">수익률</h6>
                <h3 class="mb-0 ${summary.profitRate >= 0 ? 'profit-positive' : 'profit-negative'}">
                    <c:if test="${summary.profitRate >= 0}">+</c:if>
                    <fmt:formatNumber value="${summary.profitRate}" pattern="#,##0.00"/>%
                </h3>
            </div>
        </div>
    </div>
    
    <!-- 보유 주식 목록 -->
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4><i class="fas fa-list"></i> 보유 주식 (${portfolioList.size()}개)</h4>
        <a href="/stock/list" class="btn btn-add-stock">
            <i class="fas fa-plus"></i> 주식 추가
        </a>
    </div>
    
    <c:choose>
        <c:when test="${empty portfolioList}">
            <!-- 빈 포트폴리오 -->
            <div class="empty-portfolio">
                <i class="fas fa-folder-open"></i>
                <h4 class="text-muted">보유 중인 주식이 없습니다</h4>
                <p class="text-muted mb-4">주식을 매입하여 포트폴리오를 구성하세요</p>
                <a href="/stock/list" class="btn btn-add-stock">
                    <i class="fas fa-shopping-cart"></i> 주식 매입하기
                </a>
            </div>
        </c:when>
        
        <c:otherwise>
            <!-- 주식 카드 목록 -->
            <c:forEach items="${portfolioList}" var="portfolio">
                <div class="stock-card">
                    <div class="row align-items-center">
                        <div class="col-md-3">
                            <h5 class="mb-1">${portfolio.stockName}</h5>
                            <small class="text-muted">${portfolio.stockCode}</small>
                        </div>
                        <div class="col-md-2">
                            <small class="text-muted">보유 수량</small>
                            <h6><fmt:formatNumber value="${portfolio.quantity}" pattern="#,###"/>주</h6>
                        </div>
                        <div class="col-md-2">
                            <small class="text-muted">평균 매입가</small>
                            <h6><fmt:formatNumber value="${portfolio.avgPurchasePrice}" pattern="#,###"/>원</h6>
                        </div>
                        <div class="col-md-2">
                            <small class="text-muted">현재가</small>
                            <h6><fmt:formatNumber value="${portfolio.currentPrice}" pattern="#,###"/>원</h6>
                        </div>
                        <div class="col-md-2">
                            <small class="text-muted">수익률</small>
                            <h6 class="${portfolio.profitRate >= 0 ? 'profit-positive' : 'profit-negative'}">
                                <c:if test="${portfolio.profitRate >= 0}">+</c:if>
                                <fmt:formatNumber value="${portfolio.profitRate}" pattern="#,##0.00"/>%
                            </h6>
                        </div>
                        <div class="col-md-1 text-end">
                            <button class="btn btn-sm btn-danger" onclick="deletePortfolio('${portfolio.portfolioId}')">
                                <i class="fas fa-trash"></i>
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
    function deletePortfolio(portfolioId) {
        if (confirm('정말 삭제하시겠습니까?')) {
            fetch('/portfolio/delete/' + portfolioId, {
                method: 'DELETE'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('삭제되었습니다.');
                    location.reload();
                } else {
                    alert('삭제 실패: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('삭제 중 오류가 발생했습니다.');
            });
        }
    }
    
    // 페이지 로드 시 자동 새로고침 (현재가 업데이트)
    setInterval(function() {
        location.reload();
    }, 60000); // 1분마다
</script>

</body>
</html>
