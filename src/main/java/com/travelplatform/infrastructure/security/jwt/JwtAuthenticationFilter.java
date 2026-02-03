package com.travelplatform.infrastructure.security.jwt;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.repository.UserRepository;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

/**
 * JWT Authentication Filter for validating JWT tokens and setting security
 * context.
 * This filter intercepts all requests and validates JWT tokens from
 * Authorization header.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    @Inject
    JwtTokenProvider jwtTokenProvider;

    @Inject
    UserRepository userRepository;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String PUBLIC_PATH_PREFIX = "/api/v1/auth";
    private static final String PUBLIC_ACCOMMODATION_PATH = "/api/v1/accommodations";
    private static final String PUBLIC_REEL_PATH = "/api/v1/reels/feed";
    private static final String PUBLIC_SEARCH_PATH = "/api/v1/search";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        // Skip authentication for public endpoints
        if (isPublicEndpoint(path)) {
            return;
        }

        // Extract token from Authorization header
        String token = extractToken(requestContext);

        if (token == null) {
            requestContext.abortWith(
                    ResponseUtil.unauthorizedResponse("Missing or invalid Authorization header"));
            return;
        }

        // Validate token
        if (!jwtTokenProvider.validateToken(token)) {
            requestContext.abortWith(
                    ResponseUtil.unauthorizedResponse("Invalid or expired token"));
            return;
        }

        // Extract user information from token
        UUID userId = jwtTokenProvider.getUserIdFromToken(token);
        if (userId == null) {
            requestContext.abortWith(
                    ResponseUtil.unauthorizedResponse("Invalid token: missing user ID"));
            return;
        }

        // Verify user exists and is active
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            requestContext.abortWith(
                    ResponseUtil.unauthorizedResponse("User not found"));
            return;
        }

        User user = userOptional.get();
        if (user.getStatus() != UserStatus.ACTIVE) {
            requestContext.abortWith(
                    ResponseUtil.forbiddenResponse("User account is not active"));
            return;
        }

        // Set security context
        SecurityContext securityContext = new JwtSecurityContext(user, token, requestContext.getSecurityContext());
        requestContext.setSecurityContext(securityContext);
    }

    /**
     * Extract JWT token from Authorization header.
     *
     * @param requestContext Container request context
     * @return JWT token string or null
     */
    private String extractToken(ContainerRequestContext requestContext) {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * Check if the request path is a public endpoint.
     *
     * @param path Request path
     * @return true if public endpoint, false otherwise
     */
    private boolean isPublicEndpoint(String path) {
        return path.startsWith(PUBLIC_PATH_PREFIX) ||
                path.startsWith(PUBLIC_ACCOMMODATION_PATH) ||
                path.startsWith(PUBLIC_REEL_PATH) ||
                path.startsWith(PUBLIC_SEARCH_PATH) ||
                path.contains("/openapi") ||
                path.contains("/swagger") ||
                path.contains("/health") ||
                path.contains("/metrics");
    }

    /**
     * Custom SecurityContext implementation for JWT authentication.
     */
    private static class JwtSecurityContext implements SecurityContext {
        private final User user;
        private final String token;
        private final SecurityContext originalContext;

        public JwtSecurityContext(User user, String token, SecurityContext originalContext) {
            this.user = user;
            this.token = token;
            this.originalContext = originalContext;
        }

        @Override
        public Principal getUserPrincipal() {
            return new JwtPrincipal(user, token);
        }

        @Override
        public boolean isUserInRole(String role) {
            return user.getRole().name().equals(role);
        }

        @Override
        public boolean isSecure() {
            return originalContext.isSecure();
        }

        @Override
        public String getAuthenticationScheme() {
            return "Bearer";
        }
    }

    /**
     * Custom Principal implementation for JWT authentication.
     */
    private static class JwtPrincipal implements Principal {
        private final User user;
        private final String token;

        public JwtPrincipal(User user, String token) {
            this.user = user;
            this.token = token;
        }

        @Override
        public String getName() {
            return user.getId().toString();
        }

        // @Override removed as getSubject is not in Principal
        public String getSubject() {
            return user.getId().toString();
        }

        public String getRawToken() {
            return token;
        }

        public UUID getUserId() {
            return user.getId();
        }

        public String getEmail() {
            return user.getEmail();
        }

        public UserRole getRole() {
            return user.getRole();
        }

        public UserStatus getStatus() {
            return user.getStatus();
        }
    }

    /**
     * Utility class for creating HTTP responses.
     */
    private static class ResponseUtil {
        static jakarta.ws.rs.core.Response unauthorizedResponse(String message) {
            return jakarta.ws.rs.core.Response
                    .status(jakarta.ws.rs.core.Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("UNAUTHORIZED", message))
                    .build();
        }

        static jakarta.ws.rs.core.Response forbiddenResponse(String message) {
            return jakarta.ws.rs.core.Response
                    .status(jakarta.ws.rs.core.Response.Status.FORBIDDEN)
                    .entity(new ErrorResponse("FORBIDDEN", message))
                    .build();
        }
    }

    /**
     * Error response DTO.
     */
    private static class ErrorResponse {
        private String code;
        private String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
