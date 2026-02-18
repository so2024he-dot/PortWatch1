package com.portwatch.service;

import java.util.List;

import com.portwatch.domain.PortfolioItemVO;
import com.portwatch.domain.PortfolioStockVO;
import com.portwatch.domain.PortfolioVO;

/**
 * PortfolioService Interface
 * ══════════════════════════════════════════════════════════════
 * PortfolioServiceImpl 구현체의 인터페이스
 * ══════════════════════════════════════════════════════════════
 */
public interface PortfolioService {

    // ─────────────────────────────────────────
    // 포트폴리오 CRUD
    // ─────────────────────────────────────────

    /** 포트폴리오 생성 */
    int createPortfolio(PortfolioVO portfolio);

    /** 회원별 포트폴리오 목록 조회 */
    List<PortfolioVO> getPortfoliosByMemberId(Long memberId);

    /** 포트폴리오 단건 조회 */
    PortfolioVO getPortfolioById(Long portfolioId);

    /** 포트폴리오 수정 */
    int updatePortfolio(PortfolioVO portfolio);

    /** 포트폴리오 삭제 */
    int deletePortfolio(Long portfolioId);

    // ─────────────────────────────────────────
    // 포트폴리오 종목 관리
    // ─────────────────────────────────────────

    /** 종목 추가 */
    int addStockToPortfolio(PortfolioItemVO item);

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
