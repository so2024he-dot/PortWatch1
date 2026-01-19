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
            max-width: 800px;
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
        
        .stock-select-card {
            background: #f9fafb;
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
        }
        
        .stock-info-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
            color: white;
            display: none;
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
        
        /* ✅ 환율 정보 카드 스타일 추가 */
        .exchange-rate-card {
            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
            border-radius: 15px;
            padding: 1rem 1.5rem;
            margin-bottom: 1.5rem;
            color: white;
            display: none;
        }
        
        .exchange-rate-card .rate-label {
            font-size: 0.9rem;
            opacity: 0.9;
            margin-bottom: 0.3rem;
        }
        
        .exchange-rate-card .rate-value {
            font-size: 1.3rem;
            font-weight: 700;
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
        
        /* ✅ 원화 환산 금액 강조 스타일 */
        .summary-value.krw-converted {
            color: #10b981;
            font-size: 1.2rem;
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
            <p class="text-muted">종목을 선택하고 원하는 수량을 입력하세요</p>
        </div>
        
        <!-- 주식 선택 카드 -->
        <div class="stock-select-card">
            <label class="form-label">
                <i class="fas fa-search"></i> 주식 종목 선택
            </label>
            <select id="stockSelect" class="form-select">
                <option value="">-- 종목을 선택하세요 --</option>
                <c:forEach items="${stockList}" var="stockItem">
                    <option value="${stockItem.stockCode}" 
                            data-name="${stockItem.stockName}"
                            data-price="${stockItem.currentPrice}"
                            data-country="${stockItem.country}"
                            <c:if test="${stock.stockCode == stockItem.stockCode}">selected</c:if>>
                        ${stockItem.stockName} (${stockItem.stockCode}) - 
                        <c:choose>
                            <c:when test="${stockItem.country == 'KR'}">
                                <fmt:formatNumber value="${stockItem.currentPrice}" pattern="#,##0"/>원
                            </c:when>
                            <c:otherwise>
                                $<fmt:formatNumber value="${stockItem.currentPrice}" pattern="#,##0.00"/>
                            </c:otherwise>
                        </c:choose>
                    </option>
                </c:forEach>
            </select>
        </div>
        
        <!-- ✅ 환율 정보 카드 (미국 주식인 경우만 표시) -->
        <div id="exchangeRateCard" class="exchange-rate-card">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <div class="rate-label">
                        <i class="fas fa-exchange-alt"></i> USD → KRW 환율
                    </div>
                    <div class="rate-value" id="displayExchangeRate">
                        로딩 중...
                    </div>
                </div>
                <div class="text-end">
                    <small class="rate-label">실시간 환율</small>
                    <div><i class="fas fa-sync-alt fa-spin" id="exchangeRateSpinner"></i></div>
                </div>
            </div>
        </div>
        
        <!-- 선택된 주식 정보 카드 -->
        <div id="stockInfoCard" class="stock-info-card">
            <div class="stock-name" id="displayStockName"></div>
            <div class="stock-code" id="displayStockCode"></div>
            <div class="current-price" id="displayCurrentPrice"></div>
        </div>
        
        <!-- 매입 폼 -->
        <form id="purchaseForm">
            <!-- 숨겨진 필드들 -->
            <input type="hidden" id="selectedStockCode" name="stockCode">
            <input type="hidden" id="selectedCountry" name="country">
            <input type="hidden" id="selectedPrice" name="price">
            
            <!-- 현재가 표시 (읽기 전용) -->
            <div class="form-group">
                <label class="form-label">
                    <i class="fas fa-tag"></i> 현재가
                </label>
                <div class="input-group">
                    <input type="text" id="priceDisplay" class="form-control" readonly>
                    <span class="input-group-text" id="currencySymbol">USD</span>
                </div>
            </div>
            
            <!-- 수량 입력 -->
            <div class="form-group">
                <label class="form-label">
                    <i class="fas fa-sort-numeric-up"></i> 수량
                </label>
                <input type="number" id="quantityInput" class="form-control" 
                       placeholder="수량을 입력하세요" 
                       step="1" min="1" value="1" required>
                
                <!-- 4분할 버튼 (미국 주식인 경우만 표시) -->
                <div id="fractionButtons" class="fraction-buttons" style="display: none;">
                    <button type="button" class="fraction-btn" data-value="0.25">
                        <i class="fas fa-chart-pie"></i> 1/4주
                    </button>
                    <button type="button" class="fraction-btn" data-value="0.5">
                        <i class="fas fa-chart-pie"></i> 1/2주
                    </button>
                    <button type="button" class="fraction-btn" data-value="0.75">
                        <i class="fas fa-chart-pie"></i> 3/4주
                    </button>
                    <button type="button" class="fraction-btn" data-value="1">
                        <i class="fas fa-chart-pie"></i> 1주
                    </button>
                </div>
            </div>
            
            <!-- 매입 요약 -->
            <div class="summary-card">
                <h5 style="margin-bottom: 1rem; font-weight: 700;">
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
                
                <!-- ✅ 원화 환산 금액 (미국 주식인 경우만 표시) -->
                <div id="summaryKrwRow" class="summary-row" style="display: none;">
                    <span class="summary-label">
                        <i class="fas fa-won-sign"></i> 원화 환산
                    </span>
                    <span class="summary-value krw-converted" id="summaryKrwAmount">-</span>
                </div>
                
                <div class="summary-row">
                    <span class="summary-label">수수료 (0.1%)</span>
                    <span class="summary-value" id="summaryCommission">-</span>
                </div>
                
                <div class="summary-row">
                    <span class="summary-label">총 금액</span>
                    <span class="summary-value" id="summaryTotal">-</span>
                </div>
                
                <!-- ✅ 원화 총액 (미국 주식인 경우만 표시) -->
                <div id="summaryKrwTotalRow" class="summary-row" style="display: none;">
                    <span class="summary-label">
                        <i class="fas fa-won-sign"></i> 원화 총액
                    </span>
                    <span class="summary-value krw-converted" id="summaryKrwTotal">-</span>
                </div>
            </div>
            
            <!-- 매입 버튼 -->
            <button type="submit" id="submitBtn" class="btn-purchase" disabled>
                <i class="fas fa-shopping-cart"></i> 매입하기
            </button>
            
            <!-- 알림 -->
            <div id="alertBox" class="alert-box"></div>
        </form>
    </div>
    
    <script>
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * PurchaseManager - 주식 매입 관리자 (환율 정보 추가!)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    var PurchaseManager = {
        contextPath: '${pageContext.request.contextPath}',
        stockCode: null,
        stockName: null,
        currentPrice: 0,
        country: null,
        exchangeRate: 0,  // ✅ 환율 추가
        
        /**
         * 초기화
         */
        init: function() {
            console.log('💰 PurchaseManager 초기화');
            console.log('  contextPath:', this.contextPath);
            
            // 이미 선택된 주식이 있으면 표시
            const stockSelect = document.getElementById('stockSelect');
            if (stockSelect.value) {
                const option = stockSelect.options[stockSelect.selectedIndex];
                this.updateStockInfo(option);
            }
            
            // 이벤트 리스너 등록
            this.attachEventListeners();
        },
        
        /**
         * 이벤트 리스너 등록
         */
        attachEventListeners: function() {
            // 주식 선택 변경
            document.getElementById('stockSelect').addEventListener('change', (e) => {
                const option = e.target.options[e.target.selectedIndex];
                
                if (option.value) {
                    this.updateStockInfo(option);
                    document.getElementById('submitBtn').disabled = false;
                } else {
                    document.getElementById('stockInfoCard').style.display = 'none';
                    document.getElementById('exchangeRateCard').style.display = 'none';
                    document.getElementById('submitBtn').disabled = true;
                }
            });
            
            // 수량 입력 변경
            document.getElementById('quantityInput').addEventListener('input', () => {
                this.calculateSummary();
            });
            
            // 4분할 버튼 이벤트 (동적으로 생성되므로 이벤트 위임 사용)
            document.getElementById('fractionButtons').addEventListener('click', (e) => {
                if (e.target.classList.contains('fraction-btn') || e.target.closest('.fraction-btn')) {
                    const btn = e.target.classList.contains('fraction-btn') ? 
                                 e.target : e.target.closest('.fraction-btn');
                    const value = btn.getAttribute('data-value');
                    
                    document.getElementById('quantityInput').value = value;
                    
                    // 모든 버튼 비활성화
                    document.querySelectorAll('.fraction-btn').forEach(b => {
                        b.classList.remove('active');
                    });
                    // 클릭된 버튼 활성화
                    btn.classList.add('active');
                    
                    this.calculateSummary();
                }
            });
            
            // 폼 제출
            document.getElementById('purchaseForm').addEventListener('submit', (e) => {
                e.preventDefault();
                this.executePurchase();
            });
        },
        
        /**
         * ✅ 환율 정보 가져오기 (신규 추가!)
         */
        loadExchangeRate: function() {
            console.log('💱 환율 조회 시작...');
            
            const spinner = document.getElementById('exchangeRateSpinner');
            const rateDisplay = document.getElementById('displayExchangeRate');
            
            fetch(this.contextPath + '/api/exchange/rate')
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        this.exchangeRate = parseFloat(data.rate);
                        
                        rateDisplay.textContent = this.formatNumber(this.exchangeRate) + ' 원/달러';
                        spinner.style.display = 'none';
                        
                        console.log('✅ 환율 로드 완료:', this.exchangeRate);
                        
                        // 계산 다시 실행
                        this.calculateSummary();
                    } else {
                        rateDisplay.textContent = '환율 정보 없음';
                        console.warn('⚠️ 환율 조회 실패');
                    }
                })
                .catch(error => {
                    console.error('❌ 환율 조회 에러:', error);
                    rateDisplay.textContent = '환율 조회 실패';
                    spinner.style.display = 'none';
                });
        },
        
        /**
         * 주식 정보 업데이트
         */
        updateStockInfo: function(option) {
            this.stockCode = option.value;
            this.stockName = option.getAttribute('data-name');
            this.currentPrice = parseFloat(option.getAttribute('data-price'));
            this.country = option.getAttribute('data-country');
            
            console.log('📊 주식 선택:', this.stockName, this.stockCode, this.currentPrice);
            
            // 숨겨진 필드 업데이트
            document.getElementById('selectedStockCode').value = this.stockCode;
            document.getElementById('selectedCountry').value = this.country;
            document.getElementById('selectedPrice').value = this.currentPrice;
            
            // 주식 정보 카드 표시
            document.getElementById('displayStockName').textContent = this.stockName;
            document.getElementById('displayStockCode').innerHTML = 
                this.stockCode + ' <span class="price-badge" id="displayCountryBadge">' +
                (this.country === 'KR' ? '🇰🇷 한국 주식' : '🇺🇸 미국 주식') +
                '</span>';
            
            // 현재가 표시
            const priceStr = this.country === 'KR' ? 
                this.formatNumber(this.currentPrice) + '원' :
                '$' + this.currentPrice.toFixed(2);
            document.getElementById('displayCurrentPrice').textContent = priceStr;
            document.getElementById('priceDisplay').value = this.currentPrice.toFixed(2);
            document.getElementById('currencySymbol').textContent = 
                this.country === 'KR' ? '원' : 'USD';
            
            // 주식 정보 카드 표시
            document.getElementById('stockInfoCard').style.display = 'block';
            
            // ✅ 미국 주식인 경우 환율 정보 표시
            if (this.country === 'US') {
                document.getElementById('exchangeRateCard').style.display = 'block';
                this.loadExchangeRate();
            } else {
                document.getElementById('exchangeRateCard').style.display = 'none';
            }
            
            // 4분할 버튼 표시 (미국 주식인 경우)
            const fractionButtons = document.getElementById('fractionButtons');
            const quantityInput = document.getElementById('quantityInput');
            
            if (this.country === 'US') {
                fractionButtons.style.display = 'grid';
                quantityInput.step = '0.001';
                quantityInput.min = '0.001';
            } else {
                fractionButtons.style.display = 'none';
                quantityInput.step = '1';
                quantityInput.min = '1';
                quantityInput.value = '1';
            }
            
            // 매입 버튼 활성화
            document.getElementById('submitBtn').disabled = false;
            
            // 계산 실행
            this.calculateSummary();
        },
        
        /**
         * 실시간 계산
         */
        calculateSummary: function() {
            if (!this.stockCode) return;
            
            const quantityStr = document.getElementById('quantityInput').value;
            const quantity = parseFloat(quantityStr) || 0;
            
            console.log('💰 실시간 계산:', quantity, '주 ×', this.currentPrice);
            
            // 1. 수량 표시
            const quantityDisplay = this.country === 'US' ? 
                quantity.toFixed(3) + ' 주' : 
                Math.floor(quantity) + ' 주';
            document.getElementById('summaryQuantity').textContent = quantityDisplay;
            
            // 2. 단가 표시
            const priceStr = this.country === 'KR' ? 
                this.formatNumber(this.currentPrice) + '원' : 
                '$' + this.currentPrice.toFixed(2);
            document.getElementById('summaryPrice').textContent = priceStr;
            
            // 3. 총 금액 계산
            const totalAmount = quantity * this.currentPrice;
            
            // 4. 수수료 계산 (0.1%)
            const commission = totalAmount * 0.001;
            
            // 5. 최종 금액
            const finalAmount = totalAmount + commission;
            
            // 6. ✅ 미국 주식인 경우 원화 환산 금액 표시
            if (this.country === 'US' && this.exchangeRate > 0) {
                // 원화 환산 금액 계산
                const krwAmount = totalAmount * this.exchangeRate;
                const krwTotal = finalAmount * this.exchangeRate;
                
                // 원화 환산 금액 표시
                document.getElementById('summaryKrwRow').style.display = 'flex';
                document.getElementById('summaryKrwTotalRow').style.display = 'flex';
                document.getElementById('summaryKrwAmount').textContent = 
                    this.formatNumber(krwAmount) + '원';
                document.getElementById('summaryKrwTotal').textContent = 
                    this.formatNumber(krwTotal) + '원';
            } else {
                // 한국 주식인 경우 원화 환산 금액 숨김
                document.getElementById('summaryKrwRow').style.display = 'none';
                document.getElementById('summaryKrwTotalRow').style.display = 'none';
            }
            
            // 표시
            const commissionStr = this.country === 'KR' ? 
                this.formatNumber(commission) + '원' : 
                '$' + commission.toFixed(2);
            document.getElementById('summaryCommission').textContent = commissionStr;
            
            const finalStr = this.country === 'KR' ? 
                this.formatNumber(finalAmount) + '원' : 
                '$' + finalAmount.toFixed(2);
            document.getElementById('summaryTotal').textContent = finalStr;
        },
        
        /**
         * 매입 실행
         */
        executePurchase: function() {
            if (!this.stockCode) {
                this.showAlert('danger', '종목을 먼저 선택하세요.');
                return;
            }
            
            const quantity = parseFloat(document.getElementById('quantityInput').value);
            
            if (!quantity || quantity <= 0) {
                this.showAlert('danger', '유효한 수량을 입력하세요.');
                return;
            }
            
            // 한국 주식: 정수만
            if (this.country === 'KR' && quantity !== Math.floor(quantity)) {
                this.showAlert('danger', '한국 주식은 정수 수량만 가능합니다.');
                return;
            }
            
            console.log('💳 매입 실행:', this.stockCode, quantity, '주');
            
            // 로딩 표시
            const submitBtn = document.getElementById('submitBtn');
            const originalHtml = submitBtn.innerHTML;
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 매입 중...';
            
            // API 호출
            fetch(this.contextPath + '/api/purchase/execute', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    stockCode: this.stockCode,
                    quantity: quantity,
                    price: this.currentPrice
                })
            })
            .then(response => response.json())
            .then(data => {
                console.log('✅ 매입 성공:', data);
                
                this.showAlert('success', '매입이 완료되었습니다!');
                
                setTimeout(() => {
                    window.location.href = this.contextPath + '/dashboard';
                }, 1500);
            })
            .catch(error => {
                console.error('❌ 매입 실패:', error);
                
                this.showAlert('danger', '매입에 실패했습니다: ' + error.message);
                
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalHtml;
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
            const alertBox = document.getElementById('alertBox');
            alertBox.className = 'alert alert-' + type + ' alert-box';
            alertBox.innerHTML = '<i class="fas fa-' + 
                (type === 'success' ? 'check-circle' : 
                 type === 'danger' ? 'exclamation-triangle' : 
                 'info-circle') + 
                '"></i> ' + message;
            alertBox.style.display = 'block';
            
            if (type !== 'danger') {
                setTimeout(() => {
                    alertBox.style.display = 'none';
                }, 3000);
            }
        }
    };
    
    // 페이지 로드 시 초기화
    document.addEventListener('DOMContentLoaded', function() {
        PurchaseManager.init();
    });
    </script>
</body>
</html>
