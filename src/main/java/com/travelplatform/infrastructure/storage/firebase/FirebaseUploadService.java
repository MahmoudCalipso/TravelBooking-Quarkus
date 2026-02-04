package com.travelplatform.infrastructure.storage.firebase;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of FirebaseStorageClient using Firebase Cloud Storage.
 * 
 * This service handles all Firebase Storage operations including:
 * - Uploading files
 * - Deleting files
 * - Generating signed URLs
 * - File existence checks
 * - Copying and moving files
 */
@ApplicationScoped
public class FirebaseUploadService implements FirebaseStorageClient {

    private static final Logger log = LoggerFactory.getLogger(FirebaseUploadService.class);

    @Inject
    StorageClient storageClient;

    @Inject
    Storage storage;

    @Inject
    FirebaseConfig firebaseConfig;

    @Override
    public String uploadFile(InputStream inputStream, String destinationPath, String contentType, Map<String, String> metadata) throws StorageException {
        try {
            log.info("Uploading file to Firebase Storage: {}", destinationPath);

            // Build BlobInfo with metadata
            BlobInfo.Builder blobInfoBuilder = BlobInfo.newBuilder(
                    BlobId.of(firebaseConfig.getStorageBucket(), destinationPath)
            )
                    .setContentType(contentType);

            // Add custom metadata if provided
            if (metadata != null && !metadata.isEmpty()) {
                blobInfoBuilder.setMetadata(metadata);
            }

            BlobInfo blobInfo = blobInfoBuilder.build();

            // Upload the file
            Blob blob = storage.createFrom(blobInfo, inputStream);

            // Get the public URL
            String publicUrl = String.format(
                    "https://storage.googleapis.com/%s/%s",
                    firebaseConfig.getStorageBucket(),
                    destinationPath
            );

            log.info("File uploaded successfully: {} (Size: {} bytes)", destinationPath, blob.getSize());
            return publicUrl;

        } catch (Exception e) {
            log.error("Failed to upload file to Firebase Storage: {}", destinationPath, e);
            throw new StorageException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadFile(InputStream inputStream, String destinationPath, String contentType) throws StorageException {
        return uploadFile(inputStream, destinationPath, contentType, null);
    }

    @Override
    public void deleteFile(String filePath) throws StorageException {
        try {
            log.info("Deleting file from Firebase Storage: {}", filePath);

            BlobId blobId = BlobId.of(firebaseConfig.getStorageBucket(), filePath);
            boolean deleted = storage.delete(blobId);

            if (deleted) {
                log.info("File deleted successfully: {}", filePath);
            } else {
                log.warn("File not found for deletion: {}", filePath);
            }

        } catch (Exception e) {
            log.error("Failed to delete file from Firebase Storage: {}", filePath, e);
            throw new StorageException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateSignedUrl(String filePath, int expirationMinutes) throws StorageException {
        try {
            log.info("Generating signed URL for file: {} (expires in {} minutes)", filePath, expirationMinutes);

            BlobId blobId = BlobId.of(firebaseConfig.getStorageBucket(), filePath);
            Blob blob = storage.get(blobId);

            if (blob == null) {
                throw new StorageException("File not found: " + filePath);
            }

            // Generate signed URL with expiration
            URL signedUrl = blob.signUrl(expirationMinutes, TimeUnit.MINUTES);

            log.info("Signed URL generated successfully for: {}", filePath);
            return signedUrl.toString();

        } catch (Exception e) {
            log.error("Failed to generate signed URL for file: {}", filePath, e);
            throw new StorageException("Failed to generate signed URL: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean fileExists(String filePath) {
        try {
            BlobId blobId = BlobId.of(firebaseConfig.getStorageBucket(), filePath);
            Blob blob = storage.get(blobId);
            return blob != null && blob.exists();

        } catch (Exception e) {
            log.error("Error checking file existence: {}", filePath, e);
            return false;
        }
    }

    @Override
    public long getFileSize(String filePath) throws StorageException {
        try {
            BlobId blobId = BlobId.of(firebaseConfig.getStorageBucket(), filePath);
            Blob blob = storage.get(blobId);

            if (blob == null || !blob.exists()) {
                throw new StorageException("File not found: " + filePath);
            }

            return blob.getSize();

        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get file size: {}", filePath, e);
            throw new StorageException("Failed to get file size: " + e.getMessage(), e);
        }
    }

    @Override
    public String copyFile(String sourcePath, String destinationPath) throws StorageException {
        try {
            log.info("Copying file from {} to {}", sourcePath, destinationPath);

            BlobId sourceBlobId = BlobId.of(firebaseConfig.getStorageBucket(), sourcePath);
            Blob sourceBlob = storage.get(sourceBlobId);

            if (sourceBlob == null || !sourceBlob.exists()) {
                throw new StorageException("Source file not found: " + sourcePath);
            }

            // Copy the blob
            BlobId destBlobId = BlobId.of(firebaseConfig.getStorageBucket(), destinationPath);
            Blob copiedBlob = storage.copy(
                    Storage.CopyRequest.newBuilder()
                            .setSource(sourceBlobId)
                            .setTarget(destBlobId)
                            .build()
            ).getResult();

            // Get the public URL
            String publicUrl = String.format(
                    "https://storage.googleapis.com/%s/%s",
                    firebaseConfig.getStorageBucket(),
                    destinationPath
            );

            log.info("File copied successfully: {} -> {}", sourcePath, destinationPath);
            return publicUrl;

        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to copy file: {} -> {}", sourcePath, destinationPath, e);
            throw new StorageException("Failed to copy file: " + e.getMessage(), e);
        }
    }

    @Override
    public String moveFile(String sourcePath, String destinationPath) throws StorageException {
        try {
            // First copy the file
            String publicUrl = copyFile(sourcePath, destinationPath);

            // Then delete the source file
            deleteFile(sourcePath);

            log.info("File moved successfully: {} -> {}", sourcePath, destinationPath);
            return publicUrl;

        } catch (Exception e) {
            log.error("Failed to move file: {} -> {}", sourcePath, destinationPath, e);
            throw new StorageException("Failed to move file: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a unique Firebase storage path for a media asset.
     * 
     * @param ownerType The type of owner (USER, ACCOMMODATION, etc.)
     * @param ownerId The ID of the owner
     * @param mediaType The type of media (IMAGE, VIDEO, etc.)
     * @param fileName The original file name
     * @return A unique storage path (e.g., "users/123/images/abc123.jpg")
     */
    public String generateStoragePath(String ownerType, String ownerId, String mediaType, String fileName) {
        // Extract file extension
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = fileName.substring(dotIndex);
        }

        // Generate unique filename using timestamp and random string
        String uniqueName = System.currentTimeMillis() + "_" + 
                          Integer.toHexString((int) (Math.random() * 0xFFFFFF));

        // Build path: {ownerType}/{ownerId}/{mediaType}/{uniqueName}.{extension}
        return String.format("%s/%s/%s/%s%s",
                ownerType.toLowerCase(),
                ownerId,
                mediaType.toLowerCase(),
                uniqueName,
                extension);
    }

    /**
     * Validate file size against maximum allowed size.
     * 
     * @param fileSize The file size in bytes
     * @param mediaType The type of media
     * @throws StorageException if file size exceeds limit
     */
    public void validateFileSize(long fileSize, String mediaType) throws StorageException {
        long maxSizeBytes;

        // Define maximum file sizes (in bytes)
        switch (mediaType.toUpperCase()) {
            case "IMAGE":
                maxSizeBytes = 10 * 1024 * 1024; // 10 MB
                break;
            case "VIDEO":
                maxSizeBytes = 100 * 1024 * 1024; // 100 MB
                break;
            case "AUDIO":
                maxSizeBytes = 20 * 1024 * 1024; // 20 MB
                break;
            case "DOCUMENT":
                maxSizeBytes = 25 * 1024 * 1024; // 25 MB
                break;
            default:
                maxSizeBytes = 10 * 1024 * 1024; // Default 10 MB
        }

        if (fileSize > maxSizeBytes) {
            String maxSizeMB = String.format("%.2f", maxSizeBytes / (1024.0 * 1024.0));
            throw new StorageException(
                    String.format("File size exceeds maximum allowed size of %s MB for %s", maxSizeMB, mediaType)
            );
        }
    }

    /**
     * Validate content type for media type.
     * 
     * @param contentType The MIME type
     * @param mediaType The expected media type
     * @throws StorageException if content type is invalid
     */
    public void validateContentType(String contentType, String mediaType) throws StorageException {
        if (contentType == null || contentType.isEmpty()) {
            throw new StorageException("Content type cannot be empty");
        }

        boolean isValid = false;

        switch (mediaType.toUpperCase()) {
            case "IMAGE":
                isValid = contentType.startsWith("image/");
                break;
            case "VIDEO":
                isValid = contentType.startsWith("video/");
                break;
            case "AUDIO":
                isValid = contentType.startsWith("audio/");
                break;
            case "DOCUMENT":
                isValid = contentType.equals("application/pdf") ||
                           contentType.equals("application/msword") ||
                           contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                           contentType.equals("application/vnd.ms-excel") ||
                           contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                           contentType.equals("text/plain");
                break;
        }

        if (!isValid) {
            throw new StorageException(
                    String.format("Invalid content type '%s' for media type '%s'", contentType, mediaType)
            );
        }
    }
}
