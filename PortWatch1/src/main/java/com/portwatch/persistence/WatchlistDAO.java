package com.portwatch.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.portwatch.domain.WatchlistVO;
import com.portwatch.domain.WatchlistWithPriceVO;

import java.util.List;

/**
 * 관심종목 DAO 인터페이스
 * MyBatis가 자동으로 구현체를 생성
 * 
 * memberId 기반 (DB의 member_id 컬럼)
 */
@Mapper  // ⭐ MyBatis Mapper 인터페이스임을 명시
public interface WatchlistDAO {
    
    // ========================================
    // 기본 CRUD
    // ========================================
    
    /**
     * 관심종목 추가
     */
    void insertWatchlist(WatchlistVO watchlist);
    
    /**
     * 관심종목 삭제 (watchlistId로)
     */
    void deleteWatchlistById(Integer watchlistId);
    
    /**
     * 관심종목 삭제 (memberId + stockId로)
     */
    void deleteWatchlistByMemberAndStock(@Param("memberId") Integer memberId, 
                                         @Param("stockId") Integer stockId);
    
    /**
     * 관심종목 ID로 조회
     */
    WatchlistVO selectById(Integer watchlistId);
    
    /**
     * 회원의 관심종목 목록 조회 (기본 정보만)
     */
    List<WatchlistVO> selectWatchlistByMember(Integer memberId);
    
    /**
     * 관심종목 존재 여부 확인
     * 
     * @return 존재하면 1 이상, 없으면 0
     */
    int checkExists(@Param("memberId") Integer memberId, 
                    @Param("stockId") Integer stockId);
    
    // ========================================
    // ✅ 현재가 포함 조회
    // ========================================
    
    /**
     * 회원의 관심종목 + 현재가 목록 조회
     */
    List<WatchlistWithPriceVO> selectWatchlistWithPrices(Integer memberId);
    
    /**
     * 특정 관심종목 + 현재가 조회
     */
    WatchlistWithPriceVO selectWatchlistWithPriceById(Integer watchlistId);
}
