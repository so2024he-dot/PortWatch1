<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>관심종목 - PortWatch</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <style>
        .watchlist-card {
            transition: transform 0.2s, box-shadow 0.2s;
            cursor: pointer;
            height: 100%;
        }
        
        .watchlist-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }
        
        .price-up {
            color: #dc3545;
        }
        
        .price-down {
            color: #0d6efd;
        }
        
        .remove-btn {
            position: absolute;
            top: 10px;
            right: 10px;
        }
        
        .stock-price {
            font-size: 1.5em;
            font-weight: bold;
        }
        
        .change-rate {
            font-size: 1.1em;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <!-- 네비게이션 바 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                <i class="fas fa-chart-line"></i> PortWatch
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                            <i class="fas fa-th-large"></i> 대시보드
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/stock/list">
                            <i class="fas fa-chart-bar"></i> 주식
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/watchlist/list">
                            <i class="fas fa-star"></i> 관심종목
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/news/list">
                            <i class="fas fa-newspaper"></i> 뉴스
                        </a>
                    </li>
                    <c:choose>
                        <c:when test="${not empty loginMember}">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="userDropdown" 
                                   role="button" data-bs-toggle="dropdown">
                                    <i class="fas fa-user"></i> ${loginMember.memberName}
                                </a>
                                <ul class="dropdown-menu" aria-labelledby="userDropdown">
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/member/mypage">
                                            <i class="fas fa-user-circle"></i> 마이페이지
                                        </a>
                                    </li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/member/logout">
                                            <i class="fas fa-sign-out-alt"></i> 로그아웃
                                        </a>
                                    </li>
                                </ul>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/member/login">
                                    <i class="fas fa-sign-in-alt"></i> 로그인
                                </a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>
    
    <!-- 메인 컨텐츠 -->
    <div class="container mt-4">
        <!-- 페이지 헤더 -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>
                <i class="fas fa-star"></i> 관심종목
            </h2>
            <div>
                <a href="${pageContext.request.contextPath}/stock/list" class="btn btn-outline-primary">
                    <i class="fas fa-plus"></i> 종목 추가
                </a>
            </div>
        </div>
        
        <!-- 에러 메시지 -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <!-- 관심종목 목록 -->
        <div id="watchlistContainer">
            <c:choose>
                <c:when test="${empty watchlist}">
                    <div class="alert alert-info text-center">
                        <i class="fas fa-info-circle"></i>
                        관심종목이 없습니다. 주식 목록에서 종목을 추가해보세요!
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row">
                        <c:forEach items="${watchlist}" var="item">
                            <div class="col-md-4 col-lg-3 mb-4">
                                <div class="card watchlist-card" 
                                     onclick="location.href='${pageContext.request.contextPath}/stock/detail?stockCode=${item.stockCode}'">
                                    <!-- 삭제 버튼 -->
                                    <button class="btn btn-sm btn-danger remove-btn" 
                                            onclick="event.stopPropagation(); removeFromWatchlist(${item.watchlistId})">
                                        <i class="fas fa-times"></i>
                                    </button>
                                    
                                    <div class="card-body">
                                        <!-- 종목명 -->
                                        <h5 class="card-title">
                                            ${item.stockName}
                                        </h5>
                                        
                                        <!-- 종목 코드 -->
                                        <p class="text-muted mb-2">
                                            ${item.stockCode}
                                            <span class="badge bg-secondary ms-2">
                                                ${item.marketType}
                                            </span>
                                        </p>
                                        
                                        <!-- 현재가 -->
                                        <div class="stock-price mb-2">
                                            <fmt:formatNumber value="${item.currentPrice}" type="number" groupingUsed="true"/>
                                            <c:choose>
                                                <c:when test="${item.country eq 'KR'}">원</c:when>
                                                <c:otherwise>$</c:otherwise>
                                            </c:choose>
                                        </div>
                                        
                                        <!-- 등락률 -->
                                        <div class="change-rate ${item.changeRate >= 0 ? 'price-up' : 'price-down'}">
                                            <c:choose>
                                                <c:when test="${item.changeRate >= 0}">
                                                    <i class="fas fa-arrow-up"></i> +<fmt:formatNumber value="${item.changeRate}" type="number" minFractionDigits="2" maxFractionDigits="2"/>%
                                                </c:when>
                                                <c:otherwise>
                                                    <i class="fas fa-arrow-down"></i> <fmt:formatNumber value="${item.changeRate}" type="number" minFractionDigits="2" maxFractionDigits="2"/>%
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        
                                        <!-- 매수 버튼 -->
                                        <div class="mt-3">
                                            <button class="btn btn-primary btn-sm w-100" 
                                                    onclick="event.stopPropagation(); location.href='${pageContext.request.contextPath}/stock/buy?stockCode=${item.stockCode}'">
                                                <i class="fas fa-shopping-cart"></i> 매수
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         ✅ 수정된 JavaScript - JSP EL 에러 해결
         ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ -->
    <script>
    /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * WatchlistManager 객체 - 관심종목 관리
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * 핵심 수정:
     * ❌ 잘못된 방법: contextPath: '${WatchlistManager.contextPath}',
     * ✅ 올바른 방법: contextPath: '${pageContext.request.contextPath}',
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
    
    const WatchlistManager = {
        // ✅ 올바른 방법: Controller에서 전달받은 값 직접 사용
        contextPath: '${pageContext.request.contextPath}',  // ❌ ${WatchlistManager.contextPath} 아님!
        
        /**
         * 초기화
         */
        init: function() {
            console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
            console.log('⭐ 관심종목 매니저 초기화');
            console.log('  - contextPath:', WatchlistManager.contextPath);
            console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
        }
    };
    
    /**
     * 관심종목 삭제
     */
    function removeFromWatchlist(watchlistId) {
        if (!confirm('관심종목에서 삭제하시겠습니까?')) {
            return;
        }
        
        console.log('🗑️ 관심종목 삭제:', watchlistId);
        
        fetch(WatchlistManager.contextPath + '/watchlist/remove', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'watchlistId=' + encodeURIComponent(watchlistId)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('관심종목에서 삭제되었습니다!');
                location.reload();
            } else {
                alert(data.message || '삭제에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('❌ 관심종목 삭제 실패:', error);
            alert('삭제에 실패했습니다.');
        });
    }
    
    // ✅ 페이지 로드 시 초기화
    document.addEventListener('DOMContentLoaded', function() {
        console.log('📄 Watchlist 페이지 로드 완료');
        WatchlistManager.init();
    });
    </script>
</body>
</html>
