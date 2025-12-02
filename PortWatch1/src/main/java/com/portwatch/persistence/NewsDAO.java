    package com.portwatch.persistence;

import com.portwatch.domain.NewsVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 뉴스 DAO 인터페이스
 */
public interface NewsDAO {
    
    /**
     * 최신 뉴스 조회
     */
    List<NewsVO> selectLatestNews(int limit) throws Exception;
    
    /**
     * 특정 종목 관련 뉴스 조회
     */
    List<NewsVO> selectNewsByStock(@Param("stockCode") String stockCode, 
                                    @Param("limit") int limit) throws Exception;
    
    /**
     * 뉴스 저장
     */
    void insertNews(NewsVO news) throws Exception;
    
    /**
     * 뉴스 ID로 조회
     */
    NewsVO selectNewsById(int newsId) throws Exception;
    
    /**
     * 중복 뉴스 확인 (URL 기준)
     */
    int checkDuplicateNews(String newsUrl) throws Exception;
}

    
