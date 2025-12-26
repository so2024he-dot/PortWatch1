package com.portwatch.persistence;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.portwatch.domain.WatchlistVO;

/**
 * ✅ 관심종목 DAO 인터페이스
 * 
 * @author PortWatch Team
 * @version 1.0
 */
public interface WatchlistDAO {
    
    /**
     * 회원의 관심종목 목록 조회 (주식 정보 포함)
     * 
     * @param memberId 회원 ID
     * @return 관심종목 목록
     */
    List<WatchlistVO> selectByMemberId(String memberId);
    
    /**
     * 특정 관심종목 조회 (회원 + 종목 코드)
     * 
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @return 관심종목 VO (없으면 null)
     */
    WatchlistVO selectByMemberAndStock(@Param("memberId") String memberId, 
                                       @Param("stockCode") String stockCode);
    
    /**
     * 관심종목 추가
     * 
     * @param watchlist 관심종목 VO
     * @return 추가된 행 수
     */
    int insert(WatchlistVO watchlist);
    
    /**
     * 관심종목 삭제 (ID로)
     * 
     * @param watchlistId 관심종목 ID
     * @return 삭제된 행 수
     */
    int deleteById(Long watchlistId);
    
    /**
     * 관심종목 삭제 (회원 + 종목 코드)
     * 
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @return 삭제된 행 수
     */
    int delete(@Param("memberId") String memberId, 
               @Param("stockCode") String stockCode);
    
    /**
     * 회원의 모든 관심종목 삭제
     * 
     * @param memberId 회원 ID
     * @return 삭제된 행 수
     */
    int deleteByMemberId(String memberId);
    
    /**
     * 회원의 관심종목 개수
     * 
     * @param memberId 회원 ID
     * @return 관심종목 개수
     */
    int count(String memberId);
    
    /**
     * 특정 종목을 관심종목으로 추가한 회원 수
     * 
     * @param stockCode 종목 코드
     * @return 회원 수
     */
    int countByStockCode(String stockCode);
    
    /**
     * 관심종목 여부 확인
     * 
     * @param memberId 회원 ID
     * @param stockCode 종목 코드
     * @return 존재 여부 (1: 존재, 0: 없음)
     */
    int exists(@Param("memberId") String memberId, 
               @Param("stockCode") String stockCode);
    
    /**
     * 특정 국가의 관심종목만 조회
     * 
     * @param memberId 회원 ID
     * @param country 국가 (KR, US)
     * @return 관심종목 목록
     */
    List<WatchlistVO> selectByMemberIdAndCountry(@Param("memberId") String memberId, 
                                                  @Param("country") String country);
    
    /**
     * 특정 시장의 관심종목만 조회
     * 
     * @param memberId 회원 ID
     * @param marketType 시장 타입 (KOSPI, KOSDAQ, NASDAQ, NYSE)
     * @return 관심종목 목록
     */
    List<WatchlistVO> selectByMemberIdAndMarket(@Param("memberId") String memberId, 
                                                 @Param("marketType") String marketType);
    
    /**
     * 최근 추가된 관심종목 조회 (N개)
     * 
     * @param memberId 회원 ID
     * @param limit 조회 개수
     * @return 관심종목 목록
     */
    List<WatchlistVO> selectRecentByMemberId(@Param("memberId") String memberId, 
                                              @Param("limit") int limit);
}
