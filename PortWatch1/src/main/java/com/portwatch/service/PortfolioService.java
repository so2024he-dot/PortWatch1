package com.portwatch.service;

import com.portwatch.domain.PortfolioVO;
import java.util.List;

/**
 * 포트폴리오 Service 인터페이스
 * 
 * @author PortWatch
 * @version 3.0
 */
public interface PortfolioService {
    
    /**
     * 포트폴리오 추가
     */
    void insert(PortfolioVO portfolioVO);
    
    /**
     * 포트폴리오 수정 (추가 매입 시 사용)
     */
    void update(PortfolioVO portfolioVO);
    
    /**
     * 포트폴리오 삭제
     */
    void delete(int portfolioId);
    
    /**
     * 회원의 전체 포트폴리오 조회
     */
    List<PortfolioVO> getByMember(int memberId);
    
    /**
     * 특정 포트폴리오 조회
     */
    PortfolioVO getById(int portfolioId);
    
    /**
     * 회원의 특정 종목 보유 여부 확인 (추가 매입 지원)
     * 
     * @param memberId 회원 ID
     * @param stockId 종목 ID
     * @return 이미 보유 중이면 PortfolioVO 반환, 없으면 null
     */
    PortfolioVO getByMemberAndStock(int memberId, int stockId);
}
