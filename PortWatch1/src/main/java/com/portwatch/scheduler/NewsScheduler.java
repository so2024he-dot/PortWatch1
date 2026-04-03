package com.portwatch.scheduler;

import com.portwatch.domain.NewsVO;
import com.portwatch.persistence.NewsDAO;
import com.portwatch.service.NewsService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * NewsScheduler - 뉴스 크롤링 자동 스케줄러 (한국+미국 완전판)
 *
 * ✅ 500 오류 수정 내용:
 *   [원인] @Autowired private NewsService newsService;
 *          → NewsService Bean을 찾지 못하면 기동 실패 위험
 *
 *   [수정] @Autowired(required = false) 추가
 *          → newsService null 체크 후 안전하게 사용
 *
 * ✅ 기존 기능 전부 유지:
 *   - 한국 뉴스 자동 크롤링 (30분마다)
 *   - 미국 뉴스 자동 크롤링 (1시간마다)
 *   - crawlKoreanNewsNow() : 수동 즉시 실행
 *   - crawlUSNewsNow()     : 수동 즉시 실행
 *   - crawlAllNewsNow()    : 전체 수동 즉시 실행
 *
 * 실행 시간:
 *   - 한국 뉴스: 서버 시작 1분 후, 이후 30분마다
 *   - 미국 뉴스: 서버 시작 2분 후, 이후 1시간마다
 *
 * @author PortWatch
 * @version 2.1 - 500 오류 수정 (required=false 추가)
 */
