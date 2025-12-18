package com.portwatch.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.portwatch.domain.NewsVO;

/**
 * 뉴스 크롤러 유틸리티
 * 네이버 금융에서 종목 뉴스를 크롤링
 * 
 * @author PortWatch
 * @version 1.0
 */
@Component
public class NewsCrawler {
    
    private static final String NAVER_FINANCE_NEWS_URL = "https://finance.naver.com/item/news_news.naver?code=%s&page=1";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
    
    /**
     * 네이버 금융 뉴스 크롤링
     * @param stockCode 종목 코드
     * @param stockName 종목명
     * @return 뉴스 리스트
     */
    public List<NewsVO> crawlNaverFinanceNews(String stockCode, String stockName) {
        List<NewsVO> newsList = new ArrayList<>();
        
        try {
            String url = String.format(NAVER_FINANCE_NEWS_URL, stockCode);
            
            // Jsoup으로 HTML 파싱
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .get();
            
            // 뉴스 목록 선택
            Elements newsItems = doc.select("table.type5 tbody tr");
            
            for (Element item : newsItems) {
                try {
                    // 뉴스 제목 링크
                    Element titleElement = item.select("td.title a").first();
                    if (titleElement == null) continue;
                    
                    String title = titleElement.text().trim();
                    String link = "https://finance.naver.com" + titleElement.attr("href");
                    
                    // 뉴스 출처
                    Element infoElement = item.select("td.info").first();
                    String source = infoElement != null ? infoElement.text().trim() : "네이버 금융";
                    
                    // 뉴스 날짜
                    Element dateElement = item.select("td.date").first();
                    Date publishedDate = parseNewsDate(dateElement != null ? dateElement.text().trim() : "");
                    
                    // 뉴스 객체 생성
                    NewsVO news = new NewsVO();
                    news.setStockCode(stockCode);
                    news.setStockName(stockName);
                    news.setTitle(title);
                    news.setLink(link);
                    news.setSource(source);
                    news.setPublishedDate(publishedDate);
                    
                    // 뉴스 본문 크롤링 (선택적)
                    String content = crawlNewsContent(link);
                    news.setContent(content);
                    news.setSummary(generateSummary(content));
                    
                    newsList.add(news);
                    
                    // 최대 10개까지만 수집
                    if (newsList.size() >= 10) break;
                    
                } catch (Exception e) {
                    System.err.println("  ⚠️ 뉴스 파싱 중 오류: " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            System.err.println("❌ 네이버 금융 크롤링 실패 (" + stockCode + "): " + e.getMessage());
        }
        
        return newsList;
    }
    
    /**
     * 뉴스 본문 크롤링
     * @param newsUrl 뉴스 URL
     * @return 뉴스 본문
     */
    private String crawlNewsContent(String newsUrl) {
        try {
            Document doc = Jsoup.connect(newsUrl)
                    .userAgent(USER_AGENT)
                    .timeout(5000)
                    .get();
            
            // 뉴스 본문 선택 (네이버 뉴스 구조에 맞게 조정 필요)
            Element contentElement = doc.select("div#news_read").first();
            if (contentElement != null) {
                return contentElement.text().trim();
            }
            
            // 대체 선택자
            contentElement = doc.select("div.news_view").first();
            if (contentElement != null) {
                return contentElement.text().trim();
            }
            
            return "";
            
        } catch (Exception e) {
            // 본문 크롤링 실패 시 빈 문자열 반환
            return "";
        }
    }
    
    /**
     * 뉴스 날짜 파싱
     * @param dateStr 날짜 문자열 (예: "2024.12.18")
     * @return Date 객체
     */
    private Date parseNewsDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return new Date();
        }
        
        try {
            // 네이버 금융 날짜 형식: "2024.12.18"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            // 파싱 실패 시 현재 날짜 반환
            return new Date();
        }
    }
    
    /**
     * 뉴스 요약 생성 (간단 버전)
     * @param content 뉴스 본문
     * @return 요약문 (최대 200자)
     */
    private String generateSummary(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        // 간단하게 앞부분 200자만 추출
        if (content.length() > 200) {
            return content.substring(0, 200) + "...";
        }
        
        return content;
    }
    
    /**
     * 다음 금융 뉴스 크롤링 (향후 확장)
     * @param stockCode 종목 코드
     * @param stockName 종목명
     * @return 뉴스 리스트
     */
    public List<NewsVO> crawlDaumFinanceNews(String stockCode, String stockName) {
        // TODO: 다음 금융 크롤링 구현
        return new ArrayList<>();
    }
}

    
