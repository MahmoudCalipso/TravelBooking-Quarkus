package com.travelplatform.interfaces.rest.filter;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Logging filter for HTTP requests and responses.
 * Logs request details, response details, and execution time.
 */
@Provider
@Priority(Priorities.USER)
@ApplicationScoped
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    private static final String REQUEST_ID = "requestId";
    private static final String START_TIME = "startTime";
    private static final String CLIENT_IP = "clientIp";
    private static final String USER_AGENT = "userAgent";
    private static final String METHOD = "method";
    private static final String PATH = "path";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Generate unique request ID
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID, requestId);

        // Store request start time
        MDC.put(START_TIME, Instant.now().toString());

        // Extract request details
        String clientIp = getClientIp(requestContext);
        String userAgent = requestContext.getHeaderString("User-Agent");
        String method = requestContext.getMethod();
        UriInfo uriInfo = requestContext.getUriInfo();
        String path = uriInfo.getPath();
        String queryString = uriInfo.getRequestUri().getQuery();

        // Store in MDC for response filter
        MDC.put(CLIENT_IP, clientIp);
        MDC.put(USER_AGENT, userAgent != null ? userAgent : "unknown");
        MDC.put(METHOD, method);
        MDC.put(PATH, path + (queryString != null ? "?" + queryString : ""));

        // Log incoming request
        log.info("Incoming request: {} {} from {} - Request ID: {}", method, path, clientIp, requestId);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // Calculate execution time
        String startTimeStr = MDC.get(START_TIME);
        long executionTime = 0;
        if (startTimeStr != null) {
            Instant startTime = Instant.parse(startTimeStr);
            executionTime = Duration.between(startTime, Instant.now()).toMillis();
        }

        // Extract response details
        int status = responseContext.getStatus();
        String method = MDC.get(METHOD);
        String path = MDC.get(PATH);
        String clientIp = MDC.get(CLIENT_IP);
        String requestId = MDC.get(REQUEST_ID);

        // Log response
        if (status >= 500) {
            log.error("Response: {} {} - Status: {} - Time: {}ms - Client: {} - Request ID: {}", 
                    method, path, status, executionTime, clientIp, requestId);
        } else if (status >= 400) {
            log.warn("Response: {} {} - Status: {} - Time: {}ms - Client: {} - Request ID: {}", 
                    method, path, status, executionTime, clientIp, requestId);
        } else {
            log.info("Response: {} {} - Status: {} - Time: {}ms - Client: {} - Request ID: {}", 
                    method, path, status, executionTime, clientIp, requestId);
        }

        // Clear MDC
        MDC.clear();
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
}
