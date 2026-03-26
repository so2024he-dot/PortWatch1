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
 * AdminNewsUpdateController - 완전판 (한국+미국 뉴스 + 주식 종목)
 *
 * ✅ 500 오류 수정 내용:
 *   [원인] @Autowired NewsScheduler newsScheduler;
 *          → Bean이 없으면 NoSuchBeanDefinitionException
 *          → DispatcherServlet 기동 실패 → HTTP 500
 *
 *   [수정] @Autowired → @Autowired(required = false)
 *          NewsScheduler, NewsService 모두 required=false 추가
 *          → Bean 없어도 null 처리 → 기동 성공
 *          → null 체크 후 안전하게 사용
 *
 * ✅ 기존 기능 전부 유지:
 *   - 한국 뉴스 크롤링 수동 실행
 *   - 미국 뉴스 크롤링 수동 실행
 *   - 전체 뉴스 크롤링 수동 실행
 *   - 주식 종목 크롤링 수동 실행
 *   - 뉴스 크롤링 상태 확인
 *
 * API 목록:
 *   GET /api/admin/update-news        : 한국 뉴스 크롤링 즉시 실행
 *   GET /api/admin/update-us-news     : 미국 뉴스 크롤링 즉시 실행
 *   GET /api/admin/update-all-news    : 전체 뉴스 크롤링 즉시 실행
 *   GET /api/admin/update-stock-symbols: 주식 종목 크롤링 즉시 실행
 *   GET /api/admin/news-status        : 뉴스 크롤링 상태 확인
 *
 * @author PortWatch
 * @version 2.1 - 500 오류 수정 (required=false 추가)
 */
@RestController
@RequestMapping("/api/admin")
public class AdminNewsUpdateController {

    // ✅ 수정: required=false 추가 → NewsScheduler Bean 없어도 기동 성공
    @Autowired(required = false)
    private NewsScheduler newsScheduler;

    // ✅ 수정: required=false 추가 → NewsService Bean 없어도 기동 성공
    @Autowired(required = false)
    private NewsService newsService;

    // 기존 코드에서 이미 required=false 였으므로 유지
    @Autowired(required = false)
    private StockSymbolScheduler stockSymbolScheduler;

    // ─────────────────────────────────────────────────────
    // 공통 유틸: Bean null 체크 응답 생성
    // ─────────────────────────────────────────────────────
    private ResponseEntity<Map<String, Object>> beanNotAvailable(String beanName) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", beanName + " Bean을 사용할 수 없습니다. 서버 설정을 확인하세요.");
        System.err.println("[WARN] " + beanName + " is not available (null).");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 한국 뉴스 크롤링 즉시 실행
    // GET /api/admin/update-news
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @GetMapping("/update-news")
    public ResponseEntity<Map<String, Object>> updateNews() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("[API] 한국 뉴스 크롤링 수동 실행");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // ✅ null 체크 추가
        if (newsScheduler == null) {
            return beanNotAvailable("NewsScheduler");
        }

        Map<String, Object> result = new HashMap<>();
        try {
            int savedCount = newsScheduler.crawlKoreanNewsNow();

            result.put("success", true);
            result.put("message", "한국 뉴스 크롤링 완료");
            result.put("country", "KR");
            result.put("savedCount", savedCount);
            result.put("timestamp", System.currentTimeMillis());

            System.out.println("[OK] 크롤링 완료: " + savedCount + "개");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("[ERROR] 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "한국 뉴스 크롤링 실패: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 미국 뉴스 크롤링 즉시 실행
    // GET /api/admin/update-us-news
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @GetMapping("/update-us-news")
    public ResponseEntity<Map<String, Object>> updateUSNews() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("[API] 미국 뉴스 크롤링 수동 실행");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // ✅ null 체크 추가
        if (newsScheduler == null) {
            return beanNotAvailable("NewsScheduler");
        }

        Map<String, Object> result = new HashMap<>();
        try {
            int savedCount = newsScheduler.crawlUSNewsNow();

            result.put("success", true);
            result.put("message", "미국 뉴스 크롤링 완료");
            result.put("country", "US");
            result.put("savedCount", savedCount);
            result.put("timestamp", System.currentTimeMillis());

            System.out.println("[OK] 크롤링 완료: " + savedCount + "개");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("[ERROR] 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "미국 뉴스 크롤링 실패: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 전체 뉴스 크롤링 즉시 실행 (한국 + 미국)
    // GET /api/admin/update-all-news
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @GetMapping("/update-all-news")
    public ResponseEntity<Map<String, Object>> updateAllNews() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("[API] 전체 뉴스 크롤링 수동 실행");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // ✅ null 체크 추가
        if (newsScheduler == null) {
            return beanNotAvailable("NewsScheduler");
        }

        Map<String, Object> result = new HashMap<>();
        try {
            int savedCount = newsScheduler.crawlAllNewsNow();

            result.put("success", true);
            result.put("message", "전체 뉴스 크롤링 완료");
            result.put("country", "ALL");
            result.put("savedCount", savedCount);
            result.put("timestamp", System.currentTimeMillis());

            System.out.println("[OK] 크롤링 완료: " + savedCount + "개");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("[ERROR] 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "전체 뉴스 크롤링 실패: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 주식 종목 크롤링 즉시 실행 (한국 + 미국)
    // GET /api/admin/update-stock-symbols
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @GetMapping("/update-stock-symbols")
    public ResponseEntity<Map<String, Object>> updateStockSymbols() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("[API] 주식 종목 크롤링 수동 실행");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // 기존 코드: stockSymbolScheduler null 체크 유지
        if (stockSymbolScheduler == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "StockSymbolScheduler를 사용할 수 없습니다");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
        }

        Map<String, Object> result = new HashMap<>();
        try {
            int savedCount = stockSymbolScheduler.crawlAllSymbolsNow();

            result.put("success", true);
            result.put("message", "주식 종목 크롤링 완료");
            result.put("savedCount", savedCount);
            result.put("timestamp", System.currentTimeMillis());

            System.out.println("[OK] 크롤링 완료: " + savedCount + "개");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("[ERROR] 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "주식 종목 크롤링 실패: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 뉴스 크롤링 상태 확인
    // GET /api/admin/news-status
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @GetMapping("/news-status")
    public ResponseEntity<Map<String, Object>> getNewsStatus() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("[API] 뉴스 상태 조회");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        Map<String, Object> result = new HashMap<>();

        // ✅ null 체크 추가 - newsService 없어도 상태 반환
        if (newsService == null) {
            result.put("success", false);
            result.put("message", "NewsService를 사용할 수 없습니다.");
            result.put("schedulerStatus",
                newsScheduler != null ? "NewsScheduler: active" : "NewsScheduler: not available");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
        }

        try {
            int newsCount = newsService.getRecentNews(100).size();

            result.put("success", true);
            result.put("newsCount", newsCount);
            result.put("message", "현재 저장된 뉴스: " + newsCount + "개");
            result.put("schedulerStatus",
                newsScheduler != null ? "NewsScheduler: active" : "NewsScheduler: not available");
            result.put("timestamp", System.currentTimeMillis());

            System.out.println("[OK] 현재 뉴스 개수: " + newsCount);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("[ERROR] 상태 조회 실패: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "상태 조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
