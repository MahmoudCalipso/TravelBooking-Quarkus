package com.travelplatform.infrastructure.security.oauth;

/**
 * Enum representing supported OAuth providers.
 * 
 * This enum defines the OAuth 2.0/OpenID Connect providers
 * that can be used for authentication on the platform.
 */
public enum OAuthProvider {

    /**
     * Google OAuth 2.0 provider
     * Uses OpenID Connect for user authentication
     */
    GOOGLE("google", "Google", "https://accounts.google.com/.well-known/openid-configuration"),

    /**
     * Microsoft Azure AD (Outlook) OAuth 2.0 provider
     * Uses OpenID Connect for user authentication
     */
    MICROSOFT("microsoft", "Microsoft", "https://login.microsoftonline.com/common/v2.0/.well-known/openid-configuration"),

    /**
     * Apple Sign In OAuth 2.0 provider
     * Uses OpenID Connect for user authentication
     */
    APPLE("apple", "Apple", "https://appleid.apple.com/.well-known/openid-configuration");

    private final String providerId;
    private final String displayName;
    private final String discoveryUrl;

    OAuthProvider(String providerId, String displayName, String discoveryUrl) {
        this.providerId = providerId;
        this.displayName = displayName;
        this.discoveryUrl = discoveryUrl;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDiscoveryUrl() {
        return discoveryUrl;
    }

    /**
     * Find OAuth provider by provider ID.
     * 
     * @param providerId The provider ID (e.g., "google", "microsoft", "apple")
     * @return The OAuth provider enum value
     * @throws IllegalArgumentException if provider ID is not recognized
     */
    public static OAuthProvider fromProviderId(String providerId) {
        for (OAuthProvider provider : values()) {
            if (provider.providerId.equalsIgnoreCase(providerId)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown OAuth provider: " + providerId);
    }

    /**
     * Check if the provider ID is valid.
     * 
     * @param providerId The provider ID to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidProvider(String providerId) {
        for (OAuthProvider provider : values()) {
            if (provider.providerId.equalsIgnoreCase(providerId)) {
                return true;
            }
        }
        return false;
    }
}
