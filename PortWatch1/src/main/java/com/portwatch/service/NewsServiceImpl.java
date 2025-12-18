package com.portwatch.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.portwatch.domain.NewsVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.NewsDAO;
import com.portwatch.persistence.StockDAO;
import com.portwatch.util.NewsCrawler;

/**
 * 뉴스 서비스 구현
 * 
 * @author PortWatch
 * @version 1.0
 */
@Service
public class NewsServiceImpl implements NewsService {
    
    @Autowired
    private NewsDAO newsDAO;
    
    @Autowired
    private StockDAO stockDAO;
    
    @Autowired
    private NewsCrawler newsCrawler;
    
    /**
     * 최근 뉴스 조회
     */
    @Override
    public List<NewsVO> getRecentNews(int limit) {
        try {
            return newsDAO.selectRecentNews(limit);
        } catch (Exception e) {
            System.err.println("❌ 최근 뉴스 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 뉴스 ID로 조회
     */
    @Override
    public NewsVO getNewsById(Long newsId) {
        try {
            return newsDAO.selectNewsById(newsId);
        } catch (Exception e) {
            System.err.println("❌ 뉴스 조회 실패 (ID: " + newsId + "): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 종목별 뉴스 조회
     */
    @Override
    public List<NewsVO> getNewsByStockCode(String stockCode, int limit) {
        try {
            return newsDAO.selectNewsByStockCode(stockCode, limit);
        } catch (Exception e) {
            System.err.println("❌ 종목 뉴스 조회 실패 (종목: " + stockCode + "): " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 뉴스 크롤링 및 저장
     */
    @Override
    @Transactional
    public int crawlAndSaveNews() {
        int totalCount = 0;
        
        try {
            System.out.println("🔄 전체 종목 뉴스 크롤링 시작...");
            
            // 모든 종목 조회
            List<StockVO> allStocks = stockDAO.selectAllStocks();
            
            System.out.println("  📊 총 종목 수: " + allStocks.size());
            
            // 각 종목별로 뉴스 크롤링 (최대 20개 종목만)
            int maxStocks = Math.min(20, allStocks.size());
            
            for (int i = 0; i < maxStocks; i++) {
                StockVO stock = allStocks.get(i);
                
                try {
                    int count = crawlStockNews(stock.getStockCode(), stock.getStockName());
                    totalCount += count;
                    
                    // 과도한 요청 방지를 위한 딜레이
                    Thread.sleep(1000);
                    
                } catch (Exception e) {
                    System.err.println("  ⚠️ 종목 뉴스 크롤링 실패 (" + stock.getStockCode() + "): " + e.getMessage());
                }
            }
            
            System.out.println("✅ 전체 뉴스 크롤링 완료! 총 " + totalCount + "개 수집");
            
        } catch (Exception e) {
            System.err.println("❌ 뉴스 크롤링 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        return totalCount;
    }
    
    /**
     * 특정 종목의 뉴스 크롤링
     */
    @Override
    @Transactional
    public int crawlStockNews(String stockCode, String stockName) {
        int count = 0;
        
        try {
            System.out.println("  🔍 뉴스 크롤링 중: " + stockName + " (" + stockCode + ")");
            
            // 네이버 금융에서 뉴스 크롤링
            List<NewsVO> newsList = newsCrawler.crawlNaverFinanceNews(stockCode, stockName);
            
            // 중복 체크 후 저장
            for (NewsVO news : newsList) {
                if (!isDuplicateNews(news.getLink())) {
                    if (saveNews(news)) {
                        count++;
                    }
                }
            }
            
            System.out.println("    ✓ " + count + "개 뉴스 저장");
            
        } catch (Exception e) {
            System.err.println("    ✗ 크롤링 실패: " + e.getMessage());
        }
        
        return count;
    }
    
    /**
     * 뉴스 저장
     */
    @Override
    @Transactional
    public boolean saveNews(NewsVO news) {
        try {
            return newsDAO.insertNews(news) > 0;
        } catch (Exception e) {
            System.err.println("❌ 뉴스 저장 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 중복 뉴스 체크
     */
    @Override
    public boolean isDuplicateNews(String link) {
        try {
            return newsDAO.checkDuplicateNews(link) > 0;
        } catch (Exception e) {
            System.err.println("❌ 중복 체크 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 오래된 뉴스 삭제 (30일 이상)
     */
    @Override
    @Transactional
    public int deleteOldNews() {
        try {
            int deleted = newsDAO.deleteOldNews(30);
            System.out.println("🗑️ 오래된 뉴스 " + deleted + "개 삭제 완료");
            return deleted;
        } catch (Exception e) {
            System.err.println("❌ 오래된 뉴스 삭제 실패: " + e.getMessage());
            return 0;
        }
    }
}
