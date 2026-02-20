package com.portwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portwatch.domain.StockPriceVO;
import com.portwatch.domain.StockVO;
import com.portwatch.mapper.StockMapper;
import com.portwatch.mapper.StockPriceMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * StockPriceUpdateServiceImpl
 * ══════════════════════════════════════════════════════════════
 * ✅ STOCK 테이블에서 현재가 조회
 * ✅ 크롤링된 한국/미국 주식 100개 데이터
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@Service
public class StockPriceUpdateServiceImpl implements StockPriceUpdateService {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockPriceMapper stockPriceMapper;

    /**
     * ✅ 최신 주가 조회
     * PortfolioApiController 라인 252에서 호출
     * 
     * 동작:
     * 1. STOCK 테이블에서 current_price 조회
     * 2. StockPriceVO로 변환하여 반환
     */
    @Override
    public StockPriceVO getLatestStockPrice(String stockCode) {
        try {
            // STOCK 테이블에서 주식 정보 조회
            StockVO stock = stockMapper.findByCode(stockCode);
            
            if (stock == null) {
                log.warn("종목 정보 없음: {}", stockCode);
                return null;
            }

            // StockVO → StockPriceVO 변환
            StockPriceVO priceVO = new StockPriceVO();
            priceVO.setStockCode(stockCode);
            priceVO.setClosePrice(stock.getCurrentPrice());  // 현재가
            priceVO.setOpenPrice(stock.getPreviousClose());  // 전일 종가를 시가로
            priceVO.setHighPrice(stock.getCurrentPrice());   // 현재가를 고가로
            priceVO.setLowPrice(stock.getPreviousClose());   // 전일 종가를 저가로
            priceVO.setVolume(stock.getVolume());            // 거래량
            
            return priceVO;

        } catch (Exception e) {
            log.error("최신 주가 조회 실패 (stockCode={}): {}", stockCode, e.getMessage());
            return null;
        }
    }

    /**
     * 주가 업데이트 (크롤링 후 저장)
     */
    @Override
    public int updateStockPrice(StockPriceVO stockPrice) {
        try {
            return stockPriceMapper.insert(stockPrice);
        } catch (Exception e) {
            log.error("주가 업데이트 실패: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 주가 이력 삭제 (오래된 데이터)
     */
    @Override
    public int deleteOldPrices(int days) {
        try {
            return stockPriceMapper.deleteOlderThan(days);
        } catch (Exception e) {
            log.error("오래된 주가 삭제 실패: {}", e.getMessage());
            return 0;
        }
    }
}
