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
 * 한국 주식 크롤링 Service (컴파일 오류 완전 수정)
 * ══════════════════════════════════════════════════════════════
 *
 * [수정된 컴파일 오류 - 정확한 오류 로그 기반]
 *
 * 라인 161: incompatible types: String → Integer
 *   원인: stock.setStockId("KR_" + stockCode) → stockId가 Integer
 *   수정: stock.setCrawlStockId("KR_" + stockCode)
 *         StockVO에 String crawlStockId 필드/setter 추가
 *
 * 라인 164: cannot find symbol: setMarket(String)
 *   원인: StockVO에 market 필드 없음 (marketType만 있음)
 *   수정: StockVO에 String market 필드 + setMarket() 추가
 *         stock.setMarket(market) 그대로 사용 가능
 *
 * 라인 166: incompatible types: Double → BigDecimal
 *   원인: setCurrentPrice(BigDecimal)에 double 값을 직접 전달
 *   수정: toBD(double) 헬퍼로 BigDecimal.valueOf() 변환
 *
 * 라인 167: cannot find symbol: setPreviousClose(double)
 *   원인: StockVO에 previousClose 필드 없음
 *   수정: StockVO에 BigDecimal previousClose + setPreviousClose() 추가
 *
 * 라인 168: incompatible types: Double → BigDecimal
 *   원인: setChangeAmount(BigDecimal)에 double 직접 전달
 *   수정: toBD() 변환
 *
 * 라인 169: incompatible types: Double → BigDecimal
 *   원인: setChangeRate(BigDecimal)에 double 직접 전달
 *   수정: toBD() 변환
 *
 * [크롤링 소스]
 * 네이버 금융 시가총액 페이지
 * https://finance.naver.com/sise/sise_market_sum.naver
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@Service
public class KoreaStockCrawlerService {

    @Autowired
    private StockMapper stockMapper;

    private static final String NAVER_URL  = "https://finance.naver.com/sise/sise_market_sum.naver";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final int TIMEOUT_MS = 10000;

