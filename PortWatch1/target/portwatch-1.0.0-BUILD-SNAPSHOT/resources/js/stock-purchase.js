/**
 * ✅ 주식 구매 JavaScript
 * 
 * 기능:
 * 1. 구매 모달 UI
 * 2. 실시간 검증
 * 3. 구매 실행
 * 4. 결과 피드백
 * 
 * @author PortWatch
 * @version 1.0
 */

// ========================================
// 전역 변수
// ========================================
let currentStock = null;
let validationResult = null;

// ========================================
// 1. 구매 모달 열기
// ========================================
function openPurchaseModal(stockCode) {
    console.log(`💰 구매 모달 열기: ${stockCode}`);
    
    // 종목 정보 로드
    loadStockInfo(stockCode);
    
    // 모달 표시
    $('#purchaseModal').modal('show');
    
    // 입력 필드 초기화
    resetPurchaseForm();
}

// ========================================
// 2. 종목 정보 로드
// ========================================
function loadStockInfo(stockCode) {
    $.ajax({
        url: `/api/stocks/${stockCode}`,
        method: 'GET',
        dataType: 'json',
        success: function(stock) {
            currentStock = stock;
            
            // 종목 정보 표시
            $('#purchase-stock-code').text(stock.stockCode);
            $('#purchase-stock-name').text(stock.stockName);
            $('#purchase-current-price').text(formatPrice(stock.currentPrice, stock.country));
            $('#purchase-market').text(stock.marketType);
            
            // 가격 입력 필드에 현재가 자동 입력
            $('#purchase-price').val(stock.currentPrice);
            
            // 실시간 검증 시작
            setupRealTimeValidation();
        },
        error: function(xhr, status, error) {
            console.error('❌ 종목 정보 로드 실패:', error);
            alert('종목 정보를 불러올 수 없습니다.');
            $('#purchaseModal').modal('hide');
        }
    });
}

// ========================================
// 3. 실시간 검증 설정
// ========================================
function setupRealTimeValidation() {
    // 수량 또는 가격 변경 시 자동 검증
    $('#purchase-quantity, #purchase-price').on('input', function() {
        debounce(performQuickValidation, 500)();
    });
}

// ========================================
// 4. 빠른 검증 (입력 중)
// ========================================
function performQuickValidation() {
    if (!currentStock) return;
    
    const quantity = parseFloat($('#purchase-quantity').val());
    const price = parseFloat($('#purchase-price').val());
    
    if (!quantity || !price || quantity <= 0 || price <= 0) {
        clearValidationMessage();
        return;
    }
    
    // 총 금액 계산 및 표시
    updateTotalAmount(quantity, price);
    
    // 서버 검증 (간단 체크)
    $.ajax({
        url: `/api/purchase/quick-check?stockCode=${currentStock.stockCode}&quantity=${quantity}&price=${price}`,
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            if (response.success && response.validation.valid) {
                showValidationMessage('success', '✅ 구매 가능합니다.');
                $('#btn-execute-purchase').prop('disabled', false);
            } else {
                showValidationMessage('error', '❌ ' + response.validation.message);
                $('#btn-execute-purchase').prop('disabled', true);
            }
        },
        error: function() {
            showValidationMessage('warning', '⚠️ 검증 중 오류가 발생했습니다.');
            $('#btn-execute-purchase').prop('disabled', true);
        }
    });
}

// ========================================
// 5. 최종 검증 (구매 전)
// ========================================
function validatePurchase() {
    return new Promise((resolve, reject) => {
        if (!currentStock) {
            reject('종목 정보가 없습니다.');
            return;
        }
        
        const quantity = parseFloat($('#purchase-quantity').val());
        const price = parseFloat($('#purchase-price').val());
        
        if (!quantity || !price || quantity <= 0 || price <= 0) {
            reject('수량과 가격을 올바르게 입력해주세요.');
            return;
        }
        
        // 로딩 표시
        showLoadingInModal();
        
        $.ajax({
            url: '/api/purchase/validate',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                stockCode: currentStock.stockCode,
                quantity: quantity,
                price: price
            }),
            success: function(response) {
                hideLoadingInModal();
                
                if (response.success) {
                    validationResult = response.validation;
                    resolve(response);
                } else {
                    reject(response.message);
                }
            },
            error: function(xhr, status, error) {
                hideLoadingInModal();
                reject('검증 중 오류가 발생했습니다: ' + error);
            }
        });
    });
}

