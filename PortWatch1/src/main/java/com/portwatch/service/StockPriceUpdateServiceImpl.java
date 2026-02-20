package com.portwatch.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portwatch.domain.StockPriceVO;
import com.portwatch.domain.StockVO;
import com.portwatch.mapper.StockMapper;
import com.portwatch.mapper.StockPriceMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * StockPriceUpdateServiceImpl - 완전판
 * ══════════════════════════════════════════════════════════════
 * ✅ 15개 메서드 완전 구현
 * ✅ 네이버 금융 크롤링
 * ✅ STOCK 테이블 current_price 업데이트
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@Service
public class StockPriceUpdateServiceImpl implements StockPriceUpdateService {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockPriceMapper stockPriceMapper;

    // ─────────────────────────────────────────
    // 기본 조회 (3개)
    // ─────────────────────────────────────────

    @Override
    public StockPriceVO getLatestStockPrice(String stockCode) {
        try {
            StockVO stock = stockMapper.findByCode(stockCode);
            if (stock == null) return null;

            StockPriceVO priceVO = new StockPriceVO();
            priceVO.setStockCode(stockCode);
            priceVO.setClosePrice(stock.getCurrentPrice());
            priceVO.setOpenPrice(stock.getPreviousClose());
            priceVO.setHighPrice(stock.getCurrentPrice());
            priceVO.setLowPrice(stock.getPreviousClose());
            priceVO.setVolume(stock.getVolume());
            
            return priceVO;
        } catch (Exception e) {
            log.error("최신 주가 조회 실패 (stockCode={}): {}", stockCode, e.getMessage());
            return null;
        }
    }

    @Override
    public List<StockPriceVO> getStockPriceHistory(String stockCode, int days) {
        try {
            return stockPriceMapper.findHistoryByDays(stockCode, days);
        } catch (Exception e) {
            log.error("주가 이력 조회 실패 (stockCode={}, days={}): {}", stockCode, days, e.getMessage());
            return null;
        }
    }

    @Override
    public StockPriceVO getLatestPrice(String stockCode) {
        return getLatestStockPrice(stockCode);
    }

    // ─────────────────────────────────────────
    // 크롤링 (3개)
    // ─────────────────────────────────────────

    @Override
    public Map<String, Object> crawlStockPriceFromNaver(String stockCode) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("네이버 금융 크롤링 시작: {}", stockCode);
            
            String url = "https://finance.naver.com/item/main.nhn?code=" + stockCode;
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(5000)
                .get();
            
            // 현재가
            Element priceElement = doc.selectFirst("p.no_today .blind");
            String currentPrice = priceElement != null ? priceElement.text().replace(",", "") : "0";
            
            // 전일 대비
            Element changeElement = doc.selectFirst("p.no_exday .blind");
            String changeAmount = changeElement != null ? changeElement.text().replace(",", "") : "0";
            
            // 거래량
            Element volumeElement = doc.selectFirst("td.first span");
            String volume = volumeElement != null ? volumeElement.text().replace(",", "") : "0";
            
            result.put("stockCode", stockCode);
            result.put("currentPrice", new BigDecimal(currentPrice));
            result.put("changeAmount", new BigDecimal(changeAmount));
            result.put("volume", Long.parseLong(volume));
            result.put("crawledAt", new Timestamp(System.currentTimeMillis()));
            