    /* ═══════════════════════════════════════════════════════════
     * [메인] TOP 100 크롤링 → AWS MySQL 저장
     * ═══════════════════════════════════════════════════════════ */
    @Transactional
    public int crawlAndUpdateTop100Stocks() {
        log.info("════════════════════════════════════════");
        log.info("  한국 주식 TOP 100 크롤링 시작");
        log.info("  소스: 네이버 금융 시가총액 페이지");
        log.info("════════════════════════════════════════");

        List<StockVO> stockList = new ArrayList<>();

        try {
            // 1~10 페이지 크롤링 (페이지당 약 10개 = 총 100개)
            for (int page = 1; page <= 10; page++) {
                log.info("[{}/10페이지] 크롤링 중...", page);

                List<StockVO> pageResult = crawlPage(page);
                stockList.addAll(pageResult);

                log.info("  → {}개 수집 (누적 {}개)", pageResult.size(), stockList.size());

                if (page < 10) {
                    Thread.sleep(600); // 서버 부하 방지
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("크롤링 인터럽트");
        } catch (Exception e) {
            log.error("크롤링 중 오류: {}", e.getMessage(), e);
        }

        log.info("크롤링 완료: 총 {}개 수집", stockList.size());

        // AWS MySQL 저장
        return saveToDatabase(stockList, "KR");
    }

    /* ═══════════════════════════════════════════════════════════
     * [페이지 크롤링] 네이버 금융 특정 페이지
     * ═══════════════════════════════════════════════════════════ */
    private List<StockVO> crawlPage(int page) {
        List<StockVO> list = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(NAVER_URL + "?&page=" + page)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .get();

            Elements rows = doc.select("table.type_2 tbody tr");

            for (Element row : rows) {
                // 구분 행 건너뜀
                if (!row.select("td.blank_08").isEmpty()) continue;

                Elements tds = row.select("td");
                if (tds.size() < 7) continue;

                try {
                    StockVO stock = buildStockVO(tds);
                    if (stock != null) list.add(stock);
                } catch (Exception e) {
                    log.debug("행 파싱 실패 (건너뜀): {}", e.getMessage());
                }
            }

        } catch (IOException e) {
            log.error("네이버 금융 접속 실패 (page={}): {}", page, e.getMessage());
        }

        return list;
    }

    /* ═══════════════════════════════════════════════════════════
     * [VO 생성] 오류 수정 핵심 부분
     * ═══════════════════════════════════════════════════════════ */
    private StockVO buildStockVO(Elements tds) {

        // 종목 코드 추출
        String href = tds.get(1).select("a").attr("href");
        if (href.isEmpty() || !href.contains("code=")) return null;
        String stockCode = href.split("code=")[1].trim();
        if (stockCode.isEmpty()) return null;

        // 종목명
        String stockName = tds.get(1).text().trim();

        // 가격 파싱 (String → double → BigDecimal)
        double currentPriceD  = parseNum(tds.get(2).text());
        double changeAmountD  = parseNum(tds.get(3).text());
        double changeRateD    = parseNum(tds.get(4).text().replace("%", ""));
        long   volumeL        = parseLng(tds.get(6).text());
        double previousCloseD = currentPriceD - changeAmountD;

        // 시장 구분
        String market = guessMarket(stockCode);

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ✅ 컴파일 오류 수정 완료 구간 (라인 161 ~ 169)
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        StockVO stock = new StockVO();

        // 라인 161 수정: incompatible types String → Integer
        //   ❌ 기존: stock.setStockId("KR_" + stockCode);
        //   ✅ 수정: setCrawlStockId() 사용 (String 타입 별도 필드)
        stock.setCrawlStockId("KR_" + stockCode);

        stock.setStockCode(stockCode);
        stock.setStockName(stockName);

        // 라인 164 수정: cannot find symbol setMarket(String)
        //   ❌ 기존: 오류 (market 필드 없었음)
        //   ✅ 수정: StockVO에 market 필드 + setter 추가 → 정상 호출 가능
        stock.setMarket(market);

        stock.setCountry("KR");

        // 라인 166 수정: incompatible types: Double → BigDecimal
        //   ❌ 기존: stock.setCurrentPrice(currentPriceD);  // double → BigDecimal 불가
        //   ✅ 수정: toBD() 헬퍼로 BigDecimal.valueOf() 변환
        stock.setCurrentPrice(toBD(currentPriceD));

        // 라인 167 수정: cannot find symbol setPreviousClose(double)
        //   ❌ 기존: 오류 (previousClose 필드 없었음)
        //   ✅ 수정: StockVO에 previousClose 필드 + setter 추가 → 정상 호출 가능
        stock.setPreviousClose(toBD(previousCloseD));

        // 라인 168 수정: incompatible types: Double → BigDecimal
        stock.setChangeAmount(toBD(changeAmountD));

        // 라인 169 수정: incompatible types: Double → BigDecimal
        stock.setChangeRate(toBD(changeRateD));

        stock.setVolume(volumeL);
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

        return stock;
    }

    /* ═══════════════════════════════════════════════════════════
     * [DB 저장] AWS MySQL에 insertCrawl 사용
     * ═══════════════════════════════════════════════════════════ */
    private int saveToDatabase(List<StockVO> list, String country) {
        log.info("AWS MySQL 저장 시작 (국가={}, {}개)", country, list.size());

        // 기존 데이터 삭제
        try {
            int deleted = stockMapper.deleteByCountry(country);
            log.info("  기존 데이터 삭제: {}개", deleted);
        } catch (Exception e) {
            log.warn("  기존 데이터 삭제 실패 (계속 진행): {}", e.getMessage());
        }

        // 신규 삽입
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
     * [유틸리티]
     * ═══════════════════════════════════════════════════════════ */

    /** 시장 구분 (KOSPI / KOSDAQ) */
    private String guessMarket(String stockCode) {
        try {
            return Integer.parseInt(stockCode) >= 200000 ? "KOSDAQ" : "KOSPI";
        } catch (NumberFormatException e) {
            return "KOSPI";
        }
    }

    /** double → BigDecimal 변환 (컴파일 오류 핵심 해결) */
    private BigDecimal toBD(double val) {
        return BigDecimal.valueOf(val);
    }

    /** 문자열 → double (쉼표/공백 제거) */
    private double parseNum(String text) {
        if (text == null || text.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(text.replaceAll("[,\\s]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /** 문자열 → long */
    private long parseLng(String text) {
        if (text == null || text.trim().isEmpty()) return 0L;
        try {
            return Long.parseLong(text.replaceAll("[,\\s]", ""));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
