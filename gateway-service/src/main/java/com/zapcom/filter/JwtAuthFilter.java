
package com.zapcom.filter;

import com.zapcom.exception.ApiGatewayException;
import com.zapcom.utils.ApiGatewayUtils;
import com.zapcom.utils.Constants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements GatewayFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private ApiGatewayUtils utils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        
        logger.debug("Processing request to path: {}", path);
        
        // Check if this path should be excluded from authentication
        if (shouldSkipAuth(path)) {
            logger.debug("Skipping authentication for path: {}", path);
            return chain.filter(exchange);
        }
        
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            logger.error("Missing authorization header for path: {}", path);
            throw new ApiGatewayException("Missing authorization header", "UNAUTHORIZED");
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("Invalid authorization header format: {}", authHeader);
            throw new ApiGatewayException("Invalid authorization header format", "UNAUTHORIZED");
        }

        String token = authHeader.substring(7);
        logger.debug("Extracted token: {}", token);
        
        try {
            Claims claims = utils.validateToken(token);
            logger.debug("Token validated successfully. Subject: {}, Role: {}", 
                claims.getSubject(), claims.get("role", String.class));
            
            // Add user details to headers for downstream services
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header(Constants.USER_ID_HEADER, claims.getSubject())
                .header(Constants.USER_ROLE_HEADER, 
                    claims.get("role", String.class) != null ? 
                    claims.get("role", String.class) : "USER")
                .build();
            
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token expired: {}", e.getMessage());
            throw new ApiGatewayException("Token has expired", "UNAUTHORIZED", e);
        } catch (MalformedJwtException e) {
            logger.error("JWT token is malformed: {}", e.getMessage());
            throw new ApiGatewayException("Malformed token", "UNAUTHORIZED", e);
        } catch (SignatureException e) {
            logger.error("JWT signature validation failed: {}", e.getMessage());
            throw new ApiGatewayException("Invalid token signature", "UNAUTHORIZED", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
            throw new ApiGatewayException("Unsupported token format", "UNAUTHORIZED", e);
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage(), e);
            throw new ApiGatewayException("Invalid or expired token", "UNAUTHORIZED", e);
        }
    }
    
    private boolean shouldSkipAuth(String path) {
        List<String> openApiEndpoints = List.of(
            "/api/auth/login", 
            "/api/auth/register", 
            "/api/auth/token/refresh"
        );
        
        return openApiEndpoints.stream().anyMatch(path::startsWith);
    }
}
