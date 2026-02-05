package com.travelplatform.infrastructure.notification;

import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.model.user.UserProfile;
import com.travelplatform.domain.repository.UserRepository;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

/**
 * SMS service for sending SMS notifications.
 * This service uses Twilio API for SMS delivery.
 */
@ApplicationScoped
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Inject
    @ConfigProperty(name = "twilio.account.sid")
    private String accountSid;

    @Inject
    @ConfigProperty(name = "twilio.auth.token")
    private String authToken;

    @Inject
    @ConfigProperty(name = "twilio.phone.number")
    private String fromPhoneNumber;

    @Inject
    @ConfigProperty(name = "sms.enabled")
    private boolean smsEnabled;

    @Inject
    @ConfigProperty(name = "sms.default.verification.expiry-minutes", defaultValue = "10")
    int defaultVerificationExpiryMinutes;

    @Inject
    UserRepository userRepository;

    /**
     * Send an SMS message to a single phone number.
     *
     * @param toPhoneNumber The recipient phone number (E.164 format)
     * @param message The message content
     * @return true if SMS was sent successfully, false otherwise
     */
    public boolean sendSms(String toPhoneNumber, String message) {
        if (!smsEnabled) {
            log.warn("SMS service is disabled. Skipping SMS to: {}", toPhoneNumber);
            return false;
        }
        if (!hasTwilioConfiguration()) {
            log.error("Twilio credentials or sender phone are missing. Cannot send SMS to {}", toPhoneNumber);
            return false;
        }

        log.info("Sending SMS to: {}", toPhoneNumber);

        try {
            return sendToTwilio(toPhoneNumber, message);

        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", toPhoneNumber, e);
            return false;
        }
    }

    /**
     * Send an SMS verification code.
     *
     * @param toPhoneNumber The recipient phone number
     * @param verificationCode The verification code
     * @param expiryMinutes The code expiry in minutes
     * @return true if SMS was sent successfully, false otherwise
     */
    public boolean sendVerificationCode(String toPhoneNumber, String verificationCode, int expiryMinutes) {
        String message = "Your verification code is: " + verificationCode + 
                      ". This code will expire in " + expiryMinutes + " minutes.";
        
        return sendSms(toPhoneNumber, message);
    }

    public boolean sendVerificationCode(String toPhoneNumber, String verificationCode) {
        return sendVerificationCode(toPhoneNumber, verificationCode, defaultVerificationExpiryMinutes);
    }

    /**
     * Send a booking confirmation SMS.
     *
     * @param toPhoneNumber The recipient phone number
     * @param bookingId The booking ID
     * @param accommodationTitle The accommodation title
     * @param checkInDate The check-in date
     * @return true if SMS was sent successfully, false otherwise
     */
    public boolean sendBookingConfirmation(String toPhoneNumber, String bookingId, 
                                       String accommodationTitle, String checkInDate) {
        String message = "Booking Confirmed! Your booking (" + bookingId + 
                      ") at " + accommodationTitle + " is confirmed for " + checkInDate + 
                      ". We look forward to hosting you!";
        
        return sendSms(toPhoneNumber, message);
    }

    /**
     * Send a booking reminder 24h before check-in.
     *
     * @param booking booking domain object
     * @return true if SMS was queued successfully, false otherwise
     */
    public boolean sendBookingReminder(Booking booking) {
        Recipient recipient = resolveRecipientFromUser(booking.getUserId());
        if (recipient == null) {
            log.warn("Skipping booking reminder because no phone is available for user {}", booking.getUserId());
            return false;
        }
        String message = "Reminder: your stay starts on " + DATE_FORMATTER.format(booking.getCheckInDate()) +
                ". Booking ID " + booking.getId() + ". We look forward to welcoming you.";
        return sendSms(recipient.phone(), message);
    }

    /**
     * Send a booking cancellation SMS.
     *
     * @param toPhoneNumber The recipient phone number
     * @param bookingId The booking ID
     * @param accommodationTitle The accommodation title
     * @return true if SMS was sent successfully, false otherwise
     */
    public boolean sendBookingCancellation(String toPhoneNumber, String bookingId, 
                                       String accommodationTitle) {
        String message = "Booking Cancelled. Your booking (" + bookingId + 
                      ") at " + accommodationTitle + " has been cancelled. " +
                      "We hope to see you again soon!";
        
        return sendSms(toPhoneNumber, message);
    }

    /**
     * Send a payment received SMS.
     *
     * @param toPhoneNumber The recipient phone number
     * @param amount The payment amount
     * @param currency The currency code
     * @return true if SMS was sent successfully, false otherwise
     */
    public boolean sendPaymentReceived(String toPhoneNumber, String amount, String currency) {
        String message = "Payment Received. Your payment of " + amount + " " + currency + 
                      " was successful. Thank you for your payment!";
        
        return sendSms(toPhoneNumber, message);
    }

    /**
     * Send an event reminder SMS.
     *
     * @param toPhoneNumber The recipient phone number
     * @param eventTitle The event title
     * @param eventDate The event date
     * @return true if SMS was sent successfully, false otherwise
     */
    public boolean sendEventReminder(String toPhoneNumber, String eventTitle, String eventDate) {
        String message = "Event Reminder. " + eventTitle + " is starting on " + eventDate + 
                      ". Don't miss it!";
        
        return sendSms(toPhoneNumber, message);
    }

    /**
     * Send a password reset SMS.
     *
     * @param toPhoneNumber The recipient phone number
     * @param resetCode The password reset code
     * @param expiryMinutes The code expiry in minutes
     * @return true if SMS was sent successfully, false otherwise
     */
    public boolean sendPasswordReset(String toPhoneNumber, String resetCode, int expiryMinutes) {
        String message = "Password Reset. Your password reset code is: " + resetCode + 
                      ". This code will expire in " + expiryMinutes + " minutes. " +
                      "If you didn't request a password reset, please ignore this message.";
        
        return sendSms(toPhoneNumber, message);
    }

    /**
     * Send a two-factor authentication code.
     *
     * @param toPhoneNumber The recipient phone number
     * @param otpCode The one-time password code
     * @param expiryMinutes The code expiry in minutes
     * @return true if SMS was sent successfully, false otherwise
     */
    public boolean sendTwoFactorCode(String toPhoneNumber, String otpCode, int expiryMinutes) {
        String message = "Your verification code is: " + otpCode + 
                      ". This code will expire in " + expiryMinutes + " minutes. " +
                      "Do not share this code with anyone.";
        
        return sendSms(toPhoneNumber, message);
    }

    /**
     * Send a custom SMS message.
     *
     * @param toPhoneNumber The recipient phone number
     * @param message The message content
     * @return true if SMS was sent successfully, false otherwise
     */
    public boolean sendCustomSms(String toPhoneNumber, String message) {
        return sendSms(toPhoneNumber, message);
    }

    /**
     * Send an SMS to multiple phone numbers.
     *
     * @param toPhoneNumbers The list of recipient phone numbers
     * @param message The message content
     * @return true if SMS was sent successfully, false otherwise
     */
    public boolean sendBulkSms(java.util.List<String> toPhoneNumbers, String message) {
        if (!smsEnabled) {
            log.warn("SMS service is disabled. Skipping bulk SMS to {} numbers", toPhoneNumbers.size());
            return false;
        }

        if (!hasTwilioConfiguration()) {
            log.error("Twilio credentials or sender phone are missing. Cannot send bulk SMS to {} recipients",
                    toPhoneNumbers.size());
            return false;
        }

        log.info("Sending bulk SMS to {} numbers", toPhoneNumbers.size());

        try {
            return sendBulkToTwilio(toPhoneNumbers, message);

        } catch (Exception e) {
            log.error("Failed to send bulk SMS", e);
            return false;
        }
    }

    /**
     * Send an SMS using Twilio API.
     */
    private boolean sendToTwilio(String toPhoneNumber, String message) {
        try {
            Twilio.init(accountSid, authToken);
            Message twilioMessage = Message.creator(new PhoneNumber(toPhoneNumber), new PhoneNumber(fromPhoneNumber), message)
                    .create();
            Message.Status status = twilioMessage.getStatus();
            boolean success = status != Message.Status.FAILED && status != Message.Status.UNDELIVERED;
            if (!success) {
                log.error("Twilio failed to deliver message to {}. Status={}, SID={}", toPhoneNumber, status,
                        twilioMessage.getSid());
            }
            return success;
        } catch (ApiException e) {
            log.error("Twilio API error sending SMS to {}: {}", toPhoneNumber, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Send bulk SMS using Twilio API.
     */
    private boolean sendBulkToTwilio(java.util.List<String> toPhoneNumbers, String message) {
        boolean allSuccess = true;
        for (String number : toPhoneNumbers) {
            boolean success = sendToTwilio(number, message);
            allSuccess = allSuccess && success;
        }
        return allSuccess;
    }

    /**
     * Validate a phone number format.
     *
     * @param phoneNumber The phone number to validate
     * @return true if phone number is valid, false otherwise
     */
    public boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }

        // Basic E.164 format validation
        // Format: +[country code][number]
        // Example: +14155551234
        String e164Pattern = "^\\+[1-9]\\d{1,14}$";
        return phoneNumber.matches(e164Pattern);
    }

    /**
     * Format a phone number to E.164 format.
     *
     * @param phoneNumber The phone number to format
     * @return Formatted phone number in E.164 format
     */
    public String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "";
        }

        // Remove all non-digit characters except leading +
        String cleaned = phoneNumber.replaceAll("[^0-9+]", "");
        
        // Ensure it starts with +
        if (!cleaned.startsWith("+")) {
            cleaned = "+" + cleaned;
        }

        return cleaned;
    }

    /**
     * Mask a phone number for privacy (e.g., +1***1234).
     *
     * @param phoneNumber The phone number to mask
     * @return Masked phone number
     */
    public String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 7) {
            return phoneNumber;
        }

        // Show first 3 and last 4 digits
        return phoneNumber.substring(0, 3) + "***" + phoneNumber.substring(phoneNumber.length() - 4);
    }

    /**
     * Calculate SMS cost estimate.
     *
     * @param messageLength The length of the message
     * @return Estimated cost in USD
     */
    public double calculateSmsCost(int messageLength) {
        // Twilio pricing (example rates)
        // US: $0.0075 per segment (160 chars)
        // International: $0.05 per segment
        
        final int SEGMENT_LENGTH = 160;
        int segments = (int) Math.ceil((double) messageLength / SEGMENT_LENGTH);
        
        // Assuming US rate
        return segments * 0.0075;
    }

    /**
     * Check if message exceeds single SMS segment limit.
     *
     * @param message The message to check
     * @return true if message exceeds 160 characters, false otherwise
     */
    public boolean exceedsSingleSegment(String message) {
        return message != null && message.length() > 160;
    }

    /**
     * Get the number of SMS segments required for a message.
     *
     * @param message The message to check
     * @return Number of SMS segments
     */
    public int getSegmentCount(String message) {
        if (message == null || message.isEmpty()) {
            return 0;
        }

        final int SEGMENT_LENGTH = 160;
        return (int) Math.ceil((double) message.length() / SEGMENT_LENGTH);
    }

    private boolean hasTwilioConfiguration() {
        return accountSid != null && !accountSid.isBlank()
                && authToken != null && !authToken.isBlank()
                && fromPhoneNumber != null && !fromPhoneNumber.isBlank();
    }

    private Recipient resolveRecipientFromUser(UUID userId) {
        Optional<UserProfile> profile = userRepository.findProfileByUserId(userId);
        return profile.map(userProfile -> new Recipient(formatPhoneNumber(userProfile.getPhoneNumber())))
                .filter(r -> validatePhoneNumber(r.phone()))
                .orElse(null);
    }

    private record Recipient(String phone) {
    }
}
