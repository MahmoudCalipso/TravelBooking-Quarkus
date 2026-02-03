package com.travelplatform.infrastructure.storage;

/**
 * Exception thrown when storage operations fail.
 */
public class StorageException extends RuntimeException {

    private final String errorCode;

    public StorageException(String message) {
        super(message);
        this.errorCode = "STORAGE_ERROR";
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "STORAGE_ERROR";
    }

    public StorageException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public StorageException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // Common error codes
    public static final String UPLOAD_FAILED = "UPLOAD_FAILED";
    public static final String DELETE_FAILED = "DELETE_FAILED";
    public static final String FILE_NOT_FOUND = "FILE_NOT_FOUND";
    public static final String INVALID_FILE_TYPE = "INVALID_FILE_TYPE";
    public static final String FILE_TOO_LARGE = "FILE_TOO_LARGE";
    public static final String STORAGE_QUOTA_EXCEEDED = "STORAGE_QUOTA_EXCEEDED";
    public static final String PERMISSION_DENIED = "PERMISSION_DENIED";
    public static final String NETWORK_ERROR = "NETWORK_ERROR";
    public static final String INVALID_URL = "INVALID_URL";
}
