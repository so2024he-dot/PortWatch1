package com.portwatch.service;

import java.util.List;
import com.portwatch.domain.NewsVO;

/**
 * 뉴스 서비스 인터페이스
 * 
 * @author PortWatch
 * @version 2.0 (API 메서드 추가)
 */
public interface NewsService {
    
    /**
     * 최근 뉴스 조회 (DB에서)
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
     * 종목별 뉴스 조회 (DB에서)
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
    
    // ========================================
    // API용 추가 메서드
    // ========================================
    
    /**
     * 네이버 금융에서 실시간 뉴스 가져오기 (크롤링만, 저장 안 함)
     * @param limit 조회할 뉴스 개수
     * @return 뉴스 리스트
     */
    List<NewsVO> fetchNaverFinanceNews(int limit);
    
    /**
     * 종목별 실시간 뉴스 조회 (크롤링 후 즉시 반환)
     * @param stockCode 종목 코드
     * @param limit 조회할 뉴스 개수
     * @return 뉴스 리스트
     */
    List<NewsVO> getNewsByStock(String stockCode, int limit);
    
    /**
     * DB에 저장된 최신 뉴스 조회 (getRecentNews와 동일)
     * @param limit 조회할 뉴스 개수
     * @return 뉴스 리스트
     */
    List<NewsVO> getLatestNews(int limit);
}
