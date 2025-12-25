package com.portwatch.controller;

import com.portwatch.domain.StockPriceVO;
import com.portwatch.service.StockPriceUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 주가 업데이트 REST API 컨트롤러
 * 실시간 주가 업데이트 및 조회 API 제공
 */
@RestController
@RequestMapping("/api/stock-price")
public class StockPriceUpdateApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(StockPriceUpdateApiController.class);
    
    @Autowired
    private StockPriceUpdateService stockPriceUpdateService;
    
    /**
     * 단일 종목 실시간 주가 업데이트
     * GET /api/stock-price/update/{stockCode}
     * 
     * 예시: GET /api/stock-price/update/005930
     */
    @GetMapping("/update/{stockCode}")
    public ResponseEntity<Map<String, Object>> updateSingleStock(@PathVariable String stockCode) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("📊 API 호출: 종목 {} 주가 업데이트 요청", stockCode);
            
            StockPriceVO stockPrice = stockPriceUpdateService.updateSingleStock(stockCode);
            
            response.put("success", true);
            response.put("message", "주가 업데이트 성공");
            response.put("data", stockPrice);
            
            logger.info("✅ 종목 {} 주가 업데이트 API 성공", stockCode);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ 종목 {} 주가 업데이트 API 실패: {}", stockCode, e.getMessage());
            
            response.put("success", false);
            response.put("message", "주가 업데이트 실패: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 여러 종목 실시간 주가 업데이트
     * POST /api/stock-price/update-multiple
     * 
     * Request Body:
     * {
     *   "stockCodes": ["005930", "000660", "035420"]
     * }
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
            
            logger.info("📊 API 호출: {} 개 종목 주가 업데이트 요청", stockCodes.size());
            
            Map<String, StockPriceVO> results = stockPriceUpdateService.updateMultipleStocks(stockCodes);
            
            response.put("success", true);
            response.put("message", "주가 업데이트 완료");
            response.put("totalCount", stockCodes.size());
            response.put("successCount", results.size());
            response.put("data", results);
            
            logger.info("✅ 여러 종목 주가 업데이트 API 성공: {}/{}", results.size(), stockCodes.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ 여러 종목 주가 업데이트 API 실패: {}", e.getMessage());
            
            response.put("success", false);
            response.put("message", "주가 업데이트 실패: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 전체 종목 실시간 주가 업데이트 (주의: 시간 소요)
     * POST /api/stock-price/update-all
     * 
     * 주의: 이 API는 모든 종목을 업데이트하므로 시간이 오래 걸립니다.
     *      프로덕션에서는 비동기 처리나 스케줄러 사용을 권장합니다.
     */
    @PostMapping("/update-all")
    public ResponseEntity<Map<String, Object>> updateAllStocks() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("📊 API 호출: 전체 종목 주가 업데이트 요청 (위험: 시간 소요)");
            
            int successCount = stockPriceUpdateService.updateAllStocks();
            
            response.put("success", true);
            response.put("message", "전체 종목 주가 업데이트 완료");
            response.put("successCount", successCount);
            
            logger.info("✅ 전체 종목 주가 업데이트 API 성공: {} 개 종목", successCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ 전체 종목 주가 업데이트 API 실패: {}", e.getMessage());
            
            response.put("success", false);
            response.put("message", "주가 업데이트 실패: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 특정 종목의 최신 주가 조회
     * GET /api/stock-price/latest/{stockCode}
     * 
     * 예시: GET /api/stock-price/latest/005930
     */
    @GetMapping("/latest/{stockCode}")
    public ResponseEntity<Map<String, Object>> getLatestStockPrice(@PathVariable String stockCode) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("📊 API 호출: 종목 {} 최신 주가 조회", stockCode);
            
            StockPriceVO stockPrice = stockPriceUpdateService.getLatestStockPrice(stockCode);
            
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
            logger.error("❌ 종목 {} 최신 주가 조회 실패: {}", stockCode, e.getMessage());
            
            response.put("success", false);
            response.put("message", "주가 조회 실패: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 특정 종목의 주가 히스토리 조회
     * GET /api/stock-price/history/{stockCode}?days=30
     * 
     * 예시: GET /api/stock-price/history/005930?days=30
     */
    @GetMapping("/history/{stockCode}")
    public ResponseEntity<Map<String, Object>> getStockPriceHistory(
            @PathVariable String stockCode,
            @RequestParam(defaultValue = "30") int days) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("📊 API 호출: 종목 {} 주가 히스토리 조회 (최근 {}일)", stockCode, days);
            
            List<StockPriceVO> priceHistory = stockPriceUpdateService.getStockPriceHistory(stockCode, days);
            
            response.put("success", true);
            response.put("message", "주가 히스토리 조회 성공");
            response.put("data", priceHistory);
            response.put("count", priceHistory.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ 종목 {} 주가 히스토리 조회 실패: {}", stockCode, e.getMessage());
            
            response.put("success", false);
            response.put("message", "주가 히스토리 조회 실패: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 네이버 금융에서 실시간 크롤링 (DB 저장 없이 조회만)
     * GET /api/stock-price/crawl/{stockCode}
     * 
     * 예시: GET /api/stock-price/crawl/005930
     */
    @GetMapping("/crawl/{stockCode}")
    public ResponseEntity<Map<String, Object>> crawlStockPrice(@PathVariable String stockCode) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("📊 API 호출: 종목 {} 실시간 크롤링 (저장 안 함)", stockCode);
            
            Map<String, Object> crawledData = stockPriceUpdateService.crawlStockPriceFromNaver(stockCode);
            
            response.put("success", true);
            response.put("message", "크롤링 성공");
            response.put("data", crawledData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ 종목 {} 크롤링 실패: {}", stockCode, e.getMessage());
            
            response.put("success", false);
            response.put("message", "크롤링 실패: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
