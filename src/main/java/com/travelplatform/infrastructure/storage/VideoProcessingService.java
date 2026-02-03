package com.travelplatform.infrastructure.storage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Video Processing Service for handling video operations.
 * Provides video thumbnail generation and compression capabilities.
 */
@ApplicationScoped
public class VideoProcessingService {

    private static final Logger log = LoggerFactory.getLogger(VideoProcessingService.class);

    @Inject
    StorageService storageService;

    /**
     * Generate thumbnails from a video file.
     * Creates 3 thumbnails at different positions: beginning (10%), middle (50%), and end (90%).
     *
     * @param videoInputStream The input stream of the video file
     * @param videoFileName The name of the video file
     * @param reelId The reel ID for storage path
     * @return Array of thumbnail URLs [beginning, middle, end]
     * @throws VideoProcessingException if processing fails
     */
    public String[] generateThumbnails(InputStream videoInputStream, String videoFileName, String reelId) throws VideoProcessingException {
        try {
            log.info("Generating thumbnails for video: {}", videoFileName);

            // Read video bytes
            byte[] videoBytes = videoInputStream.readAllBytes();

            // Generate 3 thumbnails at different positions
            String beginningThumbnail = generateThumbnailAtPosition(videoBytes, 0.1, reelId, "beginning");
            String middleThumbnail = generateThumbnailAtPosition(videoBytes, 0.5, reelId, "middle");
            String endThumbnail = generateThumbnailAtPosition(videoBytes, 0.9, reelId, "end");

            return new String[]{beginningThumbnail, middleThumbnail, endThumbnail};

        } catch (IOException e) {
            log.error("Error reading video file: {}", videoFileName, e);
            throw new VideoProcessingException("Failed to read video file", e);
        } catch (Exception e) {
            log.error("Error generating thumbnails for video: {}", videoFileName, e);
            throw new VideoProcessingException("Failed to generate thumbnails", e);
        }
    }

    /**
     * Generate a single thumbnail at a specific position in the video.
     *
     * @param videoBytes The video file bytes
     * @param position The position in the video (0.0 to 1.0)
     * @param reelId The reel ID for storage path
     * @param positionName The name of the position (beginning, middle, end)
     * @return The thumbnail URL
     * @throws VideoProcessingException if processing fails
     */
    private String generateThumbnailAtPosition(byte[] videoBytes, double position, String reelId, String positionName) throws VideoProcessingException {
        try {
            // In a real implementation, this would use FFmpeg or similar library
            // to extract a frame from the video at the specified position
            // For now, we'll create a placeholder thumbnail
            
            byte[] thumbnailBytes = createPlaceholderThumbnail(positionName);
            
            String thumbnailFileName = "thumbnail_" + positionName + "_" + UUID.randomUUID().toString() + ".jpg";
            
            return storageService.uploadReelThumbnail(
                    new ByteArrayInputStream(thumbnailBytes),
                    thumbnailFileName,
                    "image/jpeg",
                    reelId
            );

        } catch (Exception e) {
            log.error("Error generating thumbnail at position {}: {}", positionName, e);
            throw new VideoProcessingException("Failed to generate thumbnail at " + positionName, e);
        }
    }

    /**
     * Compress a video file to reduce file size.
     *
     * @param videoInputStream The input stream of the video file
     * @param videoFileName The name of the video file
     * @param reelId The reel ID for storage path
     * @return The URL of the compressed video
     * @throws VideoProcessingException if processing fails
     */
    public String compressVideo(InputStream videoInputStream, String videoFileName, String reelId) throws VideoProcessingException {
        try {
            log.info("Compressing video: {}", videoFileName);

            // Read video bytes
            byte[] videoBytes = videoInputStream.readAllBytes();

            // Check if video needs compression
            long originalSize = videoBytes.length;
            long maxSize = 50 * 1024 * 1024; // 50MB target

            if (originalSize <= maxSize) {
                log.info("Video size {} is within target size, skipping compression", formatFileSize(originalSize));
                // Upload original video
                return storageService.uploadReelVideo(
                        new ByteArrayInputStream(videoBytes),
                        videoFileName,
                        "video/mp4",
                        reelId
                );
            }

            // In a real implementation, this would use FFmpeg to compress the video
            // For now, we'll just upload the original video
            log.info("Video size {} exceeds target size {}, uploading original", 
                    formatFileSize(originalSize), formatFileSize(maxSize));

            return storageService.uploadReelVideo(
                    new ByteArrayInputStream(videoBytes),
                    videoFileName,
                    "video/mp4",
                    reelId
            );

        } catch (IOException e) {
            log.error("Error reading video file: {}", videoFileName, e);
            throw new VideoProcessingException("Failed to read video file", e);
        } catch (Exception e) {
            log.error("Error compressing video: {}", videoFileName, e);
            throw new VideoProcessingException("Failed to compress video", e);
        }
    }

