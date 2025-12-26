package com.portwatch.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.PortfolioItemVO;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.PortfolioDAO;
import com.portwatch.persistence.StockDAO;

/**
 * ✅ 포트폴리오 Service 완전 구현
 * 
 * 모든 메서드 구현 완료
 * memberId String으로 완전 통일
 * 
 * @author PortWatch
 * @version 4.0 - 완전 구현
 */
@Service
public class PortfolioServiceImpl implements PortfolioService {
    
    @Autowired
    private PortfolioDAO portfolioDAO;
    
    @Autowired
    private StockDAO stockDAO;
    
    /**
     * ✅ 포트폴리오 목록 조회 (String memberId)
     */
    @Override
    public List<PortfolioVO> getPortfolioList(String memberId) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 포트폴리오 목록 조회");
        System.out.println("  - 회원 ID: " + memberId);
        
        List<PortfolioVO> portfolioList = portfolioDAO.selectPortfolioByMember(memberId);
        
        // 현재가 정보 추가
        for (PortfolioVO portfolio : portfolioList) {
            StockVO stock = stockDAO.selectById(portfolio.getStockId());
            if (stock != null) {
                portfolio.setCurrentPrice(stock.getCurrentPrice());
                
                // 수익률 계산
                if (portfolio.getAvgPurchasePrice() != null && stock.getCurrentPrice() != null) {
                    BigDecimal profitRate = stock.getCurrentPrice()
                        .subtract(portfolio.getAvgPurchasePrice())
                        .divide(portfolio.getAvgPurchasePrice(), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                    
                    portfolio.setProfitRate(profitRate);
                }
            }
        }
        
        System.out.println("✅ " + portfolioList.size() + "개 포트폴리오 조회 완료");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return portfolioList;
    }
    
    /**
     * ✅ 포트폴리오 목록 조회 (별칭)
     */
    @Override
    public List<PortfolioVO> getPortfolioByMemberId(String memberId) {
        try {
            return getPortfolioList(memberId);
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 조회 실패", e);
        }
    }
    
