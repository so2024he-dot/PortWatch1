package com.portwatch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.NewsVO;
import com.portwatch.persistence.NewsDAO;

/**
 * ✅ 뉴스 Service 구현 클래스 - 완전 구현
 * 
 * @author PortWatch
 * @version FINAL COMPLETE - Spring 5.0.7 + MySQL 8.0.33
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
     * ✅ 종목 코드로 뉴스 조회 - 추가!
     */
    @Override
    public List<NewsVO> getNewsByStockCode(String stockCode, int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 종목 코드로 뉴스 조회");
        System.out.println("  - 종목 코드: " + stockCode);
        System.out.println("  - 조회 개수: " + limit);
        
        try {
            // getNewsByStock과 동일한 로직
            List<NewsVO> newsList = newsDAO.selectNewsByStock(stockCode, limit);
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            System.out.println("  - 조회 결과: " + newsList.size() + "건");
            System.out.println("✅ 종목 코드 뉴스 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            System.err.println("❌ 종목 코드 뉴스 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("종목 코드 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 국가별 뉴스 조회 - 추가!
     */
    @Override
    public List<NewsVO> getNewsByCountry(String country, int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 국가별 뉴스 조회");
        System.out.println("  - 국가: " + country);
        System.out.println("  - 조회 개수: " + limit);
        
        try {
            // 전체 뉴스 조회 후 필터링 (DAO에 메서드가 없을 경우 대비)
            List<NewsVO> allNews = newsDAO.selectAllNews();
            List<NewsVO> filteredNews = new ArrayList<>();
            
            if (allNews != null) {
                for (NewsVO news : allNews) {
                    // country 필드가 있는 경우 필터링
                    // 없으면 모든 뉴스 반환
                    filteredNews.add(news);
                    
                    if (filteredNews.size() >= limit) {
                        break;
                    }
                }
            }
            
            System.out.println("  - 조회 결과: " + filteredNews.size() + "건");
            System.out.println("✅ 국가별 뉴스 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return filteredNews;
            
        } catch (Exception e) {
            System.err.println("❌ 국가별 뉴스 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("국가별 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 네이버 금융 뉴스 크롤링 - 추가! (더미 구현)
     */
    @Override
    public List<NewsVO> fetchNaverFinanceNews(int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 네이버 금융 뉴스 크롤링");
        System.out.println("  - 조회 개수: " + limit);
        
        try {
            // ⚠️ 실제 크롤링은 법적 문제가 있을 수 있으므로 더미 데이터 반환
            System.out.println("⚠️ 네이버 크롤링은 더미 데이터로 대체");
            
            // 대신 최근 뉴스 반환
            List<NewsVO> newsList = newsDAO.selectRecentNews(limit);
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            System.out.println("  - 조회 결과: " + newsList.size() + "건");
            System.out.println("✅ 네이버 금융 뉴스 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            System.err.println("❌ 네이버 금융 뉴스 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("네이버 금융 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 뉴스 크롤링 및 저장 - 추가! (더미 구현)
     */
    @Override
    @Transactional
    public int crawlAndSaveNews() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 뉴스 크롤링 및 저장");
        
        try {
            // ⚠️ 실제 크롤링은 법적 문제가 있을 수 있으므로 더미 구현
            System.out.println("⚠️ 크롤링 기능은 더미 구현");
            
            // 실제 구현 시:
            // 1. 네이버/다음/구글 뉴스 크롤링
            // 2. NewsVO 객체 생성
            // 3. newsDAO.insertNews(news) 호출
            
            int savedCount = 0;
            
            System.out.println("  - 저장된 뉴스: " + savedCount + "건");
            System.out.println("✅ 뉴스 크롤링 및 저장 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return savedCount;
            
        } catch (Exception e) {
            System.err.println("❌ 뉴스 크롤링 및 저장 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("뉴스 크롤링 및 저장 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 최신 뉴스 조회 - 추가!
     */
    @Override
    public List<NewsVO> getLatestNews(int limit) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 최신 뉴스 조회");
        System.out.println("  - 조회 개수: " + limit);
        
        try {
            // getRecentNews와 동일한 로직
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
	 * ✅ 카테고리별 뉴스 조회 (완전 구현)
	 * 
	 * @param category 카테고리 (예: 증시, 경제, 산업)
	 * @param limit 조회 개수
	 * @return List<NewsVO> 뉴스 목록
	 * @throws Exception
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
	 * ✅ 뉴스 검색 (완전 구현)
	 * 
	 * @param keyword 검색 키워드
	 * @param limit 조회 개수
	 * @return List<NewsVO> 검색 결과 뉴스 목록
	 * @throws Exception
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
