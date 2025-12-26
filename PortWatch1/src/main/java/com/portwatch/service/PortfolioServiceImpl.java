package com.portwatch.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.PortfolioItemVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.PortfolioDAO;
import com.portwatch.persistence.StockDAO;

/**
 * ✅ 포트폴리오 Service 구현 V3 (완전 구현)
 * 
 * @author PortWatch
 * @version 3.0 FINAL
 */
@Service
public class PortfolioServiceImpl implements PortfolioService {
    
    @Autowired
    private PortfolioDAO portfolioDAO;
    
    @Autowired
    private StockDAO stockDAO;
    
    /**
     * ✅ 회원 ID로 포트폴리오 조회 (별칭)
     */
    @Override
    public List<PortfolioVO> getPortfolioByMemberId(String memberId) {
        return getPortfolioList(memberId);
    }
    
    /**
     * ✅ 포트폴리오 목록 조회
     */
    @Override
    public List<PortfolioVO> getPortfolioList(String memberId) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 포트폴리오 목록 조회");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            List<PortfolioVO> portfolioList = portfolioDAO.selectPortfolioByMember(memberId);
            
            for (PortfolioVO portfolio : portfolioList) {
                StockVO stock = stockDAO.selectById(portfolio.getStockId());
                
                if (stock != null) {
                    portfolio.setCurrentPrice(stock.getCurrentPrice());
                    portfolio.setStockCode(stock.getStockCode());
                    portfolio.setStockName(stock.getStockName());
                    portfolio.setCountry(stock.getCountry());
                    portfolio.setMarketType(stock.getMarketType());
                    
                    // 수익률 계산
                    if (portfolio.getAvgPurchasePrice() != null && stock.getCurrentPrice() != null
                        && portfolio.getAvgPurchasePrice().compareTo(BigDecimal.ZERO) > 0) {
                        
                        BigDecimal profitRate = stock.getCurrentPrice()
                            .subtract(portfolio.getAvgPurchasePrice())
                            .divide(portfolio.getAvgPurchasePrice(), 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"))
                            .setScale(2, RoundingMode.HALF_UP);
                        
                        portfolio.setProfitRate(profitRate);
                    }
                }
            }
            
            System.out.println("  - 보유 종목: " + portfolioList.size() + "개");
            System.out.println("✅ 포트폴리오 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return portfolioList;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return new ArrayList<>();
        }
    }
    
    /**
     * ✅ 회원 + 종목코드로 포트폴리오 조회
     */
    @Override
    public PortfolioVO getPortfolioByMemberAndStock(String memberId, String stockCode) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 포트폴리오 조회 (회원 + 종목)");
        System.out.println("  - 회원 ID: " + memberId);
        System.out.println("  - 종목코드: " + stockCode);
        
        try {
            // 1. 종목코드로 주식 ID 조회
            StockVO stock = stockDAO.selectByCode(stockCode);
            
            if (stock == null) {
                System.out.println("❌ 존재하지 않는 종목코드");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return null;
            }
            
            // 2. 포트폴리오 조회
            PortfolioVO portfolio = portfolioDAO.selectByMemberAndStock(memberId, stock.getStockId());
            
            if (portfolio != null) {
                portfolio.setCurrentPrice(stock.getCurrentPrice());
                portfolio.setStockCode(stock.getStockCode());
                portfolio.setStockName(stock.getStockName());
                System.out.println("✅ 포트폴리오 조회 성공");
            } else {
                System.out.println("❌ 포트폴리오 없음");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return portfolio;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return null;
        }
    }
    
