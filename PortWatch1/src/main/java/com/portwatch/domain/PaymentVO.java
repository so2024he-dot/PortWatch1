package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 결제 정보 VO
 * 포트폴리오 주식 구매 시 결제 정보 관리
 * 
 * @author PortWatch
 * @version 1.0
 */
public class PaymentVO {
    
    // 결제 정보
    private Long paymentId;
    private Integer memberId;
    private Long portfolioId;
    
    // 구매 정보
    private Integer stockId;
    private String stockCode;
    private String stockName;
    private BigDecimal quantity;          // 구매 수량
    private BigDecimal purchasePrice;     // 구매 단가
    private BigDecimal totalAmount;       // 총 구매 금액
    
    // 결제 수단 정보
    private String paymentMethod;    // CARD, BANK_TRANSFER, TOSS, PAYPAL
    private String cardNumber;       // 카드번호 (마스킹)
    private String cardCompany;      // 카드사
    private String bankName;         // 은행명
    
    // 결제 처리 정보
    private String paymentStatus;    // PENDING, COMPLETED, FAILED, CANCELLED
    private String transactionId;    // PG사 거래 ID
    private String pgProvider;       // TOSS, INICIS, KCP, STRIPE, PAYPAL
    
    // 국가 및 통화 정보
    private String country;          // KR, US, JP, etc.
    private String currency;         // KRW, USD, JPY
    private BigDecimal exchangeRate; // 환율 (외화 결제 시)
    private BigDecimal localAmount;  // 현지 통화 금액
    
    // 시간 정보
    private Timestamp createdAt;
    private Timestamp paidAt;
    private Timestamp cancelledAt;
    
    // 추가 정보
    private String memo;             // 메모
    private String receiptUrl;       // 영수증 URL
    
    // 기본 생성자
    public PaymentVO() {}
    
    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    
    public Integer getMemberId() {
        return memberId;
    }
    
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
    
    public Long getPortfolioId() {
        return portfolioId;
    }
    
    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }
    
    public Integer getStockId() {
        return stockId;
    }
    
    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }
    
    public String getStockCode() {
        return stockCode;
    }
    
    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }
    
    public String getStockName() {
        return stockName;
    }
    
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
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
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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
    
    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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
    
    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }
    
    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
    
    public BigDecimal getLocalAmount() {
        return localAmount;
    }
    
    public void setLocalAmount(BigDecimal localAmount) {
        this.localAmount = localAmount;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getPaidAt() {
        return paidAt;
    }
    
    public void setPaidAt(Timestamp paidAt) {
        this.paidAt = paidAt;
    }
    
    public Timestamp getCancelledAt() {
        return cancelledAt;
    }
    
    public void setCancelledAt(Timestamp cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public String getReceiptUrl() {
        return receiptUrl;
    }
    
    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }
    
    @Override
    public String toString() {
        return "PaymentVO{" +
                "paymentId=" + paymentId +
                ", memberId=" + memberId +
                ", stockCode='" + stockCode + '\'' +
                ", quantity=" + quantity +
                ", totalAmount=" + totalAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", country='" + country + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}
