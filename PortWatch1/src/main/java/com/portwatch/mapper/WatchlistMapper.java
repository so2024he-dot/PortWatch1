package com.portwatch.mapper;

import java.util.List;

import com.portwatch.domain.WatchlistVO;
import com.portwatch.domain.WatchlistWithPriceVO;

/**
 * WatchlistMapper Interface
 * ══════════════════════════════════════════════════════════════
 * 문제: WatchlistMapper.xml은 로드됐지만 WatchlistMapper.class 미등록
 * 원인: WatchlistMapper.java 인터페이스 파일이 없었음
 * 해결: 이 파일 생성 → 자동 Bean 등록
 *
 * 주의: WatchlistVO.class, WatchlistWithPriceVO.class 는
 *       콘솔 로그에서 domain에서 정상 로드됨 (이미 있음)
 * ══════════════════════════════════════════════════════════════
 */
public interface WatchlistMapper {

    /** 관심종목 추가 */
    int insert(WatchlistVO watchlist);

    /** 관심종목 단건 조회 */
    WatchlistVO findById(Long watchlistId);

    /** 회원별 관심종목 조회 */
    List<WatchlistVO> findByMemberId(Long memberId);

    /**
     * 회원별 관심종목 + 현재가 조회 (STOCK 테이블 JOIN)
     * WatchlistWithPriceVO.class 이미 있음 (콘솔 로그 확인)
     */
    List<WatchlistWithPriceVO> findWithPriceByMemberId(Long memberId);

    /** 중복 체크 */
    WatchlistVO findByMemberAndStock(Long memberId, String stockCode);

    /** 관심종목 삭제 */
    int delete(Long watchlistId);

    /** 회원 관심종목 전체 삭제 */
    int deleteByMemberId(Long memberId);

    /** 관심종목 수 */
    int countByMemberId(Long memberId);
}
