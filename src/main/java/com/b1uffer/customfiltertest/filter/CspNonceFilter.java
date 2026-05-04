package com.b1uffer.customfiltertest.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CspNonceFilter extends OncePerRequestFilter {
    private final SecureRandom secureRandom = new SecureRandom();
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        byte[] b = new byte[16];
        secureRandom.nextBytes(b);

        String nonce = Base64.getUrlEncoder().withoutPadding().encodeToString(b);
        request.setAttribute("cspNonce", nonce);
        response.setHeader("Conent-Security-Policy",
                "default-src 'self'; script-src 'self' 'nonce-" + nonce + "' " +
                        "'strict-dynamic'; object-src 'none'; base-uri 'self'; frame-ancestors 'none'");

        filterChain.doFilter(request, response);
    }
}
