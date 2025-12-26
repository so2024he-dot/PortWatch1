package com.portwatch.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portwatch.domain.NewsVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.NewsDAO;
import com.portwatch.persistence.StockDAO;

/**
 * ✅ 초기 데이터 자동 생성 컨트롤러
 * 
 * 프레젠테이션 및 테스트를 위한 더미 데이터 생성
 * 
 * @author PortWatch Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/init")
public class InitDataController {
    
    @Autowired
    private StockDAO stockDAO;
    
    @Autowired
    private NewsDAO newsDAO;
    
    private Random random = new Random();
    
    /**
     * ✅ 모든 초기 데이터 생성
     * POST /api/init/all
     */
    @PostMapping("/all")
    public ResponseEntity<Map<String, Object>> initAllData() {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🚀 초기 데이터 생성 시작");
            
            // 1. 한국 주식 생성
            int krStockCount = createKoreanStocks();
            
            // 2. 미국 주식 생성
            int usStockCount = createUSStocks();
            
            // 3. 뉴스 생성
            int newsCount = createDummyNews();
            
            System.out.println("✅ 초기 데이터 생성 완료!");
            System.out.println("  - 한국 주식: " + krStockCount + "개");
            System.out.println("  - 미국 주식: " + usStockCount + "개");
            System.out.println("  - 뉴스: " + newsCount + "개");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            result.put("success", true);
            result.put("message", "초기 데이터 생성 완료");
            result.put("krStockCount", krStockCount);
            result.put("usStockCount", usStockCount);
            result.put("newsCount", newsCount);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("❌ 초기 데이터 생성 실패: " + e.getMessage());
            e.printStackTrace();
            
            result.put("success", false);
            result.put("message", "초기 데이터 생성 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * ✅ 한국 주식 더미 데이터 생성
     */
    @PostMapping("/stocks/korean")
    public ResponseEntity<Map<String, Object>> createKoreanStocks() {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            int count = createKoreanStocks();
            
            result.put("success", true);
            result.put("count", count);
            result.put("message", count + "개의 한국 주식 생성 완료");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "한국 주식 생성 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * ✅ 미국 주식 더미 데이터 생성
     */
    @PostMapping("/stocks/us")
    public ResponseEntity<Map<String, Object>> createUSStocks() {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            int count = createUSStocks();
            
            result.put("success", true);
            result.put("count", count);
            result.put("message", count + "개의 미국 주식 생성 완료");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "미국 주식 생성 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * ✅ 뉴스 더미 데이터 생성
     */
    @PostMapping("/news")
    public ResponseEntity<Map<String, Object>> createNews() {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            int count = createDummyNews();
            
            result.put("success", true);
            result.put("count", count);
            result.put("message", count + "개의 뉴스 생성 완료");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "뉴스 생성 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Private Helper Methods
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    /**
     * 한국 주식 생성 로직
     */
    private int createKoreanStocks() throws Exception {
        
        // 한국 대표 종목
        String[][] koreanStocks = {
            {"005930", "삼성전자", "KOSPI"},
            {"000660", "SK하이닉스", "KOSPI"},
            {"035420", "NAVER", "KOSPI"},
            {"035720", "카카오", "KOSPI"},
            {"051910", "LG화학", "KOSPI"},
            {"006400", "삼성SDI", "KOSPI"},
            {"207940", "삼성바이오로직스", "KOSPI"},
            {"005380", "현대차", "KOSPI"},
            {"000270", "기아", "KOSPI"},
            {"068270", "셀트리온", "KOSPI"},
            {"028260", "삼성물산", "KOSPI"},
            {"105560", "KB금융", "KOSPI"},
            {"055550", "신한지주", "KOSPI"},
            {"096770", "SK이노베이션", "KOSPI"},
            {"017670", "SK텔레콤", "KOSPI"},
            {"034730", "SK", "KOSPI"},
            {"003550", "LG", "KOSPI"},
            {"066570", "LG전자", "KOSPI"},
            {"012330", "현대모비스", "KOSPI"},
            {"009150", "삼성전기", "KOSPI"}
        };
        
        int count = 0;
        
        for (String[] stock : koreanStocks) {
            try {
                // 중복 체크
                StockVO existing = stockDAO.selectByCode(stock[0]);
                
                if (existing == null) {
                    StockVO newStock = new StockVO();
                    newStock.setStockCode(stock[0]);
                    newStock.setStockName(stock[1]);
                    newStock.setMarketType(stock[2]);
                    newStock.setCountry("KR");
                    
                    // 랜덤 가격 생성 (10,000 ~ 500,000원)
                    BigDecimal price = new BigDecimal(10000 + random.nextInt(490000));
                    newStock.setCurrentPrice(price);
                    newStock.setOpenPrice(price.multiply(new BigDecimal("0.99")));
                    newStock.setHighPrice(price.multiply(new BigDecimal("1.02")));
                    newStock.setLowPrice(price.multiply(new BigDecimal("0.98")));
                    newStock.setPreviousClose(price.multiply(new BigDecimal("0.995")));
                    
                    // 거래량
                    newStock.setVolume(1000000L + random.nextInt(9000000));
                    
                    // 시가총액 (억원)
                    newStock.setMarketCap(new BigDecimal(100000L + random.nextInt(900000)));
                    
                    newStock.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    newStock.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    
                    stockDAO.insertStock(newStock);
                    count++;
                    
                    System.out.println("✅ 한국 주식 추가: " + stock[1] + " (" + stock[0] + ")");
                }
                
            } catch (Exception e) {
                System.err.println("❌ 주식 추가 실패: " + stock[1] + " - " + e.getMessage());
            }
        }
        
        return count;
    }
    
    /**
     * 미국 주식 생성 로직
     */
    private int createUSStocks() throws Exception {
        
        // 미국 대표 종목
        String[][] usStocks = {
            {"AAPL", "Apple Inc.", "NASDAQ"},
            {"MSFT", "Microsoft Corporation", "NASDAQ"},
            {"GOOGL", "Alphabet Inc.", "NASDAQ"},
            {"AMZN", "Amazon.com Inc.", "NASDAQ"},
            {"NVDA", "NVIDIA Corporation", "NASDAQ"},
            {"TSLA", "Tesla Inc.", "NASDAQ"},
            {"META", "Meta Platforms Inc.", "NASDAQ"},
            {"NFLX", "Netflix Inc.", "NASDAQ"},
            {"AMD", "Advanced Micro Devices", "NASDAQ"},
            {"INTC", "Intel Corporation", "NASDAQ"},
            {"JPM", "JPMorgan Chase & Co.", "NYSE"},
            {"BAC", "Bank of America Corp", "NYSE"},
            {"WMT", "Walmart Inc.", "NYSE"},
            {"V", "Visa Inc.", "NYSE"},
            {"JNJ", "Johnson & Johnson", "NYSE"},
            {"PG", "Procter & Gamble Co", "NYSE"},
            {"DIS", "Walt Disney Company", "NYSE"},
            {"MA", "Mastercard Inc", "NYSE"},
            {"HD", "Home Depot Inc", "NYSE"},
            {"KO", "Coca-Cola Company", "NYSE"}
        };
        
        int count = 0;
        
        for (String[] stock : usStocks) {
            try {
                // 중복 체크
                StockVO existing = stockDAO.selectByCode(stock[0]);
                
                if (existing == null) {
                    StockVO newStock = new StockVO();
                    newStock.setStockCode(stock[0]);
                    newStock.setStockName(stock[1]);
                    newStock.setMarketType(stock[2]);
                    newStock.setCountry("US");
                    
                    // 랜덤 가격 생성 (USD $50 ~ $500)
                    BigDecimal price = new BigDecimal(50 + random.nextInt(450));
                    newStock.setCurrentPrice(price);
                    newStock.setOpenPrice(price.multiply(new BigDecimal("0.99")));
                    newStock.setHighPrice(price.multiply(new BigDecimal("1.02")));
                    newStock.setLowPrice(price.multiply(new BigDecimal("0.98")));
                    newStock.setPreviousClose(price.multiply(new BigDecimal("0.995")));
                    
                    // 거래량
                    newStock.setVolume(10000000L + random.nextInt(90000000));
                    
                    // 시가총액 (백만 USD)
                    newStock.setMarketCap(new BigDecimal(10000L + random.nextInt(990000)));
                    
                    newStock.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    newStock.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    
                    stockDAO.insertStock(newStock);
                    count++;
                    
                    System.out.println("✅ 미국 주식 추가: " + stock[1] + " (" + stock[0] + ")");
                }
                
            } catch (Exception e) {
                System.err.println("❌ 주식 추가 실패: " + stock[1] + " - " + e.getMessage());
            }
        }
        
        return count;
    }
    
    /**
     * 뉴스 생성 로직
     */
    private int createDummyNews() throws Exception {
        
        List<NewsVO> newsList = new ArrayList<>();
        
        // 한국 뉴스 템플릿
        String[][] koreanNews = {
            {"삼성전자, AI 반도체 신제품 출시 예정", "https://finance.naver.com/news/", "네이버금융", "005930", "삼성전자"},
            {"SK하이닉스, HBM3E 양산 본격화", "https://finance.naver.com/news/", "네이버금융", "000660", "SK하이닉스"},
            {"NAVER, AI 검색 서비스 업그레이드", "https://finance.naver.com/news/", "네이버금융", "035420", "NAVER"},
            {"카카오, 모빌리티 사업 확장", "https://finance.naver.com/news/", "네이버금융", "035720", "카카오"},
            {"LG화학, 배터리 수주 급증", "https://finance.naver.com/news/", "네이버금융", "051910", "LG화학"},
            {"현대차, 전기차 판매 호조", "https://finance.naver.com/news/", "네이버금융", "005380", "현대차"},
            {"기아, 북미시장 점유율 상승", "https://finance.naver.com/news/", "네이버금융", "000270", "기아"},
            {"셀트리온, 바이오시밀러 FDA 승인", "https://finance.naver.com/news/", "네이버금융", "068270", "셀트리온"},
            {"KB금융, 디지털 금융 강화", "https://finance.naver.com/news/", "네이버금융", "105560", "KB금융"},
            {"삼성SDI, 전고체 배터리 개발 속도", "https://finance.naver.com/news/", "네이버금융", "006400", "삼성SDI"}
        };
        
        // 미국 뉴스 템플릿
        String[][] usNews = {
            {"Apple unveils new AI features", "https://finance.yahoo.com/", "Yahoo Finance", "AAPL", "Apple"},
            {"Microsoft expands cloud services", "https://finance.yahoo.com/", "Yahoo Finance", "MSFT", "Microsoft"},
            {"Google announces quantum computing breakthrough", "https://finance.yahoo.com/", "Yahoo Finance", "GOOGL", "Alphabet"},
            {"Amazon Prime Day sales record", "https://finance.yahoo.com/", "Yahoo Finance", "AMZN", "Amazon"},
            {"NVIDIA releases new GPU architecture", "https://finance.yahoo.com/", "Yahoo Finance", "NVDA", "NVIDIA"},
            {"Tesla achieves production milestone", "https://finance.yahoo.com/", "Yahoo Finance", "TSLA", "Tesla"},
            {"Meta launches new VR platform", "https://finance.yahoo.com/", "Yahoo Finance", "META", "Meta"},
            {"Netflix subscriber growth exceeds expectations", "https://finance.yahoo.com/", "Yahoo Finance", "NFLX", "Netflix"},
            {"AMD gains market share in data centers", "https://finance.yahoo.com/", "Yahoo Finance", "AMD", "AMD"},
            {"Intel announces manufacturing expansion", "https://finance.yahoo.com/", "Yahoo Finance", "INTC", "Intel"}
        };
        
        int count = 0;
        
        // 한국 뉴스 생성
        for (String[] news : koreanNews) {
            try {
                NewsVO newsVO = new NewsVO();
                newsVO.setTitle(news[0]);
                newsVO.setLink(news[1]);
                newsVO.setSource(news[2]);
                newsVO.setStockCode(news[3]);
                newsVO.setStockName(news[4]);
                newsVO.setCountry("KR");
                newsVO.setPublishedAt(new Timestamp(System.currentTimeMillis() - random.nextInt(86400000)));
                newsVO.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                
                newsDAO.insertNews(newsVO);
                count++;
                
            } catch (Exception e) {
                System.err.println("❌ 뉴스 추가 실패: " + news[0]);
            }
        }
        
        // 미국 뉴스 생성
        for (String[] news : usNews) {
            try {
                NewsVO newsVO = new NewsVO();
                newsVO.setTitle(news[0]);
                newsVO.setLink(news[1]);
                newsVO.setSource(news[2]);
                newsVO.setStockCode(news[3]);
                newsVO.setStockName(news[4]);
                newsVO.setCountry("US");
                newsVO.setPublishedAt(new Timestamp(System.currentTimeMillis() - random.nextInt(86400000)));
                newsVO.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                
                newsDAO.insertNews(newsVO);
                count++;
                
            } catch (Exception e) {
                System.err.println("❌ 뉴스 추가 실패: " + news[0]);
            }
        }
        
        return count;
    }
}
