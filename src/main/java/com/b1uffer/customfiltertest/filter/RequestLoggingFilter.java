package com.b1uffer.customfiltertest.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {
    // OncePerRequestFilter

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Trace ID 발급 (요청 단위 추적)
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId); // MDC

        long start = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();

        // 민감정보 마스킹 예시(아주 단순화)
        String maskedQuery =
                (query == null) ? "" : query.replaceAll("(?i)(password|pwd)=([^&]+)", "$1=****");
        log.info("[REQ][{}] {} {}{}", traceId, method, uri, (maskedQuery.isEmpty() ? "" : "?" + maskedQuery));

        try {
            filterChain.doFilter(request, response);
        } finally {
            long took = System.currentTimeMillis() - start; // 걸린 시간
            int status = response.getStatus();
            log.info("[RES][{}] status={} took={}", traceId, status, took);
            MDC.clear();
        }
    }
}