    /**
     * ✅ 포트폴리오 요약 통계
     */
    @Override
    public Map<String, Object> getPortfolioSummary(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 포트폴리오 요약 통계");
        System.out.println("  - 회원 ID: " + memberId);
        
        Map<String, Object> summary = new HashMap<>();
        
        try {
            List<PortfolioVO> portfolioList = getPortfolioList(memberId);
            
            BigDecimal totalInvestment = BigDecimal.ZERO;
            BigDecimal totalCurrentValue = BigDecimal.ZERO;
            int totalStockCount = portfolioList.size();
            int krStockCount = 0;
            int usStockCount = 0;
            
            for (PortfolioVO portfolio : portfolioList) {
                if (portfolio.getAvgPurchasePrice() != null && portfolio.getQuantity() != null) {
                    BigDecimal investment = portfolio.getAvgPurchasePrice()
                        .multiply(portfolio.getQuantity())
                        .setScale(2, RoundingMode.HALF_UP);
                    totalInvestment = totalInvestment.add(investment);
                }
                
                if (portfolio.getCurrentPrice() != null && portfolio.getQuantity() != null) {
                    BigDecimal currentValue = portfolio.getCurrentPrice()
                        .multiply(portfolio.getQuantity())
                        .setScale(2, RoundingMode.HALF_UP);
                    totalCurrentValue = totalCurrentValue.add(currentValue);
                }
                
                if ("KR".equals(portfolio.getCountry())) {
                    krStockCount++;
                } else if ("US".equals(portfolio.getCountry())) {
                    usStockCount++;
                }
            }
            
            BigDecimal totalProfit = totalCurrentValue.subtract(totalInvestment);
            
            BigDecimal totalProfitRate = BigDecimal.ZERO;
            if (totalInvestment.compareTo(BigDecimal.ZERO) > 0) {
                totalProfitRate = totalProfit
                    .divide(totalInvestment, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
            }
            
            summary.put("totalInvestment", totalInvestment);
            summary.put("totalCurrentValue", totalCurrentValue);
            summary.put("totalProfit", totalProfit);
            summary.put("totalProfitRate", totalProfitRate);
            summary.put("totalStockCount", totalStockCount);
            summary.put("krStockCount", krStockCount);
            summary.put("usStockCount", usStockCount);
            
            System.out.println("  - 총 투자금: " + totalInvestment);
            System.out.println("  - 총 평가금액: " + totalCurrentValue);
            System.out.println("  - 총 수익률: " + totalProfitRate + "%");
            System.out.println("✅ 포트폴리오 요약 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return summary;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 요약 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 요약 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 주식 추가 (파라미터 버전)
     */
    @Override
    @Transactional
    public boolean addStockToPortfolio(String memberId, String stockCode, double quantity, double price)
            throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("➕ 포트폴리오 주식 추가");
        System.out.println("  - 회원 ID: " + memberId);
        System.out.println("  - 종목코드: " + stockCode);
        System.out.println("  - 수량: " + quantity);
        System.out.println("  - 가격: " + price);
        
        try {
            // 1. 종목코드로 주식 ID 조회
            StockVO stock = stockDAO.selectByCode(stockCode);
            
            if (stock == null) {
                throw new Exception("존재하지 않는 종목코드: " + stockCode);
            }
            
            // 2. 중복 체크
            if (checkDuplicate(memberId, stock.getStockId())) {
                throw new Exception("이미 보유 중인 종목입니다.");
            }
            
            // 3. PortfolioVO 생성
            PortfolioVO portfolio = new PortfolioVO();
            portfolio.setMemberId(memberId);
            portfolio.setStockId(stock.getStockId());
            portfolio.setQuantity(new BigDecimal(quantity));
            portfolio.setAvgPurchasePrice(new BigDecimal(price));
            
            // 4. DB 저장
            portfolioDAO.insert(portfolio);
            
            System.out.println("✅ 포트폴리오 주식 추가 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 주식 추가 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 주식 추가 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 주식 추가 (PortfolioItemVO 버전)
     */
    @Override
    @Transactional
    public boolean addStockToPortfolio(PortfolioItemVO item) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("➕ 포트폴리오 주식 추가 (PortfolioItemVO)");
        
        try {
            // PortfolioItemVO → PortfolioVO 변환
            PortfolioVO portfolio = new PortfolioVO();
            portfolio.setMemberId(item.getMemberId());
            portfolio.setStockId(item.getStockId());
            portfolio.setQuantity(item.getQuantity());
            portfolio.setAvgPurchasePrice(item.getAvgPurchasePrice());
            
            // 중복 체크
            if (checkDuplicate(item.getMemberId(), item.getStockId())) {
                System.out.println("❌ 이미 보유 중인 종목");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return false;
            }
            
            portfolioDAO.insert(portfolio);
            
            System.out.println("✅ 포트폴리오 주식 추가 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 주식 추가 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return false;
        }
    }
    
    /**
     * ✅ 주식 제거 (수량 감소)
     */
    @Override
    @Transactional
    public boolean removeStockFromPortfolio(String memberId, String stockCode, double quantity) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("➖ 포트폴리오 주식 제거");
        System.out.println("  - 회원 ID: " + memberId);
        System.out.println("  - 종목코드: " + stockCode);
        System.out.println("  - 수량: " + quantity);
        
        try {
            // 1. 포트폴리오 조회
            PortfolioVO portfolio = getPortfolioByMemberAndStock(memberId, stockCode);
            
            if (portfolio == null) {
                throw new Exception("보유하지 않은 종목입니다.");
            }
            
            // 2. 수량 계산
            BigDecimal currentQuantity = portfolio.getQuantity();
            BigDecimal removeQuantity = new BigDecimal(quantity);
            BigDecimal newQuantity = currentQuantity.subtract(removeQuantity);
            
            // 3. 전체 제거 vs 일부 제거
            if (newQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                // 전체 제거
                portfolioDAO.delete(portfolio.getPortfolioId().longValue());
                System.out.println("  - 전체 제거");
            } else {
                // 일부 제거
                portfolio.setQuantity(newQuantity);
                portfolioDAO.update(portfolio);
                System.out.println("  - 남은 수량: " + newQuantity);
            }
            
            System.out.println("✅ 포트폴리오 주식 제거 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 주식 제거 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return false;
        }
    }
    
    /**
     * ✅ 포트폴리오 업데이트
     */
    @Override
    @Transactional
    public void updatePortfolio(PortfolioVO portfolio) throws Exception {
        try {
            portfolioDAO.update(portfolio);
        } catch (Exception e) {
            throw new Exception("포트폴리오 업데이트 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 총 평가금액 조회
     */
    @Override
    public double getTotalValue(String memberId) {
        try {
            Map<String, Object> summary = getPortfolioSummary(memberId);
            BigDecimal totalValue = (BigDecimal) summary.get("totalCurrentValue");
            return totalValue != null ? totalValue.doubleValue() : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * ✅ 총 수익금 조회
     */
    @Override
    public double getTotalProfit(String memberId) {
        try {
            Map<String, Object> summary = getPortfolioSummary(memberId);
            BigDecimal totalProfit = (BigDecimal) summary.get("totalProfit");
            return totalProfit != null ? totalProfit.doubleValue() : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * ✅ 총 수익률 조회
     */
    @Override
    public double getTotalProfitRate(String memberId) {
        try {
            Map<String, Object> summary = getPortfolioSummary(memberId);
            BigDecimal totalProfitRate = (BigDecimal) summary.get("totalProfitRate");
            return totalProfitRate != null ? totalProfitRate.doubleValue() : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * ✅ 중복 체크 (String memberId, Integer stockId)
     */
    @Override
    public boolean checkDuplicate(String memberId, Integer stockId) {
        try {
            PortfolioVO portfolio = portfolioDAO.selectByMemberAndStock(memberId, stockId);
            return portfolio != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * ✅ 포트폴리오 삭제 (회원 + 종목코드)
     */
    @Override
    @Transactional
    public boolean deletePortfolio(String memberId, String stockCode) {
        try {
            PortfolioVO portfolio = getPortfolioByMemberAndStock(memberId, stockCode);
            
            if (portfolio == null) {
                return false;
            }
            
            portfolioDAO.delete(portfolio.getPortfolioId().longValue());
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * ✅ 포트폴리오 삭제 (포트폴리오 ID)
     */
    @Override
    @Transactional
    public void deletePortfolio(Long portfolioId) {
        try {
            portfolioDAO.delete(portfolioId);
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 삭제 실패", e);
        }
    }
    
    /**
     * ✅ 전체 포트폴리오 삭제 (회원 ID)
     */
    @Override
    @Transactional
    public boolean deleteAllPortfolio(String memberId) {
        try {
            List<PortfolioVO> portfolioList = getPortfolioList(memberId);
            
            for (PortfolioVO portfolio : portfolioList) {
                portfolioDAO.delete(portfolio.getPortfolioId().longValue());
            }
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * ✅ 포트폴리오 조회 (Integer memberId, Integer stockId)
     */
    @Override
    public PortfolioVO getByMemberAndStock(Integer memberId, Integer stockId) {
        try {
            return portfolioDAO.selectByMemberAndStock(String.valueOf(memberId), stockId);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * ✅ 포트폴리오 업데이트
     */
    @Override
    public void update(PortfolioVO portfolio) {
        try {
            portfolioDAO.update(portfolio);
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 업데이트 실패", e);
        }
    }
    
    /**
     * ✅ 포트폴리오 추가
     */
    @Override
    public void insert(PortfolioVO portfolio) {
        try {
            portfolioDAO.insert(portfolio);
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 추가 실패", e);
        }
    }
    
    /**
     * ✅ 포트폴리오 삭제
     */
    @Override
    public void delete(Integer portfolioId) {
        try {
            portfolioDAO.delete(portfolioId.longValue());
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 삭제 실패", e);
        }
    }

	@Override
	public List<PortfolioVO> getPortfolioList(Integer memberId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getPortfolioSummary(Integer memberId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkDuplicate(Integer memberId, Integer stockId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteAllPortfolio(Long portfolioId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addStockToPortfolio(PortfolioVO portfolio) {
		// TODO Auto-generated method stub
		return false;
	}
}
