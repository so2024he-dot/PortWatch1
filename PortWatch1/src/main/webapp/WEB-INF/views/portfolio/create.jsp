<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../common/header.jsp" />

<style>
.create-container {
	max-width: 800px;
	margin: 0 auto;
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
	font-size: 3.5rem;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	margin-bottom: 1rem;
}

.create-title {
	font-size: 2rem;
	font-weight: 700;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	margin: 0;
}

.form-label {
	font-weight: 600;
	color: #374151;
	margin-bottom: 0.5rem;
	display: flex;
	align-items: center;
	gap: 0.5rem;
}

.form-label .badge {
	font-size: 0.75rem;
	padding: 0.25rem 0.5rem;
}

.form-control, .form-select {
	border-radius: 10px;
	border: 2px solid #e5e7eb;
	padding: 0.75rem 1rem;
	font-size: 1rem;
	transition: all 0.3s;
}

.form-control:focus, .form-select:focus {
	border-color: var(--primary-color);
	box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.invalid-feedback {
	display: block;
	color: #dc2626;
	font-size: 0.875rem;
	margin-top: 0.25rem;
}

.btn-submit {
	width: 100%;
	padding: 1rem;
	font-size: 1.1rem;
	font-weight: 600;
	border-radius: 10px;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	border: none;
	color: white;
	transition: all 0.3s;
	margin-top: 1.5rem;
}

.btn-submit:hover {
	transform: translateY(-2px);
	box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-cancel {
	width: 100%;
	padding: 1rem;
	font-size: 1.1rem;
	font-weight: 600;
	border-radius: 10px;
	background: #6b7280;
	border: none;
	color: white;
	transition: all 0.3s;
}

.btn-cancel:hover {
	background: #4b5563;
}

.info-box {
	background: #f0f9ff;
	border-left: 4px solid #3b82f6;
	padding: 1rem 1.25rem;
	border-radius: 8px;
	margin-bottom: 1.5rem;
}

.info-box i {
	color: #3b82f6;
	font-size: 1.25rem;
}

/* 실시간 계산 미리보기 */
.preview-card {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
	border-radius: 15px;
	padding: 1.5rem;
	margin: 1.5rem 0;
	box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.preview-title {
	font-size: 1rem;
	opacity: 0.9;
	margin-bottom: 1rem;
	font-weight: 500;
}

.preview-amount {
	font-size: 2rem;
	font-weight: 700;
	margin: 0;
}

.preview-details {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 1rem;
	margin-top: 1rem;
	padding-top: 1rem;
	border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.preview-item {
	text-align: center;
}

.preview-item-label {
	font-size: 0.875rem;
	opacity: 0.8;
	margin-bottom: 0.25rem;
}

.preview-item-value {
	font-size: 1.25rem;
	font-weight: 600;
}

.input-hint {
	display: block;
	font-size: 0.875rem;
	color: #6b7280;
	margin-top: 0.5rem;
}

.input-example {
	display: inline-block;
	background: #f3f4f6;
	padding: 0.25rem 0.75rem;
	border-radius: 6px;
	font-family: monospace;
	font-size: 0.875rem;
	margin-top: 0.25rem;
}

@media ( max-width : 576px) {
	.create-card {
		padding: 1.5rem;
	}
	.create-title {
		font-size: 1.5rem;
	}
	.create-icon {
		font-size: 2.5rem;
	}
	.preview-amount {
		font-size: 1.5rem;
	}
	.preview-details {
		grid-template-columns: 1fr;
	}
}
</style>

<div class="create-container animate-fade-in">
	<div class="create-card">
		<div class="create-header">
			<div class="create-icon">
				<i class="bi bi-plus-circle"></i>
			</div>
			<h2 class="create-title">종목 추가</h2>
		</div>

		<!-- Error Message -->
		<c:if test="${not empty error}">
			<div class="alert alert-danger" role="alert">
				<i class="bi bi-exclamation-circle me-2"></i>${error}
			</div>
		</c:if>

		<!-- Info Box -->
		<div class="info-box">
			<i class="bi bi-info-circle me-2"></i> <strong>안내:</strong> 한국 주식은 <strong>1주
				단위</strong>로 거래됩니다. 보유 수량과 평균 매입가를 정확히 입력해주세요.
		</div>

		<!-- Form -->
		<form:form
			action="${pageContext.request.contextPath}/portfolio/create"
			method="post" modelAttribute="portfolioVO" id="portfolioForm">

			<!-- 종목 선택 -->
			<div class="mb-4">
				<label for="stockId" class="form-label"> <i
					class="bi bi-search"></i> 종목 선택 <span class="badge bg-danger">필수</span>
				</label>
				<form:select path="stockId" class="form-select" required="required"
					id="stockSelect">
					<form:option value="">종목을 선택하세요</form:option>
					<c:forEach var="stock" items="${stockList}">
						<form:option value="${stock.stock_id}">
                            ${stock.stock_name} (${stock.stock_code}) - 
                            <c:choose>
								<c:when test="${stock.market_type == 'KOSPI'}">코스피</c:when>
								<c:when test="${stock.market_type == 'KOSDAQ'}">코스닥</c:when>
								<c:otherwise>${stock.market_type}</c:otherwise>
							</c:choose>
						</form:option>
					</c:forEach>
				</form:select>
				<form:errors path="stockId" cssClass="invalid-feedback" />
			</div>

			<!-- 보유 수량 -->
			<div class="mb-4">
				<label for="quantity" class="form-label"> <i
					class="bi bi-123"></i> 보유 수량 <span class="badge bg-danger">필수</span>
					<span class="badge bg-info">1주 단위</span>
				</label>
				<form:input path="quantity" type="number" class="form-control"
					min="0.01" step="0.01" required="required"
					placeholder="보유 수량 (예: 1, 0.5, 0.1)" id="quantityInput" />
				<small class="text-muted"> 💡 소수점 매입 가능 (예: 0.5주, 0.1주) </small>
				<form:errors path="quantity" cssClass="invalid-feedback" />
				<span class="input-hint"> <i class="bi bi-lightbulb"></i> 정수만
					입력 가능합니다 (예: 10주, 100주)
				</span>
				<div class="input-example">예: 10 (10주)</div>
			</div>

			<!-- 평균 매입가 -->
			<div class="mb-4">
				<label for="avgPurchasePrice" class="form-label"> <i
					class="bi bi-cash"></i> 평균 매입가 (1주당 가격) <span
					class="badge bg-danger">필수</span>
				</label>
				<div class="input-group">
					<form:input path="avgPurchasePrice" type="number"
						class="form-control" placeholder="1주당 평균 매입가를 입력하세요" min="1"
						step="1" required="required" id="priceInput" />
					<span class="input-group-text">원</span>
				</div>
				<form:errors path="avgPurchasePrice" cssClass="invalid-feedback" />
				<span class="input-hint"> <i class="bi bi-lightbulb"></i> 1주당
					가격입니다. 정수로 입력하세요 (예: 50000원, 72500원)
				</span>
				<div class="input-example">예: 50000 (5만원/주)</div>
			</div>

			<div class="preview-item">
				<span>총 매입금액</span> <strong id="previewTotal">-</strong>
			</div>
			<!-- ✅ 미국 주식용 한화 표시 추가 -->
			<div class="preview-item" id="krwPreview" style="display: none;">
				<span>한화 환산</span> <strong id="previewKRW">-</strong> <small
					class="text-muted" id="exchangeRateInfo"></small>
			</div>

			<!-- 매입일 -->
			<div class="mb-4">
				<label for="purchaseDate" class="form-label"> <i
					class="bi bi-calendar"></i> 매입 일자 <span class="badge bg-secondary">선택</span>
				</label>
				<form:input path="purchaseDate" type="date" class="form-control"
					id="dateInput" />
				<form:errors path="purchaseDate" cssClass="invalid-feedback" />
			</div>

			<!-- 실시간 계산 미리보기 -->
			<div class="preview-card" id="previewCard" style="display: none;">
				<div class="preview-title">
					<i class="bi bi-calculator me-2"></i>투자 금액 미리보기
				</div>
				<h3 class="preview-amount" id="totalAmount">0원</h3>
				<div class="preview-details">
					<div class="preview-item">
						<div class="preview-item-label">보유 수량</div>
						<div class="preview-item-value" id="previewQuantity">0주</div>
					</div>
					<div class="preview-item">
						<div class="preview-item-label">평균 단가</div>
						<div class="preview-item-value" id="previewPrice">0원</div>
					</div>
				</div>
			</div>

			<!-- 버튼 -->
			<div class="row g-3">
				<div class="col-md-6">
					<a href="${pageContext.request.contextPath}/portfolio/list"
						class="btn btn-cancel"> <i class="bi bi-x-circle me-2"></i>취소
					</a>
				</div>
				<div class="col-md-6">
					<button type="submit" class="btn btn-submit" id="submitBtn">
						<i class="bi bi-check-circle me-2"></i>추가하기
					</button>
				</div>
			</div>
		</form:form>
	</div>
</div>

<script>
$(document).ready(function() {
    const stockSelect = $('#stockId');
    const quantityInput = $('#quantityInput');
    const priceInput = $('#purchasePrice');
    const previewQuantity = $('#previewQuantity');
    const previewPrice = $('#previewPrice');
    const previewTotal = $('#previewTotal');
    const previewKRW = $('#previewKRW');
    const krwPreview = $('#krwPreview');
    const exchangeRateInfo = $('#exchangeRateInfo');
    
    // 환율 정보 (서버에서 전달받음)
    const EXCHANGE_RATE = ${not empty exchangeRate ? exchangeRate : 1310.00};
    
    // 현재 선택된 종목 정보
    let currentStock = {
        code: '',
        name: '',
        marketType: '',
        isUSStock: false
    };
    
    // 종목 선택 시
    stockSelect.on('change', function() {
        const selectedOption = $(this).find('option:selected');
        
        if (!selectedOption.val()) {
            currentStock = { code: '', name: '', marketType: '', isUSStock: false };
            krwPreview.hide();
            return;
        }
        
        const stockCode = selectedOption.data('code');
        const stockName = selectedOption.data('name');
        const marketType = selectedOption.data('market');
        
        currentStock = {
            code: stockCode,
            name: stockName,
            marketType: marketType,
            isUSStock: (marketType === 'NASDAQ' || marketType === 'NYSE' || marketType === 'AMEX')
        };
        
        // 현재가 자동 가져오기
        fetchCurrentPrice(stockCode);
        
        // 미국 주식이면 한화 표시 영역 보이기
        if (currentStock.isUSStock) {
            krwPreview.show();
            exchangeRateInfo.text('환율: 1 USD = ₩' + EXCHANGE_RATE.toLocaleString('ko-KR', {minimumFractionDigits: 2}));
        } else {
            krwPreview.hide();
        }
        
        updatePreview();
    });
    
    // 입력값 변경 시 미리보기 업데이트
    quantityInput.on('input', updatePreview);
    priceInput.on('input', updatePreview);
    
    // 미리보기 업데이트 함수
    function updatePreview() {
        const quantity = parseFloat(quantityInput.val()) || 0;
        const price = parseFloat(priceInput.val()) || 0;
        
        if (quantity > 0) {
            previewQuantity.text(quantity.toLocaleString('ko-KR', {minimumFractionDigits: 0, maximumFractionDigits: 2}) + '주');
        } else {
            previewQuantity.text('-');
        }
        
        if (price > 0) {
            if (currentStock.isUSStock) {
                previewPrice.text('$' + price.toLocaleString('ko-KR', {minimumFractionDigits: 2}));
            } else {
                previewPrice.text('₩' + price.toLocaleString('ko-KR', {minimumFractionDigits: 0}));
            }
        } else {
            previewPrice.text('-');
        }
        
        if (quantity > 0 && price > 0) {
            const total = quantity * price;
            
            if (currentStock.isUSStock) {
                // 미국 주식: 달러 + 한화
                previewTotal.text('$' + total.toLocaleString('ko-KR', {minimumFractionDigits: 2}));
                
                const krwTotal = total * EXCHANGE_RATE;
                previewKRW.html(
                    '₩' + krwTotal.toLocaleString('ko-KR', {minimumFractionDigits: 0}) + 
                    '<br><small style="color: #6b7280; font-weight: normal;">' +
                    '($' + total.toLocaleString('ko-KR', {minimumFractionDigits: 2}) + ' × ' + 
                    EXCHANGE_RATE.toLocaleString('ko-KR', {minimumFractionDigits: 2}) + ')</small>'
                );
            } else {
                // 한국 주식: 원화만
                previewTotal.text('₩' + total.toLocaleString('ko-KR', {minimumFractionDigits: 0}));
            }
        } else {
            previewTotal.text('-');
            previewKRW.text('-');
        }
    }
    
    // 현재가 가져오기
    function fetchCurrentPrice(stockCode) {
        $.ajax({
            url: '${pageContext.request.contextPath}/api/stock/current-price/' + stockCode,
            type: 'GET',
            success: function(data) {
                if (data && data.currentPrice) {
                    priceInput.val(data.currentPrice);
                    updatePreview();
                }
            },
            error: function(xhr, status, error) {
                console.error('현재가 조회 실패:', error);
            }
        });
    }
    
    // 폼 제출 전 검증
    $('form').on('submit', function(e) {
        const quantity = parseFloat(quantityInput.val());
        const price = parseFloat(priceInput.val());
        
        if (!stockSelect.val()) {
            e.preventDefault();
            alert('종목을 선택해주세요.');
            stockSelect.focus();
            return false;
        }
        
        if (!quantity || quantity <= 0) {
            e.preventDefault();
            alert('수량을 입력해주세요.');
            quantityInput.focus();
            return false;
        }
        
        if (!price || price <= 0) {
            e.preventDefault();
            alert('매입가를 입력해주세요.');
            priceInput.focus();
            return false;
        }
        
        // 확인 메시지
        const total = quantity * price;
        let confirmMsg = `다음 내용으로 종목을 추가하시겠습니까?\n\n` +
                        `종목: ${currentStock.name} (${currentStock.code})\n` +
                        `수량: ${quantity.toLocaleString('ko-KR', {maximumFractionDigits: 2})}주\n`;
        
        if (currentStock.isUSStock) {
            const krwTotal = total * EXCHANGE_RATE;
            confirmMsg += `매입가: $${price.toLocaleString('ko-KR', {minimumFractionDigits: 2})}\n` +
                         `총액: $${total.toLocaleString('ko-KR', {minimumFractionDigits: 2})}\n` +
                         `한화: ₩${krwTotal.toLocaleString('ko-KR', {minimumFractionDigits: 0})}`;
        } else {
            confirmMsg += `매입가: ₩${price.toLocaleString('ko-KR', {minimumFractionDigits: 0})}\n` +
                         `총액: ₩${total.toLocaleString('ko-KR', {minimumFractionDigits: 0})}`;
        }
        
        return confirm(confirmMsg);
    });
});

</script>

<style>
/* 알림 애니메이션 추가 */
@
keyframes slideInRight {from { transform:translateX(100%);
	opacity: 0;
}

to {
	transform: translateX(0);
	opacity: 1;
}
}
</
script
>
<
jsp




:include


 


page




="../
common
/




footer




.jsp




"
/
>
