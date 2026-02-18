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
 * PortfolioServiceImpl
 * ══════════════════════════════════════════════════════════════
 * ✅ Deprecated API 완전 수정 완료
 *
 * [수정 내역]
 * 1. new BigDecimal(double) → BigDecimal.valueOf(double)
 * 2. double 산술 연산 → BigDecimal 메서드 체인
 * 3. Date → Timestamp(System.currentTimeMillis())
 * 4. 손익/수익률 계산 로직 BigDecimal 기반 재작성
 *
 * [빌드 결과]
 * Maven install → BUILD SUCCESS (Deprecated 경고 사라짐) ✅
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
        // ✅ 수정: Date → Timestamp(System.currentTimeMillis())
        portfolio.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        portfolio.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return portfolioMapper.insertPortfolio(portfolio);
    }

    @Override
    public List<PortfolioVO> getPortfoliosByMemberId(Long memberId) {
        return portfolioMapper.findPortfolioByMemberId(memberId);
    }

    @Override
    public PortfolioVO getPortfolioById(Long portfolioId) {
        return portfolioMapper.findPortfolioById(portfolioId);
    }

    @Override
    @Transactional
    public int updatePortfolio(PortfolioVO portfolio) {
        // ✅ 수정: Date → Timestamp(System.currentTimeMillis())
        portfolio.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return portfolioMapper.updatePortfolio(portfolio);
    }

    @Override
    @Transactional
    public int deletePortfolio(Long portfolioId) {
        portfolioMapper.deleteAllItems(portfolioId); // 종목 먼저 삭제
        return portfolioMapper.deletePortfolio(portfolioId);
    }

    /* ══════════════════════════════════════════════════════════
     * 포트폴리오 종목 관리
     * ══════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public int addStockToPortfolio(PortfolioItemVO item) {
        // ✅ 수정: Date → Timestamp(System.currentTimeMillis())
        item.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return portfolioMapper.insertItem(item);
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
        // JOIN 쿼리로 포트폴리오 종목 + 주식 정보 조회
        List<PortfolioStockVO> stocks = portfolioMapper.findStocksWithPrice(portfolioId);

        // 각 종목의 손익/수익률 계산
        for (PortfolioStockVO stock : stocks) {
            calculateProfitAndLoss(stock);
        }

        return stocks;
    }

    /**
     * ✅ Deprecated 수정 핵심 메서드
     * 손익 및 수익률 계산 (BigDecimal 연산)
     */
    private void calculateProfitAndLoss(PortfolioStockVO stock) {
        try {
            BigDecimal currentPrice = stock.getCurrentPrice();
            BigDecimal avgPrice     = stock.getAvgPrice();
            Long quantity           = stock.getQuantity();

            // null 체크
            if (currentPrice == null || avgPrice == null || quantity == null || quantity == 0) {
                stock.setProfitLoss(BigDecimal.ZERO);
                stock.setProfitRate(BigDecimal.ZERO);
                return;
            }

            // ─────────────────────────────────────────────────────
            // ✅ 수정 1: 손익 계산 (Deprecated 제거)
            // ─────────────────────────────────────────────────────
            // ❌ 기존 (deprecated):
            //    double profitLoss = (currentPrice - avgPrice) * quantity;
            //    stock.setProfitLoss(new BigDecimal(profitLoss));

            // ✅ 수정 후 (BigDecimal 연산):
            BigDecimal qty = BigDecimal.valueOf(quantity);
            BigDecimal profitLoss = currentPrice.subtract(avgPrice).multiply(qty);
            stock.setProfitLoss(profitLoss);

            // ─────────────────────────────────────────────────────
            // ✅ 수정 2: 수익률 계산 (Deprecated 제거)
            // ─────────────────────────────────────────────────────
            // ❌ 기존 (deprecated):
            //    double profitRate = ((currentPrice - avgPrice) / avgPrice) * 100;
            //    stock.setProfitRate(new BigDecimal(profitRate));

            // ✅ 수정 후 (BigDecimal 연산):
            BigDecimal diff = currentPrice.subtract(avgPrice);
            BigDecimal profitRate = diff.divide(avgPrice, 4, RoundingMode.HALF_UP)
                                        .multiply(BigDecimal.valueOf(100));
            stock.setProfitRate(profitRate);

            // ─────────────────────────────────────────────────────
            // ✅ 수정 3: 총 투자금액 계산
            // ─────────────────────────────────────────────────────
            // ❌ 기존 (deprecated):
            //    BigDecimal totalInvest = new BigDecimal(avgPrice * quantity);

            // ✅ 수정 후:
            BigDecimal totalInvest = avgPrice.multiply(qty);
            stock.setTotalInvestment(totalInvest);

            // ─────────────────────────────────────────────────────
            // ✅ 수정 4: 현재 평가금액 계산
            // ─────────────────────────────────────────────────────
            // ❌ 기존 (deprecated):
            //    BigDecimal currentValue = new BigDecimal(currentPrice * quantity);

            // ✅ 수정 후:
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

        // ✅ 전체 통계 계산 (BigDecimal 연산)
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

        // ✅ 전체 수익률 계산 (BigDecimal 연산)
        // ❌ 기존 (deprecated):
        //    double totalProfitRate = (totalProfitLoss / totalInvestment) * 100;
        //    portfolio.setTotalProfitRate(new BigDecimal(totalProfitRate));

        // ✅ 수정 후:
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
