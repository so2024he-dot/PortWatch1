package com.portwatch.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.portwatch.service.StockService;

@Controller
@RequestMapping("/stock")
public class StockController {
    
    @Autowired
    private StockService stockService;
    
    @GetMapping("/search")
    public String searchPage() {
        return "stock/search";
    }
    
    @GetMapping("/detail/{stockCode}")
    public String detailPage(@PathVariable String stockCode, Model model) {
        try {
            model.addAttribute("stockCode", stockCode);
            return "stock/detail";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/stock/search";
        }
    }
}
