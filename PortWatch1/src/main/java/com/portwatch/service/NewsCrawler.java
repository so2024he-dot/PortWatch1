package com.portwatch.service;

import com.portwatch.domain.NewsVO;
import java.util.List;

/**
 * 뉴스 크롤러 인터페이스
 * 
 * NaverNewsCrawler, USNewsCrawler가 구현
 * 
 * @author PortWatch
 * @version 2.0 - Spring 5.0.7 + MySQL 8.0.33 호환
 */
public interface NewsCrawler {
    
    /**
     * 뉴스 크롤링
     * 
     * @param stockCode 종목 코드
     * @param stockName 종목명
     * @return 뉴스 목록
     */
    List<NewsVO> crawlNews(String stockCode, String stockName);
    
    /**
     * 크롤러 타입 반환
     * 
     * @return 크롤러 타입 (KR, US)
     */
    String getCrawlerType();
    
    /**
     * 최대 뉴스 개수 설정
     * 
     * @param maxCount 최대 개수
     */
    void setMaxCount(int maxCount);
    
    /**
     * 특정 종목 코드 지원 여부 확인
     * 
     * @param stockCode 종목 코드
     * @return 지원하면 true
     */
    boolean supports(String stockCode);
}
