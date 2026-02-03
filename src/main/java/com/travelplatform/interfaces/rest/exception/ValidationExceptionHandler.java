package com.travelplatform.interfaces.rest.exception;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Exception handler for validation errors.
 * Provides detailed field-level validation error responses.
 */
@Provider
@ApplicationScoped
public class ValidationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger log = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        log.warn("Validation error: {}", exception.getMessage());

        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        List<FieldError> fieldErrors = violations.stream()
                .map(this::toFieldError)
                .collect(Collectors.toList());

        ValidationErrorResponse response = new ValidationErrorResponse(
                "VALIDATION_ERROR",
                "One or more fields failed validation",
                fieldErrors,
                Instant.now()
        );

        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }

    /**
     * Convert ConstraintViolation to FieldError.
     */
    private FieldError toFieldError(ConstraintViolation<?> violation) {
        String field = violation.getPropertyPath().toString();
        String message = violation.getMessage();
        String invalidValue = violation.getInvalidValue() != null ? violation.getInvalidValue().toString() : null;

        // Extract field name from property path
        if (field.contains(".")) {
            field = field.substring(field.lastIndexOf('.') + 1);
        }

        return new FieldError(field, message, invalidValue);
    }

    /**
     * Validation error response DTO.
     */
    public static class ValidationErrorResponse {
        private String error;
        private String message;
        private List<FieldError> details;
        private Instant timestamp;

        public ValidationErrorResponse(String error, String message, List<FieldError> details, Instant timestamp) {
            this.error = error;
            this.message = message;
            this.details = details;
            this.timestamp = timestamp;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }

        public List<FieldError> getDetails() {
            return details;
        }

        public Instant getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Field error DTO.
     */
    public static class FieldError {
        private String field;
        private String message;
        private String invalidValue;

        public FieldError(String field, String message, String invalidValue) {
            this.field = field;
            this.message = message;
            this.invalidValue = invalidValue;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }

        public String getInvalidValue() {
            return invalidValue;
        }
    }
}
