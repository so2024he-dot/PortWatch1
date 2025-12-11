package com.portwatch.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.portwatch.domain.MemberVO;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.StockVO;
import com.portwatch.service.PortfolioService;
import com.portwatch.service.StockService;

/**
 * 포트폴리오 API Controller (추가 매입 지원)
 * 
 * ✅ 수정사항:
 * -  Integer → BigDecimal로 변경하여 분할 매수 지원
 * - 0.5주, 0.1주 등 소수점 단위 매입 가능
 * - 추가 매입 시 수량 합산 + 평균 매입가 재계산
 * 
 * @author PortWatch
 * @version 3.0 (추가 매입 지원)
 */
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioApiController {
    
    @Autowired
    private PortfolioService portfolioService;
    
    @Autowired
    private StockService stockService;
    
    /**
     * 포트폴리오에 종목 추가 (분할 매수 + 추가 매입 지원)
     * 
     * @param stockId 종목 ID
     * @param quantity 수량 (소수점 지원: 0.5, 0.1 등)
     * @param avgPurchasePrice 평균 매입가
     * @param purchaseDate 매입일 (선택)
     * @param session HTTP 세션
     * @return 처리 결과
     */
    @PostMapping("/add")
    public Map<String, Object> addToPortfolio(
            @RequestParam(required = false) Integer stockId,
            @RequestParam(required = false) String quantity,
            @RequestParam(required = false) String avgPurchasePrice,
            @RequestParam(required = false) String purchaseDate,
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🌐 API: 포트폴리오 추가 요청");
        
        try {
            // ============================================
            // 1. 로그인 체크
            // ============================================
            MemberVO member = (MemberVO) session.getAttribute("member");
            Integer memberId = (Integer) session.getAttribute("memberId");
            
            if (member == null && memberId == null) {
                result.put("success", false);
                result.put("message", "로그인이 필요합니다.");
                result.put("requireLogin", true);
                return result;
            }
            
            // memberId 결정
            int finalMemberId = (member != null) ? member.getMemberId() : memberId;
            System.out.println("👤 회원 ID: " + finalMemberId);
            
            // ============================================
            // 2. stockId 검증
            // ============================================
            if (stockId == null || stockId <= 0) {
                result.put("success", false);
                result.put("message", "종목 정보가 올바르지 않습니다.");
                return result;
            }
            
            // 종목 정보 조회
            StockVO stock = stockService.getStockById(stockId);
            if (stock == null) {
                result.put("success", false);
                result.put("message", "종목을 찾을 수 없습니다.");
                return result;
            }
            
            System.out.println("📊 종목: " + stock.getStockName() + " (" + stock.getStockCode() + ")");
            
            // ============================================
            // 3. quantity 검증 및 변환
            // ============================================
            BigDecimal quantityDecimal;
            if (quantity == null || quantity.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "수량을 입력해주세요.");
                return result;
            }
            
            try {
                quantityDecimal = new BigDecimal(quantity.trim());
                
                if (quantityDecimal.compareTo(new BigDecimal("0.01")) < 0) {
                    result.put("success", false);
                    result.put("message", "수량은 0.01 이상이어야 합니다.");
                    return result;
                }
            } catch (NumberFormatException e) {
                result.put("success", false);
                result.put("message", "수량 형식이 올바르지 않습니다. (예: 1, 0.5, 0.1)");
                return result;
            }
            
            System.out.println("📥 신규 수량: " + quantityDecimal);
            
            // ============================================
            // 4. avgPurchasePrice 검증
            // ============================================
            if (avgPurchasePrice == null || avgPurchasePrice.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "평균 매입가를 입력해주세요.");
                return result;
            }
            
            BigDecimal price;
            try {
                price = new BigDecimal(avgPurchasePrice.trim());
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    result.put("success", false);
                    result.put("message", "평균 매입가는 0보다 커야 합니다.");
                    return result;
                }
            } catch (NumberFormatException e) {
                result.put("success", false);
                result.put("message", "평균 매입가 형식이 올바르지 않습니다.");
                return result;
            }
            
            System.out.println("💰 신규 매입가: " + price);
            
            // ============================================
            // 5. 기존 포트폴리오 확인 (추가 매입 체크)
            // ============================================
            PortfolioVO existing = portfolioService.getByMemberAndStock(finalMemberId, stockId);
            
            if (existing != null) {
                // ============================================
                // 추가 매입: 수량 합산 + 평균 매입가 재계산
                // ============================================
                System.out.println("🔄 추가 매입 처리");
                System.out.println("  - 기존 수량: " + existing.getQuantity());
                System.out.println("  - 기존 평균가: " + existing.getAvgPurchasePrice());
                
                // 기존 총 매입액
                BigDecimal existingTotal = existing.getQuantity()
                    .multiply(existing.getAvgPurchasePrice());
                
                // 신규 총 매입액
                BigDecimal newTotal = quantityDecimal.multiply(price);
                
                // 합산 수량
                BigDecimal totalQuantity = existing.getQuantity().add(quantityDecimal);
                
                // 새로운 평균 매입가
                BigDecimal newAvgPrice = existingTotal.add(newTotal)
                    .divide(totalQuantity, 2, RoundingMode.HALF_UP);
                
                System.out.println("  - 합산 수량: " + totalQuantity);
                System.out.println("  - 새로운 평균가: " + newAvgPrice);
                
                // 업데이트
                existing.setQuantity(totalQuantity);
                existing.setAvgPurchasePrice(newAvgPrice);
                
                if (purchaseDate != null && !purchaseDate.trim().isEmpty()) {
                    try {
                        existing.setPurchaseDate(java.sql.Date.valueOf(purchaseDate.trim()));
                    } catch (IllegalArgumentException e) {
                        // 날짜 형식 오류는 무시하고 계속 진행
                    }
                }
                
                portfolioService.update(existing);
                
                result.put("success", true);
                result.put("message", String.format("%s를 추가 매입했습니다! (총 %s주, 평균가 %s)", 
                    stock.getStockName(),
                    totalQuantity.stripTrailingZeros().toPlainString(),
                    newAvgPrice.stripTrailingZeros().toPlainString()));
                result.put("isAdditionalPurchase", true);
                result.put("data", Map.of(
                    "portfolioId", existing.getPortfolioId(),
                    "totalQuantity", totalQuantity.toString(),
                    "avgPurchasePrice", newAvgPrice.toString(),
                    "totalAmount", totalQuantity.multiply(newAvgPrice).toString()
                ));
                
            } else {
                // ============================================
                // 신규 매입
                // ============================================
                System.out.println("🆕 신규 매입 처리");
                
                PortfolioVO portfolioVO = new PortfolioVO();
                portfolioVO.setMemberId(finalMemberId);
                portfolioVO.setStockId(stockId);
                portfolioVO.setQuantity(quantityDecimal);
                portfolioVO.setAvgPurchasePrice(price);
                
                if (purchaseDate != null && !purchaseDate.trim().isEmpty()) {
                    try {
                        portfolioVO.setPurchaseDate(java.sql.Date.valueOf(purchaseDate.trim()));
                    } catch (IllegalArgumentException e) {
                        result.put("success", false);
                        result.put("message", "매입 일자 형식이 올바르지 않습니다. (YYYY-MM-DD)");
                        return result;
                    }
                }
                
                portfolioService.insert(portfolioVO);
                
                result.put("success", true);
                result.put("message", String.format("%s를 포트폴리오에 추가했습니다! (%s주, 매입가 %s)",
                    stock.getStockName(),
                    quantityDecimal.stripTrailingZeros().toPlainString(),
                    price.stripTrailingZeros().toPlainString()));
                result.put("isAdditionalPurchase", false);
                result.put("data", Map.of(
                    "portfolioId", portfolioVO.getPortfolioId(),
                    "quantity", quantityDecimal.toString(),
                    "avgPurchasePrice", price.toString(),
                    "totalAmount", quantityDecimal.multiply(price).toString()
                ));
            }
            
            System.out.println("✅ 포트폴리오 추가 성공");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 추가 실패");
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", false);
            
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                if (errorMsg.contains("중복") || errorMsg.contains("Duplicate")) {
                    result.put("message", "이미 포트폴리오에 등록된 종목입니다.");
                } else if (errorMsg.contains("stock_code") || errorMsg.contains("stock_name")) {
                    result.put("message", "종목 정보를 불러올 수 없습니다. 관리자에게 문의하세요.");
                } else {
                    result.put("message", "포트폴리오 추가 중 오류가 발생했습니다: " + errorMsg);
                }
            } else {
                result.put("message", "포트폴리오 추가 중 오류가 발생했습니다.");
            }
        }
        
        return result;
    }
    
    /**
     * 포트폴리오 수정 (수량/가격 업데이트)
     */
    @PostMapping("/update")
    public Map<String, Object> updatePortfolio(
            @RequestParam Long portfolioId,
            @RequestParam String quantity,
            @RequestParam String avgPurchasePrice,
            @RequestParam(required = false) String purchaseDate,
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 로그인 체크
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member == null) {
                result.put("success", false);
                result.put("message", "로그인이 필요합니다.");
                return result;
            }
            
            PortfolioVO portfolioVO = new PortfolioVO();
            portfolioVO.setPortfolioId(portfolioId);
            portfolioVO.setQuantity(new BigDecimal(quantity));
            portfolioVO.setAvgPurchasePrice(new BigDecimal(avgPurchasePrice));
            
            if (purchaseDate != null && !purchaseDate.trim().isEmpty()) {
                portfolioVO.setPurchaseDate(java.sql.Date.valueOf(purchaseDate));
            }
            
            portfolioService.update(portfolioVO);
            
            result.put("success", true);
            result.put("message", "포트폴리오가 수정되었습니다.");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "수정 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 포트폴리오 삭제
     */
    @PostMapping("/delete")
    public Map<String, Object> deletePortfolio(
            @RequestParam Long portfolioId,
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 로그인 체크
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member == null) {
                result.put("success", false);
                result.put("message", "로그인이 필요합니다.");
                return result;
            }
            
            portfolioService.delete(portfolioId.intValue());
            
            result.put("success", true);
            result.put("message", "포트폴리오에서 삭제되었습니다.");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "삭제 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}
