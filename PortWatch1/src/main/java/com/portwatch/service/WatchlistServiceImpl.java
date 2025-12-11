package com.portwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.portwatch.domain.WatchlistVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.WatchlistDAO;
import com.portwatch.persistence.StockDAO;
import java.util.List;

/**
 * 관심종목 서비스 구현
 * 
 * ✅ 완벽 수정 버전 - Map 제거, @Param 사용
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
        
        // ✅ 중복 확인 - @Param 방식
        int count = watchlistDAO.checkExists(watchlist.getMemberId(), watchlist.getStockId());
        if (count > 0) {
            throw new Exception("이미 관심종목에 등록되어 있습니다.");
        }
        
        // ✅ 관심종목 추가 - 이전 버전은 이 부분이 빠져있었음!
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
        
        // stockCode로 종목 찾기
        StockVO stock = stockDAO.selectByCode(stockCode);
        if (stock == null) {
            throw new Exception("존재하지 않는 종목입니다: " + stockCode);
        }
        
        // WatchlistVO 생성
        WatchlistVO watchlist = new WatchlistVO();
        watchlist.setMemberId(memberId);
        watchlist.setStockId(stock.getStockId());
        
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
        
        // ✅ @Param 방식
        int count = watchlistDAO.checkExists(memberId, stockId);
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
        
        // stockCode로 종목 찾기
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
        
        // ✅ @Param 방식
        watchlistDAO.deleteWatchlistByMemberAndStock(memberId, stockId);
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
        
        // ✅ watchlistId로 직접 삭제
        watchlistDAO.deleteWatchlistById(watchlistId);
    }
}
