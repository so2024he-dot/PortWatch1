package com.portwatch.persistence;

import com.portwatch.domain.NewsVO;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * ✅ [수정] NewsDAO 인터페이스
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 수정 내역:
 *   - existsByUrl(@Param("url") String url) : @Param 추가
 *     → NewsMapper.xml의 #{url} 파라미터와 이름 일치시킴
 *   - checkDuplicateUrl(@Param("link") String link) : @Param 추가
 *     → NewsMapper.xml의 #{link} 파라미터와 이름 일치시킴
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Mapper
public interface NewsDAO {

    // ──────────────────────────────────────────
    // INSERT / UPDATE / DELETE
    // ──────────────────────────────────────────

    void insertNews(NewsVO news) throws Exception;

    void updateNews(NewsVO news) throws Exception;

    void deleteNews(Long newsId) throws Exception;

    // ──────────────────────────────────────────
    // SELECT - 단건
    // ──────────────────────────────────────────

    NewsVO selectNewsById(Long newsId) throws Exception;

    // ──────────────────────────────────────────
    // SELECT - 목록
    // ──────────────────────────────────────────

    List<NewsVO> selectAllNews();

    List<NewsVO> selectRecentNews(int limit);

    /** 최신 뉴스 (selectLatestNews - selectRecentNews 와 동일) */
    List<NewsVO> selectLatestNews(int limit) throws Exception;

    List<NewsVO> selectNewsByStock(String stockCode, int limit);

    List<NewsVO> selectNewsByStockCode(String stockCode, int limit) throws Exception;

    List<NewsVO> selectNewsByCountry(String country, int limit) throws Exception;

    List<NewsVO> selectByCategory(String category, int limit) throws Exception;

    List<NewsVO> search(String keyword, int limit) throws Exception;

    // ──────────────────────────────────────────
    // COUNT / 중복 체크
    // ──────────────────────────────────────────

    /**
     * ✅ [수정] URL 존재 여부 확인
     *    @Param("url") 추가 → NewsMapper.xml #{url} 과 일치
     */
    boolean existsByUrl(@Param("url") String url) throws Exception;

    /**
     * ✅ [수정] URL 중복 건수 반환 (int)
     *    @Param("link") 추가 → NewsMapper.xml #{link} 과 일치
     */
    int checkDuplicateUrl(@Param("link") String link) throws Exception;

    int getTotalNewsCount() throws Exception;

    int getTodayNewsCount() throws Exception;

    int countAllNews() throws Exception;
}
