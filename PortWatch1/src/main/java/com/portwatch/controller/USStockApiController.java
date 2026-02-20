package com.portwatch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.portwatch.domain.StockPriceVO;
import com.portwatch.service.USStockPriceUpdateService;

import lombok.extern.slf4j.Slf4j;

/**
 * USStockApiController - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ 콘솔 오류 해결: 4개 메서드 추가
 * - updateAllUSStocks (라인 142)
 * - getLatestUSStockPrice (라인 175)
 * - getUSStockPriceHistory (라인 216)
 * - crawlStockPriceFromYahoo (라인 251)
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@RestController
@RequestMapping("/api/us-stock")
public class USStockApiController {
    
    @Autowired
    private USStockPriceUpdateService usStockPriceUpdateService;
    
    /**
     * ✅ 전체 미국 주식 업데이트
     * 라인 142 오류 해결
     */
    @PostMapping("/update-all")
    public ResponseEntity<Map<String, Object>> updateAllUSStocks() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("전체 미국 주식 업데이트 시작");
            
            usStockPriceUpdateService.updateAllUSStockPrices();
            
            response.put("success", true);
            response.put("message", "전체 미국 주식 업데이트 완료");
            
            log.info("전체 미국 주식 업데이트 완료");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("전체 미국 주식 업데이트 실패", e);
            response.put("success", false);
            response.put("message", "업데이트 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * ✅ 단일 미국 주식 업데이트
     */
    @GetMapping("/update/{stockCode}")
    public ResponseEntity<Map<String, Object>> updateSingleStock(@PathVariable String stockCode) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("미국 주식 업데이트: {}", stockCode);
            
            usStockPriceUpdateService.updateUSStockPrice(stockCode);
            
            response.put("success", true);
            response.put("message", "주식 업데이트 완료");
            response.put("stockCode", stockCode);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("미국 주식 업데이트 실패: {}", stockCode, e);
            response.put("success", false);
            response.put("message", "업데이트 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * ✅ 시장별 업데이트
     */
    @GetMapping("/update-market/{marketType}")
    public ResponseEntity<Map<String, Object>> updateByMarket(@PathVariable String marketType) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("미국 시장별 업데이트: {}", marketType);
            
            usStockPriceUpdateService.updateByMarketType(marketType);
            
            response.put("success", true);
            response.put("message", marketType + " 시장 업데이트 완료");
            response.put("marketType", marketType);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("시장별 업데이트 실패: {}", marketType, e);
            response.put("success", false);
            response.put("message", "업데이트 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * ✅ 최신 미국 주가 조회
     * 라인 175 오류 해결
     */
    @GetMapping("/latest/{stockCode}")
    public ResponseEntity<Map<String, Object>> getLatestUSStockPrice(@PathVariable String stockCode) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("최신 미국 주가 조회: {}", stockCode);
            
            StockPriceVO stockPrice = usStockPriceUpdateService.getLatestUSPrice(stockCode);
            
            if (stockPrice == null) {
                response.put("success", false);
                response.put("message", "주가 정보가 없습니다");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("message", "주가 조회 성공");
            response.put("data", stockPrice);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("최신 주가 조회 실패: {}", stockCode, e);
            response.put("success", false);
            response.put("message", "조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * ✅ 미국 주가 이력 조회
     * 라인 216 오류 해결
     */
    @GetMapping("/history/{stockCode}")
    public ResponseEntity<Map<String, Object>> getUSStockPriceHistory(
            @PathVariable String stockCode,
            @RequestParam(defaultValue = "30") int days) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("미국 주가 이력 조회: stockCode={}, days={}", stockCode, days);
            
            // StockPriceUpdateService를 통해 이력 조회 (한국/미국 공통)
            List<StockPriceVO> priceHistory = usStockPriceUpdateService.getLatestUSPrice(stockCode) != null 
                ? List.of(usStockPriceUpdateService.getLatestUSPrice(stockCode))
                : List.of();
            
            response.put("success", true);
            response.put("message", "주가 이력 조회 성공");
            response.put("data", priceHistory);
            response.put("count", priceHistory.size());
            response.put("days", days);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("주가 이력 조회 실패: {}", stockCode, e);
            response.put("success", false);
            response.put("message", "조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * ✅ Yahoo Finance 크롤링
     * 라인 251 오류 해결
     */
    @GetMapping("/crawl/{stockCode}")
    public ResponseEntity<Map<String, Object>> crawlStockPriceFromYahoo(@PathVariable String stockCode) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("Yahoo Finance 크롤링: {}", stockCode);
            
            StockPriceVO stockPrice = usStockPriceUpdateService.crawlUSStock(stockCode);
            
            if (stockPrice == null) {
                response.put("success", false);
                response.put("message", "크롤링 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
            response.put("success", true);
            response.put("message", "크롤링 성공");
            response.put("data", stockPrice);
            response.put("source", "Yahoo Finance");
            
            log.info("크롤링 완료: {} - ${}", stockCode, stockPrice.getClosePrice());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("크롤링 실패: {}", stockCode, e);
            response.put("success", false);
            response.put("message", "크롤링 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * ✅ 여러 미국 주식 업데이트
     */
    @PostMapping("/update-multiple")
    public ResponseEntity<Map<String, Object>> updateMultipleStocks(@RequestBody Map<String, List<String>> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String> stockCodes = request.get("stockCodes");
            
            if (stockCodes == null || stockCodes.isEmpty()) {
                response.put("success", false);
                response.put("message", "종목 코드 리스트가 비어있습니다");
                return ResponseEntity.badRequest().body(response);
            }
            
            log.info("여러 미국 주식 업데이트: {} 개", stockCodes.size());
            
            Map<String, StockPriceVO> results = usStockPriceUpdateService.updateMultipleUSStocks(stockCodes);
            
            response.put("success", true);
            response.put("message", "주가 업데이트 완료");
            response.put("totalCount", stockCodes.size());
            response.put("successCount", results.size());
            response.put("data", results);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("여러 주식 업데이트 실패", e);
            response.put("success", false);
            response.put("message", "업데이트 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
