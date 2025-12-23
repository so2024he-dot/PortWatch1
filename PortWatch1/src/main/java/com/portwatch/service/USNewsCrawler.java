package com.portwatch.service;

import com.portwatch.domain.NewsVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * ✅ 미국 주식 뉴스 크롤러 (완전 구현)
 * 
 * 출처: Yahoo Finance, MarketWatch, Bloomberg
 * 
 * @author PortWatch
 * @version 3.0 - Spring 5.0.7 호환
 */
@Component("usNewsCrawler")
public class USNewsCrawler implements NewsCrawler {
    
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
    private int maxCount = 10;
    
    /**
     * ✅ 미국 주식 뉴스 크롤링
     */
    @Override
    public List<NewsVO> crawlNews(String stockCode, String stockName) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🇺🇸 미국 주식 뉴스 크롤링 시작");
        System.out.println("  종목 코드: " + stockCode);
        System.out.println("  종목명: " + stockName);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        List<NewsVO> newsList = new ArrayList<>();
        
        try {
            // Yahoo Finance에서 뉴스 크롤링
            List<NewsVO> yahooNews = crawlYahooFinance(stockCode, stockName);
            newsList.addAll(yahooNews);
            
            // MarketWatch에서 뉴스 크롤링 (추가)
            if (newsList.size() < maxCount) {
                List<NewsVO> marketWatchNews = crawlMarketWatch(stockCode, stockName);
                newsList.addAll(marketWatchNews);
            }
            
            System.out.println("✅ 총 " + newsList.size() + "개 뉴스 크롤링 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 뉴스 크롤링 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
        
        return newsList.size() > maxCount ? newsList.subList(0, maxCount) : newsList;
    }
    
    /**
     * Yahoo Finance 뉴스 크롤링
     */
    private List<NewsVO> crawlYahooFinance(String stockCode, String stockName) {
        List<NewsVO> newsList = new ArrayList<>();
        
        try {
            String url = "https://finance.yahoo.com/quote/" + stockCode + "/news";
            
            System.out.println("📰 Yahoo Finance 크롤링: " + url);
            
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .get();
            
            // 뉴스 항목 선택
            Elements newsElements = doc.select("div.Mb\\(5px\\) h3 a");
            
            if (newsElements.isEmpty()) {
                // 대체 선택자 시도
                newsElements = doc.select("div.js-stream-content h3 a");
            }
            
            if (newsElements.isEmpty()) {
                // 두 번째 대체 선택자
                newsElements = doc.select("a[data-test-locator='mega-item-header']");
            }
            
            System.out.println("  → " + newsElements.size() + "개 뉴스 발견");
            
            int count = 0;
            for (Element element : newsElements) {
                if (count >= maxCount) break;
                
                try {
                    String title = element.text();
                    String link = element.attr("abs:href");
                    
                    if (title != null && !title.isEmpty() && link != null && !link.isEmpty()) {
                        NewsVO news = new NewsVO();
                        news.setStockCode(stockCode);
                        news.setStockName(stockName);
                        news.setTitle(title);
                        news.setLink(link);
                        news.setSource("Yahoo Finance");
                        news.setCountry("US");
                        news.setPublishedAt(new Timestamp(System.currentTimeMillis()));
                        news.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                        
                        newsList.add(news);
                        count++;
                        
                        System.out.println("  ✅ " + count + ". " + title);
                    }
                } catch (Exception e) {
                    System.err.println("  ⚠️ 뉴스 파싱 실패: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Yahoo Finance 크롤링 실패: " + e.getMessage());
        }
        
        return newsList;
    }
    
    /**
     * MarketWatch 뉴스 크롤링
     */
    private List<NewsVO> crawlMarketWatch(String stockCode, String stockName) {
        List<NewsVO> newsList = new ArrayList<>();
        
        try {
            String url = "https://www.marketwatch.com/investing/stock/" + stockCode.toLowerCase();
            
            System.out.println("📰 MarketWatch 크롤링: " + url);
            
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .get();
            
            // 뉴스 항목 선택
            Elements newsElements = doc.select("div.element--article h3 a");
            
            if (newsElements.isEmpty()) {
                // 대체 선택자
                newsElements = doc.select("div.article__content a.link");
            }
            
            System.out.println("  → " + newsElements.size() + "개 뉴스 발견");
            
            int count = 0;
            for (Element element : newsElements) {
                if (count >= 5) break; // MarketWatch는 최대 5개만
                
                try {
                    String title = element.text();
                    String link = element.attr("abs:href");
                    
                    if (title != null && !title.isEmpty() && link != null && !link.isEmpty()) {
                        NewsVO news = new NewsVO();
                        news.setStockCode(stockCode);
                        news.setStockName(stockName);
                        news.setTitle(title);
                        news.setLink(link);
                        news.setSource("MarketWatch");
                        news.setCountry("US");
                        news.setPublishedAt(new Timestamp(System.currentTimeMillis()));
                        news.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                        
                        newsList.add(news);
                        count++;
                        
                        System.out.println("  ✅ " + count + ". " + title);
                    }
                } catch (Exception e) {
                    System.err.println("  ⚠️ 뉴스 파싱 실패: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ MarketWatch 크롤링 실패: " + e.getMessage());
        }
        
        return newsList;
    }
    
    /**
     * 크롤러 타입 반환
     */
    @Override
    public String getCrawlerType() {
        return "US";
    }
    
    /**
     * 최대 뉴스 개수 설정
     */
    @Override
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
    
    /**
     * 특정 종목 코드 지원 여부 확인
     */
    @Override
    public boolean supports(String stockCode) {
        // 미국 주식은 알파벳 대문자로 구성
        return stockCode != null && stockCode.matches("[A-Z]+");
    }
}
