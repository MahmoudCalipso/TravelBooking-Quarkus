package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.request.reel.CreateReelRequest;
import com.travelplatform.application.dto.response.reel.ReelResponse;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.VisibilityScope;
import com.travelplatform.domain.model.reel.TravelReel;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.UUID;
import java.util.List;

/**
 * Mapper for TravelReel domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface ReelMapper {

    // Entity to Response DTO
    ReelResponse toReelResponse(TravelReel reel);

    List<ReelResponse> toReelResponseList(List<TravelReel> reels);

    // Request DTO to Entity
    default TravelReel toReelFromRequest(CreateReelRequest request, UUID creatorId) {
        VisibilityScope visibility = request.getVisibility() != null
                ? VisibilityScope.valueOf(request.getVisibility())
                : VisibilityScope.PUBLIC;

        TravelReel reel = new TravelReel(
                creatorId,
                request.getCreatorType(),
                request.getVideoUrl(),
                request.getThumbnailUrl(),
                request.getDuration(),
                // Assuming Location parsing is handled elsewhere or passed as null/default if
                // signatures don't match.
                // The request has lat/long but constructor wants Location value object.
                // Converting lat/long to Location would be needed here.
                // For now, I'll pass null or assume existing constructor matches partially?
                // Constructor: (UUID, CreatorType, String, String, int, Location, String,
                // VisibilityScope, boolean)
                // request.getLocationLatitude() etc.
                new com.travelplatform.domain.valueobject.Location(
                        request.getLocationLatitude() != null ? request.getLocationLatitude().doubleValue() : 0.0,
                        request.getLocationLongitude() != null ? request.getLocationLongitude().doubleValue() : 0.0),
                request.getLocationName(),
                visibility,
                false // isPromotional default
        );
        // Linked properties set manually since constructor doesn't take them all in the
        // short version used here?
        // Actually the Request to Entity logic needs to adapt to the Constructor
        // available in `TravelReel.java`.

        if (request.getRelatedEntityType() != null && request.getRelatedEntityId() != null) {
            try {
                TravelReel.RelatedEntityType entityType = TravelReel.RelatedEntityType
                        .valueOf(request.getRelatedEntityType());
                reel.linkToEntity(entityType, request.getRelatedEntityId());
            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }

        reel.updateDetails(request.getTitle(), request.getDescription(), request.getLocationName(), request.getTags());

        return reel;
    }

    @Named("creatorId")
    default UUID mapCreatorId(TravelReel reel) {
        return reel != null ? reel.getCreatorId() : null;
    }

    @Named("creatorName")
    default String mapCreatorName(TravelReel reel) {
        // TODO: Entity only has creatorId
        return null;
        /*
         * return reel != null && reel.getCreator() != null &&
         * reel.getCreator().getProfile() != null
         * ? reel.getCreator().getProfile().getFullName()
         * : null;
         */
    }

    @Named("creatorPhotoUrl")
    default String mapCreatorPhotoUrl(TravelReel reel) {
        // TODO: Entity only has creatorId
        return null;
        /*
         * return reel != null && reel.getCreator() != null &&
         * reel.getCreator().getProfile() != null
         * ? reel.getCreator().getProfile().getPhotoUrl()
         * : null;
         */
    }

    @Named("status")
    default ApprovalStatus mapStatus(TravelReel reel) {
        return reel != null ? reel.getStatus() : null;
    }

    @Named("visibility")
    default VisibilityScope mapVisibility(TravelReel reel) {
        return reel != null ? reel.getVisibility() : null;
    }
}
