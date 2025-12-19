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
            max-width: 600px;
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
            background: #f9fafb;
            border-radius: 12px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
        }
        
        .stock-name {
            font-size: 1.5rem;
            font-weight: 700;
            color: #1f2937;
        }
        
        .stock-code {
            color: #6b7280;
            font-size: 1rem;
        }
        
        .current-price {
            font-size: 2rem;
            font-weight: 700;
            color: #667eea;
            margin-top: 1rem;
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
            font-size: 1rem;
            transition: all 0.3s;
        }
        
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        .validation-result {
            padding: 1rem;
            border-radius: 10px;
            margin-bottom: 1.5rem;
            display: none;
        }
        
        .validation-result.success {
            background: #d1fae5;
            border: 2px solid #10b981;
            color: #065f46;
        }
        
        .validation-result.error {
            background: #fee2e2;
            border: 2px solid #ef4444;
            color: #991b1b;
        }
        
        .validation-result.warning {
            background: #fef3c7;
            border: 2px solid #f59e0b;
            color: #92400e;
        }
        
        .summary-box {
            background: #f9fafb;
            border-radius: 12px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
        }
        
        .summary-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.5rem 0;
            border-bottom: 1px solid #e5e7eb;
        }
        
        .summary-row:last-child {
            border-bottom: none;
            font-weight: 700;
            font-size: 1.2rem;
            color: #667eea;
        }
        
        .btn-purchase {
            width: 100%;
            padding: 1rem;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 12px;
            font-size: 1.1rem;
            font-weight: 700;
            transition: all 0.3s;
        }
        
        .btn-purchase:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
        }
        
        .btn-purchase:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }
        
        .btn-validate {
            width: 100%;
            padding: 0.75rem;
            background: white;
            color: #667eea;
            border: 2px solid #667eea;
            border-radius: 10px;
            font-weight: 600;
            margin-bottom: 1rem;
            transition: all 0.3s;
        }
        
        .btn-validate:hover {
            background: #667eea;
            color: white;
        }
        
        .spinner-border-sm {
            width: 1rem;
            height: 1rem;
            border-width: 0.2rem;
        }
    </style>
