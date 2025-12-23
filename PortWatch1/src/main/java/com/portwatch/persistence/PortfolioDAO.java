package com.portwatch.persistence;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.portwatch.domain.PortfolioVO;

/**
 * ✅ 포트폴리오 DAO 인터페이스
 * 
 * @author PortWatch
 * @version 1.0
 */
@Mapper
public interface PortfolioDAO {
    
    /**
     * 회원의 전체 포트폴리오 조회
     * @param memberId 회원 ID
     * @return 포트폴리오 목록
     */
    List<PortfolioVO> selectPortfolioByMemberId(@Param("memberId") String memberId);
    
    /**
     * 특정 종목의 포트폴리오 조회
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @return 포트폴리오 (없으면 null)
     */
    PortfolioVO selectPortfolioByMemberAndStock(
        @Param("memberId") String memberId, 
        @Param("stockCode") String stockCode
    );
    
    /**
     * 포트폴리오 추가
     * @param portfolio 포트폴리오 정보
     * @return 추가된 행 수
     * @throws Exception 
     */
    int insertPortfolio(PortfolioVO portfolio) throws Exception;
    
    /**
     * 포트폴리오 수정 (수량, 평균가 업데이트)
     * @param portfolio 포트폴리오 정보
     * @return 수정된 행 수
     */
    int updatePortfolio(PortfolioVO portfolio);
    
    /**
     * 포트폴리오 삭제 (특정 종목)
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @return 삭제된 행 수
     */
    int deletePortfolio(
        @Param("memberId") String memberId, 
        @Param("stockCode") String stockCode
    );
    
    /**
     * 포트폴리오 전체 삭제 (회원의 모든 포트폴리오)
     * @param memberId 회원 ID
     * @return 삭제된 행 수
     */
    int deleteAllPortfolio(@Param("memberId") String memberId);
    
    /**
     * 포트폴리오 보유 종목 수 조회
     * @param memberId 회원 ID
     * @return 종목 수
     */
    int countPortfolio(@Param("memberId") String memberId);

	void deletePortfolio(Long portfolioId);

	void deletePortfolioByMemberAndStock(String memberId, Integer stockId);
}
