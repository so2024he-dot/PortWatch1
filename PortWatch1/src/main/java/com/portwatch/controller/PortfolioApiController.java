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
 * PORTFOLIO API CONTROLLER - REST API (완전 수정 버전)
 * Spring 5.0.7 + MySQL 8.0.33
 * 
 * 수정 내역:
 * 1. ✅ StockPriceService → StockPriceUpdateService 변경
 * 2. ✅ BigDecimal → double 변환 처리 (.doubleValue())
 * 3. ✅ Null 체크 강화 (NPE 방지)
 * 4. ✅ 타입 안정성 보장
 * 
 * 기능:
 * - 포트폴리오 목록 조회 (JSON)
 * - 포트폴리오 상세 조회 (JSON)
 * - 포트폴리오 요약 정보 (총자산, 수익률 등)
 * 
 * Dashboard에서 AJAX로 호출하는 API
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@RestController
@RequestMapping("/api/portfolio")
@Log4j
public class PortfolioApiController {
    
    @Setter(onMethod_ = @Autowired)
    private PortfolioService portfolioService;
    
    // ✅ StockPriceService → StockPriceUpdateService로 변경
    @Setter(onMethod_ = @Autowired)
    private StockPriceUpdateService stockPriceUpdateService;
    
    /**
     * ✅ 포트폴리오 목록 + 요약 정보 조회
     * URL: /api/portfolio/list (GET)
     * 
     * 응답 형식:
     * {
     *   "success": true,
     *   "portfolios": [...],
     *   "summary": {
     *     "totalAssets": 10000000,
     *     "totalProfit": 500000,
     *     "profitRate": 5.0
     *   }
     * }
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
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String memberId = member.getMemberId();
            log.info("  - 회원 ID: " + memberId);
            
            // 포트폴리오 목록 조회
            List<PortfolioVO> portfolioList = portfolioService.getPortfolioList(memberId);
            
            if (portfolioList == null) {
                log.warn("⚠️ 포트폴리오 목록이 null");
                portfolioList = new ArrayList<>();
            }
            
            // 각 포트폴리오에 현재가 정보 추가
            List<Map<String, Object>> enrichedPortfolios = new ArrayList<>();
            
            double totalAssets = 0;
            double totalCost = 0;
            
            for (PortfolioVO portfolio : portfolioList) {
                Map<String, Object> portfolioData = new HashMap<>();
                
                // 기본 정보
                portfolioData.put("portfolioId", portfolio.getPortfolioId());
                portfolioData.put("portfolioName", 
                    portfolio.getStockName() != null ? portfolio.getStockName() : "Unknown");
                portfolioData.put("stockCode", portfolio.getStockCode());
                portfolioData.put("stockName", portfolio.getStockName());
                
                // ✅ BigDecimal → double 변환 (Null 체크 포함)
                double quantity = toDouble(portfolio.getQuantity());
                double purchasePrice = toDouble(portfolio.getPurchasePrice());
                
                portfolioData.put("quantity", quantity);
                portfolioData.put("purchasePrice", purchasePrice);
                
                // 현재가 조회
                double currentPrice = getCurrentPrice(portfolio.getStockCode());
                portfolioData.put("currentPrice", currentPrice);
                
                // ✅ 평가 금액 계산 (double 타입으로 안전하게 계산)
                double totalValue = currentPrice * quantity;
                portfolioData.put("totalValue", totalValue);
                
                // ✅ 손익 계산 (double 타입으로 안전하게 계산)
                double cost = purchasePrice * quantity;
                double profitLoss = totalValue - cost;
                double profitRate = cost > 0 ? (profitLoss / cost) * 100 : 0;
                
                portfolioData.put("profitLoss", profitLoss);
                portfolioData.put("profitRate", profitRate);
                
                enrichedPortfolios.add(portfolioData);
                
                // 총계 누적
                totalAssets += totalValue;
                totalCost += cost;
            }
            
            // 요약 정보
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalAssets", totalAssets);
            summary.put("totalProfit", totalAssets - totalCost);
            summary.put("profitRate", totalCost > 0 ? ((totalAssets - totalCost) / totalCost) * 100 : 0);
            
            response.put("success", true);
            response.put("portfolios", enrichedPortfolios);
            response.put("summary", summary);
            
            log.info("✅ 포트폴리오 조회 완료");
            log.info("  - 포트폴리오 수: " + enrichedPortfolios.size());
            log.info("  - 총 자산: " + String.format("%,.0f원", totalAssets));
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ 포트폴리오 조회 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            response.put("success", false);
            response.put("message", "포트폴리오 조회에 실패했습니다: " + e.getMessage());
            response.put("portfolios", new ArrayList<>());
            response.put("summary", createEmptySummary());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * ✅ 포트폴리오 상세 조회 (JSON)
     * URL: /api/portfolio/{portfolioId} (GET)
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
            // 세션 체크
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member == null) {
                log.warn("❌ 로그인 필요");
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // 포트폴리오 조회
            PortfolioVO portfolio = portfolioService.getPortfolio(portfolioId);
            
            if (portfolio == null) {
                log.warn("⚠️ 포트폴리오를 찾을 수 없음");
                response.put("success", false);
                response.put("message", "포트폴리오를 찾을 수 없습니다.");
                log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // 현재가 조회
            double currentPrice = getCurrentPrice(portfolio.getStockCode());
            
            // ✅ BigDecimal → double 변환
            double quantity = toDouble(portfolio.getQuantity());
            double purchasePrice = toDouble(portfolio.getPurchasePrice());
            
            // 상세 정보 구성
            Map<String, Object> portfolioData = new HashMap<>();
            portfolioData.put("portfolioId", portfolio.getPortfolioId());
            portfolioData.put("portfolioName", 
                portfolio.getStockName() != null ? portfolio.getStockName() : "Unknown");
            portfolioData.put("stockCode", portfolio.getStockCode());
            portfolioData.put("stockName", portfolio.getStockName());
            portfolioData.put("quantity", quantity);
            portfolioData.put("purchasePrice", purchasePrice);
            portfolioData.put("currentPrice", currentPrice);
            
            // ✅ 계산 (double 타입으로 안전하게)
            double totalValue = currentPrice * quantity;
            double cost = purchasePrice * quantity;
            double profitLoss = totalValue - cost;
            double profitRate = cost > 0 ? (profitLoss / cost) * 100 : 0;
            
            portfolioData.put("totalValue", totalValue);
            portfolioData.put("cost", cost);
            portfolioData.put("profitLoss", profitLoss);
            portfolioData.put("profitRate", profitRate);
            
            response.put("success", true);
            response.put("portfolio", portfolioData);
            
            log.info("✅ 포트폴리오 상세 조회 완료");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ 포트폴리오 조회 실패: " + e.getMessage(), e);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            response.put("success", false);
            response.put("message", "포트폴리오 조회에 실패했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Private 유틸리티 메서드
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * ✅ 현재가 조회 (수정됨)
     * StockPriceUpdateService 사용
     */
    private double getCurrentPrice(String stockCode) {
        if (stockCode == null || stockCode.trim().isEmpty()) {
            log.warn("⚠️ stockCode가 null 또는 비어있음");
            return 0.0;
        }
        
        try {
            // ✅ stockPriceUpdateService 사용
            StockPriceVO latestPrice = stockPriceUpdateService.getLatestStockPrice(stockCode);
            
            if (latestPrice != null && latestPrice.getClosePrice() != null) {
                // ✅ BigDecimal → double 변환
                return latestPrice.getClosePrice().doubleValue();
            }
            
            log.debug("⚠️ 최신 주가 정보 없음 (stockCode: " + stockCode + ")");
            
        } catch (Exception e) {
            log.warn("⚠️ 현재가 조회 실패 (stockCode: " + stockCode + "): " + e.getMessage());
        }
        
        // ✅ 기본값 반환
        return 0.0;
    }
    
    /**
     * ✅ BigDecimal → double 안전 변환
     * Null 체크 포함
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
        summary.put("totalAssets", 0.0);
        summary.put("totalProfit", 0.0);
        summary.put("profitRate", 0.0);
        return summary;
    }
}
