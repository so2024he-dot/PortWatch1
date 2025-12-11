package com.portwatch.persistence;

import com.portwatch.domain.PortfolioVO;
import java.util.List;
import java.util.Map;

/**
 * 포트폴리오 DAO 인터페이스
 * 
 * @author PortWatch
 * @version 3.0
 */
public interface PortfolioDAO {
    
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
    List<PortfolioVO> selectByMember(int memberId);
    
    /**
     * 특정 포트폴리오 조회
     */
    PortfolioVO selectById(int portfolioId);
    
    /**
     * 회원의 특정 종목 보유 여부 확인 (추가 매입 지원)
     * 
     * @param params Map with memberId and stockId
     * @return 이미 보유 중이면 PortfolioVO 반환, 없으면 null
     */
    PortfolioVO selectByMemberAndStock(Map<String, Object> params);
}
