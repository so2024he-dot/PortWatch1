package com.portwatch.service;

import com.portwatch.domain.PortfolioVO;
import com.portwatch.persistence.PortfolioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 포트폴리오 Service 구현
 * 
 * @author PortWatch
 * @version 3.0
 */
@Service
public class PortfolioServiceImpl implements PortfolioService {
    
    @Autowired
    private PortfolioDAO portfolioDAO;
    
    @Override
    @Transactional
    public void insert(PortfolioVO portfolioVO) {
        portfolioDAO.insert(portfolioVO);
    }
    
    @Override
    @Transactional
    public void update(PortfolioVO portfolioVO) {
        portfolioDAO.update(portfolioVO);
    }
    
    @Override
    @Transactional
    public void delete(int portfolioId) {
        portfolioDAO.delete(portfolioId);
    }
    
    @Override
    public List<PortfolioVO> getByMember(int memberId) {
        return portfolioDAO.selectByMember(memberId);
    }
    
    @Override
    public PortfolioVO getById(int portfolioId) {
        return portfolioDAO.selectById(portfolioId);
    }
    
    @Override
    public PortfolioVO getByMemberAndStock(int memberId, int stockId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("memberId", Integer.valueOf(memberId));
        params.put("stockId", Integer.valueOf(stockId));
        
        return portfolioDAO.selectByMemberAndStock(params);
    }
}
