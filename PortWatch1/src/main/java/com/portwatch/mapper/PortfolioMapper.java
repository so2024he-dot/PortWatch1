package com.portwatch.mapper;

import java.util.List;

import com.portwatch.domain.PortfolioItemVO;
import com.portwatch.domain.PortfolioStockVO;
import com.portwatch.domain.PortfolioVO;

/**
 * PortfolioMapper Interface
 * ══════════════════════════════════════════════════════════════
 * 문제: PortfolioMapper.xml은 로드됐지만 PortfolioMapper.class 미등록
 * 원인: PortfolioMapper.java 인터페이스 파일이 없었음
 * 해결: 이 파일 생성 → 자동 Bean 등록
 *
 * 주의: PortfolioStockVO.class, PortfolioItemVO.class 는
 *       콘솔 로그에서 domain에서 정상 로드됨 (이미 있음)
 * ══════════════════════════════════════════════════════════════
 */
public interface PortfolioMapper {

    // ── 포트폴리오 CRUD ──────────────────────────────────────

    /** 포트폴리오 생성 */
    int insertPortfolio(PortfolioVO portfolio);

    /** 포트폴리오 단건 조회 */
    PortfolioVO findPortfolioById(Long portfolioId);

    /** 회원별 포트폴리오 목록 조회 */
    List<PortfolioVO> findPortfolioByMemberId(Long memberId);

    /** 포트폴리오 이름 수정 */
    int updatePortfolio(PortfolioVO portfolio);

    /** 포트폴리오 삭제 */
    int deletePortfolio(Long portfolioId);

    // ── 포트폴리오 종목 CRUD ─────────────────────────────────

    /** 종목 추가 */
    int insertItem(PortfolioItemVO item);

    /** 포트폴리오별 종목 목록 조회 */
    List<PortfolioItemVO> findItemsByPortfolioId(Long portfolioId);

    /** 종목별 조회 (주식 정보 포함 JOIN) */
    List<PortfolioStockVO> findStocksWithPrice(Long portfolioId);

    /** 종목 수정 (평균단가, 수량) */
    int updateItem(PortfolioItemVO item);

    /** 종목 삭제 */
    int deleteItem(Long itemId);

    /** 포트폴리오 전체 종목 삭제 (포트폴리오 삭제 시) */
    int deleteAllItems(Long portfolioId);
}
