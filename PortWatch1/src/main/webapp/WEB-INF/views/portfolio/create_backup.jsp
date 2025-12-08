<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../common/header.jsp" />

<style>
    .create-container {
        max-width: 800px;
        margin: 0 auto;
    }
    
    .create-card {
        background: white;
        border-radius: 20px;
        padding: 2.5rem;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    }
    
    .create-header {
        text-align: center;
        margin-bottom: 2rem;
        padding-bottom: 1.5rem;
        border-bottom: 2px solid #f3f4f6;
    }
    
    .create-icon {
        font-size: 3.5rem;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 1rem;
    }
    
    .create-title {
        font-size: 2rem;
        font-weight: 700;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin: 0;
    }
    
    .form-label {
        font-weight: 600;
        color: #374151;
        margin-bottom: 0.5rem;
    }
    
    .form-control, .form-select {
        border-radius: 10px;
        border: 2px solid #e5e7eb;
        padding: 0.75rem 1rem;
        font-size: 1rem;
        transition: all 0.3s;
    }
    
    .form-control:focus, .form-select:focus {
        border-color: var(--primary-color);
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }
    
    .invalid-feedback {
        display: block;
        color: #dc2626;
        font-size: 0.875rem;
        margin-top: 0.25rem;
    }
    
    .btn-submit {
        width: 100%;
        padding: 1rem;
        font-size: 1.1rem;
        font-weight: 600;
        border-radius: 10px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        color: white;
        transition: all 0.3s;
        margin-top: 1.5rem;
    }
    
    .btn-submit:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }
    
    .btn-cancel {
        width: 100%;
        padding: 1rem;
        font-size: 1.1rem;
        font-weight: 600;
        border-radius: 10px;
        background: #6b7280;
        border: none;
        color: white;
        transition: all 0.3s;
    }
    
    .btn-cancel:hover {
        background: #4b5563;
    }
    
    .info-box {
        background: #f0f9ff;
        border-left: 4px solid #3b82f6;
        padding: 1rem 1.25rem;
        border-radius: 8px;
        margin-bottom: 1.5rem;
    }
    
    .info-box i {
        color: #3b82f6;
        font-size: 1.25rem;
    }
    
    @media (max-width: 576px) {
        .create-card {
            padding: 1.5rem;
        }
        
        .create-title {
            font-size: 1.5rem;
        }
        
        .create-icon {
            font-size: 2.5rem;
        }
    }
</style>

<div class="create-container animate-fade-in">
    <div class="create-card">
        <div class="create-header">
            <div class="create-icon">
                <i class="bi bi-plus-circle"></i>
            </div>
            <h2 class="create-title">종목 추가</h2>
        </div>
        
        <!-- Error Message -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">
                <i class="bi bi-exclamation-circle me-2"></i>${error}
            </div>
        </c:if>
        
        <!-- Info Box -->
        <div class="info-box">
            <i class="bi bi-info-circle me-2"></i>
            <strong>안내:</strong> 포트폴리오에 새로운 종목을 추가합니다. 수익률은 자동으로 계산됩니다.
        </div>
        
        <!-- Form -->
        <form:form action="${pageContext.request.contextPath}/portfolio/create" 
                   method="post" 
                   modelAttribute="portfolioVO">
            
            <div class="mb-4">
                <label for="stockId" class="form-label">
                    <i class="bi bi-search me-2"></i>종목 선택 *
                </label>
                <form:select path="stockId" class="form-select" required="required">
                    <form:option value="">종목을 선택하세요</form:option>
                    <c:forEach var="stock" items="${stockList}">
                        <form:option value="${stock.stock_id}">
                            ${stock.stock_name} (${stock.stock_code}) - 
                            <c:choose>
                                <c:when test="${stock.market_type == 'KOSPI'}">코스피</c:when>
                                <c:when test="${stock.market_type == 'KOSDAQ'}">코스닥</c:when>
                                <c:otherwise>${stock.market_type}</c:otherwise>
                            </c:choose>
                        </form:option>
                    </c:forEach>
                </form:select>
                <form:errors path="stockId" cssClass="invalid-feedback" />
            </div>
            
            <div class="mb-4">
                <label for="quantity" class="form-label">
                    <i class="bi bi-123 me-2"></i>보유 수량 *
                </label>
                <form:input path="quantity" 
                           type="number" 
                           class="form-control" 
                           placeholder="보유 수량을 입력하세요" 
                           min="1"
                           required="required" />
                <form:errors path="quantity" cssClass="invalid-feedback" />
            </div>
            
            <div class="mb-4">
                <label for="avgPurchasePrice" class="form-label">
                    <i class="bi bi-cash me-2"></i>평균 매입가 *
                </label>
                <form:input path="avgPurchasePrice" 
                           type="number" 
                           class="form-control" 
                           placeholder="평균 매입가를 입력하세요 (원)" 
                           min="1"
                           step="1"
                           required="required" />
                <form:errors path="avgPurchasePrice" cssClass="invalid-feedback" />
                <small class="text-muted">예: 50000 (5만원)</small>
            </div>
            
            <div class="mb-4">
                <label for="purchaseDate" class="form-label">
                    <i class="bi bi-calendar me-2"></i>매입 일자 (선택)
                </label>
                <form:input path="purchaseDate" 
                           type="date" 
                           class="form-control" />
                <form:errors path="purchaseDate" cssClass="invalid-feedback" />
            </div>
            
            <div class="row g-3">
                <div class="col-md-6">
                    <a href="${pageContext.request.contextPath}/portfolio/list" 
                       class="btn btn-cancel">
                        <i class="bi bi-x-circle me-2"></i>취소
                    </a>
                </div>
                <div class="col-md-6">
                    <button type="submit" class="btn btn-submit">
                        <i class="bi bi-check-circle me-2"></i>추가하기
                    </button>
                </div>
            </div>
        </form:form>
    </div>
</div>

<script>
// Calculate preview
$('#quantity, #avgPurchasePrice').on('input', function() {
    const quantity = $('#quantity').val() || 0;
    const avgPrice = $('#avgPurchasePrice').val() || 0;
    const total = quantity * avgPrice;
    
    if (total > 0) {
        console.log('총 투자금액:', total.toLocaleString() + '원');
    }
});
</script>

<jsp:include page="../common/footer.jsp" />
