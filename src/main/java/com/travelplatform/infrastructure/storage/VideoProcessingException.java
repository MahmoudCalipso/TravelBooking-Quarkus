package com.travelplatform.infrastructure.storage;

/**
 * Exception thrown when video processing operations fail.
 */
public class VideoProcessingException extends RuntimeException {

    private final String errorCode;

    public VideoProcessingException(String message) {
        super(message);
        this.errorCode = "VIDEO_PROCESSING_ERROR";
    }

    public VideoProcessingException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "VIDEO_PROCESSING_ERROR";
    }

    public VideoProcessingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public VideoProcessingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // Common error codes
    public static final String INVALID_VIDEO_FORMAT = "INVALID_VIDEO_FORMAT";
    public static final String VIDEO_TOO_LARGE = "VIDEO_TOO_LARGE";
    public static final String VIDEO_TOO_SHORT = "VIDEO_TOO_SHORT";
    public static final String VIDEO_TOO_LONG = "VIDEO_TOO_LONG";
    public static final String THUMBNAIL_GENERATION_FAILED = "THUMBNAIL_GENERATION_FAILED";
    public static final String COMPRESSION_FAILED = "COMPRESSION_FAILED";
    public static final String METADATA_EXTRACTION_FAILED = "METADATA_EXTRACTION_FAILED";
    public static final String UNSUPPORTED_CODEC = "UNSUPPORTED_CODEC";
    public static final String CORRUPTED_FILE = "CORRUPTED_FILE";
}
