package com.travelplatform.application.service.user;

import com.travelplatform.application.dto.request.user.ChangePasswordRequest;
import com.travelplatform.application.dto.request.user.UpdatePreferencesRequest;
import com.travelplatform.application.dto.request.user.UpdateProfileRequest;
import com.travelplatform.application.dto.response.user.PreferencesResponse;
import com.travelplatform.application.dto.response.user.ProfileResponse;
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

import java.util.List;
import java.util.UUID;

/**
 * Application Service for User operations.
 * Orchestrates user-related business workflows.
 */
@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    UserMapper userMapper;

    @Inject
    UserValidator userValidator;

    /**
     * Get user by ID.
     */
    @Transactional
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userMapper.toUserResponse(user);
    }

    /**
     * Get user by email.
     */
    @Transactional
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userMapper.toUserResponse(user);
    }

    /**
     * Get current user's profile.
     */
    @Transactional
    public ProfileResponse getCurrentUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userMapper.toProfileResponse(user.getProfile());
    }

    /**
     * Get public user profile by ID.
     */
    @Transactional
    public ProfileResponse getPublicProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ProfileResponse response = userMapper.toProfileResponse(user.getProfile());

        // Mask sensitive information for public profile
        if (response.getProfile() != null) {
            response.getProfile().setPhoneNumber(null);
            response.getProfile().setDrivingLicenseCategory(null);
        }

        return response;
    }

    /**
     * Update user profile.
     */
    @Transactional
    public ProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate update request
        userValidator.validateProfileUpdate(request);

        // Update profile
        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = new UserProfile(user.getId());
            user.setProfile(profile);
        }

        userMapper.updateProfileFromRequest(request, profile);

        // Save updated user
        userRepository.save(user);

        return userMapper.toProfileResponse(user.getProfile());
    }

    /**
     * Update user preferences.
     */
    @Transactional
    public PreferencesResponse updatePreferences(UUID userId, UpdatePreferencesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate update request
        userValidator.validatePreferencesUpdate(request);

        // Update preferences
        UserPreferences preferences = user.getPreferences();
        if (preferences == null) {
            preferences = new UserPreferences(user.getId());
            user.setPreferences(preferences);
        }

        userMapper.updatePreferencesFromRequest(request, preferences);

        // Save updated user
        userRepository.save(user);

        return userMapper.toPreferencesResponse(user);
    }

    /**
     * Change user password.
     */
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate password change request
        userValidator.validatePasswordChange(request);

        // Verify current password
        if (!user.getPasswordHash().equals(hashPassword(request.getCurrentPassword()))) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update password
        user.setPasswordHash(hashPassword(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Delete user account.
     */
    @Transactional
    public void deleteAccount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // SUPER_ADMIN cannot be deleted
        if (user.getRole() == UserRole.SUPER_ADMIN) {
            throw new IllegalArgumentException("Super admin accounts cannot be deleted");
        }

        // Soft delete by setting status to DELETED
        user.markAsDeleted();
        userRepository.save(user);
    }

    /**
     * Follow a user.
     */
    @Transactional
    public void followUser(UUID followerId, UUID followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Follower not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("User to follow not found"));

        // Check if already following
        if (userRepository.isFollowing(followerId, followingId)) {
            throw new IllegalArgumentException("Already following this user");
        }

        // Add follow relationship
        userRepository.addFollow(followerId, followingId);
    }

    /**
     * Unfollow a user.
     */
    @Transactional
    public void unfollowUser(UUID followerId, UUID followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("Cannot unfollow yourself");
        }

        // Check if following
        if (!userRepository.isFollowing(followerId, followingId)) {
            throw new IllegalArgumentException("Not following this user");
        }

        // Remove follow relationship
        userRepository.removeFollow(followerId, followingId);
    }

    /**
     * Get user's followers.
     */
    @Transactional
    public List<UserResponse> getFollowers(UUID userId, int page, int pageSize) {
        List<User> followers = userRepository.findFollowers(userId, page, pageSize);
        return userMapper.toUserResponseList(followers);
    }

    /**
     * Get user's following.
     */
    @Transactional
    public List<UserResponse> getFollowing(UUID userId, int page, int pageSize) {
        List<User> following = userRepository.findFollowing(userId, page, pageSize);
        return userMapper.toUserResponseList(following);
    }

    /**
     * Search users by name or email.
     */
    @Transactional
    public List<UserResponse> searchUsers(String query, int page, int pageSize) {
        List<User> users = userRepository.search(query, page, pageSize);
        return userMapper.toUserResponseList(users);
    }

    /**
     * Get users by role.
     */
    @Transactional
    public List<UserResponse> getUsersByRole(UserRole role, int page, int pageSize) {
        List<User> users = userRepository.findByRolePaginated(role, page, pageSize);
        return userMapper.toUserResponseList(users);
    }

    /**
     * Get users by status.
     */
    @Transactional
    public List<UserResponse> getUsersByStatus(UserStatus status, int page, int pageSize) {
        List<User> users = userRepository.findByStatusPaginated(status, page, pageSize);
        return userMapper.toUserResponseList(users);
    }

    /**
     * Activate user account.
     */
    @Transactional
    public void activateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    /**
     * Suspend user account.
     */
    @Transactional
    public void suspendUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // SUPER_ADMIN cannot be suspended
        if (user.getRole() == UserRole.SUPER_ADMIN) {
            throw new IllegalArgumentException("Super admin accounts cannot be suspended");
        }

        user.suspend();
        userRepository.save(user);
    }

    /**
     * Verify email address.
     */
    @Transactional
    public void verifyEmail(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setEmailVerified(true);
        userRepository.save(user);
    }

    /**
     * Update last login timestamp.
     */
    @Transactional
    public void updateLastLogin(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateLastLogin();
        userRepository.save(user);
    }

    /**
     * Hash password (placeholder - should use BCrypt in production).
     */
    private String hashPassword(String password) {
        // TODO: Implement BCrypt hashing
        return password;
    }
}
