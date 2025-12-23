package com.portwatch.persistence;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.portwatch.domain.WatchlistVO;
import com.portwatch.domain.WatchlistWithPriceVO;

/**
 * ✅ 관심종목 DAO 인터페이스 (완전 수정)
 * 
 * 수정 사항:
 * - @Mapper 어노테이션 추가 (필수!)
 * 
 * @author PortWatch
 * @version 3.0
 */
@Mapper  // ✅ MyBatis Mapper 어노테이션 (필수!)
public interface WatchlistDAO {
    
    // ========================================
    // 기본 CRUD
    // ========================================
    
    /**
     * 관심종목 추가
     * @param watchlist 관심종목 정보
     * @return 추가된 행 수
     */
    int insertWatchlist(WatchlistVO watchlist);
    
    /**
     * ID로 조회
     * @param watchlistId 관심종목 ID
     * @return 관심종목 정보
     */
    WatchlistVO selectById(@Param("watchlistId") Integer watchlistId);
    
    /**
     * 회원의 관심종목 목록 조회 (기본)
     * @param memberId 회원 ID
     * @return 관심종목 목록
     */
    List<WatchlistVO> selectWatchlistByMember(@Param("memberId") String memberId);
    
    /**
     * 관심종목 존재 여부 확인
     * @param memberId 회원 ID
     * @param stockId 종목 ID
     * @return 존재하면 1 이상, 없으면 0
     */
    int checkExists(@Param("memberId") String memberId, @Param("stockId") Integer stockId);
    
    /**
     * 관심종목 삭제 (memberId, stockId 사용)
     * @param memberId 회원 ID
     * @param stockId 종목 ID
     * @return 삭제된 행 수
     */
    int deleteWatchlistByMemberAndStock(@Param("memberId") String memberId, @Param("stockId") Integer stockId);
    
    /**
     * 관심종목 삭제 (watchlistId 사용)
     * @param watchlistId 관심종목 ID
     * @return 삭제된 행 수
     */
    int deleteWatchlistById(@Param("watchlistId") Integer watchlistId);
    
    /**
     * 회원의 모든 관심종목 삭제
     * @param memberId 회원 ID
     * @return 삭제된 행 수
     */
    int deleteAllWatchlistByMember(@Param("memberId") String memberId);
    
    // ========================================
    // 가격 정보 포함 조회
    // ========================================
    
    /**
     * ✅ 회원의 관심종목 목록 조회 (현재가 포함)
     * @param memberId 회원 ID
     * @return 관심종목 + 가격 정보 목록
     */
    List<WatchlistWithPriceVO> selectWatchlistWithPrices(@Param("memberId") String memberId);
    
    /**
     * ✅ 특정 관심종목 조회 (현재가 포함)
     * @param watchlistId 관심종목 ID
     * @return 관심종목 + 가격 정보
     */
    WatchlistWithPriceVO selectWatchlistWithPriceById(@Param("watchlistId") Integer watchlistId);
    
    // ========================================
    // 통계
    // ========================================
    
    /**
     * 관심종목 개수 조회
     * @param memberId 회원 ID
     * @return 관심종목 개수
     */
    int countWatchlist(@Param("memberId") String memberId);
}
