package com.mercadonosso.reviews_service.core.ports.out;

import com.mercadonosso.reviews_service.adapters.out.http.ListingResponseDTO;
import org.springframework.stereotype.Component;

@Component
public interface ListingServiceClient {
    ListingResponseDTO findListingById(String listingId); // Changed from UUID to String
}
