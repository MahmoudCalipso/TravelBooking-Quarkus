package com.travelplatform.interfaces.rest.filter;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.annotation.Priority;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiting filter to prevent abuse and DDoS attacks.
 * Implements sliding window rate limiting per IP address.
 */
@Provider
@Priority(Priorities.AUTHORIZATION)
@ApplicationScoped
public class RateLimitFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    // Rate limit configuration
    private static final int AUTH_REQUESTS_PER_15_MINUTES = 5;
    private static final int FILE_UPLOADS_PER_HOUR = 10;
    private static final int API_REQUESTS_PER_HOUR = 1000;
    private static final int COMMENTS_PER_HOUR = 20;

    private static final Duration AUTH_WINDOW = Duration.ofMinutes(15);
    private static final Duration FILE_UPLOAD_WINDOW = Duration.ofHours(1);
    private static final Duration API_WINDOW = Duration.ofHours(1);
    private static final Duration COMMENT_WINDOW = Duration.ofHours(1);

    @Inject
    RedisDataSource redisDataSource;

    @Inject
    @ConfigProperty(name = "rate-limit.use-redis", defaultValue = "false")
    boolean useRedis;

    @Inject
    @ConfigProperty(name = "rate-limit.redis-prefix", defaultValue = "rate:limit")
    String redisPrefix;

    private ValueCommands<String, Long> redisCounters;
    private KeyCommands<String> redisKeys;

    // In-memory rate limit tracking (in production, use Redis)
    private final Map<String, RateLimitEntry> authRateLimits = new ConcurrentHashMap<>();
    private final Map<String, RateLimitEntry> fileUploadRateLimits = new ConcurrentHashMap<>();
    private final Map<String, RateLimitEntry> apiRateLimits = new ConcurrentHashMap<>();
    private final Map<String, RateLimitEntry> commentRateLimits = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        if (redisDataSource != null) {
            redisCounters = redisDataSource.value(Long.class);
            redisKeys = redisDataSource.key();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String clientIp = getClientIp(requestContext);
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        log.debug("Rate limit check for IP: {}, Path: {}, Method: {}", clientIp, path, method);

        // Check rate limits based on endpoint type
        if (isAuthEndpoint(path)) {
            checkRateLimit(clientIp, authRateLimits, AUTH_REQUESTS_PER_15_MINUTES, AUTH_WINDOW, 
                    "Too many authentication attempts. Please try again later.", "auth");
        } else if (isFileUploadEndpoint(path)) {
            checkRateLimit(clientIp, fileUploadRateLimits, FILE_UPLOADS_PER_HOUR, FILE_UPLOAD_WINDOW,
                    "Too many file uploads. Please try again later.", "upload");
        } else if (isCommentEndpoint(path)) {
            checkRateLimit(clientIp, commentRateLimits, COMMENTS_PER_HOUR, COMMENT_WINDOW,
                    "Too many comments. Please slow down.", "comment");
        } else {
            checkRateLimit(clientIp, apiRateLimits, API_REQUESTS_PER_HOUR, API_WINDOW,
                    "Too many requests. Please try again later.", "api");
        }
    }

    /**
     * Check if the request exceeds the rate limit.
     *
     * @param clientIp The client IP address
     * @param rateLimitMap The rate limit map to use
     * @param maxRequests Maximum allowed requests
     * @param window The time window
     * @param errorMessage The error message to return
     */
    private void checkRateLimit(String clientIp, Map<String, RateLimitEntry> rateLimitMap,
                                 int maxRequests, Duration window, String errorMessage, String bucket) {
        if (useRedis && redisCounters != null) {
            applyRedisRateLimit(clientIp, rateLimitMap, maxRequests, window, errorMessage, bucket);
            return;
        }

        RateLimitEntry entry = rateLimitMap.computeIfAbsent(clientIp, k -> new RateLimitEntry());

        Instant now = Instant.now();
        entry.cleanupOldEntries(now, window);

        if (entry.getCount() >= maxRequests) {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            throw new RateLimitExceededException(errorMessage);
        }

        entry.increment();
        log.debug("Rate limit count for IP {}: {}/{}", clientIp, entry.getCount(), maxRequests);
    }

    /**
     * Get client IP address from request.
     *
     * @param requestContext The request context
     * @return The client IP address
     */
    private String getClientIp(ContainerRequestContext requestContext) {
        String xForwardedFor = requestContext.getHeaderString("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Get the first IP in the chain
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = requestContext.getHeaderString("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return requestContext.getUriInfo().getRequestUri().getHost();
    }

    /**
     * Check if the endpoint is an authentication endpoint.
     *
     * @param path The request path
     * @return True if authentication endpoint
     */
    private boolean isAuthEndpoint(String path) {
        return path.contains("/auth/login") || 
               path.contains("/auth/register") || 
               path.contains("/auth/forgot-password") ||
               path.contains("/auth/reset-password");
    }

    /**
     * Check if the endpoint is a file upload endpoint.
     *
     * @param path The request path
     * @return True if file upload endpoint
     */
    private boolean isFileUploadEndpoint(String path) {
        return path.contains("/upload") || 
               path.contains("/images") || 
               path.contains("/videos");
    }

    /**
     * Check if the endpoint is a comment endpoint.
     *
     * @param path The request path
     * @return True if comment endpoint
     */
    private boolean isCommentEndpoint(String path) {
        return path.contains("/comments");
    }

    /**
     * Rate limit entry tracking requests within a time window.
     */
    private static class RateLimitEntry {
        private final AtomicInteger count = new AtomicInteger(0);
        private Instant windowStart = Instant.now();

        public int getCount() {
            return count.get();
        }

        public void increment() {
            count.incrementAndGet();
        }

        /**
         * Clean up old entries outside the time window.
         *
         * @param now The current time
         * @param window The time window
         */
        public void cleanupOldEntries(Instant now, Duration window) {
            if (Duration.between(windowStart, now).compareTo(window) > 0) {
                // Reset the window
                count.set(0);
                windowStart = now;
            }
        }
    }

    private void applyRedisRateLimit(String clientIp, Map<String, RateLimitEntry> fallbackMap,
                                     int maxRequests, Duration window, String errorMessage, String bucket) {
        try {
            String key = String.format("%s:%s:%s", redisPrefix, bucket, clientIp);
            long current = redisCounters.incr(key);
            if (current == 1) {
                redisKeys.expire(key, window);
            }
            if (current > maxRequests) {
                log.warn("Rate limit exceeded (redis) for IP: {}, bucket: {}", clientIp, bucket);
                throw new RateLimitExceededException(errorMessage);
            }
            log.debug("Redis rate limit for IP {} bucket {}: {}/{}", clientIp, bucket, current, maxRequests);
        } catch (Exception e) {
            log.error("Redis rate limit check failed, falling back to in-memory for {}", clientIp, e);
            useRedis = false;
            checkRateLimit(clientIp, fallbackMap, maxRequests, window, errorMessage, bucket);
        }
    }

    /**
     * Custom exception for rate limit exceeded.
     */
    public static class RateLimitExceededException extends RuntimeException {
        public RateLimitExceededException(String message) {
            super(message);
        }
    }

    /**
     * Exception mapper for rate limit exceeded.
     */
    @Provider
    public static class RateLimitExceptionMapper implements jakarta.ws.rs.ext.ExceptionMapper<RateLimitExceededException> {
        @Override
        public Response toResponse(RateLimitExceededException exception) {
            return Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .entity(new ErrorResponse("RATE_LIMIT_EXCEEDED", exception.getMessage()))
                    .header("X-RateLimit-Limit", "1000")
                    .header("X-RateLimit-Remaining", "0")
                    .header("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + 3600000))
                    .build();
        }
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
