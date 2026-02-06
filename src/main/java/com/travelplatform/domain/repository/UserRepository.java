package com.travelplatform.domain.repository;

import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.model.user.UserProfile;
import com.travelplatform.domain.model.user.UserPreferences;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User aggregate.
 * Defines the contract for user data access operations.
 */
public interface UserRepository {

    /**
     * Saves a new user.
     *
     * @param user user to save
     * @return saved user
     */
    User save(User user);

    /**
     * Updates an existing user.
     *
     * @param user user to update
     * @return updated user
     */
    User update(User user);

    /**
     * Deletes a user by ID.
     *
     * @param id user ID
     */
    void deleteById(UUID id);

    /**
     * Finds a user by ID.
     *
     * @param id user ID
     * @return optional user
     */
    Optional<User> findById(UUID id);

    /**
     * Finds a user by email.
     *
     * @param email user email
     * @return optional user
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists by email.
     *
     * @param email user email
     * @return true if user exists
     */
    boolean existsByEmail(String email);

    /**
     * Finds all users.
     *
     * @return list of all users
     */
    List<User> findAll();

    /**
     * Finds all users with pagination.
     *
     * @param page     page number
     * @param pageSize page size
     * @return list of users
     */
    List<User> findAll(int page, int pageSize);

    /**
     * Finds all users with filters and pagination.
     */
    List<User> findAll(UserRole role, UserStatus status, java.time.LocalDate startDate, java.time.LocalDate endDate,
            int page, int pageSize);

    /**
     * Counts users with filters.
     */
    long count(UserRole role, UserStatus status, java.time.LocalDate startDate, java.time.LocalDate endDate);

    /**
     * Finds users by role.
     *
     * @param role user role
     * @return list of users with the role
     */
    List<User> findByRole(UserRole role);

    /**
     * Finds users by status.
     *
     * @param status user status
     * @return list of users with the status
     */
    List<User> findByStatus(UserStatus status);

    /**
     * Finds users by role and status.
     *
     * @param role   user role
     * @param status user status
     * @return list of users with the role and status
     */
    List<User> findByRoleAndStatus(UserRole role, UserStatus status);

    /**
     * Finds users by role with pagination.
     *
     * @param role     user role
     * @param page     page number (0-indexed)
     * @param pageSize page size
     * @return list of users
     */
    List<User> findByRolePaginated(UserRole role, int page, int pageSize);

    /**
     * Finds users by status with pagination.
     *
     * @param status   user status
     * @param page     page number (0-indexed)
     * @param pageSize page size
     * @return list of users
     */
    List<User> findByStatusPaginated(UserStatus status, int page, int pageSize);

    /**
     * Counts users by role.
     *
     * @param role user role
     * @return count of users with the role
     */
    long countByRole(UserRole role);

    /**
     * Counts users by status.
     *
     * @param status user status
     * @return count of users with the status
     */
    long countByStatus(UserStatus status);

    /**
     * Counts all users.
     *
     * @return total count of users
     */
    long countAll();

    /**
     * Standard count method.
     */
    long count();

    /**
     * Finds user profile by user ID.
     *
     * @param userId user ID
     * @return optional user profile
     */
    Optional<UserProfile> findProfileByUserId(UUID userId);

    /**
     * Finds user preferences by user ID.
     *
     * @param userId user ID
     * @return optional user preferences
     */
    Optional<UserPreferences> findPreferencesByUserId(UUID userId);

    /**
     * Searches users by name or email.
     *
     * @param searchTerm search term
     * @return list of matching users
     */
    List<User> searchByNameOrEmail(String searchTerm);

    /**
     * Searches users by name or email with pagination.
     *
     * @param query    search query
     * @param page     page number
     * @param pageSize page size
     * @return list of matching users
     */
    List<User> search(String query, int page, int pageSize);

    /**
     * Finds users who follow a specific user.
     *
     * @param userId user ID
     * @return list of followers
     */
    List<User> findFollowersByUserId(UUID userId);

    /**
     * Finds users followed by a specific user.
     *
     * @param userId user ID
     * @return list of following
     */
    List<User> findFollowingByUserId(UUID userId);

    /**
     * Counts followers of a user.
     *
     * @param userId user ID
     * @return count of followers
     */
    long countFollowersByUserId(UUID userId);

    /**
     * Counts following of a user.
     *
     * @param userId user ID
     * @return count of following
     */
    long countFollowingByUserId(UUID userId);

    /**
     * Checks if a user is following another user.
     */
    boolean isFollowing(UUID followerId, UUID followingId);

    /**
     * Adds a follow relationship.
     */
    void addFollow(UUID followerId, UUID followingId);

    /**
     * Removes a follow relationship.
     */
    void removeFollow(UUID followerId, UUID followingId);

    /**
     * Finds followers with pagination.
     */
    List<User> findFollowers(UUID userId, int page, int pageSize);

    /**
     * Finds following with pagination.
     */
    List<User> findFollowing(UUID userId, int page, int pageSize);
}
