<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../common/header.jsp" />

<style>
    .simulation-container {
        max-width: 1400px;
        margin: 0 auto;
        padding: 2rem;
    }
    
    .simulation-header {
        text-align: center;
        margin-bottom: 3rem;
    }
    
    .simulation-title {
        font-size: 2.5rem;
        font-weight: 700;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 0.5rem;
    }
    
    .simulation-subtitle {
        color: #6b7280;
        font-size: 1.1rem;
    }
    
    .simulation-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 2rem;
        margin-bottom: 2rem;
    }
    
    @media (max-width: 1024px) {
        .simulation-grid {
            grid-template-columns: 1fr;
        }
    }
    
    .simulation-card {
        background: white;
        border-radius: 20px;
        padding: 2rem;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }
    
    .card-header {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        margin-bottom: 1.5rem;
        padding-bottom: 1rem;
        border-bottom: 2px solid #f3f4f6;
    }
    
    .card-icon {
        font-size: 2rem;
    }
    
    .card-title {
        font-size: 1.5rem;
        font-weight: 700;
        color: #1f2937;
    }
    
    .portfolio-list {
        max-height: 400px;
        overflow-y: auto;
    }
    
    .portfolio-item {
        background: #f9fafb;
        border-radius: 12px;
        padding: 1.25rem;
        margin-bottom: 1rem;
        cursor: pointer;
        transition: all 0.3s;
        border: 2px solid transparent;
    }
    
    .portfolio-item:hover {
        background: #f3f4f6;
        transform: translateX(5px);
    }
    
    .portfolio-item.selected {
        background: linear-gradient(135deg, #667eea20 0%, #764ba220 100%);
        border-color: #667eea;
    }
    
    .stock-name {
        font-size: 1.125rem;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 0.5rem;
    }
    
    .stock-info {
        display: flex;
        justify-content: space-between;
        font-size: 0.875rem;
        color: #6b7280;
    }
    
    .simulation-controls {
        background: #f9fafb;
        border-radius: 15px;
        padding: 1.5rem;
        margin-bottom: 1.5rem;
    }
    
    .control-group {
        margin-bottom: 1.25rem;
    }
    
    .control-label {
        display: block;
        font-weight: 600;
        color: #374151;
        margin-bottom: 0.5rem;
    }
    
    .control-input {
        width: 100%;
        padding: 0.75rem;
        border: 2px solid #e5e7eb;
        border-radius: 10px;
        font-size: 1rem;
        transition: all 0.3s;
    }
    
    .control-input:focus {
        outline: none;
        border-color: #667eea;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }
    
    .control-buttons {
        display: flex;
        gap: 1rem;
    }
    
    .btn-simulate {
        flex: 1;
        padding: 0.875rem;
        border-radius: 10px;
        border: none;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        font-weight: 600;
        font-size: 1rem;
        cursor: pointer;
        transition: all 0.3s;
    }
    
    .btn-simulate:hover {
        transform: translateY(-2px);
        box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
    }
    
    .btn-reset {
        padding: 0.875rem 2rem;
        border-radius: 10px;
        border: 2px solid #e5e7eb;
        background: white;
        color: #6b7280;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s;
    }
    
    .btn-reset:hover {
        border-color: #667eea;
        color: #667eea;
    }
    
    .result-section {
        background: white;
        border-radius: 20px;
        padding: 2rem;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        display: none;
    }
    
    .result-section.show {
        display: block;
        animation: slideIn 0.5s ease-out;
    }
    
    @keyframes slideIn {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
    
    .result-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 1.5rem;
        margin-bottom: 2rem;
    }
    
    .result-card {
        background: linear-gradient(135deg, #f9fafb 0%, #ffffff 100%);
        border-radius: 15px;
        padding: 1.5rem;
        border: 2px solid #f3f4f6;
    }
    
    .result-label {
        font-size: 0.875rem;
        color: #6b7280;
        margin-bottom: 0.5rem;
    }
    
    .result-value {
        font-size: 1.75rem;
        font-weight: 700;
        color: #1f2937;
    }
    
    .result-value.positive {
        color: #10b981;
    }
    
    .result-value.negative {
        color: #ef4444;
    }
    
    .result-chart {
        background: #f9fafb;
        border-radius: 15px;
        padding: 2rem;
        min-height: 300px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .empty-state {
        text-align: center;
        padding: 3rem;
        color: #9ca3af;
    }
    
    .empty-icon {
        font-size: 4rem;
        margin-bottom: 1rem;
    }
    
    .no-portfolio {
        text-align: center;
        padding: 3rem;
        color: #9ca3af;
    }
    
    .comparison-table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 1.5rem;
    }
    
    .comparison-table th,
    .comparison-table td {
        padding: 1rem;
        text-align: left;
        border-bottom: 1px solid #f3f4f6;
    }
    
    .comparison-table th {
        background: #f9fafb;
        font-weight: 600;
        color: #374151;
    }
    
    .comparison-table tr:hover {
        background: #f9fafb;
    }
</style>

<div class="simulation-container">
    <div class="simulation-header">
        <h1 class="simulation-title">🎮 포트폴리오 시뮬레이션</h1>
        <p class="simulation-subtitle">가상의 시나리오로 포트폴리오 변화를 시뮬레이션해보세요</p>
    </div>
    
    <c:choose>
        <c:when test="${empty portfolioList}">
            <div class="no-portfolio">
                <div class="empty-icon">📭</div>
                <h3>포트폴리오가 비어있습니다</h3>
                <p>먼저 포트폴리오에 종목을 추가해주세요.</p>
                <a href="${pageContext.request.contextPath}/portfolio/create" 
                   class="btn-simulate" 
                   style="display: inline-block; margin-top: 1rem; text-decoration: none;">
                    종목 추가하기
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="simulation-grid">
                <!-- 좌측: 포트폴리오 선택 -->
                <div class="simulation-card">
                    <div class="card-header">
                        <span class="card-icon">📋</span>
                        <h2 class="card-title">내 포트폴리오</h2>
                    </div>
                    
                    <div class="portfolio-list">
                        <c:forEach var="portfolio" items="${portfolioList}">
                            <div class="portfolio-item" 
                                 data-portfolio-id="${portfolio.portfolioId}"
                                 data-stock-name="${portfolio.stockName}"
                                 data-stock-code="${portfolio.stockCode}"
                                 data-quantity="${portfolio.quantity}"
                                 data-avg-price="${portfolio.averagePurchasePrice}"
                                 data-current-price="${portfolio.currentPrice}">
                                <div class="stock-name">${portfolio.stockName}</div>
                                <div class="stock-info">
                                    <span>보유: <fmt:formatNumber value="${portfolio.quantity}" pattern="#,##0.##"/>주</span>
                                    <span>평균가: <fmt:formatNumber value="${portfolio.averagePurchasePrice}" pattern="#,##0"/>원</span>
                                </div>
                                <div class="stock-info">
                                    <span>현재가: <fmt:formatNumber value="${portfolio.currentPrice}" pattern="#,##0"/>원</span>
                                    <c:set var="profitRate" 
                                           value="${((portfolio.currentPrice - portfolio.averagePurchasePrice) / portfolio.averagePurchasePrice * 100)}" />
                                    <span class="${profitRate >= 0 ? 'positive' : 'negative'}">
                                        수익률: <fmt:formatNumber value="${profitRate}" pattern="#,##0.00"/>%
                                    </span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
                
                <!-- 우측: 시뮬레이션 설정 -->
                <div class="simulation-card">
                    <div class="card-header">
                        <span class="card-icon">⚙️</span>
                        <h2 class="card-title">시뮬레이션 설정</h2>
                    </div>
                    
                    <div id="simulationForm">
                        <div class="simulation-controls">
                            <div class="control-group">
                                <label class="control-label">선택한 종목</label>
                                <input type="text" class="control-input" id="selectedStock" 
                                       value="종목을 선택해주세요" readonly>
                                <input type="hidden" id="selectedPortfolioId">
                                <input type="hidden" id="currentQuantity">
                                <input type="hidden" id="currentAvgPrice">
                                <input type="hidden" id="currentPrice">
                            </div>
                            
                            <div class="control-group">
                                <label class="control-label">시뮬레이션 시나리오</label>
                                <select class="control-input" id="scenario">
                                    <option value="">선택하세요</option>
                                    <option value="price_change">주가 변동 시뮬레이션</option>
                                    <option value="additional_buy">추가 매수 시뮬레이션</option>
                                    <option value="sell">매도 시뮬레이션</option>
                                </select>
                            </div>
                            
                            <!-- 주가 변동 시뮬레이션 -->
                            <div id="priceChangeGroup" class="control-group" style="display: none;">
                                <label class="control-label">예상 주가 변동 (%)</label>
                                <input type="number" class="control-input" id="priceChangePercent" 
                                       placeholder="예: 10 (10% 상승), -5 (5% 하락)" step="0.1">
                            </div>
                            
                            <!-- 추가 매수 시뮬레이션 -->
                            <div id="additionalBuyGroup" style="display: none;">
                                <div class="control-group">
                                    <label class="control-label">추가 매수 수량</label>
                                    <input type="number" class="control-input" id="additionalQuantity" 
                                           placeholder="추가 매수할 수량" min="0" step="0.01">
                                </div>
                                <div class="control-group">
                                    <label class="control-label">추가 매수 가격</label>
                                    <input type="number" class="control-input" id="additionalPrice" 
                                           placeholder="매수 가격" min="0">
                                </div>
                            </div>
                            
                            <!-- 매도 시뮬레이션 -->
                            <div id="sellGroup" style="display: none;">
                                <div class="control-group">
                                    <label class="control-label">매도 수량</label>
                                    <input type="number" class="control-input" id="sellQuantity" 
                                           placeholder="매도할 수량" min="0" step="0.01">
                                </div>
                                <div class="control-group">
                                    <label class="control-label">매도 가격</label>
                                    <input type="number" class="control-input" id="sellPrice" 
                                           placeholder="매도 가격" min="0">
                                </div>
                            </div>
                        </div>
                        
                        <div class="control-buttons">
                            <button type="button" class="btn-simulate" onclick="runSimulation()">
                                🚀 시뮬레이션 실행
                            </button>
                            <button type="button" class="btn-reset" onclick="resetSimulation()">
                                🔄 초기화
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- 시뮬레이션 결과 -->
            <div class="result-section" id="resultSection">
                <div class="card-header">
                    <span class="card-icon">📊</span>
                    <h2 class="card-title">시뮬레이션 결과</h2>
                </div>
                
                <div class="result-grid">
                    <div class="result-card">
                        <div class="result-label">현재 평가금액</div>
                        <div class="result-value" id="currentValue">-</div>
                    </div>
                    
                    <div class="result-card">
                        <div class="result-label">시뮬레이션 후 평가금액</div>
                        <div class="result-value" id="simulatedValue">-</div>
                    </div>
                    
                    <div class="result-card">
                        <div class="result-label">평가금액 변화</div>
                        <div class="result-value" id="valueChange">-</div>
                    </div>
                    
                    <div class="result-card">
                        <div class="result-label">수익률 변화</div>
                        <div class="result-value" id="rateChange">-</div>
                    </div>
                </div>
                
                <table class="comparison-table">
                    <thead>
                        <tr>
                            <th>구분</th>
                            <th>현재</th>
                            <th>시뮬레이션 후</th>
                            <th>변화</th>
                        </tr>
                    </thead>
                    <tbody id="comparisonBody">
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script>
let selectedPortfolio = null;

// 포트폴리오 선택
document.querySelectorAll('.portfolio-item').forEach(item => {
    item.addEventListener('click', function() {
        // 모든 항목에서 selected 제거
        document.querySelectorAll('.portfolio-item').forEach(i => i.classList.remove('selected'));
        
        // 현재 항목 선택
        this.classList.add('selected');
        
        // 데이터 저장
        selectedPortfolio = {
            id: this.dataset.portfolioId,
            name: this.dataset.stockName,
            code: this.dataset.stockCode,
            quantity: parseFloat(this.dataset.quantity),
            avgPrice: parseFloat(this.dataset.avgPrice),
            currentPrice: parseFloat(this.dataset.currentPrice)
        };
        
        // 폼 업데이트
        document.getElementById('selectedStock').value = selectedPortfolio.name + ' (' + selectedPortfolio.code + ')';
        document.getElementById('selectedPortfolioId').value = selectedPortfolio.id;
        document.getElementById('currentQuantity').value = selectedPortfolio.quantity;
        document.getElementById('currentAvgPrice').value = selectedPortfolio.avgPrice;
        document.getElementById('currentPrice').value = selectedPortfolio.currentPrice;
        
        // 결과 숨기기
        document.getElementById('resultSection').classList.remove('show');
    });
});

// 시나리오 선택 시 입력 필드 표시/숨김
document.getElementById('scenario').addEventListener('change', function() {
    const scenario = this.value;
    
    document.getElementById('priceChangeGroup').style.display = 'none';
    document.getElementById('additionalBuyGroup').style.display = 'none';
    document.getElementById('sellGroup').style.display = 'none';
    
    if (scenario === 'price_change') {
        document.getElementById('priceChangeGroup').style.display = 'block';
    } else if (scenario === 'additional_buy') {
        document.getElementById('additionalBuyGroup').style.display = 'block';
        // 현재가를 기본값으로 설정
        if (selectedPortfolio) {
            document.getElementById('additionalPrice').value = selectedPortfolio.currentPrice;
        }
    } else if (scenario === 'sell') {
        document.getElementById('sellGroup').style.display = 'block';
        // 현재가를 기본값으로 설정
        if (selectedPortfolio) {
            document.getElementById('sellPrice').value = selectedPortfolio.currentPrice;
        }
    }
});

// 시뮬레이션 실행
function runSimulation() {
    if (!selectedPortfolio) {
        alert('포트폴리오를 선택해주세요.');
        return;
    }
    
    const scenario = document.getElementById('scenario').value;
    if (!scenario) {
        alert('시뮬레이션 시나리오를 선택해주세요.');
        return;
    }
    
    let result = null;
    
    if (scenario === 'price_change') {
        result = simulatePriceChange();
    } else if (scenario === 'additional_buy') {
        result = simulateAdditionalBuy();
    } else if (scenario === 'sell') {
        result = simulateSell();
    }
    
    if (result) {
        displayResult(result);
    }
}

// 주가 변동 시뮬레이션
function simulatePriceChange() {
    const changePercent = parseFloat(document.getElementById('priceChangePercent').value);
    
    if (isNaN(changePercent)) {
        alert('변동률을 입력해주세요.');
        return null;
    }
    
    const currentValue = selectedPortfolio.quantity * selectedPortfolio.currentPrice;
    const simulatedPrice = selectedPortfolio.currentPrice * (1 + changePercent / 100);
    const simulatedValue = selectedPortfolio.quantity * simulatedPrice;
    
    const currentProfit = currentValue - (selectedPortfolio.quantity * selectedPortfolio.avgPrice);
    const currentProfitRate = (currentProfit / (selectedPortfolio.quantity * selectedPortfolio.avgPrice)) * 100;
    
    const simulatedProfit = simulatedValue - (selectedPortfolio.quantity * selectedPortfolio.avgPrice);
    const simulatedProfitRate = (simulatedProfit / (selectedPortfolio.quantity * selectedPortfolio.avgPrice)) * 100;
    
    return {
        type: 'price_change',
        currentValue: currentValue,
        simulatedValue: simulatedValue,
        valueChange: simulatedValue - currentValue,
        currentProfitRate: currentProfitRate,
        simulatedProfitRate: simulatedProfitRate,
        comparison: [
            ['보유 수량', selectedPortfolio.quantity + '주', selectedPortfolio.quantity + '주', '-'],
            ['평균 매입가', formatNumber(selectedPortfolio.avgPrice) + '원', formatNumber(selectedPortfolio.avgPrice) + '원', '-'],
            ['주가', formatNumber(selectedPortfolio.currentPrice) + '원', formatNumber(simulatedPrice) + '원', formatNumber(simulatedPrice - selectedPortfolio.currentPrice) + '원'],
            ['평가금액', formatNumber(currentValue) + '원', formatNumber(simulatedValue) + '원', formatNumber(simulatedValue - currentValue) + '원'],
            ['평가손익', formatNumber(currentProfit) + '원', formatNumber(simulatedProfit) + '원', formatNumber(simulatedProfit - currentProfit) + '원']
        ]
    };
}

// 추가 매수 시뮬레이션
function simulateAdditionalBuy() {
    const addQty = parseFloat(document.getElementById('additionalQuantity').value);
    const addPrice = parseFloat(document.getElementById('additionalPrice').value);
    
    if (isNaN(addQty) || isNaN(addPrice)) {
        alert('추가 매수 정보를 모두 입력해주세요.');
        return null;
    }
    
    if (addQty <= 0 || addPrice <= 0) {
        alert('수량과 가격은 0보다 커야 합니다.');
        return null;
    }
    
    const currentValue = selectedPortfolio.quantity * selectedPortfolio.currentPrice;
    const currentTotalCost = selectedPortfolio.quantity * selectedPortfolio.avgPrice;
    
    const newQuantity = selectedPortfolio.quantity + addQty;
    const newTotalCost = currentTotalCost + (addQty * addPrice);
    const newAvgPrice = newTotalCost / newQuantity;
    const simulatedValue = newQuantity * selectedPortfolio.currentPrice;
    
    const currentProfit = currentValue - currentTotalCost;
    const currentProfitRate = (currentProfit / currentTotalCost) * 100;
    
    const simulatedProfit = simulatedValue - newTotalCost;
    const simulatedProfitRate = (simulatedProfit / newTotalCost) * 100;
    
    return {
        type: 'additional_buy',
        currentValue: currentValue,
        simulatedValue: simulatedValue,
        valueChange: simulatedValue - currentValue,
        currentProfitRate: currentProfitRate,
        simulatedProfitRate: simulatedProfitRate,
        comparison: [
            ['보유 수량', selectedPortfolio.quantity + '주', newQuantity + '주', '+' + addQty + '주'],
            ['평균 매입가', formatNumber(selectedPortfolio.avgPrice) + '원', formatNumber(newAvgPrice) + '원', formatNumber(newAvgPrice - selectedPortfolio.avgPrice) + '원'],
            ['총 매입금액', formatNumber(currentTotalCost) + '원', formatNumber(newTotalCost) + '원', '+' + formatNumber(addQty * addPrice) + '원'],
            ['평가금액', formatNumber(currentValue) + '원', formatNumber(simulatedValue) + '원', formatNumber(simulatedValue - currentValue) + '원'],
            ['평가손익', formatNumber(currentProfit) + '원', formatNumber(simulatedProfit) + '원', formatNumber(simulatedProfit - currentProfit) + '원']
        ]
    };
}

// 매도 시뮬레이션
function simulateSell() {
    const sellQty = parseFloat(document.getElementById('sellQuantity').value);
    const sellPrice = parseFloat(document.getElementById('sellPrice').value);
    
    if (isNaN(sellQty) || isNaN(sellPrice)) {
        alert('매도 정보를 모두 입력해주세요.');
        return null;
    }
    
    if (sellQty <= 0 || sellPrice <= 0) {
        alert('수량과 가격은 0보다 커야 합니다.');
        return null;
    }
    
    if (sellQty > selectedPortfolio.quantity) {
        alert('보유 수량보다 많이 매도할 수 없습니다.');
        return null;
    }
    
    const currentValue = selectedPortfolio.quantity * selectedPortfolio.currentPrice;
    const currentTotalCost = selectedPortfolio.quantity * selectedPortfolio.avgPrice;
    
    const newQuantity = selectedPortfolio.quantity - sellQty;
    const sellRevenue = sellQty * sellPrice;
    const sellCost = sellQty * selectedPortfolio.avgPrice;
    const sellProfit = sellRevenue - sellCost;
    
    const newTotalCost = currentTotalCost - sellCost;
    const simulatedValue = newQuantity * selectedPortfolio.currentPrice;
    
    const currentProfit = currentValue - currentTotalCost;
    const currentProfitRate = (currentProfit / currentTotalCost) * 100;
    
    const simulatedProfit = simulatedValue - newTotalCost;
    const simulatedProfitRate = newQuantity > 0 ? (simulatedProfit / newTotalCost) * 100 : 0;
    
    return {
        type: 'sell',
        currentValue: currentValue,
        simulatedValue: simulatedValue,
        valueChange: simulatedValue - currentValue,
        currentProfitRate: currentProfitRate,
        simulatedProfitRate: simulatedProfitRate,
        comparison: [
            ['보유 수량', selectedPortfolio.quantity + '주', newQuantity + '주', '-' + sellQty + '주'],
            ['평균 매입가', formatNumber(selectedPortfolio.avgPrice) + '원', formatNumber(selectedPortfolio.avgPrice) + '원', '-'],
            ['매도 수익', '-', formatNumber(sellProfit) + '원', formatNumber(sellProfit) + '원'],
            ['잔여 평가금액', formatNumber(currentValue) + '원', formatNumber(simulatedValue) + '원', formatNumber(simulatedValue - currentValue) + '원'],
            ['잔여 평가손익', formatNumber(currentProfit) + '원', formatNumber(simulatedProfit) + '원', formatNumber(simulatedProfit - currentProfit) + '원']
        ]
    };
}

// 결과 표시
function displayResult(result) {
    document.getElementById('currentValue').textContent = formatNumber(result.currentValue) + '원';
    document.getElementById('simulatedValue').textContent = formatNumber(result.simulatedValue) + '원';
    
    const valueChangeElement = document.getElementById('valueChange');
    valueChangeElement.textContent = (result.valueChange >= 0 ? '+' : '') + formatNumber(result.valueChange) + '원';
    valueChangeElement.className = 'result-value ' + (result.valueChange >= 0 ? 'positive' : 'negative');
    
    const rateChangeElement = document.getElementById('rateChange');
    const rateDiff = result.simulatedProfitRate - result.currentProfitRate;
    rateChangeElement.textContent = (rateDiff >= 0 ? '+' : '') + formatNumber(rateDiff) + '%';
    rateChangeElement.className = 'result-value ' + (rateDiff >= 0 ? 'positive' : 'negative');
    
    // 비교 테이블
    const tbody = document.getElementById('comparisonBody');
    tbody.innerHTML = '';
    result.comparison.forEach(row => {
        const tr = document.createElement('tr');
        row.forEach(cell => {
            const td = document.createElement('td');
            td.textContent = cell;
            tr.appendChild(td);
        });
        tbody.appendChild(tr);
    });
    
    // 결과 표시
    document.getElementById('resultSection').classList.add('show');
    document.getElementById('resultSection').scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

// 초기화
function resetSimulation() {
    document.getElementById('scenario').value = '';
    document.getElementById('priceChangePercent').value = '';
    document.getElementById('additionalQuantity').value = '';
    document.getElementById('additionalPrice').value = '';
    document.getElementById('sellQuantity').value = '';
    document.getElementById('sellPrice').value = '';
    
    document.getElementById('priceChangeGroup').style.display = 'none';
    document.getElementById('additionalBuyGroup').style.display = 'none';
    document.getElementById('sellGroup').style.display = 'none';
    
    document.getElementById('resultSection').classList.remove('show');
}

// 숫자 포맷팅
function formatNumber(num) {
    return Math.round(num).toLocaleString('ko-KR');
}
</script>

<jsp:include page="../common/footer.jsp" />
