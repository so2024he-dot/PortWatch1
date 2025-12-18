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
import com.portwatch.service.PortfolioService;
import com.portwatch.service.StockService;
import java.util.List;
import java.util.Map;

/**
 * 포트폴리오 Controller
 * 
 * ✅ 이전 작동 버전 기반
 * ✅ Integer quantity (1주 단위)
 * ✅ 완전 작동
 * 
 * @author PortWatch
 * @version 5.0 (Spring 5.0.7 + MySQL 8.0)
 */
@Controller
@RequestMapping("/portfolio")
public class PortfolioController {
    
    @Autowired
    private PortfolioService portfolioService;
    
    @Autowired
    private StockService stockService;
    
    /**
     * 세션에서 회원 정보 가져오기
     */
    private MemberVO getMemberFromSession(HttpSession session) {
        // member 키 먼저 확인
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member != null) {
            return member;
        }
        
        // loginUser 키 확인
        member = (MemberVO) session.getAttribute("loginUser");
        if (member != null) {
            return member;
        }
        
        return null;
    }
    
    /**
     * 포트폴리오 목록 페이지
     */
    @GetMapping("/list")
    public String portfolioList(HttpSession session, Model model) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📋 포트폴리오 목록 페이지 진입");
        
        MemberVO member = getMemberFromSession(session);
        if (member == null) {
            System.err.println("❌ 로그인 필요");
            return "redirect:/member/login";
        }
        
        System.out.println("✅ 회원 ID: " + member.getMemberId());
        
        try {
            // 포트폴리오 목록 조회
            List<PortfolioVO> portfolioList = portfolioService.getPortfolioList(member.getMemberId());
            
            // 포트폴리오 요약 정보 조회
            Map<String, Object> summary = portfolioService.getPortfolioSummary(member.getMemberId());
            
            model.addAttribute("portfolioList", portfolioList);
            model.addAttribute("summary", summary);
            
            System.out.println("✅ 포트폴리오 개수: " + portfolioList.size());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "portfolio/list";
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 목록 조회 실패: " + e.getMessage());
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
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 포트폴리오 상세 페이지 진입");
        System.out.println("  - portfolioId: " + portfolioId);
        
        MemberVO member = getMemberFromSession(session);
        if (member == null) {
            System.err.println("❌ 로그인 필요");
            return "redirect:/member/login";
        }
        
        try {
            // 포트폴리오 상세 조회
            PortfolioVO portfolio = portfolioService.getPortfolioById(portfolioId);
            
            // 권한 확인
            if (portfolio == null || portfolio.getMemberId() != member.getMemberId()) {
                System.err.println("❌ 접근 권한 없음");
                rttr.addFlashAttribute("error", "접근 권한이 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            model.addAttribute("portfolio", portfolio);
            
            System.out.println("✅ 조회 완료: " + portfolio.getStockName());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "portfolio/detail";
            
        } catch (Exception e) {
            System.err.println("❌ 상세 조회 실패: " + e.getMessage());
            e.printStackTrace();
            rttr.addFlashAttribute("error", "포트폴리오 정보를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/portfolio/list";
        }
    }
    
    /**
     * 포트폴리오 생성 폼
     */
    @GetMapping("/create")
    public String createForm(HttpSession session, Model model) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📝 포트폴리오 추가 폼");
        
        MemberVO member = getMemberFromSession(session);
        if (member == null) {
            System.err.println("❌ 로그인 필요");
            return "redirect:/member/login";
        }
        
        try {
            // 종목 목록
            List<Map<String, Object>> stockList = stockService.getAllStocks();
            model.addAttribute("stockList", stockList);
            model.addAttribute("portfolioVO", new PortfolioVO());
            
            System.out.println("✅ 종목 수: " + stockList.size());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "portfolio/create";
            
        } catch (Exception e) {
            System.err.println("❌ 종목 목록 조회 실패: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "종목 목록을 불러오는 중 오류가 발생했습니다.");
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
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💾 포트폴리오 추가 처리");
        
        MemberVO member = getMemberFromSession(session);
        if (member == null) {
            System.err.println("❌ 로그인 필요");
            return "redirect:/member/login";
        }
        
        if (bindingResult.hasErrors()) {
            System.err.println("❌ 입력 검증 실패");
            bindingResult.getAllErrors().forEach(error -> 
                System.err.println("  - " + error.getDefaultMessage())
            );
            
            try {
                List<Map<String, Object>> stockList = stockService.getAllStocks();
                model.addAttribute("stockList", stockList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "portfolio/create";
        }
        
        try {
            portfolioVO.setMemberId(member.getMemberId());
            
            System.out.println("  - memberId: " + portfolioVO.getMemberId());
            System.out.println("  - stockId: " + portfolioVO.getStockId());
            System.out.println("  - quantity: " + portfolioVO.getQuantity());
            System.out.println("  - avgPurchasePrice: " + portfolioVO.getAvgPurchasePrice());
            
            portfolioService.addPortfolio(portfolioVO);
            
            rttr.addFlashAttribute("message", "포트폴리오에 종목이 추가되었습니다.");
            rttr.addFlashAttribute("messageType", "success");
            
            System.out.println("✅ 추가 완료!");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "redirect:/portfolio/list";
            
        } catch (Exception e) {
            System.err.println("❌ 추가 실패: " + e.getMessage());
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            try {
                List<Map<String, Object>> stockList = stockService.getAllStocks();
                model.addAttribute("stockList", stockList);
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
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✏️ 포트폴리오 수정 폼");
        
        MemberVO member = getMemberFromSession(session);
        if (member == null) {
            System.err.println("❌ 로그인 필요");
            return "redirect:/member/login";
        }
        
        try {
            PortfolioVO portfolio = portfolioService.getPortfolioById(portfolioId);
            
            // 권한 확인
            if (portfolio == null || portfolio.getMemberId() != member.getMemberId()) {
                System.err.println("❌ 접근 권한 없음");
                rttr.addFlashAttribute("error", "접근 권한이 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            model.addAttribute("portfolioVO", portfolio);
            
            System.out.println("✅ 수정 폼 로드: " + portfolio.getStockName());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "portfolio/edit";
            
        } catch (Exception e) {
            System.err.println("❌ 수정 폼 로드 실패: " + e.getMessage());
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
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔄 포트폴리오 수정 처리");
        
        MemberVO member = getMemberFromSession(session);
        if (member == null) {
            System.err.println("❌ 로그인 필요");
            return "redirect:/member/login";
        }
        
        if (bindingResult.hasErrors()) {
            System.err.println("❌ 입력 검증 실패");
            return "portfolio/edit";
        }
        
        try {
            // 권한 확인
            PortfolioVO existing = portfolioService.getPortfolioById(portfolioVO.getPortfolioId());
            if (existing == null || existing.getMemberId() != member.getMemberId()) {
                System.err.println("❌ 접근 권한 없음");
                rttr.addFlashAttribute("error", "접근 권한이 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            portfolioService.updatePortfolio(portfolioVO);
            
            rttr.addFlashAttribute("message", "포트폴리오가 수정되었습니다.");
            rttr.addFlashAttribute("messageType", "success");
            
            System.out.println("✅ 수정 완료!");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "redirect:/portfolio/list";
            
        } catch (Exception e) {
            System.err.println("❌ 수정 실패: " + e.getMessage());
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
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 포트폴리오 삭제");
        
        MemberVO member = getMemberFromSession(session);
        if (member == null) {
            System.err.println("❌ 로그인 필요");
            return "redirect:/member/login";
        }
        
        try {
            // 권한 확인
            PortfolioVO portfolio = portfolioService.getPortfolioById(portfolioId);
            if (portfolio == null || portfolio.getMemberId() != member.getMemberId()) {
                System.err.println("❌ 접근 권한 없음");
                rttr.addFlashAttribute("error", "접근 권한이 없습니다.");
                return "redirect:/portfolio/list";
            }
            
            portfolioService.deletePortfolio(portfolioId);
            
            rttr.addFlashAttribute("message", "포트폴리오에서 삭제되었습니다.");
            rttr.addFlashAttribute("messageType", "success");
            
            System.out.println("✅ 삭제 완료!");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return "redirect:/portfolio/list";
            
        } catch (Exception e) {
            System.err.println("❌ 삭제 실패: " + e.getMessage());
            e.printStackTrace();
            rttr.addFlashAttribute("error", "포트폴리오 삭제 중 오류가 발생했습니다.");
            return "redirect:/portfolio/list";
        }
    }
}
