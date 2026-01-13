package com.portwatch.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.portwatch.domain.NewsVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.StockDAO;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * NaverNewsCrawler - 실제 MySQL 테이블 구조 반영
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 수정:
 * 1. setLink → setNewsUrl (news_url 필드)
 * 2. setSource → setName (name 필드)
 * 3. setCountry → 제거 (테이블에 없음!)
 * 4. setPublishedAt → setPublishedDate (published_date 필드, LocalDateTime)
 * 5. newsCode, newsTitle, newsCol 추가
 * 
 * @author PortWatch
 * @version 11.0 FINAL - 실제 테이블 반영
 */
@Component
public class NaverNewsCrawler implements NewsCrawler {
    
    @Autowired
    private StockDAO stockDAO;
    
    private int maxCount = 10;
    
    @Override
    public List<NewsVO> crawlNews(String stockCode, String stockName) {
        List<NewsVO> newsList = new ArrayList<>();
        
        try {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📰 네이버 금융 뉴스 크롤링 시작");
            System.out.println("  - 종목 코드: " + stockCode);
            System.out.println("  - 종목명: " + stockName);
            
            // 네이버 금융 뉴스 URL
            String naverUrl = "https://finance.naver.com/item/news.naver?code=" + stockCode;
            System.out.println("  - URL: " + naverUrl);
            
            Document doc = Jsoup.connect(naverUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            // 뉴스 목록 선택
            Elements newsItems = doc.select("table.type5 tr");
            
            if (newsItems.isEmpty()) {
                newsItems = doc.select("div.news_area");
            }
            
            System.out.println("  - 발견된 뉴스: " + newsItems.size() + "개");
            
            Integer stockId = getStockId(stockCode);
            int newsIndex = 1;
            
            for (Element item : newsItems) {
                if (newsList.size() >= maxCount) break;
                
                try {
                    Element linkElement = item.selectFirst("a.tit");
                    
                    if (linkElement != null) {
                        String title = linkElement.text();
                        String url = linkElement.attr("abs:href");
                        
                        if (!title.isEmpty() && !url.isEmpty() && url.contains("naver.com")) {
                            NewsVO news = new NewsVO();
                            news.setStockId(stockId);
                            news.setStockCode(stockCode);
                            news.setStockName(stockName);
                            
                            // ✅ 실제 테이블 필드
                            news.setTitle("[" + stockName + "] " + title);
                            news.setNewsTitle(title);
                            news.setNewsUrl(url);  // ✅ setLink → setNewsUrl
                            news.setName("네이버 금융");  // ✅ setSource → setName
                            news.setNewsCode("NAVER" + System.currentTimeMillis() + newsIndex);
                            news.setNewsCol("STOCK_NEWS");
                            
                            // ❌ setCountry 제거! (테이블에 없음)
                            
                            // ✅ setPublishedAt → setPublishedDate (LocalDateTime)
                            news.setPublishedDate(LocalDateTime.now());
                            
                            newsList.add(news);
                            newsIndex++;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("  ⚠️ 뉴스 아이템 파싱 실패: " + e.getMessage());
                }
            }
            
            // 뉴스가 부족하면 검색 결과 추가
            if (newsList.size() < 5) {
                List<NewsVO> searchNews = searchNaverNews(stockCode, stockName, newsIndex);
                newsList.addAll(searchNews);
            }
            
            // maxCount 제한
            if (newsList.size() > maxCount) {
                newsList = newsList.subList(0, maxCount);
            }
            
            System.out.println("✅ 네이버 금융 뉴스 크롤링 완료: " + newsList.size() + "개");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (IOException e) {
            System.err.println("❌ 뉴스 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        return newsList;
    }
    
    /**
     * 네이버 뉴스 검색
     */
    private List<NewsVO> searchNaverNews(String stockCode, String stockName, int startIndex) {
        List<NewsVO> newsList = new ArrayList<>();
        
        try {
            System.out.println("  🔍 네이버 뉴스 검색");
            
            String keyword = URLEncoder.encode(stockName + " 주식", "UTF-8");
            String searchUrl = "https://search.naver.com/search.naver?where=news&query=" + keyword;
            
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Elements newsItems = doc.select("div.news_area");
            Integer stockId = getStockId(stockCode);
            int newsIndex = startIndex;
            
            for (Element item : newsItems) {
                if (newsList.size() >= 5) break;
                
                try {
                    Element titleElement = item.selectFirst("a.news_tit");
                    
                    if (titleElement != null) {
                        String title = titleElement.text();
                        String url = titleElement.attr("abs:href");
                        
                        if (!title.isEmpty() && !url.isEmpty()) {
                            NewsVO news = new NewsVO();
                            news.setStockId(stockId);
                            news.setStockCode(stockCode);
                            news.setStockName(stockName);
                            
                            // ✅ 실제 테이블 필드
                            news.setTitle("[" + stockName + "] " + title);
                            news.setNewsTitle(title);
                            news.setNewsUrl(url);  // ✅ setLink → setNewsUrl
                            news.setName("네이버 뉴스");  // ✅ setSource → setName
                            news.setNewsCode("NAVER_SEARCH" + System.currentTimeMillis() + newsIndex);
                            news.setNewsCol("STOCK_NEWS");
                            
                            // ❌ setCountry 제거!
                            
                            // ✅ setPublishedAt → setPublishedDate
                            news.setPublishedDate(LocalDateTime.now());
                            
                            newsList.add(news);
                            newsIndex++;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("  ⚠️ 검색 결과 파싱 실패: " + e.getMessage());
                }
            }
            
            System.out.println("  ✅ 네이버 뉴스 검색 완료: " + newsList.size() + "개");
            
        } catch (Exception e) {
            System.err.println("  ⚠️ 네이버 뉴스 검색 실패: " + e.getMessage());
        }
        
        return newsList;
    }
    
    /**
     * ✅ 종목 ID 조회 메서드
     */
    private Integer getStockId(String stockCode) {
        try {
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            
            if (stock != null) {
                return stock.getStockId();
            } else {
                System.err.println("⚠️ 종목을 찾을 수 없습니다: " + stockCode);
                return null;
            }
        } catch (Exception e) {
            System.err.println("⚠️ 종목 ID 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public String getCrawlerType() {
        return "KR";
    }
    
    @Override
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
    
    /**
     * ✅ supports 메서드 구현
     */
    @Override
    public boolean supports(String stockCode) {
        // 한국 주식: 숫자 6자리
        return stockCode != null && stockCode.matches("^\\d{6}$");
    }
}
