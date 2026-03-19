package com.portwatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * SecurityConfig - Spring Security 설정
 *
 * ✅ 해결하는 오류:
 *   web.xml에 springSecurityFilterChain 필터가 등록되어 있으나
 *   대응 빈이 없어 NoSuchBeanDefinitionException 발생
 *   → 모든 요청이 500 에러 또는 차단됨
 *
 * ✅ 설정 내용:
 *   - 로그인/회원가입 페이지: 인증 없이 접근 허용
 *   - 정적 리소스(/resources/**): 허용
 *   - API 엔드포인트(/api/**): 허용 (주식, 뉴스 데이터 조회)
 *   - 나머지: 로그인 필요
 *   - CSRF: 비활성화 (Ajax POST 사용 중)
 *   - Spring Security 기본 폼 로그인: 비활성화 (커스텀 로그인 사용)
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
                // 공개 접근 허용 - 회원가입/로그인
                .antMatchers(
                    "/member/login",
                    "/member/signup",
                    "/member/logout",
                    "/member/check-email",
                    "/member/check-id"
                ).permitAll()

                // 정적 리소스 허용
                .antMatchers("/resources/**", "/favicon.ico").permitAll()

                // 주식/뉴스 API - 비로그인 조회 허용 (필요 시 변경)
                .antMatchers(
                    "/api/stocks/**",
                    "/api/news/**",
                    "/api/market/**",
                    "/api/exchange-rate/**"
                ).permitAll()

                // 나머지 모든 요청: 로그인 필요
                .anyRequest().authenticated()

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

    /**
     * BCryptPasswordEncoder 빈 등록
     * root-context.xml에도 등록되어 있으므로 중복 방지를 위해
     * root-context.xml에서 제거하거나 여기서만 사용
     *
     * ※ root-context.xml의 passwordEncoder 빈과 충돌 시:
     *    root-context.xml에서 해당 빈을 제거하세요.
     */
    // root-context.xml에 이미 BCryptPasswordEncoder 빈이 있으므로
    // 여기서는 정의하지 않음 (중복 빈 방지)
}
