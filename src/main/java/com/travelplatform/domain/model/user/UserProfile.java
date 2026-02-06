package com.travelplatform.domain.model.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a user's profile information.
 * Part of the User aggregate.
 */
public class UserProfile {
    private final UUID id;
    private final UUID userId;
    private String fullName;
    private String photoUrl;
    private LocalDate birthDate;
    private Gender gender;
    private String bio;
    private String location;
    private String phoneNumber;
    private DrivingLicenseCategory drivingLicenseCategory;
    private WorkStatus occupation;
    private String stripeConnectAccountId;
    private String bankName;
    private String bankAccountIban;
    private String bankAccountBic;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Creates a new UserProfile for a user.
     *
     * @param userId user ID
     */
    public UserProfile(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reconstructs a UserProfile from persistence.
     *
     * @param id                     profile ID
     * @param userId                 user ID
     * @param fullName               full name
     * @param photoUrl               profile photo URL
     * @param birthDate              birth date
     * @param gender                 gender
     * @param bio                    biography
     * @param location               location
     * @param phoneNumber            phone number
     * @param drivingLicenseCategory driving license category
     * @param occupation             occupation
     * @param createdAt              creation timestamp
     * @param updatedAt              last update timestamp
     */
    public UserProfile(UUID id, UUID userId, String fullName, String photoUrl, LocalDate birthDate,
            Gender gender, String bio, String location, String phoneNumber,
            DrivingLicenseCategory drivingLicenseCategory, WorkStatus occupation,
            String stripeConnectAccountId, String bankName, String bankAccountIban, String bankAccountBic,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.photoUrl = photoUrl;
        this.birthDate = birthDate;
        this.gender = gender;
        this.bio = bio;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.drivingLicenseCategory = drivingLicenseCategory;
        this.occupation = occupation;
        this.stripeConnectAccountId = stripeConnectAccountId;
        this.bankName = bankName;
        this.bankAccountIban = bankAccountIban;
        this.bankAccountBic = bankAccountBic;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public DrivingLicenseCategory getDrivingLicenseCategory() {
        return drivingLicenseCategory;
    }

    public WorkStatus getOccupation() {
        return occupation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Updates the full name.
     *
     * @param fullName new full name
     */
    public void updateFullName(String fullName) {
        this.fullName = fullName;
        this.updatedAt = LocalDateTime.now();
    }

    public void setFullName(String fullName) {
        updateFullName(fullName);
    }

    /**
     * Updates the profile photo URL.
     *
     * @param photoUrl new photo URL
     */
    public void updatePhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPhotoUrl(String photoUrl) {
        updatePhotoUrl(photoUrl);
    }

    /**
     * Updates the birth date.
     *
     * @param birthDate new birth date
     */
    public void updateBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        this.updatedAt = LocalDateTime.now();
    }

    public void setBirthDate(LocalDate birthDate) {
        updateBirthDate(birthDate);
    }

    /**
     * Updates the gender.
     *
     * @param gender new gender
     */
    public void updateGender(Gender gender) {
        this.gender = gender;
        this.updatedAt = LocalDateTime.now();
    }

    public void setGender(Gender gender) {
        updateGender(gender);
    }

    /**
     * Updates the bio.
     *
     * @param bio new bio
     */
    public void updateBio(String bio) {
        this.bio = bio;
        this.updatedAt = LocalDateTime.now();
    }

    public void setBio(String bio) {
        updateBio(bio);
    }

    /**
     * Updates the location.
     *
     * @param location new location
     */
    public void updateLocation(String location) {
        this.location = location;
        this.updatedAt = LocalDateTime.now();
    }

    public void setLocation(String location) {
        updateLocation(location);
    }

    /**
     * Updates the phone number.
     *
     * @param phoneNumber new phone number
     */
    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPhoneNumber(String phoneNumber) {
        updatePhoneNumber(phoneNumber);
    }

    /**
     * Updates the driving license category.
     *
     * @param drivingLicenseCategory new driving license category
     */
    public void updateDrivingLicenseCategory(DrivingLicenseCategory drivingLicenseCategory) {
        this.drivingLicenseCategory = drivingLicenseCategory;
        this.updatedAt = LocalDateTime.now();
    }

    public void setDrivingLicenseCategory(DrivingLicenseCategory drivingLicenseCategory) {
        updateDrivingLicenseCategory(drivingLicenseCategory);
    }

    /**
     * Updates the occupation.
     *
     * @param occupation new occupation
     */
    public void updateOccupation(WorkStatus occupation) {
        this.occupation = occupation;
        this.updatedAt = LocalDateTime.now();
    }

    public void setOccupation(WorkStatus occupation) {
        updateOccupation(occupation);
    }

    public String getStripeConnectAccountId() {
        return stripeConnectAccountId;
    }

    public void setStripeConnectAccountId(String stripeConnectAccountId) {
        this.stripeConnectAccountId = stripeConnectAccountId;
        this.updatedAt = LocalDateTime.now();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
        this.updatedAt = LocalDateTime.now();
    }

    public String getBankAccountIban() {
        return bankAccountIban;
    }

    public void setBankAccountIban(String bankAccountIban) {
        this.bankAccountIban = bankAccountIban;
        this.updatedAt = LocalDateTime.now();
    }

    public String getBankAccountBic() {
        return bankAccountBic;
    }

    public void setBankAccountBic(String bankAccountBic) {
        this.bankAccountBic = bankAccountBic;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the profile has a photo.
     *
     * @return true if photo URL is set
     */
    public boolean hasPhoto() {
        return photoUrl != null && !photoUrl.isEmpty();
    }

    /**
     * Checks if the profile has a bio.
     *
     * @return true if bio is set
     */
    public boolean hasBio() {
        return bio != null && !bio.isEmpty();
    }

    /**
     * Checks if the profile has a location.
     *
     * @return true if location is set
     */
    public boolean hasLocation() {
        return location != null && !location.isEmpty();
    }

    /**
     * Calculates the age based on birth date.
     *
     * @return age in years, or null if birth date is not set
     */
    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserProfile that = (UserProfile) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("UserProfile{id=%s, userId=%s, fullName='%s'}", id, userId, fullName);
    }
}
