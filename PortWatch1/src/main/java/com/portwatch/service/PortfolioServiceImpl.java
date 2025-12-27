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

import com.portwatch.domain.PortfolioItemVO;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.PortfolioDAO;
import com.portwatch.persistence.StockDAO;

/**
 * ✅ 포트폴리오 Service 구현 클래스 - 완전 구현
 * 
 * @author PortWatch
 * @version FINAL COMPLETE - Spring 5.0.7 + MySQL 8.0.33
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
    public List<PortfolioVO> getPortfolioList(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 포트폴리오 목록 조회 (String)");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            List<PortfolioVO> portfolioList = portfolioDAO.selectPortfolioByMember(memberId);
            
            if (portfolioList == null) {
                portfolioList = new ArrayList<>();
            }
            
            System.out.println("  - 포트폴리오 개수: " + portfolioList.size());
            System.out.println("✅ 포트폴리오 목록 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return portfolioList;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 목록 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 목록 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오 목록 조회 (Integer memberId) - 추가!
     */
    @Override
    public List<PortfolioVO> getPortfolioList(Integer memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 포트폴리오 목록 조회 (Integer)");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            // Integer를 String으로 변환
            String memberIdStr = String.valueOf(memberId);
            return getPortfolioList(memberIdStr);
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 목록 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 목록 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오 조회 by Member ID - 추가!
     */
    @Override
    public List<PortfolioVO> getPortfolioByMemberId(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 포트폴리오 조회 by Member ID");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            return getPortfolioList(memberId);
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오 조회 by Member and Stock - 추가!
     */
    @Override
    public PortfolioVO getPortfolioByMemberAndStock(String memberId, String stockCode) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 포트폴리오 조회 by Member and Stock");
        System.out.println("  - 회원 ID: " + memberId);
        System.out.println("  - 종목 코드: " + stockCode);
        
        try {
            // 1. 종목 코드로 stockId 조회
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            
            if (stock == null) {
                System.out.println("⚠️ 종목을 찾을 수 없습니다");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return null;
            }
            
            // 2. memberId와 stockId로 포트폴리오 조회
            Integer stockId = stock.getStockId();
            PortfolioVO portfolio = portfolioDAO.selectByMemberAndStock(memberId, stockId);
            
            if (portfolio != null) {
                System.out.println("✅ 포트폴리오 조회 완료");
                System.out.println("  - 수량: " + portfolio.getQuantity());
            } else {
                System.out.println("⚠️ 포트폴리오를 찾을 수 없습니다");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return portfolio;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오 요약 정보 조회 (String memberId)
     */
    @Override
    public Map<String, Object> getPortfolioSummary(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📈 포트폴리오 요약 정보 조회");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            List<PortfolioVO> portfolioList = getPortfolioList(memberId);
            
            BigDecimal totalInvestment = BigDecimal.ZERO;
            BigDecimal totalCurrentValue = BigDecimal.ZERO;
            BigDecimal totalProfit = BigDecimal.ZERO;
            double totalProfitRate = 0.0;
            int stockCount = portfolioList.size();
            
            for (PortfolioVO portfolio : portfolioList) {
                if (portfolio.getPurchasePrice() != null && portfolio.getQuantity() != null) {
                    BigDecimal investment = ((BigDecimal) portfolio.getPurchasePrice())
                        .multiply(portfolio.getQuantity());
                    totalInvestment = totalInvestment.add(investment);
                }
                
                if (portfolio.getCurrentPrice() != null && portfolio.getQuantity() != null) {
                    BigDecimal currentValue = portfolio.getCurrentPrice()
                        .multiply(portfolio.getQuantity());
                    totalCurrentValue = totalCurrentValue.add(currentValue);
                }
            }
            
            totalProfit = totalCurrentValue.subtract(totalInvestment);
            
            if (totalInvestment.compareTo(BigDecimal.ZERO) > 0) {
                totalProfitRate = totalProfit.divide(totalInvestment, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
            }
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalInvestment", totalInvestment);
            summary.put("totalCurrentValue", totalCurrentValue);
            summary.put("totalProfit", totalProfit);
            summary.put("totalProfitRate", totalProfitRate);
            summary.put("stockCount", stockCount);
            
            System.out.println("  - 총 투자액: " + totalInvestment);
            System.out.println("  - 현재 평가액: " + totalCurrentValue);
            System.out.println("  - 총 수익: " + totalProfit);
            System.out.println("  - 수익률: " + totalProfitRate + "%");
            System.out.println("✅ 포트폴리오 요약 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return summary;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 요약 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 요약 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오 요약 정보 조회 (Integer memberId) - 추가!
     */
    @Override
    public Map<String, Object> getPortfolioSummary(Integer memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📈 포트폴리오 요약 정보 조회 (Integer)");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            // Integer를 String으로 변환
            String memberIdStr = String.valueOf(memberId);
            return getPortfolioSummary(memberIdStr);
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 요약 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 요약 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 중복 체크 (String memberId, Integer stockId)
     */
    @Override
    public boolean checkDuplicate(String memberId, Integer stockId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 중복 체크 (String, Integer)");
        System.out.println("  - 회원 ID: " + memberId);
        System.out.println("  - 종목 ID: " + stockId);
        
        try {
            PortfolioVO portfolio = portfolioDAO.selectByMemberAndStock(memberId, stockId);
            boolean exists = (portfolio != null);
            
            if (exists) {
                System.out.println("⚠️ 이미 보유 중인 종목");
            } else {
                System.out.println("✅ 보유하지 않은 종목");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return exists;
            
        } catch (Exception e) {
            System.err.println("❌ 중복 체크 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("중복 체크 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 중복 체크 (Integer memberId, Integer stockId) - 추가!
     */
    @Override
    public boolean checkDuplicate(Integer memberId, Integer stockId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 중복 체크 (Integer, Integer)");
        System.out.println("  - 회원 ID: " + memberId);
        System.out.println("  - 종목 ID: " + stockId);
        
        try {
            // Integer를 String으로 변환
            String memberIdStr = String.valueOf(memberId);
            return checkDuplicate(memberIdStr, stockId);
            
        } catch (Exception e) {
            System.err.println("❌ 중복 체크 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("중복 체크 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오 전체 삭제 (String memberId)
     */
    @Override
    @Transactional
    public void deleteAllPortfolio(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 포트폴리오 전체 삭제 (String)");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            portfolioDAO.deleteAllByMember(memberId);
            
            System.out.println("✅ 포트폴리오 전체 삭제 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 전체 삭제 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 전체 삭제 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오 전체 삭제 (Long portfolioId) - 추가!
     */
    @Override
    @Transactional
    public void deleteAllPortfolio(Long portfolioId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 포트폴리오 삭제 by ID");
        System.out.println("  - 포트폴리오 ID: " + portfolioId);
        
        try {
            // portfolioId로 삭제 (단일 삭제)
            portfolioDAO.deletePortfolio(portfolioId);
            
            System.out.println("✅ 포트폴리오 삭제 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 삭제 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 삭제 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오에 주식 추가 (PortfolioVO)
     */
    @Override
    @Transactional
    public boolean addStockToPortfolio(PortfolioVO portfolio) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("➕ 포트폴리오에 주식 추가 (PortfolioVO)");
        System.out.println("  - 회원 ID: " + portfolio.getMemberId());
        System.out.println("  - 종목 ID: " + portfolio.getStockId());
        
        try {
            // 중복 체크
            PortfolioVO existingPortfolio = portfolioDAO.selectByMemberAndStock(
                portfolio.getMemberId(), 
                portfolio.getStockId()
            );
            
            if (existingPortfolio != null) {
                // 이미 존재하면 수량과 평균 단가 업데이트
                BigDecimal newQuantity = existingPortfolio.getQuantity().add(portfolio.getQuantity());
                
                // 평균 매입 단가 계산
                BigDecimal existingTotalCost = ((BigDecimal) existingPortfolio.getPurchasePrice())
                    .multiply(existingPortfolio.getQuantity());
                BigDecimal newTotalCost = ((BigDecimal) portfolio.getPurchasePrice())
                    .multiply(portfolio.getQuantity());
                BigDecimal combinedTotalCost = existingTotalCost.add(newTotalCost);
                BigDecimal averagePrice = combinedTotalCost
                    .divide(newQuantity, 2, RoundingMode.HALF_UP);
                
                existingPortfolio.setQuantity(newQuantity);
                existingPortfolio.setPurchasePrice(averagePrice);
                
                portfolioDAO.updatePortfolio(existingPortfolio);
                
                System.out.println("✅ 기존 포트폴리오 업데이트 완료");
                System.out.println("  - 새 수량: " + newQuantity);
                System.out.println("  - 평균 단가: " + averagePrice);
            } else {
                // 새로 추가
                portfolioDAO.insertPortfolio(portfolio);
                
                System.out.println("✅ 새 포트폴리오 추가 완료");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 추가 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 추가 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오에 주식 추가 (4개 파라미터) - 추가!
     */
    @Override
    @Transactional
    public boolean addStockToPortfolio(String memberId, String stockCode, double quantity, double price) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("➕ 포트폴리오에 주식 추가 (4 params)");
        System.out.println("  - 회원 ID: " + memberId);
        System.out.println("  - 종목 코드: " + stockCode);
        System.out.println("  - 수량: " + quantity);
        System.out.println("  - 가격: " + price);
        
        try {
            // 1. 종목 코드로 stockId 조회
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            
            if (stock == null) {
                System.out.println("❌ 종목을 찾을 수 없습니다");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return false;
            }
            
            // 2. PortfolioVO 생성
            PortfolioVO portfolio = new PortfolioVO();
            portfolio.setMemberId(memberId);
            portfolio.setStockId(stock.getStockId());
            portfolio.setQuantity(quantity);
            portfolio.setPurchasePrice(BigDecimal.valueOf(price));
            portfolio.setCurrentPrice(BigDecimal.valueOf(price));
            
            // 3. 추가
            return addStockToPortfolio(portfolio);
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 추가 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 추가 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오에 주식 추가 (PortfolioItemVO) - 추가!
     */
    @Override
    @Transactional
    public boolean addStockToPortfolio(PortfolioItemVO item) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("➕ 포트폴리오에 주식 추가 (PortfolioItemVO)");
        
        try {
            // PortfolioItemVO → PortfolioVO 변환
            PortfolioVO portfolio = new PortfolioVO();
            portfolio.setMemberId(item.getMemberId());
            portfolio.setStockId(item.getStockId());
            portfolio.setQuantity(item.getQuantity());
            portfolio.setPurchasePrice(item.getPurchasePrice());
            portfolio.setCurrentPrice(item.getCurrentPrice() != null ? 
                item.getCurrentPrice() : item.getPurchasePrice());
            
            return addStockToPortfolio(portfolio);
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 추가 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 추가 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오에서 주식 제거
     */
    @Override
    @Transactional
    public boolean removeStockFromPortfolio(String memberId, String stockCode, double quantity) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("➖ 포트폴리오에서 주식 제거");
        System.out.println("  - 회원 ID: " + memberId);
        System.out.println("  - 종목 코드: " + stockCode);
        System.out.println("  - 수량: " + quantity);
        
        try {
            // 1. 종목 코드로 stockId 조회
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            
            if (stock == null) {
                System.out.println("❌ 종목을 찾을 수 없습니다");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return false;
            }
            
            // 2. 기존 포트폴리오 조회
            PortfolioVO portfolio = portfolioDAO.selectByMemberAndStock(memberId, stock.getStockId());
            
            if (portfolio == null) {
                System.out.println("❌ 포트폴리오를 찾을 수 없습니다");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return false;
            }
            
            // 3. 수량 비교
            if (portfolio.getQuantity().compareTo(BigDecimal.valueOf(quantity)) <= 0) {
                // 전체 삭제
                portfolioDAO.deletePortfolio(portfolio.getPortfolioId());
                System.out.println("✅ 포트폴리오 전체 삭제 완료");
            } else {
                // 일부 감소
                portfolio.setQuantity(portfolio.getQuantity().subtract(BigDecimal.valueOf(quantity)));
                portfolioDAO.updatePortfolio(portfolio);
                System.out.println("✅ 포트폴리오 수량 감소 완료");
                System.out.println("  - 남은 수량: " + portfolio.getQuantity());
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 제거 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 제거 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오 조회 (ID로)
     */
    @Override
    public PortfolioVO getPortfolioById(Long portfolioId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 포트폴리오 조회 (ID)");
        System.out.println("  - 포트폴리오 ID: " + portfolioId);
        
        try {
            PortfolioVO portfolio = portfolioDAO.selectPortfolioById(portfolioId);
            
            if (portfolio != null) {
                System.out.println("✅ 포트폴리오 조회 완료");
            } else {
                System.out.println("⚠️ 포트폴리오를 찾을 수 없습니다");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return portfolio;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오 업데이트 - 추가!
     */
    @Override
    @Transactional
    public void updatePortfolio(PortfolioVO portfolio) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✏️ 포트폴리오 업데이트");
        System.out.println("  - 포트폴리오 ID: " + portfolio.getPortfolioId());
        
        try {
            portfolioDAO.updatePortfolio(portfolio);
            
            System.out.println("✅ 포트폴리오 업데이트 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 업데이트 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 업데이트 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 총 평가액 조회 - 추가!
     */
    @Override
    public double getTotalValue(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💰 총 평가액 조회");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            Map<String, Object> summary = getPortfolioSummary(memberId);
            BigDecimal totalValue = (BigDecimal) summary.get("totalCurrentValue");
            
            double result = totalValue.doubleValue();
            
            System.out.println("  - 총 평가액: " + result);
            System.out.println("✅ 총 평가액 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return result;
            
        } catch (Exception e) {
            System.err.println("❌ 총 평가액 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("총 평가액 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 총 수익 조회 - 추가!
     */
    @Override
    public double getTotalProfit(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📈 총 수익 조회");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            Map<String, Object> summary = getPortfolioSummary(memberId);
            BigDecimal totalProfit = (BigDecimal) summary.get("totalProfit");
            
            double result = totalProfit.doubleValue();
            
            System.out.println("  - 총 수익: " + result);
            System.out.println("✅ 총 수익 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return result;
            
        } catch (Exception e) {
            System.err.println("❌ 총 수익 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("총 수익 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 총 수익률 조회 - 추가!
     */
    @Override
    public double getTotalProfitRate(String memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 총 수익률 조회");
        System.out.println("  - 회원 ID: " + memberId);
        
        try {
            Map<String, Object> summary = getPortfolioSummary(memberId);
            double totalProfitRate = (Double) summary.get("totalProfitRate");
            
            System.out.println("  - 총 수익률: " + totalProfitRate + "%");
            System.out.println("✅ 총 수익률 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return totalProfitRate;
            
        } catch (Exception e) {
            System.err.println("❌ 총 수익률 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("총 수익률 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오 삭제 (String memberId, String stockCode) - 추가!
     */
    @Override
    @Transactional
    public boolean deletePortfolio(String memberId, String stockCode) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 포트폴리오 삭제 (String, String)");
        System.out.println("  - 회원 ID: " + memberId);
        System.out.println("  - 종목 코드: " + stockCode);
        
        try {
            // 1. 종목 코드로 stockId 조회
            StockVO stock = stockDAO.selectStockByCode(stockCode);
            
            if (stock == null) {
                System.out.println("❌ 종목을 찾을 수 없습니다");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return false;
            }
            
            // 2. 포트폴리오 조회
            PortfolioVO portfolio = portfolioDAO.selectByMemberAndStock(memberId, stock.getStockId());
            
            if (portfolio == null) {
                System.out.println("❌ 포트폴리오를 찾을 수 없습니다");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return false;
            }
            
            // 3. 삭제
            portfolioDAO.deletePortfolio(portfolio.getPortfolioId());
            
            System.out.println("✅ 포트폴리오 삭제 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 삭제 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 삭제 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오 삭제 (Long portfolioId) - 추가!
     */
    @Override
    @Transactional
    public void deletePortfolio(Long portfolioId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 포트폴리오 삭제 (Long)");
        System.out.println("  - 포트폴리오 ID: " + portfolioId);
        
        try {
            portfolioDAO.deletePortfolio(portfolioId);
            
            System.out.println("✅ 포트폴리오 삭제 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 삭제 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 삭제 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ Member와 Stock으로 조회 - 추가!
     */
    @Override
    public PortfolioVO getByMemberAndStock(Integer memberId, Integer stockId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 Member와 Stock으로 조회");
        System.out.println("  - 회원 ID: " + memberId);
        System.out.println("  - 종목 ID: " + stockId);
        
        try {
            String memberIdStr = String.valueOf(memberId);
            PortfolioVO portfolio = portfolioDAO.selectByMemberAndStock(memberIdStr, stockId);
            
            if (portfolio != null) {
                System.out.println("✅ 포트폴리오 조회 완료");
            } else {
                System.out.println("⚠️ 포트폴리오를 찾을 수 없습니다");
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return portfolio;
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 조회 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오 update - 추가!
     */
    @Override
    @Transactional
    public void update(PortfolioVO portfolio) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✏️ 포트폴리오 update");
        
        try {
            portfolioDAO.updatePortfolio(portfolio);
            
            System.out.println("✅ 포트폴리오 update 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 update 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 update 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오 insert - 추가!
     */
    @Override
    @Transactional
    public void insert(PortfolioVO portfolio) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("➕ 포트폴리오 insert");
        
        try {
            portfolioDAO.insertPortfolio(portfolio);
            
            System.out.println("✅ 포트폴리오 insert 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 insert 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 insert 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ 포트폴리오 delete - 추가!
     */
    @Override
    @Transactional
    public void delete(Integer portfolioId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 포트폴리오 delete");
        System.out.println("  - 포트폴리오 ID: " + portfolioId);
        
        try {
            Long portfolioIdLong = Long.valueOf(portfolioId);
            portfolioDAO.deletePortfolio(portfolioIdLong);
            
            System.out.println("✅ 포트폴리오 delete 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 delete 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            throw new Exception("포트폴리오 delete 실패: " + e.getMessage(), e);
        }
    }
}
