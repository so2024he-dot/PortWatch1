package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 포트폴리오 아이템 VO
 * 
 * PortfolioServiceImpl.addStockToPortfolio(PortfolioItemVO) 전용
 * 
 * @author PortWatch
 * @version 2.0 - Spring 5.0.7 + MySQL 8.0.33 호환
 */
@Data
public class PortfolioItemVO {
    
    private String memberId;           // 회원 ID
    private String stockCode;          // 종목 코드
    private BigDecimal quantity;       // 수량
    private BigDecimal price;          // 가격
    private BigDecimal purchasePrice;  // 현재가 
    private Timestamp purchaseDate;    // 현재시간
    private int StockId;               // 
    
    // 기본 생성자
    public PortfolioItemVO() {
    }
    
    // 전체 생성자
    public PortfolioItemVO(String memberId, String stockCode, BigDecimal quantity, BigDecimal price) {
        this.memberId = memberId;
        this.stockCode = stockCode;
        this.quantity = quantity;
        this.price = price;
    }
    
    // Getters and Setters
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public String getStockCode() {
        return stockCode;
    }
    
    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public void setPurchaseDate() {
    	this.setPurchaseDate();;
    }   
    
    // 현재가 메서드 추가
	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	
	}

	@Override
	public String toString() {
		return "PortfolioItemVO [memberId=" + memberId + ", stockCode=" + stockCode + ", quantity=" + quantity
				+ ", price=" + price + ", purchasePrice=" + purchasePrice + "]";
	}

	
	public BigDecimal getAvgPurchasePrice() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
