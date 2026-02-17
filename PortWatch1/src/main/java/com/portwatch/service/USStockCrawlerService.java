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
 * 미국 주식 크롤링 Service
 * ══════════════════════════════════════════════════════════════
 * [문제 3 수정] Bean 네이밍 오류
 *
 * 콘솔 로그 증거:
 *   USStockCrawlerService     ← 대문자 U로 시작 (비정상)
 *   USStockPriceUpdateServiceImpl ← 대문자 U로 시작 (비정상)
 *
 * 원인:
 *   Spring은 @Service 클래스명 첫 글자를 소문자로 변환
 *   단, 첫 두 글자가 모두 대문자("US")이면 변환하지 않음
 *   → BeanUtils.decapitalize() 규칙에 의해 "USStockCrawlerService" 그대로 등록
 *   → 다른 곳에서 @Autowired로 주입 시 이름 불일치 가능성
 *
 * 해결:
 *   @Service("usStockCrawlerService") 명시적 이름 지정
 *   → 콘솔에서 "usStockCrawlerService" 소문자로 정상 등록됨
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@Service("usStockCrawlerService")   // ★ 수정: 명시적 이름 지정 (기존: @Service 만 있었음)
public class USStockCrawlerService {

    @Autowired
    private StockMapper stockMapper;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final int TIMEOUT_MS = 15000;

    /** S&P 500 + NASDAQ 상위 100 종목 */
    private static final List<String> TOP_100 = Arrays.asList(
        "AAPL", "MSFT", "GOOGL", "AMZN", "NVDA", "META", "TSLA", "AVGO", "ORCL", "ADBE",
        "JPM", "V", "MA", "BAC", "WFC", "GS", "MS", "AXP", "BLK", "SCHW",
        "LLY", "UNH", "JNJ", "ABBV", "MRK", "TMO", "ABT", "DHR", "BMY", "AMGN",
        "WMT", "COST", "HD", "LOW", "MCD", "SBUX", "TGT", "NKE", "TJX", "BKNG",
        "HON", "CAT", "GE", "BA", "UPS", "RTX", "DE", "MMM", "LMT", "NOC",
        "XOM", "CVX", "COP", "SLB", "OXY", "PSX", "MPC", "VLO", "EOG", "HAL",
        "PG", "KO", "PEP", "PM", "MO", "MDLZ", "CL", "KMB", "GIS", "HSY",
        "VZ", "T", "TMUS", "CHTR", "CMCSA", "NFLX", "DIS", "PARA", "WBD", "FOXA",
        "PLD", "AMT", "CCI", "EQIX", "PSA", "DLR", "WELL", "SPG", "O", "VICI",
        "NEE", "DUK", "SO", "D", "AEP", "EXC", "SRE", "XEL", "PCG", "ED"
    );

    private static final List<String> NASDAQ_SET = Arrays.asList(
        "AAPL", "MSFT", "GOOGL", "GOOG", "AMZN", "NVDA", "META", "TSLA",
        "AVGO", "COST", "CSCO", "ADBE", "PEP", "NFLX", "INTC", "AMD",
        "CMCSA", "TMUS", "TXN", "QCOM", "INTU", "AMGN", "SBUX", "CHTR"
    );

    /* ═══════════════════════════════════════════════════════════
     * [메인] TOP 100 크롤링 → AWS MySQL 저장
     * ═══════════════════════════════════════════════════════════ */
    @Transactional
    public int crawlAndUpdateTop100Stocks() {
        log.info("════════════════════════════════════════");
        log.info("  미국 주식 TOP 100 크롤링 시작");
        log.info("════════════════════════════════════════");

        List<StockVO> list = new ArrayList<>();

        for (int i = 0; i < TOP_100.size(); i++) {
            String symbol = TOP_100.get(i);
            log.info("[{}/{}] {} 크롤링 중...", i + 1, TOP_100.size(), symbol);
            try {
                StockVO s = crawlSymbol(symbol);
                if (s != null) list.add(s);
                Thread.sleep(400);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.warn("  {} 실패 (건너뜀): {}", symbol, e.getMessage());
            }
        }

        log.info("크롤링 완료: {}개 수집", list.size());
        return saveToDatabase(list, "US");
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

        String stockName = parseStockName(doc, symbol);
        double price     = parsePrice(doc, symbol);
        if (price <= 0) return null;

        double change    = parseField(doc, symbol, "regularMarketChange");
        double changeR   = parseField(doc, symbol, "regularMarketChangePercent");
        long   volume    = parseVolume(doc, symbol);
        String market    = NASDAQ_SET.contains(symbol) ? "NASDAQ" : "NYSE";

        StockVO s = new StockVO();
        s.setCrawlStockId("US_" + symbol);
        s.setStockCode(symbol);
        s.setStockName(stockName);
        s.setMarket(market);
        s.setCountry("US");
        s.setCurrentPrice(toBD(price));
        s.setPreviousClose(toBD(price - change));
        s.setChangeAmount(toBD(change));
        s.setChangeRate(toBD(changeR));
        s.setVolume(volume);
        return s;
    }

    /* ═══════════════════════════════════════════════════════════
     * [DB 저장]
     * ═══════════════════════════════════════════════════════════ */
    private int saveToDatabase(List<StockVO> list, String country) {
        try { stockMapper.deleteByCountry(country); } catch (Exception ignored) {}
        int saved = 0;
        for (StockVO s : list) {
            try { stockMapper.insertCrawl(s); saved++; }
            catch (Exception e) { log.error("삽입 실패 [{}]: {}", s.getStockCode(), e.getMessage()); }
        }
        log.info("AWS MySQL 저장 완료: {}개", saved);
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
            String v = doc.select(
                "fin-streamer[data-symbol='" + symbol + "'][data-field='regularMarketPrice']"
            ).attr("data-value");
            if (!v.isEmpty()) return Double.parseDouble(v);
            v = doc.select("span[data-testid='qsp-price']").text().replaceAll("[,$\\s]", "");
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

    private BigDecimal toBD(double val) {
        return BigDecimal.valueOf(val);
    }
}
