package com.portwatch.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.portwatch.domain.PortfolioVO;

/**
 * ✅ 포트폴리오 DAO 인터페이스
 * 
 * @Param 어노테이션 완벽 적용!
 * @author PortWatch
 * @version 2.0 - MyBatis 파라미터 바인딩 수정
 */
@Mapper
public interface PortfolioDAO {
    
    /**
     * 회원의 전체 포트폴리오 조회
     */
    List<PortfolioVO> selectPortfolioByMemberId(@Param("memberId") String memberId);
    
    /**
     * 특정 종목의 포트폴리오 조회 (stockCode 기준)
     */
    PortfolioVO selectPortfolioByMemberAndStock(
        @Param("memberId") String memberId, 
        @Param("stockCode") String stockCode
    );
    
    /**
     * 포트폴리오 추가
     */
    int insertPortfolio(PortfolioVO portfolio) throws Exception;
    
    /**
     * 포트폴리오 수정 (수량, 평균가 업데이트)
     */
    int updatePortfolio(PortfolioVO portfolio);
    
    /**
     * 포트폴리오 삭제 (특정 종목)
     */
    int deletePortfolio(
        @Param("memberId") String memberId, 
        @Param("stockCode") String stockCode
    );
    
    /**
     * 포트폴리오 전체 삭제 (회원의 모든 포트폴리오)
     */
    int deleteAllPortfolio(@Param("memberId") String memberId);
    
    /**
     * 포트폴리오 보유 종목 수 조회
     */
    int countPortfolio(@Param("memberId") String memberId);
    
    /**
     * ⭐ 포트폴리오 삭제 (ID 기준)
     */
    void deletePortfolio(@Param("portfolioId") Long portfolioId);
    
    /**
     * ⭐⭐⭐ 가장 중요! stockId 기준 조회 (@Param 추가!)
     */
    PortfolioVO selectByMemberAndStock(
        @Param("memberId") String memberId, 
        @Param("stockId") Integer stockId
    );
    
    /**
     * 회원의 포트폴리오 목록 조회
     */
    List<PortfolioVO> selectPortfolioByMember(@Param("memberId") String memberId);
    
    /**
     * 포트폴리오 삭제 (ID 기준, 별칭)
     */
    void delete(@Param("portfolioId") Long portfolioId);
    
    /**
     * 포트폴리오 추가 (별칭)
     */
    void insert(PortfolioVO portfolio);
    
    /**
     * 포트폴리오 수정 (별칭)
     */
    void update(PortfolioVO portfolio);
    
    /**
     * stockCode 기준 조회 (별칭)
     */
    PortfolioVO selectByMemberAndStockCode(
        @Param("memberId") String memberId, 
        @Param("stockCode") String stockCode
    );
    
    /**
     * ID로 포트폴리오 조회
     */
    PortfolioVO selectPortfolioById(@Param("portfolioId") Long portfolioId);
    
    /**
     * 회원의 모든 포트폴리오 삭제
     */
    void deleteAllByMember(@Param("memberId") String memberId);
}
