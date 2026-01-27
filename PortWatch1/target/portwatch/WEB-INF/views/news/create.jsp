<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>포트폴리오 추가 - PortWatch</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .purchase-container {
            max-width: 700px;
            margin: 0 auto;
            background: white;
            border-radius: 20px;
            padding: 2rem;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }
        
        .purchase-header {
            text-align: center;
            margin-bottom: 2rem;
        }
        
        .purchase-header h2 {
            font-weight: 700;
            color: #1f2937;
            margin-bottom: 0.5rem;
        }
        
        .stock-info-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
            color: white;
            display: none;
        }
        
        .stock-info-card.active {
            display: block;
        }
        
        .stock-name {
            font-size: 1.5rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }
        
        .stock-code {
            opacity: 0.9;
            font-size: 1rem;
            margin-bottom: 1rem;
        }
        
        .current-price {
            font-size: 2rem;
            font-weight: 700;
            margin-top: 0.5rem;
        }
        
        .price-badge {
            background: rgba(255,255,255,0.2);
            padding: 5px 12px;
            border-radius: 10px;
            display: inline-block;
            font-size: 0.85rem;
        }
        
        .exchange-rate-card {
            background: #fff3cd;
            border: 2px solid #ffc107;
            border-radius: 10px;
            padding: 1rem;
            margin-bottom: 1.5rem;
            display: none;
        }
        
        .exchange-rate-card.active {
            display: block;
        }
        
        .exchange-rate-card .rate-value {
            font-size: 1.3rem;
            font-weight: 700;
            color: #856404;
        }
        
        .form-group {
            margin-bottom: 1.5rem;
        }
        
        .form-label {
            font-weight: 600;
            color: #374151;
            margin-bottom: 0.5rem;
            display: block;
        }
        
        .form-control, .form-select {
            border: 2px solid #e5e7eb;
            border-radius: 10px;
            padding: 0.75rem 1rem;
            font-size: 1.1rem;
            font-weight: 600;
            transition: all 0.3s;
        }
        
        .form-control:focus, .form-select:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        .fraction-buttons {
            display: none;
            grid-template-columns: repeat(4, 1fr);
            gap: 10px;
            margin-top: 10px;
        }
        
        .fraction-buttons.active {
            display: grid;
        }
        
        .fraction-btn {
            padding: 10px;
            border: 2px solid #e5e7eb;
            background: white;
            border-radius: 10px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .fraction-btn:hover {
            border-color: #667eea;
            background: #f3f4f6;
        }
        
        .fraction-btn.selected {
            border-color: #667eea;
            background: #667eea;
            color: white;
        }
        
        .summary-card {
            background: #f9fafb;
            border-radius: 15px;
            padding: 1.5rem;
            margin-top: 1.5rem;
        }
        
        .summary-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.75rem 0;
            border-bottom: 1px solid #e5e7eb;
        }
        
        .summary-row:last-child {
            border-bottom: none;
            font-size: 1.2rem;
            font-weight: 700;
            color: #667eea;
        }
        
        .summary-label {
            font-weight: 600;
            color: #6b7280;
        }
        
        .summary-value {
            font-weight: 700;
            color: #1f2937;
            font-size: 1.1rem;
        }
        
        .btn-purchase {
            width: 100%;
            padding: 1rem;
            font-size: 1.1rem;
            font-weight: 700;
            border-radius: 12px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            transition: all 0.3s;
        }
        
        .btn-purchase:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
        }
        
        .btn-purchase:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }
        
        .alert-box {
            display: none;
            margin-top: 1rem;
            border-radius: 10px;
        }
        
        .input-group-text {
            background: #f3f4f6;
            border: 2px solid #e5e7eb;
            font-weight: 600;
        }
    </style>
