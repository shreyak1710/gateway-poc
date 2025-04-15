
package com.zapcom.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestLoggingFilter implements GatewayFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Generate tracking ID for the request
        String trackingId = UUID.randomUUID().toString();
        
        // Add tracking ID to request headers
        ServerHttpRequest modifiedRequest = request.mutate()
            .header("X-Tracking-Id", trackingId)
            .build();
        
        // Log request details
        logger.info("Request: [{}] {} {}, client: {}, headers: {}", 
            trackingId,
            request.getMethod(), 
            request.getURI(), 
            request.getRemoteAddress(), 
            request.getHeaders());
        
        // Record request start time
        long startTime = System.currentTimeMillis();
        
        return chain.filter(exchange.mutate().request(modifiedRequest).build())
            .doFinally(signalType -> {
                // Log response time on completion
                long duration = System.currentTimeMillis() - startTime;
                logger.info("Response: [{}] completed in {} ms", trackingId, duration);
            });
    }

    @Override
    public int getOrder() {
        // Execute early in the filter chain
        return -100;
    }
}
