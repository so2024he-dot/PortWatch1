package com.portwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.StockDAO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockServiceImpl implements StockService {
    
    @Autowired
    private StockDAO stockDAO;
    
    @Override
    public List<StockVO> searchStocks(String keyword, String marketType) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("marketType", marketType);
        return stockDAO.searchStocks(params);
    }
    
    @Override
    public List<StockVO> getAutocomplete(String keyword) throws Exception {
        return stockDAO.getStockAutocomplete(keyword);
    }
    
    @Override
    public StockVO getStockByCode(String stockCode) throws Exception {
        return stockDAO.getStockByCode(stockCode);
    }
    
    @Override
    public List<StockVO> getTopVolume(int limit) throws Exception {
        return stockDAO.getTopVolume(limit);
    }
    
    @Override
    public List<StockVO> getTopGainers(int limit) throws Exception {
        return stockDAO.getTopGainers(limit);
    }
}
