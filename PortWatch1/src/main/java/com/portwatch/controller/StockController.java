    package com.portwatch.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.portwatch.domain.StockVO;
import com.portwatch.service.StockService;

import java.util.List;

@Controller
@RequestMapping("/stock")
public class StockController {
    
    @Autowired
    private StockService stockService;
    
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
            
            model.addAttribute("stock", stock);
            model.addAttribute("stockCode", stockCode);
            
            return "stock/detail";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "종목 정보를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/stock/list";
        }
    }
}

    
