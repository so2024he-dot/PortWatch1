    package com.portwatch.service;

import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.PortfolioStockVO;
import java.util.List;
import java.util.Map;

/**
 * 포트폴리오 서비스 인터페이스
 * 더미 데이터 없이 DB 데이터만 사용
 */
public interface PortfolioService {
    
    /**
     * 포트폴리오에 종목 추가
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
     * 포트폴리오 요약 정보 (총 투자금액, 평가금액, 손익 등)
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
    
    /**
     * 포트폴리오에 속한 종목 목록 조회
     */
    List<PortfolioStockVO> getPortfolioStocks(Long portfolioId) throws Exception;
}

    
