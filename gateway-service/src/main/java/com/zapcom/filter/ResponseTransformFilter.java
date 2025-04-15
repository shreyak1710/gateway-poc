
package com.zapcom.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ResponseTransformFilter implements GatewayFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    // Add common response headers
                    HttpHeaders headers = exchange.getResponse().getHeaders();
                    headers.add("X-Gateway-Version", "1.0.0");
                    
                    // Set security headers
                    headers.add("X-Content-Type-Options", "nosniff");
                    headers.add("X-Frame-Options", "DENY");
                    headers.add("X-XSS-Protection", "1; mode=block");
                    
                    // CORS headers are handled by the global CORS configuration
                }));
    }

    @Override
    public int getOrder() {
        // Execute just before the NettyWriteResponseFilter
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }
}
