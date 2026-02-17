package com.portwatch.mapper;

import java.util.List;

import com.portwatch.domain.NewsVO;

/**
 * NewsMapper Interface
 * ══════════════════════════════════════════════════════════════
 * 문제: 콘솔 로그에서 NewsMapper.xml은 로드됐지만
 *       NewsMapper.class가 Bean으로 등록되지 않음
 *
 * 원인: NewsMapper.java 인터페이스 파일이 없었음
 *
 * 해결: 이 파일 생성 → MapperScannerConfigurer가 자동 감지
 *       → 콘솔에 "Identified candidate component class: NewsMapper.class" 추가됨
 *
 * NewsMapper.xml namespace: com.portwatch.mapper.NewsMapper
 * ══════════════════════════════════════════════════════════════
 */
public interface NewsMapper {

    /** 뉴스 등록 */
    int insert(NewsVO news);

    /** 전체 뉴스 조회 (최신순) */
    List<NewsVO> findAll();

    /** 종목별 뉴스 조회 */
    List<NewsVO> findByStockCode(String stockCode);

    /** 국가별 뉴스 조회 */
    List<NewsVO> findByCountry(String country);

    /** 최근 N개 뉴스 조회 */
    List<NewsVO> findRecent(int limit);

    /** 키워드 검색 */
    List<NewsVO> searchByKeyword(String keyword);

    /** 뉴스 삭제 */
    int delete(Long newsId);

    /** 오래된 뉴스 삭제 (N일 이전) */
    int deleteOlderThan(int days);

    /** 전체 뉴스 수 */
    int count();
}
