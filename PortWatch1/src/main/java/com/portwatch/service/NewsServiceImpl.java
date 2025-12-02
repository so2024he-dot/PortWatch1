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
import java.util.ArrayList;
import java.util.List;

/**
 * 뉴스 서비스 구현
 * Jsoup을 사용한 네이버 금융 뉴스 크롤링
 */
@Service
public class NewsServiceImpl implements NewsService {
    
    @Autowired
    private NewsDAO newsDAO;
    
    /**
     * 최신 뉴스 조회
     */
    @Override
    public List<NewsVO> getLatestNews(int limit) throws Exception {
        return newsDAO.selectLatestNews(limit);
    }
    
    /**
     * 특정 종목 관련 뉴스 조회
     */
    @Override
    public List<NewsVO> getNewsByStock(String stockCode, int limit) throws Exception {
        return newsDAO.selectNewsByStock(stockCode, limit);
    }
    
    /**
     * 네이버 금융 뉴스 크롤링
     */
    @Override
    public List<NewsVO> fetchNaverFinanceNews(int limit) throws Exception {
        List<NewsVO> newsList = new ArrayList<>();
        
        try {
            // 네이버 금융 증권 뉴스 페이지
            String url = "https://finance.naver.com/news/news_list.naver?mode=LSS2D&section_id=101&section_id2=258";
            
            // Jsoup으로 HTML 파싱
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(5000)
                    .get();
            
            // 뉴스 목록 추출
            Elements newsElements = doc.select("ul.newsList > li");
            
            int count = 0;
            for (Element element : newsElements) {
                if (count >= limit) break;
                
                try {
                    // 뉴스 제목과 링크
                    Element titleElement = element.selectFirst("a.tit");
                    if (titleElement == null) continue;
                    
                    String title = titleElement.text();
                    String newsUrl = "https://finance.naver.com" + titleElement.attr("href");
                    
                    // 뉴스 요약
                    Element summaryElement = element.selectFirst("p.desc");
                    String summary = summaryElement != null ? summaryElement.text() : "";
                    
                    // 언론사
                    Element sourceElement = element.selectFirst("span.press");
                    String source = sourceElement != null ? sourceElement.text() : "네이버금융";
                    
                    // 날짜
                    Element dateElement = element.selectFirst("span.date");
                    String dateStr = dateElement != null ? dateElement.text() : "";
                    
                    // NewsVO 생성
                    NewsVO news = new NewsVO();
                    news.setNewsTitle(title);
                    news.setNewsContent(summary);
                    news.setNewsSource(source);
                    news.setNewsUrl(newsUrl);
                    news.setNewsPubDate(new Timestamp(System.currentTimeMillis()));
                    news.setNewsRegDate(new Timestamp(System.currentTimeMillis()));
                    
                    newsList.add(news);
                    count++;
                    
                } catch (Exception e) {
                    System.err.println("뉴스 파싱 오류: " + e.getMessage());
                    continue;
                }
            }
            
            System.out.println("✅ 네이버 금융 뉴스 " + newsList.size() + "개 수집 완료");
            
        } catch (Exception e) {
            System.err.println("❌ 네이버 금융 뉴스 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        return newsList;
    }
    
    /**
     * 뉴스 저장
     */
    @Override
    @Transactional
    public void saveNews(NewsVO news) throws Exception {
        newsDAO.insertNews(news);
    }
    
    /**
     * 뉴스 ID로 조회
     */
    @Override
    public NewsVO getNewsById(int newsId) throws Exception {
        return newsDAO.selectNewsById(newsId);
    }
}

    
