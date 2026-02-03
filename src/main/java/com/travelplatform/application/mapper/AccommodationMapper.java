package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.request.accommodation.CreateAccommodationRequest;
import com.travelplatform.application.dto.response.accommodation.AccommodationResponse;
import com.travelplatform.application.dto.response.accommodation.AccommodationResponse.AccommodationImageResponse;
import com.travelplatform.domain.enums.AccommodationType;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.model.accommodation.AccommodationImage;
import com.travelplatform.domain.model.accommodation.AccommodationAmenity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Accommodation domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface AccommodationMapper {

    // Entity to Response DTO
    AccommodationResponse toAccommodationResponse(Accommodation accommodation);

    List<AccommodationResponse> toAccommodationResponseList(List<Accommodation> accommodations);

    default AccommodationImageResponse toAccommodationImageResponse(AccommodationImage image) {
        if (image == null) {
            return null;
        }
        return new AccommodationImageResponse(
                image.getId(),
                image.getImageUrl(),
                image.getDisplayOrder(),
                image.isPrimary(),
                image.getCaption());
    }

    default List<AccommodationImageResponse> mapImages(List<AccommodationImage> images) {
        if (images == null) {
            return null;
        }
        return images.stream()
                .map(this::toAccommodationImageResponse)
                .collect(Collectors.toList());
    }

    default List<String> mapAmenities(List<AccommodationAmenity> amenities) {
        if (amenities == null) {
            return null;
        }
        return amenities.stream()
                .map(AccommodationAmenity::getAmenityName)
                .collect(Collectors.toList());
    }

    // Request DTO to Entity
    // Request DTO to Entity
    default Accommodation toAccommodationFromRequest(CreateAccommodationRequest request, java.util.UUID supplierId) {
        com.travelplatform.domain.valueobject.Address address = new com.travelplatform.domain.valueobject.Address(
                request.getAddress(),
                request.getCity(),
                request.getStateProvince(),
                request.getCountry(),
                request.getPostalCode());

        com.travelplatform.domain.valueobject.Money basePrice = new com.travelplatform.domain.valueobject.Money(
                request.getBasePrice(),
                request.getCurrency());

        Accommodation accommodation = new Accommodation(
                supplierId,
                AccommodationType.valueOf(request.getType()),
                request.getTitle(),
                request.getDescription(),
                address,
                new com.travelplatform.domain.valueobject.Location(
                        request.getLatitude() != null ? request.getLatitude().doubleValue() : 0.0,
                        request.getLongitude() != null ? request.getLongitude().doubleValue() : 0.0),
                basePrice,
                request.getMaxGuests());

        // Set optional fields
        if (request.getBedrooms() != null)
            accommodation.setBedrooms(request.getBedrooms());
        if (request.getBeds() != null)
            accommodation.setBeds(request.getBeds());
        if (request.getBathrooms() != null)
            accommodation.setBathrooms(request.getBathrooms().doubleValue());
        if (request.getSquareMeters() != null)
            accommodation.setSquareMeters(request.getSquareMeters());
        if (request.getCheckInTime() != null)
            accommodation.setCheckInTime(request.getCheckInTime());
        if (request.getCheckOutTime() != null)
            accommodation.setCheckOutTime(request.getCheckOutTime());
        if (request.getMinimumNights() != null)
            accommodation.setMinimumNights(request.getMinimumNights());
        if (request.getMaximumNights() != null)
            accommodation.setMaximumNights(request.getMaximumNights());
        if (request.getCancellationPolicy() != null)
            accommodation.setCancellationPolicy(request.getCancellationPolicy());
        if (request.getIsInstantBook() != null)
            accommodation.setIsInstantBook(request.getIsInstantBook());

        return accommodation;
    }

    default AccommodationImage toAccommodationImageFromRequest(CreateAccommodationRequest.ImageRequest imageRequest,
            java.util.UUID accommodationId) {
        // Using Constructor instead of create
        return new AccommodationImage(
                accommodationId,
                imageRequest.getImageUrl(),
                imageRequest.getDisplayOrder(),
                imageRequest.getIsPrimary(),
                imageRequest.getCaption());
    }

    default AccommodationAmenity toAccommodationAmenityFromRequest(
            CreateAccommodationRequest.AmenityRequest amenityRequest, java.util.UUID accommodationId) {
        // Using Constructor
        return new AccommodationAmenity(
                accommodationId,
                amenityRequest.getAmenityName(),
                amenityRequest.getCategory());
    }

    @Named("supplierId")
    default java.util.UUID mapSupplierId(Accommodation accommodation) {
        return accommodation != null ? accommodation.getSupplierId() : null;
    }

    @Named("supplierName")
    default String mapSupplierName(Accommodation accommodation) {
        // TODO: Entity only has supplierId
        return null;
        /*
         * return accommodation != null && accommodation.getSupplier() != null
         * ? accommodation.getSupplier().getProfile().getFullName()
         * : null;
         */
    }

    @Named("status")
    default ApprovalStatus mapStatus(Accommodation accommodation) {
        return accommodation != null ? accommodation.getStatus() : null;
    }
}
