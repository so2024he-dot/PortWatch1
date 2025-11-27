<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="container mt-4">
    <div class="row mb-4">
        <div class="col">
            <h2><i class="bi bi-pencil-square"></i> 포트폴리오 수정</h2>
        </div>
    </div>
    
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <div class="card">
        <div class="card-body">
            <form:form method="post" action="${pageContext.request.contextPath}/portfolio/edit" 
                       modelAttribute="portfolioVO">
                
                <!-- Hidden Fields -->
                <form:hidden path="portfolioId" />
                <form:hidden path="memberId" />
                <form:hidden path="stockId" />
                
                <!-- 종목 정보 (읽기 전용) -->
                <div class="mb-3">
                    <label class="form-label">종목</label>
                    <div class="form-control bg-light" readonly>
                        ${portfolioVO.stockName} (${portfolioVO.stockCode})
                    </div>
                    <small class="form-text text-muted">종목은 수정할 수 없습니다. 종목을 변경하려면 삭제 후 다시 추가하세요.</small>
                </div>
                
                <!-- 보유 수량 -->
                <div class="mb-3">
                    <label for="quantity" class="form-label">보유 수량 <span class="text-danger">*</span></label>
                    <form:input path="quantity" type="number" class="form-control" 
                                placeholder="예: 100" min="1" required="true" />
                    <form:errors path="quantity" cssClass="text-danger small" />
                </div>
                
                <!-- 평균 매입가 -->
                <div class="mb-3">
                    <label for="avgPurchasePrice" class="form-label">평균 매입가 <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <form:input path="avgPurchasePrice" type="number" class="form-control" 
                                    placeholder="예: 50000" min="0" step="0.01" required="true" />
                        <span class="input-group-text">원</span>
                    </div>
                    <form:errors path="avgPurchasePrice" cssClass="text-danger small" />
                </div>
                
                <!-- 매입일 -->
                <div class="mb-3">
                    <label for="purchaseDate" class="form-label">매입일 <span class="text-danger">*</span></label>
                    <form:input path="purchaseDate" type="date" class="form-control" required="true" />
                    <form:errors path="purchaseDate" cssClass="text-danger small" />
                </div>
                
                <!-- 투자 금액 미리보기 -->
                <div class="alert alert-info" id="investmentPreview" style="display: none;">
                    <strong>투자 금액:</strong> <span id="totalInvestment">0</span>원
                </div>
                
                <!-- 버튼 -->
                <div class="d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/portfolio/list" class="btn btn-secondary">
                        <i class="bi bi-arrow-left"></i> 취소
                    </a>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-check-circle"></i> 수정하기
                    </button>
                </div>
            </form:form>
        </div>
    </div>
</div>

<script>
// 투자 금액 미리보기
document.addEventListener('DOMContentLoaded', function() {
    const quantityInput = document.getElementById('quantity');
    const priceInput = document.getElementById('avgPurchasePrice');
    const preview = document.getElementById('investmentPreview');
    const totalInvestment = document.getElementById('totalInvestment');
    
    function updatePreview() {
        const quantity = parseFloat(quantityInput.value) || 0;
        const price = parseFloat(priceInput.value) || 0;
        const total = quantity * price;
        
        if (total > 0) {
            totalInvestment.textContent = total.toLocaleString('ko-KR');
            preview.style.display = 'block';
        } else {
            preview.style.display = 'none';
        }
    }
    
    quantityInput.addEventListener('input', updatePreview);
    priceInput.addEventListener('input', updatePreview);
    
    // 초기 계산
    updatePreview();
});
</script>

<jsp:include page="../common/footer.jsp" />
