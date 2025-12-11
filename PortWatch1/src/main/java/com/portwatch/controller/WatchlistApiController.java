package com.portwatch.controller;

import com.portwatch.domain.WatchlistWithPriceVO;
import com.portwatch.persistence.WatchlistDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관심종목 REST API 컨트롤러
 * 
 * ✅ Integer memberId 사용 (타입 통일)
 */
@Controller
@RequestMapping("/api/watchlist")
public class WatchlistApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(WatchlistApiController.class);
    
    @Autowired
    private WatchlistDAO watchlistDAO;
    
    /**
     * 관심종목 전체 가격 조회
     * 
     * GET /api/watchlist/prices
     */
    @RequestMapping(value = "/prices", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllPrices(HttpSession session) {
        logger.info("📊 [API] 관심종목 가격 조회 요청");
        
        Map<String, Object> response = new HashMap<String, Object>();
        
        // ✅ Integer memberId 사용 (세션 속성명 주의!)
        Integer memberId = (Integer) session.getAttribute("memberId");
        // ⚠️ 만약 세션에 "userId"로 저장되어 있다면:
        // Integer memberId = (Integer) session.getAttribute("userId");
        
        if (memberId == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        try {
            List<WatchlistWithPriceVO> watchlist = watchlistDAO.selectWatchlistWithPrices(memberId);
            
            response.put("success", true);
            response.put("message", "가격 조회 성공");
            response.put("count", watchlist.size());
            response.put("data", watchlist);
            
            logger.info("✅ [API] 관심종목 가격 조회 성공: {}개", watchlist.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ [API] 가격 조회 실패", e);
            
            response.put("success", false);
            response.put("message", "가격 조회 중 오류가 발생했습니다");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 특정 관심종목 가격 조회
     * 
     * GET /api/watchlist/{watchlistId}/price
     */
    @RequestMapping(value = "/{watchlistId}/price", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSinglePrice(
            @PathVariable("watchlistId") Integer watchlistId,
            HttpSession session) {
        
        logger.info("📊 [API] 단일 관심종목 가격 조회: watchlistId={}", watchlistId);
        
        Map<String, Object> response = new HashMap<String, Object>();
        
        // ✅ Integer memberId 사용
        Integer memberId = (Integer) session.getAttribute("memberId");
        // ⚠️ 또는: Integer memberId = (Integer) session.getAttribute("userId");
        
        if (memberId == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        try {
            WatchlistWithPriceVO item = watchlistDAO.selectWatchlistWithPriceById(watchlistId);
            
            if (item == null) {
                response.put("success", false);
                response.put("message", "관심종목을 찾을 수 없습니다");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // ✅ getMemberId() 사용
            if (!item.getMemberId().equals(memberId)) {
                response.put("success", false);
                response.put("message", "권한이 없습니다");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            response.put("success", true);
            response.put("message", "가격 조회 성공");
            response.put("data", item);
            
            logger.info("✅ [API] 단일 가격 조회 성공: {}", item.getStockCode());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ [API] 가격 조회 실패", e);
            
            response.put("success", false);
            response.put("message", "가격 조회 중 오류가 발생했습니다");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
