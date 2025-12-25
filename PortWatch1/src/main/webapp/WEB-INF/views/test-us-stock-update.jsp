<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>🇺🇸 미국 주식 업데이트 테스트</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }
        
        h1 {
            text-align: center;
            color: white;
            margin-bottom: 30px;
            font-size: 2.5em;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
        }
        
        .test-section {
            background: white;
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
        }
        
        .test-section.us {
            border-top: 5px solid #4facfe;
        }
        
        h2 {
            color: #4facfe;
            margin-bottom: 20px;
            border-bottom: 2px solid #4facfe;
            padding-bottom: 10px;
        }
        
        .input-group {
            margin-bottom: 15px;
        }
        
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #333;
        }
        
        input[type="text"], input[type="number"] {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        
        input[type="text"]:focus, input[type="number"]:focus {
            outline: none;
            border-color: #4facfe;
        }
        
        button {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            border: none;
            padding: 12px 30px;
            border-radius: 8px;
            font-size: 16px;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
            margin-right: 10px;
        }
        
        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(79, 172, 254, 0.4);
        }
        
        button:active {
            transform: translateY(0);
        }
        
        .result {
            margin-top: 20px;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 8px;
            border-left: 4px solid #4facfe;
        }
        
        .result h3 {
            color: #4facfe;
            margin-bottom: 10px;
        }
        
        .result pre {
            background: #fff;
            padding: 15px;
            border-radius: 5px;
            overflow-x: auto;
            font-size: 14px;
            border: 1px solid #ddd;
        }
        
        .loading {
            display: none;
            text-align: center;
            padding: 20px;
            color: #4facfe;
            font-weight: bold;
        }
        
        .loading.active {
            display: block;
        }
        
        .spinner {
            border: 4px solid #f3f3f3;
            border-top: 4px solid #4facfe;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            animation: spin 1s linear infinite;
            margin: 0 auto 10px;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        .success {
            color: #28a745;
            font-weight: bold;
        }
        
        .error {
            color: #dc3545;
            font-weight: bold;
        }
        
        .stock-info {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            padding: 20px;
            border-radius: 10px;
            margin-top: 15px;
        }
        
        .stock-info h4 {
            margin-bottom: 10px;
            font-size: 1.3em;
        }
        
        .price-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 15px;
            margin-top: 15px;
        }
        
        .price-item {
            background: rgba(255, 255, 255, 0.2);
            padding: 10px;
            border-radius: 5px;
        }
        
        .price-item strong {
            display: block;
            margin-bottom: 5px;
            font-size: 0.9em;
        }
        
        .price-item span {
            font-size: 1.2em;
            font-weight: bold;
        }
        
        .badge {
            display: inline-block;
            padding: 5px 10px;
            background: #28a745;
            color: white;
            border-radius: 5px;
            font-size: 0.8em;
            margin-left: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🇺🇸 미국 주식 API 테스트 페이지</h1>
        
        <!-- 1. API 상태 체크 -->
        <div class="test-section us">
            <h2>1️⃣ API 상태 체크</h2>
            <button onclick="checkHealth()">📡 API 상태 확인</button>
            
            <div class="loading" id="loading0">
                <div class="spinner"></div>
                확인 중...
            </div>
            
            <div class="result" id="result0" style="display:none;"></div>
        </div>
        
        <!-- 2. 단일 종목 업데이트 -->
        <div class="test-section us">
            <h2>2️⃣ 단일 종목 업데이트</h2>
            <div class="input-group">
                <label for="stockCode1">미국 종목 심볼 입력 <span class="badge">예: AAPL, MSFT</span></label>
                <input type="text" id="stockCode1" placeholder="예: AAPL (Apple)" value="AAPL">
            </div>
            <button onclick="updateSingleStock()">🔄 주가 업데이트</button>
            <button onclick="getLatestPrice()">📊 최신 주가 조회</button>
            
            <div class="loading" id="loading1">
                <div class="spinner"></div>
                처리 중...
            </div>
            
            <div class="result" id="result1" style="display:none;"></div>
        </div>
        
        <!-- 3. 여러 종목 업데이트 -->
        <div class="test-section us">
            <h2>3️⃣ 여러 종목 일괄 업데이트</h2>
            <div class="input-group">
                <label for="stockCodes">종목 심볼 입력 (쉼표로 구분) <span class="badge">최대 25개</span></label>
                <input type="text" id="stockCodes" placeholder="예: AAPL,MSFT,GOOGL" value="AAPL,MSFT,GOOGL">
            </div>
            <button onclick="updateMultipleStocks()">🔄 일괄 업데이트</button>
            
            <div class="loading" id="loading2">
                <div class="spinner"></div>
                처리 중... (시간이 걸릴 수 있습니다)
            </div>
            
            <div class="result" id="result2" style="display:none;"></div>
        </div>
        
        <!-- 4. 주가 히스토리 조회 -->
        <div class="test-section us">
            <h2>4️⃣ 주가 히스토리 조회</h2>
            <div class="input-group">
                <label for="stockCode3">종목 심볼 입력:</label>
                <input type="text" id="stockCode3" placeholder="예: AAPL" value="AAPL">
            </div>
            <div class="input-group">
                <label for="days">조회 일수:</label>
                <input type="number" id="days" placeholder="30" value="30" min="1" max="365">
            </div>
            <button onclick="getStockHistory()">📈 히스토리 조회</button>
            
            <div class="loading" id="loading3">
                <div class="spinner"></div>
                조회 중...
            </div>
            
            <div class="result" id="result3" style="display:none;"></div>
        </div>
        
        <!-- 5. 실시간 크롤링 -->
        <div class="test-section us">
            <h2>5️⃣ 실시간 크롤링 (DB 저장 없음)</h2>
            <div class="input-group">
                <label for="stockCode4">종목 심볼 입력:</label>
                <input type="text" id="stockCode4" placeholder="예: AAPL" value="AAPL">
            </div>
            <button onclick="crawlStockPrice()">🕷️ 실시간 크롤링</button>
            
            <div class="loading" id="loading4">
                <div class="spinner"></div>
                크롤링 중...
            </div>
            
            <div class="result" id="result4" style="display:none;"></div>
        </div>
        
        <!-- 6. 전체 업데이트 -->
        <div class="test-section us">
            <h2>6️⃣ 전체 미국 주식 업데이트</h2>
            <p style="color: #dc3545; margin-bottom: 15px;">
                ⚠️ 주의: 무료 API는 하루 25개 종목만 업데이트 가능합니다!
            </p>
            <button onclick="updateAllUSStocks()">🌐 전체 업데이트</button>
            
            <div class="loading" id="loading5">
                <div class="spinner"></div>
                전체 업데이트 중... (시간이 오래 걸립니다)
            </div>
            
            <div class="result" id="result5" style="display:none;"></div>
        </div>
    </div>
    
    <script>
        // API Base URL
        const API_BASE = '${pageContext.request.contextPath}/api/stock/us';
        
        console.log('API Base URL:', API_BASE);
        
        // 0. API 상태 체크
        async function checkHealth() {
            showLoading('loading0', true);
            hideResult('result0');
            
            try {
                const response = await fetch(API_BASE + '/health');
                const data = await response.json();
                
                showLoading('loading0', false);
                displayResult('result0', data);
            } catch (error) {
                showLoading('loading0', false);
                displayError('result0', error);
            }
        }
        
        // 1. 단일 종목 업데이트
        async function updateSingleStock() {
            const stockCode = document.getElementById('stockCode1').value.trim();
            if (!stockCode) {
                alert('종목 심볼을 입력하세요');
                return;
            }
            
            showLoading('loading1', true);
            hideResult('result1');
            
            try {
                const response = await fetch(API_BASE + '/' + stockCode + '/update', {
                    method: 'POST'
                });
                const data = await response.json();
                
                showLoading('loading1', false);
                displayResult('result1', data, true);
            } catch (error) {
                showLoading('loading1', false);
                displayError('result1', error);
            }
        }
        
        // 최신 주가 조회
        async function getLatestPrice() {
            const stockCode = document.getElementById('stockCode1').value.trim();
            if (!stockCode) {
                alert('종목 심볼을 입력하세요');
                return;
            }
            
            showLoading('loading1', true);
            hideResult('result1');
            
            try {
                const response = await fetch(API_BASE + '/' + stockCode + '/latest');
                const data = await response.json();
                
                showLoading('loading1', false);
                displayResult('result1', data, true);
            } catch (error) {
                showLoading('loading1', false);
                displayError('result1', error);
            }
        }
        
        // 2. 여러 종목 업데이트
        async function updateMultipleStocks() {
            const stockCodesInput = document.getElementById('stockCodes').value.trim();
            if (!stockCodesInput) {
                alert('종목 심볼을 입력하세요');
                return;
            }
            
            const stockCodes = stockCodesInput.split(',').map(code => code.trim());
            
            if (stockCodes.length > 25) {
                if (!confirm('무료 API는 하루 25개까지만 가능합니다. 처음 25개만 업데이트하시겠습니까?')) {
                    return;
                }
            }
            
            showLoading('loading2', true);
            hideResult('result2');
            
            try {
                const response = await fetch(API_BASE + '/update/batch', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(stockCodes)
                });
                const data = await response.json();
                
                showLoading('loading2', false);
                displayResult('result2', data);
            } catch (error) {
                showLoading('loading2', false);
                displayError('result2', error);
            }
        }
        
        // 3. 주가 히스토리 조회
        async function getStockHistory() {
            const stockCode = document.getElementById('stockCode3').value.trim();
            const days = document.getElementById('days').value.trim();
            
            if (!stockCode || !days) {
                alert('종목 심볼과 조회 일수를 입력하세요');
                return;
            }
            
            showLoading('loading3', true);
            hideResult('result3');
            
            try {
                const response = await fetch(API_BASE + '/' + stockCode + '/history?days=' + days);
                const data = await response.json();
                
                showLoading('loading3', false);
                displayResult('result3', data);
            } catch (error) {
                showLoading('loading3', false);
                displayError('result3', error);
            }
        }
        
        // 4. 실시간 크롤링
        async function crawlStockPrice() {
            const stockCode = document.getElementById('stockCode4').value.trim();
            if (!stockCode) {
                alert('종목 심볼을 입력하세요');
                return;
            }
            
            showLoading('loading4', true);
            hideResult('result4');
            
            try {
                const response = await fetch(API_BASE + '/' + stockCode + '/crawl');
                const data = await response.json();
                
                showLoading('loading4', false);
                displayResult('result4', data, true);
            } catch (error) {
                showLoading('loading4', false);
                displayError('result4', error);
            }
        }
        
        // 5. 전체 업데이트
        async function updateAllUSStocks() {
            if (!confirm('전체 미국 주식을 업데이트하시겠습니까? (최대 25개)')) {
                return;
            }
            
            showLoading('loading5', true);
            hideResult('result5');
            
            try {
                const response = await fetch(API_BASE + '/update/all', {
                    method: 'POST'
                });
                const data = await response.json();
                
                showLoading('loading5', false);
                displayResult('result5', data);
            } catch (error) {
                showLoading('loading5', false);
                displayError('result5', error);
            }
        }
        
        // Helper Functions
        function showLoading(elementId, show) {
            const element = document.getElementById(elementId);
            if (show) {
                element.classList.add('active');
            } else {
                element.classList.remove('active');
            }
        }
        
        function hideResult(elementId) {
            document.getElementById(elementId).style.display = 'none';
        }
        
        function displayResult(elementId, data, showStockInfo = false) {
            const resultDiv = document.getElementById(elementId);
            resultDiv.style.display = 'block';
            
            let html = '<h3 class="' + (data.success ? 'success' : 'error') + '">' +
                (data.success ? '✅ 성공' : '❌ 실패') + ': ' + data.message +
                '</h3>';
            
            if (showStockInfo && data.success && data.data) {
                const stock = data.data;
                html += '<div class="stock-info">' +
                    '<h4>' + (stock.stockSymbol || '종목') + '</h4>' +
                    '<div class="price-grid">' +
                    '<div class="price-item"><strong>현재가</strong><span>$' + formatPrice(stock.closePrice) + '</span></div>' +
                    '<div class="price-item"><strong>시가</strong><span>$' + formatPrice(stock.openPrice) + '</span></div>' +
                    '<div class="price-item"><strong>고가</strong><span>$' + formatPrice(stock.highPrice) + '</span></div>' +
                    '<div class="price-item"><strong>저가</strong><span>$' + formatPrice(stock.lowPrice) + '</span></div>' +
                    '<div class="price-item"><strong>거래량</strong><span>' + formatVolume(stock.volume) + '</span></div>' +
                    '</div></div>';
            }
            
            html += '<pre>' + JSON.stringify(data, null, 2) + '</pre>';
            resultDiv.innerHTML = html;
        }
        
        function displayError(elementId, error) {
            const resultDiv = document.getElementById(elementId);
            resultDiv.style.display = 'block';
            resultDiv.innerHTML = '<h3 class="error">❌ 에러 발생</h3><pre>' + (error.message || error) + '</pre>';
        }
        
        function formatPrice(price) {
            if (!price) return 'N/A';
            return parseFloat(price).toFixed(2);
        }
        
        function formatVolume(volume) {
            if (!volume) return 'N/A';
            return new Intl.NumberFormat('en-US').format(volume);
        }
    </script>
</body>
</html>
