package com.portwatch.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.PaymentVO;
import com.portwatch.domain.StockVO;
import com.portwatch.service.PaymentService;
import com.portwatch.service.StockService;

/**
 * 결제 컨트롤러
 * 
 * @author PortWatch
 * @version 1.0 - 한글 인코딩 수정
 */
@Controller
@RequestMapping("/payment")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private StockService stockService;
    
    /**
     * 결제 페이지 이동
     * /payment/checkout?stockId=1&quantity=10&price=75000
     */
    @GetMapping("/checkout")
    public String checkoutPage(
            @RequestParam("stockId") Integer stockId,
            @RequestParam("quantity") BigDecimal quantity,
            @RequestParam("price") BigDecimal price,
            HttpSession session,
            Model model) {
        
        try {
            // 로그인 체크
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            if (loginMember == null) {
                return "redirect:/member/login";
            }
            
            // 종목 정보 조회
            StockVO stock = stockService.getStockById(stockId);
            if (stock == null) {
                model.addAttribute("error", "종목을 찾을 수 없습니다.");
                return "error/404";
            }
            
            // 총 금액 계산
            BigDecimal totalAmount = price.multiply(quantity);
            
            // 국가 및 통화 결정
            String country = "KR";
            String currency = "KRW";
            String pgProvider = "TOSS";
            
            if ("NASDAQ".equals(stock.getMarketType()) || 
                "NYSE".equals(stock.getMarketType()) || 
                "AMEX".equals(stock.getMarketType())) {
                country = "US";
                currency = "USD";
                pgProvider = "STRIPE";
            }
            
            // 모델에 데이터 추가
            model.addAttribute("stock", stock);
            model.addAttribute("quantity", quantity);
            model.addAttribute("price", price);
            model.addAttribute("totalAmount", totalAmount);
            model.addAttribute("country", country);
            model.addAttribute("currency", currency);
            model.addAttribute("pgProvider", pgProvider);
            
            return "payment/checkout";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * 결제 처리 API
     * POST /payment/process
     */
    @PostMapping("/process")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> processPayment(
            @RequestBody Map<String, Object> paymentData,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 로그인 체크
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            if (loginMember == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // PaymentVO 생성
            PaymentVO payment = new PaymentVO();
            payment.setMemberId(loginMember.getMemberId());
            payment.setStockId((Integer) paymentData.get("stockId"));
            payment.setStockCode((String) paymentData.get("stockCode"));
            payment.setStockName((String) paymentData.get("stockName"));
            payment.setQuantity(new BigDecimal(paymentData.get("quantity").toString()));
            payment.setPurchasePrice(new BigDecimal(paymentData.get("price").toString()));
            payment.setTotalAmount(new BigDecimal(paymentData.get("totalAmount").toString()));
            payment.setPaymentMethod((String) paymentData.get("paymentMethod"));
            payment.setCardNumber((String) paymentData.get("cardNumber"));
            payment.setCardCompany((String) paymentData.get("cardCompany"));
            payment.setPgProvider((String) paymentData.get("pgProvider"));
            payment.setCountry((String) paymentData.get("country"));
            payment.setCurrency((String) paymentData.get("currency"));
            
            // 결제 처리
            Long paymentId = paymentService.processPayment(payment);
            
            response.put("success", true);
            response.put("message", "결제가 완료되었습니다.");
            response.put("paymentId", paymentId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "결제 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 결제 내역 조회
     * GET /payment/history
     */
    @GetMapping("/history")
    public String paymentHistory(HttpSession session, Model model) {
        
        try {
            // 로그인 체크
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            if (loginMember == null) {
                return "redirect:/member/login";
            }
            
            // 결제 내역 조회
            List<PaymentVO> payments = paymentService.getPaymentHistory(loginMember.getMemberId());
            Map<String, Object> summary = paymentService.getPaymentSummary(loginMember.getMemberId());
            
            model.addAttribute("payments", payments);
            model.addAttribute("summary", summary);
            
            return "payment/history";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * 결제 취소 API
     * POST /payment/cancel/{paymentId}
     */
    @PostMapping("/cancel/{paymentId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelPayment(
            @PathVariable Long paymentId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 로그인 체크
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            if (loginMember == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // 결제 취소
            paymentService.cancelPayment(paymentId);
            
            response.put("success", true);
            response.put("message", "결제가 취소되었습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "결제 취소 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * PG사 결제 승인 콜백 (Webhook)
     * POST /payment/webhook/approval
     */
    @PostMapping("/webhook/approval")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> paymentApprovalCallback(
            @RequestBody Map<String, Object> callbackData) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long paymentId = Long.parseLong(callbackData.get("paymentId").toString());
            String transactionId = (String) callbackData.get("transactionId");
            
            // 결제 승인 처리
            paymentService.approvePayment(paymentId, transactionId);
            
            response.put("success", true);
            response.put("message", "결제 승인 완료");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
