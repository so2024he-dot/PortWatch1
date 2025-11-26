package com.portwatch.service;

import com.portwatch.domain.StockVO;
import java.util.List;

public interface StockService {
    public List<StockVO> searchStocks(String keyword, String marketType) throws Exception;
    public List<StockVO> getAutocomplete(String keyword) throws Exception;
    public StockVO getStockByCode(String stockCode) throws Exception;
    public List<StockVO> getTopVolume(int limit) throws Exception;
    public List<StockVO> getTopGainers(int limit) throws Exception;
}
