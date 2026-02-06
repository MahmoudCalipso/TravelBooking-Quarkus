package com.travelplatform.interfaces.rest.controller.website;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Supplier dashboard and management endpoints placeholder.
 */
@Tag(name = "Website - Supplier", description = "Supplier-specific website endpoints")
@Path("/api/v1/website/suppliers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized(roles = { UserRole.SUPPLIER_SUBSCRIBER })
public class WebSupplierController {
    // TODO: Implement supplier dashboards, analytics, accommodations, payouts, verification, messaging.
}
