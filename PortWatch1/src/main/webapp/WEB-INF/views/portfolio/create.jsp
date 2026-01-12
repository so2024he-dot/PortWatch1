<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>포트폴리오 등록 - PortWatch</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8f9fa;
        }
        
        .form-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            padding: 30px;
            margin-top: 30px;
        }
        
        .stock-info-card {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 20px;
        }
        
        .price-badge {
            background: #0d6efd;
            color: white;
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 16px;
            font-weight: bold;
        }
        
        .btn-custom {
            border-radius: 20px;
            padding: 10px 30px;
        }
        
        .required {
            color: red;
        }
    </style>
</head>
<body>
    <!-- Header 포함 -->
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="form-container">
                    <h2 class="mb-4">
                        <i class="fas fa-plus-circle"></i> 포트폴리오 등록
                    </h2>
                    
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-triangle"></i> ${error}
                        </div>
                    </c:if>
                    
                    <form:form modelAttribute="portfolioVO" 
                               action="${pageContext.request.contextPath}/portfolio/create" 
                               method="post">
                        
                        <!-- 종목 선택 -->
                        <div class="mb-4">
                            <label class="form-label">
                                <i class="fas fa-chart-line"></i> 종목 선택 <span class="required">*</span>
                            </label>
                            <form:select path="stockId" class="form-select" id="stockSelect" onchange="updateStockInfo()">
                                <form:option value="" label="-- 종목을 선택하세요 --" />
                                <c:if test="${not empty stockList}">
                                    <c:forEach var="stock" items="${stockList}">
                                        <form:option value="${stock.stockId}" 
                                                    data-code="${stock.stockCode}"
                                                    data-name="${stock.stockName}"
                                                    data-price="${stock.currentPrice}"
                                                    data-country="${stock.country}">
                                            [${stock.country}] ${stock.stockName} (${stock.stockCode})
                                        </form:option>
                                    </c:forEach>
                                </c:if>
                            </form:select>
                            <form:errors path="stockId" cssClass="text-danger" />
                        </div>
                        
                        <!-- 선택된 종목 정보 표시 -->
                        <div id="stockInfoCard" class="stock-info-card" style="display:none;">
                            <div class="row align-items-center">
                                <div class="col-md-6">
                                    <h5 id="selectedStockName">-</h5>
                                    <p class="text-muted mb-0">
                                        <span id="selectedStockCode">-</span>
                                        <span id="selectedStockCountry" class="ms-2 badge bg-primary">-</span>
                                    </p>
                                </div>
                                <div class="col-md-6 text-end">
                                    <div class="text-muted small">현재가</div>
                                    <div class="price-badge" id="selectedStockPrice">-</div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- 수량 입력 -->
                        <div class="mb-4">
                            <label class="form-label">
                                <i class="fas fa-sort-numeric-up"></i> 수량 <span class="required">*</span>
                            </label>
                            <form:input path="quantity" type="number" class="form-control" 
                                       min="1" step="1" placeholder="구매 수량을 입력하세요"
                                       oninput="calculateTotal()" />
                            <form:errors path="quantity" cssClass="text-danger" />
                        </div>
                        
                        <!-- 매수가 입력 -->
                        <div class="mb-4">
                            <label class="form-label">
                                <i class="fas fa-dollar-sign"></i> 매수가 <span class="required">*</span>
                                <span class="text-muted small" id="priceHint"></span>
                            </label>
                            <form:input path="purchasePrice" type="number" class="form-control" 
                                       min="0.01" step="0.01" placeholder="매수 가격을 입력하세요"
                                       oninput="calculateTotal()" />
                            <form:errors path="purchasePrice" cssClass="text-danger" />
                            <div class="form-text" id="priceGuide"></div>
                        </div>
                        
                        <!-- 총 투자금액 표시 -->
                        <div class="mb-4">
                            <div class="stock-info-card">
                                <div class="row">
                                    <div class="col-6">
                                        <strong>총 투자금액:</strong>
                                    </div>
                                    <div class="col-6 text-end">
                                        <strong id="totalAmount" class="text-primary">0 원</strong>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- 매수일 -->
                        <div class="mb-4">
                            <label class="form-label">
                                <i class="fas fa-calendar"></i> 매수일
                            </label>
                            <form:input path="purchaseDate" type="date" class="form-control" />
                            <form:errors path="purchaseDate" cssClass="text-danger" />
                        </div>
                        
                        <!-- 버튼 -->
                        <div class="text-center mt-4">
                            <button type="submit" class="btn btn-primary btn-custom me-2">
                                <i class="fas fa-save"></i> 등록
                            </button>
                            <a href="${pageContext.request.contextPath}/portfolio/list" class="btn btn-secondary btn-custom">
                                <i class="fas fa-times"></i> 취소
                            </a>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // 종목 정보 업데이트
        function updateStockInfo() {
            const select = document.getElementById('stockSelect');
            const selectedOption = select.options[select.selectedIndex];
            
            if (selectedOption.value) {
                const stockName = selectedOption.getAttribute('data-name');
                const stockCode = selectedOption.getAttribute('data-code');
                const stockPrice = selectedOption.getAttribute('data-price');
                const country = selectedOption.getAttribute('data-country');
                
                // 정보 표시
                document.getElementById('selectedStockName').textContent = stockName;
                document.getElementById('selectedStockCode').textContent = stockCode;
                document.getElementById('selectedStockCountry').textContent = country === 'KR' ? '한국' : '미국';
                
                // 가격 표시 (한국/미국 구분)
                if (country === 'KR') {
                    document.getElementById('selectedStockPrice').textContent = 
                        new Intl.NumberFormat('ko-KR').format(stockPrice) + ' 원';
                    document.getElementById('priceHint').textContent = '(1주당 가격)';
                    document.getElementById('priceGuide').innerHTML = 
                        '<i class="fas fa-info-circle"></i> 한국 주식은 1주 단위로 거래됩니다.';
                } else {
                    document.getElementById('selectedStockPrice').textContent = 
                        '$' + new Intl.NumberFormat('en-US', {minimumFractionDigits: 2}).format(stockPrice);
                    document.getElementById('priceHint').textContent = '(달러 단위)';
                    document.getElementById('priceGuide').innerHTML = 
                        '<i class="fas fa-info-circle"></i> 미국 주식은 소수점 단위로 거래 가능합니다.';
                }
                
                // 매수가 input에 현재가 미리 채우기
                document.querySelector('input[name="purchasePrice"]').value = stockPrice;
                
                document.getElementById('stockInfoCard').style.display = 'block';
                
                // 총액 계산
                calculateTotal();
            } else {
                document.getElementById('stockInfoCard').style.display = 'none';
                document.getElementById('priceHint').textContent = '';
                document.getElementById('priceGuide').innerHTML = '';
            }
        }
        
        // 총 투자금액 계산
        function calculateTotal() {
            const quantity = parseFloat(document.querySelector('input[name="quantity"]').value) || 0;
            const price = parseFloat(document.querySelector('input[name="purchasePrice"]').value) || 0;
            const total = quantity * price;
            
            const select = document.getElementById('stockSelect');
            const selectedOption = select.options[select.selectedIndex];
            const country = selectedOption.getAttribute('data-country');
            
            if (total > 0) {
                if (country === 'KR') {
                    document.getElementById('totalAmount').textContent = 
                        new Intl.NumberFormat('ko-KR').format(total) + ' 원';
                } else {
                    document.getElementById('totalAmount').textContent = 
                        '$' + new Intl.NumberFormat('en-US', {minimumFractionDigits: 2}).format(total);
                }
            } else {
                document.getElementById('totalAmount').textContent = '0 원';
            }
        }
        
        // 페이지 로드 시 종목이 미리 선택된 경우 (매수 버튼으로 온 경우)
        window.onload = function() {
            const select = document.getElementById('stockSelect');
            if (select.value) {
                updateStockInfo();
            }
            
            // 오늘 날짜를 기본값으로 설정
            const today = new Date().toISOString().split('T')[0];
            const dateInput = document.querySelector('input[name="purchaseDate"]');
            if (!dateInput.value) {
                dateInput.value = today;
            }
        };
    </script>
</body>
</html>
