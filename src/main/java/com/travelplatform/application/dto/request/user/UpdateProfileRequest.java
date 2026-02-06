package com.travelplatform.application.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO for updating user profile request.
 */
public class UpdateProfileRequest {

    @Size(max = 255, message = "Full name must be less than 255 characters")
    private String fullName;

    private String photoUrl;

    private LocalDate birthDate;

    @Pattern(regexp = "MALE|FEMALE|OTHER|PREFER_NOT_TO_SAY", message = "Invalid gender")
    private String gender;

    @Size(max = 1000, message = "Bio must be less than 1000 characters")
    private String bio;

    @Size(max = 255, message = "Location must be less than 255 characters")
    private String location;

    @Size(max = 50, message = "Phone number must be less than 50 characters")
    private String phoneNumber;

    @Pattern(regexp = "NONE|A|B|C|D|E|INTERNATIONAL", message = "Invalid driving license category")
    private String drivingLicenseCategory;

    @Pattern(regexp = "WORKER|STUDENT|RETIRED|SELF_EMPLOYED|UNEMPLOYED|FREELANCER|ENTREPRENEUR|OTHER", message = "Invalid work status")
    private String occupation;

    private String bankName;

    private String bankAccountIban;

    private String bankAccountBic;

    // Getters and Setters

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDrivingLicenseCategory() {
        return drivingLicenseCategory;
    }

    public void setDrivingLicenseCategory(String drivingLicenseCategory) {
        this.drivingLicenseCategory = drivingLicenseCategory;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccountIban() {
        return bankAccountIban;
    }

    public void setBankAccountIban(String bankAccountIban) {
        this.bankAccountIban = bankAccountIban;
    }

    public String getBankAccountBic() {
        return bankAccountBic;
    }

    public void setBankAccountBic(String bankAccountBic) {
        this.bankAccountBic = bankAccountBic;
    }
}
