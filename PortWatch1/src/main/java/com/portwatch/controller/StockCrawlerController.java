package com.portwatch.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.portwatch.service.KoreaStockCrawlerService;
import com.portwatch.service.USStockCrawlerService;

import lombok.extern.slf4j.Slf4j;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 주식 크롤링 Controller (프론트엔드 ↔ 백엔드 연결)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 크롤링 UI 페이지 + REST API 제공
 *
 * [URL 매핑]
 * GET  /crawler              → 크롤링 관리 UI 페이지
 * POST /crawler/korea        → 한국 주식 크롤링 실행
 * POST /crawler/us           → 미국 주식 크롤링 실행
 * POST /crawler/all          → 전체 크롤링 실행
 * GET  /crawler/status       → 크롤링 상태 확인
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Slf4j
@Controller
@RequestMapping("/crawler")
public class StockCrawlerController {

    @Autowired
    private KoreaStockCrawlerService koreaStockCrawlerService;

    @Autowired
    private USStockCrawlerService usStockCrawlerService;

    /** 크롤링 진행 상태 (스레드 안전) */
    private volatile boolean isRunning = false;
    private volatile String  lastStatus = "대기중";
    private volatile int     lastKoreaCount = 0;
    private volatile int     lastUsCount = 0;

    /* ============================================================
     * GET /crawler → 크롤링 관리 페이지 (JSP)
     * ============================================================ */
    @GetMapping
    public String crawlerPage() {
        return "crawler/stock-update"; // /WEB-INF/views/crawler/stock-update.jsp
    }

    /* ============================================================
     * POST /crawler/korea → 한국 주식 크롤링
     * ============================================================ */
    @PostMapping("/korea")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crawlKorea() {
        Map<String, Object> result = new HashMap<>();

        if (isRunning) {
            result.put("success", false);
            result.put("message", "현재 크롤링 진행 중입니다.");
            return ResponseEntity.ok(result);
        }

        try {
            isRunning = true;
            lastStatus = "한국 주식 크롤링 중...";

            log.info("한국 주식 크롤링 요청");
            int count = koreaStockCrawlerService.crawlAndUpdateTop100Stocks();

            lastKoreaCount = count;
            lastStatus = "완료";
            result.put("success", true);
            result.put("message", "한국 주식 " + count + "개 업데이트 완료");
            result.put("count", count);

        } catch (Exception e) {
            lastStatus = "오류: " + e.getMessage();
            result.put("success", false);
            result.put("message", "크롤링 실패: " + e.getMessage());
            log.error("한국 주식 크롤링 실패", e);
        } finally {
            isRunning = false;
        }

        return ResponseEntity.ok(result);
    }

    /* ============================================================
     * POST /crawler/us → 미국 주식 크롤링
     * ============================================================ */
    @PostMapping("/us")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crawlUs() {
        Map<String, Object> result = new HashMap<>();

        if (isRunning) {
            result.put("success", false);
            result.put("message", "현재 크롤링 진행 중입니다.");
            return ResponseEntity.ok(result);
        }

        try {
            isRunning = true;
            lastStatus = "미국 주식 크롤링 중...";

            log.info("미국 주식 크롤링 요청");
            int count = usStockCrawlerService.crawlAndUpdateTop100Stocks();

            lastUsCount = count;
            lastStatus = "완료";
            result.put("success", true);
            result.put("message", "미국 주식 " + count + "개 업데이트 완료");
            result.put("count", count);

        } catch (Exception e) {
            lastStatus = "오류: " + e.getMessage();
            result.put("success", false);
            result.put("message", "크롤링 실패: " + e.getMessage());
            log.error("미국 주식 크롤링 실패", e);
        } finally {
            isRunning = false;
        }

        return ResponseEntity.ok(result);
    }

    /* ============================================================
     * POST /crawler/all → 한국 + 미국 전체 크롤링
     * ============================================================ */
    @PostMapping("/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crawlAll() {
        Map<String, Object> result = new HashMap<>();

        if (isRunning) {
            result.put("success", false);
            result.put("message", "현재 크롤링 진행 중입니다.");
            return ResponseEntity.ok(result);
        }

        try {
            isRunning = true;

            // 한국 주식
            lastStatus = "한국 주식 크롤링 중...";
            int koreaCount = koreaStockCrawlerService.crawlAndUpdateTop100Stocks();
            lastKoreaCount = koreaCount;

            // 미국 주식
            lastStatus = "미국 주식 크롤링 중...";
            int usCount = usStockCrawlerService.crawlAndUpdateTop100Stocks();
            lastUsCount = usCount;

            lastStatus = "전체 완료";
            result.put("success", true);
            result.put("message", "전체 크롤링 완료 (한국: " + koreaCount + "개, 미국: " + usCount + "개)");
            result.put("koreaCount", koreaCount);
            result.put("usCount", usCount);
            result.put("totalCount", koreaCount + usCount);

        } catch (Exception e) {
            lastStatus = "오류: " + e.getMessage();
            result.put("success", false);
            result.put("message", "전체 크롤링 실패: " + e.getMessage());
            log.error("전체 크롤링 실패", e);
        } finally {
            isRunning = false;
        }

        return ResponseEntity.ok(result);
    }

    /* ============================================================
     * GET /crawler/status → 크롤링 상태 확인
     * ============================================================ */
    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("isRunning",   isRunning);
        result.put("status",      lastStatus);
        result.put("koreaCount",  lastKoreaCount);
        result.put("usCount",     lastUsCount);
        result.put("totalCount",  lastKoreaCount + lastUsCount);
        return ResponseEntity.ok(result);
    }
}
