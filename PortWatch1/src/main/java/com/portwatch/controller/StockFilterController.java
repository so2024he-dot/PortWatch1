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
 * ✅ 종목 필터링 API 컨트롤러
 * 
 * 기능:
 * 1. 전체 종목 조회
 * 2. 나라별 필터링 (한국/미국)
 * 3. 시장별 필터링 (KOSPI/KOSDAQ/NASDAQ/NYSE)
 * 4. 업종별 필터링
 * 5. 검색 기능
 * 
 * @author PortWatch
 * @version 1.0
 */
@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "*")
public class StockFilterController {
    
    @Autowired
    private StockService stockService;
    
    /**
     * 1. 전체 종목 조회
     * GET /api/stocks
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStocks() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> stocks = stockService.getAllStocks();
            
            response.put("success", true);
            response.put("count", stocks.size());
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "종목 조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 2. 나라별 필터링
     * GET /api/stocks/country/{country}
     * 
     * @param country KR 또는 US
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<Map<String, Object>> getStocksByCountry(
            @PathVariable String country) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("📊 나라별 종목 조회: " + country);
            
            List<StockVO> stocks = stockService.getStocksByCountry(country);
            
            response.put("success", true);
            response.put("country", country);
            response.put("count", stocks.size());
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "나라별 종목 조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 3. 시장별 필터링
     * GET /api/stocks/market/{marketType}
     * 
     * @param marketType KOSPI, KOSDAQ, NASDAQ, NYSE, AMEX
     */
    @GetMapping("/market/{marketType}")
    public ResponseEntity<Map<String, Object>> getStocksByMarket(
            @PathVariable String marketType) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("📊 시장별 종목 조회: " + marketType);
            
            List<StockVO> stocks = stockService.getStocksByMarketType(marketType);
            
            response.put("success", true);
            response.put("marketType", marketType);
            response.put("count", stocks.size());
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "시장별 종목 조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 4. 업종별 필터링
     * GET /api/stocks/industry/{industry}
     */
    @GetMapping("/industry/{industry}")
    public ResponseEntity<Map<String, Object>> getStocksByIndustry(
            @PathVariable String industry) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("📊 업종별 종목 조회: " + industry);
            
            List<StockVO> stocks = stockService.getStocksByIndustry(industry);
            
            response.put("success", true);
            response.put("industry", industry);
            response.put("count", stocks.size());
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "업종별 종목 조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 5. 종목 검색 (자동완성 API)
     * GET /api/stocks/search?keyword={keyword}
     * ✅ [수정] required=false → keyword 없이 호출 시 400 방지
     *    keyword 비어있으면 빈 배열 반환 (자동완성 초기화)
     *    최대 20건으로 제한 (자동완성 드롭다운 성능 최적화)
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchStocks(
            @RequestParam(required = false, defaultValue = "") String keyword) {

        Map<String, Object> response = new HashMap<>();

        keyword = keyword.trim();

        // 빈 키워드 → 빈 결과 즉시 반환
        if (keyword.isEmpty()) {
            response.put("success", true);
            response.put("keyword", "");
            response.put("count", 0);
            response.put("stocks", java.util.Collections.emptyList());
            return ResponseEntity.ok(response);
        }

        try {
            System.out.println("🔍 종목 검색(자동완성): " + keyword);

            List<StockVO> stocks = stockService.searchStocks(keyword);
            if (stocks == null) stocks = java.util.Collections.emptyList();

            // 자동완성은 최대 20건 (드롭다운 성능)
            if (stocks.size() > 20) {
                stocks = stocks.subList(0, 20);
            }

            response.put("success", true);
            response.put("keyword", keyword);
            response.put("count", stocks.size());
            response.put("stocks", stocks);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "종목 검색 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 6. 전체 업종 목록 조회
     * GET /api/stocks/industries
     */
    @GetMapping("/industries")
    public ResponseEntity<Map<String, Object>> getAllIndustries() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String> industries = stockService.getAllIndustries();
            
            response.put("success", true);
            response.put("count", industries.size());
            response.put("industries", industries);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "업종 목록 조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 7. 거래량 상위 종목
     * GET /api/stocks/top/volume?limit=10
     */
    @GetMapping("/top/volume")
    public ResponseEntity<Map<String, Object>> getTopVolumeStocks(
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> stocks = stockService.getStocksOrderByVolume(limit);
            
            response.put("success", true);
            response.put("count", stocks.size());
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "거래량 상위 종목 조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 8. 상승률 상위 종목
     * GET /api/stocks/top/gainers?limit=10
     */
    @GetMapping("/top/gainers")
    public ResponseEntity<Map<String, Object>> getTopGainers(
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> stocks = stockService.getStocksOrderByChangeRate(limit);
            
            response.put("success", true);
            response.put("count", stocks.size());
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "상승률 상위 종목 조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 9. 복합 필터링
     * GET /api/stocks/filter?country=KR&market=KOSPI&industry=반도체
     */
    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterStocks(
            @RequestParam(name = "country", required = false) String country,
            @RequestParam(name = "market", required = false) String market,
            @RequestParam(name = "industry", required = false) String industry) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("📊 복합 필터링:");
            System.out.println("  - 나라: " + country);
            System.out.println("  - 시장: " + market);
            System.out.println("  - 업종: " + industry);
            
            List<StockVO> stocks = stockService.getAllStocks();
            
            // 나라별 필터링
            if (country != null && !country.isEmpty()) {
                stocks = filterByCountry(stocks, country);
            }
            
            // 시장별 필터링
            if (market != null && !market.isEmpty()) {
                stocks = filterByMarket(stocks, market);
            }
            
            // 업종별 필터링
            if (industry != null && !industry.isEmpty()) {
                stocks = filterByIndustry(stocks, industry);
            }
            
            response.put("success", true);
            response.put("filters", Map.of(
                "country", country != null ? country : "전체",
                "market", market != null ? market : "전체",
                "industry", industry != null ? industry : "전체"
            ));
            response.put("count", stocks.size());
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "필터링 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ========================================
    // 헬퍼 메서드
    // ========================================
    
    private List<StockVO> filterByCountry(List<StockVO> stocks, String country) {
        return stocks.stream()
            .filter(s -> country.equalsIgnoreCase(s.getCountry()))
            .collect(java.util.stream.Collectors.toList());
    }
    
    private List<StockVO> filterByMarket(List<StockVO> stocks, String market) {
        return stocks.stream()
            .filter(s -> market.equalsIgnoreCase(s.getMarketType()))
            .collect(java.util.stream.Collectors.toList());
    }
    
    private List<StockVO> filterByIndustry(List<StockVO> stocks, String industry) {
        return stocks.stream()
            .filter(s -> industry.equalsIgnoreCase(s.getIndustry()))
            .collect(java.util.stream.Collectors.toList());
    }
}
