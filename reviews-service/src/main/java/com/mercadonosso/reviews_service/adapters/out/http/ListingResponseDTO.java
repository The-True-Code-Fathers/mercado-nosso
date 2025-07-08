package com.mercadonosso.reviews_service.adapters.out.http;

import java.util.UUID;

public record ListingResponseDTO(
        UUID listingId,
        UUID sellerId
) {
}
