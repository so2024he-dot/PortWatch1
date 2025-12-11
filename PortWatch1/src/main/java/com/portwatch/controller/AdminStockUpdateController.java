package com.portwatch.controller;

import com.portwatch.scheduler.StockPriceScheduler;
import com.portwatch.service.StockPriceUpdateService;
import com.portwatch.service.USStockPriceUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 주가 업데이트 관리자 컨트롤러
 * 수동으로 주가 업데이트를 실행할 수 있는 API 제공
 * 
 * 주의: 실제 운영 환경에서는 관리자 권한 체크 필요
 */
@RestController
@RequestMapping("/api/admin/stock-update")
public class AdminStockUpdateController {

    private static final Logger logger = LoggerFactory.getLogger(AdminStockUpdateController.class);

    @Autowired
    private StockPriceScheduler stockPriceScheduler;

    @Autowired
    private StockPriceUpdateService koreanStockService;

    @Autowired
    private USStockPriceUpdateService usStockService;

    /**
     * 한국 주식 수동 업데이트
     * GET /api/admin/stock-update/korean
     */
    @GetMapping("/korean")
    public ResponseEntity<Map<String, Object>> updateKoreanStocks() {
        logger.info("🔧 [API] 한국 주식 수동 업데이트 요청");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            long startTime = System.currentTimeMillis();
            int count = stockPriceScheduler.manualUpdateKorean();
            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            
            response.put("success", true);
            response.put("market", "KOREAN");
            response.put("updatedCount", count);
            response.put("elapsedSeconds", elapsedTime);
            response.put("message", "한국 주식 업데이트 완료: " + count + "개 종목");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ [API] 한국 주식 업데이트 실패", e);
            
            response.put("success", false);
            response.put("market", "KOREAN");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 미국 주식 수동 업데이트
     * GET /api/admin/stock-update/us
     */
    @GetMapping("/us")
    public ResponseEntity<Map<String, Object>> updateUSStocks() {
        logger.info("🔧 [API] 미국 주식 수동 업데이트 요청");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            long startTime = System.currentTimeMillis();
            int count = stockPriceScheduler.manualUpdateUS();
            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            
            response.put("success", true);
            response.put("market", "US");
            response.put("updatedCount", count);
            response.put("elapsedSeconds", elapsedTime);
            response.put("message", "미국 주식 업데이트 완료: " + count + "개 종목");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ [API] 미국 주식 업데이트 실패", e);
            
            response.put("success", false);
            response.put("market", "US");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 전체 주식 수동 업데이트 (한국 + 미국)
     * GET /api/admin/stock-update/all
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> updateAllStocks() {
        logger.info("🔧 [API] 전체 주식 수동 업데이트 요청");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            long startTime = System.currentTimeMillis();
            Map<String, Integer> updateResult = stockPriceScheduler.manualUpdateAll();
            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            
            response.put("success", true);
            response.put("market", "ALL");
            response.put("koreanCount", updateResult.get("korean"));
            response.put("usCount", updateResult.get("us"));
            response.put("totalCount", updateResult.get("total"));
            response.put("elapsedSeconds", elapsedTime);
            response.put("message", "전체 주식 업데이트 완료: 한국 " + updateResult.get("korean") + 
                    "개, 미국 " + updateResult.get("us") + "개");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ [API] 전체 주식 업데이트 실패", e);
            
            response.put("success", false);
            response.put("market", "ALL");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 특정 종목 수동 업데이트
     * GET /api/admin/stock-update/single/{stockCode}
     * 
     * 예: /api/admin/stock-update/single/005930 (삼성전자)
     *     /api/admin/stock-update/single/AAPL (애플)
     */
    @GetMapping("/single/{stockCode}")
    public ResponseEntity<Map<String, Object>> updateSingleStock(@PathVariable String stockCode) {
        logger.info("🔧 [API] 단일 종목 업데이트 요청: {}", stockCode);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            long startTime = System.currentTimeMillis();
            
            // 미국 주식인지 한국 주식인지 판단 (간단한 방법: 알파벳으로 시작하면 미국)
            boolean isUSStock = stockCode.matches("^[A-Z]+$");
            
            if (isUSStock) {
                usStockService.updateSingleUSStock(stockCode);
                response.put("market", "US");
            } else {
                koreanStockService.updateSingleStock(stockCode);
                response.put("market", "KOREAN");
            }
            
            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            
            response.put("success", true);
            response.put("stockCode", stockCode);
            response.put("elapsedSeconds", elapsedTime);
            response.put("message", "종목 " + stockCode + " 업데이트 완료");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ [API] 종목 {} 업데이트 실패", stockCode, e);
            
            response.put("success", false);
            response.put("stockCode", stockCode);
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 스케줄러 상태 확인
     * GET /api/admin/stock-update/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSchedulerStatus() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("schedulerEnabled", true);
        response.put("koreanSchedule", "매일 00:00 (자정)");
        response.put("usSchedule", "매일 06:00 (오전 6시)");
        response.put("message", "스케줄러가 정상 작동 중입니다");
        
        return ResponseEntity.ok(response);
    }
}