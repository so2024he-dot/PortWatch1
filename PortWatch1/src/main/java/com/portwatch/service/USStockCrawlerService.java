package com.portwatch.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.StockVO;
import com.portwatch.mapper.StockMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 미국 주식 크롤링 Service (컴파일 오류 완전 수정)
 * ══════════════════════════════════════════════════════════════
 *
 * [수정된 컴파일 오류 - 정확한 오류 로그 기반]
 *
 * 라인 182: incompatible types: String → Integer
 *   원인: stock.setStockId("US_" + symbol) → stockId가 Integer
 *   수정: stock.setCrawlStockId("US_" + symbol)
 *
 * 라인 185: cannot find symbol: setMarket(String)
 *   원인: StockVO에 market 필드 없음
 *   수정: StockVO에 String market + setMarket() 추가
 *
 * 라인 187: cannot find symbol: setPreviousClose(double)
 *   원인: StockVO에 previousClose 필드 없음
 *   수정: StockVO에 BigDecimal previousClose + setPreviousClose() 추가
 *
 * 라인 188: incompatible types: Double → BigDecimal
 *   원인: setChangeAmount(BigDecimal)에 double 직접 전달
 *   수정: toBD() 변환
 *
 * 라인 189: incompatible types: Double → BigDecimal
 *   원인: setChangeRate(BigDecimal)에 double 직접 전달
 *   수정: toBD() 변환
 *
 * 라인 190: incompatible types: Double → BigDecimal
 *   원인: setCurrentPrice(BigDecimal)에 double 직접 전달
 *   수정: toBD() 변환
 *
 * [크롤링 소스]
 * Yahoo Finance 개별 종목 페이지
 * https://finance.yahoo.com/quote/{symbol}
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@Service
public class USStockCrawlerService {

    @Autowired
    private StockMapper stockMapper;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final int TIMEOUT_MS = 15000;

    /** S&P 500 + NASDAQ 상위 100 종목 */
    private static final List<String> TOP_100 = Arrays.asList(
        // NASDAQ 대형주
        "AAPL", "MSFT", "GOOGL", "AMZN", "NVDA", "META", "TSLA", "AVGO", "ORCL", "ADBE",
        // NYSE 금융
        "JPM", "V", "MA", "BAC", "WFC", "GS", "MS", "AXP", "BLK", "SCHW",
        // 헬스케어
        "LLY", "UNH", "JNJ", "ABBV", "MRK", "TMO", "ABT", "DHR", "BMY", "AMGN",
        // 소비재
        "WMT", "COST", "HD", "LOW", "MCD", "SBUX", "TGT", "NKE", "TJX", "BKNG",
        // 산업
        "HON", "CAT", "GE", "BA", "UPS", "RTX", "DE", "MMM", "LMT", "NOC",
        // 에너지
        "XOM", "CVX", "COP", "SLB", "OXY", "PSX", "MPC", "VLO", "EOG", "HAL",
        // 필수소비재
        "PG", "KO", "PEP", "PM", "MO", "MDLZ", "CL", "KMB", "GIS", "HSY",
        // 커뮤니케이션
        "VZ", "T", "TMUS", "CHTR", "CMCSA", "NFLX", "DIS", "PARA", "WBD", "FOXA",
        // 리츠
        "PLD", "AMT", "CCI", "EQIX", "PSA", "DLR", "WELL", "SPG", "O", "VICI",
        // 유틸리티
        "NEE", "DUK", "SO", "D", "AEP", "EXC", "SRE", "XEL", "PCG", "ED"
    );

    /** NASDAQ 종목 목록 */
    private static final List<String> NASDAQ_SET = Arrays.asList(
        "AAPL", "MSFT", "GOOGL", "GOOG", "AMZN", "NVDA", "META", "TSLA",
        "AVGO", "COST", "CSCO", "ADBE", "PEP", "NFLX", "INTC", "AMD",
        "CMCSA", "TMUS", "TXN", "QCOM", "INTU", "AMGN", "SBUX", "CHTR",
        "GILD", "ISRG", "ADI", "VRTX", "ADP", "REGN", "MDLZ", "MU",
        "LRCX", "KLAC", "AMAT", "SNPS", "CDNS", "PYPL", "PANW", "MRVL",
        "ORCL", "ADBE", "AVGO"
    );

    /* ═══════════════════════════════════════════════════════════
     * [메인] TOP 100 크롤링 → AWS MySQL 저장
     * ═══════════════════════════════════════════════════════════ */
    @Transactional
    public int crawlAndUpdateTop100Stocks() {
        log.info("════════════════════════════════════════");
        log.info("  미국 주식 TOP 100 크롤링 시작");
        log.info("  소스: Yahoo Finance 개별 종목 페이지");
        log.info("════════════════════════════════════════");

        List<StockVO> stockList = new ArrayList<>();

        for (int i = 0; i < TOP_100.size(); i++) {
            String symbol = TOP_100.get(i);
            log.info("[{}/{}] {} 크롤링 중...", i + 1, TOP_100.size(), symbol);

            try {
                StockVO stock = crawlSymbol(symbol);
                if (stock != null) {
                    stockList.add(stock);
                    log.debug("  → {} ${}", stock.getStockName(), stock.getCurrentPrice());
                }
                Thread.sleep(400); // Yahoo Finance 요청 제한 방지
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.warn("  {} 크롤링 실패 (건너뜀): {}", symbol, e.getMessage());
            }
        }

        log.info("크롤링 완료: 총 {}개 수집", stockList.size());
        return saveToDatabase(stockList, "US");
    }

