package com.travelplatform.interfaces.rest.controller.website;

import com.travelplatform.application.dto.request.user.ChangePasswordRequest;
import com.travelplatform.application.dto.request.user.LoginRequest;
import com.travelplatform.application.dto.request.user.RegisterUserRequest;
import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.dto.response.user.AuthResponse;
import com.travelplatform.application.service.user.AuthenticationService;
import com.travelplatform.application.service.user.UserService;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import com.travelplatform.infrastructure.security.jwt.JwtTokenProvider;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * REST controller for authentication operations.
 * Handles user registration, login, logout, token refresh, and password reset.
 */
@Path("/api/v1/website/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Website - Authentication", description = "Website authentication endpoints")
@Authorized(allowAnonymous = true)
public class WebAuthController {

    private static final Logger log = LoggerFactory.getLogger(WebAuthController.class);

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private UserService userService;

    @Inject
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user.
     *
     * @param request The registration request
     * @return AuthResponse with JWT token
     */
    @POST
    @Path("/register")
    @Authorized(allowAnonymous = true)
    @Operation(summary = "Register new user", description = "Register a new user account with email and password")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "User registered successfully"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "409", description = "Email already exists")
    })
    public Response registerUser(@Valid RegisterUserRequest request) {
        try {
            log.info("Registration request for email: {}", request.getEmail());

            AuthResponse authResponse = authenticationService.registerUser(request);

            return Response.status(Response.Status.CREATED)
                    .entity(new SuccessResponse<>(authResponse, "User registered successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Registration failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * User login.
     *
     * @param request The login request
     * @return AuthResponse with JWT token
     */
    @POST
    @Path("/login")
    @Authorized(allowAnonymous = true)
    @Operation(summary = "User login", description = "Authenticate user with email and password")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Login successful"),
            @APIResponse(responseCode = "401", description = "Invalid credentials"),
            @APIResponse(responseCode = "403", description = "Account suspended or deleted")
    })
    public Response login(@Valid LoginRequest request) {
        try {
            log.info("Login request for email: {}", request.getEmail());

            AuthResponse authResponse = authenticationService.login(request);

            return Response.ok()
                    .entity(new SuccessResponse<>(authResponse, "Login successful"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Login failed: {}", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("AUTHENTICATION_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * User logout.
     *
     * @param securityContext The security context
     * @return Success response
     */
    @POST
    @Path("/logout")
    @Authorized
    @Operation(summary = "User logout", description = "Logout the current user and invalidate token")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Logout successful"),
            @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response logout(@Context SecurityContext securityContext) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Logout request for user: {}", userId);

            authenticationService.logout(UUID.fromString(userId));

            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Logout successful"))
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error during logout", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Refresh JWT token.
     *
     * @param securityContext The security context
     * @return New AuthResponse with refreshed token
     */
    @POST
    @Path("/refresh")
    @Authorized
    @Operation(summary = "Refresh JWT token", description = "Refresh the current JWT token")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Token refreshed successfully"),
            @APIResponse(responseCode = "401", description = "Invalid or expired token")
    })
    public Response refreshToken(@Context SecurityContext securityContext) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Token refresh request for user: {}", userId);

            AuthResponse authResponse = authenticationService.refreshToken(UUID.fromString(userId));

            return Response.ok()
                    .entity(new SuccessResponse<>(authResponse, "Token refreshed successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("TOKEN_REFRESH_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during token refresh", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Request password reset.
     *
     * @param email The user email
     * @return Success response
     */
    @POST
    @Path("/forgot-password")
    @Authorized(allowAnonymous = true)
    @Operation(summary = "Request password reset", description = "Send password reset email to user")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Password reset email sent"),
            @APIResponse(responseCode = "404", description = "User not found")
    })
    public Response forgotPassword(@QueryParam("email") String email) {
        try {
            log.info("Password reset request for email: {}", email);

            authenticationService.requestPasswordReset(email);

            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Password reset email sent"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Password reset request failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("USER_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during password reset request", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Reset password with token.
     *
     * @param token   The reset token
     * @param request The password reset request
     * @return Success response
     */
    @POST
    @Path("/reset-password")
    @Authorized(allowAnonymous = true)
    @Operation(summary = "Reset password", description = "Reset password using reset token")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Password reset successful"),
            @APIResponse(responseCode = "400", description = "Invalid or expired token")
    })
    public Response resetPassword(@QueryParam("token") String token, @Valid ChangePasswordRequest request) {
        try {
            log.info("Password reset with token");

            authenticationService.resetPassword(token, request.getNewPassword());

            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Password reset successful"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Password reset failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("RESET_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during password reset", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Verify email address.
     *
     * @param token The verification token
     * @return Success response
     */
    @POST
    @Path("/verify-email")
    @Authorized(allowAnonymous = true)
    @Operation(summary = "Verify email", description = "Verify user email address")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Email verified successfully"),
            @APIResponse(responseCode = "400", description = "Invalid or expired token")
    })
    public Response verifyEmail(@QueryParam("token") String token) {
        try {
            log.info("Email verification request");

            authenticationService.verifyEmail(token);

            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Email verified successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Email verification failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("VERIFICATION_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during email verification", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Change password (authenticated).
     *
     * @param securityContext The security context
     * @param request         The password change request
     * @return Success response
     */
    @POST
    @Path("/change-password")
    @Authorized
    @Operation(summary = "Change password", description = "Change user password (requires authentication)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Password changed successfully"),
            @APIResponse(responseCode = "400", description = "Invalid current password"),
            @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response changePassword(@Context SecurityContext securityContext, @Valid ChangePasswordRequest request) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Password change request for user: {}", userId);

            authenticationService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());

            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Password changed successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Password change failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("PASSWORD_CHANGE_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during password change", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Validate JWT token.
     *
     * @param token The JWT token
     * @return Success response
     */
    @GET
    @Path("/validate")
    @Authorized(allowAnonymous = true)
    @Operation(summary = "Validate token", description = "Validate JWT token")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Token is valid"),
            @APIResponse(responseCode = "401", description = "Token is invalid or expired")
    })
    public Response validateToken(@HeaderParam("Authorization") String token) {
        try {
            log.info("Token validation request");

            boolean isValid = jwtTokenProvider.validateToken(token);

            if (isValid) {
                return Response.ok()
                        .entity(new SuccessResponse<>(true, "Token is valid"))
                        .build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("INVALID_TOKEN", "Token is invalid or expired"))
                        .build();
            }

        } catch (Exception e) {
            log.error("Unexpected error during token validation", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get current authenticated user info.
     *
     * @param securityContext The security context
     * @return User response
     */
    @GET
    @Path("/me")
    @Authorized
    @Operation(summary = "Get current user", description = "Get current authenticated user information")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "User information retrieved"),
            @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response getCurrentUser(@Context SecurityContext securityContext) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Get current user request: {}", userId);

            var userResponse = userService.getUserById(UUID.fromString(userId));

            return Response.ok()
                    .entity(new SuccessResponse<>(userResponse, "User information retrieved"))
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error getting current user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }
}
