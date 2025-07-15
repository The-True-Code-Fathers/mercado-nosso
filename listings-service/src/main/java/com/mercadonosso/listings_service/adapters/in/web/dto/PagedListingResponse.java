package com.mercadonosso.listings_service.adapters.in.web.dto;

import java.util.List;

public record PagedListingResponse(
        List<ListingResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious,
        boolean isEmpty
) {
}