@Component
public class NewsScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NewsScheduler.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // ✅ required=false: NewsService 없어도 스케줄러 Bean 등록 성공
    @Autowired(required = false)
    private NewsService newsService;

    // 미국 뉴스 직접 DB 저장용 (NewsDAO 사용)
    @Autowired(required = false)
    private NewsDAO newsDAO;

    // 미국 뉴스 RSS 피드 (MarketWatch - 공개 RSS)
    private static final String MARKETWATCH_RSS =
        "https://feeds.content.dowjones.io/public/rss/mw_marketpulse";
    // Yahoo Finance RSS (미국 시장 뉴스)
    private static final String YAHOO_FINANCE_RSS =
        "https://feeds.finance.yahoo.com/rss/2.0/headline?s=^GSPC,^DJI,^IXIC&region=US&lang=en-US";

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 한국 뉴스 자동 크롤링 (30분마다)
    // initialDelay=60000  : 서버 시작 후 1분 후 첫 실행
    // fixedDelay=1800000  : 이전 완료 후 30분 대기
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ✅ [2026-04-02 수정] initialDelay: 60000(1분) → 480000(8분)
    //    이유: Tomcat 시작 후 DB 연결 풀이 안정화되기 전에 스케줄러가 실행되면
    //         Communications link failure → HikariPool 타임아웃 연쇄 발생
    //         8분 대기로 RDS 마이너 업그레이드 재시작 완료 + HikariPool 안정화 확보
    @Scheduled(initialDelay = 480000, fixedDelay = 1800000)
    public void crawlKoreanNewsAutomatically() {
        logger.info("========================================");
        logger.info("[KR] 한국 뉴스 자동 크롤링 시작: {}", dateFormat.format(new Date()));
        logger.info("========================================");

        // ✅ newsService null 체크
        if (newsService == null) {
            logger.warn("[KR] NewsService를 사용할 수 없습니다. 크롤링 건너뜀.");
            return;
        }

        try {
            int savedCount = newsService.crawlAndSaveNews();

            logger.info("========================================");
            logger.info("[KR] 한국 뉴스 크롤링 완료!");
            logger.info("     저장된 뉴스 수: {} 개", savedCount);
            logger.info("     완료 시간: {}", dateFormat.format(new Date()));
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("========================================");
            logger.error("[KR] 한국 뉴스 크롤링 실패!", e);
            logger.error("     실패 시간: {}", dateFormat.format(new Date()));
            logger.error("     오류 메시지: {}", e.getMessage());
            logger.error("========================================");
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 미국 뉴스 자동 크롤링 (1시간마다)
    // initialDelay=120000 : 서버 시작 후 2분 후 첫 실행
    // fixedDelay=3600000  : 이전 완료 후 1시간 대기
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ✅ [2026-04-02 수정] initialDelay: 120000(2분) → 900000(15분)
    //    이유: 한국 뉴스(8분) + 주식(10분/12분) 스케줄러보다 늦게 시작
    @Scheduled(initialDelay = 900000, fixedDelay = 3600000)
    public void crawlUSNewsAutomatically() {
        logger.info("========================================");
        logger.info("[US] 미국 뉴스 자동 크롤링 시작: {}", dateFormat.format(new Date()));
        logger.info("========================================");

        try {
            int totalSaved = crawlUSNewsFromRSS();

            logger.info("========================================");
            logger.info("[US] 미국 뉴스 크롤링 완료!");
            logger.info("     저장된 뉴스: {} 개", totalSaved);
            logger.info("     완료 시간: {}", dateFormat.format(new Date()));
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("========================================");
            logger.error("[US] 미국 뉴스 크롤링 실패!", e);
            logger.error("     실패 시간: {}", dateFormat.format(new Date()));
            logger.error("     오류 메시지: {}", e.getMessage());
            logger.error("========================================");
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 한국 뉴스 즉시 크롤링 (수동 호출용)
    // AdminNewsUpdateController.updateNews() 에서 호출
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public int crawlKoreanNewsNow() throws Exception {
        logger.info("========================================");
        logger.info("[KR] 수동 한국 뉴스 크롤링 시작: {}", dateFormat.format(new Date()));
        logger.info("========================================");

        // ✅ newsService null 체크
        if (newsService == null) {
            logger.warn("[KR] NewsService를 사용할 수 없습니다.");
            return 0;
        }

        int savedCount = newsService.crawlAndSaveNews();

        logger.info("========================================");
        logger.info("[KR] 수동 한국 뉴스 크롤링 완료: {} 개", savedCount);
        logger.info("========================================");

        return savedCount;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 미국 뉴스 즉시 크롤링 (수동 호출용)
    // AdminNewsUpdateController.updateUSNews() 에서 호출
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public int crawlUSNewsNow() throws Exception {
        logger.info("========================================");
        logger.info("[US] 수동 미국 뉴스 크롤링 시작: {}", dateFormat.format(new Date()));
        logger.info("========================================");

        int savedCount = crawlUSNewsFromRSS();

        logger.info("========================================");
        logger.info("[US] 수동 미국 뉴스 크롤링 완료: {} 개", savedCount);
        logger.info("========================================");

        return savedCount;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 미국 뉴스 RSS 크롤링 (실제 구현)
    // Yahoo Finance RSS → Jsoup XML 파싱 → NewsDAO로 MySQL 저장
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private int crawlUSNewsFromRSS() {
        if (newsDAO == null) {
            logger.warn("[US-RSS] NewsDAO 없음 - 크롤링 건너뜀");
            return 0;
        }

        int savedCount = 0;

        // Yahoo Finance RSS: S&P500, 나스닥 뉴스 피드
        String[] rssUrls = {
            "https://feeds.finance.yahoo.com/rss/2.0/headline?s=%5EGSPC&region=US&lang=en-US",
            "https://feeds.finance.yahoo.com/rss/2.0/headline?s=%5EIXIC&region=US&lang=en-US"
        };

        for (String rssUrl : rssUrls) {
            try {
                logger.info("  [US-RSS] 크롤링: {}", rssUrl);

                Document doc = Jsoup.connect(rssUrl)
                        .timeout(10000)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .parser(Parser.xmlParser())
                        .get();

                Elements items = doc.select("item");
                logger.info("  [US-RSS] {}건 발견", items.size());

                for (Element item : items) {
                    try {
                        Element titleEl = item.select("title").first();
                        Element linkEl  = item.select("link").first();
                        if (titleEl == null || linkEl == null) continue;

                        String title = titleEl.text();
                        String link  = linkEl.text();
                        if (title.isEmpty() || link.isEmpty()) continue;

                        // 중복 체크
                        if (newsDAO.existsByUrl(link)) continue;

                        NewsVO news = new NewsVO();
                        news.setTitle(title);
                        news.setNewsUrl(link);
                        news.setNewsCode("US_" + System.currentTimeMillis() + "_" + savedCount);
                        news.setName("Yahoo Finance");
                        news.setStockCode("US_MARKET");
                        news.setPublishedDate(LocalDateTime.now());

                        newsDAO.insertNews(news);
                        savedCount++;

                    } catch (Exception e) {
                        logger.warn("  [US-RSS] 개별 뉴스 저장 실패: {}", e.getMessage());
                    }
                }

            } catch (Exception e) {
                logger.warn("  [US-RSS] RSS 접속 실패({}): {}", rssUrl, e.getMessage());
            }
        }

        return savedCount;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 전체 뉴스 크롤링 (한국 + 미국)
    // AdminNewsUpdateController.updateAllNews() 에서 호출
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public int crawlAllNewsNow() throws Exception {
        logger.info("========================================");
        logger.info("[ALL] 전체 뉴스 크롤링 시작: {}", dateFormat.format(new Date()));
        logger.info("========================================");

        int totalCount = 0;

        // 한국 뉴스
        try {
            int krCount = crawlKoreanNewsNow();
            totalCount += krCount;
            logger.info("  [KR] 한국 뉴스: {} 개", krCount);
        } catch (Exception e) {
            logger.error("  [KR] 한국 뉴스 크롤링 실패: {}", e.getMessage());
        }

        // 미국 뉴스
        try {
            int usCount = crawlUSNewsNow();
            totalCount += usCount;
            logger.info("  [US] 미국 뉴스: {} 개", usCount);
        } catch (Exception e) {
            logger.error("  [US] 미국 뉴스 크롤링 실패: {}", e.getMessage());
        }

        logger.info("========================================");
        logger.info("[ALL] 전체 뉴스 크롤링 완료: {} 개", totalCount);
        logger.info("========================================");

        return totalCount;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 스케줄러 상태 반환
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public String getStatus() {
        return "NewsScheduler active | NewsService: "
                + (newsService != null ? "connected" : "not available");
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 수동 크롤링 트리거 (하위 호환)
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public void triggerCrawl() {
        logger.info("[NewsScheduler] triggerCrawl() - 수동 크롤링 트리거");
        crawlKoreanNewsAutomatically();
    }
}
