<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:include page="../common/header.jsp" />

<div class="container">
    <h2 class="mb-4">
        <i class="bi bi-briefcase"></i> <spring:message code="portfolio.title" />
    </h2>
    
    <!-- 포트폴리오 요약 -->
    <div class="row mb-4">
        <div class="col-md-3 col-sm-6 mb-3">
            <div class="card summary-card bg-primary text-white">
                <div class="card-body">
                    <h6 class="card-subtitle mb-2">총 투자금액</h6>
                    <h3 class="card-title mb-0" id="totalInvestment">0원</h3>
                </div>
            </div>
        </div>
        <div class="col-md-3 col-sm-6 mb-3">
            <div class="card summary-card bg-success text-white">
                <div class="card-body">
                    <h6 class="card-subtitle mb-2">총 평가금액</h6>
                    <h3 class="card-title mb-0" id="totalValue">0원</h3>
                </div>
            </div>
        </div>
        <div class="col-md-3 col-sm-6 mb-3">
            <div class="card summary-card bg-info text-white">
                <div class="card-body">
                    <h6 class="card-subtitle mb-2">총 손익</h6>
                    <h3 class="card-title mb-0" id="totalProfit">0원</h3>
                </div>
            </div>
        </div>
        <div class="col-md-3 col-sm-6 mb-3">
            <div class="card summary-card bg-warning text-white">
                <div class="card-body">
                    <h6 class="card-subtitle mb-2">총 수익률</h6>
                    <h3 class="card-title mb-0" id="totalReturn">0%</h3>
                </div>
            </div>
        </div>
    </div>
    
    <!-- 종목 추가 버튼 -->
    <div class="mb-3">
        <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addStockModal">
            <i class="bi bi-plus-circle"></i> 종목 추가
        </button>
    </div>
    
    <!-- 포트폴리오 목록 -->
    <div class="card">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>종목명</th>
                            <th class="text-end">보유수량</th>
                            <th class="text-end">평균단가</th>
                            <th class="text-end">현재가</th>
                            <th class="text-end">평가금액</th>
                            <th class="text-end">손익</th>
                            <th class="text-end">수익률</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody id="portfolioList">
                        <tr>
                            <td colspan="8" class="text-center">로딩 중...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
    loadPortfolio();
    setInterval(loadPortfolio, 10000);
});

function loadPortfolio() {
    $.get('${pageContext.request.contextPath}/api/portfolio/list', function(response) {
        if (response.success) {
            displayPortfolio(response.data);
            displaySummary(response.summary);
        }
    });
}

function displayPortfolio(portfolios) {
    if (!portfolios || portfolios.length === 0) {
        $('#portfolioList').html('<tr><td colspan="8" class="text-center">등록된 종목이 없습니다.</td></tr>');
        return;
    }
    
    let html = '';
    portfolios.forEach(function(item) {
        const profitLoss = item.profitLoss || 0;
        const profitRate = item.profitRate || 0;
        const priceClass = profitLoss >= 0 ? 'text-danger' : 'text-primary';
        
        html += '<tr>';
        html += '<td><strong>' + item.stockName + '</strong><br><small class="text-muted">' + item.stockCode + '</small></td>';
        html += '<td class="text-end">' + (item.quantity || 0).toLocaleString() + '</td>';
        html += '<td class="text-end">' + (item.avgPurchasePrice || 0).toLocaleString() + '원</td>';
        html += '<td class="text-end">' + (item.currentPrice || 0).toLocaleString() + '원</td>';
        html += '<td class="text-end">' + ((item.currentPrice || 0) * (item.quantity || 0)).toLocaleString() + '원</td>';
        html += '<td class="text-end ' + priceClass + '">' + (profitLoss >= 0 ? '+' : '') + profitLoss.toLocaleString() + '원</td>';
        html += '<td class="text-end ' + priceClass + '">' + (profitRate >= 0 ? '+' : '') + profitRate.toFixed(2) + '%</td>';
        html += '<td>';
        html += '<button class="btn btn-sm btn-outline-danger" onclick="deleteStock(' + item.portfolioId + ')"><i class="bi bi-trash"></i></button>';
        html += '</td>';
        html += '</tr>';
    });
    
    $('#portfolioList').html(html);
}

function displaySummary(summary) {
    if (!summary) return;
    $('#totalInvestment').text((summary.totalInvestment || 0).toLocaleString() + '원');
    $('#totalValue').text((summary.totalValue || 0).toLocaleString() + '원');
    $('#totalProfit').text(((summary.totalProfit || 0) >= 0 ? '+' : '') + (summary.totalProfit || 0).toLocaleString() + '원');
    $('#totalReturn').text(((summary.totalProfitRate || 0) >= 0 ? '+' : '') + (summary.totalProfitRate || 0).toFixed(2) + '%');
}

function deleteStock(portfolioId) {
    if (!confirm('정말 삭제하시겠습니까?')) return;
    $.post('${pageContext.request.contextPath}/api/portfolio/delete', {portfolioId: portfolioId}, function(response) {
        if (response.success) {
            alert('삭제되었습니다.');
            loadPortfolio();
        }
    });
}
</script>

<jsp:include page="../common/footer.jsp" />
