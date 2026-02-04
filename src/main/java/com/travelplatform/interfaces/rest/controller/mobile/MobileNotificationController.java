package com.travelplatform.interfaces.rest.controller.mobile;

import com.travelplatform.interfaces.rest.controller.NotificationController;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Mobile notification endpoints.
 */
@Path("/api/v1/mobile/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"SUPPLIER_SUBSCRIBER", "TRAVELER", "ASSOCIATION_MANAGER"})
public class MobileNotificationController extends NotificationController {
}
