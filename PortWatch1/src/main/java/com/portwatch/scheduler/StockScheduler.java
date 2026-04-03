package com.portwatch.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.portwatch.service.KoreaStockCrawlerService;
import com.portwatch.service.USStockPriceUpdateService;

/**
 * StockScheduler - 주식 데이터 자동 크롤링 스케줄러
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 * 스케줄 설계:
 *   ┌────────────────────┬──────────────────────┬──────────────┐
 *   │  대상               │ 실행 주기             │ 첫 실행      │
 *   ├────────────────────┼──────────────────────┼──────────────┤
 *   │ 한국 주식 (KRX)     │ 30분마다              │ 서버시작 2분  │
 *   │ 미국 주식 (NYSE)    │ 60분마다              │ 서버시작 3분  │
 *   └────────────────────┴──────────────────────┴──────────────┘
 *
 * ✅ @Autowired(required = false): DB 연결 실패해도 Tomcat 기동 가능
 * ✅ null 체크: Service Bean이 없어도 안전하게 건너뜀
 *
 * ⚠️ 이 클래스가 동작하려면 root-context.xml에 반드시 필요:
 *    <task:annotation-driven scheduler="taskScheduler"/>
 *    <task:scheduler id="taskScheduler" pool-size="5"/>
 */
@Component
public class StockScheduler {

    private static final Logger logger = LoggerFactory.getLogger(StockScheduler.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired(required = false)
    private KoreaStockCrawlerService koreaStockCrawlerService;

    @Autowired(required = false)
    private USStockPriceUpdateService usStockPriceUpdateService;

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 한국 주식 자동 크롤링
    // initialDelay = 120000 (2분): 서버 시작 후 DB 연결 안정화 대기
    // fixedDelay   = 1800000 (30분): 이전 크롤링 완료 후 30분 대기
    //
    // 크롤링 대상: 네이버 금융 시가총액 TOP 100
    // 저장 위치: AWS RDS MySQL STOCK 테이블
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ✅ [2026-04-02 수정] initialDelay: 120000(2분) → 600000(10분)
    //    이유: Tomcat 시작 직후 RDS 연결이 불안정할 수 있음
    //         (RDS 자동 마이너 버전 업그레이드 시 재시작 발생 → 09:03 오류 원인)
    //         10분 대기 시 HikariPool이 충분히 연결을 확보한 후 스케줄러 실행
    @Scheduled(initialDelay = 600000, fixedDelay = 1800000)
    public void crawlKoreanStocksScheduled() {
        logger.info("════════════════════════════════════════");
        logger.info("[KR-주식] 한국 주식 자동 크롤링 시작: {}", dateFormat.format(new Date()));
        logger.info("════════════════════════════════════════");

        if (koreaStockCrawlerService == null) {
            logger.warn("[KR-주식] KoreaStockCrawlerService를 사용할 수 없습니다. 건너뜀.");
            return;
        }

        try {
            long startTime = System.currentTimeMillis();
            int savedCount = koreaStockCrawlerService.crawlAndUpdateTop100Stocks();
            long elapsed = System.currentTimeMillis() - startTime;

            logger.info("════════════════════════════════════════");
            logger.info("[KR-주식] 크롤링 완료!");
            logger.info("  저장/갱신: {}개 종목", savedCount);
            logger.info("  소요시간: {}ms", elapsed);
            logger.info("  완료시각: {}", dateFormat.format(new Date()));
            logger.info("════════════════════════════════════════");

        } catch (Exception e) {
            logger.error("[KR-주식] 크롤링 실패: {}", e.getMessage());
            logger.error("[KR-주식] 원인:", e);
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 미국 주식 자동 크롤링
    // initialDelay = 180000 (3분): 한국 크롤링 후 추가 대기
    // fixedDelay   = 3600000 (1시간): 이전 크롤링 완료 후 1시간 대기
    //
    // 크롤링 대상: NYSE/NASDAQ 주요 100개 종목
    // 저장 위치: AWS RDS MySQL STOCK 테이블
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ✅ [2026-04-02 수정] initialDelay: 180000(3분) → 720000(12분)
    //    이유: 한국 주식 크롤러(10분) + 2분 여유 후 실행
    @Scheduled(initialDelay = 720000, fixedDelay = 3600000)
    public void crawlUSStocksScheduled() {
        logger.info("════════════════════════════════════════");
        logger.info("[US-주식] 미국 주식 자동 크롤링 시작: {}", dateFormat.format(new Date()));
        logger.info("════════════════════════════════════════");

        if (usStockPriceUpdateService == null) {
            logger.warn("[US-주식] USStockPriceUpdateService를 사용할 수 없습니다. 건너뜀.");
            return;
        }

        try {
            long startTime = System.currentTimeMillis();
            usStockPriceUpdateService.updateAllUSStockPrices();
            long elapsed = System.currentTimeMillis() - startTime;

            logger.info("════════════════════════════════════════");
            logger.info("[US-주식] 크롤링 완료!");
            logger.info("  소요시간: {}ms", elapsed);
            logger.info("  완료시각: {}", dateFormat.format(new Date()));
            logger.info("════════════════════════════════════════");

        } catch (Exception e) {
            logger.error("[US-주식] 크롤링 실패: {}", e.getMessage());
            logger.error("[US-주식] 원인:", e);
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 수동 즉시 크롤링 (API 호출용)
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public int crawlKoreanStocksNow() {
        logger.info("[KR-주식] 수동 크롤링 시작: {}", dateFormat.format(new Date()));

        if (koreaStockCrawlerService == null) {
            logger.warn("[KR-주식] Service 없음");
            return 0;
        }

        try {
            int count = koreaStockCrawlerService.crawlAndUpdateTop100Stocks();
            logger.info("[KR-주식] 수동 크롤링 완료: {}개", count);
            return count;
        } catch (Exception e) {
            logger.error("[KR-주식] 수동 크롤링 실패: {}", e.getMessage());
            return 0;
        }
    }

    public void crawlUSStocksNow() {
        logger.info("[US-주식] 수동 크롤링 시작: {}", dateFormat.format(new Date()));

        if (usStockPriceUpdateService == null) {
            logger.warn("[US-주식] Service 없음");
            return;
        }

        try {
            usStockPriceUpdateService.updateAllUSStockPrices();
            logger.info("[US-주식] 수동 크롤링 완료: {}", dateFormat.format(new Date()));
        } catch (Exception e) {
            logger.error("[US-주식] 수동 크롤링 실패: {}", e.getMessage());
        }
    }

    public String getStatus() {
        return String.format(
            "StockScheduler | KR: %s | US: %s",
            koreaStockCrawlerService != null ? "연결됨" : "미연결",
            usStockPriceUpdateService != null ? "연결됨" : "미연결"
        );
    }
}
