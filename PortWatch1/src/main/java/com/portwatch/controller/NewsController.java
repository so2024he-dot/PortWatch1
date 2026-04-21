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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.NewsVO;
import com.portwatch.domain.StockVO;
import com.portwatch.service.NewsService;
import com.portwatch.service.StockService;

/**
 * 뉴스 컨트롤러
 * 
 * @author PortWatch
 * @version 2.0 (자동 새로고침 추가)
 */
@Controller
@RequestMapping("/news")
public class NewsController {
    
    @Autowired(required = false)
    private NewsService newsService;

    @Autowired(required = false)
    private StockService stockService;
    
    /**
     * 뉴스 목록 페이지
     * GET /news/list
     */
    @GetMapping("/list")
    public String newsList(HttpSession session, Model model) {

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📰 뉴스 목록 조회");

        List<NewsVO> newsList = java.util.Collections.emptyList();
        String dbError = null;

        if (newsService == null) {
            dbError = "뉴스 서비스를 사용할 수 없습니다. DB 연결을 확인해 주세요.";
        } else {
            try {
                List<NewsVO> fetched = newsService.getRecentNews(50);
                if (fetched != null) {
                    newsList = fetched;
                }
                System.out.println("  - 뉴스 개수: " + newsList.size());
                System.out.println("✅ 뉴스 목록 조회 완료!");
            } catch (Exception e) {
                e.printStackTrace();
                dbError = "뉴스 데이터를 불러오지 못했습니다. (DB 연결 확인 필요: " + e.getMessage() + ")";
                System.out.println("⚠️ 뉴스 조회 실패 (빈 목록으로 표시): " + e.getMessage());
            }
        }

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        model.addAttribute("newsList", newsList);
        if (dbError != null) {
            model.addAttribute("dbError", dbError);
        }

        return "news/list";
    }
    
    /**
     * 뉴스 새로고침 API (AJAX)
     * POST /news/refresh
     */
    @PostMapping("/refresh")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> refreshNews() {

        Map<String, Object> response = new HashMap<>();

        // ✅ newsService null 체크 (DB 연결 실패 시 NPE 방지)
        if (newsService == null) {
            response.put("success", false);
            response.put("message", "뉴스 서비스를 사용할 수 없습니다. DB 연결을 먼저 확인하세요.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }

        try {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🔄 뉴스 자동 새로고침");

            // 뉴스 크롤링 실행
            int count = newsService.crawlAndSaveNews();
            
            System.out.println("  - 수집된 뉴스: " + count + "개");
            System.out.println("✅ 뉴스 새로고침 완료!");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            response.put("success", true);
            response.put("message", "뉴스를 새로고침했습니다.");
            response.put("count", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            
            System.out.println("❌ 뉴스 새로고침 실패: " + e.getMessage());
            
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 뉴스 상세 페이지
     * GET /news/detail/{newsId}
     */
    @GetMapping("/detail/{newsId}")
    public String newsDetail(Long newsId, Model model) {
        
        try {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📰 뉴스 상세 조회");
            System.out.println("  - 뉴스 ID: " + newsId);
            
            // 뉴스 조회
            NewsVO news = newsService.getNewsById(newsId);
            
            if (news == null) {
                model.addAttribute("error", "뉴스를 찾을 수 없습니다.");
                return "error/404";
            }
            
            System.out.println("  - 뉴스 제목: " + news.getTitle());
            System.out.println("✅ 뉴스 상세 조회 완료!");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            model.addAttribute("news", news);
            
            return "news/detail";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * 종목별 뉴스 조회 API
     * GET /news/api/stock/{stockCode}
     */
    @GetMapping("/api/stock/{stockCode}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStockNews(@org.springframework.web.bind.annotation.PathVariable String stockCode) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (newsService == null) {
                response.put("success", false);
                response.put("news", java.util.Collections.emptyList());
                response.put("count", 0);
                response.put("message", "뉴스 서비스 사용 불가");
                return ResponseEntity.ok(response);
            }

            // 종목 뉴스 조회
            List<NewsVO> newsList = newsService.getNewsByStockCode(stockCode, 20);

            response.put("success", true);
            response.put("news", newsList);      // ✅ JS 접근키 "news" 통일
            response.put("count", newsList.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("news", java.util.Collections.emptyList());
            response.put("count", 0);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * ✅ [신규] 종목별 뉴스 자동 크롤링 API
     * GET /news/api/stock/{stockCode}/crawl
     *
     * 동작:
     * 1. 종목 코드로 STOCK 테이블에서 종목명 조회
     * 2. NaverNewsCrawler로 종목명 키워드 뉴스 검색
     * 3. DB 저장 후 뉴스 목록 반환
     */
    @GetMapping("/api/stock/{stockCode}/crawl")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crawlStockNews(
            @org.springframework.web.bind.annotation.PathVariable String stockCode) {

        Map<String, Object> response = new HashMap<>();

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🕷️ 종목별 자동 뉴스 크롤링: " + stockCode);

        try {
            if (newsService == null) {
                response.put("success", false);
                response.put("message", "뉴스 서비스 사용 불가 (DB 연결 확인)");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
            }

            // 1. 종목명 조회
            String stockName = stockCode; // 기본값: 코드 그대로 사용
            if (stockService != null) {
                try {
                    StockVO stock = stockService.getStockByCode(stockCode);
                    if (stock != null && stock.getStockName() != null && !stock.getStockName().isEmpty()) {
                        stockName = stock.getStockName();
                    }
                } catch (Exception ex) {
                    System.out.println("⚠️ 종목 정보 조회 실패, 코드로 검색: " + ex.getMessage());
                }
            }

            System.out.println("  - 종목명: " + stockName);

            // 2. 크롤링 실행
            int savedCount = newsService.crawlAndSaveByStockCode(stockCode, stockName);

            // 3. 크롤링 후 최신 뉴스 반환
            List<NewsVO> newsList = newsService.getNewsByStockCode(stockCode, 20);

            System.out.println("  - 신규 저장: " + savedCount + "건");
            System.out.println("  - 조회 결과: " + newsList.size() + "건");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            response.put("success", true);
            response.put("savedCount", savedCount);
            response.put("news", newsList);
            response.put("count", newsList.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ 자동 크롤링 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            response.put("success", false);
            response.put("news", java.util.Collections.emptyList());
            response.put("count", 0);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
