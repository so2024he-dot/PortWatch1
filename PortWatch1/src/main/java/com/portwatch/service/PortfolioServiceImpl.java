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
     * ✅ 회원별 포트폴리오 목록 (String 버전 - 신규!)
     * DashboardController, PortfolioController 호출용
     */
    @Override
    public List<PortfolioVO> getPortfolioByMemberId(String memberId) {
        try {
            // String → Long 변환 시도
            Long memberIdLong = Long.parseLong(memberId);
            return portfolioMapper.findPortfolioByMemberId(memberIdLong);
        } catch (NumberFormatException e) {
            log.warn("memberId 변환 실패 (String→Long): {}", memberId);
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
     * ✅ 종목 추가 (4개 파라미터 버전 - 신규!)
     * StockPurchaseApiController 호출용
     * 
     * 동작:
     * 1. memberId로 기본 포트폴리오 조회/생성
     * 2. PortfolioItemVO 생성
     * 3. insertItem 호출
     */
    @Override
    @Transactional
    public boolean addStockToPortfolio(String memberId, String stockCode, double quantity, double price) {
        try {
            log.info("종목 추가 시작: memberId={}, stockCode={}, quantity={}, price={}", 
                     memberId, stockCode, quantity, price);

            // 1. memberId로 포트폴리오 조회
            List<PortfolioVO> portfolios = getPortfolioByMemberId(memberId);
            
            Long portfolioId;
            if (portfolios == null || portfolios.isEmpty()) {
                // 포트폴리오가 없으면 생성
                log.info("포트폴리오 없음, 신규 생성");
                PortfolioVO newPortfolio = new PortfolioVO();
                newPortfolio.setMemberId(memberId);
                newPortfolio.setPortfolioName("기본 포트폴리오");
                createPortfolio(newPortfolio);
                
                // 다시 조회
                portfolios = getPortfolioByMemberId(memberId);
                if (portfolios == null || portfolios.isEmpty()) {
                    log.error("포트폴리오 생성 실패");
                    return false;
                }
                portfolioId = portfolios.get(0).getPortfolioId();
            } else {
                // 첫 번째 포트폴리오 사용
                portfolioId = portfolios.get(0).getPortfolioId();
            }

            // 2. MySQL에서 종목 정보 조회
            StockVO stock = stockMapper.findByCode(stockCode);
            if (stock == null) {
                log.error("종목 정보 없음: {}", stockCode);
                return false;
            }

            // 3. PortfolioItemVO 생성
            PortfolioItemVO item = new PortfolioItemVO();
            item.setPortfolioId(portfolioId);
            item.setStockCode(stockCode);
            item.setQuantity(BigDecimal.valueOf(quantity));
            item.setAvgPrice(BigDecimal.valueOf(price));
            item.setPurchasePrice(BigDecimal.valueOf(price));

            // 4. 종목 추가
            int result = addStockToPortfolio(item);
            
            log.info("종목 추가 완료: result={}", result);
            return result > 0;

        } catch (Exception e) {
            log.error("종목 추가 실패", e);
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