            log.info("크롤링 성공: {} - 현재가 {}", stockCode, currentPrice);
            
        } catch (Exception e) {
            log.error("크롤링 실패 (stockCode={}): {}", stockCode, e.getMessage());
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @Override
    public StockPriceVO crawlKoreanStock(String stockCode) {
        try {
            Map<String, Object> crawled = crawlStockPriceFromNaver(stockCode);
            
            if (crawled.containsKey("error")) {
                return null;
            }
            
            StockPriceVO priceVO = new StockPriceVO();
            priceVO.setStockCode(stockCode);
            priceVO.setClosePrice((BigDecimal) crawled.get("currentPrice"));
            priceVO.setVolume((Long) crawled.get("volume"));
            priceVO.setCreatedAt((Timestamp) crawled.get("crawledAt"));
            
            return priceVO;
            
        } catch (Exception e) {
            log.error("한국 주식 크롤링 실패 (stockCode={}): {}", stockCode, e.getMessage());
            return null;
        }
    }

    @Override
    public List<StockPriceVO> crawlMultipleKoreanStocks(List<String> stockCodes) {
        return stockCodes.stream()
            .map(this::crawlKoreanStock)
            .filter(price -> price != null)
            .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────
    // 업데이트 (6개)
    // ─────────────────────────────────────────

    @Override
    public StockPriceVO updateSingleStock(String stockCode) {
        try {
            log.info("단일 종목 업데이트 시작: {}", stockCode);
            
            // 크롤링
            Map<String, Object> crawled = crawlStockPriceFromNaver(stockCode);
            
            if (crawled.containsKey("error")) {
                throw new RuntimeException("크롤링 실패");
            }
            
            // STOCK 테이블 업데이트
            BigDecimal currentPrice = (BigDecimal) crawled.get("currentPrice");
            Long volume = (Long) crawled.get("volume");
            
            StockVO stock = stockMapper.findByCode(stockCode);
            if (stock != null) {
                stock.setCurrentPrice(currentPrice);
                stock.setVolume(volume);
                stock.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                stockMapper.update(stock);
            }
            
            // StockPriceVO 생성
            StockPriceVO priceVO = new StockPriceVO();
            priceVO.setStockCode(stockCode);
            priceVO.setClosePrice(currentPrice);
            priceVO.setVolume(volume);
            
            // STOCK_PRICE 테이블 저장
            stockPriceMapper.insert(priceVO);
            
            log.info("단일 종목 업데이트 완료: {}", stockCode);
            return priceVO;
            
        } catch (Exception e) {
            log.error("단일 종목 업데이트 실패 (stockCode={}): {}", stockCode, e.getMessage());
            throw new RuntimeException("업데이트 실패: " + e.getMessage());
        }
    }

    @Override
    public Map<String, StockPriceVO> updateMultipleStocks(List<String> stockCodes) {
        Map<String, StockPriceVO> results = new HashMap<>();
        
        for (String stockCode : stockCodes) {
            try {
                StockPriceVO price = updateSingleStock(stockCode);
                results.put(stockCode, price);
            } catch (Exception e) {
                log.error("종목 업데이트 실패 (stockCode={}): {}", stockCode, e.getMessage());
            }
        }
        
        return results;
    }

    @Override
    public int updateAllStocks() {
        try {
            log.info("전체 종목 업데이트 시작");
            
            // 한국 주식만 조회
            List<StockVO> koreanStocks = stockMapper.findByCountry("KR");
            
            int successCount = 0;
            for (StockVO stock : koreanStocks) {
                try {
                    updateSingleStock(stock.getStockCode());
                    successCount++;
                    Thread.sleep(1000); // 1초 대기 (서버 부하 방지)
                } catch (Exception e) {
                    log.error("종목 업데이트 실패: {}", stock.getStockCode());
                }
            }
            
            log.info("전체 종목 업데이트 완료: {}/{}", successCount, koreanStocks.size());
            return successCount;
            
        } catch (Exception e) {
            log.error("전체 종목 업데이트 실패: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public void updateAllStockPrices() {
        updateAllStocks();
    }

    @Override
    public void updateStockPrice(String stockCode) {
        updateSingleStock(stockCode);
    }

    @Override
    public void updateByMarketType(String marketType) {
        try {
            log.info("시장별 업데이트 시작: {}", marketType);
            
            List<StockVO> stocks = stockMapper.findByMarketType(marketType);
            
            int successCount = 0;
            for (StockVO stock : stocks) {
                try {
                    updateSingleStock(stock.getStockCode());
                    successCount++;
                    Thread.sleep(1000);
                } catch (Exception e) {
                    log.error("종목 업데이트 실패: {}", stock.getStockCode());
                }
            }
            
            log.info("시장별 업데이트 완료: {}/{}", successCount, stocks.size());
            
        } catch (Exception e) {
            log.error("시장별 업데이트 실패 (marketType={}): {}", marketType, e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // 저장/삭제 (3개)
    // ─────────────────────────────────────────

    @Override
    public int saveStockPrice(StockPriceVO stockPrice) {
        try {
            return stockPriceMapper.insert(stockPrice);
        } catch (Exception e) {
            log.error("주가 저장 실패: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public int updateStockPrice(StockPriceVO stockPrice) {
        try {
            return stockPriceMapper.insert(stockPrice);
        } catch (Exception e) {
            log.error("주가 업데이트 실패: {}", e.getMessage());
            return 0;
        }
    }

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
