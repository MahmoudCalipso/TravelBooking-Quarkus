package com.travelplatform.domain.repository;

import com.travelplatform.domain.model.notification.Notification;
import com.travelplatform.domain.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Notification aggregate.
 * Defines the contract for notification data access operations.
 */
public interface NotificationRepository {

    /**
     * Saves a new notification.
     *
     * @param notification notification to save
     * @return saved notification
     */
    Notification save(Notification notification);

    /**
     * Updates an existing notification.
     *
     * @param notification notification to update
     * @return updated notification
     */
    Notification update(Notification notification);

    /**
     * Deletes a notification by ID.
     *
     * @param id notification ID
     */
    void deleteById(UUID id);

    /**
     * Finds a notification by ID.
     *
     * @param id notification ID
     * @return optional notification
     */
    Optional<Notification> findById(UUID id);

    /**
     * Finds all notifications.
     *
     * @return list of all notifications
     */
    List<Notification> findAll();

    /**
     * Finds notifications by user ID.
     *
     * @param userId user ID
     * @return list of notifications for the user
     */
    List<Notification> findByUserId(UUID userId);

    /**
     * Finds notifications by user ID with pagination.
     *
     * @param userId   user ID
     * @param page     page number (0-indexed)
     * @param pageSize  page size
     * @return list of notifications
     */
    List<Notification> findByUserIdPaginated(UUID userId, int page, int pageSize);

    /**
     * Finds notifications by type.
     *
     * @param type notification type
     * @return list of notifications with the type
     */
    List<Notification> findByType(NotificationType type);

    /**
     * Finds notifications by user ID and type.
     *
     * @param userId user ID
     * @param type   notification type
     * @return list of notifications
     */
    List<Notification> findByUserIdAndType(UUID userId, NotificationType type);

    /**
     * Finds unread notifications by user ID.
     *
     * @param userId user ID
     * @return list of unread notifications
     */
    List<Notification> findUnreadByUserId(UUID userId);

    /**
     * Finds read notifications by user ID.
     *
     * @param userId user ID
     * @return list of read notifications
     */
    List<Notification> findReadByUserId(UUID userId);

    /**
     * Finds notifications by user ID and read status.
     *
     * @param userId user ID
     * @param isRead read status
     * @return list of notifications
     */
    List<Notification> findByUserIdAndIsRead(UUID userId, boolean isRead);

    /**
     * Finds notifications by related entity.
     *
     * @param entityType entity type (BOOKING, REEL, REVIEW, MESSAGE, etc.)
     * @param entityId    entity ID
     * @return list of notifications
     */
    List<Notification> findByRelatedEntity(String entityType, UUID entityId);

    /**
     * Finds notifications by user ID and related entity.
     *
     * @param userId     user ID
     * @param entityType entity type
     * @param entityId    entity ID
     * @return list of notifications
     */
    List<Notification> findByUserIdAndRelatedEntity(UUID userId, String entityType, UUID entityId);

