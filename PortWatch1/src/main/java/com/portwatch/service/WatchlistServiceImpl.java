package com.portwatch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.WatchlistVO;
import com.portwatch.persistence.WatchlistDAO;

/**
 * ✅ 관심종목 서비스 구현
 * 
 * @author PortWatch Team
 * @version 1.0
 */
@Service
public class WatchlistServiceImpl implements WatchlistService {
    
    @Autowired
    private WatchlistDAO watchlistDAO;
    
    /**
     * 회원의 관심종목 목록 조회
     */
    @Override
    public List<WatchlistVO> getWatchlistByMemberId(String memberId) {
        
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("회원 ID가 필요합니다.");
        }
        
        try {
            return watchlistDAO.selectByMemberId(memberId);
            
        } catch (Exception e) {
            System.err.println("관심종목 목록 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * 관심종목에 추가
     */
    @Override
    @Transactional
    public boolean addToWatchlist(String memberId, String stockCode) {
        
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("회원 ID가 필요합니다.");
        }
        
        if (stockCode == null || stockCode.trim().isEmpty()) {
            throw new IllegalArgumentException("종목 코드가 필요합니다.");
        }
        
        try {
            // 중복 확인
            if (isInWatchlist(memberId, stockCode)) {
                System.out.println("이미 관심종목에 등록되어 있습니다: " + stockCode);
                return false;
            }
            
            // WatchlistVO 생성
            WatchlistVO watchlist = new WatchlistVO();
            watchlist.setMemberId(memberId);
            watchlist.setStockCode(stockCode);
            
            // 추가
            int result = watchlistDAO.insert(watchlist);
            
            return result > 0;
            
        } catch (Exception e) {
            System.err.println("관심종목 추가 실패: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 관심종목에서 제거
     */
    @Override
    @Transactional
    public boolean removeFromWatchlist(String memberId, String stockCode) {
        
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("회원 ID가 필요합니다.");
        }
        
        if (stockCode == null || stockCode.trim().isEmpty()) {
            throw new IllegalArgumentException("종목 코드가 필요합니다.");
        }
        
        try {
            int result = watchlistDAO.delete(memberId, stockCode);
            return result > 0;
            
        } catch (Exception e) {
            System.err.println("관심종목 제거 실패: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 특정 종목이 관심종목에 있는지 확인
     */
    @Override
    public boolean isInWatchlist(String memberId, String stockCode) {
        
        if (memberId == null || stockCode == null) {
            return false;
        }
        
        try {
            WatchlistVO watchlist = watchlistDAO.selectByMemberAndStock(memberId, stockCode);
            return watchlist != null;
            
        } catch (Exception e) {
            System.err.println("관심종목 확인 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 회원의 관심종목 개수 조회
     */
    @Override
    public int countWatchlist(String memberId) {
        
        if (memberId == null || memberId.trim().isEmpty()) {
            return 0;
        }
        
        try {
            return watchlistDAO.count(memberId);
            
        } catch (Exception e) {
            System.err.println("관심종목 개수 조회 실패: " + e.getMessage());
            return 0;
        }
    }
}
