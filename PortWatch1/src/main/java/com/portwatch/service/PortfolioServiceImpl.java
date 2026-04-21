package com.portwatch.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.PortfolioItemVO;
import com.portwatch.domain.PortfolioStockVO;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.StockVO;
import com.portwatch.mapper.PortfolioMapper;
import com.portwatch.mapper.StockMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * PortfolioServiceImpl - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ Deprecated API 완전 수정 (BigDecimal 연산)
 * ✅ Controller 호출 메서드 100% 구현
 * ✅ String memberId 버전 지원
 * ✅ 4개 파라미터 addStockToPortfolio 구현
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@Service
public class PortfolioServiceImpl implements PortfolioService {

    @Autowired
    private PortfolioMapper portfolioMapper;

    @Autowired
    private StockMapper stockMapper;

    /* ══════════════════════════════════════════════════════════
     * 포트폴리오 CRUD
     * ══════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public int createPortfolio(PortfolioVO portfolio) {
        portfolio.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        portfolio.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return portfolioMapper.insertPortfolio(portfolio);
    }

    /**
     * ✅ 회원별 포트폴리오 목록 (Long 버전 - 기존 유지)
     */
    @Override
    public List<PortfolioVO> getPortfoliosByMemberId(Long memberId) {
        return portfolioMapper.findPortfolioByMemberId(memberId);
    }

    /**
     * ✅ 회원별 포트폴리오 목록 (String 버전 - 직접 String 조회)
     * ══════════════════════════════════════════════════════════════
     * [수정] MemberVO.memberId = VARCHAR(50) ("admin", "kim123" 등)
     *        기존: Long.parseLong() 시도 → NumberFormatException → 빈 목록
     *        수정: findPortfolioByMemberIdStr(String) 직접 호출
     * ══════════════════════════════════════════════════════════════
     */
    @Override
    public List<PortfolioVO> getPortfolioByMemberId(String memberId) {
        try {
            if (memberId == null || memberId.trim().isEmpty()) {
                log.warn("memberId is null or empty");
                return new ArrayList<>();
            }
            return portfolioMapper.findPortfolioByMemberIdStr(memberId);
        } catch (Exception e) {
            log.error("포트폴리오 조회 실패: memberId={}", memberId, e);
            return new ArrayList<>();
        }
    }

    /**
     * ✅ 회원별 포트폴리오 목록 (별칭 - String 버전)
     * PortfolioApiController 호출용
     */
    @Override
    public List<PortfolioVO> getPortfolioList(String memberId) {
        return getPortfolioByMemberId(memberId);  // 위임
    }

    /**
     * ✅ 포트폴리오 단건 조회 (Long 버전 - 기존 유지)
     */
    @Override
    public PortfolioVO getPortfolioById(Long portfolioId) {
        return portfolioMapper.findPortfolioById(portfolioId);
    }

    /**
     * ✅ 포트폴리오 단건 조회 (별칭 - 신규!)
     * PortfolioApiController, PortfolioController 호출용
     */
    @Override
    public PortfolioVO getPortfolio(Long portfolioId) {
        return getPortfolioById(portfolioId);  // 위임
    }

    @Override
    @Transactional
    public int updatePortfolio(PortfolioVO portfolio) {
        portfolio.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return portfolioMapper.updatePortfolio(portfolio);
    }

    @Override
    @Transactional
    public int deletePortfolio(Long portfolioId) {
        portfolioMapper.deleteAllItems(portfolioId);
        return portfolioMapper.deletePortfolio(portfolioId);
    }

    /* ══════════════════════════════════════════════════════════
     * 포트폴리오 종목 관리
     * ══════════════════════════════════════════════════════════ */

    /**
     * ✅ 종목 추가 (PortfolioItemVO 버전 - 기존 유지)
     */
    @Override
    @Transactional
    public int addStockToPortfolio(PortfolioItemVO item) {
        item.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return portfolioMapper.insertItem(item);
    }

