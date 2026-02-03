package com.travelplatform.infrastructure.config;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * CORS (Cross-Origin Resource Sharing) configuration for the Travel Platform application.
 * This class provides CORS-related configuration and utilities.
 */
@ApplicationScoped
public class CorsConfig {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    @Inject
    @ConfigProperty(name = "quarkus.http.cors.enabled", defaultValue = "true")
    private boolean corsEnabled;

    @Inject
    @ConfigProperty(name = "quarkus.http.cors.origins", defaultValue = "*")
    private String corsOrigins;

    @Inject
    @ConfigProperty(name = "quarkus.http.cors.methods", defaultValue = "GET,POST,PUT,PATCH,DELETE,OPTIONS")
    private String corsMethods;

    @Inject
    @ConfigProperty(name = "quarkus.http.cors.headers", defaultValue = "*")
    private String corsHeaders;

    @Inject
    @ConfigProperty(name = "quarkus.http.cors.exposed-headers", defaultValue = "Authorization,Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers")
    private String corsExposedHeaders;

    @Inject
    @ConfigProperty(name = "quarkus.http.cors.access-control-allow-credentials", defaultValue = "true")
    private boolean corsAllowCredentials;

    @Inject
    @ConfigProperty(name = "quarkus.http.cors.max-age", defaultValue = "3600")
    private int corsMaxAge;

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
     * @return Set of allowed origins
     */
    public Set<String> getAllowedOrigins() {
        Set<String> origins = new HashSet<>();
        
        if ("*".equals(corsOrigins)) {
            origins.add("*");
        } else {
            String[] originArray = corsOrigins.split(",");
            for (String origin : originArray) {
                origins.add(origin.trim());
            }
        }
        
        return origins;
    }

    /**
     * Get allowed CORS methods.
     *
     * @return Set of allowed methods
     */
    public Set<String> getAllowedMethods() {
        Set<String> methods = new HashSet<>();
        String[] methodArray = corsMethods.split(",");
        for (String method : methodArray) {
            methods.add(method.trim().toUpperCase());
        }
        return methods;
    }

    /**
     * Get allowed CORS headers.
     *
     * @return Set of allowed headers
     */
    public Set<String> getAllowedHeaders() {
        Set<String> headers = new HashSet<>();
        
        if ("*".equals(corsHeaders)) {
            headers.add("*");
        } else {
            String[] headerArray = corsHeaders.split(",");
            for (String header : headerArray) {
                headers.add(header.trim());
            }
        }
        
        return headers;
    }

    /**
     * Get exposed CORS headers.
     *
     * @return Set of exposed headers
     */
    public Set<String> getExposedHeaders() {
        Set<String> headers = new HashSet<>();
        String[] headerArray = corsExposedHeaders.split(",");
        for (String header : headerArray) {
            headers.add(header.trim());
        }
        return headers;
    }

    /**
     * Check if credentials are allowed in CORS.
     *
     * @return true if credentials are allowed
     */
    public boolean isAllowCredentials() {
        return corsAllowCredentials;
    }

    /**
     * Get CORS max age in seconds.
     *
     * @return CORS max age
     */
    public int getMaxAge() {
        return corsMaxAge;
    }

    /**
     * Check if an origin is allowed.
     *
     * @param origin The origin to check
     * @return true if origin is allowed
     */
    public boolean isOriginAllowed(String origin) {
        if (origin == null) {
            return false;
        }
        
        Set<String> allowedOrigins = getAllowedOrigins();
        
        // Wildcard allows all origins
        if (allowedOrigins.contains("*")) {
            return true;
        }
        
        // Check if origin is in allowed list
        return allowedOrigins.contains(origin);
    }

    /**
     * Check if a method is allowed.
     *
     * @param method The HTTP method to check
     * @return true if method is allowed
     */
    public boolean isMethodAllowed(String method) {
        if (method == null) {
            return false;
        }
        
        Set<String> allowedMethods = getAllowedMethods();
        return allowedMethods.contains(method.toUpperCase());
    }

