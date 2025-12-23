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
 * ✅ 포트폴리오 서비스 구현체 (완전 구현)
 * 
 * @author PortWatch
 * @version 3.0 - 모든 메서드 구현 완료 + Spring 5.0.7 호환
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
        
        // DAO에 이 메서드가 없으면 구현 필요
        try {
            // portfolioDAO.selectPortfolioByMemberAndStock 사용
            List<PortfolioVO> list = portfolioDAO.selectPortfolioByMemberId(memberId);
            
            PortfolioVO result = null;
            for (PortfolioVO portfolio : list) {
                if (stockCode.equals(portfolio.getStockCode())) {
                    result = portfolio;
                    break;
                }
            }
            
            if (result != null) {
                StockVO stock = stockDAO.selectStockByCode(stockCode);
                if (stock != null) {
                    result.setCurrentPrice(stock.getCurrentPrice());
                    result.setStockName(stock.getStockName());
                }
            }
            
            return result;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 조회 실패: " + e.getMessage());
            return null;
        }
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
                totalBuyAmount += portfolio.getQuantity().doubleValue() * portfolio.getAvgPrice().doubleValue();
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
            PortfolioVO existingPortfolio = getPortfolioByMemberAndStock(memberId, stockCode);
            
            if (existingPortfolio != null) {
                // ✅ 기존 보유 종목 - 평균 매입가 계산 후 수량 증가
                System.out.println("📌 기존 보유 종목 - 수량 추가");
                
                double existingQuantity = existingPortfolio.getQuantity().doubleValue();
                double existingAvgPrice = existingPortfolio.getAvgPrice().doubleValue();
                
                // 평균 매입가 계산: (기존 총액 + 신규 총액) / (기존 수량 + 신규 수량)
                double totalAmount = (existingQuantity * existingAvgPrice) + (quantity * price);
                double totalQuantity = existingQuantity + quantity;
                double newAvgPrice = totalAmount / totalQuantity;
                
                System.out.println("  - 기존 수량: " + existingQuantity);
                System.out.println("  - 기존 평균가: " + existingAvgPrice);
                System.out.println("  - 신규 평균가: " + newAvgPrice);
                System.out.println("  - 총 수량: " + totalQuantity);
                
                // 포트폴리오 업데이트
                existingPortfolio.setQuantity(new java.math.BigDecimal(totalQuantity));
                existingPortfolio.setAvgPrice(new java.math.BigDecimal(newAvgPrice));
                
                portfolioDAO.updatePortfolio(existingPortfolio);
                
                System.out.println("✅ 포트폴리오 업데이트 성공");
                System.out.println("══════════════════════════════════════════");
                return true;
                
            } else {
                // ✅ 신규 종목 - 포트폴리오에 추가
                System.out.println("📌 신규 종목 - 포트폴리오에 추가");
                
                // stockCode로 stockId 찾기
                StockVO stock = stockDAO.selectStockByCode(stockCode);
                if (stock == null) {
                    System.err.println("❌ 종목을 찾을 수 없습니다: " + stockCode);
                    return false;
                }
                
                PortfolioVO newPortfolio = new PortfolioVO();
                newPortfolio.setMemberId(memberId);
                newPortfolio.setStockId(stock.getStockId());
                newPortfolio.setStockCode(stockCode);
                newPortfolio.setQuantity(new java.math.BigDecimal(quantity));
                newPortfolio.setAvgPrice(new java.math.BigDecimal(price));
                
                portfolioDAO.insertPortfolio(newPortfolio);
                
                System.out.println("✅ 포트폴리오 추가 성공");
                System.out.println("══════════════════════════════════════════");
                return true;
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
            return false;
        }
        
        return addStockToPortfolio(
            item.getMemberId(),
            item.getStockCode(),
            item.getQuantity().doubleValue(),
            item.getPrice().doubleValue()
        );
    }
    
    /**
     * ✅ 주식 매도 - 포트폴리오에서 제거 또는 수량 감소
     */
    @Override
    @Transactional
    public boolean removeStockFromPortfolio(String memberId, String stockCode, double quantity) {
        try {
            PortfolioVO portfolio = getPortfolioByMemberAndStock(memberId, stockCode);
            
            if (portfolio == null) {
                System.err.println("❌ 보유하지 않은 종목입니다.");
                return false;
            }
            
            double currentQuantity = portfolio.getQuantity().doubleValue();
            
            if (currentQuantity < quantity) {
                System.err.println("❌ 보유 수량보다 많이 매도할 수 없습니다.");
                return false;
            }
            
            if (currentQuantity == quantity) {
                // 전량 매도 - 포트폴리오에서 삭제
                return deletePortfolio(memberId, stockCode);
            } else {
                // 일부 매도 - 수량만 감소
                portfolio.setQuantity(new java.math.BigDecimal(currentQuantity - quantity));
                portfolioDAO.updatePortfolio(portfolio);
                return true;
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
            
            portfolioDAO.updatePortfolio(portfolio);
            
            System.out.println("✅ 포트폴리오 업데이트 성공");
            
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
                totalValue += portfolio.getQuantity().doubleValue() * portfolio.getCurrentPrice().doubleValue();
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
                double buyAmount = portfolio.getQuantity().doubleValue() * portfolio.getAvgPrice().doubleValue();
                double currentAmount = portfolio.getQuantity().doubleValue() * portfolio.getCurrentPrice().doubleValue();
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
                totalBuyAmount += portfolio.getQuantity().doubleValue() * portfolio.getAvgPrice().doubleValue();
                totalCurrentAmount += portfolio.getQuantity().doubleValue() * portfolio.getCurrentPrice().doubleValue();
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
            
            PortfolioVO portfolio = getPortfolioByMemberAndStock(memberId, stock.getStockCode());
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
            // stockCode로 stockId 찾기
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            if (stock == null) {
                return false;
            }
            
            portfolioDAO.deletePortfolioByMemberAndStock(memberId, stock.getStockId());
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 삭제 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * ✅ 포트폴리오 삭제 (Long portfolioId 버전)
     */
    @Override
    @Transactional
    public void deletePortfolio(Long portfolioId) {
        if (portfolioId == null) {
            throw new IllegalArgumentException("포트폴리오 ID가 null입니다.");
        }
        
        try {
            System.out.println("🗑️ 포트폴리오 삭제: ID=" + portfolioId);
            
            portfolioDAO.deletePortfolio(portfolioId);
            
            System.out.println("✅ 포트폴리오 삭제 성공");
            
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
            List<PortfolioVO> list = portfolioDAO.selectPortfolioByMemberId(memberId);
            
            for (PortfolioVO portfolio : list) {
                portfolioDAO.deletePortfolio(portfolio.getPortfolioId());
            }
            
            return true;
            
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
        throw new UnsupportedOperationException("이 메서드는 사용되지 않습니다. deleteAllPortfolio(String memberId)를 사용하세요.");
    }
    
    /**
     * ✅ PortfolioApiController 전용 메서드들
     */
    
    @Override
    public PortfolioVO getByMemberAndStock(Integer memberId, Integer stockId) {
        // Integer memberId를 String으로 변환
        String memberIdStr = String.valueOf(memberId);
        
        // stockId로 stockCode 찾기
        StockVO stock = stockDAO.selectById(stockId);
        if (stock == null) {
            return null;
        }
        
        return getPortfolioByMemberAndStock(memberIdStr, stock.getStockCode());
    }
    
    @Override
    @Transactional
    public void update(PortfolioVO portfolio) {
        updatePortfolio(portfolio);
    }
    
    @Override
    @Transactional
    public void insert(PortfolioVO portfolio) {
        try {
            portfolioDAO.insertPortfolio(portfolio);
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 추가 실패", e);
        }
    }
    
    @Override
    @Transactional
    public void delete(Integer portfolioId) {
        deletePortfolio(portfolioId.longValue());
    }
}
