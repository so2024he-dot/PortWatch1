package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 결제 VO
 * 
 * ✅ String memberId 사용
 * @author PortWatch
 * @version 2.0 - Spring 5.0.7 + MySQL 8.0.33 호환
 */
@Data
public class PaymentVO {
    
    private Long paymentId;              // 결제 ID
    private String memberId;             // 회원 ID (String)
    private Integer stockId;             // 종목 ID
    private String stockCode;            // 종목 코드
    private String stockName;            // 종목명
    private BigDecimal quantity;         // 수량
    private BigDecimal purchasePrice;    // 매입 가격
    private BigDecimal totalAmount;      // 총 결제 금액
    private String paymentMethod;        // 결제 수단 (CARD, BANK_TRANSFER)
    private String paymentStatus;        // 결제 상태 (PENDING, COMPLETED, CANCELLED)
    private String cardNumber;           // 카드 번호 (마스킹)
    private String cardCompany;          // 카드사
    private String pgProvider;           // PG사 (TOSS, STRIPE, INICIS)
    private String transactionId;        // 거래 ID (PG사 거래번호)
    private Long portfolioId;            // 연결된 포트폴리오 ID
    private String country;              // 국가 (KR, US)
    private String currency;             // 통화 (KRW, USD)
    private BigDecimal exchangeRate;     // 환율
    private BigDecimal localAmount;      // 원화 환산 금액
    private Timestamp createdAt;         // 생성일시
    private Timestamp updatedAt;         // 수정일시
    
    // 기본 생성자
    public PaymentVO() {
    }
    
    // Getters and Setters
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
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
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
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public Long getPortfolioId() {
        return portfolioId;
    }
    
    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
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
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "PaymentVO{" +
                "paymentId=" + paymentId +
                ", memberId='" + memberId + '\'' +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", quantity=" + quantity +
                ", totalAmount=" + totalAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}
