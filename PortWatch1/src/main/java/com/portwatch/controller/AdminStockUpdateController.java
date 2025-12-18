package com.portwatch.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portwatch.service.StockPriceUpdateService;
import com.portwatch.service.USStockPriceUpdateService;

/**
 * 관리자용 주가 업데이트 API Controller
 * 
 * 수동 크롤링 및 테스트용 엔드포인트 제공
 * 
 * @author PortWatch
 * @version 3.0 (Spring 5.0.7 + MySQL 8.0)
 */
@RestController
@RequestMapping("/api/admin")
public class AdminStockUpdateController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminStockUpdateController.class);
    
    @Autowired
    private StockPriceUpdateService stockPriceUpdateService;
    
    @Autowired
    private USStockPriceUpdateService usStockPriceUpdateService;
    
    /**
     * 전체 주식 업데이트 (한국 + 미국)
     * 
     * GET /api/admin/update-all
     * 
     * 경고: 시간이 오래 걸릴 수 있습니다 (10분 이상)
     */
    @GetMapping("/update-all")
    public ResponseEntity<Map<String, Object>> updateAll() {
        Map<String, Object> response = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🔄 전체 주식 현재가 업데이트 시작");
        
        try {
            long startTime = System.currentTimeMillis();
            
            // 한국 주식
            logger.info("🇰🇷 한국 주식 업데이트 시작...");
            stockPriceUpdateService.updateAllStockPrices();
            
            // 미국 주식
            logger.info("🇺🇸 미국 주식 업데이트 시작...");
            usStockPriceUpdateService.updateAllUSStockPrices();
            
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000; // 초
            
            response.put("success", true);
            response.put("message", "전체 주식 현재가 업데이트 완료");
            response.put("duration", duration + "초");
            
            logger.info("✅ 전체 업데이트 완료 ({}초 소요)", duration);
            
        } catch (Exception e) {
            logger.error("❌ 전체 업데이트 실패", e);
            response.put("success", false);
            response.put("message", "업데이트 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        } finally {
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 한국 주식만 업데이트
     * 
     * GET /api/admin/update-korean
     */
    @GetMapping("/update-korean")
    public ResponseEntity<Map<String, Object>> updateKorean() {
        Map<String, Object> response = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🇰🇷 한국 주식 현재가 업데이트 시작");
        
        try {
            long startTime = System.currentTimeMillis();
            
            stockPriceUpdateService.updateAllStockPrices();
            
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000;
            
            response.put("success", true);
            response.put("message", "한국 주식 현재가 업데이트 완료");
            response.put("duration", duration + "초");
            
            logger.info("✅ 한국 주식 업데이트 완료 ({}초 소요)", duration);
            
        } catch (Exception e) {
            logger.error("❌ 한국 주식 업데이트 실패", e);
            response.put("success", false);
            response.put("message", "업데이트 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        } finally {
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 미국 주식만 업데이트
     * 
     * GET /api/admin/update-us
     * 
     * 경고: API 제한으로 인해 매우 느릴 수 있습니다
     */
    @GetMapping("/update-us")
    public ResponseEntity<Map<String, Object>> updateUS() {
        Map<String, Object> response = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🇺🇸 미국 주식 현재가 업데이트 시작");
        
        try {
            long startTime = System.currentTimeMillis();
            
            usStockPriceUpdateService.updateAllUSStockPrices();
            
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000;
            
            response.put("success", true);
            response.put("message", "미국 주식 현재가 업데이트 완료");
            response.put("duration", duration + "초");
            
            logger.info("✅ 미국 주식 업데이트 완료 ({}초 소요)", duration);
            
        } catch (Exception e) {
            logger.error("❌ 미국 주식 업데이트 실패", e);
            response.put("success", false);
            response.put("message", "업데이트 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        } finally {
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 특정 종목만 업데이트
     * 
     * GET /api/admin/update-stock?stockCode=005930
     * 
     * @param stockCode 종목 코드 (예: 005930, AAPL)
     */
    @GetMapping("/update-stock")
    public ResponseEntity<Map<String, Object>> updateStock(
            @RequestParam String stockCode) {
        
        Map<String, Object> response = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🔄 종목 현재가 업데이트: {}", stockCode);
        
        try {
            // 한국 주식인지 미국 주식인지 판단
            boolean isUSStock = stockCode.matches("[A-Z]{1,5}"); // 알파벳만
            
            if (isUSStock) {
                logger.info("🇺🇸 미국 주식으로 판단: {}", stockCode);
                usStockPriceUpdateService.updateUSStockPrice(stockCode);
            } else {
                logger.info("🇰🇷 한국 주식으로 판단: {}", stockCode);
                stockPriceUpdateService.updateStockPrice(stockCode);
            }
            
            response.put("success", true);
            response.put("message", "종목 업데이트 완료: " + stockCode);
            response.put("stockCode", stockCode);
            response.put("type", isUSStock ? "미국 주식" : "한국 주식");
            
            logger.info("✅ 종목 업데이트 완료: {}", stockCode);
            
        } catch (Exception e) {
            logger.error("❌ 종목 업데이트 실패: {}", stockCode, e);
            response.put("success", false);
            response.put("message", "업데이트 실패: " + e.getMessage());
            response.put("stockCode", stockCode);
            return ResponseEntity.status(500).body(response);
        } finally {
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 특정 시장만 업데이트 (KOSPI, KOSDAQ, NASDAQ, NYSE, AMEX)
     * 
     * GET /api/admin/update-market?marketType=KOSPI
     * 
     * @param marketType 시장 타입
     */
    @GetMapping("/update-market")
    public ResponseEntity<Map<String, Object>> updateMarket(
            @RequestParam String marketType) {
        
        Map<String, Object> response = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📊 {} 시장 현재가 업데이트 시작", marketType);
        
        try {
            long startTime = System.currentTimeMillis();
            
            // 미국 시장인지 한국 시장인지 판단
            boolean isUSMarket = marketType.equals("NASDAQ") || 
                                marketType.equals("NYSE") || 
                                marketType.equals("AMEX");
            
            if (isUSMarket) {
                usStockPriceUpdateService.updateByMarketType(marketType);
            } else {
                stockPriceUpdateService.updateByMarketType(marketType);
            }
            
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000;
            
            response.put("success", true);
            response.put("message", marketType + " 시장 업데이트 완료");
            response.put("marketType", marketType);
            response.put("duration", duration + "초");
            
            logger.info("✅ {} 업데이트 완료 ({}초 소요)", marketType, duration);
            
        } catch (Exception e) {
            logger.error("❌ {} 업데이트 실패", marketType, e);
            response.put("success", false);
            response.put("message", "업데이트 실패: " + e.getMessage());
            response.put("marketType", marketType);
            return ResponseEntity.status(500).body(response);
        } finally {
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 크롤링 시스템 상태 확인
     * 
     * GET /api/admin/update-status
     */
    @GetMapping("/update-status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("success", true);
            response.put("message", "크롤링 시스템 정상 작동 중");
            response.put("koreanService", "StockPriceUpdateService");
            response.put("usService", "USStockPriceUpdateService");
            response.put("availableEndpoints", new String[] {
                "/api/admin/update-all",
                "/api/admin/update-korean",
                "/api/admin/update-us",
                "/api/admin/update-stock?stockCode={code}",
                "/api/admin/update-market?marketType={market}",
                "/api/admin/update-status"
            });
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "시스템 오류: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
}
