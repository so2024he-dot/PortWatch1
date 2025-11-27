    package com.portwatch.persistence;

import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.PortfolioStockVO;
import java.util.List;
import java.util.Map;

/**
 * 포트폴리오 DAO 인터페이스
 */
public interface PortfolioDAO {
    
    /**
     * 포트폴리오 추가
     */
    void insertPortfolio(PortfolioVO portfolio) throws Exception;
    
    /**
     * 회원의 포트폴리오 목록 조회
     */
    List<PortfolioVO> selectPortfolioByMember(int memberId) throws Exception;
    
    /**
     * 포트폴리오 ID로 조회
     */
    PortfolioVO selectPortfolioById(long portfolioId) throws Exception;
    
    /**
     * 중복 확인
     */
    int checkDuplicate(Map<String, Object> params) throws Exception;
    
    /**
     * 포트폴리오 수정
     */
    void updatePortfolio(PortfolioVO portfolio) throws Exception;
    
    /**
     * 포트폴리오 삭제
     */
    void deletePortfolio(long portfolioId) throws Exception;
    
    /**
     * 포트폴리오 요약 정보
     */
    Map<String, Object> getPortfolioSummary(int memberId) throws Exception;
    
    /**
     * 포트폴리오에 속한 종목 목록
     */
    List<PortfolioStockVO> selectPortfolioStocks(Long portfolioId) throws Exception;
}

    
