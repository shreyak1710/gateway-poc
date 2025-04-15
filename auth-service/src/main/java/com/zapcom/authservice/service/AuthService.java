
package com.zapcom.authservice.service;

import com.zapcom.authservice.model.AuthRequest;
import com.zapcom.authservice.model.AuthResponse;
import com.zapcom.authservice.model.RegisterRequest;
import com.zapcom.authservice.model.User;
import com.zapcom.authservice.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private int refreshExpiration;

    public AuthResponse register(RegisterRequest request) {
        // Validate if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create new user
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName()
        );

        userRepository.save(user);

        // Generate tokens
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken();

        // Save refresh token to user
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getUsername(),
                user.getRoles().toArray(new String[0])
        );
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Get user from repository
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Generate tokens
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken();

        // Save refresh token to user
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getUsername(),
                user.getRoles().toArray(new String[0])
        );
    }

    public AuthResponse refreshToken(String refreshToken) {
        // Find user by refresh token
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // Generate new tokens
        String accessToken = generateAccessToken(user);
        String newRefreshToken = generateRefreshToken();

        // Update refresh token
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return new AuthResponse(
                accessToken,
                newRefreshToken,
                user.getId(),
                user.getUsername(),
                user.getRoles().toArray(new String[0])
        );
    }

    private String generateAccessToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(user.getId())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 1000))
                .signWith(key)
                .compact();
    }

    private String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
