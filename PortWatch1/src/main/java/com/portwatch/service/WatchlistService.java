package com.portwatch.service;

import java.util.List;
import com.portwatch.domain.WatchlistVO;

/**
 * ✅ 관심종목 서비스 인터페이스
 * 
 * @author PortWatch Team
 * @version 1.0
 */
public interface WatchlistService {
    
    /**
     * 회원의 관심종목 목록 조회
     * 
     * @param memberId 회원 ID
     * @return 관심종목 목록
     */
    List<WatchlistVO> getWatchlistByMemberId(String memberId);
    
    /**
     * 관심종목에 추가
     * 
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @return 성공 여부
     */
    boolean addToWatchlist(String memberId, String stockCode);
    
    /**
     * 관심종목에서 제거
     * 
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @return 성공 여부
     */
    boolean removeFromWatchlist(String memberId, String stockCode);
    
    /**
     * 특정 종목이 관심종목에 있는지 확인
     * 
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @return 존재 여부
     */
    boolean isInWatchlist(String memberId, String stockCode);
    
    /**
     * 회원의 관심종목 개수 조회
     * 
     * @param memberId 회원 ID
     * @return 관심종목 개수
     */
    int countWatchlist(String memberId);
}
