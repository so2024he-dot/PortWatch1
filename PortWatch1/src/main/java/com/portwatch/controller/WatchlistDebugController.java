package com.portwatch.controller;

import com.portwatch.domain.WatchlistVO;
import com.portwatch.domain.WatchlistWithPriceVO;
import com.portwatch.persistence.WatchlistDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🔍 관심종목 디버깅 컨트롤러
 * 
 * 문제 해결용 임시 컨트롤러
 * 
 * 엔드포인트:
 * - GET /watchlist/debug/session : 세션 정보 확인
 * - GET /watchlist/debug/data : 데이터 확인
 * - GET /watchlist/debug/full : 전체 진단
 */
@Controller
@RequestMapping("/watchlist/debug")
public class WatchlistDebugController {
    
    private static final Logger logger = LoggerFactory.getLogger(WatchlistDebugController.class);
    
    @Autowired
    private WatchlistDAO watchlistDAO;
    
    /**
     * 세션 정보 확인
     * 
     * GET /watchlist/debug/session
     */
    @RequestMapping(value = "/session", method = RequestMethod.GET)
    @ResponseBody
    public String checkSession(HttpSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 세션 정보 확인 ===\n\n");
        
        // 세션 ID
        sb.append("세션 ID: ").append(session.getId()).append("\n\n");
        
        // 모든 세션 속성 출력
        sb.append("=== 세션 속성 목록 ===\n");
        Enumeration<String> attributeNames = session.getAttributeNames();
        
        boolean hasAttributes = false;
        while (attributeNames.hasMoreElements()) {
            hasAttributes = true;
            String name = attributeNames.nextElement();
            Object value = session.getAttribute(name);
            sb.append("- ").append(name).append(" = ").append(value)
              .append(" (타입: ").append(value.getClass().getSimpleName()).append(")\n");
        }
        
        if (!hasAttributes) {
            sb.append("⚠️ 세션 속성이 비어있습니다! (로그인 안 됨)\n");
        }
        
        sb.append("\n");
        
        // memberId 확인
        Object memberId = session.getAttribute("memberId");
        Object userId = session.getAttribute("userId");
        
        sb.append("=== 로그인 정보 확인 ===\n");
        sb.append("memberId = ").append(memberId).append("\n");
        sb.append("userId = ").append(userId).append("\n");
        
        if (memberId == null && userId == null) {
            sb.append("\n❌ 에러: memberId와 userId 모두 없습니다!\n");
            sb.append("→ 로그인이 제대로 되지 않았거나, 세션 속성명이 다릅니다.\n");
        } else if (memberId != null) {
            sb.append("\n✅ memberId 발견: ").append(memberId).append("\n");
        } else if (userId != null) {
            sb.append("\n✅ userId 발견: ").append(userId).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 데이터베이스 데이터 확인
     * 
     * GET /watchlist/debug/data?memberId=1
     */
    @RequestMapping(value = "/data", method = RequestMethod.GET)
    @ResponseBody
    public String checkData(HttpSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 데이터베이스 확인 ===\n\n");
        
        // 세션에서 memberId 가져오기 (여러 방법 시도)
        Integer memberId = null;
        
        Object memberIdObj = session.getAttribute("memberId");
        Object userIdObj = session.getAttribute("userId");
        
        if (memberIdObj != null) {
            if (memberIdObj instanceof Integer) {
                memberId = (Integer) memberIdObj;
            } else if (memberIdObj instanceof Long) {
                memberId = ((Long) memberIdObj).intValue();
            }
            sb.append("✅ memberId 사용: ").append(memberId).append("\n\n");
        } else if (userIdObj != null) {
            if (userIdObj instanceof Integer) {
                memberId = (Integer) userIdObj;
            } else if (userIdObj instanceof Long) {
                memberId = ((Long) userIdObj).intValue();
            }
            sb.append("✅ userId 사용: ").append(memberId).append("\n\n");
        } else {
            sb.append("❌ 에러: 세션에 memberId/userId 없음!\n");
            sb.append("→ 먼저 /watchlist/debug/session을 확인하세요.\n");
            return sb.toString();
        }
        
        try {
            // 기본 관심종목 조회
            sb.append("=== 기본 관심종목 조회 ===\n");
            List<WatchlistVO> basicList = watchlistDAO.selectWatchlistByMember(memberId);
            sb.append("조회 결과: ").append(basicList.size()).append("개\n");
            
            if (basicList.isEmpty()) {
                sb.append("⚠️ 관심종목이 비어있습니다!\n");
                sb.append("→ DB에 데이터를 추가하거나, 관심종목 추가 기능을 테스트하세요.\n\n");
            } else {
                for (WatchlistVO vo : basicList) {
                    sb.append("  - watchlistId=").append(vo.getWatchlistId())
                      .append(", stockId=").append(vo.getStockId())
                      .append(", addedAt=").append(vo.getAddedAt()).append("\n");
                }
                sb.append("\n");
            }
            
            // 현재가 포함 조회
            sb.append("=== 현재가 포함 관심종목 조회 ===\n");
            List<WatchlistWithPriceVO> priceList = watchlistDAO.selectWatchlistWithPrices(memberId);
            sb.append("조회 결과: ").append(priceList.size()).append("개\n");
            
            if (priceList.isEmpty()) {
                sb.append("⚠️ 현재가 포함 조회도 비어있습니다!\n");
                sb.append("→ DB 데이터를 확인하세요.\n\n");
            } else {
                for (WatchlistWithPriceVO vo : priceList) {
                    sb.append("  - ").append(vo.getStockCode())
                      .append(" (").append(vo.getStockName()).append(")")
                      .append(", 현재가=").append(vo.getCurrentPrice())
                      .append(", 시장=").append(vo.getMarketType()).append("\n");
                }
                sb.append("\n✅ 데이터가 정상적으로 조회됩니다!\n");
                sb.append("→ Controller나 JSP 문제일 가능성이 높습니다.\n");
            }
            
        } catch (Exception e) {
            sb.append("\n❌ 에러 발생!\n");
            sb.append("에러 메시지: ").append(e.getMessage()).append("\n");
            sb.append("에러 타입: ").append(e.getClass().getName()).append("\n");
            
            logger.error("디버그 중 에러 발생", e);
        }
        
        return sb.toString();
    }
    
    /**
     * 전체 진단 (세션 + 데이터)
     * 
     * GET /watchlist/debug/full
     */
    @RequestMapping(value = "/full", method = RequestMethod.GET, produces = "text/plain; charset=UTF-8")
    @ResponseBody
    public String fullDiagnosis(HttpSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================\n");
        sb.append("🔍 관심종목 전체 진단\n");
        sb.append("====================================\n\n");
        
        // 1. 세션 확인
        sb.append(checkSession(session));
        sb.append("\n\n");
        
        // 2. 데이터 확인
        sb.append(checkData(session));
        sb.append("\n\n");
        
        sb.append("====================================\n");
        sb.append("진단 완료!\n");
        sb.append("====================================\n");
        
        return sb.toString();
    }
}
