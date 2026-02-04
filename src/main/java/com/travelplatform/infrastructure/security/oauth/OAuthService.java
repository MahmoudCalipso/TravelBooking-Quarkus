package com.travelplatform.infrastructure.security.oauth;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * OAuth Authentication Service.
 *
 * NOTE: OAuth client integration is not implemented in this module.
 * This service currently provides minimal wiring for controller compilation.
 */
@ApplicationScoped
public class OAuthService {

    private static final Logger log = LoggerFactory.getLogger(OAuthService.class);

    @Inject
    UserRepository userRepository;

    @ConfigProperty(name = "app.base.url", defaultValue = "http://localhost:8080")
    String appBaseUrl;

    /**
     * Get OAuth authorization URL for the specified provider.
     */
    public String getAuthorizationUrl(OAuthProvider provider, UserRole role, String state) {
        log.info("Generating authorization URL for provider: {}, role: {}", provider, role);
        if (role != UserRole.TRAVELER && role != UserRole.SUPPLIER_SUBSCRIBER) {
            throw new IllegalArgumentException(
                "OAuth authentication is only available for TRAVELER and SUPPLIER_SUBSCRIBER roles");
        }
        return appBaseUrl + "/api/v1/auth/oauth/" + provider.getProviderId()
            + "?state=" + state + "&role=" + role.name();
    }

    /**
     * Handle OAuth callback and authenticate user.
     *
     * NOTE: Actual token exchange and userinfo resolution is not implemented.
     */
    public AuthResponse handleOAuthCallback(OAuthProvider provider, String authorizationCode, UserRole role) {
        throw new UnsupportedOperationException(
            "OAuth callback logic is not implemented for provider: " + provider);
    }

    /**
     * Get OAuth provider configuration info.
     */
    public OAuthProviderInfo getProviderInfo(OAuthProvider provider) {
        OAuthProviderInfo info = new OAuthProviderInfo();
        info.setProviderId(provider.getProviderId());
        info.setDisplayName(provider.getDisplayName());
        info.setDiscoveryUrl(provider.getDiscoveryUrl());
        return info;
    }

    /**
     * Validate OAuth state parameter.
     */
    public boolean validateState(String state, String expectedState) {
        return state != null && state.equals(expectedState);
    }

    /**
     * Generate a secure state parameter for OAuth flow.
     */
    public String generateState() {
        return UUID.randomUUID().toString();
    }

    /**
     * OAuth provider information DTO.
     */
    public static class OAuthProviderInfo {
        private String providerId;
        private String displayName;
        private String discoveryUrl;
        private String authorizationEndpoint;
        private String tokenEndpoint;
        private String userInfoEndpoint;

        public String getProviderId() {
            return providerId;
        }

        public void setProviderId(String providerId) {
            this.providerId = providerId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDiscoveryUrl() {
            return discoveryUrl;
        }

        public void setDiscoveryUrl(String discoveryUrl) {
            this.discoveryUrl = discoveryUrl;
        }

        public String getAuthorizationEndpoint() {
            return authorizationEndpoint;
        }

        public void setAuthorizationEndpoint(String authorizationEndpoint) {
            this.authorizationEndpoint = authorizationEndpoint;
        }

        public String getTokenEndpoint() {
            return tokenEndpoint;
        }

        public void setTokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
        }

        public String getUserInfoEndpoint() {
            return userInfoEndpoint;
        }

        public void setUserInfoEndpoint(String userInfoEndpoint) {
            this.userInfoEndpoint = userInfoEndpoint;
        }
    }

    /**
     * Authentication response DTO.
     */
    public static class AuthResponse {
        private String token;
        private String tokenType;
        private long expiresIn;
        private UserResponse user;
        private boolean isNewUser;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public long getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
        }

        public UserResponse getUser() {
            return user;
        }

        public void setUser(UserResponse user) {
            this.user = user;
        }

        public boolean isNewUser() {
            return isNewUser;
        }

        public void setNewUser(boolean isNewUser) {
            this.isNewUser = isNewUser;
        }
    }

    /**
     * User response DTO.
     */
    public static class UserResponse {
        private UUID id;
        private String email;
        private UserRole role;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public UserRole getRole() {
            return role;
        }

        public void setRole(UserRole role) {
            this.role = role;
        }
    }
}