// ========================================
// 6. 구매 실행
// ========================================
function executePurchase() {
    console.log('💳 구매 실행 시작...');
    
    // 1. 최종 검증
    validatePurchase()
        .then(function(validationResponse) {
            console.log('✅ 최종 검증 통과');
            
            // 2. 확인 대화상자
            const quantity = parseFloat($('#purchase-quantity').val());
            const price = parseFloat($('#purchase-price').val());
            const totalAmount = quantity * price;
            
            const confirmMessage = `
                ${currentStock.stockName} (${currentStock.stockCode})
                수량: ${quantity}
                가격: ${formatPrice(price, currentStock.country)}
                총 금액: ${formatPrice(totalAmount, currentStock.country)}
                
                구매하시겠습니까?
            `;
            
            if (!confirm(confirmMessage)) {
                console.log('❌ 사용자가 구매를 취소했습니다.');
                return;
            }
            
            // 3. 구매 실행
            showLoadingInModal();
            
            $.ajax({
                url: '/api/purchase/execute',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    stockCode: currentStock.stockCode,
                    quantity: quantity,
                    price: price
                }),
                success: function(response) {
                    hideLoadingInModal();
                    
                    if (response.success) {
                        console.log('✅ 구매 완료!');
                        
                        // 성공 메시지
                        showSuccessModal(response.purchase);
                        
                        // 모달 닫기
                        $('#purchaseModal').modal('hide');
                        
                        // 포트폴리오 새로고침
                        if (typeof refreshPortfolio === 'function') {
                            refreshPortfolio();
                        }
                    } else {
                        console.error('❌ 구매 실패:', response.message);
                        alert('구매 실패: ' + response.message);
                    }
                },
                error: function(xhr, status, error) {
                    hideLoadingInModal();
                    console.error('❌ 구매 실행 중 오류:', error);
                    
                    if (xhr.status === 401) {
                        alert('로그인이 필요합니다.');
                        window.location.href = '/login';
                    } else {
                        alert('구매 처리 중 오류가 발생했습니다: ' + error);
                    }
                }
            });
        })
        .catch(function(error) {
            console.error('❌ 검증 실패:', error);
            alert(error);
        });
}

// ========================================
// 7. UI 업데이트 함수
// ========================================
function updateTotalAmount(quantity, price) {
    const totalAmount = quantity * price;
    const country = currentStock ? currentStock.country : 'KR';
    
    $('#purchase-total-amount').text(formatPrice(totalAmount, country));
    
    // 수수료 계산 (간단 버전)
    const commission = country === 'US' ? 0.99 * 1300 : totalAmount * 0.00015;
    $('#purchase-commission').text(formatPrice(commission, country));
    
    // 최종 금액
    const finalAmount = totalAmount + commission;
    $('#purchase-final-amount').text(formatPrice(finalAmount, country));
}

function showValidationMessage(type, message) {
    const container = $('#validation-message');
    
    container.removeClass('success error warning');
    container.addClass(type);
    container.text(message);
    container.show();
}

function clearValidationMessage() {
    $('#validation-message').hide();
}

function showLoadingInModal() {
    $('#btn-execute-purchase').prop('disabled', true);
    $('#purchase-loading').show();
}

function hideLoadingInModal() {
    $('#btn-execute-purchase').prop('disabled', false);
    $('#purchase-loading').hide();
}

// ========================================
// 8. 성공 모달
// ========================================
function showSuccessModal(purchase) {
    const modal = $(`
        <div class="modal fade" id="successModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title">
                            <i class="fas fa-check-circle"></i> 구매 완료
                        </h5>
                        <button type="button" class="close text-white" data-dismiss="modal">
                            <span>&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <div class="purchase-summary">
                            <h4>${purchase.stockName} (${purchase.stockCode})</h4>
                            <table class="table">
                                <tr>
                                    <td>수량</td>
                                    <td class="text-right font-weight-bold">${purchase.quantity}</td>
                                </tr>
                                <tr>
                                    <td>매입가</td>
                                    <td class="text-right">${formatPrice(purchase.price, currentStock.country)}</td>
                                </tr>
                                <tr>
                                    <td>매입 금액</td>
                                    <td class="text-right">${formatPrice(purchase.totalAmount, currentStock.country)}</td>
                                </tr>
                                <tr>
                                    <td>수수료</td>
                                    <td class="text-right">${formatPrice(purchase.commission, currentStock.country)}</td>
                                </tr>
                                <tr class="table-primary">
                                    <td class="font-weight-bold">최종 금액</td>
                                    <td class="text-right font-weight-bold">
                                        ${formatPrice(purchase.finalAmount, currentStock.country)}
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" data-dismiss="modal">
                            확인
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `);
    
    $('body').append(modal);
    $('#successModal').modal('show');
    
    // 모달 닫힐 때 제거
    $('#successModal').on('hidden.bs.modal', function() {
        $(this).remove();
    });
}

// ========================================
// 9. 폼 초기화
// ========================================
function resetPurchaseForm() {
    $('#purchase-quantity').val('');
    $('#purchase-price').val('');
    $('#purchase-total-amount').text('-');
    $('#purchase-commission').text('-');
    $('#purchase-final-amount').text('-');
    clearValidationMessage();
    $('#btn-execute-purchase').prop('disabled', true);
}

// ========================================
// 10. 유틸리티 함수
// ========================================
function formatPrice(price, country) {
    if (!price) return '-';
    
    if (country === 'US') {
        return '$' + Number(price).toFixed(2);
    } else {
        return Number(price).toLocaleString() + '원';
    }
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// ========================================
// 전역 함수 노출
// ========================================
window.openPurchaseModal = openPurchaseModal;
window.executePurchase = executePurchase;
