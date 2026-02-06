package com.travelplatform.domain.service;

import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.model.reel.TravelReel;
import com.travelplatform.domain.model.review.Review;
import com.travelplatform.domain.valueobject.DateRange;
import com.travelplatform.domain.valueobject.Money;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Domain service for business validation.
 * Handles all business validation rules across the platform.
 */
@jakarta.enterprise.context.ApplicationScoped
public class ValidationService {

    // ==================== User Validation ====================

    /**
     * Validates user registration data.
     *
     * @param email    user email
     * @param password user password
     * @param role     user role
     * @return validation result
     */
    public ValidationResult validateRegistration(String email, String password, String role) {
        ValidationResult result = new ValidationResult();

        if (email == null || email.trim().isEmpty()) {
            result.addError("email", "Email is required");
        } else if (!isValidEmail(email)) {
            result.addError("email", "Invalid email format");
        }

        if (password == null || password.trim().isEmpty()) {
            result.addError("password", "Password is required");
        } else if (!isValidPassword(password)) {
            result.addError("password",
                    "Password must be at least 8 characters with uppercase, lowercase, number, and special character");
        }

        if (role == null || role.trim().isEmpty()) {
            result.addError("role", "Role is required");
        } else if (!isValidRole(role)) {
            result.addError("role", "Invalid role");
        }

        return result;
    }

    /**
     * Validates user profile update.
     *
     * @param fullName  user's full name
     * @param birthDate user's birth date
     * @param phone     user's phone number
     * @return validation result
     */
    public ValidationResult validateProfileUpdate(String fullName, LocalDate birthDate, String phone) {
        ValidationResult result = new ValidationResult();

        if (fullName != null && !fullName.trim().isEmpty()) {
            if (fullName.length() > 255) {
                result.addError("fullName", "Full name must be less than 255 characters");
            }
        }

        if (birthDate != null) {
            if (birthDate.isAfter(LocalDate.now())) {
                result.addError("birthDate", "Birth date cannot be in the future");
            }
            if (birthDate.isBefore(LocalDate.now().minusYears(120))) {
                result.addError("birthDate", "Birth date cannot be more than 120 years ago");
            }
        }

        if (phone != null && !phone.trim().isEmpty()) {
            if (!isValidPhoneNumber(phone)) {
                result.addError("phone", "Invalid phone number format");
            }
        }

        return result;
    }

    /**
     * Validates if a user can follow another user.
     *
     * @param followerId  user who wants to follow
     * @param followingId user to be followed
     * @return validation result
     */
    public ValidationResult validateFollow(UUID followerId, UUID followingId) {
        ValidationResult result = new ValidationResult();

        if (followerId == null) {
            result.addError("followerId", "Follower ID is required");
        }

        if (followingId == null) {
            result.addError("followingId", "Following ID is required");
        }

        if (followerId.equals(followingId)) {
            result.addError("followingId", "Cannot follow yourself");
        }

        return result;
    }

    // ==================== Accommodation Validation ====================

    /**
     * Validates accommodation creation/update.
     *
     * @param accommodation accommodation to validate
     * @return validation result
     */
    public ValidationResult validateAccommodation(Accommodation accommodation) {
        if (accommodation == null) {
            ValidationResult result = new ValidationResult();
            result.addError("accommodation", "Accommodation is required");
            return result;
        }
        return validateAccommodationDetails(
                accommodation.getTitle(),
                accommodation.getDescription(),
                accommodation.getAddress() != null ? accommodation.getAddress().getStreetAddress() : null,
                accommodation.getCity(),
                accommodation.getCountry(),
                accommodation.getBasePrice(),
                accommodation.getMaxGuests(),
                accommodation.getMinimumNights(),
                accommodation.getMaximumNights(),
                accommodation.getLocation());
    }

