package com.portwatch.scheduler;

import com.portwatch.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * NewsScheduler - 뉴스 크롤링 자동 스케줄러 (신규 생성!)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * ✅ 핵심 기능:
 * - 매 30분마다 네이버 금융 뉴스 자동 크롤링
 * - 크롤링된 뉴스 자동으로 DB 저장
 * - 서버 시작 시 1분 후 첫 실행
 * 
 * 실행 시간:
 * - 서버 시작 후 1분: initialDelay = 60000 (1분)
 * - 이후 30분마다: fixedDelay = 1800000 (30분)
 * 
 * @author PortWatch
 * @version 1.0 - 2026.01.16
 */
@Component
public class NewsScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NewsScheduler.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private NewsService newsService;

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 뉴스 자동 크롤링 및 저장 (30분마다)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * @Scheduled(initialDelay = 60000, fixedDelay = 1800000)
     * - initialDelay: 서버 시작 후 60초(1분) 후 첫 실행
     * - fixedDelay: 이전 작업 완료 후 1800초(30분) 후 재실행
     */
    @Scheduled(initialDelay = 60000, fixedDelay = 1800000)
    public void crawlNewsAutomatically() {
        logger.info("========================================");
        logger.info("📰 뉴스 자동 크롤링 시작: {}", dateFormat.format(new Date()));
        logger.info("========================================");

        try {
            // 뉴스 크롤링 및 저장
            int savedCount = newsService.crawlAndSaveNews();
            
            logger.info("========================================");
            logger.info("✅ 뉴스 크롤링 완료!");
            logger.info("   저장된 뉴스 수: {} 개", savedCount);
            logger.info("   완료 시간: {}", dateFormat.format(new Date()));
            logger.info("========================================");
            
        } catch (Exception e) {
            logger.error("========================================");
            logger.error("❌ 뉴스 크롤링 실패!", e);
            logger.error("   실패 시간: {}", dateFormat.format(new Date()));
            logger.error("   오류 메시지: {}", e.getMessage());
            logger.error("========================================");
        }
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * ✅ 즉시 뉴스 크롤링 (수동 호출용)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * 관리자 API나 테스트 용도로 사용
     */
    public int crawlNewsNow() throws Exception {
        logger.info("========================================");
        logger.info("📰 수동 뉴스 크롤링 시작: {}", dateFormat.format(new Date()));
        logger.info("========================================");
        
        int savedCount = newsService.crawlAndSaveNews();
        
        logger.info("========================================");
        logger.info("✅ 수동 뉴스 크롤링 완료: {} 개", savedCount);
        logger.info("========================================");
        
        return savedCount;
    }
}
