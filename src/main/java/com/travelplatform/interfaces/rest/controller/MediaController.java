package com.travelplatform.interfaces.rest.controller;

import com.travelplatform.application.dto.request.media.UploadConfirmRequest;
import com.travelplatform.application.dto.request.media.UploadRequestRequest;
import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.dto.response.media.UploadConfirmResponse;
import com.travelplatform.application.dto.response.media.UploadRequestResponse;
import com.travelplatform.domain.model.media.MediaAsset;
import com.travelplatform.domain.repository.MediaAssetRepository;
import com.travelplatform.infrastructure.security.CurrentUser;
import com.travelplatform.infrastructure.storage.firebase.FirebaseStorageClient;
import com.travelplatform.infrastructure.storage.firebase.FirebaseStorageClient.StorageException;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Media Controller.
 * 
 * Handles media upload and management using Firebase Storage as the single source of truth.
 * Media files are stored in Firebase Storage, and only metadata is stored in the database.
 * 
 * Upload Flow:
 * 1. Client requests upload URL (POST /api/v1/media/upload-request)
 * 2. Backend generates signed URL and creates pending MediaAsset record
 * 3. Client uploads file directly to Firebase Storage using signed URL
 * 4. Client confirms upload (POST /api/v1/media/confirm)
 * 5. Backend verifies file exists and updates MediaAsset record
 */
