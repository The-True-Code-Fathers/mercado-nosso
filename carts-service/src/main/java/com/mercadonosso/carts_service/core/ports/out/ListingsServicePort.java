package com.mercadonosso.carts_service.core.ports.out;

import java.util.Optional;
import java.util.UUID;

public interface ListingsServicePort {
    Optional<ListingDetails> findListingsById(UUID listingId);
}
