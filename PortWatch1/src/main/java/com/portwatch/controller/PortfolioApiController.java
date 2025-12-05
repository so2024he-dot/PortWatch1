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

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioApiController {
    
    @Autowired
    private PortfolioService portfolioService;
    
    @PostMapping("/add")
    public Map<String, Object> addToPortfolio(
            @RequestParam(required = false) Integer stockId,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) String avgPurchasePrice,
            @RequestParam(required = false) String purchaseDate,
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 로그인 체크
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member == null) {
                result.put("success", false);
                result.put("message", "로그인이 필요합니다.");
                result.put("requireLogin", true);
                return result;
            }
            
            // 파라미터 검증
            if (stockId == null || stockId <= 0) {
                result.put("success", false);
                result.put("message", "종목 정보가 올바르지 않습니다.");
                return result;
            }
            
            if (quantity == null || quantity <= 0) {
                result.put("success", false);
                result.put("message", "수량은 1 이상이어야 합니다.");
                return result;
            }
            
            if (avgPurchasePrice == null || avgPurchasePrice.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "평균 매입가를 입력해주세요.");
                return result;
            }
            
            // VO 생성
            PortfolioVO portfolioVO = new PortfolioVO();
            portfolioVO.setMemberId(member.getMemberId());
            portfolioVO.setStockId(stockId);
            portfolioVO.setQuantity(quantity);
            
            // 가격 변환
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
            
            // 날짜 설정
            if (purchaseDate != null && !purchaseDate.trim().isEmpty()) {
                try {
                    portfolioVO.setPurchaseDate(java.sql.Date.valueOf(purchaseDate.trim()));
                } catch (IllegalArgumentException e) {
                    result.put("success", false);
                    result.put("message", "매입 일자 형식이 올바르지 않습니다.");
                    return result;
                }
            }
            
            // 포트폴리오에 추가
            portfolioService.addPortfolio(portfolioVO);
            
            result.put("success", true);
            result.put("message", "포트폴리오에 추가되었습니다.");
            
        } catch (Exception e) {
            result.put("success", false);
            if (e.getMessage() != null && e.getMessage().contains("중복")) {
                result.put("message", "이미 포트폴리오에 등록된 종목입니다.");
            } else {
                result.put("message", "포트폴리오 추가 중 오류가 발생했습니다.");
            }
            e.printStackTrace();
        }
        
        return result;
    }
}

    
