package com.travelplatform.application.validator;

import com.travelplatform.application.dto.request.reel.CreateReelRequest;
import com.travelplatform.domain.enums.VisibilityScope;
import com.travelplatform.domain.service.ValidationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Validator for reel-related operations.
 * Provides additional validation beyond bean validation annotations.
 */
@ApplicationScoped
public class ReelValidator {

    private static final int MIN_DURATION = 1;
    private static final int MAX_DURATION = 90;
    private static final int MIN_TITLE_LENGTH = 1;
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MIN_DESCRIPTION_LENGTH = 1;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MIN_TAGS = 0;
    private static final int MAX_TAGS = 30;
    private static final int MAX_TAG_LENGTH = 50;

    @Inject
    ValidationService validationService;

    /**
     * Validates reel creation request.
     */
    public void validateReelCreation(CreateReelRequest request) {
        // Video URL validation
        if (request.getVideoUrl() == null || request.getVideoUrl().isBlank()) {
            throw new IllegalArgumentException("Video URL is required");
        }

        // Thumbnail URL validation
        if (request.getThumbnailUrl() == null || request.getThumbnailUrl().isBlank()) {
            throw new IllegalArgumentException("Thumbnail URL is required");
        }

        // Duration validation
        if (request.getDuration() != null) {
            if (request.getDuration() < MIN_DURATION) {
                throw new IllegalArgumentException("Video duration must be at least " + MIN_DURATION + " second");
            }
            if (request.getDuration() > MAX_DURATION) {
                throw new IllegalArgumentException("Video duration cannot exceed " + MAX_DURATION + " seconds");
            }
        }

        // Title validation
        if (request.getTitle() != null) {
            if (request.getTitle().length() < MIN_TITLE_LENGTH) {
                throw new IllegalArgumentException("Title is too short");
            }
            if (request.getTitle().length() > MAX_TITLE_LENGTH) {
                throw new IllegalArgumentException("Title cannot exceed " + MAX_TITLE_LENGTH + " characters");
            }
        }

        // Description validation
        if (request.getDescription() != null) {
            if (request.getDescription().length() < MIN_DESCRIPTION_LENGTH) {
                throw new IllegalArgumentException("Description is too short");
            }
            if (request.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
                throw new IllegalArgumentException("Description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters");
            }
        }

        // Visibility validation
        if (request.getVisibility() != null) {
            try {
                VisibilityScope.valueOf(request.getVisibility());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid visibility scope");
            }
        }

        // Tags validation
        if (request.getTags() != null) {
            validateTags(request.getTags());
        }

        // Location validation
        if (request.getLocationLatitude() != null || request.getLocationLongitude() != null) {
            if (request.getLocationLatitude() == null || request.getLocationLongitude() == null) {
                throw new IllegalArgumentException("Both latitude and longitude must be provided");
            }

            if (request.getLocationLatitude() < -90 || request.getLocationLatitude() > 90) {
                throw new IllegalArgumentException("Invalid latitude value");
            }

            if (request.getLocationLongitude() < -180 || request.getLocationLongitude() > 180) {
                throw new IllegalArgumentException("Invalid longitude value");
            }
        }

        // Use domain validation service
        validationService.validateReel(
            request.getTitle(),
            request.getDescription(),
            request.getVideoUrl(),
            request.getThumbnailUrl()
        );
    }

    /**
     * Validates reel tags.
     */
    public void validateTags(List<String> tags) {
        if (tags == null) {
            return;
        }

        if (tags.size() < MIN_TAGS) {
            throw new IllegalArgumentException("At least " + MIN_TAGS + " tag is required");
        }

        if (tags.size() > MAX_TAGS) {
            throw new IllegalArgumentException("Maximum " + MAX_TAGS + " tags allowed");
        }

        // Check for empty tags
        for (String tag : tags) {
            if (tag == null || tag.isBlank()) {
                throw new IllegalArgumentException("Tags cannot be empty");
            }

            if (tag.length() > MAX_TAG_LENGTH) {
                throw new IllegalArgumentException("Tag cannot exceed " + MAX_TAG_LENGTH + " characters");
            }
        }

        // Check for duplicates
        long uniqueCount = tags.stream().distinct().count();
        if (uniqueCount != tags.size()) {
            throw new IllegalArgumentException("Duplicate tags are not allowed");
        }
    }

    /**
     * Validates reel comment.
     */
    public void validateComment(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Comment content is required");
        }

        if (content.length() < 1) {
            throw new IllegalArgumentException("Comment is too short");
        }

        if (content.length() > 300) {
            throw new IllegalArgumentException("Comment cannot exceed 300 characters");
        }
    }

    /**
     * Validates reel report.
     */
    public void validateReport(String reason, String description) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Report reason is required");
        }

        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Report description cannot exceed 500 characters");
        }
    }
}
