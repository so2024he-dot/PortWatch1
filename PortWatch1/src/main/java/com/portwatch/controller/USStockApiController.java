package com.portwatch.controller;

import com.portwatch.domain.StockPriceVO;
import com.portwatch.service.USStockPriceUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 미국 주식 업데이트 REST API 컨트롤러 (Spring MVC 5.0.7)
 * 
 * @Controller + @ResponseBody 사용 (Spring MVC 호환)
 * 
 * 엔드포인트:
 * - GET  /api/stock/us/health : API 상태 체크
 * - POST /api/stock/us/{symbol}/update : 단일 종목 업데이트
 * - POST /api/stock/us/update/batch : 여러 종목 일괄 업데이트
 * - POST /api/stock/us/update/all : 전체 미국 주식 업데이트
 * - GET  /api/stock/us/{symbol}/latest : 최신 주가 조회
 * - GET  /api/stock/us/{symbol}/history : 주가 히스토리 조회
 * - GET  /api/stock/us/{symbol}/crawl : 즉시 크롤링 (DB 저장 없음)
 */
@Controller
@RequestMapping("/api/stock/us")
public class USStockApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(USStockApiController.class);
    
    @Autowired
    private USStockPriceUpdateService usStockService;
    
    /**
     * API 상태 체크
     * 
     * GET /api/stock/us/health
     */
    @RequestMapping(value = "/health", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<String, Object>();
        
        response.put("status", "healthy");
        response.put("service", "US Stock Price Update Service");
        response.put("message", "Alpha Vantage API 연동 정상");
        response.put("free_plan_limit", "25 requests/day, 5 requests/minute");
        response.put("spring_version", "Spring MVC 5.0.7");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 단일 미국 주식 업데이트
     * 
     * POST /api/stock/us/AAPL/update
     */
    @RequestMapping(value = "/{symbol}/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateSingleStock(@PathVariable("symbol") String symbol) {
        logger.info("🔄 [API] 미국 주식 업데이트 요청: {}", symbol);
        
        Map<String, Object> response = new HashMap<String, Object>();
        
        try {
            StockPriceVO stockPrice = usStockService.updateSingleUSStock(symbol);
            
            response.put("success", true);
            response.put("message", "종목 " + symbol + " 주가 업데이트 완료");
            response.put("data", stockPrice);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ [API] 업데이트 실패: {} - {}", symbol, e.getMessage());
            
            response.put("success", false);
            response.put("message", "업데이트 실패: " + e.getMessage());
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 여러 미국 주식 일괄 업데이트
     * 
     * POST /api/stock/us/update/batch
     * Body: ["AAPL", "MSFT", "GOOGL"]
     */
    @RequestMapping(value = "/update/batch", method = RequestMethod.POST, 
                    consumes = MediaType.APPLICATION_JSON_VALUE, 
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateMultipleStocks(@RequestBody List<String> symbols) {
        logger.info("🔄 [API] 일괄 업데이트 요청: {}개 종목", symbols.size());
        
        Map<String, Object> response = new HashMap<String, Object>();
        
        try {
            Map<String, StockPriceVO> results = usStockService.updateMultipleUSStocks(symbols);
            
            response.put("success", true);
            response.put("message", results.size() + "/" + symbols.size() + " 종목 업데이트 완료");
            response.put("total", symbols.size());
            response.put("success_count", results.size());
            response.put("data", results);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ [API] 일괄 업데이트 실패: {}", e.getMessage());
            
            response.put("success", false);
            response.put("message", "일괄 업데이트 실패: " + e.getMessage());
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 전체 미국 주식 업데이트 (무료 API는 하루 25개 제한)
     * 
     * POST /api/stock/us/update/all
     */
    @RequestMapping(value = "/update/all", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateAllUSStocks() {
        logger.info("🔄 [API] 전체 미국 주식 업데이트 요청");
        
        Map<String, Object> response = new HashMap<String, Object>();
        
        try {
            int successCount = usStockService.updateAllUSStocks();
            
            response.put("success", true);
            response.put("message", "전체 미국 주식 업데이트 완료");
            response.put("success_count", successCount);
            response.put("warning", "무료 API는 하루 25개 종목만 업데이트 가능합니다");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ [API] 전체 업데이트 실패: {}", e.getMessage());
            
            response.put("success", false);
            response.put("message", "전체 업데이트 실패: " + e.getMessage());
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 최신 주가 조회
     * 
     * GET /api/stock/us/AAPL/latest
     */
    @RequestMapping(value = "/{symbol}/latest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getLatestPrice(@PathVariable("symbol") String symbol) {
        logger.info("📊 [API] 최신 주가 조회: {}", symbol);
        
        Map<String, Object> response = new HashMap<String, Object>();
        
        try {
            StockPriceVO stockPrice = usStockService.getLatestUSStockPrice(symbol);
            
            if (stockPrice == null) {
                response.put("success", false);
                response.put("message", "주가 데이터가 없습니다");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("message", "최신 주가 조회 성공");
            response.put("data", stockPrice);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ [API] 조회 실패: {} - {}", symbol, e.getMessage());
            
            response.put("success", false);
            response.put("message", "조회 실패: " + e.getMessage());
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 주가 히스토리 조회
     * 
     * GET /api/stock/us/AAPL/history?days=30
     */
    @RequestMapping(value = "/{symbol}/history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPriceHistory(
            @PathVariable("symbol") String symbol,
            @RequestParam(value = "days", defaultValue = "30") int days) {
        
        logger.info("📊 [API] 주가 히스토리 조회: {} ({}일)", symbol, days);
        
        Map<String, Object> response = new HashMap<String, Object>();
        
        try {
            List<StockPriceVO> history = usStockService.getUSStockPriceHistory(symbol, days);
            
            response.put("success", true);
            response.put("message", "주가 히스토리 조회 성공");
            response.put("symbol", symbol);
            response.put("days", days);
            response.put("count", history.size());
            response.put("data", history);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ [API] 히스토리 조회 실패: {} - {}", symbol, e.getMessage());
            
            response.put("success", false);
            response.put("message", "히스토리 조회 실패: " + e.getMessage());
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 즉시 크롤링 (DB 저장 없이)
     * 
     * GET /api/stock/us/AAPL/crawl
     */
    @RequestMapping(value = "/{symbol}/crawl", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crawlStockPrice(@PathVariable("symbol") String symbol) {
        logger.info("🕷️ [API] 실시간 크롤링 요청: {}", symbol);
        
        Map<String, Object> response = new HashMap<String, Object>();
        
        try {
            Map<String, Object> crawledData = usStockService.crawlStockPriceFromYahoo(symbol);
            
            response.put("success", true);
            response.put("message", "실시간 데이터 크롤링 성공");
            response.put("data", crawledData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ [API] 크롤링 실패: {} - {}", symbol, e.getMessage());
            
            response.put("success", false);
            response.put("message", "크롤링 실패: " + e.getMessage());
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
