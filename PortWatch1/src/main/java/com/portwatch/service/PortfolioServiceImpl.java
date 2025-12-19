package com.portwatch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.PortfolioDAO;
import com.portwatch.persistence.StockDAO;

/**
 * ✅ 포트폴리오 서비스 구현체
 * 
 * @author PortWatch
 * @version 1.0
 */
@Service
public class PortfolioServiceImpl implements PortfolioService {
    
    @Autowired
    private PortfolioDAO portfolioDAO;
    
    @Autowired
    private StockDAO stockDAO;
    
    /**
     * 회원의 전체 포트폴리오 조회
     */
    @Override
    public List<PortfolioVO> getPortfolioByMemberId(String memberId) {
        System.out.println("📊 포트폴리오 조회: " + memberId);
        
        List<PortfolioVO> portfolioList = portfolioDAO.selectPortfolioByMemberId(memberId);
        
        // 각 포트폴리오에 현재가 정보 추가
        for (PortfolioVO portfolio : portfolioList) {
            StockVO stock = stockDAO.selectStockByCode(portfolio.getStockCode());
            if (stock != null) {
                portfolio.setCurrentPrice(stock.getCurrentPrice());
                portfolio.setStockName(stock.getStockName());
            }
        }
        
        System.out.println("✅ " + portfolioList.size() + "개 종목 조회 완료");
        return portfolioList;
    }
    
    /**
     * 특정 종목의 포트폴리오 조회
     */
    @Override
    public PortfolioVO getPortfolioByMemberAndStock(String memberId, String stockCode) {
        System.out.println("📊 포트폴리오 조회: " + memberId + " / " + stockCode);
        
        PortfolioVO portfolio = portfolioDAO.selectPortfolioByMemberAndStock(memberId, stockCode);
        
        if (portfolio != null) {
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            if (stock != null) {
                portfolio.setCurrentPrice(stock.getCurrentPrice());
                portfolio.setStockName(stock.getStockName());
            }
        }
        
        return portfolio;
    }
    