    /**
     * Check if a header is allowed.
     *
     * @param header The header to check
     * @return true if header is allowed
     */
    public boolean isHeaderAllowed(String header) {
        if (header == null) {
            return false;
        }
        
        Set<String> allowedHeaders = getAllowedHeaders();
        
        // Wildcard allows all headers
        if (allowedHeaders.contains("*")) {
            return true;
        }
        
        // Check if header is in allowed list (case-insensitive)
        for (String allowedHeader : allowedHeaders) {
            if (allowedHeader.equalsIgnoreCase(header)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Get CORS headers for a preflight request.
     *
     * @param origin The request origin
     * @param method The request method
     * @param requestHeaders The request headers
     * @return Map of CORS headers
     */
    public java.util.Map<String, String> getPreflightHeaders(String origin, String method, String requestHeaders) {
        java.util.Map<String, String> headers = new java.util.HashMap<>();
        
        if (isOriginAllowed(origin)) {
            headers.put("Access-Control-Allow-Origin", origin);
        }
        
        headers.put("Access-Control-Allow-Methods", String.join(", ", getAllowedMethods()));
        headers.put("Access-Control-Allow-Headers", String.join(", ", getAllowedHeaders()));
        headers.put("Access-Control-Expose-Headers", String.join(", ", getExposedHeaders()));
        headers.put("Access-Control-Max-Age", String.valueOf(corsMaxAge));
        
        if (corsAllowCredentials) {
            headers.put("Access-Control-Allow-Credentials", "true");
        }
        
        return headers;
    }

    /**
     * Get CORS headers for a simple request.
     *
     * @param origin The request origin
     * @return Map of CORS headers
     */
    public java.util.Map<String, String> getSimpleHeaders(String origin) {
        java.util.Map<String, String> headers = new java.util.HashMap<>();
        
        if (isOriginAllowed(origin)) {
            headers.put("Access-Control-Allow-Origin", origin);
        }
        
        headers.put("Access-Control-Expose-Headers", String.join(", ", getExposedHeaders()));
        
        if (corsAllowCredentials) {
            headers.put("Access-Control-Allow-Credentials", "true");
        }
        
        return headers;
    }

    /**
     * Validate CORS configuration.
     *
     * @return true if configuration is valid
     */
    public boolean validateConfiguration() {
        boolean isValid = true;
        
        // Check if origins are valid
        Set<String> origins = getAllowedOrigins();
        if (origins.isEmpty()) {
            log.warn("CORS origins list is empty");
            isValid = false;
        }
        
        // Check if methods are valid
        Set<String> methods = getAllowedMethods();
        if (methods.isEmpty()) {
            log.warn("CORS methods list is empty");
            isValid = false;
        }
        
        // Validate HTTP methods
        Set<String> validMethods = new HashSet<>(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
        ));
        
        for (String method : methods) {
            if (!validMethods.contains(method)) {
                log.warn("Invalid CORS method: {}", method);
                isValid = false;
            }
        }
        
        // Check max age
        if (corsMaxAge < 0 || corsMaxAge > 86400) {
            log.warn("CORS max age should be between 0 and 86400 seconds");
            isValid = false;
        }
        
        return isValid;
    }

    /**
     * Get CORS configuration summary.
     *
     * @return Map containing configuration summary
     */
    public java.util.Map<String, Object> getConfigurationSummary() {
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        
        summary.put("enabled", corsEnabled);
        summary.put("origins", getAllowedOrigins());
        summary.put("methods", getAllowedMethods());
        summary.put("headers", getAllowedHeaders());
        summary.put("exposedHeaders", getExposedHeaders());
        summary.put("allowCredentials", corsAllowCredentials);
        summary.put("maxAge", corsMaxAge);
        summary.put("valid", validateConfiguration());
        
        return summary;
    }

    /**
     * Add an origin to the allowed origins list.
     * Note: This is a runtime addition and won't persist.
     *
     * @param origin The origin to add
     */
    public void addAllowedOrigin(String origin) {
        if (origin != null && !origin.isEmpty()) {
            if (!"*".equals(corsOrigins)) {
                if (corsOrigins.isEmpty()) {
                    corsOrigins = origin;
                } else {
                    corsOrigins += "," + origin;
                }
                log.info("Added origin to CORS allowed list: {}", origin);
            }
        }
    }

    /**
     * Remove an origin from the allowed origins list.
     * Note: This is a runtime removal and won't persist.
     *
     * @param origin The origin to remove
     */
    public void removeAllowedOrigin(String origin) {
        if (origin != null && !"*".equals(corsOrigins)) {
            Set<String> origins = getAllowedOrigins();
            if (origins.remove(origin)) {
                corsOrigins = String.join(",", origins);
                log.info("Removed origin from CORS allowed list: {}", origin);
            }
        }
    }

    /**
     * Add a method to the allowed methods list.
     * Note: This is a runtime addition and won't persist.
     *
     * @param method The method to add
     */
    public void addAllowedMethod(String method) {
        if (method != null && !method.isEmpty()) {
            Set<String> methods = getAllowedMethods();
            String upperMethod = method.toUpperCase();
            if (!methods.contains(upperMethod)) {
                methods.add(upperMethod);
                corsMethods = String.join(",", methods);
                log.info("Added method to CORS allowed list: {}", upperMethod);
            }
        }
    }

    /**
     * Remove a method from the allowed methods list.
     * Note: This is a runtime removal and won't persist.
     *
     * @param method The method to remove
     */
    public void removeAllowedMethod(String method) {
        if (method != null) {
            Set<String> methods = getAllowedMethods();
            if (methods.remove(method.toUpperCase())) {
                corsMethods = String.join(",", methods);
                log.info("Removed method from CORS allowed list: {}", method);
            }
        }
    }

    /**
     * Add a header to the allowed headers list.
     * Note: This is a runtime addition and won't persist.
     *
     * @param header The header to add
     */
    public void addAllowedHeader(String header) {
        if (header != null && !header.isEmpty() && !"*".equals(corsHeaders)) {
            Set<String> headers = getAllowedHeaders();
            if (!headers.contains(header)) {
                headers.add(header);
                corsHeaders = String.join(",", headers);
                log.info("Added header to CORS allowed list: {}", header);
            }
        }
    }

    /**
     * Remove a header from the allowed headers list.
     * Note: This is a runtime removal and won't persist.
     *
     * @param header The header to remove
     */
    public void removeAllowedHeader(String header) {
        if (header != null && !"*".equals(corsHeaders)) {
            Set<String> headers = getAllowedHeaders();
            if (headers.remove(header)) {
                corsHeaders = String.join(",", headers);
                log.info("Removed header from CORS allowed list: {}", header);
            }
        }
    }

    /**
     * Reset CORS configuration to defaults.
     */
    public void resetToDefaults() {
        corsOrigins = "*";
        corsMethods = "GET,POST,PUT,PATCH,DELETE,OPTIONS";
        corsHeaders = "*";
        corsExposedHeaders = "Authorization,Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers";
        corsAllowCredentials = true;
        corsMaxAge = 3600;
        
        log.info("CORS configuration reset to defaults");
    }
}
