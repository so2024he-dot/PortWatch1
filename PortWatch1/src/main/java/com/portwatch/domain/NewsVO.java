package com.portwatch.domain;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 뉴스 도메인 객체
 * 
 * @author PortWatch
 * @version 1.0
 */
public class NewsVO {
    
    private Long newsId;              // 뉴스 ID (PK)
    private String stockCode;         // 종목 코드
    private String stockName;         // 종목명
    private String title;             // 뉴스 제목
    private String content;           // 뉴스 내용
    private String summary;           // 뉴스 요약
    private String link;              // 뉴스 원문 링크
    private String source;            // 뉴스 출처 (네이버 금융, 다음 금융 등)
    private String imageUrl;          // 이미지 URL
    private Date publishedDate;       // 뉴스 발행일
    private Date createdDate;         // 등록일
    private String sentiment;         // 감성 분석 (긍정/부정/중립)
    
    // 기본 생성자
    public NewsVO() {}
    
    // 전체 생성자
    public NewsVO(String stockCode, String stockName, String title, String content, 
                  String link, String source, Date publishedDate) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.title = title;
        this.content = content;
        this.link = link;
        this.source = source;
        this.publishedDate = publishedDate;
    }
    
    // Getters and Setters
    public Long getNewsId() {
        return newsId;
    }
    
    public void setNewsId(Long newsId) {
        this.newsId = newsId;
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
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
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
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Date getPublishedDate() {
        return publishedDate;
    }
    
    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getSentiment() {
        return sentiment;
    }
    
    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }
    
    @Override
    public String toString() {
        return "NewsVO [newsId=" + newsId + ", stockCode=" + stockCode + ", stockName=" + stockName 
                + ", title=" + title + ", source=" + source + ", publishedDate=" + publishedDate + "]";
    }

	public void setStockId(Integer stockId) {
		// TODO Auto-generated method stub
		
	}

	public void setCountry(String string) {
		// TODO Auto-generated method stub
		
	}

	public void setPublishedAt(Timestamp timestamp) {
		// TODO Auto-generated method stub
		
	}
}
