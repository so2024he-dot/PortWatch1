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
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * NewsApiController - 뉴스 API 컨트롤러
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * 주요 기능:
 * - 최근 뉴스 조회 (GET /api/news/recent)
 * - 뉴스 크롤링 (POST /api/news/crawl)
 * 
 * 수정 내역:
 * - 2025-12-29: API 엔드포인트 수정 (/api/news/all → /api/news/recent)
 * 
 * @author PortWatch Team
 * @version 1.0
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@RestController
@RequestMapping("/api/news")
public class NewsApiController {

    private static final Logger log = LoggerFactory.getLogger(NewsApiController.class);

    @Autowired
    private NewsService newsService;

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 최근 뉴스 조회
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/news/recent
     * 파라미터: limit (조회 개수, 기본값: 50)
     * 
     * 응답 형식:
     * {
     *   "success": true,
     *   "news": [...],
     *   "count": 40
     * }
     * 
     * 또는 뉴스 배열만 반환:
     * [...]
     * 
     * @param limit 조회 개수
     * @return ResponseEntity<Map<String, Object>>
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentNews(
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📰 최근 뉴스 조회");
        log.info("  - 조회 개수: " + limit);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 뉴스 조회
            List<NewsVO> newsList = newsService.getRecentNews(limit);
            
            log.info("  - 조회 결과: " + newsList.size() + "건");
            log.info("✅ 최근 뉴스 조회 완료");
            
            // 응답 데이터 구성
            response.put("success", true);
            response.put("news", newsList);
            response.put("newsList", newsList);  // 호환성을 위한 추가 필드
            response.put("count", newsList.size());
            response.put("message", newsList.size() + "개의 뉴스를 불러왔습니다.");
            
        } catch (Exception e) {
            log.error("❌ 최근 뉴스 조회 실패", e);
            
            response.put("success", false);
            response.put("news", new Object[0]);
            response.put("newsList", new Object[0]);
            response.put("count", 0);
            response.put("error", "뉴스를 불러오는데 실패했습니다.");
            response.put("message", e.getMessage());
        }
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return ResponseEntity.ok(response);
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 전체 뉴스 조회 (하위 호환성)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/news/all
     * 
     * 설명: 
     * - /api/news/all → /api/news/recent로 리다이렉트
     * - 하위 호환성을 위한 엔드포인트
     * 
     * @return ResponseEntity<Map<String, Object>>
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllNews() {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📰 전체 뉴스 조회 (하위 호환)");
        log.info("  - /api/news/all → /api/news/recent 호출");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return getRecentNews(50);
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 뉴스 크롤링
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: POST /api/news/crawl
     * 
     * 기능:
     * - 네이버 금융 뉴스 크롤링
     * - 중복 제거
     * - DB 저장
     * 
     * 응답 형식:
     * {
     *   "success": true,
     *   "count": 15,
     *   "message": "15개의 새로운 뉴스를 불러왔습니다."
     * }
     * 
     * @return ResponseEntity<Map<String, Object>>
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @PostMapping("/crawl")
    public ResponseEntity<Map<String, Object>> crawlNews() {
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🕷️ 뉴스 크롤링 시작");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 뉴스 크롤링 실행
            int newCount = newsService.crawlAndSaveNews();
            
            log.info("  - 새로운 뉴스: " + newCount + "건");
            log.info("✅ 뉴스 크롤링 완료");
            
            response.put("success", true);
            response.put("count", newCount);
            response.put("newCount", newCount);  // 호환성을 위한 추가 필드
            response.put("message", newCount + "개의 새로운 뉴스를 불러왔습니다.");
            
        } catch (Exception e) {
            log.error("❌ 뉴스 크롤링 실패", e);
            
            response.put("success", false);
            response.put("count", 0);
            response.put("newCount", 0);
            response.put("error", "뉴스 크롤링에 실패했습니다.");
            response.put("message", e.getMessage());
        }
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return ResponseEntity.ok(response);
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 카테고리별 뉴스 조회
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/news/category
     * 파라미터: 
     * - category: 카테고리명
     * - limit: 조회 개수 (기본값: 20)
     * 
     * @param category 카테고리명
     * @param limit 조회 개수
     * @return ResponseEntity<Map<String, Object>>
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @GetMapping("/category")
    public ResponseEntity<Map<String, Object>> getNewsByCategory(
            @RequestParam("category") String category,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📰 카테고리별 뉴스 조회");
        log.info("  - 카테고리: " + category);
        log.info("  - 조회 개수: " + limit);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<NewsVO> newsList = newsService.getNewsByCategory(category, limit);
            
            log.info("  - 조회 결과: " + newsList.size() + "건");
            log.info("✅ 카테고리별 뉴스 조회 완료");
            
            response.put("success", true);
            response.put("news", newsList);
            response.put("newsList", newsList);
            response.put("count", newsList.size());
            response.put("category", category);
            
        } catch (Exception e) {
            log.error("❌ 카테고리별 뉴스 조회 실패", e);
            
            response.put("success", false);
            response.put("news", new Object[0]);
            response.put("newsList", new Object[0]);
            response.put("count", 0);
            response.put("error", "뉴스를 불러오는데 실패했습니다.");
        }
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return ResponseEntity.ok(response);
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 뉴스 검색
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /api/news/search
     * 파라미터: 
     * - keyword: 검색 키워드
     * - limit: 조회 개수 (기본값: 20)
     * 
     * @param keyword 검색 키워드
     * @param limit 조회 개수
     * @return ResponseEntity<Map<String, Object>>
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchNews(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🔍 뉴스 검색");
        log.info("  - 검색어: " + keyword);
        log.info("  - 조회 개수: " + limit);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<NewsVO> newsList = newsService.searchNews(keyword, limit);
            
            log.info("  - 검색 결과: " + newsList.size() + "건");
            log.info("✅ 뉴스 검색 완료");
            
            response.put("success", true);
            response.put("news", newsList);
            response.put("newsList", newsList);
            response.put("count", newsList.size());
            response.put("keyword", keyword);
            
        } catch (Exception e) {
            log.error("❌ 뉴스 검색 실패", e);
            
            response.put("success", false);
            response.put("news", new Object[0]);
            response.put("newsList", new Object[0]);
            response.put("count", 0);
            response.put("error", "검색에 실패했습니다.");
        }
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return ResponseEntity.ok(response);
    }
}
