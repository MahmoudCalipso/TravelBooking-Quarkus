package com.travelplatform.application.dto.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Instant;
import java.util.List;

/**
 * Paginated response wrapper for list-based API responses.
 * Provides pagination metadata along with the data.
 * 
 * @param <T> Type of items in the list
 */
@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginatedResponse<T> {

    private boolean success;
    private List<T> data;
    private PaginationMetadata pagination;
    private Instant timestamp;

    /**
     * Default constructor.
     */
    public PaginatedResponse() {
        this.success = true;
        this.timestamp = Instant.now();
    }

    /**
     * Constructor with data and pagination metadata.
     * 
     * @param data       List of items
     * @param pagination Pagination metadata
     */
    public PaginatedResponse(List<T> data, PaginationMetadata pagination) {
        this.success = true;
        this.data = data;
        this.pagination = pagination;
        this.timestamp = Instant.now();
    }

    /**
     * Create a paginated response.
     * 
     * @param data       List of items
     * @param totalItems Total number of items
     * @param page       Current page number (0-indexed)
     * @param size       Page size
     * @param <T>        Type of items
     * @return PaginatedResponse instance
     */
    public static <T> PaginatedResponse<T> of(List<T> data, long totalItems, int page, int size) {
        PaginationMetadata pagination = new PaginationMetadata(totalItems, page, size);
        return new PaginatedResponse<>(data, pagination);
    }

    /**
     * Pagination metadata class.
     */
    @RegisterForReflection
    public static class PaginationMetadata {
        private long totalItems;
        private int page;
        private int size;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;

        public PaginationMetadata() {
        }

        public PaginationMetadata(long totalItems, int page, int size) {
            this.totalItems = totalItems;
            this.page = page;
            this.size = size;
            this.totalPages = (int) Math.ceil((double) totalItems / size);
            this.hasNext = page < (totalPages - 1);
            this.hasPrevious = page > 0;
        }

        // Getters and Setters

        public long getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(long totalItems) {
            this.totalItems = totalItems;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public boolean isHasPrevious() {
            return hasPrevious;
        }

        public void setHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }
    }

    // Getters and Setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public PaginationMetadata getPagination() {
        return pagination;
    }

    public void setPagination(PaginationMetadata pagination) {
        this.pagination = pagination;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
