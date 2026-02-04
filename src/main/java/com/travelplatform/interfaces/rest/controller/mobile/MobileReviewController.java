package com.travelplatform.interfaces.rest.controller.mobile;

import com.travelplatform.interfaces.rest.controller.ReviewController;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Mobile review endpoints.
 */
@Path("/api/v1/mobile/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"SUPPLIER_SUBSCRIBER", "TRAVELER", "ASSOCIATION_MANAGER"})
public class MobileReviewController extends ReviewController {
}
