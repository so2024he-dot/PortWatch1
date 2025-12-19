package com.portwatch.service;

import com.portwatch.domain.NewsVO;
import java.util.List;

/**
 * 뉴스 크롤러 인터페이스
 * 
 * ✅ 느슨한 결합 (Loose Coupling)
 * ✅ 한국/미국 크롤러 통일된 인터페이스
 * ✅ 테스트 용이성
 * 
 * @author PortWatch
 * @version 8.0
 */
public interface NewsCrawler {
    
	 boolean supports(String stockCode);
    /**
     * 주식 뉴스 크롤링
     * 
     * @param stockCode 종목 코드
     * @param stockName 종목명
     * @return 뉴스 목록 (NewsVO)
     */
    List<NewsVO> crawlNews(String stockCode, String stockName);
    
    /**
     * 크롤러 타입 반환 (KR/US)
     * 
     * @return 국가 코드
     */
    String getCrawlerType();
    
    /**
     * 최대 크롤링 개수 설정
     * 
     * @param maxCount 최대 개수
     */
    void setMaxCount(int maxCount);
    
   
   
}
