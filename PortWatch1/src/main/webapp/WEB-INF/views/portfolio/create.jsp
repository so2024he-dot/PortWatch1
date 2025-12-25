<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../common/header.jsp" />

<style>
    .create-container {
        max-width: 900px;
        margin: 0 auto;
        padding: 2rem;
    }
    
    .create-card {
        background: white;
        border-radius: 20px;
        padding: 2.5rem;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    }
    
    .create-header {
        text-align: center;
        margin-bottom: 2rem;
        padding-bottom: 1.5rem;
        border-bottom: 2px solid #f3f4f6;
    }
    
    .create-icon {
        font-size: 3rem;
        margin-bottom: 1rem;
    }
    
    .create-title {
        font-size: 2rem;
        font-weight: 700;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }
    
    /* 종목 정보 카드 */
    .stock-info-card {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 15px;
        padding: 1.5rem;
        color: white;
        margin-bottom: 1.5rem;
        display: none;
    }
    
    .stock-info-card.show {
        display: block;
    }
    
    .stock-info-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 0.75rem;
    }
    
    .stock-info-label {
        font-size: 0.875rem;
        opacity: 0.9;
    }
    
    .stock-info-value {
        font-size: 1.25rem;
        font-weight: 700;
    }
    
    .badge-fractional {
        display: inline-block;
        background: rgba(255, 255, 255, 0.2);
        padding: 0.25rem 0.75rem;
        border-radius: 20px;
        font-size: 0.75rem;
        margin-left: 0.5rem;
    }
    
    .form-control, .form-select {
        border-radius: 10px;
        border: 2px solid #e5e7eb;
        padding: 0.75rem 1rem;
        width: 100%;
    }
    
    .quick-buttons {
        display: flex;
        gap: 0.5rem;
        margin-top: 0.5rem;
    }
    
    .btn-quick {
        flex: 1;
        padding: 0.5rem;
        border-radius: 8px;
        border: 2px solid #e5e7eb;
        background: white;
        color: #667eea;
        cursor: pointer;
    }
    
    .btn-quick:hover {
        background: #667eea;
        color: white;
    }
    
    .preview-card {
        background: #f9fafb;
        border-radius: 15px;
        padding: 1.5rem;
        margin-top: 1.5rem;
    }
    
    .preview-item {
        display: flex;
        justify-content: space-between;
        padding: 0.75rem 0;
        border-bottom: 1px solid #e5e7eb;
    }
    
    .btn-submit {
        width: 100%;
        padding: 1rem;
        font-size: 1.1rem;
        border-radius: 10px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        color: white;
        margin-top: 1.5rem;
        cursor: pointer;
    }
</style>

