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
import com.portwatch.service.NewsService;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * AdminNewsUpdateController - 뉴스 크롤링 수동 실행 API (신규!)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 기능:
 * - 뉴스 크롤링 수동 실행
 * - 뉴스 크롤링 상태 확인
 * 
 * API 목록:
 * - GET /api/admin/update-news: 뉴스 크롤링 즉시 실행
 * - GET /api/admin/news-status: 뉴스 크롤링 상태 확인
 * 
 * @author PortWatch
 * @version 1.0 - 2026.01.16
 */
@RestController
@RequestMapping("/api/admin")
public class AdminNewsUpdateController {
    
    @Autowired
    private NewsScheduler newsScheduler;
    
    @Autowired
    private NewsService newsService;
    
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 뉴스 크롤링 즉시 실행
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
        System.out.println("📰 [API] 뉴스 크롤링 수동 실행");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 뉴스 크롤링 실행
            int savedCount = newsScheduler.crawlNewsNow();
            
            result.put("success", true);
            result.put("message", "뉴스 크롤링 완료");
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
            result.put("message", "뉴스 크롤링 실패: " + e.getMessage());
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
