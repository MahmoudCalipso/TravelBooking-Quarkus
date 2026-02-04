package com.travelplatform.application.validator;

import com.travelplatform.application.dto.request.user.ChangePasswordRequest;
import com.travelplatform.application.dto.request.user.RegisterUserRequest;
import com.travelplatform.application.dto.request.user.UpdateProfileRequest;
import com.travelplatform.application.dto.request.user.UpdatePreferencesRequest;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import com.travelplatform.domain.model.user.Gender;
import com.travelplatform.domain.model.user.WorkStatus;
import com.travelplatform.domain.service.ValidationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

/**
 * Validator for user-related operations.
 * Provides additional validation beyond bean validation annotations.
 */
@ApplicationScoped
public class UserValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[1-9]\\d{1,14}$");

    private static final int MIN_AGE = 13;
    private static final int MAX_AGE = 120;

    @Inject
    ValidationService validationService;

    /**
     * Validates user registration request.
     */
    public void validateRegistration(RegisterUserRequest request) {
        // Email validation
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Password validation (handled by bean validation, but additional checks here)
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        // Role validation
        if (request.getRole() != null) {
            try {
                UserRole.valueOf(request.getRole());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid user role");
            }
        }

        // Use domain validation service for additional checks
        validationService.validateRegistration(
                request.getEmail(),
                request.getPassword(),
                request.getRole() != null ? request.getRole() : UserRole.TRAVELER.name());
    }

    /**
     * Validates login request.
     */
    public void validateLogin(com.travelplatform.application.dto.request.user.LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
    }

    /**
     * Validates profile update request.
     */
    public void validateProfileUpdate(UpdateProfileRequest request) {
        // Birth date validation
        if (request.getBirthDate() != null) {
            LocalDate birthDate = request.getBirthDate();
            LocalDate today = LocalDate.now();
            int age = Period.between(birthDate, today).getYears();

            if (age < MIN_AGE) {
                throw new IllegalArgumentException("User must be at least " + MIN_AGE + " years old");
            }
            if (age > MAX_AGE) {
                throw new IllegalArgumentException("Invalid birth date");
            }
        }

        // Phone number validation
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            if (!PHONE_PATTERN.matcher(request.getPhoneNumber()).matches()) {
                throw new IllegalArgumentException("Invalid phone number format");
            }
        }

        // Gender validation
        if (request.getGender() != null) {
            try {
                Gender.valueOf(request.getGender());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid gender value");
            }
        }

        // Work status validation
        if (request.getOccupation() != null) {
            try {
                WorkStatus.valueOf(request.getOccupation());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid work status value");
            }
        }

        // Use domain validation service
        validationService.validateProfileUpdate(
                request.getFullName(),
                request.getBirthDate(),
                request.getPhoneNumber());
    }

    /**
     * Validates preferences update request.
     */
    public void validatePreferencesUpdate(UpdatePreferencesRequest request) {
        // Budget range validation
        if (request.getBudgetRange() != null) {
            String budget = request.getBudgetRange().toUpperCase();
            if (!budget.equals("BUDGET") && !budget.equals("MODERATE") && !budget.equals("LUXURY")) {
                throw new IllegalArgumentException("Invalid budget range");
            }
        }

        // Travel style validation
        if (request.getTravelStyle() != null) {
            String style = request.getTravelStyle().toUpperCase();
            if (!style.equals("ADVENTURE") && !style.equals("CULTURAL") &&
                    !style.equals("RELAXATION") && !style.equals("BUSINESS")) {
                throw new IllegalArgumentException("Invalid travel style");
            }
        }

        // Use domain validation service
        validationService.validateUserPreferences(
                request.getPreferredDestinations(),
                request.getInterests());
    }

    /**
     * Validates password change request.
     */
    public void validatePasswordChange(ChangePasswordRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Password change request is required");
        }
        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            throw new IllegalArgumentException("Current password is required");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("New password is required");
        }
        if (request.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters");
        }
        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }
    }

    /**
     * Validates email format.
     */
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates phone number format.
     */
    public boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
}
