package com.portwatch.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portwatch.domain.StockVO;
import com.portwatch.persistence.StockMapper;

/**
 * 매일 종목 추가 및 SQL 파일 저장 서비스
 */
@Service
public class DailyStockBackupService {
    
    private static final Logger logger = LoggerFactory.getLogger(DailyStockBackupService.class);
    
    @Autowired
    private StockMapper stockMapper;
    
    // SQL 파일 저장 경로
    private static final String BACKUP_DIR = "C:/portwatch_backups/";
    
    /**
     * 매일 자정에 실행 (00:00:00)
     * Cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void dailyBackupStocks() {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📦 매일 자동 백업 시작");
        logger.info("  시간: {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        try {
            // 1. 모든 종목 조회
            List<StockVO> stocks = stockMapper.findAll();
            
            if (stocks == null || stocks.isEmpty()) {
                logger.warn("  ⚠️ 백업할 종목이 없습니다.");
                return;
            }
            
            // 2. SQL 파일 생성
            String fileName = generateBackupFileName();
            File sqlFile = createSqlFile(fileName, stocks);
            
            logger.info("  ✅ 백업 완료!");
            logger.info("  - 종목 수: {} 개", stocks.size());
            logger.info("  - 파일: {}", sqlFile.getAbsolutePath());
            logger.info("  - 크기: {} KB", sqlFile.length() / 1024);
            
        } catch (Exception e) {
            logger.error("❌ 백업 실패: {}", e.getMessage(), e);
        }
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * 매일 오전 9시에 실행 (주식 시장 개장 전)
     */
    @Scheduled(cron = "0 0 9 * * MON-FRI")
    public void morningBackup() {
        logger.info("🌅 오전 백업 실행 (09:00)");
        dailyBackupStocks();
    }
    
    /**
     * 매일 오후 6시에 실행 (주식 시장 종료 후)
     */
    @Scheduled(cron = "0 0 18 * * MON-FRI")
    public void eveningBackup() {
        logger.info("🌆 오후 백업 실행 (18:00)");
        dailyBackupStocks();
    }
    
    /**
     * 수동 백업 메서드
     */
    @Transactional(readOnly = true)
    public String manualBackup() {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🔧 수동 백업 실행");
        
        try {
            List<StockVO> stocks = stockMapper.findAll();
            
            if (stocks == null || stocks.isEmpty()) {
                return "백업할 종목이 없습니다.";
            }
            
            String fileName = generateBackupFileName();
            File sqlFile = createSqlFile(fileName, stocks);
            
            String result = String.format(
                "백업 성공!\n" +
                "- 종목 수: %d 개\n" +
                "- 파일: %s\n" +
                "- 크기: %d KB",
                stocks.size(),
                sqlFile.getAbsolutePath(),
                sqlFile.length() / 1024
            );
            
            logger.info("  ✅ " + result);
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return result;
            
        } catch (Exception e) {
            logger.error("❌ 수동 백업 실패: {}", e.getMessage());
            return "백업 실패: " + e.getMessage();
        }
    }
    
    /**
     * 새 종목 추가 및 즉시 백업
     */
    @Transactional
    public String addStockAndBackup(StockVO stock) {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("➕ 종목 추가 및 백업");
        logger.info("  종목 코드: {}", stock.getStockCode());
        logger.info("  종목 이름: {}", stock.getStockName());
        
        try {
            // 1. 종목 추가
            stockMapper.insert(stock);
            logger.info("  ✅ 종목 추가 완료");
            
            // 2. 즉시 백업
            List<StockVO> allStocks = stockMapper.findAll();
            String fileName = "manual_" + generateBackupFileName();
            File sqlFile = createSqlFile(fileName, allStocks);
            
            String result = String.format(
                "종목 추가 및 백업 완료!\n" +
                "- 추가된 종목: %s (%s)\n" +
                "- 전체 종목 수: %d 개\n" +
                "- 백업 파일: %s",
                stock.getStockName(),
                stock.getStockCode(),
                allStocks.size(),
                sqlFile.getAbsolutePath()
            );
            
            logger.info("  ✅ " + result);
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return result;
            
        } catch (Exception e) {
            logger.error("❌ 종목 추가 및 백업 실패: {}", e.getMessage());
            throw new RuntimeException("종목 추가 실패: " + e.getMessage());
        }
    }
    
