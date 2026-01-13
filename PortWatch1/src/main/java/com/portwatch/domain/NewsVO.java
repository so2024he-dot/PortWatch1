package com.portwatch.domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * NewsVO - 실제 MySQL 테이블 구조에 맞춤
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 실제 NEWS 테이블 구조:
 * 1. news_id (BIGINT, PK, AI)
 * 2. title (VARCHAR(500))
 * 3. stock_id (INT)
 * 4. stock_code (VARCHAR(20))
 * 5. news_code (VARCHAR(50))
 * 6. news_title (VARCHAR(500))
 * 7. news_url (VARCHAR(1000))
 * 8. published_date (DATETIME)
 * 9. created_at (TIMESTAMP)
 * 10. newsCol (VARCHAR(45))
 * 11. name (VARCHAR(100))
 * 
 * @author PortWatch
 * @version 4.0 FINAL - MySQL 실제 테이블 반영
 */
@Data
public class NewsVO {
    
    // ✅ 실제 테이블 컬럼들
    private Long newsId;                // news_id BIGINT
    private String title;               // title VARCHAR(500)
    private Integer stockId;            // stock_id INT
    private String stockCode;           // stock_code VARCHAR(20)
    private String newsCode;            // news_code VARCHAR(50)
    private String newsTitle;           // news_title VARCHAR(500)
    private String newsUrl;             // news_url VARCHAR(1000)
    private LocalDateTime publishedDate; // published_date DATETIME
    private Timestamp createdAt;        // created_at TIMESTAMP
    private String newsCol;             // newsCol VARCHAR(45)
    private String name;                // name VARCHAR(100)
    
    // ✅ 추가 필드 (JOIN으로 가져오는 값)
    private String stockName;           // stock 테이블에서 JOIN
    
    // 기본 생성자
    public NewsVO() {
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Getter & Setter
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    public Long getNewsId() {
        return newsId;
    }
    
    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
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
    
    public String getNewsCode() {
        return newsCode;
    }
    
    public void setNewsCode(String newsCode) {
        this.newsCode = newsCode;
    }
    
    public String getNewsTitle() {
        return newsTitle;
    }
    
    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }
    
    public String getNewsUrl() {
        return newsUrl;
    }
    
    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }
    
    public LocalDateTime getPublishedDate() {
        return publishedDate;
    }
    
    public void setPublishedDate(LocalDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getNewsCol() {
        return newsCol;
    }
    
    public void setNewsCol(String newsCol) {
        this.newsCol = newsCol;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getStockName() {
        return stockName;
    }
    
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
    
    @Override
    public String toString() {
        return "NewsVO [newsId=" + newsId + 
               ", title=" + title + 
               ", stockCode=" + stockCode + 
               ", stockName=" + stockName + 
               ", newsTitle=" + newsTitle + 
               ", newsUrl=" + newsUrl + 
               ", publishedDate=" + publishedDate + 
               ", name=" + name + 
               ", newsCode=" + newsCode + "]";
    }
}
