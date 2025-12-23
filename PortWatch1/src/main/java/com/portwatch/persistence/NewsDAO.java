package com.portwatch.persistence;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import com.portwatch.domain.NewsVO;

/**
 * 뉴스 DAO 인터페이스 (persistence 패키지)
 * 
 * @author PortWatch
 * @version 1.0
 */
@Mapper
public interface NewsDAO {
    
    /**
     * 최근 뉴스 조회
     * @param limit 조회할 뉴스 개수
     * @return 뉴스 리스트
     */
    List<NewsVO> selectRecentNews(@Param("limit") int limit);
    
    /**
     * 뉴스 ID로 조회
     * @param newsId 뉴스 ID
     * @return 뉴스 객체
     */
    NewsVO selectNewsById(@Param("newsId") Long newsId);
    
    /**
     * 종목별 뉴스 조회
     * @param stockCode 종목 코드
     * @param limit 조회할 뉴스 개수
     * @return 뉴스 리스트
     */
    List<NewsVO> selectNewsByStockCode(@Param("stockCode") String stockCode, @Param("limit") int limit);
    
    /**
     * 뉴스 등록
     * @param news 뉴스 객체
     * @return 등록된 행 수
     */
    int insertNews(NewsVO news);
    
    /**
     * 중복 뉴스 체크
     * @param link 뉴스 링크
     * @return 중복 개수
     */
    int checkDuplicateNews(@Param("link") String link);
    
    /**
     * 오래된 뉴스 삭제
     * @param days 일 수
     * @return 삭제된 행 수
     */
    int deleteOldNews(@Param("days") int days);
    
    /**
     * 뉴스 수정
     * @param news 뉴스 객체
     * @return 수정된 행 수
     */
    int updateNews(NewsVO news);
    
    /**
     * 뉴스 삭제
     * @param newsId 뉴스 ID
     * @return 삭제된 행 수
     */
    int deleteNews(@Param("newsId") Long newsId);
}