</head>
<body>
    <div class="purchase-container">
        <!-- 헤더 -->
        <div class="purchase-header">
            <h2>
                <i class="fas fa-plus-circle"></i> 포트폴리오 추가
            </h2>
            <p class="text-muted">주식을 선택하고 매수하세요</p>
        </div>
        
        <!-- 주식 선택 -->
        <div class="form-group">
            <label class="form-label">
                <i class="fas fa-search"></i> 주식 선택
            </label>
            <select id="stockSelect" class="form-select">
                <option value="">-- 종목을 선택하세요 --</option>
                <c:forEach items="${stockList}" var="stock">
                    <option 
                        value="${stock.stockCode}"
                        data-name="${stock.stockName}"
                        data-price="${stock.currentPrice}"
                        data-country="${stock.country}">
                        ${stock.stockName} (${stock.stockCode})
                        <c:if test="${stock.country == 'KR'}">🇰🇷</c:if>
                        <c:if test="${stock.country == 'US'}">🇺🇸</c:if>
                    </option>
                </c:forEach>
            </select>
        </div>
        
        <!-- 주식 정보 카드 -->
        <div class="stock-info-card" id="stockInfoCard">
            <div class="stock-name" id="displayStockName">-</div>
            <div class="stock-code" id="displayStockCode">
                - 
                <span class="price-badge" id="displayCountryBadge">-</span>
            </div>
            <div>현재가</div>
            <div class="current-price" id="displayCurrentPrice">-</div>
        </div>
        
        <!-- 환율 정보 카드 (미국 주식인 경우만) -->
        <div class="exchange-rate-card" id="exchangeRateCard">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <i class="fas fa-exchange-alt"></i> 
                    <strong>환율 정보</strong>
                </div>
                <div class="rate-value">
                    1 USD = <span id="rateValue">1,350.00</span> KRW
                </div>
            </div>
            <small class="text-muted d-block mt-2">
                ※ 원화 환산 금액은 참고용이며, 실제 거래는 달러로 진행됩니다.
            </small>
        </div>
        
        <!-- 매입 폼 -->
        <form id="purchaseForm">
            <!-- 수량 입력 -->
            <div class="form-group">
                <label class="form-label">
                    <i class="fas fa-hashtag"></i> 매입 수량
                </label>
                <div class="input-group">
                    <input type="number" 
                           id="quantityInput" 
                           class="form-control"
                           placeholder="수량 입력"
                           step="1"
                           min="1"
                           value="1"
                           required>
                    <span class="input-group-text">주</span>
                </div>
                
                <!-- 미국 주식 4분할 버튼 -->
                <div class="fraction-buttons" id="fractionButtons">
                    <button type="button" class="fraction-btn" data-value="0.25">
                        1/4주<br><small>(0.25)</small>
                    </button>
                    <button type="button" class="fraction-btn" data-value="0.5">
                        1/2주<br><small>(0.5)</small>
                    </button>
                    <button type="button" class="fraction-btn" data-value="0.75">
                        3/4주<br><small>(0.75)</small>
                    </button>
                    <button type="button" class="fraction-btn selected" data-value="1">
                        1주<br><small>(1.0)</small>
                    </button>
                </div>
            </div>
            
            <!-- 매입 단가 (읽기 전용) -->
            <div class="form-group">
                <label class="form-label">
                    <i class="fas fa-tag"></i> 매입 단가 (현재가)
                </label>
                <div class="input-group">
                    <input type="text" 
                           id="priceDisplay" 
                           class="form-control"
                           value="-"
                           readonly>
                    <span class="input-group-text" id="priceUnit">-</span>
                </div>
                
                <!-- 미국 주식 원화 환산 -->
                <small class="text-muted mt-1 d-none" id="priceInKrwContainer">
                    <i class="fas fa-won-sign"></i> 
                    원화 환산: <strong id="priceInKrw">-</strong>원
                </small>
            </div>
            
            <!-- 매입 요약 -->
            <div class="summary-card">
                <h5 class="mb-3">
                    <i class="fas fa-calculator"></i> 매입 요약
                </h5>
                
                <div class="summary-row">
                    <span class="summary-label">수량</span>
                    <span class="summary-value" id="summaryQuantity">-</span>
                </div>
                
                <div class="summary-row">
                    <span class="summary-label">단가</span>
                    <span class="summary-value" id="summaryPrice">-</span>
                </div>
                
                <div class="summary-row">
                    <span class="summary-label">수수료 (0.1%)</span>
                    <span class="summary-value" id="summaryCommission">-</span>
                </div>
                
                <div class="summary-row">
                    <span class="summary-label">총 투자 금액</span>
                    <span class="summary-value" id="summaryTotal">-</span>
                </div>
                
                <!-- 미국 주식 원화 표시 -->
                <div class="summary-row d-none" id="summaryTotalKrwContainer" 
                     style="border-top: 2px solid #667eea; margin-top: 0.5rem; padding-top: 1rem;">
                    <span class="summary-label">
                        <i class="fas fa-won-sign"></i> 원화 환산
                    </span>
                    <span class="summary-value" id="summaryTotalKrw" style="color: #f59e0b;">
                        -
                    </span>
                </div>
            </div>
            
            <!-- 알림 메시지 -->
            <div id="alertBox" class="alert-box"></div>
            
            <!-- 매입 버튼 -->
            <button type="submit" class="btn btn-purchase mt-3" id="submitBtn" disabled>
                <i class="fas fa-check-circle"></i> 포트폴리오에 추가하기
            </button>
            
            <!-- 취소 버튼 -->
            <a href="${pageContext.request.contextPath}/portfolio/list" 
               class="btn btn-secondary w-100 mt-2">
                <i class="fas fa-arrow-left"></i> 취소
            </a>
        </form>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    
    <script>
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 포트폴리오 추가 관리자 - 환율 표시 버전 (2026.01.19)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    const PurchaseManager = {
        stockCode: '',
        stockName: '',
        currentPrice: 0,
        country: '',
        contextPath: '${pageContext.request.contextPath}',
        memberId: '${member.memberId}',
        exchangeRate: 1350.00, // 기본값
        
        /**
         * 초기화
         */
        init: function() {
            console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
            console.log('💰 포트폴리오 추가 관리자 초기화');
            console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
            
            this.bindEvents();
            this.loadExchangeRate();
        },
        
        /**
         * 환율 로드
         */
        loadExchangeRate: function() {
            console.log('💱 환율 조회 중...');
            
            $.ajax({
                url: this.contextPath + '/api/exchange/rate',
                type: 'GET',
                success: (response) => {
                    if (response.success) {
                        this.exchangeRate = parseFloat(response.rate);
                        $('#rateValue').text(this.formatNumber(this.exchangeRate));
                        
                        console.log('✅ 환율 로드 완료:', this.exchangeRate);
                    }
                },
                error: () => {
                    console.warn('⚠️ 환율 조회 실패, 기본값 사용');
                }
            });
        },
        
        /**
         * 이벤트 바인딩
         */
        bindEvents: function() {
            // 주식 선택
            $('#stockSelect').on('change', (e) => {
                const option = e.target.selectedOptions[0];
                
                if (!option || !option.value) {
                    this.resetForm();
                    return;
                }
                
                this.updateStockInfo(option);
            });
            
            // 수량 입력 변경
            $('#quantityInput').on('input', () => {
                this.calculateSummary();
            });
            
            // 4분할 버튼
            $('.fraction-btn').on('click', (e) => {
                const value = $(e.currentTarget).data('value');
                $('#quantityInput').val(value);
                
                $('.fraction-btn').removeClass('selected');
                $(e.currentTarget).addClass('selected');
                
                this.calculateSummary();
            });
            
            // 폼 제출
            $('#purchaseForm').on('submit', (e) => {
                e.preventDefault();
                this.executePurchase();
            });
        },
        
        /**
         * 주식 정보 업데이트
         */
        updateStockInfo: function(option) {
            // 데이터 읽기
            this.stockCode = option.value;
            this.stockName = option.getAttribute('data-name');
            this.currentPrice = parseFloat(option.getAttribute('data-price'));
            this.country = option.getAttribute('data-country');
            
            console.log('📊 선택된 주식:', this.stockName, this.stockCode, this.country);
            
            // 주식 정보 카드 표시
            $('#displayStockName').text(this.stockName);
            $('#displayStockCode').text(this.stockCode);
            $('#displayCountryBadge').text(this.country === 'KR' ? '🇰🇷 한국 주식' : '🇺🇸 미국 주식');
            
            // 현재가 표시
            const priceStr = this.country === 'KR' ? 
                this.formatNumber(this.currentPrice) + '원' : 
                '$' + this.currentPrice.toFixed(2);
            $('#displayCurrentPrice').text(priceStr);
            
            $('#stockInfoCard').addClass('active');
            
            // 수량 입력 설정
            if (this.country === 'US') {
                $('#quantityInput').attr('step', '0.001').attr('min', '0.001');
                $('#fractionButtons').addClass('active');
                $('#exchangeRateCard').addClass('active');
                $('#priceInKrwContainer').removeClass('d-none');
                $('#summaryTotalKrwContainer').removeClass('d-none');
            } else {
                $('#quantityInput').attr('step', '1').attr('min', '1').val('1');
                $('#fractionButtons').removeClass('active');
                $('#exchangeRateCard').removeClass('active');
                $('#priceInKrwContainer').addClass('d-none');
                $('#summaryTotalKrwContainer').addClass('d-none');
            }
            
            // 매입 단가 표시
            $('#priceDisplay').val(this.currentPrice.toFixed(2));
            $('#priceUnit').text(this.country === 'KR' ? '원' : 'USD');
            
            // 제출 버튼 활성화
            $('#submitBtn').prop('disabled', false);
            
            // 계산 실행
            this.calculateSummary();
        },
        
        /**
         * 실시간 계산
         */
        calculateSummary: function() {
            if (!this.stockCode) {
                return;
            }
            
            const quantity = parseFloat($('#quantityInput').val()) || 0;
            
            console.log('💰 실시간 계산:', quantity, '주');
            
            // 1. 수량 표시
            const quantityStr = this.country === 'US' ? 
                quantity.toFixed(3) + ' 주' : 
                Math.floor(quantity) + ' 주';
            $('#summaryQuantity').text(quantityStr);
            
            // 2. 단가 표시
            const priceStr = this.country === 'KR' ? 
                this.formatNumber(this.currentPrice) + '원' : 
                '$' + this.currentPrice.toFixed(2);
            $('#summaryPrice').text(priceStr);
            
            // 3. 총 금액 계산
            const totalAmount = quantity * this.currentPrice;
            
            // 4. 수수료 계산
            const commission = totalAmount * 0.001;
            
            // 5. 최종 금액
            const finalAmount = totalAmount + commission;
            
            // 수수료 표시
            const commissionStr = this.country === 'KR' ? 
                this.formatNumber(commission) + '원' : 
                '$' + commission.toFixed(2);
            $('#summaryCommission').text(commissionStr);
            
            // 총 투자 금액 표시
            const finalStr = this.country === 'KR' ? 
                this.formatNumber(finalAmount) + '원' : 
                '$' + finalAmount.toFixed(2);
            $('#summaryTotal').text(finalStr);
            
            // 미국 주식 원화 환산
            if (this.country === 'US') {
                const priceInKrw = this.currentPrice * this.exchangeRate;
                const totalInKrw = finalAmount * this.exchangeRate;
                
                $('#priceInKrw').text(this.formatNumber(priceInKrw));
                $('#summaryTotalKrw').text(this.formatNumber(totalInKrw) + '원');
            }
        },
        
        /**
         * 매입 실행
         */
        executePurchase: function() {
            const quantity = parseFloat($('#quantityInput').val());
            
            if (!this.stockCode) {
                this.showAlert('danger', '주식을 선택하세요.');
                return;
            }
            
            if (!quantity || quantity <= 0) {
                this.showAlert('danger', '유효한 수량을 입력하세요.');
                return;
            }
            
            // 한국 주식: 정수만
            if (this.country === 'KR' && quantity % 1 !== 0) {
                this.showAlert('danger', '한국 주식은 정수 수량만 가능합니다.');
                return;
            }
            
            console.log('💳 포트폴리오 추가 실행:', this.stockCode, quantity, '주');
            
            // 로딩 표시
            const submitBtn = $('#submitBtn');
            const originalHtml = submitBtn.html();
            submitBtn.prop('disabled', true);
            submitBtn.html('<i class="fas fa-spinner fa-spin"></i> 추가 중...');
            
            // API 호출
            $.ajax({
                url: this.contextPath + '/api/purchase/execute',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    stockCode: this.stockCode,
                    quantity: quantity,
                    price: this.currentPrice
                }),
                success: (response) => {
                    console.log('✅ 추가 성공:', response);
                    
                    this.showAlert('success', '포트폴리오에 추가되었습니다!');
                    
                    setTimeout(() => {
                        window.location.href = this.contextPath + '/portfolio/list';
                    }, 1500);
                },
                error: (xhr) => {
                    console.error('❌ 추가 실패:', xhr);
                    
                    const error = xhr.responseJSON || {};
                    const message = error.message || '포트폴리오 추가에 실패했습니다.';
                    
                    this.showAlert('danger', message);
                    
                    submitBtn.prop('disabled', false);
                    submitBtn.html(originalHtml);
                }
            });
        },
        
        /**
         * 폼 초기화
         */
        resetForm: function() {
            this.stockCode = '';
            this.stockName = '';
            this.currentPrice = 0;
            this.country = '';
            
            $('#stockInfoCard').removeClass('active');
            $('#exchangeRateCard').removeClass('active');
            $('#fractionButtons').removeClass('active');
            $('#priceInKrwContainer').addClass('d-none');
            $('#summaryTotalKrwContainer').addClass('d-none');
            
            $('#quantityInput').val('1');
            $('#priceDisplay').val('-');
            $('#priceUnit').text('-');
            
            $('#summaryQuantity').text('-');
            $('#summaryPrice').text('-');
            $('#summaryCommission').text('-');
            $('#summaryTotal').text('-');
            
            $('#submitBtn').prop('disabled', true);
        },
        
        /**
         * 숫자 포맷팅
         */
        formatNumber: function(num) {
            return Math.floor(num).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        },
        
        /**
         * 알림 표시
         */
        showAlert: function(type, message) {
            const alertBox = $('#alertBox');
            alertBox.removeClass('alert-success alert-danger alert-warning alert-info');
            alertBox.addClass('alert alert-' + type);
            alertBox.html('<i class="fas fa-' + 
                (type === 'success' ? 'check-circle' : 
                 type === 'danger' ? 'exclamation-triangle' : 
                 'info-circle') + 
                '"></i> ' + message);
            alertBox.show();
            
            if (type !== 'danger') {
                setTimeout(() => {
                    alertBox.fadeOut();
                }, 3000);
            }
        }
    };
    
    // 페이지 로드 시 초기화
    $(document).ready(function() {
        PurchaseManager.init();
    });
    </script>
</body>
</html>