    /* ═══════════════════════════════════════════════════════════
     * [개별 종목] Yahoo Finance 크롤링
     * ═══════════════════════════════════════════════════════════ */
    private StockVO crawlSymbol(String symbol) throws IOException {
        Document doc = Jsoup.connect("https://finance.yahoo.com/quote/" + symbol)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .header("Accept-Language", "en-US,en;q=0.9")
                .get();

        // 종목명
        String stockName = parseStockName(doc, symbol);

        // 현재가
        double currentPriceD = parsePrice(doc, symbol);
        if (currentPriceD <= 0) {
            log.warn("  {} 현재가 파싱 실패", symbol);
            return null;
        }

        // 변동 데이터
        double changeAmountD  = parseField(doc, symbol, "regularMarketChange");
        double changeRateD    = parseField(doc, symbol, "regularMarketChangePercent");
        long   volumeL        = parseVolume(doc, symbol);
        double previousCloseD = currentPriceD - changeAmountD;

        // 시장 구분
        String market = NASDAQ_SET.contains(symbol) ? "NASDAQ" : "NYSE";

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ✅ 컴파일 오류 수정 완료 구간 (라인 182 ~ 190)
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        StockVO stock = new StockVO();

        // 라인 182 수정: incompatible types String → Integer
        //   ❌ 기존: stock.setStockId("US_" + symbol);
        //   ✅ 수정: setCrawlStockId() 사용
        stock.setCrawlStockId("US_" + symbol);

        stock.setStockCode(symbol);
        stock.setStockName(stockName);

        // 라인 185 수정: cannot find symbol setMarket(String)
        //   ✅ 수정: StockVO에 market 필드 + setter 추가
        stock.setMarket(market);

        stock.setCountry("US");

        // 라인 187 수정: cannot find symbol setPreviousClose(double)
        //   ✅ 수정: StockVO에 previousClose 필드 + setter 추가
        stock.setPreviousClose(toBD(previousCloseD));

        // 라인 188 수정: incompatible types: Double → BigDecimal
        stock.setChangeAmount(toBD(changeAmountD));

        // 라인 189 수정: incompatible types: Double → BigDecimal
        stock.setChangeRate(toBD(changeRateD));

        // 라인 190 수정: incompatible types: Double → BigDecimal
        stock.setCurrentPrice(toBD(currentPriceD));

        stock.setVolume(volumeL);
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

        return stock;
    }

    /* ═══════════════════════════════════════════════════════════
     * [DB 저장] AWS MySQL에 insertCrawl 사용
     * ═══════════════════════════════════════════════════════════ */
    private int saveToDatabase(List<StockVO> list, String country) {
        log.info("AWS MySQL 저장 시작 (국가={}, {}개)", country, list.size());

        try {
            int deleted = stockMapper.deleteByCountry(country);
            log.info("  기존 데이터 삭제: {}개", deleted);
        } catch (Exception e) {
            log.warn("  기존 데이터 삭제 실패 (계속 진행): {}", e.getMessage());
        }

        int saved = 0;
        for (StockVO stock : list) {
            try {
                stockMapper.insertCrawl(stock);
                saved++;
            } catch (Exception e) {
                log.error("  삽입 실패: [{}] {}", stock.getStockCode(), e.getMessage());
            }
        }

        log.info("════════════════════════════════════════");
        log.info("  AWS MySQL 저장 완료: {}개", saved);
        log.info("════════════════════════════════════════");
        return saved;
    }

    /* ═══════════════════════════════════════════════════════════
     * [파싱 헬퍼]
     * ═══════════════════════════════════════════════════════════ */

    private String parseStockName(Document doc, String fallback) {
        try {
            org.jsoup.nodes.Element el = doc.select("h1[class*='yf-']").first();
            if (el != null) {
                String t = el.text();
                return t.contains("(") ? t.substring(0, t.indexOf("(")).trim() : t;
            }
        } catch (Exception ignored) {}
        return fallback;
    }

    private double parsePrice(Document doc, String symbol) {
        try {
            // 방법 1: fin-streamer
            String v = doc.select(
                "fin-streamer[data-symbol='" + symbol + "'][data-field='regularMarketPrice']"
            ).attr("data-value");
            if (!v.isEmpty()) return Double.parseDouble(v);

            // 방법 2: span[data-testid]
            v = doc.select("span[data-testid='qsp-price']").text()
                   .replaceAll("[,$\\s]", "");
            if (!v.isEmpty()) return Double.parseDouble(v);
        } catch (Exception ignored) {}
        return 0.0;
    }

    private double parseField(Document doc, String symbol, String field) {
        try {
            String v = doc.select(
                "fin-streamer[data-symbol='" + symbol + "'][data-field='" + field + "']"
            ).attr("data-value");
            if (!v.isEmpty()) return Double.parseDouble(v);
        } catch (Exception ignored) {}
        return 0.0;
    }

    private long parseVolume(Document doc, String symbol) {
        try {
            String v = doc.select(
                "fin-streamer[data-symbol='" + symbol + "'][data-field='regularMarketVolume']"
            ).attr("data-value");
            if (!v.isEmpty()) return Long.parseLong(v);
        } catch (Exception ignored) {}
        return 0L;
    }

    /** double → BigDecimal 변환 */
    private BigDecimal toBD(double val) {
        return BigDecimal.valueOf(val);
    }
}
