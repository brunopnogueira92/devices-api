package com.devices.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * API Key authentication filter.
 * Validates X-API-Key header for all /api/** endpoints.
 * Enabled only when api.key.enabled=true (disabled by default for testing).
 */
@Component
@ConditionalOnProperty(name = "api.key.enabled", havingValue = "true")
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${api.key:devices-api-key-2025-secure}")
    private String validApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // Only validate API key for /api/** endpoints
        if (requestPath.startsWith("/api/")) {
            String apiKey = request.getHeader("X-API-Key");
            
            if (apiKey == null || !apiKey.equals(validApiKey)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized: Missing or invalid API Key\"}");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
