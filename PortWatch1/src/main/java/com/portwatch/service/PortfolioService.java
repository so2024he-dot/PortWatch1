package com.portwatch.service;

import com.portwatch.domain.PortfolioVO;
import java.util.List;
import java.util.Map;

/**
 * 포트폴리오 Service 인터페이스
 * 
 * ✅ 이전 작동 버전 기반
 * 
 * @author PortWatch
 * @version 5.0 (Spring 5.0.7 + MySQL 8.0)
 */
public interface PortfolioService {
    
    /**
     * 포트폴리오 추가
     */
    void addPortfolio(PortfolioVO portfolio) throws Exception;
    
    /**
     * 회원의 포트폴리오 목록 조회
     */
    List<PortfolioVO> getPortfolioList(int memberId) throws Exception;
    
    /**
     * 포트폴리오 ID로 조회
     */
    PortfolioVO getPortfolioById(Long portfolioId) throws Exception;
    
    /**
     * 포트폴리오 요약 정보
     */
    Map<String, Object> getPortfolioSummary(int memberId) throws Exception;
    
    /**
     * 포트폴리오 수정
     */
    void updatePortfolio(PortfolioVO portfolio) throws Exception;
    
    /**
     * 포트폴리오 삭제
     */
    void deletePortfolio(long portfolioId) throws Exception;
}
