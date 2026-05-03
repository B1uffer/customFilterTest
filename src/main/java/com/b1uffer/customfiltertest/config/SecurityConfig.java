package com.b1uffer.customfiltertest.config;

import com.b1uffer.customfiltertest.filter.AuditFilter;
import com.b1uffer.customfiltertest.filter.PerformanceMonitoringFilter;
import com.b1uffer.customfiltertest.filter.RequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
public class SecurityConfig {
    /**
     * 필요시 다중 FilterChain을 구현해서 URL별로 다른 필터 조합을 구성할 수 있다
     */
    @Bean
    public SecurityFilterChain defaultChain(HttpSecurity http) throws Exception {
        http
                // (1) 커스텀 필터 체인에 삽입하기(앞, 뒤, 동일위치 선택 가능)
                .addFilterBefore(new RequestLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new AuditFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new PerformanceMonitoringFilter(), UsernamePasswordAuthenticationFilter.class)

                // (2) 간단한 인증 정책 (데모)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/secure/**").authenticated()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()) // 편의를 위해 HTTP Basic 활성화
                .csrf(csrf -> csrf.disable() // CSRF 비활성화
//                        .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**") // 특정 경로 예외 처리하기
                )

                // XSS
                .headers(headers -> headers
                        // X-Content-Type-Options: nosniff, 전역 nosniff 적용
                        .contentTypeOptions(Customizer.withDefaults())

                        // contentSecurityPolicy(CSP)
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; " +
                                "script-src 'self' 'nonce-{{nonce}}' 'strict-dinamic; " + // {{nonce}}는 요청마다 서버가 생성해서 넣는 동적 nonce값임
                                "object-src 'none'; base-url 'self'; frame-ancestors 'none'"))
                        // .xssProtection(x -> x.block(true)) 는 레거시 : 최신 브라우저 대다수 무시
                )
        ;
        return http.build();
    }

    /**
     * 혹시나 postman 테스트에 문제가 있다면, 밑에 있는 메서드 2개를 주석처리 할 것
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("user")
                        .password(encoder.encode("password")).roles("USER").build(),
                User.withUsername("admin")
                        .password(encoder.encode("password")).roles("ADMIN").build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setParameterName("_csrf");
        repository.setHeaderName("X-CSRF-TOKEN");
        return repository;
    }
}
