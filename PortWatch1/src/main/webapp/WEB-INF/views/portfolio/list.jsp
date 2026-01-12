<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>포트폴리오 목록 - PortWatch</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8f9fa;
        }
        
        .portfolio-card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            padding: 20px;
            margin-bottom: 20px;
            transition: transform 0.2s;
        }
        
        .portfolio-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.15);
        }
        
        .profit-positive {
            color: #dc3545;
        }
        
        .profit-negative {
            color: #0d6efd;
        }
        
        .news-category {
            background: #0d6efd;
            color: white;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 12px;
        }
        
        .btn-custom {
            border-radius: 20px;
            padding: 8px 20px;
        }
    </style>
</head>
<body>
    <!-- Header 포함 -->
    <jsp:include page="../common/header.jsp" />
    
    <div class="container mt-4">
        <div class="row">
            <div class="col-12">
                <h2 class="mb-4">
                    <i class="fas fa-briefcase"></i> 내 포트폴리오
                </h2>
                
                <!-- 포트폴리오 목록 -->
                <c:choose>
                    <c:when test="${empty portfolioList}">
                        <div class="alert alert-info text-center">
                            <i class="fas fa-info-circle"></i>
                            등록된 포트폴리오가 없습니다.
                            <br>
                            <a href="${pageContext.request.contextPath}/portfolio/create" class="btn btn-primary btn-custom mt-3">
                                <i class="fas fa-plus"></i> 포트폴리오 추가
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- 추가 버튼 -->
                        <div class="text-end mb-3">
                            <a href="${pageContext.request.contextPath}/portfolio/create" class="btn btn-primary btn-custom">
                                <i class="fas fa-plus"></i> 포트폴리오 추가
                            </a>
                        </div>
                        
                        <!-- 포트폴리오 카드 -->
                        <c:forEach var="portfolio" items="${portfolioList}">
                            <div class="portfolio-card">
                                <div class="row align-items-center">
                                    <div class="col-md-3">
                                        <h5 class="mb-1">${portfolio.stockName}</h5>
                                        <small class="text-muted">${portfolio.stockCode}</small>
                                    </div>
                                    <div class="col-md-2">
                                        <div class="text-muted">수량</div>
                                        <strong><fmt:formatNumber value="${portfolio.quantity}" type="number"/> 주</strong>
                                    </div>
                                    <div class="col-md-2">
                                        <div class="text-muted">매수 평균가</div>
                                        <strong><fmt:formatNumber value="${portfolio.purchasePrice}" type="number" groupingUsed="true"/> 원</strong>
                                    </div>
                                    <div class="col-md-2">
                                        <div class="text-muted">현재가</div>
                                        <strong><fmt:formatNumber value="${portfolio.currentPrice}" type="number" groupingUsed="true"/> 원</strong>
                                    </div>
                                    <div class="col-md-2">
                                        <div class="text-muted">평가손익</div>
                                        <c:set var="profitLoss" value="${(portfolio.currentPrice - portfolio.purchasePrice) * portfolio.quantity}" />
                                        <strong class="${profitLoss >= 0 ? 'profit-positive' : 'profit-negative'}">
                                            <c:choose>
                                                <c:when test="${profitLoss >= 0}">
                                                    +<fmt:formatNumber value="${profitLoss}" type="number" groupingUsed="true"/> 원
                                                </c:when>
                                                <c:otherwise>
                                                    <fmt:formatNumber value="${profitLoss}" type="number" groupingUsed="true"/> 원
                                                </c:otherwise>
                                            </c:choose>
                                        </strong>
                                    </div>
                                    <div class="col-md-1 text-end">
                                        <button class="btn btn-sm btn-danger" onclick="deletePortfolio(${portfolio.portfolioId})">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function deletePortfolio(portfolioId) {
            if (confirm('정말 삭제하시겠습니까?')) {
                location.href = '${pageContext.request.contextPath}/portfolio/delete?portfolioId=' + portfolioId;
            }
        }
    </script>
</body>
</html>
