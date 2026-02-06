package com.portwatch.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 결제 정보 VO (완전판)
 */
public class PaymentVO {
    
    // 기본 정보
    private Long paymentId;
    private String memberId;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    
    // ⭐ PaymentController 필수 필드
    private Integer stockId;
    private String stockCode;
    private String stockName;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private BigDecimal totalAmount;
    
    // 카드 정보
    private String cardNumber;
    private String cardCompany;
    private String pgProvider;
    private String country;
    private String currency;
    
    // 결제 세부
    private String paymentKey;
    private String orderId;
    private String orderName;
    private String customerEmail;
    private String customerName;
    private String transactionId;
    
    // 날짜
    private Date requestedAt;
    private Date approvedAt;
    private Date cancelledAt;
    private String cancelReason;
    private String failReason;
    private Date createdAt;
    private Date updatedAt;
    
    // Constructors
    public PaymentVO() {
        this.paymentStatus = "PENDING";
    }
    
    // Getters and Setters
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    // ⭐ PaymentController 필수
    public Integer getStockId() { return stockId; }
    public void setStockId(Integer stockId) { this.stockId = stockId; }
    
    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }
    
    public String getStockName() { return stockName; }
    public void setStockName(String stockName) { this.stockName = stockName; }
    
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    // 카드 정보
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    
    public String getCardCompany() { return cardCompany; }
    public void setCardCompany(String cardCompany) { this.cardCompany = cardCompany; }
    
    public String getPgProvider() { return pgProvider; }
    public void setPgProvider(String pgProvider) { this.pgProvider = pgProvider; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    // 결제 세부
    public String getPaymentKey() { return paymentKey; }
    public void setPaymentKey(String paymentKey) { this.paymentKey = paymentKey; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getOrderName() { return orderName; }
    public void setOrderName(String orderName) { this.orderName = orderName; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    // 날짜
    public Date getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Date requestedAt) { this.requestedAt = requestedAt; }
    
    public Date getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Date approvedAt) { this.approvedAt = approvedAt; }
    
    public Date getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Date cancelledAt) { this.cancelledAt = cancelledAt; }
    
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    
    public String getFailReason() { return failReason; }
    public void setFailReason(String failReason) { this.failReason = failReason; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
