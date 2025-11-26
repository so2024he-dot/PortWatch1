<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>종목 목록 - PortWatch</title>
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
        
        .page-header {
            background: white;
            padding: 2rem;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            margin-bottom: 2rem;
            text-align: center;
        }
        
        .page-header h1 {
            color: #667eea;
            font-size: 2.5rem;
            margin-bottom: 0.5rem;
        }
        
        .page-header p {
            color: #666;
            font-size: 1.1rem;
        }
        
        /* 검색 섹션 */
        .search-section {
            background: white;
            padding: 2rem;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            margin-bottom: 2rem;
        }
        
        .search-form {
            display: flex;
            gap: 1rem;
        }
        
        .search-input {
            flex: 1;
            padding: 1rem;
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            font-size: 1rem;
        }
        
        .search-input:focus {
            outline: none;
            border-color: #667eea;
        }
        
        .search-select {
            padding: 1rem;
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            font-size: 1rem;
            min-width: 150px;
        }
        
        .btn {
            padding: 1rem 2rem;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            font-weight: 600;
            font-size: 1rem;
            transition: all 0.3s;
        }
        
        .btn-primary {
            background: #667eea;
            color: white;
        }
        
        .btn-primary:hover {
            background: #5568d3;
        }
        
        /* 필터 버튼 */
        .filters {
            display: flex;
            gap: 0.5rem;
            margin-top: 1rem;
        }
        
        .filter-btn {
            padding: 0.5rem 1rem;
            border: 2px solid #e0e0e0;
            background: white;
            border-radius: 20px;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .filter-btn:hover, .filter-btn.active {
            background: #667eea;
            color: white;
            border-color: #667eea;
        }
        
        /* 종목 카드 */
        .stocks-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 1.5rem;
        }
        
        .stock-card {
            background: white;
            padding: 1.5rem;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            transition: transform 0.3s;
            cursor: pointer;
        }
        
        .stock-card:hover {
            transform: translateY(-5px);
        }
        
        .stock-header {
            display: flex;
            justify-content: space-between;
            align-items: start;
            margin-bottom: 1rem;
        }
        
        .stock-name {
            font-size: 1.3rem;
            font-weight: 600;
            color: #333;
            margin-bottom: 0.25rem;
        }
        
        .stock-code {
            color: #999;
            font-size: 0.9rem;
        }
        
        .stock-market {
            padding: 0.25rem 0.75rem;
            border-radius: 15px;
            font-size: 0.8rem;
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
        
        .market-konex {
            background: #fff3e0;
            color: #f57c00;
        }
        
        .stock-price {
            font-size: 1.8rem;
            font-weight: bold;
            color: #667eea;
            margin-bottom: 0.5rem;
        }
        
        .stock-change {
            font-size: 1rem;
            font-weight: 600;
        }
        
        .change-up {
            color: #4caf50;
        }
        
        .change-down {
            color: #f44336;
        }
        
        .stock-sector {
            color: #666;
            font-size: 0.9rem;
            margin-top: 0.5rem;
        }
        
        .stock-actions {
            display: flex;
            gap: 0.5rem;
            margin-top: 1rem;
        }
        
        .btn-small {
            padding: 0.5rem 1rem;
            font-size: 0.9rem;
        }
        
        .btn-secondary {
            background: #e0e0e0;
            color: #333;
        }
        
        .btn-secondary:hover {
            background: #d0d0d0;
        }
        
        /* 빈 상태 */
        .empty-state {
            background: white;
            padding: 4rem 2rem;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            text-align: center;
        }
        
        .empty-icon {
            font-size: 4rem;
            margin-bottom: 1rem;
        }
        
        .empty-state h3 {
            color: #333;
            margin-bottom: 0.5rem;
        }
        
        .empty-state p {
            color: #666;
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
        <!-- 페이지 헤더 -->
        <div class="page-header">
            <h1>📊 종목 목록</h1>
            <p>KOSPI, KOSDAQ, KONEX 종목을 검색하고 분석하세요</p>
        </div>
        
        <!-- 검색 섹션 -->
        <div class="search-section">
            <form action="${pageContext.request.contextPath}/stock/list" method="get" class="search-form">
                <input type="text" name="keyword" class="search-input" placeholder="종목명 또는 종목코드 검색..." value="${param.keyword}">
                <select name="market" class="search-select">
                    <option value="">전체 시장</option>
                    <option value="KOSPI" ${param.market == 'KOSPI' ? 'selected' : ''}>KOSPI</option>
                    <option value="KOSDAQ" ${param.market == 'KOSDAQ' ? 'selected' : ''}>KOSDAQ</option>
                    <option value="KONEX" ${param.market == 'KONEX' ? 'selected' : ''}>KONEX</option>
                </select>
                <button type="submit" class="btn btn-primary">🔍 검색</button>
            </form>
            
            <!-- 섹터 필터 -->
            <div class="filters">
                <button class="filter-btn ${empty param.sector ? 'active' : ''}" onclick="location.href='${pageContext.request.contextPath}/stock/list'">전체</button>
                <button class="filter-btn ${param.sector == 'IT' ? 'active' : ''}" onclick="location.href='${pageContext.request.contextPath}/stock/list?sector=IT'">IT</button>
                <button class="filter-btn ${param.sector == '금융' ? 'active' : ''}" onclick="location.href='${pageContext.request.contextPath}/stock/list?sector=금융'">금융</button>
                <button class="filter-btn ${param.sector == '제조' ? 'active' : ''}" onclick="location.href='${pageContext.request.contextPath}/stock/list?sector=제조'">제조</button>
                <button class="filter-btn ${param.sector == '화학' ? 'active' : ''}" onclick="location.href='${pageContext.request.contextPath}/stock/list?sector=화학'">화학</button>
                <button class="filter-btn ${param.sector == '인터넷' ? 'active' : ''}" onclick="location.href='${pageContext.request.contextPath}/stock/list?sector=인터넷'">인터넷</button>
            </div>
        </div>
        
        <!-- 종목 목록 -->
        <c:choose>
            <c:when test="${not empty stocks}">
                <div class="stocks-grid">
                    <c:forEach items="${stocks}" var="stock">
                        <div class="stock-card" onclick="location.href='${pageContext.request.contextPath}/stock/detail/${stock.stockCode}'">
                            <div class="stock-header">
                                <div>
                                    <div class="stock-name">${stock.stockName}</div>
                                    <div class="stock-code">${stock.stockCode}</div>
                                </div>
                                <span class="stock-market market-${stock.stockMarket.toLowerCase()}">
                                    ${stock.stockMarket}
                                </span>
                            </div>
                            
                            <div class="stock-price">
                                <fmt:formatNumber value="${stock.currentPrice}" type="number" />원
                            </div>
                            
                            <div class="stock-change ${stock.priceChange >= 0 ? 'change-up' : 'change-down'}">
                                <c:choose>
                                    <c:when test="${stock.priceChange >= 0}">
                                        ▲ +<fmt:formatNumber value="${stock.priceChange}" type="number" />원 
                                        (+<fmt:formatNumber value="${stock.priceChangeRate}" pattern="0.00" />%)
                                    </c:when>
                                    <c:otherwise>
                                        ▼ <fmt:formatNumber value="${stock.priceChange}" type="number" />원 
                                        (<fmt:formatNumber value="${stock.priceChangeRate}" pattern="0.00" />%)
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <c:if test="${not empty stock.stockSector}">
                                <div class="stock-sector">📁 ${stock.stockSector}</div>
                            </c:if>
                            
                            <div class="stock-actions">
                                <button onclick="event.stopPropagation(); addToWatchlist('${stock.stockCode}')" class="btn btn-secondary btn-small">
                                    ⭐ 관심종목
                                </button>
                                <button onclick="event.stopPropagation(); location.href='${pageContext.request.contextPath}/stock/detail/${stock.stockCode}'" class="btn btn-primary btn-small">
                                    상세보기
                                </button>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-icon">🔍</div>
                    <h3>검색 결과가 없습니다</h3>
                    <p>다른 검색어나 필터를 시도해보세요</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    
    <script>
        function addToWatchlist(stockCode) {
            // 관심종목 추가 기능 (AJAX로 구현)
            fetch('${pageContext.request.contextPath}/watchlist/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ stockCode: stockCode })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('관심종목에 추가되었습니다.');
                } else {
                    alert(data.message || '추가에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('오류가 발생했습니다.');
            });
        }
    </script>
</body>
</html>