@Path("/api/v1/media")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MediaController {

    private static final Logger log = LoggerFactory.getLogger(MediaController.class);

    @Inject
    MediaAssetRepository mediaAssetRepository;

    @Inject
    FirebaseStorageClient firebaseStorageClient;

    @Inject
    CurrentUser currentUser;

    /**
     * Request upload URL for a new media file.
     * 
     * This endpoint generates a signed URL that the client can use to upload
     * the file directly to Firebase Storage. A pending MediaAsset record is created.
     * 
     * @param request Upload request details
     * @return Response containing signed URL and upload metadata
     */
    @POST
    @Path("/upload-request")
    @Transactional
    public Response requestUploadUrl(@Valid UploadRequestRequest request) {
        log.info("Upload request received - OwnerType: {}, OwnerId: {}, MediaType: {}", 
                request.getOwnerType(), request.getOwnerId(), request.getMediaType());

        try {
            // Validate owner ID format
            UUID ownerUuid;
            try {
                ownerUuid = UUID.fromString(request.getOwnerId());
            } catch (IllegalArgumentException e) {
                ErrorResponse error = new ErrorResponse("INVALID_OWNER_ID", "Owner ID must be a valid UUID");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Validate content type
            if (!isAllowedContentType(request.getMediaType(), request.getContentType())) {
                ErrorResponse error = new ErrorResponse("INVALID_CONTENT_TYPE",
                        "Content type is not allowed for this media type");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Validate file size if provided
            if (request.getFileSize() != null) {
                Long maxFileSize = getMaxFileSize(request.getMediaType());
                if (maxFileSize != null && request.getFileSize() > maxFileSize) {
                    ErrorResponse error = new ErrorResponse("FILE_SIZE_EXCEEDED",
                            "File size exceeds maximum allowed size for this media type");
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(error)
                            .build();
                }
            }

            // Generate Firebase storage path
            String firebasePath = generateStoragePath(
                    request.getOwnerType(),
                    ownerUuid,
                    request.getMediaType(),
                    request.getFileName(),
                    request.getFolderPath());

            // Generate signed URL for upload
            String uploadUrl = firebaseStorageClient.generateSignedUrl(
                    firebasePath,
                    15
            );

            // Create MediaAsset record
            MediaAsset mediaAsset = new MediaAsset();
            mediaAsset.setId(UUID.randomUUID());
            mediaAsset.setOwnerId(ownerUuid);
            mediaAsset.setOwnerType(request.getOwnerType());
            mediaAsset.setMediaType(request.getMediaType());
            mediaAsset.setFirebasePath(firebasePath);
            mediaAsset.setPublicUrl(null);
            mediaAsset.setSizeBytes(request.getFileSize() != null ? request.getFileSize() : 0L);
            mediaAsset.setMimeType(request.getContentType());
            mediaAsset.setCreatedAt(LocalDateTime.now());
            mediaAsset.setUpdatedAt(LocalDateTime.now());

            mediaAssetRepository.save(mediaAsset);

            // Build response
            UploadRequestResponse response = new UploadRequestResponse();
            response.setUploadUrl(uploadUrl);
            response.setFirebasePath(firebasePath);
            response.setMediaAssetId(mediaAsset.getId().toString());
            response.setMediaType(request.getMediaType());
            response.setMaxFileSize(getMaxFileSize(request.getMediaType()));
            response.setAllowedContentTypes(getAllowedContentTypes(request.getMediaType()));
            response.setUrlExpirationMinutes(15L); // 15 minutes default

            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setSuccess(true);
            successResponse.setData(response);
            successResponse.setMessage("Upload URL generated successfully");

            return Response.ok(successResponse).build();

        } catch (StorageException e) {
            log.error("Error generating upload URL", e);
            ErrorResponse error = new ErrorResponse("STORAGE_ERROR",
                    "Failed to generate upload URL: " + e.getMessage());
            return Response.serverError().entity(error).build();
        } catch (Exception e) {
            log.error("Unexpected error during upload request", e);
            ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred");
            return Response.serverError().entity(error).build();
        }
    }

    /**
     * Confirm successful media upload.
     * 
     * This endpoint is called after the client has successfully uploaded the file
     * to Firebase Storage. The backend verifies the file exists and updates
     * the MediaAsset record with the final metadata.
     * 
     * @param mediaAssetId The ID of the media asset
     * @param request Upload confirmation details
     * @return Response containing confirmed media asset details
     */
    @POST
    @Path("/{mediaAssetId}/confirm")
    @Transactional
    public Response confirmUpload(
            @PathParam("mediaAssetId") String mediaAssetId,
            @Valid UploadConfirmRequest request) {
        log.info("Upload confirmation received - MediaAssetId: {}, FirebasePath: {}", 
                mediaAssetId, request.getFirebasePath());

        try {
            // Validate media asset ID format
            UUID assetId;
            try {
                assetId = UUID.fromString(mediaAssetId);
            } catch (IllegalArgumentException e) {
                ErrorResponse error = new ErrorResponse("INVALID_MEDIA_ASSET_ID",
                        "Media asset ID must be a valid UUID");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Find media asset
            MediaAsset mediaAsset = mediaAssetRepository.findById(assetId).orElse(null);
            if (mediaAsset == null) {
                ErrorResponse error = new ErrorResponse("MEDIA_ASSET_NOT_FOUND", "Media asset not found");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(error)
                        .build();
            }

            // Verify Firebase path matches
            if (!mediaAsset.getFirebasePath().equals(request.getFirebasePath())) {
                ErrorResponse error = new ErrorResponse("FIREBASE_PATH_MISMATCH",
                        "Firebase path does not match media asset");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Verify file exists in Firebase Storage
            if (!firebaseStorageClient.fileExists(request.getFirebasePath())) {
                ErrorResponse error = new ErrorResponse("FILE_NOT_FOUND",
                        "File not found in Firebase Storage");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(error)
                        .build();
            }

            // Get actual file size from Firebase
            Long actualFileSize = firebaseStorageClient.getFileSize(request.getFirebasePath());

            // Generate public URL
            String publicUrl = request.getPublicUrl();
            if (publicUrl == null || publicUrl.isEmpty()) {
                publicUrl = firebaseStorageClient.generateSignedUrl(
                        request.getFirebasePath(),
                        60
                );
            }

            // Update media asset with confirmed details
            mediaAsset.updatePublicUrl(publicUrl);
            mediaAsset.setSizeBytes(actualFileSize);
            mediaAssetRepository.save(mediaAsset);

            // Build response
            UploadConfirmResponse response = new UploadConfirmResponse();
            response.setMediaAssetId(mediaAsset.getId().toString());
            response.setFirebasePath(mediaAsset.getFirebasePath());
            response.setPublicUrl(mediaAsset.getPublicUrl());
            response.setOwnerType(mediaAsset.getOwnerType());
            response.setOwnerId(mediaAsset.getOwnerId().toString());
            response.setMediaType(mediaAsset.getMediaType());
            response.setSizeBytes(mediaAsset.getSizeBytes());
            response.setMimeType(mediaAsset.getMimeType());
            response.setFormattedSize(mediaAsset.getFormattedSize());
            response.setCreatedAt(mediaAsset.getCreatedAt());
            response.setUpdatedAt(mediaAsset.getUpdatedAt());

            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setSuccess(true);
            successResponse.setData(response);
            successResponse.setMessage("Media upload confirmed successfully");

            return Response.ok(successResponse).build();

        } catch (StorageException e) {
            log.error("Error confirming upload", e);
            ErrorResponse error = new ErrorResponse("STORAGE_ERROR",
                    "Failed to confirm upload: " + e.getMessage());
            return Response.serverError().entity(error).build();
        } catch (Exception e) {
            log.error("Unexpected error during upload confirmation", e);
            ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred");
            return Response.serverError().entity(error).build();
        }
    }

    /**
     * Get media asset details.
     * 
     * @param mediaAssetId The ID of the media asset
     * @return Response containing media asset details
     */
    @GET
    @Path("/{mediaAssetId}")
    public Response getMediaAsset(@PathParam("mediaAssetId") String mediaAssetId) {
        log.info("Get media asset request - MediaAssetId: {}", mediaAssetId);

        try {
            // Validate media asset ID format
            UUID assetId;
            try {
                assetId = UUID.fromString(mediaAssetId);
            } catch (IllegalArgumentException e) {
                ErrorResponse error = new ErrorResponse("INVALID_MEDIA_ASSET_ID",
                        "Media asset ID must be a valid UUID");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Find media asset
            MediaAsset mediaAsset = mediaAssetRepository.findById(assetId).orElse(null);
            if (mediaAsset == null) {
                ErrorResponse error = new ErrorResponse("MEDIA_ASSET_NOT_FOUND", "Media asset not found");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(error)
                        .build();
            }

            // Build response
            Map<String, Object> data = new HashMap<>();
            data.put("mediaAssetId", mediaAsset.getId().toString());
            data.put("firebasePath", mediaAsset.getFirebasePath());
            data.put("publicUrl", mediaAsset.getPublicUrl());
            data.put("ownerType", mediaAsset.getOwnerType());
            data.put("ownerId", mediaAsset.getOwnerId());
            data.put("mediaType", mediaAsset.getMediaType());
            data.put("sizeBytes", mediaAsset.getSizeBytes());
            data.put("mimeType", mediaAsset.getMimeType());
            data.put("formattedSize", mediaAsset.getFormattedSize());
            data.put("createdAt", mediaAsset.getCreatedAt());
            data.put("updatedAt", mediaAsset.getUpdatedAt());

            SuccessResponse response = new SuccessResponse();
            response.setSuccess(true);
            response.setData(data);
            response.setMessage("Media asset retrieved successfully");

            return Response.ok(response).build();

        } catch (Exception e) {
            log.error("Error retrieving media asset", e);
            ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred");
            return Response.serverError().entity(error).build();
        }
    }

    /**
     * Delete media asset.
     * 
     * Deletes the media asset from both the database and Firebase Storage.
     * 
     * @param mediaAssetId The ID of the media asset
     * @return Response confirming deletion
     */
    @DELETE
    @Path("/{mediaAssetId}")
    @Transactional
    public Response deleteMediaAsset(@PathParam("mediaAssetId") String mediaAssetId) {
        log.info("Delete media asset request - MediaAssetId: {}", mediaAssetId);

        try {
            // Validate media asset ID format
            UUID assetId;
            try {
                assetId = UUID.fromString(mediaAssetId);
            } catch (IllegalArgumentException e) {
                ErrorResponse error = new ErrorResponse("INVALID_MEDIA_ASSET_ID",
                        "Media asset ID must be a valid UUID");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Find media asset
            MediaAsset mediaAsset = mediaAssetRepository.findById(assetId).orElse(null);
            if (mediaAsset == null) {
                ErrorResponse error = new ErrorResponse("MEDIA_ASSET_NOT_FOUND", "Media asset not found");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(error)
                        .build();
            }

            // Delete from Firebase Storage
            try {
                firebaseStorageClient.deleteFile(mediaAsset.getFirebasePath());
            } catch (StorageException e) {
                log.warn("Failed to delete file from Firebase Storage: {}", e.getMessage());
                // Continue with database deletion even if Firebase deletion fails
            }

            // Delete from database
            mediaAssetRepository.deleteById(assetId);

            SuccessResponse response = new SuccessResponse();
            response.setSuccess(true);
            response.setMessage("Media asset deleted successfully");

            return Response.ok(response).build();

        } catch (Exception e) {
            log.error("Error deleting media asset", e);
            ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred");
            return Response.serverError().entity(error).build();
        }
    }

    /**
     * Get media assets by owner.
     * 
     * @param ownerType The type of owner
     * @param ownerId The ID of the owner
     * @return Response containing list of media assets
     */
    @GET
    @Path("/owner/{ownerType}/{ownerId}")
    public Response getMediaAssetsByOwner(
            @PathParam("ownerType") String ownerType,
            @PathParam("ownerId") String ownerId) {
        log.info("Get media assets by owner request - OwnerType: {}, OwnerId: {}", ownerType, ownerId);

        try {
            // Validate owner type
            MediaAsset.OwnerType type;
            try {
                type = MediaAsset.OwnerType.valueOf(ownerType.toUpperCase());
            } catch (IllegalArgumentException e) {
                ErrorResponse error = new ErrorResponse("INVALID_OWNER_TYPE", "Invalid owner type");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Validate owner ID format
            UUID ownerUuid;
            try {
                ownerUuid = UUID.fromString(ownerId);
            } catch (IllegalArgumentException e) {
                ErrorResponse error = new ErrorResponse("INVALID_OWNER_ID", "Owner ID must be a valid UUID");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Find media assets
            var mediaAssets = mediaAssetRepository.findByOwnerIdAndOwnerType(ownerUuid, type);

            // Build response
            Map<String, Object> data = new HashMap<>();
            data.put("mediaAssets", mediaAssets);
            data.put("count", mediaAssets.size());

            SuccessResponse response = new SuccessResponse();
            response.setSuccess(true);
            response.setData(data);
            response.setMessage("Media assets retrieved successfully");

            return Response.ok(response).build();

        } catch (Exception e) {
            log.error("Error retrieving media assets by owner", e);
            ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred");
            return Response.serverError().entity(error).build();
        }
    }

    // Helper methods

    private Long getMaxFileSize(MediaAsset.MediaType mediaType) {
        return switch (mediaType) {
            case IMAGE -> 10L * 1024L * 1024L; // 10MB
            case VIDEO -> 100L * 1024L * 1024L; // 100MB
            case AUDIO -> 20L * 1024L * 1024L; // 20MB
            case DOCUMENT -> 25L * 1024L * 1024L; // 25MB
        };
    }

    private String[] getAllowedContentTypes(MediaAsset.MediaType mediaType) {
        return switch (mediaType) {
            case IMAGE -> new String[]{
                    "image/jpeg",
                    "image/jpg",
                    "image/png",
                    "image/gif",
                    "image/webp"
            };
            case VIDEO -> new String[]{
                    "video/mp4",
                    "video/mpeg",
                    "video/quicktime",
                    "video/x-msvideo",
                    "video/webm"
            };
            case AUDIO -> new String[]{
                    "audio/mpeg",
                    "audio/mp3",
                    "audio/wav",
                    "audio/ogg",
                    "audio/webm"
            };
            case DOCUMENT -> new String[]{
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "text/plain"
            };
        };
    }

    private boolean isAllowedContentType(MediaAsset.MediaType mediaType, String contentType) {
        if (contentType == null) {
            return false;
        }
        for (String allowed : getAllowedContentTypes(mediaType)) {
            if (allowed.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }

    private String generateStoragePath(
            MediaAsset.OwnerType ownerType,
            UUID ownerId,
            MediaAsset.MediaType mediaType,
            String fileName,
            String folderPath) {
        String extension = "";
        if (fileName != null) {
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = fileName.substring(dotIndex);
            }
        }
        String uniqueName = System.currentTimeMillis() + "_" +
                Integer.toHexString((int) (Math.random() * 0xFFFFFF));
        String basePath = ownerType.name().toLowerCase() + "/" + ownerId + "/" + mediaType.name().toLowerCase();
        if (folderPath != null && !folderPath.isBlank()) {
            basePath = basePath + "/" + folderPath.trim().replaceAll("^/+", "").replaceAll("/+$", "");
        }
        return basePath + "/" + uniqueName + extension;
    }
}
