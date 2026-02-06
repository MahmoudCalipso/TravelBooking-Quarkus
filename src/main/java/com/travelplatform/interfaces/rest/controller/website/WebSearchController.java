package com.travelplatform.interfaces.rest.controller.website;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Website search endpoints placeholder.
 */
@Tag(name = "Website - Search", description = "Global search across accommodations, reels, events, users")
@Path("/api/v1/website/search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized(roles = { UserRole.TRAVELER, UserRole.SUPPLIER_SUBSCRIBER, UserRole.ASSOCIATION_MANAGER })
public class WebSearchController {
    // TODO: Implement search endpoints for accommodations, reels, events, users, messages.
}
