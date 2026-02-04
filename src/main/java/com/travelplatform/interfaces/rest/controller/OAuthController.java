package com.travelplatform.interfaces.rest.controller;

import com.travelplatform.application.dto.request.oauth.OAuthLoginRequest;
import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.oauth.OAuthProvider;
import com.travelplatform.infrastructure.security.oauth.OAuthService;
import com.travelplatform.infrastructure.security.oauth.OAuthService.AuthResponse;
import com.travelplatform.infrastructure.security.oauth.OAuthService.OAuthProviderInfo;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth Authentication Controller.
 * 
 * Handles OAuth 2.0/OpenID Connect authentication for multiple providers:
 * - Google
 * - Microsoft (Azure AD / Outlook)
 * - Apple
 * 
 * Supports both sign-up (new users) and sign-in (existing users) for:
 * - TRAVELER role
 * - SUPPLIER_SUBSCRIBER role
 */
@Path("/api/v1/oauth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OAuthController {

    private static final Logger log = LoggerFactory.getLogger(OAuthController.class);

    @Inject
    OAuthService oauthService;

    /**
     * Get OAuth authorization URL for specified provider.
     * 
     * This endpoint initiates the OAuth flow by returning the authorization URL
     * that the user should be redirected to.
     * 
     * @param provider The OAuth provider (google, microsoft, apple)
     * @param role The user role (TRAVELER or SUPPLIER_SUBSCRIBER)
     * @return Response containing authorization URL
     */
    @GET
    @Path("/authorize/{provider}")
    public Response getAuthorizationUrl(
            @PathParam("provider") String provider,
            @QueryParam("role") String role,
            @QueryParam("redirect_uri") String redirectUri) {
        
        log.info("OAuth authorization request - Provider: {}, Role: {}", provider, role);

        try {
            // Validate provider
            if (!OAuthProvider.isValidProvider(provider)) {
                ErrorResponse error = new ErrorResponse("INVALID_PROVIDER",
                        "Invalid OAuth provider. Supported providers: google, microsoft, apple");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Validate role
            UserRole userRole;
            try {
                userRole = UserRole.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                ErrorResponse error = new ErrorResponse("INVALID_ROLE",
                        "Invalid role. Supported roles: TRAVELER, SUPPLIER_SUBSCRIBER");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Validate role is allowed for OAuth
            if (userRole != UserRole.TRAVELER && userRole != UserRole.SUPPLIER_SUBSCRIBER) {
                ErrorResponse error = new ErrorResponse("INVALID_ROLE_FOR_OAUTH",
                        "OAuth authentication is only available for TRAVELER and SUPPLIER_SUBSCRIBER roles");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Generate state for CSRF protection
            String state = oauthService.generateState();

            // Get authorization URL
            OAuthProvider oauthProvider = OAuthProvider.fromProviderId(provider);
            String authUrl = oauthService.getAuthorizationUrl(oauthProvider, userRole, state);

            // Build response
            Map<String, Object> data = new HashMap<>();
            data.put("authorization_url", authUrl);
            data.put("provider", provider);
            data.put("state", state);

            SuccessResponse response = new SuccessResponse();
            response.setSuccess(true);
            response.setData(data);
            response.setMessage("Authorization URL generated successfully");

            return Response.ok(response).build();

        } catch (Exception e) {
            log.error("Error generating OAuth authorization URL", e);
            ErrorResponse error = new ErrorResponse("AUTHORIZATION_ERROR",
                    "Failed to generate authorization URL: " + e.getMessage());
            return Response.serverError().entity(error).build();
        }
    }

    /**
     * Handle OAuth callback and authenticate user.
     * 
     * This endpoint handles the callback from OAuth provider after user authorization.
     * It exchanges the authorization code for tokens and authenticates the user.
     * 
     * @param request The OAuth login request containing authorization code and state
     * @return Response containing JWT token and user information
     */
    @POST
    @Path("/callback")
    public Response handleOAuthCallback(@Valid OAuthLoginRequest request) {
        log.info("OAuth callback received - Provider: {}, Role: {}", request.getProvider(), request.getRole());

        try {
            // Validate provider
            if (!OAuthProvider.isValidProvider(request.getProvider())) {
                ErrorResponse error = new ErrorResponse("INVALID_PROVIDER", "Invalid OAuth provider");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Validate role
            UserRole userRole = request.getRole();

            // Validate role is allowed for OAuth
            if (userRole != UserRole.TRAVELER && userRole != UserRole.SUPPLIER_SUBSCRIBER) {
                ErrorResponse error = new ErrorResponse("INVALID_ROLE_FOR_OAUTH",
                        "OAuth authentication is only available for TRAVELER and SUPPLIER_SUBSCRIBER roles");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Get OAuth provider
            OAuthProvider oauthProvider = OAuthProvider.fromProviderId(request.getProvider());

            // Handle OAuth callback
            AuthResponse authResponse = oauthService.handleOAuthCallback(
                    oauthProvider,
                    request.getAuthorizationCode(),
                    userRole
            );

            // Build response
            Map<String, Object> data = new HashMap<>();
            data.put("token", authResponse.getToken());
            data.put("token_type", authResponse.getTokenType());
            data.put("expires_in", authResponse.getExpiresIn());
            data.put("user", authResponse.getUser());
            data.put("is_new_user", authResponse.isNewUser());

            SuccessResponse response = new SuccessResponse();
            response.setSuccess(true);
            response.setData(data);
            response.setMessage(authResponse.isNewUser() ? 
                    "Account created successfully via OAuth" : 
                    "Logged in successfully via OAuth");

            return Response.ok(response).build();

        } catch (UnsupportedOperationException e) {
            log.error("OAuth callback not implemented", e);
            ErrorResponse error = new ErrorResponse("OAUTH_NOT_IMPLEMENTED", e.getMessage());
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(error).build();
        } catch (Exception e) {
            log.error("Unexpected error during OAuth callback", e);
            ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR",
                    "An unexpected error occurred during OAuth authentication");
            return Response.serverError().entity(error).build();
        }
    }

    /**
     * Get OAuth provider information.
     * 
     * Returns configuration details for all supported OAuth providers.
     * 
     * @return Response containing provider information
     */
    @GET
    @Path("/providers")
    public Response getProviders() {
        log.info("OAuth providers information requested");

        try {
            Map<String, OAuthProviderInfo> providers = new HashMap<>();
            
            for (OAuthProvider provider : OAuthProvider.values()) {
                OAuthProviderInfo info = oauthService.getProviderInfo(provider);
                providers.put(provider.getProviderId(), info);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("providers", providers);

            SuccessResponse response = new SuccessResponse();
            response.setSuccess(true);
            response.setData(data);
            response.setMessage("OAuth providers information retrieved successfully");

            return Response.ok(response).build();

        } catch (Exception e) {
            log.error("Error retrieving OAuth providers information", e);
            ErrorResponse error = new ErrorResponse(
                    "PROVIDERS_ERROR",
                    "Failed to retrieve OAuth providers information");
            return Response.serverError().entity(error).build();
        }
    }

    /**
     * Get OAuth provider information for specific provider.
     * 
     * @param provider The OAuth provider (google, microsoft, apple)
     * @return Response containing provider information
     */
    @GET
    @Path("/providers/{provider}")
    public Response getProviderInfo(@PathParam("provider") String provider) {
        log.info("OAuth provider information requested - Provider: {}", provider);

        try {
            // Validate provider
            if (!OAuthProvider.isValidProvider(provider)) {
                ErrorResponse error = new ErrorResponse("INVALID_PROVIDER", "Invalid OAuth provider");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(error)
                        .build();
            }

            // Get provider info
            OAuthProvider oauthProvider = OAuthProvider.fromProviderId(provider);
            OAuthProviderInfo info = oauthService.getProviderInfo(oauthProvider);

            Map<String, Object> data = new HashMap<>();
            data.put("provider", info);

            SuccessResponse response = new SuccessResponse();
            response.setSuccess(true);
            response.setData(data);
            response.setMessage("OAuth provider information retrieved successfully");

            return Response.ok(response).build();

        } catch (Exception e) {
            log.error("Error retrieving OAuth provider information", e);
            ErrorResponse error = new ErrorResponse("PROVIDER_ERROR",
                    "Failed to retrieve OAuth provider information");
            return Response.serverError().entity(error).build();
        }
    }
}
