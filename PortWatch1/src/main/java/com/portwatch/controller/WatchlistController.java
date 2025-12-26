package com.portwatch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.WatchlistVO;
import com.portwatch.service.WatchlistService;

/**
 * ✅ 관심종목 컨트롤러 (신규 생성)
 * 
 * @author PortWatch Team
 * @version 1.0
 */
@Controller
@RequestMapping("/watchlist")
public class WatchlistController {
    
    @Autowired(required = false)
    private WatchlistService watchlistService;
    
    /**
     * ✅ 관심종목 목록 페이지
     * GET /watchlist/list
     */
    @GetMapping("/list")
    public String watchlistPage(HttpSession session, Model model) {
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("⭐ 관심종목 목록 조회");
        
        // 로그인 체크
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            System.out.println("❌ 로그인 필요");
            return "redirect:/member/login";
        }
        
        try {
            String memberId = loginMember.getMemberId();
            System.out.println("  - 회원 ID: " + memberId);
            
            if (watchlistService != null) {
                // 관심종목 목록 조회
                List<WatchlistVO> watchlist = watchlistService.getWatchlistByMemberId(memberId);
                
                System.out.println("  - 관심종목 개수: " + watchlist.size());
                System.out.println("✅ 관심종목 조회 완료");
                
                model.addAttribute("watchlist", watchlist);
            } else {
                System.out.println("⚠️ WatchlistService is null");
                model.addAttribute("watchlist", new java.util.ArrayList<>());
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            model.addAttribute("loginMember", loginMember);
            return "watchlist/list";
            
        } catch (Exception e) {
            System.err.println("❌ 관심종목 조회 실패: " + e.getMessage());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "관심종목 조회 중 오류가 발생했습니다.");
            model.addAttribute("watchlist", new java.util.ArrayList<>());
            return "watchlist/list";
        }
    }
    
    /**
     * 관심종목 추가 (AJAX)
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addWatchlist(
            @RequestParam("stockCode") String stockCode,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            
            if (loginMember == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (watchlistService == null) {
                response.put("success", false);
                response.put("message", "WatchlistService를 사용할 수 없습니다.");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
            }
            
            String memberId = loginMember.getMemberId();
            
            boolean added = watchlistService.addToWatchlist(memberId, stockCode);
            
            if (added) {
                response.put("success", true);
                response.put("message", "관심종목에 추가되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "이미 관심종목에 등록되어 있습니다.");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 관심종목 삭제 (AJAX)
     */
    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteWatchlist(
            @RequestParam("stockCode") String stockCode,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            
            if (loginMember == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (watchlistService == null) {
                response.put("success", false);
                response.put("message", "WatchlistService를 사용할 수 없습니다.");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
            }
            
            String memberId = loginMember.getMemberId();
            
            boolean deleted = watchlistService.removeFromWatchlist(memberId, stockCode);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "관심종목에서 삭제되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "삭제에 실패했습니다.");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 관심종목 전체 조회 (AJAX)
     */
    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllWatchlist(HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
            
            if (loginMember == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (watchlistService == null) {
                response.put("success", false);
                response.put("message", "WatchlistService를 사용할 수 없습니다.");
                response.put("watchlist", new java.util.ArrayList<>());
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
            }
            
            String memberId = loginMember.getMemberId();
            
            List<WatchlistVO> watchlist = watchlistService.getWatchlistByMemberId(memberId);
            
            response.put("success", true);
            response.put("watchlist", watchlist);
            response.put("count", watchlist.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "오류 발생: " + e.getMessage());
            response.put("watchlist", new java.util.ArrayList<>());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
