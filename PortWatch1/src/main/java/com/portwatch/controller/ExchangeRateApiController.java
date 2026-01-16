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
 * ExchangeRateApiController - 환율 API
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * API 목록:
 * - GET /api/exchange/rate: 현재 환율 조회
 * - GET /api/exchange/convert: 금액 변환 (USD ↔ KRW)
 * 
 * @author PortWatch
 * @version 1.0 - 2026.01.16
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
     * 
     * 사용 예시:
     * fetch('/api/exchange/rate')
     *   .then(response => response.json())
     *   .then(data => {
     *       console.log('환율:', data.rate);
     *   });
     * 
     * @return 환율 정보
     */
    @GetMapping("/rate")
    public ResponseEntity<Map<String, Object>> getCurrentRate() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💱 [API] 환율 조회");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            BigDecimal rate = exchangeRateService.getCurrentExchangeRate();
            
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
     * 
     * 파라미터:
     * - amount: 변환할 금액
     * - from: 원본 통화 (USD 또는 KRW)
     * - to: 대상 통화 (KRW 또는 USD)
     * 
     * 사용 예시:
     * // USD $100 → KRW
     * fetch('/api/exchange/convert?amount=100&from=USD&to=KRW')
     *   .then(response => response.json())
     *   .then(data => {
     *       console.log('변환 금액:', data.convertedAmount);
     *   });
     * 
     * @param amount 변환할 금액
     * @param from 원본 통화
     * @param to 대상 통화
     * @return 변환 결과
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
            BigDecimal rate = exchangeRateService.getCurrentExchangeRate();
            
            // USD → KRW
            if ("USD".equalsIgnoreCase(from) && "KRW".equalsIgnoreCase(to)) {
                convertedAmount = exchangeRateService.convertUsdToKrw(amount);
                
            // KRW → USD
            } else if ("KRW".equalsIgnoreCase(from) && "USD".equalsIgnoreCase(to)) {
                convertedAmount = exchangeRateService.convertKrwToUsd(amount);
                
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
