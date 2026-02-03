package com.travelplatform.application.dto.response.common;

import java.time.Instant;
import java.util.List;

/**
 * DTO for error response.
 */
public class ErrorResponse {

    private Boolean success;
    private ErrorDetail error;
    private Instant timestamp;

    public ErrorResponse() {
        this.success = false;
        this.timestamp = Instant.now();
    }

    public ErrorResponse(String code, String message) {
        this();
        this.error = new ErrorDetail(code, message);
    }

    public ErrorResponse(String code, String message, List<FieldError> details) {
        this();
        this.error = new ErrorDetail(code, message, details);
    }

    // Getters and Setters

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public ErrorDetail getError() {
        return error;
    }

    public void setError(ErrorDetail error) {
        this.error = error;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Inner class for error details.
     */
    public static class ErrorDetail {
        private String code;
        private String message;
        private List<FieldError> details;

        public ErrorDetail() {
        }

        public ErrorDetail(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public ErrorDetail(String code, String message, List<FieldError> details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }

        // Getters and Setters

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<FieldError> getDetails() {
            return details;
        }

        public void setDetails(List<FieldError> details) {
            this.details = details;
        }
    }

    /**
     * Inner class for field-specific errors.
     */
    public static class FieldError {
        private String field;
        private String message;

        public FieldError() {
        }

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        // Getters and Setters

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
