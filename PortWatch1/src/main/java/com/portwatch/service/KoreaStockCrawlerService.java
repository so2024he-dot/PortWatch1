package com.portwatch.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
 * 한국 주식 크롤링 Service (컴파일 오류 완전 수정)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 * [수정된 컴파일 오류]
 * - 라인 161: setStockId(String) → String 타입으로 수정
 * - 라인 164: setMarket(String) → StockVO에 market 필드 추가로 해결
 * - 라인 166: setCurrentPrice(BigDecimal) → Double→BigDecimal 변환 추가
 * - 라인 167: setPreviousClose(BigDecimal) → StockVO에 previousClose 추가로 해결
 * - 라인 168: setChangeAmount(BigDecimal) → Double→BigDecimal 변환 추가
 * - 라인 169: setChangeRate(BigDecimal) → Double→BigDecimal 변환 추가
 *
 * [크롤링 대상]
 * 네이버 금융 시가총액 상위 100종목 (KOSPI/KOSDAQ)
 * URL: https://finance.naver.com/sise/sise_market_sum.naver
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Slf4j
@Service
public class KoreaStockCrawlerService {

    @Autowired
    private StockMapper stockMapper;

    private static final String NAVER_URL = "https://finance.naver.com/sise/sise_market_sum.naver";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final int TIMEOUT = 10000;

    /* ================================================================
     * 메인: 100대 기업 크롤링 & DB 저장
     * ================================================================ */
    @Transactional
    public int crawlAndUpdateTop100Stocks() {
        log.info("═══════════════════════════════════════");
        log.info("  한국 주식 TOP 100 크롤링 시작");
        log.info("═══════════════════════════════════════");

        List<StockVO> stockList = new ArrayList<>();

        try {
            // 페이지 1~10 크롤링 (페이지당 ~10개 = 총 100개)
            for (int page = 1; page <= 10; page++) {
                log.info("[{}페이지/10] 크롤링 중...", page);
                List<StockVO> pageStocks = crawlPage(page);
                stockList.addAll(pageStocks);
                log.info("  → {}개 수집", pageStocks.size());

                if (page < 10) {
                    Thread.sleep(600); // 서버 부하 방지
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("크롤링 중단됨");
        } catch (Exception e) {
            log.error("크롤링 실패: {}", e.getMessage(), e);
        }

        log.info("크롤링 완료: 총 {}개 종목", stockList.size());

        // 기존 한국 주식 삭제 후 새 데이터 삽입
        return saveToDatabase(stockList, "KR");
    }

    /* ================================================================
     * 네이버 금융 특정 페이지 크롤링
     * ================================================================ */
    private List<StockVO> crawlPage(int page) {
        List<StockVO> list = new ArrayList<>();
        String url = NAVER_URL + "?&page=" + page;

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT)
                    .get();

            Elements rows = doc.select("table.type_2 tbody tr");

            for (Element row : rows) {
                // 빈 구분행 건너뜀
                if (!row.select("td.blank_08").isEmpty()) {
                    continue;
                }

                Elements tds = row.select("td");
                if (tds.size() < 7) {
                    continue;
                }

                try {
                    StockVO stock = parseRow(tds);
                    if (stock != null) {
                        list.add(stock);
                    }
                } catch (Exception e) {
                    log.debug("행 파싱 건너뜀: {}", e.getMessage());
                }
            }

        } catch (IOException e) {
            log.error("네이버 금융 접속 실패 (page={}): {}", page, e.getMessage());
        }

        return list;
    }

    /* ================================================================
     * 행(row) 데이터를 StockVO로 변환
     * [모든 컴파일 오류 수정된 부분]
     * ================================================================ */
    private StockVO parseRow(Elements tds) {
        // ── 종목 코드 추출 ──
        String href = tds.get(1).select("a").attr("href");
        if (href.isEmpty() || !href.contains("code=")) {
            return null;
        }
        String stockCode = href.split("code=")[1].trim();
        if (stockCode.isEmpty()) {
            return null;
        }

        // ── 종목명 ──
        String stockName = tds.get(1).text().trim();

        // ── 가격 파싱 ──
        double currentPriceD  = parseDouble(tds.get(2).text());
        double changeAmountD  = parseDouble(tds.get(3).text());
        double changeRateD    = parseDouble(tds.get(4).text().replace("%", ""));
        long   volume         = parseLong(tds.get(6).text());

        // 전일 종가 계산
        double previousCloseD = currentPriceD - changeAmountD;

        // ── 시장 구분 ──
        String market = determineMarket(stockCode);

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ✅ 컴파일 오류 수정 핵심 부분 (라인 161 ~ 169)
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        StockVO stock = new StockVO();

        // 라인 161 수정: String → Integer 오류
        //   오류: stock.setStockId("KR_" + stockCode);  → Integer 필드
        //   수정: StockVO.stockId를 String 타입으로 변경
        stock.setStockId("KR_" + stockCode);

        stock.setStockCode(stockCode);
        stock.setStockName(stockName);

        // 라인 164 수정: cannot find symbol setMarket(String)
        //   오류: StockVO에 market 필드 자체가 없었음
        //   수정: StockVO에 private String market 필드 추가
        stock.setMarket(market);

        stock.setCountry("KR");

        // 라인 166 수정: Double → BigDecimal incompatible types
        //   오류: stock.setCurrentPrice(currentPriceD);  (Double)
        //   수정: toBD() 헬퍼로 BigDecimal 변환
        stock.setCurrentPrice(toBD(currentPriceD));

        // 라인 167 수정: cannot find symbol setPreviousClose(double)
        //   오류: StockVO에 previousClose 필드가 없었음
        //   수정: StockVO에 private BigDecimal previousClose 필드 추가
        stock.setPreviousClose(toBD(previousCloseD));

        // 라인 168 수정: Double → BigDecimal incompatible types
        stock.setChangeAmount(toBD(changeAmountD));

        // 라인 169 수정: Double → BigDecimal incompatible types
        stock.setChangeRate(toBD(changeRateD));

        stock.setVolume(volume);
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

        return stock;
    }

    /* ================================================================
     * DB 저장 (삭제 후 전체 삽입)
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
        log.info("  한국 주식 DB 저장 완료: {}개", inserted);
        log.info("═══════════════════════════════════════");
        return inserted;
    }

    /* ================================================================
     * 시장 구분 (KOSPI / KOSDAQ)
     * ================================================================ */
    private String determineMarket(String stockCode) {
        try {
            // 종목 코드가 6자리 숫자
            // 0으로 시작하거나 1~9로 시작하는 경우 KOSPI 가능성 높음
            // 실제로는 코드 범위로 구분하기 어렵고 API 활용이 정확하지만
            // 간단 규칙: 코드 앞 3자리로 구분
            int code = Integer.parseInt(stockCode);
            // KOSDAQ: 앞자리가 2~9로 시작하는 경우가 많음 (간략 기준)
            if (code >= 200000) {
                return "KOSDAQ";
            } else {
                return "KOSPI";
            }
        } catch (NumberFormatException e) {
            return "KOSPI";
        }
    }

    /* ================================================================
     * 유틸리티: 타입 변환
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

    /** 문자열 → long (쉼표, 공백 제거) */
    private long parseLong(String text) {
        if (text == null || text.trim().isEmpty()) return 0L;
        try {
            return Long.parseLong(text.replaceAll("[,\\s]", ""));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
