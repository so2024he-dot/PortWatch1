package com.portwatch.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portwatch.scheduler.NewsScheduler;
import com.portwatch.scheduler.StockSymbolScheduler;
import com.portwatch.service.NewsService;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * AdminNewsUpdateController - 완전판 (한국+미국 뉴스 + 주식 종목)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 기능:
 * - 한국 뉴스 크롤링 수동 실행
 * - 미국 뉴스 크롤링 수동 실행 (신규!)
 * - 전체 뉴스 크롤링 수동 실행 (신규!)
 * - 주식 종목 크롤링 수동 실행 (신규!)
 * - 뉴스 크롤링 상태 확인
 * 
 * API 목록:
 * - GET /api/admin/update-news: 한국 뉴스 크롤링 즉시 실행
 * - GET /api/admin/update-us-news: 미국 뉴스 크롤링 즉시 실행 (신규!)
 * - GET /api/admin/update-all-news: 전체 뉴스 크롤링 즉시 실행 (신규!)
 * - GET /api/admin/update-stock-symbols: 주식 종목 크롤링 즉시 실행 (신규!)
 * - GET /api/admin/news-status: 뉴스 크롤링 상태 확인
 * 
 * @author PortWatch
 * @version 2.0 - 2026.01.16 (미국 뉴스 + 주식 종목 추가)
 */
@RestController
@RequestMapping("/api/admin")
public class AdminNewsUpdateController {
    
    @Autowired
    private NewsScheduler newsScheduler;
    
    @Autowired
    private NewsService newsService;
    
    @Autowired(required = false)
    private StockSymbolScheduler stockSymbolScheduler;
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 한국 뉴스 크롤링 즉시 실행
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/admin/update-news
     * 
     * 사용 방법:
     * 브라우저에서: http://localhost:8088/api/admin/update-news
     * 
     * @return 크롤링 결과
     */
    @GetMapping("/update-news")
    public ResponseEntity<Map<String, Object>> updateNews() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 [API] 한국 뉴스 크롤링 수동 실행");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 한국 뉴스 크롤링 실행
            int savedCount = newsScheduler.crawlKoreanNewsNow();
            
            result.put("success", true);
            result.put("message", "한국 뉴스 크롤링 완료");
            result.put("country", "KR");
            result.put("savedCount", savedCount);
            result.put("timestamp", System.currentTimeMillis());
            
            System.out.println("✅ 크롤링 완료: " + savedCount + "개");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("❌ 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "한국 뉴스 크롤링 실패: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 미국 뉴스 크롤링 즉시 실행 (신규!)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/admin/update-us-news
     * 
     * @return 크롤링 결과
     */
    @GetMapping("/update-us-news")
    public ResponseEntity<Map<String, Object>> updateUSNews() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 [API] 미국 뉴스 크롤링 수동 실행");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 미국 뉴스 크롤링 실행
            int savedCount = newsScheduler.crawlUSNewsNow();
            
            result.put("success", true);
            result.put("message", "미국 뉴스 크롤링 완료");
            result.put("country", "US");
            result.put("savedCount", savedCount);
            result.put("timestamp", System.currentTimeMillis());
            
            System.out.println("✅ 크롤링 완료: " + savedCount + "개");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("❌ 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "미국 뉴스 크롤링 실패: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 전체 뉴스 크롤링 즉시 실행 (한국 + 미국) - 신규!
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/admin/update-all-news
     * 
     * @return 크롤링 결과
     */
    @GetMapping("/update-all-news")
    public ResponseEntity<Map<String, Object>> updateAllNews() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 [API] 전체 뉴스 크롤링 수동 실행");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 전체 뉴스 크롤링 실행
            int savedCount = newsScheduler.crawlAllNewsNow();
            
            result.put("success", true);
            result.put("message", "전체 뉴스 크롤링 완료");
            result.put("country", "ALL");
            result.put("savedCount", savedCount);
            result.put("timestamp", System.currentTimeMillis());
            
            System.out.println("✅ 크롤링 완료: " + savedCount + "개");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("❌ 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "전체 뉴스 크롤링 실패: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 주식 종목 크롤링 즉시 실행 (한국 + 미국) - 신규!
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/admin/update-stock-symbols
     * 
     * @return 크롤링 결과
     */
    @GetMapping("/update-stock-symbols")
    public ResponseEntity<Map<String, Object>> updateStockSymbols() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 [API] 주식 종목 크롤링 수동 실행");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Map<String, Object> result = new HashMap<>();
        
        if (stockSymbolScheduler == null) {
            result.put("success", false);
            result.put("message", "StockSymbolScheduler를 사용할 수 없습니다");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
        }
        
        try {
            // 주식 종목 크롤링 실행
            int savedCount = stockSymbolScheduler.crawlAllSymbolsNow();
            
            result.put("success", true);
            result.put("message", "주식 종목 크롤링 완료");
            result.put("savedCount", savedCount);
            result.put("timestamp", System.currentTimeMillis());
            
            System.out.println("✅ 크롤링 완료: " + savedCount + "개");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("❌ 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "주식 종목 크롤링 실패: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 뉴스 크롤링 상태 확인
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/admin/news-status
     * 
     * @return 뉴스 현황
     */
    @GetMapping("/news-status")
    public ResponseEntity<Map<String, Object>> getNewsStatus() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 [API] 뉴스 상태 조회");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 최근 뉴스 개수 조회
            int newsCount = newsService.getRecentNews(100).size();
            
            result.put("success", true);
            result.put("newsCount", newsCount);
            result.put("message", "현재 저장된 뉴스: " + newsCount + "개");
            result.put("timestamp", System.currentTimeMillis());
            
            System.out.println("✅ 현재 뉴스 개수: " + newsCount);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("❌ 상태 조회 실패: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            result.put("message", "상태 조회 실패: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
