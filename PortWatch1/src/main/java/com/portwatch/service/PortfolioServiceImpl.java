package com.portwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.persistence.PortfolioDAO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioServiceImpl implements PortfolioService {
    
    @Autowired
    private PortfolioDAO portfolioDAO;
    
    @Override
    public void addPortfolio(PortfolioVO portfolio) throws Exception {
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
    public Map<String, Object> getPortfolioSummary(int memberId) throws Exception {
        return portfolioDAO.getPortfolioSummary(memberId);
    }
    
    @Override
    public void updatePortfolio(PortfolioVO portfolio) throws Exception {
        portfolioDAO.updatePortfolio(portfolio);
    }
    
    @Override
    public void deletePortfolio(long portfolioId) throws Exception {
        portfolioDAO.deletePortfolio(portfolioId);
    }
}
