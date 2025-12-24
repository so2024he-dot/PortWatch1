package com.portwatch.domain;

import java.sql.Timestamp;

import lombok.Data;


/**
 * ✅ 뉴스 VO (완전 구현)
 * 
 * NEWS 테이블과 1:1 매핑
 * 
 * @author PortWatch
 * @version 3.0 - MySQL 8.0 호환
 */
@Data
public class NewsVO {
    
    private Long newsId;              // news_id BIGINT
    private Integer stockId;          // stock_id INT
    private String stockCode;         // stock_code VARCHAR(20)
    private String stockName;         // stock_name VARCHAR(100)
    private String title;             // title VARCHAR(500)
    private String link;              // link VARCHAR(1000)
    private String content;           // content TEXT
    private String source;            // source VARCHAR(100)
    private String country;           // country VARCHAR(10)
    private Timestamp publishedAt;    // published_at TIMESTAMP
    private Timestamp createdAt;      // created_at TIMESTAMP
    private Timestamp updatedAt;      // updated_at TIMESTAMP
    
    // 기본 생성자
    public NewsVO() {
    }
    
    // Getter & Setter
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
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
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
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "NewsVO [newsId=" + newsId + ", stockCode=" + stockCode + ", stockName=" + stockName + 
               ", title=" + title + ", source=" + source + ", country=" + country + 
               ", publishedAt=" + publishedAt + "]";
    }
}
