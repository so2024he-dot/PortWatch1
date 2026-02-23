package com.portwatch.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.portwatch.service.StockPriceUpdateService;
import com.portwatch.service.USStockPriceUpdateService;

import lombok.extern.slf4j.Slf4j;

/**
 * StockPriceScheduler - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ 오류 해결: updateAllUSStocks() 메서드 호출
 * ✅ 한국 100대 기업 자동 크롤링 (평일 09:00, 15:30)
 * ✅ 미국 100대 기업 자동 크롤링 (평일 23:30, 06:00)
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@Component
public class StockPriceScheduler {
    
    @Autowired
    private StockPriceUpdateService stockPriceUpdateService;
    
    @Autowired
    private USStockPriceUpdateService usStockPriceUpdateService;
    
    /**
     * ✅ 한국 주식 장 시작 전 업데이트
     * 매일 평일 09:00 (한국 증시 개장 전)
     */
    @Scheduled(cron = "0 0 9 * * MON-FRI")
    public void updateKoreanStocksBeforeOpen() {
        log.info("========================================");
        log.info("한국 주식 장 시작 전 업데이트 시작 (09:00)");
        log.info("========================================");
        
        try {
            stockPriceUpdateService.updateAllStockPrices();
            log.info("✅ 한국 100대 기업 크롤링 완료 (09:00)");
        } catch (Exception e) {
            log.error("❌ 한국 주식 업데이트 실패 (09:00)", e);
        }
    }
    
    /**
     * ✅ 한국 주식 장 마감 후 업데이트
     * 매일 평일 15:30 (한국 증시 마감 후)
     */
    @Scheduled(cron = "0 30 15 * * MON-FRI")
    public void updateKoreanStocksAfterClose() {
        log.info("========================================");
        log.info("한국 주식 장 마감 후 업데이트 시작 (15:30)");
        log.info("========================================");
        
        try {
            stockPriceUpdateService.updateAllStockPrices();
            log.info("✅ 한국 100대 기업 크롤링 완료 (15:30)");
        } catch (Exception e) {
            log.error("❌ 한국 주식 업데이트 실패 (15:30)", e);
        }
    }
    
    /**
     * ✅ 미국 주식 장 마감 후 업데이트
     * 매일 평일 06:00 (미국 증시 마감 후, 한국 시간 기준)
     */
    @Scheduled(cron = "0 0 6 * * TUE-SAT")
    public void updateUSStocksAfterClose() {
        log.info("========================================");
        log.info("미국 주식 장 마감 후 업데이트 시작 (06:00)");
        log.info("========================================");
        
        try {
            // ✅ 오류 해결: updateAllUSStockPrices() 호출
            usStockPriceUpdateService.updateAllUSStockPrices();
            log.info("✅ 미국 100대 기업 크롤링 완료 (06:00)");
        } catch (Exception e) {
            log.error("❌ 미국 주식 업데이트 실패 (06:00)", e);
        }
    }
    
    /**
     * ✅ 미국 주식 장 시작 전 업데이트
     * 매일 평일 23:30 (미국 증시 개장 전, 한국 시간 기준)
     */
    @Scheduled(cron = "0 30 23 * * MON-FRI")
    public void updateUSStocksBeforeOpen() {
        log.info("========================================");
        log.info("미국 주식 장 시작 전 업데이트 시작 (23:30)");
        log.info("========================================");
        
        try {
            // ✅ 오류 해결: updateAllUSStockPrices() 호출
            usStockPriceUpdateService.updateAllUSStockPrices();
            log.info("✅ 미국 100대 기업 크롤링 완료 (23:30)");
        } catch (Exception e) {
            log.error("❌ 미국 주식 업데이트 실패 (23:30)", e);
        }
    }
    
    /**
     * 한국 주식 실시간 업데이트 (5분마다)
     * 평일 09:00 ~ 15:30
     */
    @Scheduled(cron = "0 */5 9-15 * * MON-FRI")
    public void updateKoreanStocksRealtime() {
        log.info("한국 주식 실시간 업데이트 (5분마다)");
        
        try {
            stockPriceUpdateService.updateAllStockPrices();
            log.info("✅ 한국 주식 실시간 업데이트 완료");
        } catch (Exception e) {
            log.error("❌ 한국 주식 실시간 업데이트 실패", e);
        }
    }
    
    /**
     * 미국 주식 실시간 업데이트 (10분마다)
     * 평일 23:30 ~ 06:00 (다음날)
     */
    @Scheduled(cron = "0 */10 23-23,0-6 * * MON-SAT")
    public void updateUSStocksRealtime() {
        log.info("미국 주식 실시간 업데이트 (10분마다)");
        
        try {
            // ✅ 오류 해결: updateAllUSStockPrices() 호출
            usStockPriceUpdateService.updateAllUSStockPrices();
            log.info("✅ 미국 주식 실시간 업데이트 완료");
        } catch (Exception e) {
            log.error("❌ 미국 주식 실시간 업데이트 실패", e);
        }
    }
    
    /**
     * 매일 자정 전체 업데이트 (백업용)
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void updateAllStocksDaily() {
        log.info("========================================");
        log.info("매일 자정 전체 업데이트 시작 (00:00)");
        log.info("========================================");
        
        try {
            // 한국 주식
            stockPriceUpdateService.updateAllStockPrices();
            log.info("✅ 한국 100대 기업 백업 크롤링 완료");
            
            // 잠시 대기 (서버 부하 분산)
            Thread.sleep(5000);
            
            // 미국 주식
            // ✅ 오류 해결: updateAllUSStockPrices() 호출
            usStockPriceUpdateService.updateAllUSStockPrices();
            log.info("✅ 미국 100대 기업 백업 크롤링 완료");
            
            log.info("========================================");
            log.info("전체 업데이트 완료!");
            log.info("========================================");
        } catch (Exception e) {
            log.error("❌ 전체 업데이트 실패", e);
        }
    }
    
    /**
     * 수동 테스트용 메서드
     */
    public void manualUpdateAll() {
        log.info("수동 전체 업데이트 시작");
        
        try {
            stockPriceUpdateService.updateAllStockPrices();
            log.info("✅ 한국 주식 수동 업데이트 완료");
            
            usStockPriceUpdateService.updateAllUSStockPrices();
            log.info("✅ 미국 주식 수동 업데이트 완료");
        } catch (Exception e) {
            log.error("❌ 수동 업데이트 실패", e);
        }
    }
}