</head>
<body>
    
    <div class="purchase-container">
        
        <!-- 헤더 -->
        <div class="purchase-header">
            <h2><i class="fas fa-shopping-cart"></i> 주식 매입</h2>
            <p class="text-muted">종목을 선택하고 수량과 가격을 입력하세요</p>
        </div>
        
        <!-- 종목 정보 -->
        <div class="stock-info-card">
            <div class="stock-name" id="stockName">${stock.stockName}</div>
            <div class="stock-code" id="stockCode">${stock.stockCode}</div>
            <div class="current-price" id="currentPrice">
                <fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0" />원
            </div>
            <c:if test="${stock.country == 'US'}">
                <div class="text-muted mt-2">
                    <i class="fas fa-flag-usa"></i> 미국 주식 (소수점 매입 가능)
                </div>
            </c:if>
        </div>
        
        <!-- 매입 폼 -->
        <form id="purchaseForm">
            
            <input type="hidden" id="portfolioId" name="portfolioId" value="${portfolioId}">
            <input type="hidden" id="stockCodeInput" name="stockCode" value="${stock.stockCode}">
            
            <!-- 수량 입력 -->
            <div class="form-group">
                <label class="form-label" for="quantity">
                    <i class="fas fa-layer-group"></i> 매입 수량
                </label>
                <input type="number" 
                       class="form-control" 
                       id="quantity" 
                       name="quantity"
                       placeholder="수량을 입력하세요"
                       step="${stock.country == 'US' ? '0.001' : '1'}"
                       min="0"
                       required>
                <small class="text-muted">
                    <c:choose>
                        <c:when test="${stock.country == 'US'}">
                            미국 주식: 소수점 3자리까지 입력 가능
                        </c:when>
                        <c:otherwise>
                            한국 주식: 정수만 입력 가능
                        </c:otherwise>
                    </c:choose>
                </small>
            </div>
            
            <!-- 가격 입력 -->
            <div class="form-group">
                <label class="form-label" for="price">
                    <i class="fas fa-won-sign"></i> 매입 가격
                </label>
                <input type="number" 
                       class="form-control" 
                       id="price" 
                       name="price"
                       placeholder="가격을 입력하세요"
                       value="${stock.currentPrice}"
                       step="0.01"
                       min="0"
                       required>
                <small class="text-muted">
                    현재가 기준 ±10% 범위 내에서 입력
                </small>
            </div>
            
            <!-- 검증 버튼 -->
            <button type="button" class="btn-validate" onclick="validatePurchase()">
                <i class="fas fa-check-circle"></i> 매입 가능 여부 확인
            </button>
            
            <!-- 검증 결과 -->
            <div id="validationResult" class="validation-result"></div>
            
            <!-- 매입 요약 -->
            <div id="summaryBox" class="summary-box" style="display: none;">
                <h6 class="mb-3"><i class="fas fa-calculator"></i> 매입 요약</h6>
                <div class="summary-row">
                    <span>매입 금액</span>
                    <span id="totalAmount">0원</span>
                </div>
                <div class="summary-row">
                    <span>수수료</span>
                    <span id="commission">0원</span>
                </div>
                <div class="summary-row">
                    <span>총 필요 금액</span>
                    <span id="requiredAmount">0원</span>
                </div>
            </div>
            
            <!-- 매입 버튼 -->
            <button type="submit" class="btn-purchase" id="purchaseBtn" disabled>
                <i class="fas fa-shopping-cart"></i> 매입하기
            </button>
            
        </form>
        
        <!-- 취소 버튼 -->
        <button type="button" class="btn btn-outline-secondary mt-3 w-100" onclick="window.history.back()">
            <i class="fas fa-times"></i> 취소
        </button>
        
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    
    <script>
        let validationPassed = false;
        
        /**
         * 매입 가능 여부 검증
         */
        function validatePurchase() {
            const stockCode = $('#stockCodeInput').val();
            const quantity = parseFloat($('#quantity').val());
            const price = parseFloat($('#price').val());
            
            if (!quantity || !price) {
                showValidationResult('error', '수량과 가격을 모두 입력해주세요.');
                return;
            }
            
            // 로딩 표시
            $('.btn-validate').html('<span class="spinner-border spinner-border-sm"></span> 검증 중...');
            $('.btn-validate').prop('disabled', true);
            
            $.ajax({
                url: '${pageContext.request.contextPath}/api/purchase/validate',
                type: 'POST',
                data: {
                    stockCode: stockCode,
                    quantity: quantity,
                    price: price
                },
                success: function(response) {
                    if (response.valid) {
                        // 검증 성공
                        showValidationResult('success', '✅ ' + response.message);
                        
                        // 요약 정보 표시
                        updateSummary(response);
                        
                        // 매입 버튼 활성화
                        $('#purchaseBtn').prop('disabled', false);
                        validationPassed = true;
                        
                        // 경고 메시지 (시장 시간 등)
                        if (response.marketTimeWarning) {
                            showValidationResult('warning', '⚠️ ' + response.marketTimeWarning, true);
                        }
                    } else {
                        // 검증 실패
                        showValidationResult('error', '❌ ' + response.message);
                        $('#purchaseBtn').prop('disabled', true);
                        $('#summaryBox').hide();
                        validationPassed = false;
                    }
                },
                error: function(xhr, status, error) {
                    showValidationResult('error', '검증 중 오류가 발생했습니다.');
                    console.error('Validation error:', error);
                },
                complete: function() {
                    // 버튼 복원
                    $('.btn-validate').html('<i class="fas fa-check-circle"></i> 매입 가능 여부 확인');
                    $('.btn-validate').prop('disabled', false);
                }
            });
        }
        
        /**
         * 검증 결과 표시
         */
        function showValidationResult(type, message, append = false) {
            const $result = $('#validationResult');
            
            if (append) {
                // 기존 메시지에 추가
                $result.append('<div class="mt-2">' + message + '</div>');
            } else {
                // 새 메시지로 교체
                $result.removeClass('success error warning');
                $result.addClass(type);
                $result.html(message);
            }
            
            $result.show();
        }
        
        /**
         * 매입 요약 업데이트
         */
        function updateSummary(response) {
            const totalAmount = response.totalAmount || 0;
            const commission = response.commission || 0;
            const requiredAmount = totalAmount + commission;
            
            $('#totalAmount').text(formatNumber(totalAmount) + '원');
            $('#commission').text(formatNumber(commission) + '원');
            $('#requiredAmount').text(formatNumber(requiredAmount) + '원');
            
            $('#summaryBox').show();
        }
        
        /**
         * 숫자 포맷팅
         */
        function formatNumber(num) {
            return Math.round(num).toLocaleString('ko-KR');
        }
        
        /**
         * 매입 실행
         */
        $('#purchaseForm').on('submit', function(e) {
            e.preventDefault();
            
            if (!validationPassed) {
                alert('먼저 매입 가능 여부를 확인해주세요.');
                return;
            }
            
            if (!confirm('정말로 매입하시겠습니까?')) {
                return;
            }
            
            const formData = {
                portfolioId: $('#portfolioId').val(),
                stockCode: $('#stockCodeInput').val(),
                quantity: parseFloat($('#quantity').val()),
                price: parseFloat($('#price').val())
            };
            
            // 로딩 표시
            $('#purchaseBtn').html('<span class="spinner-border spinner-border-sm"></span> 매입 중...');
            $('#purchaseBtn').prop('disabled', true);
            
            $.ajax({
                url: '${pageContext.request.contextPath}/api/purchase/execute',
                type: 'POST',
                data: formData,
                success: function(response) {
                    if (response.success) {
                        alert('매입이 완료되었습니다!');
                        window.location.href = '${pageContext.request.contextPath}/portfolio/detail/' + formData.portfolioId;
                    } else {
                        alert('매입 실패: ' + response.message);
                        $('#purchaseBtn').html('<i class="fas fa-shopping-cart"></i> 매입하기');
                        $('#purchaseBtn').prop('disabled', false);
                    }
                },
                error: function(xhr, status, error) {
                    alert('매입 중 오류가 발생했습니다.');
                    console.error('Purchase error:', error);
                    $('#purchaseBtn').html('<i class="fas fa-shopping-cart"></i> 매입하기');
                    $('#purchaseBtn').prop('disabled', false);
                }
            });
        });
        
        /**
         * 입력값 변경 시 검증 상태 초기화
         */
        $('#quantity, #price').on('input', function() {
            validationPassed = false;
            $('#purchaseBtn').prop('disabled', true);
            $('#validationResult').hide();
            $('#summaryBox').hide();
        });
    </script>
    
</body>
</html>
