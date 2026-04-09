<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>결제하기 - PortWatch</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">
    <style>
        .payment-container {
            max-width: 800px;
            margin: 50px auto;
            padding: 30px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .payment-header {
            text-align: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #007bff;
        }
        
        .payment-header h2 {
            color: #007bff;
            margin-bottom: 10px;
        }
        
        .order-info {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
        }
        
        .order-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 15px;
            padding-bottom: 15px;
            border-bottom: 1px solid #dee2e6;
        }
        
        .order-item:last-child {
            border-bottom: none;
        }
        
        .order-label {
            font-weight: bold;
            color: #495057;
        }
        
        .order-value {
            color: #212529;
        }
        
        .total-amount {
            font-size: 1.3em;
            color: #007bff;
            font-weight: bold;
        }
        
        .payment-method {
            margin-bottom: 30px;
        }
        
        .payment-method h3 {
            margin-bottom: 20px;
            color: #343a40;
        }
        
        .method-option {
            display: flex;
            align-items: center;
            padding: 15px;
            border: 2px solid #dee2e6;
            border-radius: 8px;
            margin-bottom: 10px;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .method-option:hover {
            border-color: #007bff;
            background: #f8f9fa;
        }
        
        .method-option input[type="radio"] {
            margin-right: 10px;
        }
        
        .card-form {
            display: none;
            margin-top: 20px;
            padding: 20px;
            background: #f8f9fa;
            border-radius: 8px;
        }
        
        .card-form.active {
            display: block;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            color: #495057;
        }
        
        .form-group input,
        .form-group select {
            width: 100%;
            padding: 12px;
            border: 1px solid #ced4da;
            border-radius: 5px;
            font-size: 1em;
        }
        
        .pay-button {
            width: 100%;
            padding: 15px;
            background: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 1.2em;
            font-weight: bold;
            cursor: pointer;
            transition: background 0.3s;
        }
        
        .pay-button:hover {
            background: #0056b3;
        }
        
        .pay-button:disabled {
            background: #6c757d;
            cursor: not-allowed;
        }
        
        .country-badge {
            display: inline-block;
            padding: 5px 10px;
            background: #28a745;
            color: white;
            border-radius: 5px;
            font-size: 0.9em;
        }
        
        .currency-info {
            font-size: 0.9em;
            color: #6c757d;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <%@ include file="../common/header.jsp" %>
    
    <div class="payment-container">
        <div class="payment-header">
            <h2>💳 주식 구매 결제</h2>
            <p>안전한 결제를 위해 정확한 정보를 입력해주세요</p>
        </div>
        
        <!-- 주문 정보 -->
        <div class="order-info">
            <h3>📋 주문 정보</h3>
            
            <div class="order-item">
                <span class="order-label">종목</span>
                <span class="order-value">${stock.stockName} (${stock.stockCode})</span>
            </div>
            
            <div class="order-item">
                <span class="order-label">시장</span>
                <span class="order-value">
                    ${stock.marketType}
                    <span class="country-badge">${country}</span>
                </span>
            </div>
            
            <div class="order-item">
                <span class="order-label">구매 수량</span>
                <span class="order-value"><fmt:formatNumber value="${quantity}" pattern="#,##0.0000" />주</span>
            </div>
            
            <div class="order-item">
                <span class="order-label">구매 단가</span>
                <span class="order-value"><fmt:formatNumber value="${price}" pattern="#,##0.00" /> ${currency}</span>
            </div>
            
            <div class="order-item">
                <span class="order-label">총 결제 금액</span>
                <span class="order-value total-amount">
                    <fmt:formatNumber value="${totalAmount}" pattern="#,##0.00" /> ${currency}
                </span>
            </div>
            
            <c:if test="${currency != 'KRW'}">
                <div class="currency-info">
                    ※ 원화 환산 금액은 결제 시점의 환율이 적용됩니다.
                </div>
            </c:if>
        </div>
        
        <!-- 결제 수단 선택 -->
        <div class="payment-method">
            <h3>💳 결제 수단</h3>
            
            <div class="method-option" onclick="selectPaymentMethod('CARD')">
                <input type="radio" name="paymentMethod" value="CARD" checked>
                <label>신용카드 / 체크카드</label>
            </div>
            
            <c:if test="${country == 'KR'}">
                <div class="method-option" onclick="selectPaymentMethod('BANK')">
                    <input type="radio" name="paymentMethod" value="BANK">
                    <label>계좌이체</label>
                </div>
                
                <div class="method-option" onclick="selectPaymentMethod('TOSS')">
                    <input type="radio" name="paymentMethod" value="TOSS">
                    <label>토스페이</label>
                </div>
            </c:if>
            
            <c:if test="${country == 'US'}">
                <div class="method-option" onclick="selectPaymentMethod('PAYPAL')">
                    <input type="radio" name="paymentMethod" value="PAYPAL">
                    <label>PayPal</label>
                </div>
            </c:if>
        </div>
        
        <!-- 카드 정보 입력 폼 -->
        <div id="cardForm" class="card-form active">
            <h4>카드 정보</h4>
            
            <div class="form-group">
                <label>카드번호</label>
                <input type="text" id="cardNumber" placeholder="1234-5678-9012-3456" 
                       maxlength="19" required>
            </div>
            
            <div class="form-group">
                <label>카드사</label>
                <select id="cardCompany" required>
                    <option value="">선택하세요</option>
                    <c:if test="${country == 'KR'}">
                        <option value="신한카드">신한카드</option>
                        <option value="삼성카드">삼성카드</option>
                        <option value="KB국민카드">KB국민카드</option>
                        <option value="현대카드">현대카드</option>
                        <option value="롯데카드">롯데카드</option>
                        <option value="우리카드">우리카드</option>
                        <option value="NH농협카드">NH농협카드</option>
                        <option value="하나카드">하나카드</option>
                    </c:if>
                    <c:if test="${country == 'US'}">
                        <option value="VISA">VISA</option>
                        <option value="MasterCard">MasterCard</option>
                        <option value="American Express">American Express</option>
                        <option value="Discover">Discover</option>
                    </c:if>
                </select>
            </div>
            
            <div class="form-group">
                <label>유효기간</label>
                <input type="text" id="cardExpiry" placeholder="MM/YY" maxlength="5" required>
            </div>
            
            <div class="form-group">
                <label>CVC</label>
                <input type="password" id="cardCvc" placeholder="***" maxlength="3" required>
            </div>
        </div>
        
        <!-- 결제 버튼 -->
        <button class="pay-button" onclick="processPayment()">
            <fmt:formatNumber value="${totalAmount}" pattern="#,##0.00" /> ${currency} 결제하기
        </button>
    </div>
    
    <%@ include file="../common/footer.jsp" %>
    
    <script>
        const paymentData = {
            stockId: ${stock.stockId},
            stockCode: '${stock.stockCode}',
            stockName: '${stock.stockName}',
            quantity: ${quantity},
            price: ${price},
            totalAmount: ${totalAmount},
            country: '${country}',
            currency: '${currency}',
            pgProvider: '${pgProvider}'
        };
        
        function selectPaymentMethod(method) {
            document.querySelectorAll('input[name="paymentMethod"]').forEach(radio => {
                radio.checked = (radio.value === method);
            });
            
            // 카드 결제만 카드 정보 입력 폼 표시
            const cardForm = document.getElementById('cardForm');
            if (method === 'CARD') {
                cardForm.classList.add('active');
            } else {
                cardForm.classList.remove('active');
            }
        }
        
        function processPayment() {
            const selectedMethod = document.querySelector('input[name="paymentMethod"]:checked').value;
            
            // 결제 데이터 구성
            const data = {
                ...paymentData,
                paymentMethod: selectedMethod
            };
            
            // 카드 결제인 경우 카드 정보 추가
            if (selectedMethod === 'CARD') {
                const cardNumber = document.getElementById('cardNumber').value;
                const cardCompany = document.getElementById('cardCompany').value;
                const cardExpiry = document.getElementById('cardExpiry').value;
                const cardCvc = document.getElementById('cardCvc').value;
                
                // 유효성 검사
                if (!cardNumber || !cardCompany || !cardExpiry || !cardCvc) {
                    alert('카드 정보를 모두 입력해주세요.');
                    return;
                }
                
                // 카드번호 마스킹 (마지막 4자리만 표시)
                const maskedNumber = cardNumber.substr(0, 4) + '-****-****-' + cardNumber.substr(-4);
                
                data.cardNumber = maskedNumber;
                data.cardCompany = cardCompany;
            }
            
            // 결제 처리
            if (!confirm(`${data.totalAmount.toLocaleString()} ${data.currency}을(를) 결제하시겠습니까?`)) {
                return;
            }
            
            // 버튼 비활성화
            const button = document.querySelector('.pay-button');
            button.disabled = true;
            button.textContent = '결제 처리 중...';
            
            // AJAX 요청
            fetch('${pageContext.request.contextPath}/payment/process', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(response => response.json())
            .then(result => {
                if (result.success) {
                    alert('✅ 결제가 완료되었습니다!\n포트폴리오가 자동으로 생성되었습니다.');
                    window.location.href = '${pageContext.request.contextPath}/portfolio/list';
                } else {
                    alert('❌ 결제 실패: ' + result.message);
                    button.disabled = false;
                    button.textContent = `${data.totalAmount.toLocaleString()} ${data.currency} 결제하기`;
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('❌ 결제 처리 중 오류가 발생했습니다.');
                button.disabled = false;
                button.textContent = `${data.totalAmount.toLocaleString()} ${data.currency} 결제하기`;
            });
        }
        
        // 카드번호 자동 포맷팅
        document.getElementById('cardNumber')?.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\s/g, '');
            let formattedValue = value.match(/.{1,4}/g)?.join('-') || value;
            e.target.value = formattedValue;
        });
        
        // 유효기간 자동 포맷팅
        document.getElementById('cardExpiry')?.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length >= 2) {
                value = value.substr(0, 2) + '/' + value.substr(2, 2);
            }
            e.target.value = value;
        });
    </script>
</body>
</html>
