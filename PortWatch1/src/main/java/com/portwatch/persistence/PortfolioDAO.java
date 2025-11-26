package com.portwatch.persistence;

import com.portwatch.domain.PortfolioVO;
import java.util.List;
import java.util.Map;

public interface PortfolioDAO {
    public void insertPortfolio(PortfolioVO portfolio) throws Exception;
    public List<PortfolioVO> selectPortfolioByMember(int memberId) throws Exception;
    public PortfolioVO selectPortfolioById(long portfolioId) throws Exception;
    public int checkDuplicate(Map<String, Object> params) throws Exception;
    public void updatePortfolio(PortfolioVO portfolio) throws Exception;
    public void deletePortfolio(long portfolioId) throws Exception;
    public Map<String, Object> getPortfolioSummary(int memberId) throws Exception;
}
