package com.portwatch.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.StockPriceVO;
import com.portwatch.service.PortfolioService;
import com.portwatch.service.StockPriceUpdateService;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * PORTFOLIO API CONTROLLER - 완전 개선 버전
 * Frontend와 100% 매칭되는 JSON 응답
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@RestController
@RequestMapping("/api/portfolio")
@Log4j
public class PortfolioApiController {
    
    @Setter(onMethod_ = @Autowired)
    private PortfolioService portfolioService;
    
    @Setter(onMethod_ = @Autowired)
    private StockPriceUpdateService stockPriceUpdateService;
    
    /**
     * ✅ 포트폴리오 목록 + 요약 정보 조회 (완전 개선)
     * URL: /api/portfolio/list (GET)
     * 
     * Frontend dashboard.jsp와 100% 매칭
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getPortfolioList(HttpSession session) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📊 [API] 포트폴리오 목록 조회");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 세션 체크
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member == null) {
                log.warn("❌ 로그인 필요");
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                response.put("portfolios", new ArrayList<>());
                response.put("summary", createEmptySummary());
                log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return ResponseEntity.ok(response); // 200 OK로 반환 (프론트엔드 처리 편의)
            }
            
            String memberId = member.getMemberId();
            log.info("  - 회원 ID: " + memberId);
            
            // 포트폴리오 목록 조회
            List<PortfolioVO> portfolioList = portfolioService.getPortfolioList(memberId);
            
            if (portfolioList == null || portfolioList.isEmpty()) {
                log.info("ℹ️ 포트폴리오 목록이 비어있음");
                response.put("success", true);
                response.put("portfolios", new ArrayList<>());
                response.put("summary", createEmptySummary());
                log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return ResponseEntity.ok(response);
            }
            
            // ✅ Frontend와 완벽하게 매칭되는 데이터 구조
            List<Map<String, Object>> enrichedPortfolios = new ArrayList<>();
            
            double totalValue = 0;      // 총 평가금액
            double totalCost = 0;       // 총 투자원금
            
            for (PortfolioVO portfolio : portfolioList) {
                try {
                    Map<String, Object> portfolioData = new HashMap<>();
                    
                    // ✅ 기본 정보
                    portfolioData.put("portfolioId", portfolio.getPortfolioId());
                    portfolioData.put("stockCode", portfolio.getStockCode());
                    portfolioData.put("stockName", 
                        portfolio.getStockName() != null ? portfolio.getStockName() : portfolio.getStockCode());
                    
                    // ✅ 수량 및 가격 (안전한 변환)
                    double quantity = toDouble(portfolio.getQuantity());
                    double purchasePrice = toDouble(portfolio.getPurchasePrice());
                    
                    portfolioData.put("quantity", quantity);
                    portfolioData.put("purchasePrice", purchasePrice);
                    
                    // ✅ 현재가 조회
                    double currentPrice = getCurrentPrice(portfolio.getStockCode());
                    portfolioData.put("currentPrice", currentPrice);
                    
                    // ✅ 계산 (Frontend 변수명과 정확히 매칭)
                    double itemTotalValue = currentPrice * quantity;      // 평가금액
                    double itemCost = purchasePrice * quantity;           // 투자원금
                    double profit = itemTotalValue - itemCost;            // 손익
                    double profitRate = itemCost > 0 ? (profit / itemCost) * 100 : 0;  // 수익률
                    
                    portfolioData.put("totalValue", itemTotalValue);
                    portfolioData.put("profit", profit);
                    portfolioData.put("profitRate", profitRate);
                    
                    enrichedPortfolios.add(portfolioData);
                    
                    // 총계 누적
                    totalValue += itemTotalValue;
                    totalCost += itemCost;
                    
                } catch (Exception e) {
                    log.warn("⚠️ 포트폴리오 항목 처리 실패: " + e.getMessage());
                }
            }
            
            // ✅ Frontend와 정확히 매칭되는 요약 정보
            double totalProfit = totalValue - totalCost;
            double returnRate = totalCost > 0 ? (totalProfit / totalCost) * 100 : 0;
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalValue", totalValue);      // 총 평가금액
            summary.put("totalCost", totalCost);        // 총 투자원금
            summary.put("totalProfit", totalProfit);    // 총 손익
            summary.put("returnRate", returnRate);      // 수익률
            
            response.put("success", true);
            response.put("portfolios", enrichedPortfolios);
            response.put("summary", summary);
            
            log.info("✅ 포트폴리오 조회 완료");
            log.info("  - 포트폴리오 수: " + enrichedPortfolios.size());
            log.info("  - 총 평가금액: " + String.format("%,.0f원", totalValue));
            log.info("  - 총 투자원금: " + String.format("%,.0f원", totalCost));
            log.info("  - 총 손익: " + String.format("%+,.0f원", totalProfit));
            log.info("  - 수익률: " + String.format("%+.2f%%", returnRate));
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ 포트폴리오 조회 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            response.put("success", false);
            response.put("message", "포트폴리오 조회에 실패했습니다: " + e.getMessage());
            response.put("portfolios", new ArrayList<>());
            response.put("summary", createEmptySummary());
            
            return ResponseEntity.ok(response); // 200 OK로 반환
        }
    }
    
    /**
     * ✅ 포트폴리오 상세 조회
     */
    @GetMapping("/{portfolioId}")
    public ResponseEntity<Map<String, Object>> getPortfolioDetail(
            @PathVariable Long portfolioId,
            HttpSession session) {
        
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📊 [API] 포트폴리오 상세 조회");
        log.info("  - 포트폴리오 ID: " + portfolioId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.ok(response);
            }
            
            PortfolioVO portfolio = portfolioService.getPortfolio(portfolioId);
            
            if (portfolio == null) {
                response.put("success", false);
                response.put("message", "포트폴리오를 찾을 수 없습니다.");
                return ResponseEntity.ok(response);
            }
            
            double currentPrice = getCurrentPrice(portfolio.getStockCode());
            double quantity = toDouble(portfolio.getQuantity());
            double purchasePrice = toDouble(portfolio.getPurchasePrice());
            
            Map<String, Object> portfolioData = new HashMap<>();
            portfolioData.put("portfolioId", portfolio.getPortfolioId());
            portfolioData.put("stockCode", portfolio.getStockCode());
            portfolioData.put("stockName", portfolio.getStockName());
            portfolioData.put("quantity", quantity);
            portfolioData.put("purchasePrice", purchasePrice);
            portfolioData.put("currentPrice", currentPrice);
            
            double totalValue = currentPrice * quantity;
            double cost = purchasePrice * quantity;
            double profit = totalValue - cost;
            double profitRate = cost > 0 ? (profit / cost) * 100 : 0;
            
            portfolioData.put("totalValue", totalValue);
            portfolioData.put("cost", cost);
            portfolioData.put("profit", profit);
            portfolioData.put("profitRate", profitRate);
            
            response.put("success", true);
            response.put("portfolio", portfolioData);
            
            log.info("✅ 포트폴리오 상세 조회 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ 포트폴리오 조회 실패: " + e.getMessage(), e);
            response.put("success", false);
            response.put("message", "포트폴리오 조회에 실패했습니다.");
            return ResponseEntity.ok(response);
        }
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Private 유틸리티 메서드
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * ✅ 현재가 조회
     */
    private double getCurrentPrice(String stockCode) {
        if (stockCode == null || stockCode.trim().isEmpty()) {
            return 0.0;
        }
        
        try {
            StockPriceVO latestPrice = stockPriceUpdateService.getLatestStockPrice(stockCode);
            
            if (latestPrice != null && latestPrice.getClosePrice() != null) {
                return latestPrice.getClosePrice().doubleValue();
            }
            
            log.debug("⚠️ 최신 주가 정보 없음 (stockCode: " + stockCode + ")");
            
        } catch (Exception e) {
            log.warn("⚠️ 현재가 조회 실패 (stockCode: " + stockCode + "): " + e.getMessage());
        }
        
        return 0.0;
    }
    
    /**
     * ✅ BigDecimal → double 안전 변환
     */
    private double toDouble(BigDecimal value) {
        if (value == null) {
            return 0.0;
        }
        return value.doubleValue();
    }
    
    /**
     * ✅ 빈 요약 정보 생성
     */
    private Map<String, Object> createEmptySummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalValue", 0.0);
        summary.put("totalCost", 0.0);
        summary.put("totalProfit", 0.0);
        summary.put("returnRate", 0.0);
        return summary;
    }
}
