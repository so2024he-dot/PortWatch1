package com.portwatch.controller;

import com.portwatch.domain.NewsVO;
import com.portwatch.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 뉴스 API 컨트롤러 (AJAX용)
 */
@RestController
@RequestMapping("/api/news")
public class NewsApiController {
    
    @Autowired
    private NewsService newsService;
    
    /**
     * 최신 뉴스 조회 (실시간 크롤링)
     */
    @GetMapping("/latest")
    public Map<String, Object> getLatestNews(@RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<NewsVO> newsList = newsService.fetchNaverFinanceNews(limit);
            result.put("success", true);
            result.put("newsList", newsList);
            result.put("count", newsList.size());
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "뉴스를 불러오는 중 오류가 발생했습니다.");
        }
        
        return result;
    }
    
    /**
     * 종목별 뉴스 조회
     */
    @GetMapping("/stock/{stockCode}")
    public Map<String, Object> getStockNews(@PathVariable String stockCode,
                                            @RequestParam(defaultValue = "5") int limit) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<NewsVO> newsList = newsService.getNewsByStock(stockCode, limit);
            result.put("success", true);
            result.put("newsList", newsList);
            result.put("count", newsList.size());
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "뉴스를 불러오는 중 오류가 발생했습니다.");
        }
        
        return result;
    }
    
    /**
     * DB에 저장된 최신 뉴스 조회
     */
    @GetMapping("/saved")
    public Map<String, Object> getSavedNews(@RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<NewsVO> newsList = newsService.getLatestNews(limit);
            result.put("success", true);
            result.put("newsList", newsList);
            result.put("count", newsList.size());
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "뉴스를 불러오는 중 오류가 발생했습니다.");
        }
        
        return result;
    }
}

    