    public ValidationResult validateAccommodationDetails(String title, String description, String address, String city,
            String country,
            Money basePrice, Integer maxGuests, Integer minNights, Integer maxNights,
            com.travelplatform.domain.valueobject.Location location) {
        ValidationResult result = new ValidationResult();

        if (title == null || title.trim().isEmpty()) {
            result.addError("title", "Title is required");
        } else if (title.length() > 255) {
            result.addError("title", "Title must be less than 255 characters");
        }

        if (description == null || description.trim().isEmpty()) {
            result.addError("description", "Description is required");
        } else if (description.length() > 5000) {
            result.addError("description", "Description must be less than 5000 characters");
        }

        if (address == null || address.trim().isEmpty()) {
            result.addError("address", "Address is required");
        }

        if (city == null || city.trim().isEmpty()) {
            result.addError("city", "City is required");
        }

        if (country == null || country.trim().isEmpty()) {
            result.addError("country", "Country is required");
        }

        if (basePrice == null) {
            result.addError("basePrice", "Base price is required");
        } else if (basePrice.getAmount().doubleValue() <= 0) {
            result.addError("basePrice", "Base price must be positive");
        }

        if (maxGuests == null || maxGuests <= 0) {
            result.addError("maxGuests", "Maximum guests must be positive");
        } else if (maxGuests > 50) {
            result.addError("maxGuests", "Maximum guests cannot exceed 50");
        }

        if (minNights != null && minNights < 1) {
            result.addError("minimumNights", "Minimum nights must be at least 1");
        }

        if (maxNights != null && maxNights < 1) {
            result.addError("maximumNights", "Maximum nights must be at least 1");
        }

        if (minNights != null && maxNights != null) {
            if (minNights > maxNights) {
                result.addError("minimumNights", "Minimum nights cannot be greater than maximum nights");
            }
        }

        if (location != null) {
            if (!isValidLatitude(location.getLatitude())) {
                result.addError("latitude", "Invalid latitude value");
            }
            if (!isValidLongitude(location.getLongitude())) {
                result.addError("longitude", "Invalid longitude value");
            }
        }

        return result;
    }

    /**
     * Validates accommodation availability update.
     *
     * @param accommodationId accommodation ID
     * @param dateRange       date range to update
     * @param isAvailable     availability status
     * @return validation result
     */
    public ValidationResult validateAvailabilityUpdate(UUID accommodationId, DateRange dateRange,
            boolean isAvailable) {
        ValidationResult result = new ValidationResult();

        if (accommodationId == null) {
            result.addError("accommodationId", "Accommodation ID is required");
        }

        if (dateRange == null) {
            result.addError("dateRange", "Date range is required");
        } else {
            if (dateRange.getStartDate() == null) {
                result.addError("startDate", "Start date is required");
            }
            if (dateRange.getEndDate() == null) {
                result.addError("endDate", "End date is required");
            }
            if (dateRange.getStartDate().isBefore(LocalDate.now())) {
                result.addError("startDate", "Start date cannot be in the past");
            }
        }

        return result;
    }

    // ==================== Booking Validation ====================

    /**
     * Validates booking creation.
     *
     * @param accommodation accommodation to book
     * @param checkInDate   check-in date
     * @param checkOutDate  check-out date
     * @param guests        number of guests
     * @return validation result
     */
    public ValidationResult validateBooking(Accommodation accommodation, LocalDate checkInDate,
            LocalDate checkOutDate, int guests) {
        ValidationResult result = new ValidationResult();

        if (accommodation == null) {
            result.addError("accommodation", "Accommodation is required");
            return result;
        }

        if (checkInDate == null) {
            result.addError("checkInDate", "Check-in date is required");
        } else if (checkInDate.isBefore(LocalDate.now())) {
            result.addError("checkInDate", "Check-in date cannot be in the past");
        }

        if (checkOutDate == null) {
            result.addError("checkOutDate", "Check-out date is required");
        } else if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            result.addError("checkOutDate", "Check-out date must be after check-in date");
        }

        if (guests <= 0) {
            result.addError("guests", "Number of guests must be positive");
        } else if (guests > accommodation.getMaxGuests()) {
            result.addError("guests", "Number of guests exceeds maximum capacity");
        }

        // Validate minimum nights
        if (accommodation.getMinimumNights() > 0) {
            int nights = DateRange.calculateNights(checkInDate, checkOutDate);
            if (nights < accommodation.getMinimumNights()) {
                result.addError("checkInDate", "Minimum stay of " +
                        accommodation.getMinimumNights() + " nights required");
            }
        }

