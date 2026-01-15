package com.portwatch.service;

import com.portwatch.domain.NewsVO;
import java.util.List;
import java.util.Map;

/**
 * ✅ 뉴스 Service 인터페이스
 * 
 * @author PortWatch
 * @version 3.0
 */
public interface NewsService {
    
    /**
     * 최신 뉴스 조회
     */
    List<NewsVO> getRecentNews(int limit) throws Exception;
    
    /**
     * 종목별 뉴스 조회
     */
    List<NewsVO> getNewsByStockCode(String stockCode, int limit) throws Exception;
    
    /**
     * 종목별 뉴스 조회 (별칭)
     */
    List<NewsVO> getNewsByStock(String stockCode, int limit) throws Exception;
    
    /**
     * 국가별 뉴스 조회
     */
    List<NewsVO> getNewsByCountry(String country, int limit) throws Exception;
    
    /**
     * 뉴스 ID로 조회
     */
    NewsVO getNewsById(Long newsId) throws Exception;
    
    /**
     * 네이버 금융 뉴스 크롤링
     */
    List<NewsVO> fetchNaverFinanceNews(int limit) throws Exception;
    
    /**
     * 뉴스 크롤링 및 저장
     */
    int crawlAndSaveNews() throws Exception;
    
    /**
     * 뉴스 통계 조회
     */
    Map<String, Object> getNewsStats() throws Exception;
    
    /**
     * DB에 저장된 최신 뉴스 조회 (별칭)
     */
    List<NewsVO> getLatestNews(int limit) throws Exception;

	/**
	 * ✅ 전체 뉴스 조회
	 * 
	 * @return 전체 뉴스 리스트
	 */
	List<NewsVO> getAllNews() throws Exception;

	/**
	 * ✅ 뉴스 추가
	 * 
	 * @param news 뉴스 정보
	 */
	void insertNews(NewsVO news) throws Exception;

	List<NewsVO> getNewsByCategory(String category, int limit) throws Exception;

	List<NewsVO> searchNews(String keyword, int limit) throws Exception;

	int getTotalNewsCount();

	
}
