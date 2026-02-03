package com.travelplatform.infrastructure.storage;

import java.io.InputStream;
import java.util.List;

/**
 * Storage Service interface for file operations.
 * This interface defines the contract for file storage operations,
 * allowing different implementations (Firebase, S3, local, etc.).
 */
public interface StorageService {

    /**
     * Upload a file to storage.
     *
     * @param inputStream The input stream of the file to upload
     * @param fileName The name of the file
     * @param contentType The MIME type of the file
     * @param path The storage path (e.g., "accommodations/images/")
     * @return The public URL of the uploaded file
     * @throws StorageException if upload fails
     */
    String uploadFile(InputStream inputStream, String fileName, String contentType, String path) throws StorageException;

    /**
     * Upload a file to storage with a custom file name.
     *
     * @param inputStream The input stream of the file to upload
     * @param fileName The name of the file
     * @param contentType The MIME type of the file
     * @param path The storage path (e.g., "accommodations/images/")
     * @param customFileName Custom name for the file in storage
     * @return The public URL of the uploaded file
     * @throws StorageException if upload fails
     */
    String uploadFile(InputStream inputStream, String fileName, String contentType, String path, String customFileName) throws StorageException;

    /**
     * Delete a file from storage.
     *
     * @param fileUrl The public URL of the file to delete
     * @throws StorageException if deletion fails
     */
    void deleteFile(String fileUrl) throws StorageException;

    /**
     * Delete multiple files from storage.
     *
     * @param fileUrls List of public URLs of the files to delete
     * @throws StorageException if deletion fails
     */
    void deleteFiles(List<String> fileUrls) throws StorageException;

    /**
     * Get a signed URL for a file (for temporary access).
     *
     * @param fileUrl The public URL of the file
     * @param expirationInSeconds The expiration time in seconds
     * @return The signed URL
     * @throws StorageException if URL generation fails
     */
    String getSignedUrl(String fileUrl, long expirationInSeconds) throws StorageException;

    /**
     * Check if a file exists in storage.
     *
     * @param fileUrl The public URL of the file
     * @return true if the file exists, false otherwise
     */
    boolean fileExists(String fileUrl);

    /**
     * Get the file size in bytes.
     *
     * @param fileUrl The public URL of the file
     * @return The file size in bytes
     * @throws StorageException if operation fails
     */
    long getFileSize(String fileUrl) throws StorageException;

    /**
     * Upload a profile photo.
     *
     * @param inputStream The input stream of the photo
     * @param fileName The name of the file
     * @param contentType The MIME type of the file
     * @param userId The user ID
     * @return The public URL of the uploaded photo
     * @throws StorageException if upload fails
     */
    String uploadProfilePhoto(InputStream inputStream, String fileName, String contentType, String userId) throws StorageException;

    /**
     * Upload an accommodation image.
     *
     * @param inputStream The input stream of the image
     * @param fileName The name of the file
     * @param contentType The MIME type of the file
     * @param accommodationId The accommodation ID
     * @return The public URL of the uploaded image
     * @throws StorageException if upload fails
     */
    String uploadAccommodationImage(InputStream inputStream, String fileName, String contentType, String accommodationId) throws StorageException;

    /**
     * Upload a reel video.
     *
     * @param inputStream The input stream of the video
     * @param fileName The name of the file
     * @param contentType The MIME type of the file
     * @param reelId The reel ID
     * @return The public URL of the uploaded video
     * @throws StorageException if upload fails
     */
    String uploadReelVideo(InputStream inputStream, String fileName, String contentType, String reelId) throws StorageException;

    /**
     * Upload a reel thumbnail.
     *
     * @param inputStream The input stream of the thumbnail
     * @param fileName The name of the file
     * @param contentType The MIME type of the file
     * @param reelId The reel ID
     * @return The public URL of the uploaded thumbnail
     * @throws StorageException if upload fails
     */
    String uploadReelThumbnail(InputStream inputStream, String fileName, String contentType, String reelId) throws StorageException;

    /**
     * Upload a review photo.
     *
     * @param inputStream The input stream of the photo
     * @param fileName The name of the file
     * @param contentType The MIME type of the file
     * @param reviewId The review ID
     * @return The public URL of the uploaded photo
     * @throws StorageException if upload fails
     */
    String uploadReviewPhoto(InputStream inputStream, String fileName, String contentType, String reviewId) throws StorageException;

    /**
     * Upload a chat attachment.
     *
     * @param inputStream The input stream of the attachment
     * @param fileName The name of the file
     * @param contentType The MIME type of the file
     * @param chatGroupId The chat group ID
     * @return The public URL of the uploaded attachment
     * @throws StorageException if upload fails
     */
    String uploadChatAttachment(InputStream inputStream, String fileName, String contentType, String chatGroupId) throws StorageException;

    /**
     * Upload a direct message attachment.
     *
     * @param inputStream The input stream of the attachment
     * @param fileName The name of the file
     * @param contentType The MIME type of the file
     * @param conversationId The conversation ID
     * @return The public URL of the uploaded attachment
     * @throws StorageException if upload fails
     */
    String uploadDirectMessageAttachment(InputStream inputStream, String fileName, String contentType, String conversationId) throws StorageException;

    /**
     * Validate file type.
     *
     * @param fileName The name of the file
     * @param contentType The MIME type of the file
     * @param allowedTypes List of allowed content types
     * @return true if the file type is allowed, false otherwise
     */
    boolean validateFileType(String fileName, String contentType, List<String> allowedTypes);

    /**
     * Validate file size.
     *
     * @param fileSize The file size in bytes
     * @param maxSizeInBytes The maximum allowed file size in bytes
     * @return true if the file size is valid, false otherwise
     */
    boolean validateFileSize(long fileSize, long maxSizeInBytes);

    /**
     * Get the storage path for a resource type.
     *
     * @param resourceType The type of resource (e.g., "profile", "accommodation", "reel")
     * @param resourceId The ID of the resource
     * @return The storage path
     */
    String getStoragePath(String resourceType, String resourceId);
}
