package com.portwatch.controller;

import com.portwatch.domain.NewsVO;
import com.portwatch.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 뉴스 컨트롤러
 */
@Controller
@RequestMapping("/news")
public class NewsController {
    
    @Autowired
    private NewsService newsService;
    
    /**
     * 뉴스 목록 페이지
     */
    @GetMapping("/list")
    public String newsList(@RequestParam(defaultValue = "20") int limit, Model model) {
        try {
            // 실시간 뉴스 크롤링
            List<NewsVO> newsList = newsService.fetchNaverFinanceNews(limit);
            model.addAttribute("newsList", newsList);
            model.addAttribute("newsCount", newsList.size());
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "뉴스를 불러오는 중 오류가 발생했습니다.");
        }
        
        return "news/list";
    }
    
    /**
     * 종목별 뉴스
     */
    @GetMapping("/stock/{stockCode}")
    public String stockNews(@PathVariable String stockCode, Model model) {
        try {
            List<NewsVO> newsList = newsService.getNewsByStock(stockCode, 10);
            model.addAttribute("newsList", newsList);
            model.addAttribute("stockCode", stockCode);
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "뉴스를 불러오는 중 오류가 발생했습니다.");
        }
        
        return "news/stock";
    }
}

    
