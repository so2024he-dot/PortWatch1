package com.portwatch.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.portwatch.domain.NewsVO;

import lombok.extern.log4j.Log4j;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * NaverNewsCrawler - 네이버 금융 뉴스 크롤러 (수정 버전)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 수정:
 * 1. setUrl() → setNewsUrl() (NewsVO의 실제 메소드명)
 * 2. setSummary() → newsTitle 필드 활용
 * 3. setSource() → setName() (NewsVO의 실제 메소드명)
 * 4. setCategory() → setNewsCol() (NewsVO의 실제 필드)
 * 5. setPublishedAt() → setPublishedDate() (NewsVO의 실제 메소드명)
 * 
 * @version 2.0 FINAL - NewsVO 실제 필드 반영
 */
@Component
@Log4j
public class NaverNewsCrawler {
    
    // 네이버 금융 뉴스 URL
    private static final String NAVER_FINANCE_NEWS_URL = "https://finance.naver.com/news/news_list.naver?mode=LSS2D&section_id=101&section_id2=258";
    
    // 타임아웃 설정
    private static final int TIMEOUT = 10000; // 10초
    
    /**
     * ✅ 뉴스 크롤링
     */
    public List<NewsVO> crawlNews() {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🕷️ 네이버 금융 뉴스 크롤링 시작");
        log.info("  - URL: " + NAVER_FINANCE_NEWS_URL);
        
        List<NewsVO> newsListResult = new ArrayList<>();
        
        try {
            // HTML 문서 가져오기
            Document doc = Jsoup.connect(NAVER_FINANCE_NEWS_URL)
                    .timeout(TIMEOUT)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();
            
            log.info("✅ HTML 문서 로드 성공");
            
            // 뉴스 목록 선택
            Elements newsElements = doc.select("dd.articleSubject");
            
            log.info("  - 발견된 뉴스 항목: " + newsElements.size() + "개");
            
            for (Element element : newsElements) {
                try {
                    // 제목과 링크 추출
                    Element linkElement = element.selectFirst("a");
                    if (linkElement == null) continue;
                    
                    String title = linkElement.text().trim();
                    String relativeUrl = linkElement.attr("href");
                    String url = "https://finance.naver.com" + relativeUrl;
                    
                    // 요약 추출 (다음 형제 요소에서)
                    Element summaryElement = element.nextElementSibling();
                    String summary = "";
                    if (summaryElement != null && summaryElement.tagName().equals("dd")) {
                        summary = summaryElement.text().trim();
                    }
                    
                    // ✅ NewsVO 생성 - 실제 NewsVO 필드에 맞게 수정!
                    NewsVO news = new NewsVO();
                    
                    // ✅ 1. title 설정 (그대로)
                    news.setTitle(title);
                    
                    // ✅ 2. setUrl() → setNewsUrl() 수정!
                    news.setNewsUrl(url);
                    
                    // ✅ 3. setSummary() → setNewsTitle() 수정! (요약을 newsTitle에 저장)
                    news.setNewsTitle(summary);
                    
                    // ✅ 4. setSource() → setName() 수정!
                    news.setName("네이버금융");
                    
                    // ✅ 5. setCategory() → setNewsCol() 수정!
                    news.setNewsCol("증권");
                    
                    // ✅ 6. setPublishedAt() → setPublishedDate() 수정!
                    news.setPublishedDate(LocalDateTime.now());
                    
                    // ✅ 추가: newsCode 설정
                    news.setNewsCode("NAVER_" + System.currentTimeMillis());
                    
                    newsListResult.add(news);
                    
                    log.debug("  📰 [" + newsListResult.size() + "] " + title);
                    
                } catch (Exception e) {
                    log.warn("  ⚠️ 뉴스 항목 파싱 실패: " + e.getMessage());
                }
            }
            
            log.info("✅ 네이버 금융 뉴스 크롤링 완료: " + newsListResult.size() + "개");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            log.error("❌ 네이버 뉴스 크롤링 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
        
        return newsListResult;
    }
}
