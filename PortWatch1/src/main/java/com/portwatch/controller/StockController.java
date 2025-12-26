package com.portwatch.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.StockVO;
import com.portwatch.service.StockService;

/**
 * ✅ 주식 컨트롤러 (완전 수정)
 * 
 * URL 매핑:
 * - /stock/list → 주식 목록 페이지
 * - /stock/detail → 주식 상세 페이지
 * 
 * @author PortWatch Team
 * @version 2.0 - URL 매핑 수정
 */
@Controller
@RequestMapping("/stock")  // ✅ "/stocks" → "/stock"으로 변경
public class StockController {
    
    @Autowired(required = false)
    private StockService stockService;
    
    /**
     * ✅ 주식 목록 페이지
     * GET /stock/list
     */
    @GetMapping("/list")
    public String stockList(
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "market", required = false) String market,
            HttpSession session,
            Model model) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 주식 목록 조회");
        System.out.println("  - country: " + country);
        System.out.println("  - market: " + market);
        
        // 로그인 체크 (선택사항)
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        try {
            List<StockVO> stockList;
            
            if (stockService != null) {
                // 필터링 조건에 따라 주식 조회
                if (country != null && !country.isEmpty()) {
                    stockList = stockService.getStocksByCountry(country);
                    System.out.println("  - 국가 필터: " + country);
                } else if (market != null && !market.isEmpty()) {
                    stockList = stockService.getStocksByMarket(market);
                    System.out.println("  - 시장 필터: " + market);
                } else {
                    stockList = stockService.getAllStocks();
                    System.out.println("  - 전체 조회");
                }
                
                System.out.println("  - 조회된 주식 수: " + stockList.size());
                model.addAttribute("stockList", stockList);
                
            } else {
                System.out.println("⚠️ StockService is null");
                model.addAttribute("stockList", new java.util.ArrayList<>());
                model.addAttribute("errorMessage", "StockService를 사용할 수 없습니다.");
            }
            
            System.out.println("✅ 주식 목록 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            // 필터 정보
            model.addAttribute("selectedCountry", country);
            model.addAttribute("selectedMarket", market);
            model.addAttribute("loginMember", loginMember);
            
            return "stock/list";
            
        } catch (Exception e) {
            System.err.println("❌ 주식 목록 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "주식 목록을 조회하는 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("stockList", new java.util.ArrayList<>());
            
            return "stock/list";
        }
    }
    
    /**
     * 주식 상세 페이지
     * GET /stock/detail
     */
    @GetMapping("/detail")
    public String stockDetail(
            @RequestParam("code") String stockCode,
            HttpSession session,
            Model model) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📈 주식 상세 조회: " + stockCode);
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        try {
            if (stockService != null) {
                StockVO stock = stockService.getStockByCode(stockCode);
                
                if (stock == null) {
                    System.out.println("⚠️ 주식을 찾을 수 없습니다: " + stockCode);
                    model.addAttribute("errorMessage", "주식을 찾을 수 없습니다.");
                    return "error";
                }
                
                System.out.println("  - 주식명: " + stock.getStockName());
                System.out.println("  - 현재가: " + stock.getCurrentPrice());
                System.out.println("✅ 주식 상세 조회 완료");
                
                model.addAttribute("stock", stock);
                
            } else {
                System.out.println("⚠️ StockService is null");
                model.addAttribute("errorMessage", "StockService를 사용할 수 없습니다.");
                return "error";
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            model.addAttribute("loginMember", loginMember);
            return "stock/detail";
            
        } catch (Exception e) {
            System.err.println("❌ 주식 상세 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "주식 정보를 조회하는 중 오류가 발생했습니다.");
            return "error";
        }
    }
    
    /**
     * ✅ 주식 검색 (선택사항)
     * GET /stock/search
     */
    @GetMapping("/search")
    public String searchStock(
            @RequestParam("keyword") String keyword,
            HttpSession session,
            Model model) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 주식 검색: " + keyword);
        
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        try {
            if (stockService != null) {
                List<StockVO> stockList = stockService.searchStocks(keyword);
                
                System.out.println("  - 검색 결과: " + stockList.size() + "개");
                System.out.println("✅ 주식 검색 완료");
                
                model.addAttribute("stockList", stockList);
                model.addAttribute("keyword", keyword);
                
            } else {
                System.out.println("⚠️ StockService is null");
                model.addAttribute("stockList", new java.util.ArrayList<>());
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            model.addAttribute("loginMember", loginMember);
            return "stock/list";
            
        } catch (Exception e) {
            System.err.println("❌ 주식 검색 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "주식 검색 중 오류가 발생했습니다.");
            model.addAttribute("stockList", new java.util.ArrayList<>());
            
            return "stock/list";
        }
    }
}
