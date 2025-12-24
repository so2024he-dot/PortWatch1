package com.portwatch.controller;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.StockVO;
import com.portwatch.domain.WatchlistVO;
import com.portwatch.domain.WatchlistWithPriceVO;
import com.portwatch.persistence.StockDAO;
import com.portwatch.persistence.WatchlistDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 관심종목 컨트롤러
 * 
 * @author PortWatch
 * @version 2.0 - 세션 필드명 통일 (loginMember)
 */
@Controller
@RequestMapping("/watchlist")
public class WatchlistController {
    
    private static final Logger logger = LoggerFactory.getLogger(WatchlistController.class);
    
    @Autowired
    private WatchlistDAO watchlistDAO;
    
    @Autowired
    private StockDAO stockDAO;
    
    /**
     * 관심종목 목록 페이지 (현재가 포함)
     * 
     * GET /watchlist/list
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(HttpSession session, Model model) {
        logger.info("📋 관심종목 목록 조회 요청");
        
        // 세션에서 회원 정보 가져오기
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            logger.warn("⚠️ 로그인하지 않은 사용자");
            return "redirect:/member/login";
        }
        
        String memberId = loginMember.getMemberId();
        
        try {
            // ✅ 관심종목 + 현재가 정보 조회
            List<WatchlistWithPriceVO> watchlist = watchlistDAO.selectWatchlistWithPrices(memberId);
            
            logger.info("✅ 관심종목 조회 완료: {}개", watchlist.size());
            
            // 통계 정보 계산
            int totalCount = watchlist.size();
            int koreanStockCount = 0;
            int usStockCount = 0;
            
            for (WatchlistWithPriceVO item : watchlist) {
                if (item.isKoreanStock()) {
                    koreanStockCount++;
                } else if (item.isUSStock()) {
                    usStockCount++;
                }
            }
            
            // Model에 데이터 추가
            model.addAttribute("watchlist", watchlist);
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("koreanStockCount", koreanStockCount);
            model.addAttribute("usStockCount", usStockCount);
            
            return "watchlist/list";
            
        } catch (Exception e) {
            logger.error("❌ 관심종목 조회 실패", e);
            model.addAttribute("errorMessage", "관심종목 조회 중 오류가 발생했습니다");
            return "error";
        }
    }
    
    /**
     * 관심종목 추가 (stockId 사용)
     * 
     * POST /watchlist/add
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String add(
            @RequestParam(value = "stockId", required = false) Integer stockId,
            @RequestParam(value = "stockCode", required = false) String stockCode,
            HttpSession session) {
        
        logger.info("➕ 관심종목 추가 요청: stockId={}, stockCode={}", stockId, stockCode);
        
        // 세션에서 회원 정보 가져오기
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            return "{\"success\": false, \"message\": \"로그인이 필요합니다\"}";
        }
        
        String memberId = loginMember.getMemberId();
        
        try {
            // stockCode로 요청한 경우 stockId 찾기
            if (stockId == null && stockCode != null) {
                StockVO stock = stockDAO.selectByCode(stockCode);
                if (stock == null) {
                    return "{\"success\": false, \"message\": \"종목을 찾을 수 없습니다\"}";
                }
                stockId = stock.getStockId();
                logger.info("   stockCode {} → stockId {}", stockCode, stockId);
            }
            
            if (stockId == null) {
                return "{\"success\": false, \"message\": \"종목 정보가 없습니다\"}";
            }
            
            // 이미 추가되어 있는지 확인
            int exists = watchlistDAO.checkExists(memberId, stockId);
            if (exists > 0) {
                return "{\"success\": false, \"message\": \"이미 관심종목에 추가되어 있습니다\"}";
            }
            
            // 관심종목 추가
            WatchlistVO watchlist = new WatchlistVO();
            watchlist.setMemberId(memberId);
            watchlist.setStockId(stockId);
            
            watchlistDAO.insertWatchlist(watchlist);
            
            logger.info("✅ 관심종목 추가 완료");
            return "{\"success\": true, \"message\": \"관심종목에 추가되었습니다\"}";
            
        } catch (Exception e) {
            logger.error("❌ 관심종목 추가 실패", e);
            return "{\"success\": false, \"message\": \"추가 중 오류가 발생했습니다: " + e.getMessage() + "\"}";
        }
    }
    
    /**
     * 관심종목 삭제
     * 
     * POST /watchlist/delete
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(@RequestParam("watchlistId") Integer watchlistId, HttpSession session) {
        logger.info("🗑️ 관심종목 삭제 요청: watchlistId={}", watchlistId);
        
        // 세션에서 회원 정보 가져오기
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        
        if (loginMember == null) {
            return "{\"success\": false, \"message\": \"로그인이 필요합니다\"}";
        }
        
        String memberId = loginMember.getMemberId();
        
        try {
            // 본인 소유 확인
            WatchlistVO watchlist = watchlistDAO.selectById(watchlistId);
            if (watchlist == null || !watchlist.getMemberId().equals(memberId)) {
                return "{\"success\": false, \"message\": \"권한이 없습니다\"}";
            }
            
            // 삭제
            watchlistDAO.deleteWatchlistById(watchlistId);
            
            logger.info("✅ 관심종목 삭제 완료");
            return "{\"success\": true, \"message\": \"관심종목에서 삭제되었습니다\"}";
            
        } catch (Exception e) {
            logger.error("❌ 관심종목 삭제 실패", e);
            return "{\"success\": false, \"message\": \"삭제 중 오류가 발생했습니다\"}";
        }
    }
}
