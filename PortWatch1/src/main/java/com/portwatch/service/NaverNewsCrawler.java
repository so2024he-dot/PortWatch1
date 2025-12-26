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
 * ✅ 최종 수정: NaverNewsCrawler.java
 * 
 * 수정 사항:
 * 1. getStockId() 메서드 - StockVO 타입으로 정확히 수정
 * 2. supports() 메서드 구현
 * 3. 느슨한 결합을 위한 인터페이스 구현
 * 
 * @author PortWatch
 * @version 10.0 - 타입 안정성 완료
 * @param <StockDAO>
 */
@Component
public class NaverNewsCrawler<StockDAO> implements NewsCrawler {
    
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
                // 대체 선택자
                newsItems = doc.select("div.news_area");
            }
            
            System.out.println("  - 발견된 뉴스: " + newsItems.size() + "개");
            
            Integer stockId = getStockId(stockCode);
            
            for (Element item : newsItems) {
                if (newsList.size() >= maxCount) break;
                
                try {
                    Element linkElement = item.selectFirst("a.tit");
                    
                    if (linkElement != null) {
                        String title = linkElement.text();
                        String link = linkElement.attr("abs:href");
                        
                        // 네이버 뉴스 링크만 처리
                        if (!title.isEmpty() && !link.isEmpty() && link.contains("naver.com")) {
                            NewsVO news = new NewsVO();
                            news.setStockId(stockId);
                            news.setStockCode(stockCode);
                            news.setStockName(stockName);
                            news.setTitle(title);
                            news.setLink(link);
                            news.setSource("네이버 금융");
                            news.setCountry("KR");
                            news.setPublishedAt(new Timestamp(System.currentTimeMillis()));
                            
                            newsList.add(news);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("  ⚠️ 뉴스 아이템 파싱 실패: " + e.getMessage());
                }
            }
            
            // 뉴스가 부족하면 검색 결과 추가
            if (newsList.size() < 5) {
                List<NewsVO> searchNews = searchNaverNews(stockCode, stockName);
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
    private List<NewsVO> searchNaverNews(String stockCode, String stockName) {
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
            
            for (Element item : newsItems) {
                if (newsList.size() >= 5) break;
                
                try {
                    Element titleElement = item.selectFirst("a.news_tit");
                    
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
                            news.setSource("네이버 뉴스");
                            news.setCountry("KR");
                            news.setPublishedAt(new Timestamp(System.currentTimeMillis()));
                            
                            newsList.add(news);
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
     * ✅ 수정: 종목 ID 조회 메서드
     * - StockVO 타입으로 정확히 받기
     * - null 안정성 확보
     */
    private Integer getStockId(String stockCode) {
        try {
            // ✅ StockVO로 정확히 받기
            StockVO stock = ((com.portwatch.persistence.StockDAO) stockDAO).selectStockByCode(stockCode);
            
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
     * ✅ 추가: supports 메서드 구현
     * 
     * 한국 주식 종목 코드 패턴:
     * - 숫자 6자리 (예: 005930, 000660)
     * 
     * @param stockCode 종목 코드
     * @return 한국 주식이면 true
     */
    @Override
    public boolean supports(String stockCode) {
        // 한국 주식: 숫자 6자리
        return stockCode != null && stockCode.matches("^\\d{6}$");
    }
}
