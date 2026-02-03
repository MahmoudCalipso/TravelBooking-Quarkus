package com.travelplatform.infrastructure.security.authorization;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.model.user.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Role-Based Access Control (RBAC) for authorization.
 * This class provides methods to check user permissions based on roles.
 */
@ApplicationScoped
public class RoleBasedAccessControl {

    @Inject
    SecurityContext securityContext;

    /**
     * Check if the current user has the specified role.
     *
     * @param role Role to check
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(UserRole role) {
        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {
            return false;
        }

        if (principal instanceof JsonWebToken) {
            JsonWebToken jwt = (JsonWebToken) principal;
            Set<String> groups = jwt.getGroups();
            return groups != null && groups.contains(role.name());
        }

        return false;
    }

    /**
     * Check if the current user has any of the specified roles.
     *
     * @param roles Roles to check
     * @return true if user has any of the roles, false otherwise
     */
    public boolean hasAnyRole(UserRole... roles) {
        return Arrays.stream(roles).anyMatch(this::hasRole);
    }

    /**
     * Check if the current user has all of the specified roles.
     *
     * @param roles Roles to check
     * @return true if user has all of the roles, false otherwise
     */
    public boolean hasAllRoles(UserRole... roles) {
        return Arrays.stream(roles).allMatch(this::hasRole);
    }

    /**
     * Check if the current user is a SUPER_ADMIN.
     *
     * @return true if user is SUPER_ADMIN, false otherwise
     */
    public boolean isSuperAdmin() {
        return hasRole(UserRole.SUPER_ADMIN);
    }

    /**
     * Check if the current user is a TRAVELER.
     *
     * @return true if user is TRAVELER, false otherwise
     */
    public boolean isTraveler() {
        return hasRole(UserRole.TRAVELER);
    }

    /**
     * Check if the current user is a SUPPLIER_SUBSCRIBER.
     *
     * @return true if user is SUPPLIER_SUBSCRIBER, false otherwise
     */
    public boolean isSupplierSubscriber() {
        return hasRole(UserRole.SUPPLIER_SUBSCRIBER);
    }

    /**
     * Check if the current user is an ASSOCIATION_MANAGER.
     *
     * @return true if user is ASSOCIATION_MANAGER, false otherwise
     */
    public boolean isAssociationManager() {
        return hasRole(UserRole.ASSOCIATION_MANAGER);
    }

