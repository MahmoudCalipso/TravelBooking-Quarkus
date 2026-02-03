package com.travelplatform.interfaces.rest.filter;

import com.travelplatform.infrastructure.security.JwtTokenProvider;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Authentication filter for JWT token validation.
 * Validates JWT tokens on protected endpoints.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Inject
    private JwtTokenProvider jwtTokenProvider;

    @Inject
    private SecurityIdentity securityIdentity;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Skip authentication if no Authorization header
        if (authHeader == null || authHeader.isEmpty()) {
            log.debug("No Authorization header found");
            return;
        }

        // Extract token from Authorization header
        String token = extractToken(authHeader);
        if (token == null) {
            log.warn("Invalid Authorization header format");
            requestContext.abortWith(createUnauthorizedResponse("Invalid Authorization header format"));
            return;
        }

        // Validate token
        try {
            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("Invalid or expired token");
                requestContext.abortWith(createUnauthorizedResponse("Invalid or expired token"));
                return;
            }

            // Extract user ID from token and set in security context
            String userId = jwtTokenProvider.getUserIdFromToken(token);
            if (userId != null) {
                log.debug("User authenticated: {}", userId);
                // SecurityIdentity is automatically populated by Quarkus JWT extension
            }

        } catch (Exception e) {
            log.error("Error validating token", e);
            requestContext.abortWith(createUnauthorizedResponse("Authentication failed"));
        }
    }

    /**
     * Extract JWT token from Authorization header.
     *
     * @param authHeader The Authorization header value
     * @return The JWT token or null if invalid format
     */
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Create unauthorized response.
     *
     * @param message The error message
     * @return Response with 401 status
     */
    private Response createUnauthorizedResponse(String message) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("UNAUTHORIZED", message))
                .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"TravelPlatform\"")
                .build();
    }

    /**
     * Error response DTO.
     */
    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}
