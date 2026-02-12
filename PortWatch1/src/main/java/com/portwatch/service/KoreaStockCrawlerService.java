package com.portwatch.service;

import java.io.IOException;
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
 * 한국 주식 크롤링 Service (수정 완료)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 라인 160, 163, 165-168 setter 메소드 수정
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Slf4j
@Service
public class KoreaStockCrawlerService {

    @Autowired
    private StockMapper stockMapper;

    /**
     * 네이버 금융 시가총액 URL
     */
    private static final String NAVER_MARKET_CAP_URL = "https://finance.naver.com/sise/sise_market_sum.naver";
    
    /**
     * User-Agent
     */
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
    
    /**
     * Timeout (10초)
     */
    private static final int TIMEOUT = 10000;

    /**
     * 한국 주식 100대 기업 크롤링 및 업데이트
     */
    @Transactional
    public int crawlAndUpdateTop100Stocks() {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("한국 주식 100대 기업 크롤링 시작");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            List<StockVO> stockList = new ArrayList<>();
            
            // 10페이지 크롤링 (페이지당 10개 = 총 100개)
            for (int page = 1; page <= 10; page++) {
                log.info("페이지 {}/10 크롤링 중...", page);
                
                List<StockVO> pageStocks = crawlPage(page);
                stockList.addAll(pageStocks);
                
                // 서버 부하 방지 (0.5초 대기)
                Thread.sleep(500);
            }

            log.info("크롤링 완료: {}개 종목", stockList.size());

            // 기존 한국 주식 데이터 삭제
            int deletedCount = stockMapper.deleteByCountry("KR");
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
            log.info("한국 주식 업데이트 완료: {}개", insertedCount);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            return insertedCount;

        } catch (Exception e) {
            log.error("한국 주식 크롤링 실패", e);
            throw new RuntimeException("한국 주식 크롤링 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 특정 페이지 크롤링
     */
    private List<StockVO> crawlPage(int page) throws IOException {
        List<StockVO> stockList = new ArrayList<>();

        String url = NAVER_MARKET_CAP_URL + "?&page=" + page;
        
        Document doc = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT)
                .get();

        Elements rows = doc.select("table.type_2 tbody tr");

        for (Element row : rows) {
            try {
                // 빈 행 건너뛰기
                if (row.select("td.blank_08").size() > 0) {
                    continue;
                }

                Elements tds = row.select("td");
                if (tds.size() < 12) {
                    continue;
                }

                // 종목 코드
                String stockCode = tds.get(1).select("a").attr("href");
                if (stockCode.isEmpty()) {
                    continue;
                }
                stockCode = stockCode.split("code=")[1];

                // 종목명
                String stockName = tds.get(1).text();

                // 현재가
                String currentPriceStr = tds.get(2).text().replaceAll(",", "");
                Double currentPrice = parseDouble(currentPriceStr);

                // 전일 대비
                String changeAmountStr = tds.get(3).text().replaceAll(",", "");
                Double changeAmount = parseDouble(changeAmountStr);

                // 등락률
                String changeRateStr = tds.get(4).text().replace("%", "").replaceAll(",", "");
                Double changeRate = parseDouble(changeRateStr);

                // 거래량
                String volumeStr = tds.get(6).text().replaceAll(",", "");
                Long volume = parseLong(volumeStr);

                // 시장 구분 (KOSPI/KOSDAQ)
                String market = determineMarket(stockCode);

                // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                // ✅ 수정된 부분 (라인 160-168)
                // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                StockVO stock = new StockVO();
                stock.setStockId("KR_" + stockCode);              // 라인 160 수정
                stock.setStockCode(stockCode);
                stock.setStockName(stockName);
                stock.setMarket(market);                          // 라인 163 수정
                stock.setCountry("KR");
                stock.setCurrentPrice(currentPrice);              // 라인 165 수정
                stock.setPreviousClose(currentPrice - changeAmount); // 라인 166 수정
                stock.setChangeAmount(changeAmount);              // 라인 167 수정
                stock.setChangeRate(changeRate);                  // 라인 168 수정
                stock.setVolume(volume);
                // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

                stockList.add(stock);

            } catch (Exception e) {
                log.warn("행 파싱 실패: {}", e.getMessage());
            }
        }

        return stockList;
    }

    /**
     * 시장 구분 (KOSPI/KOSDAQ)
     */
    private String determineMarket(String stockCode) {
        try {
            int code = Integer.parseInt(stockCode);
            
            // KOSPI: 005000~009999, KOSDAQ: 그 외
            if (code >= 5000 && code <= 9999) {
                return "KOSPI";
            } else if (code >= 100000) {
                return "KOSDAQ";
            } else {
                return "KOSPI";
            }
        } catch (Exception e) {
            return "KOSPI";
        }
    }

    /**
     * String → Double 변환
     */
    private Double parseDouble(String str) {
        try {
            if (str == null || str.trim().isEmpty()) {
                return 0.0;
            }
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
            if (str == null || str.trim().isEmpty()) {
                return 0L;
            }
            return Long.parseLong(str);
        } catch (Exception e) {
            return 0L;
        }
    }
}
