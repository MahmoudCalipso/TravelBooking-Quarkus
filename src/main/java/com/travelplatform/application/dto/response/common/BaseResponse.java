package com.travelplatform.application.dto.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Instant;

/**
 * Generic base response wrapper for all API responses.
 * Provides a consistent structure across all endpoints.
 * 
 * @param <T> Type of the data payload
 */
@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Instant timestamp;

    /**
     * Default constructor.
     */
    public BaseResponse() {
        this.timestamp = Instant.now();
    }

    /**
     * Constructor for successful response with data.
     * 
     * @param data Response data
     */
    public BaseResponse(T data) {
        this.success = true;
        this.data = data;
        this.timestamp = Instant.now();
    }

    /**
     * Constructor for successful response with data and message.
     * 
     * @param data    Response data
     * @param message Success message
     */
    public BaseResponse(T data, String message) {
        this.success = true;
        this.data = data;
        this.message = message;
        this.timestamp = Instant.now();
    }

    /**
     * Constructor for response with explicit success flag.
     * 
     * @param success Success flag
     * @param message Response message
     * @param data    Response data
     */
    public BaseResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now();
    }

    /**
     * Create a successful response with data.
     * 
     * @param data Response data
     * @param <T>  Type of data
     * @return BaseResponse instance
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(data);
    }

    /**
     * Create a successful response with data and message.
     * 
     * @param data    Response data
     * @param message Success message
     * @param <T>     Type of data
     * @return BaseResponse instance
     */
    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<>(data, message);
    }

    /**
     * Create a successful response with only a message.
     * 
     * @param message Success message
     * @param <T>     Type of data
     * @return BaseResponse instance
     */
    public static <T> BaseResponse<T> success(String message) {
        return new BaseResponse<>(true, message, null);
    }

    /**
     * Create an error response with message.
     * 
     * @param message Error message
     * @param <T>     Type of data
     * @return BaseResponse instance
     */
    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>(false, message, null);
    }

    /**
     * Create an error response with message and data.
     * 
     * @param message Error message
     * @param data    Error details
     * @param <T>     Type of data
     * @return BaseResponse instance
     */
    public static <T> BaseResponse<T> error(String message, T data) {
        return new BaseResponse<>(false, message, data);
    }

    // Getters and Setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
