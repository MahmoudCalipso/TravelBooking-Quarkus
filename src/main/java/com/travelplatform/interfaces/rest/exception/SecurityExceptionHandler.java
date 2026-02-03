package com.travelplatform.interfaces.rest.exception;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

/**
 * Exception handler for security-related exceptions.
 * Handles authentication and authorization errors.
 */
@Provider
@ApplicationScoped
public class SecurityExceptionHandler implements ExceptionMapper<SecurityException> {

    private static final Logger log = LoggerFactory.getLogger(SecurityExceptionHandler.class);

    @Override
    public Response toResponse(SecurityException exception) {
        if (exception instanceof NotAuthorizedException) {
            return handleNotAuthorizedException((NotAuthorizedException) exception);
        }

        if (exception instanceof ForbiddenException) {
            return handleForbiddenException((ForbiddenException) exception);
        }

        // Handle generic security exceptions
        return handleGenericSecurityException(exception);
    }

    /**
     * Handle NotAuthorizedException (401).
     */
    private Response handleNotAuthorizedException(NotAuthorizedException exception) {
        log.warn("Unauthorized access attempt: {}", exception.getMessage());
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse(
                        "UNAUTHORIZED",
                        "Authentication is required to access this resource",
                        Instant.now()
                ))
                .build();
    }

    /**
     * Handle ForbiddenException (403).
     */
    private Response handleForbiddenException(ForbiddenException exception) {
        log.warn("Forbidden access attempt: {}", exception.getMessage());
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorResponse(
                        "FORBIDDEN",
                        "You do not have permission to access this resource",
                        Instant.now()
                ))
                .build();
    }

    /**
     * Handle generic security exceptions.
     */
    private Response handleGenericSecurityException(SecurityException exception) {
        log.error("Security exception occurred", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse(
                        "SECURITY_ERROR",
                        "A security error occurred. Please contact support if the problem persists.",
                        Instant.now()
                ))
                .build();
    }

    /**
     * Error response DTO.
     */
    public static class ErrorResponse {
        private String error;
        private String message;
        private Instant timestamp;

        public ErrorResponse(String error, String message, Instant timestamp) {
            this.error = error;
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }

        public Instant getTimestamp() {
            return timestamp;
        }
    }
}
