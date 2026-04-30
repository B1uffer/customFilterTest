package com.b1uffer.customfiltertest.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AuditFilter extends GenericFilter {
    /**
     * GenericFilterBean
     * 감사 로그
     */
    private static final Logger log = LoggerFactory.getLogger(AuditFilter.class);
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest http = (HttpServletRequest) servletRequest;
        String user = (http.getUserPrincipal() != null ? http.getUserPrincipal().getName() : "ANONYMOUS");
        String uri = http.getRequestURI();
        log.debug("[AUDIT] user={} uri={}", user, uri);

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
