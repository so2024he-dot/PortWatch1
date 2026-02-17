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
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 미국 주식 크롤링 Service (컴파일 오류 완전 수정)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 * [수정된 컴파일 오류]
 * - 라인 182: setStockId(String) → String 타입으로 수정
 * - 라인 185: setMarket(String) → StockVO에 market 필드 추가로 해결
 * - 라인 187: setPreviousClose(BigDecimal) → StockVO에 previousClose 추가로 해결
 * - 라인 188: setChangeAmount(BigDecimal) → Double→BigDecimal 변환 추가
 * - 라인 189: setChangeRate(BigDecimal) → Double→BigDecimal 변환 추가
 * - 라인 190: setCurrentPrice(BigDecimal) → Double→BigDecimal 변환 추가
 *
 * [크롤링 대상]
 * Yahoo Finance 개별 종목 페이지 (S&P 500 / NASDAQ TOP 100)
 * URL: https://finance.yahoo.com/quote/{symbol}
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Slf4j
@Service
public class USStockCrawlerService {

    @Autowired
    private StockMapper stockMapper;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final int TIMEOUT = 15000;

    /** S&P 500 + NASDAQ 상위 100 종목 */
    private static final List<String> TOP_100_SYMBOLS = Arrays.asList(
        // Mega-Cap Tech (NASDAQ)
        "AAPL", "MSFT", "GOOGL", "AMZN", "NVDA", "META", "TSLA", "AVGO", "ORCL", "ADBE",
        // Finance (NYSE)
        "JPM", "V", "MA", "BAC", "WFC", "GS", "MS", "AXP", "BLK", "SCHW",
        // Healthcare
        "LLY", "UNH", "JNJ", "ABBV", "MRK", "TMO", "ABT", "DHR", "BMY", "AMGN",
        // Consumer Discretionary
        "WMT", "COST", "HD", "LOW", "MCD", "SBUX", "TGT", "NKE", "TJX", "BKNG",
        // Industrial
        "HON", "CAT", "GE", "BA", "UPS", "RTX", "DE", "MMM", "LMT", "NOC",
        // Energy
        "XOM", "CVX", "COP", "SLB", "OXY", "PSX", "MPC", "VLO", "EOG", "HAL",
        // Consumer Staples
        "PG", "KO", "PEP", "PM", "MO", "MDLZ", "CL", "KMB", "GIS", "HSY",
        // Communication
        "VZ", "T", "TMUS", "CHTR", "CMCSA", "NFLX", "DIS", "PARA", "WBD", "FOXA",
        // Real Estate
        "PLD", "AMT", "CCI", "EQIX", "PSA", "DLR", "WELL", "SPG", "O", "VICI",
        // Utilities
        "NEE", "DUK", "SO", "D", "AEP", "EXC", "SRE", "XEL", "PCG", "ED"
    );

    /* ================================================================
     * 메인: 100대 기업 크롤링 & DB 저장
     * ================================================================ */
    @Transactional
    public int crawlAndUpdateTop100Stocks() {
        log.info("═══════════════════════════════════════");
        log.info("  미국 주식 TOP 100 크롤링 시작");
        log.info("═══════════════════════════════════════");

        List<StockVO> stockList = new ArrayList<>();

        for (int i = 0; i < TOP_100_SYMBOLS.size(); i++) {
            String symbol = TOP_100_SYMBOLS.get(i);
            log.info("[{}/{}] {} 크롤링 중...", i + 1, TOP_100_SYMBOLS.size(), symbol);

            try {
                StockVO stock = crawlSymbol(symbol);
                if (stock != null) {
                    stockList.add(stock);
                    log.info("  → ${} ({})", stock.getCurrentPrice(), stock.getStockName());
                }
                Thread.sleep(400); // Yahoo Finance 요청 제한 방지
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.warn("  → 크롤링 실패 (건너뜀): {}", e.getMessage());
            }
        }

        log.info("크롤링 완료: 총 {}개 종목", stockList.size());
        return saveToDatabase(stockList, "US");
    }

    /* ================================================================
     * Yahoo Finance 개별 종목 크롤링
     * ================================================================ */
    private StockVO crawlSymbol(String symbol) throws IOException {
        String url = "https://finance.yahoo.com/quote/" + symbol;

        Document doc = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT)
                .header("Accept-Language", "en-US,en;q=0.9")
                .get();

        // ── 종목명 파싱 ──
        String stockName = parseStockName(doc, symbol);

        // ── 현재가 파싱 ──
        double currentPriceD = parseCurrentPrice(doc, symbol);
        if (currentPriceD <= 0) {
            log.warn("  {} 현재가 파싱 실패", symbol);
            return null;
        }

        // ── 변동액, 변동률 파싱 ──
        double changeAmountD = parseChange(doc, symbol, "regularMarketChange");
        double changeRateD   = parseChange(doc, symbol, "regularMarketChangePercent");

        // ── 거래량 파싱 ──
        long   volume        = parseVolume(doc, symbol);

        // ── 전일 종가 계산 ──
        double previousCloseD = currentPriceD - changeAmountD;

        // ── 시장 구분 ──
        String market = determineMarket(symbol);

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ✅ 컴파일 오류 수정 핵심 부분 (라인 182 ~ 190)
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        StockVO stock = new StockVO();

        // 라인 182 수정: incompatible types String → Integer
        //   오류: stock.setStockId("US_" + symbol);  → Integer 필드
        //   수정: StockVO.stockId를 String 타입으로 변경
        stock.setStockId("US_" + symbol);

        stock.setStockCode(symbol);
        stock.setStockName(stockName);

