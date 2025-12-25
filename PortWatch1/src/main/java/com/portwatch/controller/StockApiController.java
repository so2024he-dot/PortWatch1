package com.portwatch.controller;

import com.portwatch.domain.StockVO;
import com.portwatch.service.ExchangeRateService;
import com.portwatch.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 종목 정보 API Controller
 * 
 * Spring 5.0.7 RELEASE + MySQL 8.0 완전 호환
 * 
 * @author PortWatch
 * @version 2.0
 */
@RestController
@RequestMapping("/api/stock")
public class StockApiController {
    
    @Autowired
    private StockService stockService;
    
    @Autowired
    private ExchangeRateService exchangeRateService;
    
    /**
     * 종목 정보 조회 API
     * 
     * GET /api/stock/info/{stockCode}
     * 
     * Spring 5.0.7 완전 호환 버전
     * - produces = MediaType.APPLICATION_JSON_UTF8_VALUE (Spring 5.0.7 권장)
     * - 명시적 타입 변환
     * - NULL 안전 처리
     */
    @GetMapping(value = "/info/{stockCode}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, Object>> getStockInfo(@PathVariable("stockCode") String stockCode) {
        Map<String, Object> response = new HashMap<String, Object>();
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 종목 정보 API 호출: " + stockCode);
        
        try {
            // 종목 정보 조회
            StockVO stock = stockService.getStockByCode(stockCode);
            
            if (stock == null) {
                System.err.println("❌ 종목을 찾을 수 없음: " + stockCode);
                response.put("success", Boolean.FALSE);
                response.put("message", "종목을 찾을 수 없습니다: " + stockCode);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            System.out.println("✅ 종목 조회 성공: " + stock.getStockName());
            
            // 기본 정보
            response.put("success", Boolean.TRUE);
            response.put("stockCode", stock.getStockCode());
            response.put("stockName", stock.getStockName());
            response.put("marketType", stock.getMarketType());
            
            // industry는 NULL일 수 있음
            if (stock.getIndustry() != null) {
                response.put("industry", stock.getIndustry());
            } else {
                response.put("industry", "");
            }
            
            // 현재가 - NULL 체크 필수!
            if (stock.getCurrentPrice() != null) {
                // BigDecimal을 Double로 변환 (JSON 직렬화 안전)
                double currentPriceValue = stock.getCurrentPrice().doubleValue();
                response.put("currentPrice", Double.valueOf(currentPriceValue));
                System.out.println("💰 현재가: " + currentPriceValue);
            } else {
                response.put("currentPrice", null);
                System.out.println("⚠️ 현재가 정보 없음");
            }
            
            // 미국 주식 여부 확인
            String marketType = stock.getMarketType();
            boolean isUSStock = false;
            
            if (marketType != null) {
                isUSStock = marketType.equals("NASDAQ") || 
                           marketType.equals("NYSE") || 
                           marketType.equals("AMEX");
            }
            
            response.put("isUSStock", Boolean.valueOf(isUSStock));
            System.out.println("🌎 미국 주식 여부: " + isUSStock);
            
            // 분할 매입 가능 여부 (미국 주식만)
            response.put("fractionalTrading", Boolean.valueOf(isUSStock));
            
            // 미국 주식이면 환율 정보 추가
            if (isUSStock) {
                try {
                    BigDecimal exchangeRate = exchangeRateService.getUSDToKRW();
                    double exchangeRateValue = exchangeRate.doubleValue();
                    response.put("exchangeRate", Double.valueOf(exchangeRateValue));
                    System.out.println("💱 환율: " + exchangeRateValue);
                    
                    // 현재가가 있으면 한화로 변환
                    if (stock.getCurrentPrice() != null) {
                        BigDecimal krwPrice = exchangeRateService.convertUSDToKRW(stock.getCurrentPrice());
                        double krwPriceValue = krwPrice.doubleValue();
                        response.put("currentPriceKRW", Double.valueOf(krwPriceValue));
                        System.out.println("💴 한화 환산: " + krwPriceValue);
                    }
                } catch (Exception e) {
                    // 환율 조회 실패해도 계속 진행
                    System.err.println("⚠️ 환율 조회 실패: " + e.getMessage());
                    response.put("exchangeRate", Double.valueOf(1310.0));
                }
            }
            
            // 추천 매입 단위
            if (isUSStock) {
                response.put("minQuantity", Double.valueOf(0.01));
                response.put("stepQuantity", Double.valueOf(0.01));
            } else {
                response.put("minQuantity", Integer.valueOf(1));
                response.put("stepQuantity", Integer.valueOf(1));
            }
            
            // 가격 변동 정보
            if (stock.getPriceChange() != null) {
                response.put("priceChange", Double.valueOf(stock.getPriceChange().doubleValue()));
            }
            
            if (stock.getPriceChangeRate() != null) {
                response.put("priceChangeRate", Double.valueOf(stock.getPriceChangeRate().doubleValue()));
            }
            
            System.out.println("✅ API 응답 생성 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ 종목 정보 조회 중 오류 발생");
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            response.put("success", Boolean.FALSE);
            response.put("message", "종목 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 현재가만 간단히 조회
     * 
     * GET /api/stock/current-price/{stockCode}
     */
    @GetMapping(value = "/current-price/{stockCode}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, Object>> getCurrentPrice(@PathVariable("stockCode") String stockCode) {
        Map<String, Object> response = new HashMap<String, Object>();
        
        try {
            StockVO stock = stockService.getStockByCode(stockCode);
            
            if (stock == null || stock.getCurrentPrice() == null) {
                response.put("success", Boolean.FALSE);
                response.put("message", "현재가를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", Boolean.TRUE);
            response.put("currentPrice", Double.valueOf(stock.getCurrentPrice().doubleValue()));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", Boolean.FALSE);
            response.put("message", "현재가 조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
