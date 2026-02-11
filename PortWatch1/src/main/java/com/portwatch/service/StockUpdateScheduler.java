package com.portwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 주식 데이터 자동 업데이트 스케줄러
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 매일 오전 9시, 오후 6시에 한국/미국 주식 100대 기업 자동 업데이트
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Slf4j
@Service
public class StockUpdateScheduler {

    @Autowired
    private KoreaStockCrawlerService koreaStockCrawlerService;

    @Autowired
    private USStockCrawlerService usStockCrawlerService;

    /**
     * 매일 오전 9시 - 한국/미국 주식 전체 업데이트
     * Cron: 0 0 9 * * * (초 분 시 일 월 요일)
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void updateAllStocksMorning() {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("⏰ [스케줄] 오전 9시 주식 데이터 자동 업데이트 시작");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            long startTime = System.currentTimeMillis();

            // 한국 주식 업데이트
            int koreaCount = koreaStockCrawlerService.crawlAndUpdateTop100Stocks();

            // 미국 주식 업데이트
            int usCount = usStockCrawlerService.crawlAndUpdateTop100Stocks();

            long duration = (System.currentTimeMillis() - startTime) / 1000;

            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("✅ [스케줄] 오전 9시 업데이트 완료");
            log.info("   한국: {}개 | 미국: {}개 | 소요시간: {}초", koreaCount, usCount, duration);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        } catch (Exception e) {
            log.error("❌ [스케줄] 오전 9시 업데이트 실패", e);
        }
    }

    /**
     * 매일 오후 6시 - 한국/미국 주식 전체 업데이트
     * Cron: 0 0 18 * * * (초 분 시 일 월 요일)
     */
    @Scheduled(cron = "0 0 18 * * *")
    public void updateAllStocksEvening() {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("⏰ [스케줄] 오후 6시 주식 데이터 자동 업데이트 시작");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            long startTime = System.currentTimeMillis();

            // 한국 주식 업데이트
            int koreaCount = koreaStockCrawlerService.crawlAndUpdateTop100Stocks();

            // 미국 주식 업데이트
            int usCount = usStockCrawlerService.crawlAndUpdateTop100Stocks();

            long duration = (System.currentTimeMillis() - startTime) / 1000;

            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("✅ [스케줄] 오후 6시 업데이트 완료");
            log.info("   한국: {}개 | 미국: {}개 | 소요시간: {}초", koreaCount, usCount, duration);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        } catch (Exception e) {
            log.error("❌ [스케줄] 오후 6시 업데이트 실패", e);
        }
    }

    /**
     * 매시간 정각 - 한국 주식 업데이트 (평일 9시~15시)
     * Cron: 0 0 9-15 * * MON-FRI
     */
    @Scheduled(cron = "0 0 9-15 * * MON-FRI")
    public void updateKoreaStocksHourly() {
        log.info("⏰ [스케줄] 한국 주식 시간당 업데이트 시작");

        try {
            int koreaCount = koreaStockCrawlerService.crawlAndUpdateTop100Stocks();
            log.info("✅ [스케줄] 한국 주식 업데이트 완료: {}개", koreaCount);

        } catch (Exception e) {
            log.error("❌ [스케줄] 한국 주식 시간당 업데이트 실패", e);
        }
    }

    /**
     * 30분마다 - 미국 주식 업데이트 (평일 23시~06시 - 미국 시장 시간)
     * Cron: 0 0,30 23,0-6 * * MON-FRI
     */
    @Scheduled(cron = "0 0,30 23,0-6 * * MON-FRI")
    public void updateUSStocksHalfHourly() {
        log.info("⏰ [스케줄] 미국 주식 30분당 업데이트 시작");

        try {
            int usCount = usStockCrawlerService.crawlAndUpdateTop100Stocks();
            log.info("✅ [스케줄] 미국 주식 업데이트 완료: {}개", usCount);

        } catch (Exception e) {
            log.error("❌ [스케줄] 미국 주식 30분당 업데이트 실패", e);
        }
    }
}
