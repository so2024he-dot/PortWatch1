package com.portwatch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portwatch.domain.StockVO;
import com.portwatch.mapper.StockMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * StockServiceImpl
 * ══════════════════════════════════════════════════════════════
 * ✅ StockMapper 기반 주식 서비스 구현
 * ✅ 크롤링된 한국/미국 주식 100개 데이터 조회
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockMapper stockMapper;

    /**
     * ✅ 종목 코드로 주식 조회
     * StockPurchaseApiController 호출
     * 
     * MySQL STOCK 테이블에서 조회
     * stock_id: "KR_005930", "US_AAPL" 형식
     */
    @Override
    public StockVO getStockByCode(String stockCode) {
        try {
            return stockMapper.findByCode(stockCode);
        } catch (Exception e) {
            log.error("종목 조회 실패 (stockCode={}): {}", stockCode, e.getMessage());
            return null;
        }
    }

    /**
     * ✅ 전체 주식 목록 조회
     * PortfolioController 호출 (생성/수정 폼용)
     * 
     * 크롤링된 한국 100개 + 미국 100개 = 총 200개 반환
     */
    @Override
    public List<StockVO> getAllStocks() {
        try {
            return stockMapper.findAll();
        } catch (Exception e) {
            log.error("전체 주식 조회 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * ✅ 국가별 주식 목록
     * country: "KR" 또는 "US"
     */
    @Override
    public List<StockVO> getStocksByCountry(String country) {
        try {
            return stockMapper.findByCountry(country);
        } catch (Exception e) {
            log.error("국가별 주식 조회 실패 (country={}): {}", country, e.getMessage());
            return null;
        }
    }

    /**
     * ✅ 시장별 주식 목록
     * market: "KOSPI", "KOSDAQ", "NYSE", "NASDAQ"
     */
    @Override
    public List<StockVO> getStocksByMarket(String market) {
        try {
            return stockMapper.findByMarket(market);
        } catch (Exception e) {
            log.error("시장별 주식 조회 실패 (market={}): {}", market, e.getMessage());
            return null;
        }
    }

    /**
     * ✅ 주식 검색
     * keyword: 종목 코드 또는 종목명
     */
    @Override
    public List<StockVO> searchStocks(String keyword) {
        try {
            return stockMapper.searchStocks(keyword);
        } catch (Exception e) {
            log.error("주식 검색 실패 (keyword={}): {}", keyword, e.getMessage());
            return null;
        }
    }

    /**
     * 주식 등록 (관리자 기능)
     */
    @Override
    public int insertStock(StockVO stock) {
        try {
            return stockMapper.insert(stock);
        } catch (Exception e) {
            log.error("주식 등록 실패: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 주식 수정 (관리자 기능)
     */
    @Override
    public int updateStock(StockVO stock) {
        try {
            return stockMapper.update(stock);
        } catch (Exception e) {
            log.error("주식 수정 실패: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 주식 삭제 (관리자 기능)
     */
    @Override
    public int deleteStock(String stockId) {
        try {
            return stockMapper.delete(stockId);
        } catch (Exception e) {
            log.error("주식 삭제 실패: {}", e.getMessage());
            return 0;
        }
    }
}
