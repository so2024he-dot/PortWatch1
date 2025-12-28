package com.portwatch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * StockController - 주식 관리 컨트롤러
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * 주요 기능:
 * - 주식 목록 조회 (국가별, 시장별 필터링)
 * - 주식 상세 정보
 * - 주식 검색
 * - 매수 페이지
 * 
 * 수정 내역:
 * - 2025-12-29: URL 매핑 수정 (/stocks, /stock/buy)
 * - 슬래시 제거로 올바른 경로 매핑
 * 
 * @author PortWatch Team
 * @version 1.0
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Controller
@RequestMapping("/stock")
public class StockController {

    private static final Logger log = LoggerFactory.getLogger(StockController.class);

    @Autowired
    private StockService stockService;

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * /stocks → /stock/list 리다이렉트
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /stocks
     * 매핑: @GetMapping("stocks") → /stock/stocks (X)
     *       @GetMapping("stocks") → /stocks (O) - 슬래시 제거!
     * 
     * 설명: 
     * - 클래스 레벨 @RequestMapping("/stock")과 조합
     * - 메서드 레벨에서 슬래시로 시작하면 절대 경로
     * - 슬래시 없으면 상대 경로 (클래스 경로에 추가)
     * 
     * @return redirect:/stock/list
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @GetMapping("stocks")
    public String redirectStocks() {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🔀 /stocks 리다이렉트");
        log.info("  - 대상: /stock/list");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return "redirect:/stock/list";
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 주식 목록 조회 (필터링)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /stock/list
     * 파라미터:
     * - country: KR, US, ALL (선택)
     * - market: KOSPI, KOSDAQ, NASDAQ, NYSE, ALL (선택)
     * 
     * Model 속성:
     * - selectedCountry: 선택된 국가 (기본값: ALL)
     * - selectedMarket: 선택된 시장 (기본값: ALL)
     * - stocks: 주식 목록
     * 
     * JSP에서 사용:
     * ${selectedCountry} → JavaScript 변수 초기화
     * ${selectedMarket} → 필터 버튼 상태 관리
     * 
     * @param country 국가 코드
     * @param market 시장 코드
     * @param model Model 객체
     * @return stock/list.jsp
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @GetMapping("/list")
    public String list(@RequestParam(value = "country", required = false) String country,
                      @RequestParam(value = "market", required = false) String market,
                      Model model) {
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📊 주식 목록 조회");
        log.info("  - country: " + (country != null ? country : "전체"));
        log.info("  - market: " + (market != null ? market : "전체"));
        
        // 필터 로그
        if (country != null && !country.equals("ALL")) {
            log.info("  - 필터: country=" + country);
        }
        if (market != null && !market.equals("ALL")) {
            log.info("  - 필터: market=" + market);
        }
        if ((country == null || country.equals("ALL")) && 
            (market == null || market.equals("ALL"))) {
            log.info("  - 필터: 없음 (전체)");
        }
        
        // Model에 필터 정보 추가 (JSP에서 사용)
        model.addAttribute("selectedCountry", country != null ? country : "ALL");
        model.addAttribute("selectedMarket", market != null ? market : "ALL");
        
        // 주식 목록 조회
        List<StockVO> stocks = null;
        
        try {
            if (country != null && !country.equals("ALL") && 
                market != null && !market.equals("ALL")) {
                // 국가 + 시장 필터
                stocks = stockService.getStocksByCountryAndMarket(country, market);
                log.info("✅ 필터링된 주식 조회: " + stocks.size() + "건");
            } else if (country != null && !country.equals("ALL")) {
                // 국가 필터만
                stocks = stockService.getStocksByCountry(country);
                log.info("✅ 국가별 주식 조회: " + stocks.size() + "건");
            } else if (market != null && !market.equals("ALL")) {
                // 시장 필터만
                stocks = stockService.getStocksByMarket(market);
                log.info("✅ 시장별 주식 조회: " + stocks.size() + "건");
            } else {
                // 전체 조회
                stocks = stockService.getAllStocks();
                log.info("✅ 전체 주식 조회: " + stocks.size() + "건");
            }
            
            model.addAttribute("stocks", stocks);
            
        } catch (Exception e) {
            log.error("❌ 주식 목록 조회 실패", e);
            model.addAttribute("error", "주식 목록을 불러오는데 실패했습니다.");
        }
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return "stock/list";
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 주식 상세 정보
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /stock/detail
     * 파라미터: stockCode (종목 코드)
     * 
     * @param stockCode 종목 코드
     * @param model Model 객체
     * @return stock/detail.jsp
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @GetMapping("/detail")
    public String detail(@RequestParam("stockCode") String stockCode, Model model) {
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📈 주식 상세 정보 조회");
        log.info("  - stockCode: " + stockCode);
        
        try {
            StockVO stock = stockService.getStockByCode(stockCode);
            
            if (stock == null) {
                log.warn("⚠️ 주식을 찾을 수 없음");
                model.addAttribute("error", "주식 정보를 찾을 수 없습니다.");
                return "redirect:/stock/list";
            }
            
            model.addAttribute("stock", stock);
            log.info("✅ 주식 상세 정보 조회 완료");
            
        } catch (Exception e) {
            log.error("❌ 주식 상세 조회 실패", e);
            model.addAttribute("error", "주식 정보를 불러오는데 실패했습니다.");
            return "redirect:/stock/list";
        }
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return "stock/detail";
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 주식 매수 페이지
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /stock/buy
     * 매핑: @GetMapping("buy") → /stock/buy (O)
     * 
     * 파라미터: stockCode (종목 코드)
     * 
     * 기능:
     * 1. 로그인 체크
     * 2. 주식 정보 조회
     * 3. 포트폴리오 등록 페이지로 이동
     * 
     * @param stockCode 종목 코드
     * @param session HttpSession
     * @param model Model 객체
     * @return portfolio/create.jsp
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @GetMapping("buy")
    public String buyStock(@RequestParam("stockCode") String stockCode,
                          HttpSession session, Model model) {
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("💰 주식 매수 페이지");
        log.info("  - stockCode: " + stockCode);
        
        // 로그인 체크
        MemberVO member = (MemberVO) session.getAttribute("loginMember");
        if (member == null) {
            log.info("❌ 로그인 필요");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        log.info("✅ 로그인 회원: " + member.getMemberId());
        
        try {
            // 주식 정보 조회
            StockVO stock = stockService.getStockByCode(stockCode);
            
            if (stock == null) {
                log.warn("⚠️ 주식을 찾을 수 없음");
                model.addAttribute("error", "주식 정보를 찾을 수 없습니다.");
                return "redirect:/stock/list";
            }
            
            model.addAttribute("stock", stock);
            model.addAttribute("member", member);
            
            log.info("✅ 매수 페이지 데이터 준비 완료");
            log.info("  - 종목명: " + stock.getStockName());
            log.info("  - 현재가: " + stock.getCurrentPrice());
            
        } catch (Exception e) {
            log.error("❌ 주식 정보 조회 실패", e);
            model.addAttribute("error", "주식 정보를 불러오는데 실패했습니다.");
            return "redirect:/stock/list";
        }
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return "portfolio/create";
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 주식 검색 페이지
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * URL: GET /stock/search
     * 파라미터: keyword (검색어)
     * 
     * @param keyword 검색어
     * @param model Model 객체
     * @return stock/search.jsp
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false) String keyword,
                        Model model) {
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🔍 주식 검색");
        log.info("  - 검색어: " + keyword);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            try {
                List<StockVO> stocks = stockService.searchStocks(keyword.trim());
                model.addAttribute("stocks", stocks);
                model.addAttribute("keyword", keyword);
                log.info("✅ 검색 결과: " + stocks.size() + "건");
            } catch (Exception e) {
                log.error("❌ 주식 검색 실패", e);
                model.addAttribute("error", "검색에 실패했습니다.");
            }
        }
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return "stock/search";
    }
}
