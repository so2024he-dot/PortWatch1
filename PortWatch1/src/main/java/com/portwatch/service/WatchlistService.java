package com.portwatch.service;

import java.util.List;
import com.portwatch.domain.WatchlistVO;

/**
 * ✅ 관심종목 서비스 인터페이스
 * 
 * @author PortWatch
 * @version 1.0
 */
public interface WatchlistService {
    
    /**
     * 관심종목 추가 (VO 객체 사용)
     * @param watchlist 관심종목 정보
     * @throws Exception
     */
    void addWatchlist(WatchlistVO watchlist) throws Exception;
    
    /**
     * 관심종목 추가 (memberId, stockCode 사용)
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @throws Exception
     */
    void addToWatchlist(String memberId, String stockCode) throws Exception;
    
    /**
     * 회원의 관심종목 목록 조회
     * @param memberId 회원 ID
     * @return 관심종목 목록
     * @throws Exception
     */
    List<WatchlistVO> getWatchlistByMember(String memberId) throws Exception;
    
    /**
     * 관심종목 존재 여부 확인 (memberId, stockId 사용)
     * @param memberId 회원 ID
     * @param stockId 종목 ID
     * @return 존재 여부
     * @throws Exception
     */
    boolean checkExists(String memberId, Integer stockId) throws Exception;
    
    /**
     * 관심종목 존재 여부 확인 (memberId, stockCode 사용)
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @return 존재 여부
     * @throws Exception
     */
    boolean isInWatchlist(String memberId, String stockCode) throws Exception;
    
    /**
     * 관심종목 삭제 (memberId, stockId 사용)
     * @param memberId 회원 ID
     * @param stockId 종목 ID
     * @throws Exception
     */
    void deleteWatchlist(String memberId, Integer stockId) throws Exception;
    
    /**
     * 관심종목 삭제 (watchlistId 사용)
     * @param watchlistId 관심종목 ID
     * @throws Exception
     */
    void removeFromWatchlist(Integer watchlistId) throws Exception;

	boolean checkWatchlist(String memberId, Integer stockId);
}
