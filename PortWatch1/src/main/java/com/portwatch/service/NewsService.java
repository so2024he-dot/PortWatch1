package com.portwatch.service;

import java.util.List;
import com.portwatch.domain.NewsVO;

/**
 * 뉴스 서비스 인터페이스
 * 
 * @author PortWatch
 * @version 1.0
 */
public interface NewsService {
    
    /**
     * 최근 뉴스 조회
     * @param limit 조회할 뉴스 개수
     * @return 뉴스 리스트
     */
    List<NewsVO> getRecentNews(int limit);
    
    /**
     * 뉴스 ID로 조회
     * @param newsId 뉴스 ID
     * @return 뉴스 객체
     */
    NewsVO getNewsById(Long newsId);
    
    /**
     * 종목별 뉴스 조회
     * @param stockCode 종목 코드
     * @param limit 조회할 뉴스 개수
     * @return 뉴스 리스트
     */
    List<NewsVO> getNewsByStockCode(String stockCode, int limit);
    
    /**
     * 뉴스 크롤링 및 저장
     * @return 수집된 뉴스 개수
     */
    int crawlAndSaveNews();
    
    /**
     * 특정 종목의 뉴스 크롤링 및 저장
     * @param stockCode 종목 코드
     * @param stockName 종목명
     * @return 수집된 뉴스 개수
     */
    int crawlStockNews(String stockCode, String stockName);
    
    /**
     * 뉴스 저장
     * @param news 뉴스 객체
     * @return 저장 성공 여부
     */
    boolean saveNews(NewsVO news);
    
    /**
     * 중복 뉴스 체크
     * @param link 뉴스 링크
     * @return 중복 여부
     */
    boolean isDuplicateNews(String link);
    
    /**
     * 오래된 뉴스 삭제 (30일 이상)
     * @return 삭제된 뉴스 개수
     */
    int deleteOldNews();
}