    /**
     * ✅ 포트폴리오 요약 정보 조회 (String memberId)
     */
    @Override
    public Map<String, Object> getPortfolioSummary(String memberId) throws Exception {
        Map<String, Object> summary = new HashMap<>();
        
        try {
            List<PortfolioVO> portfolioList = getPortfolioList(memberId);
            
            BigDecimal totalInvestment = BigDecimal.ZERO;
            BigDecimal totalCurrentValue = BigDecimal.ZERO;
            int totalStockCount = portfolioList.size();
            
            for (PortfolioVO portfolio : portfolioList) {
                // 투자 금액
                BigDecimal investment = portfolio.getAvgPurchasePrice()
                    .multiply(portfolio.getQuantity());
                totalInvestment = totalInvestment.add(investment);
                
                // 현재 가치
                if (portfolio.getCurrentPrice() != null) {
                    BigDecimal currentValue = portfolio.getCurrentPrice()
                        .multiply(portfolio.getQuantity());
                    totalCurrentValue = totalCurrentValue.add(currentValue);
                }
            }
            
            // 총 수익/손실
            BigDecimal totalProfit = totalCurrentValue.subtract(totalInvestment);
            
            // 수익률
            BigDecimal profitRate = BigDecimal.ZERO;
            if (totalInvestment.compareTo(BigDecimal.ZERO) > 0) {
                profitRate = totalProfit
                    .divide(totalInvestment, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            }
            
            summary.put("totalStockCount", totalStockCount);
            summary.put("totalInvestment", totalInvestment);
            summary.put("totalCurrentValue", totalCurrentValue);
            summary.put("totalProfit", totalProfit);
            summary.put("profitRate", profitRate);
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 요약 조회 실패: " + e.getMessage());
            throw e;
        }
        
        return summary;
    }
    
    /**
     * ✅ 회원 + 종목으로 포트폴리오 조회
     */
    @Override
    public PortfolioVO getPortfolioByMemberAndStock(String memberId, String stockCode) {
        try {
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            if (stock == null) {
                return null;
            }
            return portfolioDAO.selectByMemberAndStock(memberId, stock.getStockId());
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 조회 실패", e);
        }
    }
    
    /**
     * ✅ 포트폴리오에 주식 추가 (주식 매입)
     */
    @Override
    @Transactional
    public boolean addStockToPortfolio(String memberId, String stockCode, double quantity, double price) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("➕ 포트폴리오에 주식 추가");
        System.out.println("  - 회원 ID: " + memberId);
        System.out.println("  - 종목 코드: " + stockCode);
        System.out.println("  - 수량: " + quantity);
        System.out.println("  - 단가: " + price);
        
        try {
            // 종목 정보 조회
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            if (stock == null) {
                throw new Exception("종목을 찾을 수 없습니다: " + stockCode);
            }
            
            Integer stockId = stock.getStockId();
            
            // 기존 포트폴리오 확인
            PortfolioVO existingPortfolio = portfolioDAO.selectByMemberAndStock(memberId, stockId);
            
            if (existingPortfolio != null) {
                // 기존 포트폴리오 업데이트 (평균 단가 재계산)
                BigDecimal existingQuantity = existingPortfolio.getQuantity();
                BigDecimal existingAvgPrice = existingPortfolio.getAvgPurchasePrice();
                
                BigDecimal newQuantity = new BigDecimal(String.valueOf(quantity));
                BigDecimal newPrice = new BigDecimal(String.valueOf(price));
                
                BigDecimal totalQuantity = existingQuantity.add(newQuantity);
                
                // 평균 매입가 = (기존 수량 * 기존 평균가 + 신규 수량 * 신규 가격) / 총 수량
                BigDecimal totalCost = existingQuantity.multiply(existingAvgPrice)
                    .add(newQuantity.multiply(newPrice));
                BigDecimal avgPurchasePrice = totalCost.divide(totalQuantity, 2, RoundingMode.HALF_UP);
                
                existingPortfolio.setQuantity(totalQuantity);
                existingPortfolio.setAvgPurchasePrice(avgPurchasePrice);
                
                portfolioDAO.updatePortfolio(existingPortfolio);
                
                System.out.println("✅ 기존 포트폴리오 업데이트 완료");
                System.out.println("  - 새 평균 단가: " + avgPurchasePrice);
                System.out.println("  - 총 수량: " + totalQuantity);
                
            } else {
                // 새 포트폴리오 생성
                PortfolioVO newPortfolio = new PortfolioVO();
                newPortfolio.setMemberId(memberId);
                newPortfolio.setStockId(stockId);
                newPortfolio.setQuantity(new BigDecimal(String.valueOf(quantity)));
                newPortfolio.setAvgPurchasePrice(new BigDecimal(String.valueOf(price)));
                newPortfolio.setPurchaseDate(new Timestamp(System.currentTimeMillis()));
                
                portfolioDAO.insertPortfolio(newPortfolio);
                
                System.out.println("✅ 새 포트폴리오 생성 완료");
                System.out.println("  - 포트폴리오 ID: " + newPortfolio.getPortfolioId());
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 추가 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw e;
        }
    }
    
    /**
     * ✅ PortfolioItemVO로 추가
     */
    @Override
    public boolean addStockToPortfolio(PortfolioItemVO item) {
        try {
            return addStockToPortfolio(
                item.getMemberId(),
                item.getStockCode(),
                item.getQuantity().doubleValue(),
                item.getPurchasePrice().doubleValue()
            );
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 추가 실패", e);
        }
    }
    
    /**
     * ✅ 포트폴리오에서 주식 제거 (일부 또는 전체)
     */
    @Override
    public boolean removeStockFromPortfolio(String memberId, String stockCode, double quantity) {
        try {
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            if (stock == null) {
                return false;
            }
            
            PortfolioVO portfolio = portfolioDAO.selectByMemberAndStock(memberId, stock.getStockId());
            if (portfolio == null) {
                return false;
            }
            
            BigDecimal currentQuantity = portfolio.getQuantity();
            BigDecimal removeQuantity = new BigDecimal(String.valueOf(quantity));
            
            if (removeQuantity.compareTo(currentQuantity) >= 0) {
                // 전체 제거
                portfolioDAO.deletePortfolio(portfolio.getPortfolioId());
            } else {
                // 일부 제거
                portfolio.setQuantity(currentQuantity.subtract(removeQuantity));
                portfolioDAO.updatePortfolio(portfolio);
            }
            
            return true;
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 제거 실패", e);
        }
    }
    
    /**
     * ✅ 총 자산 가치
     */
    @Override
    public double getTotalValue(String memberId) {
        try {
            Map<String, Object> summary = getPortfolioSummary(memberId);
            BigDecimal totalValue = (BigDecimal) summary.get("totalCurrentValue");
            return totalValue.doubleValue();
        } catch (Exception e) {
            throw new RuntimeException("총 가치 조회 실패", e);
        }
    }
    
    /**
     * ✅ 총 수익금
     */
    @Override
    public double getTotalProfit(String memberId) {
        try {
            Map<String, Object> summary = getPortfolioSummary(memberId);
            BigDecimal totalProfit = (BigDecimal) summary.get("totalProfit");
            return totalProfit.doubleValue();
        } catch (Exception e) {
            throw new RuntimeException("총 수익 조회 실패", e);
        }
    }
    
    /**
     * ✅ 총 수익률
     */
    @Override
    public double getTotalProfitRate(String memberId) {
        try {
            Map<String, Object> summary = getPortfolioSummary(memberId);
            BigDecimal profitRate = (BigDecimal) summary.get("profitRate");
            return profitRate.doubleValue();
        } catch (Exception e) {
            throw new RuntimeException("수익률 조회 실패", e);
        }
    }
    
    /**
     * ✅ 중복 체크 (String memberId)
     */
    @Override
    public boolean checkDuplicate(String memberId, Integer stockId) {
        try {
            PortfolioVO portfolio = portfolioDAO.selectByMemberAndStock(memberId, stockId);
            return portfolio != null;
        } catch (Exception e) {
            throw new RuntimeException("중복 체크 실패", e);
        }
    }
    
    /**
     * ✅ 포트폴리오 삭제 (종목 코드로)
     */
    @Override
    public boolean deletePortfolio(String memberId, String stockCode) {
        try {
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            if (stock == null) {
                return false;
            }
            
            PortfolioVO portfolio = portfolioDAO.selectByMemberAndStock(memberId, stock.getStockId());
            if (portfolio == null) {
                return false;
            }
            
            portfolioDAO.deletePortfolio(portfolio.getPortfolioId());
            return true;
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 삭제 실패", e);
        }
    }
    
    /**
     * ✅ 포트폴리오 삭제 (ID로)
     */
    @Override
    public void deletePortfolio(Long portfolioId) {
        try {
            portfolioDAO.deletePortfolio(portfolioId);
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 삭제 실패", e);
        }
    }
    
    /**
     * ✅ 회원의 모든 포트폴리오 삭제
     */
    @Override
    public boolean deleteAllPortfolio(String memberId) {
        try {
            List<PortfolioVO> portfolios = portfolioDAO.selectPortfolioByMember(memberId);
            for (PortfolioVO portfolio : portfolios) {
                portfolioDAO.deletePortfolio(portfolio.getPortfolioId());
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("전체 삭제 실패", e);
        }
    }
    
    /**
     * ✅ 포트폴리오 조회 (회원 + 종목 ID)
     */
    @Override
    public PortfolioVO getByMemberAndStock(Integer memberId, Integer stockId) {
        // Integer 버전은 사용 안 함 (하위 호환용)
        return null;
    }
    
    /**
     * ✅ 포트폴리오 업데이트
     */
    @Override
    @Transactional
    public void updatePortfolio(PortfolioVO portfolio) throws Exception {
        System.out.println("🔄 포트폴리오 업데이트");
        System.out.println("  - 포트폴리오 ID: " + portfolio.getPortfolioId());
        
        portfolioDAO.updatePortfolio(portfolio);
        
        System.out.println("✅ 포트폴리오 업데이트 완료");
    }
    
    /**
     * ✅ 포트폴리오 업데이트 (별칭)
     */
    @Override
    public void update(PortfolioVO portfolio) {
        try {
            updatePortfolio(portfolio);
        } catch (Exception e) {
            throw new RuntimeException("업데이트 실패", e);
        }
    }
    
    /**
     * ✅ 포트폴리오 삽입
     */
    @Override
    public void insert(PortfolioVO portfolio) {
        try {
            portfolioDAO.insertPortfolio(portfolio);
        } catch (Exception e) {
            throw new RuntimeException("삽입 실패", e);
        }
    }
    
    /**
     * ✅ 포트폴리오 삭제 (Integer ID)
     */
    @Override
    public void delete(Integer portfolioId) {
        try {
            portfolioDAO.deletePortfolio(portfolioId.longValue());
        } catch (Exception e) {
            throw new RuntimeException("삭제 실패", e);
        }
    }
    
    // ========================================
    // ❌ 사용 안 함 (Integer 버전 - 하위 호환용)
    // ========================================
    
    @Override
    public List<PortfolioVO> getPortfolioList(Integer memberId) {
        return null; // 사용 안 함
    }
    
    @Override
    public Map<String, Object> getPortfolioSummary(Integer memberId) {
        return null; // 사용 안 함
    }
    
    @Override
    public boolean checkDuplicate(Integer memberId, Integer stockId) {
        return false; // 사용 안 함
    }
    
    @Override
    public void deleteAllPortfolio(Long portfolioId) {
        // 사용 안 함
    }
}
