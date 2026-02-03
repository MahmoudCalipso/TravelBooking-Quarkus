package com.travelplatform.domain.model.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a user's travel preferences and notification
 * settings.
 * Part of the User aggregate.
 */
public class UserPreferences {
    private final UUID id;
    private final UUID userId;
    private List<String> preferredDestinations;
    private BudgetRange budgetRange;
    private TravelStyle travelStyle;
    private List<String> interests;
    private boolean emailNotifications;
    private boolean pushNotifications;
    private boolean smsNotifications;
    private String notificationTypes;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Creates a new UserPreferences for a user with default values.
     *
     * @param userId user ID
     */
    public UserPreferences(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.preferredDestinations = new ArrayList<>();
        this.budgetRange = BudgetRange.MODERATE;
        this.travelStyle = TravelStyle.RELAXATION;
        this.interests = new ArrayList<>();
        this.emailNotifications = true;
        this.pushNotifications = true;
        this.smsNotifications = false;
        this.notificationTypes = "{}";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reconstructs a UserPreferences from persistence.
     */
    public UserPreferences(UUID id, UUID userId, List<String> preferredDestinations, BudgetRange budgetRange,
            TravelStyle travelStyle, List<String> interests, boolean emailNotifications,
            boolean pushNotifications, boolean smsNotifications, String notificationTypes,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.preferredDestinations = preferredDestinations != null ? new ArrayList<>(preferredDestinations)
                : new ArrayList<>();
        this.budgetRange = budgetRange;
        this.travelStyle = travelStyle;
        this.interests = interests != null ? new ArrayList<>(interests) : new ArrayList<>();
        this.emailNotifications = emailNotifications;
        this.pushNotifications = pushNotifications;
        this.smsNotifications = smsNotifications;
        this.notificationTypes = notificationTypes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public List<String> getPreferredDestinations() {
        return new ArrayList<>(preferredDestinations);
    }

    public BudgetRange getBudgetRange() {
        return budgetRange;
    }

    public TravelStyle getTravelStyle() {
        return travelStyle;
    }

    public List<String> getInterests() {
        return new ArrayList<>(interests);
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public boolean isPushNotifications() {
        return pushNotifications;
    }

    public boolean isSmsNotifications() {
        return smsNotifications;
    }

    public String getNotificationTypes() {
        return notificationTypes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Updates the preferred destinations.
     *
     * @param destinations new list of preferred destinations
     */
    public void updatePreferredDestinations(List<String> destinations) {
        this.preferredDestinations = destinations != null ? new ArrayList<>(destinations) : new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }

    public void setPreferredDestinations(List<String> destinations) {
        updatePreferredDestinations(destinations);
    }

    /**
     * Adds a preferred destination.
     *
     * @param destination destination to add
     */
    public void addPreferredDestination(String destination) {
        if (destination != null && !destination.trim().isEmpty() && !this.preferredDestinations.contains(destination)) {
            this.preferredDestinations.add(destination.trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Removes a preferred destination.
     *
     * @param destination destination to remove
     */
    public void removePreferredDestination(String destination) {
        this.preferredDestinations.remove(destination);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the budget range.
     *
     * @param budgetRange new budget range
     */
    public void updateBudgetRange(BudgetRange budgetRange) {
        this.budgetRange = budgetRange;
        this.updatedAt = LocalDateTime.now();
    }

    public void setBudgetRange(BudgetRange budgetRange) {
        updateBudgetRange(budgetRange);
    }

    /**
     * Updates the travel style.
     *
     * @param travelStyle new travel style
     */
    public void updateTravelStyle(TravelStyle travelStyle) {
        this.travelStyle = travelStyle;
        this.updatedAt = LocalDateTime.now();
    }

    public void setTravelStyle(TravelStyle travelStyle) {
        updateTravelStyle(travelStyle);
    }

    /**
     * Updates the interests.
     *
     * @param interests new list of interests
     */
    public void updateInterests(List<String> interests) {
        this.interests = interests != null ? new ArrayList<>(interests) : new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }

    public void setInterests(List<String> interests) {
        updateInterests(interests);
    }

    /**
     * Adds an interest.
     *
     * @param interest interest to add
     */
    public void addInterest(String interest) {
        if (interest != null && !interest.trim().isEmpty() && !this.interests.contains(interest)) {
            this.interests.add(interest.trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Removes an interest.
     *
     * @param interest interest to remove
     */
    public void removeInterest(String interest) {
        this.interests.remove(interest);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates email notification preference.
     *
     * @param enabled true to enable email notifications
     */
    public void setEmailNotifications(boolean enabled) {
        this.emailNotifications = enabled;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates push notification preference.
     *
     * @param enabled true to enable push notifications
     */
    public void setPushNotifications(boolean enabled) {
        this.pushNotifications = enabled;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates SMS notification preference.
     *
     * @param enabled true to enable SMS notifications
     */
    public void setSmsNotifications(boolean enabled) {
        this.smsNotifications = enabled;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates notification types configuration.
     *
     * @param notificationTypes JSON string with notification type preferences
     */
    public void updateNotificationTypes(String notificationTypes) {
        this.notificationTypes = notificationTypes;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if any notification type is enabled.
     *
     * @return true if at least one notification type is enabled
     */
    public boolean hasNotificationsEnabled() {
        return emailNotifications || pushNotifications || smsNotifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserPreferences that = (UserPreferences) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("UserPreferences{id=%s, userId=%s, budgetRange=%s, travelStyle=%s}",
                id, userId, budgetRange, travelStyle);
    }

    /**
     * Enumeration of budget ranges.
     */
    public enum BudgetRange {
        BUDGET,
        MODERATE,
        LUXURY
    }

    /**
     * Enumeration of travel styles.
     */
    public enum TravelStyle {
        ADVENTURE,
        CULTURAL,
        RELAXATION,
        BUSINESS
    }
}
