    package com.portwatch.service;

import com.portwatch.domain.NewsVO;
import java.util.List;

/**
 * 뉴스 서비스 인터페이스
 */
public interface NewsService {
    
    /**
     * 최신 뉴스 조회 (limit 개수)
     */
    List<NewsVO> getLatestNews(int limit) throws Exception;
    
    /**
     * 특정 종목 관련 뉴스 조회
     */
    List<NewsVO> getNewsByStock(String stockCode, int limit) throws Exception;
    
    /**
     * 네이버 금융 뉴스 크롤링 (실시간)
     */
    List<NewsVO> fetchNaverFinanceNews(int limit) throws Exception;
    
    /**
     * 뉴스 저장 (DB)
     */
    void saveNews(NewsVO news) throws Exception;
    
    /**
     * 뉴스 ID로 조회
     */
    NewsVO getNewsById(int newsId) throws Exception;
}

    
