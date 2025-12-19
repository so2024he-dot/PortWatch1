package com.portwatch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.portwatch.domain.StockVO;
import com.portwatch.service.StockService;

/**
 * ✅ 수정사항: MarketApiController.java
 * 
 * 1. topVolume 메서드 (45-49번) - getStocksOrderByVolume 호출
 * 2. topGainers 메서드 (52-57번) - getStocksOrderByChangeRate 호출
 * 
 * 원인: StockService 인터페이스에 정의된 메서드명과 일치하지 않았음
 */
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
    
    /**
     * ✅ 수정: 거래량 상위 종목 조회
     * - getStocksOrderByVolume 메서드 호출
     */
    @GetMapping("/top-volume")
    public ResponseEntity<List<StockVO>> topVolume(
            @RequestParam(defaultValue = "10") int limit) throws Exception {
        
        List<StockVO> stocks = stockService.getStocksOrderByVolume(limit);
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }

    /**
     * ✅ 수정: 상승률 상위 종목 조회
     * - getStocksOrderByChangeRate 메서드 호출
     */
    @GetMapping("/top-gainers")
    public ResponseEntity<List<StockVO>> topGainers(
            @RequestParam(defaultValue = "10") int limit) throws Exception {
        
        List<StockVO> stocks = stockService.getStocksOrderByChangeRate(limit);
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }

}