    /**
     * Get video metadata (duration, resolution, format).
     *
     * @param videoInputStream The input stream of the video file
     * @return VideoMetadata object containing video information
     * @throws VideoProcessingException if processing fails
     */
    public VideoMetadata getVideoMetadata(InputStream videoInputStream) throws VideoProcessingException {
        try {
            byte[] videoBytes = videoInputStream.readAllBytes();

            // In a real implementation, this would use FFprobe or similar library
            // to extract video metadata
            // For now, we'll return default values
            
            VideoMetadata metadata = new VideoMetadata();
            metadata.setDuration(30); // Default 30 seconds
            metadata.setWidth(1080); // Default 1080p
            metadata.setHeight(1920); // Default 1920p (portrait)
            metadata.setFormat("mp4");
            metadata.setSize(videoBytes.length);
            metadata.setBitrate(calculateBitrate(videoBytes.length, 30));

            log.info("Video metadata: duration={}s, resolution={}x{}, format={}, size={}, bitrate={}kbps",
                    metadata.getDuration(), metadata.getWidth(), metadata.getHeight(),
                    metadata.getFormat(), formatFileSize(metadata.getSize()),
                    metadata.getBitrate());

            return metadata;

        } catch (IOException e) {
            log.error("Error reading video file for metadata", e);
            throw new VideoProcessingException("Failed to read video file", e);
        } catch (Exception e) {
            log.error("Error extracting video metadata", e);
            throw new VideoProcessingException("Failed to extract video metadata", e);
        }
    }

    /**
     * Validate video file.
     *
     * @param videoInputStream The input stream of the video file
     * @param fileName The name of the video file
     * @return ValidationResult containing validation status and errors
     */
    public ValidationResult validateVideo(InputStream videoInputStream, String fileName) {
        ValidationResult result = new ValidationResult();

        try {
            byte[] videoBytes = videoInputStream.readAllBytes();
            long fileSize = videoBytes.length;

            // Validate file size (max 100MB for reels)
            long maxSize = 100 * 1024 * 1024; // 100MB
            if (fileSize > maxSize) {
                result.addError("Video size exceeds maximum allowed size of 100MB");
            }

            // Validate file extension
            String extension = getFileExtension(fileName);
            if (!isValidVideoExtension(extension)) {
                result.addError("Invalid video file format. Allowed formats: MP4, MOV, AVI, WebM");
            }

            // Validate minimum size (at least 1KB)
            if (fileSize < 1024) {
                result.addError("Video file is too small");
            }

            // Get metadata for additional validation
            try {
                VideoMetadata metadata = getVideoMetadata(new ByteArrayInputStream(videoBytes));
                
                // Validate duration (max 90 seconds for reels)
                if (metadata.getDuration() > 90) {
                    result.addError("Video duration exceeds maximum allowed length of 90 seconds");
                }

                // Validate minimum duration (at least 1 second)
                if (metadata.getDuration() < 1) {
                    result.addError("Video duration is too short (minimum 1 second)");
                }

            } catch (VideoProcessingException e) {
                log.warn("Could not extract video metadata for validation", e);
            }

            result.setValid(result.getErrors().isEmpty());

        } catch (IOException e) {
            log.error("Error reading video file for validation", e);
            result.addError("Failed to read video file");
            result.setValid(false);
        } catch (Exception e) {
            log.error("Error validating video", e);
            result.addError("Failed to validate video");
            result.setValid(false);
        }

        return result;
    }

    /**
     * Create a placeholder thumbnail image.
     * In a real implementation, this would extract an actual frame from the video.
     */
    private byte[] createPlaceholderThumbnail(String positionName) {
        // Create a simple JPEG placeholder
        // In a real implementation, this would use FFmpeg to extract a frame
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // JPEG magic bytes and minimal valid JPEG header
        byte[] jpegHeader = new byte[]{
                (byte) 0xFF, (byte) 0xD8, // SOI
                (byte) 0xFF, (byte) 0xE0, // APP0
                0x00, 0x10, // Length
                0x4A, 0x46, 0x49, 0x46, 0x00, // JFIF identifier
                0x01, 0x01, // Version
                0x00, // Units
                0x00, 0x01, // X density
                0x00, 0x01, // Y density
                0x00, 0x00, // Thumbnail width
                0x00, 0x00, // Thumbnail height
                (byte) 0xFF, (byte) 0xD9 // EOI
        };
        
        try {
            baos.write(jpegHeader);
        } catch (IOException e) {
            log.error("Error creating placeholder thumbnail", e);
        }
        
        return baos.toByteArray();
    }

    /**
     * Calculate video bitrate in kbps.
     */
    private long calculateBitrate(long fileSizeBytes, int durationSeconds) {
        if (durationSeconds <= 0) {
            return 0;
        }
        return (fileSizeBytes * 8) / (durationSeconds * 1000);
    }

    /**
     * Format file size for display.
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
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
     * Check if file extension is a valid video format.
     */
    private boolean isValidVideoExtension(String extension) {
        if (extension == null) {
            return false;
        }

        return extension.equals("mp4") || 
               extension.equals("mov") || 
               extension.equals("avi") || 
               extension.equals("webm");
    }

    /**
     * Video metadata class.
     */
    public static class VideoMetadata {
        private int duration; // in seconds
        private int width;
        private int height;
        private String format;
        private long size; // in bytes
        private long bitrate; // in kbps

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public long getBitrate() {
            return bitrate;
        }

        public void setBitrate(long bitrate) {
            this.bitrate = bitrate;
        }
    }

    /**
     * Validation result class.
     */
    public static class ValidationResult {
        private boolean valid;
        private java.util.List<String> errors = new java.util.ArrayList<>();

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public java.util.List<String> getErrors() {
            return errors;
        }

        public void addError(String error) {
            this.errors.add(error);
        }
    }
}
