package com.portwatch.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.persistence.NewsDAO;
import com.portwatch.persistence.StockDAO;
import com.portwatch.domain.NewsVO;
import com.portwatch.domain.StockVO;

/**
 * ✅ 최종 수정: NewsServiceImpl.java
 * 
 * 수정 사항:
 * 1. selectCrawler() 메서드 - StockVO 타입 정확히 수정
 * 2. getNewsByStock() 메서드 - StockVO 타입 정확히 수정
 * 3. fetchNaverFinanceNews() 메서드 - StockVO 타입 정확히 수정
 * 4. 느슨한 결합 완전 적용
 * 
 * @author PortWatch
 * @version 10.0 - 타입 안정성 + 느슨한 결합 완료
 */
@Service
public class NewsServiceImpl implements NewsService {
    
    @Autowired
    private NewsDAO newsDAO;
    
    @Autowired
    private StockDAO stockDAO;
    
    /**
     * ✅ 느슨한 결합 (Loose Coupling)
     * - 모든 NewsCrawler 구현체를 리스트로 주입
     * - Spring이 자동으로 NaverNewsCrawler, USNewsCrawler를 찾아 주입
     */
    @Inject
    private List<NewsCrawler> crawlers;
    
    /**
     * 최근 뉴스 조회 (DB에서)
     */
    @Override
    public List<NewsVO> getRecentNews(int limit) {
        try {
            return newsDAO.selectRecentNews(limit);
        } catch (Exception e) {
            System.err.println("❌ 최근 뉴스 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 뉴스 ID로 조회
     */
    @Override
    public NewsVO getNewsById(Long newsId) {
        try {
            return newsDAO.selectNewsById(newsId);
        } catch (Exception e) {
            System.err.println("❌ 뉴스 조회 실패 (ID: " + newsId + "): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 종목별 뉴스 조회 (DB에서)
     */
    @Override
    public List<NewsVO> getNewsByStockCode(String stockCode, int limit) {
        try {
            return newsDAO.selectNewsByStockCode(stockCode, limit);
        } catch (Exception e) {
            System.err.println("❌ 종목 뉴스 조회 실패 (종목: " + stockCode + "): " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 뉴스 크롤링 및 저장
     */
    @Override
    @Transactional
    public int crawlAndSaveNews() {
        int totalCount = 0;
        
        try {
            System.out.println("🔄 전체 종목 뉴스 크롤링 시작...");
            
            // 모든 종목 조회
            List<StockVO> allStocks = stockDAO.selectAllStocksListVos();
            
            System.out.println("  📊 총 종목 수: " + allStocks.size());
            
            // 각 종목별로 뉴스 크롤링 (최대 20개 종목만)
            int maxStocks = Math.min(20, allStocks.size());
            
            for (int i = 0; i < maxStocks; i++) {
                StockVO stock = allStocks.get(i);
                
                try {
                    int count = crawlStockNews(stock.getStockCode(), stock.getStockName());
                    totalCount += count;
                    
                    // 과도한 요청 방지를 위한 딜레이
                    Thread.sleep(1000);
                    
                } catch (Exception e) {
                    System.err.println("  ⚠️ 종목 뉴스 크롤링 실패 (" + stock.getStockCode() + "): " + e.getMessage());
                }
            }
            
            System.out.println("✅ 전체 뉴스 크롤링 완료! 총 " + totalCount + "개 수집");
            
        } catch (Exception e) {
            System.err.println("❌ 뉴스 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        return totalCount;
    }
    
    /**
     * ✅ 특정 종목의 뉴스 크롤링 (한국/미국 자동 구분)
     * - selectCrawler 메서드를 통해 적절한 크롤러 선택
     */
    @Override
    @Transactional
    public int crawlStockNews(String stockCode, String stockName) {
        int count = 0;
        
        try {
            System.out.println("  📝 뉴스 크롤링 중: " + stockName + " (" + stockCode + ")");
            
            // ✅ 크롤러 선택
            NewsCrawler crawler = selectCrawler(stockCode);
            
            if (crawler == null) {
                System.err.println("  ❌ 크롤러를 찾을 수 없습니다.");
                return 0;
            }
            
            // ✅ 뉴스 크롤링 (NewsVO 반환)
            List<NewsVO> newsList = crawler.crawlNews(stockCode, stockName);
            
            // 중복 체크 후 저장
            for (NewsVO news : newsList) {
                if (!isDuplicateNews(news.getLink())) {
                    if (saveNews(news)) {
                        count++;
                    }
                }
            }
            
            System.out.println("    ✓ " + count + "개 뉴스 저장");
            
        } catch (Exception e) {
            System.err.println("    ✗ 크롤링 실패: " + e.getMessage());
        }
        
        return count;
    }
    
    /**
     * ✅ 수정: 종목별 실시간 뉴스 조회 (한국/미국 자동 구분)
     * - StockVO 타입으로 정확히 수정
     * 크롤링 후 즉시 반환 (DB 저장 안 함)
     */
    @Override
    public List<NewsVO> getNewsByStock(String stockCode, int limit) {
        try {
            System.out.println("📊 종목별 실시간 뉴스 조회: " + stockCode);
            
            // ✅ StockVO로 정확히 받기
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            String stockName = (stock != null) ? stock.getStockName() : stockCode;
            
            // ✅ 크롤러 선택
            NewsCrawler crawler = selectCrawler(stockCode);
            
            if (crawler == null) {
                System.err.println("  ❌ 크롤러를 찾을 수 없습니다.");
                return new ArrayList<>();
            }
            
            // ✅ 최대 개수 설정
            crawler.setMaxCount(limit);
            
            // ✅ 뉴스 크롤링 (NewsVO 반환)
            List<NewsVO> newsList = crawler.crawlNews(stockCode, stockName);
            
            System.out.println("  ✓ " + newsList.size() + "개 뉴스 반환");
            
            return newsList;
            
        } catch (Exception e) {
            System.err.println("❌ 종목 뉴스 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * ✅ 수정: 크롤러 선택 메서드 (느슨한 결합)
     * - StockVO 타입으로 정확히 수정
     * 
     * 동작 원리:
     * 1. 종목 코드로 해당 종목의 국가 정보 조회
     * 2. 국가 정보가 없으면 종목 코드 패턴으로 판단
     * 3. supports() 메서드를 통해 적절한 크롤러 선택
     * 
     * @param stockCode 종목 코드
     * @return 적절한 뉴스 크롤러
     */
    private NewsCrawler selectCrawler(String stockCode) {
        try {
            System.out.println("  🔍 크롤러 선택 중...");
            
            // ✅ 1. DB에서 종목 정보 조회 (StockVO로 정확히 받기)
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            
            String country = null;
            if (stock != null && stock.getCountry() != null) {
                country = stock.getCountry();
                System.out.println("  📍 DB에서 국가 확인: " + country);
            }
            
            // 2. 국가 정보가 없으면 종목 코드로 판단
            if (country == null) {
                // 미국 주식: 알파벳만 (AAPL, TSLA 등)
                boolean isUSStock = stockCode.matches("^[A-Z]+$");
                country = isUSStock ? "US" : "KR";
                System.out.println("  📍 코드 패턴으로 국가 추론: " + country + " (종목: " + stockCode + ")");
            }
            
            // 3. 적절한 크롤러 찾기
            final String finalCountry = country;
            NewsCrawler selectedCrawler = crawlers.stream()
                .filter(crawler -> {
                    boolean supports = crawler.getCrawlerType().equals(finalCountry);
                    System.out.println("    🔹 " + crawler.getClass().getSimpleName() + 
                                     " (타입: " + crawler.getCrawlerType() + "): " + 
                                     (supports ? "✅ 선택" : "❌ 제외"));
                    return supports;
                })
                .findFirst()
                .orElse(null);
            
            if (selectedCrawler != null) {
                System.out.println("  ✅ 크롤러 선택 완료: " + selectedCrawler.getClass().getSimpleName());
            } else {
                System.err.println("  ❌ 적절한 크롤러를 찾을 수 없습니다!");
                // 기본값: 첫 번째 크롤러 (보통 NaverNewsCrawler)
                selectedCrawler = crawlers.isEmpty() ? null : crawlers.get(0);
            }
            
            return selectedCrawler;
            
        } catch (Exception e) {
            System.err.println("  ⚠️ 크롤러 선택 실패: " + e.getMessage());
            e.printStackTrace();
            // 기본값: 첫 번째 크롤러
            return crawlers.isEmpty() ? null : crawlers.get(0);
        }
    }
    
    /**
     * 뉴스 저장
     */
    @Override
    @Transactional
    public boolean saveNews(NewsVO news) {
        try {
            return newsDAO.insertNews(news) > 0;
        } catch (Exception e) {
            System.err.println("❌ 뉴스 저장 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 중복 뉴스 체크
     */
    @Override
    public boolean isDuplicateNews(String link) {
        try {
            return newsDAO.checkDuplicateNews(link) > 0;
        } catch (Exception e) {
            System.err.println("❌ 중복 체크 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 오래된 뉴스 삭제 (30일 이상)
     */
    @Override
    @Transactional
    public int deleteOldNews() {
        try {
            int deleted = newsDAO.deleteOldNews(30);
            System.out.println("🗑️ 오래된 뉴스 " + deleted + "개 삭제 완료");
            return deleted;
        } catch (Exception e) {
            System.err.println("❌ 오래된 뉴스 삭제 실패: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * ✅ 수정: 네이버 금융에서 실시간 뉴스 가져오기
     * - StockVO 타입으로 정확히 수정
     * - 느슨한 결합으로 크롤러 선택
     */
    @Override
    public List<NewsVO> fetchNaverFinanceNews(int limit) {
        List<NewsVO> allNews = new ArrayList<>();
        
        try {
            System.out.println("📰 실시간 뉴스 크롤링 시작 (limit: " + limit + ")");
            
            // 인기 종목 리스트
            String[] popularStocks = {
                "005930", // 삼성전자
                "000660", // SK하이닉스
                "035420", // NAVER
                "035720", // 카카오
                "373220"  // LG에너지솔루션
            };
            
            // ✅ 한국 크롤러 찾기
            NewsCrawler naverCrawler = crawlers.stream()
                .filter(crawler -> "KR".equals(crawler.getCrawlerType()))
                .findFirst()
                .orElse(null);
            
            if (naverCrawler == null) {
                System.err.println("❌ 한국 크롤러를 찾을 수 없습니다!");
                return allNews;
            }
            
            naverCrawler.setMaxCount(3);
            
            // 각 종목별로 뉴스 크롤링
            for (String stockCode : popularStocks) {
                if (allNews.size() >= limit) break;
                
                try {
                    // ✅ StockVO로 정확히 받기
                    StockVO stock = stockDAO.selectStockByCode(stockCode);
                    String stockName = (stock != null) ? stock.getStockName() : "종목";
                    
                    // ✅ 뉴스 크롤링
                    List<NewsVO> stockNews = naverCrawler.crawlNews(stockCode, stockName);
                    
                    // 최대 limit까지만 추가
                    for (NewsVO news : stockNews) {
                        if (allNews.size() >= limit) break;
                        allNews.add(news);
                    }
                    
                    // 과도한 요청 방지
                    Thread.sleep(500);
                    
                } catch (Exception e) {
                    System.err.println("  ⚠️ 종목 뉴스 크롤링 실패 (" + stockCode + "): " + e.getMessage());
                }
            }
            
            System.out.println("✅ 실시간 뉴스 " + allNews.size() + "개 수집 완료");
            
        } catch (Exception e) {
            System.err.println("❌ 실시간 뉴스 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        return allNews;
    }
    
    /**
     * DB에 저장된 최신 뉴스 조회
     */
    @Override
    public List<NewsVO> getLatestNews(int limit) {
        return getRecentNews(limit);
    }
}
