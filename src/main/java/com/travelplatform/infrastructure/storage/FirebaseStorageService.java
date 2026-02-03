package com.travelplatform.infrastructure.storage;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Firebase Storage Service implementation.
 * Provides file storage operations using Google Cloud Storage (Firebase Storage).
 */
@ApplicationScoped
public class FirebaseStorageService implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseStorageService.class);

    @Inject
    @ConfigProperty(name = "firebase.storage.bucket.name", defaultValue = "travel-platform-storage")
    String bucketName;

    @Inject
    @ConfigProperty(name = "firebase.storage.project.id", defaultValue = "travel-platform")
    String projectId;

    @Inject
    @ConfigProperty(name = "firebase.storage.base.url", defaultValue = "https://storage.googleapis.com")
    String baseUrl;

    @Inject
    @ConfigProperty(name = "storage.max.file.size", defaultValue = "104857600") // 100MB
    long maxFileSize;

    @Inject
    @ConfigProperty(name = "storage.signed.url.expiration", defaultValue = "3600") // 1 hour
    long signedUrlExpiration;

    private Storage storage;

    /**
     * Get or create Storage instance.
     */
    private Storage getStorage() {
        if (storage == null) {
            storage = StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .build()
                    .getService();
        }
        return storage;
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileName, String contentType, String path) throws StorageException {
        return uploadFile(inputStream, fileName, contentType, path, null);
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileName, String contentType, String path, String customFileName) throws StorageException {
        try {
            // Validate file type
            if (!validateFileType(fileName, contentType, getAllowedContentTypes())) {
                throw new StorageException(StorageException.INVALID_FILE_TYPE, 
                        "File type not allowed: " + contentType);
            }

            // Read input stream to bytes for size validation
            byte[] bytes = inputStream.readAllBytes();

            // Validate file size
            if (!validateFileSize(bytes.length, maxFileSize)) {
                throw new StorageException(StorageException.FILE_TOO_LARGE, 
                        "File size exceeds maximum allowed size of " + (maxFileSize / 1024 / 1024) + "MB");
            }

            // Generate file name
            String finalFileName = customFileName != null ? customFileName : generateUniqueFileName(fileName);
            String objectName = path + finalFileName;

            // Create blob info
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                    .setContentType(contentType)
                    .build();

            // Upload file
            Blob blob = getStorage().createFrom(blobInfo, bytes);

            if (blob == null) {
                throw new StorageException(StorageException.UPLOAD_FAILED, "Failed to upload file: " + fileName);
            }

            // Return public URL
            return getPublicUrl(objectName);

        } catch (IOException e) {
            log.error("Error reading input stream for file: {}", fileName, e);
            throw new StorageException(StorageException.UPLOAD_FAILED, "Failed to read file: " + fileName, e);
        } catch (Exception e) {
            log.error("Error uploading file: {}", fileName, e);
            throw new StorageException(StorageException.UPLOAD_FAILED, "Failed to upload file: " + fileName, e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) throws StorageException {
        try {
            String objectName = extractObjectNameFromUrl(fileUrl);
            if (objectName == null) {
                throw new StorageException(StorageException.INVALID_URL, "Invalid file URL: " + fileUrl);
            }

            boolean deleted = getStorage().delete(bucketName, objectName);

            if (!deleted) {
                throw new StorageException(StorageException.FILE_NOT_FOUND, "File not found: " + fileUrl);
            }

            log.info("Successfully deleted file: {}", fileUrl);

        } catch (Exception e) {
            log.error("Error deleting file: {}", fileUrl, e);
            throw new StorageException(StorageException.DELETE_FAILED, "Failed to delete file: " + fileUrl, e);
        }
    }

    @Override
    public void deleteFiles(List<String> fileUrls) throws StorageException {
        for (String fileUrl : fileUrls) {
            try {
                deleteFile(fileUrl);
            } catch (StorageException e) {
                log.warn("Failed to delete file: {}, continuing with others", fileUrl, e);
            }
        }
    }

    @Override
    public String getSignedUrl(String fileUrl, long expirationInSeconds) throws StorageException {
        try {
            String objectName = extractObjectNameFromUrl(fileUrl);
            if (objectName == null) {
                throw new StorageException(StorageException.INVALID_URL, "Invalid file URL: " + fileUrl);
            }

            BlobId blobId = BlobId.of(bucketName, objectName);
            URL url = getStorage().signUrl(blobId, expirationInSeconds, TimeUnit.SECONDS);

            return url.toString();

        } catch (Exception e) {
            log.error("Error generating signed URL for: {}", fileUrl, e);
            throw new StorageException(StorageException.NETWORK_ERROR, "Failed to generate signed URL", e);
        }
    }

    @Override
    public boolean fileExists(String fileUrl) {
        try {
            String objectName = extractObjectNameFromUrl(fileUrl);
            if (objectName == null) {
                return false;
            }

            Blob blob = getStorage().get(bucketName, objectName);
            return blob != null && blob.exists();

        } catch (Exception e) {
            log.error("Error checking if file exists: {}", fileUrl, e);
            return false;
        }
    }

    @Override
    public long getFileSize(String fileUrl) throws StorageException {
        try {
            String objectName = extractObjectNameFromUrl(fileUrl);
            if (objectName == null) {
                throw new StorageException(StorageException.INVALID_URL, "Invalid file URL: " + fileUrl);
            }

            Blob blob = getStorage().get(bucketName, objectName);
            if (blob == null || !blob.exists()) {
                throw new StorageException(StorageException.FILE_NOT_FOUND, "File not found: " + fileUrl);
            }

            return blob.getSize();

        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting file size for: {}", fileUrl, e);
            throw new StorageException(StorageException.NETWORK_ERROR, "Failed to get file size", e);
        }
    }

    @Override
    public String uploadProfilePhoto(InputStream inputStream, String fileName, String contentType, String userId) throws StorageException {
        return uploadFile(inputStream, fileName, contentType, "profiles/" + userId + "/");
    }

    @Override
    public String uploadAccommodationImage(InputStream inputStream, String fileName, String contentType, String accommodationId) throws StorageException {
        return uploadFile(inputStream, fileName, contentType, "accommodations/" + accommodationId + "/");
    }

    @Override
    public String uploadReelVideo(InputStream inputStream, String fileName, String contentType, String reelId) throws StorageException {
        // Validate video content type
        List<String> videoTypes = List.of("video/mp4", "video/quicktime", "video/x-msvideo", "video/webm");
        if (!validateFileType(fileName, contentType, videoTypes)) {
            throw new StorageException(StorageException.INVALID_FILE_TYPE, 
                    "Video file type not allowed: " + contentType);
        }

        // Validate video size (max 100MB for reels)
        long maxVideoSize = 100 * 1024 * 1024; // 100MB
        try {
            byte[] bytes = inputStream.readAllBytes();
            if (!validateFileSize(bytes.length, maxVideoSize)) {
                throw new StorageException(StorageException.FILE_TOO_LARGE, 
                        "Video size exceeds maximum allowed size of 100MB");
            }
            return uploadFile(new ByteArrayInputStream(bytes), fileName, contentType, "reels/" + reelId + "/");
        } catch (IOException e) {
            throw new StorageException(StorageException.UPLOAD_FAILED, "Failed to read video file", e);
        }
    }

    @Override
    public String uploadReelThumbnail(InputStream inputStream, String fileName, String contentType, String reelId) throws StorageException {
        return uploadFile(inputStream, fileName, contentType, "reels/" + reelId + "/thumbnails/");
    }

    @Override
    public String uploadReviewPhoto(InputStream inputStream, String fileName, String contentType, String reviewId) throws StorageException {
        return uploadFile(inputStream, fileName, contentType, "reviews/" + reviewId + "/");
    }

    @Override
    public String uploadChatAttachment(InputStream inputStream, String fileName, String contentType, String chatGroupId) throws StorageException {
        return uploadFile(inputStream, fileName, contentType, "chat/" + chatGroupId + "/");
    }

    @Override
    public String uploadDirectMessageAttachment(InputStream inputStream, String fileName, String contentType, String conversationId) throws StorageException {
        return uploadFile(inputStream, fileName, contentType, "messages/" + conversationId + "/");
    }

    @Override
    public boolean validateFileType(String fileName, String contentType, List<String> allowedTypes) {
        if (contentType == null || allowedTypes == null) {
            return false;
        }

        // Check content type
        boolean typeAllowed = allowedTypes.stream()
                .anyMatch(allowed -> contentType.toLowerCase().startsWith(allowed.toLowerCase()));

        if (!typeAllowed) {
            return false;
        }

        // Check file extension
        String extension = getFileExtension(fileName);
        if (extension == null) {
            return false;
        }

        // Map common extensions to content types
        return isExtensionValidForContentType(extension, contentType);
    }

    @Override
    public boolean validateFileSize(long fileSize, long maxSizeInBytes) {
        return fileSize > 0 && fileSize <= maxSizeInBytes;
    }

    @Override
    public String getStoragePath(String resourceType, String resourceId) {
        return switch (resourceType.toLowerCase()) {
            case "profile" -> "profiles/" + resourceId + "/";
            case "accommodation" -> "accommodations/" + resourceId + "/";
            case "reel" -> "reels/" + resourceId + "/";
            case "review" -> "reviews/" + resourceId + "/";
            case "chat" -> "chat/" + resourceId + "/";
            case "message" -> "messages/" + resourceId + "/";
            default -> "uploads/" + resourceId + "/";
        };
    }

    /**
     * Get all allowed content types.
     */
    private List<String> getAllowedContentTypes() {
        return List.of(
                // Images
                "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp",
                // Videos
                "video/mp4", "video/quicktime", "video/x-msvideo", "video/webm",
                // Documents
                "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                // Files
                "text/plain"
        );
    }

    /**
     * Generate a unique file name.
     */
    private String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String baseName = originalFileName != null ? 
                originalFileName.substring(0, originalFileName.lastIndexOf('.')) : "file";
        
        // Remove special characters from base name
        baseName = baseName.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        // Generate UUID and combine with base name
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return baseName + "_" + uuid + (extension != null ? "." + extension : "");
    }

    /**
     * Get file extension from file name.
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return null;
        }

        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * Check if file extension is valid for content type.
     */
    private boolean isExtensionValidForContentType(String extension, String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> extension.equals("jpg") || extension.equals("jpeg");
            case "image/png" -> extension.equals("png");
            case "image/gif" -> extension.equals("gif");
            case "image/webp" -> extension.equals("webp");
            case "video/mp4" -> extension.equals("mp4");
            case "video/quicktime" -> extension.equals("mov");
            case "video/x-msvideo" -> extension.equals("avi");
            case "video/webm" -> extension.equals("webm");
            case "application/pdf" -> extension.equals("pdf");
            case "application/msword" -> extension.equals("doc");
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> extension.equals("docx");
            case "text/plain" -> extension.equals("txt");
            default -> false;
        };
    }

    /**
     * Extract object name from public URL.
     */
    private String extractObjectNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        try {
            // Expected URL format: https://storage.googleapis.com/bucket-name/object-name
            String pattern = baseUrl + "/" + bucketName + "/";
            if (fileUrl.startsWith(pattern)) {
                return fileUrl.substring(pattern.length());
            }

            // Try alternative format
            int bucketIndex = fileUrl.indexOf("/" + bucketName + "/");
            if (bucketIndex != -1) {
                return fileUrl.substring(bucketIndex + bucketName.length() + 2);
            }

            return null;

        } catch (Exception e) {
            log.error("Error extracting object name from URL: {}", fileUrl, e);
            return null;
        }
    }

    /**
     * Get public URL for an object.
     */
    private String getPublicUrl(String objectName) {
        return baseUrl + "/" + bucketName + "/" + objectName;
    }

    /**
     * Generate MD5 hash of a string.
     */
    private String generateMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating MD5 hash", e);
            return UUID.randomUUID().toString();
        }
    }
}
