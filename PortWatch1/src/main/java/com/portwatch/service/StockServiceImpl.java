    package com.portwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.StockDAO;
import java.util.List;
import java.util.Map;

/**
 * 종목 서비스 구현
 */
@Service
public class StockServiceImpl implements StockService {
    
    @Autowired
    private StockDAO stockDAO;
    
    /**
     * 모든 종목 조회 (Map 형태)
     * JSP에서 snake_case로 접근 가능
     */
    @Override
    public List<Map<String, Object>> getAllStocks() throws Exception {
        return stockDAO.selectAllStocks();
    }
    
    /**
     * 모든 종목 목록 조회 (VO 형태)
     */
    @Override
    public List<StockVO> getAllStocksList() throws Exception {
        return stockDAO.selectAll();
    }
    
    /**
     * 종목 코드로 조회
     */
    @Override
    public StockVO getStockByCode(String stockCode) throws Exception {
        return stockDAO.selectByCode(stockCode);
    }
    
    /**
     * 종목 ID로 조회
     */
    @Override
    public StockVO getStockById(Integer stockId) throws Exception {
        return stockDAO.selectById(stockId);
    }
    
    /**
     * 종목 검색 (키워드 + 시장 타입)
     */
    @Override
    public List<StockVO> searchStocks(String keyword, String marketType) throws Exception {
        // 키워드와 시장 타입 모두 있는 경우
        if (keyword != null && !keyword.isEmpty() && 
            marketType != null && !marketType.isEmpty() && !marketType.equals("ALL")) {
            return stockDAO.searchStocksWithMarket(keyword, marketType);
        }
        // 키워드만 있는 경우
        else if (keyword != null && !keyword.isEmpty()) {
            return stockDAO.searchStocks(keyword);
        }
        // 시장 타입만 있는 경우
        else if (marketType != null && !marketType.isEmpty() && !marketType.equals("ALL")) {
            return stockDAO.selectByMarket(marketType);
        }
        // 둘 다 없는 경우 - 전체 조회
        else {
            return stockDAO.selectAll();
        }
    }
    
    /**
     * 자동완성 (상위 10개)
     */
    @Override
    public List<StockVO> getAutocomplete(String keyword) throws Exception {
        return stockDAO.autocomplete(keyword, 10);
    }
    
    /**
     * 시장별 종목 조회
     */
    @Override
    public List<StockVO> getStocksByMarket(String market) throws Exception {
        return stockDAO.selectByMarket(market);
    }
    
    /**
     * 거래량 상위 종목
     */
    @Override
    public List<StockVO> getTopVolume(int limit) throws Exception {
        return stockDAO.selectTopVolume(limit);
    }
    
    /**
     * 상승률 상위 종목
     */
    @Override
    public List<StockVO> getTopGainers(int limit) throws Exception {
        return stockDAO.selectTopGainers(limit);
    }
}

    
