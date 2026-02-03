package com.travelplatform.interfaces.rest.exception;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Global exception handler for all unhandled exceptions.
 * Provides consistent error responses across the application.
 */
@Provider
@ApplicationScoped
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Exception exception) {
        ErrorResponse errorResponse;

        if (exception instanceof EntityNotFoundException) {
            errorResponse = handleEntityNotFoundException((EntityNotFoundException) exception);
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        }

        if (exception instanceof OptimisticLockException) {
            errorResponse = handleOptimisticLockException((OptimisticLockException) exception);
            return Response.status(Response.Status.CONFLICT).entity(errorResponse).build();
        }

        if (exception instanceof ConstraintViolationException) {
            errorResponse = handleConstraintViolationException((ConstraintViolationException) exception);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

        if (exception instanceof IllegalArgumentException) {
            errorResponse = handleIllegalArgumentException((IllegalArgumentException) exception);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

        if (exception instanceof IllegalStateException) {
            errorResponse = handleIllegalStateException((IllegalStateException) exception);
            return Response.status(Response.Status.CONFLICT).entity(errorResponse).build();
        }

        // Handle all other exceptions as internal server error
        errorResponse = handleGenericException(exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
    }

    /**
     * Handle EntityNotFoundException.
     */
    private ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {
        log.warn("Entity not found: {}", exception.getMessage());
        return new ErrorResponse(
                "NOT_FOUND",
                exception.getMessage(),
                Instant.now()
        );
    }

    /**
     * Handle OptimisticLockException.
     */
    private ErrorResponse handleOptimisticLockException(OptimisticLockException exception) {
        log.warn("Optimistic lock exception: {}", exception.getMessage());
        return new ErrorResponse(
                "CONFLICT",
                "The resource was modified by another user. Please refresh and try again.",
                Instant.now()
        );
    }

    /**
     * Handle ConstraintViolationException.
     */
    private ErrorResponse handleConstraintViolationException(ConstraintViolationException exception) {
        log.warn("Constraint violation: {}", exception.getMessage());
        return new ErrorResponse(
                "VALIDATION_ERROR",
                exception.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", ")),
                Instant.now()
        );
    }

    /**
     * Handle IllegalArgumentException.
     */
    private ErrorResponse handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn("Illegal argument: {}", exception.getMessage());
        return new ErrorResponse(
                "BAD_REQUEST",
                exception.getMessage(),
                Instant.now()
        );
    }

    /**
     * Handle IllegalStateException.
     */
    private ErrorResponse handleIllegalStateException(IllegalStateException exception) {
        log.warn("Illegal state: {}", exception.getMessage());
        return new ErrorResponse(
                "CONFLICT",
                exception.getMessage(),
                Instant.now()
        );
    }

    /**
     * Handle generic exceptions.
     */
    private ErrorResponse handleGenericException(Exception exception) {
        log.error("Unexpected error occurred", exception);
        return new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.",
                Instant.now()
        );
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
