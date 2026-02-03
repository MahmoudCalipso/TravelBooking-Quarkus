package com.travelplatform.application.dto.response.common;

import java.time.Instant;

/**
 * DTO for success response.
 */
public class SuccessResponse<T> {

    private Boolean success;
    private T data;
    private String message;
    private Instant timestamp;

    public SuccessResponse() {
        this.success = true;
        this.timestamp = Instant.now();
    }

    public SuccessResponse(T data, String message) {
        this();
        this.data = data;
        this.message = message;
    }

    public SuccessResponse(T data) {
        this(data, null);
    }

    public SuccessResponse(String message) {
        this(null, message);
    }

    // Getters and Setters

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
