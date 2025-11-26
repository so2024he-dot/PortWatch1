package com.portwatch.controller;

import java.util.HashMap;
import java.util.List;
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
    
    @GetMapping("/list")
    public Map<String, Object> getPortfolioList(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member == null) {
                result.put("success", false);
                result.put("message", "로그인이 필요합니다.");
                return result;
            }
            
            List<PortfolioVO> portfolios = portfolioService.getPortfolioList(member.getMemberId());
            Map<String, Object> summary = portfolioService.getPortfolioSummary(member.getMemberId());
            
            result.put("success", true);
            result.put("data", portfolios);
            result.put("summary", summary);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/add")
    public Map<String, Object> addPortfolio(@RequestBody PortfolioVO portfolio, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member == null) {
                result.put("success", false);
                result.put("message", "로그인이 필요합니다.");
                return result;
            }
            
            portfolio.setMemberId(member.getMemberId());
            portfolioService.addPortfolio(portfolio);
            
            result.put("success", true);
            result.put("message", "포트폴리오에 추가되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/update")
    public Map<String, Object> updatePortfolio(@RequestBody PortfolioVO portfolio, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member == null) {
                result.put("success", false);
                result.put("message", "로그인이 필요합니다.");
                return result;
            }
            
            portfolioService.updatePortfolio(portfolio);
            
            result.put("success", true);
            result.put("message", "포트폴리오가 수정되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/delete")
    public Map<String, Object> deletePortfolio(@RequestParam long portfolioId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
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
            result.put("message", e.getMessage());
        }
        return result;
    }
}
