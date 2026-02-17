package com.portwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portwatch.mapper.StockMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 미국 주가 업데이트 Service
 * ══════════════════════════════════════════════════════════════
 * [문제 3 수정] Bean 네이밍 오류
 *
 * 콘솔 로그:
 *   USStockPriceUpdateServiceImpl  ← 대문자 U로 등록 (비정상)
 *
 * 해결:
 *   @Service("usStockPriceUpdateServiceImpl") 명시적 이름 지정
 *   → 소문자 'u'로 시작하는 빈 이름으로 정상 등록
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@Service("usStockPriceUpdateServiceImpl")   // ★ 수정: 명시적 소문자 이름
public class USStockPriceUpdateServiceImpl {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private USStockCrawlerService usStockCrawlerService;

    /**
     * 미국 주식 가격 업데이트
     * 크롤러 실행 후 DB 저장
     */
    public int updateAllUSStockPrices() {
        log.info("미국 주가 업데이트 시작");
        try {
            int count = usStockCrawlerService.crawlAndUpdateTop100Stocks();
            log.info("미국 주가 업데이트 완료: {}개", count);
            return count;
        } catch (Exception e) {
            log.error("미국 주가 업데이트 실패: {}", e.getMessage(), e);
            return 0;
        }
    }
}
