    package com.portwatch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.portwatch.domain.StockVO;
import com.portwatch.service.StockService;

@RestController
@RequestMapping("/api/stock")
public class StockApiController {
    
    @Autowired
    private StockService stockService;
    
    @GetMapping("/search")
    public Map<String, Object> searchStocks(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String marketType) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<StockVO> stocks = stockService.searchStocks(keyword, marketType);
            result.put("success", true);
            result.put("data", stocks);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/autocomplete")
    public Map<String, Object> autocomplete(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<StockVO> stocks = stockService.getAutocomplete(keyword);
            result.put("success", true);
            result.put("data", stocks);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/detail/{stockCode}")
    public Map<String, Object> getStockDetail(@PathVariable String stockCode) {
        Map<String, Object> result = new HashMap<>();
        try {
            StockVO stock = stockService.getStockByCode(stockCode);
            result.put("success", true);
            result.put("data", stock);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 종목 ID로 현재가 정보 조회 (포트폴리오 추가 시 사용)
     */
    @GetMapping("/info/{stockId}")
    public Map<String, Object> getStockInfo(@PathVariable Integer stockId) {
        Map<String, Object> result = new HashMap<>();
        try {
            StockVO stock = stockService.getStockById(stockId);
            
            if (stock != null) {
                result.put("success", true);
                result.put("stockId", stock.getStockId());
                result.put("stockCode", stock.getStockCode());
                result.put("stockName", stock.getStockName());
                result.put("currentPrice", stock.getCurrentPrice());
                result.put("marketType", stock.getMarketType());
            } else {
                result.put("success", false);
                result.put("message", "종목 정보를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "종목 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}

    
