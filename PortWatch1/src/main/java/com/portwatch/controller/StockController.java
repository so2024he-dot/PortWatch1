package com.portwatch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
 * ✅ 오류 해결: setStockId() 사용 (라인 174)
 * ✅ 한국/미국 100대 기업 크롤링 지원
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
    
    /**
     * 주식 목록 페이지
     */
    @GetMapping("/list")
    public String list(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String market,
            Model model) {
        
        log.info("주식 목록 조회: country={}, market={}", country, market);
        
        List<StockVO> stocks;
        
        if (country != null && market != null) {
            stocks = stockService.getStocksByCountryAndMarket(country, market);
        } else if (country != null) {
            stocks = stockService.getStocksByCountry(country);
        } else if (market != null) {
            stocks = stockService.getStocksByMarket(market);
        } else {
            stocks = stockService.getAllStocks();
        }
        
        model.addAttribute("stocks", stocks);
        model.addAttribute("selectedCountry", country);
        model.addAttribute("selectedMarket", market);
        
        return "stock/list";
    }
    
    /**
     * 주식 상세 페이지
     */
    @GetMapping("/detail/{stockCode}")
    public String detail(@PathVariable String stockCode, Model model) {
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
     * 한국 주식 검색
     */
    @GetMapping("/search/korea")
    public String searchKorea(@RequestParam String keyword, Model model) {
        log.info("한국 주식 검색: {}", keyword);
        
        List<StockVO> stocks = stockService.searchStocks(keyword);
        
        // 한국 주식만 필터링
        stocks = stocks.stream()
                .filter(s -> "KR".equals(s.getCountry()))
                .toList();
        
        model.addAttribute("stocks", stocks);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCountry", "KR");
        
        return "stock/list";
    }
    
    /**
     * 미국 주식 검색
     */
    @GetMapping("/search/us")
    public String searchUS(@RequestParam String keyword, Model model) {
        log.info("미국 주식 검색: {}", keyword);
        
        List<StockVO> stocks = stockService.searchStocks(keyword);
        
        // 미국 주식만 필터링
        stocks = stocks.stream()
                .filter(s -> "US".equals(s.getCountry()))
                .toList();
        
        model.addAttribute("stocks", stocks);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCountry", "US");
        
        return "stock/list";
    }
    
    /**
     * ✅ 주식 정보 수정 (라인 174 오류 해결)
     */
    @PostMapping("/update/{stockCode}")
    @ResponseBody
    public String updateStock(@PathVariable String stockCode, @RequestBody StockVO stock) {
        log.info("주식 정보 수정: {}", stockCode);
        
        try {
            // ✅ stockId 설정 (오류 해결)
            StockVO existingStock = stockService.getStockByCode(stockCode);
            if (existingStock != null) {
                stock.setStockId(existingStock.getStockId());
                stock.setStockCode(stockCode);
                
                stockService.updateStock(stock);
                log.info("주식 정보 수정 완료: {}", stockCode);
                return "SUCCESS";
            } else {
                log.warn("주식을 찾을 수 없습니다: {}", stockCode);
                return "NOT_FOUND";
            }
        } catch (Exception e) {
            log.error("주식 정보 수정 실패: {}", stockCode, e);
            return "FAIL";
        }
    }
    
    /**
     * 한국 100대 기업 크롤링
     */
    @PostMapping("/crawl/korea/top100")
    @ResponseBody
    public String crawlKoreaTop100() {
        log.info("한국 100대 기업 크롤링 시작");
        
        try {
            stockPriceUpdateService.updateAllStockPrices();
            log.info("한국 100대 기업 크롤링 완료");
            return "SUCCESS";
        } catch (Exception e) {
            log.error("한국 100대 기업 크롤링 실패", e);
            return "FAIL";
        }
    }
    
    /**
     * 미국 100대 기업 크롤링
     */
    @PostMapping("/crawl/us/top100")
    @ResponseBody
    public String crawlUSTop100() {
        log.info("미국 100대 기업 크롤링 시작");
        
        try {
            usStockPriceUpdateService.updateAllUSStockPrices();
            log.info("미국 100대 기업 크롤링 완료");
            return "SUCCESS";
        } catch (Exception e) {
            log.error("미국 100대 기업 크롤링 실패", e);
            return "FAIL";
        }
    }
    
    /**
     * 전체 크롤링 (한국 + 미국)
     */
    @PostMapping("/crawl/all")
    @ResponseBody
    public String crawlAll() {
        log.info("전체 크롤링 시작 (한국 + 미국)");
        
        try {
            // 한국 크롤링
            stockPriceUpdateService.updateAllStockPrices();
            log.info("한국 크롤링 완료");
            
            // 미국 크롤링
            usStockPriceUpdateService.updateAllUSStockPrices();
            log.info("미국 크롤링 완료");
            
            return "SUCCESS";
        } catch (Exception e) {
            log.error("전체 크롤링 실패", e);
            return "FAIL";
        }
    }
    
    /**
     * 거래량 상위 종목
     */
    @GetMapping("/top/volume")
    public String topVolume(@RequestParam(defaultValue = "10") int limit, Model model) {
        log.info("거래량 상위 {} 종목 조회", limit);
        
        List<StockVO> stocks = stockService.getStocksOrderByVolume(limit);
        
        model.addAttribute("stocks", stocks);
        model.addAttribute("sortType", "volume");
        
        return "stock/list";
    }
    
    /**
     * 상승률 상위 종목
     */
    @GetMapping("/top/gainers")
    public String topGainers(@RequestParam(defaultValue = "10") int limit, Model model) {
        log.info("상승률 상위 {} 종목 조회", limit);
        
        List<StockVO> stocks = stockService.getStocksOrderByChangeRate(limit);
        
        model.addAttribute("stocks", stocks);
        model.addAttribute("sortType", "gainers");
        
        return "stock/list";
    }
    
    /**
     * 하락률 상위 종목
     */
    @GetMapping("/top/losers")
    public String topLosers(@RequestParam(defaultValue = "10") int limit, Model model) {
        log.info("하락률 상위 {} 종목 조회", limit);
        
        List<StockVO> stocks = stockService.getStocksOrderByChangeRateDesc(limit);
        
        model.addAttribute("stocks", stocks);
        model.addAttribute("sortType", "losers");
        
        return "stock/list";
    }
    
    /**
     * 업종별 조회
     */
    @GetMapping("/industry/{industry}")
    public String byIndustry(@PathVariable String industry, Model model) {
        log.info("업종별 조회: {}", industry);
        
        List<StockVO> stocks = stockService.getStocksByIndustry(industry);
        
        model.addAttribute("stocks", stocks);
        model.addAttribute("selectedIndustry", industry);
        
        return "stock/list";
    }
    
    /**
     * 전체 업종 목록
     */
    @GetMapping("/industries")
    @ResponseBody
    public List<String> getAllIndustries() {
        return stockService.getAllIndustries();
    }
}
