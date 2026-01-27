<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ì¤ìê° ì£¼ê° ìë°ì´í¸ íì¤í¸</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
        
        h2 {
            color: #667eea;
            margin-bottom: 20px;
            border-bottom: 2px solid #667eea;
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
        
        input[type="text"] {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        
        input[type="text"]:focus {
            outline: none;
            border-color: #667eea;
        }
        
        button {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        button:active {
            transform: translateY(0);
        }
        
        .result {
            margin-top: 20px;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 8px;
            border-left: 4px solid #667eea;
        }
        
        .result h3 {
            color: #667eea;
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
            color: #667eea;
            font-weight: bold;
        }
        
        .loading.active {
            display: block;
        }
        
        .spinner {
            border: 4px solid #f3f3f3;
            border-top: 4px solid #667eea;
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
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
    </style>
</head>
<body>
    <div class="container">
        <h1>ð ì¤ìê° ì£¼ê° ìë°ì´í¸ íì¤í¸</h1>
        
        <!-- ë¨ì¼ ì¢ëª© ìë°ì´í¸ -->
        <div class="test-section">
            <h2>1ï¸â£ ë¨ì¼ ì¢ëª© ìë°ì´í¸</h2>
            <div class="input-group">
                <label for="stockCode1">ì¢ëª© ì½ë ìë ¥:</label>
                <input type="text" id="stockCode1" placeholder="ì: 005930 (ì¼ì±ì ì)" value="005930">
            </div>
            <button onclick="updateSingleStock()">ì£¼ê° ìë°ì´í¸</button>
            <button onclick="getLatestPrice()">ìµì  ì£¼ê° ì¡°í</button>
            
            <div class="loading" id="loading1">
                <div class="spinner"></div>
                ì²ë¦¬ ì¤...
            </div>
            
            <div class="result" id="result1" style="display:none;"></div>
        </div>
        
        <!-- ì¬ë¬ ì¢ëª© ìë°ì´í¸ -->
        <div class="test-section">
            <h2>2ï¸â£ ì¬ë¬ ì¢ëª© ìë°ì´í¸</h2>
            <div class="input-group">
                <label for="stockCodes">ì¢ëª© ì½ë ìë ¥ (ì¼íë¡ êµ¬ë¶):</label>
                <input type="text" id="stockCodes" placeholder="ì: 005930,000660,035420" value="005930,000660,035420">
            </div>
            <button onclick="updateMultipleStocks()">ì¬ë¬ ì¢ëª© ìë°ì´í¸</button>
            
            <div class="loading" id="loading2">
                <div class="spinner"></div>
                ì²ë¦¬ ì¤... (ìê°ì´ ê±¸ë¦´ ì ììµëë¤)
            </div>
            
            <div class="result" id="result2" style="display:none;"></div>
        </div>
        
        <!-- ì£¼ê° íì¤í ë¦¬ ì¡°í -->
        <div class="test-section">
            <h2>3ï¸â£ ì£¼ê° íì¤í ë¦¬ ì¡°í</h2>
            <div class="input-group">
                <label for="stockCode3">ì¢ëª© ì½ë ìë ¥:</label>
                <input type="text" id="stockCode3" placeholder="ì: 005930" value="005930">
            </div>
            <div class="input-group">
                <label for="days">ì¡°í ì¼ì:</label>
                <input type="text" id="days" placeholder="ì: 30" value="30">
            </div>
            <button onclick="getStockHistory()">íì¤í ë¦¬ ì¡°í</button>
            
            <div class="loading" id="loading3">
                <div class="spinner"></div>
                ì²ë¦¬ ì¤...
            </div>
            
            <div class="result" id="result3" style="display:none;"></div>
        </div>
        
        <!-- ì¤ìê° í¬ë¡¤ë§ -->
        <div class="test-section">
            <h2>4ï¸â£ ì¤ìê° í¬ë¡¤ë§ (ì ì¥ ì í¨)</h2>
            <div class="input-group">
                <label for="stockCode4">ì¢ëª© ì½ë ìë ¥:</label>
                <input type="text" id="stockCode4" placeholder="ì: 005930" value="005930">
            </div>
            <button onclick="crawlStockPrice()">ì¤ìê° í¬ë¡¤ë§</button>
            
            <div class="loading" id="loading4">
                <div class="spinner"></div>
                í¬ë¡¤ë§ ì¤...
            </div>
            
            <div class="result" id="result4" style="display:none;"></div>
        </div>
    </div>
    
    <script>
        // API Base URL
        const API_BASE = '/api/stock-price';
        
        // 1. ë¨ì¼ ì¢ëª© ìë°ì´í¸
        async function updateSingleStock() {
            const stockCode = document.getElementById('stockCode1').value.trim();
            if (!stockCode) {
                alert('ì¢ëª© ì½ëë¥¼ ìë ¥íì¸ì');
                return;
            }
            
            showLoading('loading1', true);
            hideResult('result1');
            
            try {
                const response = await fetch(`${API_BASE}/update/${stockCode}`);
                const data = await response.json();
                
                showLoading('loading1', false);
                displayResult('result1', data, true);
            } catch (error) {
                showLoading('loading1', false);
                displayError('result1', error);
            }
        }
        
        // ìµì  ì£¼ê° ì¡°í
        async function getLatestPrice() {
            const stockCode = document.getElementById('stockCode1').value.trim();
            if (!stockCode) {
                alert('ì¢ëª© ì½ëë¥¼ ìë ¥íì¸ì');
                return;
            }
            
            showLoading('loading1', true);
            hideResult('result1');
            
            try {
                const response = await fetch(`${API_BASE}/latest/${stockCode}`);
                const data = await response.json();
                
                showLoading('loading1', false);
                displayResult('result1', data, true);
            } catch (error) {
                showLoading('loading1', false);
                displayError('result1', error);
            }
        }
        
        // 2. ì¬ë¬ ì¢ëª© ìë°ì´í¸
        async function updateMultipleStocks() {
            const stockCodesInput = document.getElementById('stockCodes').value.trim();
            if (!stockCodesInput) {
                alert('ì¢ëª© ì½ëë¥¼ ìë ¥íì¸ì');
                return;
            }
            
            const stockCodes = stockCodesInput.split(',').map(code => code.trim());
            
            showLoading('loading2', true);
            hideResult('result2');
            
            try {
                const response = await fetch(`${API_BASE}/update-multiple`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ stockCodes })
                });
                const data = await response.json();
                
                showLoading('loading2', false);
                displayResult('result2', data);
            } catch (error) {
                showLoading('loading2', false);
                displayError('result2', error);
            }
        }
        
        // 3. ì£¼ê° íì¤í ë¦¬ ì¡°í
        async function getStockHistory() {
            const stockCode = document.getElementById('stockCode3').value.trim();
            const days = document.getElementById('days').value.trim();
            
            if (!stockCode || !days) {
                alert('ì¢ëª© ì½ëì ì¡°í ì¼ìë¥¼ ìë ¥íì¸ì');
                return;
            }
            
            showLoading('loading3', true);
            hideResult('result3');
            
            try {
                const response = await fetch(`${API_BASE}/history/${stockCode}?days=${days}`);
                const data = await response.json();
                
                showLoading('loading3', false);
                displayResult('result3', data);
            } catch (error) {
                showLoading('loading3', false);
                displayError('result3', error);
            }
        }
        
        // 4. ì¤ìê° í¬ë¡¤ë§
        async function crawlStockPrice() {
            const stockCode = document.getElementById('stockCode4').value.trim();
            if (!stockCode) {
                alert('ì¢ëª© ì½ëë¥¼ ìë ¥íì¸ì');
                return;
            }
            
            showLoading('loading4', true);
            hideResult('result4');
            
            try {
                const response = await fetch(`${API_BASE}/crawl/${stockCode}`);
                const data = await response.json();
                
                showLoading('loading4', false);
                displayResult('result4', data, true);
            } catch (error) {
                showLoading('loading4', false);
                displayError('result4', error);
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
            
            let html = `<h3 class="${data.success ? 'success' : 'error'}">
                ${data.success ? 'â ì±ê³µ' : 'â ì¤í¨'}: ${data.message}
            </h3>`;
            
            if (showStockInfo && data.success && data.data) {
                const stock = data.data;
                html += `
                    <div class="stock-info">
                        <h4>${stock.stockName || 'ì¢ëª©'} (${stock.stockCode || 'N/A'})</h4>
                        <div class="price-grid">
                            <div class="price-item">
                                <strong>íì¬ê°</strong>
                                <span>${formatPrice(stock.closePrice)}</span>
                            </div>
                            <div class="price-item">
                                <strong>ìê°</strong>
                                <span>${formatPrice(stock.openPrice)}</span>
                            </div>
                            <div class="price-item">
                                <strong>ê³ ê°</strong>
                                <span>${formatPrice(stock.highPrice)}</span>
                            </div>
                            <div class="price-item">
                                <strong>ì ê°</strong>
                                <span>${formatPrice(stock.lowPrice)}</span>
                            </div>
                            <div class="price-item">
                                <strong>ê±°ëë</strong>
                                <span>${formatVolume(stock.volume)}</span>
                            </div>
                            <div class="price-item">
                                <strong>ê±°ëëê¸</strong>
                                <span>${formatValue(stock.tradingValue)}</span>
                            </div>
                        </div>
                    </div>
                `;
            }
            
            html += `<pre>${JSON.stringify(data, null, 2)}</pre>`;
            resultDiv.innerHTML = html;
        }
        
        function displayError(elementId, error) {
            const resultDiv = document.getElementById(elementId);
            resultDiv.style.display = 'block';
            resultDiv.innerHTML = `
                <h3 class="error">â ìë¬ ë°ì</h3>
                <pre>${error.message || error}</pre>
            `;
        }
        
        function formatPrice(price) {
            if (!price) return 'N/A';
            return new Intl.NumberFormat('ko-KR').format(price) + 'ì';
        }
        
        function formatVolume(volume) {
            if (!volume) return 'N/A';
            return new Intl.NumberFormat('ko-KR').format(volume);
        }
        
        function formatValue(value) {
            if (!value) return 'N/A';
            const billion = value / 100000000;
            return billion.toFixed(2) + 'ìµì';
        }
    </script>
</body>
</html>
