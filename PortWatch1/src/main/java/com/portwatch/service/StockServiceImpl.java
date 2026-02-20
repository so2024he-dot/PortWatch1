package com.portwatch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portwatch.domain.StockVO;
import com.portwatch.mapper.StockMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * StockServiceImpl - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ 20개 메서드 완전 구현
 * ✅ 크롤링 데이터 기반 (한국 100 + 미국 100)
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockMapper stockMapper;

    // ─────────────────────────────────────────
    // 기본 조회 (8개)
    // ─────────────────────────────────────────

    @Override
    public StockVO getStockByCode(String stockCode) {
        try {
            return stockMapper.findByCode(stockCode);
        } catch (Exception e) {
            log.error("종목 조회 실패 (stockCode={}): {}", stockCode, e.getMessage());
            return null;
        }
    }

    @Override
    public StockVO getStockById(Integer stockId) {
        try {
            return stockMapper.findById(stockId);
        } catch (Exception e) {
            log.error("종목 조회 실패 (stockId={}): {}", stockId, e.getMessage());
            return null;
        }
    }

    @Override
    public List<StockVO> getAllStocks() {
        try {
            return stockMapper.findAll();
        } catch (Exception e) {
            log.error("전체 주식 조회 실패: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<StockVO> getStocksByCountry(String country) {
        try {
            return stockMapper.findByCountry(country);
        } catch (Exception e) {
            log.error("국가별 주식 조회 실패 (country={}): {}", country, e.getMessage());
            return null;
        }
    }

    @Override
    public List<StockVO> getStocksByMarket(String market) {
        try {
            return stockMapper.findByMarket(market);
        } catch (Exception e) {
            log.error("시장별 주식 조회 실패 (market={}): {}", market, e.getMessage());
            return null;
        }
    }

    @Override
    public List<StockVO> getStocksByMarketType(String marketType) {
        try {
            return stockMapper.findByMarketType(marketType);
        } catch (Exception e) {
            log.error("시장 타입별 주식 조회 실패 (marketType={}): {}", marketType, e.getMessage());
            return null;
        }
    }

    @Override
    public List<StockVO> getStocksByCountryAndMarket(String country, String market) {
        try {
            return stockMapper.findByCountryAndMarket(country, market);
        } catch (Exception e) {
            log.error("국가+시장 주식 조회 실패 (country={}, market={}): {}", country, market, e.getMessage());
            return null;
        }
    }

    @Override
    public List<StockVO> searchStocks(String keyword) {
        try {
            return stockMapper.searchStocks(keyword);
        } catch (Exception e) {
            log.error("주식 검색 실패 (keyword={}): {}", keyword, e.getMessage());
            return null;
        }
    }

    // ─────────────────────────────────────────
    // 업종 관련 (2개)
    // ─────────────────────────────────────────

    @Override
    public List<StockVO> getStocksByIndustry(String industry) {
        try {
            return stockMapper.findByIndustry(industry);
        } catch (Exception e) {
            log.error("업종별 주식 조회 실패 (industry={}): {}", industry, e.getMessage());
            return null;
        }
    }

    @Override
    public List<String> getAllIndustries() {
        try {
            return stockMapper.findAllIndustries();
        } catch (Exception e) {
            log.error("전체 업종 조회 실패: {}", e.getMessage());
            return null;
        }
    }

    // ─────────────────────────────────────────
    // 정렬 조회 (4개)
    // ─────────────────────────────────────────

    @Override
    public List<StockVO> getStocksOrderByVolume(int limit) {
        try {
            return stockMapper.findTopByVolume(limit);
        } catch (Exception e) {
            log.error("거래량 상위 조회 실패 (limit={}): {}", limit, e.getMessage());
            return null;
        }
    }

    @Override
    public List<StockVO> getStocksOrderByChangeRate(int limit) {
        try {
            return stockMapper.findTopByChangeRate(limit);
        } catch (Exception e) {
            log.error("상승률 상위 조회 실패 (limit={}): {}", limit, e.getMessage());
            return null;
        }
    }

    @Override
    public List<StockVO> getStocksOrderByChangeRateDesc(int limit) {
        try {
            return stockMapper.findTopByChangeRateDesc(limit);
        } catch (Exception e) {
            log.error("하락률 상위 조회 실패 (limit={}): {}", limit, e.getMessage());
            return null;
        }
    }

    @Override
    public List<StockVO> findRecentlyUpdated(int limit) {
        try {
            return stockMapper.findRecentlyUpdated(limit);
        } catch (Exception e) {
            log.error("최근 업데이트 조회 실패 (limit={}): {}", limit, e.getMessage());
            return null;
        }
    }

    // ─────────────────────────────────────────
    // 통계 (2개)
    // ─────────────────────────────────────────

    @Override
    public int countByCountry(String country) {
        try {
            return stockMapper.countByCountry(country);
        } catch (Exception e) {
            log.error("국가별 개수 조회 실패 (country={}): {}", country, e.getMessage());
            return 0;
        }
    }

    @Override
    public int countByMarket(String market) {
        try {
            return stockMapper.countByMarket(market);
        } catch (Exception e) {
            log.error("시장별 개수 조회 실패 (market={}): {}", market, e.getMessage());
            return 0;
        }
    }

    // ─────────────────────────────────────────
    // CRUD (4개)
    // ─────────────────────────────────────────

    @Override
    public int insertStock(StockVO stock) {
        try {
            return stockMapper.insert(stock);
        } catch (Exception e) {
            log.error("주식 등록 실패: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public int updateStock(StockVO stock) {
        try {
            return stockMapper.update(stock);
        } catch (Exception e) {
            log.error("주식 수정 실패: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public int deleteStock(String stockId) {
        try {
            return stockMapper.delete(stockId);
        } catch (Exception e) {
            log.error("주식 삭제 실패: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public List<StockVO> findByMarketAndCountry(String market, String country) {
        try {
            return stockMapper.findByMarketAndCountry(market, country);
        } catch (Exception e) {
            log.error("시장+국가 조회 실패 (market={}, country={}): {}", market, country, e.getMessage());
            return null;
        }
    }
}
