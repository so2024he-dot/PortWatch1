package com.portwatch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.StockVO;
import com.portwatch.service.StockService;
import com.portwatch.service.WatchlistService;

/**
 * 종목 컨트롤러
 * 
 * @author PortWatch Team
 * @version 2.0
 */
@Controller
@RequestMapping("/stock")
public class StockController {
    
    @Autowired
    private StockService stockService;
    
    @Autowired
    private WatchlistService watchlistService;
    
    /**
     * 종목 목록 페이지
     */
    @GetMapping("/list")
    public String stockList(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String marketType,
            Model model) {
        
        try {
            // 기본값: 한국 주식
            if (country == null) {
                country = "KR";
            }
            
            List<StockVO> stockList;
            
            if (marketType != null && !marketType.isEmpty()) {
                // 시장별 조회
                stockList = stockService.getStocksByMarket(marketType);
            } else if ("KR".equals(country)) {
                // 한국 전체
                stockList = stockService.getKoreanStocks();
            } else {
                // 미국 전체
                stockList = stockService.getUSStocks();
            }
            
            model.addAttribute("stockList", stockList);
            model.addAttribute("country", country);
            model.addAttribute("marketType", marketType);
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "종목 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "stock/list";
    }
    
    /**
     * 종목 상세 페이지
     */
    @GetMapping("/detail/{stockId}")
    public String stockDetail(
            @PathVariable Integer stockId,
            HttpSession session,
            Model model) {
        
        try {
            // 종목 정보 조회
            StockVO stock = stockService.getStockById(stockId);
            
            if (stock == null) {
                model.addAttribute("errorMessage", "종목을 찾을 수 없습니다.");
                return "redirect:/stock/list";
            }
            
            model.addAttribute("stock", stock);
            
            // 로그인 여부 확인
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            
            if (loginMember != null) {
                // 관심종목 등록 여부 확인
                String memberId = loginMember.getMemberId();
                boolean isInWatchlist = watchlistService.checkWatchlist(memberId, stockId);
                model.addAttribute("isInWatchlist", isInWatchlist);
            }
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "종목 상세 조회 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/stock/list";
        }
        
        return "stock/detail";
    }
    
    /**
     * 종목 검색 (AJAX)
     */
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchStock(
            @RequestParam String keyword) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> results = stockService.searchStocks(keyword);
            
            response.put("success", true);
            response.put("stocks", results);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 주식 매수 페이지
     */
    @GetMapping("/purchase")
    public String purchasePage(
            @RequestParam(required = false) Integer stockId,
            HttpSession session,
            Model model) {
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        try {
            if (stockId != null) {
                StockVO stock = stockService.getStockById(stockId);
                model.addAttribute("stock", stock);
            }
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "stock/purchase";
    }
    
    /**
     * 종목 정보 조회 (AJAX)
     */
    @GetMapping("/info/{stockId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStockInfo(
            @PathVariable Integer stockId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            StockVO stock = stockService.getStockById(stockId);
            
            if (stock == null) {
                response.put("success", false);
                response.put("message", "종목을 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("stock", stock);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 한국 주식 목록 (AJAX)
     */
    @GetMapping("/korean")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getKoreanStocks() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> stocks = stockService.getKoreanStocks();
            
            response.put("success", true);
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 미국 주식 목록 (AJAX)
     */
    @GetMapping("/us")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUSStocks() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> stocks = stockService.getUSStocks();
            
            response.put("success", true);
            response.put("stocks", stocks);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 시장별 종목 목록 (AJAX)
     */
    @GetMapping("/market/{marketType}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStocksByMarket(
            @PathVariable String marketType) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<StockVO> stocks = stockService.getStocksByMarket(marketType);
            
            response.put("success", true);
            response.put("stocks", stocks);
            response.put("marketType", marketType);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
