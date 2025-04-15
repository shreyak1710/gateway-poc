
package com.zapcom.filter;

import com.zapcom.exception.ApiGatewayException;
import com.zapcom.utils.ApiGatewayUtils;
import com.zapcom.utils.Constants;

import io.jsonwebtoken.Claims;
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

    @Autowired
    private ApiGatewayUtils utils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Check if this path should be excluded from authentication
        if (shouldSkipAuth(request.getPath().toString())) {
            return chain.filter(exchange);
        }
        
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new ApiGatewayException("Missing authorization header", "UNAUTHORIZED");
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ApiGatewayException("Invalid authorization header format", "UNAUTHORIZED");
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = utils.validateToken(token);
            
            // Add user details to headers for downstream services
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header(Constants.USER_ID_HEADER, claims.getSubject())
                .header(Constants.USER_ROLE_HEADER, claims.get("role", String.class))
                .build();
            
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
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
