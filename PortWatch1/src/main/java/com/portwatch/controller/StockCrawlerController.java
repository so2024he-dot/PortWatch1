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
 * 주식 크롤링 Controller
 * ══════════════════════════════════════════════════
 * GET  /crawler        → 크롤링 UI 페이지 (JSP)
 * POST /crawler/korea  → 한국 주식 크롤링
 * POST /crawler/us     → 미국 주식 크롤링
 * POST /crawler/all    → 전체 크롤링
 * GET  /crawler/status → 상태 조회 (Ajax 폴링)
 * ══════════════════════════════════════════════════
 */
@Slf4j
@Controller
@RequestMapping("/crawler")
public class StockCrawlerController {

    @Autowired
    private KoreaStockCrawlerService koreaService;

    @Autowired
    private USStockCrawlerService usService;

    private volatile boolean isRunning   = false;
    private volatile String  lastStatus  = "대기중";
    private volatile int     lastKrCount = 0;
    private volatile int     lastUsCount = 0;

    /** GET /crawler → views/crawler/stock-update.jsp */
    @GetMapping
    public String page() {
        return "crawler/stock-update";
    }

    /** POST /crawler/korea */
    @PostMapping("/korea")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crawlKorea() {
        Map<String, Object> res = new HashMap<>();
        if (isRunning) {
            res.put("success", false);
            res.put("message", "크롤링 진행 중입니다.");
            return ResponseEntity.ok(res);
        }
        try {
            isRunning   = true;
            lastStatus  = "한국 주식 크롤링 중...";
            int count   = koreaService.crawlAndUpdateTop100Stocks();
            lastKrCount = count;
            lastStatus  = "완료";
            res.put("success", true);
            res.put("message", "한국 주식 " + count + "개 AWS MySQL 저장 완료");
            res.put("count",   count);
        } catch (Exception e) {
            lastStatus = "오류";
            res.put("success", false);
            res.put("message", "실패: " + e.getMessage());
            log.error("한국 주식 크롤링 실패", e);
        } finally {
            isRunning = false;
        }
        return ResponseEntity.ok(res);
    }

    /** POST /crawler/us */
    @PostMapping("/us")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crawlUs() {
        Map<String, Object> res = new HashMap<>();
        if (isRunning) {
            res.put("success", false);
            res.put("message", "크롤링 진행 중입니다.");
            return ResponseEntity.ok(res);
        }
        try {
            isRunning   = true;
            lastStatus  = "미국 주식 크롤링 중...";
            int count   = usService.crawlAndUpdateTop100Stocks();
            lastUsCount = count;
            lastStatus  = "완료";
            res.put("success", true);
            res.put("message", "미국 주식 " + count + "개 AWS MySQL 저장 완료");
            res.put("count",   count);
        } catch (Exception e) {
            lastStatus = "오류";
            res.put("success", false);
            res.put("message", "실패: " + e.getMessage());
            log.error("미국 주식 크롤링 실패", e);
        } finally {
            isRunning = false;
        }
        return ResponseEntity.ok(res);
    }

    /** POST /crawler/all */
    @PostMapping("/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crawlAll() {
        Map<String, Object> res = new HashMap<>();
        if (isRunning) {
            res.put("success", false);
            res.put("message", "크롤링 진행 중입니다.");
            return ResponseEntity.ok(res);
        }
        try {
            isRunning = true;
            lastStatus = "한국 주식 크롤링 중...";
            int kr = koreaService.crawlAndUpdateTop100Stocks();
            lastKrCount = kr;

            lastStatus = "미국 주식 크롤링 중...";
            int us = usService.crawlAndUpdateTop100Stocks();
            lastUsCount = us;

            lastStatus = "전체 완료";
            res.put("success",    true);
            res.put("message",    "총 " + (kr + us) + "개 저장 완료");
            res.put("koreaCount", kr);
            res.put("usCount",    us);
            res.put("totalCount", kr + us);
        } catch (Exception e) {
            lastStatus = "오류";
            res.put("success", false);
            res.put("message", "실패: " + e.getMessage());
            log.error("전체 크롤링 실패", e);
        } finally {
            isRunning = false;
        }
        return ResponseEntity.ok(res);
    }

    /** GET /crawler/status */
    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> res = new HashMap<>();
        res.put("isRunning",  isRunning);
        res.put("status",     lastStatus);
        res.put("koreaCount", lastKrCount);
        res.put("usCount",    lastUsCount);
        res.put("totalCount", lastKrCount + lastUsCount);
        return ResponseEntity.ok(res);
    }
}
