package com.portwatch.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
 * NewsServiceImpl - 실제 MySQL 테이블 구조 반영
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 수정:
 * 1. setSource() → setName()
 * 2. setLink() → setNewsUrl()
 * 3. setPublishedAt() → setPublishedDate()
 * 4. 추가 필드: newsCode, newsTitle, newsCol
 * 
 * @version 5.0 ULTIMATE - 실제 테이블 반영
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
     * ⭐ 뉴스 크롤링 및 저장 (실제 테이블 구조 반영!)
     */
    @Override
    @Transactional
    public int crawlAndSaveNews() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 뉴스 크롤링 및 저장 (더미 데이터)");
        
        try {
            int savedCount = 0;
            Random random = new Random();
            
            // ✅ 종목 코드 목록
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
            
            // ✅ 뉴스 제목 템플릿
            String[] titles = {
                "주가 급등 전망",
                "신기술 발표",
                "실적 개선",
                "글로벌 진출",
                "신제품 출시"
            };
            
            String[] newsTitles = {
                "분석: 목표가 상향 조정",
                "시장 전망: 긍정적 평가",
                "투자 의견: 매수 유지",
                "애널리스트: 성장 기대",
                "전문가: 강세 전망"
            };
            
            // ✅ 뉴스 소스 (name 필드)
            String[] names = {"연합뉴스", "한국경제", "매일경제", "Reuters", "Bloomberg"};
            
            // ✅ 10개 뉴스 생성
            for (int i = 0; i < 10; i++) {
                NewsVO news = new NewsVO();
                
                // 랜덤 종목 선택
                String stockCode = stockCodes[random.nextInt(stockCodes.length)];
                news.setStockCode(stockCode);
                
                // 뉴스 코드 생성 (NEWS + timestamp)
                news.setNewsCode("NEWS" + System.currentTimeMillis() + i);
                
                // 제목 설정
                news.setTitle("[" + stockCode + "] " + titles[random.nextInt(titles.length)]);
                news.setNewsTitle(newsTitles[random.nextInt(newsTitles.length)]);
                
                // URL 설정
                news.setNewsUrl("https://finance.example.com/news/" + news.getNewsCode());
                
                // 소스 설정 (name 필드)
                news.setName(names[random.nextInt(names.length)]);
                
                // newsCol 설정
                news.setNewsCol("STOCK_NEWS");
                
                // ✅ 발행 시간 (최근 24시간 내 랜덤)
                LocalDateTime publishedDate = LocalDateTime.now()
                    .minusHours(random.nextInt(24));
                news.setPublishedDate(publishedDate);  // ✅ setPublishedDate 사용!
                
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
        return getRecentNews(limit);
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

	@Override
	public List<NewsVO> getNewsByStockCode(String stockCode, int limit) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<NewsVO> fetchNaverFinanceNews(int limit) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
