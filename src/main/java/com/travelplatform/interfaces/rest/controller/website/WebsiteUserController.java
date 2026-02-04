package com.travelplatform.interfaces.rest.controller.website;

import com.travelplatform.interfaces.rest.controller.UserController;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Website booking user endpoints.
 */
@Path("/api/v1/website-booking/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"SUPPLIER_SUBSCRIBER", "TRAVELER", "ASSOCIATION_MANAGER"})
public class WebsiteUserController extends UserController {
}
