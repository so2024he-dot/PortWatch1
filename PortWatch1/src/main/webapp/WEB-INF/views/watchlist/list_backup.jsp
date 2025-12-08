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
        
        .btn-success {
            background: #10b981;
            color: white;
            margin-right: 5px;
        }
        
        .btn-success:hover {
            background: #059669;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(16, 185, 129, 0.3);
        }
        
        /* Modal Styles */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
        }
        
        .modal.show {
            display: block;
        }
        
        .modal-content {
            background: white;
            margin: 5% auto;
            padding: 30px;
            border-radius: 15px;
            width: 90%;
            max-width: 500px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            animation: slideIn 0.3s;
        }
        
        @keyframes slideIn {
            from { transform: translateY(-50px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }
        
        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #f1f3f5;
        }
        
        .modal-header h2 {
            color: #333;
            font-size: 22px;
            margin: 0;
        }
        
        .close {
            font-size: 28px;
            font-weight: bold;
            color: #999;
            cursor: pointer;
            line-height: 1;
            transition: color 0.2s;
        }
        
        .close:hover {
            color: #333;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-weight: 600;
            font-size: 14px;
        }
        
        .form-group input {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #e9ecef;
            border-radius: 8px;
            font-size: 15px;
            transition: border-color 0.3s;
        }
        
        .form-group input:focus {
            outline: none;
            border-color: #667eea;
        }
        
        .stock-info {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        
        .stock-info-name {
            font-size: 18px;
            font-weight: bold;
            color: #333;
            margin-bottom: 5px;
        }
        
        .stock-info-code {
            font-size: 14px;
            color: #666;
        }
        
        .stock-info-price {
            font-size: 16px;
            color: #667eea;
            font-weight: 600;
            margin-top: 5px;
        }
        
        .modal-footer {
            display: flex;
            gap: 10px;
            margin-top: 25px;
        }
        
        .modal-footer .btn {
            flex: 1;
            padding: 12px;
            font-size: 15px;
        }
        
        .btn-gray {
            background: #6b7280;
            color: white;
        }
        
        .btn-gray:hover {
            background: #4b5563;
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
            <p>관심있는 종목을 한눈에 확인하고 포트폴리오에 바로 추가하세요</p>
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
                                        <span class="market-badge market-${item.marketType == 'KOSPI' ? 'kospi' : 'kosdaq'}">
                                            ${item.marketType}
                                        </span>
                                    </td>
                                    <td>${item.industry}</td>
                                    <td>
                                        <div class="price">
                                            <c:choose>
                                                <c:when test="${not empty item.currentPrice}">
                                                    <fmt:formatNumber value="${item.currentPrice}" pattern="#,##0"/>원
                                                </c:when>
                                                <c:otherwise>-</c:otherwise>
                                            </c:choose>
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
                                        <fmt:formatDate value="${item.addedAt}" pattern="yyyy-MM-dd"/>
                                    </td>
                                    <td>
                                        <button type="button" 
                                                class="btn btn-success portfolio-add-btn" 
                                                data-stock-id="${item.stockId}"
                                                data-stock-name="${item.stockName}"
                                                data-stock-code="${item.stockCode}"
                                                data-current-price="${not empty item.currentPrice ? item.currentPrice : 0}">
                                            📊 포트폴리오
                                        </button>
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
    
    <!-- 포트폴리오 추가 모달 -->
    <div id="portfolioModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2>📊 포트폴리오에 추가</h2>
                <span class="close">&times;</span>
            </div>
            
            <div class="stock-info">
                <div class="stock-info-name" id="modalStockName"></div>
                <div class="stock-info-code">종목코드: <span id="modalStockCode"></span></div>
                <div class="stock-info-price" id="modalCurrentPrice"></div>
            </div>
            
            <form id="addPortfolioForm">
                <input type="hidden" id="modalStockId" name="stockId">
                
                <div class="form-group">
                    <label for="quantity">보유 수량 *</label>
                    <input type="number" 
                           id="quantity" 
                           name="quantity" 
                           placeholder="예: 10" 
                           min="1" 
                           required>
                </div>
                
                <div class="form-group">
                    <label for="avgPurchasePrice">평균 매입가 (원) *</label>
                    <input type="number" 
                           id="avgPurchasePrice" 
                           name="avgPurchasePrice" 
                           placeholder="예: 50000" 
                           min="1" 
                           required>
                </div>
                
                <div class="form-group">
                    <label for="purchaseDate">매입 일자 (선택)</label>
                    <input type="date" 
                           id="purchaseDate" 
                           name="purchaseDate">
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-gray" id="closeModalBtn">취소</button>
                    <button type="submit" class="btn btn-success">추가하기</button>
                </div>
            </form>
        </div>
    </div>
    
    <!-- jQuery 라이브러리 -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    
    <script>
    console.log('=== 관심종목 페이지 로드 완료 ===');
    
    // 페이지 로드 후 실행
    $(document).ready(function() {
        console.log('jQuery 로드 완료');
        console.log('contextPath:', '${pageContext.request.contextPath}');
        
        // 메시지 자동 숨김
        setTimeout(function() {
            $('.message').fadeOut('slow');
        }, 3000);
        
        // 포트폴리오 버튼 클릭 이벤트 (클래스 셀렉터 사용)
        $(document).on('click', '.portfolio-add-btn', function() {
            console.log('=== 포트폴리오 버튼 클릭됨 ===');
            
            var stockId = $(this).data('stock-id');
            var stockName = $(this).data('stock-name');
            var stockCode = $(this).data('stock-code');
            var currentPrice = $(this).data('current-price') || 0;
            
            console.log('전달된 데이터:', {
                stockId: stockId,
                stockName: stockName,
                stockCode: stockCode,
                currentPrice: currentPrice
            });
            
            // 데이터 유효성 검사
            if (!stockId) {
                console.error('stockId가 없습니다!');
                alert('종목 정보가 올바르지 않습니다.');
                return;
            }
            
            openAddToPortfolioModal(stockId, stockName, stockCode, currentPrice);
        });
        
        // 모달 닫기 버튼
        $('.close, #closeModalBtn').on('click', function() {
            console.log('모달 닫기 버튼 클릭');
            closeModal();
        });
        
        // 모달 외부 클릭 시 닫기
        $('#portfolioModal').on('click', function(event) {
            if ($(event.target).is('#portfolioModal')) {
                console.log('모달 외부 클릭');
                closeModal();
            }
        });
        
        // ESC 키로 모달 닫기
        $(document).on('keydown', function(event) {
            if (event.key === 'Escape') {
                console.log('ESC 키 눌림');
                closeModal();
            }
        });
        
        // 포트폴리오 추가 폼 제출
        $('#addPortfolioForm').on('submit', function(e) {
            e.preventDefault();
            console.log('=== 폼 제출 시작 ===');
            
            var stockId = $('#modalStockId').val();
            var quantity = $('#quantity').val();
            var avgPurchasePrice = $('#avgPurchasePrice').val();
            var purchaseDate = $('#purchaseDate').val();
            
            var formData = {
                stockId: stockId,
                quantity: quantity,
                avgPurchasePrice: avgPurchasePrice,
                purchaseDate: purchaseDate
            };
            
            console.log('전송할 데이터:', formData);
            
            // 유효성 검사
            if (!stockId || stockId === 'undefined') {
                alert('종목 정보가 올바르지 않습니다.');
                console.error('stockId 오류:', stockId);
                return;
            }
            
            if (!quantity || quantity <= 0) {
                alert('보유 수량을 입력해주세요.');
                return;
            }
            
            if (!avgPurchasePrice || avgPurchasePrice <= 0) {
                alert('평균 매입가를 입력해주세요.');
                return;
            }
            
            // AJAX 요청
            $.ajax({
                url: '${pageContext.request.contextPath}/api/portfolio/add',
                type: 'POST',
                data: formData,
                success: function(response) {
                    console.log('=== 응답 성공 ===');
                    console.log('응답 데이터:', response);
                    
                    if (response.success) {
                        alert('✅ ' + response.message);
                        closeModal();
                        
                        // 포트폴리오 페이지로 이동할지 묻기
                        if (confirm('포트폴리오 페이지로 이동하시겠습니까?')) {
                            location.href = '${pageContext.request.contextPath}/portfolio/list';
                        } else {
                            // 페이지 새로고침
                            location.reload();
                        }
                    } else {
                        if (response.requireLogin) {
                            alert('⚠️ 로그인이 필요합니다.');
                            location.href = '${pageContext.request.contextPath}/member/login';
                        } else {
                            alert('❌ ' + response.message);
                        }
                    }
                },
                error: function(xhr, status, error) {
                    console.error('=== AJAX 오류 ===');
                    console.error('Status:', status);
                    console.error('Error:', error);
                    console.error('Response:', xhr.responseText);
                    alert('❌ 포트폴리오 추가 중 오류가 발생했습니다.\n' + error);
                }
            });
        });
    });
    
    // 모달 열기 함수
    function openAddToPortfolioModal(stockId, stockName, stockCode, currentPrice) {
        console.log('=== 모달 열기 함수 실행 ===');
        console.log('Parameters:', {stockId, stockName, stockCode, currentPrice});
        
        // 값 설정
        $('#modalStockId').val(stockId);
        $('#modalStockName').text(stockName);
        $('#modalStockCode').text(stockCode);
        
        // 현재가 표시 및 자동 입력
        var priceText = currentPrice > 0 ? Number(currentPrice).toLocaleString() + '원' : '가격 정보 없음';
        $('#modalCurrentPrice').text('현재가: ' + priceText);
        
        if (currentPrice > 0) {
            $('#avgPurchasePrice').val(Math.floor(currentPrice));
        }
        
        // 오늘 날짜 자동 설정
        var today = new Date().toISOString().split('T')[0];
        $('#purchaseDate').val(today);
        
        // 모달 표시
        $('#portfolioModal').addClass('show');
        console.log('모달 열림 완료');
        
        // 설정된 값 확인
        console.log('설정된 값 확인:', {
            modalStockId: $('#modalStockId').val(),
            modalStockName: $('#modalStockName').text(),
            modalStockCode: $('#modalStockCode').text()
        });
    }
    
    // 모달 닫기 함수
    function closeModal() {
        $('#portfolioModal').removeClass('show');
        $('#addPortfolioForm')[0].reset();
        console.log('모달 닫힘');
    }
    </script>
</body>
</html>

    
