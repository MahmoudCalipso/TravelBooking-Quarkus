package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.request.reel.CreateReelRequest;
import com.travelplatform.application.dto.response.reel.ReelResponse;
import com.travelplatform.domain.enums.VisibilityScope;
import com.travelplatform.domain.model.reel.TravelReel;
import com.travelplatform.domain.valueobject.Location;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Mapper for TravelReel domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface ReelMapper {

    default ReelResponse toReelResponse(TravelReel reel) {
        if (reel == null) {
            return null;
        }
        ReelResponse response = new ReelResponse();
        response.setId(reel.getId());
        response.setCreatorId(reel.getCreatorId());
        response.setCreatorName(null);
        response.setCreatorPhotoUrl(null);
        response.setCreatorType(reel.getCreatorType() != null ? reel.getCreatorType().name() : null);
        response.setVideoUrl(reel.getVideoUrl());
        response.setThumbnailUrl(reel.getThumbnailUrl());
        response.setTitle(reel.getTitle());
        response.setDescription(reel.getDescription());
        response.setDuration(reel.getDuration());
        if (reel.getLocation() != null) {
            response.setLocationLatitude(BigDecimal.valueOf(reel.getLocation().getLatitude()));
            response.setLocationLongitude(BigDecimal.valueOf(reel.getLocation().getLongitude()));
        }
        response.setLocationName(reel.getLocationName());
        response.setTags(reel.getTags());
        response.setRelatedEntityType(reel.getRelatedEntityType() != null ? reel.getRelatedEntityType().name() : null);
        response.setRelatedEntityId(reel.getRelatedEntityId());
        response.setRelatedEntityTitle(null);
        response.setVisibility(reel.getVisibility());
        response.setStatus(reel.getStatus());
        response.setIsPromotional(reel.isPromotional());
        response.setViewCount(reel.getViewCount());
        response.setUniqueViewCount(reel.getUniqueViewCount());
        response.setLikeCount(reel.getLikeCount());
        response.setCommentCount(reel.getCommentCount());
        response.setShareCount(reel.getShareCount());
        response.setBookmarkCount(reel.getBookmarkCount());
        response.setAverageWatchTime(reel.getAverageWatchTime());
        if (reel.getCompletionRate() != null) {
            response.setCompletionRate(BigDecimal.valueOf(reel.getCompletionRate()));
        }
        response.setCreatedAt(reel.getCreatedAt());
        response.setUpdatedAt(reel.getUpdatedAt());
        response.setApprovedAt(reel.getApprovedAt());
        response.setIsLikedByCurrentUser(Boolean.FALSE);
        response.setIsBookmarkedByCurrentUser(Boolean.FALSE);
        return response;
    }

    default List<ReelResponse> toReelResponseList(List<TravelReel> reels) {
        if (reels == null) {
            return null;
        }
        List<ReelResponse> responses = new ArrayList<>(reels.size());
        for (TravelReel reel : reels) {
            responses.add(toReelResponse(reel));
        }
        return responses;
    }

    // Request DTO to Entity
    default TravelReel toReelFromRequest(CreateReelRequest request, UUID creatorId) {
        VisibilityScope visibility = request.getVisibility() != null
                ? VisibilityScope.valueOf(request.getVisibility())
                : VisibilityScope.PUBLIC;

        Location location = new Location(
                request.getLocationLatitude() != null ? request.getLocationLatitude().doubleValue() : 0.0,
                request.getLocationLongitude() != null ? request.getLocationLongitude().doubleValue() : 0.0);

        TravelReel reel = new TravelReel(
                creatorId,
                request.getCreatorType(),
                request.getVideoUrl(),
                request.getThumbnailUrl(),
                request.getDuration(),
                location,
                request.getLocationName(),
                visibility,
                false);

        if (request.getRelatedEntityType() != null && request.getRelatedEntityId() != null) {
            try {
                TravelReel.RelatedEntityType entityType = TravelReel.RelatedEntityType
                        .valueOf(request.getRelatedEntityType());
                reel.linkToEntity(entityType, request.getRelatedEntityId());
            } catch (IllegalArgumentException e) {
                // ignore invalid related entity type and keep it unlinked
            }
        }

        reel.updateDetails(request.getTitle(), request.getDescription(), request.getLocationName(), request.getTags());

        return reel;
    }
}