        // Validate maximum nights
        if (accommodation.getMaximumNights() != null) {
            int nights = DateRange.calculateNights(checkInDate, checkOutDate);
            if (nights > accommodation.getMaximumNights()) {
                result.addError("checkInDate", "Maximum stay of " +
                        accommodation.getMaximumNights() + " nights exceeded");
            }
        }

        return result;
    }

    /**
     * Validates booking cancellation.
     *
     * @param booking booking to cancel
     * @return validation result
     */
    public ValidationResult validateBookingCancellation(Booking booking) {
        ValidationResult result = new ValidationResult();

        if (booking == null) {
            result.addError("booking", "Booking is required");
            return result;
        }

        String status = booking.getStatus().name();

        if (status.equals("CANCELLED")) {
            result.addError("status", "Booking is already cancelled");
        }

        if (status.equals("COMPLETED") || status.equals("NO_SHOW")) {
            result.addError("status", "Cannot cancel completed or no-show booking");
        }

        // Check if cancellation is allowed based on check-in date
        LocalDate checkInDate = booking.getCheckInDate();
        if (checkInDate != null && checkInDate.isBefore(LocalDate.now().plusDays(1))) {
            result.addError("checkInDate", "Cancellation must be made at least 24 hours before check-in");
        }

        return result;
    }

    // ==================== Reel Validation ====================

    /**
     * Validates reel creation/update.
     *
     * @param reel reel to validate
     * @return validation result
     */
    public ValidationResult validateReel(TravelReel reel) {
        ValidationResult result = new ValidationResult();

        if (reel == null) {
            result.addError("reel", "Reel is required");
            return result;
        }

        if (reel.getVideoUrl() == null || reel.getVideoUrl().trim().isEmpty()) {
            result.addError("videoUrl", "Video URL is required");
        }

        if (reel.getThumbnailUrl() == null || reel.getThumbnailUrl().trim().isEmpty()) {
            result.addError("thumbnailUrl", "Thumbnail URL is required");
        }

        if (reel.getDuration() <= 0) {
            result.addError("duration", "Duration must be positive");
        } else if (reel.getDuration() > 90) {
            result.addError("duration", "Duration cannot exceed 90 seconds");
        }

        if (reel.getTitle() != null && reel.getTitle().length() > 100) {
            result.addError("title", "Title must be less than 100 characters");
        }

        if (reel.getDescription() != null && reel.getDescription().length() > 500) {
            result.addError("description", "Description must be less than 500 characters");
        }

        if (reel.getVisibility() == null) {
            result.addError("visibility", "Visibility is required");
        }

        if (reel.getLocation() != null) {
            if (!isValidLatitude(reel.getLocation().getLatitude())) {
                result.addError("latitude", "Invalid latitude value");
            }
            if (!isValidLongitude(reel.getLocation().getLongitude())) {
                result.addError("longitude", "Invalid longitude value");
            }
        }

        return result;
    }

    /**
     * Validates reel comment.
     *
     * @param content comment content
     * @return validation result
     */
    public ValidationResult validateReelComment(String content) {
        ValidationResult result = new ValidationResult();

        if (content == null || content.trim().isEmpty()) {
            result.addError("content", "Comment content is required");
        } else if (content.length() > 300) {
            result.addError("content", "Comment must be less than 300 characters");
        }

        return result;
    }

    // ==================== Review Validation ====================

    /**
     * Validates review creation.
     *
     * @param review review to validate
     * @return validation result
     */
    public ValidationResult validateReview(Review review) {
        ValidationResult result = new ValidationResult();

        if (review == null) {
            result.addError("review", "Review is required");
            return result;
        }

        if (review.getOverallRating() < 1 || review.getOverallRating() > 5) {
            result.addError("overallRating", "Overall rating must be between 1 and 5");
        }

        if (review.getCleanlinessRating() != null &&
                (review.getCleanlinessRating() < 1 || review.getCleanlinessRating() > 5)) {
            result.addError("cleanlinessRating", "Cleanliness rating must be between 1 and 5");
        }

        if (review.getAccuracyRating() != null &&
                (review.getAccuracyRating() < 1 || review.getAccuracyRating() > 5)) {
            result.addError("accuracyRating", "Accuracy rating must be between 1 and 5");
        }

        if (review.getCommunicationRating() != null &&
                (review.getCommunicationRating() < 1 || review.getCommunicationRating() > 5)) {
            result.addError("communicationRating", "Communication rating must be between 1 and 5");
        }

        if (review.getLocationRating() != null &&
                (review.getLocationRating() < 1 || review.getLocationRating() > 5)) {
            result.addError("locationRating", "Location rating must be between 1 and 5");
        }

        if (review.getValueRating() != null &&
                (review.getValueRating() < 1 || review.getValueRating() > 5)) {
            result.addError("valueRating", "Value rating must be between 1 and 5");
        }

        if (review.getContent() == null || review.getContent().trim().isEmpty()) {
            result.addError("content", "Review content is required");
        } else if (review.getContent().length() > 2000) {
            result.addError("content", "Review content must be less than 2000 characters");
        }

        if (review.getTitle() != null && review.getTitle().length() > 150) {
            result.addError("title", "Review title must be less than 150 characters");
        }

        return result;
    }

    // ==================== Helper Methods ====================

    /**
     * Validates email format.
     *
     * @param email email to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null)
            return false;
        String emailRegex = "^[A-Za-z0-9+._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Validates password strength.
     *
     * @param password password to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidPassword(String password) {
        if (password == null)
            return false;
        if (password.length() < 8)
            return false;

        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+|\\-=\\[\\]{};:'\",.<>/?].*");

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }

    /**
     * Validates user role.
     *
     * @param role role to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidRole(String role) {
        if (role == null)
            return false;
        return role.equals("SUPER_ADMIN") ||
                role.equals("TRAVELER") ||
                role.equals("SUPPLIER_SUBSCRIBER") ||
                role.equals("ASSOCIATION_MANAGER");
    }

    /**
     * Validates phone number format.
     *
     * @param phone phone number to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidPhoneNumber(String phone) {
        if (phone == null)
            return false;
        // Simple phone validation (international format)
        String phoneRegex = "^\\+?[0-9]{10,15}$";
        return phone.matches(phoneRegex);
    }

    /**
     * Validates latitude value.
     *
     * @param latitude latitude to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidLatitude(double latitude) {
        return latitude >= -90 && latitude <= 90;
    }

    /**
     * Validates longitude value.
     *
     * @param longitude longitude to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidLongitude(double longitude) {
        return longitude >= -180 && longitude <= 180;
    }

    // ==================== Validation Result Class ====================

    /**
     * Represents the result of a validation operation.
     */
    public static class ValidationResult {
        private final List<String> errors = new java.util.ArrayList<>();
        private final List<String> fieldNames = new java.util.ArrayList<>();

        /**
         * Adds an error to the validation result.
         *
         * @param fieldName name of the field with error
         * @param message   error message
         */
        public void addError(String fieldName, String message) {
            errors.add(message);
            fieldNames.add(fieldName);
        }

        /**
         * Checks if validation passed.
         *
         * @return true if no errors, false otherwise
         */
        public boolean isValid() {
            return errors.isEmpty();
        }

        /**
         * Gets all error messages.
         *
         * @return list of error messages
         */
        public List<String> getErrors() {
            return errors;
        }

        /**
         * Gets all field names with errors.
         *
         * @return list of field names
         */
        public List<String> getFieldNames() {
            return fieldNames;
        }

        /**
         * Gets error message for a specific field.
         *
         * @param fieldName field name
         * @return error message or null if no error
         */
        public String getErrorForField(String fieldName) {
            for (int i = 0; i < fieldNames.size(); i++) {
                if (fieldNames.get(i).equals(fieldName)) {
                    return errors.get(i);
                }
            }
            return null;
        }
    }

    /**
     * Validates user preferences update.
     *
     * @param preferredDestinations list of preferred destinations
     * @param interests             list of interests
     * @return validation result
     */
    public ValidationResult validateUserPreferences(List<String> preferredDestinations, List<String> interests) {
        ValidationResult result = new ValidationResult();

        if (preferredDestinations != null) {
            for (String dest : preferredDestinations) {
                if (dest.length() > 100) {
                    result.addError("preferredDestinations", "Destination name too long");
                }
            }
        }

        if (interests != null) {
            for (String interest : interests) {
                if (interest.length() > 50) {
                    result.addError("interests", "Interest name too long");
                }
            }
        }

        return result;
    }
}
