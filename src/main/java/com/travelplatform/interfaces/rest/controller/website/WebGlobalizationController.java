package com.travelplatform.interfaces.rest.controller.website;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Website globalization endpoints (placeholder).
 */
@Tag(name = "Website - Globalization", description = "Website endpoints for localization and currency settings")
@Path("/api/v1/website/globalization")
@Produces(MediaType.APPLICATION_JSON)
@Authorized(roles = { UserRole.TRAVELER, UserRole.SUPPLIER_SUBSCRIBER, UserRole.ASSOCIATION_MANAGER })
public class WebGlobalizationController {
    // TODO: Implement website localization/currency endpoints
}
