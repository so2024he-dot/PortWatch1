package com.portwatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 홈 컨트롤러
 * 
 * @author PortWatch
 * @version 1.0
 */
@Controller
public class HomeController {
    
    /**
     * 루트 경로 - 메인 페이지로 리다이렉트
     * http://localhost:8088/
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/main";
    }
    
    /**
     * 메인 페이지
     * http://localhost:8088/main
     */
    @GetMapping("/main")
    public String main() {
        return "main/index";
    }
    
    /**
     * 대시보드 페이지
     * http://localhost:8088/dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard/index";
    }
}
