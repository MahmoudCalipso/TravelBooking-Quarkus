package com.travelplatform.infrastructure.config;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.JwtAuthenticationFilter;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Security configuration for the Travel Platform application.
 * This class provides security-related configuration and utilities.
 */
@ApplicationScoped
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Inject
    @ConfigProperty(name = "jwt.expiration.time", defaultValue = "86400000")
    private long jwtExpirationTime;

    @Inject
    @ConfigProperty(name = "jwt.refresh.expiration.time", defaultValue = "604800000")
    private long jwtRefreshExpirationTime;

    @Inject
    @ConfigProperty(name = "security.cors.enabled", defaultValue = "true")
    private boolean corsEnabled;

    @Inject
    @ConfigProperty(name = "security.cors.allowed.origins", defaultValue = "*")
    private String corsAllowedOrigins;

    @Inject
    @ConfigProperty(name = "security.cors.allowed.methods", defaultValue = "GET,POST,PUT,PATCH,DELETE,OPTIONS")
    private String corsAllowedMethods;

    @Inject
    @ConfigProperty(name = "security.cors.allowed.headers", defaultValue = "*")
    private String corsAllowedHeaders;

    @Inject
    @ConfigProperty(name = "security.cors.exposed.headers", defaultValue = "Authorization,Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers")
    private String corsExposedHeaders;

    @Inject
    @ConfigProperty(name = "security.cors.allow.credentials", defaultValue = "true")
    private boolean corsAllowCredentials;

    @Inject
    @ConfigProperty(name = "security.cors.max.age", defaultValue = "3600")
    private long corsMaxAge;

    @Inject
    @ConfigProperty(name = "security.rate.limit.enabled", defaultValue = "true")
    private boolean rateLimitEnabled;

    @Inject
    @ConfigProperty(name = "security.rate.limit.requests.per.minute", defaultValue = "60")
    private int rateLimitRequestsPerMinute;

    @Inject
    @ConfigProperty(name = "security.rate.limit.requests.per.hour", defaultValue = "1000")
    private int rateLimitRequestsPerHour;

    @Inject
    @ConfigProperty(name = "security.rate.limit.requests.per.day", defaultValue = "10000")
    private int rateLimitRequestsPerDay;

    @Inject
    @ConfigProperty(name = "security.csrf.enabled", defaultValue = "false")
    private boolean csrfEnabled;

    @Inject
    @ConfigProperty(name = "security.xss.enabled", defaultValue = "true")
    private boolean xssEnabled;

    @Inject
    @ConfigProperty(name = "security.content.security.policy", defaultValue = "default-src 'self'")
    private String contentSecurityPolicy;

    @Inject
    @ConfigProperty(name = "security.frame.options", defaultValue = "DENY")
    private String frameOptions;

    @Inject
    @ConfigProperty(name = "security.x.content.type.options", defaultValue = "nosniff")
    private String xContentTypeOptions;

    @Inject
    @ConfigProperty(name = "security.strict.transport.security", defaultValue = "max-age=31536000; includeSubDomains")
    private String strictTransportSecurity;

    /**
     * Get JWT token expiration time in milliseconds.
     *
     * @return JWT expiration time
     */
    public long getJwtExpirationTime() {
        return jwtExpirationTime;
    }

    /**
     * Get JWT refresh token expiration time in milliseconds.
     *
     * @return JWT refresh expiration time
     */
    public long getJwtRefreshExpirationTime() {
        return jwtRefreshExpirationTime;
    }

    /**
     * Check if CORS is enabled.
     *
     * @return true if CORS is enabled
     */
    public boolean isCorsEnabled() {
        return corsEnabled;
    }

    /**
     * Get allowed CORS origins.
     *
     * @return Array of allowed origins
     */
    public String[] getCorsAllowedOrigins() {
        return corsAllowedOrigins.split(",");
    }

    /**
     * Get allowed CORS methods.
     *
     * @return Array of allowed methods
     */
    public String[] getCorsAllowedMethods() {
        return corsAllowedMethods.split(",");
    }

    /**
     * Get allowed CORS headers.
     *
     * @return Array of allowed headers
     */
    public String[] getCorsAllowedHeaders() {
        return corsAllowedHeaders.split(",");
    }

    /**
     * Get exposed CORS headers.
     *
     * @return Array of exposed headers
     */
    public String[] getCorsExposedHeaders() {
        return corsExposedHeaders.split(",");
    }

    /**
     * Check if credentials are allowed in CORS.
     *
     * @return true if credentials are allowed
     */
    public boolean isCorsAllowCredentials() {
        return corsAllowCredentials;
    }

    /**
     * Get CORS max age in seconds.
     *
     * @return CORS max age
     */
    public long getCorsMaxAge() {
        return corsMaxAge;
    }

    /**
     * Check if rate limiting is enabled.
     *
     * @return true if rate limiting is enabled
     */
    public boolean isRateLimitEnabled() {
        return rateLimitEnabled;
    }

    /**
     * Get rate limit requests per minute.
     *
     * @return Requests per minute limit
     */
    public int getRateLimitRequestsPerMinute() {
        return rateLimitRequestsPerMinute;
    }

    /**
     * Get rate limit requests per hour.
     *
     * @return Requests per hour limit
     */
    public int getRateLimitRequestsPerHour() {
        return rateLimitRequestsPerHour;
    }

    /**
     * Get rate limit requests per day.
     *
     * @return Requests per day limit
     */
    public int getRateLimitRequestsPerDay() {
        return rateLimitRequestsPerDay;
    }

    /**
     * Check if CSRF protection is enabled.
     *
     * @return true if CSRF is enabled
     */
    public boolean isCsrfEnabled() {
        return csrfEnabled;
    }

    /**
     * Check if XSS protection is enabled.
     *
     * @return true if XSS protection is enabled
     */
    public boolean isXssEnabled() {
        return xssEnabled;
    }

    /**
     * Get Content Security Policy.
     *
     * @return CSP header value
     */
    public String getContentSecurityPolicy() {
        return contentSecurityPolicy;
    }

    /**
     * Get X-Frame-Options header value.
     *
     * @return X-Frame-Options header value
     */
    public String getFrameOptions() {
        return frameOptions;
    }

    /**
     * Get X-Content-Type-Options header value.
     *
     * @return X-Content-Type-Options header value
     */
    public String getXContentTypeOptions() {
        return xContentTypeOptions;
    }

    /**
     * Get Strict-Transport-Security header value.
     *
     * @return HSTS header value
     */
    public String getStrictTransportSecurity() {
        return strictTransportSecurity;
    }

    /**
     * Check if a user role has permission to access a resource.
     *
     * @param userRole The user role
     * @param requiredRoles The required roles
     * @return true if user has permission
     */
    public boolean hasPermission(UserRole userRole, Set<UserRole> requiredRoles) {
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return true;
        }
        return requiredRoles.contains(userRole);
    }

    /**
     * Check if a user role has permission to access a resource.
     *
     * @param userRole The user role
     * @param requiredRoles The required roles
     * @return true if user has permission
     */
    public boolean hasPermission(UserRole userRole, UserRole... requiredRoles) {
        if (requiredRoles == null || requiredRoles.length == 0) {
            return true;
        }
        return Arrays.asList(requiredRoles).contains(userRole);
    }

    /**
     * Check if a user is a SUPER_ADMIN.
     *
     * @param userRole The user role
     * @return true if user is SUPER_ADMIN
     */
    public boolean isSuperAdmin(UserRole userRole) {
        return UserRole.SUPER_ADMIN.equals(userRole);
    }

    /**
     * Check if a user is a TRAVELER.
     *
     * @param userRole The user role
     * @return true if user is TRAVELER
     */
    public boolean isTraveler(UserRole userRole) {
        return UserRole.TRAVELER.equals(userRole);
    }

    /**
     * Check if a user is a SUPPLIER_SUBSCRIBER.
     *
     * @param userRole The user role
     * @return true if user is SUPPLIER_SUBSCRIBER
     */
    public boolean isSupplierSubscriber(UserRole userRole) {
        return UserRole.SUPPLIER_SUBSCRIBER.equals(userRole);
    }

    /**
     * Check if a user is an ASSOCIATION_MANAGER.
     *
     * @param userRole The user role
     * @return true if user is ASSOCIATION_MANAGER
     */
    public boolean isAssociationManager(UserRole userRole) {
        return UserRole.ASSOCIATION_MANAGER.equals(userRole);
    }

    /**
     * Check if a user can access admin endpoints.
     *
     * @param userRole The user role
     * @return true if user can access admin endpoints
     */
    public boolean canAccessAdmin(UserRole userRole) {
        return isSuperAdmin(userRole);
    }

    /**
     * Check if a user can access supplier endpoints.
     *
     * @param userRole The user role
     * @return true if user can access supplier endpoints
     */
    public boolean canAccessSupplier(UserRole userRole) {
        return isSupplierSubscriber(userRole) || isSuperAdmin(userRole);
    }

    /**
     * Check if a user can access association endpoints.
     *
     * @param userRole The user role
     * @return true if user can access association endpoints
     */
    public boolean canAccessAssociation(UserRole userRole) {
        return isAssociationManager(userRole) || isSuperAdmin(userRole);
    }

    /**
     * Check if a user can access traveler endpoints.
     *
     * @param userRole The user role
     * @return true if user can access traveler endpoints
     */
    public boolean canAccessTraveler(UserRole userRole) {
        return isTraveler(userRole) || isSupplierSubscriber(userRole) || 
               isAssociationManager(userRole) || isSuperAdmin(userRole);
    }

    /**
     * Check if a user can manage content (approve/reject).
     *
     * @param userRole The user role
     * @return true if user can manage content
     */
    public boolean canManageContent(UserRole userRole) {
        return isSuperAdmin(userRole);
    }

    /**
     * Check if a user can view analytics.
     *
     * @param userRole The user role
     * @return true if user can view analytics
     */
    public boolean canViewAnalytics(UserRole userRole) {
        return isSuperAdmin(userRole) || isSupplierSubscriber(userRole) || isAssociationManager(userRole);
    }

    /**
     * Check if a user can create bookings.
     *
     * @param userRole The user role
     * @return true if user can create bookings
     */
    public boolean canCreateBookings(UserRole userRole) {
        return isTraveler(userRole) || isSupplierSubscriber(userRole) || 
               isAssociationManager(userRole) || isSuperAdmin(userRole);
    }

    /**
     * Check if a user can create accommodations.
     *
     * @param userRole The user role
     * @return true if user can create accommodations
     */
    public boolean canCreateAccommodations(UserRole userRole) {
        return isSupplierSubscriber(userRole) || isSuperAdmin(userRole);
    }

    /**
     * Check if a user can create events.
     *
     * @param userRole The user role
     * @return true if user can create events
     */
    public boolean canCreateEvents(UserRole userRole) {
        return isAssociationManager(userRole) || isSuperAdmin(userRole);
    }

    /**
     * Check if a user can create reels.
     *
     * @param userRole The user role
     * @return true if user can create reels
     */
    public boolean canCreateReels(UserRole userRole) {
        return isTraveler(userRole) || isSupplierSubscriber(userRole) || 
               isAssociationManager(userRole) || isSuperAdmin(userRole);
    }

    /**
     * Check if a user can create reviews.
     *
     * @param userRole The user role
     * @return true if user can create reviews
     */
    public boolean canCreateReviews(UserRole userRole) {
        return isTraveler(userRole) || isSupplierSubscriber(userRole) || 
               isAssociationManager(userRole) || isSuperAdmin(userRole);
    }

    /**
     * Check if a user can send messages.
     *
     * @param userRole The user role
     * @return true if user can send messages
     */
    public boolean canSendMessages(UserRole userRole) {
        return isTraveler(userRole) || isSupplierSubscriber(userRole) || 
               isAssociationManager(userRole) || isSuperAdmin(userRole);
    }

    /**
     * Check if a user can manage users.
     *
     * @param userRole The user role
     * @return true if user can manage users
     */
    public boolean canManageUsers(UserRole userRole) {
        return isSuperAdmin(userRole);
    }

    /**
     * Check if a user can manage subscriptions.
     *
     * @param userRole The user role
     * @return true if user can manage subscriptions
     */
    public boolean canManageSubscriptions(UserRole userRole) {
        return isSuperAdmin(userRole);
    }

    /**
     * Get all roles that can access a specific resource type.
     *
     * @param resourceType The resource type
     * @return Set of allowed roles
     */
    public Set<UserRole> getAllowedRolesForResource(String resourceType) {
        Set<UserRole> allowedRoles = new HashSet<>();
        
        switch (resourceType.toLowerCase()) {
            case "admin":
                allowedRoles.add(UserRole.SUPER_ADMIN);
                break;
            case "supplier":
                allowedRoles.add(UserRole.SUPPLIER_SUBSCRIBER);
                allowedRoles.add(UserRole.SUPER_ADMIN);
                break;
            case "association":
                allowedRoles.add(UserRole.ASSOCIATION_MANAGER);
                allowedRoles.add(UserRole.SUPER_ADMIN);
                break;
            case "traveler":
                allowedRoles.add(UserRole.TRAVELER);
                allowedRoles.add(UserRole.SUPPLIER_SUBSCRIBER);
                allowedRoles.add(UserRole.ASSOCIATION_MANAGER);
                allowedRoles.add(UserRole.SUPER_ADMIN);
                break;
            case "public":
                // No roles required
                break;
            default:
                log.warn("Unknown resource type: {}", resourceType);
        }
        
        return allowedRoles;
    }

    /**
     * Check if a path is public (no authentication required).
     *
     * @param path The request path
     * @return true if path is public
     */
    public boolean isPublicPath(String path) {
        if (path == null) {
            return false;
        }
        
        String lowerPath = path.toLowerCase();
        
        // Public endpoints
        return lowerPath.startsWith("/api/v1/auth/register") ||
               lowerPath.startsWith("/api/v1/auth/login") ||
               lowerPath.startsWith("/api/v1/accommodations") && !lowerPath.contains("/create") ||
               lowerPath.startsWith("/api/v1/reels/feed") ||
               lowerPath.startsWith("/api/v1/reels/trending") ||
               lowerPath.startsWith("/api/v1/reels/") && lowerPath.contains("/location/") ||
               lowerPath.startsWith("/api/v1/events") && !lowerPath.contains("/create") ||
               lowerPath.startsWith("/api/v1/programs") && !lowerPath.contains("/create") ||
               lowerPath.startsWith("/api/v1/search/") ||
               lowerPath.startsWith("/q/") || // Quarkus dev endpoints
               lowerPath.startsWith("/swagger") ||
               lowerPath.startsWith("/openapi");
    }

    /**
     * Check if a path requires admin role.
     *
     * @param path The request path
     * @return true if path requires admin role
     */
    public boolean isAdminPath(String path) {
        if (path == null) {
            return false;
        }
        
        String lowerPath = path.toLowerCase();
        return lowerPath.startsWith("/api/v1/admin/");
    }

    /**
     * Check if a path requires supplier role.
     *
     * @param path The request path
     * @return true if path requires supplier role
     */
    public boolean isSupplierPath(String path) {
        if (path == null) {
            return false;
        }
        
        String lowerPath = path.toLowerCase();
        return lowerPath.startsWith("/api/v1/supplier/");
    }

    /**
     * Get security headers for HTTP responses.
     *
     * @return Map of security headers
     */
    public java.util.Map<String, String> getSecurityHeaders() {
        java.util.Map<String, String> headers = new java.util.HashMap<>();
        
        headers.put("X-Content-Type-Options", xContentTypeOptions);
        headers.put("X-Frame-Options", frameOptions);
        headers.put("Content-Security-Policy", contentSecurityPolicy);
        headers.put("Strict-Transport-Security", strictTransportSecurity);
        headers.put("X-XSS-Protection", "1; mode=block");
        headers.put("Referrer-Policy", "strict-origin-when-cross-origin");
        headers.put("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
        
        return headers;
    }

    /**
     * Validate a security token.
     *
     * @param token The token to validate
     * @return true if token is valid
     */
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        // Basic validation - actual validation is done by JwtAuthenticationFilter
        return token.startsWith("Bearer ") && token.length() > 7;
    }

    /**
     * Extract token from Authorization header.
     *
     * @param authHeader The Authorization header
     * @return The token or null if invalid
     */
    public String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    /**
     * Create a security identity for a user.
     *
     * @param userId The user ID
     * @param email The user email
     * @param role The user role
     * @return The security identity
     */
    public SecurityIdentity createSecurityIdentity(String userId, String email, UserRole role) {
        QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder();
        
        builder.setPrincipal(() -> userId);
        builder.addCredential(new org.eclipse.microprofile.jwt.JsonWebToken() {
            @Override
            public String getName() {
                return userId;
            }

            @Override
            public java.util.Set<String> getClaimNames() {
                return java.util.Set.of("sub", "email", "role");
            }

            @Override
            public Object getClaim(String claimName) {
                switch (claimName) {
                    case "sub":
                        return userId;
                    case "email":
                        return email;
                    case "role":
                        return role.name();
                    default:
                        return null;
                }
            }

            @Override
            public java.util.Set<String> getGroups() {
                return java.util.Set.of(role.name());
            }

            @Override
            public boolean isUserInRole(String role) {
                return role.equals(this.getClaim("role"));
            }
        });
        
        return builder.build();
    }
}
