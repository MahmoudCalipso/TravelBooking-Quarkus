package com.travelplatform.application.service.supplier;

import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.model.user.UserProfile;
import com.travelplatform.domain.repository.UserRepository;
import com.travelplatform.infrastructure.notification.EmailService;
import com.travelplatform.infrastructure.notification.PushNotificationService;
import com.travelplatform.infrastructure.notification.SmsService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Supplier automated messaging using simple templating.
 */
@ApplicationScoped
public class AutomatedMessagingService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private final Map<String, String> customTemplates = new ConcurrentHashMap<>();

    @Inject
    UserRepository userRepository;

    @Inject
    EmailService emailService;

    @Inject
    SmsService smsService;

    @Inject
    PushNotificationService pushNotificationService;

    public String sendAutomatedMessage(UUID bookingId, Booking booking, MessageTemplate template) {
        String content = fillTemplate(template.content(), booking);
        sendEmailIfPossible(booking, template.name(), content);
        sendSmsIfPossible(booking, content);
        // Push notifications can be targeted when device tokens are registered
        return content;
    }

    public MessageTemplate createTemplate(UUID supplierId, String name, String content) {
        String key = supplierId + ":" + name;
        customTemplates.put(key, content);
        return new MessageTemplate(name, content);
    }

    public String getTemplate(UUID supplierId, String name) {
        return customTemplates.get(supplierId + ":" + name);
    }

    private String fillTemplate(String template, Booking booking) {
        String text = template;
        text = text.replace("{{guestName}}", resolveGuestName(booking.getUserId()));
        text = text.replace("{{checkInDate}}", DATE_FORMATTER.format(booking.getCheckInDate()));
        text = text.replace("{{checkOutDate}}", DATE_FORMATTER.format(booking.getCheckOutDate()));
        text = text.replace("{{propertyId}}", booking.getAccommodationId().toString());
        return text;
    }

    private String resolveGuestName(UUID userId) {
        return userRepository.findProfileByUserId(userId)
                .map(UserProfile::getFullName)
                .filter(s -> !s.isBlank())
                .orElse("Guest");
    }

    private void sendEmailIfPossible(Booking booking, String subject, String content) {
        userRepository.findById(booking.getUserId()).ifPresent(user -> {
            emailService.sendCustomEmail(user.getEmail(), resolveGuestName(user.getId()), subject, content, content);
        });
    }

    private void sendSmsIfPossible(Booking booking, String content) {
        userRepository.findProfileByUserId(booking.getUserId())
                .map(UserProfile::getPhoneNumber)
                .filter(phone -> smsService.validatePhoneNumber(smsService.formatPhoneNumber(phone)))
                .ifPresent(phone -> smsService.sendSms(smsService.formatPhoneNumber(phone), content));
    }

    /**
     * Simple template holder.
     */
    public record MessageTemplate(String name, String content) {
    }
}
