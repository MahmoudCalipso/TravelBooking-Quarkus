package com.travelplatform.application.dto.request.user;

import jakarta.validation.constraints.Pattern;

import java.util.List;

/**
 * DTO for updating user preferences request.
 */
public class UpdatePreferencesRequest {

    private List<String> preferredDestinations;

    @Pattern(regexp = "BUDGET|MODERATE|LUXURY", message = "Invalid budget range")
    private String budgetRange;

    @Pattern(regexp = "ADVENTURE|CULTURAL|RELAXATION|BUSINESS|ROMANTIC|FAMILY|SOLO|FOODIE|NATURE|URBAN",
            message = "Invalid travel style")
    private String travelStyle;

    private List<String> interests;

    private Boolean emailNotifications;

    private Boolean pushNotifications;

    private Boolean smsNotifications;

    // Getters and Setters

    public List<String> getPreferredDestinations() {
        return preferredDestinations;
    }

    public void setPreferredDestinations(List<String> preferredDestinations) {
        this.preferredDestinations = preferredDestinations;
    }

    public String getBudgetRange() {
        return budgetRange;
    }

    public void setBudgetRange(String budgetRange) {
        this.budgetRange = budgetRange;
    }

    public String getTravelStyle() {
        return travelStyle;
    }

    public void setTravelStyle(String travelStyle) {
        this.travelStyle = travelStyle;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public Boolean getEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public Boolean getPushNotifications() {
        return pushNotifications;
    }

    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public Boolean getSmsNotifications() {
        return smsNotifications;
    }

    public void setSmsNotifications(Boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }
}
