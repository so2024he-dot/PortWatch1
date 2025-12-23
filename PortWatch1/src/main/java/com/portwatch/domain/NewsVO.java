package com.portwatch.domain;

import java.sql.Timestamp;

/**
 * 뉴스 VO
 * 
 * @author PortWatch
 * @version 2.0 - Spring 5.0.7 + MySQL 8.0.33 호환
 */
public class NewsVO {
    
    private Long newsId;            // 뉴스 ID
    private Integer stockId;        // 종목 ID
    private String stockCode;       // 종목 코드
    private String stockName;       // 종목명
    private String title;           // 뉴스 제목
    private String link;            // 뉴스 링크
    private String source;          // 출처 (네이버금융, Yahoo Finance 등)
    private String country;         // 국가 (KR, US)
    private Timestamp publishedAt;  // 게시일
    private Timestamp createdAt;    // 생성일시
    
    // 기본 생성자
    public NewsVO() {
    }
    
    // Getters and Setters
    public Long getNewsId() {
        return newsId;
    }
    
    public void setNewsId(Long newsId) {
        this.newsId = newsId;
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public Timestamp getPublishedAt() {
        return publishedAt;
    }
    
    public void setPublishedAt(Timestamp publishedAt) {
        this.publishedAt = publishedAt;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "NewsVO{" +
                "newsId=" + newsId +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
