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
     */
    List<PortfolioVO> getPortfolioByMemberId(String memberId);
    
    /**
     * 포트폴리오 목록 조회 (별칭)
     * 
     * @param memberId 회원 ID (String)
     * @return 포트폴리오 목록
     */
    List<PortfolioVO> getPortfolioList(String memberId);
    
    /**
     * 포트폴리오 목록 조회 (Integer 버전 - Deprecated)
     * 
     * @param memberId 회원 ID (Integer)
     * @return 포트폴리오 목록
     * @deprecated Use getPortfolioList(String memberId) instead
     */
    @Deprecated
    List<PortfolioVO> getPortfolioList(Integer memberId);
    
    /**
     * 특정 종목의 포트폴리오 조회
     * 
     * @param memberId 회원 ID (String)
     * @param stockCode 종목 코드
     * @return 포트폴리오 정보
     */
    PortfolioVO getPortfolioByMemberAndStock(String memberId, String stockCode);
    
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
     * @deprecated Use getPortfolioSummary(String memberId) instead
     */
    @Deprecated
    Map<String, Object> getPortfolioSummary(Integer memberId);
    
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
     */
    boolean addStockToPortfolio(PortfolioItemVO item);
    
    /**
     * 주식 매도 - 포트폴리오에서 제거 또는 수량 감소
     * 
     * @param memberId 회원 ID (String)
     * @param stockCode 종목 코드
     * @param quantity 수량
     * @return 성공 여부
     */
    boolean removeStockFromPortfolio(String memberId, String stockCode, double quantity);
    
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
     */
    double getTotalValue(String memberId);
    
    /**
     * 포트폴리오 총 손익 계산
     * 
     * @param memberId 회원 ID (String)
     * @return 총 손익
     */
    double getTotalProfit(String memberId);
    
    /**
     * 포트폴리오 수익률 계산
     * 
     * @param memberId 회원 ID (String)
     * @return 수익률 (%)
     */
    double getTotalProfitRate(String memberId);
    
    /**
     * 중복 체크
     * 
     * @param memberId 회원 ID (String)
     * @param stockId 종목 ID
     * @return 존재하면 true
     */
    boolean checkDuplicate(String memberId, Integer stockId);
    
    /**
     * 중복 체크 (Integer 버전 - Deprecated)
     * 
     * @param memberId 회원 ID (Integer)
     * @param stockId 종목 ID
     * @return 존재하면 true
     * @deprecated Use checkDuplicate(String memberId, Integer stockId) instead
     */
    @Deprecated
    boolean checkDuplicate(Integer memberId, Integer stockId);
    
    /**
     * 포트폴리오 삭제 (특정 종목)
     * 
     * @param memberId 회원 ID (String)
     * @param stockCode 종목 코드
     * @return 성공 여부
     */
    boolean deletePortfolio(String memberId, String stockCode);
    
    /**
     * 포트폴리오 삭제 (portfolioId)
     * 
     * @param portfolioId 포트폴리오 ID
     */
    void deletePortfolio(Long portfolioId);
    
    /**
     * 포트폴리오 전체 삭제
     * 
     * @param memberId 회원 ID (String)
     * @return 성공 여부
     */
    boolean deleteAllPortfolio(String memberId);
    
    /**
     * 포트폴리오 전체 삭제 (portfolioId 버전)
     * 
     * @param portfolioId 포트폴리오 ID
     */
    void deleteAllPortfolio(Long portfolioId);
    
    /**
     * PortfolioApiController 전용 메서드들
     */
    
    /**
     * 포트폴리오 조회 (회원 + 종목)
     * 
     * @param memberId 회원 ID (Integer - PortfolioApiController 호환)
     * @param stockId 종목 ID
     * @return 포트폴리오 정보
     */
    PortfolioVO getByMemberAndStock(Integer memberId, Integer stockId);
    
    /**
     * 포트폴리오 업데이트 (PortfolioApiController 호환)
     * 
     * @param portfolio 포트폴리오 정보
     */
    void update(PortfolioVO portfolio);
    
    /**
     * 포트폴리오 추가 (PortfolioApiController 호환)
     * 
     * @param portfolio 포트폴리오 정보
     */
    void insert(PortfolioVO portfolio);
    
    /**
     * 포트폴리오 삭제 (PortfolioApiController 호환)
     * 
     * @param portfolioId 포트폴리오 ID (Integer)
     */
    void delete(Integer portfolioId);

	boolean addStockToPortfolio(PortfolioVO portfolio);
}
