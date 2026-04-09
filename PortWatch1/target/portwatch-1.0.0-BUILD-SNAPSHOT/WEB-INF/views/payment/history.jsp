<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>💳 결제 내역 - PortWatch</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <style>
        body {
            background-color: #f8f9fa;
        }
        
        .payment-header {
            background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
            color: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        
        .payment-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 15px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            transition: all 0.3s;
        }
        
        .payment-card:hover {
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        
        .status-completed {
            background: #28a745;
            color: white;
            padding: 5px 15px;
            border-radius: 15px;
            font-size: 0.85em;
        }
        
        .status-pending {
            background: #ffc107;
            color: white;
            padding: 5px 15px;
            border-radius: 15px;
            font-size: 0.85em;
        }
        
        .status-cancelled {
            background: #dc3545;
            color: white;
            padding: 5px 15px;
            border-radius: 15px;
            font-size: 0.85em;
        }
        
        .empty-payment {
            text-align: center;
            padding: 60px 20px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .empty-payment i {
            font-size: 80px;
            color: #ccc;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="/">📊 PortWatch</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="/stock/list">주식 목록</a></li>
                <li class="nav-item"><a class="nav-link" href="/portfolio">포트폴리오</a></li>
                <li class="nav-item"><a class="nav-link" href="/watchlist">관심종목</a></li>
                <li class="nav-item"><a class="nav-link active" href="/payment/history">결제 내역</a></li>
                <li class="nav-item"><a class="nav-link" href="/member/logout">로그아웃</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container mt-4">
    
    <!-- 결제 내역 헤더 -->
    <div class="payment-header">
        <h2><i class="fas fa-credit-card"></i> 결제 내역</h2>
        <p class="mb-0">주식 매입 및 결제 내역을 확인하세요</p>
    </div>
    
    <!-- 결제 내역 목록 -->
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4><i class="fas fa-list"></i> 전체 결제 내역 (${paymentList.size()}건)</h4>
    </div>
    
    <c:choose>
        <c:when test="${empty paymentList}">
            <!-- 빈 결제 내역 -->
            <div class="empty-payment">
                <i class="fas fa-receipt"></i>
                <h4 class="text-muted">결제 내역이 없습니다</h4>
                <p class="text-muted mb-4">주식을 매입하면 결제 내역이 표시됩니다</p>
                <a href="/stock/list" class="btn btn-primary">
                    <i class="fas fa-shopping-cart"></i> 주식 매입하기
                </a>
            </div>
        </c:when>
        
        <c:otherwise>
            <!-- 결제 카드 목록 -->
            <c:forEach items="${paymentList}" var="payment">
                <div class="payment-card">
                    <div class="row align-items-center">
                        <div class="col-md-2">
                            <small class="text-muted">결제일</small>
                            <h6><fmt:formatDate value="${payment.createdAt}" pattern="yyyy-MM-dd"/></h6>
                            <small class="text-muted"><fmt:formatDate value="${payment.createdAt}" pattern="HH:mm:ss"/></small>
                        </div>
                        <div class="col-md-3">
                            <small class="text-muted">종목</small>
                            <h6>${payment.stockName}</h6>
                            <small class="text-muted">${payment.stockCode}</small>
                        </div>
                        <div class="col-md-2">
                            <small class="text-muted">수량 / 단가</small>
                            <h6>
                                <fmt:formatNumber value="${payment.quantity}" pattern="#,###"/>주 / 
                                <fmt:formatNumber value="${payment.purchasePrice}" pattern="#,###"/>원
                            </h6>
                        </div>
                        <div class="col-md-2">
                            <small class="text-muted">총 결제금액</small>
                            <h5 class="text-primary">
                                <fmt:formatNumber value="${payment.totalAmount}" pattern="#,###"/>원
                            </h5>
                        </div>
                        <div class="col-md-2">
                            <small class="text-muted">결제 수단</small>
                            <h6>${payment.paymentMethod}</h6>
                        </div>
                        <div class="col-md-1 text-end">
                            <c:choose>
                                <c:when test="${payment.paymentStatus == 'COMPLETED'}">
                                    <span class="status-completed">완료</span>
                                </c:when>
                                <c:when test="${payment.paymentStatus == 'PENDING'}">
                                    <span class="status-pending">대기</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-cancelled">취소</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    
                    <!-- 상세 정보 (접을 수 있음) -->
                    <div class="collapse mt-3" id="detail${payment.paymentId}">
                        <hr>
                        <div class="row">
                            <div class="col-md-4">
                                <small class="text-muted">결제 ID</small>
                                <p>${payment.paymentId}</p>
                            </div>
                            <div class="col-md-4">
                                <small class="text-muted">거래 ID</small>
                                <p>${payment.transactionId}</p>
                            </div>
                            <div class="col-md-4">
                                <small class="text-muted">포트폴리오 ID</small>
                                <p>${payment.portfolioId}</p>
                            </div>
                        </div>
                        <c:if test="${payment.paymentStatus == 'COMPLETED'}">
                            <button class="btn btn-sm btn-danger" onclick="cancelPayment('${payment.paymentId}')">
                                <i class="fas fa-times"></i> 결제 취소
                            </button>
                        </c:if>
                    </div>
                    
                    <!-- 상세 보기 토글 -->
                    <div class="text-end mt-2">
                        <button class="btn btn-sm btn-outline-secondary" 
                                data-bs-toggle="collapse" 
                                data-bs-target="#detail${payment.paymentId}">
                            <i class="fas fa-chevron-down"></i> 상세 보기
                        </button>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
    
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // 결제 취소
    function cancelPayment(paymentId) {
        if (confirm('정말 결제를 취소하시겠습니까?\n포트폴리오에서도 삭제됩니다.')) {
            fetch('/payment/cancel/' + paymentId, {
                method: 'POST'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('결제가 취소되었습니다.');
                    location.reload();
                } else {
                    alert('취소 실패: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('취소 중 오류가 발생했습니다.');
            });
        }
    }
</script>

</body>
</html>