    /**
     * Finds notifications created after a date.
     *
     * @param userId user ID
     * @param date   creation date threshold
     * @return list of notifications
     */
    List<Notification> findByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime date);

    /**
     * Finds notifications created before a date.
     *
     * @param userId user ID
     * @param date   creation date threshold
     * @return list of notifications
     */
    List<Notification> findByUserIdAndCreatedAtBefore(UUID userId, LocalDateTime date);

    /**
     * Finds notifications created between dates.
     *
     * @param userId    user ID
     * @param startDate start date
     * @param endDate   end date
     * @return list of notifications
     */
    List<Notification> findByUserIdAndCreatedAtBetween(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds notifications by user ID sorted by creation date (newest first).
     *
     * @param userId user ID
     * @param limit  maximum number of results
     * @return list of notifications
     */
    List<Notification> findLatestByUserId(UUID userId, int limit);

    /**
     * Finds unread notifications by user ID sorted by creation date (newest first).
     *
     * @param userId user ID
     * @param limit  maximum number of results
     * @return list of notifications
     */
    List<Notification> findLatestUnreadByUserId(UUID userId, int limit);

    /**
     * Counts notifications by user.
     *
     * @param userId user ID
     * @return count of notifications for the user
     */
    long countByUserId(UUID userId);

    /**
     * Counts unread notifications by user.
     *
     * @param userId user ID
     * @return count of unread notifications
     */
    long countUnreadByUserId(UUID userId);

    /**
     * Counts read notifications by user.
     *
     * @param userId user ID
     * @return count of read notifications
     */
    long countReadByUserId(UUID userId);

    /**
     * Counts notifications by type.
     *
     * @param type notification type
     * @return count of notifications with the type
     */
    long countByType(NotificationType type);

    /**
     * Counts notifications by user and type.
     *
     * @param userId user ID
     * @param type   notification type
     * @return count of notifications
     */
    long countByUserIdAndType(UUID userId, NotificationType type);

    /**
     * Counts all notifications.
     *
     * @return total count of notifications
     */
    long countAll();

    /**
     * Marks all notifications as read for a user.
     *
     * @param userId user ID
     * @return number of notifications marked as read
     */
    long markAllAsReadByUserId(UUID userId);

    /**
     * Marks notifications as read by user ID and type.
     *
     * @param userId user ID
     * @param type   notification type
     * @return number of notifications marked as read
     */
    long markAsReadByUserIdAndType(UUID userId, NotificationType type);

    /**
     * Deletes old notifications before a date.
     *
     * @param date date threshold
     * @return number of deleted notifications
     */
    long deleteOldNotificationsBeforeDate(LocalDateTime date);

    /**
     * Deletes read notifications for a user.
     *
     * @param userId user ID
     * @return number of deleted notifications
     */
    long deleteReadNotificationsByUserId(UUID userId);

    /**
     * Deletes notifications by related entity.
     *
     * @param entityType entity type
     * @param entityId    entity ID
     * @return number of deleted notifications
     */
    long deleteByRelatedEntity(String entityType, UUID entityId);

    /**
     * Finds notifications by user ID with pagination and sorting.
     *
     * @param userId   user ID
     * @param page     page number (0-indexed)
     * @param pageSize  page size
     * @param isRead   optional read status filter (null for all)
     * @return list of notifications
     */
    List<Notification> findByUserIdPaginatedWithFilter(UUID userId, int page, int pageSize, Boolean isRead);

    /**
     * Finds notifications by user ID, type, and read status.
     *
     * @param userId user ID
     * @param type   notification type
     * @param isRead read status
     * @return list of notifications
     */
    List<Notification> findByUserIdAndTypeAndIsRead(UUID userId, NotificationType type, boolean isRead);

    /**
     * Finds notifications by user ID and multiple types.
     *
     * @param userId user ID
     * @param types  list of notification types
     * @return list of notifications
     */
    List<Notification> findByUserIdAndTypes(UUID userId, List<NotificationType> types);

    /**
     * Finds notifications by user ID, multiple types, and read status.
     *
     * @param userId user ID
     * @param types  list of notification types
     * @param isRead read status
     * @return list of notifications
     */
    List<Notification> findByUserIdAndTypesAndIsRead(UUID userId, List<NotificationType> types, boolean isRead);

    /**
     * Finds notifications by user ID with action URL.
     *
     * @param userId user ID
     * @return list of notifications with action URLs
     */
    List<Notification> findWithActionUrlByUserId(UUID userId);

    /**
     * Finds notifications by user ID without action URL.
     *
     * @param userId user ID
     * @return list of notifications without action URLs
     */
    List<Notification> findWithoutActionUrlByUserId(UUID userId);

    /**
     * Finds notifications by user ID created in the last N days.
     *
     * @param userId user ID
     * @param days   number of days
     * @return list of notifications
     */
    List<Notification> findByUserIdAndLastNDays(UUID userId, int days);

    /**
     * Finds notifications by user ID created in the last N hours.
     *
     * @param userId user ID
     * @param hours  number of hours
     * @return list of notifications
     */
    List<Notification> findByUserIdAndLastNHours(UUID userId, int hours);

    /**
     * Finds notifications by user ID created in the last N minutes.
     *
     * @param userId  user ID
     * @param minutes number of minutes
     * @return list of notifications
     */
    List<Notification> findByUserIdAndLastNMinutes(UUID userId, int minutes);

    /**
     * Finds notifications by user ID sorted by creation date (oldest first).
     *
     * @param userId user ID
     * @param limit  maximum number of results
     * @return list of notifications
     */
    List<Notification> findOldestByUserId(UUID userId, int limit);

    /**
     * Finds notifications by user ID and title keyword.
     *
     * @param userId  user ID
     * @param keyword keyword to search in title
     * @return list of notifications
     */
    List<Notification> findByUserIdAndTitleContaining(UUID userId, String keyword);

    /**
     * Finds notifications by user ID and message keyword.
     *
     * @param userId  user ID
     * @param keyword keyword to search in message
     * @return list of notifications
     */
    List<Notification> findByUserIdAndMessageContaining(UUID userId, String keyword);

    /**
     * Finds notifications by user ID and keyword (title or message).
     *
     * @param userId  user ID
     * @param keyword keyword to search
     * @return list of notifications
     */
    List<Notification> findByUserIdAndKeyword(UUID userId, String keyword);

    /**
     * Finds notifications by user ID with pagination, sorted by creation date (newest first).
     *
     * @param userId   user ID
     * @param page     page number (0-indexed)
     * @param pageSize  page size
     * @return list of notifications
     */
    List<Notification> findByUserIdPaginatedSortedByDateDesc(UUID userId, int page, int pageSize);

    /**
     * Finds notifications by user ID with pagination, sorted by creation date (oldest first).
     *
     * @param userId   user ID
     * @param page     page number (0-indexed)
     * @param pageSize  page size
     * @return list of notifications
     */
    List<Notification> findByUserIdPaginatedSortedByDateAsc(UUID userId, int page, int pageSize);

    /**
     * Finds notifications by user ID and read status with pagination.
     *
     * @param userId   user ID
     * @param isRead   read status
     * @param page     page number (0-indexed)
     * @param pageSize  page size
     * @return list of notifications
     */
    List<Notification> findByUserIdAndIsReadPaginated(UUID userId, boolean isRead, int page, int pageSize);

    /**
     * Finds notifications by user ID and type with pagination.
     *
     * @param userId   user ID
     * @param type     notification type
     * @param page     page number (0-indexed)
     * @param pageSize  page size
     * @return list of notifications
     */
    List<Notification> findByUserIdAndTypePaginated(UUID userId, NotificationType type, int page, int pageSize);

    /**
     * Finds notifications by user ID, type, and read status with pagination.
     *
     * @param userId   user ID
     * @param type     notification type
     * @param isRead   read status
     * @param page     page number (0-indexed)
     * @param pageSize  page size
     * @return list of notifications
     */
    List<Notification> findByUserIdAndTypeAndIsReadPaginated(UUID userId, NotificationType type, boolean isRead, int page, int pageSize);
}
