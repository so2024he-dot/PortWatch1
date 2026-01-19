package com.portwatch.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portwatch.domain.StockVO;
import com.portwatch.service.ExchangeRateService;
import com.portwatch.service.StockService;

import lombok.extern.log4j.Log4j;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * StockApiController - 주식 정보 API (환율 기능 추가!)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * @author PortWatch
 * @version 2.0 - 2026.01.16
 */
@RestController
@RequestMapping("/api/stock")
@Log4j
public class StockApiController {
    
    @Autowired
    private StockService stockService;
    
    @Autowired
    private ExchangeRateService exchangeRateService;
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 주식 정보 조회
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/stock/info/{stockCode}
     */
    @GetMapping(value = "/info/{stockCode}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getStockInfo(@PathVariable String stockCode) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📊 [API] 주식 정보 조회");
        log.info("  - stockCode: " + stockCode);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            StockVO stock = stockService.getStockByCode(stockCode);
            
            if (stock == null) {
                result.put("success", false);
                result.put("message", "주식을 찾을 수 없습니다.");
                log.warn("⚠️ 주식을 찾을 수 없음: " + stockCode);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            
            result.put("success", true);
            result.put("stock", stock);
            result.put("timestamp", System.currentTimeMillis());
            
            log.info("✅ 주식 정보 조회 완료: " + stock.getStockName());
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("❌ 주식 정보 조회 실패", e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "주식 정보 조회에 실패했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 현재가 조회
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/stock/current-price/{stockCode}
     */
    @GetMapping(value = "/current-price/{stockCode}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getCurrentPrice(@PathVariable String stockCode) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("💰 [API] 현재가 조회");
        log.info("  - stockCode: " + stockCode);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            StockVO stock = stockService.getStockByCode(stockCode);
            
            if (stock == null) {
                result.put("success", false);
                result.put("message", "주식을 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            
            result.put("success", true);
            result.put("stockCode", stock.getStockCode());
            result.put("stockName", stock.getStockName());
            result.put("currentPrice", stock.getCurrentPrice());
            result.put("country", stock.getCountry());
            result.put("timestamp", System.currentTimeMillis());
            
            log.info("✅ 현재가: " + stock.getCurrentPrice());
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("❌ 현재가 조회 실패", e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "현재가 조회에 실패했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ USD → KRW 환율 조회 (신규 추가!)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/stock/exchange-rate
     */
    @GetMapping(value = "/exchange-rate", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getUSDToKRW() {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("💱 [API] 환율 조회 (Stock API)");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            BigDecimal rate = exchangeRateService.getUSDToKRW();
            
            result.put("success", true);
            result.put("rate", rate);
            result.put("currency", "USD/KRW");
            result.put("timestamp", System.currentTimeMillis());
            
            log.info("✅ 환율: " + rate);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("❌ 환율 조회 실패", e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "환율 조회에 실패했습니다");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ USD → KRW 변환 (신규 추가!)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/stock/convert-usd-krw/{usdAmount}
     */
    @GetMapping(value = "/convert-usd-krw/{usdAmount}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, Object>> convertUSDToKRW(@PathVariable BigDecimal usdAmount) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("💱 [API] USD → KRW 변환 (Stock API)");
        log.info("  - USD: " + usdAmount);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            BigDecimal krwAmount = exchangeRateService.convertUSDToKRW(usdAmount);
            BigDecimal rate = exchangeRateService.getUSDToKRW();
            
            result.put("success", true);
            result.put("usdAmount", usdAmount);
            result.put("krwAmount", krwAmount);
            result.put("rate", rate);
            result.put("timestamp", System.currentTimeMillis());
            
            log.info("✅ 변환 완료: " + krwAmount + "원");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("❌ 변환 실패", e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "변환에 실패했습니다");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
