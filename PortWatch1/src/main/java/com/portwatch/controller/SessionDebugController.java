package com.portwatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 세션 디버그 컨트롤러
 * 
 * 관심종목 로그인 문제 진단용
 */
@Controller
@RequestMapping("/debug")
public class SessionDebugController {
    
    /**
     * 세션 진단 페이지
     * 
     * GET /debug/session
     */
    @GetMapping("/session")
    public String sessionDebug() {
        return "session-debug";
    }
}
