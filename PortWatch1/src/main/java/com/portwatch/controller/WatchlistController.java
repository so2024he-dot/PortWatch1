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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.WatchlistVO;
import com.portwatch.service.WatchlistService;

/**
 * 관심종목 Controller
 * API 없이 완전 작동
 */
@Controller
@RequestMapping("/watchlist")
public class WatchlistController {
    
    @Autowired
    private WatchlistService watchlistService;
    
    /**
     * 관심종목 목록 페이지
     */
    @GetMapping("/list")
    public String list(HttpSession session, Model model, RedirectAttributes rttr) {
        MemberVO member = (MemberVO) session.getAttribute("member");
        
        if (member == null) {
            rttr.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }
        
        try {
            List<WatchlistVO> watchlist = watchlistService.getWatchlistByMember(member.getMemberId());
            model.addAttribute("watchlist", watchlist);
            return "watchlist/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "관심종목 목록을 불러오는 중 오류가 발생했습니다.");
            return "watchlist/list";
        }
    }
    
    /**
     * 관심종목 추가 (AJAX)
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> add(
            @RequestParam String stockCode,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        MemberVO member = (MemberVO) session.getAttribute("member");
        
        if (member == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        try {
            // 이미 추가되어 있는지 확인
            if (watchlistService.isInWatchlist(member.getMemberId(), stockCode)) {
                response.put("success", false);
                response.put("message", "이미 관심종목에 추가되어 있습니다.");
                return ResponseEntity.ok(response);
            }
            
            // 관심종목 추가
            watchlistService.addToWatchlist(member.getMemberId(), stockCode);
            
            response.put("success", true);
            response.put("message", "관심종목에 추가되었습니다.");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "관심종목 추가 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 관심종목 삭제
     */
    @PostMapping("/remove/{watchlistId}")
    public String remove(
            @PathVariable Integer watchlistId,
            HttpSession session,
            RedirectAttributes rttr) {
        
        MemberVO member = (MemberVO) session.getAttribute("member");
        
        if (member == null) {
            rttr.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }
        
        try {
            watchlistService.removeFromWatchlist(watchlistId);
            rttr.addFlashAttribute("message", "관심종목에서 삭제되었습니다.");
            rttr.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            e.printStackTrace();
            rttr.addFlashAttribute("error", "관심종목 삭제 중 오류가 발생했습니다.");
        }
        
        return "redirect:/watchlist/list";
    }
    
    /**
     * 관심종목 삭제 (AJAX)
     */
    @PostMapping("/remove/ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeAjax(
            @RequestParam Integer watchlistId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        MemberVO member = (MemberVO) session.getAttribute("member");
        
        if (member == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        try {
            watchlistService.removeFromWatchlist(watchlistId);
            response.put("success", true);
            response.put("message", "관심종목에서 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "삭제 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 관심종목 여부 확인 (AJAX)
     */
    @GetMapping("/check")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> check(
            @RequestParam String stockCode,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        MemberVO member = (MemberVO) session.getAttribute("member");
        
        if (member == null) {
            response.put("isInWatchlist", false);
            return ResponseEntity.ok(response);
        }
        
        try {
            boolean isInWatchlist = watchlistService.isInWatchlist(member.getMemberId(), stockCode);
            response.put("isInWatchlist", isInWatchlist);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("isInWatchlist", false);
            return ResponseEntity.ok(response);
        }
    }
}
