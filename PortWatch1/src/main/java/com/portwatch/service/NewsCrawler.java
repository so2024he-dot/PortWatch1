package com.portwatch.service;

import com.portwatch.domain.NewsVO;
import java.util.List;

/**
 * ✅ 뉴스 크롤러 인터페이스
 * 
 * NaverNewsCrawler, USNewsCrawler가 구현
 * 
 * @author PortWatch
 * @version 3.0
 */
public interface NewsCrawler {
    
    /**
     * 뉴스 크롤링
     * 
     * @param stockCode 종목 코드
     * @param stockName 종목명
     * @return 크롤링된 뉴스 목록
     */
    List<NewsVO> crawlNews(String stockCode, String stockName);
    
    /**
     * 크롤러 타입 반환 (KR, US)
     */
    String getCrawlerType();
    
    /**
     * 최대 뉴스 개수 설정
     */
    void setMaxCount(int maxCount);
    
    /**
     * 특정 종목 코드 지원 여부
     */
    boolean supports(String stockCode);
}
