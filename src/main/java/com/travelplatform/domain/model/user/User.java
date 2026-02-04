package com.travelplatform.domain.model.user;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import com.travelplatform.domain.valueobject.ContactInfo;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a user account.
 * This is the aggregate root for the user aggregate.
 */
public class User {
    private final UUID id;
    private final String email;
    private String passwordHash;
    private final UserRole role;
    private UserStatus status;
    private boolean emailVerified;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;

    // Associated entities (part of the aggregate)
    private UserProfile profile;
    private UserPreferences preferences;

    /**
     * Creates a new User with the specified details.
     *
     * @param email        user email (must be unique)
     * @param passwordHash hashed password
     * @param role         user role
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public User(String email, String passwordHash, UserRole role) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        this.id = UUID.randomUUID();
        this.email = email.trim().toLowerCase();
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = UserStatus.ACTIVE;
        this.emailVerified = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastLoginAt = null;
    }

    /**
     * Reconstructs a User from persistence (used by repository implementations).
     *
     * @param id            user ID
     * @param email         user email
     * @param passwordHash  hashed password
     * @param role          user role
     * @param status        user status
     * @param emailVerified email verification flag
     * @param createdAt     creation timestamp
     * @param updatedAt     last update timestamp
     * @param lastLoginAt   last login timestamp
     */
    public User(UUID id, String email, String passwordHash, UserRole role, UserStatus status,
            boolean emailVerified, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLoginAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.emailVerified = emailVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLoginAt = lastLoginAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    /**
     * Sets the user profile.
     *
     * @param profile user profile
     */
    public void setProfile(UserProfile profile) {
        this.profile = profile;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Sets the user preferences.
     *
     * @param preferences user preferences
     */
    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
        this.passwordHash = passwordHash;
        this.updatedAt = LocalDateTime.now();
    }

    public void setStatus(UserStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks the user's email as verified.
     */
    public void verifyEmail() {
        this.emailVerified = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Records a successful login.
     */
    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the last login timestamp.
     * Alias for recordLogin.
     */
    public void updateLastLogin() {
        recordLogin();
    }

    /**
     * Suspends the user account.
     */
    public void suspend() {
        this.status = UserStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Activates the user account.
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks the user account as deleted.
     */
    public void markAsDeleted() {
        this.status = UserStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the user is active.
     *
     * @return true if user status is ACTIVE
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    /**
     * Checks if the user is suspended.
     *
     * @return true if user status is SUSPENDED
     */
    public boolean isSuspended() {
        return this.status == UserStatus.SUSPENDED;
    }

    /**
     * Checks if the user is deleted.
     *
     * @return true if user status is DELETED
     */
    public boolean isDeleted() {
        return this.status == UserStatus.DELETED;
    }

    /**
     * Checks if the user has the specified role.
     *
     * @param role role to check
     * @return true if user has the role
     */
    public boolean hasRole(UserRole role) {
        return this.role == role;
    }

    /**
     * Checks if the user is a SUPER_ADMIN.
     *
     * @return true if user is SUPER_ADMIN
     */
    public boolean isSuperAdmin() {
        return this.role == UserRole.SUPER_ADMIN;
    }

    /**
     * Checks if the user is a TRAVELER.
     *
     * @return true if user is TRAVELER
     */
    public boolean isTraveler() {
        return this.role == UserRole.TRAVELER;
    }

    /**
     * Checks if the user is a SUPPLIER_SUBSCRIBER.
     *
     * @return true if user is SUPPLIER_SUBSCRIBER
     */
    public boolean isSupplier() {
        return this.role == UserRole.SUPPLIER_SUBSCRIBER;
    }

    /**
     * Checks if the user is an ASSOCIATION_MANAGER.
     *
     * @return true if user is ASSOCIATION_MANAGER
     */
    public boolean isAssociationManager() {
        return this.role == UserRole.ASSOCIATION_MANAGER;
    }

    /**
     * Checks if the user can create accommodations.
     *
     * @return true if user is SUPPLIER_SUBSCRIBER
     */
    public boolean canCreateAccommodations() {
        return this.role == UserRole.SUPPLIER_SUBSCRIBER;
    }

    /**
     * Checks if the user can create travel programs.
     *
     * @return true if user is ASSOCIATION_MANAGER
     */
    public boolean canCreateTravelPrograms() {
        return this.role == UserRole.ASSOCIATION_MANAGER;
    }

    /**
     * Checks if the user can approve content.
     *
     * @return true if user is SUPER_ADMIN
     */
    public boolean canApproveContent() {
        return this.role == UserRole.SUPER_ADMIN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("User{id=%s, email='%s', role=%s, status=%s}",
                id, email, role, status);
    }
}
