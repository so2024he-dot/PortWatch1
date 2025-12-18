<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>결제 내역 - PortWatch</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">
    <style>
        .payment-history-container {
            max-width: 1200px;
            margin: 50px auto;
            padding: 30px;
        }
        
        .page-header {
            margin-bottom: 30px;
        }
        
        .page-header h2 {
            color: #007bff;
            margin-bottom: 10px;
        }
        
        .summary-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .summary-card {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        
        .summary-card h4 {
            color: #6c757d;
            font-size: 0.9em;
            margin-bottom: 10px;
        }
        
        .summary-card .value {
            font-size: 1.5em;
            font-weight: bold;
            color: #007bff;
        }
        
        .payment-table {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        thead {
            background: #007bff;
            color: white;
        }
        
        th, td {
            padding: 15px;
            text-align: left;
        }
        
        tbody tr:hover {
            background: #f8f9fa;
        }
        
        .status-badge {
            display: inline-block;
            padding: 5px 10px;
            border-radius: 5px;
            font-size: 0.85em;
            font-weight: bold;
        }
        
        .status-completed {
            background: #28a745;
            color: white;
        }
        
        .status-pending {
            background: #ffc107;
            color: #212529;
        }
        
        .status-failed {
            background: #dc3545;
            color: white;
        }
        
        .status-cancelled {
            background: #6c757d;
            color: white;
        }
        
        .country-flag {
            font-size: 1.5em;
            margin-right: 5px;
        }
        
        .cancel-btn {
            padding: 5px 10px;
            background: #dc3545;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 0.9em;
        }
        
        .cancel-btn:hover {
            background: #c82333;
        }
        
        .cancel-btn:disabled {
            background: #6c757d;
            cursor: not-allowed;
        }
        
        .empty-message {
            text-align: center;
            padding: 50px;
            color: #6c757d;
        }
    </style>
</head>
<body>
    <%@ include file="../common/header.jsp" %>
    
    <div class="payment-history-container">
        <div class="page-header">
            <h2>💳 결제 내역</h2>
            <p>주식 구매 결제 내역을 확인할 수 있습니다</p>
        </div>
        
        <!-- 요약 카드 -->
        <div class="summary-cards">
            <div class="summary-card">
                <h4>총 결제 건수</h4>
                <div class="value">${summary.totalPayments}건</div>
            </div>
            
            <div class="summary-card">
                <h4>완료된 결제</h4>
                <div class="value">${summary.completedPayments}건</div>
            </div>
            
            <div class="summary-card">
                <h4>총 결제 금액</h4>
                <div class="value">
                    <fmt:formatNumber value="${summary.totalAmount}" pattern="#,##0" />원
                </div>
            </div>
            
            <div class="summary-card">
                <h4>평균 결제 금액</h4>
                <div class="value">
                    <fmt:formatNumber value="${summary.avgPayment}" pattern="#,##0" />원
                </div>
            </div>
        </div>
        
        <!-- 결제 내역 테이블 -->
        <div class="payment-table">
            <c:if test="${empty payments}">
                <div class="empty-message">
                    <h3>📭 결제 내역이 없습니다</h3>
                    <p>주식을 구매하면 결제 내역이 여기에 표시됩니다.</p>
                </div>
            </c:if>
            
            <c:if test="${not empty payments}">
                <table>
                    <thead>
                        <tr>
                            <th>결제일시</th>
                            <th>종목</th>
                            <th>수량</th>
                            <th>금액</th>
                            <th>결제수단</th>
                            <th>국가</th>
                            <th>상태</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${payments}" var="payment">
                            <tr>
                                <td>
                                    <fmt:formatDate value="${payment.createdAt}" 
                                                   pattern="yyyy-MM-dd HH:mm" />
                                </td>
                                <td>
                                    <strong>${payment.stockName}</strong><br>
                                    <small>${payment.stockCode}</small>
                                </td>
                                <td>
                                    <fmt:formatNumber value="${payment.quantity}" 
                                                     pattern="#,##0.0000" />주
                                </td>
                                <td>
                                    <fmt:formatNumber value="${payment.totalAmount}" 
                                                     pattern="#,##0.00" /> 
                                    ${payment.currency}
                                    <c:if test="${payment.currency != 'KRW'}">
                                        <br>
                                        <small style="color: #6c757d;">
                                            (<fmt:formatNumber value="${payment.localAmount}" 
                                                             pattern="#,##0" />원)
                                        </small>
                                    </c:if>
                                </td>
                                <td>
                                    ${payment.paymentMethod}
                                    <c:if test="${payment.paymentMethod == 'CARD'}">
                                        <br>
                                        <small>${payment.cardCompany}</small>
                                    </c:if>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${payment.country == 'KR'}">
                                            <span class="country-flag">🇰🇷</span> 한국
                                        </c:when>
                                        <c:when test="${payment.country == 'US'}">
                                            <span class="country-flag">🇺🇸</span> 미국
                                        </c:when>
                                        <c:otherwise>
                                            ${payment.country}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${payment.paymentStatus == 'COMPLETED'}">
                                            <span class="status-badge status-completed">완료</span>
                                        </c:when>
                                        <c:when test="${payment.paymentStatus == 'PENDING'}">
                                            <span class="status-badge status-pending">대기</span>
                                        </c:when>
                                        <c:when test="${payment.paymentStatus == 'FAILED'}">
                                            <span class="status-badge status-failed">실패</span>
                                        </c:when>
                                        <c:when test="${payment.paymentStatus == 'CANCELLED'}">
                                            <span class="status-badge status-cancelled">취소</span>
                                        </c:when>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:if test="${payment.paymentStatus == 'COMPLETED' || payment.paymentStatus == 'PENDING'}">
                                        <button class="cancel-btn" 
                                                onclick="cancelPayment(${payment.paymentId})">
                                            취소
                                        </button>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
    </div>
    
    <%@ include file="../common/footer.jsp" %>
    
    <script>
        function cancelPayment(paymentId) {
            if (!confirm('결제를 취소하시겠습니까?\n포트폴리오에서도 함께 삭제됩니다.')) {
                return;
            }
            
            fetch('${pageContext.request.contextPath}/payment/cancel/' + paymentId, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => response.json())
            .then(result => {
                if (result.success) {
                    alert('✅ 결제가 취소되었습니다.');
                    location.reload();
                } else {
                    alert('❌ 결제 취소 실패: ' + result.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('❌ 결제 취소 중 오류가 발생했습니다.');
            });
        }
    </script>
</body>
</html>
