package com.b1uffer.customfiltertest.config;

import com.b1uffer.customfiltertest.filter.AuditFilter;
import com.b1uffer.customfiltertest.filter.PerformanceMonitoringFilter;
import com.b1uffer.customfiltertest.filter.RequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
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
                .csrf(csrf -> csrf.disable()); // CSRF 비활성화

        return http.build();
    }
}
