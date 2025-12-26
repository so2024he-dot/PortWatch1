package com.portwatch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.portwatch.domain.StockVO;
import com.portwatch.service.StockService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ✅ 주식 컨트롤러 (완벽 수정 - Exception 처리)
 * 
 * 수정 사항:
 * - Exception 처리 완료
 * - 생성자 주입
 * - 필터 로직 개선
 * 
 * @author PortWatch
 * @version 12.0 - Exception Fixed
 */
@Controller
@RequestMapping("/stock")
public class StockController {
    
    private final StockService stockService;
    
    /**
     * 생성자 주입 (권장)
     */
    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }
    
    /**
     * ✅ /stocks → /stock/list 리다이렉트
     */
    @GetMapping("s")
    public String redirectToList() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔀 /stocks → /stock/list 리다이렉트");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return "redirect:/stock/list";
    }
    
    /**
     * ✅ 주식 목록 조회
     */
    @GetMapping("/list")
    public String list(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String market,
            Model model) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 주식 목록 조회");
        System.out.println("  - country: " + (country != null ? country : "전체"));
        System.out.println("  - market: " + (market != null ? market : "전체"));
        
        try {
            List<StockVO> stocks = filterStocks(country, market);
            
            System.out.println("  - 조회 결과: " + stocks.size() + "개");
            System.out.println("✅ 주식 목록 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            model.addAttribute("stockList", stocks);
            model.addAttribute("country", country);
            model.addAttribute("market", market);
            
            return "stock/list";
            
        } catch (Exception e) {
            System.err.println("❌ 주식 목록 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "주식 목록을 불러오는데 실패했습니다: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * ✅ 필터 로직 (Exception 처리 완료)
     */
    private List<StockVO> filterStocks(String country, String market) {
        try {
            boolean hasCountry = country != null && !country.isEmpty();
            boolean hasMarket = market != null && !market.isEmpty();
            
            // Case 1: country와 market 모두 지정
            if (hasCountry && hasMarket) {
                System.out.println("  - 필터: country=" + country + " AND market=" + market);
                List<StockVO> allStocks = stockService.getAllStocks();
                return allStocks.stream()
                    .filter(s -> country.equalsIgnoreCase(s.getCountry()) 
                              && market.equalsIgnoreCase(s.getMarketType()))
                    .collect(Collectors.toList());
            }
            
            // Case 2: country만 지정
            if (hasCountry) {
                System.out.println("  - 필터: country=" + country);
                return stockService.getStocksByCountry(country);
            }
            
            // Case 3: market만 지정
            if (hasMarket) {
                System.out.println("  - 필터: market=" + market);
                return stockService.getStocksByMarket(market);
            }
            
            // Case 4: 필터 없음 (전체)
            System.out.println("  - 필터: 없음 (전체)");
            return stockService.getAllStocks();
            
        } catch (Exception e) {
            System.err.println("❌ 필터링 중 에러 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // 빈 리스트 반환
        }
    }
    
    /**
     * ✅ 주식 상세 조회 (ID)
     */
    @GetMapping("/detail/{stockId}")
    public String detail(@PathVariable Long stockId, Model model) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 주식 상세 조회 (ID)");
        System.out.println("  - stock_id: " + stockId);
        
        try {
            StockVO stock = stockService.getStockById(stockId);
            
            if (stock == null) {
                System.out.println("❌ 종목을 찾을 수 없습니다.");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                model.addAttribute("errorMessage", "종목을 찾을 수 없습니다.");
                return "error";
            }
            
            System.out.println("  - 종목명: " + stock.getStockName());
            System.out.println("  - 종목코드: " + stock.getStockCode());
            System.out.println("  - 현재가: " + stock.getCurrentPrice());
            System.out.println("✅ 주식 상세 조회 완료");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            model.addAttribute("stock", stock);
            return "stock/detail";
            
        } catch (Exception e) {
            System.err.println("❌ 주식 상세 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "주식 상세 정보를 불러오는데 실패했습니다: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * ✅ 주식 상세 조회 (코드)
     */
    @GetMapping("/code/{stockCode}")
    public String getByCode(@PathVariable String stockCode, Model model) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 주식 상세 조회 (종목코드)");
        System.out.println("  - stock_code: " + stockCode);
        
        try {
            StockVO stock = stockService.getStockByCode(stockCode);
            
            if (stock == null) {
                System.out.println("❌ 종목을 찾을 수 없습니다.");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                model.addAttribute("errorMessage", "종목을 찾을 수 없습니다: " + stockCode);
                return "error";
            }
            
            System.out.println("  - 종목명: " + stock.getStockName());
            System.out.println("  - stock_id: " + stock.getStockId());
            System.out.println("✅ 주식 조회 완료 → 상세 페이지 리다이렉트");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "redirect:/stock/detail/" + stock.getStockId();
            
        } catch (Exception e) {
            System.err.println("❌ 주식 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "주식 정보를 불러오는데 실패했습니다: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * ✅ 주식 메인 페이지
     */
    @GetMapping({"", "/"})
    public String stockMain(Model model) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 주식 메인 페이지 → 목록으로 리다이렉트");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return "redirect:/stock/list";
    }
}
