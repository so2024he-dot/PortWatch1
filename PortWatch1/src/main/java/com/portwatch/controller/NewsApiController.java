package com.portwatch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portwatch.domain.NewsVO;
import com.portwatch.service.NewsService;

/**
 * ✅ 뉴스 API 컨트롤러 (수정 버전)
 * REST API 엔드포인트 제공
 * 
 * @author PortWatch Team
 * @version 3.1 - /api/news/all 엔드포인트 추가
 */
@RestController
@RequestMapping("/api/news")
public class NewsApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsApiController.class);
    
    @Autowired(required = false)
    private NewsService newsService;
    
    /**
     * ✅ 모든 뉴스 조회 (list.jsp에서 사용)
     * GET /api/news/all
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllNews() {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📰 전체 뉴스 조회 API 호출");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (newsService == null) {
                logger.error("❌ NewsService is null");
                response.put("success", false);
                response.put("message", "NewsService가 초기화되지 않았습니다.");
                response.put("newsList", java.util.Collections.emptyList());
                return ResponseEntity.status(500).body(response);
            }
            
            // 뉴스 목록 조회 (최대 50개)
            List<NewsVO> newsList = newsService.getRecentNews(50);
            
            logger.info("✅ 전체 뉴스 {} 건 조회 완료", newsList.size());
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            response.put("success", true);
            response.put("newsList", newsList);
            response.put("count", newsList.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ 전체 뉴스 조회 실패", e);
            response.put("success", false);
            response.put("message", "뉴스 조회 실패: " + e.getMessage());
            response.put("newsList", java.util.Collections.emptyList());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 뉴스 크롤링 시작
     * POST /api/news/crawl
     */
    @PostMapping("/crawl")
    public ResponseEntity<Map<String, Object>> crawlNews() {
        logger.info("뉴스 크롤링 API 호출");
        
        try {
            if (newsService == null) {
                logger.error("NewsService is null");
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "NewsService가 초기화되지 않았습니다.");
                return ResponseEntity.status(500).body(error);
            }
            
            // 뉴스 크롤링 실행
            int count = newsService.crawlAndSaveNews();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            response.put("message", count + "개의 뉴스를 크롤링했습니다.");
            
            logger.info("뉴스 크롤링 완료 - {} 건", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("뉴스 크롤링 실패", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "크롤링 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * 최신 뉴스 조회
     * GET /api/news/recent?limit=10
     */
    @GetMapping("/recent")
    public ResponseEntity<List<NewsVO>> getRecentNews(
            @RequestParam(defaultValue = "10") int limit) {
        
        logger.info("최신 뉴스 조회 API 호출 - limit: {}", limit);
        
        try {
            if (newsService == null) {
                logger.error("NewsService is null");
                return ResponseEntity.status(500).build();
            }
            
            List<NewsVO> newsList = newsService.getRecentNews(limit);
            logger.info("뉴스 {} 건 조회 완료", newsList.size());
            
            return ResponseEntity.ok(newsList);
            
        } catch (Exception e) {
            logger.error("뉴스 조회 실패", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 종목별 뉴스 조회
     * GET /api/news/stock?stockCode=005930&limit=5
     */
    @GetMapping("/stock")
    public ResponseEntity<List<NewsVO>> getNewsByStock(
            @RequestParam String stockCode,
            @RequestParam(defaultValue = "5") int limit) {
        
        logger.info("종목별 뉴스 조회 API 호출 - stockCode: {}, limit: {}", stockCode, limit);
        
        try {
            if (newsService == null) {
                logger.error("NewsService is null");
                return ResponseEntity.status(500).build();
            }
            
            List<NewsVO> newsList = newsService.getNewsByStockCode(stockCode, limit);
            logger.info("종목 {} 뉴스 {} 건 조회 완료", stockCode, newsList.size());
            
            return ResponseEntity.ok(newsList);
            
        } catch (Exception e) {
            logger.error("종목별 뉴스 조회 실패", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 뉴스 통계 조회
     * GET /api/news/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getNewsStats() {
        logger.info("뉴스 통계 조회 API 호출");
        
        try {
            if (newsService == null) {
                logger.error("NewsService is null");
                return ResponseEntity.status(500).build();
            }
            
            Map<String, Object> stats = newsService.getNewsStats();
            logger.info("뉴스 통계 조회 완료");
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("뉴스 통계 조회 실패", e);
            return ResponseEntity.status(500).build();
        }
    }
}
