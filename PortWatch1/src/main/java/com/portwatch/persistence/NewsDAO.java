package com.portwatch.persistence;

import com.portwatch.domain.NewsVO;
import java.util.List;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * ✅ 뉴스 DAO 인터페이스 (완벽 수정 버전)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 추가 메소드:
 * 1. existsByUrl() - URL 중복 체크 (boolean 반환)
 * 2. countAllNews() - 전체 뉴스 개수 조회
 * 
 * @author PortWatch
 * @version 4.0 - MySQL 8.0 호환 + 누락 메소드 추가
 */
public interface NewsDAO {
    
    /**
     * 최신 뉴스 조회
     */
    List<NewsVO> selectLatestNews(int limit) throws Exception;
    
    /**
     * 종목별 뉴스 조회 (stockCode)
     */
    List<NewsVO> selectNewsByStockCode(String stockCode, int limit) throws Exception;
    
    /**
     * 국가별 뉴스 조회
     */
    List<NewsVO> selectNewsByCountry(String country, int limit) throws Exception;
    
    /**
     * 뉴스 ID로 조회
     */
    NewsVO selectNewsById(Long newsId) throws Exception;
    
    /**
     * 뉴스 삽입
     */
    void insertNews(NewsVO news) throws Exception;
    
    /**
     * 뉴스 URL 중복 체크 (int 반환)
     */
    int checkDuplicateUrl(String link) throws Exception;
    
    /**
     * 뉴스 업데이트
     */
    void updateNews(NewsVO news) throws Exception;
    
    /**
     * 뉴스 삭제
     */
    void deleteNews(Long newsId) throws Exception;
    
    /**
     * 전체 뉴스 개수
     */
    int getTotalNewsCount() throws Exception;
    
    /**
     * 오늘 등록된 뉴스 개수
     */
    int getTodayNewsCount() throws Exception;
    
    /**
     * 전체 뉴스 조회
     */
    List<NewsVO> selectAllNews();
    
    /**
     * 최근 뉴스 조회
     */
    List<NewsVO> selectRecentNews(int limit);
    
    /**
     * 종목별 뉴스 조회
     */
    List<NewsVO> selectNewsByStock(String stockCode, int limit);
    
    /**
     * ✅ 카테고리별 뉴스 조회
     * 
     * @param category 카테고리 (예: 증시, 경제, 산업)
     * @param limit 조회 개수
     * @return List<NewsVO>
     * @throws Exception
     */
    List<NewsVO> selectByCategory(String category, int limit) throws Exception;
    
    /**
     * ✅ 뉴스 검색
     * 
     * @param keyword 검색 키워드
     * @param limit 조회 개수
     * @return List<NewsVO>
     * @throws Exception
     */
    List<NewsVO> search(String keyword, int limit) throws Exception;
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ✅ 추가 메소드 (NewsServiceImpl에서 사용)
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * ✅ URL 존재 여부 확인 (boolean 반환)
     * 
     * @param url 확인할 뉴스 URL
     * @return URL이 존재하면 true, 아니면 false
     */
    boolean existsByUrl(String url) throws Exception;
    
    /**
     * ✅ 전체 뉴스 개수 조회 (countAllNews)
     * 
     * @return 전체 뉴스 개수
     */
    int countAllNews() throws Exception;
}
