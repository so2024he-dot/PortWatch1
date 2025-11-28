package com.portwatch.domain;

import java.sql.Timestamp;

/**
 * 뉴스 VO
 * NEWS 테이블 매핑
 */
public class NewsVO {
    private Integer newsId;
    private String newsTitle;
    private String newsContent;
    private String newsSource;
    private String newsUrl;
    private Timestamp newsPubDate;
    private Timestamp newsRegDate;
    
    // 기본 생성자
    public NewsVO() {}
    
    // Getters and Setters
    public Integer getNewsId() {
        return newsId;
    }
    
    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }
    
    public String getNewsTitle() {
        return newsTitle;
    }
    
    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }
    
    public String getNewsContent() {
        return newsContent;
    }
    
    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }
    
    public String getNewsSource() {
        return newsSource;
    }
    
    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }
    
    public String getNewsUrl() {
        return newsUrl;
    }
    
    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }
    
    public Timestamp getNewsPubDate() {
        return newsPubDate;
    }
    
    public void setNewsPubDate(Timestamp newsPubDate) {
        this.newsPubDate = newsPubDate;
    }
    
    public Timestamp getNewsRegDate() {
        return newsRegDate;
    }
    
    public void setNewsRegDate(Timestamp newsRegDate) {
        this.newsRegDate = newsRegDate;
    }
    
    @Override
    public String toString() {
        return "NewsVO{" +
                "newsId=" + newsId +
                ", newsTitle='" + newsTitle + '\'' +
                ", newsSource='" + newsSource + '\'' +
                ", newsPubDate=" + newsPubDate +
                '}';
    }
}
