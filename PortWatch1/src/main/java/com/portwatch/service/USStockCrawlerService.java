package com.portwatch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.StockVO;
import com.portwatch.mapper.StockMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 미국 주식 크롤링 Service
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * Yahoo Finance에서 S&P 500 시가총액 상위 100개 종목 크롤링
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Slf4j
@Service
public class USStockCrawlerService {

    @Autowired
    private StockMapper stockMapper;

    /**
     * Yahoo Finance Screener URL (시가총액 상위)
     */
    private static final String YAHOO_SCREENER_URL = "https://finance.yahoo.com/screener/predefined/ms_mega_cap";
    
    /**
     * User-Agent
     */
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
    
    /**
     * Timeout (15초)
     */
    private static final int TIMEOUT = 15000;

    /**
     * 미국 주식 100대 기업 목록 (S&P 500 상위 100개)
     */
    private static final String[] TOP_100_SYMBOLS = {
        // Tech Giants
        "AAPL", "MSFT", "GOOGL", "AMZN", "NVDA", "META", "TSLA", "AVGO", "ORCL", "ADBE",
        // Finance
        "JPM", "V", "MA", "BAC", "WFC", "GS", "MS", "AXP", "BLK", "SCHW",
        // Healthcare
        "LLY", "UNH", "JNJ", "ABBV", "MRK", "TMO", "ABT", "DHR", "BMY", "AMGN",
        // Consumer
        "WMT", "COST", "HD", "LOW", "MCD", "SBUX", "TGT", "NKE", "TJX", "DIS",
        // Industrial
        "HON", "CAT", "GE", "BA", "UPS", "RTX", "DE", "MMM", "LMT", "NOC",
        // Energy
        "XOM", "CVX", "COP", "SLB", "OXY", "PSX", "MPC", "VLO", "EOG", "PXD",
        // Consumer Staples
        "PG", "KO", "PEP", "PM", "MO", "MDLZ", "CL", "KMB", "GIS", "K",
        // Telecom
        "VZ", "T", "TMUS", "CHTR", "CMCSA", "DIS", "NFLX", "PARA", "WBD", "FOX",
        // Real Estate
        "PLD", "AMT", "CCI", "EQIX", "PSA", "DLR", "WELL", "SPG", "O", "VICI",
        // Utilities
        "NEE", "DUK", "SO", "D", "AEP", "EXC", "SRE", "XEL", "PCG", "ED"
    };

    /**
     * 미국 주식 100대 기업 크롤링 및 업데이트
     */
    @Transactional
    public int crawlAndUpdateTop100Stocks() {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("미국 주식 100대 기업 크롤링 시작");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            List<StockVO> stockList = new ArrayList<>();
            
            // 100개 종목 크롤링
            for (int i = 0; i < TOP_100_SYMBOLS.length; i++) {
                String symbol = TOP_100_SYMBOLS[i];
                log.info("종목 {}/100 크롤링 중: {}", (i + 1), symbol);
                
                try {
                    StockVO stock = crawlStockData(symbol);
                    if (stock != null) {
                        stockList.add(stock);
                    }
                    
                    // 서버 부하 방지 (0.3초 대기)
                    Thread.sleep(300);
                    
                } catch (Exception e) {
                    log.warn("종목 크롤링 실패: {} - {}", symbol, e.getMessage());
                }
            }

            log.info("크롤링 완료: {}개 종목", stockList.size());

            // 기존 미국 주식 데이터 삭제
            int deletedCount = stockMapper.deleteByCountry("US");
            log.info("기존 데이터 삭제: {}개", deletedCount);

            // 새로운 데이터 삽입
            int insertedCount = 0;
            for (StockVO stock : stockList) {
                try {
                    stockMapper.insert(stock);
                    insertedCount++;
                } catch (Exception e) {
                    log.error("종목 삽입 실패: {} - {}", stock.getStockCode(), e.getMessage());
                }
            }

            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("미국 주식 업데이트 완료: {}개", insertedCount);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            return insertedCount;

        } catch (Exception e) {
            log.error("미국 주식 크롤링 실패", e);
            throw new RuntimeException("미국 주식 크롤링 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 개별 종목 데이터 크롤링 (Yahoo Finance)
     */
    private StockVO crawlStockData(String symbol) throws IOException {
        String url = "https://finance.yahoo.com/quote/" + symbol;
        
        Document doc = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT)
                .get();

        try {
            // 종목명
            String stockName = doc.select("h1[class*='yf-']").first().text();
            if (stockName.contains("(")) {
                stockName = stockName.substring(0, stockName.indexOf("(")).trim();
            }

            // 현재가
            String currentPriceStr = doc.select("fin-streamer[data-symbol='" + symbol + "'][data-field='regularMarketPrice']")
                    .first()
                    .attr("data-value");
            Double currentPrice = parseDouble(currentPriceStr);

            // 변동액
            String changeAmountStr = doc.select("fin-streamer[data-symbol='" + symbol + "'][data-field='regularMarketChange']")
                    .first()
                    .attr("data-value");
            Double changeAmount = parseDouble(changeAmountStr);

            // 변동률
            String changeRateStr = doc.select("fin-streamer[data-symbol='" + symbol + "'][data-field='regularMarketChangePercent']")
                    .first()
                    .attr("data-value");
            Double changeRate = parseDouble(changeRateStr);

            // 거래량
            String volumeStr = doc.select("fin-streamer[data-symbol='" + symbol + "'][data-field='regularMarketVolume']")
                    .first()
                    .attr("data-value");
            Long volume = parseLong(volumeStr);

            // 시장 구분
            String market = determineMarket(symbol);

            // StockVO 생성
            StockVO stock = new StockVO();
            stock.setStockId("US_" + symbol);
            stock.setStockCode(symbol);
            stock.setStockName(stockName);
            stock.setMarket(market);
            stock.setCountry("US");
            stock.setCurrentPrice(currentPrice);
            stock.setPreviousClose(currentPrice - changeAmount);
            stock.setChangeAmount(changeAmount);
            stock.setChangeRate(changeRate);
            stock.setVolume(volume);

            log.debug("크롤링 성공: {} - ${}", symbol, currentPrice);
            
            return stock;

        } catch (Exception e) {
            log.error("데이터 파싱 실패: {} - {}", symbol, e.getMessage());
            return null;
        }
    }

    /**
     * 시장 구분 (NYSE/NASDAQ)
     */
    private String determineMarket(String symbol) {
        // NASDAQ 주요 기업
        String[] nasdaqSymbols = {
            "AAPL", "MSFT", "GOOGL", "GOOG", "AMZN", "NVDA", "META", "TSLA", 
            "AVGO", "COST", "CSCO", "ADBE", "PEP", "NFLX", "INTC", "AMD",
            "CMCSA", "TMUS", "TXN", "QCOM", "INTU", "AMGN", "HON", "SBUX",
            "GILD", "ISRG", "ADI", "VRTX", "ADP", "REGN", "MDLZ", "MU",
            "LRCX", "KLAC", "AMAT", "SNPS", "CDNS", "PYPL", "PANW", "MRVL"
        };
        
        for (String nasdaq : nasdaqSymbols) {
            if (symbol.equals(nasdaq)) {
                return "NASDAQ";
            }
        }
        
        return "NYSE";
    }

    /**
     * String → Double 변환
     */
    private Double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * String → Long 변환
     */
    private Long parseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return 0L;
        }
    }
}
