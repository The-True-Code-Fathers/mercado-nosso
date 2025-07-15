package com.mercadonosso.listings_service.core.domain;

import java.util.List;

public class PagedResult<T> {
    private final List<T> content;
    private final Pagination pagination;
    private final long totalElements;
    private final int totalPages;

    public PagedResult(List<T> content, Pagination pagination, long totalElements) {
        this.content = content;
        this.pagination = pagination;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pagination.getSize());
    }

    public List<T> getContent() {
        return content;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean hasNext() {
        return pagination.getPage() + 1 < totalPages;
    }

    public boolean hasPrevious() {
        return pagination.getPage() > 0;
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    public int getSize() {
        return content.size();
    }

    @Override
    public String toString() {
        return "PagedResult{" +
                "content size=" + content.size() +
                ", pagination=" + pagination +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                '}';
    }
}
