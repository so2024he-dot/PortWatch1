package com.portwatch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.portwatch.domain.WatchlistVO;
import com.portwatch.domain.WatchlistWithPriceVO;

/**
 * WatchlistMapper Interface
 * ══════════════════════════════════════════════════════════════
 * ✅ 누락된 Mapper 인터페이스 추가
 * - 콘솔 로그에서 WatchlistMapper가 스캔되지 않음
 * ══════════════════════════════════════════════════════════════
 */
public interface WatchlistMapper {

    // 기본 CRUD
    int insert(WatchlistVO watchlist);
    WatchlistVO findById(Long watchlistId);
    List<WatchlistVO> findByMemberId(String memberId);
    int delete(Long watchlistId);
    int deleteByStockCode(@Param("memberId") String memberId, @Param("stockCode") String stockCode);
    
    // 가격 정보 포함 조회
    List<WatchlistWithPriceVO> findWithPriceByMemberId(String memberId);
    
    // 중복 체크
    int countByMemberAndStock(@Param("memberId") String memberId, @Param("stockCode") String stockCode);
    
    // 전체 개수
    int countByMemberId(String memberId);
}