<div class="create-container">
    <div class="create-card">
        <div class="create-header">
            <div class="create-icon">📊</div>
            <h2 class="create-title">포트폴리오에 종목 추가</h2>
        </div>
        
        <!-- 디버그 정보 (개발용) -->
        <div id="debugInfo" style="background: #f0f0f0; padding: 10px; margin-bottom: 20px; font-size: 12px; font-family: monospace; display: none;">
            <strong>🔧 디버그 정보:</strong><br>
            환율: ${exchangeRate}<br>
            종목 수: ${stockList.size()}<br>
            <span id="debugLog"></span>
        </div>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        
        <form:form action="${pageContext.request.contextPath}/portfolio/create" 
                   method="post" 
                   modelAttribute="portfolioVO" 
                   id="portfolioForm">
            
            <!-- 종목 정보 카드 -->
            <div class="stock-info-card" id="stockInfoCard">
                <div class="stock-info-row">
                    <div class="stock-info-label">선택한 종목</div>
                    <div class="stock-info-value" id="infoStockName">-</div>
                </div>
                <div class="stock-info-row">
                    <div class="stock-info-label">시장</div>
                    <div>
                        <span class="stock-info-value" id="infoMarketType">-</span>
                        <span class="badge-fractional" id="fractionalBadge" style="display: none;">
                            ✨ 분할 매입 가능
                        </span>
                    </div>
                </div>
                <div class="stock-info-row">
                    <div class="stock-info-label">현재가</div>
                    <div>
                        <div class="stock-info-value" id="infoCurrentPrice">불러오는 중...</div>
                        <div style="font-size: 0.875rem; opacity: 0.8; margin-top: 0.25rem;" id="infoCurrentPriceKRW"></div>
                    </div>
                </div>
            </div>
            
            <!-- 종목 선택 -->
            <div class="mb-3">
                <label class="form-label">종목 선택 <span style="color: #dc2626;">*</span></label>
                <form:select path="stockId" class="form-select" required="required" id="stockId">
                    <form:option value="">종목을 선택하세요</form:option>
                    <c:forEach items="${stockList}" var="stock">
                        <form:option value="${stock.stock_id}" 
                                    data-code="${stock.stock_code}"
                                    data-name="${stock.stock_name}"
                                    data-market="${stock.market_type}">
                            ${stock.stock_name} (${stock.stock_code}) - ${stock.market_type}
                        </form:option>
                    </c:forEach>
                </form:select>
            </div>
            
            <!-- 보유 수량 -->
            <div class="mb-3">
                <label class="form-label">보유 수량 <span style="color: #dc2626;">*</span></label>
                <form:input path="quantity" 
                           type="number" 
                           class="form-control" 
                           min="0.01"
                           step="0.01"
                           required="required"
                           placeholder="예: 1, 0.5, 0.1"
                           id="quantityInput" />
                <div class="quick-buttons" id="quickButtons" style="display: none;">
                    <button type="button" class="btn-quick" data-qty="0.1">0.1주</button>
                    <button type="button" class="btn-quick" data-qty="0.5">0.5주</button>
                    <button type="button" class="btn-quick" data-qty="1">1주</button>
                    <button type="button" class="btn-quick" data-qty="10">10주</button>
                </div>
            </div>
            
            <!-- 평균 매입가 -->
            <div class="mb-3">
                <label class="form-label">평균 매입가 <span style="color: #dc2626;">*</span></label>
                <form:input path="avgPurchasePrice" 
                           type="number" 
                           class="form-control" 
                           min="0.01"
                           step="0.01"
                           required="required"
                           placeholder="매입가를 입력하세요"
                           id="purchasePrice" />
            </div>
            
            <!-- 매입일 -->
            <div class="mb-3">
                <label class="form-label">매입일 <span style="color: #dc2626;">*</span></label>
                <form:input path="purchaseDate" 
                           type="date" 
                           class="form-control" 
                           required="required"
                           id="purchaseDate" />
            </div>
            
            <!-- 미리보기 -->
            <div class="preview-card">
                <div style="font-weight: 700; margin-bottom: 1rem;">📋 매입 정보 미리보기</div>
                <div class="preview-item">
                    <span>종목</span>
                    <strong id="previewStock">-</strong>
                </div>
                <div class="preview-item">
                    <span>수량</span>
                    <strong id="previewQuantity">-</strong>
                </div>
                <div class="preview-item">
                    <span>매입가</span>
                    <strong id="previewPrice">-</strong>
                </div>
                <div class="preview-item">
                    <span>총 매입금액</span>
                    <strong id="previewTotal" style="font-size: 1.25rem; color: #667eea;">-</strong>
                </div>
                <div class="preview-item" id="krwPreview" style="display: none;">
                    <span>한화 환산</span>
                    <strong id="previewKRW">-</strong>
                </div>
            </div>
            
            <button type="submit" class="btn-submit">✅ 포트폴리오에 추가</button>
        </form:form>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
