package com.portwatch.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portwatch.service.ExchangeRateService;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * ExchangeRateApiController - 환율 API (수정 완료!)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * @author PortWatch
 * @version 2.0 - 2026.01.16
 */
@RestController
@RequestMapping("/api/exchange")
public class ExchangeRateApiController {
    
    @Autowired
    private ExchangeRateService exchangeRateService;
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 현재 환율 조회
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/exchange/rate
     */
    @GetMapping("/rate")
    public ResponseEntity<Map<String, Object>> getCurrentRate() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💱 [API] 환율 조회");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            BigDecimal rate = exchangeRateService.getUSDToKRW();
            
            result.put("success", true);
            result.put("rate", rate);
            result.put("currency", "USD/KRW");
            result.put("timestamp", System.currentTimeMillis());
            
            System.out.println("✅ 환율: " + rate + " KRW/USD");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("❌ 환율 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "환율 조회에 실패했습니다");
            
            return ResponseEntity.ok(result);
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 금액 변환
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/exchange/convert?amount=100&from=USD&to=KRW
     */
    @GetMapping("/convert")
    public ResponseEntity<Map<String, Object>> convertCurrency(
            @RequestParam BigDecimal amount,
            @RequestParam String from,
            @RequestParam String to) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💱 [API] 환율 변환");
        System.out.println("  - 금액: " + amount);
        System.out.println("  - 원본: " + from);
        System.out.println("  - 대상: " + to);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            BigDecimal convertedAmount;
            BigDecimal rate = exchangeRateService.getUSDToKRW();
            
            // USD → KRW
            if ("USD".equalsIgnoreCase(from) && "KRW".equalsIgnoreCase(to)) {
                convertedAmount = exchangeRateService.convertUSDToKRW(amount);
                
            // KRW → USD
            } else if ("KRW".equalsIgnoreCase(from) && "USD".equalsIgnoreCase(to)) {
                convertedAmount = exchangeRateService.convertKRWToUSD(amount);
                
            } else {
                result.put("success", false);
                result.put("message", "지원하지 않는 통화 쌍입니다");
                return ResponseEntity.badRequest().body(result);
            }
            
            result.put("success", true);
            result.put("originalAmount", amount);
            result.put("convertedAmount", convertedAmount);
            result.put("from", from);
            result.put("to", to);
            result.put("rate", rate);
            result.put("timestamp", System.currentTimeMillis());
            
            System.out.println("✅ 변환 완료: " + convertedAmount);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("❌ 변환 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "변환에 실패했습니다: " + e.getMessage());
            
            return ResponseEntity.ok(result);
        }
    }
}
