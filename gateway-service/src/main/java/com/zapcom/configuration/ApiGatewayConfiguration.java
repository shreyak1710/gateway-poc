
package com.zapcom.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zapcom.filter.JwtAuthFilter;
import com.zapcom.filter.RequestLoggingFilter;
import com.zapcom.filter.ResponseTransformFilter;

@Configuration
public class ApiGatewayConfiguration {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    
    @Autowired
    private RequestLoggingFilter requestLoggingFilter;
    
    @Autowired
    private ResponseTransformFilter responseTransformFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Auth service route
            .route("auth-service", r -> r.path("/api/auth/**")
                .filters(f -> f
                    .rewritePath("/api/auth/(?<segment>.*)", "/auth/${segment}")
                    .filter(requestLoggingFilter)
                    .filter(responseTransformFilter))
                .uri("lb://AUTH-SERVICE"))
            
            // Customer service route (protected with JWT)
            .route("customer-service", r -> r.path("/api/customers/**")
                .filters(f -> f
                    .rewritePath("/api/customers/(?<segment>.*)", "/customers/${segment}")
                    .filter(jwtAuthFilter)
                    .filter(requestLoggingFilter)
                    .filter(responseTransformFilter))
                .uri("lb://CUSTOMER-SERVICE"))
            
            .build();
    }
}
