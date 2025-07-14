package com.mercadonosso.carts_service.core.ports.out;

import java.util.Optional;

import org.bson.types.ObjectId;

public interface ListingsServicePort {
    Optional<ListingDetails> findListingsById(ObjectId listingId);
}
