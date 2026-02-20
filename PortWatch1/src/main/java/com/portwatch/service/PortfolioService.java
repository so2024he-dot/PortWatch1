package com.portwatch.service;

import java.util.List;

import com.portwatch.domain.PortfolioItemVO;
import com.portwatch.domain.PortfolioStockVO;
import com.portwatch.domain.PortfolioVO;

/**
 * PortfolioService Interface - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ Controller 호출 메서드 100% 커버
 * ✅ String memberId 버전 지원
 * ✅ 4개 파라미터 addStockToPortfolio 지원
 * ══════════════════════════════════════════════════════════════
 */
public interface PortfolioService {

    // ─────────────────────────────────────────
    // 포트폴리오 CRUD
    // ─────────────────────────────────────────

    /** 포트폴리오 생성 */
    int createPortfolio(PortfolioVO portfolio);

    /**
     * ✅ 회원별 포트폴리오 목록 조회 (Long 버전)
     * PortfolioMapper.findPortfolioByMemberId 호출
     */
    List<PortfolioVO> getPortfoliosByMemberId(Long memberId);

    /**
     * ✅ 회원별 포트폴리오 목록 조회 (String 버전)
     * DashboardController, PortfolioController에서 호출
     */
    List<PortfolioVO> getPortfolioByMemberId(String memberId);

    /**
     * ✅ 회원별 포트폴리오 목록 조회 (별칭 - String 버전)
     * PortfolioApiController에서 호출
     */
    List<PortfolioVO> getPortfolioList(String memberId);

    /**
     * ✅ 포트폴리오 단건 조회 (Long 버전)
     */
    PortfolioVO getPortfolioById(Long portfolioId);

    /**
     * ✅ 포트폴리오 단건 조회 (별칭)
     * PortfolioApiController, PortfolioController에서 호출
     */
    PortfolioVO getPortfolio(Long portfolioId);

    /** 포트폴리오 수정 */
    int updatePortfolio(PortfolioVO portfolio);

    /** 포트폴리오 삭제 */
    int deletePortfolio(Long portfolioId);

    // ─────────────────────────────────────────
    // 포트폴리오 종목 관리
    // ─────────────────────────────────────────

    /**
     * ✅ 종목 추가 (PortfolioItemVO 버전)
     */
    int addStockToPortfolio(PortfolioItemVO item);

    /**
     * ✅ 종목 추가 (4개 파라미터 버전 - 신규!)
     * StockPurchaseApiController에서 호출
     * 
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @param quantity 수량
     * @param price 매입 가격
     * @return 성공 여부
     */
    boolean addStockToPortfolio(String memberId, String stockCode, double quantity, double price);

    /** 포트폴리오 종목 목록 조회 */
    List<PortfolioItemVO> getItemsByPortfolioId(Long portfolioId);

    /** 종목 수정 */
    int updateItem(PortfolioItemVO item);

    /** 종목 삭제 */
    int deleteItem(Long itemId);

    // ─────────────────────────────────────────
    // 포트폴리오 상세 (손익 계산 포함)
    // ─────────────────────────────────────────

    /** 포트폴리오 상세 조회 (현재가 + 손익 계산) */
    List<PortfolioStockVO> getPortfolioWithStocks(Long portfolioId);

    /** 포트폴리오 요약 통계 */
    PortfolioVO getPortfolioSummary(Long portfolioId);

    // ─────────────────────────────────────────
    // 유틸리티
    // ─────────────────────────────────────────

    /** 종목 중복 체크 */
    boolean isStockInPortfolio(Long portfolioId, String stockCode);

    /** 포트폴리오 종목 수 */
    int getStockCount(Long portfolioId);
}
