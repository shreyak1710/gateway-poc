
package com.zapcom.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zapcom.filter.JwtAuthFilter;
import com.zapcom.filter.RequestLoggingFilter;
import com.zapcom.filter.ResponseTransformFilter;
import com.zapcom.utils.Constants;

@Configuration
public class ApiGatewayConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayConfiguration.class);

    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    
    @Autowired
    private RequestLoggingFilter requestLoggingFilter;
    
    @Autowired
    private ResponseTransformFilter responseTransformFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        logger.info("Configuring API Gateway routes with static service discovery");
        
        return builder.routes()
            // Auth service route
            .route("auth-service", r -> {
                logger.info("Configuring auth-service route for path: {}", Constants.AUTH_PATH + "/**");
                return r.path(Constants.AUTH_PATH + "/**")
                    .filters(f -> f
                        .rewritePath(Constants.AUTH_PATH + "/(?<segment>.*)", "/auth/${segment}")
                        .filter(requestLoggingFilter)
                        .filter(responseTransformFilter))
                    .uri("http://localhost:8081");
            })
            
            // Customer service route (protected with JWT)
            .route("customer-service", r -> {
                logger.info("Configuring customer-service route for path: {}", Constants.CUSTOMER_PATH + "/**");
                return r.path(Constants.CUSTOMER_PATH + "/**")
                    .filters(f -> f
                        .rewritePath(Constants.CUSTOMER_PATH + "/(?<segment>.*)", "/customers/${segment}")
                        .filter(jwtAuthFilter)
                        .filter(requestLoggingFilter)
                        .filter(responseTransformFilter))
                    .uri("http://localhost:8082");
            })
            
            .build();
    }
}
