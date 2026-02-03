package com.travelplatform.infrastructure.security;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

/**
 * Request-scoped bean to access the current authenticated user's information.
 * This is a convenience wrapper around SecurityIdentity and JsonWebToken.
 */
@RequestScoped
public class CurrentUser {

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    JsonWebToken jwt;

    /**
     * Check if the current user is authenticated.
     *
     * @return true if authenticated
     */
    public boolean isAuthenticated() {
        return securityIdentity != null && !securityIdentity.isAnonymous();
    }

    /**
     * Get the current user's ID.
     *
     * @return User ID or null if not authenticated
     */
    public UUID getId() {
        if (!isAuthenticated()) {
            return null;
        }
        String subject = jwt.getSubject();
        if (subject != null) {
            try {
                return UUID.fromString(subject);
            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }
        return null;
    }

    /**
     * Get the current user's email.
     *
     * @return User email or null if not authenticated
     */
    public String getEmail() {
        if (!isAuthenticated()) {
            return null;
        }
        return jwt.getClaim("email");
    }

    /**
     * Get the current user's role.
     *
     * @return User role or null if not authenticated
     */
    public UserRole getRole() {
        if (!isAuthenticated()) {
            return null;
        }
        String roleStr = jwt.getClaim("role");
        if (roleStr != null) {
            try {
                return UserRole.valueOf(roleStr);
            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }
        return null;
    }

    /**
     * Get the current user's status.
     *
     * @return User status or null if not authenticated
     */
    public UserStatus getStatus() {
        if (!isAuthenticated()) {
            return null;
        }
        String statusStr = jwt.getClaim("status");
        if (statusStr != null) {
            try {
                return UserStatus.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }
        return null;
    }

    /**
     * Check if the user has a specific role.
     *
     * @param role Role to check
     * @return true if user has the role
     */
    public boolean hasRole(UserRole role) {
        return isAuthenticated() && securityIdentity.hasRole(role.name());
    }
}
