package com.portwatch.service;

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
 * ✅ 포트폴리오 서비스 구현체
 * 
 * @author PortWatch
 * @version 2.0 - 전체 메서드 구현 완료 + 한글 인코딩 수정
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
     * ✅ 포트폴리오 목록 조회 (getPortfolioByMemberId 재사용)
     */
    @Override
    public List<PortfolioVO> getPortfolioList(String memberId) {
        return getPortfolioByMemberId(memberId);
    }
    
    /**
     * ❌ Integer 버전은 사용 안 함 - String으로 통일
     */
    @Override
    public List<PortfolioVO> getPortfolioList(Integer memberId) {
        throw new UnsupportedOperationException("memberId는 String 타입을 사용하세요");
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
     * ✅ 포트폴리오 요약 정보 조회 (String memberId)
     */
    @Override
    public Map<String, Object> getPortfolioSummary(String memberId) {
        Map<String, Object> summary = new HashMap<>();
        
        try {
            // 전체 포트폴리오 조회
            List<PortfolioVO> portfolioList = getPortfolioByMemberId(memberId);
            
            // 총 평가금액 계산
            double totalValue = getTotalValue(memberId);
            
            // 총 손익 계산
            double totalProfit = getTotalProfit(memberId);
            
            // 수익률 계산
            double totalProfitRate = getTotalProfitRate(memberId);
            
            // 총 매입금액 계산
            double totalBuyAmount = 0.0;
            for (PortfolioVO portfolio : portfolioList) {
                totalBuyAmount += portfolio.getQuantity() * portfolio.getAvgPrice();
            }
            
            // 보유 종목 수
            int stockCount = portfolioList.size();
            
            // 요약 정보 저장
            summary.put("totalValue", totalValue);              // 총 평가금액
            summary.put("totalProfit", totalProfit);            // 총 손익
            summary.put("totalProfitRate", totalProfitRate);    // 수익률
            summary.put("totalBuyAmount", totalBuyAmount);      // 총 매입금액
            summary.put("stockCount", stockCount);              // 보유 종목 수
            
            System.out.println("✅ 포트폴리오 요약 조회 완료");
            System.out.println("   총 평가금액: " + totalValue);
            System.out.println("   총 손익: " + totalProfit);
            System.out.println("   수익률: " + totalProfitRate + "%");
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 요약 조회 실패: " + e.getMessage());
            e.printStackTrace();
            
            // 에러 발생 시 기본값
            summary.put("totalValue", 0.0);
            summary.put("totalProfit", 0.0);
            summary.put("totalProfitRate", 0.0);
            summary.put("totalBuyAmount", 0.0);
            summary.put("stockCount", 0);
        }
        
        return summary;
    }
    
    /**
     * ❌ Integer 버전은 사용 안 함
     */
    @Override
    public Map<String, Object> getPortfolioSummary(Integer memberId) {
        throw new UnsupportedOperationException("memberId는 String 타입을 사용하세요");
    }
    
    /**
     * ✅ 주식 매수 - 포트폴리오에 추가 또는 수량 증가 (핵심 메서드!)
     */
    @Override
    @Transactional
    public boolean addStockToPortfolio(String memberId, String stockCode, double quantity, double price) {
        try {
            System.out.println("══════════════════════════════════════════");
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
                    System.out.println("══════════════════════════════════════════");
                    return true;
                } else {
                    System.err.println("❌ 포트폴리오 업데이트 실패");
                    System.out.println("══════════════════════════════════════════");
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
                    System.out.println("══════════════════════════════════════════");
                    return true;
                } else {
                    System.err.println("❌ 포트폴리오 추가 실패");
                    System.out.println("══════════════════════════════════════════");
                    return false;
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 추가 중 오류: " + e.getMessage());
            e.printStackTrace();
            System.out.println("══════════════════════════════════════════");
            return false;
        }
    }
    
    /**
     * ✅ PortfolioItemVO를 사용한 주식 추가
     */
    @Override
    @Transactional
    public boolean addStockToPortfolio(PortfolioItemVO item) {
        if (item == null) {
            System.err.println("❌ PortfolioItemVO가 null입니다.");
            return false;
        }
        
        return addStockToPortfolio(
            item.getMemberId(),
            item.getStockCode(),
            item.getQuantity(),
            item.getPurchasePrice()
        );
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
     * ✅ 포트폴리오 업데이트
     */
    @Override
    @Transactional
    public void updatePortfolio(PortfolioVO portfolio) {
        if (portfolio == null) {
            throw new IllegalArgumentException("포트폴리오 정보가 null입니다.");
        }
        
        try {
            System.out.println("🔄 포트폴리오 업데이트: " + portfolio.getStockCode());
            
            int result = portfolioDAO.updatePortfolio(portfolio);
            
            if (result > 0) {
                System.out.println("✅ 포트폴리오 업데이트 성공");
            } else {
                System.err.println("❌ 포트폴리오 업데이트 실패 (영향받은 행 없음)");
            }
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 업데이트 중 오류: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("포트폴리오 업데이트 실패", e);
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
     * ✅ 중복 체크 (String memberId + Integer stockId)
     */
    @Override
    public boolean checkDuplicate(String memberId, Integer stockId) {
        try {
            // stockId로 stockCode를 찾아서 체크
            StockVO stock = stockDAO.selectById(stockId);
            if (stock == null) {
                return false;
            }
            
            PortfolioVO portfolio = portfolioDAO.selectPortfolioByMemberAndStock(memberId, stock.getStockCode());
            return portfolio != null;
            
        } catch (Exception e) {
            System.err.println("❌ 중복 체크 중 오류: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * ❌ Integer memberId 버전은 사용 안 함
     */
    @Override
    public boolean checkDuplicate(Integer memberId, Integer stockId) {
        throw new UnsupportedOperationException("memberId는 String 타입을 사용하세요");
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
     * ✅ 포트폴리오 삭제 (Long portfolioId 버전)
     * 주의: DAO에 해당 메서드가 없으면 UnsupportedOperationException 발생
     */
    @Override
    @Transactional
    public void deletePortfolio(Long portfolioId) {
        if (portfolioId == null) {
            throw new IllegalArgumentException("포트폴리오 ID가 null입니다.");
        }
        
        try {
            System.out.println("🗑️ 포트폴리오 삭제: ID=" + portfolioId);
            
            // portfolioId로 삭제하는 DAO 메서드가 필요
            // 현재는 memberId + stockCode로만 삭제 가능하므로 예외 처리
            throw new UnsupportedOperationException(
                "portfolioId로 삭제하는 기능은 DAO에 구현이 필요합니다. " +
                "대신 deletePortfolio(String memberId, String stockCode)를 사용하세요."
            );
            
        } catch (UnsupportedOperationException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 삭제 중 오류: " + e.getMessage());
            throw new RuntimeException("포트폴리오 삭제 실패", e);
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
    
    /**
     * ✅ 포트폴리오 전체 삭제 (Long portfolioId 버전)
     */
    @Override
    @Transactional
    public void deleteAllPortfolio(Long portfolioId) {
        if (portfolioId == null) {
            throw new IllegalArgumentException("포트폴리오 ID가 null입니다.");
        }
        
        try {
            System.out.println("🗑️ 포트폴리오 전체 삭제: ID=" + portfolioId);
            
            // portfolioId로 전체 삭제하는 DAO 메서드가 필요
            throw new UnsupportedOperationException(
                "portfolioId로 전체 삭제하는 기능은 DAO에 구현이 필요합니다. " +
                "대신 deleteAllPortfolio(String memberId)를 사용하세요."
            );
            
        } catch (UnsupportedOperationException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 전체 삭제 중 오류: " + e.getMessage());
            throw new RuntimeException("포트폴리오 전체 삭제 실패", e);
        }
    }
}
