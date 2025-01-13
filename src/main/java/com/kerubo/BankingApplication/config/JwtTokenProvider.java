package com.kerubo.BankingApplication.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static io.jsonwebtoken.Jwts.*;

@Component
public class JwtTokenProvider {

    // Injecting the secret key and expiration time for the JWT token from application properties
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiry}")
    private Long jwtExpirationDate;

    // Generate JWT token using the provided authentication details
    public String generateToken(Authentication authentication) {
        String userName = authentication.getName();  // Extract the username from the authentication object
        Date currentDate = new Date();  // Current date and time
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);  // Set the expiration time for the token

        // Build and sign the JWT token with the username, issued at time, and expiration date
        return builder()
                .setSubject(userName)  // Set the username as the subject of the token
                .setIssuedAt(currentDate)  // Set the issued at date
                .setExpiration(expireDate)  // Set the expiration date
                .signWith(key())  // Sign the token using the secret key
                .compact();  // Generate the token
    }

    // Decode the JWT secret and create a Key object for signing/verifying
    private Key key() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);  // Decode the base64 encoded secret key
        return Keys.hmacShaKeyFor(keyBytes);  // Return a secret key object for HMAC-SHA signing
    }

    // Extract the username from the JWT token
    public String getUsername(String token) {
        // Parse the token and extract the claims (payload) using the secret key
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())  // Provide the secret key for verification
                .build()               // Build the JWT parser
                .parseClaimsJws(token)  // Parse the token to extract the claims
                .getBody();             // Get the body of the token, which contains the claims

        return claims.getSubject();  // Return the username (subject) from the claims
    }

    // Validate the JWT token to ensure it is not expired or malformed
    public boolean validateToken(String token){
        try {
            // Parse the token with the secret key and verify its validity
            Jwts.parserBuilder()
                    .setSigningKey(key())  // Provide the signing key
                    .build()               // Build the JWT parser
                    .parse(token);         // Parse the token

            return true;  // If parsing succeeds, the token is valid
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            // Catch any exceptions thrown during parsing (e.g., expired token, invalid signature)
            throw new RuntimeException(e);  // Throw a runtime exception if token is invalid
        }
    }
}
