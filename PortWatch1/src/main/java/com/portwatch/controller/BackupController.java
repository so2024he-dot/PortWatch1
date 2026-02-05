package com.portwatch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.portwatch.domain.MemberVO;
import com.portwatch.domain.StockVO;
import com.portwatch.service.DailyStockBackupService;

/**
 * 백업 관리 컨트롤러
 */
@Controller
@RequestMapping("/backup")
public class BackupController {
    
    private static final Logger logger = LoggerFactory.getLogger(BackupController.class);
    
    @Autowired
    private DailyStockBackupService backupService;
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 백업 관리 페이지
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @GetMapping("/manage")
    public String backupManagePage(HttpSession session, Model model) {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("📦 백업 관리 페이지 접근");
        
        // 관리자 확인
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            logger.warn("  ❌ 로그인 필요");
            return "redirect:/member/login";
        }
        
        if (!"ADMIN".equals(loginMember.getMemberRole())) {
            logger.warn("  ❌ 관리자 권한 필요");
            return "redirect:/dashboard";
        }
        
        // 백업 파일 목록
        List<String> backupFiles = backupService.listBackupFiles();
        model.addAttribute("backupFiles", backupFiles);
        
        logger.info("  ✅ 백업 파일 {} 개", backupFiles.size());
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return "backup/manage";
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 수동 백업 실행
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @PostMapping("/execute")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> executeBackup(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("🔧 수동 백업 요청");
        
        // 관리자 확인
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null || !"ADMIN".equals(loginMember.getMemberRole())) {
            logger.warn("  ❌ 권한 없음");
            result.put("success", false);
            result.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        
        try {
            String message = backupService.manualBackup();
            
            result.put("success", true);
            result.put("message", message);
            
            logger.info("  ✅ 백업 성공");
            
        } catch (Exception e) {
            logger.error("  ❌ 백업 실패: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "백업 실패: " + e.getMessage());
        }
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return ResponseEntity.ok(result);
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 백업 파일 목록 조회
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> listBackupFiles(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        // 관리자 확인
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null || !"ADMIN".equals(loginMember.getMemberRole())) {
            result.put("success", false);
            result.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        
        try {
            List<String> files = backupService.listBackupFiles();
            
            result.put("success", true);
            result.put("files", files);
            result.put("count", files.size());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "파일 목록 조회 실패: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 종목 추가 및 자동 백업 API
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @PostMapping("/add-stock")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addStockWithBackup(
            @RequestBody StockVO stock,
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("➕ 종목 추가 및 백업 API");
        
        // 관리자 확인
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null || !"ADMIN".equals(loginMember.getMemberRole())) {
            logger.warn("  ❌ 권한 없음");
            result.put("success", false);
            result.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        
        try {
            String message = backupService.addStockAndBackup(stock);
            
            result.put("success", true);
            result.put("message", message);
            result.put("stock", stock);
            
            logger.info("  ✅ 종목 추가 및 백업 성공");
            
        } catch (Exception e) {
            logger.error("  ❌ 실패: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "종목 추가 실패: " + e.getMessage());
        }
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return ResponseEntity.ok(result);
    }
}