// Spring 5.0.7 + MySQL 8.0 완전 호환 JavaScript (ES5)
$(document).ready(function() {
    console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
    console.log('🚀 포트폴리오 생성 페이지 초기화 (Spring 5.0.7)');
    
    // 폼 요소
    var stockSelect = $('#stockId');
    var quantityInput = $('#quantityInput');
    var priceInput = $('#purchasePrice');
    var dateInput = $('#purchaseDate');
    
    // 정보 카드
    var stockInfoCard = $('#stockInfoCard');
    var infoStockName = $('#infoStockName');
    var infoMarketType = $('#infoMarketType');
    var infoCurrentPrice = $('#infoCurrentPrice');
    var infoCurrentPriceKRW = $('#infoCurrentPriceKRW');
    var fractionalBadge = $('#fractionalBadge');
    var quickButtons = $('#quickButtons');
    var krwPreview = $('#krwPreview');
    
    // 미리보기
    var previewStock = $('#previewStock');
    var previewQuantity = $('#previewQuantity');
    var previewPrice = $('#previewPrice');
    var previewTotal = $('#previewTotal');
    var previewKRW = $('#previewKRW');
    
    // 환율 (서버에서 전달)
    var EXCHANGE_RATE = parseFloat('${exchangeRate}') || 1310.0;
    console.log('💱 환율: ' + EXCHANGE_RATE + ' KRW/USD');
    
    // 현재 선택된 종목
    var currentStock = null;
    
    // ============================================
    // 종목 선택 이벤트
    // ============================================
    stockSelect.on('change', function() {
        var selectedOption = $(this).find('option:selected');
        
        if (!selectedOption.val()) {
            stockInfoCard.removeClass('show');
            quickButtons.hide();
            krwPreview.hide();
            currentStock = null;
            updatePreview();
            return;
        }
        
        var stockCode = selectedOption.data('code');
        var stockName = selectedOption.data('name');
        var marketType = selectedOption.data('market');
        
        console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
        console.log('📊 종목 선택: ' + stockCode + ' - ' + stockName);
        
        fetchStockInfo(stockCode, stockName, marketType);
    });
    
    // ============================================
    // 종목 정보 가져오기 (API)
    // ============================================
    function fetchStockInfo(stockCode, stockName, marketType) {
        console.log('🔄 API 호출 시작: /api/stock/info/' + stockCode);
        
        infoStockName.text(stockName + ' (' + stockCode + ')');
        infoMarketType.text(marketType);
        infoCurrentPrice.text('불러오는 중...');
        infoCurrentPriceKRW.text('');
        stockInfoCard.addClass('show');
        
        $.ajax({
            url: '${pageContext.request.contextPath}/api/stock/info/' + stockCode,
            type: 'GET',
            dataType: 'json',
            timeout: 10000,
            success: function(response) {
                console.log('✅ API 응답 받음:', response);
                
                if (response.success === true) {
                    currentStock = response;
                    displayStockInfo(response);
                } else {
                    console.error('❌ API 오류:', response.message);
                    showError('종목 정보를 가져올 수 없습니다: ' + response.message);
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ AJAX 오류:', status, error);
                console.error('응답 상태:', xhr.status);
                console.error('응답 내용:', xhr.responseText);
                showError('API 호출 실패: ' + error);
            }
        });
    }
    
    // ============================================
    // 종목 정보 표시
    // ============================================
    function displayStockInfo(data) {
        console.log('📝 종목 정보 표시 시작');
        console.log('- stockCode:', data.stockCode);
        console.log('- stockName:', data.stockName);
        console.log('- marketType:', data.marketType);
        console.log('- currentPrice:', data.currentPrice);
        console.log('- isUSStock:', data.isUSStock);
        
        infoStockName.text(data.stockName + ' (' + data.stockCode + ')');
        infoMarketType.text(data.marketType);
        
        // 현재가 표시
        if (data.currentPrice != null && data.currentPrice > 0) {
            if (data.isUSStock === true) {
                var priceUSD = parseFloat(data.currentPrice);
                infoCurrentPrice.text('$' + priceUSD.toFixed(2));
                
                // 한화 환산
                var exchangeRate = data.exchangeRate || EXCHANGE_RATE;
                var priceKRW = priceUSD * exchangeRate;
                infoCurrentPriceKRW.text('≈ ₩' + Math.round(priceKRW).toLocaleString('ko-KR'));
                
                // 매입가 자동 입력
                priceInput.val(priceUSD.toFixed(2));
                
                console.log('💰 미국 주식 현재가: $' + priceUSD.toFixed(2));
                console.log('💴 한화 환산: ₩' + Math.round(priceKRW).toLocaleString());
                
            } else {
                var priceKRW = parseFloat(data.currentPrice);
                infoCurrentPrice.text('₩' + Math.round(priceKRW).toLocaleString('ko-KR'));
                priceInput.val(Math.round(priceKRW));
                
                console.log('💰 한국 주식 현재가: ₩' + Math.round(priceKRW).toLocaleString());
            }
        } else {
            infoCurrentPrice.text('가격 정보 없음');
            console.warn('⚠️ 현재가 정보 없음');
        }
        
        // 분할 매입 가능 여부
        if (data.fractionalTrading === true) {
            fractionalBadge.show();
            quickButtons.show();
            quantityInput.attr('step', '0.01').attr('min', '0.01');
            console.log('✨ 분할 매입 가능');
        } else {
            fractionalBadge.hide();
            quickButtons.hide();
            quantityInput.attr('step', '1').attr('min', '1');
        }
        
        // 한화 환산 표시
        if (data.isUSStock === true) {
            krwPreview.show();
        } else {
            krwPreview.hide();
        }
        
        console.log('✅ 종목 정보 표시 완료');
        console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
        
        updatePreview();
    }
    
    // ============================================
    // 오류 표시
    // ============================================
    function showError(message) {
        infoCurrentPrice.text('오류: ' + message);
        console.error('❌ ' + message);
    }
    
    // ============================================
    // 빠른 입력 버튼
    // ============================================
    $('.btn-quick').on('click', function() {
        var qty = $(this).data('qty');
        quantityInput.val(qty);
        updatePreview();
    });
    
    // ============================================
    // 입력값 변경 시
    // ============================================
    quantityInput.on('input', updatePreview);
    priceInput.on('input', updatePreview);
    
    // ============================================
    // 미리보기 업데이트
    // ============================================
    function updatePreview() {
        if (!currentStock) {
            previewStock.text('-');
            previewQuantity.text('-');
            previewPrice.text('-');
            previewTotal.text('-');
            previewKRW.text('-');
            return;
        }
        
        previewStock.text(currentStock.stockName + ' (' + currentStock.stockCode + ')');
        
        var quantity = parseFloat(quantityInput.val()) || 0;
        var price = parseFloat(priceInput.val()) || 0;
        
        if (quantity > 0) {
            previewQuantity.text(quantity.toFixed(2) + '주');
        } else {
            previewQuantity.text('-');
        }
        
        if (price > 0) {
            if (currentStock.isUSStock === true) {
                previewPrice.text('$' + price.toFixed(2));
            } else {
                previewPrice.text('₩' + Math.round(price).toLocaleString('ko-KR'));
            }
        } else {
            previewPrice.text('-');
        }
        
        if (quantity > 0 && price > 0) {
            var total = quantity * price;
            
            if (currentStock.isUSStock === true) {
                previewTotal.text('$' + total.toFixed(2));
                
                var exchangeRate = currentStock.exchangeRate || EXCHANGE_RATE;
                var krwTotal = total * exchangeRate;
                previewKRW.text('₩' + Math.round(krwTotal).toLocaleString('ko-KR') + ' ($' + total.toFixed(2) + ' × ₩' + exchangeRate.toFixed(2) + ')');
            } else {
                previewTotal.text('₩' + Math.round(total).toLocaleString('ko-KR'));
            }
        } else {
            previewTotal.text('-');
            previewKRW.text('-');
        }
    }
    
    // ============================================
    // 오늘 날짜 기본값
    // ============================================
    if (!dateInput.val()) {
        var today = new Date();
        var year = today.getFullYear();
        var month = ('0' + (today.getMonth() + 1)).slice(-2);
        var day = ('0' + today.getDate()).slice(-2);
        dateInput.val(year + '-' + month + '-' + day);
    }
    
    console.log('✅ 초기화 완료');
    console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
});
</script>

<jsp:include page="../common/footer.jsp" />
