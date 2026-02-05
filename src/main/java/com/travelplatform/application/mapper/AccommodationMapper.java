package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.request.accommodation.CreateAccommodationRequest;
import com.travelplatform.application.dto.response.accommodation.AccommodationResponse;
import com.travelplatform.application.dto.response.accommodation.AccommodationResponse.AccommodationImageResponse;
import com.travelplatform.domain.enums.AccommodationType;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.model.accommodation.AccommodationImage;
import com.travelplatform.domain.model.accommodation.AccommodationAmenity;
import com.travelplatform.domain.valueobject.Address;
import com.travelplatform.domain.valueobject.Location;
import com.travelplatform.domain.valueobject.Money;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Accommodation domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface AccommodationMapper {

    // Custom mapping methods for Money to BigDecimal
    @Named("moneyToBigDecimal")
    default BigDecimal moneyToBigDecimal(Money money) {
        return money != null ? money.getAmount() : null;
    }

    // Custom mapping methods for Address to String components
    @Named("addressToString")
    default String addressToString(Address address) {
        return address != null ? address.toString() : null;
    }

    @Named("addressToCity")
    default String addressToCity(Address address) {
        return address != null ? address.getCity() : null;
    }

    @Named("addressToState")
    default String addressToState(Address address) {
        return address != null ? address.getStateProvince() : null;
    }

    @Named("addressToCountry")
    default String addressToCountry(Address address) {
        return address != null ? address.getCountry() : null;
    }

    @Named("addressToPostalCode")
    default String addressToPostalCode(Address address) {
        return address != null ? address.getPostalCode() : null;
    }

    // Custom mapping methods for Location to BigDecimal
    @Named("locationToLatitude")
    default BigDecimal locationToLatitude(Location location) {
        return location != null ? BigDecimal.valueOf(location.getLatitude()) : null;
    }

    @Named("locationToLongitude")
    default BigDecimal locationToLongitude(Location location) {
        return location != null ? BigDecimal.valueOf(location.getLongitude()) : null;
    }

    @Named("doubleToBigDecimal")
    default BigDecimal doubleToBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }

    // Entity to Response DTO
    default AccommodationResponse toAccommodationResponse(Accommodation accommodation) {
        if (accommodation == null) {
            return null;
        }
        AccommodationResponse response = new AccommodationResponse();
        response.setId(accommodation.getId());
        response.setSupplierId(accommodation.getSupplierId());
        response.setType(accommodation.getType());
        response.setTitle(accommodation.getTitle());
        response.setDescription(accommodation.getDescription());
        response.setAddress(addressToString(accommodation.getAddress()));
        response.setCity(addressToCity(accommodation.getAddress()));
        response.setStateProvince(addressToState(accommodation.getAddress()));
        response.setCountry(addressToCountry(accommodation.getAddress()));
        response.setPostalCode(addressToPostalCode(accommodation.getAddress()));
        response.setLatitude(locationToLatitude(accommodation.getLocation()));
        response.setLongitude(locationToLongitude(accommodation.getLocation()));
        response.setBasePrice(moneyToBigDecimal(accommodation.getBasePrice()));
        response.setCurrency(accommodation.getBasePrice() != null ? accommodation.getBasePrice().getCurrencyCode() : "USD");
        response.setMaxGuests(accommodation.getMaxGuests());
        response.setBedrooms(accommodation.getBedrooms());
        response.setBeds(accommodation.getBeds());
        response.setBathrooms(accommodation.getBathrooms() != null ? BigDecimal.valueOf(accommodation.getBathrooms()) : null);
        response.setStatus(accommodation.getStatus());
        response.setIsPremium(accommodation.isPremium());
        response.setIsInstantBook(accommodation.isInstantBook());
        response.setCheckInTime(accommodation.getCheckInTime() != null ? accommodation.getCheckInTime().toString() : "15:00");
        response.setCheckOutTime(accommodation.getCheckOutTime() != null ? accommodation.getCheckOutTime().toString() : "11:00");
        response.setMinimumNights(accommodation.getMinimumNights());
        response.setMaximumNights(accommodation.getMaximumNights());
        response.setCancellationPolicy(accommodation.getCancellationPolicy());
        response.setViewCount(accommodation.getViewCount());
        response.setBookingCount(accommodation.getBookingCount());
        response.setAverageRating(accommodation.getAverageRating() != null ? BigDecimal.valueOf(accommodation.getAverageRating()) : null);
        response.setReviewCount(accommodation.getReviewCount());
        response.setCreatedAt(accommodation.getCreatedAt());
        response.setUpdatedAt(accommodation.getUpdatedAt());
        response.setApprovedAt(accommodation.getApprovedAt());
        response.setImages(toAccommodationImageResponseList(accommodation.getImages()));
        response.setAmenities(toAmenityStringList(accommodation.getAmenities()));
        return response;
    }

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

    default List<AccommodationImageResponse> toAccommodationImageResponseList(List<AccommodationImage> images) {
        if (images == null) {
            return null;
        }
        return images.stream()
                .map(this::toAccommodationImageResponse)
                .collect(Collectors.toList());
    }

    default List<String> toAmenityStringList(List<AccommodationAmenity> amenities) {
        if (amenities == null) {
            return null;
        }
        return amenities.stream()
                .map(AccommodationAmenity::getAmenityName)
                .collect(Collectors.toList());
    }

    // Request DTO to Entity
    default Accommodation toAccommodationFromRequest(CreateAccommodationRequest request, UUID supplierId) {
        Address address = new Address(
                request.getAddress(),
                request.getCity(),
                request.getStateProvince(),
                request.getCountry(),
                request.getPostalCode()
        );
        
        Location location = new Location(
                request.getLatitude() != null ? request.getLatitude().doubleValue() : 0.0,
                request.getLongitude() != null ? request.getLongitude().doubleValue() : 0.0
        );
        
        Money basePrice = new Money(
                request.getBasePrice(),
                request.getCurrency() != null ? request.getCurrency() : "USD"
        );
        
        Accommodation accommodation = new Accommodation(
                supplierId,
                AccommodationType.valueOf(request.getType()),
                request.getTitle(),
                request.getDescription(),
                address,
                location,
                basePrice,
                request.getMaxGuests()
        );
        
        return accommodation;
    }

    default void updateAccommodationFromRequest(CreateAccommodationRequest request, @MappingTarget Accommodation accommodation) {
        if (request.getTitle() != null) {
            accommodation.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            accommodation.setDescription(request.getDescription());
        }
        if (request.getMaxGuests() > 0) {
            accommodation.setMaxGuests(request.getMaxGuests());
        }
        if (request.getBedrooms() != null) {
            accommodation.setBedrooms(request.getBedrooms());
        }
        if (request.getBeds() != null) {
            accommodation.setBeds(request.getBeds());
        }
        if (request.getBathrooms() != null) {
            accommodation.setBathrooms(request.getBathrooms());
        }
        if (request.getBasePrice() != null) {
            accommodation.setBasePrice(request.getBasePrice());
            if (request.getCurrency() != null) {
                accommodation.setCurrency(request.getCurrency());
            }
        }
        if (request.getMinimumNights() != null) {
            accommodation.setMinimumNights(request.getMinimumNights());
        }
        if (request.getMaximumNights() != null) {
            accommodation.setMaximumNights(request.getMaximumNights());
        }
        if (request.getCancellationPolicy() != null) {
            accommodation.setCancellationPolicy(request.getCancellationPolicy());
        }
    }
}
