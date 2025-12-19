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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * ✅ 최종 수정: USNewsCrawler.java
 * 
 * 수정 사항:
 * 1. getStockId() 메서드 - StockVO 타입으로 정확히 수정
 * 2. supports() 메서드 구현
 * 3. 느슨한 결합을 위한 인터페이스 구현
 * 
 * @author PortWatch
 * @version 10.0 - 타입 안정성 완료
 */
@Component
public class USNewsCrawler implements NewsCrawler {
    
    @Autowired
    private StockDAO stockDAO;
    
    private int maxCount = 10;
    
    @Override
    public List<NewsVO> crawlNews(String stockCode, String stockName) {
        List<NewsVO> newsList = new ArrayList<>();
        
        try {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📰 미국 주식 뉴스 크롤링 시작");
            System.out.println("  - 종목 코드: " + stockCode);
            System.out.println("  - 종목명: " + stockName);
            
            // Yahoo Finance 크롤링
            List<NewsVO> yahooNews = crawlYahooFinance(stockCode, stockName);
            newsList.addAll(yahooNews);
            
            // 뉴스가 부족하면 Google Finance 추가
            if (newsList.size() < 5) {
                List<NewsVO> googleNews = crawlGoogleFinance(stockCode, stockName);
                newsList.addAll(googleNews);
            }
            
            // maxCount 제한
            if (newsList.size() > maxCount) {
                newsList = newsList.subList(0, maxCount);
            }
            
            System.out.println("✅ 미국 주식 뉴스 크롤링 완료: " + newsList.size() + "개");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 뉴스 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        return newsList;
    }
    
    /**
     * Yahoo Finance 크롤링
     */
    private List<NewsVO> crawlYahooFinance(String stockCode, String stockName) {
        List<NewsVO> newsList = new ArrayList<>();
        
        try {
            String yahooUrl = "https://finance.yahoo.com/quote/" + stockCode + "/news";
            System.out.println("  - Yahoo Finance URL: " + yahooUrl);
            
            Document doc = Jsoup.connect(yahooUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            // 뉴스 아이템 선택
            Elements newsItems = doc.select("div.Ov\\(h\\) > a");
            
            if (newsItems.isEmpty()) {
                newsItems = doc.select("a[data-test-locator='mega-item-header']");
            }
            
            System.out.println("  - Yahoo Finance 뉴스: " + newsItems.size() + "개");
            
            // 종목 ID 조회
            Integer stockId = getStockId(stockCode);
            
            for (Element item : newsItems) {
                if (newsList.size() >= maxCount) break;
                
                try {
                    String title = item.text();
                    String link = item.attr("abs:href");
                    
                    if (!title.isEmpty() && !link.isEmpty()) {
                        NewsVO news = new NewsVO();
                        news.setStockId(stockId);
                        news.setStockCode(stockCode);
                        news.setStockName(stockName);
                        news.setTitle(title);
                        news.setLink(link);
                        news.setSource("Yahoo Finance");
                        news.setCountry("US");
                        news.setPublishedAt(new Timestamp(System.currentTimeMillis()));
                        
                        newsList.add(news);
                    }
                } catch (Exception e) {
                    System.err.println("  ⚠️ 뉴스 아이템 파싱 실패: " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            System.err.println("  ⚠️ Yahoo Finance 크롤링 실패: " + e.getMessage());
        }
        
        return newsList;
    }
    
    /**
     * Google Finance 크롤링
     */
    private List<NewsVO> crawlGoogleFinance(String stockCode, String stockName) {
        List<NewsVO> newsList = new ArrayList<>();
        
        try {
            String googleUrl = "https://www.google.com/finance/quote/" + stockCode + ":NASDAQ";
            System.out.println("  - Google Finance URL: " + googleUrl);
            
            Document doc = Jsoup.connect(googleUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Elements newsItems = doc.select("div.yY3Lee");
            System.out.println("  - Google Finance 뉴스: " + newsItems.size() + "개");
            
            Integer stockId = getStockId(stockCode);
            
            for (Element item : newsItems) {
                if (newsList.size() >= 5) break;
                
                try {
                    Element titleElement = item.selectFirst("div.Yfwt5");
                    Element linkElement = item.selectFirst("a");
                    
                    if (titleElement != null && linkElement != null) {
                        String title = titleElement.text();
                        String link = linkElement.attr("abs:href");
                        
                        if (!title.isEmpty() && !link.isEmpty()) {
                            NewsVO news = new NewsVO();
                            news.setStockId(stockId);
                            news.setStockCode(stockCode);
                            news.setStockName(stockName);
                            news.setTitle(title);
                            news.setLink(link);
                            news.setSource("Google Finance");
                            news.setCountry("US");
                            news.setPublishedAt(new Timestamp(System.currentTimeMillis()));
                            
                            newsList.add(news);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("  ⚠️ Google 뉴스 파싱 실패: " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            System.err.println("  ⚠️ Google Finance 크롤링 실패: " + e.getMessage());
        }
        
        return newsList;
    }
    
    /**
     * Google News 검색 크롤링 (대체 방법)
     */
    public List<NewsVO> searchGoogleNews(String stockCode, String stockName) {
        List<NewsVO> newsList = new ArrayList<>();
        
        try {
            System.out.println("🔍 Google News 검색 크롤링");
            
            String keyword = URLEncoder.encode(stockCode + " " + stockName + " stock news", "UTF-8");
            String searchUrl = "https://news.google.com/search?q=" + keyword + "&hl=en-US&gl=US&ceid=US:en";
            
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Elements articles = doc.select("article");
            Integer stockId = getStockId(stockCode);
            
            for (Element article : articles) {
                if (newsList.size() >= maxCount) break;
                
                try {
                    Element titleElement = article.selectFirst("a.DY5T1d");
                    
                    if (titleElement != null) {
                        String title = titleElement.text();
                        String link = titleElement.attr("abs:href");
                        
                        if (!title.isEmpty() && !link.isEmpty()) {
                            NewsVO news = new NewsVO();
                            news.setStockId(stockId);
                            news.setStockCode(stockCode);
                            news.setStockName(stockName);
                            news.setTitle(title);
                            news.setLink(link);
                            news.setSource("Google News");
                            news.setCountry("US");
                            news.setPublishedAt(new Timestamp(System.currentTimeMillis()));
                            
                            newsList.add(news);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("  ⚠️ 기사 파싱 실패: " + e.getMessage());
                }
            }
            
            System.out.println("✅ Google News 크롤링 완료: " + newsList.size() + "개");
            
        } catch (Exception e) {
            System.err.println("❌ Google News 크롤링 실패: " + e.getMessage());
        }
        
        return newsList;
    }
    
    /**
     * ✅ 수정: 종목 ID 조회 메서드
     * - StockVO 타입으로 정확히 받기
     * - null 안정성 확보
     */
    private Integer getStockId(String stockCode) {
        try {
            // ✅ StockVO로 정확히 받기 (캐스팅 불필요)
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
        return "US";
    }
    
    @Override
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
    
    /**
     * ✅ supports 메서드 구현
     * 
     * 미국 주식 종목 코드 패턴:
     * - 알파벳만 (예: AAPL, TSLA, MSFT)
     * 
     * @param stockCode 종목 코드
     * @return 미국 주식이면 true
     */
    @Override
    public boolean supports(String stockCode) {
        // 미국 주식: 알파벳만
        return stockCode != null && stockCode.matches("^[A-Z]+$");
    }
}
