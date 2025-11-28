package com.portwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.portwatch.domain.WatchlistVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.WatchlistDAO;
import com.portwatch.persistence.StockDAO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관심종목 서비스 구현
 * stock_id 기반 (DB 테이블 구조에 맞춤)
 */
@Service
public class WatchlistServiceImpl implements WatchlistService {
    
    @Autowired
    private WatchlistDAO watchlistDAO;
    
    @Autowired
    private StockDAO stockDAO;
    
    /**
     * 관심종목 추가 (VO 객체 사용)
     */
    @Override
    @Transactional
    public void addWatchlist(WatchlistVO watchlist) throws Exception {
        // null 체크
        if (watchlist == null) {
            throw new IllegalArgumentException("관심종목 정보가 없습니다.");
        }
        
        // 필수 값 체크
        if (watchlist.getMemberId() == null || watchlist.getMemberId() <= 0) {
            throw new IllegalArgumentException("회원 ID가 유효하지 않습니다.");
        }
        
        if (watchlist.getStockId() == null || watchlist.getStockId() <= 0) {
            throw new IllegalArgumentException("종목 ID가 유효하지 않습니다.");
        }
        
        // 중복 확인
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("memberId", watchlist.getMemberId());
        params.put("stockId", watchlist.getStockId());
        
        int count = watchlistDAO.checkExists(params);
        if (count > 0) {
            throw new Exception("이미 관심종목에 등록되어 있습니다.");
        }
        
        // 관심종목 추가
        watchlistDAO.insertWatchlist(watchlist);
    }
    
    /**
     * 관심종목 추가 (memberId, stockCode 사용)
     * Controller에서 호출
     */
    @Override
    @Transactional
    public void addToWatchlist(int memberId, String stockCode) throws Exception {
        // 유효성 검사
        if (memberId <= 0) {
            throw new IllegalArgumentException("회원 ID가 유효하지 않습니다.");
        }
        
        if (stockCode == null || stockCode.trim().isEmpty()) {
            throw new IllegalArgumentException("종목 코드가 유효하지 않습니다.");
        }
        
        // stockCode로 종목 찾기 - selectByCode 사용!
        StockVO stock = stockDAO.selectByCode(stockCode);
        if (stock == null) {
            throw new Exception("존재하지 않는 종목입니다: " + stockCode);
        }
        
        // WatchlistVO 생성 - stockId 사용
        WatchlistVO watchlist = new WatchlistVO();
        watchlist.setMemberId(memberId);
        watchlist.setStockId(stock.getStockId());  // stock_id 설정
        
        // 추가
        addWatchlist(watchlist);
    }
    
    /**
     * 회원의 관심종목 목록 조회
     */
    @Override
    public List<WatchlistVO> getWatchlistByMember(int memberId) throws Exception {
        // 유효성 검사
        if (memberId <= 0) {
            throw new IllegalArgumentException("회원 ID가 유효하지 않습니다.");
        }
        
        return watchlistDAO.selectWatchlistByMember(memberId);
    }
    
    /**
     * 관심종목 존재 여부 확인 (memberId, stockId 사용)
     */
    @Override
    public boolean checkExists(int memberId, int stockId) throws Exception {
        // 유효성 검사
        if (memberId <= 0 || stockId <= 0) {
            return false;
        }
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("memberId", memberId);
        params.put("stockId", stockId);
        
        int count = watchlistDAO.checkExists(params);
        return count > 0;
    }
    
    /**
     * 관심종목 존재 여부 확인 (memberId, stockCode 사용)
     * Controller에서 호출
     */
    @Override
    public boolean isInWatchlist(int memberId, String stockCode) throws Exception {
        // 유효성 검사
        if (memberId <= 0) {
            return false;
        }
        
        if (stockCode == null || stockCode.trim().isEmpty()) {
            return false;
        }
        
        // stockCode로 종목 찾기 - selectByCode 사용!
        StockVO stock = stockDAO.selectByCode(stockCode);
        if (stock == null) {
            return false;
        }
        
        // stockId로 존재 여부 확인
        return checkExists(memberId, stock.getStockId());
    }
    
    /**
     * 관심종목 삭제 (memberId, stockId 사용)
     */
    @Override
    @Transactional
    public void deleteWatchlist(int memberId, int stockId) throws Exception {
        // 유효성 검사
        if (memberId <= 0) {
            throw new IllegalArgumentException("회원 ID가 유효하지 않습니다.");
        }
        
        if (stockId <= 0) {
            throw new IllegalArgumentException("종목 ID가 유효하지 않습니다.");
        }
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("memberId", memberId);
        params.put("stockId", stockId);
        
        watchlistDAO.deleteWatchlist(params);
    }
    
    /**
     * 관심종목 삭제 (watchlistId 사용)
     * Controller에서 호출
     */
    @Override
    @Transactional
    public void removeFromWatchlist(int watchlistId) throws Exception {
        // 유효성 검사
        if (watchlistId <= 0) {
            throw new IllegalArgumentException("관심종목 ID가 유효하지 않습니다.");
        }
        
        // watchlistId로 삭제
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("watchlistId", watchlistId);
        
        watchlistDAO.deleteWatchlist(params);
    }
}