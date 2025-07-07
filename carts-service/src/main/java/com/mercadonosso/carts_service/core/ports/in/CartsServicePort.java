package com.mercadonosso.carts_service.core.ports.in;

import java.util.UUID;

import com.mercadonosso.carts_service.core.domain.CartsEntity;

public interface CartsServicePort {
    CartsEntity findById(UUID userId);

    CartsEntity create(UUID userId, UUID listingId, int quantity);

    CartsEntity remove(UUID userId, UUID listingId);

    CartsEntity update(UUID userId, UUID listingId, int quantity);

    void requestClear(UUID userId);

    void processClear(UUID userId);
}
