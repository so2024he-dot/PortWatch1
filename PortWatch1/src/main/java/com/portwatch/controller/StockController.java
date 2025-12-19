package com.portwatch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.portwatch.domain.StockVO;
import com.portwatch.service.StockService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 종목 컨트롤러
 * 
 * @author PortWatch
 * @version 7.0 - 종목 구분 기능 추가 (전체/시장별/나라별)
 */
@Controller
@RequestMapping("/stock")
public class StockController {
    
    @Autowired
    private StockService stockService;
    
    /**
     * 종목 목록 페이지
     */
    @GetMapping("/list")
    public String stockList(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String value,
            Model model) {
        
        try {
            List<StockVO> stockList;
            
            // ✅ 필터링 적용
            if ("market".equals(filter) && value != null) {
                // 시장별 필터 (KOSPI, KOSDAQ, NASDAQ, NYSE)
                stockList = stockService.getStocksByMarketType(value);
                model.addAttribute("filterType", "시장별");
                model.addAttribute("filterValue", value);
                
            } else if ("country".equals(filter) && value != null) {
                // 나라별 필터 (KR, US)
                stockList = stockService.getStocksByCountry(value);
                model.addAttribute("filterType", "국가별");
                model.addAttribute("filterValue", value.equals("KR") ? "한국" : "미국");
                
            } else if ("industry".equals(filter) && value != null) {
                // 업종별 필터
                stockList = stockService.getStocksByIndustry(value);
                model.addAttribute("filterType", "업종별");
                model.addAttribute("filterValue", value);
                
            } else {
                // 전체 종목
                stockList = stockService.getAllStocks();
                model.addAttribute("filterType", "전체");
            }
            
            model.addAttribute("stockList", stockList);
            model.addAttribute("totalCount", stockList.size());
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "종목 조회 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "stock/list";
    }
    
    /**
     * 종목 목록 조회 (AJAX) - 필터링 지원
     */
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStockListApi(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String value) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> stockList;
            
            if ("market".equals(filter) && value != null) {
                stockList = stockService.getStocksByMarketType(value);
            } else if ("country".equals(filter) && value != null) {
                stockList = stockService.getStocksByCountry(value);
            } else if ("industry".equals(filter) && value != null) {
                stockList = stockService.getStocksByIndustry(value);
            } else {
                stockList = stockService.getAllStocks();
            }
            
            response.put("success", true);
            response.put("stockList", stockList);
            response.put("totalCount", stockList.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 시장별 종목 수 조회 (AJAX)
     */
    @GetMapping("/api/market-summary")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMarketSummary() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Integer> summary = new HashMap<>();
            
            // 한국 시장
            summary.put("KOSPI", stockService.getStocksByMarketType("KOSPI").size());
            summary.put("KOSDAQ", stockService.getStocksByMarketType("KOSDAQ").size());
            
            // 미국 시장
            summary.put("NASDAQ", stockService.getStocksByMarketType("NASDAQ").size());
            summary.put("NYSE", stockService.getStocksByMarketType("NYSE").size());
            
            // 국가별
            summary.put("KR", stockService.getStocksByCountry("KR").size());
            summary.put("US", stockService.getStocksByCountry("US").size());
            
            response.put("success", true);
            response.put("summary", summary);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 업종 목록 조회 (AJAX)
     */
    @GetMapping("/api/industries")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getIndustries() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String> industries = stockService.getAllIndustries();
            
            response.put("success", true);
            response.put("industries", industries);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 종목 상세 정보 조회
     */
    @GetMapping("/detail/{stockCode}")
    public String stockDetail(@PathVariable String stockCode, Model model) {
        try {
            StockVO stock = stockService.getStockByCode(stockCode);
            
            if (stock == null) {
                model.addAttribute("errorMessage", "종목을 찾을 수 없습니다.");
                return "error/404";
            }
            
            model.addAttribute("stock", stock);
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "종목 조회 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "stock/detail";
    }
    
    /**
     * 종목 검색 (AJAX)
     */
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchStocks(
            @RequestParam String keyword) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> stockList = stockService.searchStocks(keyword);
            
            response.put("success", true);
            response.put("stockList", stockList);
            response.put("totalCount", stockList.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        }
    }
}
