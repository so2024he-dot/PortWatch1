package com.portwatch.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.NewsVO;
import com.portwatch.persistence.NewsDAO;

import lombok.extern.log4j.Log4j;


/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * NewsServiceImpl - 완벽 수정 버전
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 수정:
 * 1. ✅ 중복 import 제거
 * 2. ✅ NaverNewsCrawler Autowired 추가
 * 3. ✅ 잘못된 메소드 구조 수정 (113-125번 라인)
 * 4. ✅ 메소드 밖의 코드를 메소드 안으로 이동 (368-414번 라인)
 * 5. ✅ 중복된 getNewsById 메소드 제거
 * 6. ✅ 구현되지 않은 메소드 구현 (getNewsByStockCode, fetchNaverFinanceNews, getTotalNewsCount)
 * 7. ✅ 띄어쓰기 오류 수정 (newsListFrom Naver → newsListFromNaver)
 * 
 * @version 6.0 ULTIMATE - 완전 수정 버전
 */
@Service
@Log4j
public class NewsServiceImpl implements NewsService {
    
    @Autowired
    private NewsDAO newsDAO;
    
    @Autowired
    private NaverNewsCrawler naverNewsCrawler;
    
    /**
     * ✅ 전체 뉴스 조회
     */
    @Override
    public List<NewsVO> getAllNews() throws Exception {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📰 전체 뉴스 조회");
        
        try {
            List<NewsVO> newsList = newsDAO.selectAllNews();
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            log.info("  - 뉴스 개수: " + newsList.size());
            log.info("✅ 전체 뉴스 조회 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            log.error("❌ 전체 뉴스 조회 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("전체 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 최근 뉴스 조회
     */
    @Override
    public List<NewsVO> getRecentNews(int limit) throws Exception {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📰 최근 뉴스 조회");
        log.info("  - 조회 개수: " + limit);
        
        try {
            List<NewsVO> newsList = newsDAO.selectRecentNews(limit);
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            log.info("  - 조회 결과: " + newsList.size() + "건");
            log.info("✅ 최근 뉴스 조회 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            log.error("❌ 최근 뉴스 조회 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("최근 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 종목별 뉴스 조회
     */
    @Override
    public List<NewsVO> getNewsByStock(String stockCode, int limit) throws Exception {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📰 종목별 뉴스 조회");
        log.info("  - 종목 코드: " + stockCode);
        log.info("  - 조회 개수: " + limit);
        
        try {
            List<NewsVO> newsList = newsDAO.selectNewsByStock(stockCode, limit);
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            log.info("  - 조회 결과: " + newsList.size() + "건");
            log.info("✅ 종목별 뉴스 조회 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            log.error("❌ 종목별 뉴스 조회 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("종목별 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 국가별 뉴스 조회
     */
    @Override
    public List<NewsVO> getNewsByCountry(String country, int limit) throws Exception {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🌍 국가별 뉴스 조회");
        log.info("  - 국가: " + country);
        log.info("  - 조회 개수: " + limit);
        
        try {
            List<NewsVO> newsList = newsDAO.selectNewsByCountry(country, limit);
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            log.info("  - 조회 결과: " + newsList.size() + "건");
            log.info("✅ 국가별 뉴스 조회 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            log.error("❌ 국가별 뉴스 조회 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("국가별 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ⭐ 뉴스 크롤링 및 저장 (실제 NaverNewsCrawler 사용)
     */
    @Override
    @Transactional
    public int crawlAndSaveNews() throws Exception {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🔄 뉴스 크롤링 시작");
        
        try {
            // ✅ 네이버 금융 뉴스 크롤링 (띄어쓰기 오류 수정!)
            List<NewsVO> newsListFromNaver = naverNewsCrawler.crawlNews();
            
            if (newsListFromNaver == null || newsListFromNaver.isEmpty()) {
                log.warn("⚠️ 크롤링된 뉴스가 없습니다.");
                return 0;
            }
            
            log.info("  - 크롤링된 뉴스: " + newsListFromNaver.size() + "개");
            
            // 중복 체크 및 저장
            int savedCount = 0;
            for (NewsVO news : newsListFromNaver) {
                try {
                    // URL 중복 체크
                    boolean exists = newsDAO.existsByUrl(news.getNewsUrl());
                    
                    if (!exists) {
                        newsDAO.insertNews(news);
                        savedCount++;
                        log.debug("  ✅ 저장: " + news.getTitle());
                    } else {
                        log.debug("  ⏭️ 중복: " + news.getTitle());
                    }
                    
                } catch (Exception e) {
                    log.warn("  ⚠️ 뉴스 저장 실패: " + news.getTitle() + " - " + e.getMessage());
                }
            }
            
            log.info("✅ 뉴스 크롤링 완료");
            log.info("  - 총 크롤링: " + newsListFromNaver.size() + "개");
            log.info("  - 신규 저장: " + savedCount + "개");
            log.info("  - 중복 제외: " + (newsListFromNaver.size() - savedCount) + "개");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return savedCount;
            
        } catch (Exception e) {
            log.error("❌ 뉴스 크롤링 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new RuntimeException("뉴스 크롤링 중 오류 발생", e);
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
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📊 뉴스 통계 조회");
        
        try {
            List<NewsVO> allNews = getAllNews();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCount", allNews.size());
            stats.put("recentCount", Math.min(10, allNews.size()));
            
            log.info("  - 전체 뉴스: " + allNews.size() + "건");
            log.info("✅ 뉴스 통계 조회 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return stats;
            
        } catch (Exception e) {
            log.error("❌ 뉴스 통계 조회 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("뉴스 통계 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 뉴스 추가
     */
    @Override
    @Transactional
    public void insertNews(NewsVO news) throws Exception {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📝 뉴스 추가");
        log.info("  - 제목: " + news.getTitle());
        
        try {
            newsDAO.insertNews(news);
            
            log.info("✅ 뉴스 추가 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            log.error("❌ 뉴스 추가 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("뉴스 추가 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 뉴스 조회 (ID로) - 단일 메소드로 통합
     */
    @Override
    public NewsVO getNewsById(Long newsId) throws Exception {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🔍 뉴스 조회");
        log.info("  - 뉴스 ID: " + newsId);
        
        try {
            NewsVO news = newsDAO.selectNewsById(newsId);
            
            if (news != null) {
                log.info("✅ 뉴스 조회 완료");
                log.info("  - 제목: " + news.getTitle());
            } else {
                log.warn("⚠️ 뉴스를 찾을 수 없습니다");
            }
            
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return news;
            
        } catch (Exception e) {
            log.error("❌ 뉴스 조회 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("뉴스 조회 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ 카테고리별 뉴스 조회
     */
    @Override
    public List<NewsVO> getNewsByCategory(String category, int limit) throws Exception {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📂 카테고리별 뉴스 조회");
        log.info("  - 카테고리: " + category);
        log.info("  - 조회 개수: " + limit);
        
        try {
            List<NewsVO> newsList = newsDAO.selectByCategory(category, limit);
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            log.info("  - 조회 결과: " + newsList.size() + "건");
            log.info("✅ 카테고리별 뉴스 조회 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            log.error("❌ 카테고리별 뉴스 조회 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("카테고리별 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ 뉴스 검색
     */
    @Override
    public List<NewsVO> searchNews(String keyword, int limit) throws Exception {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🔍 뉴스 검색");
        log.info("  - 검색어: " + keyword);
        log.info("  - 조회 개수: " + limit);
        
        try {
            List<NewsVO> newsList = newsDAO.search(keyword, limit);
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            log.info("  - 검색 결과: " + newsList.size() + "건");
            log.info("✅ 뉴스 검색 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            log.error("❌ 뉴스 검색 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("뉴스 검색 실패: " + e.getMessage(), e);
        }
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ✅ 추가 구현: 구현되지 않았던 메소드들
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * ✅ 종목 코드로 뉴스 조회 (신규 구현)
     */
    @Override
    public List<NewsVO> getNewsByStockCode(String stockCode, int limit) throws Exception {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📰 종목 코드로 뉴스 조회");
        log.info("  - 종목 코드: " + stockCode);
        log.info("  - 조회 개수: " + limit);
        
        try {
            // getNewsByStock 메소드 재사용
            List<NewsVO> newsList = newsDAO.selectNewsByStock(stockCode, limit);
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            log.info("  - 조회 결과: " + newsList.size() + "건");
            log.info("✅ 종목 코드로 뉴스 조회 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            log.error("❌ 종목 코드로 뉴스 조회 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("종목 코드로 뉴스 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 네이버 금융 뉴스 가져오기 (신규 구현)
     */
    @Override
    public List<NewsVO> fetchNaverFinanceNews(int limit) throws Exception {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📰 네이버 금융 뉴스 가져오기");
        log.info("  - 조회 개수: " + limit);
        
        try {
            // NaverNewsCrawler로 뉴스 크롤링
            List<NewsVO> newsList = naverNewsCrawler.crawlNews();
            
            if (newsList == null) {
                newsList = new ArrayList<>();
            }
            
            // limit 적용
            if (newsList.size() > limit) {
                newsList = newsList.subList(0, limit);
            }
            
            log.info("  - 가져온 뉴스: " + newsList.size() + "건");
            log.info("✅ 네이버 금융 뉴스 가져오기 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newsList;
            
        } catch (Exception e) {
            log.error("❌ 네이버 금융 뉴스 가져오기 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("네이버 금융 뉴스 가져오기 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 전체 뉴스 개수 조회 (신규 구현)
     */
    @Override
    public int getTotalNewsCount() {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🔢 전체 뉴스 개수 조회");
        
        try {
            int count = newsDAO.countAllNews();
            
            log.info("  - 전체 뉴스 개수: " + count + "건");
            log.info("✅ 전체 뉴스 개수 조회 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return count;
            
        } catch (Exception e) {
            log.error("❌ 전체 뉴스 개수 조회 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return 0;
        }
    }
}
