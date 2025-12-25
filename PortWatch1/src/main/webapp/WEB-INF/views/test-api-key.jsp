<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>🔑 Alpha Vantage API 설정 확인</title>
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
            padding: 40px 20px;
        }
        
        .container {
            max-width: 900px;
            margin: 0 auto;
        }
        
        .card {
            background: white;
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 20px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
        }
        
        h1 {
            color: #667eea;
            margin-bottom: 20px;
            font-size: 2em;
        }
        
        h2 {
            color: #333;
            margin: 20px 0 10px 0;
            font-size: 1.3em;
            border-bottom: 2px solid #667eea;
            padding-bottom: 10px;
        }
        
        .info-box {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            margin: 20px 0;
        }
        
        .info-box h3 {
            color: #667eea;
            margin-bottom: 10px;
        }
        
        .info-item {
            margin: 10px 0;
            padding: 10px;
            background: white;
            border-radius: 5px;
        }
        
        .info-item strong {
            color: #764ba2;
            display: inline-block;
            min-width: 150px;
        }
        
        .btn {
            background: #667eea;
            color: white;
            border: none;
            padding: 12px 30px;
            border-radius: 8px;
            cursor: pointer;
            font-size: 1em;
            font-weight: bold;
            margin: 10px 5px;
            transition: all 0.3s;
        }
        
        .btn:hover {
            background: #764ba2;
            transform: translateY(-2px);
        }
        
        .btn-success {
            background: #28a745;
        }
        
        .btn-success:hover {
            background: #218838;
        }
        
        .btn-warning {
            background: #ffc107;
            color: #333;
        }
        
        .btn-warning:hover {
            background: #e0a800;
        }
        
        .test-result {
            margin-top: 20px;
            padding: 20px;
            border-radius: 10px;
            display: none;
        }
        
        .test-result.success {
            background: #d4edda;
            border: 2px solid #28a745;
            color: #155724;
        }
        
        .test-result.error {
            background: #f8d7da;
            border: 2px solid #dc3545;
            color: #721c24;
        }
        
        .code-block {
            background: #2d2d2d;
            color: #f8f8f2;
            padding: 20px;
            border-radius: 10px;
            overflow-x: auto;
            font-family: 'Courier New', monospace;
            margin: 15px 0;
        }
        
        .step {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 5px 15px;
            border-radius: 20px;
            display: inline-block;
            margin-right: 10px;
            font-weight: bold;
        }
        
        .warning {
            background: #fff3cd;
            border-left: 4px solid #ffc107;
            padding: 15px;
            margin: 15px 0;
            border-radius: 5px;
        }
        
        .success {
            background: #d4edda;
            border-left: 4px solid #28a745;
            padding: 15px;
            margin: 15px 0;
            border-radius: 5px;
        }
        
        .loading {
            display: none;
            text-align: center;
            padding: 20px;
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
        
        ul, ol {
            margin-left: 20px;
            margin-top: 10px;
        }
        
        li {
            margin: 5px 0;
            line-height: 1.6;
        }
        
        a {
            color: #667eea;
            text-decoration: none;
            font-weight: bold;
        }
        
        a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- 헤더 -->
        <div class="card">
            <h1>🔑 Alpha Vantage API 설정 확인</h1>
            <p>미국 주식 가격 업데이트를 위한 Alpha Vantage API 설정을 확인하고 테스트합니다.</p>
        </div>
        
        <!-- API 키 발급 안내 -->
        <div class="card">
            <h2><span class="step">Step 1</span> API 키 발급</h2>
            
            <div class="info-box">
                <h3>📝 무료 API 키 발급 (5분 소요)</h3>
                <ol>
                    <li>웹사이트 방문: <a href="https://www.alphavantage.co/support/#api-key" target="_blank">https://www.alphavantage.co/support/#api-key</a></li>
                    <li>이메일 주소 입력</li>
                    <li>"GET FREE API KEY" 버튼 클릭</li>
                    <li>발급된 API 키 복사 (예: ABC123XYZ456)</li>
                </ol>
            </div>
            
            <div class="warning">
                ⚠️ <strong>무료 플랜 제한:</strong><br>
                • 하루 25회 API 호출 제한<br>
                • 분당 5회 호출 제한<br>
                • 프로젝트 테스트에는 충분합니다!
            </div>
        </div>
        
        <!-- API 키 설정 -->
        <div class="card">
            <h2><span class="step">Step 2</span> API 키 설정</h2>
            
            <div class="info-box">
                <h3>📂 application.properties 파일 수정</h3>
                <p><strong>경로:</strong> src/main/resources/application.properties</p>
                
                <div class="code-block">alphavantage.api.key=YOUR_API_KEY_HERE
↓ 변경
alphavantage.api.key=ABC123XYZ456</div>
            </div>
            
            <div class="success">
                ✅ <strong>설정 완료 후:</strong><br>
                1. 프로젝트 Clean & Build<br>
                2. 서버 재시작<br>
                3. 아래 테스트 버튼으로 확인
            </div>
        </div>
        
        <!-- API 테스트 -->
        <div class="card">
            <h2><span class="step">Step 3</span> API 연결 테스트</h2>
            
            <div class="info-box">
                <h3>🧪 테스트할 종목 선택</h3>
                <p>Apple(AAPL) 주식으로 API가 정상 작동하는지 확인합니다.</p>
            </div>
            
            <button class="btn btn-success" onclick="testAPI()">
                🧪 API 연결 테스트 (AAPL)
            </button>
            
            <button class="btn btn-warning" onclick="testMultiple()">
                📊 여러 종목 테스트 (AAPL, MSFT, TSLA)
            </button>
            
            <div class="loading" id="loading">
                <div class="spinner"></div>
                <p>API 호출 중... 잠시만 기다려주세요.</p>
            </div>
            
            <div class="test-result" id="testResult"></div>
        </div>
        
        <!-- 사용 가이드 -->
        <div class="card">
            <h2>📖 사용 가이드</h2>
            
            <div class="info-box">
                <h3>🎯 미국 주식 업데이트 방법</h3>
                
                <h4>1. 단일 종목 업데이트</h4>
                <div class="code-block">URL: /portwatch/api/us-stock/update/AAPL
메소드: GET</div>
                
                <h4>2. 여러 종목 업데이트</h4>
                <div class="code-block">URL: /portwatch/api/us-stock/update-multiple
메소드: POST
Body: ["AAPL", "MSFT", "TSLA"]</div>
                
                <h4>3. 관심종목 페이지에서 확인</h4>
                <div class="code-block">URL: /portwatch/watchlist/list</div>
            </div>
            
            <div class="info-box">
                <h3>⏰ 자동 업데이트 스케줄러</h3>
                <p>매일 오전 7시에 자동으로 미국 주식 가격이 업데이트됩니다.</p>
                <p>(미국 장 마감 후, 한국 시간 기준)</p>
            </div>
        </div>
        
        <!-- 문제 해결 -->
        <div class="card">
            <h2>🔧 문제 해결</h2>
            
            <div class="info-box">
                <h3>❌ "응답에 주가 데이터가 없습니다"</h3>
                <p><strong>원인:</strong> API 키가 설정되지 않았거나 잘못되었습니다.</p>
                <p><strong>해결:</strong> application.properties 파일에서 API 키 확인 후 서버 재시작</p>
            </div>
            
            <div class="info-box">
                <h3>❌ "API 요청 제한 초과"</h3>
                <p><strong>원인:</strong> 하루 25회 또는 분당 5회 제한 초과</p>
                <p><strong>해결:</strong> 내일 다시 시도하거나 프리미엄 플랜 구매</p>
            </div>
            
            <div class="info-box">
                <h3>❌ "종목을 찾을 수 없습니다"</h3>
                <p><strong>원인:</strong> 데이터베이스에 해당 종목이 없습니다.</p>
                <p><strong>해결:</strong> STOCK 테이블에 종목 추가 필요</p>
            </div>
        </div>
        
        <!-- 프리미엄 플랜 안내 -->
        <div class="card">
            <h2>💎 프리미엄 플랜 (선택사항)</h2>
            
            <div class="info-box">
                <h3>더 많은 API 호출이 필요하신가요?</h3>
                <p><strong>가격:</strong> 월 $49.99부터</p>
                <p><strong>혜택:</strong></p>
                <ul>
                    <li>분당 75회 호출</li>
                    <li>월 75,000회 호출</li>
                    <li>실시간 데이터</li>
                    <li>더 빠른 응답 속도</li>
                </ul>
                <p><strong>구매:</strong> <a href="https://www.alphavantage.co/premium/" target="_blank">https://www.alphavantage.co/premium/</a></p>
            </div>
        </div>
        
        <!-- 뒤로 가기 -->
        <div class="card" style="text-align: center;">
            <button class="btn" onclick="history.back()">
                ← 뒤로 가기
            </button>
            <button class="btn btn-success" onclick="location.href='${pageContext.request.contextPath}/watchlist/list'">
                ⭐ 관심종목 보기
            </button>
        </div>
    </div>
    
    <script>
        // API 연결 테스트
        function testAPI() {
            const loading = document.getElementById('loading');
            const resultDiv = document.getElementById('testResult');
            
            loading.style.display = 'block';
            resultDiv.style.display = 'none';
            
            fetch('${pageContext.request.contextPath}/api/us-stock/update/AAPL')
                .then(response => response.json())
                .then(data => {
                    loading.style.display = 'none';
                    resultDiv.style.display = 'block';
                    
                    if (data.success) {
                        resultDiv.className = 'test-result success';
                        resultDiv.innerHTML = `
                            <h3>✅ API 연결 성공!</h3>
                            <p><strong>종목:</strong> AAPL (Apple Inc.)</p>
                            <p><strong>현재가:</strong> $${data.stockPrice.closePrice}</p>
                            <p><strong>시가:</strong> $${data.stockPrice.openPrice}</p>
                            <p><strong>고가:</strong> $${data.stockPrice.highPrice}</p>
                            <p><strong>저가:</strong> $${data.stockPrice.lowPrice}</p>
                            <p><strong>거래량:</strong> ${data.stockPrice.volume.toLocaleString()}</p>
                            <p><strong>거래일:</strong> ${data.stockPrice.tradeDate}</p>
                            <hr style="margin: 15px 0;">
                            <p style="color: #28a745; font-weight: bold;">
                                🎉 Alpha Vantage API가 정상적으로 작동합니다!
                            </p>
                            <p>이제 관심종목 페이지에서 미국 주식 가격을 확인할 수 있습니다.</p>
                        `;
                    } else {
                        showError(data.message || '알 수 없는 오류');
                    }
                })
                .catch(error => {
                    loading.style.display = 'none';
                    showError(error.message);
                });
        }
        
        // 여러 종목 테스트
        function testMultiple() {
            const loading = document.getElementById('loading');
            const resultDiv = document.getElementById('testResult');
            
            loading.style.display = 'block';
            resultDiv.style.display = 'none';
            
            fetch('${pageContext.request.contextPath}/api/us-stock/update-multiple', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(['AAPL', 'MSFT', 'TSLA'])
            })
                .then(response => response.json())
                .then(data => {
                    loading.style.display = 'none';
                    resultDiv.style.display = 'block';
                    
                    if (data.success) {
                        resultDiv.className = 'test-result success';
                        let html = '<h3>✅ 여러 종목 업데이트 성공!</h3>';
                        
                        Object.keys(data.results).forEach(symbol => {
                            const stock = data.results[symbol];
                            html += `
                                <div style="background: white; padding: 10px; margin: 10px 0; border-radius: 5px;">
                                    <p><strong>${symbol}</strong> - $${stock.closePrice}</p>
                                </div>
                            `;
                        });
                        
                        html += '<p style="color: #28a745; font-weight: bold; margin-top: 15px;">🎉 모든 종목 업데이트 완료!</p>';
                        resultDiv.innerHTML = html;
                    } else {
                        showError(data.message || '알 수 없는 오류');
                    }
                })
                .catch(error => {
                    loading.style.display = 'none';
                    showError(error.message);
                });
        }
        
        // 에러 표시
        function showError(message) {
            const resultDiv = document.getElementById('testResult');
            resultDiv.style.display = 'block';
            resultDiv.className = 'test-result error';
            resultDiv.innerHTML = `
                <h3>❌ API 호출 실패</h3>
                <p><strong>오류 메시지:</strong></p>
                <p>${message}</p>
                <hr style="margin: 15px 0;">
                <p><strong>해결 방법:</strong></p>
                <ul style="text-align: left;">
                    <li>application.properties 파일에서 API 키 확인</li>
                    <li>API 키에 공백이나 따옴표가 없는지 확인</li>
                    <li>프로젝트 Clean & Build 후 서버 재시작</li>
                    <li>API 호출 제한 (하루 25회)을 초과하지 않았는지 확인</li>
                </ul>
            `;
        }
    </script>
</body>
</html>
