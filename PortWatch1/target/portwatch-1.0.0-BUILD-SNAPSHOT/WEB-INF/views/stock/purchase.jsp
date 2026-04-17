<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>주식 매입 - PortWatch</title>
    
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
        
        .form-control {
            border: 2px solid #e5e7eb;
            border-radius: 10px;
            padding: 0.75rem 1rem;
            font-size: 1.1rem;
            font-weight: 600;
            transition: all 0.3s;
        }
        
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        .fraction-buttons {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 10px;
            margin-top: 10px;
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
        
        .fraction-btn.active {
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
        
        .btn-purchase:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
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
                <i class="fas fa-shopping-cart"></i> 주식 매입
            </h2>
            <p class="text-muted">원하는 수량을 입력하고 매입하세요</p>
        </div>
        
        <!-- 주식 정보 카드 -->
        <div class="stock-info-card">
            <div class="stock-name">${stock.stockName}</div>
            <div class="stock-code">
                ${stock.stockCode} 
                <span class="price-badge">
                    <c:choose>
                        <c:when test="${stock.country == 'KR'}">
                            🇰🇷 한국 주식
                        </c:when>
                        <c:otherwise>
                            🇺🇸 미국 주식
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div>현재가</div>
            <div class="current-price">
                <c:choose>
                    <c:when test="${stock.country == 'KR'}">
                        <fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0"/>원
                    </c:when>
                    <c:otherwise>
                        $<fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0.00"/>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        
        <!-- 환율 정보 카드 (미국 주식인 경우만) -->
        <c:if test="${stock.country == 'US'}">
            <div class="exchange-rate-card" id="exchangeRateCard">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <i class="fas fa-exchange-alt"></i> 
                        <strong>환율 정보</strong>
                    </div>
                    <div class="rate-value" id="exchangeRate">
                        1 USD = <span id="rateValue">1,350.00</span> KRW
                    </div>
                </div>
                <small class="text-muted d-block mt-2">
                    ※ 원화 환산 금액은 참고용이며, 실제 거래는 달러로 진행됩니다.
                </small>
            </div>
        </c:if>
        
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
                           <c:choose>
                               <c:when test="${stock.country == 'KR'}">
                                   step="1" min="1"
                               </c:when>
                               <c:otherwise>
                                   step="0.001" min="0.001"
                               </c:otherwise>
                           </c:choose>
                           value="1"
                           required>
                    <span class="input-group-text">주</span>
                </div>
                
                <!-- 미국 주식 4분할 버튼 -->
                <c:if test="${stock.country == 'US'}">
                    <div class="fraction-buttons">
                        <button type="button" class="fraction-btn" data-value="0.25">
                            1/4주<br><small>(0.25)</small>
                        </button>
                        <button type="button" class="fraction-btn" data-value="0.5">
                            1/2주<br><small>(0.5)</small>
                        </button>
                        <button type="button" class="fraction-btn" data-value="0.75">
                            3/4주<br><small>(0.75)</small>
                        </button>
                        <button type="button" class="fraction-btn active" data-value="1">
                            1주<br><small>(1.0)</small>
                        </button>
                    </div>
                </c:if>
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
                           value="<fmt:formatNumber value='${stock.currentPrice}' pattern='#,##0.00'/>"
                           readonly>
                    <span class="input-group-text">
                        <c:if test="${stock.country == 'KR'}">원</c:if>
                        <c:if test="${stock.country == 'US'}">USD</c:if>
                    </span>
                </div>
                
                <!-- 미국 주식 원화 환산 -->
                <c:if test="${stock.country == 'US'}">
                    <small class="text-muted mt-1 d-block">
                        <i class="fas fa-won-sign"></i> 
                        원화 환산: <strong id="priceInKrw">-</strong>원
                    </small>
                </c:if>
            </div>
            
            <!-- 매입 요약 -->
            <div class="summary-card">
                <h5 class="mb-3">
                    <i class="fas fa-calculator"></i> 매입 요약
                </h5>
                
                <div class="summary-row">
                    <span class="summary-label">수량</span>
                    <span class="summary-value" id="summaryQuantity">1.000 주</span>
                </div>
                
                <div class="summary-row">
                    <span class="summary-label">단가</span>
                    <span class="summary-value" id="summaryPrice">
                        <c:choose>
                            <c:when test="${stock.country == 'KR'}">
                                <fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0"/>원
                            </c:when>
                            <c:otherwise>
                                $<fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0.00"/>
                            </c:otherwise>
                        </c:choose>
                    </span>
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
                <c:if test="${stock.country == 'US'}">
                    <div class="summary-row" style="border-top: 2px solid #667eea; margin-top: 0.5rem; padding-top: 1rem;">
                        <span class="summary-label">
                            <i class="fas fa-won-sign"></i> 원화 환산
                        </span>
                        <span class="summary-value" id="summaryTotalKrw" style="color: #f59e0b;">
                            -
                        </span>
                    </div>
                </c:if>
            </div>
            
            <!-- 알림 메시지 -->
            <div id="alertBox" class="alert-box"></div>
            
            <!-- 매입 버튼 -->
            <button type="submit" class="btn btn-purchase mt-3">
                <i class="fas fa-check-circle"></i> 매입하기
            </button>
            
            <!-- 취소 버튼 -->
            <a href="${pageContext.request.contextPath}/stock/list" 
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
     * 주식 매입 관리자 - 환율 표시 버전 (2026.01.16)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    const PurchaseManager = {
        stockCode: '${stock.stockCode}',
        stockName: '${stock.stockName}',
        // ✅ null 안전 처리: ${stock.currentPrice}가 null이면 NaN → BigDecimal("null") → 500 에러
        currentPrice: parseFloat('${not empty stock.currentPrice ? stock.currentPrice : 0}') || 0,
        country: '${stock.country}',
        contextPath: '${pageContext.request.contextPath}',
        // ✅ StockController는 loginMember 키로 저장 (member 키 아님)
        memberId: '${not empty loginMember ? loginMember.memberId : ""}',
        exchangeRate: 1350.00, // 기본값
        
        /**
         * 초기화
         */
        init: function() {
            console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
            console.log('💰 매입 관리자 초기화');
            console.log('  - 종목:', this.stockName);
            console.log('  - 코드:', this.stockCode);
            console.log('  - 현재가:', this.currentPrice);
            console.log('  - 국가:', this.country);
            console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
            
            this.bindEvents();
            
            // 미국 주식인 경우 환율 로드
            if (this.country === 'US') {
                this.loadExchangeRate();
            }
            
            this.calculateSummary();
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
                        
                        // 환율 로드 후 계산 실행
                        this.calculateSummary();
                    }
                },
                error: (xhr) => {
                    console.warn('⚠️ 환율 조회 실패, 기본값 사용');
                }
            });
        },
        
        /**
         * 이벤트 바인딩
         */
        bindEvents: function() {
            // 수량 입력 변경
            $('#quantityInput').on('input', () => {
                this.calculateSummary();
            });
            
            // 4분할 버튼
            $('.fraction-btn').on('click', (e) => {
                const value = $(e.currentTarget).data('value');
                $('#quantityInput').val(value);
                
                $('.fraction-btn').removeClass('active');
                $(e.currentTarget).addClass('active');
                
                this.calculateSummary();
            });
            
            // 폼 제출
            $('#purchaseForm').on('submit', (e) => {
                e.preventDefault();
                this.executePurchase();
            });
        },
        
        /**
         * 실시간 계산
         */
        calculateSummary: function() {
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
            
            console.log('  - 총 금액:', totalAmount);
            console.log('  - 수수료:', commission);
            console.log('  - 최종 금액:', finalAmount);
        },
        
        /**
         * 매입 실행
         */
        executePurchase: function() {
            const quantity = parseFloat($('#quantityInput').val());
            
            if (!quantity || quantity <= 0) {
                this.showAlert('danger', '유효한 수량을 입력하세요.');
                return;
            }
            
            // 한국 주식: 정수만
            if (this.country === 'KR' && quantity % 1 !== 0) {
                this.showAlert('danger', '한국 주식은 정수 수량만 가능합니다.');
                return;
            }
            
            console.log('💳 매입 실행:', this.stockCode, quantity, '주');
            
            // 로딩 표시
            const submitBtn = $('button[type="submit"]');
            const originalHtml = submitBtn.html();
            submitBtn.prop('disabled', true);
            submitBtn.html('<i class="fas fa-spinner fa-spin"></i> 매입 중...');
            
            // ✅ currentPrice 재확인 (NaN 방어)
            const safePrice = (isNaN(this.currentPrice) || this.currentPrice <= 0)
                ? 1
                : this.currentPrice;

            console.log('  - API 전송 가격:', safePrice);

            // API 호출
            $.ajax({
                url: this.contextPath + '/api/purchase/execute',
                type: 'POST',
                contentType: 'application/json',
                // ✅ xhrFields로 쿠키(세션) 전송 보장
                xhrFields: { withCredentials: true },
                data: JSON.stringify({
                    stockCode: this.stockCode,
                    quantity: quantity,
                    price: safePrice
                }),
                success: (response) => {
                    console.log('✅ 매입 성공:', response);

                    const qty   = response.quantity   || quantity;
                    const price = response.price      || safePrice;
                    const name  = response.stockName  || this.stockName;

                    this.showAlert('success',
                        '✅ 매입 완료! ' + name + ' ' + qty + '주 @ ' +
                        (this.country === 'KR'
                            ? this.formatNumber(price) + '원'
                            : '$' + parseFloat(price).toFixed(2)));

                    setTimeout(() => {
                        // ✅ dashboard → portfolio 페이지로 이동
                        window.location.href = this.contextPath + '/portfolio';
                    }, 1500);
                },
                error: (xhr) => {
                    console.error('❌ 매입 실패:', xhr.status, xhr.responseJSON);

                    submitBtn.prop('disabled', false);
                    submitBtn.html(originalHtml);

                    // ✅ 401: 세션 만료 → 로그인 페이지
                    if (xhr.status === 401) {
                        alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
                        window.location.href = this.contextPath + '/member/login';
                        return;
                    }

                    const error   = xhr.responseJSON || {};
                    const message = error.message    || '매수 처리 중 오류가 발생했습니다.';
                    this.showAlert('danger', '❌ ' + message);
                }
            });
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
