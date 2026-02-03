package com.travelplatform.interfaces.rest.controller;

import com.travelplatform.application.dto.request.accommodation.CreateAccommodationRequest;
import com.travelplatform.application.dto.request.accommodation.SearchAccommodationRequest;
import com.travelplatform.application.dto.response.accommodation.AccommodationResponse;
import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.PageResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.service.accommodation.AccommodationService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for accommodation operations.
 * Handles accommodation search, creation, and management.
 */
@Path("/api/v1/accommodations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Accommodations", description = "Accommodation search and management")
public class AccommodationController {

    private static final Logger log = LoggerFactory.getLogger(AccommodationController.class);

    @Inject
    private AccommodationService accommodationService;

    /**
     * Search accommodations (public).
     *
     * @param city      The city
     * @param country   The country
     * @param type      The accommodation type
     * @param minPrice  The minimum price
     * @param maxPrice  The maximum price
     * @param maxGuests The maximum number of guests
     * @param bedrooms  The number of bedrooms
     * @param checkIn   The check-in date
     * @param checkOut  The check-out date
     * @param amenities The list of amenities
     * @param page      The page number
     * @param pageSize  The page size
     * @param sortBy    The sort field
     * @param sortOrder The sort order
     * @return Paginated list of accommodations
     */
    @GET
    @PermitAll
    @Operation(summary = "Search accommodations", description = "Search for accommodations with filters")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Accommodations retrieved successfully")
    })
    public Response searchAccommodations(
            @QueryParam("city") String city,
            @QueryParam("country") String country,
            @QueryParam("type") String type,
            @QueryParam("minPrice") Double minPrice,
            @QueryParam("maxPrice") Double maxPrice,
            @QueryParam("maxGuests") Integer maxGuests,
            @QueryParam("bedrooms") Integer bedrooms,
            @QueryParam("checkIn") LocalDate checkIn,
            @QueryParam("checkOut") LocalDate checkOut,
            @QueryParam("amenities") List<String> amenities,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("sortOrder") @DefaultValue("desc") String sortOrder) {
        try {
            log.info("Search accommodations request - city: {}, country: {}, type: {}", city, country, type);

            SearchAccommodationRequest request = new SearchAccommodationRequest();
            request.setCity(city);
            request.setCountry(country);
            request.setType(type);
            request.setMinPrice(minPrice);
            request.setMaxPrice(maxPrice);
            request.setMaxGuests(maxGuests);
            request.setBedrooms(bedrooms);
            request.setCheckIn(checkIn);
            request.setCheckOut(checkOut);
            request.setAmenities(amenities);
            request.setPage(page);
            request.setPageSize(pageSize);
            request.setSortBy(sortBy);
            request.setSortOrder(sortOrder);

            PageResponse<AccommodationResponse> accommodations = accommodationService.searchAccommodations(request);

            return Response.ok()
                    .entity(accommodations)
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error searching accommodations", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get accommodation by ID.
     *
     * @param accommodationId The accommodation ID
     * @return Accommodation response
     */
    @GET
    @Path("/{accommodationId}")
    @PermitAll
    @Operation(summary = "Get accommodation by ID", description = "Get accommodation details by ID")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Accommodation retrieved successfully"),
            @APIResponse(responseCode = "404", description = "Accommodation not found")
    })
    public Response getAccommodationById(@PathParam("accommodationId") UUID accommodationId) {
        try {
            log.info("Get accommodation by ID request: {}", accommodationId);

            AccommodationResponse accommodation = accommodationService.getAccommodationById(accommodationId);

            // Increment view count
            accommodationService.incrementViewCount(accommodationId);

            return Response.ok()
                    .entity(new SuccessResponse<>(accommodation, "Accommodation retrieved successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Accommodation not found: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("ACCOMMODATION_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error getting accommodation", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Check accommodation availability.
     *
     * @param accommodationId The accommodation ID
     * @param checkIn         The check-in date
     * @param checkOut        The check-out date
     * @return Availability response
     */
    @GET
    @Path("/{accommodationId}/availability")
    @PermitAll
    @Operation(summary = "Check availability", description = "Check accommodation availability for dates")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Availability checked successfully"),
            @APIResponse(responseCode = "404", description = "Accommodation not found")
    })
    public Response checkAvailability(
            @PathParam("accommodationId") UUID accommodationId,
            @QueryParam("checkIn") LocalDate checkIn,
            @QueryParam("checkOut") LocalDate checkOut) {
        try {
            log.info("Check availability request for accommodation: {}, dates: {} to {}", accommodationId, checkIn,
                    checkOut);

            boolean isAvailable = accommodationService.checkAvailability(accommodationId, checkIn, checkOut);

            return Response.ok()
                    .entity(new SuccessResponse<>(isAvailable, "Availability checked successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Availability check failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("AVAILABILITY_CHECK_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error checking availability", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Find nearby accommodations.
     *
     * @param latitude  The latitude
     * @param longitude The longitude
     * @param radiusKm  The radius in kilometers
     * @param page      The page number
     * @param pageSize  The page size
     * @return Paginated list of nearby accommodations
     */
    @GET
    @Path("/nearby")
    @PermitAll
    @Operation(summary = "Find nearby accommodations", description = "Find accommodations near a location")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Nearby accommodations retrieved successfully")
    })
    public Response findNearbyAccommodations(
            @QueryParam("latitude") Double latitude,
            @QueryParam("longitude") Double longitude,
            @QueryParam("radiusKm") @DefaultValue("10") Double radiusKm,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            log.info("Find nearby accommodations request - lat: {}, lng: {}, radius: {}km", latitude, longitude,
                    radiusKm);

            PageResponse<AccommodationResponse> accommodations = accommodationService.findNearbyAccommodations(latitude,
                    longitude, radiusKm, page, pageSize);

            return Response.ok()
                    .entity(accommodations)
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error finding nearby accommodations", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Create new accommodation (supplier only).
     *
     * @param securityContext The security context
     * @param request         The accommodation creation request
     * @return Created accommodation response
     */
    @POST
    @Authenticated
    @RolesAllowed("SUPPLIER_SUBSCRIBER")
    @Operation(summary = "Create accommodation", description = "Create a new accommodation (supplier only)")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Accommodation created successfully"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response createAccommodation(@Context SecurityContext securityContext,
            @Valid CreateAccommodationRequest request) {
        try {
            String supplierId = securityContext.getUserPrincipal().getName();
            log.info("Create accommodation request by supplier: {}", supplierId);

            AccommodationResponse accommodation = accommodationService.createAccommodation(UUID.fromString(supplierId),
                    request);

            return Response.status(Response.Status.CREATED)
                    .entity(new SuccessResponse<>(accommodation, "Accommodation created successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Accommodation creation failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error creating accommodation", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Update accommodation (supplier only).
     *
     * @param securityContext The security context
     * @param accommodationId The accommodation ID
     * @param request         The accommodation update request
     * @return Updated accommodation response
     */
    @PUT
    @Path("/{accommodationId}")
    @Authenticated
    @RolesAllowed("SUPPLIER_SUBSCRIBER")
    @Operation(summary = "Update accommodation", description = "Update an accommodation (supplier only)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Accommodation updated successfully"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions"),
            @APIResponse(responseCode = "404", description = "Accommodation not found")
    })
    public Response updateAccommodation(
            @Context SecurityContext securityContext,
            @PathParam("accommodationId") UUID accommodationId,
            @Valid CreateAccommodationRequest request) {
        try {
            String supplierId = securityContext.getUserPrincipal().getName();
            log.info("Update accommodation request: {} by supplier: {}", accommodationId, supplierId);

            AccommodationResponse accommodation = accommodationService.updateAccommodation(
                    UUID.fromString(supplierId), accommodationId, request);

            return Response.ok()
                    .entity(new SuccessResponse<>(accommodation, "Accommodation updated successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Accommodation update failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error updating accommodation", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Delete accommodation (supplier only).
     *
     * @param securityContext The security context
     * @param accommodationId The accommodation ID
     * @return Success response
     */
    @DELETE
    @Path("/{accommodationId}")
    @Authenticated
    @RolesAllowed("SUPPLIER_SUBSCRIBER")
    @Operation(summary = "Delete accommodation", description = "Delete an accommodation (supplier only)")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Accommodation deleted successfully"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions"),
            @APIResponse(responseCode = "404", description = "Accommodation not found")
    })
    public Response deleteAccommodation(
            @Context SecurityContext securityContext,
            @PathParam("accommodationId") UUID accommodationId) {
        try {
            String supplierId = securityContext.getUserPrincipal().getName();
            log.info("Delete accommodation request: {} by supplier: {}", accommodationId, supplierId);

            accommodationService.deleteAccommodation(UUID.fromString(supplierId), accommodationId);

            return Response.noContent().build();

        } catch (IllegalArgumentException e) {
            log.error("Accommodation deletion failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("DELETION_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error deleting accommodation", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get supplier's accommodations.
     *
     * @param securityContext The security context
     * @param page            The page number
     * @param pageSize        The page size
     * @return Paginated list of accommodations
     */
    @GET
    @Path("/my-accommodations")
    @Authenticated
    @RolesAllowed("SUPPLIER_SUBSCRIBER")
    @Operation(summary = "Get my accommodations", description = "Get current supplier's accommodations")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Accommodations retrieved successfully"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getMyAccommodations(
            @Context SecurityContext securityContext,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            String supplierId = securityContext.getUserPrincipal().getName();
            log.info("Get my accommodations request for supplier: {}", supplierId);

            PageResponse<AccommodationResponse> accommodations = accommodationService
                    .getAccommodationsBySupplier(UUID.fromString(supplierId), page, pageSize);

            return Response.ok()
                    .entity(accommodations)
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error getting my accommodations", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Upload accommodation image.
     *
     * @param securityContext The security context
     * @param accommodationId The accommodation ID
     * @param imageUrl        The image URL
     * @param caption         The image caption
     * @param isPrimary       Whether this is the primary image
     * @return Success response
     */
    @POST
    @Path("/{accommodationId}/images")
    @Authenticated
    @RolesAllowed("SUPPLIER_SUBSCRIBER")
    @Operation(summary = "Upload accommodation image", description = "Upload an image for accommodation")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Image uploaded successfully"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions"),
            @APIResponse(responseCode = "404", description = "Accommodation not found")
    })
    public Response uploadImage(
            @Context SecurityContext securityContext,
            @PathParam("accommodationId") UUID accommodationId,
            @FormParam("imageUrl") String imageUrl,
            @FormParam("caption") String caption,
            @FormParam("isPrimary") @DefaultValue("false") boolean isPrimary) {
        try {
            String supplierId = securityContext.getUserPrincipal().getName();
            log.info("Upload image request for accommodation: {} by supplier: {}", accommodationId, supplierId);

            accommodationService.addImage(UUID.fromString(supplierId), accommodationId, imageUrl, caption, isPrimary);

            return Response.status(Response.Status.CREATED)
                    .entity(new SuccessResponse<>(null, "Image uploaded successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Image upload failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("UPLOAD_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error uploading image", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Remove accommodation image.
     *
     * @param securityContext The security context
     * @param accommodationId The accommodation ID
     * @param imageId         The image ID
     * @return Success response
     */
    @DELETE
    @Path("/{accommodationId}/images/{imageId}")
    @Authenticated
    @RolesAllowed("SUPPLIER_SUBSCRIBER")
    @Operation(summary = "Remove accommodation image", description = "Remove an image from accommodation")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Image removed successfully"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions"),
            @APIResponse(responseCode = "404", description = "Accommodation or image not found")
    })
    public Response removeImage(
            @Context SecurityContext securityContext,
            @PathParam("accommodationId") UUID accommodationId,
            @PathParam("imageId") UUID imageId) {
        try {
            String supplierId = securityContext.getUserPrincipal().getName();
            log.info("Remove image request: {} from accommodation: {} by supplier: {}", imageId, accommodationId,
                    supplierId);

            accommodationService.removeImage(UUID.fromString(supplierId), accommodationId, imageId);

            return Response.noContent().build();

        } catch (IllegalArgumentException e) {
            log.error("Image removal failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("REMOVAL_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error removing image", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Update accommodation availability.
     *
     * @param securityContext The security context
     * @param accommodationId The accommodation ID
     * @param date            The date
     * @param isAvailable     Whether available
     * @param priceOverride   The price override
     * @return Success response
     */
    @PUT
    @Path("/{accommodationId}/availability")
    @Authenticated
    @RolesAllowed("SUPPLIER_SUBSCRIBER")
    @Operation(summary = "Update availability", description = "Update accommodation availability for a date")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Availability updated successfully"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions"),
            @APIResponse(responseCode = "404", description = "Accommodation not found")
    })
    public Response updateAvailability(
            @Context SecurityContext securityContext,
            @PathParam("accommodationId") UUID accommodationId,
            @FormParam("date") LocalDate date,
            @FormParam("isAvailable") boolean isAvailable,
            @FormParam("priceOverride") Double priceOverride) {
        try {
            String supplierId = securityContext.getUserPrincipal().getName();
            log.info("Update availability request for accommodation: {} on date: {}", accommodationId, date);

            accommodationService.updateAvailability(UUID.fromString(supplierId), accommodationId, date, isAvailable,
                    priceOverride);

            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Availability updated successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Availability update failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("UPDATE_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error updating availability", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get accommodation analytics (supplier only).
     *
     * @param securityContext The security context
     * @param accommodationId The accommodation ID
     * @return Analytics response
     */
    @GET
    @Path("/{accommodationId}/analytics")
    @Authenticated
    @RolesAllowed("SUPPLIER_SUBSCRIBER")
    @Operation(summary = "Get accommodation analytics", description = "Get analytics for accommodation (supplier only)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Analytics retrieved successfully"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions"),
            @APIResponse(responseCode = "404", description = "Accommodation not found")
    })
    public Response getAnalytics(
            @Context SecurityContext securityContext,
            @PathParam("accommodationId") UUID accommodationId) {
        try {
            String supplierId = securityContext.getUserPrincipal().getName();
            log.info("Get analytics request for accommodation: {} by supplier: {}", accommodationId, supplierId);

            var analytics = accommodationService.getAccommodationAnalytics(UUID.fromString(supplierId),
                    accommodationId);

            return Response.ok()
                    .entity(new SuccessResponse<>(analytics, "Analytics retrieved successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Analytics retrieval failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("ACCOMMODATION_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error getting analytics", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }
}
