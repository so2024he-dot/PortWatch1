<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>대시보드 - PortWatch</title>
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
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 30px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        
        .header h1 {
            color: #333;
            font-size: 32px;
            margin-bottom: 10px;
        }
        
        .header p {
            color: #666;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background: white;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.1);
        }
        
        .stat-card h3 {
            font-size: 13px;
            color: #888;
            margin-bottom: 12px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        
        .stat-card .value {
            font-size: 36px;
            font-weight: bold;
            color: #667eea;
            margin-bottom: 8px;
        }
        
        .stat-card .sub-value {
            font-size: 14px;
            color: #999;
        }
        
        .profit-positive {
            color: #4caf50 !important;
        }
        
        .profit-negative {
            color: #f44336 !important;
        }
        
        .section {
            background: white;
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 30px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        
        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #f1f3f5;
        }
        
        .section-header h2 {
            font-size: 22px;
            color: #333;
        }
        
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s;
        }
        
        .btn-primary {
            background: #667eea;
            color: white;
        }
        
        .btn-primary:hover {
            background: #5568d3;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }
        
        .portfolio-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
        }
        
        .portfolio-card {
            border: 2px solid #e9ecef;
            border-radius: 12px;
            padding: 20px;
            transition: all 0.3s;
        }
        
        .portfolio-card:hover {
            border-color: #667eea;
            transform: translateY(-4px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.1);
        }
        
        .portfolio-card h3 {
            color: #333;
            margin-bottom: 8px;
            font-size: 18px;
        }
        
        .portfolio-stats {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
            margin-top: 15px;
        }
        
        .portfolio-stat {
            text-align: center;
            padding: 12px;
            background: #f8f9fa;
            border-radius: 8px;
        }
        
        .portfolio-stat-label {
            font-size: 12px;
            color: #888;
            margin-bottom: 5px;
        }
        
        .portfolio-stat-value {
            font-size: 20px;
            font-weight: bold;
            color: #333;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        thead {
            background: #f8f9fa;
        }
        
        th {
            padding: 12px;
            text-align: left;
            font-weight: 600;
            color: #555;
            border-bottom: 2px solid #e9ecef;
            font-size: 14px;
        }
        
        td {
            padding: 15px 12px;
            border-bottom: 1px solid #f1f3f5;
        }
        
        tbody tr:hover {
            background: #f8f9fa;
        }
        
        .stock-name {
            font-weight: 600;
            color: #333;
        }
        
        .stock-code {
            color: #888;
            font-size: 13px;
        }
        
        .news-item {
            padding: 15px 0;
            border-bottom: 1px solid #e9ecef;
        }
        
        .news-item:last-child {
            border-bottom: none;
        }
        
        .news-title {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
            font-size: 15px;
        }
        
        .news-meta {
            font-size: 13px;
            color: #888;
        }
        
        .empty-state {
            text-align: center;
            padding: 40px 20px;
            color: #888;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Header -->
        <div class="header">
            <h1>📊 대시보드</h1>
            <p>환영합니다, ${member.memberName}님! 포트폴리오 현황을 확인하세요.</p>
        </div>
        
        <!-- Main Stats -->
        <div class="stats-grid">
            <div class="stat-card">
                <h3>총 투자금액</h3>
                <div class="value">
                    <fmt:formatNumber value="${dashboard.totalInvestment}" pattern="#,##0"/>원
                </div>
            </div>
            
            <div class="stat-card">
                <h3>현재 평가금액</h3>
                <div class="value">
                    <fmt:formatNumber value="${dashboard.totalCurrentValue}" pattern="#,##0"/>원
                </div>
            </div>
            
            <div class="stat-card">
                <h3>총 손익</h3>
                <div class="value ${dashboard.totalProfitLoss >= 0 ? 'profit-positive' : 'profit-negative'}">
                    <fmt:formatNumber value="${dashboard.totalProfitLoss}" pattern="#,##0"/>원
                </div>
                <div class="sub-value ${dashboard.totalProfitLossRate >= 0 ? 'profit-positive' : 'profit-negative'}">
                    <c:choose>
                        <c:when test="${dashboard.totalProfitLossRate >= 0}">
                            ▲ <fmt:formatNumber value="${dashboard.totalProfitLossRate}" pattern="0.00"/>%
                        </c:when>
                        <c:otherwise>
                            ▼ <fmt:formatNumber value="${-dashboard.totalProfitLossRate}" pattern="0.00"/>%
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <div class="stat-card">
                <h3>포트폴리오</h3>
                <div class="value">${dashboard.totalPortfolios}</div>
                <div class="sub-value">보유 종목 ${dashboard.totalStocks}개</div>
            </div>
        </div>
        
        <!-- Portfolio Summary -->
        <div class="section">
            <div class="section-header">
                <h2>포트폴리오 현황</h2>
                <a href="${pageContext.request.contextPath}/portfolio/create" class="btn btn-primary">
                    + 새 포트폴리오
                </a>
            </div>
            
            <c:choose>
                <c:when test="${empty dashboard.portfolioSummaries}">
                    <div class="empty-state">
                        <p>아직 생성된 포트폴리오가 없습니다.</p>
                        <a href="${pageContext.request.contextPath}/portfolio/create" class="btn btn-primary">
                            첫 포트폴리오 만들기
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="portfolio-grid">
                        <c:forEach items="${dashboard.portfolioSummaries}" var="portfolio">
                            <div class="portfolio-card">
                                <h3>${portfolio.portfolioName}</h3>
                                <div class="portfolio-stats">
                                    <div class="portfolio-stat">
                                        <div class="portfolio-stat-label">투자금액</div>
                                        <div class="portfolio-stat-value">
                                            <fmt:formatNumber value="${portfolio.investment}" pattern="#,##0"/>
                                        </div>
                                    </div>
                                    <div class="portfolio-stat">
                                        <div class="portfolio-stat-label">평가금액</div>
                                        <div class="portfolio-stat-value">
                                            <fmt:formatNumber value="${portfolio.currentValue}" pattern="#,##0"/>
                                        </div>
                                    </div>
                                    <div class="portfolio-stat">
                                        <div class="portfolio-stat-label">손익</div>
                                        <div class="portfolio-stat-value ${portfolio.profitLoss >= 0 ? 'profit-positive' : 'profit-negative'}">
                                            <fmt:formatNumber value="${portfolio.profitLoss}" pattern="#,##0"/>
                                        </div>
                                    </div>
                                    <div class="portfolio-stat">
                                        <div class="portfolio-stat-label">수익률</div>
                                        <div class="portfolio-stat-value ${portfolio.profitLossRate >= 0 ? 'profit-positive' : 'profit-negative'}">
                                            <fmt:formatNumber value="${portfolio.profitLossRate}" pattern="0.00"/>%
                                        </div>
                                    </div>
                                </div>
                                <a href="${pageContext.request.contextPath}/portfolio/detail/${portfolio.portfolioId}" 
                                   class="btn btn-primary" style="width:100%; margin-top:15px; text-align:center;">
                                    상세보기
                                </a>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- Top Stocks -->
        <div class="section">
            <div class="section-header">
                <h2>보유 종목 TOP 5</h2>
                <a href="${pageContext.request.contextPath}/stock/list" class="btn btn-primary">
                    전체 종목 보기
                </a>
            </div>
            
            <c:choose>
                <c:when test="${empty dashboard.topStocks}">
                    <div class="empty-state">
                        <p>보유 중인 종목이 없습니다.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>종목명</th>
                                <th>보유수량</th>
                                <th>평균매입가</th>
                                <th>현재가</th>
                                <th>손익률</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${dashboard.topStocks}" var="stock" end="4">
                                <tr>
                                    <td>
                                        <div class="stock-name">${stock.stockName}</div>
                                        <div class="stock-code">${stock.stockCode}</div>
                                    </td>
                                    <td><fmt:formatNumber value="${stock.stockQuantity}" pattern="#,##0"/>주</td>
                                    <td><fmt:formatNumber value="${stock.stockAvgPrice}" pattern="#,##0"/>원</td>
                                    <td><fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0"/>원</td>
                                    <td class="${stock.profitLossRate >= 0 ? 'profit-positive' : 'profit-negative'}">
                                        <fmt:formatNumber value="${stock.profitLossRate}" pattern="0.00"/>%
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- Recent News -->
        <div class="section">
            <div class="section-header">
                <h2>최신 뉴스</h2>
                <a href="${pageContext.request.contextPath}/news/list" class="btn btn-primary">
                    전체 뉴스
                </a>
            </div>
            
            <c:choose>
                <c:when test="${empty dashboard.recentNews}">
                    <div class="empty-state">
                        <p>뉴스가 없습니다.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${dashboard.recentNews}" var="news" end="4">
                        <div class="news-item">
                            <div class="news-title">${news.newsTitle}</div>
                            <div class="news-meta">
                                ${news.newsSource} · 
                                <fmt:formatDate value="${news.newsPubDate}" pattern="yyyy-MM-dd HH:mm"/>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- Quick Actions -->
        <div class="section">
            <h2 style="margin-bottom:20px;">빠른 실행</h2>
            <div style="display:grid; grid-template-columns:repeat(auto-fit, minmax(200px, 1fr)); gap:15px;">
                <a href="${pageContext.request.contextPath}/portfolio/list" class="btn btn-primary">
                    포트폴리오 관리
                </a>
                <a href="${pageContext.request.contextPath}/stock/list" class="btn btn-primary">
                    종목 검색
                </a>
                <a href="${pageContext.request.contextPath}/watchlist/list" class="btn btn-primary">
                    관심종목
                </a>
                <a href="${pageContext.request.contextPath}/member/mypage" class="btn btn-primary">
                    마이페이지
                </a>
            </div>
        </div>
    </div>
</body>
</html>
