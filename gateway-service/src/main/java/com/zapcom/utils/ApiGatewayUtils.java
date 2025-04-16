
package com.zapcom.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class ApiGatewayUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;
    
    private JwtParser jwtParser;
    
    private SecretKey getSigningKey() {
        logger.debug("Generating signing key from secret (length: {})", jwtSecret.length());
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    private JwtParser getJwtParser() {
        if (jwtParser == null) {
            logger.debug("Initializing JWT parser");
            jwtParser = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build();
        }
        return jwtParser;
    }

    public Claims validateToken(String token) {
        logger.debug("Validating JWT token");
        Claims claims = getJwtParser()
                .parseClaimsJws(token)
                .getBody();
        
        if (isTokenExpired(claims)) {
            logger.warn("Token is expired. Expiration: {}", claims.getExpiration());
            throw new RuntimeException("JWT token has expired");
        }
        
        logger.debug("Token validation successful. Subject: {}", claims.getSubject());
        return claims;
    }
    
    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
