package com.mercadonosso.carts_service.core.ports.in;

import java.util.UUID;

import com.mercadonosso.carts_service.core.domain.CartsEntity;

public interface CartsServicePort {
    CartsEntity searchById(UUID userId);
    CartsEntity add(UUID userId, UUID listingId, int quantity);
    CartsEntity remove(UUID userId, UUID listingId);
    CartsEntity update(UUID userId, UUID listingId, int quantity);
    void clear(UUID userId);
}
