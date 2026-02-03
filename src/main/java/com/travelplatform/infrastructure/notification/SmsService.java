package com.travelplatform.infrastructure.notification;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SMS service for sending SMS notifications.
 * This service uses Twilio API for SMS delivery.
 */
@ApplicationScoped
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

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
        if (!smsEnabled) {
            log.warn("SMS service is disabled. Skipping custom SMS to: {}", toPhoneNumber);
            return false;
        }

        log.info("Sending custom SMS to: {}", toPhoneNumber);

        try {
            return sendToTwilio(toPhoneNumber, message);

        } catch (Exception e) {
            log.error("Failed to send custom SMS to: {}", toPhoneNumber, e);
            return false;
        }
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
     * This is a placeholder implementation.
     * In production, integrate with Twilio Java SDK.
     */
    private boolean sendToTwilio(String toPhoneNumber, String message) {
        // TODO: Integrate with Twilio Java SDK
        // Twilio.init(accountSid, authToken);
        // Message.creator(new PhoneNumber(fromPhoneNumber), new PhoneNumber(toPhoneNumber))
        //         .setBody(message)
        //         .create();

        log.info("SMS sent successfully to: {} (placeholder implementation)", toPhoneNumber);
        return true;
    }

    /**
     * Send bulk SMS using Twilio API.
     * This is a placeholder implementation.
     */
    private boolean sendBulkToTwilio(java.util.List<String> toPhoneNumbers, String message) {
        // TODO: Integrate with Twilio Java SDK for bulk messaging
        // Twilio.init(accountSid, authToken);
        // List<PhoneNumber> recipients = toPhoneNumbers.stream()
        //         .map(PhoneNumber::new)
        //         .collect(Collectors.toList());
        // Message.creator(new PhoneNumber(fromPhoneNumber), recipients)
        //         .setBody(message)
        //         .create();

        log.info("Bulk SMS sent successfully to {} numbers (placeholder implementation)", toPhoneNumbers.size());
        return true;
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
}
