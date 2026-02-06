package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.request.user.LoginRequest;
import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.dto.response.user.AuthResponse;
import com.travelplatform.application.service.user.AuthenticationService;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Admin-only authentication controller to isolate SUPER_ADMIN login flows.
 */
@Path("/api/v1/admin/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Authentication", description = "Protected SUPER_ADMIN authentication endpoints")
public class AdminAuthController {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthController.class);

    @Inject
    AuthenticationService authenticationService;

    /**
     * SUPER_ADMIN login endpoint. Allows anonymous access for credential submission but
     * enforces that the authenticated user has the SUPER_ADMIN role before issuing a token.
     */
    @POST
    @Path("/login")
    @Authorized(allowAnonymous = true)
    @Operation(summary = "Admin login", description = "Authenticate SUPER_ADMIN users")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Login successful"),
        @APIResponse(responseCode = "401", description = "Invalid credentials"),
        @APIResponse(responseCode = "403", description = "User is not SUPER_ADMIN")
    })
    public Response adminLogin(@Valid LoginRequest request) {
        try {
            log.info("Admin login request for email: {}", request.getEmail());

            AuthResponse authResponse = authenticationService.login(request);

            if (authResponse.getUser() == null || !UserRole.SUPER_ADMIN.name().equals(authResponse.getUser().getRole())) {
                log.warn("Rejected admin login for non-super admin user: {}", request.getEmail());
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new ErrorResponse("FORBIDDEN", "User is not SUPER_ADMIN"))
                        .build();
            }

            return Response.ok()
                    .entity(new SuccessResponse<>(authResponse, "Admin login successful"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Admin login failed: {}", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("AUTHENTICATION_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during admin login", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }
}
