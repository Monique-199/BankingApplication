package com.kerubo.BankingApplication.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Marks this class as a Spring component, enabling it to be detected and registered as a filter.
@AllArgsConstructor // Lombok annotation to generate a constructor with all fields as arguments.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtTokenProvider jwtTokenProvider; // Handles JWT generation, validation, and parsing.
    private UserDetailsService userDetailsService; // Service for loading user details from the database or other sources.

    /**
     * Filters each incoming request to check if it contains a valid JWT token.
     *
     * @param request     Incoming HTTP request.
     * @param response    HTTP response.
     * @param filterChain Filter chain to pass the request to the next filter in the chain.
     * @throws ServletException If a servlet-related error occurs.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Extract the JWT token from the Authorization header of the request.
        String token = getTokenFromRequest(request);

        // Validate the token and process authentication if it's valid.
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // Extract the username from the token.
            String username = jwtTokenProvider.getUsername(token);

            // Load user details using the username.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Create an authentication token using the user details and their authorities.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            // Set additional request details in the authentication token.
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set the authentication token in the SecurityContextHolder, making the user authenticated.
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // Continue the filter chain after processing the authentication.
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header of the request.
     *
     * @param request HTTP request.
     * @return The JWT token if present, otherwise null.
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // Retrieve the Authorization header from the request.
        String bearerToken = request.getHeader("Authorization");

        // Check if the header contains a non-empty value and starts with "Bearer".
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Return the token by removing the "Bearer " prefix.
            return bearerToken.substring(7);
        }

        // Return null if the token is not present or invalid.
        return null;
    }
}
