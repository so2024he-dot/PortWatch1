package com.portwatch.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 결제 정보 VO (완전판)
 * PaymentController에서 필요한 모든 setter 포함
 */
public class PaymentVO {
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 기본 정보
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private Long paymentId;              // 결제 ID
    private String memberId;             // 회원 ID
    private BigDecimal amount;           // 결제 금액
    private String paymentMethod;        // 결제 수단
    private String paymentStatus;        // 결제 상태
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ⭐ PaymentController에서 필요한 추가 필드
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private BigDecimal purchasePrice;    // 구매 가격
    private BigDecimal totalAmount;      // 총 금액
    private String cardNumber;           // 카드 번호
    private String cardCompany;          // 카드 회사
    private String pgProvider;           // PG사
    private String country;              // 국가
    private String currency;             // 통화
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 결제 세부 정보
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private String paymentKey;           // 결제 고유 키
    private String orderId;              // 주문 ID
    private String orderName;            // 주문명
    private String customerEmail;        // 고객 이메일
    private String customerName;         // 고객 이름
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 날짜 정보
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private Date requestedAt;            // 결제 요청 시간
    private Date approvedAt;             // 결제 승인 시간
    private Date cancelledAt;            // 결제 취소 시간
    private String cancelReason;         // 취소 사유
    private String failReason;           // 실패 사유
    private Date createdAt;              // 생성 시간
    private Date updatedAt;              // 수정 시간
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Constructors
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    public PaymentVO() {
    }
    
    public PaymentVO(String memberId, BigDecimal amount, String paymentMethod) {
        this.memberId = memberId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = "PENDING";
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Getters and Setters
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    // 기본 정보
    public Long getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    // ⭐ PaymentController 필수 Setters
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }
    
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    public String getCardCompany() {
        return cardCompany;
    }
    
    public void setCardCompany(String cardCompany) {
        this.cardCompany = cardCompany;
    }
    
    public String getPgProvider() {
        return pgProvider;
    }
    
    public void setPgProvider(String pgProvider) {
        this.pgProvider = pgProvider;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    // 결제 세부 정보
    public String getPaymentKey() {
        return paymentKey;
    }
    
    public void setPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderName() {
        return orderName;
    }
    
    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    // 날짜 정보
    public Date getRequestedAt() {
        return requestedAt;
    }
    
    public void setRequestedAt(Date requestedAt) {
        this.requestedAt = requestedAt;
    }
    
    public Date getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(Date approvedAt) {
        this.approvedAt = approvedAt;
    }
    
    public Date getCancelledAt() {
        return cancelledAt;
    }
    
    public void setCancelledAt(Date cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
    
    public String getCancelReason() {
        return cancelReason;
    }
    
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
    
    public String getFailReason() {
        return failReason;
    }
    
    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Utility Methods
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    public void completePayment(String paymentKey) {
        this.paymentKey = paymentKey;
        this.paymentStatus = "COMPLETED";
        this.approvedAt = new Date();
    }
    
    public void failPayment(String reason) {
        this.paymentStatus = "FAILED";
        this.failReason = reason;
    }
    
    public void cancelPayment(String reason) {
        this.paymentStatus = "CANCELLED";
        this.cancelReason = reason;
        this.cancelledAt = new Date();
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(this.paymentStatus);
    }
    
    public boolean isPending() {
        return "PENDING".equals(this.paymentStatus);
    }
    
    @Override
    public String toString() {
        return "PaymentVO{" +
                "paymentId=" + paymentId +
                ", memberId='" + memberId + '\'' +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", totalAmount=" + totalAmount +
                ", cardCompany='" + cardCompany + '\'' +
                ", country='" + country + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}
