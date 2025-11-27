package com.portwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.PortfolioStockVO;
import com.portwatch.persistence.PortfolioDAO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 포트폴리오 서비스 구현
 */
@Service
public class PortfolioServiceImpl implements PortfolioService {
    
    @Autowired
    private PortfolioDAO portfolioDAO;
    
    @Override
    @Transactional
    public void addPortfolio(PortfolioVO portfolio) throws Exception {
        // 중복 확인
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", portfolio.getMemberId());
        params.put("stockId", portfolio.getStockId());
        
        int count = portfolioDAO.checkDuplicate(params);
        if (count > 0) {
            throw new Exception("이미 포트폴리오에 등록된 종목입니다.");
        }
        
        portfolioDAO.insertPortfolio(portfolio);
    }
    
    @Override
    public List<PortfolioVO> getPortfolioList(int memberId) throws Exception {
        return portfolioDAO.selectPortfolioByMember(memberId);
    }
    
    @Override
    public PortfolioVO getPortfolioById(Long portfolioId) throws Exception {
        return portfolioDAO.selectPortfolioById(portfolioId);
    }
    
    @Override
    public Map<String, Object> getPortfolioSummary(int memberId) throws Exception {
        Map<String, Object> summary = portfolioDAO.getPortfolioSummary(memberId);
        
        // null 값 처리
        if (summary == null) {
            summary = new HashMap<>();
            summary.put("totalInvestment", 0);
            summary.put("totalValue", 0);
            summary.put("totalProfit", 0);
            summary.put("totalProfitRate", 0.0);
        }
        
        // null 값을 0으로 변환
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
        
        return summary;
    }
    
    @Override
    @Transactional
    public void updatePortfolio(PortfolioVO portfolio) throws Exception {
        portfolioDAO.updatePortfolio(portfolio);
    }
    
    @Override
    @Transactional
    public void deletePortfolio(long portfolioId) throws Exception {
        portfolioDAO.deletePortfolio(portfolioId);
    }
    
    @Override
    public List<PortfolioStockVO> getPortfolioStocks(Long portfolioId) throws Exception {
        // 이 메서드는 PORTFOLIO_STOCK 테이블을 사용하는 경우에 구현
        // 현재는 PORTFOLIO 테이블만 사용하므로 빈 리스트 반환
        return null;
    }
}

    
