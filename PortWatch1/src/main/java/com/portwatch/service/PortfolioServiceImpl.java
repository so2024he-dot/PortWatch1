package com.portwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.persistence.PortfolioDAO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 포트폴리오 Service 구현
 * 
 * ✅ 이전 작동 버전 기반
 * ✅ 중복 체크 포함
 * 
 * @author PortWatch
 * @version 5.0 (Spring 5.0.7 + MySQL 8.0)
 */
@Service
public class PortfolioServiceImpl implements PortfolioService {
    
    @Autowired
    private PortfolioDAO portfolioDAO;
    
    @Override
    @Transactional
    public void addPortfolio(PortfolioVO portfolio) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💼 Service: 포트폴리오 추가");
        
        // 중복 확인
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", portfolio.getMemberId());
        params.put("stockId", portfolio.getStockId());
        
        int count = portfolioDAO.checkDuplicate(params);
        if (count > 0) {
            System.err.println("❌ Service: 중복된 종목!");
            throw new Exception("이미 포트폴리오에 등록된 종목입니다.");
        }
        
        portfolioDAO.insertPortfolio(portfolio);
        
        System.out.println("✅ Service: 추가 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    @Override
    public List<PortfolioVO> getPortfolioList(int memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💼 Service: 포트폴리오 목록 조회");
        
        List<PortfolioVO> result = portfolioDAO.selectPortfolioByMember(memberId);
        
        System.out.println("✅ Service: " + (result != null ? result.size() : 0) + "개 조회 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return result;
    }
    
    @Override
    public PortfolioVO getPortfolioById(Long portfolioId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💼 Service: 포트폴리오 ID로 조회");
        
        PortfolioVO result = portfolioDAO.selectPortfolioById(portfolioId);
        
        System.out.println("✅ Service: 조회 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return result;
    }
    
    @Override
    public Map<String, Object> getPortfolioSummary(int memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💼 Service: 포트폴리오 요약 조회");
        
        Map<String, Object> summary = portfolioDAO.getPortfolioSummary(memberId);
        
        // null 값 처리
        if (summary == null) {
            summary = new HashMap<>();
            summary.put("stockCount", 0);
            summary.put("totalInvestment", 0);
            summary.put("totalValue", 0);
            summary.put("totalProfit", 0);
            summary.put("totalProfitRate", 0.0);
        }
        
        // null 값을 0으로 변환
        if (summary.get("stockCount") == null) {
            summary.put("stockCount", 0);
        }
        if (summary.get("totalInvestment") == null) {
            summary.put("totalInvestment", 0);
        }
        if (summary.get("totalValue") == null) {
            summary.put("totalValue", 0);
        }
        if (summary.get("totalProfit") == null) {
            summary.put("totalProfit", 0);
        }
        if (summary.get("totalProfitRate") == null) {
            summary.put("totalProfitRate", 0.0);
        }
        
        System.out.println("✅ Service: 요약 조회 완료!");
        System.out.println("  - 종목 수: " + summary.get("stockCount"));
        System.out.println("  - 총 투자금액: " + summary.get("totalInvestment"));
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return summary;
    }
    
    @Override
    @Transactional
    public void updatePortfolio(PortfolioVO portfolio) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💼 Service: 포트폴리오 수정");
        
        portfolioDAO.updatePortfolio(portfolio);
        
        System.out.println("✅ Service: 수정 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    @Override
    @Transactional
    public void deletePortfolio(long portfolioId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💼 Service: 포트폴리오 삭제");
        
        portfolioDAO.deletePortfolio(portfolioId);
        
        System.out.println("✅ Service: 삭제 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