    /**
     * ✅ 종목 추가 (4개 파라미터 버전 - 완전 수정!)
     * StockPurchaseApiController 호출용
     *
     * ══════════════════════════════════════════════════════════════
     * [수정 핵심 - 2026-04-21]
     *   1. portfolioMapper.insertPortfolio 직접 호출
     *      → createPortfolio(this.xxx) 자기 호출은 @Transactional 무시됨
     *      → 직접 mapper 호출로 변경
     *   2. PORTFOLIO FK 제약 없이 삽입 가능하도록 SQL 수정 예정
     *      (EC2 SQL: ALTER TABLE PORTFOLIO DROP FOREIGN KEY fk_portfolio_member)
     *   3. 게스트 사용자("guest_user") 지원:
     *      FK 제약 제거 후 insertPortfolio 정상 작동
     *   4. 상세 에러 로깅 추가 (원인 파악 용이)
     * ══════════════════════════════════════════════════════════════
     */
    @Override
    @Transactional
    public boolean addStockToPortfolio(String memberId, String stockCode, double quantity, double price) {
        log.info("════ 종목 추가 시작 ════");
        log.info("  memberId={}, stockCode={}, quantity={}, price={}",
                 memberId, stockCode, quantity, price);

        try {
            // ── 유효성 검사 ──
            if (memberId == null || memberId.trim().isEmpty()) {
                log.error("  ❌ memberId 비어있음");
                return false;
            }
            if (stockCode == null || stockCode.trim().isEmpty()) {
                log.error("  ❌ stockCode 비어있음");
                return false;
            }

            // ── STEP 1: 포트폴리오 조회 ──
            List<PortfolioVO> portfolios = portfolioMapper.findPortfolioByMemberIdStr(memberId);
            log.info("  포트폴리오 조회 결과: {}건", portfolios == null ? "null" : portfolios.size());

            Long portfolioId;
            if (portfolios == null || portfolios.isEmpty()) {
                // 포트폴리오가 없으면 신규 생성
                log.info("  포트폴리오 없음 → 신규 생성");
                PortfolioVO newPortfolio = new PortfolioVO();
                newPortfolio.setMemberId(memberId);
                newPortfolio.setPortfolioName("기본 포트폴리오");
                newPortfolio.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
                newPortfolio.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

                try {
                    int inserted = portfolioMapper.insertPortfolio(newPortfolio);
                    log.info("  insertPortfolio 결과: {} (생성된 portfolioId={})", inserted, newPortfolio.getPortfolioId());
                } catch (Exception ex) {
                    log.error("  ❌ insertPortfolio 실패: {}", ex.getMessage(), ex);
                    // FK 제약 오류(1452)이면 게스트 계정일 가능성 → 에러 메시지 명확히
                    if (ex.getMessage() != null && (ex.getMessage().contains("1452") || ex.getMessage().contains("foreign key"))) {
                        log.error("  ❌ FK 제약 위반: member_id='{}' 가 MEMBER 테이블에 없음", memberId);
                        log.error("     EC2 SQL 실행 필요: ALTER TABLE PORTFOLIO DROP FOREIGN KEY fk_portfolio_member");
                    }
                    return false;
                }

                // 생성된 portfolioId 사용 (useGeneratedKeys=true)
                if (newPortfolio.getPortfolioId() != null && newPortfolio.getPortfolioId() > 0) {
                    portfolioId = newPortfolio.getPortfolioId();
                } else {
                    // 다시 조회
                    portfolios = portfolioMapper.findPortfolioByMemberIdStr(memberId);
                    if (portfolios == null || portfolios.isEmpty()) {
                        log.error("  ❌ 포트폴리오 생성 후 재조회 실패");
                        return false;
                    }
                    portfolioId = portfolios.get(0).getPortfolioId();
                }
            } else {
                portfolioId = portfolios.get(0).getPortfolioId();
                log.info("  기존 portfolioId={} 사용", portfolioId);
            }

            // ── STEP 2: 종목 정보 조회 ──
            StockVO stock = stockMapper.findByCode(stockCode);
            if (stock == null) {
                log.error("  ❌ 종목 정보 없음: stockCode={}", stockCode);
                return false;
            }
            log.info("  종목 확인: {} ({})", stock.getStockName(), stockCode);

            // ── STEP 3: PORTFOLIO_ITEM 삽입 ──
            PortfolioItemVO item = new PortfolioItemVO();
            item.setPortfolioId(portfolioId);
            item.setStockCode(stockCode);
            item.setQuantity(BigDecimal.valueOf(quantity));
            item.setAvgPrice(BigDecimal.valueOf(price));
            item.setPurchasePrice(BigDecimal.valueOf(price));
            item.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

            int result;
            try {
                result = portfolioMapper.insertItem(item);
                log.info("  insertItem 결과: {} (itemId={})", result, item.getItemId());
            } catch (Exception ex) {
                log.error("  ❌ insertItem 실패: {}", ex.getMessage(), ex);
                return false;
            }

            if (result > 0) {
                log.info("════ 종목 추가 완료 ✅ ════");
                return true;
            } else {
                log.warn("  ⚠️ insertItem 결과 0 (영향받은 행 없음)");
                return false;
            }

        } catch (Exception e) {
            log.error("════ 종목 추가 실패 ❌ ════: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<PortfolioItemVO> getItemsByPortfolioId(Long portfolioId) {
        return portfolioMapper.findItemsByPortfolioId(portfolioId);
    }

    @Override
    @Transactional
    public int updateItem(PortfolioItemVO item) {
        return portfolioMapper.updateItem(item);
    }

    @Override
    @Transactional
    public int deleteItem(Long itemId) {
        return portfolioMapper.deleteItem(itemId);
    }

    /* ══════════════════════════════════════════════════════════
     * 포트폴리오 상세 조회 (현재가 + 손익 계산)
     * ══════════════════════════════════════════════════════════ */

    @Override
    public List<PortfolioStockVO> getPortfolioWithStocks(Long portfolioId) {
        List<PortfolioStockVO> stocks = portfolioMapper.findStocksWithPrice(portfolioId);

        for (PortfolioStockVO stock : stocks) {
            calculateProfitAndLoss(stock);
        }

        return stocks;
    }

    /**
     * ✅ Deprecated 수정 핵심 메서드 (BigDecimal 연산)
     */
    private void calculateProfitAndLoss(PortfolioStockVO stock) {
        try {
            BigDecimal currentPrice = stock.getCurrentPrice();
            BigDecimal avgPrice     = stock.getAvgPrice();
            Long quantity           = stock.getQuantity();

            if (currentPrice == null || avgPrice == null || quantity == null || quantity == 0) {
                stock.setProfitLoss(BigDecimal.ZERO);
                stock.setProfitRate(BigDecimal.ZERO);
                return;
            }

            BigDecimal qty = BigDecimal.valueOf(quantity);
            BigDecimal profitLoss = currentPrice.subtract(avgPrice).multiply(qty);
            stock.setProfitLoss(profitLoss);

            BigDecimal diff = currentPrice.subtract(avgPrice);
            BigDecimal profitRate = diff.divide(avgPrice, 4, RoundingMode.HALF_UP)
                                        .multiply(BigDecimal.valueOf(100));
            stock.setProfitRate(profitRate);

            BigDecimal totalInvest = avgPrice.multiply(qty);
            stock.setTotalInvestment(totalInvest);

            BigDecimal currentValue = currentPrice.multiply(qty);
            stock.setCurrentValue(currentValue);

        } catch (ArithmeticException e) {
            log.error("손익 계산 오류 (0으로 나누기): {}", stock.getStockCode(), e);
            stock.setProfitLoss(BigDecimal.ZERO);
            stock.setProfitRate(BigDecimal.ZERO);
        } catch (Exception e) {
            log.error("손익 계산 오류: {}", stock.getStockCode(), e);
            stock.setProfitLoss(BigDecimal.ZERO);
            stock.setProfitRate(BigDecimal.ZERO);
        }
    }

    /* ══════════════════════════════════════════════════════════
     * 포트폴리오 요약 통계
     * ══════════════════════════════════════════════════════════ */

    @Override
    public PortfolioVO getPortfolioSummary(Long portfolioId) {
        PortfolioVO portfolio = portfolioMapper.findPortfolioById(portfolioId);
        if (portfolio == null) return null;

        List<PortfolioStockVO> stocks = getPortfolioWithStocks(portfolioId);

        BigDecimal totalInvestment = BigDecimal.ZERO;
        BigDecimal totalCurrentValue = BigDecimal.ZERO;
        BigDecimal totalProfitLoss = BigDecimal.ZERO;

        for (PortfolioStockVO stock : stocks) {
            if (stock.getTotalInvestment() != null) {
                totalInvestment = totalInvestment.add(stock.getTotalInvestment());
            }
            if (stock.getCurrentValue() != null) {
                totalCurrentValue = totalCurrentValue.add(stock.getCurrentValue());
            }
            if (stock.getProfitLoss() != null) {
                totalProfitLoss = totalProfitLoss.add(stock.getProfitLoss());
            }
        }

        BigDecimal totalProfitRate = BigDecimal.ZERO;
        if (totalInvestment.compareTo(BigDecimal.ZERO) > 0) {
            totalProfitRate = totalProfitLoss
                .divide(totalInvestment, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        }

        portfolio.setTotalInvestment(totalInvestment);
        portfolio.setTotalCurrentValue(totalCurrentValue);
        portfolio.setTotalProfitLoss(totalProfitLoss);
        portfolio.setTotalProfitRate(totalProfitRate);

        return portfolio;
    }

    /* ══════════════════════════════════════════════════════════
     * 종목 중복 체크
     * ══════════════════════════════════════════════════════════ */

    @Override
    public boolean isStockInPortfolio(Long portfolioId, String stockCode) {
        List<PortfolioItemVO> items = portfolioMapper.findItemsByPortfolioId(portfolioId);
        return items.stream().anyMatch(item -> item.getStockCode().equals(stockCode));
    }

    /* ══════════════════════════════════════════════════════════
     * 포트폴리오 종목 수 조회
     * ══════════════════════════════════════════════════════════ */

    @Override
    public int getStockCount(Long portfolioId) {
        List<PortfolioItemVO> items = portfolioMapper.findItemsByPortfolioId(portfolioId);
        return items != null ? items.size() : 0;
    }
}
