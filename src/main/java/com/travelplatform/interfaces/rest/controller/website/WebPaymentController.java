package com.travelplatform.interfaces.rest.controller.website;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Website payment endpoints placeholder matching the RBAC spec.
 */
@Tag(name = "Website - Payment", description = "Website payment endpoints")
@Path("/api/v1/website/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized(roles = { UserRole.TRAVELER, UserRole.SUPPLIER_SUBSCRIBER, UserRole.ASSOCIATION_MANAGER })
public class WebPaymentController {
    // TODO: Implement website payment operations per specification.
}
