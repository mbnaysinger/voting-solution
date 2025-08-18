package br.com.naysinger.common.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PagedResult<T> {
    @JsonProperty("content")
    private List<T> content;

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("page_size")
    private int pageSize;

    @JsonProperty("total_elements")
    private long totalElements;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("has_next")
    private boolean hasNext;

    @JsonProperty("has_previous")
    private boolean hasPrevious;

    @JsonProperty("sort_by")
    private String sortBy;

    @JsonProperty("sort_direction")
    private String sortDirection;

    public PagedResult(List<T> content, int currentPage, int pageSize, long totalElements,
                       int totalPages, String sortBy, String sortDirection) {
        this.content = content;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.hasNext = currentPage < totalPages - 1;
        this.hasPrevious = currentPage > 0;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    public PagedResult(List<T> content, int currentPage, int pageSize, long totalElements, int totalPages) {
        this(content, currentPage, pageSize, totalElements, totalPages, null, null);
    }

    public PagedResult() {
        this.content = new ArrayList<>();
    }

    public static <T> PagedResult<T> empty(int page, int size) {
        return new PagedResult<>(
                new ArrayList<>(),
                page,
                size,
                0L,
                0,
                null,
                null
        );
    }

    public static <T> PagedResult<T> empty(int page, int size, String sortBy, String sortDirection) {
        return new PagedResult<>(
                new ArrayList<>(),
                page,
                size,
                0L,
                0,
                sortBy,
                sortDirection
        );
    }

    public static <T> PagedResult<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PagedResult<>(content, page, size, totalElements, totalPages);
    }

    public static <T> PagedResult<T> of(List<T> content, int page, int size, long totalElements,
                                        String sortBy, String sortDirection) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PagedResult<>(content, page, size, totalElements, totalPages, sortBy, sortDirection);
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
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

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    @Override
    public String toString() {
        return "PagedResult{" +
                "content=" + content +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}