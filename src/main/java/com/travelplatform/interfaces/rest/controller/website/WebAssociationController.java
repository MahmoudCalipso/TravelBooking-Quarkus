package com.travelplatform.interfaces.rest.controller.website;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Association manager endpoints placeholder.
 */
@Tag(name = "Website - Association", description = "Association manager website endpoints")
@Path("/api/v1/website/associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized(roles = { UserRole.ASSOCIATION_MANAGER })
public class WebAssociationController {
    // TODO: Implement association dashboards, events, programs, payouts, members, compliance.
}
