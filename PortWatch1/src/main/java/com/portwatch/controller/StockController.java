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
import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.StockVO;
import com.portwatch.service.StockService;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * StockController - 완전 수정 버전
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * 수정 사항:
 * 1. ✅ 세션 키 통일: loginMember → member
 * 2. ✅ 매수 버튼 완벽 연동
 * 3. ✅ portfolio/create로 이동 시 portfolioVO 제공
 * 
 * @version 2.0
 */
@Controller
@RequestMapping("/stock")
public class StockController {

    private static final Logger log = LoggerFactory.getLogger(StockController.class);

    @Autowired
    private StockService stockService;

    /**
     * ✅ 주식 목록 조회
     * URL: GET /stock/list
     */
    @GetMapping("/list")
    public String list(@RequestParam(value = "country", required = false, defaultValue = "ALL") String country,
                      @RequestParam(value = "market", required = false, defaultValue = "ALL") String market,
                      Model model, HttpSession session) {
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📈 주식 목록 조회");
        log.info("  - 국가: " + country);
        log.info("  - 시장: " + market);
        
        try {
            // ✅ 세션 체크 (선택사항)
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member != null) {
                model.addAttribute("loginMember", member);  // JSP 호환성
                log.info("  - 회원: " + member.getMemberId());
            }
            
            List<StockVO> stocks = null;
            
            // 필터링 로직
            if ("ALL".equals(country) && "ALL".equals(market)) {
                stocks = stockService.getAllStocks();
            } else if (!"ALL".equals(country) && "ALL".equals(market)) {
                stocks = stockService.getStocksByCountry(country);
            } else if ("ALL".equals(country) && !"ALL".equals(market)) {
                stocks = stockService.getStocksByMarket(market);
            } else {
                stocks = stockService.getStocksByCountryAndMarket(country, market);
            }
            
            model.addAttribute("stocks", stocks);
            model.addAttribute("selectedCountry", country);
            model.addAttribute("selectedMarket", market);
            
            log.info("✅ 주식 목록 조회 완료: " + (stocks != null ? stocks.size() : 0) + "개");
            
        } catch (Exception e) {
            log.error("❌ 주식 목록 조회 실패", e);
            model.addAttribute("stocks", List.of());
            model.addAttribute("error", "주식 목록을 불러오는데 실패했습니다.");
        }
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return "stock/list";
    }

    /**
     * ✅ 주식 상세 조회
     * URL: GET /stock/detail
     */
    @GetMapping("/detail")
    public String detail(@RequestParam("stockCode") String stockCode,
                        Model model, HttpSession session) {
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📊 주식 상세 조회");
        log.info("  - stockCode: " + stockCode);
        
        try {
            // ✅ 세션 체크
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member != null) {
                model.addAttribute("loginMember", member);
            }
            
            StockVO stock = stockService.getStockByCode(stockCode);
            
            if (stock == null) {
                log.warn("⚠️ 주식을 찾을 수 없음");
                return "redirect:/stock/list";
            }
            
            model.addAttribute("stock", stock);
            log.info("✅ 주식 상세 조회 완료: " + stock.getStockName());
            
        } catch (Exception e) {
            log.error("❌ 주식 상세 조회 실패", e);
            return "redirect:/stock/list";
        }
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return "stock/detail";
    }

    /**
     * ✅ 주식 매수 페이지 (완전 수정!)
     * URL: GET /stock/buy
     * 
     * 수정 내용:
     * 1. loginMember → member로 통일
     * 2. portfolioVO 추가 (BindingResult 에러 방지)
     * 3. stock 정보 제공
     */
    @GetMapping("/buy")
    public String buyStock(@RequestParam("stockCode") String stockCode,
                          HttpSession session, Model model) {
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("💰 주식 매수 페이지");
        log.info("  - stockCode: " + stockCode);
        
        // ✅ 세션 체크 (member로 통일!)
        MemberVO member = (MemberVO) session.getAttribute("member");
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
            
            // ✅ portfolioVO 생성 및 초기값 설정
            PortfolioVO portfolioVO = new PortfolioVO();
            portfolioVO.setStockId(stock.getStockId());
            portfolioVO.setStockCode(stock.getStockCode());
            portfolioVO.setMemberId(member.getMemberId());
            
            // ✅ Model에 추가 (BindingResult 에러 방지)
            model.addAttribute("portfolioVO", portfolioVO);
            model.addAttribute("stock", stock);
            model.addAttribute("member", member);
            model.addAttribute("loginMember", member);  // JSP 호환성
            
            // ✅ 전체 종목 리스트 제공 (선택 변경 가능하도록)
            List<StockVO> stockList = stockService.getAllStocks();
            model.addAttribute("stockList", stockList);
            
            log.info("✅ 매수 페이지 데이터 준비 완료");
            log.info("  - 종목명: " + stock.getStockName());
            log.info("  - 현재가: " + stock.getCurrentPrice());
            log.info("  - portfolioVO 추가 완료");
            
        } catch (Exception e) {
            log.error("❌ 주식 정보 조회 실패", e);
            model.addAttribute("error", "주식 정보를 불러오는데 실패했습니다.");
            return "redirect:/stock/list";
        }
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return "portfolio/create";
    }

    /**
     * ✅ 주식 검색
     * URL: GET /stock/search
     */
    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false) String keyword,
                        Model model, HttpSession session) {
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("🔍 주식 검색");
        log.info("  - 검색어: " + keyword);
        
        // 세션 체크
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member != null) {
            model.addAttribute("loginMember", member);
        }
        
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
