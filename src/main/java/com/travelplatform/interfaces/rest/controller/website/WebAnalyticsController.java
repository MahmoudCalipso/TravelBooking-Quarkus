package com.travelplatform.interfaces.rest.controller.website;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Cross-role analytics endpoints placeholder.
 */
@Tag(name = "Website - Analytics", description = "Role-specific dashboards and exports")
@Path("/api/v1/website/analytics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized(roles = { UserRole.TRAVELER, UserRole.SUPPLIER_SUBSCRIBER, UserRole.ASSOCIATION_MANAGER })
public class WebAnalyticsController {
    // TODO: Implement dashboards and data export endpoints per specification.
}
