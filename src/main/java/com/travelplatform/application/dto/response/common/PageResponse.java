package com.travelplatform.application.dto.response.common;

import java.util.List;

/**
 * DTO for paginated response.
 */
public class PageResponse<T> {

    private List<T> data;
    private PaginationInfo pagination;

    public PageResponse() {
    }

    public PageResponse(List<T> data, PaginationInfo pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    // Getters and Setters

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public PaginationInfo getPagination() {
        return pagination;
    }

    public void setPagination(PaginationInfo pagination) {
        this.pagination = pagination;
    }

    /**
     * Inner class for pagination information.
     */
    public static class PaginationInfo {
        private Integer page;
        private Integer pageSize;
        private Integer totalPages;
        private Long totalItems;
        private Boolean hasNext;
        private Boolean hasPrevious;

        public PaginationInfo() {
        }

        public PaginationInfo(Integer page, Integer pageSize, Long totalItems) {
            this.page = page;
            this.pageSize = pageSize;
            this.totalItems = totalItems;
            this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
            this.hasNext = page < totalPages;
            this.hasPrevious = page > 1;
        }

        // Getters and Setters

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }

        public Integer getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(Integer totalPages) {
            this.totalPages = totalPages;
        }

        public Long getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(Long totalItems) {
            this.totalItems = totalItems;
        }

        public Boolean getHasNext() {
            return hasNext;
        }

        public void setHasNext(Boolean hasNext) {
            this.hasNext = hasNext;
        }

        public Boolean getHasPrevious() {
            return hasPrevious;
        }

        public void setHasPrevious(Boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }
    }
}
