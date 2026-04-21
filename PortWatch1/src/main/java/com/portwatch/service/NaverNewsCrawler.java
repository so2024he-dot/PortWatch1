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
 * 6. [신규] crawlNewsByKeyword() - 종목명 키워드 뉴스 크롤링 추가
 *
 * @version 3.0 - 종목별 자동 크롤링 추가
 */
@Component
@Log4j
public class NaverNewsCrawler {

    // 네이버 금융 뉴스 URL
    private static final String NAVER_FINANCE_NEWS_URL = "https://finance.naver.com/news/news_list.naver?mode=LSS2D&section_id=101&section_id2=258";
    // 네이버 뉴스 검색 URL (종목명 검색)
    private static final String NAVER_SEARCH_NEWS_URL  = "https://search.naver.com/search.naver?where=news&query=%s+주식&sm=tab_pge&start=1";

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

    /**
     * ✅ [신규] 종목명 키워드로 네이버 뉴스 검색 크롤링
     *
     * @param keyword   종목명 (예: "유한양행", "삼성전자", "McDonald's")
     * @param stockCode DB에 저장할 stock_code (예: "000100", "MCD")
     * @return 크롤링된 뉴스 목록
     */
    public List<NewsVO> crawlNewsByKeyword(String keyword, String stockCode) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🕷️ 종목 키워드 뉴스 크롤링: " + keyword + " (" + stockCode + ")");

        List<NewsVO> result = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            log.warn("⚠️ 키워드가 비어있습니다.");
            return result;
        }

        try {
            String encodedKeyword = java.net.URLEncoder.encode(keyword, "UTF-8");
            String searchUrl = String.format(NAVER_SEARCH_NEWS_URL, encodedKeyword);
            log.info("  - 검색 URL: " + searchUrl);

            Document doc = Jsoup.connect(searchUrl)
                    .timeout(TIMEOUT)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8")
                    .header("Referer", "https://www.naver.com/")
                    .get();

            // 네이버 뉴스 검색 결과 파싱 (PC 검색)
            Elements newsItems = doc.select("div.news_area");
            if (newsItems.isEmpty()) {
                // 모바일/대체 셀렉터 시도
                newsItems = doc.select("li.bx");
            }

            log.info("  - 검색 결과 항목: " + newsItems.size() + "개");

            for (Element item : newsItems) {
                try {
                    // 제목 추출
                    Element titleEl = item.selectFirst("a.news_tit");
                    if (titleEl == null) titleEl = item.selectFirst("a.title");
                    if (titleEl == null) continue;

                    String title = titleEl.text().trim();
                    String newsUrl = titleEl.attr("href");

                    if (title.isEmpty() || newsUrl.isEmpty()) continue;
                    // 외부 URL (네이버 뉴스 외) 도 허용

                    // 출처 추출
                    Element srcEl = item.selectFirst("span.info_group a.info.press");
                    if (srcEl == null) srcEl = item.selectFirst("span.info.press");
                    String source = (srcEl != null) ? srcEl.text().trim() : "네이버뉴스";

                    NewsVO news = new NewsVO();
                    news.setTitle(title);
                    news.setNewsUrl(newsUrl);
                    news.setStockCode(stockCode);      // ✅ 종목 코드 설정!
                    news.setName(source);
                    news.setNewsCol("종목뉴스");
                    news.setPublishedDate(LocalDateTime.now());
                    news.setNewsCode("NAVER_KW_" + System.currentTimeMillis() + "_" + result.size());

                    result.add(news);

                    if (result.size() >= 15) break;  // 최대 15건

                } catch (Exception e) {
                    log.warn("  ⚠️ 뉴스 항목 파싱 실패: " + e.getMessage());
                }
            }

            log.info("✅ 키워드 뉴스 크롤링 완료: " + result.size() + "개");

        } catch (Exception e) {
            log.error("❌ 키워드 뉴스 크롤링 실패 [" + keyword + "]: " + e.getMessage(), e);
        }

        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return result;
    }
}
