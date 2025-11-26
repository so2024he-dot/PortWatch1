package com.portwatch.service;

import com.portwatch.domain.PortfolioVO;
import java.util.List;
import java.util.Map;

public interface PortfolioService {
    public void addPortfolio(PortfolioVO portfolio) throws Exception;
    public List<PortfolioVO> getPortfolioList(int memberId) throws Exception;
    public Map<String, Object> getPortfolioSummary(int memberId) throws Exception;
    public void updatePortfolio(PortfolioVO portfolio) throws Exception;
    public void deletePortfolio(long portfolioId) throws Exception;
}
