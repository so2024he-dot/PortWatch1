package com.portwatch.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.portwatch.domain.MemberVO;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.PortfolioStockVO;
import com.portwatch.service.PortfolioService;
import com.portwatch.service.StockService;
import com.portwatch.service.ExchangeRateService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 포트폴리오 Controller (환율 정보 지원)
 * 
 * ✅ 수정사항:
 * - createForm에 환율 정보 추가
 * - 미국 주식 포트폴리오 추가 지원
 * 
 * @version 2.0
 */
@Controller
@RequestMapping("/portfolio")
public class PortfolioController {
    
    @Autowired
    private PortfolioService portfolioService;
    
    @Autowired
    private StockService stockService;
    
    @Autowired
    private ExchangeRateService exchangeRateService;
    
    /**
     * 포트폴리오 목록 페이지
     */
    @GetMapping("/list")
    public String portfolioList(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";
        }
        
        try {
            // 포트폴리오 목록 조회
            List<PortfolioVO> portfolioList = portfolioService.getPortfolioList(member.getMemberId());
            
            // 포트폴리오 요약 정보 조회
            Map<String, Object> summary = portfolioService.getPortfolioSummary(member.getMemberId());
            
            // 종목 목록 (추가용)
            List<Map<String, Object>> stockList = stockService.getAllStocks();
            
            model.addAttribute("portfolioList", portfolioList);
            model.addAttribute("summary", summary);
            model.addAttribute("stockList", stockList);
            
            // ✅ 환율 정보 추가 (미국 주식 표시용)
            try {
                BigDecimal exchangeRate = exchangeRateService.getUSDToKRW();
                model.addAttribute("exchangeRate", exchangeRate);
            } catch (Exception e) {
                model.addAttribute("exchangeRate", new BigDecimal("1310.00"));
            }
            
            return "portfolio/list";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "포트폴리오 목록을 불러오는 중 오류가 발생했습니다.");
            return "portfolio/list";
        }
    }
    
    /**
     * 포트폴리오 상세 페이지
     */
    @GetMapping("/detail/{portfolioId}")
    public String portfolioDetail(@PathVariable Long portfolioId, 
                                  HttpSession session, 
                                  Model model,
                                  RedirectAttributes rttr) {
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";
        }
        
        try {
            // 포트폴리오 상세 조회
            PortfolioVO portfolio = portfolioService.getPortfolioById(portfolioId);
            
            // 권한 확인
            if (portfolio == null || portfolio.getMemberId() != member.getMemberId()) {
                rttr.addFlashAttribute("error", "접근 권한이 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            // 해당 포트폴리오의 종목 목록
            List<PortfolioStockVO> stocks = portfolioService.getPortfolioStocks(portfolioId);
            
            model.addAttribute("portfolio", portfolio);
            model.addAttribute("stocks", stocks);
            
            return "portfolio/detail";
            
        } catch (Exception e) {
            e.printStackTrace();
            rttr.addFlashAttribute("error", "포트폴리오 정보를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/portfolio/list";
        }
    }
    
    /**
     * 포트폴리오 생성 폼 (✅ 환율 정보 추가)
     */
    @GetMapping("/create")
    public String createForm(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";
        }
        
        try {
            // 종목 목록
            List<Map<String, Object>> stockList = stockService.getAllStocks();
            model.addAttribute("stockList", stockList);
            model.addAttribute("portfolioVO", new PortfolioVO());
            
            // ✅ 환율 정보 추가
            try {
                BigDecimal exchangeRate = exchangeRateService.getUSDToKRW();
                model.addAttribute("exchangeRate", exchangeRate);
                System.out.println("✅ 환율 정보: 1 USD = " + exchangeRate + " KRW");
            } catch (Exception e) {
                System.err.println("⚠️ 환율 조회 실패, 기본값 사용: " + e.getMessage());
                model.addAttribute("exchangeRate", new BigDecimal("1310.00"));
            }
            
            return "portfolio/create";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "종목 목록을 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("exchangeRate", new BigDecimal("1310.00"));
            return "portfolio/create";
        }
    }
    
    /**
     * 포트폴리오 생성 처리
     */
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute PortfolioVO portfolioVO,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model,
                        RedirectAttributes rttr) {
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";
        }
        
        if (bindingResult.hasErrors()) {
            try {
                List<Map<String, Object>> stockList = stockService.getAllStocks();
                model.addAttribute("stockList", stockList);
                
                // 환율 정보도 다시 추가
                try {
                    BigDecimal exchangeRate = exchangeRateService.getUSDToKRW();
                    model.addAttribute("exchangeRate", exchangeRate);
                } catch (Exception e) {
                    model.addAttribute("exchangeRate", new BigDecimal("1310.00"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "portfolio/create";
        }
        
        try {
            portfolioVO.setMemberId(member.getMemberId());
            portfolioService.addPortfolio(portfolioVO);
            
            rttr.addFlashAttribute("message", "포트폴리오에 종목이 추가되었습니다.");
            rttr.addFlashAttribute("messageType", "success");
            return "redirect:/portfolio/list";
            
        } catch (Exception e) {
            e.printStackTrace();
            
            try {
                List<Map<String, Object>> stockList = stockService.getAllStocks();
                model.addAttribute("stockList", stockList);
                
                // 환율 정보도 다시 추가
                try {
                    BigDecimal exchangeRate = exchangeRateService.getUSDToKRW();
                    model.addAttribute("exchangeRate", exchangeRate);
                } catch (Exception ex) {
                    model.addAttribute("exchangeRate", new BigDecimal("1310.00"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            if (e.getMessage().contains("중복")) {
                model.addAttribute("error", e.getMessage());
            } else {
                model.addAttribute("error", "포트폴리오 추가 중 오류가 발생했습니다.");
            }
            return "portfolio/create";
        }
    }
    
    /**
     * 포트폴리오 수정 폼
     */
    @GetMapping("/edit/{portfolioId}")
    public String editForm(@PathVariable Long portfolioId,
                          HttpSession session,
                          Model model,
                          RedirectAttributes rttr) {
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";
        }
        
        try {
            PortfolioVO portfolio = portfolioService.getPortfolioById(portfolioId);
            
            // 권한 확인
            if (portfolio == null || portfolio.getMemberId() != member.getMemberId()) {
                rttr.addFlashAttribute("error", "접근 권한이 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            model.addAttribute("portfolioVO", portfolio);
            return "portfolio/edit";
            
        } catch (Exception e) {
            e.printStackTrace();
            rttr.addFlashAttribute("error", "포트폴리오 정보를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/portfolio/list";
        }
    }
    
    /**
     * 포트폴리오 수정 처리
     */
    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute PortfolioVO portfolioVO,
                      BindingResult bindingResult,
                      HttpSession session,
                      Model model,
                      RedirectAttributes rttr) {
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";
        }
        
        if (bindingResult.hasErrors()) {
            return "portfolio/edit";
        }
        
        try {
            // 권한 확인
            PortfolioVO existing = portfolioService.getPortfolioById(portfolioVO.getPortfolioId());
            if (existing == null || existing.getMemberId() != member.getMemberId()) {
                rttr.addFlashAttribute("error", "접근 권한이 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            portfolioService.updatePortfolio(portfolioVO);
            
            rttr.addFlashAttribute("message", "포트폴리오가 수정되었습니다.");
            rttr.addFlashAttribute("messageType", "success");
            return "redirect:/portfolio/list";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "포트폴리오 수정 중 오류가 발생했습니다.");
            return "portfolio/edit";
        }
    }
    
    /**
     * 포트폴리오 삭제
     */
    @PostMapping("/delete/{portfolioId}")
    public String delete(@PathVariable Long portfolioId,
                        HttpSession session,
                        RedirectAttributes rttr) {
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";
        }
        
        try {
            // 권한 확인
            PortfolioVO portfolio = portfolioService.getPortfolioById(portfolioId);
            if (portfolio == null || portfolio.getMemberId() != member.getMemberId()) {
                rttr.addFlashAttribute("error", "접근 권한이 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            portfolioService.deletePortfolio(portfolioId);
            
            rttr.addFlashAttribute("message", "포트폴리오에서 종목이 삭제되었습니다.");
            rttr.addFlashAttribute("messageType", "success");
            
        } catch (Exception e) {
            e.printStackTrace();
            rttr.addFlashAttribute("error", "포트폴리오 삭제 중 오류가 발생했습니다.");
        }
        
        return "redirect:/portfolio/list";
    }
}
