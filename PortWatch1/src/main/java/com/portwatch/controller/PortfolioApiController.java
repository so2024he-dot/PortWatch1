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
 * 포트폴리오 REST API Controller
 * AJAX 요청 처리용
 */
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioApiController {
    
    @Autowired
    private PortfolioService portfolioService;
    
    /**
     * 포트폴리오에 종목 추가 (AJAX)
     */
    @PostMapping("/add")
    public Map<String, Object> addToPortfolio(@RequestParam Integer stockId,  // Long을 Integer로 변경
                                              @RequestParam Integer quantity,
                                              @RequestParam Long avgPurchasePrice,
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
            
            // 포트폴리오 VO 생성
            PortfolioVO portfolioVO = new PortfolioVO();
            portfolioVO.setMemberId(member.getMemberId());
            
            // Integer 타입으로 직접 설정
            portfolioVO.setStockId(stockId);
            
            portfolioVO.setQuantity(quantity);
            
            // Long을 BigDecimal로 변환하여 설정
            portfolioVO.setAvgPurchasePrice(new BigDecimal(avgPurchasePrice));
            
            if (purchaseDate != null && !purchaseDate.isEmpty()) {
                portfolioVO.setPurchaseDate(java.sql.Date.valueOf(purchaseDate));
            }
            
            // 포트폴리오에 추가
            portfolioService.addPortfolio(portfolioVO);
            
            result.put("success", true);
            result.put("message", "포트폴리오에 추가되었습니다.");
            
        } catch (Exception e) {
            result.put("success", false);
            
            // 중복 종목 체크
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

    
