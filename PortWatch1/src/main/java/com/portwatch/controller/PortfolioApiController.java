package com.portwatch.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.portwatch.domain.MemberVO;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.service.PortfolioService;

/**
 * 포트폴리오 API Controller (분할 매수 지원)
 * 
 * ✅ 수정사항:
 * - quantity를 Integer → BigDecimal로 변경하여 분할 매수 지원
 * - 0.5주, 0.1주 등 소수점 단위 매입 가능
 * 
 * @version 2.0
 */
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioApiController {
    
    @Autowired
    private PortfolioService portfolioService;
    
    /**
     * 포트폴리오에 종목 추가 (분할 매수 지원)
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
            @RequestParam(required = false) String quantity,  // ✅ String으로 받아서 BigDecimal 변환
            @RequestParam(required = false) String avgPurchasePrice,
            @RequestParam(required = false) String purchaseDate,
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // ============================================
            // 1. 로그인 체크
            // ============================================
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member == null) {
                result.put("success", false);
                result.put("message", "로그인이 필요합니다.");
                result.put("requireLogin", true);
                return result;
            }
            
            // ============================================
            // 2. stockId 검증
            // ============================================
            if (stockId == null || stockId <= 0) {
                result.put("success", false);
                result.put("message", "종목 정보가 올바르지 않습니다.");
                return result;
            }
            
            // ============================================
            // 3. quantity 검증 및 변환 (✅ 핵심 수정)
            // ============================================
            BigDecimal quantityDecimal;
            if (quantity == null || quantity.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "수량을 입력해주세요.");
                return result;
            }
            
            try {
                quantityDecimal = new BigDecimal(quantity.trim());
                
                // 수량은 0.01 이상이어야 함
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
            
            // ============================================
            // 4. avgPurchasePrice 검증
            // ============================================
            if (avgPurchasePrice == null || avgPurchasePrice.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "평균 매입가를 입력해주세요.");
                return result;
            }
            
            // ============================================
            // 5. VO 생성
            // ============================================
            PortfolioVO portfolioVO = new PortfolioVO();
            portfolioVO.setMemberId(member.getMemberId());
            portfolioVO.setStockId(stockId);
            portfolioVO.setQuantity(quantityDecimal);  // ✅ BigDecimal 설정
            
            // ============================================
            // 6. 평균 매입가 변환
            // ============================================
            try {
                BigDecimal price = new BigDecimal(avgPurchasePrice.trim());
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    result.put("success", false);
                    result.put("message", "평균 매입가는 0보다 커야 합니다.");
                    return result;
                }
                portfolioVO.setAvgPurchasePrice(price);
            } catch (NumberFormatException e) {
                result.put("success", false);
                result.put("message", "평균 매입가 형식이 올바르지 않습니다.");
                return result;
            }
            
            // ============================================
            // 7. 매입 날짜 설정 (선택)
            // ============================================
            if (purchaseDate != null && !purchaseDate.trim().isEmpty()) {
                try {
                    portfolioVO.setPurchaseDate(java.sql.Date.valueOf(purchaseDate.trim()));
                } catch (IllegalArgumentException e) {
                    result.put("success", false);
                    result.put("message", "매입 일자 형식이 올바르지 않습니다. (YYYY-MM-DD)");
                    return result;
                }
            }
            
            // ============================================
            // 8. 포트폴리오에 추가
            // ============================================
            portfolioService.addPortfolio(portfolioVO);
            
            result.put("success", true);
            result.put("message", "포트폴리오에 추가되었습니다.");
            
            // ✅ 추가 정보 반환
            result.put("data", Map.of(
                "portfolioId", portfolioVO.getPortfolioId(),
                "quantity", quantityDecimal.toString(),
                "totalAmount", quantityDecimal.multiply(portfolioVO.getAvgPurchasePrice())
            ));
            
        } catch (Exception e) {
            result.put("success", false);
            
            // 에러 메시지 상세 분석
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
            
            // 디버깅용 로그 (개발 환경)
            System.err.println("=== 포트폴리오 추가 에러 ===");
            System.err.println("stockId: " + stockId);
            System.err.println("quantity: " + quantity);
            System.err.println("avgPurchasePrice: " + avgPurchasePrice);
            e.printStackTrace();
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
            
            // VO 생성
            PortfolioVO portfolioVO = new PortfolioVO();
            portfolioVO.setPortfolioId(portfolioId);
            portfolioVO.setQuantity(new BigDecimal(quantity));
            portfolioVO.setAvgPurchasePrice(new BigDecimal(avgPurchasePrice));
            
            if (purchaseDate != null && !purchaseDate.trim().isEmpty()) {
                portfolioVO.setPurchaseDate(java.sql.Date.valueOf(purchaseDate));
            }
            
            // 업데이트
            portfolioService.updatePortfolio(portfolioVO);
            
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
            
            portfolioService.deletePortfolio(portfolioId);
            
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

    
