package com.portwatch.service;

import com.portwatch.domain.NewsVO;
import com.portwatch.persistence.NewsDAO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

/**
 * ✅ 뉴스 Service 구현 (완전 버전)
 * 
 * @author PortWatch
 * @version 3.0 - Spring 5.0.7 호환
 */
@Service
public class NewsServiceImpl implements NewsService {
    
    @Autowired
    private NewsDAO newsDAO;
    
    @Override
    public List<NewsVO> getRecentNews(int limit) throws Exception {
        return newsDAO.selectLatestNews(limit);
    }
    
    @Override
    public List<NewsVO> getNewsByStockCode(String stockCode, int limit) throws Exception {
        return newsDAO.selectNewsByStockCode(stockCode, limit);
    }
    
    @Override
    public List<NewsVO> getNewsByStock(String stockCode, int limit) throws Exception {
        return getNewsByStockCode(stockCode, limit);
    }
    
    @Override
    public List<NewsVO> getNewsByCountry(String country, int limit) throws Exception {
        return newsDAO.selectNewsByCountry(country, limit);
    }
    
    @Override
    public NewsVO getNewsById(Long newsId) throws Exception {
        return newsDAO.selectNewsById(newsId);
    }
    
    @Override
    public List<NewsVO> getLatestNews(int limit) throws Exception {
        return getRecentNews(limit);
    }
    
    /**
     * ✅ 네이버 금융 뉴스 크롤링
     */
    @Override
    public List<NewsVO> fetchNaverFinanceNews(int limit) throws Exception {
        List<NewsVO> newsList = new ArrayList<>();
        
        try {
            String url = "https://finance.naver.com/news/news_list.naver?mode=LSS2D&section_id=101&section_id2=258";
            
            System.out.println("📰 네이버 금융 뉴스 크롤링 시작: " + url);
            
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Elements newsElements = doc.select("li.newsList");
            
            if (newsElements.isEmpty()) {
                newsElements = doc.select("div.news_area");
            }
            
            System.out.println("발견된 뉴스: " + newsElements.size() + "개");
            
            for (Element element : newsElements) {
                if (newsList.size() >= limit) break;
                
                try {
                    Element linkElement = element.selectFirst("a");
                    
                    if (linkElement != null) {
                        String title = linkElement.text();
                        String link = linkElement.attr("abs:href");
                        
                        if (!title.isEmpty() && !link.isEmpty()) {
                            NewsVO news = new NewsVO();
                            news.setTitle(title);
                            news.setLink(link);
                            news.setSource("네이버금융");
                            news.setCountry("KR");
                            news.setPublishedAt(new Timestamp(System.currentTimeMillis()));
                            news.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                            
                            newsList.add(news);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("뉴스 파싱 실패: " + e.getMessage());
                }
            }
            
            System.out.println("✅ 크롤링 완료: " + newsList.size() + "개");
            
        } catch (Exception e) {
            System.err.println("❌ 네이버 금융 크롤링 실패: " + e.getMessage());
            throw e;
        }
        
        return newsList;
    }
    
    /**
     * ✅ 뉴스 크롤링 및 DB 저장
     */
    @Override
    @Transactional
    public int crawlAndSaveNews() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 뉴스 크롤링 및 저장 시작");
        
        int savedCount = 0;
        
        try {
            List<NewsVO> crawledNews = fetchNaverFinanceNews(20);
            
            for (NewsVO news : crawledNews) {
                try {
                    // 중복 체크
                    int duplicateCount = newsDAO.checkDuplicateUrl(news.getLink());
                    
                    if (duplicateCount == 0) {
                        newsDAO.insertNews(news);
                        savedCount++;
                    }
                } catch (Exception e) {
                    System.err.println("뉴스 저장 실패: " + e.getMessage());
                }
            }
            
            System.out.println("✅ " + savedCount + "개 뉴스 저장 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 뉴스 크롤링 실패: " + e.getMessage());
            throw e;
        }
        
        return savedCount;
    }
    
    /**
     * ✅ 뉴스 통계
     */
    @Override
    public Map<String, Object> getNewsStats() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            int totalCount = newsDAO.getTotalNewsCount();
            int todayCount = newsDAO.getTodayNewsCount();
            
            stats.put("totalCount", totalCount);
            stats.put("todayCount", todayCount);
            stats.put("success", true);
            
        } catch (Exception e) {
            stats.put("success", false);
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }
}
