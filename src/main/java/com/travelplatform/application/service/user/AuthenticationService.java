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
import com.travelplatform.domain.repository.UserSessionRepository;
import com.travelplatform.infrastructure.persistence.entity.UserSessionEntity;
import com.travelplatform.infrastructure.security.jwt.JwtTokenProvider;
import com.travelplatform.infrastructure.security.password.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.HttpHeaders;

import java.time.LocalDateTime;
import java.util.List;
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

    @Inject
    JwtTokenProvider jwtTokenProvider;

    @Inject
    PasswordEncoder passwordEncoder;

    @Inject
    UserSessionRepository userSessionRepository;

    @Inject
    HttpHeaders httpHeaders;

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
                request.getEmail(),
                hashPassword(request.getPassword()),
                UserRole.valueOf(request.getRole()));

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
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(TOKEN_EXPIRATION_HOURS * 3600);
        response.setUser(userMapper.toUserResponse(user));
        return response;
    }

    /**
     * Alias for controller compatibility.
     */
    @Transactional
    public AuthResponse registerUser(RegisterUserRequest request) {
        return register(request);
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
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Check user status
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalArgumentException("Account is suspended");
        }

        if (user.getStatus() == UserStatus.DELETED) {
            throw new IllegalArgumentException("Account is deleted");
        }

        if (user.getStatus() == UserStatus.BANNED) {
            throw new IllegalArgumentException("Account is banned");
        }

        // Update last login
        user.updateLastLogin();
        userRepository.save(user);

        // Generate JWT token
        String token = generateJwtToken(user);

        // Save session
        saveSession(user, token);

        // Build response
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(TOKEN_EXPIRATION_HOURS * 3600);
        response.setUser(userMapper.toUserResponse(user));
        return response;
    }

    private void saveSession(User user, String token) {
        try {
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS);
            UserSessionEntity session = new UserSessionEntity(
                    UUID.randomUUID(),
                    user.getId(),
                    hashToken(token),
                    expiresAt);

            if (httpHeaders != null) {
                session.setIpAddress(extractIpAddress());
                List<String> userAgent = httpHeaders.getRequestHeader(HttpHeaders.USER_AGENT);
                if (userAgent != null && !userAgent.isEmpty()) {
                    session.setUserAgent(userAgent.get(0));
                }
            }

            userSessionRepository.save(session);
        } catch (Exception e) {
            // Log but don't fail login
        }
    }

    private String hashToken(String token) {
        return Integer.toHexString(token.hashCode());
    }

    private String extractIpAddress() {
        if (httpHeaders == null)
            return null;
        List<String> forwardedFor = httpHeaders.getRequestHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.get(0).split(",")[0].trim();
        }
        return null;
    }

    /**
     * Refresh JWT token.
     */
    @Transactional
    public AuthResponse refreshToken(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Account is not active");
        }

        String token = generateJwtToken(user);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(TOKEN_EXPIRATION_HOURS * 3600);
        response.setUser(userMapper.toUserResponse(user));
        return response;
    }

    /**
     * Logout user (invalidate token).
     */
    @Transactional
    public void logout(UUID userId) {
        userSessionRepository.deleteAllByUserId(userId);
    }

    /**
     * Verify email address (Placeholder).
     */
    @Transactional
    public void verifyEmail(String token) {
        // TODO: Implement email verification logic
    }

    /**
     * Request password reset.
     */
    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // TODO: Generate reset token and send email
    }

    /**
     * Reset password with token.
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // TODO: Verify token and reset password
    }

    /**
     * Change password for authenticated user.
     */
    @Transactional
    public void changePassword(String userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid current password");
        }
        user.setPasswordHash(hashPassword(newPassword));
        userRepository.save(user);
    }

    /**
     * Validate JWT token and return user.
     */
    @Transactional
    public UserResponse validateToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        UUID userId = jwtTokenProvider.getUserIdFromToken(token);
        if (userId == null) {
            throw new IllegalArgumentException("Invalid token: missing user ID");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userMapper.toUserResponse(user);
    }

    /**
     * Generate JWT token for user.
     */
    private String generateJwtToken(User user) {
        return jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getStatus());
    }

    /**
     * Hash password using configured PasswordEncoder (BCrypt).
     */
    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
