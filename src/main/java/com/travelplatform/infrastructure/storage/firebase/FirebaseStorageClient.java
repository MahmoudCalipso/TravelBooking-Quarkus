package com.travelplatform.infrastructure.storage.firebase;

import java.io.InputStream;
import java.util.Map;

/**
 * Interface for Firebase Storage operations.
 * 
 * This interface defines the contract for interacting with Firebase Storage.
 * It provides methods for uploading, deleting, and generating signed URLs for media files.
 */
public interface FirebaseStorageClient {

    /**
     * Upload a file to Firebase Storage.
     * 
     * @param inputStream The input stream of the file to upload
     * @param destinationPath The destination path in Firebase Storage (e.g., "users/123/profile.jpg")
     * @param contentType The MIME type of the file (e.g., "image/jpeg")
     * @param metadata Additional metadata to attach to the file
     * @return The public URL of the uploaded file
     * @throws StorageException if the upload fails
     */
    String uploadFile(InputStream inputStream, String destinationPath, String contentType, Map<String, String> metadata) throws StorageException;

    /**
     * Upload a file to Firebase Storage with default metadata.
     * 
     * @param inputStream The input stream of the file to upload
     * @param destinationPath The destination path in Firebase Storage
     * @param contentType The MIME type of the file
     * @return The public URL of the uploaded file
     * @throws StorageException if the upload fails
     */
    String uploadFile(InputStream inputStream, String destinationPath, String contentType) throws StorageException;

    /**
     * Delete a file from Firebase Storage.
     * 
     * @param filePath The path of the file to delete
     * @throws StorageException if the deletion fails
     */
    void deleteFile(String filePath) throws StorageException;

    /**
     * Generate a signed URL for a file.
     * Signed URLs allow temporary access to private files.
     * 
     * @param filePath The path of the file
     * @param expirationMinutes The URL expiration time in minutes
     * @return The signed URL
     * @throws StorageException if URL generation fails
     */
    String generateSignedUrl(String filePath, int expirationMinutes) throws StorageException;

    /**
     * Check if a file exists in Firebase Storage.
     * 
     * @param filePath The path of the file to check
     * @return true if the file exists, false otherwise
     */
    boolean fileExists(String filePath);

    /**
     * Get the size of a file in bytes.
     * 
     * @param filePath The path of the file
     * @return The file size in bytes
     * @throws StorageException if the file doesn't exist or cannot be accessed
     */
    long getFileSize(String filePath) throws StorageException;

    /**
     * Copy a file to a new location.
     * 
     * @param sourcePath The source file path
     * @param destinationPath The destination file path
     * @return The public URL of the copied file
     * @throws StorageException if the copy operation fails
     */
    String copyFile(String sourcePath, String destinationPath) throws StorageException;

    /**
     * Move a file to a new location.
     * 
     * @param sourcePath The source file path
     * @param destinationPath The destination file path
     * @return The public URL of the moved file
     * @throws StorageException if the move operation fails
     */
    String moveFile(String sourcePath, String destinationPath) throws StorageException;

    /**
     * Exception thrown when storage operations fail.
     */
    class StorageException extends RuntimeException {
        public StorageException(String message) {
            super(message);
        }

        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
