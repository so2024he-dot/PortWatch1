package com.portwatch.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portwatch.service.KoreaStockCrawlerService;
import com.portwatch.service.USStockCrawlerService;

import lombok.extern.slf4j.Slf4j;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 주식 크롤링 API Controller
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 한국/미국 주식 100대 기업 데이터 자동 업데이트 API
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Slf4j
@RestController
@RequestMapping("/api/crawler")
public class StockCrawlerController {

    @Autowired
    private KoreaStockCrawlerService koreaStockCrawlerService;

    @Autowired
    private USStockCrawlerService usStockCrawlerService;

    /**
     * 한국 주식 100대 기업 수동 업데이트
     * GET /api/crawler/korea/update
     */
    @PostMapping("/korea/update")
    public ResponseEntity<Map<String, Object>> updateKoreaStocks() {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("API: 한국 주식 수동 업데이트 요청");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        Map<String, Object> response = new HashMap<>();

        try {
            long startTime = System.currentTimeMillis();
            
            int updatedCount = koreaStockCrawlerService.crawlAndUpdateTop100Stocks();
            
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000;

            response.put("success", true);
            response.put("message", "한국 주식 업데이트 성공");
            response.put("country", "KR");
            response.put("updatedCount", updatedCount);
            response.put("duration", duration + "초");

            log.info("한국 주식 업데이트 완료: {}개 ({}초)", updatedCount, duration);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("한국 주식 업데이트 실패", e);

            response.put("success", false);
            response.put("message", "한국 주식 업데이트 실패: " + e.getMessage());
            response.put("country", "KR");
            response.put("updatedCount", 0);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 미국 주식 100대 기업 수동 업데이트
     * POST /api/crawler/us/update
     */
    @PostMapping("/us/update")
    public ResponseEntity<Map<String, Object>> updateUSStocks() {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("API: 미국 주식 수동 업데이트 요청");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        Map<String, Object> response = new HashMap<>();

        try {
            long startTime = System.currentTimeMillis();
            
            int updatedCount = usStockCrawlerService.crawlAndUpdateTop100Stocks();
            
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000;

            response.put("success", true);
            response.put("message", "미국 주식 업데이트 성공");
            response.put("country", "US");
            response.put("updatedCount", updatedCount);
            response.put("duration", duration + "초");

            log.info("미국 주식 업데이트 완료: {}개 ({}초)", updatedCount, duration);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("미국 주식 업데이트 실패", e);

            response.put("success", false);
            response.put("message", "미국 주식 업데이트 실패: " + e.getMessage());
            response.put("country", "US");
            response.put("updatedCount", 0);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 한국 + 미국 주식 전체 업데이트
     * POST /api/crawler/all/update
     */
    @PostMapping("/all/update")
    public ResponseEntity<Map<String, Object>> updateAllStocks() {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("API: 전체 주식 수동 업데이트 요청");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        Map<String, Object> response = new HashMap<>();

        try {
            long startTime = System.currentTimeMillis();
            
            // 한국 주식 업데이트
            int koreaCount = koreaStockCrawlerService.crawlAndUpdateTop100Stocks();
            
            // 미국 주식 업데이트
            int usCount = usStockCrawlerService.crawlAndUpdateTop100Stocks();
            
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000;

            response.put("success", true);
            response.put("message", "전체 주식 업데이트 성공");
            response.put("koreaCount", koreaCount);
            response.put("usCount", usCount);
            response.put("totalCount", koreaCount + usCount);
            response.put("duration", duration + "초");

            log.info("전체 주식 업데이트 완료: 한국 {}개, 미국 {}개 ({}초)", 
                    koreaCount, usCount, duration);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("전체 주식 업데이트 실패", e);

            response.put("success", false);
            response.put("message", "전체 주식 업데이트 실패: " + e.getMessage());
            response.put("koreaCount", 0);
            response.put("usCount", 0);
            response.put("totalCount", 0);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 크롤러 상태 확인
     * GET /api/crawler/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCrawlerStatus() {
        Map<String, Object> response = new HashMap<>();

        response.put("status", "active");
        response.put("version", "1.0.0");
        response.put("supportedCountries", new String[]{"KR", "US"});
        response.put("maxStocksPerCountry", 100);
        response.put("description", "주식 자동 크롤링 시스템");

        return ResponseEntity.ok(response);
    }
}