    /**
     * 백업 파일명 생성
     */
    private String generateBackupFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "stock_backup_" + sdf.format(new Date()) + ".sql";
    }
    
    /**
     * SQL 파일 생성
     */
    private File createSqlFile(String fileName, List<StockVO> stocks) throws IOException {
        // 백업 디렉토리 생성
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        
        // SQL 파일 생성
        File sqlFile = new File(backupDir, fileName);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sqlFile))) {
            // SQL 헤더
            writer.write("-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            writer.write("-- PortWatch 종목 백업 파일\n");
            writer.write("-- 생성 일시: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
            writer.write("-- 총 종목 수: " + stocks.size() + " 개\n");
            writer.write("-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            
            // 데이터베이스 선택
            writer.write("USE portwatch_db;\n\n");
            
            // 기존 데이터 삭제 (옵션)
            writer.write("-- 기존 데이터 삭제 (필요시 주석 해제)\n");
            writer.write("-- DELETE FROM stock;\n\n");
            
            // INSERT 쿼리 생성
            writer.write("-- 종목 데이터 삽입\n");
            
            int count = 0;
            StringBuilder insertBuilder = new StringBuilder();
            
            for (StockVO stock : stocks) {
                if (count % 50 == 0) {
                    if (count > 0) {
                        // 이전 INSERT 완료
                        insertBuilder.append(";\n\n");
                        writer.write(insertBuilder.toString());
                        insertBuilder = new StringBuilder();
                    }
                    // 새 INSERT 시작
                    insertBuilder.append("INSERT INTO stock (stock_code, stock_name, market_type, country, industry, current_price, change_rate, volume, market_cap) VALUES\n");
                } else {
                    insertBuilder.append(",\n");
                }
                
                // VALUES 추가
                insertBuilder.append(String.format(
                    "    ('%s', '%s', '%s', '%s', %s, %s, %s, %s, %s)",
                    escapeSQL(stock.getStockCode()),
                    escapeSQL(stock.getStockName()),
                    escapeSQL(stock.getMarketType()),
                    escapeSQL(stock.getCountry()),
                    stock.getIndustry() != null ? "'" + escapeSQL(stock.getIndustry()) + "'" : "NULL",
                    stock.getCurrentPrice() != null ? stock.getCurrentPrice() : "NULL",
                    stock.getChangeRate() != null ? stock.getChangeRate() : "0.00",
                    stock.getVolume() != null ? stock.getVolume() : "NULL",
                    stock.getMarketCap() != null ? stock.getMarketCap() : "NULL"
                ));
                
                count++;
            }
            
            // 마지막 INSERT 완료
            if (insertBuilder.length() > 0) {
                insertBuilder.append(";\n\n");
                writer.write(insertBuilder.toString());
            }
            
            // 통계 쿼리
            writer.write("-- 백업 확인 쿼리\n");
            writer.write("SELECT '✅ 백업 완료!' AS STATUS;\n");
            writer.write("SELECT COUNT(*) AS total_stocks FROM stock;\n");
            writer.write("SELECT country, COUNT(*) AS count FROM stock GROUP BY country;\n");
            writer.write("SELECT market_type, COUNT(*) AS count FROM stock GROUP BY market_type;\n");
        }
        
        return sqlFile;
    }
    
    /**
     * SQL Injection 방지
     */
    private String escapeSQL(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("'", "''")
                   .replace("\\", "\\\\");
    }
    
    /**
     * 백업 파일 목록 조회
     */
    public List<String> listBackupFiles() {
        logger.info("📂 백업 파일 목록 조회");
        
        List<String> fileList = new ArrayList<>();
        File backupDir = new File(BACKUP_DIR);
        
        if (!backupDir.exists()) {
            logger.warn("  ⚠️ 백업 디렉토리 없음: {}", BACKUP_DIR);
            return fileList;
        }
        
        File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".sql"));
        
        if (files != null) {
            for (File file : files) {
                String info = String.format(
                    "%s (%d KB) - %s",
                    file.getName(),
                    file.length() / 1024,
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified()))
                );
                fileList.add(info);
                logger.info("  - {}", info);
            }
        }
        
        logger.info("  총 {} 개 백업 파일", fileList.size());
        return fileList;
    }
    
    /**
     * 오래된 백업 파일 삭제 (30일 이상)
     */
    @Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시
    public void cleanOldBackups() {
        logger.info("🗑️ 오래된 백업 파일 정리");
        
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            return;
        }
        
        long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
        int deletedCount = 0;
        
        File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".sql"));
        
        if (files != null) {
            for (File file : files) {
                if (file.lastModified() < thirtyDaysAgo) {
                    if (file.delete()) {
                        logger.info("  🗑️ 삭제: {}", file.getName());
                        deletedCount++;
                    }
                }
            }
        }
        
        logger.info("  ✅ {} 개 파일 삭제 완료", deletedCount);
    }
}
