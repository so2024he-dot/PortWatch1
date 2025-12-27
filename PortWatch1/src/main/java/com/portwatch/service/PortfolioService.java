package com.portwatch.service;

import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.PortfolioItemVO;
import java.util.List;
import java.util.Map;

/**
 * 포트폴리오 Service 인터페이스
 * 
 * ✅ String memberId 사용
 * @author PortWatch
 * @version 2.0 - Spring 5.0.7 + MySQL 8.0.33 호환
 */
public interface PortfolioService {
    
    /**
     * 회원의 전체 포트폴리오 조회
     * 
     * @param memberId 회원 ID (String)
     * @return 포트폴리오 목록
     * @throws Exception 
     */
    List<PortfolioVO> getPortfolioByMemberId(String memberId) throws Exception;
    
    /**
     * 포트폴리오 목록 조회 (별칭)
     * 
     * @param memberId 회원 ID (String)
     * @return 포트폴리오 목록
     * @throws Exception 
     */
    List<PortfolioVO> getPortfolioList(String memberId) throws Exception;
    
    /**
     * 포트폴리오 목록 조회 (Integer 버전 - Deprecated)
     * 
     * @param memberId 회원 ID (Integer)
     * @return 포트폴리오 목록
     * @throws Exception 
     * @deprecated Use getPortfolioList(String memberId) instead
     */
    @Deprecated
    List<PortfolioVO> getPortfolioList(Integer memberId) throws Exception;
    
    /**
     * 특정 종목의 포트폴리오 조회
     * 
     * @param memberId 회원 ID (String)
     * @param stockCode 종목 코드
     * @return 포트폴리오 정보
     * @throws Exception 
     */
    PortfolioVO getPortfolioByMemberAndStock(String memberId, String stockCode) throws Exception;
    
    /**
     * 포트폴리오 요약 정보 조회
     * 
     * @param memberId 회원 ID (String)
     * @return 요약 정보 (총 평가액, 총 손익, 수익률 등)
     * @throws Exception 
     */
    Map<String, Object> getPortfolioSummary(String memberId) throws Exception;
    
    /**
     * 포트폴리오 요약 정보 조회 (Integer 버전 - Deprecated)
     * 
     * @param memberId 회원 ID (Integer)
     * @return 요약 정보
     * @throws Exception 
     * @deprecated Use getPortfolioSummary(String memberId) instead
     */
    @Deprecated
    Map<String, Object> getPortfolioSummary(Integer memberId) throws Exception;
    
    /**
     * 주식 매수 - 포트폴리오에 추가 또는 수량 증가
     * 
     * @param memberId 회원 ID (String)
     * @param stockCode 종목 코드
     * @param quantity 수량
     * @param price 매입 가격
     * @return 성공 여부
     * @throws Exception 
     */
    boolean addStockToPortfolio(String memberId, String stockCode, double quantity, double price) throws Exception;
    
    /**
     * 주식 매수 - PortfolioItemVO 사용
     * 
     * @param item 포트폴리오 아이템
     * @return 성공 여부
     * @throws Exception 
     */
    boolean addStockToPortfolio(PortfolioItemVO item) throws Exception;
    
    /**
     * 주식 매도 - 포트폴리오에서 제거 또는 수량 감소
     * 
     * @param memberId 회원 ID (String)
     * @param stockCode 종목 코드
     * @param quantity 수량
     * @return 성공 여부
     * @throws Exception 
     */
    boolean removeStockFromPortfolio(String memberId, String stockCode, double quantity) throws Exception;
    
    /**
     * 포트폴리오 업데이트
     * 
     * @param portfolio 포트폴리오 정보
     * @throws Exception 
     */
    void updatePortfolio(PortfolioVO portfolio) throws Exception;
    
    /**
     * 포트폴리오 총 평가액 계산
     * 
     * @param memberId 회원 ID (String)
     * @return 총 평가액
     * @throws Exception 
     */
    double getTotalValue(String memberId) throws Exception;
    
    /**
     * 포트폴리오 총 손익 계산
     * 
     * @param memberId 회원 ID (String)
     * @return 총 손익
     * @throws Exception 
     */
    double getTotalProfit(String memberId) throws Exception;
    
    /**
     * 포트폴리오 수익률 계산
     * 
     * @param memberId 회원 ID (String)
     * @return 수익률 (%)
     * @throws Exception 
     */
    double getTotalProfitRate(String memberId) throws Exception;
    
    /**
     * 중복 체크
     * 
     * @param memberId 회원 ID (String)
     * @param stockId 종목 ID
     * @return 존재하면 true
     * @throws Exception 
     */
    boolean checkDuplicate(String memberId, Integer stockId) throws Exception;
    
    /**
     * 중복 체크 (Integer 버전 - Deprecated)
     * 
     * @param memberId 회원 ID (Integer)
     * @param stockId 종목 ID
     * @return 존재하면 true
     * @throws Exception 
     * @deprecated Use checkDuplicate(String memberId, Integer stockId) instead
     */
    @Deprecated
    boolean checkDuplicate(Integer memberId, Integer stockId) throws Exception;
    
    /**
     * 포트폴리오 삭제 (특정 종목)
     * 
     * @param memberId 회원 ID (String)
     * @param stockCode 종목 코드
     * @return 성공 여부
     * @throws Exception 
     */
    boolean deletePortfolio(String memberId, String stockCode) throws Exception;
    
    /**
     * 포트폴리오 삭제 (portfolioId)
     * 
     * @param portfolioId 포트폴리오 ID
     * @throws Exception 
     */
    void deletePortfolio(Long portfolioId) throws Exception;
    
    /**
     * 포트폴리오 전체 삭제
     * 
     * @param memberId 회원 ID (String)
     * @return 성공 여부
     * @throws Exception 
     */
    void deleteAllPortfolio(String memberId) throws Exception;
    
    /**
     * 포트폴리오 전체 삭제 (portfolioId 버전)
     * 
     * @param portfolioId 포트폴리오 ID
     * @throws Exception 
     */
    void deleteAllPortfolio(Long portfolioId) throws Exception;
    
    /**
     * PortfolioApiController 전용 메서드들
     */
    
    /**
     * 포트폴리오 조회 (회원 + 종목)
     * 
     * @param memberId 회원 ID (Integer - PortfolioApiController 호환)
     * @param stockId 종목 ID
     * @return 포트폴리오 정보
     * @throws Exception 
     */
    PortfolioVO getByMemberAndStock(Integer memberId, Integer stockId) throws Exception;
    
    /**
     * 포트폴리오 업데이트 (PortfolioApiController 호환)
     * 
     * @param portfolio 포트폴리오 정보
     * @throws Exception 
     */
    void update(PortfolioVO portfolio) throws Exception;
    
    /**
     * 포트폴리오 추가 (PortfolioApiController 호환)
     * 
     * @param portfolio 포트폴리오 정보
     * @throws Exception 
     */
    void insert(PortfolioVO portfolio) throws Exception;
    
    /**
     * 포트폴리오 삭제 (PortfolioApiController 호환)
     * 
     * @param portfolioId 포트폴리오 ID (Integer)
     * @throws Exception 
     */
    void delete(Integer portfolioId) throws Exception;

	boolean addStockToPortfolio(PortfolioVO portfolio) throws Exception;

	/**
	 * ✅ 포트폴리오 조회 (ID로)
	 * 
	 * @param portfolioId 포트폴리오 ID
	 * @return 포트폴리오 정보
	 */
	PortfolioVO getPortfolioById(Long portfolioId) throws Exception;
}