    /**
     * Check if the current user is authenticated.
     *
     * @return true if user is authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return securityContext.getUserPrincipal() != null;
    }

    /**
     * Get the current user ID.
     *
     * @return User ID or empty if not authenticated
     */
    public Optional<UUID> getCurrentUserId() {
        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {
            return Optional.empty();
        }

        if (principal instanceof JsonWebToken) {
            JsonWebToken jwt = (JsonWebToken) principal;
            String subject = jwt.getSubject();
            try {
                return Optional.of(UUID.fromString(subject));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    /**
     * Get the current user email.
     *
     * @return User email or empty if not available
     */
    public Optional<String> getCurrentUserEmail() {
        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {
            return Optional.empty();
        }

        if (principal instanceof JsonWebToken) {
            JsonWebToken jwt = (JsonWebToken) principal;
            return Optional.ofNullable(jwt.getClaim("email"));
        }

        return Optional.empty();
    }

    /**
     * Get the current user role.
     *
     * @return User role or empty if not available
     */
    public Optional<UserRole> getCurrentUserRole() {
        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {
            return Optional.empty();
        }

        if (principal instanceof JsonWebToken) {
            JsonWebToken jwt = (JsonWebToken) principal;
            String roleClaim = jwt.getClaim("role");
            try {
                return Optional.of(UserRole.valueOf(roleClaim));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    /**
     * Check if the current user can access the specified user's resource.
     * Users can access their own resources, and SUPER_ADMIN can access all resources.
     *
     * @param resourceOwnerId ID of the resource owner
     * @return true if user can access the resource, false otherwise
     */
    public boolean canAccessResource(UUID resourceOwnerId) {
        if (isSuperAdmin()) {
            return true;
        }

        Optional<UUID> currentUserId = getCurrentUserId();
        return currentUserId.isPresent() && currentUserId.get().equals(resourceOwnerId);
    }

    /**
     * Check if the current user can create accommodations.
     * Only SUPPLIER_SUBSCRIBER and SUPER_ADMIN can create accommodations.
     *
     * @return true if user can create accommodations, false otherwise
     */
    public boolean canCreateAccommodations() {
        return hasAnyRole(UserRole.SUPPLIER_SUBSCRIBER, UserRole.SUPER_ADMIN);
    }

    /**
     * Check if the current user can approve content.
     * Only SUPER_ADMIN can approve content.
     *
     * @return true if user can approve content, false otherwise
     */
    public boolean canApproveContent() {
        return isSuperAdmin();
    }

    /**
     * Check if the current user can moderate content.
     * Only SUPER_ADMIN can moderate content.
     *
     * @return true if user can moderate content, false otherwise
     */
    public boolean canModerateContent() {
        return isSuperAdmin();
    }

    /**
     * Check if the current user can create events.
     * Only ASSOCIATION_MANAGER and SUPER_ADMIN can create events.
     *
     * @return true if user can create events, false otherwise
     */
    public boolean canCreateEvents() {
        return hasAnyRole(UserRole.ASSOCIATION_MANAGER, UserRole.SUPER_ADMIN);
    }

    /**
     * Check if the current user can create bookings.
     * All authenticated users can create bookings.
     *
     * @return true if user can create bookings, false otherwise
     */
    public boolean canCreateBookings() {
        return isAuthenticated();
    }

    /**
     * Check if the current user can create reviews.
     * All authenticated users can create reviews.
     *
     * @return true if user can create reviews, false otherwise
     */
    public boolean canCreateReviews() {
        return isAuthenticated();
    }

    /**
     * Check if the current user can create reels.
     * All authenticated users can create reels.
     *
     * @return true if user can create reels, false otherwise
     */
    public boolean canCreateReels() {
        return isAuthenticated();
    }

    /**
     * Check if the current user can access admin endpoints.
     * Only SUPER_ADMIN can access admin endpoints.
     *
     * @return true if user can access admin endpoints, false otherwise
     */
    public boolean canAccessAdminEndpoints() {
        return isSuperAdmin();
    }

    /**
     * Check if the current user can access supplier endpoints.
     * Only SUPPLIER_SUBSCRIBER and SUPER_ADMIN can access supplier endpoints.
     *
     * @return true if user can access supplier endpoints, false otherwise
     */
    public boolean canAccessSupplierEndpoints() {
        return hasAnyRole(UserRole.SUPPLIER_SUBSCRIBER, UserRole.SUPER_ADMIN);
    }

    /**
     * Check if the current user can access association endpoints.
     * Only ASSOCIATION_MANAGER and SUPER_ADMIN can access association endpoints.
     *
     * @return true if user can access association endpoints, false otherwise
     */
    public boolean canAccessAssociationEndpoints() {
        return hasAnyRole(UserRole.ASSOCIATION_MANAGER, UserRole.SUPER_ADMIN);
    }

    /**
     * Check if the current user can manage users.
     * Only SUPER_ADMIN can manage users.
     *
     * @return true if user can manage users, false otherwise
     */
    public boolean canManageUsers() {
        return isSuperAdmin();
    }

    /**
     * Check if the current user can view analytics.
     * SUPER_ADMIN can view all analytics, SUPPLIER_SUBSCRIBER can view their own analytics.
     *
     * @return true if user can view analytics, false otherwise
     */
    public boolean canViewAnalytics() {
        return hasAnyRole(UserRole.SUPER_ADMIN, UserRole.SUPPLIER_SUBSCRIBER);
    }

    /**
     * Check if the current user can delete content.
     * SUPER_ADMIN can delete any content, other users can only delete their own content.
     *
     * @param contentOwnerId ID of the content owner
     * @return true if user can delete the content, false otherwise
     */
    public boolean canDeleteContent(UUID contentOwnerId) {
        return canAccessResource(contentOwnerId);
    }

    /**
     * Check if the current user can edit content.
     * SUPER_ADMIN can edit any content, other users can only edit their own content.
     *
     * @param contentOwnerId ID of the content owner
     * @return true if user can edit the content, false otherwise
     */
    public boolean canEditContent(UUID contentOwnerId) {
        return canAccessResource(contentOwnerId);
    }

    /**
     * Get all roles that can access the specified endpoint.
     *
     * @param endpoint Endpoint path
     * @return List of roles that can access the endpoint
     */
    public List<UserRole> getRolesForEndpoint(String endpoint) {
        if (endpoint == null || endpoint.isEmpty()) {
            return List.of();
        }

        // Admin endpoints
        if (endpoint.startsWith("/api/v1/admin/")) {
            return List.of(UserRole.SUPER_ADMIN);
        }

        // Supplier endpoints
        if (endpoint.startsWith("/api/v1/supplier/")) {
            return List.of(UserRole.SUPPLIER_SUBSCRIBER, UserRole.SUPER_ADMIN);
        }

        // Accommodation creation
        if (endpoint.equals("/api/v1/accommodations") && 
            (endpoint.contains("POST") || endpoint.contains("PUT") || endpoint.contains("DELETE"))) {
            return List.of(UserRole.SUPPLIER_SUBSCRIBER, UserRole.SUPER_ADMIN);
        }

        // Event creation
        if (endpoint.equals("/api/v1/events") && 
            (endpoint.contains("POST") || endpoint.contains("PUT") || endpoint.contains("DELETE"))) {
            return List.of(UserRole.ASSOCIATION_MANAGER, UserRole.SUPER_ADMIN);
        }

        // All authenticated users can access these endpoints
        if (endpoint.startsWith("/api/v1/bookings") ||
            endpoint.startsWith("/api/v1/reviews") ||
            endpoint.startsWith("/api/v1/reels") ||
            endpoint.startsWith("/api/v1/chat") ||
            endpoint.startsWith("/api/v1/notifications") ||
            endpoint.startsWith("/api/v1/users/profile")) {
            return List.of(UserRole.values());
        }

        // Public endpoints
        if (endpoint.startsWith("/api/v1/auth/") ||
            endpoint.startsWith("/api/v1/accommodations") ||
            endpoint.startsWith("/api/v1/reels/feed") ||
            endpoint.startsWith("/api/v1/search")) {
            return List.of(UserRole.values());
        }

        return List.of();
    }
}
