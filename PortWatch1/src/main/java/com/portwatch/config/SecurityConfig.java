package com.portwatch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * SecurityConfig - Spring Security 설정
 *
 * ✅ 403 오류 수정:
 *   - 루트(/) 및 공개 페이지 permitAll 추가
 *   - 크롤러/어드민 API permitAll 추가
 *   - exchange-rate → exchange/** 패턴 수정
 *   - 미인증 접근 시 403 대신 로그인 페이지로 리다이렉트
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (Ajax JSON POST 요청 사용 중)
            .csrf().disable()

            // URL 접근 권한 설정
            .authorizeRequests()
                // 루트 및 메인 페이지 - 비로그인 접근 허용
                .antMatchers(
                    "/",
                    "/home",
                    "/main"
                ).permitAll()

                // 회원 관련 - 비로그인 접근 허용
                .antMatchers(
                    "/member/login",
                    "/member/signup",
                    "/member/logout",
                    "/member/guest-login",
                    "/member/check-email",
                    "/member/check-id"
                ).permitAll()

                // 정적 리소스 허용
                .antMatchers("/resources/**", "/favicon.ico").permitAll()

                // 주식/뉴스/시장 페이지 - 비로그인 조회 허용
                .antMatchers(
                    "/stock/**",
                    "/news/**"
                ).permitAll()

                // 주식/뉴스/시장/환율 API - 비로그인 조회 허용
                .antMatchers(
                    "/api/stocks/**",
                    "/api/stock/**",
                    "/api/news/**",
                    "/api/market/**",
                    "/api/exchange/**",
                    "/api/exchange/rate",
                    "/api/exchange/convert",
                    "/api/us-stock/**",
                    "/api/stock-price/**"
                ).permitAll()

                // 회원 API - 비로그인 접근 허용 (이메일 중복확인, 인증코드 발송/검증)
                .antMatchers("/api/member/**").permitAll()

                // 크롤러 엔드포인트 - 허용 (수동 트리거용)
                .antMatchers(
                    "/crawler/**",
                    "/api/admin/**"
                ).permitAll()

                // 디버그 엔드포인트 - 허용 (BCrypt 해시 생성, DB 연결 확인 등)
                .antMatchers("/debug/**").permitAll()

                // 나머지 모든 요청: 로그인 필요
                // (포트폴리오, 관심종목, 대시보드, 결제, 마이페이지)
                .anyRequest().authenticated()

            .and()

            // 미인증 접근 시 로그인 페이지로 리다이렉트 (403 대신)
            .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendRedirect(request.getContextPath() + "/member/login"))

            .and()

            // Spring Security 기본 폼 로그인 비활성화
            // (MemberController에서 커스텀 처리)
            .formLogin().disable()

            // HTTP Basic 인증 비활성화
            .httpBasic().disable()

            // 로그아웃 설정
            .logout()
                .logoutUrl("/member/logout")
                .logoutSuccessUrl("/member/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
    }
}
