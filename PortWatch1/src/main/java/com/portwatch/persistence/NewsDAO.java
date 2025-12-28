package com.portwatch.persistence;

import com.portwatch.domain.NewsVO;
import java.util.List;

/**
 * ✅ 뉴스 DAO 인터페이스
 * 
 * @author PortWatch
 * @version 3.0 - MySQL 8.0 호환
 */
public interface NewsDAO {
    
    /**
     * 최신 뉴스 조회
     */
    List<NewsVO> selectLatestNews(int limit) throws Exception;
    
    /**
     * 종목별 뉴스 조회
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
     * 뉴스 URL 중복 체크
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

	List<NewsVO> selectAllNews();

	List<NewsVO> selectRecentNews(int limit);

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
}
