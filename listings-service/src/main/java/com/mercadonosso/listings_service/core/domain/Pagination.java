package com.mercadonosso.listings_service.core.domain;

public class Pagination {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final int page;
    private final int size;
    private final int offset;

    public Pagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be non-negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        
        this.page = page;
        this.size = size;
        this.offset = page * size;
    }

    public static Pagination defaultPagination() {
        return new Pagination(DEFAULT_PAGE, DEFAULT_SIZE);
    }

    public static Pagination of(int page, int size) {
        int validatedSize = Math.min(size, MAX_SIZE);
        return new Pagination(page, validatedSize);
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "page=" + page +
                ", size=" + size +
                ", offset=" + offset +
                '}';
    }
}
