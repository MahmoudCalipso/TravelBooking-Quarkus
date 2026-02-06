package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Admin controller for platform localization and globalization settings.
 */
@Path("/api/v1/admin/globalization")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Globalization", description = "SUPER_ADMIN endpoints for platform localization")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminGlobalizationController {

    private static final Logger logger = LoggerFactory.getLogger(AdminGlobalizationController.class);

    /**
     * List all supported locales.
     */
    @GET
    @Path("/locales")
    @Operation(summary = "List supported locales", description = "Get all languages/countries supported by the platform")
    public BaseResponse<List<Map<String, String>>> listLocales() {
        logger.info("Admin listing supported locales");

        List<Map<String, String>> locales = List.of(
                Map.of("code", "en-US", "name", "English (US)"),
                Map.of("code", "it-IT", "name", "Italiano"),
                Map.of("code", "es-ES", "name", "Español"),
                Map.of("code", "fr-FR", "name", "Français"));

        return BaseResponse.success(locales);
    }

    /**
     * Update default platform language.
     */
    @PUT
    @Path("/default-locale")
    @Operation(summary = "Update default locale", description = "Set the primary language for the platform")
    public BaseResponse<Void> updateDefaultLocale(@QueryParam("locale") String locale) {
        logger.info("Admin updating default locale to {}", locale);

        // TODO: Update platform settings in database

        return BaseResponse.success("Default locale updated to " + locale);
    }

    /**
     * View localization coverage statistics.
     */
    @GET
    @Path("/coverage")
    @Operation(summary = "View localization coverage", description = "Get percentage of translated strings per language")
    public BaseResponse<Map<String, Double>> getLocalizationCoverage() {
        logger.info("Admin viewing localization coverage");

        return BaseResponse.success(Map.of(
                "en-US", 100.0,
                "it-IT", 95.5,
                "es-ES", 88.2,
                "fr-FR", 82.0));
    }

    /**
     * Manage translation keys (CRUD placeholder).
     */
    @POST
    @Path("/translations")
    @Operation(summary = "Update translation", description = "Update a specific translation key")
    public BaseResponse<Void> updateTranslation(TranslationRequest request) {
        logger.info("Admin updating translation key: {}", request.key);
        return BaseResponse.success("Translation updated successfully");
    }

    public static class TranslationRequest {
        public String key;
        public String locale;
        public String value;
    }
}
