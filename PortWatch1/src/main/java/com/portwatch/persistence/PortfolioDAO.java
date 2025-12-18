package com.portwatch.persistence;

import org.apache.ibatis.annotations.Mapper;
import com.portwatch.domain.PortfolioVO;
import java.util.List;
import java.util.Map;

/**
 * 포트폴리오 DAO 인터페이스
 * MyBatis가 자동으로 구현체를 생성
 * 
 * ✅ 이전 작동 버전 기반
 * ✅ 간단하고 명확한 메서드명
 * 
 * @author PortWatch
 * @version 5.0 (Spring 5.0.7 + MySQL 8.0)
 */
@Mapper  // ⭐ MyBatis Mapper 인터페이스임을 명시
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
     * 중복 확인 (회원 + 종목)
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
}
