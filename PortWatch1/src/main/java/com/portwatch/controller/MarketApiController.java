package com.portwatch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.portwatch.domain.StockVO;
import com.portwatch.service.StockService;

@RestController
@RequestMapping("/api/market")
public class MarketApiController {
    
    @Autowired
    private StockService stockService;
    
    @GetMapping("/indices")
    public Map<String, Object> getMarketIndices() {
        Map<String, Object> result = new HashMap<>();
        try {
            // TODO: 실제 시장 지수 조회 로직
            result.put("success", true);
            result.put("kospi", new HashMap<String, Object>() {{
                put("value", "2500.00");
                put("change", "+15.50");
                put("changeRate", "+0.62%");
            }});
            result.put("kosdaq", new HashMap<String, Object>() {{
                put("value", "850.00");
                put("change", "-5.20");
                put("changeRate", "-0.61%");
            }});
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/top-volume")
    public Map<String, Object> getTopVolume(@RequestParam(defaultValue = "5") int limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<StockVO> stocks = stockService.getTopVolume(limit);
            result.put("success", true);
            result.put("data", stocks);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/top-gainers")
    public Map<String, Object> getTopGainers(@RequestParam(defaultValue = "5") int limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<StockVO> stocks = stockService.getTopGainers(limit);
            result.put("success", true);
            result.put("data", stocks);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
