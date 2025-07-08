package com.mercadonosso.reviews_service.core.ports.out;
import com.mercadonosso.reviews_service.adapters.out.http.ListingResponseDTO;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface ListingServiceClient {
    ListingResponseDTO findListingById(UUID listingId);
}