        // 라인 185 수정: cannot find symbol setMarket(String)
        //   오류: StockVO에 market 필드 자체가 없었음
        //   수정: StockVO에 private String market 필드 추가
        stock.setMarket(market);

        stock.setCountry("US");

        // 라인 187 수정: cannot find symbol setPreviousClose(double)
        //   오류: StockVO에 previousClose 필드가 없었음
        //   수정: StockVO에 private BigDecimal previousClose 필드 추가
        stock.setPreviousClose(toBD(previousCloseD));

        // 라인 188 수정: incompatible types Double → BigDecimal
        stock.setChangeAmount(toBD(changeAmountD));

        // 라인 189 수정: incompatible types Double → BigDecimal
        stock.setChangeRate(toBD(changeRateD));

        // 라인 190 수정: incompatible types Double → BigDecimal
        stock.setCurrentPrice(toBD(currentPriceD));

        stock.setVolume(volume);
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

        return stock;
    }

    /* ================================================================
     * DB 저장
     * ================================================================ */
    private int saveToDatabase(List<StockVO> stockList, String country) {
        log.info("DB 저장 시작 (국가: {}, {}개)", country, stockList.size());

        try {
            int deleted = stockMapper.deleteByCountry(country);
            log.info("  기존 데이터 삭제: {}개", deleted);
        } catch (Exception e) {
            log.warn("  기존 데이터 삭제 실패 (무시): {}", e.getMessage());
        }

        int inserted = 0;
        for (StockVO stock : stockList) {
            try {
                stockMapper.insert(stock);
                inserted++;
            } catch (Exception e) {
                log.error("  삽입 실패: {} → {}", stock.getStockCode(), e.getMessage());
            }
        }

        log.info("═══════════════════════════════════════");
        log.info("  미국 주식 DB 저장 완료: {}개", inserted);
        log.info("═══════════════════════════════════════");
        return inserted;
    }

    /* ================================================================
     * Yahoo Finance 파싱 헬퍼 메서드
     * ================================================================ */

    /** 종목명 파싱 */
    private String parseStockName(Document doc, String symbol) {
        try {
            // 방법 1: h1 태그
            org.jsoup.nodes.Element h1 = doc.select("h1[class*='yf-']").first();
            if (h1 != null) {
                String text = h1.text();
                if (text.contains("(")) {
                    text = text.substring(0, text.indexOf("(")).trim();
                }
                if (!text.isEmpty()) return text;
            }
            // 방법 2: title 태그에서 추출
            String title = doc.title();
            if (title.contains("(")) {
                return title.substring(0, title.indexOf("(")).trim();
            }
        } catch (Exception e) {
            log.debug("종목명 파싱 실패: {}", symbol);
        }
        return symbol; // 실패 시 심볼 반환
    }

    /** 현재가 파싱 */
    private double parseCurrentPrice(Document doc, String symbol) {
        try {
            // fin-streamer 태그 방식
            String val = doc.select(
                "fin-streamer[data-symbol='" + symbol + "'][data-field='regularMarketPrice']"
            ).attr("data-value");
            if (!val.isEmpty()) return Double.parseDouble(val);

            // span[data-testid] 방식 (Yahoo Finance 업데이트 대응)
            val = doc.select("span[data-testid='qsp-price']").text();
            if (!val.isEmpty()) return parseDouble(val);
        } catch (Exception e) {
            log.debug("현재가 파싱 실패: {}", symbol);
        }
        return 0.0;
    }

    /** 변동 데이터 파싱 (변동액, 변동률) */
    private double parseChange(Document doc, String symbol, String field) {
        try {
            String val = doc.select(
                "fin-streamer[data-symbol='" + symbol + "'][data-field='" + field + "']"
            ).attr("data-value");
            if (!val.isEmpty()) return Double.parseDouble(val);
        } catch (Exception e) {
            log.debug("{} 파싱 실패: {}", field, symbol);
        }
        return 0.0;
    }

    /** 거래량 파싱 */
    private long parseVolume(Document doc, String symbol) {
        try {
            String val = doc.select(
                "fin-streamer[data-symbol='" + symbol + "'][data-field='regularMarketVolume']"
            ).attr("data-value");
            if (!val.isEmpty()) return Long.parseLong(val);
        } catch (Exception e) {
            log.debug("거래량 파싱 실패: {}", symbol);
        }
        return 0L;
    }

    /* ================================================================
     * 시장 구분 (NASDAQ / NYSE)
     * ================================================================ */
    private static final List<String> NASDAQ_SYMBOLS = Arrays.asList(
        "AAPL", "MSFT", "GOOGL", "GOOG", "AMZN", "NVDA", "META", "TSLA",
        "AVGO", "COST", "CSCO", "ADBE", "PEP", "NFLX", "INTC", "AMD",
        "CMCSA", "TMUS", "TXN", "QCOM", "INTU", "AMGN", "SBUX",
        "GILD", "ISRG", "ADI", "VRTX", "ADP", "REGN", "MDLZ", "MU",
        "LRCX", "KLAC", "AMAT", "SNPS", "CDNS", "PYPL", "PANW", "MRVL"
    );

    private String determineMarket(String symbol) {
        return NASDAQ_SYMBOLS.contains(symbol) ? "NASDAQ" : "NYSE";
    }

    /* ================================================================
     * 유틸리티
     * ================================================================ */

    /** Double → BigDecimal (컴파일 오류 핵심 수정) */
    private BigDecimal toBD(double value) {
        return BigDecimal.valueOf(value);
    }

    /** 문자열 → double (쉼표, 공백 제거) */
    private double parseDouble(String text) {
        if (text == null || text.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(text.replaceAll("[,\\s]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
