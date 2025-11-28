<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>관심종목 - PortWatch</title>
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
            max-width: 1200px;
            margin: 0 auto;
        }
        
        .header {
            background: white;
            padding: 25px 30px;
            border-radius: 15px;
            margin-bottom: 30px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        
        .header h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 28px;
        }
        
        .header p {
            color: #666;
            font-size: 14px;
        }
        
        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background: white;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.1);
        }
        
        .stat-card h3 {
            font-size: 14px;
            color: #888;
            margin-bottom: 8px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .stat-card .value {
            font-size: 32px;
            font-weight: bold;
            color: #667eea;
        }
        
        .watchlist-table {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        thead {
            background: #f8f9fa;
        }
        
        th {
            padding: 15px;
            text-align: left;
            font-weight: 600;
            color: #555;
            border-bottom: 2px solid #e9ecef;
        }
        
        td {
            padding: 15px;
            border-bottom: 1px solid #f1f3f5;
        }
        
        tbody tr:hover {
            background: #f8f9fa;
            transition: background 0.2s;
        }
        
        .stock-name {
            font-weight: 600;
            color: #333;
            font-size: 16px;
        }
        
        .stock-code {
            color: #888;
            font-size: 13px;
            margin-top: 3px;
        }
        
        .market-badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
        }
        
        .market-kospi {
            background: #e3f2fd;
            color: #1976d2;
        }
        
        .market-kosdaq {
            background: #f3e5f5;
            color: #7b1fa2;
        }
        
        .price {
            font-size: 18px;
            font-weight: bold;
            color: #333;
        }
        
        .price-change {
            font-size: 14px;
            margin-top: 3px;
        }
        
        .up {
            color: #f44336;
        }
        
        .down {
            color: #2196f3;
        }
        
        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            transition: all 0.3s;
        }
        
        .btn-danger {
            background: #f44336;
            color: white;
        }
        
        .btn-danger:hover {
            background: #d32f2f;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(244, 67, 54, 0.3);
        }
        
        .btn-primary {
            background: #667eea;
            color: white;
        }
        
        .btn-primary:hover {
            background: #5568d3;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #888;
        }
        
        .empty-state-icon {
            font-size: 64px;
            margin-bottom: 20px;
        }
        
        .empty-state h3 {
            font-size: 20px;
            color: #666;
            margin-bottom: 10px;
        }
        
        .empty-state p {
            font-size: 14px;
            margin-bottom: 20px;
        }
        
        .message {
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-weight: 500;
        }
        
        .message-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .message-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .actions {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Header -->
        <div class="header">
            <h1>⭐ 관심종목</h1>
            <p>관심있는 종목을 한눈에 확인하세요</p>
        </div>
        
        <!-- Messages -->
        <c:if test="${not empty message}">
            <div class="message message-${messageType}">
                ${message}
            </div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="message message-error">
                ${error}
            </div>
        </c:if>
        
        <!-- Stats -->
        <div class="stats">
            <div class="stat-card">
                <h3>관심종목</h3>
                <div class="value">${watchlist.size()}</div>
            </div>
        </div>
        
        <!-- Actions -->
        <div class="actions">
            <a href="${pageContext.request.contextPath}/stock/list" class="btn btn-primary">
                + 종목 추가하기
            </a>
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                홈으로
            </a>
        </div>
        
        <!-- Watchlist Table -->
        <div class="watchlist-table">
            <c:choose>
                <c:when test="${empty watchlist}">
                    <div class="empty-state">
                        <div class="empty-state-icon">⭐</div>
                        <h3>관심종목이 없습니다</h3>
                        <p>종목 목록에서 관심있는 종목을 추가해보세요!</p>
                        <a href="${pageContext.request.contextPath}/stock/list" class="btn btn-primary">
                            종목 둘러보기
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>종목명</th>
                                <th>시장</th>
                                <th>섹터</th>
                                <th>현재가</th>
                                <th>등락률</th>
                                <th>등록일</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${watchlist}" var="item">
                                <tr>
                                    <td>
                                        <div class="stock-name">${item.stockName}</div>
                                        <div class="stock-code">${item.stockCode}</div>
                                    </td>
                                    <td>
                                        <span class="market-badge market-${item.stockMarket == 'KOSPI' ? 'kospi' : 'kosdaq'}">
                                            ${item.stockMarket}
                                        </span>
                                    </td>
                                    <td>${item.stockSector}</td>
                                    <td>
                                        <div class="price">
                                            <fmt:formatNumber value="${item.currentPrice}" pattern="#,##0"/>원
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${item.priceChange > 0}">
                                                <div class="price-change up">
                                                    ▲ <fmt:formatNumber value="${item.priceChange}" pattern="#,##0"/>
                                                    (<fmt:formatNumber value="${item.priceChangeRate}" pattern="0.00"/>%)
                                                </div>
                                            </c:when>
                                            <c:when test="${item.priceChange < 0}">
                                                <div class="price-change down">
                                                    ▼ <fmt:formatNumber value="${-item.priceChange}" pattern="#,##0"/>
                                                    (<fmt:formatNumber value="${-item.priceChangeRate}" pattern="0.00"/>%)
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="price-change">-</div>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${item.watchlistRegDate}" pattern="yyyy-MM-dd"/>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/watchlist/remove/${item.watchlistId}" 
                                              method="post" style="display:inline;"
                                              onsubmit="return confirm('관심종목에서 삭제하시겠습니까?');">
                                            <button type="submit" class="btn btn-danger">삭제</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>
