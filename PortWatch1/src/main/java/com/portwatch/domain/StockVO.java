package com.portwatch.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

/**
 * 주식 종목 VO
 * 
 * ✅ 수정사항:
 * 1. 재귀 호출 제거 (getPriceChange, getPriceChangeRate)
 * 2. @Data 어노테이션으로 getter/setter 자동 생성 (중복 제거)
 * 3. 필드명 정리 및 일관성 유지
 * 
 * @author PortWatch
 * @version 3.0 - Spring 5.0.7 + MySQL 8.0.33 호환
 */
@Data
public class StockVO {
    
    // ========================================
    // ✅ 필드 정의
    // ========================================
    
    private Integer stockId;           // 종목 ID (PRIMARY KEY, AUTO_INCREMENT)
    private String stockCode;          // 종목 코드 (005930, AAPL 등)
    private String stockName;          // 종목명 (삼성전자, Apple Inc. 등)
    private String country;            // 국가 (KR, US)
    private String marketType;         // 시장 (KOSPI, KOSDAQ, NASDAQ, NYSE)
    private BigDecimal currentPrice;   // 현재가
    private BigDecimal changeAmount;   // 전일 대비 변동액
    private BigDecimal changeRate;     // 전일 대비 변동률 (%)
    private Long tradingVolume;        // 거래량
    private BigDecimal marketCap;      // 시가총액
    private String sector;             // 섹터 (기술, 금융, 헬스케어 등)
    private String industry;           // 업종 (반도체, 자동차, 소프트웨어 등)
    private Timestamp updatedAt;       // 업데이트 시간
    private String string;
    private StockVO stock;
    // ========================================
    // ✅ 생성자
    // ========================================
    
    /** 기본 생성자 */
    public StockVO() {
    }
    
    /**
     * 주요 정보 생성자
     * @param stockCode 종목 코드
     * @param stockName 종목명
     * @param country 국가
     * @param marketType 시장 유형
     */
    public StockVO(String stockCode, String stockName, String country, String marketType) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.country = country;
        this.marketType = marketType;
    }
    
    // ========================================
    // ✅ 편의 메서드 (재귀 호출 제거!)
    // ========================================
    
    /**
     * 전일 대비 변동액 반환
     * @return changeAmount
     */
    public BigDecimal getPriceChange() {
        return this.changeAmount;  // ✅ 수정: 필드를 직접 반환
    }
    
    /**
     * 전일 대비 변동률 반환
     * @return changeRate
     */
    public BigDecimal getPriceChangeRate() {
        return this.changeRate;  // ✅ 수정: 필드를 직접 반환
    }
    
    /**
     * 가격 상승 여부 확인
     * @return 상승이면 true, 하락 또는 보합이면 false
     */
    public boolean isPriceUp() {
        return changeAmount != null && changeAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 가격 하락 여부 확인
     * @return 하락이면 true, 상승 또는 보합이면 false
     */
    public boolean isPriceDown() {
        return changeAmount != null && changeAmount.compareTo(BigDecimal.ZERO) < 0;
    }
    
    /**
     * 한국 주식 여부 확인
     * @return 한국 주식이면 true
     */
    public boolean isKoreanStock() {
        return "KR".equals(country);
    }
    
    /**
     * 미국 주식 여부 확인
     * @return 미국 주식이면 true
     */
    public boolean isUSStock() {
        return "US".equals(country);
    }
   
    // ========================================
    // ✅ Lombok @Data가 자동 생성하는 메서드:
    // - getter/setter (모든 필드)
    // - toString()
    // - equals() / hashCode()
    // ========================================
    public List<StockVO> selectByMarket(String string) {
		this.string = string;
		return null;
		
    }

	public void update(StockVO stock) {
		this.stock = stock;
		
	}
	
    @Override
	public String toString() {
		return "StockVO [stockId=" + stockId + ", stockCode=" + stockCode + ", stockName=" + stockName + ", country="
				+ country + ", marketType=" + marketType + ", currentPrice=" + currentPrice + ", changeAmount="
				+ changeAmount + ", changeRate=" + changeRate + ", tradingVolume=" + tradingVolume + ", marketCap="
				+ marketCap + ", sector=" + sector + ", industry=" + industry + ", updatedAt=" + updatedAt + ", string="
				+ string + ", stock=" + stock + "]";
	}
}