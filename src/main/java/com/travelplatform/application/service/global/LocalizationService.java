package com.travelplatform.application.service.global;

import jakarta.enterprise.context.ApplicationScoped;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Localization helper using Java ResourceBundle with graceful fallback.
 */
@ApplicationScoped
public class LocalizationService {

    public String translate(String key, Locale locale) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public String format(String key, Locale locale, Object... args) {
        String pattern = translate(key, locale);
        return MessageFormat.format(pattern, args);
    }

    public LocalizedContent getLocalizedContent(UUID entityId, Locale locale, String defaultContent) {
        // Placeholder: return default content; extend with persistence-backed translations
        return new LocalizedContent(entityId, locale.getLanguage(), defaultContent);
    }

    public record LocalizedContent(UUID entityId, String locale, String content) {
    }
}
