package com.portwatch.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.portwatch.domain.StockVO;
import com.portwatch.domain.NewsVO;
import com.portwatch.service.StockService;
import com.portwatch.service.NewsService;

import java.util.List;

@Controller
@RequestMapping("/stock")
public class StockController {
    
    @Autowired
    private StockService stockService;
    
    @Autowired
    private NewsService newsService;
    
    
        
    /**
     * 종목 목록 페이지
     */
    @GetMapping("/list")
    public String stockList(
            @RequestParam(defaultValue = "ALL") String market,
            @RequestParam(defaultValue = "") String keyword,
            Model model) {
        
        try {
            List<StockVO> stockList;
            
            if (!keyword.isEmpty()) {
                // 검색 모드
                if (market.equals("ALL")) {
                    stockList = stockService.searchStocks(keyword, null);
                } else {
                    stockList = stockService.searchStocks(keyword, market);
                }
            } else if (market.equals("ALL")) {
                // 전체 종목
                stockList = stockService.getAllStocksList();
            } else {
                // 시장별 종목 (KOSPI/KOSDAQ)
                stockList = stockService.getStocksByMarket(market);
            }
            
            model.addAttribute("stockList", stockList);
            model.addAttribute("market", market);
            model.addAttribute("keyword", keyword);
            model.addAttribute("stockCount", stockList.size());
            
            return "stock/list";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "종목 목록을 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("market", market);
            model.addAttribute("keyword", keyword);
            return "stock/list";
        }
    }
    
    /**
     * 종목 검색 페이지
     */
    @GetMapping("/search")
    public String searchPage() {
        return "stock/search";
    }
    
    /**
     * 종목 상세 페이지
     */
    @GetMapping("/detail/{stockCode}")
    public String detailPage(@PathVariable String stockCode, Model model) {
        try {
            StockVO stock = stockService.getStockByCode(stockCode);
            
            if (stock == null) {
                model.addAttribute("error", "종목을 찾을 수 없습니다.");
                return "redirect:/stock/list";
            }
            
            // 종목 정보
            model.addAttribute("stock", stock);
            model.addAttribute("stockCode", stockCode);
            
            // 종목 관련 뉴스 가져오기
            try {
                List<NewsVO> newsList = newsService.getNewsByStock(stockCode, 5);
                model.addAttribute("newsList", newsList);
            } catch (Exception e) {
                System.err.println("뉴스 로드 실패: " + e.getMessage());
                model.addAttribute("newsList", null);
            }
            
            return "stock/detail";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "종목 정보를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/stock/list";
        }
    }
}