package com.portwatch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.portwatch.domain.StockVO;
import com.portwatch.service.StockService;
import com.portwatch.service.StockPriceUpdateService;
import com.portwatch.service.USStockPriceUpdateService;

import lombok.extern.slf4j.Slf4j;

/**
 * StockController - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ Java 11 + Spring 5.0.7 완전 호환
 * ✅ AWS RDS MySQL 연동
 * ✅ 한국/미국 100대 기업 크롤링
 * ✅ REST API 지원
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@Controller
@RequestMapping("/stock")
public class StockController {
    
    @Autowired
    private StockService stockService;
    
    @Autowired
    private StockPriceUpdateService stockPriceUpdateService;
    
    @Autowired
    private USStockPriceUpdateService usStockPriceUpdateService;
    
    // ════════════════════════════════════════════════════════════
    // 페이지 컨트롤러
    // ════════════════════════════════════════════════════════════
    
    /**
     * 주식 목록 페이지 — 50개씩 페이지네이션 지원
     * ══════════════════════════════════════════════════════════════
     * ✅ [수정] page 파라미터 추가 → 50개씩 분할 표시
     *    1시간 단위로 순환 표시하려면 JS setInterval + 페이지 이동 사용
     * ══════════════════════════════════════════════════════════════
     */
    @GetMapping("/list")
    public String list(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String market,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        final int PAGE_SIZE = 50;
        log.info("주식 목록: country={}, market={}, industry={}, sortBy={}, page={}",
                country, market, industry, sortBy, page);

        List<StockVO> stocks;
        int totalCount = 0;

        try {
            // 필터링
            if (country != null && market != null) {
                stocks = stockService.getStocksByCountryAndMarket(country, market);
            } else if (country != null) {
                stocks = stockService.getStocksByCountry(country);
            } else if (market != null) {
                stocks = stockService.getStocksByMarket(market);
            } else if (industry != null) {
                stocks = stockService.getStocksByIndustry(industry);
            } else {
                stocks = stockService.getAllStocks();
            }
            if (stocks == null) stocks = java.util.Collections.emptyList();

            // 정렬
            if (!stocks.isEmpty()) {
                if ("volume".equals(sortBy)) {
                    List<StockVO> tmp = stockService.getStocksOrderByVolume(stocks.size());
                    if (tmp != null) stocks = tmp;
                } else if ("gainers".equals(sortBy)) {
                    List<StockVO> tmp = stockService.getStocksOrderByChangeRate(stocks.size());
                    if (tmp != null) stocks = tmp;
                } else if ("losers".equals(sortBy)) {
                    List<StockVO> tmp = stockService.getStocksOrderByChangeRateDesc(stocks.size());
                    if (tmp != null) stocks = tmp;
                }
            }

            totalCount = stocks.size();

            // ✅ 50개씩 페이지네이션 (서버사이드 slicing)
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
            if (page < 0) page = 0;
            if (page >= totalPages && totalPages > 0) page = totalPages - 1;

            int fromIndex = page * PAGE_SIZE;
            int toIndex   = Math.min(fromIndex + PAGE_SIZE, stocks.size());
            if (fromIndex < stocks.size()) {
                stocks = stocks.subList(fromIndex, toIndex);
            } else {
                stocks = java.util.Collections.emptyList();
            }

            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages",  totalPages);
            model.addAttribute("totalCount",  totalCount);
            model.addAttribute("pageSize",    PAGE_SIZE);

        } catch (Exception e) {
            log.error("주식 목록 조회 실패", e);
            stocks = java.util.Collections.emptyList();
        }

        model.addAttribute("stocks", stocks);
        model.addAttribute("selectedCountry", country);
        model.addAttribute("selectedMarket",  market);
        model.addAttribute("selectedIndustry", industry);
        model.addAttribute("sortBy", sortBy);

        java.util.List<String> industries = stockService.getAllIndustries();
        model.addAttribute("industries",
                industries != null ? industries : java.util.Collections.emptyList());

        return "stock/list";
    }
    
    /**
     * 주식 상세 페이지 - PathVariable 방식 (/stock/detail/005930)
     * ══════════════════════════════════════════════════════════════
     * ✅ [수정] PathVariable + QueryParam 두 가지 방식 모두 지원
     *    stock/list.jsp에서 ?stockCode=xxx QueryParam 방식으로 호출 → 404 발생
     *    → 두 방식 모두 같은 로직 처리
     * ══════════════════════════════════════════════════════════════
     */
    @GetMapping("/detail/{stockCode}")
    public String detailByPath(@PathVariable String stockCode, Model model) {
        return loadDetailPage(stockCode, model);
    }

    /**
     * 주식 상세 페이지 - QueryParam 방식 (/stock/detail?stockCode=005930)
     * ✅ [수정 핵심] stock/list.jsp onclick에서 이 URL 사용 → 기존 404 해결
     */
    @GetMapping("/detail")
    public String detailByParam(
            @RequestParam(required = false) String stockCode,
            Model model) {
        if (stockCode == null || stockCode.isEmpty()) {
            return "redirect:/stock/list";
        }
        return loadDetailPage(stockCode, model);
    }

    /**
     * 상세 페이지 공통 로직
     */
    private String loadDetailPage(String stockCode, Model model) {
        log.info("주식 상세 조회: {}", stockCode);
        StockVO stock = stockService.getStockByCode(stockCode);
        if (stock == null) {
            log.warn("주식 정보를 찾을 수 없습니다: {}", stockCode);
            return "redirect:/stock/list";
        }
        model.addAttribute("stock", stock);
        return "stock/detail";
    }

    /**
     * 주식 매수 페이지 (/stock/buy?stockCode=005930)
     * ══════════════════════════════════════════════════════════════
     * ✅ [신규] stock/list.jsp 매수 버튼 onclick에서 이 URL 호출
     *    기존: 매핑 없어서 404 발생
     *    수정: GET /stock/buy → stock/purchase.jsp 반환
     * ══════════════════════════════════════════════════════════════
     */
    @GetMapping("/buy")
    public String buyPage(
            @RequestParam(required = false) String stockCode,
            HttpSession session,
            Model model) {

        log.info("주식 매수 페이지: stockCode={}", stockCode);

        // 로그인 체크
        Object loginMember = session.getAttribute("loginMember");
        if (loginMember == null) loginMember = session.getAttribute("member");
        if (loginMember == null) {
            log.warn("매수 페이지: 로그인 필요");
            return "redirect:/member/login";
        }

        // 종목 코드로 주식 조회
        if (stockCode != null && !stockCode.isEmpty()) {
            StockVO stock = stockService.getStockByCode(stockCode);
            if (stock != null) {
                model.addAttribute("stock", stock);
                log.info("매수 대상 종목: {} ({})", stock.getStockName(), stock.getStockCode());
            } else {
                log.warn("종목 없음: {}", stockCode);
                model.addAttribute("errorMsg", "종목을 찾을 수 없습니다: " + stockCode);
            }
        }

        // 전체 종목 목록 (드롭다운용)
        model.addAttribute("stockList", stockService.getAllStocks());
        model.addAttribute("loginMember", loginMember);

        return "stock/purchase";
    }
    
    /**
     * 주식 검색 페이지
     */
    @GetMapping("/search")
    public String search(
            @RequestParam String keyword,
            @RequestParam(required = false) String country,
            Model model) {
        
        log.info("주식 검색: keyword={}, country={}", keyword, country);
        
        List<StockVO> stocks = stockService.searchStocks(keyword);
        
        // ✅ Java 11 호환: toList() → collect(Collectors.toList())
        if ("KR".equals(country)) {
            stocks = stocks.stream()
                    .filter(s -> "KR".equals(s.getCountry()))
                    .collect(Collectors.toList());
        } else if ("US".equals(country)) {
            stocks = stocks.stream()
                    .filter(s -> "US".equals(s.getCountry()))
                    .collect(Collectors.toList());
        }
        
        model.addAttribute("stocks", stocks);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCountry", country);
        
        return "stock/list";
    }
    
    // ════════════════════════════════════════════════════════════
    // REST API - 주식 조회
    // ════════════════════════════════════════════════════════════
    
    /**
     * 전체 주식 조회 API
     */
    @GetMapping("/api/stocks")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllStocksApi() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> stocks = stockService.getAllStocks();
            
            response.put("success", true);
            response.put("count", stocks.size());
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("주식 조회 실패", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            
            // ✅ Spring 5.0.7 호환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 국가별 주식 조회 API
     */
    @GetMapping("/api/stocks/country/{country}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStocksByCountryApi(
            @PathVariable String country) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> stocks = stockService.getStocksByCountry(country);
            
            response.put("success", true);
            response.put("country", country);
            response.put("count", stocks.size());
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("국가별 주식 조회 실패: {}", country, e);
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 시장별 주식 조회 API
     */
    @GetMapping("/api/stocks/market/{market}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStocksByMarketApi(
            @PathVariable String market) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> stocks = stockService.getStocksByMarket(market);
            
            response.put("success", true);
            response.put("market", market);
            response.put("count", stocks.size());
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("시장별 주식 조회 실패: {}", market, e);
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ════════════════════════════════════════════════════════════
    // ✅ 크롤링 API - 한국/미국 100대 기업
    // ════════════════════════════════════════════════════════════
    
    /**
     * ✅ 한국 100대 기업 크롤링
     */
    @PostMapping("/crawl/korea/top100")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crawlKoreaTop100() {
        log.info("════════════════════════════════════════");
        log.info("한국 100대 기업 크롤링 시작");
        log.info("════════════════════════════════════════");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            long startTime = System.currentTimeMillis();
            
            // 한국 주식 크롤링
            stockPriceUpdateService.updateAllStockPrices();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.info("✅ 한국 100대 기업 크롤링 완료 ({}ms)", duration);
            
            response.put("success", true);
            response.put("message", "한국 100대 기업 크롤링 완료");
            response.put("duration", duration);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ 한국 100대 기업 크롤링 실패", e);
            
            response.put("success", false);
            response.put("message", "크롤링 실패: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * ✅ 미국 100대 기업 크롤링
     */
    @PostMapping("/crawl/us/top100")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crawlUSTop100() {
        log.info("════════════════════════════════════════");
        log.info("미국 100대 기업 크롤링 시작");
        log.info("════════════════════════════════════════");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            long startTime = System.currentTimeMillis();
            
            // 미국 주식 크롤링
            usStockPriceUpdateService.updateAllUSStockPrices();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.info("✅ 미국 100대 기업 크롤링 완료 ({}ms)", duration);
            
            response.put("success", true);
            response.put("message", "미국 100대 기업 크롤링 완료");
            response.put("duration", duration);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ 미국 100대 기업 크롤링 실패", e);
            
            response.put("success", false);
            response.put("message", "크롤링 실패: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * ✅ 전체 크롤링 (한국 + 미국)
     */
    @PostMapping("/crawl/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crawlAll() {
        log.info("════════════════════════════════════════");
        log.info("전체 크롤링 시작 (한국 + 미국)");
        log.info("════════════════════════════════════════");
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> results = new HashMap<>();
        
        try {
            long totalStartTime = System.currentTimeMillis();
            
            // 1. 한국 크롤링
            log.info("1/2 한국 100대 기업 크롤링 시작...");
            long koreaStart = System.currentTimeMillis();
            stockPriceUpdateService.updateAllStockPrices();
            long koreaDuration = System.currentTimeMillis() - koreaStart;
            log.info("✅ 한국 크롤링 완료 ({}ms)", koreaDuration);
            
            Map<String, Object> koreaResult = new HashMap<>();
            koreaResult.put("success", true);
            koreaResult.put("duration", koreaDuration);
            results.put("korea", koreaResult);
            
            // 잠시 대기 (서버 부하 분산)
            Thread.sleep(2000);
            
            // 2. 미국 크롤링
            log.info("2/2 미국 100대 기업 크롤링 시작...");
            long usStart = System.currentTimeMillis();
            usStockPriceUpdateService.updateAllUSStockPrices();
            long usDuration = System.currentTimeMillis() - usStart;
            log.info("✅ 미국 크롤링 완료 ({}ms)", usDuration);
            
            Map<String, Object> usResult = new HashMap<>();
            usResult.put("success", true);
            usResult.put("duration", usDuration);
            results.put("us", usResult);
            
            long totalDuration = System.currentTimeMillis() - totalStartTime;
            
            log.info("════════════════════════════════════════");
            log.info("✅ 전체 크롤링 완료!");
            log.info("  - 한국: {}ms", koreaDuration);
            log.info("  - 미국: {}ms", usDuration);
            log.info("  - 총 시간: {}ms", totalDuration);
            log.info("════════════════════════════════════════");
            
            response.put("success", true);
            response.put("message", "전체 크롤링 완료");
            response.put("totalDuration", totalDuration);
            response.put("results", results);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ 전체 크롤링 실패", e);
            
            response.put("success", false);
            response.put("message", "크롤링 실패: " + e.getMessage());
            response.put("results", results);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 특정 주식 크롤링
     */
    @PostMapping("/crawl/{stockCode}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crawlStock(@PathVariable String stockCode) {
        log.info("특정 주식 크롤링: {}", stockCode);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            StockVO stock = stockService.getStockByCode(stockCode);
            
            if (stock == null) {
                response.put("success", false);
                response.put("message", "주식을 찾을 수 없습니다: " + stockCode);
                return ResponseEntity.badRequest().body(response);
            }
            
            // 국가에 따라 크롤링
            if ("KR".equals(stock.getCountry())) {
                stockPriceUpdateService.updateStockPrice(stockCode);
            } else if ("US".equals(stock.getCountry())) {
                usStockPriceUpdateService.updateUSStockPrice(stockCode);
            }
            
            log.info("✅ {} 크롤링 완료", stockCode);
            
            response.put("success", true);
            response.put("message", "크롤링 완료");
            response.put("stockCode", stockCode);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ {} 크롤링 실패", stockCode, e);
            
            response.put("success", false);
            response.put("message", "크롤링 실패: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ════════════════════════════════════════════════════════════
    // REST API - 주식 관리
    // ════════════════════════════════════════════════════════════
    
    /**
     * ✅ 주식 정보 수정
     */
    @PostMapping("/update/{stockCode}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStock(
            @PathVariable String stockCode,
            @RequestBody StockVO stock) {
        
        log.info("주식 정보 수정: {}", stockCode);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            StockVO existingStock = stockService.getStockByCode(stockCode);
            
            if (existingStock != null) {
                stock.setStockId(existingStock.getStockId());
                stock.setStockCode(stockCode);
                
                stockService.updateStock(stock);
                
                log.info("✅ 주식 정보 수정 완료: {}", stockCode);
                
                response.put("success", true);
                response.put("message", "수정 완료");
                response.put("stock", stock);
                
                return ResponseEntity.ok(response);
            } else {
                log.warn("주식을 찾을 수 없습니다: {}", stockCode);
                
                response.put("success", false);
                response.put("message", "주식을 찾을 수 없습니다");
                
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error("주식 정보 수정 실패: {}", stockCode, e);
            
            response.put("success", false);
            response.put("message", "수정 실패: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ════════════════════════════════════════════════════════════
    // REST API - 주식 순위
    // ════════════════════════════════════════════════════════════
    
    /**
     * 거래량 상위 종목
     */
    @GetMapping("/api/top/volume")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTopVolume(
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> stocks = stockService.getStocksOrderByVolume(limit);
            
            response.put("success", true);
            response.put("type", "volume");
            response.put("count", stocks.size());
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("거래량 상위 조회 실패", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 상승률 상위 종목
     */
    @GetMapping("/api/top/gainers")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTopGainers(
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> stocks = stockService.getStocksOrderByChangeRate(limit);
            
            response.put("success", true);
            response.put("type", "gainers");
            response.put("count", stocks.size());
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("상승률 상위 조회 실패", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 하락률 상위 종목
     */
    @GetMapping("/api/top/losers")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTopLosers(
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> stocks = stockService.getStocksOrderByChangeRateDesc(limit);
            
            response.put("success", true);
            response.put("type", "losers");
            response.put("count", stocks.size());
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("하락률 상위 조회 실패", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 업종 목록
     */
    @GetMapping("/api/industries")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllIndustries() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String> industries = stockService.getAllIndustries();
            
            response.put("success", true);
            response.put("count", industries.size());
            response.put("industries", industries);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("업종 목록 조회 실패", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
