package com.travelplatform.interfaces.rest.controller.website;

import com.travelplatform.interfaces.rest.controller.ReelController;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Website booking reel endpoints.
 */
@Path("/api/v1/website-booking/reels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"SUPPLIER_SUBSCRIBER", "TRAVELER", "ASSOCIATION_MANAGER"})
public class WebsiteReelController extends ReelController {
}