    /**
     * ✅ 주식 매수 - 포트폴리오에 추가 또는 수량 증가 (핵심 메서드!)
     */
    @Override
    @Transactional
    public boolean addStockToPortfolio(String memberId, String stockCode, double quantity, double price) {
        try {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("💰 포트폴리오에 추가");
            System.out.println("  - 회원 ID: " + memberId);
            System.out.println("  - 종목 코드: " + stockCode);
            System.out.println("  - 수량: " + quantity);
            System.out.println("  - 가격: " + price);
            
            // 1. 기존 포트폴리오 조회
            PortfolioVO existingPortfolio = portfolioDAO.selectPortfolioByMemberAndStock(memberId, stockCode);
            
            if (existingPortfolio != null) {
                // ✅ 기존 보유 종목 - 평균 매입가 계산 후 수량 증가
                System.out.println("📌 기존 보유 종목 - 수량 추가");
                
                double existingQuantity = existingPortfolio.getQuantity();
                double existingAvgPrice = existingPortfolio.getAvgPrice();
                
                // 평균 매입가 계산: (기존 총액 + 신규 총액) / (기존 수량 + 신규 수량)
                double totalAmount = (existingQuantity * existingAvgPrice) + (quantity * price);
                double totalQuantity = existingQuantity + quantity;
                double newAvgPrice = totalAmount / totalQuantity;
                
                System.out.println("  - 기존 수량: " + existingQuantity);
                System.out.println("  - 기존 평균가: " + existingAvgPrice);
                System.out.println("  - 신규 평균가: " + newAvgPrice);
                System.out.println("  - 총 수량: " + totalQuantity);
                
                // 포트폴리오 업데이트
                existingPortfolio.setQuantity(totalQuantity);
                existingPortfolio.setAvgPrice(newAvgPrice);
                
                int updateResult = portfolioDAO.updatePortfolio(existingPortfolio);
                
                if (updateResult > 0) {
                    System.out.println("✅ 포트폴리오 업데이트 성공");
                    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    return true;
                } else {
                    System.err.println("❌ 포트폴리오 업데이트 실패");
                    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    return false;
                }
                
            } else {
                // ✅ 신규 종목 - 포트폴리오에 추가
                System.out.println("📌 신규 종목 - 포트폴리오에 추가");
                
                PortfolioVO newPortfolio = new PortfolioVO();
                newPortfolio.setMemberId(memberId);
                newPortfolio.setStockCode(stockCode);
                newPortfolio.setQuantity(quantity);
                newPortfolio.setAvgPrice(price);
                
                int insertResult = portfolioDAO.insertPortfolio(newPortfolio);
                
                if (insertResult > 0) {
                    System.out.println("✅ 포트폴리오 추가 성공");
                    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    return true;
                } else {
                    System.err.println("❌ 포트폴리오 추가 실패");
                    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    return false;
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 추가 중 오류: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return false;
        }
    }
    
    /**
     * 주식 매도 - 포트폴리오에서 수량 감소 또는 삭제
     */
    @Override
    @Transactional
    public boolean removeStockFromPortfolio(String memberId, String stockCode, double quantity, double price) {
        try {
            System.out.println("💸 포트폴리오에서 제거: " + stockCode);
            
            PortfolioVO portfolio = portfolioDAO.selectPortfolioByMemberAndStock(memberId, stockCode);
            
            if (portfolio == null) {
                System.err.println("❌ 보유하지 않은 종목입니다.");
                return false;
            }
            
            double currentQuantity = portfolio.getQuantity();
            
            if (currentQuantity < quantity) {
                System.err.println("❌ 보유 수량보다 많이 매도할 수 없습니다.");
                return false;
            }
            
            if (currentQuantity == quantity) {
                // 전량 매도 - 포트폴리오에서 삭제
                int deleteResult = portfolioDAO.deletePortfolio(memberId, stockCode);
                return deleteResult > 0;
            } else {
                // 일부 매도 - 수량만 감소
                portfolio.setQuantity(currentQuantity - quantity);
                int updateResult = portfolioDAO.updatePortfolio(portfolio);
                return updateResult > 0;
            }
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 제거 중 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 포트폴리오 총 평가액 계산
     */
    @Override
    public double getTotalValue(String memberId) {
        List<PortfolioVO> portfolioList = getPortfolioByMemberId(memberId);
        
        double totalValue = 0.0;
        
        for (PortfolioVO portfolio : portfolioList) {
            if (portfolio.getCurrentPrice() != null) {
                totalValue += portfolio.getQuantity() * portfolio.getCurrentPrice();
            }
        }
        
        return totalValue;
    }
    
    /**
     * 포트폴리오 총 손익 계산
     */
    @Override
    public double getTotalProfit(String memberId) {
        List<PortfolioVO> portfolioList = getPortfolioByMemberId(memberId);
        
        double totalProfit = 0.0;
        
        for (PortfolioVO portfolio : portfolioList) {
            if (portfolio.getCurrentPrice() != null) {
                double buyAmount = portfolio.getQuantity() * portfolio.getAvgPrice();
                double currentAmount = portfolio.getQuantity() * portfolio.getCurrentPrice();
                totalProfit += (currentAmount - buyAmount);
            }
        }
        
        return totalProfit;
    }
    
    /**
     * 포트폴리오 수익률 계산
     */
    @Override
    public double getTotalProfitRate(String memberId) {
        List<PortfolioVO> portfolioList = getPortfolioByMemberId(memberId);
        
        double totalBuyAmount = 0.0;
        double totalCurrentAmount = 0.0;
        
        for (PortfolioVO portfolio : portfolioList) {
            if (portfolio.getCurrentPrice() != null) {
                totalBuyAmount += portfolio.getQuantity() * portfolio.getAvgPrice();
                totalCurrentAmount += portfolio.getQuantity() * portfolio.getCurrentPrice();
            }
        }
        
        if (totalBuyAmount == 0) {
            return 0.0;
        }
        
        return ((totalCurrentAmount - totalBuyAmount) / totalBuyAmount) * 100;
    }
    
    /**
     * 포트폴리오 삭제 (특정 종목)
     */
    @Override
    @Transactional
    public boolean deletePortfolio(String memberId, String stockCode) {
        try {
            int result = portfolioDAO.deletePortfolio(memberId, stockCode);
            return result > 0;
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 삭제 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 포트폴리오 전체 삭제
     */
    @Override
    @Transactional
    public boolean deleteAllPortfolio(String memberId) {
        try {
            int result = portfolioDAO.deleteAllPortfolio(memberId);
            return result > 0;
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 전체 삭제 실패: " + e.getMessage());
            return false;
        }
    }
}
