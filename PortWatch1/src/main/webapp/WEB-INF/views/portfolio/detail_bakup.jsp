<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${portfolio.portfolioName} - PortWatch</title>
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
        }
        
        nav {
            background: rgba(255, 255, 255, 0.95);
            padding: 1rem 2rem;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .nav-brand {
            font-size: 1.5rem;
            font-weight: bold;
            color: #667eea;
            text-decoration: none;
        }
        
        .nav-links {
            display: flex;
            gap: 2rem;
            list-style: none;
        }
        
        .nav-links a {
            text-decoration: none;
            color: #333;
            font-weight: 500;
        }
        
        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 2rem;
        }
        
        .portfolio-header {
            background: white;
            padding: 2rem;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            margin-bottom: 2rem;
        }
        
        .portfolio-title {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
        }
        
        .portfolio-title h1 {
            color: #667eea;
            font-size: 2rem;
        }
        
        .portfolio-actions {
            display: flex;
            gap: 0.5rem;
        }
        
        .btn {
            padding: 0.5rem 1.5rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 500;
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
        }
        
        .btn-secondary {
            background: #e0e0e0;
            color: #333;
        }
        
        .btn-danger {
            background: #f44336;
            color: white;
        }
        
        .portfolio-desc {
            color: #666;
            margin-bottom: 1rem;
        }
        
        .portfolio-meta {
            display: flex;
            gap: 2rem;
            color: #999;
            font-size: 0.9rem;
        }
        
        /* 통계 카드 */
        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-bottom: 2rem;
        }
        
        .stat-card {
            background: white;
            padding: 1.5rem;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            text-align: center;
        }
        
        .stat-label {
            color: #666;
            font-size: 0.9rem;
            margin-bottom: 0.5rem;
        }
        
        .stat-value {
            font-size: 1.8rem;
            font-weight: bold;
            color: #667eea;
        }
        
        .stat-value.profit {
            color: #4caf50;
        }
        
        .stat-value.loss {
            color: #f44336;
        }
        
        /* 종목 테이블 */
        .stocks-section {
            background: white;
            padding: 2rem;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
        }
        
        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
        }
        
        .section-header h2 {
            color: #333;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        thead {
            background: #f5f5f5;
        }
        
        th {
            padding: 1rem;
            text-align: left;
            font-weight: 600;
            color: #666;
            border-bottom: 2px solid #e0e0e0;
        }
        
        td {
            padding: 1rem;
            border-bottom: 1px solid #f0f0f0;
        }
        
        tbody tr:hover {
            background: #f9f9f9;
        }
        
        .stock-name {
            font-weight: 600;
            color: #333;
        }
        
        .stock-code {
            color: #999;
            font-size: 0.9rem;
        }
        
        .profit {
            color: #4caf50;
            font-weight: 600;
        }
        
        .loss {
            color: #f44336;
            font-weight: 600;
        }
        
        .empty-state {
            text-align: center;
            padding: 3rem;
            color: #999;
        }
        
        .empty-state-icon {
            font-size: 4rem;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <nav>
        <a href="${pageContext.request.contextPath}/" class="nav-brand">📈 PortWatch</a>
        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/">홈</a></li>
            <li><a href="${pageContext.request.contextPath}/portfolio/list">포트폴리오</a></li>
            <li><a href="${pageContext.request.contextPath}/stock/list">종목</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <!-- 포트폴리오 헤더 -->
        <div class="portfolio-header">
            <div class="portfolio-title">
                <h1>📊 ${portfolio.portfolioName}</h1>
                <div class="portfolio-actions">
                    <a href="${pageContext.request.contextPath}/portfolio/edit/${portfolio.portfolioId}" class="btn btn-secondary">수정</a>
                    <button onclick="if(confirm('정말 삭제하시겠습니까?')) location.href='${pageContext.request.contextPath}/portfolio/delete/${portfolio.portfolioId}'" class="btn btn-danger">삭제</button>
                    <a href="${pageContext.request.contextPath}/portfolio/list" class="btn btn-secondary">목록</a>
                </div>
            </div>
            
            <c:if test="${not empty portfolio.portfolioDesc}">
                <p class="portfolio-desc">${portfolio.portfolioDesc}</p>
            </c:if>
            
            <div class="portfolio-meta">
                <span>생성일: <fmt:formatDate value="${portfolio.portfolioCreateDate}" pattern="yyyy-MM-dd" /></span>
                <span>수정일: <fmt:formatDate value="${portfolio.portfolioUpdateDate}" pattern="yyyy-MM-dd HH:mm" /></span>
            </div>
        </div>
        
        <!-- 통계 카드 -->
        <div class="stats">
            <div class="stat-card">
                <div class="stat-label">총 투자금액</div>
                <div class="stat-value">
                    <fmt:formatNumber value="${totalInvestment}" type="number" />원
                </div>
            </div>
            
            <div class="stat-card">
                <div class="stat-label">현재 평가액</div>
                <div class="stat-value">
                    <fmt:formatNumber value="${currentValue}" type="number" />원
                </div>
            </div>
            
            <div class="stat-card">
                <div class="stat-label">손익</div>
                <div class="stat-value ${profitLoss >= 0 ? 'profit' : 'loss'}">
                    <c:choose>
                        <c:when test="${profitLoss >= 0}">+</c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                    <fmt:formatNumber value="${profitLoss}" type="number" />원
                </div>
            </div>
            
            <div class="stat-card">
                <div class="stat-label">수익률</div>
                <div class="stat-value ${profitLossRate >= 0 ? 'profit' : 'loss'}">
                    <c:choose>
                        <c:when test="${profitLossRate >= 0}">+</c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                    <fmt:formatNumber value="${profitLossRate}" pattern="0.00" />%
                </div>
            </div>
        </div>
        
        <!-- 보유 종목 -->
        <div class="stocks-section">
            <div class="section-header">
                <h2>보유 종목</h2>
                <a href="${pageContext.request.contextPath}/portfolio/addStock/${portfolio.portfolioId}" class="btn btn-primary">
                    + 종목 추가
                </a>
            </div>
            
            <c:choose>
                <c:when test="${not empty stocks}">
                    <table>
                        <thead>
                            <tr>
                                <th>종목명</th>
                                <th>수량</th>
                                <th>평균 매입가</th>
                                <th>현재가</th>
                                <th>매입금액</th>
                                <th>평가금액</th>
                                <th>손익</th>
                                <th>수익률</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${stocks}" var="stock">
                                <tr>
                                    <td>
                                        <div class="stock-name">${stock.stockName}</div>
                                        <div class="stock-code">${stock.stockCode}</div>
                                    </td>
                                    <td><fmt:formatNumber value="${stock.stockQuantity}" type="number" /></td>
                                    <td><fmt:formatNumber value="${stock.stockAvgPrice}" type="number" />원</td>
                                    <td><fmt:formatNumber value="${stock.currentPrice}" type="number" />원</td>
                                    <td><fmt:formatNumber value="${stock.totalPurchaseAmount}" type="number" />원</td>
                                    <td><fmt:formatNumber value="${stock.currentValue}" type="number" />원</td>
                                    <td class="${stock.profitLoss >= 0 ? 'profit' : 'loss'}">
                                        <c:choose>
                                            <c:when test="${stock.profitLoss >= 0}">+</c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                        <fmt:formatNumber value="${stock.profitLoss}" type="number" />원
                                    </td>
                                    <td class="${stock.profitLossRate >= 0 ? 'profit' : 'loss'}">
                                        <c:choose>
                                            <c:when test="${stock.profitLossRate >= 0}">+</c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                        <fmt:formatNumber value="${stock.profitLossRate}" pattern="0.00" />%
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/portfolio/editStock/${stock.portfolioStockId}" class="btn btn-secondary" style="padding: 0.25rem 0.75rem; font-size: 0.9rem;">수정</a>
                                        <button onclick="if(confirm('삭제하시겠습니까?')) location.href='${pageContext.request.contextPath}/portfolio/deleteStock/${stock.portfolioStockId}'" class="btn btn-danger" style="padding: 0.25rem 0.75rem; font-size: 0.9rem;">삭제</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <div class="empty-state-icon">📭</div>
                        <h3>보유한 종목이 없습니다</h3>
                        <p>종목을 추가하여 포트폴리오를 시작하세요</p>
                        <a href="${pageContext.request.contextPath}/portfolio/addStock/${portfolio.portfolioId}" class="btn btn-primary" style="margin-top: 1rem;">
                            종목 추가하기
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>
