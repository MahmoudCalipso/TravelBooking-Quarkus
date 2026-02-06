package com.travelplatform.infrastructure.security.authorization;

import com.travelplatform.domain.enums.UserRole;
import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for declarative authorization on controller methods.
 * This annotation specifies which roles can access an endpoint and whether
 * resource ownership is required.
 * 
 * Usage examples:
 * - @Authorized(roles = {UserRole.SUPER_ADMIN})
 * - @Authorized(roles = {UserRole.TRAVELER, UserRole.SUPPLIER_SUBSCRIBER})
 * - @Authorized(roles = {UserRole.TRAVELER}, requireOwner = true)
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Authorized {

    /**
     * Array of roles allowed to access this endpoint.
     * If empty, all authenticated users are allowed.
     * 
     * @return Array of allowed user roles
     */
    @Nonbinding
    UserRole[] roles() default {};

    /**
     * Whether the user must own the resource being accessed.
     * When true, the interceptor will verify that the current user ID
     * matches the resource owner ID.
     * 
     * @return true if resource ownership is required
     */
    @Nonbinding
    boolean requireOwner() default false;

    /**
     * Optional granular permissions beyond roles.
     * These are custom permission strings that can be checked
     * by the PermissionEvaluator.
     * 
     * @return Array of required permissions
     */
    @Nonbinding
    String[] permissions() default {};

    /**
     * Whether to allow anonymous access.
     * If true, the endpoint can be accessed without authentication.
     * This is useful for public endpoints like login, register, etc.
     * 
     * @return true if anonymous access is allowed
     */
    @Nonbinding
    boolean allowAnonymous() default false;
}
