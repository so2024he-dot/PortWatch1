package com.portwatch.scheduler;

import com.portwatch.service.StockPriceUpdateService;
import com.portwatch.service.USStockPriceUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 주가 데이터 자동 업데이트 스케줄러
 * 
 * 실행 시간:
 * - 한국 주식(KOSPI/KOSDAQ): 매일 00:00 (자정) - 한국 장 마감 후
 * - 미국 주식(NASDAQ/NYSE): 매일 06:00 (오전 6시) - 미국 장 마감 후 (EST 16:00 = KST 다음날 06:00)
 */
@Component
public class StockPriceScheduler {

    private static final Logger logger = LoggerFactory.getLogger(StockPriceScheduler.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private StockPriceUpdateService koreanStockService; // 한국 주식

    @Autowired
    private USStockPriceUpdateService usStockService; // 미국 주식

    /**
     * 한국 주식: 매일 자정(00:00)에 자동 실행
     * cron = "초 분 시 일 월 요일"
     * "0 0 0 * * *" = 매일 자정
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void updateKoreanStocksDaily() {
        logger.info("========================================");
        logger.info("🇰🇷 한국 주식 자동 업데이트 시작: {}", dateFormat.format(new Date()));
        logger.info("========================================");

        try {
            int updatedCount = koreanStockService.updateAllStocks();
            
            logger.info("========================================");
            logger.info("✅ 한국 주식 업데이트 완료!");
            logger.info("   업데이트된 종목 수: {} 개", updatedCount);
            logger.info("   완료 시간: {}", dateFormat.format(new Date()));
            logger.info("========================================");
            
        } catch (Exception e) {
            logger.error("========================================");
            logger.error("❌ 한국 주식 업데이트 실패!", e);
            logger.error("   실패 시간: {}", dateFormat.format(new Date()));
            logger.error("   오류 메시지: {}", e.getMessage());
            logger.error("========================================");
        }
    }

    /**
     * 미국 주식: 매일 오전 6시(한국시간)에 자동 실행
     * 미국 EST 16:00 (장 마감) = KST 다음날 06:00
     * "0 0 6 * * *" = 매일 오전 6시
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void updateUSStocksDaily() {
        logger.info("========================================");
        logger.info("🇺🇸 미국 주식 자동 업데이트 시작: {}", dateFormat.format(new Date()));
        logger.info("========================================");

        try {
            int updatedCount = usStockService.updateAllUSStocks();
            
            logger.info("========================================");
            logger.info("✅ 미국 주식 업데이트 완료!");
            logger.info("   업데이트된 종목 수: {} 개", updatedCount);
            logger.info("   완료 시간: {}", dateFormat.format(new Date()));
            logger.info("========================================");
            
        } catch (Exception e) {
            logger.error("========================================");
            logger.error("❌ 미국 주식 업데이트 실패!", e);
            logger.error("   실패 시간: {}", dateFormat.format(new Date()));
            logger.error("   오류 메시지: {}", e.getMessage());
            logger.error("========================================");
        }
    }

    /**
     * 테스트용: 매 10분마다 전체 업데이트 (개발/테스트 시에만 사용)
     * 실제 운영 시에는 주석 처리하세요!
     * "0 *\/10 * * * *" = 매 10분마다
     */
    // @Scheduled(cron = "0 */10 * * * *")
    public void updateAllStocksEvery10Minutes() {
        logger.info("========================================");
        logger.info("🔄 [테스트] 10분마다 전체 주가 업데이트: {}", dateFormat.format(new Date()));
        logger.info("========================================");
        
        try {
            // 한국 주식 업데이트
            logger.info("🇰🇷 [테스트] 한국 주식 업데이트 중...");
            int koreanCount = koreanStockService.updateAllStocks();
            logger.info("✅ [테스트] 한국 주식 완료: {} 개", koreanCount);
            
            // 미국 주식 업데이트
            logger.info("🇺🇸 [테스트] 미국 주식 업데이트 중...");
            int usCount = usStockService.updateAllUSStocks();
            logger.info("✅ [테스트] 미국 주식 완료: {} 개", usCount);
            
            logger.info("========================================");
            logger.info("✅ [테스트] 전체 업데이트 완료 - 한국: {}, 미국: {}", koreanCount, usCount);
            logger.info("========================================");
            
        } catch (Exception e) {
            logger.error("========================================");
            logger.error("❌ [테스트] 업데이트 실패: {}", e.getMessage());
            logger.error("========================================");
        }
    }

    /**
     * 수동 실행용 메서드 - 한국 주식
     * Controller에서 호출 가능
     * 
     * 사용 예시:
     * @GetMapping("/api/admin/update-korean")
     * public ResponseEntity<?> updateKorean() {
     *     int count = scheduler.manualUpdateKorean();
     *     return ResponseEntity.ok("업데이트 완료: " + count);
     * }
     */
    public int manualUpdateKorean() throws Exception {
        logger.info("========================================");
        logger.info("🔧 수동 한국 주식 업데이트 실행: {}", dateFormat.format(new Date()));
        logger.info("========================================");
        
        int count = koreanStockService.updateAllStocks();
        
        logger.info("========================================");
        logger.info("✅ 수동 한국 주식 업데이트 완료: {} 개", count);
        logger.info("========================================");
        
        return count;
    }

    /**
     * 수동 실행용 메서드 - 미국 주식
     * Controller에서 호출 가능
     * 
     * 사용 예시:
     * @GetMapping("/api/admin/update-us")
     * public ResponseEntity<?> updateUS() {
     *     int count = scheduler.manualUpdateUS();
     *     return ResponseEntity.ok("업데이트 완료: " + count);
     * }
     */
    public int manualUpdateUS() throws Exception {
        logger.info("========================================");
        logger.info("🔧 수동 미국 주식 업데이트 실행: {}", dateFormat.format(new Date()));
        logger.info("========================================");
        
        int count = usStockService.updateAllUSStocks();
        
        logger.info("========================================");
        logger.info("✅ 수동 미국 주식 업데이트 완료: {} 개", count);
        logger.info("========================================");
        
        return count;
    }

    /**
     * 수동 실행용 메서드 - 전체 주식 (한국 + 미국)
     * Controller에서 호출 가능
     */
    public Map<String, Integer> manualUpdateAll() throws Exception {
        logger.info("========================================");
        logger.info("🔧 수동 전체 주식 업데이트 실행: {}", dateFormat.format(new Date()));
        logger.info("========================================");
        
        int koreanCount = koreanStockService.updateAllStocks();
        int usCount = usStockService.updateAllUSStocks();
        
        Map<String, Integer> result = new HashMap<>();
        result.put("korean", koreanCount);
        result.put("us", usCount);
        result.put("total", koreanCount + usCount);
        
        logger.info("========================================");
        logger.info("✅ 수동 전체 업데이트 완료 - 한국: {}, 미국: {}, 총: {}", 
                koreanCount, usCount, koreanCount + usCount);
        logger.info("========================================");
        
        return result;
    }
}
