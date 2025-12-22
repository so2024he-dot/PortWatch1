package com.portwatch.service;

import java.util.List;
import java.util.Map;

import com.portwatch.domain.PortfolioItemVO;
import com.portwatch.domain.PortfolioVO;

/**
 * ✅ 포트폴리오 서비스 인터페이스
 * 
 * @author PortWatch
 * @version 1.0
 */
public interface PortfolioService {
    
    /**
     * 회원의 전체 포트폴리오 조회
     * @param memberId 회원 ID
     * @return 포트폴리오 목록
     */
    List<PortfolioVO> getPortfolioByMemberId(String memberId);
    
    /**
     * 특정 종목의 포트폴리오 조회
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @return 포트폴리오 정보 (없으면 null)
     */
    PortfolioVO getPortfolioByMemberAndStock(String memberId, String stockCode);
    
    /**
     * ✅ 주식 매수 - 포트폴리오에 추가 또는 수량 증가
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @param quantity 매수 수량
     * @param price 매수 단가
     * @return 성공 여부
     */
    boolean addStockToPortfolio(String memberId, String stockCode, double quantity, double price);
    
    /**
     * 주식 매도 - 포트폴리오에서 수량 감소 또는 삭제
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @param quantity 매도 수량
     * @param price 매도 단가
     * @return 성공 여부
     */
    boolean removeStockFromPortfolio(String memberId, String stockCode, double quantity, double price);
    
    /**
     * 포트폴리오 총 평가액 계산
     * @param memberId 회원 ID
     * @return 총 평가액
     */
    double getTotalValue(String memberId);
    
    /**
     * 포트폴리오 총 손익 계산
     * @param memberId 회원 ID
     * @return 총 손익
     */
    double getTotalProfit(String memberId);
    
    /**
     * 포트폴리오 수익률 계산
     * @param memberId 회원 ID
     * @return 수익률(%)
     */
    double getTotalProfitRate(String memberId);
    
    /**
     * 포트폴리오 삭제 (특정 종목)
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @return 성공 여부
     */
    boolean deletePortfolio(String memberId, String stockCode);


    
    /**
     * 포트폴리오 전체 삭제 (회원의 모든 포트폴리오)
     * @param memberId 회원 ID
     * @return 성공 여부
     */
	
	  boolean deleteAllPortfolio(String memberId);
	  
	  Map<String, Object> getPortfolioSummary(String memberId);
	  
	  List<PortfolioVO> getPortfolioList(Integer memberId);
	  
	  Map<String, Object> getPortfolioSummary(Integer memberId);
	  
	  boolean addStockToPortfolio(PortfolioItemVO item);
	  
	  void updatePortfolio(PortfolioVO portfolio);
	  
	  void deleteAllPortfolio(Long portfolioId);
	  
	  boolean checkDuplicate(String memberId, Integer stockId);
	  
	  void deletePortfolio(Long portfolioId);
	  
	  List<PortfolioVO> getPortfolioList(String memberId);
	 
}
