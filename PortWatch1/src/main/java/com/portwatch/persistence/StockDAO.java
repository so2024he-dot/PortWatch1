package com.portwatch.persistence;

import com.portwatch.domain.StockVO;
import java.util.List;
import java.util.Map;

public interface StockDAO {
    public List<StockVO> searchStocks(Map<String, Object> params) throws Exception;
    public List<StockVO> getStockAutocomplete(String keyword) throws Exception;
    public StockVO getStockByCode(String stockCode) throws Exception;
    public StockVO getStockById(int stockId) throws Exception;
    public List<StockVO> getTopVolume(int limit) throws Exception;
    public List<StockVO> getTopGainers(int limit) throws Exception;
}
