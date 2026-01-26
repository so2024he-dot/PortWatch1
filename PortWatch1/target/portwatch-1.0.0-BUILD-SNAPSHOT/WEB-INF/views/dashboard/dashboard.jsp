<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PortWatch - Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <div class="container">
        <!-- Header -->
        <header class="dashboard-header">
            <h1>📊 PortWatch Dashboard</h1>
            <div class="user-info">
                <span>환영합니다, <strong>${loginMember.memberName}</strong>님</span>
                <a href="${pageContext.request.contextPath}/member/logout" class="btn-logout">로그아웃</a>
            </div>
        </header>

        <!-- Navigation -->
        <nav class="dashboard-nav">
            <ul>
                <li><a href="${pageContext.request.contextPath}/dashboard" class="active">대시보드</a></li>
                <li><a href="${pageContext.request.contextPath}/portfolio/list">포트폴리오</a></li>
                <li><a href="${pageContext.request.contextPath}/stock/list">종목 조회</a></li>
                <li><a href="${pageContext.request.contextPath}/watchlist/list">관심 종목</a></li>
                <li><a href="${pageContext.request.contextPath}/news/list">뉴스</a></li>
            </ul>
        </nav>

        <!-- Main Content -->
        <main class="dashboard-main">
            <!-- Portfolio Summary -->
            <section class="portfolio-summary">
                <h2>📈 포트폴리오 요약</h2>
                <div class="summary-cards">
                    <div class="card">
                        <h3>총 평가액</h3>
                        <p class="value">
                            <c:choose>
                                <c:when test="${empty portfolioList}">
                                    0원
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatNumber value="${totalValue}" pattern="#,##0"/>원
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                    <div class="card">
                        <h3>총 손익</h3>
                        <p class="value profit">
                            <c:choose>
                                <c:when test="${empty portfolioList}">
                                    0원 (0%)
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatNumber value="${totalProfit}" pattern="#,##0"/>원 
                                    (<fmt:formatNumber value="${totalProfitRate}" pattern="#,##0.00"/>%)
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                    <div class="card">
                        <h3>보유 종목</h3>
                        <p class="value">
                            <c:choose>
                                <c:when test="${empty portfolioList}">
                                    0개
                                </c:when>
                                <c:otherwise>
                                    ${portfolioList.size()}개
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </div>
            </section>

            <!-- Portfolio List -->
            <section class="portfolio-list">
                <h2>📋 보유 종목 목록</h2>
                <c:choose>
                    <c:when test="${empty portfolioList}">
                        <div class="empty-state">
                            <p>보유 중인 종목이 없습니다.</p>
                            <a href="${pageContext.request.contextPath}/stock/list" class="btn-primary">종목 구매하기</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table class="portfolio-table">
                            <thead>
                                <tr>
                                    <th>종목명</th>
                                    <th>종목코드</th>
                                    <th>보유 수량</th>
                                    <th>평균 단가</th>
                                    <th>현재가</th>
                                    <th>평가액</th>
                                    <th>손익</th>
                                    <th>수익률</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${portfolioList}" var="portfolio">
                                    <tr>
                                        <td>${portfolio.stockName}</td>
                                        <td>${portfolio.stockCode}</td>
                                        <td><fmt:formatNumber value="${portfolio.quantity}" pattern="#,##0.####"/></td>
                                        <td><fmt:formatNumber value="${portfolio.avgPrice}" pattern="#,##0.00"/>원</td>
                                        <td><fmt:formatNumber value="${portfolio.currentPrice}" pattern="#,##0.00"/>원</td>
                                        <td><fmt:formatNumber value="${portfolio.totalValue}" pattern="#,##0"/>원</td>
                                        <td class="${portfolio.profit >= 0 ? 'profit' : 'loss'}">
                                            <fmt:formatNumber value="${portfolio.profit}" pattern="#,##0"/>원
                                        </td>
                                        <td class="${portfolio.profitRate >= 0 ? 'profit' : 'loss'}">
                                            <fmt:formatNumber value="${portfolio.profitRate}" pattern="#,##0.00"/>%
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </section>

            <!-- Chart Section -->
            <section class="chart-section">
                <h2>📊 포트폴리오 차트</h2>
                <div class="chart-container">
                    <canvas id="portfolioChart"></canvas>
                </div>
            </section>

            <!-- Quick Actions -->
            <section class="quick-actions">
                <h2>⚡ 빠른 작업</h2>
                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/stock/list" class="btn-action">종목 구매</a>
                    <a href="${pageContext.request.contextPath}/portfolio/list" class="btn-action">포트폴리오 관리</a>
                    <a href="${pageContext.request.contextPath}/watchlist/list" class="btn-action">관심 종목</a>
                    <a href="${pageContext.request.contextPath}/news/list" class="btn-action">뉴스 보기</a>
                </div>
            </section>
        </main>

        <!-- Footer -->
        <footer class="dashboard-footer">
            <p>&copy; 2026 PortWatch. All rights reserved.</p>
        </footer>
    </div>

    <script>
        // Portfolio Chart
        <c:if test="${not empty portfolioList}">
        const ctx = document.getElementById('portfolioChart').getContext('2d');
        const portfolioChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: [
                    <c:forEach items="${portfolioList}" var="portfolio" varStatus="status">
                        '${portfolio.stockName}'<c:if test="${!status.last}">,</c:if>
                    </c:forEach>
                ],
                datasets: [{
                    label: '평가액',
                    data: [
                        <c:forEach items="${portfolioList}" var="portfolio" varStatus="status">
                            ${portfolio.totalValue}<c:if test="${!status.last}">,</c:if>
                        </c:forEach>
                    ],
                    backgroundColor: [
                        '#FF6384',
                        '#36A2EB',
                        '#FFCE56',
                        '#4BC0C0',
                        '#9966FF',
                        '#FF9F40',
                        '#FF6384',
                        '#C9CBCF'
                    ]
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom'
                    },
                    title: {
                        display: true,
                        text: '종목별 비중'
                    }
                }
            }
        });
        </c:if>
    </script>
</body>
</html>
