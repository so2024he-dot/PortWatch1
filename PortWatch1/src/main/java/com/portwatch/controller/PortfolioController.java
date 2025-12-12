package com.portwatch.controller;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.PortfolioVO;
import com.portwatch.domain.StockVO;
import com.portwatch.service.ExchangeRateService;
import com.portwatch.service.PortfolioService;
import com.portwatch.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Enumeration;
import java.util.List;

/**
 * 포트폴리오 Controller
 * 
 * Spring 5.0.7 RELEASE + MySQL 8.0 완전 호환
 * 추가 매입 지원 + 로그인 세션 문제 해결
 * 
 * @author PortWatch
 * @version 3.2 (StockService 타입 에러 수정)
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
     * 세션에서 memberId 추출 (여러 세션 키 지원)
     * 
     * 가능한 세션 키:
     * - memberId (Integer)
     * - member (MemberVO)
     * - loginUser (MemberVO)
     * - userId (Integer)
     */
    private Integer getMemberIdFromSession(HttpSession session) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 세션 정보 확인 시작");
        
        // 세션의 모든 속성 출력 (디버깅용)
        Enumeration<String> attributeNames = session.getAttributeNames();
        System.out.println("📋 세션에 저장된 모든 속성:");
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            Object value = session.getAttribute(name);
            System.out.println("  - " + name + " = " + value + " (" + (value != null ? value.getClass().getSimpleName() : "null") + ")");
        }
        
        // 1. memberId (Integer) 직접 확인
        Integer memberId = (Integer) session.getAttribute("memberId");
        if (memberId != null) {
            System.out.println("✅ memberId 발견: " + memberId);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return memberId;
        }
        
        // 2. member (MemberVO) 객체에서 추출
        MemberVO member = (MemberVO) session.getAttribute("member");
        if (member != null && member.getMemberId() != null) {
            System.out.println("✅ member 객체에서 memberId 추출: " + member.getMemberId());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return member.getMemberId();
        }
        
        // 3. loginUser (MemberVO) 객체에서 추출
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser != null && loginUser.getMemberId() != null) {
            System.out.println("✅ loginUser 객체에서 memberId 추출: " + loginUser.getMemberId());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return loginUser.getMemberId();
        }
        
        // 4. userId (Integer) 확인
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            System.out.println("✅ userId 발견: " + userId);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return userId;
        }
        
        // 5. user_id (Integer) 확인 (언더스코어 버전)
        Integer user_id = (Integer) session.getAttribute("user_id");
        if (user_id != null) {
            System.out.println("✅ user_id 발견: " + user_id);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return user_id;
        }
        
        System.err.println("❌ 세션에서 memberId를 찾을 수 없음!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return null;
    }
    
    /**
     * 포트폴리오 추가 폼
     */
    @GetMapping("/create")
    public String createForm(HttpSession session, Model model) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📝 포트폴리오 추가 페이지 진입");
        
        // 세션 ID 출력
        System.out.println("🔑 세션 ID: " + session.getId());
        
        // 로그인 체크 (여러 세션 키 지원)
        Integer memberId = getMemberIdFromSession(session);
        if (memberId == null) {
            System.err.println("❌ 로그인 필요 - 로그인 페이지로 리다이렉트");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        System.out.println("✅ 회원 ID 확인: " + memberId);
        
        // 환율 정보 가져오기
        try {
            BigDecimal exchangeRate = exchangeRateService.getUSDToKRW();
            model.addAttribute("exchangeRate", exchangeRate);
            System.out.println("✅ 환율 정보: 1 USD = " + exchangeRate + " KRW");
        } catch (Exception e) {
            System.err.println("⚠️ 환율 조회 실패, 기본값 사용: " + e.getMessage());
            model.addAttribute("exchangeRate", new BigDecimal("1310.00"));
        }
        
        // ✅ 수정: getAllStocks() → getAllStocksList()
        // 전체 종목 목록 조회 (List<StockVO> 반환)
        try {
            List<StockVO> stockList = stockService.getAllStocksList(); // ✅ 타입 일치!
            model.addAttribute("stockList", stockList);
            System.out.println("✅ 전체 종목 수: " + stockList.size());
            
            // 시장별 종목 수 출력 (디버깅)
            long kospiCount = stockList.stream()
                .filter(s -> "KOSPI".equalsIgnoreCase(s.getMarketType()))
                .count();
            long kosdaqCount = stockList.stream()
                .filter(s -> "KOSDAQ".equalsIgnoreCase(s.getMarketType()))
                .count();
            long usCount = stockList.stream()
                .filter(s -> s.getMarketType() != null && 
                       (s.getMarketType().equalsIgnoreCase("NASDAQ") || 
                        s.getMarketType().equalsIgnoreCase("NYSE") ||
                        s.getMarketType().equalsIgnoreCase("AMEX")))
                .count();
            
            System.out.println("  - KOSPI: " + kospiCount + "개");
            System.out.println("  - KOSDAQ: " + kosdaqCount + "개");
            System.out.println("  - 미국 종목: " + usCount + "개");
            
        } catch (Exception e) {
            System.err.println("❌ 종목 목록 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        model.addAttribute("portfolioVO", new PortfolioVO());
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return "portfolio/create";
    }
    
    /**
     * 포트폴리오 추가 처리 (추가 매입 지원)
     */
    @PostMapping("/create")
    public String create(PortfolioVO portfolioVO, 
                        HttpSession session, 
                        RedirectAttributes redirectAttributes) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💾 포트폴리오 추가 요청");
        System.out.println("🔑 세션 ID: " + session.getId());
        
        // 로그인 체크 (여러 세션 키 지원)
        Integer memberId = getMemberIdFromSession(session);
        if (memberId == null) {
            System.err.println("❌ 로그인 필요 - 로그인 페이지로 리다이렉트");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        System.out.println("✅ 회원 ID 확인: " + memberId);
        portfolioVO.setMemberId(memberId);
        
        try {
            // 종목 정보 조회
            StockVO stock = stockService.getStockById(portfolioVO.getStockId());
            if (stock == null) {
                System.err.println("❌ 종목을 찾을 수 없음: " + portfolioVO.getStockId());
                redirectAttributes.addFlashAttribute("error", "종목을 찾을 수 없습니다.");
                return "redirect:/portfolio/create";
            }
            
            System.out.println("📊 종목 정보:");
            System.out.println("  - 종목명: " + stock.getStockName());
            System.out.println("  - 종목코드: " + stock.getStockCode());
            System.out.println("  - 시장: " + stock.getMarketType());
            
            System.out.println("📥 신규 매입 정보:");
            System.out.println("  - 수량: " + portfolioVO.getQuantity());
            System.out.println("  - 매입가: " + portfolioVO.getAvgPurchasePrice());
            System.out.println("  - 매입일: " + portfolioVO.getPurchaseDate());
            
            // 기존 포트폴리오 확인 (추가 매입 체크)
            PortfolioVO existing = portfolioService.getByMemberAndStock(memberId, portfolioVO.getStockId());
            
            if (existing != null) {
                // 추가 매입: 기존 보유 종목에 추가
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("🔄 추가 매입 처리 (기존 보유 종목)");
                System.out.println("📊 기존 보유 정보:");
                System.out.println("  - 보유 수량: " + existing.getQuantity());
                System.out.println("  - 평균 매입가: " + existing.getAvgPurchasePrice());
                
                // 기존 총 매입액 = 기존 수량 × 기존 평균 매입가
                BigDecimal existingTotal = existing.getQuantity()
                    .multiply(existing.getAvgPurchasePrice());
                System.out.println("  - 기존 총 매입액: " + existingTotal);
                
                // 신규 총 매입액 = 신규 수량 × 신규 매입가
                BigDecimal newTotal = portfolioVO.getQuantity()
                    .multiply(portfolioVO.getAvgPurchasePrice());
                System.out.println("  - 신규 총 매입액: " + newTotal);
                
                // 합산 수량 = 기존 수량 + 신규 수량
                BigDecimal totalQuantity = existing.getQuantity()
                    .add(portfolioVO.getQuantity());
                System.out.println("  - 합산 수량: " + totalQuantity);
                
                // 새로운 평균 매입가 = (기존 총액 + 신규 총액) / 합산 수량
                BigDecimal newAvgPrice = existingTotal
                    .add(newTotal)
                    .divide(totalQuantity, 2, RoundingMode.HALF_UP);
                System.out.println("  - 새로운 평균 매입가: " + newAvgPrice);
                
                // 기존 레코드 업데이트
                existing.setQuantity(totalQuantity);
                existing.setAvgPurchasePrice(newAvgPrice);
                existing.setPurchaseDate(portfolioVO.getPurchaseDate());
                
                portfolioService.update(existing);
                
                System.out.println("✅ 추가 매입 완료!");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                redirectAttributes.addFlashAttribute("successMessage", 
                    String.format("✅ %s를 추가 매입했습니다! (총 %s주, 평균가 %s)", 
                        stock.getStockName(), 
                        totalQuantity.stripTrailingZeros().toPlainString(),
                        newAvgPrice.stripTrailingZeros().toPlainString()));
                
            } else {
                // 신규 매입: 새로운 종목 추가
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("🆕 신규 매입 처리 (새로운 종목)");
                
                portfolioService.insert(portfolioVO);
                
                System.out.println("✅ 신규 매입 완료!");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                redirectAttributes.addFlashAttribute("successMessage", 
                    String.format("✅ %s를 포트폴리오에 추가했습니다! (%s주, 매입가 %s)", 
                        stock.getStockName(), 
                        portfolioVO.getQuantity().stripTrailingZeros().toPlainString(),
                        portfolioVO.getAvgPurchasePrice().stripTrailingZeros().toPlainString()));
            }
            
            return "redirect:/portfolio/list";
            
        } catch (Exception e) {
            System.err.println("❌ 포트폴리오 추가 실패");
            e.printStackTrace();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            redirectAttributes.addFlashAttribute("error", 
                "포트폴리오 추가 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/portfolio/create";
        }
    }
    
    /**
     * 포트폴리오 목록
     */
    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📋 포트폴리오 목록 페이지 진입");
        System.out.println("🔑 세션 ID: " + session.getId());
        
        // 로그인 체크 (여러 세션 키 지원)
        Integer memberId = getMemberIdFromSession(session);
        if (memberId == null) {
            System.err.println("❌ 로그인 필요 - 로그인 페이지로 리다이렉트");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return "redirect:/member/login";
        }
        
        System.out.println("✅ 회원 ID 확인: " + memberId);
        
        try {
            // 환율 정보
            BigDecimal exchangeRate = exchangeRateService.getUSDToKRW();
            model.addAttribute("exchangeRate", exchangeRate);
        } catch (Exception e) {
            model.addAttribute("exchangeRate", new BigDecimal("1310.00"));
        }
        
        List<PortfolioVO> portfolioList = portfolioService.getByMember(memberId);
        model.addAttribute("portfolioList", portfolioList);
        
        System.out.println("✅ 포트폴리오 개수: " + portfolioList.size());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return "portfolio/list";
    }
}
