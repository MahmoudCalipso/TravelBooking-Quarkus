package com.travelplatform.interfaces.rest.controller.mobile;

import com.travelplatform.interfaces.rest.controller.UserController;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Mobile user endpoints.
 */
@Path("/api/v1/mobile/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"SUPPLIER_SUBSCRIBER", "TRAVELER", "ASSOCIATION_MANAGER"})
public class MobileUserController extends UserController {
}
