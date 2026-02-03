package com.travelplatform.application.service.user;

import com.travelplatform.application.dto.request.user.LoginRequest;
import com.travelplatform.application.dto.request.user.RegisterUserRequest;
import com.travelplatform.application.dto.response.user.AuthResponse;
import com.travelplatform.application.dto.response.user.UserResponse;
import com.travelplatform.application.mapper.UserMapper;
import com.travelplatform.application.validator.UserValidator;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.model.user.UserPreferences;
import com.travelplatform.domain.model.user.UserProfile;
import com.travelplatform.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Application Service for Authentication operations.
 * Handles user registration, login, and token generation.
 */
@ApplicationScoped
public class AuthenticationService {

    @Inject
    UserRepository userRepository;

    @Inject
    UserMapper userMapper;

    @Inject
    UserValidator userValidator;

    private static final long TOKEN_EXPIRATION_HOURS = 24;

    /**
     * Register a new user.
     */
    @Transactional
    public AuthResponse register(RegisterUserRequest request) {
        // Validate registration request
        userValidator.validateRegistration(request);

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Create new user

        User user = new User(
                UUID.randomUUID(),
                request.getEmail(),
                hashPassword(request.getPassword()),
                request.getRole());

        // Create default profile
        UserProfile profile = new UserProfile(user.getId());
        user.setProfile(profile);

        // Create default preferences
        UserPreferences preferences = new UserPreferences(user.getId());
        user.setPreferences(preferences);

        // Save user
        userRepository.save(user);

        // Generate JWT token
        String token = generateJwtToken(user);

        // Build response
        // Build response
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(TOKEN_EXPIRATION_HOURS * 3600);
        response.setUser(userMapper.toUserResponse(user));
        return response;
    }

    /**
     * Login user.
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Validate login request
        userValidator.validateLogin(request);

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // Verify password
        if (!user.getPasswordHash().equals(hashPassword(request.getPassword()))) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Check user status
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalArgumentException("Account is suspended");
        }

        if (user.getStatus() == UserStatus.DELETED) {
            throw new IllegalArgumentException("Account is deleted");
        }

        // Update last login
        user.updateLastLogin();
        userRepository.save(user);

        // Generate JWT token
        String token = generateJwtToken(user);

        // Build response
        // Build response
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(TOKEN_EXPIRATION_HOURS * 3600);
        response.setUser(userMapper.toUserResponse(user));
        return response;
    }

    /**
     * Refresh JWT token.
     */
    @Transactional
    public AuthResponse refreshToken(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check user status
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Account is not active");
        }

        // Generate new JWT token
        String token = generateJwtToken(user);

        // Build response
        // Build response
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(TOKEN_EXPIRATION_HOURS * 3600);
        response.setUser(userMapper.toUserResponse(user));
        return response;
    }

    /**
     * Logout user (invalidate token).
     * In a real implementation, this would add the token to a blacklist.
     */
    public void logout(UUID userId) {
        // TODO: Implement token blacklisting
        // For now, this is a placeholder
    }

    /**
     * Verify email address with token.
     */
    @Transactional
    public void verifyEmail(String token) {
        // TODO: Implement email verification with token
        // For now, this is a placeholder
    }

    /**
     * Request password reset.
     */
    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // TODO: Generate password reset token and send email
        // For now, this is a placeholder
    }

    /**
     * Reset password with token.
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // TODO: Verify token and reset password
        // For now, this is a placeholder
    }

    /**
     * Validate JWT token and return user.
     */
    @Transactional
    public UserResponse validateToken(String token) {
        // TODO: Implement JWT token validation
        // For now, this is a placeholder
        throw new UnsupportedOperationException("Token validation not implemented yet");
    }

    /**
     * Generate JWT token for user.
     */
    private String generateJwtToken(User user) {
        // TODO: Implement JWT token generation
        // For now, return a placeholder token
        return "jwt_token_" + user.getId() + "_" + System.currentTimeMillis();
    }

    /**
     * Hash password (placeholder - should use BCrypt in production).
     */
    private String hashPassword(String password) {
        // TODO: Implement BCrypt hashing
        return password;
    }
}
