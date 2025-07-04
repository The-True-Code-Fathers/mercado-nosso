package com.mercadonosso.reviews_service.adapters.in.web.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReviewsResponse(
        UUID id,
        UUID listingId,
        UUID buyerId,
        Integer rating,
        String message,
        List<String> imagesUrls,
        LocalDateTime createdAt
) {
}
