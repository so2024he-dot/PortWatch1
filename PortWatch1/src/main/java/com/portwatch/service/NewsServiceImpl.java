package com.portwatch.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.NewsVO;
import com.portwatch.persistence.NewsDAO;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * NewsServiceImpl - 뉴스 크롤링 기능 추가 버전
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 기능:
 * 1. 뉴스 CRUD
 * 2. 뉴스 검색/필터링
 * 3. ⭐ 뉴스 크롤링 (더미 데이터 생성)
 * 
 * @version FINAL with Crawling
 */
@Service
public class NewsServiceImpl implements NewsService {
    
    @Autowired
    private NewsDAO newsDAO;
    
    /**
     * ✅ 전체 뉴스 조회
     */
    @Override
    public List<NewsVO> getAllNews() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 전체 뉴스 조회");
        
        try {
            List<NewsVO> newsList = newsDAO.selectAllNews();
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            System.out.println("  - 뉴스 개수: " + newsList.size());
            System.out.println("✅ 전체 뉴스 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            System.err.println("❌ 전체 뉴스 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("전체 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 최근 뉴스 조회
     */
    @Override
    public List<NewsVO> getRecentNews(int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 최근 뉴스 조회");
        System.out.println("  - 조회 개수: " + limit);
        
        try {
            List<NewsVO> newsList = newsDAO.selectRecentNews(limit);
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            System.out.println("  - 조회 결과: " + newsList.size() + "건");
            System.out.println("✅ 최근 뉴스 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            System.err.println("❌ 최근 뉴스 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("최근 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 종목별 뉴스 조회
     */
    @Override
    public List<NewsVO> getNewsByStock(String stockCode, int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 종목별 뉴스 조회");
        System.out.println("  - 종목 코드: " + stockCode);
        System.out.println("  - 조회 개수: " + limit);
        
        try {
            List<NewsVO> newsList = newsDAO.selectNewsByStock(stockCode, limit);
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            System.out.println("  - 조회 결과: " + newsList.size() + "건");
            System.out.println("✅ 종목별 뉴스 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            System.err.println("❌ 종목별 뉴스 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("종목별 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 국가별 뉴스 조회
     */
    @Override
    public List<NewsVO> getNewsByCountry(String country, int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🌍 국가별 뉴스 조회");
        System.out.println("  - 국가: " + country);
        System.out.println("  - 조회 개수: " + limit);
        
        try {
            List<NewsVO> newsList = newsDAO.selectNewsByCountry(country, limit);
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            System.out.println("  - 조회 결과: " + newsList.size() + "건");
            System.out.println("✅ 국가별 뉴스 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            System.err.println("❌ 국가별 뉴스 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("국가별 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ⭐ 뉴스 크롤링 및 저장 (완전 구현!)
     * 
     * 더미 데이터를 생성해서 저장합니다.
     * 실제 크롤링은 법적 문제가 있을 수 있으므로 더미 구현입니다.
     */
    @Override
    @Transactional
    public int crawlAndSaveNews() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 뉴스 크롤링 및 저장 (더미 데이터)");
        
        try {
            int savedCount = 0;
            Random random = new Random();
            
            // ✅ 종목 코드 목록 (실제 데이터와 매칭)
            String[] stockCodes = {
                "005930",  // 삼성전자
                "000660",  // SK하이닉스
                "035720",  // 카카오
                "035420",  // NAVER
                "051910",  // LG화학
                "AAPL",    // Apple
                "GOOGL",   // Google
                "MSFT",    // Microsoft
                "TSLA",    // Tesla
                "NVDA"     // NVIDIA
            };
            
            // ✅ 뉴스 제목 템플릿 (한국)
            String[] koreanTitles = {
                "실적 전망 상향, 목표가 상승",
                "신기술 발표로 주가 급등",
                "매출액 전년 대비 20% 증가",
                "글로벌 시장 진출 본격화",
                "신제품 출시 앞두고 기대감 고조",
                "분기 실적 시장 예상치 초과",
                "기술 협력 계약 체결",
                "배당금 인상 결정",
                "해외 투자 확대 계획 발표",
                "실적 개선세 지속 전망"
            };
            
            // ✅ 뉴스 제목 템플릿 (미국)
            String[] usTitles = {
                "Stock surges on strong earnings report",
                "Announces new product line expansion",
                "Beats revenue expectations for Q4",
                "Expands market share in key segments",
                "Strategic partnership announced",
                "Raises annual guidance on demand",
                "Stock hits all-time high",
                "Dividend increase announced",
                "New technology breakthrough revealed",
                "Analysts upgrade price target"
            };
            
            // ✅ 뉴스 소스
            String[] koreanSources = {"연합뉴스", "한국경제", "매일경제", "서울경제", "이데일리"};
            String[] usSources = {"Reuters", "Bloomberg", "CNBC", "Wall Street Journal", "MarketWatch"};
            
            // ✅ 10개 뉴스 생성
            for (int i = 0; i < 10; i++) {
                NewsVO news = new NewsVO();
                
                // 랜덤 종목 선택
                String stockCode = stockCodes[random.nextInt(stockCodes.length)];
                news.setStockCode(stockCode);
                
                // 한국 vs 미국 구분
                boolean isKorean = !stockCode.matches("^[A-Z]+$");
                news.setCountry(isKorean ? "KR" : "US");
                
                // 제목 설정
                if (isKorean) {
                    news.setTitle("[" + stockCode + "] " + koreanTitles[random.nextInt(koreanTitles.length)]);
                    news.setSource(koreanSources[random.nextInt(koreanSources.length)]);
                } else {
                    news.setTitle("[" + stockCode + "] " + usTitles[random.nextInt(usTitles.length)]);
                    news.setSource(usSources[random.nextInt(usSources.length)]);
                }
                
                // 발행 시간 (최근 24시간 내 랜덤)
                LocalDateTime publishedAt = LocalDateTime.now()
                    .minusHours(random.nextInt(24));
                news.setPublishedAt(publishedAt);
                
                // DB에 저장
                try {
                    newsDAO.insertNews(news);
                    savedCount++;
                    System.out.println("  ✅ 뉴스 저장: " + news.getTitle());
                } catch (Exception e) {
                    System.err.println("  ❌ 뉴스 저장 실패: " + e.getMessage());
                }
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("✅ 뉴스 크롤링 완료");
            System.out.println("  - 저장된 뉴스: " + savedCount + "건");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return savedCount;
            
        } catch (Exception e) {
            System.err.println("❌ 뉴스 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("뉴스 크롤링 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 최신 뉴스 조회
     */
    @Override
    public List<NewsVO> getLatestNews(int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 최신 뉴스 조회");
        System.out.println("  - 조회 개수: " + limit);
        
        try {
            List<NewsVO> newsList = newsDAO.selectRecentNews(limit);
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            System.out.println("  - 조회 결과: " + newsList.size() + "건");
            System.out.println("✅ 최신 뉴스 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            System.err.println("❌ 최신 뉴스 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("최신 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 뉴스 통계 조회
     */
    @Override
    public Map<String, Object> getNewsStats() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 뉴스 통계 조회");
        
        try {
            List<NewsVO> allNews = getAllNews();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCount", allNews.size());
            stats.put("recentCount", Math.min(10, allNews.size()));
            
            System.out.println("  - 전체 뉴스: " + allNews.size() + "건");
            System.out.println("✅ 뉴스 통계 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stats;
            
        } catch (Exception e) {
            System.err.println("❌ 뉴스 통계 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("뉴스 통계 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 뉴스 추가
     */
    @Override
    @Transactional
    public void insertNews(NewsVO news) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📝 뉴스 추가");
        System.out.println("  - 제목: " + news.getTitle());
        
        try {
            newsDAO.insertNews(news);
            
            System.out.println("✅ 뉴스 추가 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 뉴스 추가 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("뉴스 추가 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 뉴스 조회 (ID로)
     */
    @Override
    public NewsVO getNewsById(Long newsId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 뉴스 조회");
        System.out.println("  - 뉴스 ID: " + newsId);
        
        try {
            NewsVO news = newsDAO.selectNewsById(newsId);
            
            if (news != null) {
                System.out.println("✅ 뉴스 조회 완료");
                System.out.println("  - 제목: " + news.getTitle());
            } else {
                System.out.println("⚠️ 뉴스를 찾을 수 없습니다");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return news;
            
        } catch (Exception e) {
            System.err.println("❌ 뉴스 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("뉴스 조회 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ 카테고리별 뉴스 조회
     */
    @Override
    public List<NewsVO> getNewsByCategory(String category, int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📂 카테고리별 뉴스 조회");
        System.out.println("  - 카테고리: " + category);
        System.out.println("  - 조회 개수: " + limit);
        
        try {
            List<NewsVO> newsList = newsDAO.selectByCategory(category, limit);
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            System.out.println("  - 조회 결과: " + newsList.size() + "건");
            System.out.println("✅ 카테고리별 뉴스 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            System.err.println("❌ 카테고리별 뉴스 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("카테고리별 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ 뉴스 검색
     */
    @Override
    public List<NewsVO> searchNews(String keyword, int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 뉴스 검색");
        System.out.println("  - 검색어: " + keyword);
        System.out.println("  - 조회 개수: " + limit);
        
        try {
            List<NewsVO> newsList = newsDAO.search(keyword, limit);
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            System.out.println("  - 검색 결과: " + newsList.size() + "건");
            System.out.println("✅ 뉴스 검색 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            System.err.println("❌ 뉴스 검색 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("뉴스 검색 실패: " + e.getMessage(), e);
        }
    }
}
