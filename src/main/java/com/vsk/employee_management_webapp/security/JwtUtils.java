package com.vsk.employee_management_webapp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys; // Import Keys
import io.jsonwebtoken.security.SignatureException; // Import SignatureException
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {

    @Value("${employee_app.jwtSecret}")
    private String jwtSecret;

    @Value("${employee_app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private Key key; // Store the key after decoding/generating

    // Constructor or PostConstruct method to initialize the key securely
    public JwtUtils(@Value("${employee_app.jwtSecret}") String jwtSecret) {
        // Option 1: Decode from base64 string provided in properties (ensure it's long enough)
        // This requires you to generate a sufficiently long base64 string beforehand.
        // For HS512, it must be at least 64 bytes (512 bits)
        // Example: Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded())
        try {
            this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
            log.info("JWT secret key initialized from application.properties.");
        } catch (IllegalArgumentException e) {
            // This catches the error if the base64 string is too short/invalid
            log.error("Provided JWT secret is not a valid base64 string or is too short for HS512. Generating a new one for development.", e);
            // Fallback for development if the provided key is bad.
            // In production, this should be a critical error or rely on external secret management.
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            log.warn("Generated a new HS512 secret key dynamically. FOR DEVELOPMENT ONLY. Ensure 'employee_app.jwtSecret' is a strong, base64-encoded 512-bit key in production.");
            // You might want to log this generated key for a one-time use to put into properties
            // System.out.println("New generated key (Base64 encoded): " + Base64.getEncoder().encodeToString(key.getEncoded()));
        }

    }


    // Use the stored 'key' directly
    private Key getSigningKey() {
        return key;
    }

    // Generate token from authentication object
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        log.debug("Generating JWT token for user: {}", userPrincipal.getUsername());

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // Extract username from token
    public String getUserNameFromJwtToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Validate token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            log.debug("JWT token is valid.");
            return true;
        } catch (SignatureException e) { // Use specific SignatureException
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    // Extract a specific claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}