package com.travelplatform.application.dto.request.oauth;

import com.travelplatform.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * OAuth Login Request DTO.
 * 
 * Used when user completes OAuth flow and returns with authorization code.
 */
public class OAuthLoginRequest {

    @NotBlank(message = "Provider is required")
    private String provider;

    @NotBlank(message = "Authorization code is required")
    private String authorizationCode;

    @NotNull(message = "Role is required")
    private UserRole role;

    @NotBlank(message = "State is required")
    private String state;

    // Getters and Setters

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
