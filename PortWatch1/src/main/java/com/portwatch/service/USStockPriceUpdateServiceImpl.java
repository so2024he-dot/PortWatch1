package com.portwatch.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * USStockPriceUpdateServiceImpl - 신규
 * ══════════════════════════════════════════════════════════════
 * ✅ 8개 메서드 완전 구현
 * ✅ Yahoo Finance 크롤링
 * ✅ STOCK 테이블 current_price 업데이트
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@Service("usStockPriceUpdateService")
public class USStockPriceUpdateServiceImpl implements USStockPriceUpdateService {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockPriceMapper stockPriceMapper;

    @Override
    public void updateAllUSStockPrices() {
        try {
            log.info("전체 미국 주식 업데이트 시작");
            
            List<StockVO> usStocks = stockMapper.findByCountry("US");
            
            int successCount = 0;
            for (StockVO stock : usStocks) {
                try {
                    updateSingleUSStock(stock.getStockCode());
                    successCount++;
                    Thread.sleep(2000); // 2초 대기
                } catch (Exception e) {
                    log.error("미국 주식 업데이트 실패: {}", stock.getStockCode());
                }
            }
            
            log.info("전체 미국 주식 업데이트 완료: {}/{}", successCount, usStocks.size());
            
        } catch (Exception e) {
            log.error("전체 미국 주식 업데이트 실패: {}", e.getMessage());
        }
    }

    @Override
    public void updateUSStockPrice(String stockCode) {
        updateSingleUSStock(stockCode);
    }

    @Override
    public void updateByMarketType(String marketType) {
        try {
            log.info("미국 시장별 업데이트 시작: {}", marketType);
            
            List<StockVO> stocks = stockMapper.findByMarketType(marketType);
            
            int successCount = 0;
            for (StockVO stock : stocks) {
                try {
                    updateSingleUSStock(stock.getStockCode());
                    successCount++;
                    Thread.sleep(2000);
                } catch (Exception e) {
                    log.error("종목 업데이트 실패: {}", stock.getStockCode());
                }
            }
            
            log.info("미국 시장별 업데이트 완료: {}/{}", successCount, stocks.size());
            
        } catch (Exception e) {
            log.error("미국 시장별 업데이트 실패 (marketType={}): {}", marketType, e.getMessage());
        }
    }

    @Override
    public StockPriceVO updateSingleUSStock(String stockCode) {
        try {
            log.info("단일 미국 주식 업데이트 시작: {}", stockCode);
            
            // 크롤링
            StockPriceVO priceVO = crawlUSStock(stockCode);
            
            if (priceVO == null) {
                throw new RuntimeException("크롤링 실패");
            }
            
            // STOCK 테이블 업데이트
            StockVO stock = stockMapper.findByCode(stockCode);
            if (stock != null) {
                stock.setCurrentPrice(priceVO.getClosePrice());
                stock.setVolume(priceVO.getVolume());
                stock.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                stockMapper.update(stock);
            }
            
            // STOCK_PRICE 테이블 저장
            stockPriceMapper.insert(priceVO);
            
            log.info("단일 미국 주식 업데이트 완료: {}", stockCode);
            return priceVO;
            
        } catch (Exception e) {
            log.error("단일 미국 주식 업데이트 실패 (stockCode={}): {}", stockCode, e.getMessage());
            throw new RuntimeException("업데이트 실패: " + e.getMessage());
        }
    }

    @Override
    public Map<String, StockPriceVO> updateMultipleUSStocks(List<String> stockCodes) {
        Map<String, StockPriceVO> results = new HashMap<>();
        
        for (String stockCode : stockCodes) {
            try {
                StockPriceVO price = updateSingleUSStock(stockCode);
                results.put(stockCode, price);
            } catch (Exception e) {
                log.error("미국 주식 업데이트 실패 (stockCode={}): {}", stockCode, e.getMessage());
            }
        }
        
        return results;
    }

    @Override
    public StockPriceVO crawlUSStock(String stockCode) {
        try {
            log.info("Yahoo Finance 크롤링 시작: {}", stockCode);
            
            String url = "https://finance.yahoo.com/quote/" + stockCode;
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();
            
            // 현재가 (fin-streamer data-symbol)
            Element priceElement = doc.selectFirst("fin-streamer[data-symbol='" + stockCode + "'][data-field='regularMarketPrice']");
            String currentPrice = "0";
            if (priceElement != null) {
                currentPrice = priceElement.attr("value");
            } else {
                // 대체 셀렉터
                Element altPriceElement = doc.selectFirst("fin-streamer.Fw\\(b\\).Fz\\(36px\\)");
                if (altPriceElement != null) {
                    currentPrice = altPriceElement.text().replace(",", "");
                }
            }
            
            // 거래량
            Element volumeElement = doc.selectFirst("fin-streamer[data-symbol='" + stockCode + "'][data-field='regularMarketVolume']");
            String volume = "0";
            if (volumeElement != null) {
                volume = volumeElement.attr("value").replace(",", "");
            }
            
            StockPriceVO priceVO = new StockPriceVO();
            priceVO.setStockCode(stockCode);
            priceVO.setClosePrice(new BigDecimal(currentPrice));
            priceVO.setVolume(Long.parseLong(volume));
            priceVO.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            
            log.info("크롤링 성공: {} - 현재가 ${}", stockCode, currentPrice);
            return priceVO;
            
        } catch (Exception e) {
            log.error("미국 주식 크롤링 실패 (stockCode={}): {}", stockCode, e.getMessage());
            return null;
        }
    }

    @Override
    public int saveUSStockPrice(StockPriceVO stockPrice) {
        try {
            return stockPriceMapper.insert(stockPrice);
        } catch (Exception e) {
            log.error("미국 주가 저장 실패: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public StockPriceVO getLatestUSPrice(String stockCode) {
        try {
            StockVO stock = stockMapper.findByCode(stockCode);
            if (stock == null) return null;

            StockPriceVO priceVO = new StockPriceVO();
            priceVO.setStockCode(stockCode);
            priceVO.setClosePrice(stock.getCurrentPrice());
            priceVO.setVolume(stock.getVolume());
            
            return priceVO;
        } catch (Exception e) {
            log.error("미국 최신 가격 조회 실패 (stockCode={}): {}", stockCode, e.getMessage());
            return null;
        }
    }
}
