package com.travelplatform.infrastructure.config;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OpenAPI/Swagger configuration for the Travel Platform application.
 * This class provides API documentation configuration.
 */
@ApplicationScoped
@OpenAPIDefinition(info = @Info(title = "Travel Platform API", version = "1.0.0", description = "Enterprise-grade travel platform backend API with support for accommodations, travel reels, bookings, reviews, events, and more.", contact = @Contact(name = "Travel Platform Team", email = "support@travelplatform.com", url = "https://travelplatform.com"), license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT")), servers = {
        @Server(description = "Local Development Server", url = "http://localhost:8080"),
        @Server(description = "Development Server", url = "https://dev-api.travelplatform.com"),
        @Server(description = "Staging Server", url = "https://staging-api.travelplatform.com"),
        @Server(description = "Production Server", url = "https://api.travelplatform.com")
}, security = {
        @SecurityRequirement(name = "jwtAuth")
}, tags = {
        @Tag(name = "Authentication", description = "User authentication and authorization endpoints"),
        @Tag(name = "Users", description = "User profile and account management"),
        @Tag(name = "Accommodations", description = "Accommodation listings and search"),
        @Tag(name = "Travel Reels", description = "Short video content and engagement"),
        @Tag(name = "Bookings", description = "Booking management and payments"),
        @Tag(name = "Reviews", description = "Accommodation reviews and ratings"),
        @Tag(name = "Events", description = "Travel events and programs"),
        @Tag(name = "Chat", description = "Messaging and chat functionality"),
        @Tag(name = "Notifications", description = "User notifications"),
        @Tag(name = "Admin", description = "Administrative endpoints")
})
@SecurityScheme(securitySchemeName = "jwtAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "JWT authentication token")
@Unremovable
public class OpenApiConfig implements OASFilter {

    private static final Logger log = LoggerFactory.getLogger(OpenApiConfig.class);

    @Inject
    @ConfigProperty(name = "quarkus.application.name", defaultValue = "travel-platform")
    private String applicationName;

    @Inject
    @ConfigProperty(name = "quarkus.application.version", defaultValue = "1.0.0")
    private String applicationVersion;

    @Inject
    @ConfigProperty(name = "openapi.scan.packages", defaultValue = "com.travelplatform")
    private String scanPackages;

    @Inject
    @ConfigProperty(name = "openapi.scan.exclude.packages", defaultValue = "")
    private String excludePackages;

    @Inject
    @ConfigProperty(name = "openapi.scan.exclude.classes", defaultValue = "")
    private String excludeClasses;

    @Inject
    @ConfigProperty(name = "openapi.servers", defaultValue = "")
    private String customServers;

    @Inject
    @ConfigProperty(name = "openapi.info.title", defaultValue = "Travel Platform API")
    private String apiTitle;

    @Inject
    @ConfigProperty(name = "openapi.info.description", defaultValue = "Enterprise-grade travel platform backend API")
    private String apiDescription;

    @Inject
    @ConfigProperty(name = "openapi.info.version", defaultValue = "1.0.0")
    private String apiVersion;

    @Inject
    @ConfigProperty(name = "openapi.info.termsOfService", defaultValue = "")
    private String termsOfService;

    @Inject
    @ConfigProperty(name = "openapi.info.contact.email", defaultValue = "support@travelplatform.com")
    private String contactEmail;

    @Inject
    @ConfigProperty(name = "openapi.info.contact.name", defaultValue = "Travel Platform Team")
    private String contactName;

    @Inject
    @ConfigProperty(name = "openapi.info.contact.url", defaultValue = "https://travelplatform.com")
    private String contactUrl;

    @Inject
    @ConfigProperty(name = "openapi.info.license.name", defaultValue = "MIT License")
    private String licenseName;

    @Inject
    @ConfigProperty(name = "openapi.info.license.url", defaultValue = "https://opensource.org/licenses/MIT")
    private String licenseUrl;

    @Inject
    @ConfigProperty(name = "openapi.security.jwt.enabled", defaultValue = "true")
    private boolean jwtSecurityEnabled;

    @Inject
    @ConfigProperty(name = "openapi.security.jwt.scheme", defaultValue = "bearer")
    private String jwtScheme;

    @Inject
    @ConfigProperty(name = "openapi.security.jwt.bearerFormat", defaultValue = "JWT")
    private String jwtBearerFormat;

    @Inject
    @ConfigProperty(name = "openapi.operation.id.strategy", defaultValue = "method")
    private String operationIdStrategy;

    @Inject
    @ConfigProperty(name = "openapi.path.servers", defaultValue = "")
    private String pathServers;

    @Inject
    @ConfigProperty(name = "openapi.filter", defaultValue = "true")
    private boolean filterEnabled;

    /**
     * Filter the OpenAPI document.
     * This method can be used to modify the generated OpenAPI document.
     *
     * @param openApi The OpenAPI document
     * @return The filtered OpenAPI document
     */
    @Override
    @Override
    public void filterOpenAPI(OpenAPI openApi) {
        if (!filterEnabled) {
            return;
        }

        log.debug("Filtering OpenAPI document");

        // Add custom servers if configured
        if (customServers != null && !customServers.isEmpty()) {
            List<org.eclipse.microprofile.openapi.models.servers.Server> servers = new ArrayList<>();
            String[] serverArray = customServers.split(",");
            for (String server : serverArray) {
                org.eclipse.microprofile.openapi.models.servers.Server s = new io.smallrye.openapi.api.models.servers.ServerImpl();
                s.setUrl(server.trim());
                servers.add(s);
            }
            openApi.setServers(servers);
        }

        // Add security schemes
        if (jwtSecurityEnabled) {
            org.eclipse.microprofile.openapi.models.security.SecurityScheme securityScheme = new io.smallrye.openapi.api.models.security.SecuritySchemeImpl();
            securityScheme.setType(org.eclipse.microprofile.openapi.models.security.SecurityScheme.Type.HTTP);
            securityScheme.setScheme(jwtScheme);
            securityScheme.setBearerFormat(jwtBearerFormat);
            securityScheme.setDescription("JWT authentication token");

            if (openApi.getComponents() == null) {
                openApi.setComponents(new io.smallrye.openapi.api.models.ComponentsImpl());
            }
            openApi.getComponents().addSecurityScheme("jwtAuth", securityScheme);
        }

        log.info("OpenAPI document filtered successfully");
    }

    /**
     * Get the application name.
     *
     * @return Application name
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Get the application version.
     *
     * @return Application version
     */
    public String getApplicationVersion() {
        return applicationVersion;
    }

    /**
     * Get the scan packages.
     *
     * @return Scan packages
     */
    public String getScanPackages() {
        return scanPackages;
    }

    /**
     * Get the exclude packages.
     *
     * @return Exclude packages
     */
    public String getExcludePackages() {
        return excludePackages;
    }

    /**
     * Get the exclude classes.
     *
     * @return Exclude classes
     */
    public String getExcludeClasses() {
        return excludeClasses;
    }

    /**
     * Get the API title.
     *
     * @return API title
     */
    public String getApiTitle() {
        return apiTitle;
    }

    /**
     * Get the API description.
     *
     * @return API description
     */
    public String getApiDescription() {
        return apiDescription;
    }

    /**
     * Get the API version.
     *
     * @return API version
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * Get the terms of service URL.
     *
     * @return Terms of service URL
     */
    public String getTermsOfService() {
        return termsOfService;
    }

    /**
     * Get the contact email.
     *
     * @return Contact email
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * Get the contact name.
     *
     * @return Contact name
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Get the contact URL.
     *
     * @return Contact URL
     */
    public String getContactUrl() {
        return contactUrl;
    }

    /**
     * Get the license name.
     *
     * @return License name
     */
    public String getLicenseName() {
        return licenseName;
    }

    /**
     * Get the license URL.
     *
     * @return License URL
     */
    public String getLicenseUrl() {
        return licenseUrl;
    }

    /**
     * Check if JWT security is enabled.
     *
     * @return true if JWT security is enabled
     */
    public boolean isJwtSecurityEnabled() {
        return jwtSecurityEnabled;
    }

    /**
     * Get the JWT scheme.
     *
     * @return JWT scheme
     */
    public String getJwtScheme() {
        return jwtScheme;
    }

    /**
     * Get the JWT bearer format.
     *
     * @return JWT bearer format
     */
    public String getJwtBearerFormat() {
        return jwtBearerFormat;
    }

    /**
     * Get the operation ID strategy.
     *
     * @return Operation ID strategy
     */
    public String getOperationIdStrategy() {
        return operationIdStrategy;
    }

    /**
     * Check if the filter is enabled.
     *
     * @return true if filter is enabled
     */
    public boolean isFilterEnabled() {
        return filterEnabled;
    }

    /**
     * Get OpenAPI configuration summary.
     *
     * @return Map containing configuration summary
     */
    public java.util.Map<String, Object> getConfigurationSummary() {
        java.util.Map<String, Object> summary = new java.util.HashMap<>();

        summary.put("applicationName", applicationName);
        summary.put("applicationVersion", applicationVersion);
        summary.put("scanPackages", scanPackages);
        summary.put("excludePackages", excludePackages);
        summary.put("excludeClasses", excludeClasses);
        summary.put("apiTitle", apiTitle);
        summary.put("apiDescription", apiDescription);
        summary.put("apiVersion", apiVersion);
        summary.put("contactEmail", contactEmail);
        summary.put("contactName", contactName);
        summary.put("contactUrl", contactUrl);
        summary.put("licenseName", licenseName);
        summary.put("licenseUrl", licenseUrl);
        summary.put("jwtSecurityEnabled", jwtSecurityEnabled);
        summary.put("jwtScheme", jwtScheme);
        summary.put("jwtBearerFormat", jwtBearerFormat);
        summary.put("operationIdStrategy", operationIdStrategy);
        summary.put("filterEnabled", filterEnabled);

        return summary;
    }
}
