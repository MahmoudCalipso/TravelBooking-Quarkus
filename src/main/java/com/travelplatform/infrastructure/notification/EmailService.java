package com.travelplatform.infrastructure.notification;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Email service for sending transactional and marketing emails.
 * This service uses SendGrid API for email delivery.
 */
@ApplicationScoped
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Inject
    @ConfigProperty(name = "email.from.address")
    private String fromAddress;

    @Inject
    @ConfigProperty(name = "email.from.name")
    private String fromName;

    @Inject
    @ConfigProperty(name = "email.sendgrid.api.key")
    private String sendGridApiKey;

    @Inject
    @ConfigProperty(name = "email.enabled")
    private boolean emailEnabled;

    /**
     * Send a welcome email to a new user.
     *
     * @param toEmail The recipient email address
     * @param toName The recipient name
     * @param verificationUrl The email verification URL
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendWelcomeEmail(String toEmail, String toName, String verificationUrl) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. Skipping welcome email to: {}", toEmail);
            return false;
        }

        log.info("Sending welcome email to: {}", toEmail);

        try {
            String subject = "Welcome to Travel Platform!";
            String htmlContent = buildWelcomeEmailContent(toName, verificationUrl);
            String textContent = buildWelcomeEmailText(toName, verificationUrl);

            return sendEmail(toEmail, toName, subject, htmlContent, textContent);

        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
            return false;
        }
    }

    /**
     * Send an email verification email.
     *
     * @param toEmail The recipient email address
     * @param toName The recipient name
     * @param verificationUrl The email verification URL
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendEmailVerification(String toEmail, String toName, String verificationUrl) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. Skipping verification email to: {}", toEmail);
            return false;
        }

        log.info("Sending email verification to: {}", toEmail);

        try {
            String subject = "Verify Your Email Address";
            String htmlContent = buildVerificationEmailContent(toName, verificationUrl);
            String textContent = buildVerificationEmailText(toName, verificationUrl);

            return sendEmail(toEmail, toName, subject, htmlContent, textContent);

        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            return false;
        }
    }

    /**
     * Send a password reset email.
     *
     * @param toEmail The recipient email address
     * @param toName The recipient name
     * @param resetUrl The password reset URL
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendPasswordReset(String toEmail, String toName, String resetUrl) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. Skipping password reset email to: {}", toEmail);
            return false;
        }

        log.info("Sending password reset email to: {}", toEmail);

        try {
            String subject = "Reset Your Password";
            String htmlContent = buildPasswordResetEmailContent(toName, resetUrl);
            String textContent = buildPasswordResetEmailText(toName, resetUrl);

            return sendEmail(toEmail, toName, subject, htmlContent, textContent);

        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            return false;
        }
    }

    /**
     * Send a booking confirmation email.
     *
     * @param toEmail The recipient email address
     * @param toName The recipient name
     * @param bookingId The booking ID
     * @param accommodationTitle The accommodation title
     * @param checkInDate The check-in date
     * @param checkOutDate The check-out date
     * @param totalAmount The total amount
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendBookingConfirmation(String toEmail, String toName, String bookingId,
                                      String accommodationTitle, String checkInDate,
                                      String checkOutDate, String totalAmount) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. Skipping booking confirmation to: {}", toEmail);
            return false;
        }

        log.info("Sending booking confirmation to: {}", toEmail);

        try {
            String subject = "Booking Confirmed - " + accommodationTitle;
            String htmlContent = buildBookingConfirmationEmailContent(toName, bookingId, accommodationTitle, 
                    checkInDate, checkOutDate, totalAmount);
            String textContent = buildBookingConfirmationEmailText(toName, bookingId, accommodationTitle,
                    checkInDate, checkOutDate, totalAmount);

            return sendEmail(toEmail, toName, subject, htmlContent, textContent);

        } catch (Exception e) {
            log.error("Failed to send booking confirmation to: {}", toEmail, e);
            return false;
        }
    }

    /**
     * Send a booking cancellation email.
     *
     * @param toEmail The recipient email address
     * @param toName The recipient name
     * @param bookingId The booking ID
     * @param accommodationTitle The accommodation title
     * @param cancellationReason The cancellation reason
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendBookingCancellation(String toEmail, String toName, String bookingId,
                                      String accommodationTitle, String cancellationReason) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. Skipping booking cancellation to: {}", toEmail);
            return false;
        }

        log.info("Sending booking cancellation to: {}", toEmail);

        try {
            String subject = "Booking Cancelled - " + accommodationTitle;
            String htmlContent = buildBookingCancellationEmailContent(toName, bookingId, accommodationTitle, cancellationReason);
            String textContent = buildBookingCancellationEmailText(toName, bookingId, accommodationTitle, cancellationReason);

            return sendEmail(toEmail, toName, subject, htmlContent, textContent);

        } catch (Exception e) {
            log.error("Failed to send booking cancellation to: {}", toEmail, e);
            return false;
        }
    }

    /**
     * Send a payment received email.
     *
     * @param toEmail The recipient email address
     * @param toName The recipient name
     * @param amount The payment amount
     * @param currency The currency code
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendPaymentReceived(String toEmail, String toName, String amount, String currency) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. Skipping payment received to: {}", toEmail);
            return false;
        }

        log.info("Sending payment received email to: {}", toEmail);

        try {
            String subject = "Payment Received";
            String htmlContent = buildPaymentReceivedEmailContent(toName, amount, currency);
            String textContent = buildPaymentReceivedEmailText(toName, amount, currency);

            return sendEmail(toEmail, toName, subject, htmlContent, textContent);

        } catch (Exception e) {
            log.error("Failed to send payment received email to: {}", toEmail, e);
            return false;
        }
    }

    /**
     * Send a review request email.
     *
     * @param toEmail The recipient email address
     * @param toName The recipient name
     * @param accommodationTitle The accommodation title
     * @param reviewUrl The review submission URL
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendReviewRequest(String toEmail, String toName, String accommodationTitle, String reviewUrl) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. Skipping review request to: {}", toEmail);
            return false;
        }

        log.info("Sending review request to: {}", toEmail);

        try {
            String subject = "How was your stay at " + accommodationTitle + "?";
            String htmlContent = buildReviewRequestEmailContent(toName, accommodationTitle, reviewUrl);
            String textContent = buildReviewRequestEmailText(toName, accommodationTitle, reviewUrl);

            return sendEmail(toEmail, toName, subject, htmlContent, textContent);

        } catch (Exception e) {
            log.error("Failed to send review request to: {}", toEmail, e);
            return false;
        }
    }

    /**
     * Send a notification email.
     *
     * @param toEmail The recipient email address
     * @param toName The recipient name
     * @param notificationType The notification type
     * @param title The notification title
     * @param message The notification message
     * @param actionUrl The action URL (optional)
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendNotificationEmail(String toEmail, String toName, String notificationType,
                                      String title, String message, String actionUrl) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. Skipping notification email to: {}", toEmail);
            return false;
        }

        log.info("Sending notification email to: {}, type: {}", toEmail, notificationType);

        try {
            String subject = title;
            String htmlContent = buildNotificationEmailContent(toName, notificationType, title, message, actionUrl);
            String textContent = buildNotificationEmailText(toName, notificationType, title, message, actionUrl);

            return sendEmail(toEmail, toName, subject, htmlContent, textContent);

        } catch (Exception e) {
            log.error("Failed to send notification email to: {}", toEmail, e);
            return false;
        }
    }

    /**
     * Send a custom email.
     *
     * @param toEmail The recipient email address
     * @param toName The recipient name
     * @param subject The email subject
     * @param htmlContent The HTML content
     * @param textContent The plain text content
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendCustomEmail(String toEmail, String toName, String subject,
                                String htmlContent, String textContent) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. Skipping custom email to: {}", toEmail);
            return false;
        }

        log.info("Sending custom email to: {}", toEmail);

        try {
            return sendEmail(toEmail, toName, subject, htmlContent, textContent);

        } catch (Exception e) {
            log.error("Failed to send custom email to: {}", toEmail, e);
            return false;
        }
    }

    /**
     * Send an email using SendGrid API.
     * This is a placeholder implementation.
     * In production, integrate with SendGrid Java SDK.
     */
    private boolean sendEmail(String toEmail, String toName, String subject,
                           String htmlContent, String textContent) {
        // TODO: Integrate with SendGrid Java SDK
        // SendGrid sg = new SendGrid(sendGridApiKey);
        // SendGrid.Email from = new SendGrid.Email(fromAddress, fromName);
        // SendGrid.Email to = new SendGrid.Email(toEmail, toName);
        // Content content = new Content("text/html", htmlContent);
        // Mail mail = new Mail(from, subject, to, content);
        // SendGrid sg = new SendGrid(sendGridApiKey);
        // Request request = new Request();
        // request.setEndpoint("mail/send");
        // request.setMail(mail);
        // Response response = sg.api(request);
        // return response.getStatusCode() == 202;

        log.info("Email sent successfully to: {} (placeholder implementation)", toEmail);
        return true;
    }

    // Email content builders

    private String buildWelcomeEmailContent(String toName, String verificationUrl) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Welcome</title></head>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h1>Welcome to Travel Platform, " + toName + "!</h1>" +
                "<p>Thank you for joining our community of travelers.</p>" +
                "<p>Please verify your email address by clicking the button below:</p>" +
                "<a href='" + verificationUrl + "' style='background-color: #4CAF50; color: white; padding: 15px 32px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; margin: 4px 2px;'>Verify Email</a>" +
                "<p>If you didn't create an account, please ignore this email.</p>" +
                "</body></html>";
    }

    private String buildWelcomeEmailText(String toName, String verificationUrl) {
        return "Welcome to Travel Platform, " + toName + "!\n\n" +
                "Thank you for joining our community of travelers.\n\n" +
                "Please verify your email address by visiting: " + verificationUrl + "\n\n" +
                "If you didn't create an account, please ignore this email.";
    }

    private String buildVerificationEmailContent(String toName, String verificationUrl) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Verify Email</title></head>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h1>Verify Your Email Address</h1>" +
                "<p>Hello " + toName + ",</p>" +
                "<p>Please verify your email address by clicking the button below:</p>" +
                "<a href='" + verificationUrl + "' style='background-color: #4CAF50; color: white; padding: 15px 32px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; margin: 4px 2px;'>Verify Email</a>" +
                "<p>This link will expire in 24 hours.</p>" +
                "</body></html>";
    }

    private String buildVerificationEmailText(String toName, String verificationUrl) {
        return "Hello " + toName + ",\n\n" +
                "Please verify your email address by visiting: " + verificationUrl + "\n\n" +
                "This link will expire in 24 hours.";
    }

    private String buildPasswordResetEmailContent(String toName, String resetUrl) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Reset Password</title></head>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h1>Reset Your Password</h1>" +
                "<p>Hello " + toName + ",</p>" +
                "<p>We received a request to reset your password. Click the button below to reset it:</p>" +
                "<a href='" + resetUrl + "' style='background-color: #4CAF50; color: white; padding: 15px 32px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; margin: 4px 2px;'>Reset Password</a>" +
                "<p>This link will expire in 1 hour.</p>" +
                "<p>If you didn't request a password reset, please ignore this email.</p>" +
                "</body></html>";
    }

    private String buildPasswordResetEmailText(String toName, String resetUrl) {
        return "Hello " + toName + ",\n\n" +
                "We received a request to reset your password. Visit the link below to reset it:\n\n" +
                resetUrl + "\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you didn't request a password reset, please ignore this email.";
    }

    private String buildBookingConfirmationEmailContent(String toName, String bookingId, String accommodationTitle,
                                                  String checkInDate, String checkOutDate, String totalAmount) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Booking Confirmed</title></head>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h1>Booking Confirmed!</h1>" +
                "<p>Hello " + toName + ",</p>" +
                "<p>Your booking has been confirmed. Here are the details:</p>" +
                "<ul>" +
                "<li><strong>Booking ID:</strong> " + bookingId + "</li>" +
                "<li><strong>Accommodation:</strong> " + accommodationTitle + "</li>" +
                "<li><strong>Check-in:</strong> " + checkInDate + "</li>" +
                "<li><strong>Check-out:</strong> " + checkOutDate + "</li>" +
                "<li><strong>Total:</strong> " + totalAmount + "</li>" +
                "</ul>" +
                "<p>We look forward to hosting you!</p>" +
                "</body></html>";
    }

    private String buildBookingConfirmationEmailText(String toName, String bookingId, String accommodationTitle,
                                                String checkInDate, String checkOutDate, String totalAmount) {
        return "Hello " + toName + ",\n\n" +
                "Your booking has been confirmed. Here are the details:\n\n" +
                "Booking ID: " + bookingId + "\n" +
                "Accommodation: " + accommodationTitle + "\n" +
                "Check-in: " + checkInDate + "\n" +
                "Check-out: " + checkOutDate + "\n" +
                "Total: " + totalAmount + "\n\n" +
                "We look forward to hosting you!";
    }

    private String buildBookingCancellationEmailContent(String toName, String bookingId, String accommodationTitle,
                                                  String cancellationReason) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Booking Cancelled</title></head>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h1>Booking Cancelled</h1>" +
                "<p>Hello " + toName + ",</p>" +
                "<p>Your booking has been cancelled. Here are the details:</p>" +
                "<ul>" +
                "<li><strong>Booking ID:</strong> " + bookingId + "</li>" +
                "<li><strong>Accommodation:</strong> " + accommodationTitle + "</li>" +
                "<li><strong>Reason:</strong> " + cancellationReason + "</li>" +
                "</ul>" +
                "<p>We hope to see you again soon!</p>" +
                "</body></html>";
    }

    private String buildBookingCancellationEmailText(String toName, String bookingId, String accommodationTitle,
                                                String cancellationReason) {
        return "Hello " + toName + ",\n\n" +
                "Your booking has been cancelled. Here are the details:\n\n" +
                "Booking ID: " + bookingId + "\n" +
                "Accommodation: " + accommodationTitle + "\n" +
                "Reason: " + cancellationReason + "\n\n" +
                "We hope to see you again soon!";
    }

    private String buildPaymentReceivedEmailContent(String toName, String amount, String currency) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Payment Received</title></head>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h1>Payment Received</h1>" +
                "<p>Hello " + toName + ",</p>" +
                "<p>We have received your payment of " + amount + " " + currency + ".</p>" +
                "<p>Thank you for your payment!</p>" +
                "</body></html>";
    }

    private String buildPaymentReceivedEmailText(String toName, String amount, String currency) {
        return "Hello " + toName + ",\n\n" +
                "We have received your payment of " + amount + " " + currency + ".\n\n" +
                "Thank you for your payment!";
    }

    private String buildReviewRequestEmailContent(String toName, String accommodationTitle, String reviewUrl) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Review Request</title></head>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h1>How was your stay?</h1>" +
                "<p>Hello " + toName + ",</p>" +
                "<p>We hope you enjoyed your stay at " + accommodationTitle + ".</p>" +
                "<p>Please take a moment to share your experience by leaving a review:</p>" +
                "<a href='" + reviewUrl + "' style='background-color: #4CAF50; color: white; padding: 15px 32px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; margin: 4px 2px;'>Write a Review</a>" +
                "</body></html>";
    }

    private String buildReviewRequestEmailText(String toName, String accommodationTitle, String reviewUrl) {
        return "Hello " + toName + ",\n\n" +
                "We hope you enjoyed your stay at " + accommodationTitle + ".\n\n" +
                "Please take a moment to share your experience by leaving a review:\n\n" +
                reviewUrl;
    }

    private String buildNotificationEmailContent(String toName, String notificationType, String title,
                                            String message, String actionUrl) {
        String actionButton = actionUrl != null && !actionUrl.isEmpty() ?
                "<a href='" + actionUrl + "' style='background-color: #4CAF50; color: white; padding: 15px 32px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; margin: 4px 2px;'>View Details</a>" : "";

        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>" + title + "</title></head>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h1>" + title + "</h1>" +
                "<p>Hello " + toName + ",</p>" +
                "<p>" + message + "</p>" +
                actionButton +
                "</body></html>";
    }

    private String buildNotificationEmailText(String toName, String notificationType, String title,
                                        String message, String actionUrl) {
        String actionLink = actionUrl != null && !actionUrl.isEmpty() ?
                "\n\nView Details: " + actionUrl : "";

        return "Hello " + toName + ",\n\n" +
                title + "\n\n" +
                message + actionLink;
    }
}
