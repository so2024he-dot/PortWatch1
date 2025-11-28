    package com.portwatch.service;

import com.portwatch.domain.WatchlistVO;
import java.util.List;

/**
 * 관심종목 서비스 인터페이스
 * 완전 버전 - 모든 메소드 포함
 */
public interface WatchlistService {
    
    /**
     * 관심종목 추가 (VO 객체 사용)
     */
    void addWatchlist(WatchlistVO watchlist) throws Exception;
    
    /**
     * 관심종목 추가 (memberId, stockCode 사용)
     * Controller에서 사용
     */
    void addToWatchlist(int memberId, String stockCode) throws Exception;
    
    /**
     * 회원의 관심종목 목록 조회
     */
    List<WatchlistVO> getWatchlistByMember(int memberId) throws Exception;
    
    /**
     * 관심종목 존재 여부 확인 (memberId, stockId 사용)
     */
    boolean checkExists(int memberId, int stockId) throws Exception;
    
    /**
     * 관심종목 존재 여부 확인 (memberId, stockCode 사용)
     * Controller에서 사용
     */
    boolean isInWatchlist(int memberId, String stockCode) throws Exception;
    
    /**
     * 관심종목 삭제 (memberId, stockId 사용)
     */
    void deleteWatchlist(int memberId, int stockId) throws Exception;
    
    /**
     * 관심종목 삭제 (watchlistId 사용)
     * Controller에서 사용
     */
    void removeFromWatchlist(int watchlistId) throws Exception;
}

    
