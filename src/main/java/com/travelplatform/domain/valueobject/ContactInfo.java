package com.travelplatform.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing contact information.
 * Immutable object - once created, cannot be modified.
 */
public class ContactInfo {
    private final String email;
    private final String phoneNumber;

    // Email validation regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Phone validation regex pattern (international format)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[1-9]\\d{1,14}$"
    );

    /**
     * Creates a new ContactInfo with email and phone number.
     * At least one contact method must be provided.
     *
     * @param email       the email address (optional)
     * @param phoneNumber the phone number (optional)
     * @throws IllegalArgumentException if both email and phone are null/empty, or if format is invalid
     */
    public ContactInfo(String email, String phoneNumber) {
        if ((email == null || email.trim().isEmpty()) && (phoneNumber == null || phoneNumber.trim().isEmpty())) {
            throw new IllegalArgumentException("At least one contact method (email or phone) must be provided");
        }

        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new IllegalArgumentException("Invalid email format");
            }
            this.email = email.trim().toLowerCase();
        } else {
            this.email = null;
        }

        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            String normalizedPhone = phoneNumber.replaceAll("[\\s\\-()]", "");
            if (!PHONE_PATTERN.matcher(normalizedPhone).matches()) {
                throw new IllegalArgumentException("Invalid phone number format");
            }
            this.phoneNumber = normalizedPhone;
        } else {
            this.phoneNumber = null;
        }
    }

    /**
     * Creates a ContactInfo with only email.
     *
     * @param email the email address
     * @throws IllegalArgumentException if email is null/empty or format is invalid
     */
    public static ContactInfo withEmail(String email) {
        return new ContactInfo(email, null);
    }

    /**
     * Creates a ContactInfo with only phone number.
     *
     * @param phoneNumber the phone number
     * @throws IllegalArgumentException if phone is null/empty or format is invalid
     */
    public static ContactInfo withPhone(String phoneNumber) {
        return new ContactInfo(null, phoneNumber);
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Checks if email is available.
     *
     * @return true if email is set
     */
    public boolean hasEmail() {
        return email != null && !email.isEmpty();
    }

    /**
     * Checks if phone number is available.
     *
     * @return true if phone number is set
     */
    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.isEmpty();
    }

    /**
     * Returns the primary contact method (email if available, otherwise phone).
     *
     * @return the primary contact method
     */
    public String getPrimaryContact() {
        return hasEmail() ? email : phoneNumber;
    }

    /**
     * Masks the email for privacy (e.g., j***@example.com).
     *
     * @return masked email or null if no email
     */
    public String getMaskedEmail() {
        if (!hasEmail()) {
            return null;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        return localPart.charAt(0) + "***" + domain;
    }

    /**
     * Masks the phone number for privacy (e.g., +1 *** *** 1234).
     *
     * @return masked phone or null if no phone
     */
    public String getMaskedPhoneNumber() {
        if (!hasPhoneNumber()) {
            return null;
        }
        int length = phoneNumber.length();
        if (length <= 4) {
            return phoneNumber;
        }
        String visiblePart = phoneNumber.substring(length - 4);
        return phoneNumber.substring(0, length - 4).replaceAll("\\d", "*") + visiblePart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactInfo that = (ContactInfo) o;
        return Objects.equals(email, that.email) && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, phoneNumber);
    }

    @Override
    public String toString() {
        return String.format("ContactInfo{email='%s', phone='%s'}",
                hasEmail() ? getMaskedEmail() : "none",
                hasPhoneNumber() ? getMaskedPhoneNumber() : "none");
    }
}
