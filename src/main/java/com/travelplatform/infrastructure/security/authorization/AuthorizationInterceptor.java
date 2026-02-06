package com.travelplatform.infrastructure.security.authorization;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.CurrentUser;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

/**
 * CDI Interceptor that enforces the @Authorized annotation.
 * This interceptor validates that the current user has the required roles
 * and permissions to access the annotated method.
 */
@Authorized
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
public class AuthorizationInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationInterceptor.class);

    @Inject
    CurrentUser currentUser;

    @Inject
    RoleBasedAccessControl rbac;

    @Inject
    PermissionEvaluator permissionEvaluator;

    /**
     * Intercept method calls and validate authorization.
     * 
     * @param context Invocation context
     * @return Result of the method invocation
     * @throws Exception if authorization fails or method execution fails
     */
    @AroundInvoke
    public Object validateAuthorization(InvocationContext context) throws Exception {
        Method method = context.getMethod();

        // Get @Authorized annotation from method or class
        Authorized authorized = method.getAnnotation(Authorized.class);
        if (authorized == null) {
            // Check class-level annotation
            authorized = method.getDeclaringClass().getAnnotation(Authorized.class);
        }

        // If no annotation found, allow access (fail-open for backward compatibility)
        if (authorized == null) {
            logger.warn("Method {} has no @Authorized annotation. Access allowed by default.",
                    method.getName());
            return context.proceed();
        }

        // Check if anonymous access is allowed
        if (authorized.allowAnonymous()) {
            logger.debug("Anonymous access allowed for method {}", method.getName());
            return context.proceed();
        }

        // Verify user is authenticated
        if (!currentUser.isAuthenticated()) {
            logger.warn("Unauthenticated access attempt to method {}", method.getName());
            throw new NotAuthorizedException("Authentication required");
        }

        // Get current user role
        UserRole userRole = currentUser.getRole();
        if (userRole == null) {
            logger.error("Authenticated user has no role assigned");
            throw new ForbiddenException("User role not found");
        }

        // Check role-based access
        UserRole[] allowedRoles = authorized.roles();
        if (allowedRoles.length > 0) {
            boolean hasRequiredRole = Arrays.stream(allowedRoles)
                    .anyMatch(role -> role == userRole);

            if (!hasRequiredRole) {
                logger.warn("User with role {} attempted to access method {} requiring roles {}",
                        userRole, method.getName(), Arrays.toString(allowedRoles));
                throw new ForbiddenException("Insufficient permissions");
            }
        }

        // Check resource ownership if required
        if (authorized.requireOwner()) {
            UUID resourceOwnerId = extractResourceOwnerId(context);
            if (resourceOwnerId != null) {
                UUID currentUserId = currentUser.getId();

                // SUPER_ADMIN can access all resources
                if (userRole != UserRole.SUPER_ADMIN && !resourceOwnerId.equals(currentUserId)) {
                    logger.warn("User {} attempted to access resource owned by {}",
                            currentUserId, resourceOwnerId);
                    throw new ForbiddenException("You can only access your own resources");
                }
            }
        }

        // Check custom permissions if specified
        String[] requiredPermissions = authorized.permissions();
        if (requiredPermissions.length > 0) {
            for (String permission : requiredPermissions) {
                if (!hasPermission(permission, context)) {
                    logger.warn("User {} lacks permission: {}", currentUser.getId(), permission);
                    throw new ForbiddenException("Missing required permission: " + permission);
                }
            }
        }

        // Authorization successful, proceed with method invocation
        logger.debug("Authorization successful for user {} accessing method {}",
                currentUser.getId(), method.getName());
        return context.proceed();
    }

    /**
     * Extract resource owner ID from method parameters.
     * Looks for parameters named "userId", "ownerId", or "resourceOwnerId".
     * 
     * @param context Invocation context
     * @return Resource owner ID or null if not found
     */
    private UUID extractResourceOwnerId(InvocationContext context) {
        Object[] parameters = context.getParameters();
        Method method = context.getMethod();

        // Try to find resource owner ID in parameters
        for (int i = 0; i < parameters.length; i++) {
            Object param = parameters[i];
            if (param instanceof UUID) {
                // Check parameter name (requires -parameters compiler flag)
                String paramName = method.getParameters()[i].getName();
                if (paramName.equalsIgnoreCase("userId") ||
                        paramName.equalsIgnoreCase("ownerId") ||
                        paramName.equalsIgnoreCase("resourceOwnerId")) {
                    return (UUID) param;
                }
            }
        }

        return null;
    }

    /**
     * Check if the current user has a specific permission.
     * This can be extended to check against a permission database or service.
     * 
     * @param permission Permission to check
     * @param context    Invocation context
     * @return true if user has permission
     */
    private boolean hasPermission(String permission, InvocationContext context) {
        // For now, delegate to PermissionEvaluator
        // This can be extended based on specific permission requirements

        switch (permission) {
            case "canApproveContent":
                return permissionEvaluator.canApproveReel(null); // Generic approval check
            case "canModerateContent":
                return rbac.canModerateContent();
            case "canManageUsers":
                return rbac.canManageUsers();
            case "canViewAnalytics":
                return permissionEvaluator.canViewAnalytics();
            default:
                logger.warn("Unknown permission: {}", permission);
                return false;
        }
    }
}
