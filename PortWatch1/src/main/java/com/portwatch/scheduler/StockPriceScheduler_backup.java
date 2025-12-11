    package com.portwatch.scheduler;

import com.portwatch.service.StockPriceUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 주가 데이터 자동 업데이트 스케줄러
 * 매일 자정에 실행되어 100개 종목의 주가를 자동으로 업데이트합니다.
 */
@Component
public class StockPriceScheduler {

    private static final Logger logger = LoggerFactory.getLogger(StockPriceScheduler.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired  // @Inject 대신 @Autowired 사용 (Spring 표준)
    private StockPriceUpdateService stockPriceUpdateService;

    /**
     * 매일 자정(00:00)에 자동 실행
     * cron = "초 분 시 일 월 요일"
     * "0 0 0 * * *" = 매일 자정
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void updateAllStockPricesDaily() {
        logger.info("========================================");
        logger.info("주가 자동 업데이트 시작: {}", dateFormat.format(new Date()));
        logger.info("========================================");

        try {
            // 전체 종목 업데이트 실행
            int updatedCount = stockPriceUpdateService.updateAllStocks();
            
            logger.info("✅ 주가 업데이트 완료!");
            logger.info("   업데이트된 종목 수: {} 개", updatedCount);
            logger.info("   완료 시간: {}", dateFormat.format(new Date()));
            
        } catch (Exception e) {
            logger.error("❌ 주가 업데이트 실패!", e);
            logger.error("   실패 시간: {}", dateFormat.format(new Date()));
            logger.error("   오류 메시지: {}", e.getMessage());
        }
        
        logger.info("========================================");
    }

    /**
     * 테스트용: 매 10분마다 실행 (개발/테스트 시에만 사용)
     * 실제 운영 시에는 주석 처리하세요
     */
    // @Scheduled(cron = "0 */10 * * * *")
    public void updateAllStockPricesEvery10Minutes() {
        logger.info("🔄 [테스트] 10분마다 주가 업데이트: {}", dateFormat.format(new Date()));
        
        try {
            int updatedCount = stockPriceUpdateService.updateAllStocks();
            logger.info("✅ [테스트] 업데이트 완료: {} 개", updatedCount);
        } catch (Exception e) {
            logger.error("❌ [테스트] 업데이트 실패: {}", e.getMessage());
        }
    }

    /**
     * 수동 실행용 메서드 (Controller에서 호출 가능)
     */
    public int manualUpdate() throws Exception {
        logger.info("🔧 수동 주가 업데이트 실행: {}", dateFormat.format(new Date()));
        return stockPriceUpdateService.updateAllStocks();
    }
}

    
